package com.firstrain.frapi.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.firstrain.content.similarity.DocumentSimilarityUtil;
import com.firstrain.frapi.domain.BaseSpec;
import com.firstrain.frapi.domain.Document;
import com.firstrain.frapi.domain.Parameter;
import com.firstrain.frapi.pojo.EnterprisePref;
import com.firstrain.frapi.pojo.Entity;
import com.firstrain.frapi.pojo.SearchAPIResponse;
import com.firstrain.frapi.pojo.wrapper.DocumentSet;
import com.firstrain.frapi.repository.SearchServiceRepository;
import com.firstrain.frapi.service.EntityProcessingService;
import com.firstrain.frapi.service.RestrictContentService;
import com.firstrain.frapi.service.SearchService;
import com.firstrain.frapi.util.ConvertUtil;
import com.firstrain.frapi.util.FRAPIConstant;
import com.firstrain.frapi.util.ServicesAPIUtil;
import com.firstrain.frapi.util.StatusCode;
import com.firstrain.obj.IEntityInfo;
import com.firstrain.solr.client.DocEntriesUpdator;
import com.firstrain.solr.client.DocEntry;
import com.firstrain.solr.client.DocListBucket;
import com.firstrain.solr.client.SearchResult;
import com.firstrain.solr.client.SearchSpec;
import com.firstrain.solr.client.SolrSearcher;
import com.firstrain.utils.object.PerfActivityType;
import com.firstrain.utils.object.PerfMonitor;

@Service
public class SearchServiceImpl implements SearchService {

	@Autowired
	@Qualifier("searchServiceRepositoryImpl")
	private SearchServiceRepository searchServiceRepository;
	@Autowired
	private RestrictContentService restrictContentService;
	@Autowired
	private EntityProcessingService entityProcessingService;
	@Autowired
	private ConvertUtil convertUtil;
	@Autowired
	private ServicesAPIUtil servicesAPIUtil;
	@Value("${exclude.filters}")
	private String excludeFilters;

	private final Logger LOG = Logger.getLogger(SearchServiceImpl.class);

	@Override
	public SearchAPIResponse getMultiSectionWebResults(String[] qMulti, int[] scopeMulti, String fqParam, Parameter parameter,
			EnterprisePref enterprisePref) throws Exception {

		String fq = fqParam;
		long startTime = PerfMonitor.currentTime();
		DocumentSimilarityUtil dsUtil = null;
		SearchAPIResponse searchAPIResponse = new SearchAPIResponse();

		try {

			if (qMulti == null || qMulti.length == 0) {
				searchAPIResponse.setStatusCode(StatusCode.INSUFFICIENT_ARGUMENT);
				return searchAPIResponse;
			}

			// This map is populated with entities for all single-token searches, as called need info on these searches.
			Map<String, Entity> tokenVsEntityMap = new HashMap<String, Entity>(qMulti.length);
			searchAPIResponse.setTokenVsEntityMap(tokenVsEntityMap);

			for (String q : qMulti) {
				IEntityInfo entityInfo = searchServiceRepository.getEntityInfoCache().searchTokenToEntity(q);
				if (entityInfo != null) {
					tokenVsEntityMap.put(q, convertUtil.convertEntityInfo(entityInfo));
				}
			}

			if (scopeMulti == null) {
				scopeMulti = new int[qMulti.length];
				if (qMulti.length == 2) {
					scopeMulti[0] = SearchSpec.SCOPE_MEDIUM;
					Entity e = tokenVsEntityMap.get(qMulti[1]);
					if (e != null) {
						scopeMulti[1] = e.getScope();
					} else {
						scopeMulti[1] = SearchSpec.SCOPE_NARROW;
					}
				} else {
					Arrays.fill(scopeMulti, SearchSpec.SCOPE_NARROW);
				}
			}

			if (parameter == null) {
				parameter = new Parameter();
			}

			short start = parameter.getStart() == null ? 0 : parameter.getStart();
			short count = parameter.getCount() == null ? Parameter.DEFAULT_COUNT : parameter.getCount();
			int days = parameter.getDays() == null ? Parameter.DAYS_DEFAULT_COUNT : parameter.getDays();


			BaseSpec baseSpec = new BaseSpec();
			// If pagination is not required, fetch 3 times the desired docs count
			if (!Boolean.TRUE.equals(parameter.getNeedPagination())) {
				baseSpec.setCount((short) (count * 3));
			} else {
				baseSpec.setCount(count);
			}
			baseSpec.setStart(start);
			baseSpec.setDaysCount(days);

			String excludeArticleIdsSSV =
					restrictContentService.getAllHiddenContent(enterprisePref.getEnterpriseId(), FRAPIConstant.DOCUMENT_PREFIX);
			if (!excludeArticleIdsSSV.isEmpty()) {
				baseSpec.setExcludeArticleIdsSSV(excludeArticleIdsSSV);
			}

			if (fq == null || fq.isEmpty()) {
				fq = excludeFilters;
			} else {
				fq = fq + " " + excludeFilters;
			}

			SearchResult sr = searchServiceRepository.getMultiSectionSearchResults(qMulti, scopeMulti, fq, baseSpec);

			if (sr != null && sr.buckets != null) {

				DocumentSet documentSet = new DocumentSet();
				Map<String, List<Document>> queryVsDocumentsMap = new LinkedHashMap<String, List<Document>>();
				documentSet.setDocumentBucket(queryVsDocumentsMap);
				searchAPIResponse.setWebResults(documentSet);
				searchAPIResponse.setStatusCode(StatusCode.REQUEST_SUCCESS);

				List<DocListBucket> docListBucketList = sr.buckets.subList(1, sr.buckets.size());
				dsUtil = searchServiceRepository.getDocumentSimilarityUtil();
				dsUtil.clear();

				for (int i = 0; i < docListBucketList.size(); i++) {
					if (docListBucketList.get(i).docs != null && !docListBucketList.get(i).docs.isEmpty()) {
						List<DocEntry> entries = docListBucketList.get(i).docs;
						if (entries != null && !entries.isEmpty()) {

							// Apply doc similarity only if pagination is not required
							if (!Boolean.TRUE.equals(parameter.getNeedPagination())) {
								entries = servicesAPIUtil.filterSimilarEntries(entries, dsUtil, count, false);
								dsUtil.clear();
							}

							if (entries == null || entries.isEmpty()) {
								continue;
							}

							if (Boolean.TRUE.equals(parameter.getNeedImage())) {
								DocEntriesUpdator.attachFavIconNDocImageDetails(searchServiceRepository.getFavIconServer(),
										searchServiceRepository.getDocImageServer(), entries);
							}
							if (Boolean.TRUE.equals(parameter.getNeedMatchedEntities())) {
								SolrSearcher.setupMatchesForEntries(entries, -1);
							}

							ArrayList<Document> listDocs = new ArrayList<Document>();
							for (DocEntry entry : entries) {
								listDocs.add(convertUtil.convertDocumentPOJOFromDocEntry(entry));
							}

							queryVsDocumentsMap.put(qMulti[i], listDocs);
						}
					}
				}

				entityProcessingService.getDocumentSetWithId(documentSet);
			}
		} catch (Exception e) {
			LOG.error("Error while getting multi section web results!", e);
			throw e;
		} finally {
			if (dsUtil != null) {
				dsUtil.clear();
			}
			PerfMonitor.recordStats(startTime, PerfActivityType.ReqTime, "getMultiSectionWebResults");
		}
		return searchAPIResponse;
	}
}
