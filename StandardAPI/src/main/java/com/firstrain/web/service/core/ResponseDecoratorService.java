package com.firstrain.web.service.core;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Service;

import com.firstrain.frapi.domain.CompanyTradingRange;
import com.firstrain.frapi.domain.CompanyVolume;
import com.firstrain.frapi.domain.HistoricalStat;
import com.firstrain.frapi.domain.ItemDetail;
import com.firstrain.frapi.domain.MetaEvent;
import com.firstrain.frapi.domain.MgmtTurnoverMeta;
import com.firstrain.frapi.domain.MonitorEmail;
import com.firstrain.frapi.domain.SectionSpec;
import com.firstrain.frapi.domain.VisualizationData;
import com.firstrain.frapi.domain.VisualizationData.ChartType;
import com.firstrain.frapi.domain.VisualizationData.Graph;
import com.firstrain.frapi.domain.VisualizationData.Node;
import com.firstrain.frapi.pojo.AuthAPIResponse;
import com.firstrain.frapi.pojo.EmailDetail;
import com.firstrain.frapi.pojo.EmailResponse;
import com.firstrain.frapi.pojo.Entity;
import com.firstrain.frapi.pojo.EntityBriefInfo;
import com.firstrain.frapi.pojo.MonitorAPIResponse;
import com.firstrain.frapi.pojo.MonitorBriefDetail;
import com.firstrain.frapi.pojo.MonitorEmailAPIResponse;
import com.firstrain.frapi.pojo.SearchAPIResponse;
import com.firstrain.frapi.pojo.UserAPIResponse;
import com.firstrain.frapi.pojo.wrapper.BaseSet.SectionType;
import com.firstrain.frapi.pojo.wrapper.DocumentSet;
import com.firstrain.frapi.pojo.wrapper.TweetSet;
import com.firstrain.frapi.util.DefaultEnums.CoversationStarterType;
import com.firstrain.frapi.util.DefaultEnums.INPUT_ENTITY_TYPE;
import com.firstrain.frapi.util.DefaultEnums.MatchedEntityConfidenceScore;
import com.firstrain.frapi.util.DefaultEnums.RelevanceBand;
import com.firstrain.frapi.util.FRAPIConstant;
import com.firstrain.frapi.util.StatusCode;
import com.firstrain.mip.object.FR_ICategory;
import com.firstrain.web.pojo.AuthKey;
import com.firstrain.web.pojo.ChartData;
import com.firstrain.web.pojo.Content;
import com.firstrain.web.pojo.DnBEntityStatus;
import com.firstrain.web.pojo.Document;
import com.firstrain.web.pojo.EntityData;
import com.firstrain.web.pojo.EntityDataHtml;
import com.firstrain.web.pojo.EntityStandard;
import com.firstrain.web.pojo.Event;
import com.firstrain.web.pojo.MetaData;
import com.firstrain.web.pojo.MonitorConfig;
import com.firstrain.web.pojo.MonitorInfo;
import com.firstrain.web.pojo.MonitorSearch;
import com.firstrain.web.pojo.Source;
import com.firstrain.web.pojo.Tweet;
import com.firstrain.web.pojo.WebVolume;
import com.firstrain.web.pojo.WebVolumeGraph;
import com.firstrain.web.response.AuthKeyResponse;
import com.firstrain.web.response.DnBEntityStatusResponse;
import com.firstrain.web.response.EntityDataResponse;
import com.firstrain.web.response.ItemWrapperResponse;
import com.firstrain.web.response.JSONResponse;
import com.firstrain.web.response.JSONResponse.ResStatus;
import com.firstrain.web.response.MonitorConfigResponse;
import com.firstrain.web.response.MonitorInfoResponse;
import com.firstrain.web.response.UserIdWrapperResponse;
import com.firstrain.web.service.BaseResponseDecoratorService;
import com.firstrain.web.util.AuthAPIResponseThreadLocal;
import com.firstrain.web.util.UserInfoThreadLocal;
import com.firstrain.web.wrapper.ChartDataWrapper;
import com.firstrain.web.wrapper.DnBEntityStatusListWrapper;
import com.firstrain.web.wrapper.EntityDataWrapper;
import com.firstrain.web.wrapper.EntityMap;
import com.firstrain.web.wrapper.EntityWrapper;
import com.firstrain.web.wrapper.ItemWrapper;
import com.firstrain.web.wrapper.UserIdWrapper;

@Service
public class ResponseDecoratorService extends BaseResponseDecoratorService {

	@Value("${app.base.url}")
	private String appBaseUrl;
	@Value("${tweet.exclusion.list}")
	private String tweetExclusionCSV;

	@Autowired
	private ResourceBundleMessageSource messageSource;
	private Set<SectionType> chartSectionIDs;

	@PostConstruct
	private void init() {
		chartSectionIDs = new HashSet<SectionType>();
		chartSectionIDs.add(SectionType.TT);
		chartSectionIDs.add(SectionType.BI);
		chartSectionIDs.add(SectionType.MD);
		chartSectionIDs.add(SectionType.TWT);
		chartSectionIDs.add(SectionType.GL);
		chartSectionIDs.add(SectionType.RL);
	}

	public AuthKeyResponse getAuthKeyResponse(AuthAPIResponse apiRes) {
		AuthKeyResponse res = new AuthKeyResponse();
		res.setStatus(ResStatus.SUCCESS);
		String message = null;
		if (apiRes.getStatusCode() == 116) {
			message = messageSource.getMessage(("authkey.succ.expired"), null, Locale.getDefault());
		} else {
			message = messageSource.getMessage(("authkey.succ"), null, Locale.getDefault());
		}
		res.setMessage(message);
		res.setVersion(apiRes.getApiVersion());
		AuthKey data = new AuthKey();
		data.setAuthKey(apiRes.getAuthKey());
		// date in UTC - 18 Sep 2014 07:16:43 UTC
		SimpleDateFormat formater = new SimpleDateFormat("dd MMM yyyy HH:mm:ss z");
		formater.setTimeZone(TimeZone.getTimeZone("UTC"));
		String validTill = formater.format(new Date(apiRes.getExpiryTime().getTime()));
		data.setValidTill(validTill);
		res.setResult(data);
		return res;
	}

	public UserIdWrapperResponse getUserIdResponse(UserAPIResponse apiRes, String msgKey) {
		UserIdWrapperResponse res = new UserIdWrapperResponse();
		populateStatusMessageAndVersion(res, msgKey);
		UserIdWrapper data = new UserIdWrapper();
		data.setUserId(FRAPIConstant.USER_ID_PREFIX + apiRes.getUser().getUserId());
		res.setResult(data);
		return res;
	}

	@SuppressWarnings("rawtypes")
	public JSONResponse getDeleteUserResponse(String msgKey) {
		return getSuccessMsg(msgKey); 
	}
	
	public MonitorInfoResponse getAddRemoveEntityResponse(MonitorAPIResponse apiRes, String msgKey) {
		MonitorInfoResponse res = new MonitorInfoResponse();
		res.setStatus(ResStatus.SUCCESS);
		String message = messageSource.getMessage(msgKey, null, Locale.getDefault());
		res.setVersion(Constant.getVersion());
		MonitorInfo data = new MonitorInfo();
		data.setMonitorId(FRAPIConstant.MONITOR_PREFIX + Long.toString(apiRes.getMonitorId()));
		data.setMonitorName(apiRes.getMonitorName());
		if (apiRes.getEntities() != null) {
			List<EntityWrapper> entities = new ArrayList<EntityWrapper>();
			data.setEntities(entities);
			for (com.firstrain.frapi.pojo.Entity ent : apiRes.getEntities()) {
				EntityStandard entity = new EntityStandard();
				entity.setName(ent.getName());
				entity.setSearchToken(ent.getSearchToken());
				entity.setRemoved(ent.getRemoved());
				entity.setAdded(ent.getAdded());
				if (Boolean.FALSE.equals(ent.getRemoved())) {
					entity.setErrorCode(StatusCode.RSOURCE_NOT_FOUND);
					entity.setMessage(
							messageSource.getMessage("entity.errorcode." + StatusCode.RSOURCE_NOT_FOUND, null, Locale.getDefault()));
					message = messageSource.getMessage("errorcode." + StatusCode.PARTIAL_SUCCESS, null, Locale.getDefault());
					res.setStatus(ResStatus.PARTIAL_SUCCESS);
					res.setErrorCode(StatusCode.PARTIAL_SUCCESS);
				} else if (ent.getRemoved() == null && ent.getAdded() == null) {
					entity.setErrorCode(StatusCode.RSOURCE_NOT_FOUND);
					entity.setMessage(
							messageSource.getMessage("entity.errorcode." + StatusCode.RSOURCE_NOT_FOUND, null, Locale.getDefault()));
					message = messageSource.getMessage("errorcode." + StatusCode.PARTIAL_SUCCESS, null, Locale.getDefault());
					res.setStatus(ResStatus.PARTIAL_SUCCESS);
					res.setErrorCode(StatusCode.PARTIAL_SUCCESS);
				} else if (Boolean.FALSE.equals(ent.getAdded())) {
					entity.setErrorCode(StatusCode.ENTITY_ALREADY_EXISTS);
					entity.setMessage(messageSource.getMessage("errorcode." + StatusCode.ENTITY_ALREADY_EXISTS, null, Locale.getDefault()));
					message = messageSource.getMessage("errorcode." + StatusCode.PARTIAL_SUCCESS, null, Locale.getDefault());
					res.setStatus(ResStatus.PARTIAL_SUCCESS);
					res.setErrorCode(StatusCode.PARTIAL_SUCCESS);
				}
				EntityWrapper entityWrapper = new EntityWrapper();
				entityWrapper.setEntity(entity);
				entities.add(entityWrapper);
			}
		}
		if (apiRes.getStatusCode() == StatusCode.NO_ITEMS_IN_MONITOR) {
			message += messageSource.getMessage("monitor.removeentity.noitems", null, Locale.getDefault());
		}
		res.setMessage(message);
		res.setResult(data);
		return res;
	}

