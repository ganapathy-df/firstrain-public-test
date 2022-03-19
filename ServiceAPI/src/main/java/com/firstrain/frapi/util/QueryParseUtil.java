package com.firstrain.frapi.util;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.firstrain.db.obj.Items;
import com.firstrain.frapi.util.QueryParser.ParseResult;
import com.firstrain.solr.client.SearchResult;
import com.firstrain.solr.client.SearchSpec;
import com.firstrain.solr.client.SolrSearcher;
import com.firstrain.utils.FR_DateUtils;

/**
 * @author Pratyush Nath This class will take care of parsing the search query based on the provided criteria.
 */
@Service
public class QueryParseUtil {

	private static final Logger LOG = Logger.getLogger(QueryParseUtil.class);

	private static String[] orderStr = new String[] {"relevance", "date"};

	public static SearchSpec parse(String query) {
		return parse(query, null);
	}

	// XXX : hpradhan some params are unused, but keeping it for now to keep in sync with email
	public static SearchSpec parse(String query, Timestamp lastSentTime) {

		Map<String, List<String>> map = prepareQueryMap(query);
		if (map == null || map.size() == 0) {
			LOG.info("Parse query found no params, returning null.");
			return null;
		}
		SearchSpec searchSpec = new SearchSpec();
		searchSpec.setTagExclusionScope(SearchSpec.SCOPE_BROAD);
		searchSpec.setUsePublishTime(true);
		searchSpec.setOrder(SearchSpec.ORDER_DATE);

		List<String> q = map.get("q");
		if (q != null && !q.isEmpty()) {
			searchSpec.setQ(q.get(0).trim());
		}

		List<String> fq = map.get("fq");
		if (fq != null && !fq.isEmpty()) {
			searchSpec.setFq(fq.get(0).trim());
		}

		int day = 180;
		List<String> days = map.get("days");
		if (days != null && !days.isEmpty()) {
			day = Integer.parseInt(days.get(0).trim());
		} else {
			if (lastSentTime != null) {
				day = (int) (FR_DateUtils.timeDiffInHours(new Timestamp(System.currentTimeMillis()), lastSentTime) / 24);
				day = day > 0 ? day : 180;
			}
		}
		searchSpec.setDays(day);

		List<String> bucketStr = map.get("b");
		if (bucketStr != null && !bucketStr.isEmpty()) {
			searchSpec.setBucketStr(bucketStr.get(0).trim());
		}

		List<String> tagFq = map.get("tagfq");
		if (tagFq != null && !tagFq.isEmpty()) {
			searchSpec.setTagFq(tagFq.get(0).trim());
		}

		List<String> start = map.get("start");
		if (start != null && !start.isEmpty()) {
			try {
				searchSpec.setStart(Integer.parseInt(start.get(0)));

			} catch (Throwable e) {
			}
		}

		List<String> num = map.get("num");
		if (num != null && !num.isEmpty()) {
			try {
				searchSpec.setRows(Integer.parseInt(num.get(0)));
			} catch (Throwable e) {
			}
		}

		List<String> order = map.get("order");
		if (order != null && !order.isEmpty()) {
			if (orderStr[1].equalsIgnoreCase(order.get(0))) {
				searchSpec.setOrder(1);
			}
		}

		List<String> scope = map.get("scope");
		if (scope != null && !scope.isEmpty()) {
			try {
				searchSpec.setScope(Integer.parseInt(scope.get(0)));
			} catch (Throwable e) {
			}
		}

		List<String> lastDay = map.get("lastDay");
		if (lastDay != null && !lastDay.isEmpty()) {
			searchSpec.setLastDay(lastDay.get(0).trim());
		}

		List<String> groupId = map.get("groupId");
		if (groupId != null && !groupId.isEmpty()) {
			try {
				searchSpec.setGroupId(Long.parseLong(groupId.get(0)));
			} catch (Throwable e) {
			}
		}

		List<String> lcf = map.get("lcf");
		if (lcf != null && !lcf.isEmpty()) {
			try {
				searchSpec.setHotListCompanyFilter(Integer.parseInt(lcf.get(0)));
			} catch (Throwable e) {
			}
		}
		return searchSpec;
	}

	public static SearchResult parseSearchSpec(List<Items> itemList, int start, int chunkSize) {

		String[] qArr = new String[itemList.size()];
		int[] scopeArr = new int[itemList.size()];

		getMonitorSearchSpec(itemList, qArr, scopeArr);

		SearchSpec searchSpec = new SearchSpec(new SearchSpec().setTagExclusionScope(SearchSpec.SCOPE_BROAD));

		searchSpec.setRows(chunkSize);
		searchSpec.setStart(start);
		searchSpec.setOrder(SearchSpec.ORDER_DATE);
		searchSpec.needHotListAll = false;
		searchSpec.needSearchSuggestion = false;
		searchSpec.useLikelySearchIntention = false;
		searchSpec.needQuotes = true;
		searchSpec.qMulti = qArr;
		searchSpec.scopeMulti = scopeArr;

		SolrSearcher ss = new SolrSearcher();
		SearchResult sr = new SearchResult();
		try {
			// ss.setEntitySolrServer(config.getEntitySolrServer());
			ss.parseSearchSpec(searchSpec, sr);
		} catch (Throwable t) {
			LOG.error(t.getMessage(), t);
		}
		return sr;
	}

	public static void getMonitorSearchSpec(List<Items> itemList, String[] qArr, int[] scopeArr) {
		for (int j = 0; j < itemList.size(); j++) {
			SearchSpec spec = parse(itemList.get(j).getData());
			if (spec != null) {
				String fq = spec.getFq();
				if (fq == null) {
					fq = "";
				}
				qArr[j] = (spec.getQ() + " " + fq).trim();
				scopeArr[j] = spec.scope;
			}
		}
	}

	public static Map<String, List<String>> prepareQueryMap(String query) {
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		ParseResult pResult = QueryParser.parseQueryString(query);
		for (String param : pResult.getParams()) {
			map.put(param, pResult.getParamValues(param));
		}
		return map;
	}

}
