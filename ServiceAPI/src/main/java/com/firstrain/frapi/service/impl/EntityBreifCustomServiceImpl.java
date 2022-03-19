package com.firstrain.frapi.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.firstrain.content.similarity.DocumentSimilarityUtilV3;
import com.firstrain.frapi.domain.BaseSpec;
import com.firstrain.frapi.domain.Document;
import com.firstrain.frapi.pojo.wrapper.DocumentSet;
import com.firstrain.frapi.repository.EntityBaseServiceRepository;
import com.firstrain.frapi.repository.ExcelProcessingHelperRepository;
import com.firstrain.frapi.service.EntityBaseService;
import com.firstrain.frapi.service.EntityBriefCustomService;
import com.firstrain.frapi.util.ConvertUtil;
import com.firstrain.frapi.util.ServicesAPIUtil;
import com.firstrain.obj.IEntityInfo;
import com.firstrain.obj.IEntityInfoCache;
import com.firstrain.solr.client.DocCatEntry;
import com.firstrain.solr.client.DocEntriesUpdator;
import com.firstrain.solr.client.DocEntry;
import com.firstrain.solr.client.DocListBucket;
import com.firstrain.solr.client.EntityEntry;
import com.firstrain.solr.client.HotListBucket;
import com.firstrain.solr.client.QuoteEntry;
import com.firstrain.solr.client.SearchResult;
import com.firstrain.solr.client.SearchSpec;
import com.firstrain.utils.FR_ArrayUtils;
import com.firstrain.web.pojo.Entity;
import com.firstrain.web.pojo.SearchResultWeb;

@Service
public class EntityBreifCustomServiceImpl implements EntityBriefCustomService {
	private final Logger LOG = Logger.getLogger(EntityBreifCustomServiceImpl.class);

	@Autowired
	@Qualifier("entityBaseServiceRepositoryImpl")
	private EntityBaseServiceRepository entityBaseServiceRepository;

	@Autowired
	private ExcelProcessingHelperRepository excelProcesingHelperRepository;

	@Autowired
	private ConvertUtil convertUtil;

	@Autowired
	private ServicesAPIUtil servicesAPIUtil;

	@Value("${quoteOTRRelevanceThreshold.custome:60}")
	private int quoteOTRRelevanceThreshold;

	@Value("${roles.list}")
	private String roleslistStr;

	@Value("${stopwords.file.path}")
	private String stopwordsFile;

	private final Set<String> rolesList = new HashSet<String>();

	@Autowired
	private EntityBaseService entityBaseService;

	private Map<Integer, Integer> paltinumSourceVsRank;

	private DocumentSimilarityUtilV3 dsutil;

	@PostConstruct
	public void init() {
		if (StringUtils.isNotBlank(roleslistStr)) {
			String[] split = roleslistStr.split(",");
			for (String s : split) {
				if (StringUtils.isBlank(s)) {
					continue;
				}

				rolesList.add(s.trim());
			}

			LOG.info("The roles configured for cohert list : " + rolesList);
		}

		paltinumSourceVsRank = excelProcesingHelperRepository.getPaltinumSourceVsRank();

		try {
			dsutil = new DocumentSimilarityUtilV3(stopwordsFile);
		} catch (Exception ignored) {
		}
	}

	@Override
	public HotListBucket getCoMentionCompanies(List<Long> catIds, Integer scope, Integer count, Integer daysCount) throws Exception {
		List<Integer> scopeList = new ArrayList<Integer>();
		List<String> qList = createQFromLongIds(catIds, scope, scopeList);

		SearchSpec ss = createAndPopulateSearchSpec();
		ss.needHotListCompany = true;
		ss.needHotListTopic = false;
		ss.hotListRows = count;
		ss.hotlistScope = scope;
		ss.hiddenFq = SearchSpec.defaultTokensToExclude;
		ss.setOrder(SearchSpec.ORDER_DATE);
		ss.days = daysCount;
		ss.setStart(0);
		ss.setRows(1);

		ss.qMulti = FR_ArrayUtils.collectionToStringArray(qList);
		ss.scopeMulti = FR_ArrayUtils.collectionToIntArray(scopeList);

		SearchResult searchResult = entityBaseServiceRepository.getSearcher().search(ss);
		return searchResult.facetCompanies;
	}

