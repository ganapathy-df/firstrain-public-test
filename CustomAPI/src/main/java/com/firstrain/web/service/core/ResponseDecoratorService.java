package com.firstrain.web.service.core;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Service;

import com.firstrain.db.obj.EntityBacktest;
import com.firstrain.db.obj.EntityHistory;
import com.firstrain.db.obj.PrivateEntity;
import com.firstrain.db.obj.PrivateEntityList;
import com.firstrain.frapi.util.StatusCode;
import com.firstrain.utils.JSONUtility;
import com.firstrain.web.domain.Brand;
import com.firstrain.web.domain.Group;
import com.firstrain.web.domain.MultipleMatchedEntities;
import com.firstrain.web.domain.PwToken;
import com.firstrain.web.domain.Topic;
import com.firstrain.web.domain.UpdateDefinition;
import com.firstrain.web.pojo.CategorizerObject;
import com.firstrain.web.pojo.CategorizerObject.Attribute;
import com.firstrain.web.pojo.CategorizerObject.CatEntity;
import com.firstrain.web.pojo.CategorizerObject.CatEntityWrapper;
import com.firstrain.web.pojo.Entity;
import com.firstrain.web.pojo.Location;
import com.firstrain.web.pojo.ResultJsonRes;
import com.firstrain.web.pojo.Version;
import com.firstrain.web.response.CategorizationServiceResponse;
import com.firstrain.web.response.JSONResponse;
import com.firstrain.web.response.JSONResponse.ResStatus;
import com.firstrain.web.response.PrivateEntityServiceResponse;
import com.firstrain.web.wrapper.EntityListWrapper;
import com.firstrain.web.wrapper.EntityListWrapperData;
import com.firstrain.web.wrapper.PrivateEntityWrapper;
import com.google.common.collect.Lists;

@Service
public class ResponseDecoratorService {

	private static final Logger LOG = Logger.getLogger(ResponseDecoratorService.class);
	private static final String I_COMPANIES = "I:PWCompanies";
	private static final String SEPRATOR = "C:";
	private static final String COMPANIES = "companies";
	private static final String TOPICS = "topics";
	private static final String STATUS_COMPLETED = "COMPLETED";
	private static final String STATUS_NEW = "NEW";
	private static final String STATUS_DEV = "DEV";
	private static final String STATUS_LIVE = "LIVE";
	private static final String STATUS_IN_ACTIVE = "INACTIVE";
	private static final String STATUS_ACTIVE = "ACTIVE";
	private static final String STATUS_IN_PROGRESS = "IN-PROGRESS";

	@Autowired
	private ResourceBundleMessageSource messageSource;
	@Autowired
	private PrivateEntityService privateEntityService;

	@Value("${api.version}")
	private String version;

	public String getVersion() {
		return version;
	}

	public CategorizationServiceResponse getEntityListResponse(CategorizerObject categorizeServiceResponse, String guid) {
		CategorizationServiceResponse res = new CategorizationServiceResponse();
		EntityListWrapperData resp = new EntityListWrapperData();
		res.setResult(resp);
		// set guid
		resp.setGuid(guid);
		res.setVersion(version);

		String messageKey = null;
		if (categorizeServiceResponse != null && categorizeServiceResponse.getResponseCode() == 200) {

			CatEntityWrapper catEntityWrapper = categorizeServiceResponse.getData();
			List<Entity> entityList = null;
			if (catEntityWrapper != null) {
				entityList = getEntityList(categorizeServiceResponse.getData().getCategorizerResponse());

				if (CollectionUtils.isNotEmpty(entityList)) {
					EntityListWrapper ewrap = new EntityListWrapper();
					ewrap.setEntity(entityList);
					resp.setData(ewrap);
					messageKey = "catservice.success";
					res.setStatus(ResStatus.SUCCESS);
				}
			}

			messageKey = getMessageKeyAndPopulateSuccessStatus(catEntityWrapper, entityList, messageKey, res); 

		} else {
			messageKey = populateServiceNotAvailError(res);
		}
		res.setMessage(messageSource.getMessage(messageKey, null, Locale.getDefault()));
		return res;
	}