	public MonitorConfigResponse getMonitorConfigResponse(com.firstrain.frapi.domain.MonitorConfig apiRes) {
		MonitorConfigResponse res = new MonitorConfigResponse();
		res.setStatus(ResStatus.SUCCESS);
		String message = messageSource.getMessage("gen.succ", null, Locale.getDefault());
		res.setVersion(Constant.getVersion());

		MonitorConfig data = new MonitorConfig();
		data.setId(FRAPIConstant.MONITOR_PREFIX + apiRes.getMonitorId());
		data.setName(apiRes.getMonitorName());
		data.setFilters(apiRes.getFilters());
		if (apiRes.getQueries() != null) {
			List<MonitorSearch> queries = new ArrayList<MonitorSearch>();
			data.setQueries(queries);
			for (ItemDetail item : apiRes.getQueries()) {
				MonitorSearch search = new MonitorSearch();
				search.setQueryName(item.getQueryName());
				search.setQueryString(item.getQueryString());
				queries.add(search);
			}
			if (apiRes.getQueries().isEmpty()) {
				message += messageSource.getMessage("monitor.config.noitems", null, Locale.getDefault());
			}
		}
		res.setMessage(message);
		res.setResult(data);
		return res;
	}
	
	@Override
	protected String setRelevanceBand(Integer relevanceBandParam) {
		Integer relevanceBand = relevanceBandParam;
		String relevanceBandStr;
		if (relevanceBand == null) {
			relevanceBand = (int) RelevanceBand.LOW.getValue();
		}
		if (relevanceBand == MatchedEntityConfidenceScore.VERY_HIGH.getValue()) {
			relevanceBandStr = MatchedEntityConfidenceScore.VERY_HIGH.getName();
		} else if (relevanceBand == RelevanceBand.HIGH.getValue()) {
			relevanceBandStr = RelevanceBand.HIGH.getName();
		} else if (relevanceBand == RelevanceBand.MEDIUM.getValue()) {
			relevanceBandStr = RelevanceBand.MEDIUM.getName();
		} else {
			relevanceBandStr = RelevanceBand.LOW.getName();
		}
		return relevanceBandStr;
	}

	public ItemWrapperResponse getItemWrapperResponse(TweetSet tweetSet, String msgKey, Short industryClassificationId,
			boolean excludeTweetInfo) {
		com.firstrain.frapi.domain.Tweet tweetRst = tweetSet.getTweets().get(0);
		Tweet tweet = tweetConvertor(tweetRst, industryClassificationId, excludeTweetInfo);
		ItemWrapper itemWrapper = getItemWrapper(tweet);
		ItemWrapperResponse res = getItemWrapperResponse(msgKey);
		res.setResult(itemWrapper);
		return res;
	}
	
	private Content getTweetContent(List<com.firstrain.frapi.domain.Tweet> tweetLstRst, 
			Short industryClassificationId, boolean excludeTweetInfo) {
		Content content = new Content();
		List<Tweet> tweetLst = createTweetListAndPopulate(tweetLstRst, industryClassificationId, excludeTweetInfo, content); 
		content.setItemCount(tweetLst.size());
		return content;
	}

	public EntityDataResponse getEntityDataResponse(MonitorAPIResponse monitorApiRes, String msgKey,
			Map<SectionType, SectionSpec> sectionsMap, Short industryClassificationId, boolean excludeTweetInfo) {
		EntityDataResponse res = getEntityDataResponse(monitorApiRes, msgKey, false);
		EntityDataWrapper entityDataWrapper = new EntityDataWrapper();
		EntityData entityData = createEntityDataAndPopulateResult(entityDataWrapper, res);

		MonitorBriefDetail monitorBriefDetail = monitorApiRes.getMonitorBriefDetail();
		if (monitorBriefDetail != null) {
			entityDataWrapper.setId(FRAPIConstant.MONITOR_PREFIX + monitorBriefDetail.getMonitorId());
			entityDataWrapper.setName(monitorBriefDetail.getMonitorName());

			Content docContent = getDocumentsContent(monitorBriefDetail);
			if(docContent != null) {
				setContentItemCountOffset(docContent, sectionsMap.get(SectionType.FR), 
						docContent.getTotalItemCount(), docContent.getItemCount());
				entityData.setFr(docContent);
			}
			
			com.firstrain.frapi.pojo.wrapper.TweetSet tweetSet = monitorBriefDetail.getTweetList();
			List<com.firstrain.frapi.domain.Tweet> tweetLstRst = getTweetLst(tweetSet);
			if (tweetLstRst != null && !tweetLstRst.isEmpty()) {
				Content tweetContent = getTweetContent(tweetLstRst, industryClassificationId, excludeTweetInfo);
				setContentItemCountOffset(tweetContent, sectionsMap.get(SectionType.FT), 
						tweetSet.getTotal(), tweetContent.getItemCount());
				entityData.setFt(tweetContent);
			}
			
			entityDataWrapper.setMetaData(getMetaDataEntitiyResponse(res));
			if (monitorBriefDetail.getVisualizationData() != null) {
				setChartData(sectionsMap.keySet(), res, monitorBriefDetail.getVisualizationData());
			}
		}
		return res;
	}

	public EntityDataResponse getEmailResponse(EmailResponse emailRes, String msgKey) {

		EntityDataResponse res = new EntityDataResponse();
		res.setStatus(ResStatus.SUCCESS);
		String message = messageSource.getMessage(msgKey, null, Locale.getDefault());
		if (emailRes.getStatusCode() == StatusCode.NO_ITEMS_IN_MONITOR) {
			message += messageSource.getMessage("monitor.brief.noitems", null, Locale.getDefault());
		}
		EntityDataWrapper entityDataWrapper = populateMessageAndVersionAndCreateEntityDataWrapper(res, message);
		EntityData entityData = createEntityDataAndPopulateResult(entityDataWrapper, res);

		entityDataWrapper.setId(FRAPIConstant.EMAIL_PREFIX + emailRes.getEmailId());
		entityDataWrapper.setName(emailRes.getEmailName());

		com.firstrain.web.pojo.EmailDetail emailDetailRst = new com.firstrain.web.pojo.EmailDetail();
		EmailDetail emailDetail = emailRes.getEmailDetail();
		// set Content Document
		List<com.firstrain.web.pojo.Search> searches = new ArrayList<com.firstrain.web.pojo.Search>();
		for (com.firstrain.frapi.domain.Search search : emailDetail.getSearches()) {
			List<com.firstrain.web.pojo.Document> docs = new ArrayList<com.firstrain.web.pojo.Document>();
			for (com.firstrain.frapi.domain.Document document : search.getDocuments()) {
				Document doc = documentConvertor(document);
				doc.setId(FRAPIConstant.DOCUMENT_PREFIX + doc.getId());
				docs.add(doc);
			}
			com.firstrain.web.pojo.Search searchRst = new com.firstrain.web.pojo.Search();
			searchRst.setDocuments(docs);
			searchRst.setSearchFilter(search.getSearchFilter());
			searchRst.setSearchId(FRAPIConstant.SEARCH_PREFIX + search.getSearchId());
			searchRst.setSearchName(search.getSearchName());
			searchRst.setSearchQuery(search.getSearchQuery());
			searches.add(searchRst);
		}
		emailDetailRst.setSearches(searches);
		entityData.setEmailTemplate(emailRes.getEmailTemplate());
		entityData.setEmailDetail(emailDetailRst);
		return res;
	}

