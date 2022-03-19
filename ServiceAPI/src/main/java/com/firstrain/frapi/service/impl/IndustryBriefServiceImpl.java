package com.firstrain.frapi.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.firstrain.content.similarity.DocumentSimilarityUtil;
import com.firstrain.frapi.domain.BaseSpec;
import com.firstrain.frapi.domain.Document;
import com.firstrain.frapi.domain.IndustryBriefDomain;
import com.firstrain.frapi.pojo.wrapper.DocumentSet;
import com.firstrain.frapi.repository.EntityBaseServiceRepository;
import com.firstrain.frapi.service.EntityBaseService;
import com.firstrain.frapi.service.IndustryBriefService;
import com.firstrain.frapi.util.FRAPIConstant;
import com.firstrain.frapi.util.ServicesAPIUtil;
import com.firstrain.obj.IEntityInfo;
import com.firstrain.obj.IEntityInfoCache;
import com.firstrain.solr.client.DocEntriesUpdator;
import com.firstrain.solr.client.DocEntry;
import com.firstrain.solr.client.SearchResult;
import com.firstrain.solr.client.SearchSpec;
import com.firstrain.solr.client.SolrSearcher;
import com.firstrain.utils.FR_ArrayUtils;

@Service
public class IndustryBriefServiceImpl implements IndustryBriefService {

	private final Logger LOG = Logger.getLogger(IndustryBriefServiceImpl.class);

	@Autowired
	@Qualifier("entityBaseServiceRepositoryImpl")
	private EntityBaseServiceRepository entityBaseServiceRepository;
	@Autowired
	private EntityBaseService entityBaseService;
	@Autowired
	private ServicesAPIUtil servicesAPIUtil;

	@Override
	public IndustryBriefDomain getVirtualMonitor(String catIdsString, String primaryExcahnge, String topicIdsCSV,
			AtomicBoolean onlyIndustry, Boolean isCustomized) throws Exception {
		IndustryBriefDomain industryBriefDomain = new IndustryBriefDomain();
		try {
			Set<String> allCategoryIds = new HashSet<String>();
			ArrayList<String> exchangeList = FR_ArrayUtils.csvToArrayList(primaryExcahnge);
			boolean needAllEntities = false;

			/** fetch topicIds for industry/ids if not present in request */
			if (topicIdsCSV != null && !topicIdsCSV.trim().isEmpty()) {
				List<Integer> topicIdsList = FR_ArrayUtils.csvToIntegerList(topicIdsCSV);
				int[] arrTopicIds = FR_ArrayUtils.collectionToIntArray(topicIdsList);
				for (int i : arrTopicIds) {
					allCategoryIds.add(String.valueOf(i));
				}
				industryBriefDomain.setTopicIdsArray(arrTopicIds);
			} else {
				needAllEntities = true;
			}

			String[] fields = {"attrCatId"};
			SolrDocumentList solrDocumentList = entityBaseServiceRepository.getSolrDocFromIndustryAndBizlineCatIds(
					FR_ArrayUtils.csvToArrayList(catIdsString), null, fields, needAllEntities, isCustomized);

			if (solrDocumentList == null) {
				LOG.error("No Companies/topics found in solr for industry catID: " + catIdsString);
				throw new IllegalArgumentException("Invalid industry catID");
			}
			List<IEntityInfo> companyList = new ArrayList<IEntityInfo>();
			List<IEntityInfo> topicList = new ArrayList<IEntityInfo>();
			// populate data from solr doc list.
			entityBaseServiceRepository.populateCompanyTopicsFromSolrDocs(solrDocumentList, companyList, topicList, exchangeList);

			if (!isCustomized) {
				if (companyList.size() > FRAPIConstant.MAX_ENTITY_COUNT_FOR_INDUSTRY) {
					companyList = companyList.subList(0, FRAPIConstant.MAX_ENTITY_COUNT_FOR_INDUSTRY);
				}

				if (topicList.size() > FRAPIConstant.MAX_ENTITY_COUNT_FOR_INDUSTRY) {
					topicList = topicList.subList(0, FRAPIConstant.MAX_ENTITY_COUNT_FOR_INDUSTRY);
				}
			}

			if (!companyList.isEmpty()) {
				industryBriefDomain.setCompanyIdsArray(processEntityInfo(companyList, allCategoryIds, true));
				LOG.debug("No. of Companies in Industry: " + catIdsString + " are " + companyList.size());
			}

			if (!topicList.isEmpty()) {
				industryBriefDomain.setTopicIdsArray(processEntityInfo(topicList, allCategoryIds, false));
				LOG.debug("No. of Topics in Industry: " + catIdsString + " are " + topicList.size());
			}
			// If Industry does not contain any topic/company, then return all input Industry Ids
			if (allCategoryIds.isEmpty()) {
				allCategoryIds.addAll(FR_ArrayUtils.csvToArrayList(catIdsString));
				onlyIndustry.set(true);
			}
			industryBriefDomain.setCategoryIdsSet(allCategoryIds);

		} catch (Exception e) {
			LOG.error("Error Filling Virtaul Monitor Data For Cat Ids: " + catIdsString, e);
			throw e;
		}
		return industryBriefDomain;
	}