	public CategorizationServiceResponse getEntityListResponseForCategorizePw(CategorizerObject categorizeServiceResponse, String guid,
			Brand brand, List<String> taxonomyDirectives, String brandKey, List<String> targetSearchTokens) {
		CategorizationServiceResponse res = new CategorizationServiceResponse();
		EntityListWrapperData resp = new EntityListWrapperData();
		res.setResult(resp);
		// set guid
		resp.setDocId(guid);
		res.setVersion(version);

		String messageKey = null;
		if (categorizeServiceResponse != null && categorizeServiceResponse.getResponseCode() == 200) {

			CatEntityWrapper catEntityWrapper = categorizeServiceResponse.getData();
			List<Entity> entityList = null;
			if (catEntityWrapper != null) {

				List<CatEntity> catEntities = catEntityWrapper.getCategorizerResponse();

				Set<String> pwTokens = new HashSet<String>();
				MultipleMatchedEntities multipleMatchedEntities = populateMultipleMatchedEntitiesMap(catEntities, brand.getTopicMap(),
						brand.getExcludeTopicMap(), brand.getAndTopicMap(), pwTokens, brand.getBrandInitials());
				Set<String> finallyMatchedEntities = new HashSet<String>();
				Map<String, CatEntity> finalCatEntityMap = populateMatchedTaxonomyCatEntities(pwTokens, finallyMatchedEntities, brand,
						multipleMatchedEntities, taxonomyDirectives, brandKey);

				int i = 0;
				List<String> taxonomyDirectiveLst = null;
				for (String taxonomyDirective : taxonomyDirectives) {

					if (multipleMatchedEntities != null) {
						if (!taxonomyDirective.contains(TOPICS) && !taxonomyDirective.contains(COMPANIES)
								&& !COMPANIES.equalsIgnoreCase(taxonomyDirective)) {
							finalCatEntityMap = createAndPopulateCompanyCategoryExtraCategories(finalCatEntityMap, finallyMatchedEntities, multipleMatchedEntities);
							populateExtraFrCompanyCategories(finalCatEntityMap, finallyMatchedEntities,
									multipleMatchedEntities.getPeTopicMap());
						} else if (taxonomyDirective.contains(COMPANIES) && !COMPANIES.equalsIgnoreCase(taxonomyDirective)) {
							finalCatEntityMap = createAndPopulateCompanyCategoryExtraCategories(finalCatEntityMap, finallyMatchedEntities, multipleMatchedEntities);
						} else if (taxonomyDirective.contains(TOPICS)) {
							if (finalCatEntityMap == null) {
								finalCatEntityMap = new HashMap<String, CatEntity>();
							}
							populateExtraFrCompanyCategories(finalCatEntityMap, finallyMatchedEntities,
									multipleMatchedEntities.getPeTopicMap());
						}
					}

					if (i == 0) {
						taxonomyDirectiveLst = resp.getTaxonomyDirective();
						if (taxonomyDirectiveLst == null) {
							taxonomyDirectiveLst = new ArrayList<String>();
							resp.setTaxonomyDirective(taxonomyDirectiveLst);
						}
						taxonomyDirectiveLst.add(taxonomyDirective);
						i++;
						continue;
					}

					taxonomyDirectiveLst.add(taxonomyDirective);
					// populate Companies
					if (COMPANIES.equalsIgnoreCase(taxonomyDirective)) {
						if (finalCatEntityMap == null) {
							finalCatEntityMap = new HashMap<String, CatEntity>();
						}
						populateExtraFrCompanyCategories(finalCatEntityMap, finallyMatchedEntities, multipleMatchedEntities.getCompanyMap());
					}

					i++;
				}

				if (MapUtils.isNotEmpty(finalCatEntityMap) && CollectionUtils.isNotEmpty(finalCatEntityMap.values())) {
					Collection<CatEntity> catEntitiesCol = finalCatEntityMap.values();
					List<CatEntity> lists = Lists.newArrayList(catEntitiesCol);
					Collections.sort(lists);
					entityList = getEntityListPw(lists, targetSearchTokens);
					if (CollectionUtils.isNotEmpty(entityList)) {
						EntityListWrapper ewrap = new EntityListWrapper();
						ewrap.setEntities(entityList);
						resp.setData(ewrap);
						messageKey = "catservice.success";
						res.setStatus(ResStatus.SUCCESS);
					}
				}
			}

			messageKey = getMessageKeyAndPopulateSuccessStatus(catEntityWrapper, entityList, messageKey, res); 

		} else {
			messageKey = populateServiceNotAvailError(res);
		}
		res.setMessage(messageSource.getMessage(messageKey, null, Locale.getDefault()));
		return res;
	}

	private String populateServiceNotAvailError(final CategorizationServiceResponse res) {
		String messageKey = "errorcode." + StatusCode.SERVICE_NOT_AVAIL;
		res.setErrorCode(StatusCode.SERVICE_NOT_AVAIL);
		res.setStatus(ResStatus.ERROR);
		return messageKey;
	}