	public EntityDataResponse getEntityDataResponse(MonitorEmailAPIResponse emailRes, String msgKey) {

		EntityDataResponse res = new EntityDataResponse();
		res.setStatus(ResStatus.SUCCESS);
		String message = messageSource.getMessage(msgKey, null, Locale.getDefault());
		if (emailRes.getStatusCode() == StatusCode.NO_ITEMS_IN_MONITOR) {
			message += messageSource.getMessage("monitor.brief.noitems", null, Locale.getDefault());
		}
		EntityDataWrapper entityDataWrapper = populateMessageAndVersionAndCreateEntityDataWrapper(res, message);
		EntityData entityData = createEntityDataAndPopulateResult(entityDataWrapper, res);

		entityDataWrapper.setId(FRAPIConstant.MONITOR_PREFIX + emailRes.getMonitorId());
		entityDataWrapper.setName(emailRes.getMonitorName());

		List<MonitorEmail> emails = emailRes.getEmails();
		entityData.setEmails(emails);
		entityData.setEmailTemplate(emailRes.getEmailTemplate());
		return res;
	}

	private EntityData createEntityDataAndPopulateResult(final EntityDataWrapper entityDataWrapper, final EntityDataResponse res) {
		EntityData entityData = new EntityData();
		entityDataWrapper.setData(entityData);
		res.setResult(entityDataWrapper);
		return entityData;
	}


	public EntityDataResponse getEntityDataResponse(SearchAPIResponse searchAPIResponse, String msgKey,
			Map<SectionType, SectionSpec> sectionsMap, Map<String, Object> ftlParams) {

		EntityDataResponse res = new EntityDataResponse();
		res.setStatus(ResStatus.SUCCESS);
		String message = messageSource.getMessage(msgKey, null, Locale.getDefault());
		EntityDataWrapper entityDataWrapper = populateMessageAndVersionAndCreateEntityDataWrapper(res, message);
		EntityData entityData = new EntityData();
		DocumentSet documentSet = searchAPIResponse.getWebResults();
		// company name required for cross section heading
		Map<String, Entity> entityMap = searchAPIResponse.getTokenVsEntityMap();
		if (entityMap != null) {
			String name2 = ftlParams.get("name2").toString();
			if (entityMap.get(name2) != null) {
				ftlParams.put("name2", entityMap.get(name2).getName());
			}
		}

		if (documentSet != null) {
			// set Content Document
			Map<String, List<com.firstrain.frapi.domain.Document>> documentSetMapRst = documentSet.getDocumentBucket();
			if (documentSetMapRst != null && !documentSetMapRst.isEmpty()) {
				Map<String, List<Document>> documentSetMap = new LinkedHashMap<String, List<Document>>();

				Content content = new Content();
				for (Map.Entry<String, List<com.firstrain.frapi.domain.Document>> mapRst : documentSetMapRst.entrySet()) {
					List<com.firstrain.frapi.domain.Document> documentLstRst = mapRst.getValue();
					if (documentLstRst != null && !documentLstRst.isEmpty()) {
						List<Document> documentLst = new ArrayList<Document>();
						for (com.firstrain.frapi.domain.Document documentRst : documentLstRst) {
							documentLst.add(documentConvertor(documentRst));
						}
						documentSetMap.put(mapRst.getKey(), documentLst);
					}
				}
				content.setDocumentBuckets(documentSetMap);
				entityData.setCs(content);
			}
		}
		entityDataWrapper.setData(entityData);
		res.setResult(entityDataWrapper);
		return res;
	}

	public EntityDataResponse getConversationStartersResponse(SearchAPIResponse searchAPIResponse, Map<String, String> leadcompany,
			Map<String, CoversationStarterType> coversationStarterMap, int start, int countParam) {
		int count = countParam;
		EntityDataResponse res = new EntityDataResponse();
		res.setStatus(ResStatus.SUCCESS);
		String message = messageSource.getMessage("gen.succ", null, Locale.getDefault());
		EntityDataWrapper entityDataWrapper = populateMessageAndVersionAndCreateEntityDataWrapper(res, message);
		if (leadcompany != null) {
			entityDataWrapper.setId(leadcompany.get("token"));
			entityDataWrapper.setName(leadcompany.get("name"));
		}

		EntityData entityData = new EntityData();
		entityDataWrapper.setData(entityData);

		DocumentSet documentSet = searchAPIResponse.getWebResults();
		if (documentSet != null) {
			// set Content Document
			Map<String, List<com.firstrain.frapi.domain.Document>> documentSetMapRst = documentSet.getDocumentBucket();
			if (documentSetMapRst != null && !documentSetMapRst.isEmpty()) {
				Map<String, Document> documentsMap = new HashMap<String, Document>();
				for (Map.Entry<String, List<com.firstrain.frapi.domain.Document>> mapRst : documentSetMapRst.entrySet()) {
					// to be used for tagging
					String coversationStarterType = coversationStarterMap.get(mapRst.getKey()).getLabel();
					List<com.firstrain.frapi.domain.Document> documentLstRst = mapRst.getValue();
					if (documentLstRst != null && !documentLstRst.isEmpty()) {
						for (com.firstrain.frapi.domain.Document documentRst : documentLstRst) {
							String docId = documentRst.getId();
							if (documentsMap.get(docId) != null) {
								Document doc = documentsMap.get(docId);
								// add tag here
								doc.getConversationStarterType().add(coversationStarterType);
							} else {
								Document doc = documentConvertor(documentRst);
								// add tag here
								List<String> coversationStarterTypeList = new ArrayList<String>();
								coversationStarterTypeList.add(coversationStarterType);
								doc.setConversationStarterType(coversationStarterTypeList);
								documentsMap.put(docId, doc);
							}

						}
					}
				}
				List<Document> documentList = new ArrayList<Document>(documentsMap.values());
				Collections.sort(documentList, new Comparator<Document>() {
					@Override
					public int compare(Document d1, Document d2) {
						return d2.getDate().compareTo(d1.getDate());
					}
				});
				// return documents on the basis of start and count
				if (documentList.size() < start + count) {
					if (documentList.size() <= start) {
						count = 0;
					} else {
						count = documentList.size() - start;
					}
				}
				if (count > 0) {
					entityData.setConversationStarters(documentList.subList(start, (start + count)));
				}
				entityData.setItemCount(count);
				entityData.setItemOffset(start);
				entityData.setTotalItemCount(documentList.size());
			}
		}
		res.setResult(entityDataWrapper);
		return res;
	}

	private void setContentItemCountOffset(Content content, SectionSpec ss, Integer totalItemCount, int size) {
		if (ss != null) {
			if (ss.getStart() != null) {
				content.setItemOffset((int) ss.getStart());
			}
			if (ss.getCount() != null) {
				content.setItemCount(size);
			}
		}
		content.setTotalItemCount(totalItemCount);
	}

	public EntityDataResponse getEntityDataResponse(EntityBriefInfo entityBriefInfo, String msgKey,
			Map<SectionType, SectionSpec> sectionsMap, HttpServletResponse response, Short industryClassificationId,
			boolean excludeTweetInfo, boolean isNeedPhrase, String scope) {
		return getEntityDataResponse(entityBriefInfo, msgKey, sectionsMap, response, industryClassificationId, false, excludeTweetInfo,
				isNeedPhrase, scope);
	}

	public EntityDataResponse getEntityDataResponse(EntityBriefInfo entityBriefInfo, String msgKey,
			Map<SectionType, SectionSpec> sectionsMap, HttpServletResponse response, Short industryClassificationId,
			boolean excludeTweetInfo) {
		return getEntityDataResponse(entityBriefInfo, msgKey, sectionsMap, response, industryClassificationId, false, excludeTweetInfo,
				false, null);
	}

