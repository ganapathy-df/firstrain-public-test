package com.firstrain.web.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;

import com.firstrain.frapi.domain.EntityStatus;
import com.firstrain.frapi.domain.MonitorBucketForTitle;
import com.firstrain.frapi.domain.Document.DocQuote;
import com.firstrain.frapi.pojo.AuthAPIResponse;
import com.firstrain.frapi.pojo.MonitorAPIResponse;
import com.firstrain.frapi.pojo.MonitorBriefDetail;
import com.firstrain.frapi.pojo.UserAPIResponse;
import com.firstrain.frapi.pojo.wrapper.DocumentSet;
import com.firstrain.frapi.util.FRAPIConstant;
import com.firstrain.frapi.util.StatusCode;
import com.firstrain.utils.FR_ArrayUtils;
import com.firstrain.utils.JSONUtility;
import com.firstrain.web.pojo.Content;
import com.firstrain.web.pojo.Document;
import com.firstrain.web.pojo.EntityStandard;
import com.firstrain.web.pojo.ItemData;
import com.firstrain.web.pojo.Monitor;
import com.firstrain.web.pojo.MonitorDetails;
import com.firstrain.web.pojo.MonitorInfo;
import com.firstrain.web.pojo.Source;
import com.firstrain.web.pojo.Tweet;
import com.firstrain.web.pojo.User;
import com.firstrain.web.response.EntityDataResponse;
import com.firstrain.web.response.EntityResponse;
import com.firstrain.web.response.ItemWrapperResponse;
import com.firstrain.web.response.MonitorDetailsResponse;
import com.firstrain.web.response.MonitorInfoResponse;
import com.firstrain.web.response.MonitorWrapperResponse;
import com.firstrain.web.response.UserWrapperResponse;
import com.firstrain.web.response.JSONResponse.ResStatus;
import com.firstrain.web.service.core.Constant;
import com.firstrain.web.service.core.RequestParsingService.defaultSpec;
import com.firstrain.web.util.AuthAPIResponseThreadLocal;
import com.firstrain.web.wrapper.EntityWrapper;
import com.firstrain.web.wrapper.ItemWrapper;
import com.firstrain.web.wrapper.MonitorWrapper;
import com.firstrain.web.wrapper.UserWrapper;

public abstract class BaseResponseDecoratorService {

	private static final List<String> IGNORE_ENTITY_NAMES = Collections.singletonList("Other Sector");

	@Autowired
	private ResourceBundleMessageSource messageSource;
	private static final Logger log = Logger.getLogger(BaseResponseDecoratorService.class);
	
	protected MonitorDetails getMonitorDetails(List<MonitorBucketForTitle> monitorBucketForTitleLst,
			LinkedHashMap<String, List<com.firstrain.frapi.domain.MonitorInfo>> monitorDetailsLst) {
		MonitorDetails monitorDetails = new MonitorDetails();
		LinkedHashMap<String, List<MonitorInfo>> monitors = new LinkedHashMap<String, List<MonitorInfo>>();
		if (monitorBucketForTitleLst != null && !monitorBucketForTitleLst.isEmpty()) {
			for (MonitorBucketForTitle monitorBucketForTitle : monitorBucketForTitleLst) {
				String monitorBucket = monitorBucketForTitle.getMonitorBucket();
				List<com.firstrain.frapi.domain.MonitorInfo> monitorInfoServiceApiLst = 
						monitorDetailsLst.get(monitorBucket);
				List<MonitorInfo> monitorInfoLst = new ArrayList<MonitorInfo>();

				if (monitorInfoServiceApiLst != null && !monitorInfoServiceApiLst.isEmpty()) {
					for (com.firstrain.frapi.domain.MonitorInfo monitorInfoServiceApi : monitorInfoServiceApiLst) {
						MonitorInfo monitorInfo = new MonitorInfo();
						monitorInfo.setActiveMail(monitorInfoServiceApi.getMailAvailable());
						monitorInfo.setFavoriteItemId(monitorInfoServiceApi.getFavoriteUserItemId());
						monitorInfo.setMonitorId(monitorInfoServiceApi.getMonitorId());
						monitorInfo.setMonitorName(monitorInfoServiceApi.getMonitorName());
						monitorInfoLst.add(monitorInfo);
					}
				}
				monitors.put(monitorBucket, monitorInfoLst);
				monitorDetails.setMonitors(monitors);
			}
		}
		return monitorDetails;
	}
	