	private Map<String, CatEntity> createAndPopulateCompanyCategoryExtraCategories(Map<String, CatEntity> finalCatEntityMapParam, final Set<String> finallyMatchedEntities, final MultipleMatchedEntities multipleMatchedEntities) {
		Map<String, CatEntity> finalCatEntityMap = finalCatEntityMapParam;
		if (finalCatEntityMap == null) {
			finalCatEntityMap = new HashMap<String, CatEntity>();
		}
		populateExtraFrCompanyCategories(finalCatEntityMap, finallyMatchedEntities,
				multipleMatchedEntities.getPeCompanyMap());
		return finalCatEntityMap;
	}
 
	private String getMessageKeyAndPopulateSuccessStatus(final CatEntityWrapper catEntityWrapper, final List<Entity> entityList, String messageKeyParam, final CategorizationServiceResponse res) { 
		String messageKey = messageKeyParam;
		if (catEntityWrapper == null || CollectionUtils.isEmpty(entityList)) { 
			messageKey = "catservice.nodata"; 
			res.setStatus(ResStatus.SUCCESS); 
		} 
		return messageKey; 
	} 

	private Void populateExtraFrCompanyCategories(Map<String, CatEntity> finalCatEntityMap, Set<String> finallyMatchedEntities,
			Map<String, CatEntity> companyMap) {

		if (MapUtils.isEmpty(companyMap)) {
			return null;
		}

		for (Map.Entry<String, CatEntity> map : companyMap.entrySet()) {

			String key = map.getKey();
			CatEntity catEntity = map.getValue();

			if (finallyMatchedEntities.contains(key)) {
				continue;
			}

			Attribute attribute = catEntity.getAttribute();
			Short band = catEntity.getBand();
			Short score = catEntity.getScore();

			String searchToken = attribute.getAttrSearchToken();
			Long charOffset = catEntity.getCharOffset();
			Long charCount = catEntity.getCharCount();

			LOG.info("populating Fr Company Categories, searchToken :" + searchToken + ", band : " + band + ", score : " + score
					+ ", charOffset : " + charOffset + ", charCount : " + charCount);

			finalCatEntityMap.put(key, getCatEntity(searchToken, attribute.getName(), band != null ? band : (short) -1,
					score != null ? score : (short) -1, charOffset, charCount));

		}


		return null;
	}

	private List<Entity> getEntityListPw(List<CatEntity> catEntities, List<String> targetSearchTokens) {

		if (CollectionUtils.isEmpty(catEntities)) {
			return null;
		}

		List<Entity> entityList = new ArrayList<Entity>();
		for (CatEntity catEntity : catEntities) {
			if (Boolean.TRUE.equals(catEntity.getAttrExclude())) {
				continue;
			}
			String searchToken = catEntity.getAttribute().getAttrSearchToken();
			if (CollectionUtils.isNotEmpty(targetSearchTokens)) {
				if (!targetSearchTokens.contains(searchToken)) {
					continue;
				}
			}
			Entity entity = createEntity(catEntity); 
			entity.setSearchToken(searchToken);
			entity.setRelevanceScore(catEntity.getScore());
			List<Location> location = entity.getLocation();
			if (location == null) {
				location = new ArrayList<Location>();
				entity.setLocation(location);
			}
			if (catEntity.getCharCount() != null) {
				createAndAddLocation(catEntity, location); 
			}
			entityList.add(entity);
		}

		LOG.info("final entity list in response size : " + entityList.size());
		return entityList;
	}

	private List<Entity> getEntityList(List<CatEntity> catEntities) {

		if (CollectionUtils.isEmpty(catEntities)) {
			return null;
		}

		List<Entity> entityList = new ArrayList<Entity>();
		for (CatEntity catEntity : catEntities) {
			if (Boolean.TRUE.equals(catEntity.getAttrExclude())) {
				continue;
			}
			Entity entity = createEntity(catEntity); 
			entity.setSearchToken(catEntity.getAttribute().getAttrSearchToken());
			entity.setRelevanceScore(catEntity.getScore());
			entityList.add(entity);
		}
		return entityList;
	}
 
	private Entity createEntity(final CatEntity catEntity) { 
		Entity entity = new Entity(); 
		entity.setName(catEntity.getAttribute().getName()); 
		entity.setRelevanceBand(catEntity.getBand()); 
		return entity; 
	} 