	public EntityDataResponse getEntityDataResponse(EntityBriefInfo entityBriefInfo, String msgKey,
			Map<SectionType, SectionSpec> sectionsMap, HttpServletResponse response, Short industryClassificationId, boolean isMTEvent,
			boolean excludeTweetInfo, boolean isNeedPhrase, String scope) {

		EntityDataResponse res = new EntityDataResponse();
		res.setStatus(ResStatus.SUCCESS);
		String message = messageSource.getMessage(msgKey, null, Locale.getDefault());
		EntityDataWrapper entityDataWrapper = populateMessageAndVersionAndCreateEntityDataWrapper(res, message);
		EntityData entityData = new EntityData();

		entityData.setScopeDirective(entityBriefInfo.getScopeDirective());
		
		boolean isContentAvailable = false;

		// set Content Document
		com.firstrain.frapi.pojo.wrapper.DocumentSet documentSet = entityBriefInfo.getWebResults();
		if (documentSet != null) {
			List<com.firstrain.frapi.domain.Document> documentLstRst = documentSet.getDocuments();
			if (documentLstRst != null && !documentLstRst.isEmpty()) {
				Content content = getContent(documentLstRst, sectionsMap, documentSet, SectionType.FR);
				entityData.setFr(content);
				isContentAvailable = true;
			}

			//

		}
		// set Content tweets
		com.firstrain.frapi.pojo.wrapper.TweetSet tweetSet = entityBriefInfo.getTweetList();
		if (tweetSet != null) {
			List<com.firstrain.frapi.domain.Tweet> tweetLstRst = tweetSet.getTweets();
			if (tweetLstRst != null && !tweetLstRst.isEmpty()) {
				Content content = new Content();
				List<Tweet> tweetLst = createTweetListAndPopulate(tweetLstRst, industryClassificationId, excludeTweetInfo, content); 
				content.setTweetAccelerometer(tweetSet.getTweetAccelerometer());
				setContentItemCountOffset(content, sectionsMap.get(SectionType.FT), tweetSet.getTotalCount(), tweetLst.size());
				entityData.setFt(content);
				isContentAvailable = true;
			}
		}

		// set Content AnalystCommnets
		documentSet = entityBriefInfo.getAnalystComments();
		if (documentSet != null) {
			List<com.firstrain.frapi.domain.Document> documentLstRst = documentSet.getDocuments();
			if (documentLstRst != null && !documentLstRst.isEmpty()) {
				Content content = getContent(documentLstRst, sectionsMap, documentSet, SectionType.AC);
				entityData.setAc(content);
				isContentAvailable = true;
			}
		}

		// set Content events timeline buckets for html
		com.firstrain.frapi.pojo.wrapper.EventSet eventSet = entityBriefInfo.getEventsTimeline();
		if (eventSet != null) {
			Map<String, List<com.firstrain.frapi.pojo.Event>> eventLstMapRst = eventSet.getEventBuckets();
			if (eventLstMapRst != null && !eventLstMapRst.isEmpty()) {
				Content content = new Content();
				Map<String, List<Event>> eventLstMap = new LinkedHashMap();
				int size = 0;
				for (Map.Entry<String, List<com.firstrain.frapi.pojo.Event>> eventMap : eventLstMapRst.entrySet()) {
					List<Event> eventLst = new ArrayList<Event>();
					for (com.firstrain.frapi.pojo.Event eventRst : eventMap.getValue()) {
						eventLst.add(eventConvertor(eventRst));
					}
					size += eventLst.size();
					eventLstMap.put(eventMap.getKey(), eventLst);
				}
				content.setEventBuckets(eventLstMap);
				setContentItemCountOffset(content, sectionsMap.get(SectionType.E), eventSet.getTotalCount(), size);
				entityData.setBucketedEvents(content);
				isContentAvailable = true;
			}
		}
		// set Content flat events for json
		eventSet = entityBriefInfo.getEventsTimeline();
		if (eventSet != null) {
			List<com.firstrain.frapi.pojo.Event> eventLstRst = eventSet.getEvents();
			if (eventLstRst != null && !eventLstRst.isEmpty()) {
				Content content = new Content();
				List<Event> eventLst = new ArrayList<Event>();
				for (com.firstrain.frapi.pojo.Event eventRst : eventLstRst) {
					eventLst.add(eventConvertor(eventRst));
				}
				content.setEvents(eventLst);
				setContentItemCountOffset(content, sectionsMap.get(SectionType.E), eventSet.getTotalCount(), eventLst.size());
				entityData.setE(content);
				isContentAvailable = true;
			}
		}

		// set Content for web volume graph json
		com.firstrain.frapi.pojo.Graph graph = entityBriefInfo.getWebVolumeGraph();
		if (graph != null) {
			SimpleDateFormat sdf02 = new SimpleDateFormat("yyyy-MM-dd z");
			sdf02.setTimeZone(TimeZone.getTimeZone("PST"));
			List<HistoricalStat> statList = graph.getHistoricalStat();
			if (statList != null && !statList.isEmpty()) {
				WebVolumeGraph wvg = new WebVolumeGraph();
				List<WebVolume> wvList = new ArrayList<WebVolume>();
				String lastClosingPrice = null;
				for (HistoricalStat stat : statList) {
					WebVolume wv = new WebVolume();
					CompanyVolume cv = stat.getCompanyVolume();
					if (cv != null) {
						wv.setDate(sdf02.format(cv.getDate()));
						wv.setValue(cv.getTotal());
					}
					CompanyTradingRange ctr = stat.getTradeRange();
					if (ctr != null) {
						lastClosingPrice = ctr.getClosingPriceStr();
					}
					wv.setStockprice(lastClosingPrice);
					List<com.firstrain.frapi.pojo.Event> eventList = stat.getSignals();
					if (eventList != null && !eventList.isEmpty()) {
						// only one event is populated for a single day in top 40 events algo
						wv.setEvent(eventConvertor(eventList.get(0)));
					}
					wvList.add(wv);
				}
				wvg.setWebVolume(wvList);
				entityData.setWv(wvg);
				isContentAvailable = true;
			}
		}

		// set Content turnover events
		eventSet = entityBriefInfo.getMgmtChangeEvents();
		if (eventSet != null) {
			List<com.firstrain.frapi.pojo.Event> eventLstRst = eventSet.getEvents();
			if (eventLstRst != null && !eventLstRst.isEmpty()) {
				Content content = new Content();
				List<Event> eventLst = new ArrayList<Event>();
				for (com.firstrain.frapi.pojo.Event eventRst : eventLstRst) {
					eventLst.add(eventConvertor(eventRst, isMTEvent));
				}
				content.setEvents(eventLst);
				// Use original Solr dataset size
				setContentItemCountOffset(content, sectionsMap.get(SectionType.TE),
						eventSet.getTotalCount(), eventSet.getUnfilteredDataSize());
				entityData.setTe(content);
				isContentAvailable = true;
			}
		}

		// set name and searchToken
		retrieveEntityAndPopulateWrapper(entityBriefInfo, entityDataWrapper); 
		
		entityDataWrapper.setData(entityData);
		res.setResult(entityDataWrapper);

		// set metadat for fr and ft
		entityDataWrapper.setMetaData(getMetaDataEntitiyResponse(res));

		// handling visualization
		if (entityBriefInfo.getVisualizationData() != null) {
			setChartData(sectionsMap.keySet(), res, entityBriefInfo.getVisualizationData());
		}
		// in case of no content set firstrain header in response
		if (!isContentAvailable) {
			response.setHeader("FR-Content", "0");
		}

		return res;
	}

	private Content getContent(final List<com.firstrain.frapi.domain.Document> documentLstRst, final Map<SectionType, SectionSpec> sectionsMap, final DocumentSet documentSet, final SectionType sectionType) {
		Content content = new Content();
		List<Document> documentLst = new ArrayList<Document>();
		for (com.firstrain.frapi.domain.Document documentRst : documentLstRst) {
			documentLst.add(documentConvertor(documentRst));
		}
		content.setDocuments(documentLst);
		setContentItemCountOffset(content, sectionsMap.get(sectionType), documentSet.getTotalCount(), documentLst.size());
		return content;
	}
 
	private List<Tweet> createTweetListAndPopulate(final List<com.firstrain.frapi.domain.Tweet> tweetLstRst, final Short industryClassificationId, final boolean excludeTweetInfo, final Content content) { 
		List<Tweet> tweetLst = new ArrayList<Tweet>(); 
		 
		for (com.firstrain.frapi.domain.Tweet tweetRst : tweetLstRst) { 
			tweetLst.add(tweetConvertor(tweetRst, industryClassificationId, excludeTweetInfo)); 
		} 
		content.setTweets(tweetLst); 
		return tweetLst; 
	} 

