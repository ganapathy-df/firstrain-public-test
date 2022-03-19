package com.firstrain.frapi.service.impl;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.firstrain.content.similarity.DocumentSimilarityUtil;
import com.firstrain.db.obj.Accelerometer;
import com.firstrain.frapi.FRCompletionService;
import com.firstrain.frapi.domain.AutoSuggest;
import com.firstrain.frapi.domain.AutoSuggestInfo;
import com.firstrain.frapi.domain.BaseSpec;
import com.firstrain.frapi.domain.BlendDunsInput;
import com.firstrain.frapi.domain.CompanyTradingRange;
import com.firstrain.frapi.domain.CompanyVolume;
import com.firstrain.frapi.domain.Document;
import com.firstrain.frapi.domain.EntityDetailSpec;
import com.firstrain.frapi.domain.HistoricalStat;
import com.firstrain.frapi.domain.MgmtTurnoverMeta;
import com.firstrain.frapi.domain.Parameter;
import com.firstrain.frapi.domain.Tweet;
import com.firstrain.frapi.domain.TwitterSpec;
import com.firstrain.frapi.domain.WebVolumeEventMeta;
import com.firstrain.frapi.events.IEvents;
import com.firstrain.frapi.obj.EventQueryCriteria;
import com.firstrain.frapi.obj.EventQueryCriteria.EventTypeRange;
import com.firstrain.frapi.obj.GraphQueryCriteria;
import com.firstrain.frapi.obj.MgmtTurnoverServiceSpec;
import com.firstrain.frapi.pojo.Entity;
import com.firstrain.frapi.pojo.Event;
import com.firstrain.frapi.pojo.Graph;
import com.firstrain.frapi.pojo.Graph.GraphFor;
import com.firstrain.frapi.pojo.Graph.Range;
import com.firstrain.frapi.pojo.GraphNode;
import com.firstrain.frapi.pojo.wrapper.DocumentSet;
import com.firstrain.frapi.pojo.wrapper.EventSet;
import com.firstrain.frapi.pojo.wrapper.GraphNodeSet;
import com.firstrain.frapi.pojo.wrapper.TweetSet;
import com.firstrain.frapi.repository.AccelerometerServiceRepository;
import com.firstrain.frapi.repository.EntityBaseServiceRepository;
import com.firstrain.frapi.repository.ExcelProcessingHelperRepository;
import com.firstrain.frapi.service.AutoSuggestService;
import com.firstrain.frapi.service.EntityBaseService;
import com.firstrain.frapi.service.EventService;
import com.firstrain.frapi.service.TwitterService;
import com.firstrain.frapi.util.ContentType;
import com.firstrain.frapi.util.ConvertUtil;
import com.firstrain.frapi.util.DefaultEnums.INPUT_ENTITY_TYPE;
import com.firstrain.frapi.util.EntityHandler;
import com.firstrain.frapi.util.FRAPIConstant;
import com.firstrain.frapi.util.ServicesAPIUtil;
import com.firstrain.obj.IEntityInfo;
import com.firstrain.obj.IEntityInfoCache;
import com.firstrain.solr.client.DateCount;
import com.firstrain.solr.client.DateRange;
import com.firstrain.solr.client.DocCatEntry;
import com.firstrain.solr.client.DocEntriesUpdator;
import com.firstrain.solr.client.DocEntry;
import com.firstrain.solr.client.DocListBucket;
import com.firstrain.solr.client.EntityEntry;
import com.firstrain.solr.client.QuoteEntry;
import com.firstrain.solr.client.SearchException;
import com.firstrain.solr.client.SearchResult;
import com.firstrain.solr.client.SearchSpec;
import com.firstrain.solr.client.SearchTokenEntry;
import com.firstrain.solr.client.SearchTokenEntry.Relation;
import com.firstrain.solr.client.SolrSearcher;
import com.firstrain.solr.client.util.SolrServerReader;
import com.firstrain.utils.FR_ArrayUtils;
import com.firstrain.utils.TimeUtils;
import com.firstrain.utils.TitleUtils;
import com.google.common.collect.Iterables;

import it.unimi.dsi.fastutil.longs.LongArrayList;

@Service
public class EntityBaseServiceImpl implements EntityBaseService {

	private final Logger LOG = Logger.getLogger(EntityBaseServiceImpl.class);

	private final int MAX_ACCELEROMETER_COUNT = 6;
	private final String st_THE_Regex = "^(?i)the\\s+[,'. ]*";
	private final Integer[] daysArr = { 30, 60, 180 };

	@Autowired
	@Qualifier("entityBaseServiceRepositoryImpl")
	private EntityBaseServiceRepository entityBaseServiceRepository;
	@Autowired
	private ExcelProcessingHelperRepository excelProcesingHelperRepository;
	@Autowired
	private AccelerometerServiceRepository accelerometerServiceRepository;
	@Autowired
	private TwitterService twitterService;
	@Autowired
	private EventService eventService;
	@Autowired
	private ConvertUtil convertUtil;
	@Autowired
	private ServicesAPIUtil servicesAPIUtil;
	@Autowired
	private RegionExcelUtilImpl regionExcelUtilImpl;

	@Autowired(required = true)
	protected ThreadPoolTaskExecutor taskExecutor;

	@Value("${executor.timeout}")
	protected long executorTimeout;

	@Value("${business.basics}")
	protected String businessBasics;

	@Value("${uber.topics}")
	protected String uberTopics;

	@Autowired
	private AutoSuggestService autoSuggestService;

	@Override
	public DocumentSet getDocDetails(List<Long> docIds, EntityDetailSpec spec, Collection<String> toExcludeIds,
			short industryClassificationId) throws Exception {

		DocumentSet documentSet = null;
		SolrSearcher searcher = entityBaseServiceRepository.getSearcher();
		try {
			List<DocEntry> docEntryList =
					searcher.fetch(FR_ArrayUtils.collectionToLongArray(docIds), spec.language, industryClassificationId);
			LOG.debug("No. of results for doc id " + docIds + " from solr are " + (docEntryList != null ? docEntryList.size() : 0));
			if (docEntryList == null || docEntryList.isEmpty()) {
				return documentSet;
			}
			attachDocSummaryImageAndHighlightQuote(docEntryList, spec);
			documentSet = new DocumentSet();
			List<Document> documentList = new ArrayList<Document>();
			for (DocEntry entry : docEntryList) {
				ContentType contentType = getContentTypeAndFilterOutCT(entry, toExcludeIds);
				Document docPOJO = convertUtil.convertDocumentPOJOFromDocEntry(entry);
				docPOJO.setContentType(contentType);
				documentList.add(docPOJO);
			}
			documentSet.setDocuments(documentList);
		} catch (Exception e) {
			LOG.error("Error getting doc details for " + docIds, e);
			throw e;
		}
		return documentSet;
	}