	@Override
	public SearchResultWeb getWebResultsForCatId(List<Long> secondaryCatIds, Integer scope, Integer count, Integer daysCount)
			throws Exception {

		StringBuilder sb = new StringBuilder();
		for (Long catId : secondaryCatIds) {
			IEntityInfo ei = entityBaseServiceRepository.getEntityInfoCache().catIdToEntity(String.valueOf(catId));
			String searchToken = ei.getSearchToken();
			sb.append(searchToken + " OR ");
		}

		String q = null;
		int len = sb.length();
		if (len > 0) {
			sb.setLength(len - 4);
			q = sb.toString();
		}

		BaseSpec spec = new BaseSpec();
		spec.setCount(count.shortValue());
		spec.setDaysCount(daysCount);
		spec.setScope(scope);

		SearchResultWeb result = new SearchResultWeb();
		DocumentSet webResultsForSearch = entityBaseService.getWebResultsForSearch(q, null, spec, null);
		result.setDocumentSet(webResultsForSearch);

		return result;
	}

	@Override
	public SearchResultWeb getWebResults(Long primaryCatId, List<Long> secondaryCatIds, Integer scope, Integer count, Integer daysCount,
			boolean advanceSort) throws Exception {
		List<String> qList = new ArrayList<String>();
		List<Integer> scopeList = new ArrayList<Integer>();
		IEntityInfoCache entityInfoCache = entityBaseServiceRepository.getEntityInfoCache();

		IEntityInfo companyEntity = entityInfoCache.catIdToEntity(String.valueOf(primaryCatId));
		if (companyEntity == null) {
			return new SearchResultWeb();
		}

		String primaryToken = companyEntity.getSearchToken();
		Map<Long, Entity> topicEntities = new HashMap<Long, Entity>();
		for (Long topicId : secondaryCatIds) {
			String id = String.valueOf(topicId);
			IEntityInfo catIdToEntity = entityInfoCache.catIdToEntity(id);
			if (catIdToEntity == null) {
				continue;
			}

			String token = catIdToEntity.getSearchToken();
			StringBuilder q = new StringBuilder();
			q.append(primaryToken).append(" ").append(token);

			qList.add(q.toString());
			scopeList.add(scope);

			Entity e = new Entity();
			e.setId(id);
			e.setName(catIdToEntity.getName());
			e.setSearchToken(token);
			topicEntities.put(topicId, e);
		}

		SearchSpec ss = createAndPopulateSearchSpec();
		ss.needHotListCompany = false;
		ss.needHotListTopic = false;
		ss.sectionMulti = true;
		ss.needDocList = true;
		ss.needQuotes = true;
		ss.setOrder(SearchSpec.ORDER_DATE);
		ss.days = daysCount;
		ss.setStart(0);
		ss.setRows(count * 3);

		ss.qMulti = FR_ArrayUtils.collectionToStringArray(qList);
		ss.scopeMulti = FR_ArrayUtils.collectionToIntArray(scopeList);

		SearchResult searchResult = entityBaseServiceRepository.getSearcher().search(ss);
		SearchResultWeb srw = new SearchResultWeb();

		if (CollectionUtils.isNotEmpty(searchResult.buckets)) {
			int index = -1;
			Map<String, DocEntry> uniquedocs = new HashMap<String, DocEntry>();
			Map<Long, List<DocEntry>> topicToDocsMap = new HashMap<Long, List<DocEntry>>();

			for (DocListBucket bucket : searchResult.buckets) {
				if (++index == 0) {
					continue;
				}

				List<DocEntry> entries = bucket.getDocs();
				boolean noMatch = bucket.matches <= 0 || CollectionUtils.isEmpty(entries);
				if (noMatch) {
					// skip 0 match topics.
					continue;
				}

				Long catId = secondaryCatIds.get(index - 1);
				try {
					// do similarity comparisons.
					entries = dsutil.processDocument(entries);
					if (CollectionUtils.isEmpty(entries)) {
						continue;
					}

					DocEntriesUpdator.attachFavIconNDocImageDetails(entityBaseServiceRepository.getFavIconServer(),
							entityBaseServiceRepository.getDocImageServer(), entries);

					topicToDocsMap.put(catId, entries);
					for (DocEntry d : entries) {
						List<QuoteEntry> otrQuotes = d.getOtrQuotes();
						if (CollectionUtils.isNotEmpty(otrQuotes)) {
							for (Iterator<QuoteEntry> it = otrQuotes.iterator(); it.hasNext();) {
								QuoteEntry q = it.next();
								if (q.relevance < quoteOTRRelevanceThreshold) {
									it.remove();
								}
							}
						}

						List<DocCatEntry> catEntries = d.getCatEntries();
						if (CollectionUtils.isNotEmpty(catEntries)) {
							ArrayList<DocCatEntry> roles = null;
							for (DocCatEntry dc : catEntries) {
								if (rolesList.contains(dc.entity.id)) {
									if (roles == null) {
										roles = new ArrayList<DocCatEntry>();
									}
									roles.add(dc);
								}
							}

							if (CollectionUtils.isNotEmpty(roles)) {
								d.matchedOthers = roles; // the categories which are roles
							}
						}

						uniquedocs.put(d.sitedocId, d);
					}

				} catch (Exception e) {
					LOG.error("Exception Processing Web Results!", e);
					throw e;
				}
			}

			Set<Long> topicsWithMatchesSet = topicToDocsMap.keySet();
			for (Long topicId : topicsWithMatchesSet) {
				Entity entity = topicEntities.get(topicId);
				srw.getTopicIdHavingDocs().add(entity);
			}
			topicToDocsMap.clear();

			List<DocEntry> resultList = null;
			int totalcount = 0;
			if (advanceSort) {
				resultList = new ArrayList<DocEntry>(uniquedocs.values());
				totalcount = resultList.size();

				resultList = logSortAndFilterDocEntryList(totalcount, resultList, count, compareAdvance); 
				
			} else {
				resultList = new ArrayList<DocEntry>(uniquedocs.values());
				totalcount = resultList.size();

				resultList = logSortAndFilterDocEntryList(totalcount, resultList, count, compareOnRecency); 
				
			}

			List<Document> listDocs = transformToDocuments(resultList);
			DocumentSet documentSet = new DocumentSet();
			documentSet.setDocuments(listDocs);
			documentSet.setFiling(false);
			documentSet.setScope(scope);
			documentSet.setTotalCount(totalcount);

			srw.setDocumentSet(documentSet);
			srw.setPrimaryRegion(companyEntity.getCountry());
		}

		return srw;
	}

