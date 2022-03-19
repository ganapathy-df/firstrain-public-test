/**
 * 
 */
package com.firstrain.web.controller.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.firstrain.frapi.domain.User;
import com.firstrain.frapi.pojo.wrapper.DocumentSet;
import com.firstrain.frapi.pojo.wrapper.GetBulk;
import com.firstrain.frapi.pojo.wrapper.TweetSet;
import com.firstrain.frapi.service.RehydrateService;
import com.firstrain.frapi.service.RestrictContentService;
import com.firstrain.frapi.util.StatusCode;
import com.firstrain.web.pojo.Document;
import com.firstrain.web.pojo.EntityDataHtml;
import com.firstrain.web.pojo.EntityStandard;
import com.firstrain.web.pojo.ItemData;
import com.firstrain.web.pojo.MetaData;
import com.firstrain.web.pojo.Tweet;
import com.firstrain.web.response.ItemWrapperResponse;
import com.firstrain.web.response.JSONResponse;
import com.firstrain.web.service.core.FreemarkerTemplateService;
import com.firstrain.web.service.core.RequestParsingService;
import com.firstrain.web.service.core.ResponseDecoratorService;
import com.firstrain.web.service.core.StorageService;
import com.firstrain.web.util.UserInfoThreadLocal;
import com.firstrain.web.wrapper.ItemWrapper;

/**
 * @author vgoyal
 *
 */
@Controller
@RequestMapping(value = "/item")
public class ItemController {
	private static final Logger LOG = Logger.getLogger(ItemController.class);

	@Autowired
	private RequestParsingService requestParsingService;
	@Autowired
	private FreemarkerTemplateService ftlService;
	@Autowired
	private RehydrateService rehydrateService;
	@Autowired
	private ResponseDecoratorService responseDecoratorService;
	@Autowired
	private RestrictContentService restrictContentService;
	@Autowired
	private StorageService storageService;

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/{itemId}/rehydrate", method = RequestMethod.GET, headers = "Accept=application/json")
	public JSONResponse rehydrate(HttpServletRequest request, HttpServletResponse response, @PathVariable("itemId") String itemId,
			@RequestParam(value = "results", required = false, defaultValue = "") String resultsCSV,
			@RequestParam(value = "needPhrases", required = false, defaultValue = "false") boolean needPhrases,
			@RequestParam(value = "entitylinks", required = false, defaultValue = "false") boolean entityLinking) {

		int errorCode = -1;
		ItemWrapperResponse resp = null;
		try {
			if (!itemId.startsWith("D:") && !itemId.startsWith("TW:")) {
				errorCode = StatusCode.ILLEGAL_ARGUMENT;
				LOG.warn("itemId should start with D: or TW: - " + itemId);
				throw new Exception();
			}

			List<Long> itemIds = new ArrayList<Long>();
			try {
				itemIds.add(Long.parseLong(itemId.split(":")[1]));
			} catch (Exception e) {
				errorCode = StatusCode.ILLEGAL_ARGUMENT;
				LOG.warn("item id does not contain ':' symbol: " + itemId);
				throw new Exception();
			}

			// Usagetracking - if item already hidden, So UsageTracking must be logged
			populateAttributes(request, itemId); 
			
			String metadata =
					requestParsingService.getSerializedMetadata(null, null, null, resultsCSV, null, null, needPhrases, entityLinking,
							false);
			if (metadata != null && !metadata.isEmpty()) {
				request.setAttribute("metaData", metadata);
			}

			if (restrictContentService.checkIfHiddenContent(UserInfoThreadLocal.get().getOwnedBy(), itemId)) {
				errorCode = StatusCode.ITEM_DOES_NOT_EXIST;
				LOG.warn("itemId passed in the url (" + itemId +
						") is hidden for the user " + UserInfoThreadLocal.get().getOwnedBy());
				throw new Exception();
			}


			boolean isDocument = itemId.startsWith("D:");
			short iclId = requestParsingService.getDefaultIndustryClassificationId();
			if (isDocument) {
				DocumentSet documentSet =
						rehydrateService.getDocumentView(Long.parseLong(UserInfoThreadLocal.get().getUserId()), itemIds, iclId);

				if (needPhrases || entityLinking) {

					List<String> ids = new ArrayList<String>();
					List<String> wrIds = storageService.getListOfIdsfromDocumentSet(documentSet);
					if (CollectionUtils.isNotEmpty(wrIds)) {
						ids.addAll(wrIds);
					}
					Map<String, GetBulk> map = storageService.getDocFieldsFromStorageService(ids, needPhrases, entityLinking);

					if (MapUtils.isNotEmpty(map)) {
						storageService.populateFieldInDocSet(map, documentSet);
					}

				}
				if (documentSet == null || documentSet.getDocuments() == null || documentSet.getDocuments().isEmpty()
						|| documentSet.getDocuments().get(0) == null) {
					errorCode = StatusCode.ITEM_DOES_NOT_EXIST;
					LOG.warn("No documents found for itemId passed in the url: " + itemId);
					throw new Exception();
				}
				resp = responseDecoratorService.getItemWrapperResponse(documentSet, "gen.succ");
			} else {
				TweetSet tweetSet = rehydrateService.getTweetView(Long.parseLong(UserInfoThreadLocal.get().getUserId()), itemIds);
				if (tweetSet == null || tweetSet.getTweets() == null || tweetSet.getTweets().isEmpty()
						|| tweetSet.getTweets().get(0) == null) {
					errorCode = StatusCode.ITEM_DOES_NOT_EXIST;
					LOG.warn("No tweets found for itemId passed in the url: " + itemId);
					throw new Exception();
				}
				resp = responseDecoratorService.getItemWrapperResponse(tweetSet, "gen.succ", iclId,
						responseDecoratorService.excludeTweetInfo(UserInfoThreadLocal.get().getOwnedBy()));
			}

			if (resultsCSV != null) {
				if (resultsCSV.contains("M") || resultsCSV.contains("m")) {
					resp.getResult().setMetaData(getMetaData(isDocument, resp.getResult()));
				}
				if (resultsCSV.contains("H") || resultsCSV.contains("h")) {
					Map<String, Object> ftlParams = new HashMap<String, Object>();
					ftlParams.put("reqScheme", requestParsingService.getRequestScheme(request));
					ftlParams.put("obj", resp);

					EntityDataHtml htmlFrag = new EntityDataHtml();
					if (isDocument) {
						htmlFrag.setDocument(ftlService.getHtml("document.ftl", ftlParams));
					} else {
						htmlFrag.setTweet(ftlService.getHtml("tweet.ftl", ftlParams));
					}
					resp.getResult().setHtmlFrag(htmlFrag);
				}
				if (!resultsCSV.isEmpty() && !resultsCSV.contains("D") && !resultsCSV.contains("d")) {
					// if do not populate data then set it to null
					resp.getResult().setData(null);
				}
			}
		} catch (Exception e) {
			if (errorCode > 0) {
				LOG.warn(e.getMessage());
			} else {
				LOG.error(e.getMessage(), e);
			}
			return requestParsingService.getErrorResponse(errorCode);
		}
		return resp;
	}

