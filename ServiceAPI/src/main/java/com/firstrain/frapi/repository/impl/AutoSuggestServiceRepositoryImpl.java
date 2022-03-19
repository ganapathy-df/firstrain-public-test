package com.firstrain.frapi.repository.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.util.NamedList;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.firstrain.frapi.domain.AutoSuggest;
import com.firstrain.frapi.domain.AutoSuggestInfo;
import com.firstrain.frapi.repository.AutoSuggestServiceRepository;
import com.firstrain.obj.IEntityInfo;
import com.firstrain.solr.client.autocomplete.AutoCompleteSpec;
import com.firstrain.solr.schema.EntityCoreSchema;

/**
 * @author GKhanchi
 */
@Repository
@Qualifier("AutoSuggestServiceRepositoryImpl")
public class AutoSuggestServiceRepositoryImpl extends EntityBaseServiceRepositoryImpl implements AutoSuggestServiceRepository {

	private static final Logger LOG = Logger.getLogger(AutoSuggestServiceRepositoryImpl.class);

	private static Map<String, int[]> typeDimensionMap = new HashMap<String, int[]>();

	static {
		typeDimensionMap.put("people", new int[] {EntityCoreSchema.peopleTypeDimension});
		typeDimensionMap.put("company", new int[] {EntityCoreSchema.companyTypeDimension});
		typeDimensionMap.put("topic",
				new int[] {EntityCoreSchema.topicTypeFundamentalDimension, EntityCoreSchema.topicTypeSectorDimension});
		typeDimensionMap.put("filter", new int[] {EntityCoreSchema.contentTypeDimension, EntityCoreSchema.sourceClassTypeDimension});
		typeDimensionMap.put("region", new int[] {EntityCoreSchema.regionTypeDimension});
		typeDimensionMap.put("source", new int[] {EntityCoreSchema.sourceTypeDimension});
		typeDimensionMap.put("industry", new int[] {EntityCoreSchema.industryTypeDimension});
	}

	@SuppressWarnings("rawtypes")
	@Override
	public AutoSuggest getAutoCompleteEntries(String input, String typeStr, boolean link, int count, String dimensionCSV) throws Exception {
		if (input == null || input.trim().isEmpty()) {
			LOG.info(" Input(Q) value not found, returning NULL...");
			return null;
		}
		NamedList namedList = search(this.getEntitySolrServer(), input, typeStr, link, count, dimensionCSV);
		return prepareAutoCompleteObject(namedList);
	}

	// TODO need to add proper code for honoring parameter link.
	@SuppressWarnings("rawtypes")
	private NamedList search(SolrServer server, String input, String typeCSVParam, boolean link, int count, String dimensionCSV)
			throws Exception {
		String typeCSV = typeCSVParam;
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(input);

		StringBuilder sb = new StringBuilder();
		if (dimensionCSV != null && !dimensionCSV.isEmpty()) {
			sb.append(dimensionCSV.trim());
		} else if (typeCSV != null) {
			typeCSV = typeCSV.trim();
			if (!typeCSV.isEmpty()) {
				String[] dims = typeCSV.split(",");
				for (String dimStr : dims) {
					int[] dimIdsArr = typeDimensionMap.get(dimStr.toLowerCase());
					if (dimIdsArr != null) {
						for (int i : dimIdsArr) {
							sb.append(i).append(",");
						}
					}
				}
				if (sb.length() > 0) {
					sb.setLength(sb.length() - 1);
				}
			}
		}
		if (sb.length() > 0) {
			solrQuery.setParam(AutoCompleteSpec.PARAM_DIMENSION, sb.toString());
			LOG.debug("Dimensions used :" + sb.toString());
		}
		solrQuery.setRows(count);
		solrQuery.setStart(0);
		solrQuery.setQueryType("ac");

		QueryRequest request = new QueryRequest(solrQuery, SolrRequest.METHOD.GET);
		QueryResponse response = request.process(server);

		return (NamedList) response.getResponse().get("entities");
	}

	@SuppressWarnings("rawtypes")
	private AutoSuggest prepareAutoCompleteObject(NamedList entities) {
		if (entities == null) {
			LOG.debug("No Search Result from Solr AutoComplete ");
			return null;
		}
		AutoSuggest ac = new AutoSuggest();
		List<AutoSuggestInfo> autoSuggestInfos = new ArrayList<AutoSuggestInfo>();
		for (int i = 0; i < entities.size(); i++) {
			AutoSuggestInfo data = new AutoSuggestInfo();
			String searchToken = (String) entities.getName(i);
			if (searchToken == null || searchToken.length() <= 0) {
				// try next candidate if there is no search token
				continue;
			}
			data.setSearchToken(searchToken);
			NamedList entity = (NamedList) entities.getVal(i);

			data.setDisplaySynonym((String) entity.get("synonymH"));
			data.setSynonym((String) entity.get("synonym"));
			data.setDisplayCik((String) entity.get("cikH"));
			data.setDisplaySedol((String) entity.get("sedolH"));
			data.setDisplayIsin((String) entity.get("isinH"));

			data.setName(entity.get("name").toString());
			data.setType(entity.get("type").toString());
			data.setReplaceOffset((Integer) entity.get("repOff"));
			data.setReplaceLength((Integer) entity.get("repLen"));
			String displayName = entity.get("nameH").toString();
			data.setNameH(displayName);
			if ("Company".equalsIgnoreCase(data.getType())) {
				String displayTicker = (String) entity.get("ticker");
				String displayTickerH = (String) entity.get("tickerH");
				if (displayTicker != null && displayTicker.length() > 0) {
					displayName = displayTicker + " - " + displayName;
					data.setDisplayTicker(displayTicker);
				}
				if (displayTickerH != null && displayTickerH.length() > 0) {
					data.setTickerH(displayTickerH);
				}
			}
			data.setDisplayName(displayName);
			// FIXME remove entity info cache look up and fetch it at AutoCompleteSearcher
			if (this.getEntityInfoCache() != null) {
				IEntityInfo entityInfo = this.getEntityInfoCache().searchTokenToEntity(data.getSearchToken());
				if (entityInfo != null) {
					data.setCompanyId(entityInfo.getCompanyId());
					data.setCatId(entityInfo.getId());
					data.setDocCount(entityInfo.getDocCount());
				}
			}
			autoSuggestInfos.add(data);
		}
		ac.setAutoSuggest(autoSuggestInfos);
		return ac;
	}

}