	public EntityDataResponse getMatchedEntityDataResponse(EntityBriefInfo entityBriefInfo, INPUT_ENTITY_TYPE type) {
		EntityDataResponse res = new EntityDataResponse();
		res.setStatus(ResStatus.SUCCESS);
		String message = messageSource.getMessage("gen.succ", null, Locale.getDefault());
		EntityDataWrapper entityDataWrapper = populateMessageAndVersionAndCreateEntityDataWrapper(res, message);
		EntityData entityData = new EntityData();
		List<EntityStandard> entityList = new ArrayList<EntityStandard>();

		if (entityBriefInfo.getMatchedEntity() != null) {
			for (Entity entity : entityBriefInfo.getMatchedEntity()) {
				EntityStandard e = new EntityStandard();
				e.setName(entity.getName());
				e.setSearchToken(entity.getSearchToken());
				if (type.name().equalsIgnoreCase(INPUT_ENTITY_TYPE.COMPANY.name())) {
					e.setConfidence(setRelevanceBand(entity.getBand()));
					e.setHomePage(entity.getHomePage());
				} else if (type.name().equalsIgnoreCase(INPUT_ENTITY_TYPE.INDUSTRY.name())) {
					e.setMatchType(entity.getMatchedType());
				} else if (type.name().equalsIgnoreCase(INPUT_ENTITY_TYPE.TOPIC.name())) {
					e.setSector(entity.getSector());
					e.setSegment(entity.getSegment());
					e.setIndustry(entity.getIndustry());
				}
				entityList.add(e);
			}
		}
		entityData.setEntity(entityList);

		entityDataWrapper.setData(entityData);
		res.setResult(entityDataWrapper);
		return res;
	}

	public EntityDataResponse getEntityMapResponse(EntityBriefInfo entityBriefInfo, String msgKey) {
		EntityDataResponse res = new EntityDataResponse();
		EntityDataWrapper entityDataWrapper = getEntityDataWrapper(res, msgKey, entityBriefInfo);
		if (entityBriefInfo.getEntityMap() != null) {
			com.firstrain.frapi.domain.EntityMap eMap = entityBriefInfo.getEntityMap();
			com.firstrain.frapi.domain.Entity entity = eMap.getEntity();
			EntityMap entityMap = new EntityMap();
			if (entity != null) {
				entityDataWrapper.setId(entity.getId());
				entityDataWrapper.setName(entity.getName());
				entityMap.setStreetAddress(entity.getAddress());
				entityMap.setZip(entity.getZip());
				entityMap.setState(entity.getState());
				entityMap.setCity(entity.getCity());
				entityMap.setWebsite(entity.getWebsite());
				String country = entity.getCountry();
				if (country != null && !country.isEmpty()) {
					List<com.firstrain.frapi.domain.Entity> countryList = new ArrayList<com.firstrain.frapi.domain.Entity>();
					com.firstrain.frapi.domain.Entity ent = new com.firstrain.frapi.domain.Entity();
					ent.setName(entity.getCountry());
					countryList.add(ent);
					entityMap.setCountry(countryList);
				}
			}
			if (eMap.getIndustry() != null) {
				List<com.firstrain.frapi.domain.Entity> industries = new ArrayList<com.firstrain.frapi.domain.Entity>();
				industries.add(eMap.getIndustry());
				entityMap.setIndustry(industries);
			}

			if (eMap.getSector() != null) {
				List<com.firstrain.frapi.domain.Entity> sectors = new ArrayList<com.firstrain.frapi.domain.Entity>();
				sectors.add(eMap.getSector());
				entityMap.setSector(sectors);
			}

			if (eMap.getSegment() != null) {
				List<com.firstrain.frapi.domain.Entity> segments = new ArrayList<com.firstrain.frapi.domain.Entity>();
				segments.add(eMap.getSegment());
				entityMap.setSegment(segments);
			}

			if (eMap.getCountry() != null) {
				List<com.firstrain.frapi.domain.Entity> countries = new ArrayList<com.firstrain.frapi.domain.Entity>();
				countries.add(eMap.getCountry());
				entityMap.setCountry(countries);
			}

			entityMap.setCompanyLogo(eMap.getCompanyLogo());
			entityMap.setBusinessLines(eMap.getBizlines());
			entityMap.setDomain(eMap.getDomain());
			entityMap.setLanguage(eMap.getLanguage());

			EntityData entityData = new EntityData();
			entityData.setEntityMap(entityMap);
			entityDataWrapper.setData(entityData);
		}
		res.setResult(entityDataWrapper);
		return res;
	}

	public EntityDataResponse getEntityPeersResponse(EntityBriefInfo entityBriefInfo, String msgKey) {
		EntityDataResponse res = new EntityDataResponse();
		EntityDataWrapper entityDataWrapper = getEntityDataWrapper(res, msgKey, entityBriefInfo);

		retrieveEntityAndPopulateWrapper(entityBriefInfo, entityDataWrapper); 

		if (entityBriefInfo.getMatchedEntity() != null) {

			List<EntityStandard> peers = new ArrayList<EntityStandard>();

			for (Entity peer : entityBriefInfo.getMatchedEntity()) {
				EntityStandard entityStandard = new EntityStandard();
				entityStandard.setName(peer.getName());
				entityStandard.setSearchToken(peer.getSearchToken());
				peers.add(entityStandard);
			}
			EntityData entityData = new EntityData();
			entityData.setPeers(peers);
			entityDataWrapper.setData(entityData);
		}
		res.setResult(entityDataWrapper);
		return res;
	}

	private EntityDataWrapper getEntityDataWrapper(final EntityDataResponse res, final String msgKey, final EntityBriefInfo entityBriefInfo) {
		res.setStatus(ResStatus.SUCCESS);
		String message = messageSource.getMessage(msgKey, null, Locale.getDefault());
		if (entityBriefInfo.getStatusCode() == StatusCode.ENTITY_NOT_FOUND) {
			message += messageSource.getMessage("errorcode.100", null, Locale.getDefault());
		}
		EntityDataWrapper entityDataWrapper = populateMessageAndVersionAndCreateEntityDataWrapper(res, message);
		return entityDataWrapper;
	}

	private EntityDataWrapper populateMessageAndVersionAndCreateEntityDataWrapper(final EntityDataResponse res, final String message) {
		res.setMessage(message);
		res.setVersion(Constant.getVersion());
		
		EntityDataWrapper entityDataWrapper = new EntityDataWrapper();
		return entityDataWrapper;
	}
 
	private void retrieveEntityAndPopulateWrapper(final EntityBriefInfo entityBriefInfo, final EntityDataWrapper entityDataWrapper) { 
		com.firstrain.frapi.pojo.Entity e = entityBriefInfo.getEntity(); 
		if (e != null) { 
			if (e.getDnbEntityId() != null) { 
				entityDataWrapper.setId(e.getDnbEntityId()); 
			} else { 
				entityDataWrapper.setId(e.getSearchToken()); 
			} 
			entityDataWrapper.setName(e.getName()); 
		} 
	} 

	public DnBEntityStatusResponse getDnBEntityStatusResponse(List<Entity> entityList) {
		DnBEntityStatusResponse res = new DnBEntityStatusResponse();
		populateStatusMessageAndVersion(res, "gen.succ");

		DnBEntityStatusListWrapper entityDataWrapper = new DnBEntityStatusListWrapper();
		List<DnBEntityStatus> dnbEntityList = new ArrayList<DnBEntityStatus>();
		entityDataWrapper.setEntity(dnbEntityList);
		res.setResult(entityDataWrapper);

		for (Entity entity : entityList) {
			if (entity != null) {
				DnBEntityStatus dnbEntity = new DnBEntityStatus();
				// set support property
				if (entity.getSearchToken() != null) {
					dnbEntity.setSupport(true);
				} else {
					dnbEntity.setSupport(false);
				}
				// set content property
				if (entity.getDocCount() > 0) {
					dnbEntity.setContent(true);
				} else {
					dnbEntity.setContent(false);
				}
				dnbEntity.setDuns(entity.getDnbEntityId());
				dnbEntityList.add(dnbEntity);
			}
		}
		return res;
	}

	@SuppressWarnings("rawtypes")
	public JSONResponse getSuccessMsg(String msgKey) {
		JSONResponse res = new JSONResponse();
		populateStatusMessageAndVersion(res, msgKey);
		return res;
	}