	private SearchSpec createAndPopulateSearchSpec() {
		SearchSpec ss = new SearchSpec(new SearchSpec().setTagExclusionScope(SearchSpec.SCOPE_BROAD));
		ss.optimalScopeAndDays = false;
		ss.useLikelySearchIntention = false;
		ss.needSearchSuggestion = false;
		ss.needHotListAll = false;
		return ss;
	}
 
	private List<DocEntry> logSortAndFilterDocEntryList(final int totalcount, List<DocEntry> resultListParam, final Integer count, final Comparator<DocEntry> compareOnRecency) { 
		List<DocEntry> resultList = resultListParam;
		if (LOG.isDebugEnabled()) { 
			LOG.info("total documents :" + totalcount); 
			StringBuilder b = new StringBuilder(); 
			for (int i = 0; i < totalcount; i++) { 
				DocEntry de = resultList.get(i); 
				b.append(de.getSitedocId()).append(de.getTitle()).append("\n"); 
			} 
			LOG.info(b.toString()); 
		} 
		 
		if (totalcount > count) { 
			Collections.sort(resultList, compareOnRecency); 
			resultList = resultList.subList(0, count); 
		} 
		return resultList; 
	} 
	


	private List<Document> transformToDocuments(List<DocEntry> entries) {
		List<Document> listDocs = new ArrayList<Document>();
		for (DocEntry d : entries) {
			Document doc = convertUtil.convertDocumentPOJOFromDocEntry(d, null);

			if (CollectionUtils.isNotEmpty(d.matchedOthers)) {
				List<com.firstrain.frapi.pojo.Entity> roles = new ArrayList<com.firstrain.frapi.pojo.Entity>(d.matchedOthers.size());
				for (DocCatEntry e : d.matchedOthers) {
					com.firstrain.frapi.pojo.Entity e1 = convertUtil.convertEntityFromEntityEntry(e.getEntity(), e.band);
					roles.add(e1);
				}
				doc.setRoleEntities(roles);
			}

			listDocs.add(doc);
		}

		return listDocs;
	}

