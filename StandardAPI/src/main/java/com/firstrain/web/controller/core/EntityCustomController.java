package com.firstrain.web.controller.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.firstrain.frapi.pojo.wrapper.BaseSet.SectionType;
import com.firstrain.frapi.service.EntityBriefCustomService;
import com.firstrain.frapi.util.ServicesAPIUtil;
import com.firstrain.frapi.util.StatusCode;
import com.firstrain.solr.client.EntityEntry;
import com.firstrain.solr.client.HotListBucket;
import com.firstrain.solr.client.HotListEntry;
import com.firstrain.utils.JSONUtility;
import com.firstrain.web.pojo.Entity;
import com.firstrain.web.pojo.SearchResultInputBean;
import com.firstrain.web.pojo.SearchResultWeb;
import com.firstrain.web.response.JSONResponse;
import com.firstrain.web.service.core.RequestParsingService;

@Controller
@RequestMapping(value = "/entityCustom")
public class EntityCustomController {

	private static final Logger LOG = Logger.getLogger(EntityCustomController.class);

	@Autowired
	private EntityBriefCustomService entityBriefCustomService;

	@Autowired
	private RequestParsingService requestParsingService;

	@Autowired
	private ServicesAPIUtil servicesAPIUtil;

	@RequestMapping(value = "/coMentions", method = RequestMethod.POST, headers = "Accept=application/json")
	public JSONResponse<List<Entity>> coMentions(@RequestBody String reqBody) {

		JSONResponse<List<Entity>> res = new JSONResponse<List<Entity>>();

		int errorCode = -1;
		try {
			if (reqBody == null || reqBody.isEmpty()) {
				errorCode = StatusCode.INSUFFICIENT_ARGUMENT;
				throw new Exception("reqBody is null");
			}

			SearchResultInputBean p = null;
			try {
				p = JSONUtility.deserialize(reqBody, SearchResultInputBean.class);
				if (LOG.isDebugEnabled()) {
					LOG.debug(reqBody);
				}
			} catch (Exception e) {
				errorCode = StatusCode.ILLEGAL_ARGUMENT;
				throw e;
			}

			HotListBucket hotListBucket =
					entityBriefCustomService.getCoMentionCompanies(p.getSecondaryCatIds(), p.getScope(), p.getCount(), p.getDaysCount());

			if (hotListBucket != null) {
				List<HotListEntry> hotListEntries = hotListBucket.getEntries();

				if (CollectionUtils.isNotEmpty(hotListEntries)) {
					LOG.info("CoMention Companies size : " + hotListEntries.size());

					List<Entity> entityLst = new ArrayList<Entity>();
					for (HotListEntry hotListEntry : hotListEntries) {
						EntityEntry ee = hotListEntry.getEntity();
						if (ee == null || hotListEntry.getDocCount() <= 0) {
							continue;
						}

						Entity e = new Entity();
						e.setName(ee.getName());
						e.setId(ee.getId());
						entityLst.add(e);
					}

					if (LOG.isDebugEnabled()) {
						LOG.debug(JSONUtility.serialize(entityLst));
					}

					res.setResult(entityLst);
				}
			}

			return res;
		} catch (Exception e) {
			if (errorCode > 0) {
				LOG.warn(e.getMessage());
			} else {
				LOG.error(e.getMessage(), e);
			}
			return requestParsingService.getErrorResponse(errorCode);
		}
	}

	@RequestMapping(value = "/searchResult", method = RequestMethod.POST, headers = "Accept=application/json")
	public JSONResponse<SearchResultWeb> searchResult(@RequestBody String reqBody) {

		JSONResponse<SearchResultWeb> res = new JSONResponse<SearchResultWeb>();
		int errorCode = -1;
		try {
			if (reqBody == null || reqBody.isEmpty()) {
				errorCode = StatusCode.INSUFFICIENT_ARGUMENT;
				throw new Exception("reqBody is null");
			}
			SearchResultInputBean p = null;
			try {
				p = JSONUtility.deserialize(reqBody, SearchResultInputBean.class);
				if (LOG.isDebugEnabled()) {
					LOG.debug(reqBody);
				}
			} catch (Exception e) {
				errorCode = StatusCode.ILLEGAL_ARGUMENT;
				throw e;
			}

			SearchResultWeb sr = null;
			if (SectionType.FR.name().equalsIgnoreCase(p.getType())) {
				sr = entityBriefCustomService.getWebResultsForCatId(p.getSecondaryCatIds(), p.getScope(), p.getCount(), p.getDaysCount());
			} else {
				// customized entities
				sr = entityBriefCustomService.getWebResults(p.getPrimaryCatId(), p.getSecondaryCatIds(), p.getScope(), p.getCount(),
						p.getDaysCount(), p.isAdvanceSort());
				LOG.debug("Topic Ids Size : " + sr.getTopicIdHavingDocs().size());
			}

			res.setResult(sr);
			if (LOG.isDebugEnabled()) {
				LOG.debug(JSONUtility.serialize(sr));
			}

			return res;
		} catch (Exception e) {
			if (errorCode > 0) {
				LOG.warn(e.getMessage());
			} else {
				LOG.error(e.getMessage(), e);
			}
			return requestParsingService.getErrorResponse(errorCode);
		}
	}

	/**
	 * 
	 * @param reqBody the list of categories Ids to get the count for last n {7, 180 days} days.
	 * @return The weekly count in the entity index for these entities.
	 */

	@RequestMapping(value = "/docCountForNDays", method = RequestMethod.POST, headers = "Accept=application/json")
	public JSONResponse<Map<Long, Integer>> docCountForNDays(@RequestBody String reqBody) {
		JSONResponse<Map<Long, Integer>> res = new JSONResponse<Map<Long, Integer>>();
		int errorCode = -1;
		try {
			if (reqBody == null || reqBody.isEmpty()) {
				errorCode = StatusCode.INSUFFICIENT_ARGUMENT;
				throw new Exception("reqBody is null");
			}

			SearchResultInputBean input = null;
			try {
				input = JSONUtility.deserialize(reqBody, SearchResultInputBean.class);
				if (LOG.isDebugEnabled()) {
					LOG.debug(reqBody);
				}
			} catch (Exception e) {
				errorCode = StatusCode.ILLEGAL_ARGUMENT;
				throw e;
			}

			List<Long> inputcatIds = input.getSecondaryCatIds();
			if (inputcatIds == null || inputcatIds.isEmpty()) {
				errorCode = StatusCode.INSUFFICIENT_ARGUMENT;
				throw new Exception("no category id provided for entity");
			}

			Map<Long, Integer> result = servicesAPIUtil.getNDaysCountForEntities(inputcatIds, input.getDaysCount());
			res.setResult(result);

			if (LOG.isDebugEnabled()) {
				LOG.debug(JSONUtility.serialize(result));
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

}