	public MonitorDetailsResponse getMonitorDetailsResponse(
			com.firstrain.frapi.domain.MonitorDetails monitorDetailsServiceApi) {
		MonitorDetailsResponse res = new MonitorDetailsResponse();
		res.setStatus(ResStatus.SUCCESS);
		String message = messageSource.getMessage("user.monitors", null, Locale.getDefault());
		res.setMessage(message);
		res.setVersion(Constant.getVersion());
		
		List<MonitorBucketForTitle> monitorBucketForTitleLst = 
				monitorDetailsServiceApi.getTitlesForMonitorBuckets();
		LinkedHashMap<String, List<com.firstrain.frapi.domain.MonitorInfo>> monitorDetailsLst = 
				monitorDetailsServiceApi.getMonitors();
		res.setResult(getMonitorDetails(monitorBucketForTitleLst, monitorDetailsLst));
		return res;
	}
	
	public ItemWrapperResponse getItemWrapperResponse(DocumentSet documentSet, String msgKey) {
		ItemWrapperResponse res = getItemWrapperResponse(msgKey);

		ItemWrapper itemWrapper = new ItemWrapper();
		ItemData itemData = new ItemData();
		itemData.setDocument(documentConvertor(documentSet.getDocuments().get(0)));
		itemWrapper.setData(itemData);
		res.setResult(itemWrapper);
		return res;
	}

	protected ItemWrapperResponse getItemWrapperResponse(String msgKey) {
		ItemWrapperResponse res = new ItemWrapperResponse();
		res.setStatus(ResStatus.SUCCESS);
		String message = messageSource.getMessage(msgKey, null, Locale.getDefault());
		res.setMessage(message);
		res.setVersion(Constant.getVersion());
		return res;
	}
	
	protected ItemWrapper getItemWrapper(Tweet tweet) {
		ItemWrapper itemWrapper = new ItemWrapper();
		ItemData itemData = new ItemData();
		itemData.setTweet(tweet);
		itemWrapper.setData(itemData);
		return itemWrapper;
	}
	
	public MonitorInfoResponse getMonitorInfoResponse(MonitorAPIResponse monitorApiRes, String msgKey) {
		MonitorInfoResponse res = new MonitorInfoResponse();
		res.setStatus(ResStatus.SUCCESS);
		String message = messageSource.getMessage(msgKey, null, Locale.getDefault());
		res.setMessage(message);
		res.setVersion(Constant.getVersion());

		MonitorInfo monitorInfo = new MonitorInfo();
		monitorInfo.setMonitorId(Long.toString(monitorApiRes.getMonitorId()));
		monitorInfo.setMonitorName(monitorApiRes.getMonitorName());
		res.setResult(monitorInfo);
		return res;
	}
	
	public MonitorWrapperResponse getMonitorWrapperResponse(MonitorAPIResponse monitorApiRes, String msgKey) {
		MonitorWrapperResponse res = new MonitorWrapperResponse();
		res.setStatus(ResStatus.SUCCESS);
		String message = messageSource.getMessage(msgKey, null, Locale.getDefault());
		res.setMessage(message);
		res.setVersion(Constant.getVersion());

		MonitorWrapper monitorWrapper = new MonitorWrapper();
		Monitor monitor = new Monitor();
		monitor.setId(FRAPIConstant.MONITOR_PREFIX + monitorApiRes.getMonitorId());
		monitor.setName(monitorApiRes.getMonitorName());
		monitorWrapper.setMonitor(monitor);
		res.setResult(monitorWrapper);
		return res;
	}
	