	private int[] processEntityInfo(List<IEntityInfo> entityList, Set<String> allCategoryIds, boolean isCompany) {
		int i = 0;
		int[] ids = new int[entityList.size()];
		for (IEntityInfo entityObj : entityList) {
			if (isCompany) {
				ids[i++] = entityObj.getCompanyId();
			} else {
				ids[i++] = Integer.parseInt(entityObj.getId());
			}
			allCategoryIds.add(entityObj.getId());
		}
		return ids;
	}

	@Override
	public DocumentSet getWebResults(Set<String> catIdsSet, String fq, BaseSpec bSpec) throws Exception {
		if (catIdsSet == null) {
			return null;
		}
		DocumentSet webResults = new DocumentSet();
		webResults.setCaption("Web results");
		DocumentSimilarityUtil dsutil = null;

		try {
			int desiredCount = bSpec.getCount();
			// If pagination is not required, fetch 3 times the desired docs count
			if (!Boolean.TRUE.equals(bSpec.getNeedPagination())) {
				bSpec.setCount((short) (bSpec.getCount() * 3));
			}

			// if(bSpec.getNeedPagination() == null || !bSpec.getNeedPagination().booleanValue()) {
			// bSpec.setCount((short)(bSpec.getCount()*3));
			// } else {
			// bSpec.setCount((short)FRAPIConstant.WEBRESULT_SINGLEENTITY);
			// }

			List<DocEntry> docEntries = getDocEntryForWebResults(catIdsSet, fq, bSpec, webResults);
			if (docEntries == null || docEntries.isEmpty()) {
				return null;
			}

			// Apply doc similarity only if pagination is not required
			if (!Boolean.TRUE.equals(bSpec.getNeedPagination())) {
				dsutil = entityBaseServiceRepository.getDocumentSimilarityUtil();
				docEntries = servicesAPIUtil.filterSimilarEntries(docEntries, dsutil, desiredCount, false);
			}

			DocEntriesUpdator.attachFavIconNDocImageDetails(entityBaseServiceRepository.getFavIconServer(),
					entityBaseServiceRepository.getDocImageServer(), docEntries, true, bSpec.isNeedPhrase());

			if (docEntries != null) {
				IEntityInfoCache entityInfoCache = entityBaseServiceRepository.getEntityInfoCache();
				boolean needPagination = bSpec.getNeedPagination() != null && bSpec.getNeedPagination() == true;
				List<Document> documentEntries = servicesAPIUtil.filterDocEntries(desiredCount, docEntries, catIdsSet, entityInfoCache,
						false, needPagination, bSpec.getOnlyIndustry());
				webResults.setDocuments(documentEntries);
			}
		} catch (Exception e) {
			LOG.error("Exception Getting Web Results Data", e);
			throw e;
		} finally {
			if (dsutil != null) {
				dsutil.clear();
			}
		}
		return webResults;
	}

	private List<DocEntry> getDocEntryForWebResults(Set<String> allCatIds, String fq, BaseSpec spec, DocumentSet webResults)
			throws Exception {

		List<DocEntry> docEntries = new ArrayList<DocEntry>();
		try {
			List<Integer> scopeList = new ArrayList<Integer>();
			List<String> qlist = new ArrayList<String>();

			Iterator<String> iterator = allCatIds.iterator();
			while (iterator.hasNext()) {
				IEntityInfo entityInfo = entityBaseServiceRepository.getEntityInfoCache().catIdToEntity(iterator.next());
				if (entityInfo != null) {
					scopeList.add(SearchSpec.SCOPE_NARROW);
					qlist.add(entityInfo.getSearchToken());
				}
			}

			String[] qMulti = FR_ArrayUtils.collectionToStringArray(qlist);
			int[] scopeMulti = FR_ArrayUtils.collectionToIntArray(scopeList);

			SearchResult searchResult = entityBaseService.getSearchResult(qMulti, scopeMulti, fq, spec);

			int bcount = searchResult.buckets.get(0).docs.size();
			SolrSearcher.applyApplicationFilter(searchResult);
			LOG.debug("WebResults before applying applyApplicationFilter()" + bcount + " and after " + searchResult.buckets.get(0).docs);
			docEntries.addAll(searchResult.buckets.get(0).docs);
			webResults.setTotalCount((int) searchResult.total);
			webResults.setHasMore(servicesAPIUtil.getHasMoreValue(webResults.getTotalCount(), spec.getCount(), spec.getStart()));
		} catch (Exception e) {
			LOG.error("Exception Getting Industry Transaction DocEntries", e);
			throw e;
		}
		LOG.debug("docEntries size =" + docEntries.size());
		return docEntries;
	}
}