	private Map<String, CatEntity> populateMatchedTaxonomyCatEntities(Set<String> pwTokens, Set<String> finallyMatchedEntities, Brand brand,
			MultipleMatchedEntities multipleMatchedEntities, List<String> taxonomyDirectives, String brandKey) {

		if (CollectionUtils.isEmpty(pwTokens) || MapUtils.isEmpty(brand.getPwTokenMap()) || MapUtils.isEmpty(brand.getGroupMap())) {
			return null;
		}

		Map<String, PwToken> pwTokenMap = brand.getPwTokenMap();
		Map<Integer, Group> groupMap = brand.getGroupMap();

		Map<String, CatEntity> matchedTaxonomyMap = multipleMatchedEntities.getMatchedTaxonomyMap();

		Map<String, CatEntity> catEntityMap = new HashMap<String, CatEntity>();
		for (String pwTokenStr : pwTokens) {
			PwToken pwToken = pwTokenMap.get(pwTokenStr);

			if (pwToken == null || CollectionUtils.isEmpty(pwToken.getGroups())) {
				continue;
			}

			Set<Integer> groups = pwToken.getGroups();

			LOG.debug("pwTokenStr : " + pwTokenStr + ", groups : " + groups);
			Short band = -1;
			Short score = -1;
			Long charOffset = null;
			Map<Long, Long> charMap = new HashMap<Long, Long>();

			for (Integer groupInt : groups) {

				Group group = groupMap.get(groupInt);
				CatEntity catEntity = getQualifyCatEntity(group, matchedTaxonomyMap, finallyMatchedEntities);

				if (catEntity == null) {
					continue;
				}

				Long charOffsetRes = retrieveCharOffSetAndPopulate(catEntity, charMap); 

				LOG.debug("in response find max , token : " + pwTokenStr + ", charOffset : " + charOffsetRes + ", charCount : "
						+ catEntity.getCharCount() + ", band : " + catEntity.getBand() + ", score : " + catEntity.getScore());

				charOffset = getMaxCharOffset(charOffset, charOffsetRes);
				band = getMaxBand(band, catEntity.getBand() != null ? catEntity.getBand() : -1);
				score = getMaxScore(score, catEntity.getScore() != null ? catEntity.getScore() : -1);

				if (I_COMPANIES.equalsIgnoreCase(pwTokenStr)
						&& (taxonomyDirectives.contains(brandKey.toLowerCase())
								|| taxonomyDirectives.contains((brandKey + "Companies").toLowerCase()))) {
					Attribute attribute = catEntity.getAttribute();
					String searchToken = attribute.getAttrSearchToken();

					LOG.debug("finally to process for max -> searchToken : " + searchToken + ", band : " + band + ", score : " + score
							+ ", charOffset : " + charOffset + ", charCount : " + charMap.get(charOffset));

					catEntityMap.put(searchToken, getCatEntity(searchToken, attribute.getName(), band, score, charOffset, charMap.get(charOffset)));
				}

			}

			if (!I_COMPANIES.equalsIgnoreCase(pwTokenStr)
					&& (taxonomyDirectives.contains(brandKey.toLowerCase())
							|| taxonomyDirectives.contains((brandKey + "Topics").toLowerCase()))) {

				LOG.debug("finally to process for max -> pwTokenStr : " + pwTokenStr + ", band : " + band + ", score : " + score + ", charOffset : "
						+ charOffset + ", charCount : " + charMap.get(charOffset));

				catEntityMap.put(pwTokenStr, getCatEntity(pwTokenStr, pwToken.getName(), band, score, charOffset, charMap.get(charOffset)));
			}
		}

		LOG.info("Returing Matched Taxonomy Entities Map Size : " + catEntityMap.size());
		return catEntityMap;
	}

	private Short getMaxScore(Short score, short score2) {
		return (short) Math.max(score, score2);
	}

	private Short getMaxBand(Short band, short band2) {
		return (short) Math.max(band, band2);
	}

	private Long getMaxCharOffset(Long charOffsetOld, Long charOffsetNew) {
		if (charOffsetNew == null) {
			return charOffsetOld;
		} else if (charOffsetOld == null) {
			return charOffsetNew;
		}

		return charOffsetOld > charOffsetNew ? charOffsetOld : charOffsetNew;
	}

	private CatEntity getCatEntity(String token, String name, Short band, Short score, Long charOffset, Long charCount) {
		Attribute attribute = new Attribute();
		attribute.setAttrSearchToken(token);
		attribute.setName(name);

		CatEntity catEntity = new CatEntity();
		catEntity.setAttribute(attribute);
		if (band > -1) {
			catEntity.setBand(band);
		}
		if (score > -1) {
			catEntity.setScore(score);
		}
		catEntity.setCharOffset(charOffset);
		catEntity.setCharCount(charCount);

		return catEntity;
	}