	protected EntityDataResponse getEntityDataResponse(MonitorAPIResponse monitorApiRes, String msgKey,
			boolean validateNoItems) {
		EntityDataResponse res = new EntityDataResponse();
		res.setStatus(ResStatus.SUCCESS);
		String message = messageSource.getMessage(msgKey, null, Locale.getDefault());
		if (validateNoItems && monitorApiRes.getStatusCode() == StatusCode.NO_ITEMS_IN_MONITOR) {
			message += messageSource.getMessage("monitor.brief.noitems", null, Locale.getDefault());
		}
		res.setMessage(message);
		res.setVersion(Constant.getVersion());
		return res;
	}
	
	protected Content getDocumentsContent(MonitorBriefDetail monitorBriefDetail) {
		com.firstrain.frapi.pojo.wrapper.DocumentSet documentSet = monitorBriefDetail.getWebResults();
		Content content = null;
		if (documentSet != null) {
			List<com.firstrain.frapi.domain.Document> documentLstRst = documentSet.getDocuments();
			if (documentLstRst != null && !documentLstRst.isEmpty()) {
				content = new Content();
				List<Document> documentLst = new ArrayList<Document>();
				for (com.firstrain.frapi.domain.Document documentRst : documentLstRst) {
					documentLst.add(documentConvertor(documentRst));
				}
				content.setDocuments(documentLst);
				content.setItemCount(documentLst.size());
				content.setTotalItemCount(documentSet.getTotalCount());
			}
		}
		return content;
	}
	
	protected List<com.firstrain.frapi.domain.Tweet> getTweetLst(com.firstrain.frapi.pojo.wrapper.TweetSet tweetSet) {
		List<com.firstrain.frapi.domain.Tweet> tweetLstRst = null;
		if (tweetSet != null) {
			tweetLstRst = tweetSet.getTweets();
		}
		return tweetLstRst;
	}
	
	public UserWrapperResponse getUserResponse(UserAPIResponse apiRes, String msgKey) {
		com.firstrain.frapi.domain.User apiUser = apiRes.getUser();
		User user = new User();
		user.setuCompany(apiUser.getUserCompany());
		user.setuEmail(apiUser.getEmail());
		user.setuFirstName(apiUser.getFirstName());
		user.setuLastName(apiUser.getLastName());
		user.setUserId(FRAPIConstant.USER_ID_PREFIX + apiUser.getUserId());
		user.setuStatus(apiUser.getFlags());
		user.setuTimezone(apiUser.getTimeZone());
		
		UserWrapper data = new UserWrapper();
		data.setUser(user);
		
		String message = messageSource.getMessage(msgKey, null, Locale.getDefault());
		UserWrapperResponse res = new UserWrapperResponse();
		res.setStatus(ResStatus.SUCCESS);
		res.setMessage(message);
		res.setVersion(Constant.getVersion());
		res.setResult(data);
		return res;
	}

	public EntityResponse getMonitorEntityResponse(EntityStatus estatus) {
		EntityStandard entity = new EntityStandard();
		com.firstrain.frapi.pojo.Entity ent = estatus.getEntity();
		entity.setSearchToken(ent.getSearchToken());
		entity.setName(ent.getName());
		entity.setSubscribed(estatus.getEntityStatus());
		
		EntityWrapper data = new EntityWrapper();
		data.setEntity(entity);
		data.setId(FRAPIConstant.MONITOR_PREFIX + estatus.getId());
		data.setName(estatus.getName());

		String message = messageSource.getMessage("gen.succ", null, Locale.getDefault());
		EntityResponse res = new EntityResponse();
		res.setStatus(ResStatus.SUCCESS);
		res.setMessage(message);
		res.setVersion(Constant.getVersion());
		res.setResult(data);
		return res;
	}
	