	private List<String> createQFromLongIds(List<Long> catIds, Integer scope, List<Integer> scopeList) {
		List<String> qList = new ArrayList<String>();

		for (Long catId : catIds) {
			IEntityInfo ei = entityBaseServiceRepository.getEntityInfoCache().catIdToEntity(String.valueOf(catId));
			qList.add(ei.getSearchToken());
			scopeList.add(scope);
		}

		return qList;
	}

	private final Comparator<DocEntry> compareOnRecency = new Comparator<DocEntry>() {
		@Override
		public int compare(DocEntry o1, DocEntry o2) {
			return o1.insertTime.compareTo(o2.insertTime);
		}
	};

	private final Comparator<DocEntry> compareAdvance = new Comparator<DocEntry>() {
		static final int RolesScore = 100;
		static final int personScore = 75;
		static final int platinumSrcScore = 50;
		static final int defaultScore = 10;

		@Override
		public int compare(DocEntry o1, DocEntry o2) {
			Boolean r1 = CollectionUtils.isNotEmpty(o1.matchedOthers);
			Boolean r2 = CollectionUtils.isNotEmpty(o2.matchedOthers);
			Integer score1 = defaultScore, score2 = defaultScore;
			if (r1) {
				score1 += RolesScore;
			}

			if (r2) {
				score2 += RolesScore;
			}

			Boolean q1 = CollectionUtils.isNotEmpty(o1.getOtrQuotes());
			Boolean q2 = CollectionUtils.isNotEmpty(o2.getOtrQuotes());

			if (q1) {
				score1 += personScore;
			}

			return calculateAndCompareScore(o1, o2, q2, score1, score2);

		}

		private int calculateAndCompareScore(final DocEntry o1, final DocEntry o2, final Boolean q2, Integer score1Param, Integer score2) {
		    Integer score1 = score1Param;
		    if (q2) {
		    	score2 += personScore;
		    }
		    
		    if (score1 == score2) {
		    	Integer s1 = platinumRank(o1.getSourceEntity());
		    	Integer s2 = platinumRank(o2.getSourceEntity());
		    	if (s1 >= 0) {
		    		score1 += platinumSrcScore;
		    	}
		    	if (s2 >= 0) {
		    		score2 += platinumSrcScore;
		    	}
		    
		    	if (score1 == score2) {
		    		return o1.insertTime.compareTo(o2.insertTime);
		    	} else {
		    		return score1.compareTo(score2);
		    	}
		    } else {
		    	return score1.compareTo(score2);
		    }
		}

		private Integer platinumRank(EntityEntry sourceEntity) {
			Integer srcId = Integer.valueOf(sourceEntity.getId());
			Integer rank = paltinumSourceVsRank.get(srcId);
			return rank == null ? -100 : rank;
		}

	};

}