	private void attachDocSummaryImageAndHighlightQuote(List<DocEntry> docEntryList, EntityDetailSpec spec) throws Exception {
		try {
			DocEntriesUpdator.attachFavIconNDocImageDetails(entityBaseServiceRepository.getFavIconServer(),
					entityBaseServiceRepository.getDocImageServer(), docEntryList, true, spec.needPhrase);
		} catch (Exception e) {
			LOG.error("Error while attaching docimage and favicon", e);
		}
		// try {
		// if (spec.catId != -1 && spec.needSmartSummary) {
		// IEntityInfo entityInfo = getEntityInfoCache().catIdToEntity(Integer.toString(spec.catId));
		// if (entityInfo != null
		// && (entityInfo.getType() == SearchTokenEntry.SEARCH_TOKEN_TAG_COMPANY
		// || entityInfo.getType() == SearchTokenEntry.SEARCH_TOKEN_TAG_PEOPLE || entityInfo.getType()
		// == SearchTokenEntry.SEARCH_TOKEN_TAG_SECTOR_TOPIC)) {
		// DocEntriesUpdator.attachSmartSummaryDetails(companyServiceRepository.getSmartSummarySolrServer(),
		// docEntryList, spec.catId);
		// }
		// }
		// } catch (Exception e) {
		// LOG.error("Error while attaching doc smart summary", e);
		// // throw e;
		// }
		try {
			for (DocEntry doc : docEntryList) {
				if (doc.otrQuotes != null && !doc.otrQuotes.isEmpty()) {
					for (QuoteEntry qe : doc.otrQuotes) {
						servicesAPIUtil.highlightQuote(qe);
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Error while highlighting quote", e);
		}
	}

	private ContentType getContentTypeAndFilterOutCT(DocEntry doc, Collection<String> toExcludeIds) {
		if (doc.matchedTopics != null) {
			for (Iterator<DocCatEntry> iter = doc.matchedTopics.iterator(); iter.hasNext();) {
				EntityEntry e = iter.next().entity;
				if ((toExcludeIds != null && toExcludeIds.contains(e.id)) || e.type == SearchTokenEntry.SEARCH_TOKEN_TAG_FUNDAMENTAL) {// exclude
					// company
					// topics
					iter.remove();
				}
			}
		}
		if (toExcludeIds != null && doc.matchedCompanies != null) {
			for (Iterator<DocCatEntry> iter = doc.matchedCompanies.iterator(); iter.hasNext();) {
				if (toExcludeIds.contains(iter.next().entity.id)) {
					iter.remove();
				}
			}
		}

		ContentType contentType = null;
		if (doc.matchedContentTypes != null) {
			outer: for (ContentType ct : ContentType.values()) {
				for (DocCatEntry docCat : doc.matchedContentTypes) {
					if (ct.getId() == Integer.parseInt(docCat.getEntity().getId())) {
						contentType = ct;
						break outer;
					}
				}
			}
		}
		if (contentType == null) {
			contentType = ContentType.WEBNEWS;
		}
		return contentType;
	}

	@Override
	public TweetSet getTweetDetails(List<Long> tweetIds, EntityDetailSpec spec) throws Exception {

		TweetSet tweetset = null;
		try {
			TwitterSpec tSpec = new TwitterSpec();
			// tweetset = twitterService.getTweets(new String[] { Integer.toString(spec.catId) },
			// spec.attachGroupInfo, tSpec,
			// FR_ArrayUtils.collectionToLongArray(tweetIds));

			tweetset = twitterService.getTweets(null, spec.attachGroupInfo, tSpec, FR_ArrayUtils.collectionToLongArray(tweetIds));

			if (tweetset == null) {
				return null;
			}
			List<Tweet> tweets = tweetset.getTweets();
			if (tweets == null || tweets.isEmpty()) {
				return null;
			}
			for (Tweet tweet : tweets) {
				// if (spec.needExpandedLinksAndDocForTweet) {
				// Set<Long> groupDocIds = new HashSet<Long>();
				// try {
				// Collection<String> expandedLinks = tweet.getExpandedLinks();
				// if (expandedLinks != null && !expandedLinks.isEmpty()) {
				// List<DocEntry> docList = twitterService.getRelatedDocsForTweet(expandedLinks,
				// groupDocIds, spec.tweetMatchedDocCount);
				// if (docList != null && !docList.isEmpty()) {
				// DocumentSet docSet = new DocumentSet();
				// List<Document> docs = new ArrayList<Document>();
				// attachDocSummaryImageAndHighlightQuote(docList, spec);
				// for (DocEntry de : docList) {
				// ContentType contentType = getContentTypeAndFilterOutCT(de, null);
				// Document docPOJO = convertUtil.convertDocumentPOJOFromDocEntry(de);
				// docPOJO.setContentType(contentType);
				// docPOJO.setDupCount(getGroupDocsCountForGroupId(de.getGroupId()));
				// docs.add(docPOJO);
				// }
				// docSet.setDocuments(docs);
				// tweet.setDocument(docSet);
				// }
				// }
				// } catch (Exception e) {
				// LOG.error("Error while fetching getRelatedDocsForTweet:" + tweet.getTweetId(), e);
				// // throw e;
				// }
				// try {
				// List<DocEntry> docList = twitterService.getCloselyRelatedDocs(tweet, groupDocIds,
				// spec.relatedDocCount);
				// if (docList != null && !docList.isEmpty()) {
				// DocumentSet docSet = new DocumentSet();
				// List<Document> docs = new ArrayList<Document>();
				// for (DocEntry de : docList) {
				// ContentType contentType = getContentTypeAndFilterOutCT(de, null);
				// Document docPOJO = convertUtil.convertDocumentPOJOFromDocEntry(de);
				// docPOJO.setContentType(contentType);
				// docs.add(docPOJO);
				// }
				// docSet.setDocuments(docs);
				// tweet.setRelatedDocument(docSet);
				// }
				// } catch (Exception e) {
				// LOG.error("Error while fetching getCloselyRelatedDocs:" + tweet.getTweetId(), e);
				// }
				// }
				if (spec.attachAlsoTweetedBy) {
					List<Tweet> tweetUsers = twitterService.getTweetUsers(tweet.getGroupId(), Long.valueOf(tweet.getTweetId()));
					tweet.setTweetUsers(tweetUsers);
				}
			}
		} catch (Exception e) {
			LOG.error("Error getting tweet details for " + tweetIds, e);
			throw e;
		}
		return tweetset;
	}

	@Override
	public TweetSet getTweetList(String[] catIdsAll, TwitterSpec tSpec) throws Exception {
		TweetSet tweetSet = null;
		if (catIdsAll == null || catIdsAll.length <= 0) {
			return tweetSet;
		}
		try {
			IEntityInfoCache entityCache = entityBaseServiceRepository.getEntityInfoCache();
			if (tSpec.getStart() == null) {
				tSpec.setStart((short) 0);
			}
			List<Object> catIds = new ArrayList<Object>(catIdsAll.length);
			for (String catId : catIdsAll) {
				IEntityInfo e = entityCache.catIdToEntity(catId);
				if (e != null && e.getTwitterHighCount() >= 0) {
					catIds.add(e.getId());
				}
			}

			if (!catIds.isEmpty()) {
				tSpec.setCatIds(FR_ArrayUtils.arrayListToStringArray(catIds));
				tweetSet = twitterService.getTopTweets(tSpec);
			}
			if (tweetSet != null) {
				tweetSet.setHasMore(servicesAPIUtil.getHasMoreValue(tweetSet.getTotalCount(), tSpec.getRows(), tSpec.getStart()));
				tweetSet.setScope(tSpec.getScope());
			}
		} catch (Exception e) {
			LOG.error("Exception Getting tweet list", e);
			throw e;
		}
		return tweetSet;
	}
	
	@Override
	public DocumentSet gethighlightsResults(String[] qArr, int[] scopeArr, int highlightRows) throws SearchException, SolrServerException {
		
		DocumentSet webResults = null;
		
		SearchSpec searchSpec = new SearchSpec(new SearchSpec().setTagExclusionScope(SearchSpec.SCOPE_BROAD));
		populateSearchSpec(searchSpec); 
		
		searchSpec.setStart(0);
		searchSpec.setRows(1);
		searchSpec.days = FRAPIConstant.HIGHLIGHTS_SEARCH_DAYS;

		/* to get the Highlights */
		searchSpec.sectionMulti = true;
		searchSpec.setRowsHightlight(highlightRows);

		populateQMultiAndScopeMulti(qArr, scopeArr, searchSpec); 
		
		
		SearchResult sr =  entityBaseServiceRepository.getSearcher().search(searchSpec);
		
		
		int docCount = 0;
		List<DocEntry> docEntries = null;
		if (sr != null && sr.buckets != null) {
			docEntries = sr.buckets.get(0).docs;
			docCount = (int) sr.total;
		}

		if (docEntries == null || docEntries.isEmpty()) {
			return null;
		}

		DocEntriesUpdator.attachFavIconNDocImageDetails(entityBaseServiceRepository.getFavIconServer(),
				entityBaseServiceRepository.getDocImageServer(), docEntries, true, false);

		SolrSearcher.setupMatchesForEntries(docEntries, -1);

		ArrayList<Document> listDocs = convertAndPopulateDocuments(docEntries, highlightRows); 
		
		
		if (!listDocs.isEmpty()) {
			webResults = new DocumentSet();
			webResults.setDocuments(listDocs);
			webResults.setTotalCount(docCount);
			webResults.setHasMore(servicesAPIUtil.getHasMoreValue(webResults.getTotalCount(), highlightRows, (short)0));
			webResults.setScope(SearchSpec.SCOPE_NARROW);
			webResults.setFiling(false);
		}
		
		return webResults;
	}
	
	@Override
	public DocumentSet getWebResultsForSearch(String q, final String fq, final BaseSpec spec, BlendDunsInput bdi) throws Exception {

		DocumentSet webResults = null;
		DocumentSimilarityUtil dsutil = null;
		try {
			int desiredCount = spec.getCount();
			String[] qMulti = new String[] { q };
			int[] scopeMulti = new int[] { spec.getScope() };
			List<String> dunsLst = new ArrayList<String>();
			Set<String> uberTopicLst = new HashSet<String>();
			Set<String> businessBasicLst = new HashSet<String>();
			Set<String> regionLst = new HashSet<String>();
			Set<String> businessLineLst = new HashSet<String>();

			if (Boolean.TRUE.equals(bdi.getBlendDUNS())) {

				LOG.debug("BlendDUNS : TRUE");
				String scopeDirective = bdi.getScopeDirective();
				if (StringUtils.isEmpty(scopeDirective)) {
					// scropeDirective not present
					scopeDirective = getScopeDirective(fq, spec, bdi.getDnbEntityMap(), desiredCount);
					bdi.setScopeDirective(scopeDirective);
				}

				// create a function for passing scopeDirective and get
				spec.setSectionMulti(false);
				List<String> qList = null;
				if (StringUtils.isEmpty(scopeDirective)) {
					// set last index to -1 so it will return all q list
					qList = getQListByScopeDirective(bdi.getDnbEntityMap(), -1, -1, dunsLst, uberTopicLst, businessBasicLst, regionLst,
							businessLineLst);
				} else {
					String[] sdStrArr = scopeDirective.split("\\|");
					spec.setDaysCount(Integer.parseInt(sdStrArr[3]));
					qList = getQListByScopeDirective(bdi.getDnbEntityMap(), Integer.parseInt(sdStrArr[1]), Integer.parseInt(sdStrArr[2]),
							dunsLst, uberTopicLst, businessBasicLst, regionLst, businessLineLst);
				}

				LOG.debug("scopeDirective : " + scopeDirective + ", qList : " + qList + ", days : " + spec.getDaysCount());
				qMulti = FR_ArrayUtils.collectionToStringArray(qList);
				scopeMulti = new int[qMulti.length];
				for (int i = 0; i < qMulti.length; i++) {
					scopeMulti[i] = spec.getScope();
				}
			}

			// If pagination is not required, fetch 3 times the desired docs count
			if (!Boolean.TRUE.equals(spec.getNeedPagination())) {
				spec.setCount((short) (spec.getCount() * 3));
			}

			// if(spec.getNeedPagination() == null || !spec.getNeedPagination().booleanValue()) {
			// spec.setCount((short)(spec.getCount()*3));
			// } else {
			// spec.setCount((short)FRAPIConstant.WEBRESULT_SINGLEENTITY);
			// }

			SearchResult searchResult = getSearchResult(qMulti, scopeMulti, fq, spec);
			Integer scope = searchResult.scopeIfAutoSelected;
			List<DocEntry> entries = searchResult.buckets.get(0).docs;
			Integer totalCount = (int) searchResult.total;

			if (entries == null || entries.isEmpty()) {
				return null;
			}

			// Apply doc similarity only if pagination is not required
			if (!Boolean.TRUE.equals(spec.getNeedPagination())) {
				dsutil = entityBaseServiceRepository.getDocumentSimilarityUtil();
				entries = servicesAPIUtil.filterSimilarEntries(entries, dsutil, desiredCount, false);
			}

			DocEntriesUpdator.attachFavIconNDocImageDetails(entityBaseServiceRepository.getFavIconServer(),
					entityBaseServiceRepository.getDocImageServer(), entries, true, spec.isNeedPhrase());

			// Call for populating matched entities with documents.
			Boolean needMatchedEntities = spec.getNeedMatchedEntities();
			if (Boolean.TRUE.equals(needMatchedEntities)) {
				SolrSearcher.setupMatchesForEntries(entries, -1);
			}

			ArrayList<Document> listDocs = convertAndPopulateDocuments(entries, desiredCount); 
			
			
			if (Boolean.TRUE.equals(bdi.getBlendDUNS()) && CollectionUtils.isNotEmpty(dunsLst)) {
				setSecondaryDunsInDocs(listDocs, dunsLst, uberTopicLst, businessBasicLst, regionLst, businessLineLst);
			}

			if (!listDocs.isEmpty()) {
				webResults = new DocumentSet();
				webResults.setDocuments(listDocs);
				webResults.setTotalCount(totalCount);
				webResults.setHasMore(servicesAPIUtil.getHasMoreValue(webResults.getTotalCount(), desiredCount, spec.getStart()));
				if (spec.getScope() == SearchSpec.SCOPE_AUTO) {
					webResults.setScope(scope);
				} else {
					webResults.setScope(spec.getScope());
				}
				webResults.setFiling(false);
			}
		} catch (Exception e) {
			LOG.error("Exception Getting Serach Results for entity brief Service", e);
			throw e;
		} finally {
			if (dsutil != null) {
				dsutil.clear();
			}
		}
		return webResults;
	}
 
	private ArrayList<Document> convertAndPopulateDocuments(final List<DocEntry> entries, final int desiredCount) { 
		ArrayList<Document> listDocs = new ArrayList<Document>(); 
		for (DocEntry entry : entries) { 
			if (listDocs.size() >= desiredCount) { 
				break; 
			} 
			listDocs.add(convertUtil.convertDocumentPOJOFromDocEntry(entry)); 
		} 
		return listDocs; 
	} 
	

	private void setSecondaryDunsInDocs(ArrayList<Document> listDocs, List<String> dunsLst, Set<String> uberTopicLst,
			Set<String> businessBasicLst, Set<String> regionLst, Set<String> businessLineLst) {

		if (CollectionUtils.isEmpty(listDocs)) {
			return;
		}

		for (Document d : listDocs) {
			List<Entity> entityLst = d.getCatEntries();
			if (CollectionUtils.isEmpty(entityLst)) {
				continue;
			}

			setSecondaryDuns(d, entityLst, dunsLst, uberTopicLst, businessBasicLst, regionLst, businessLineLst);
		}

	}

	private void setSecondaryDuns(Document d, List<Entity> entityLst, List<String> dunsLst, Set<String> uberTopicLst,
			Set<String> businessBasicLst, Set<String> regionLst, Set<String> businessLineLst) {

		if (CollectionUtils.isEmpty(dunsLst)) {
			return;
		}

		Map<String, Entity> entityMap = new HashMap<String, Entity>();
		for (Entity e : entityLst) {
			String searchToken = e.getSearchToken();
			if (StringUtils.isNotEmpty(searchToken)) {
				entityMap.put(searchToken, e);
			}
		}


		String startingDuns = dunsLst.get(0);
		Entity e = entityMap.get(startingDuns);
		if (e != null) {
			d.setPrimaryDunsMatchStr(e.getName());
			e.setPrimaryDunsMatch(Boolean.TRUE);
		} else {

			int i = 0;
			for (String dunST : dunsLst) {
				if (i == 0) {
					i++;
					continue;
				}

				Entity e1 = entityMap.get(dunST);
				if (e1 != null) {
					d.setPrimaryDunsMatchStr(e1.getName());
					e1.setPrimaryDunsMatch(Boolean.TRUE);
				}
				i++;
			}

			populateAdditionalMatchQualifier(d, uberTopicLst, entityMap);
			populateAdditionalMatchQualifier(d, businessBasicLst, entityMap);
			populateAdditionalMatchQualifier(d, regionLst, entityMap);
			populateAdditionalMatchQualifier(d, businessLineLst, entityMap);

		}

	}

	private void populateAdditionalMatchQualifier(Document d, Set<String> searchTokenList, Map<String, Entity> entityMap) {

		if (StringUtils.isNotEmpty(d.getAdditionalMatchQualifierStr())) {
			return;
		}

		StringBuffer sb = new StringBuffer();
		for (String searchToken : searchTokenList) {
			Entity e1 = entityMap.get(searchToken);
			if (e1 != null) {
				sb.append(", " + e1.getName());
				e1.setAdditionalMatchQualifier(Boolean.TRUE);
			}
		}

		if (sb.length() > 0) {
			d.setAdditionalMatchQualifierStr(sb.toString());
		}
	}

	private String getScopeDirective(final String fq, final BaseSpec spec, Map<String, Entity> dnbEntityMap, int desiredCount)
			throws InterruptedException, ExecutionException {

		FRCompletionService<Object[]> completionService = new FRCompletionService<Object[]>(taskExecutor.getThreadPoolExecutor());
		spec.setSectionMulti(true);
		spec.setStart((short) 0);
		Map<Integer, Integer> iVsDunsIndx = new HashMap<Integer, Integer>();
		List<String> qList = getQList(dnbEntityMap, iVsDunsIndx);

		LOG.info("qList : " + qList);
		for (final Integer days : daysArr) {
			final String[] qMulti = FR_ArrayUtils.collectionToStringArray(qList);

			final int[] scopeMulti = new int[qMulti.length];
			for (int i = 0; i < qMulti.length; i++) {
				scopeMulti[i] = spec.getScope();
			}

			// open asynchronous threads
			completionService.submit(new Callable<Object[]>() {
				@Override
				public Object[] call() throws Exception {
					SearchResult searchResult = getSearchResult(qMulti, scopeMulti, fq, spec, days);
					return new Object[] { days, searchResult };
				}
			});
		}

		Map<Integer, SearchResult> res = collectResponse(completionService);
		String scopeDirective = getScopeDirective(desiredCount, iVsDunsIndx, res, true);
		return StringUtils.isNotEmpty(scopeDirective) ? scopeDirective : getScopeDirective(desiredCount, iVsDunsIndx, res, false);
	}

	private String getScopeDirective(int desiredCount, Map<Integer, Integer> iVsDunsIndx, Map<Integer, SearchResult> res,
			boolean isFirstDunsOnly) {

		StringBuffer sb = null;

		for (Map.Entry<Integer, Integer> map11 : iVsDunsIndx.entrySet()) {
			LOG.debug("key : " + map11.getKey() + ", value : " + map11.getValue());
		}

		for (Integer days : daysArr) {

			days = isFirstDunsOnly ? 180 : days;

			Map<Long, DocEntry> entriesMap = new HashMap<Long, DocEntry>();
			SearchResult sr = res.get(days);
			for (int i = 1; i < (isFirstDunsOnly ? 3 : sr.buckets.size()); i++) {

				sb = new StringBuffer();
				sb.append(iVsDunsIndx.get(1)).append("|").append(iVsDunsIndx.get(i)).append("|").append(getContentAlgo(i - 2)).append("|")
						.append(days);

				DocListBucket dlb = sr.buckets.get(i);
				LOG.debug("days : " + days + ", i : " + i + ", docs size : " + dlb.docs.size());

				if (CollectionUtils.isEmpty(dlb.docs)) {
					continue;
				}

				for (DocEntry docEntry : dlb.docs) {
					LOG.debug("sitedocId: " + docEntry.getSitedocId());
					entriesMap.put(docEntry.getGroupId(), docEntry);
				}

				if (entriesMap.size() >= desiredCount) {
					return sb.toString();
				}
			}

			if (isFirstDunsOnly) {
				return null;
			}
		}
		return sb != null ? sb.toString() : null;
	}

	private int getContentAlgo(int i) {

		if (i <= 0) {
			return i;
		} else if (i == 1 || i == 4 || i == 7) {
			return 1;
		} else if (i == 2 || i == 5 || i == 8) {
			return 2;
		} else if (i == 3 || i == 6 || i == 9) {
			return 3;
		}
		return -2;
	}

	private Map<Integer, SearchResult> collectResponse(FRCompletionService<Object[]> completionService)
			throws InterruptedException, ExecutionException {
		Map<Integer, SearchResult> res = new HashMap<Integer, SearchResult>();
		int submissions = completionService.getSubmissions();
		for (int i = 0; i < submissions; i++) {
			Object[] obj = null;
			Future<Object[]> f = completionService.poll(this.executorTimeout, TimeUnit.MILLISECONDS);
			if (f != null) {
				obj = f.get();
				if (obj != null) {
					res.put((Integer) obj[0], (SearchResult) obj[1]);
				}
			} else {
				LOG.warn("service not responded for the given timeout " + this.executorTimeout);
			}
		}
		return res;
	}

	private List<String> getQListByScopeDirective(Map<String, Entity> dnbEntityMap, Integer lastDuns, Integer contentAlgoIndx,
			List<String> dunsLst, Set<String> uberTopicLst, Set<String> businessBasicLst, Set<String> regionLst,
			Set<String> businessLineLst) {

		if (MapUtils.isEmpty(dnbEntityMap)) {
			return null;
		}


		List<String> qList = new ArrayList<String>();
		Entity primaryEntity = null;
		Integer dunsIndx = 0;
		for (Map.Entry<String, Entity> map : dnbEntityMap.entrySet()) {
			Entity e = map.getValue();
			if (e == null) {
				continue;
			}
			String searchToken = e.getSearchToken();
			dunsLst.add(searchToken);
		}

		for (Map.Entry<String, Entity> map : dnbEntityMap.entrySet()) {

			Entity e = map.getValue();
			if (e == null) {
				dunsIndx++;
				continue;
			}

			String searchToken = e.getSearchToken();
			if (primaryEntity == null) {
				primaryEntity = e;
				qList.add(searchToken);
				if (lastDuns == dunsIndx && contentAlgoIndx == -1) {
					return qList;
				}
				qList.add(getAttachedWithUberTopics(dnbEntityMap, uberTopics, uberTopicLst));
				if (lastDuns == dunsIndx && contentAlgoIndx == 0) {
					return qList;
				}
			} else {
				qList.add(getStringSearchTokenByCatIds(searchToken, businessBasics, businessBasicLst));
				if (lastDuns == dunsIndx && contentAlgoIndx == 1) {
					return qList;
				}
				qList.add(getCountrySearchToken(searchToken, primaryEntity.getCountry(), regionLst));
				if (lastDuns == dunsIndx && contentAlgoIndx == 2) {
					return qList;
				}
				qList.add(getStringSearchTokenByCatIds(searchToken,
						primaryEntity.getBizLineCatIds() != null ? primaryEntity.getBizLineCatIds().toString() : "", businessLineLst));
				if (lastDuns == dunsIndx && contentAlgoIndx == 3) {
					return qList;
				}
			}
			dunsIndx++;
		}
		return qList;
	}

	private List<String> getQList(Map<String, Entity> dnbEntityMap, Map<Integer, Integer> iVsDunsIndxMap) {

		if (MapUtils.isEmpty(dnbEntityMap)) {
			return null;
		}

		List<String> qList = new ArrayList<String>();
		Entity primaryEntity = null;
		Integer i = 1;
		Integer dunsIndx = 0;
		for (Map.Entry<String, Entity> map : dnbEntityMap.entrySet()) {

			Entity e = map.getValue();
			if (e == null) {
				dunsIndx++;
				continue;
			}

			String searchToken = e.getSearchToken();
			if (primaryEntity == null) {
				primaryEntity = e;
				i = addIntoList(iVsDunsIndxMap, qList, i, dunsIndx, searchToken);
				i = addIntoList(iVsDunsIndxMap, qList, i, dunsIndx, getAttachedWithUberTopics(dnbEntityMap, uberTopics, null));
			} else {
				i = addIntoList(iVsDunsIndxMap, qList, i, dunsIndx, getStringSearchTokenByCatIds(searchToken, businessBasics, null));
				i = addIntoList(iVsDunsIndxMap, qList, i, dunsIndx,
						getCountrySearchToken(searchToken, primaryEntity.getCountry(), null));
				i = addIntoList(iVsDunsIndxMap, qList, i, dunsIndx,
						getStringSearchTokenByCatIds(searchToken,
								primaryEntity.getBizLineCatIds() != null ? primaryEntity.getBizLineCatIds().toString() : "", null));
			}
			dunsIndx++;
		}
		return qList;
	}

	private String getAttachedWithUberTopics(Map<String, Entity> dnbEntityMap, String catIds, Set<String> searchTokenLst) {
		return getEntitiesInOR(dnbEntityMap) + " AND " + getCatIdsInOR(catIds, searchTokenLst);
	}

	private String getCatIdsInOR(String catIds, Set<String> searchTokenLst) {

		if (StringUtils.isEmpty(catIds)) {
			return "BL:BUSINESS_LINE_NOT_FOUND";
		}

		StringBuffer sb = new StringBuffer();
		for (String s : catIds.split(",")) {
			IEntityInfo iEntityInfo = entityBaseServiceRepository.getEntityInfoCache().catIdToEntity(s.trim());
			if (iEntityInfo == null) {
				continue;
			}
			String st = iEntityInfo.getSearchToken();
			if (searchTokenLst != null) {
				searchTokenLst.add(st);
			}
			sb.append(st).append(" OR ");
		}

		if (sb.length() > 0) {
			sb.setLength(sb.length() - 4);
		} else {
			return "BL:BUSINESS_LINE_NOT_FOUND";
		}
		return sb.toString();
	}

	private String getEntitiesInOR(Map<String, Entity> dnbEntityMap) {
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, Entity> map : dnbEntityMap.entrySet()) {

			Entity e = map.getValue();
			if (e == null) {
				continue;
			}

			String searchToken = e.getSearchToken();
			sb.append(searchToken).append(" OR ");
		}

		if (sb.length() > 0) {
			sb.setLength(sb.length() - 4);
		}
		return sb.toString();
	}

	private Integer addIntoList(Map<Integer, Integer> iVsDunsIndxMap, List<String> qList, Integer iParam, Integer dunsIndx, String searchToken) {
		Integer i = iParam;
		qList.add(searchToken);
		iVsDunsIndxMap.put(i, dunsIndx);
		i++;
		return i;
	}

	private String getCountrySearchToken(String st, String country, Set<String> searchTokenLst) {

		Map<String, String> regionNameVsSearchToken = regionExcelUtilImpl.getRegionNameVsSearchToken();
		String searchToken = regionNameVsSearchToken.get(country);

		if (searchToken == null) {
			return "R:REGION_NOT_FOUND";
		}

		if (searchTokenLst != null) {
			searchTokenLst.add(searchToken);
		}
		return st + " AND " + searchToken;
	}

	private String getStringSearchTokenByCatIds(String searchToken, String catIds, Set<String> searchTokenLst) {
		return searchToken + " AND " + getCatIdsInOR(catIds, searchTokenLst);
	}

	@Override
	public SearchResult getSearchResult(String[] qMulti, int[] scopeMulti, String fq, BaseSpec spec) throws Exception {
		return getSearchResult(qMulti, scopeMulti, fq, spec, null);

	}

	public SearchResult getSearchResult(String[] qMulti, int[] scopeMulti, String fq, BaseSpec spec, Integer days) throws Exception {

		SearchResult sr = null;
		try {

			SearchSpec searchSpec = new SearchSpec(new SearchSpec().setTagExclusionScope(SearchSpec.SCOPE_BROAD));

			searchSpec.needHighlighting = false;
			searchSpec.needHotListAll = false;
			searchSpec.needSearchSuggestion = false;
			searchSpec.useLikelySearchIntention = false;
			searchSpec.needBodyLength = true;
			searchSpec.needQuotes = true;
			searchSpec.setOrder(SearchSpec.ORDER_DATE);
			searchSpec.start = spec.getStart() != null ? spec.getStart() : 0;
			searchSpec.setRows(spec.getCount());
			searchSpec.sectionMulti = spec.isSectionMulti();

			if (days != null) {
				searchSpec.days = days;
			} else if (spec.getDaysCount() > 0) {
				searchSpec.days = spec.getDaysCount();
			} else {
				searchSpec.days = FRAPIConstant.BASIC_WEB_RESULTS_SEARCH_DAYS;
			}
			if (spec.getLastDay() != null && !spec.getLastDay().isEmpty()) {
				SimpleDateFormat format1 = new SimpleDateFormat(DateRange.DEFAULT_DATE_FORMAT);
				DateRange dateRange;
				try {
					Calendar cal = Calendar.getInstance(SolrSearcher.getTimeZone());
					Date lastDay = format1.parse(spec.getLastDay());
					cal.setTime(lastDay);
					cal.add(Calendar.DATE, -searchSpec.days);
					dateRange = new DateRange(cal.getTime(), lastDay, "");
				} catch (ParseException e) {
					// get n days from end of today, treat 1 day as 24 hours
					// from the next full hour
					LOG.error("Error while parsing last day in input param.");
					throw e;
				}

				searchSpec.lastDay = format1.format(dateRange.getEnd());

				if (LOG.isDebugEnabled()) {
					LOG.debug("lastDate " + searchSpec.lastDay + " days " + searchSpec.days);
				}
			}

			populateQMultiAndScopeMulti(qMulti, scopeMulti, searchSpec); 
			

			if (fq != null && !fq.isEmpty()) {
				searchSpec.fq = fq;
			}

			if (spec.getExcludeArticleIdsSSV() != null && !spec.getExcludeArticleIdsSSV().isEmpty()) {
				searchSpec.setExcludeDocIds(spec.getExcludeArticleIdsSSV());
			}
			if (spec.getIndustryClassificationId() != null) {
				searchSpec.setIndustryClassificationId(spec.getIndustryClassificationId());
			}
			if (spec.getExcludeSourceIdsSSV() != null && !spec.getExcludeSourceIdsSSV().isEmpty()) {
				searchSpec.setExcludeSourceIds(spec.getExcludeSourceIdsSSV());
			} else if (spec.getIncludeSourceIdsSSV() != null && !spec.getIncludeSourceIdsSSV().isEmpty()) {
				searchSpec.setIncludeSourceIds(spec.getIncludeSourceIdsSSV());
			}

			sr = entityBaseServiceRepository.getSearcher().search(searchSpec);

		} catch (SearchException e) {
			LOG.error("Exception Getting Web Results From Searcher!", e);
			throw e;
		}
		return sr;
	}
 
	private void populateQMultiAndScopeMulti(final String[] qMulti, final int[] scopeMulti, final SearchSpec searchSpec) { 
		if (qMulti.length >= FRAPIConstant.SEARCHTOKEN_COUNT) { 
			String[] arr = new String[FRAPIConstant.SEARCHTOKEN_COUNT]; 
			int[] sArr = new int[FRAPIConstant.SEARCHTOKEN_COUNT]; 
		 
			System.arraycopy(qMulti, 0, arr, 0, FRAPIConstant.SEARCHTOKEN_COUNT); 
			System.arraycopy(scopeMulti, 0, sArr, 0, FRAPIConstant.SEARCHTOKEN_COUNT); 
		 
			searchSpec.qMulti = arr; 
			searchSpec.scopeMulti = sArr; 
		} else { 
			searchSpec.qMulti = qMulti; 
			searchSpec.scopeMulti = scopeMulti; 
		} 
	} 
	

	@Override
	public DocumentSet getWebResults(SearchResult sr, BaseSpec spec) throws Exception {
		DocumentSet webresults = null;
		DocumentSimilarityUtil dsutil = null;
		try {
			boolean filterSimilarEntries = !(Boolean.TRUE.equals(spec.getNeedPagination()));
			dsutil = entityBaseServiceRepository.getDocumentSimilarityUtil();
			boolean needImage = spec.getNeedImage() == null ? false : spec.getNeedImage();
			webresults = makeDocSetFromEntries(sr, filterSimilarEntries, spec.getCount(), dsutil, null, needImage,
					(spec.getStart() != null) ? spec.getStart() : 0, false, spec.isNeedPhrase());
			if (webresults != null) {
				webresults.setScope(sr.searchSpec.scope);
				webresults.setHasMore(servicesAPIUtil.getHasMoreValue(webresults.getTotalCount(), spec.getCount(), spec.getStart()));
			}
		} catch (Exception e) {
			LOG.error("Exception Processing Web Results!", e);
			throw e;
		} finally {
			if (dsutil != null) {
				dsutil.clear();
			}
		}
		return webresults;
	}

	@Override
	public DocumentSet makeDocSetFromEntries(SearchResult sr, boolean filterSimilarEntries, int reqCountAfterFiltering,
			DocumentSimilarityUtil dsutil, ContentType contentType, boolean needImage, int start, boolean isSingleEntityWebResult,
			boolean needPhrase) throws Exception {
		DocumentSet documentSet = null;
		try {

			if (sr.bucketType != SearchTokenEntry.SEARCH_TOKEN_UNKNOWN || sr.buckets.size() != 1) {
				return null;
			}

			List<DocEntry> entries = sr.buckets.get(0).docs;

			if (entries == null || entries.isEmpty()) {
				return null;
			}
			// filter similar DocEntries
			if (filterSimilarEntries && dsutil != null) {
				entries = servicesAPIUtil.filterSimilarEntries(entries, dsutil, reqCountAfterFiltering, isSingleEntityWebResult);
			}

			if (needImage || needPhrase) {
				DocEntriesUpdator.attachFavIconNDocImageDetails(entityBaseServiceRepository.getFavIconServer(),
						entityBaseServiceRepository.getDocImageServer(), entries, needImage, needPhrase);
			}
			ArrayList<com.firstrain.frapi.domain.Document> listDocs = new ArrayList<Document>(entries.size());
			if (isSingleEntityWebResult) {
				if (entries.size() > start) {
					for (int i = start; i < start + reqCountAfterFiltering && i < entries.size(); i++) {
						listDocs.add(convertUtil.convertDocumentPOJOFromDocEntry(entries.get(i), contentType));
					}
				}
			} else {
				for (DocEntry entry : entries) {
					if (listDocs.size() >= reqCountAfterFiltering) {
						break;
					}
					listDocs.add(convertUtil.convertDocumentPOJOFromDocEntry(entry, contentType));
				}
			}
			if (!listDocs.isEmpty()) {
				documentSet = new DocumentSet();
				documentSet.setDocuments(listDocs);
				documentSet.setFiling(false);
				if (isSingleEntityWebResult) {
					documentSet.setTotalCount(entries.size());
				} else {
					documentSet.setTotalCount((int) sr.total);
				}
			}
		} catch (Exception e) {
			LOG.error("Exception Making DocumentSet!", e);
			throw e;
		}
		return documentSet;
	}

	@Override
	public SearchResult getSearchResultForAnalystComments(String searchToken, BaseSpec spec) throws Exception {

		SearchResult sr = null;
		try {

			SearchSpec searchSpec = new SearchSpec(new SearchSpec().setTagExclusionScope(SearchSpec.SCOPE_BROAD));
			searchSpec.needHighlighting = false;
			populateSearchSpec(searchSpec); 
			
			searchSpec.start = spec.getStart() != null ? spec.getStart() : 0;
			searchSpec.setRows(spec.getCount());
			searchSpec.days = spec.getDaysCount();
			searchSpec.scope = SearchSpec.SCOPE_NARROW;
			searchSpec.setQ(searchToken);
			searchSpec.setFq("T:AnalystComments OR T:AnalystRatings");

			if (spec.getExcludeArticleIdsSSV() != null && !spec.getExcludeArticleIdsSSV().isEmpty()) {
				searchSpec.setExcludeDocIds(spec.getExcludeArticleIdsSSV());
			}
			sr = entityBaseServiceRepository.getSearcher().search(searchSpec);
		} catch (SearchException e) {
			LOG.error("Exception Getting Analyst Comments Results From Searcher!", e);
			throw e;
		}
		return sr;
	}
 
	private void populateSearchSpec(final SearchSpec searchSpec) { 
		searchSpec.needHotListAll = false; 
		searchSpec.needSearchSuggestion = false; 
		searchSpec.useLikelySearchIntention = false; 
		 
		searchSpec.setOrder(SearchSpec.ORDER_DATE); 
	} 
	

	@SuppressWarnings("unchecked")
	@Override
	public EventSet getEventSetForMTEvents(List<Integer> companyIds, BaseSpec baseSpec) throws Exception {

		EventSet eventSet = null;
		try {
			int startEventType = baseSpec.getStartEventType();
			int endEventType = baseSpec.getEndEventType();
			if (startEventType == 0) {
				startEventType = 1;
			}
			if (endEventType == 0) {
				endEventType = 299;
			}
			EventTypeRange range = new EventTypeRange(startEventType, endEventType);
			MgmtTurnoverServiceSpec spec = servicesAPIUtil.getMgmtSpec(baseSpec.getCount(), baseSpec.getDaysCount(),
					baseSpec.getStart() != null ? baseSpec.getStart() : 0);

			SolrDocumentList sdList = null;
			if (baseSpec.isCustomized()) {
				FRCompletionService<SolrDocumentList> completionService =
						new FRCompletionService<SolrDocumentList>(taskExecutor.getThreadPoolExecutor());
				int i = 0;
				for (List<Integer> partition : Iterables.partition(companyIds, 1000)) {
					i++;
					makeSolrCallParallel(baseSpec, range, spec, partition, completionService);
				}

				for (; i > 0; i--) {
					Future<SolrDocumentList> f = completionService.poll(this.executorTimeout, TimeUnit.MILLISECONDS);
					SolrDocumentList solrDocList = f.get();
					if (solrDocList != null) {
						if (sdList == null) {
							sdList = solrDocList;
						} else {
							sdList.addAll(solrDocList);
						}
					}
				}
			} else {
				sdList = eventService.getMgmtFromSolr(companyIds, spec, true, range, baseSpec);
			}

			List<IEvents> eventList = eventService.getEntityEventsFromSolr(sdList, null);

			if (baseSpec.isCustomized()) {
				Collections.sort(eventList, new Comparator() {
					@Override
					public int compare(Object o1, Object o2) {
						IEvents e1 = (IEvents) o1;
						IEvents e2 = (IEvents) o2;
						return e2.getReportDate().compareTo(e1.getReportDate());
					}
				});
			}

			if (eventList != null && !eventList.isEmpty()) {
				List<Event> listOfEvents = new ArrayList<Event>();
				Set<Long> trendIds = new HashSet<Long>();
				for (IEvents event : eventList) {
					Map<String, Object> props = event.getProperties();
					Long trendId = (Long) props.get("trendID");
					if (trendIds.contains(trendId)) {
						continue;
					}
					trendIds.add(trendId);
					Event eventPojo = convertUtil.convertToBaseType(event, false, true, true);
					String eventId = eventPojo.getEventId();
					eventPojo.setEventId(eventId);
					listOfEvents.add(eventPojo);

				}
				if (!listOfEvents.isEmpty()) {
					eventSet = new EventSet();

					if (baseSpec.isCustomized()) {
						eventSet.setTotalCount(listOfEvents.size());
					} else {
						eventSet.setTotalCount((int) sdList.getNumFound());
					}
					// Store size of the original data set returned by Solr query
					eventSet.setUnfilteredDataSize(sdList.size());
					eventSet.setHasMore(
							servicesAPIUtil.getHasMoreValue(eventSet.getTotalCount(), baseSpec.getCount(), baseSpec.getStart()));
					eventSet.setEvents(listOfEvents);
				}
			}
		} catch (Exception e) {
			LOG.error("Error while fetching event info for company ids: " + companyIds, e);
			throw e;
		}
		return eventSet;
	}

	private void makeSolrCallParallel(final BaseSpec baseSpec, final EventTypeRange range, final MgmtTurnoverServiceSpec spec,
			final List<Integer> partition, FRCompletionService<SolrDocumentList> completionService) {

		completionService.submit(new Callable<SolrDocumentList>() {
			@Override
			public SolrDocumentList call() throws Exception {
				return eventService.getMgmtFromSolr(partition, spec, true, range, baseSpec);
			}
		});
	}

	@Override
	public Graph getWebVolumeGraph(String catId, final int[] companyIdsArr, final int[] tCatIds, BaseSpec spec, GraphFor graphFor,
			int nDaysFromTodayParam, int scope, int[] eventType, boolean needSignals) throws Exception {

		int nDaysFromToday = nDaysFromTodayParam;
		int days = spec.getDaysCount();
		Graph graphData = null;
		try {
			IEntityInfoCache entityInfoCache = entityBaseServiceRepository.getEntityInfoCache();
			IEntityInfo entityInfo = entityInfoCache.catIdToEntity(catId);
			if (entityInfo == null) {
				return graphData;
			}
			boolean genrateGraph = false;
			EventQueryCriteria criteria = null;
			DateRange dateRange = null;
			if (nDaysFromToday > Parameter.DEFAULT_N_DAY_FROM_TODAY) {
				// nDaysFromToday = nDaysFromToday + 1; // added 1 as daterange do -1 when setting end date
				// from lastDate in DateRange.
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DATE, -nDaysFromToday);
				Date lastDay = cal.getTime();

				if (entityInfo.getType() == SearchTokenEntry.SEARCH_TOKEN_TAG_COMPANY) {
					criteria = new EventQueryCriteria(new int[] {entityInfo.getCompanyId()}, null, lastDay, days);
					genrateGraph = true;
				} else if (entityInfo.getType() == SearchTokenEntry.SEARCH_TOKEN_TAG_SECTOR_TOPIC) {
					criteria = new EventQueryCriteria(null, new int[] {Integer.parseInt(catId)}, lastDay, days);
					genrateGraph = true;
				} else if (entityInfo.getType() == SearchTokenEntry.SEARCH_TOKEN_TAG_INDUSTRY) {
					if ((companyIdsArr != null && companyIdsArr.length > 0) || (tCatIds != null && tCatIds.length > 0)) {
						criteria = new EventQueryCriteria(companyIdsArr, tCatIds, lastDay, days);
					}
					genrateGraph = true;
				}

				try {
					// get n days from a specific date
					nDaysFromToday = nDaysFromToday + 1; // added 1 as daterange do -1 when setting end date
					// from lastDate in DateRange.
					Calendar cal1 = Calendar.getInstance();
					cal1.add(Calendar.DATE, -nDaysFromToday);
					Date lastDayValue = cal1.getTime();

					SimpleDateFormat format1 = new SimpleDateFormat(DateRange.DEFAULT_DATE_FORMAT);
					String lastDay1 = format1.format(lastDayValue);
					dateRange = new DateRange(lastDay1, DateRange.DEFAULT_DATE_FORMAT, days, SolrSearcher.getTimeZone());
				} catch (ParseException e) {
					LOG.error("Date Range threw parse exception", e);
					dateRange = new DateRange(days, SolrSearcher.getTimeZone());
				}
			} else {
				if (entityInfo.getType() == SearchTokenEntry.SEARCH_TOKEN_TAG_COMPANY) {
					criteria = new EventQueryCriteria(new int[] {entityInfo.getCompanyId()}, null, days);
					genrateGraph = true;
				} else if (entityInfo.getType() == SearchTokenEntry.SEARCH_TOKEN_TAG_SECTOR_TOPIC) {
					criteria = new EventQueryCriteria(null, new int[] {Integer.parseInt(catId)}, days);
					genrateGraph = true;
				} else if (entityInfo.getType() == SearchTokenEntry.SEARCH_TOKEN_TAG_INDUSTRY) {
					if ((companyIdsArr != null && companyIdsArr.length > 0) || (tCatIds != null && tCatIds.length > 0)) {
						criteria = new EventQueryCriteria(companyIdsArr, tCatIds, days);
					}
					genrateGraph = true;
				}
				if (eventType != null) {
					criteria.setEventTypeIds(eventType);
				}
			}
			if (!genrateGraph) {
				return graphData;
			}

			List<Event> signalList = null;
			if (criteria != null && needSignals) {
				criteria.setStart(spec.getStart());

				Map<Integer, SolrDocument> eventDocMap = createEventDocMap(spec); 
				
				List<IEvents> eventList = eventService.getEventsFromSolr(criteria, eventDocMap);

				LOG.debug("spec.getCount() for Graph Events " + spec.getCount());
				List<IEvents> events = eventService.applyGraphEventFilter(eventList);
				events = eventService.applyBC(events, false, criteria.getNoOfEvents());
				events = eventService.applyBSA(events, spec.getCount(), false);

				// in case of related event we need to return html title.
				signalList = convertUtil.convertToBaseTypeForGraph(events, !spec.isNeedRelatedDoc());
				convertUtil.attachPropertiesForExpiredEvents(entityBaseServiceRepository.getSearcher(), signalList);

				// attaching related document with event
				if (spec.isNeedRelatedDoc()) {
					attachRelatedDocumentDetails(signalList, eventDocMap, spec);
				}
				signalList = getEventSetWithId(signalList);
			}

			GraphQueryCriteria qCriteria = new GraphQueryCriteria();
			if (dateRange != null) {
				qCriteria.setDateRange(dateRange);
			}
			List<SearchTokenEntry> tokens = new ArrayList<SearchTokenEntry>();
			tokens.add(new SearchTokenEntry(entityInfo.getSearchToken(), Relation.DEFAULT));

			qCriteria.setScope(scope);
			qCriteria.setSearchTokens(tokens);
			qCriteria.setCompanyId(entityInfo.getCompanyId());
			qCriteria.setCategoryId(Integer.parseInt(catId));
			qCriteria.setNumberOfDays(days);
			if (LOG.isDebugEnabled() && criteria != null) {
				LOG.debug(criteria.toString());
			}
			List<CompanyVolume> compVolumeInfo = getWebVolumeInfoFromSolr(qCriteria);

			graphData = generateGraphObject(qCriteria, compVolumeInfo, nDaysFromToday);
			servicesAPIUtil.mapHistoricalStatsToTriggers(graphData, signalList);
			graphData.setGraphFor(graphFor);
		} catch (SearchException e) {
			LOG.error("Exception getting Graph Data", e);
			throw e;
		}
		return graphData;
	}

	public List<Event> getEventSetWithId(List<Event> signalList) throws Exception {
		if (signalList == null) {
			return null;
		}
		for (Event eventPojo : signalList) {
			EntityHandler.addId(eventPojo);
		}
		return signalList;
	}

	@Override
	public List<CompanyVolume> getWebVolumeInfoFromSolr(GraphQueryCriteria criteria) throws SearchException {

		String q = SolrSearcher.constructInput(criteria.getSearchTokens());

		SearchSpec searchSpec = new SearchSpec();
		searchSpec.setTagExclusionScope(SearchSpec.SCOPE_BROAD);
		searchSpec.setScope(criteria.getScope());
		searchSpec.days = criteria.getNumberOfDays();
		searchSpec.setQ(q);
		searchSpec.setRows(0);
		searchSpec.needHotListAll = false;
		searchSpec.needHotListDate = true;

		DateRange dr = criteria.getDateRange();
		if (dr != null) {
			SimpleDateFormat format1 = new SimpleDateFormat(DateRange.DEFAULT_DATE_FORMAT);
			searchSpec.lastDay = format1.format(dr.getEnd());
		}
		SearchResult result = entityBaseServiceRepository.getSearcher().search(searchSpec);
		return toCompanyVolumeInfo(result.dateCount, criteria.getScope());
	}

	private List<CompanyVolume> toCompanyVolumeInfo(DateCount dc, int scope) {
		if (dc == null) {
			return null;
		}

		List<CompanyVolume> compVolumeInfo = new ArrayList<CompanyVolume>();
		// let me first compute out the desired range of diffId.
		SimpleDateFormat df = new SimpleDateFormat(DateRange.DEFAULT_DATE_FORMAT);
		df.setTimeZone(SolrSearcher.getTimeZone());
		Date initDate = null;
		try {
			initDate = df.parse(FRAPIConstant.INITIAL_DATE);
		} catch (ParseException e) {
			// Drop this exception
			initDate = new Date();
		}

		DateRange dr = new DateRange(initDate, new Date(dc.start), "");
		int firstDiffId = dr.getDays();
		Calendar cal = Calendar.getInstance(SolrSearcher.getTimeZone());
		cal.setTime(initDate);
		cal.add(Calendar.DAY_OF_MONTH, firstDiffId);

		LongArrayList counts = dc.getCounts();
		for (int i = 0; i < counts.size(); i++) {
			int count = (int) counts.getLong(i);

			CompanyVolume cv = new CompanyVolume();
			cv.setDiffId(firstDiffId + i);
			cv.setTotal(count);

			cv.setDate(new Timestamp(cal.getTimeInMillis()));
			compVolumeInfo.add(cv);

			cal.add(Calendar.DAY_OF_MONTH, 1);
		}

		return compVolumeInfo;
	}

	@Override
	public Graph generateGraphObject(GraphQueryCriteria criteria, List<CompanyVolume> compVolumeInfo, int nDaysFromToday) throws Exception {
		// Step 2. Generate company trading information if possible.
		int companyId = criteria.getCompanyId();
		List<CompanyTradingRange> companyTradingInfo = null;
		if (companyId > 0) {
			companyTradingInfo = getCompanyTradingInfoFromIndex(companyId);
		} else {
			LOG.debug("Not querying for company trading information as {GraphQueryCriteria : companyId} not provided.");
		}
		Map<Integer, CompanyTradingRange> companyTradingMap = getCompanyTradingMap(companyTradingInfo);

		// Step 3. Generate graph object with above information generated.
		Graph graphObj = generateHistoricalStats(compVolumeInfo, companyTradingMap, criteria.getNumberOfDays(), nDaysFromToday);

		// Step 4. Set appropriate range in graph object.
		switch (criteria.getScope()) {
		case SearchSpec.SCOPE_BROAD:
			graphObj.setCurrentRange(Range.BROAD);
			break;

		case SearchSpec.SCOPE_MEDIUM:
			graphObj.setCurrentRange(Range.MEDIUM);
			break;

		case SearchSpec.SCOPE_NARROW:
		default:
			graphObj.setCurrentRange(Range.NARROW);
			break;
		}
		DateRange dateRange = criteria.getDateRange();
		if (dateRange != null) {
			graphObj.setSDate(dateRange.getStart());
			graphObj.setEDate(dateRange.getEnd());
		}
		return graphObj;
	}

	private Map<Integer, CompanyTradingRange> getCompanyTradingMap(List<CompanyTradingRange> companyTradingInfo) {
		Map<Integer, CompanyTradingRange> map = new HashMap<Integer, CompanyTradingRange>();
		if (companyTradingInfo == null || companyTradingInfo.isEmpty()) {
			return map;
		}
		for (CompanyTradingRange range : companyTradingInfo) {
			map.put(range.getDiffId(), range);
		}
		return map;
	}

	private Graph generateHistoricalStats(List<CompanyVolume> companyVolList, Map<Integer, CompanyTradingRange> companyTradingMap, int days,
			int nDaysFromToday) {
		// construct volume map
		Map<Integer, CompanyVolume> volumeMap = new HashMap<Integer, CompanyVolume>();
		if (companyVolList != null) {
			for (CompanyVolume volume : companyVolList) {
				volumeMap.put(volume.getDiffId(), volume);
			}
		}

		SimpleDateFormat df = new SimpleDateFormat(DateRange.DEFAULT_DATE_FORMAT);
		df.setTimeZone(SolrSearcher.getTimeZone());
		Date initDate = null;
		try {
			initDate = df.parse(FRAPIConstant.INITIAL_DATE);
		} catch (ParseException e) {
			// Drop this exception
		}
		DateRange startDr;
		DateRange endDr;
		Calendar cal = Calendar.getInstance(SolrSearcher.getTimeZone());
		if (nDaysFromToday <= Parameter.DEFAULT_N_DAY_FROM_TODAY) {
			// Get the ending date range
			endDr = new DateRange(initDate, cal.getTime(), "");
		} else {
			cal.add(Calendar.DATE, -nDaysFromToday + 1);
			// Get the ending date range
			endDr = new DateRange(initDate, cal.getTime(), "");
		}
		// Get the starting date range
		cal.add(Calendar.DAY_OF_YEAR, -days + 1);
		startDr = new DateRange(initDate, cal.getTime(), "");
		Calendar cal1 = Calendar.getInstance();
		int firstDiffId = startDr.getDays();
		cal1.setTime(TimeUtils.getTimestampForDay(firstDiffId));
		int lastDiffId = endDr.getDays();

		boolean hasTradeRanges = companyTradingMap != null && companyTradingMap.size() > 2;
		Graph stats = new Graph();
		ArrayList<HistoricalStat> historicalStatList = new ArrayList<HistoricalStat>();
		for (int diffId = firstDiffId; diffId <= lastDiffId; diffId++) {
			int i = 0;
			CompanyVolume cv = volumeMap.get(diffId);
			HistoricalStat stat = new HistoricalStat();
			if (cv == null) {
				cv = new CompanyVolume();
				cv.setDiffId(diffId);
				cv.setTotal(0);

				cal.setTime(initDate);
				cal.add(Calendar.DAY_OF_MONTH, diffId);
				cv.setDate(new Timestamp(cal.getTimeInMillis()));
			}
			stat.setCompanyVolume(cv);
			if (diffId == firstDiffId) {
				while (companyTradingMap.get(diffId - i) == null && i < 7) {
					i++;
				}
			}
			if (hasTradeRanges) {
				stat.setTradeRange(companyTradingMap.get(diffId - i));
			}
			historicalStatList.add(stat);

		}
		stats.setHistoricalStat(historicalStatList);
		return stats;
	}

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public List<CompanyTradingRange> getCompanyTradingInfoFromIndex(int companyId) throws SolrServerException {
		List<CompanyTradingRange> list = null;
		String query = "attrCompanyId:" + companyId;
		LOG.debug("Query for getLastClosingPriceFromIndex: " + query);
		String[] fields = new String[] {"openingPrice", "closingPrice", "dayId"};
		SolrDocumentList docList =
				SolrServerReader.retrieveNSolrDocs(entityBaseServiceRepository.getEntitySolrServer(), query, 0, 1, fields);
		if (docList != null && !docList.isEmpty()) {
			list = new ArrayList<CompanyTradingRange>();
			for (SolrDocument doc : docList) {
				List<Integer> dayIds = (List<Integer>) ((Collection) doc.getFieldValues("dayId"));
				List<Double> closingPrices = (List<Double>) ((Collection) doc.getFieldValues("closingPrice"));
				List<Double> openingPrices = (List<Double>) ((Collection) doc.getFieldValues("openingPrice"));
				if (openingPrices == null || openingPrices.isEmpty()) {
					return null;
				}
				for (int i = 0; i < openingPrices.size(); i++) {
					CompanyTradingRange range = new CompanyTradingRange();
					range.setDiffId(dayIds.get(i).intValue());
					range.setClosingPrice(closingPrices.get(i).doubleValue());
					range.setOpeningPrice(openingPrices.get(i).doubleValue());
					list.add(range);
				}
			}
		}
		return list;
	}

	@Override
	public EventSet getEventsTimeline(int[] companyIdsArrParam, int[] tCatIds, BaseSpec spec) throws Exception {
		int[] companyIdsArr = companyIdsArrParam;
		if ((companyIdsArr == null || companyIdsArr.length == 0) && (tCatIds == null || tCatIds.length == 0)) {
			return null;
		}
		EventSet eventsTimeline = new EventSet();
		try {
			int tokensCount = 0;
			if (companyIdsArr != null && companyIdsArr.length != 0) {
				companyIdsArr = Arrays.copyOfRange(companyIdsArr, 0, Math.min(companyIdsArr.length, FRAPIConstant.SOLR_CHUNK));
				tokensCount = companyIdsArr.length;
				if (LOG.isDebugEnabled()) {
					LOG.debug("Total no of companies takes part for events timeline " + companyIdsArr.length);
				}
			}
			if (tokensCount < FRAPIConstant.SOLR_CHUNK && tCatIds != null) {
				tCatIds = Arrays.copyOfRange(tCatIds, 0, Math.min(FRAPIConstant.SOLR_CHUNK - tokensCount, tCatIds.length));
				if (LOG.isDebugEnabled()) {
					LOG.debug("Total no of topics takes part for events timeline " + tCatIds.length);
				}
				tokensCount += tCatIds.length;
			}
			EventTypeRange range = EventTypeRange.getDefaultEventTypeRange();
			EventQueryCriteria criteria = new EventQueryCriteria(companyIdsArr, tCatIds, range, spec.getDaysCount(), false);
			criteria.setNoOfEvents(FRAPIConstant.MAX_NO_OF_EVENTS);
			criteria.setStart(spec.getStart());
			// List<Integer> eventGroup = spec.getEventGroup();
			// if(eventGroup != null) {
			// int[] eventTypeIds = getEventTypeIdsFromEventGroup(eventGroup);
			// criteria.setEventTypeIds(eventTypeIds);
			// }

			// Fetch events from solr on the given criteria
			SolrDocumentList events = eventService.getEventsDocsFromSolr(criteria);
			Map<Integer, SolrDocument> eventDocMap = createEventDocMap(spec); 
			
			List<IEvents> eventList = eventService.getEntityEventsFromSolr(events, eventDocMap);
			LOG.debug("Event list size before basic clean up " + eventList.size());

			if (LOG.isDebugEnabled()) {
				LOG.debug("***********Total participated events for monitor events calculations***********");
				debugEvents(eventList);
			}
			// apply filters on this list.
			eventList = eventService.applyBC(eventList, tokensCount > 1, FRAPIConstant.MAX_NO_OF_EVENTS);
			if (eventList == null || eventList.isEmpty()) {
				return null;
			}

			if (LOG.isDebugEnabled()) {
				LOG.debug("***********Total participated events for monitor events calculations after basic cleanup***********");
				debugEvents(eventList);
			}


			/* Change for providing Navigation. */
			int totalCount = eventList.size();
			if (totalCount <= spec.getStart()) {
				return eventsTimeline;
			}
			if (totalCount >= (spec.getStart() + spec.getCount())) {
				int toIndex = spec.getStart() + spec.getCount();
				eventList = eventList.subList(spec.getStart(), toIndex);
			} else {
				eventList = eventList.subList(spec.getStart(), Math.min(eventList.size(), spec.getCount()));
			}

			if (spec.getScope() == null) {
				spec.setScope(SearchSpec.SCOPE_NARROW);
			}
			boolean needFurtherFiltering = eventList.size() > spec.getCount() && spec.getScope() == SearchSpec.SCOPE_NARROW;
			if (!needFurtherFiltering) {
				spec.setScope(SearchSpec.SCOPE_BROAD);
			} else {
				eventList = this.eventService.applyBSA(eventList, spec.getCount(), tokensCount > 1);
			}

			if (LOG.isDebugEnabled()) {
				LOG.debug(
						"***********Total participated events for monitor events calculations after basic selection algorithm***********");
				debugEvents(eventList);
			}

			LOG.debug("Event list size after applying BSA " + eventList.size());

			// re-sort the list because BSA is not ensuring ordering on report date.
			Collections.sort(eventList, new Comparator<IEvents>() {
				@Override
				public int compare(IEvents o1, IEvents o2) {
					return -o1.getDate().compareTo(o2.getDate());
				}
			});

			boolean isSingleSearch = ((companyIdsArr != null && companyIdsArr.length > 1) || (tCatIds != null && tCatIds.length > 1)
					|| (companyIdsArr != null && tCatIds != null && (companyIdsArr.length + tCatIds.length > 1)));
			boolean isIpad = (spec.getIpad() == null ? false : spec.getIpad().booleanValue());

			List<Event> pojoEventList = convertUtil.convertToBaseType(eventList, isIpad, isSingleSearch);
			// attaching related document with event
			if (spec.isNeedRelatedDoc()) {
				attachRelatedDocumentDetails(pojoEventList, eventDocMap, spec);
			}

			eventsTimeline.setEvents(pojoEventList);
			eventsTimeline.setTotalCount((int) events.getNumFound());
			eventsTimeline.setScope(spec.getScope());
		} catch (Exception e) {
			LOG.error("Error while fetching event for timeline " + e.getMessage(), e);
			throw e;
		}
		return eventsTimeline;
	}

	private void debugEvents(final List<IEvents> eventList) {
		for (IEvents event : eventList) {
			LOG.debug(
					"eventId:" + event.getEventId() + " eventDate: " + event.getDate() + " event.getType:" + event.getEventType());
		}
	}
 
	private Map<Integer, SolrDocument> createEventDocMap(final BaseSpec spec) { 
		Map<Integer, SolrDocument> eventDocMap = null; 
		if (spec.isNeedRelatedDoc()) { 
			eventDocMap = new HashMap<Integer, SolrDocument>(); 
		} 
		return eventDocMap; 
	} 
	

	@Override
	public GraphNodeSet getAccelerometerNode(String csCatIds, boolean singleEntity, boolean isIpad) throws Exception {
		GraphNodeSet graphNodeSet = null;
		List<Accelerometer> accObjList = accelerometerServiceRepository.getAccelerometer(csCatIds);
		if (accObjList != null) {
			graphNodeSet = new GraphNodeSet();
			List<GraphNode> graphNodeList = new ArrayList<GraphNode>();
			for (Accelerometer acc : accObjList) {
				GraphNode node = getNodes(acc, singleEntity, isIpad);
				if (node != null) {
					IEntityInfo entityInfo = entityBaseServiceRepository.getEntityInfoCache().catIdToEntity(node.getId());
					if (entityInfo != null) {
						node.setEntityInfo(convertUtil.convertEntityInfo(entityInfo));
					}
					graphNodeList.add(node);
				}
			}
			if (graphNodeList != null && !graphNodeList.isEmpty()) {
				trimAndSortGraphDataList(graphNodeList, singleEntity);
				graphNodeSet.setGraphNodeList(graphNodeList);
			}
		}
		return graphNodeSet;
	}

	private GraphNode getNodes(Accelerometer accObj, boolean singleEntity, boolean ipad) {
		String catId = accObj.getEntityId();
		IEntityInfoCache entityInfoCache = entityBaseServiceRepository.getEntityInfoCache();
		IEntityInfo entity = entityInfoCache.catIdToEntity(accObj.getEntityId());
		if (entity == null) {
			LOG.debug("ACC_METER ignore no Entity for entity: " + catId);
			return null;
		}

		if (!singleEntity) {
			if (accObj.getThreeDayCount() < 4) {
				LOG.debug("ACC_METER ignore RecentWindow grouped tweet count (" + accObj.getThreeDayCount() + ")< 4 for entity: "
						+ accObj.getEntityId());
				return null;
			}
			int scoreTh = 75;
			if (accObj.getScore() <= scoreTh) {
				LOG.debug("ACC_METER ignore content scores (" + accObj.getScore() + ") must be > " + scoreTh + ", for entity: "
						+ accObj.getEntityId());
				return null;
			}
		}

		String label = entity.getName();
		if (entity.getType() == SearchTokenEntry.SEARCH_TOKEN_TAG_COMPANY) {

			label = trimCompanyEndingWord(label);
		}
		GraphNode node = new GraphNode(entity.getId(), label, accObj.getScore(), accObj.getArrow());
		node.setImageName(accObj.getImageName());
		if (ipad) {
			String[] text = TitleUtils.getSmartTextAccmeteriPad(accObj.getArrow(), accObj.getScore());
			node.setSmartText0(text[0]);
			node.setSmartText(text[1]);
		} else {
			node.setSmartText(TitleUtils.getSmartTextAccmeter(accObj.getArrow(), accObj.getScore()));
		}
		node.setSearchToken(entity.getSearchToken());
		node.setName(entity.getName());
		return node;
	}

	@Override
	public String trimCompanyEndingWord(String name) {
		String trimmedName = null;
		if (name != null && !name.isEmpty()) {
			for (String regex : excelProcesingHelperRepository.getCompanyEndingWordsRegex()) {
				String temp = name.replaceAll(regex, "");
				if (!temp.equals(name)) {
					if (trimmedName == null || trimmedName.length() > temp.length()) {
						trimmedName = temp;
					}
				}
			}
			if (trimmedName != null) {
				return trimmedName.replaceAll(st_THE_Regex, "");
			} else {
				return name.replaceAll(st_THE_Regex, "");
			}
		}
		return name;
	}

	private void trimAndSortGraphDataList(List<GraphNode> nodes, boolean singleEntity) {
		if (!nodes.isEmpty()) {
			if (!singleEntity) { // multi entity
				Collections.sort(nodes, new Comparator<GraphNode>() {
					@Override
					public int compare(GraphNode o1, GraphNode o2) {
						return -Float.valueOf(o1.getValue()).compareTo(o2.getValue());
					}
				});

				if (nodes.size() > MAX_ACCELEROMETER_COUNT) {
					for (int i = nodes.size() - 1; i >= MAX_ACCELEROMETER_COUNT; i--) {
						nodes.remove(i);
					}
				}

				if (nodes.size() % 2 != 0) { // remove odd one, we need count as 2,4,6
					nodes.remove(nodes.size() - 1);
				}
			}
		}
	}

	@Override
	public List<Entity> autoSuggestForEntity(String tpEntityName, String entityType, int count, String dimensionCSV,
			Map<Integer, Integer> industryClassfMap) throws Exception {
		List<Entity> entityList = new ArrayList<Entity>();

		AutoSuggest autoSuggest = autoSuggestService.getAutoCompleteEntries(tpEntityName, entityType, false, count, dimensionCSV);
		/*
		 * if (autoSuggest == null || autoSuggest.getAutoSuggest() == null || autoSuggest.getAutoSuggest().isEmpty()) { LOG.error(
		 * "No auto suggested company found for entity name::" + tpEntityName); throw new Exception(
		 * "No auto suggested company found for entity name::" + tpEntityName); }
		 */
		if (autoSuggest != null) {
			for (AutoSuggestInfo autoSuggestInfo : autoSuggest.getAutoSuggest()) {
				IEntityInfo entityInfo = null;
				if (entityType.equalsIgnoreCase(INPUT_ENTITY_TYPE.COMPANY.name())) {
					int frcompanyId = autoSuggestInfo.getCompanyId();
					entityInfo = entityBaseServiceRepository.getEntityInfoCache().companyIdToEntity(frcompanyId);
				} else {
					entityInfo = entityBaseServiceRepository.getEntityInfoCache().catIdToEntity(autoSuggestInfo.getCatId());
				}
				if (entityInfo != null) {

					Entity ent = convertUtil.convertEntityInfo(entityInfo);

					if (entityType.equalsIgnoreCase(INPUT_ENTITY_TYPE.COMPANY.name())) {
						if( autoSuggestInfo.getSynonym() != null && !autoSuggestInfo.getSynonym().isEmpty()) {
							ent.setSynonym(autoSuggestInfo.getSynonym());
						}
					} else if (entityType.equalsIgnoreCase(INPUT_ENTITY_TYPE.TOPIC.name())) {

						IEntityInfo relEntityInfo = entityBaseServiceRepository.getEntityInfoCache()
								.catIdToEntity(Integer.toString(entityInfo.getSectorCatId()));
						if (relEntityInfo != null) {
							Entity sector = new Entity();
							sector.setSearchToken(relEntityInfo.getSearchToken());
							sector.setName(relEntityInfo.getName());
							ent.setSector(sector);
						}
						relEntityInfo = entityBaseServiceRepository.getEntityInfoCache()
								.catIdToEntity(Integer.toString(entityInfo.getSegmentCatId()));
						if (relEntityInfo != null) {
							Entity segment = new Entity();
							segment.setSearchToken(relEntityInfo.getSearchToken());
							segment.setName(relEntityInfo.getName());
							ent.setSegment(segment);
						}
						relEntityInfo = entityBaseServiceRepository.getEntityInfoCache()
								.catIdToEntity(Integer.toString(entityInfo.getIndustryCatId()));
						if (relEntityInfo != null) {
							Entity industry = new Entity();
							industry.setSearchToken(relEntityInfo.getSearchToken());
							industry.setName(relEntityInfo.getName());
							ent.setIndustry(industry);
						}
					} else if (entityType.equalsIgnoreCase(INPUT_ENTITY_TYPE.INDUSTRY.name())) {
						Integer industryClassificationId = industryClassfMap.get(Integer.parseInt(autoSuggestInfo.getCatId()));
						// show only FR industries
						if ((industryClassificationId != null && industryClassificationId > 1)) {
							continue;
						}
					}
					entityList.add(ent);
				} else {
					if (entityType.equalsIgnoreCase(INPUT_ENTITY_TYPE.COMPANY.name())) {
						LOG.debug("No Entity InfoCache for company Id = " + autoSuggestInfo.getCompanyId());
					} else {
						LOG.debug("No Entity InfoCache for Cat Id = " + autoSuggestInfo.getCatId());
					}
				}
			}
		} else {
			LOG.debug("No Company found after AutoSuggest");
		}
		return entityList;
	}



	@Override
	public void attachRelatedDocumentDetails(List<Event> pojoEventList, Map<Integer, SolrDocument> eventDocMap, BaseSpec spec)
			throws Exception {
		try {
			if (eventDocMap != null && !eventDocMap.isEmpty()) {
				EntityDetailSpec spec1 = new EntityDetailSpec();
				spec1.ipad = false;
				spec1.twitter = false;
				spec1.needPhrase = spec.isNeedPhrase();

				short industryClassificationId = -1;
				if (spec.getIndustryClassificationId() != null) {
					industryClassificationId = spec.getIndustryClassificationId();
				}
				for (Event event : pojoEventList) {
					SolrDocument sd = eventDocMap.get(Integer.parseInt(event.getEventId()));
					int eventType = event.getEventTypeId();
					if (eventType >= 1 && eventType <= 299) {
						try {
							setRelatedMtDocs(spec1, event, sd, industryClassificationId);
						} catch (Exception e) {
							LOG.error("Error while fetching related document", e);
						}
					} else if (eventType >= 350 && eventType <= 399) {
						List<Long> ids = new ArrayList<Long>();
						Long siteDocId = retrieveSiteDocId(sd); 
						
						if (siteDocId != null) {
							ids.add(siteDocId);
						}
						Collection<String> toExcludeIds = new HashSet<String>();
						toExcludeIds.add(event.getEntityInfo().getId());
						if (!ids.isEmpty()) {
							DocumentSet docSet = getDocDetails(ids, spec1, toExcludeIds, industryClassificationId);
							if (docSet == null) {
								docSet = new DocumentSet();
								List<Document> docList = new ArrayList<Document>();
								Document doc = new Document();
								doc.setUrl(event.getUrl());
								doc.setTitle(event.getTitle());
								doc.setDate(event.getDate());
								doc.setContentType(ContentType.WEBNEWS);
								docList.add(doc);
								docSet.setDocuments(docList);

							}
							WebVolumeEventMeta wvMeta = event.getWvMeta();
							if (wvMeta == null) {
								wvMeta = new WebVolumeEventMeta();
							}
							/*
							 * for(Document doc : docSet.getDocuments()) { if(doc.getGroupId() != null) {
							 * doc.setDupCount(getGroupDocsCountForGroupId(doc.getGroupId())); } }
							 */
							wvMeta.setRelatedDocument(docSet);
							event.setWvMeta(wvMeta);
						}
					} /*
					 * else if (eventType >= 300 && eventType <= 349) { try { setRelatedStockPriceEvent(event, spec1); } catch
					 * (Exception e) { LOG.error( "Error while fetching related stock price event", e); } } else if (eventType >= 400 &&
					 * eventType <= 500) { try { EventSet relatedEventSet = new EventSet(); EventTypeRange eventTypeRange = new
					 * EventTypeRange(450, 499); Map<String, List<Event>> highlightBuckets = new LinkedHashMap<String, List<Event>>();
					 * List<Event> relatedEvent = getRelatedSecEvent(event, spec1, eventTypeRange, new Date()); if (relatedEvent !=
					 * null) { highlightBuckets.put(ANNUAL_QUARTERLY_FILINGS, relatedEvent); } eventTypeRange = new EventTypeRange(400,
					 * 449); relatedEvent = getRelatedSecEvent(event, spec1, eventTypeRange, new Date()); if (relatedEvent != null) {
					 * highlightBuckets.put(_8K_FILINGS, relatedEvent); } if (!highlightBuckets.isEmpty()) {
					 * relatedEventSet.setEventBuckets(highlightBuckets); } if (!relatedEventSet.getEventBuckets().isEmpty()) {
					 * SecEventMeta secMeta = event.getSecMeta(); if (secMeta == null) { secMeta = new SecEventMeta(); }
					 * secMeta.setRelatedEvent(relatedEventSet); event.setSecMeta(secMeta); } } catch (Exception e) { LOG.error(
					 * "Error while fetching related sec event", e); }
					 * 
					 * }
					 */
				}
			}
		} catch (Exception e) {
			LOG.error("Error attaching related documents with events ", e);
			throw e;
		}
	}

	/*
	 * private void setRelatedStockPriceEvent(Event event, EntityDetailSpec spec) throws Exception { try { int days = 30 + 30; // 30 days
	 * prior and 30 days post Calendar cal = Calendar.getInstance(); cal.setTime(event.getDate()); cal.add(Calendar.DATE, 30); Date lastDay
	 * = cal.getTime(); days = calculateDaysForEvent(lastDay, days); EventQueryCriteria queryCriteria = new EventQueryCriteria(new int[] {
	 * event.getEntityInfo().getCompanyId() }, null, lastDay, days); List<IEvents> iEventList =
	 * eventService.getEventsFromSolr(queryCriteria, null); if (iEventList != null && !iEventList.isEmpty()) { iEventList =
	 * eventService.applySingleCompanyEventsFilter(iEventList, (spec.highlightCount + 1), false); List<Event> highlights = new
	 * ArrayList<Event>(); for (IEvents iEvent : iEventList) { Event event1 = convertUtil.convertToBaseType(iEvent, spec.ipad,
	 * spec.includeCompany, true); if (event1.getEventId().equals(event.getEventId())) { continue; } highlights.add(event1); } if
	 * (!highlights.isEmpty() && highlights.size() > spec.highlightCount) { highlights.subList(spec.highlightCount,
	 * highlights.size()).clear(); } BucketSpec bSpec = new BucketSpec(); bSpec.dateFieldOrMethodName = "date"; bSpec.bucketSizeThreshold =
	 * 1; Map<String, List<Event>> realtedEvent = dateBucketUtils.getListGroupByDate(highlights, bSpec); if (realtedEvent != null &&
	 * !realtedEvent.isEmpty()) { StockPriceMeta stockPriceMeta = event.getStockPriceMeta(); if (stockPriceMeta == null) { stockPriceMeta =
	 * new StockPriceMeta(); } EventSet relatedEventSet = new EventSet(); relatedEventSet.setEventBuckets(realtedEvent);
	 * stockPriceMeta.setRelatedEvent(relatedEventSet); event.setStockPriceMeta(stockPriceMeta); } } } catch (Exception e) { LOG.error(
	 * "Error getting related stock price events for " + event.getEventId(), e); throw e; } }
	 */

	private void setRelatedMtDocs(EntityDetailSpec spec, Event event, SolrDocument sd, short industryClassificationId) throws Exception {
		try {
			Long siteDocId = retrieveSiteDocId(sd); 
			
			if (siteDocId != null) {
				LOG.debug("related sitedDocIds:::" + siteDocId);
				SolrQuery solrQuery = new SolrQuery();
				String query = "url:\"" + event.getUrl() + "\"";
				solrQuery.setQuery(query);
				solrQuery.addField("id");
				solrQuery.addField("groupId");
				solrQuery.setStart(0);
				solrQuery.setRows(1);
				solrQuery.setQueryType("standard");
				SolrDocumentList sdl = entityBaseServiceRepository.getSearcher().fetchDocs(solrQuery);
				if (sdl != null && !sdl.isEmpty()) {
					Long sDocId = (Long) sdl.get(0).getFieldValue("id");
					if (sDocId != null) {
						List<Long> siteDocIds = new ArrayList<Long>(1);
						siteDocIds.add(sDocId);
						EntityDetailSpec spec1 = new EntityDetailSpec();
						spec1.ipad = spec.ipad;
						spec1.twitter = false;
						spec1.needPhrase = spec.needPhrase;

						DocumentSet documentDetails = getDocDetails(siteDocIds, spec1, null, industryClassificationId);
						if (documentDetails != null && !documentDetails.getDocuments().isEmpty()) {
							MgmtTurnoverMeta mtMeta = event.getMtMeta();
							if (mtMeta == null) {
								mtMeta = new MgmtTurnoverMeta();
							}
							/*
							 * for(Document doc : documentDetails.getDocuments()) {
							 * doc.setDupCount(getGroupDocsCountForGroupId(doc.getGroupId())); }
							 */
							mtMeta.setRelatedDocument(documentDetails);
							event.setMtMeta(mtMeta);

						}
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Error getting related docs for Mt event Id " + event.getEventId(), e);
			// throw e;
		}
	}
 
	private Long retrieveSiteDocId(final SolrDocument sd) { 
		Long siteDocId = (Long) sd.getFieldValue("evidenceIdSiteDocActive"); 
		if (siteDocId == null) { 
			siteDocId = (Long) sd.getFieldValue("evidenceIdSiteDocInActive"); 
		} 
		return siteDocId; 
	} 
	

	/*
	 * private static int calculateDaysForEvent(Date lastDay, int days) throws Exception {
	 * 
	 * Calendar current = Calendar.getInstance(); current.set(Calendar.HOUR_OF_DAY, 0); current.set(Calendar.MINUTE, 0);
	 * current.set(Calendar.SECOND, 0); current.set(Calendar.MILLISECOND, 0); current.add(Calendar.DATE,
	 * -EventQueryCriteria.DEFAULT_MAX_EVENTS_DAYS + 1); Date allowedStartDay = current.getTime();
	 * 
	 * Calendar cal = Calendar.getInstance(); cal.setTime(lastDay); cal.add(Calendar.DATE, -days); Date expected = cal.getTime();
	 * 
	 * if (expected.before(allowedStartDay)) { days -= (allowedStartDay.getTime() - expected.getTime()) / (1000 * 60 * 60 * 24); } return
	 * days; }
	 */

	/*
	 * private List<Event> getRelatedSecEvent(Event event, EntityDetailSpec spec, EventTypeRange eventTypeRange,Date lastDay) throws
	 * Exception{
	 * 
	 * // EventTypeRange eventTypeRange = new EventTypeRange(400, 500); int days = 180; EventQueryCriteria queryCriteria = new
	 * EventQueryCriteria(new int[] { event.getEntityInfo().getCompanyId() }, null, lastDay, days, eventTypeRange, true);
	 * queryCriteria.setNoOfEvents(6); List<IEvents> iEventList = eventService.getEventsFromSolr(queryCriteria, null); if (iEventList !=
	 * null && !iEventList.isEmpty()) { List<Event> highlights = new ArrayList<Event>(); for (IEvents iEvent : iEventList) { Event event1 =
	 * convertUtil.convertToBaseType(iEvent, spec.ipad, spec.includeCompany, true); if (event1.getEventId().equals(event.getEventId())) {
	 * continue; } highlights.add(event1); }
	 * 
	 * return highlights; } return null; }
	 */

	public static void main(String[] args) {
		Entity e = new Entity();
		e.setSearchToken("ABC");

		Entity e1 = new Entity();
		e1.setSearchToken("ABC1");

		List<Entity> enLst = new ArrayList<Entity>();
		enLst.add(e);
		enLst.add(e1);

		for (Entity e5 : enLst) {
			System.out.println(e5.getSearchToken() + ", " + e5.getAdded());
		}

		Map<String, Entity> map = new HashMap<String, Entity>();

		for (Entity e2 : enLst) {
			map.put(e2.getSearchToken(), e2);
		}

		Entity e3 = map.get("ABC");
		e3.setAdded(Boolean.TRUE);

		for (Entity e5 : enLst) {
			System.out.println(e5.getSearchToken() + ", " + e5.getAdded());
		}

	}
}