	private <T0 extends JSONResponse> void populateStatusMessageAndVersion(final T0 res, final String msgKey) {
		res.setStatus(ResStatus.SUCCESS);
		String message = messageSource.getMessage(msgKey, null, Locale.getDefault());
		res.setMessage(message);
		res.setVersion(Constant.getVersion());
	}

	public Void makeTweetsFieldsNullable(List<Tweet> tweets) {

		if (CollectionUtils.isEmpty(tweets)) {
			return null;
		}

		for (Tweet tweet : tweets) {

			tweet.setAuthorName(null);
			tweet.setAuthorDescription(null);
			tweet.setIndustry(null);
			tweet.setTweetHtml(null);
			tweet.setLink(null);
			tweet.setUserName(null);
			tweet.setTitle(null);
			tweet.setTweetText(null);
			tweet.setAuthorAvatar(null);
			tweet.setGroupSize(null);
			tweet.setGroupId(null);

		}

		return null;
	}

	private Tweet tweetConvertor(com.firstrain.frapi.domain.Tweet tweetRst, Short industryClassificationId, boolean excludeTweetInfo) {
		if (tweetRst == null) {
			return null;
		}

		Tweet tweet = new Tweet();
		tweet.setTweetId(tweetRst.getTweetId());
		com.firstrain.frapi.pojo.Entity entity = tweetRst.getEntity();
		if (entity != null) {
			EntityStandard e = entityConvertor(entity);
			tweet.setEntity(e);
		}
		Date date = tweetRst.getTweetCreationDate();
		if (date != null) {
			tweet.setTimeStamp(getFormatTime(date));
		}

		if (excludeTweetInfo) {
			return tweet;
		}

		tweet.setAuthorName(tweetRst.getName());
		tweet.setAuthorDescription(tweetRst.getDescription());

		// set industries
		List<EntityStandard> industries = new ArrayList<EntityStandard>();
		List<com.firstrain.frapi.pojo.Entity> industryList = tweetRst.getIndustries();
		if (industryList != null && !industryList.isEmpty()) {
			for (com.firstrain.frapi.pojo.Entity ent : industryList) {
				if (ent != null) {
					// for firstrain classification id
					if (industryClassificationId == null || industryClassificationId <= 1) {
						if (ent.getIndustryClassificationId() == null || ent.getIndustryClassificationId() <= 1) {
							EntityStandard e = entityConvertor(ent);
							// handling rehydrate api case - set the band and score same as that of
							// primary entity
							if (e.getRelevanceScore() == null && entity != null) {
								e.setRelevanceScore(entity.getRelevanceScore());
								e.setRelevanceBand(setRelevanceBand(entity.getBand()));
							}
							industries.add(e);
						}
					} else if (ent.getIndustryClassificationId() != null
							&& industryClassificationId.equals(ent.getIndustryClassificationId().shortValue())) {
						EntityStandard e = entityConvertor(ent);
						// handling rehydrate api case - set the band and score same as that of
						// primary entity
						if (e.getRelevanceScore() == null && entity != null && entity.getRelevanceScore() != null) {
							e.setRelevanceScore(entity.getRelevanceScore());
							e.setRelevanceBand(setRelevanceBand(entity.getBand()));
						}
						industries.add(e);
					}
				}
			}
		}
		if (!industries.isEmpty()) {
			tweet.setIndustry(industries);
		}
		tweet.setTweetHtml(tweetRst.getTitle());
		tweet.setLink((List<String>) tweetRst.getExpandedLinks());
		tweet.setUserName(tweetRst.getScreenName());
		tweet.setTitle(tweetRst.getCoreTweet());
		tweet.setTweetText(tweetRst.getTweet());
		tweet.setAuthorAvatar(tweetRst.getUserImage());
		tweet.setGroupSize(tweetRst.getGroupSize());
		tweet.setGroupId(tweetRst.getGroupId());

		return tweet;
	}

	private EntityStandard entityConvertor(com.firstrain.frapi.pojo.Entity entity) {
		EntityStandard e = new EntityStandard();
		String name = entity.getName();
		e.setName(name);
		e.setSearchToken(entity.getSearchToken());
		e.setRelevanceScore(entity.getRelevanceScore());
		e.setRelevanceBand(setRelevanceBand(entity.getBand()));
		return e;
	}

	private Event eventConvertor(com.firstrain.frapi.pojo.Event eventRst) {
		return eventConvertor(eventRst, false);
	}

	private Event eventConvertor(com.firstrain.frapi.pojo.Event eventRst, boolean isMTEvent) {

		if (eventRst == null) {
			return null;
		}
		Event event = new Event();
		event.setId(eventRst.getEventId());

		event.setTitle(eventRst.getTitle());
		// we need to show eventRst.getEventInformationEnum() in json istead of
		// eventRst.getEventType()
		event.setEventType(eventRst.getEventInformationEnum());
		event.setLink(eventRst.getUrl());
		Date date = eventRst.getDate();
		if (date != null) {
			event.setTimeStamp(getFormatTime(date));
		}

		// set related document information of event
		MetaEvent me = null;
		if (eventRst.getEventTypeId() >= 1 && eventRst.getEventTypeId() <= 299) {
			me = eventRst.getMtMeta();
		} else if (eventRst.getEventTypeId() >= 350 && eventRst.getEventTypeId() <= 399) {
			me = eventRst.getWvMeta();
		} /*
			 * else if (eventRst.getEventTypeId() >= 300 && eventRst.getEventTypeId() <= 349) { me = eventRst.getStockPriceMeta(); } else if
			 * (eventRst.getEventTypeId() >= 400 && eventRst.getEventTypeId() <= 500) { me = eventRst.getSecMeta(); }
			 */

		Entity entity = eventRst.getEntityInfo();
		int eventTypeId = eventRst.getEventTypeId();
		if (entity != null && !isMTEvent) {
			EntityStandard e = createEntityStandard(entity, eventTypeId); 
			List<EntityStandard> entityList = new ArrayList<EntityStandard>();
			entityList.add(e);
			event.setEntity(entityList);
		}
		if (me != null) {
			if (me.getRelatedDocument() != null) {
				DocumentSet docSet = me.getRelatedDocument();
				List<com.firstrain.frapi.domain.Document> docList = docSet.getDocuments();
				if (docList != null && !docList.isEmpty()) {
					com.firstrain.frapi.domain.Document doc = docList.get(0);
					event.setSnippet(doc.getSummary());
					if (doc.getContentType() != null) {
						event.setContentType(doc.getContentType().getLabel());
					}

					com.firstrain.frapi.pojo.Entity ent = doc.getSource();
					if (ent != null) {
						Source source = new Source();
						source.setName(ent.getName());
						source.setSearchToken(ent.getSearchToken());
						event.setSource(source);
					}
					List<Entity> catEntries = doc.getCatEntries();
					if (catEntries != null && !catEntries.isEmpty()) {
						EntityStandard primaryEntity = null;
						List<EntityStandard> entityList = event.getEntity();
						if (entityList != null && !entityList.isEmpty()) {
							primaryEntity = entityList.get(0);
						}
						for (Entity matchEnt : catEntries) {
							if (entity.getId().equals(matchEnt.getId())) {
								primaryEntity.setRelevanceBand(setRelevanceBand(matchEnt.getBand()));
								primaryEntity.setRelevanceScore(matchEnt.getRelevanceScore());
							} else if (matchEnt.getType() != null && matchEnt.getType() == FR_ICategory.TOPIC_DIMENSION_INDUSTRY_FILTER) {
								EntityStandard e = new EntityStandard();
								e.setName(matchEnt.getName());
								e.setSearchToken(matchEnt.getSearchToken());
								e.setRelevanceBand(setRelevanceBand(matchEnt.getBand()));
								e.setRelevanceScore(matchEnt.getRelevanceScore());
								entityList.add(e);
							}
						}
					}
				}
			} /*
				 * else if(me.getRelatedEvent() != null) { EventSet es = me.getRelatedEvent(); List<com.firstrain.frapi.pojo.Event>
				 * eventsList = es.getEvents(); if(eventsList != null && eventsList.size() >= 1) { com.firstrain.frapi.pojo.Event relEvent =
				 * eventsList.get(0); event.setContentType(relEvent.getSecFormType()); } }
				 */
		}

		// For MT Event set some extra field
		if (isMTEvent) {
			Date reportDate = eventRst.getReportDate();
			if (reportDate != null) {
				event.setEventDate(getFormatTime(reportDate));
			}

			event.setPersonFullName(eventRst.getPerson());
			MgmtTurnoverMeta mgmtTurnoverMeta = eventRst.getMtMeta();
			if (mgmtTurnoverMeta != null) {
				event.setOldCompany(getEntityStandardObj(mgmtTurnoverMeta.getOldCompany(), eventTypeId));
				event.setNewCompany(getEntityStandardObj(mgmtTurnoverMeta.getNewCompany(), eventTypeId));

				event.setOldTitle(mgmtTurnoverMeta.getOldDesignation());
				event.setOldGroup(mgmtTurnoverMeta.getOldGroup());
				event.setOldRegion(mgmtTurnoverMeta.getOldRegion());

				event.setNewTitle(mgmtTurnoverMeta.getNewDesignation());
				event.setNewGroup(mgmtTurnoverMeta.getNewGroup());
				event.setNewRegion(mgmtTurnoverMeta.getNewRegion());
			}
		}

		return event;
	}