	private CatEntity getQualifyCatEntity(Group group, Map<String, CatEntity> matchedTaxonomyMap, Set<String> finallyMatchedEntities) {

		if (group == null || CollectionUtils.isEmpty(group.getTokenSet()) || MapUtils.isEmpty(matchedTaxonomyMap)) {
			return null;
		}

		boolean isAllEnttiesMatched = true;

		Short band = -1;
		Short score = -1;
		Long charOffset = null;
		Map<Long, Long> charMap = new HashMap<Long, Long>();

		Set<String> tokenSet = group.getTokenSet();
		for (String token : tokenSet) {

			if (!matchedTaxonomyMap.containsKey(token)) {
				isAllEnttiesMatched = false;
				break;
			}

			CatEntity catEntity = matchedTaxonomyMap.get(token);
			Long charOffsetRes = retrieveCharOffSetAndPopulate(catEntity, charMap); 

			LOG.debug("in response find min , token : " + token + ", charOffset : " + charOffsetRes + ", charCount : " + catEntity.getCharCount()
					+ ", band : " + catEntity.getBand() + ", score : " + catEntity.getScore());
			charOffset = getMinCharOffset(charOffset, charOffsetRes);
			band = getMinBand(band, catEntity.getBand() != null ? catEntity.getBand() : -1);
			score = getMinScore(score, catEntity.getScore() != null ? catEntity.getScore() : -1);

		}

		if (!isAllEnttiesMatched) {
			LOG.debug("All Entities not Matched for tokenSet : " + tokenSet);
			return null;
		}

		LOG.info("All Entities Matched for tokenSet : " + tokenSet + ", Finally to process for min -> FrToken : " + group.getFrToken()
				+ ", band : " + band + ", score : " + score + ", charOffset : " + charOffset + ", charCount : " + charMap.get(charOffset));

		finallyMatchedEntities.addAll(tokenSet);
		return getCatEntity(group.getFrToken(), group.getFrTopic(), band, score, charOffset, charMap.get(charOffset));
	}
 
	private Long retrieveCharOffSetAndPopulate(final CatEntity catEntity, final Map<Long, Long> charMap) { 
		Long charOffsetRes = catEntity.getCharOffset(); 
		if (charOffsetRes != null) { 
			charMap.put(charOffsetRes, catEntity.getCharCount()); 
		} 
		return charOffsetRes; 
	} 

	private Short getMinScore(Short score, Short score2) {
		if (score == -1) {
			return score2;
		}
		return (short) Math.min(score, score2);
	}

	private Short getMinBand(Short band, Short band2) {
		if (band == -1) {
			return band2;
		}
		return (short) Math.min(band, band2);
	}

	private Long getMinCharOffset(Long charOffsetOld, Long charOffsetNew) {

		if (charOffsetNew == null) {
			return charOffsetOld;
		} else if (charOffsetOld == null) {
			return charOffsetNew;
		}

		return charOffsetOld < charOffsetNew ? charOffsetOld : charOffsetNew;
	}