	@RequestMapping(value = "/{itemId}/rehydrate", method = RequestMethod.GET)
	public String rehydrateHtml(Model model, HttpServletRequest request, HttpServletResponse response,
			@PathVariable("itemId") String itemId) {
		int errorCode = -1;
		try {
			if (!itemId.startsWith("D:") && !itemId.startsWith("TW:")) {
				errorCode = StatusCode.ILLEGAL_ARGUMENT;
				LOG.warn("itemId should start with D: or TW: -" + itemId);
				throw new Exception();
			}

			// Usagetracking - if item already hidden, So UsageTracking must be logged
			populateAttributes(request, itemId); 
			

			if (restrictContentService.checkIfHiddenContent(UserInfoThreadLocal.get().getOwnedBy(), itemId)) {
				errorCode = StatusCode.ITEM_DOES_NOT_EXIST;
				LOG.warn("itemId passed in the url (" + itemId +
						") is hidden for the user " + UserInfoThreadLocal.get().getOwnedBy());
				throw new Exception();
			}

			List<Long> itemIds = new ArrayList<Long>();
			try {
				itemIds.add(Long.parseLong(itemId.split(":")[1]));
			} catch (Exception e) {
				errorCode = StatusCode.ILLEGAL_ARGUMENT;
				LOG.warn("item id does not contain ':' symbol: " + itemId);
				throw new Exception();
			}

			String fileName = null;
			ItemWrapperResponse resp = null;
			boolean isDocument = itemId.startsWith("D:");
			short iclId = requestParsingService.getDefaultIndustryClassificationId();
			if (isDocument) {
				DocumentSet documentSet =
						rehydrateService.getDocumentView(Long.parseLong(UserInfoThreadLocal.get().getUserId()), itemIds, iclId);
				if (documentSet == null || documentSet.getDocuments() == null || documentSet.getDocuments().isEmpty()
						|| documentSet.getDocuments().get(0) == null) {
					errorCode = StatusCode.ITEM_DOES_NOT_EXIST;
					LOG.warn("No documents found for itemId passed in the url: " + itemId);
					throw new Exception();
				}
				resp = responseDecoratorService.getItemWrapperResponse(documentSet, "gen.succ");
				fileName = "document";
			} else {
				TweetSet tweetSet = rehydrateService.getTweetView(Long.parseLong(UserInfoThreadLocal.get().getUserId()), itemIds);
				if (tweetSet == null || tweetSet.getTweets() == null || tweetSet.getTweets().isEmpty()
						|| tweetSet.getTweets().get(0) == null) {
					errorCode = StatusCode.ITEM_DOES_NOT_EXIST;
					LOG.warn("No tweets found for itemId passed in the url: " + itemId);
					throw new Exception();
				}
				resp = responseDecoratorService.getItemWrapperResponse(tweetSet, "gen.succ", iclId, false);
				fileName = "tweet";
			}
			Map<String, Object> ftlParams = new HashMap<String, Object>();
			ftlParams.put("obj", resp);

			String html = ftlService.getHtml(fileName + ".ftl", ftlParams);
			model.addAttribute("htmlContent", html);
		} catch (Exception e) {
			if (errorCode < 0) {
				LOG.error(e.getMessage(), e);
			}
			model.addAttribute("errorMsg", requestParsingService.getErrorHtmlResponse(errorCode));
		}
		return "view";
	}
 
