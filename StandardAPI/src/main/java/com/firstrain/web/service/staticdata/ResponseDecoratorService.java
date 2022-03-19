package com.firstrain.web.service.staticdata;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Service;

import com.firstrain.frapi.domain.ItemDetail;
import com.firstrain.frapi.pojo.AuthAPIResponse;
import com.firstrain.frapi.pojo.EntityBriefInfo;
import com.firstrain.frapi.pojo.MonitorAPIResponse;
import com.firstrain.frapi.pojo.MonitorBriefDetail;
import com.firstrain.frapi.pojo.wrapper.TweetSet;
import com.firstrain.frapi.util.DefaultEnums.RelevanceBand;
import com.firstrain.frapi.util.FRAPIConstant;
import com.firstrain.frapi.util.StatusCode;
import com.firstrain.web.pojo.AuthKey;
import com.firstrain.web.pojo.Content;
import com.firstrain.web.pojo.Document;
import com.firstrain.web.pojo.EntityData;
import com.firstrain.web.pojo.EntityStandard;
import com.firstrain.web.pojo.MonitorConfig;
import com.firstrain.web.pojo.MonitorInfo;
import com.firstrain.web.pojo.MonitorSearch;
import com.firstrain.web.pojo.Tweet;
import com.firstrain.web.response.AuthKeyResponse;
import com.firstrain.web.response.EntityDataResponse;
import com.firstrain.web.response.ItemWrapperResponse;
import com.firstrain.web.response.JSONResponse;
import com.firstrain.web.response.JSONResponse.ResStatus;
import com.firstrain.web.response.MonitorConfigResponse;
import com.firstrain.web.response.MonitorInfoResponse;
import com.firstrain.web.service.BaseResponseDecoratorService;
import com.firstrain.web.wrapper.EntityDataWrapper;
import com.firstrain.web.wrapper.EntityWrapper;
import com.firstrain.web.wrapper.ItemWrapper;

@Service
public class ResponseDecoratorService extends BaseResponseDecoratorService {

	@Autowired
	private ResourceBundleMessageSource messageSource;
	@Value("${api.version}")
	private String version;

	public AuthKeyResponse getAuthKeyResponse(AuthAPIResponse apiRes) {
		AuthKeyResponse res = new AuthKeyResponse();
		res.setStatus(ResStatus.SUCCESS);
		String message = messageSource.getMessage(("authkey.succ"), null, Locale.getDefault());
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

	public MonitorInfoResponse getAddRemoveEntityResponse(MonitorAPIResponse apiRes, String msgKey) {
		MonitorInfoResponse res = new MonitorInfoResponse();
		res.setStatus(ResStatus.SUCCESS);
		String message = messageSource.getMessage(msgKey, null, Locale.getDefault());
		res.setVersion(version);
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
		res.setMessage(message);
		res.setResult(data);
		return res;
	}

	public MonitorConfigResponse getMonitorConfigResponse(com.firstrain.frapi.domain.MonitorConfig apiRes) {
		MonitorConfigResponse res = new MonitorConfigResponse();
		res.setStatus(ResStatus.SUCCESS);
		String message = messageSource.getMessage("gen.succ", null, Locale.getDefault());
		res.setVersion(version);
		res.setMessage(message);

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
		}
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
		if (relevanceBand == RelevanceBand.HIGH.getValue()) {
			relevanceBandStr = RelevanceBand.HIGH.getName();
		} else if (relevanceBand == RelevanceBand.MEDIUM.getValue()) {
			relevanceBandStr = RelevanceBand.MEDIUM.getName();
		} else {
			relevanceBandStr = RelevanceBand.LOW.getName();
		}
		return relevanceBandStr;
	}
	
	public EntityDataResponse getEntityDataResponse(MonitorAPIResponse monitorApiRes, String msgKey) {
		EntityDataResponse res = getEntityDataResponse(monitorApiRes, msgKey, false);
		EntityDataWrapper entityDataWrapper = new EntityDataWrapper();
		EntityData entityData = new EntityData();
		
		MonitorBriefDetail monitorBriefDetail = monitorApiRes.getMonitorBriefDetail();
		if (monitorBriefDetail != null) {
			Content docContent = getDocumentsContent(monitorBriefDetail);
			if(docContent != null) {
				entityData.setFr(docContent);
			}

			com.firstrain.frapi.pojo.wrapper.TweetSet tweetSet = monitorBriefDetail.getTweetList();
			List<com.firstrain.frapi.domain.Tweet> tweetLstRst = getTweetLst(tweetSet);
			if (tweetLstRst != null && !tweetLstRst.isEmpty()) {
				entityData.setFt(getTweetContent(tweetLstRst));
			}
		}
		
		entityDataWrapper.setData(entityData);
		res.setResult(entityDataWrapper);
		return res;
	}