	private MultipleMatchedEntities populateMultipleMatchedEntitiesMap(List<CatEntity> catEntities, Map<String, Topic> topicMap,
			Map<String, List<String>> excludedTopicMap, Map<String, List<String>> andTopicMap, Set<String> pwTokens, String brandInitials) {

		if (CollectionUtils.isEmpty(catEntities)) {
			return null;
		}

		LOG.info("Doc Categorize Response Size : " + catEntities.size());
		Set<String> frTokens = topicMap != null ? topicMap.keySet() : new HashSet<String>();
		Map<String, CatEntity> matchedTaxonomyMap = new HashMap<String, CatEntity>();
		Map<String, CatEntity> companyMap = new HashMap<String, CatEntity>();
		Map<String, CatEntity> peCompanyMap = new HashMap<String, CatEntity>();
		Map<String, CatEntity> peTopicMap = new HashMap<String, CatEntity>();

		LOG.debug("frTokens in excel sheet : " + frTokens);
		for (CatEntity catEntity : catEntities) {

			String searchToken = catEntity.getAttribute().getAttrSearchToken();
			if (Boolean.FALSE.equals(catEntity.getFromFirstRain())) {

				if (!searchToken.toLowerCase().contains("-" + brandInitials + ":")) {
					continue;
				}

				if (searchToken.startsWith("T")) {
					peTopicMap.put(searchToken, catEntity);
				} else if (searchToken.startsWith("C")) {
					peCompanyMap.put(searchToken, catEntity);
				}
				continue;
			}

			LOG.debug("searchToken from catEntity : " + searchToken);
			if (frTokens.contains(searchToken)) {
				matchedTaxonomyMap.put(searchToken, catEntity);
				Topic topic = topicMap.get(searchToken);
				if (topic != null) {
					Set<String> pwTokenSet = topic.getPwTokens();
					if (CollectionUtils.isNotEmpty(pwTokenSet)) {
						pwTokens.addAll(pwTokenSet);
					}
				}
			}

			if (StringUtils.isNotEmpty(searchToken) && searchToken.startsWith(SEPRATOR)) {
				companyMap.put(searchToken, catEntity);
			}
		}

		if (pwTokens != null && !pwTokens.isEmpty()) {
			Iterator<String> iter = pwTokens.iterator();
			while (iter.hasNext()) {
				String token = iter.next();
				if (excludedTopicMap.containsKey(token)) {
					List<String> excludedTokens = excludedTopicMap.get(token);
					if (excludedTokens != null) {
						for (CatEntity catEntity1 : catEntities) {
							String searchToken1 = catEntity1.getAttribute().getAttrSearchToken();
							if (excludedTokens.contains(searchToken1)) {
								iter.remove();
								break;
							}
						}
					}
				}
			}
		}

		if (pwTokens != null && !pwTokens.isEmpty()) {
			Iterator<String> iter = pwTokens.iterator();
			while (iter.hasNext()) {
				String token = iter.next();
				if (andTopicMap.containsKey(token)) {
					List<String> excludedTokens = andTopicMap.get(token);
					if (excludedTokens != null && !excludedTokens.isEmpty()) {
						boolean excludeToken = true;
						for (CatEntity catEntity1 : catEntities) {
							String searchToken1 = catEntity1.getAttribute().getAttrSearchToken();
							if (excludedTokens.contains(searchToken1)) {
								excludeToken = false;
								break;
							}
						}
						if (excludeToken) {
							iter.remove();
						}
					}
				}
			}
		}

		LOG.info("populate Multiple Matched Entities where Matched CompanyMap size : " + companyMap.size() + " and Matched TaxonomyMap size : "
				+ matchedTaxonomyMap.size() + " and pwTokenSet to process : " + pwTokens);
		MultipleMatchedEntities multipleMatchedEntities = new MultipleMatchedEntities();
		multipleMatchedEntities.setCompanyMap(companyMap);
		multipleMatchedEntities.setMatchedTaxonomyMap(matchedTaxonomyMap);
		multipleMatchedEntities.setPeCompanyMap(peCompanyMap);
		multipleMatchedEntities.setPeTopicMap(peTopicMap);
		return multipleMatchedEntities;

	}

	@SuppressWarnings("rawtypes")
	public JSONResponse getTakeDownResponse(int statusCode, String succMessageKey) {
		JSONResponse res = new JSONResponse();
		res.setVersion(version);
		res.setStatus(ResStatus.SUCCESS);
		String messageKey = null;
		if (statusCode == StatusCode.REQUEST_SUCCESS) {
			messageKey = succMessageKey;
		} else if (statusCode == StatusCode.ARTICLE_UNDER_PROCESSING) {
			messageKey = "takedown.status.processing";
		} else if (statusCode == StatusCode.ARTICLE_TAKED_DOWN) {
			messageKey = "errorcode." + statusCode;
		} else {
			messageKey = "errorcode." + statusCode;
			res.setStatus(ResStatus.ERROR);
		}

		res.setMessage(messageSource.getMessage(messageKey, null, Locale.getDefault()));
		return res;
	}

	public PrivateEntityServiceResponse getPrivateEntityServiceResponse(int errorCode, PrivateEntityWrapper privateEntityWrapper,
			String code) {
		PrivateEntityServiceResponse res = new PrivateEntityServiceResponse();
		if (privateEntityWrapper != null) {
			res.setResult(privateEntityWrapper);
		}
		return populateSuccessResponse(res, errorCode, code); 
	}

	public JSONResponse getEntityBacktestServiceResponse(int errorCode, String code) {
		JSONResponse res = new JSONResponse();
		return populateSuccessResponse(res, errorCode, code); 
	}
 
	private <T0 extends JSONResponse> T0 populateSuccessResponse(final T0 res, final int errorCode, String codeParam) { 
		String code = codeParam;
		res.setStatus(ResStatus.SUCCESS); 
		if (errorCode > -1) { 
			code = "errorcode." + errorCode; 
		} 
		res.setMessage(messageSource.getMessage(code, null, Locale.getDefault())); 
		res.setVersion(version); 
		return res; 
	} 