	private void populateAttributes(final HttpServletRequest request, final String itemId) { 
		request.setAttribute("loadview", true); 
		request.setAttribute("activityType", request.getMethod()); 
		request.setAttribute("targetId", itemId); 
		request.setAttribute("target", "rehydrate"); 
	} 
	

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/{itemId}/barMe", method = RequestMethod.POST, headers = "Accept=application/json")
	public JSONResponse barMe(HttpServletRequest request, HttpServletResponse response, @PathVariable("itemId") String itemId) {
		JSONResponse res = null;
		int errorCode = -1;
		try {

			// Usagetracking - if item already hidden, So UsageTracking must be logged
			request.setAttribute("loadview", true);
			request.setAttribute("activityType", request.getMethod());
			request.setAttribute("target", "barme");
			request.setAttribute("targetId", itemId);

			if (!itemId.startsWith("D:") && !itemId.startsWith("TW:")) {
				errorCode = StatusCode.ILLEGAL_ARGUMENT;
				LOG.warn("itemId should start with D: or TW: -" + itemId);
				throw new Exception();
			}

			List<Long> itemIds = new ArrayList<Long>();
			try {
				itemIds.add(Long.parseLong(itemId.split(":")[1]));
			} catch (Exception e) {
				errorCode = StatusCode.ILLEGAL_ARGUMENT;
				LOG.warn("item id does not contain ':' symbol: " + itemId);
				throw new Exception();
			}

			User user = UserInfoThreadLocal.get();
			long entrpriseId = user.getOwnedBy();
			long userId = Long.parseLong(user.getUserId());

			int code = restrictContentService.hideContent(userId, entrpriseId, itemId);
			if (code == 200) {
				res = responseDecoratorService.getSuccessMsg("barme.succ");
			} else {
				errorCode = code;
				throw new Exception("Error in barme api , error code: " + code);
			}
		} catch (Exception e) {
			if (errorCode > 0) {
				LOG.warn(e.getMessage());
			} else {
				LOG.error(e.getMessage(), e);
			}
			return requestParsingService.getErrorResponse(errorCode);
		}
		return res;
	}

	private MetaData getMetaData(boolean isDocument, ItemWrapper itemWrapper) {
		MetaData metaData = new MetaData();
		ItemData itemData = itemWrapper.getData();
		if (itemData != null) {
			if (isDocument) {
				Document document = itemData.getDocument();
				if (document != null) {
					List<EntityStandard> entities = document.getEntity();
					if (entities != null && !entities.isEmpty()) {
						StringBuilder name = new StringBuilder();
						for (EntityStandard e : entities) {
							if (e != null) {
								name.append(e.getName() + "|");
							}
						}
						metaData.setDocument(name.substring(0, name.length() - 1));
					}
				}
			} else {
				Tweet tweet = itemData.getTweet();
				if (tweet != null) {
					EntityStandard e = tweet.getEntity();
					if (e != null) {
						metaData.setTweet(e.getName());
					}
				}
			}
		}
		return metaData;
	}
}