	public ItemWrapperResponse getItemWrapperResponse(TweetSet tweetSet, String msgKey) {
		com.firstrain.frapi.domain.Tweet tweetRst = tweetSet.getTweets().get(0);
		Tweet tweet = tweetConvertor(tweetRst);
		ItemWrapper itemWrapper = getItemWrapper(tweet);
		ItemWrapperResponse res = getItemWrapperResponse(msgKey);
		res.setResult(itemWrapper);
		return res;
	}

	private Content getTweetContent(List<com.firstrain.frapi.domain.Tweet> tweetLstRst) {
		Content content = new Content();
		List<Tweet> tweetLst = new ArrayList<Tweet>();
		for (com.firstrain.frapi.domain.Tweet tweetRst : tweetLstRst) {
			tweetLst.add(tweetConvertor(tweetRst));
		}
		content.setTweets(tweetLst);
		return content;
	}

	public EntityDataResponse getEntityDataResponse(EntityBriefInfo entityBriefInfo, String msgKey) {

		EntityDataResponse res = new EntityDataResponse();
		populateSuccessResponse(res, msgKey); 

		EntityDataWrapper entityDataWrapper = new EntityDataWrapper();
		EntityData entityData = new EntityData();

		// set Content Document
		com.firstrain.frapi.pojo.wrapper.DocumentSet documentSet = entityBriefInfo.getWebResults();
		if (documentSet != null) {
			List<com.firstrain.frapi.domain.Document> documentLstRst = documentSet.getDocuments();
			if (documentLstRst != null && !documentLstRst.isEmpty()) {
				Content content = new Content();
				List<Document> documentLst = new ArrayList<Document>();
				for (com.firstrain.frapi.domain.Document documentRst : documentLstRst) {
					documentLst.add(documentConvertor(documentRst));
				}
				content.setDocuments(documentLst);
				entityData.setFr(content);
			}
		}
		// set Content tweets
		com.firstrain.frapi.pojo.wrapper.TweetSet tweetSet = entityBriefInfo.getTweetList();
		if (tweetSet != null) {
			List<com.firstrain.frapi.domain.Tweet> tweetLstRst = tweetSet.getTweets();
			if (tweetLstRst != null && !tweetLstRst.isEmpty()) {
				Content content = getTweetContent(tweetLstRst); 
				entityData.setFt(content);
			}
		}

		entityDataWrapper.setData(entityData);
		res.setResult(entityDataWrapper);
		return res;
	}

	@SuppressWarnings("rawtypes")
	public JSONResponse getSuccessMsg(String msgKey) {
		JSONResponse res = new JSONResponse();
		populateSuccessResponse(res, msgKey); 
		return res;
	}
 
	private <T0 extends JSONResponse> void populateSuccessResponse(final T0 res, final String msgKey) { 
		res.setStatus(ResStatus.SUCCESS); 
		String message = messageSource.getMessage(msgKey, null, Locale.getDefault()); 
		res.setMessage(message); 
		res.setVersion(version); 
	} 

	private Tweet tweetConvertor(com.firstrain.frapi.domain.Tweet tweetRst) {

		if (tweetRst == null) {
			return null;
		}

		Tweet tweet = new Tweet();
		tweet.setAuthorName(tweetRst.getName());
		tweet.setAuthorDescription(tweetRst.getDescription());
		com.firstrain.frapi.pojo.Entity entity = tweetRst.getEntity();
		if (entity != null) {
			EntityStandard e = new EntityStandard();
			String name = entity.getName();
			e.setName(name);
			e.setSearchToken(entity.getSearchToken());
			tweet.setEntity(e);
		}
		tweet.setTweetHtml(tweetRst.getTitle());
		tweet.setTweetId(tweetRst.getTweetId());
		Date date = tweetRst.getTweetCreationDate();
		if (date != null) {
			tweet.setTimeStamp(getFormatTime(date));
		}
		tweet.setLink((List<String>) tweetRst.getExpandedLinks());
		tweet.setUserName(tweetRst.getScreenName());
		tweet.setTitle(tweetRst.getCoreTweet());
		tweet.setTweetText(tweetRst.getTweet());
		tweet.setAuthorAvatar(tweetRst.getUserImage());

		return tweet;
	}
}