	public void populatePrivateEntityWrapper(PrivateEntityWrapper privateEntityWrapper, String searchToken, String name) {

		if (StringUtils.isEmpty(searchToken)) {
			return;
		}

		if (StringUtils.isNotEmpty(name)) {
			privateEntityWrapper.setName(name);
		}
		privateEntityWrapper.setSearchToken(searchToken);

	}

	public PrivateEntityWrapper getPrivateEntityWrapper(String searchToken, String name, Timestamp timestamp) {
		return getPrivateEntityWrapperByFileds(searchToken, null, null, name, timestamp);
	}

	public PrivateEntityWrapper getPrivateEntityWrapperByFileds(String searchToken, String jobId, String state, String name,
			Timestamp timestamp) {

		PrivateEntityWrapper privateEntityWrapper = new PrivateEntityWrapper();
		populatePrivateEntityWrapper(privateEntityWrapper, searchToken, name);
		privateEntityWrapper.setJobId(jobId);
		privateEntityWrapper.setState(state);

		if (timestamp != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss z");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			privateEntityWrapper.setCreatedDate(sdf.format(new Date(timestamp.getTime())));
		}

		return privateEntityWrapper;
	}

	public PrivateEntityWrapper getPrivateEntityWrapper(String searchToken, String definition, UpdateDefinition ud, String name)
			throws JsonParseException, IOException {

		PrivateEntityWrapper privateEntityWrapper = getPrivateEntityWrapper(ud, true, false);
		populatePrivateEntityWrapper(privateEntityWrapper, searchToken, name);
		privateEntityWrapper.setDefinition(privateEntityService.getJsonNodeRes(definition));
		return privateEntityWrapper;
	}