	private EntityStandard getEntityStandardObj(Entity entity, int eventTypeId) {
		if (entity == null) {
			return null;
		}

		EntityStandard e = createEntityStandard(entity, eventTypeId); 

		return e;
	}
 
	private EntityStandard createEntityStandard(final Entity entity, final int eventTypeId) { 
		EntityStandard e = new EntityStandard(); 
		e.setName(entity.getName()); 
		e.setSearchToken(entity.getSearchToken()); 
		// hardcode this information for SEC events 
		if (eventTypeId >= 400 && eventTypeId <= 500) { 
			e.setRelevanceBand(RelevanceBand.HIGH.getName()); 
			e.setRelevanceScore((short) 100); 
		} 
		return e; 
	} 

	public void setChartData(Set<SectionType> keySet, EntityDataResponse obj, VisualizationData vd) {
		EntityData ed = obj.getResult().getData();

		MetaData metaData = obj.getResult().getMetaData();

		Map<ChartType, Graph> vMap = vd.graphs;
		if (keySet.contains(SectionType.TT)) {
			ed.setTt(getChartDataWrapper(vMap, ChartType.TREE_MONITOR_SEARCH, SectionType.TT, metaData));
		}
		if (keySet.contains(SectionType.BI)) {
			ed.setBi(getChartDataWrapper(vMap, ChartType.TREE_COMPANY, SectionType.BI, metaData));
		}
		if (keySet.contains(SectionType.MD)) {
			ed.setMd(getChartDataWrapper(vMap, ChartType.TREE_TOPICS, SectionType.MD, metaData));
		}
		if (keySet.contains(SectionType.TWT)) {
			ed.setTwt(getChartDataWrapper(vMap, ChartType.ACC_METER, SectionType.TWT, metaData));
		}
		if (keySet.contains(SectionType.GL)) {
			ed.setGl(getChartDataWrapper(vMap, ChartType.GEO_WORLD, SectionType.GL, metaData));
		}
		if (keySet.contains(SectionType.RL)) {
			ed.setRl(getChartDataWrapper(vMap, ChartType.GEO_US, SectionType.RL, metaData));
		}
	}

	public void setChartDataForWebVolume(EntityDataResponse obj, String Id, SectionSpec spec, boolean debug, HttpServletResponse response) {
		EntityDataHtml edh = obj.getResult().getHtmlFrag();
		if (edh == null) {
			edh = new EntityDataHtml();
			obj.getResult().setHtmlFrag(edh);
		}
		EntityData ed = obj.getResult().getData();
		if (ed != null && ed.getWv() != null && ed.getWv().getWebVolume() != null && !ed.getWv().getWebVolume().isEmpty()) {
			edh.setFrContentWv(true);
			response.setHeader("frContentWv", "true");
		} else {
			response.setHeader("frContentWv", "false");
			edh.setFrContentWv(false);
		}
		edh.setWv(getWVChartScript(Id, spec, debug));
	}

	private String getWVChartScript(String Id, SectionSpec spec, boolean debug) {
		AuthAPIResponse authAPIResponse = AuthAPIResponseThreadLocal.get();
		String authKey = authAPIResponse.getAuthKey();
		String userId = "U:" + UserInfoThreadLocal.get().getUserId();

		int height = 115;
		int width = 850;
		if (spec != null) {
			if (spec.getHeight() > 0) {
				height = spec.getHeight();
			}
			if (spec.getWidth() > 0) {
				width = spec.getWidth();
			}
		}
		String file = "wvchart-init.min.js";
		if (debug) {
			file = "wvchart-init.js";
		}
		String urlStr = appBaseUrl + "/" + Constant.getAppName() + "/" + Constant.getVersion();
		return "<script class=\"jq-fr-wvchart\"  src=\"" + urlStr + "/js/" + file + "#authKey=" + authKey + "&frUserId=" + userId + "&id="
				+ Id + "&dim=" + width + "x" + height + "&sp=" + urlStr + "\" type=\"text/javascript\"></script>";
	}

	public void setChartDataForHtml(Set<SectionType> keySet, EntityDataResponse obj, String Id, Map<SectionType, SectionSpec> sectionsMap,
			boolean debug, String fq, HttpServletResponse response) {
		EntityDataHtml edh = new EntityDataHtml();
		obj.getResult().setHtmlFrag(edh);
		EntityData ed = obj.getResult().getData();
		if (keySet.contains(SectionType.TT)) {
			SectionSpec spec = sectionsMap.get(SectionType.TT);
			edh.setTt(getChartScript("tt", Id, spec, debug, fq));

			if (ed != null && ed.getTt() != null && ed.getTt().getNodes() != null && !ed.getTt().getNodes().isEmpty()) {
				edh.setFrContentTt(true);
				response.setHeader("frContentTt", "true");
			} else {
				response.setHeader("frContentTt", "false");
				edh.setFrContentTt(false);
			}
		}
		if (keySet.contains(SectionType.BI)) {
			SectionSpec spec = sectionsMap.get(SectionType.BI);
			edh.setBi(getChartScript("bi", Id, spec, debug, fq));

			if (ed != null && ed.getBi() != null && ed.getBi().getNodes() != null && !ed.getBi().getNodes().isEmpty()) {
				edh.setFrContentBi(true);
				response.setHeader("frContentBi", "true");
			} else {
				response.setHeader("frContentBi", "false");
				edh.setFrContentBi(false);
			}
		}
		if (keySet.contains(SectionType.MD)) {
			SectionSpec spec = sectionsMap.get(SectionType.MD);
			edh.setMd(getChartScript("md", Id, spec, debug, fq));
			if (ed != null && ed.getMd() != null && ed.getMd().getNodes() != null && !ed.getMd().getNodes().isEmpty()) {
				edh.setFrContentMd(true);
				response.setHeader("frContentMd", "true");
			} else {
				response.setHeader("frContentMd", "false");
				edh.setFrContentMd(false);
			}
		}
		if (keySet.contains(SectionType.TWT)) {
			SectionSpec spec = sectionsMap.get(SectionType.TWT);
			edh.setTwt(getChartScript("twt", Id, spec, debug, fq));
			if (ed != null && ed.getTwt() != null && ed.getTwt().getNodes() != null && !ed.getTwt().getNodes().isEmpty()) {
				edh.setFrContentTwt(true);
				response.setHeader("frContentTwt", "true");
			} else {
				response.setHeader("frContentTwt", "false");
				edh.setFrContentTwt(false);
			}
		}
		if (keySet.contains(SectionType.GL)) {
			SectionSpec spec = sectionsMap.get(SectionType.GL);
			edh.setGl(getChartScript("gl", Id, spec, debug, fq));
			if (ed != null && ed.getGl() != null && ed.getGl().getNodes() != null && !ed.getGl().getNodes().isEmpty()) {
				edh.setFrContentGl(true);
				response.setHeader("frContentGl", "true");
			} else {
				response.setHeader("frContentGl", "false");
				edh.setFrContentGl(false);
			}
		}
		if (keySet.contains(SectionType.RL)) {
			SectionSpec spec = sectionsMap.get(SectionType.RL);
			edh.setRl(getChartScript("rl", Id, spec, debug, fq));
			if (ed != null && ed.getRl() != null && ed.getRl().getNodes() != null && !ed.getRl().getNodes().isEmpty()) {
				edh.setFrContentRl(true);
				response.setHeader("frContentRl", "true");
			} else {
				response.setHeader("frContentRl", "false");
				edh.setFrContentRl(false);
			}
		}
	}