	@SuppressWarnings("unchecked")
	protected List<Integer> getTopicDimensions() {
		List<Integer> topicDimensions = new ArrayList<Integer>();
		AuthAPIResponse authAPIResponse = AuthAPIResponseThreadLocal.get();
		String specJson = authAPIResponse.getPrefJson();
		if (specJson != null) {
			try {
				Map<String, Object> specParam = JSONUtility.deserialize(specJson, Map.class);
				Object tpdCSV = specParam.get(defaultSpec.TOPIC_DIMENSION_FOR_TAGGING);
				if (tpdCSV != null) {
					topicDimensions = FR_ArrayUtils.csvToIntegerList(tpdCSV.toString());
				}
			} catch (JsonParseException e) {
				log.error("Json Parsing error while serializing specification json : " + e.getMessage(), e);
			} catch (IOException e) {
				log.error("IO error while serializing specification json : " + e.getMessage(), e);
			}
		}
		return topicDimensions;
	}
	
	protected String getFormatTime(Date date) {
		SimpleDateFormat formater = new SimpleDateFormat("dd MMM yyyy HH:mm:ss z");
		formater.setTimeZone(TimeZone.getTimeZone("UTC"));
		return formater.format(date);
	}
	
	protected void setDateDetailsInDocument(com.firstrain.frapi.domain.Document documentRst,
			Document document) {
		Date date = documentRst.getDate();
		if (date != null) {
			document.setTimeStamp(getFormatTime(date));
			document.setDate(date);
		}
	}
	
	protected void setContentTypeInDocument(com.firstrain.frapi.domain.Document documentRst,
			Document document) {
		if (documentRst.getContentType() != null) {
			document.setContentType(documentRst.getContentType().getLabel());
		}
	}
	
	protected void setSourceInDocument(com.firstrain.frapi.domain.Document documentRst,
			Document document) {
		com.firstrain.frapi.pojo.Entity entity = documentRst.getSource();
		if (entity != null) {
			Source source = new Source();
			source.setName(entity.getName());
			source.setSearchToken(entity.getSearchToken());
			document.setSource(source);
		}
	}
	
	protected void setDocQuoteInDocument(com.firstrain.frapi.domain.Document documentRst,
			Document document) {
		DocQuote docQuote = documentRst.getDocQuote();
		if (docQuote != null) {
			document.setQuotes(docQuote.getHighlightedQuote());
		}
	}
	
	protected abstract String setRelevanceBand(Integer relevanceBand);

	protected void setEntitiesInDocument(com.firstrain.frapi.domain.Document documentRst, Document document) {
		List<com.firstrain.frapi.pojo.Entity> entitiesList = documentRst.getCatEntries();
		if (entitiesList != null && !entitiesList.isEmpty()) {
			List<Integer> topicDim = getTopicDimensions();
			List<EntityStandard> entities = new ArrayList<EntityStandard>();
			for (com.firstrain.frapi.pojo.Entity e1 : entitiesList) {
				if (topicDim != null && topicDim.contains(e1.getType())
						&& !IGNORE_ENTITY_NAMES.contains(e1.getName())) {
					EntityStandard e = new EntityStandard();
					e.setName(e1.getName());
					e.setSearchToken(e1.getSearchToken());
					e.setRelevanceBand(setRelevanceBand(e1.getBand()));
					e.setRelevanceScore(e1.getRelevanceScore());
					entities.add(e);
				}
			}
			if (!entities.isEmpty()) {
				document.setEntity(entities);
			}
		}
	}
	
	protected Document documentConvertor(com.firstrain.frapi.domain.Document documentRst) {
		if (documentRst == null) {
			return null;
		}
		
		Document document = new Document();
		document.setId(documentRst.getId());
		document.setTitle(documentRst.getTitle());
		document.setGroupId(FRAPIConstant.GROUP_ID_PREFIX + documentRst.getGroupId());
		document.setSnippet(documentRst.getSummary());
		document.setLink(documentRst.getUrl());
		document.setImage(documentRst.getImage());
		document.setNgrams(documentRst.getNgrams());
		
		setDateDetailsInDocument(documentRst, document);
		setContentTypeInDocument(documentRst, document);
		setSourceInDocument(documentRst, document);
		setEntitiesInDocument(documentRst, document);
		setDocQuoteInDocument(documentRst, document);
		return document;
	}

}