	private PrivateEntityWrapper populatePrivateEntityForList(PrivateEntityList pe) throws JsonParseException, IOException {

		PrivateEntityWrapper privateEntityWrapper = new PrivateEntityWrapper();

		if (pe == null) {
			return privateEntityWrapper;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss z");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

		privateEntityWrapper.setName(pe.getName());
		privateEntityWrapper.setSearchToken(pe.getSearchToken());
		privateEntityWrapper.setState(STATUS_DEV.equalsIgnoreCase(pe.getStatus()) || STATUS_LIVE.equalsIgnoreCase(pe.getStatus())
				? STATUS_ACTIVE : STATUS_IN_ACTIVE);
		privateEntityWrapper.setCreatedDate(sdf.format(new Date(pe.getInsertTime().getTime())));
		privateEntityWrapper.setVersions(populateVersions(pe));

		return privateEntityWrapper;
	}

	private List<Version> populateVersions(PrivateEntityList pe) {

		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss z");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

		List<Version> versions = new ArrayList<Version>();
		
		if (StringUtils.isNotEmpty(pe.getDefinition_dev())) {
			Version version = new Version();
			version.setName(STATUS_DEV);
			String lastModifiedBy = StringUtils.isNotEmpty(pe.getUser()) ? pe.getUser()
					: StringUtils.isNotEmpty(pe.getEmail()) ? pe.getEmail()
							: StringUtils.isNotEmpty(pe.getCreatedByUser()) ? pe.getCreatedByUser() : pe.getCreatedByEmail();
			version.setLastModifiedBy(lastModifiedBy);
			Timestamp updateTime = pe.getUpdateTime();
			if (updateTime == null) {
				updateTime = pe.getInsertTime();
			}
			version.setLastModifiedDate(sdf.format(new Date(updateTime.getTime())));
			versions.add(version);
		}
		
		if (StringUtils.isNotEmpty(pe.getDefinition_live())) {
			Version version = new Version();
			version.setName(STATUS_LIVE);
			version.setLastModifiedBy(
					StringUtils.isNotEmpty(pe.getLastPromotedByUser()) ? pe.getLastPromotedByUser() : pe.getLastPromotedByEmail());
			version.setLastModifiedDate(sdf.format(new Date(pe.getLast_promoted().getTime())));
			versions.add(version);
			
		}

		return versions;
	}

	public PrivateEntityWrapper getPrivateEntityWrapper(List<PrivateEntityList> privateEntities) throws JsonParseException, IOException {

		PrivateEntityWrapper privateEntityWrapper = new PrivateEntityWrapper();

		if (CollectionUtils.isEmpty(privateEntities)) {
			List<PrivateEntityWrapper> pEntities = new ArrayList<PrivateEntityWrapper>();
			privateEntityWrapper.setEntities(pEntities);
			return privateEntityWrapper;
		}

		List<PrivateEntityWrapper> pEntities = new ArrayList<PrivateEntityWrapper>();
		for (PrivateEntityList privateEntity : privateEntities) {

			if (privateEntity == null) {
				continue;
			}

			pEntities.add(populatePrivateEntityForList(privateEntity));
		}

		privateEntityWrapper.setEntities(pEntities);
		return privateEntityWrapper;
	}


	public PrivateEntityWrapper populatePrivateEntityWrapper(EntityBacktest entityBacktest)
			throws JsonProcessingException, IOException {

		PrivateEntityWrapper privateEntityWrapper = new PrivateEntityWrapper();
		populatePrivateEntityWrapper(privateEntityWrapper, entityBacktest.getSearchToken(), null);

		String status = entityBacktest.getStatus();
		if (STATUS_COMPLETED.equalsIgnoreCase(status)) {
			if (entityBacktest.getResult_json() != null && !entityBacktest.getResult_json().isEmpty()) {
				List<ResultJsonRes> resultJsonResList = new LinkedList<ResultJsonRes>();
				List<CatEntity> resultJson =
						JSONUtility.deserialize(entityBacktest.getResult_json(), new TypeReference<ArrayList<CatEntity>>() {});
				for (CatEntity ce : resultJson) {
					ResultJsonRes reJsonRes = new ResultJsonRes();
					reJsonRes.setCatId(ce.getCatId());
					reJsonRes.setBody(ce.getBody());
					reJsonRes.setDocId(ce.getDocId());
					reJsonRes.setRelevanceBand(ce.getBand());
					reJsonRes.setRelevanceScore(ce.getScore());
					reJsonRes.setTitle(ce.getTitle());
					List<Location> location = new ArrayList<Location>();
					if (ce.getCharCount() != null) {
						createAndAddLocation(ce, location); 
						reJsonRes.setLocation(location);
					}
					resultJsonResList.add(reJsonRes);
				}
				privateEntityWrapper.setDocuments(resultJsonResList);
			}
		}

		privateEntityWrapper.setProgress(
				STATUS_NEW.equalsIgnoreCase(status) || STATUS_IN_PROGRESS.equalsIgnoreCase(status) ? STATUS_IN_PROGRESS : status);


		return privateEntityWrapper;

	}
 
	private void createAndAddLocation(final CatEntity ce, final List<Location> location) { 
		Location loc = new Location(); 
		loc.setCharCount(ce.getCharCount()); 
		loc.setCharOffset(ce.getCharOffset()); 
		location.add(loc); 
	} 

	public PrivateEntityWrapper getPrivateEntityWrapper(UpdateDefinition updateDefinition, boolean isState, boolean isCreatedDate) {

		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss z");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

		PrivateEntityWrapper privateEntityWrapper = new PrivateEntityWrapper();

		if (updateDefinition == null) {
			return privateEntityWrapper;
		}

		List<PrivateEntity> privateEntities = updateDefinition.getPrivateEntities();
		List<EntityHistory> entityHistories = updateDefinition.getEntityHistory();

		String lastModifiedBy = null;
		String lastModifiedDate = null;
		PrivateEntity pe = privateEntities.get(0);
		if (CollectionUtils.isNotEmpty(entityHistories)) {
			EntityHistory eh = entityHistories.get(0);
			lastModifiedBy = eh.getUser() != null ? eh.getUser() : eh.getEmail();
			lastModifiedDate = sdf.format(new Date(eh.getUpdateTime().getTime()));
		} else {
			lastModifiedBy = pe.getCreatedByUser() != null ? pe.getCreatedByUser() : pe.getCreatedByEmail();
			lastModifiedDate = sdf.format(new Date(pe.getInsertTime().getTime()));
			/*
			 * if (pe.getUpdateTime() != null) {
			 * 
			 * } else if (updateDefinition.getUpdateTime() != null) { lastModifiedDate = sdf.format(new
			 * Date(updateDefinition.getUpdateTime().getTime())); }
			 */
		}

		privateEntityWrapper.setName(pe.getName());
		privateEntityWrapper.setSearchToken(pe.getSearchToken());
		privateEntityWrapper.setLastModifiedBy(lastModifiedBy);
		privateEntityWrapper.setLastModifiedDate(lastModifiedDate);

		if (isState) {
			privateEntityWrapper.setState(STATUS_DEV.equalsIgnoreCase(pe.getStatus()) || STATUS_LIVE.equalsIgnoreCase(pe.getStatus())
					? STATUS_ACTIVE : STATUS_IN_ACTIVE);
		}

		if (isCreatedDate) {
			privateEntityWrapper.setCreatedDate(sdf.format(new Date(pe.getInsertTime().getTime())));
		}

		return privateEntityWrapper;
	}
}