	public void setChartDataForAnalyticsRibbonHtml(Set<SectionType> keySet, EntityDataResponse obj, String Id,
			Map<SectionType, SectionSpec> sectionsMap, boolean debug, String fq) {
		StringBuffer type = new StringBuffer();
		for (SectionType st : keySet) {
			if (chartSectionIDs.contains(st)) {
				type.append(st.toString().toLowerCase()).append(",");
			}
		}
		if (type.length() > 0) {
			EntityDataHtml edh = obj.getResult().getHtmlFrag();
			if (edh == null) {
				edh = new EntityDataHtml();
				obj.getResult().setHtmlFrag(edh);
			}
			// default spec -> wi and hi will be used
			SectionSpec spec = sectionsMap.get(SectionType.TT);
			String typeStr = type.substring(0, type.length() - 1);
			edh.setAnalyticsRibbon(getChartScript(typeStr, Id, spec, debug, fq));
		}
	}

	private String getChartScript(String type, String Id, SectionSpec spec, boolean debug, String fq) {
		AuthAPIResponse authAPIResponse = AuthAPIResponseThreadLocal.get();
		String authKey = authAPIResponse.getAuthKey();
		String userId = "U:" + UserInfoThreadLocal.get().getUserId();

		int height = 150;
		int width = 250;
		if (spec != null) {
			if (spec.getHeight() > 0) {
				height = spec.getHeight();
			}
			if (spec.getWidth() > 0) {
				width = spec.getWidth();
			}
		}
		String eventsStr = "click:callback0,mouseenter:callback1,mouseleave:callback2";
		if (spec != null && spec.getCallbackMethodsMap() != null && !spec.getCallbackMethodsMap().isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for (Map.Entry<String, String> entry : spec.getCallbackMethodsMap().entrySet()) {
				sb.append(entry.getKey() + ":" + entry.getValue() + ",");
			}
			eventsStr = sb.substring(0, sb.length() - 1);
		}
		String file = "visual-init.min.js";
		if (debug) {
			file = "visual-init.js";
		}
		String fqstr = "";
		if (fq != null) {
			fqstr = "&fq=" + fq;
		}

		String urlStr = appBaseUrl + "/" + Constant.getAppName() + "/" + Constant.getVersion();
		return "<script class=\"jq-fr-visualization\"  src=\"" + urlStr + "/js/" + file + "#authKey=" + authKey + "&frUserId=" + userId
				+ "&id=" + Id + "&events=" + eventsStr + "&dim=" + width + "x" + height + "&chDim=" + width + "x" + height + "&chTypes="
				+ type + fqstr + "&sp=" + urlStr + "\" type=\"text/javascript\"></script>";
	}

	public MetaData getMetaDataEntitiyResponse(EntityDataResponse entityDataResponse) {
		List<Document> documentLst = null;
		List<Tweet> tweetLst = null;
		List<Event> eventLst = null;
		if (entityDataResponse.getResult() != null) {
			EntityData entityData = entityDataResponse.getResult().getData();
			if (entityData != null) {
				Content c1 = entityData.getFr();
				Content c2 = entityData.getFt();
				Content c3 = entityData.getE();
				if (c1 != null) {
					documentLst = c1.getDocuments();
				}
				if (c2 != null) {
					tweetLst = c2.getTweets();
				}
				if (c3 != null) {
					eventLst = c3.getEvents();
				}
			}
		}
		return getMetaData(documentLst, tweetLst, eventLst);
	}

	public MetaData getMetaData(List<Document> docuemntLst, List<Tweet> tweetLst, List<Event> eventLst) {
		MetaData metaData = new MetaData();
		// set fr
		if (docuemntLst != null && !docuemntLst.isEmpty()) {
			Set<String> metaDataSet = new HashSet<String>();
			for (Document document : docuemntLst) {
				if (document != null) {
					List<EntityStandard> entities = document.getEntity();
					if (entities != null && !entities.isEmpty()) {
						for (EntityStandard e : entities) {
							if (e != null) {
								metaDataSet.add(e.getName());
							}
						}
					}
				}
			}
			metaData.setFr(setMetaData(metaData, metaDataSet));
		}

		// set ft
		if (tweetLst != null && !tweetLst.isEmpty()) {
			Set<String> metaDataSet = new HashSet<String>();
			for (Tweet tweet : tweetLst) {
				if (tweet != null) {
					EntityStandard e = tweet.getEntity();
					if (e != null) {
						metaDataSet.add(e.getName());
					}
				}
			}
			metaData.setFt(setMetaData(metaData, metaDataSet));
		}

		// set e
		if (eventLst != null && !eventLst.isEmpty()) {
			Set<String> metaDataSet = new HashSet<String>();
			for (Event event : eventLst) {
				if (event != null) {
					List<EntityStandard> entityList = event.getEntity();
					if (entityList != null && !entityList.isEmpty()) {
						for (EntityStandard e : entityList) {
							metaDataSet.add(e.getName());
						}
					}
				}
			}
			metaData.setE(setMetaData(metaData, metaDataSet));
		}
		return metaData;
	}

	private String setMetaData(MetaData metaData, Set<String> metaDataSet) {
		StringBuilder metaDataStr = new StringBuilder();
		for (String name : metaDataSet) {
			metaDataStr.append(name + "|");
		}
		if (metaDataStr.length() > 0) {
			return metaDataStr.substring(0, metaDataStr.length() - 1);
		}
		return null;
	}

	private ChartDataWrapper getChartDataWrapper(Map<ChartType, Graph> vMap, ChartType ct, SectionType st, MetaData metaData) {
		Graph graph = vMap.get(ct);
		ChartDataWrapper cdw = null;
		if (graph.nodes != null) {
			cdw = new ChartDataWrapper();
			cdw.setTitle(st.getName());
			StringBuilder metaDataStr = new StringBuilder();

			List<ChartData> cdl = new ArrayList<ChartData>();
			for (Node node : graph.nodes) {
				if (node.subtree != null) {
					for (Node subnode : node.subtree) {
						createChartDataAndAdd(subnode, ct, st, metaDataStr, cdl); 
					}
				} else {
					createChartDataAndAdd(node, ct, st, metaDataStr, cdl); 
				}
			}
			cdw.setNodes(cdl);
			// set metaData
			if (metaDataStr.length() > 0) {
				setMetadatForVisualization(metaData, metaDataStr.substring(0, metaDataStr.length() - 1), st);
			}
		}
		return cdw;
	}
 
	private void createChartDataAndAdd(final Node node, final ChartType ct, final SectionType st, final StringBuilder metaDataStr, final List<ChartData> cdl) { 
		ChartData cd = new ChartData(); 
		cd.setIntensity(Integer.toString(node.intensity)); 
		cd.setQuery(node.query); 
		cd.setSearchToken(node.searchToken); 
		cd.setSmartText(node.smartText); 
		if (ct.equals(ChartType.GEO_US)) { 
			cd.setStateCode(node.cc); 
		} else { 
			cd.setCountryCode(node.cc); 
		} 
		cd.setValue(Float.toString(node.value)); 
		if (st.equals(SectionType.GL) || st.equals(SectionType.RL)) { 
			cd.setName(node.label); 
			metaDataStr.append(node.label + "|"); 
		} else { 
			cd.setName(node.name); 
			metaDataStr.append(node.name + "|"); 
		} 
		cdl.add(cd); 
	} 

	private void setMetadatForVisualization(MetaData metaData, String metaDataStr, SectionType st) {
		if (st.equals(SectionType.TT)) {
			metaData.setTt(metaDataStr);
		}
		if (st.equals(SectionType.BI)) {
			metaData.setBi(metaDataStr);
		}
		if (st.equals(SectionType.MD)) {
			metaData.setMd(metaDataStr);
		}
		if (st.equals(SectionType.TWT)) {
			metaData.setTwt(metaDataStr);
		}
		if (st.equals(SectionType.GL)) {
			metaData.setGl(metaDataStr);
		}
		if (st.equals(SectionType.RL)) {
			metaData.setRl(metaDataStr);
		}
	}

	public boolean excludeTweetInfo(long enterpriseId) {

		if (tweetExclusionCSV == null || tweetExclusionCSV.trim().isEmpty()) {
			return true;
		}

		String[] enterpriseList = tweetExclusionCSV.trim().split(",");
		for (String configEnterprise : enterpriseList) {
			if (configEnterprise.equals(String.valueOf(enterpriseId))) {
				return false;
			}
		}

		return true;
	}
}
