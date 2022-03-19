package com.firstrain.frapi.service.impl;

import com.firstrain.content.similarity.DocumentSimilarityUtil;
import com.firstrain.frapi.FRCompletionService;
import com.firstrain.frapi.domain.BaseSpec;
import com.firstrain.frapi.domain.BlendDunsInput;
import com.firstrain.frapi.domain.CompanyVolume;
import com.firstrain.frapi.domain.Document;
import com.firstrain.frapi.domain.EntityDetailSpec;
import com.firstrain.frapi.domain.TwitterSpec;
import com.firstrain.frapi.events.EventObj;
import com.firstrain.frapi.events.IEvents;
import com.firstrain.frapi.obj.EventQueryCriteria;
import com.firstrain.frapi.obj.GraphQueryCriteria;
import com.firstrain.frapi.pojo.Entity;
import com.firstrain.frapi.pojo.Event;
import com.firstrain.frapi.pojo.wrapper.DocumentSet;
import com.firstrain.frapi.repository.AccelerometerServiceRepository;
import com.firstrain.frapi.repository.EntityBaseServiceRepository;
import com.firstrain.frapi.repository.ExcelProcessingHelperRepository;
import com.firstrain.frapi.repository.impl.IndustryClassificationMap;
import com.firstrain.frapi.service.AutoSuggestService;
import com.firstrain.frapi.service.EventService;
import com.firstrain.frapi.util.EntityHandler;
import com.firstrain.frapi.util.FRAPIConstant;
import com.firstrain.frapi.util.ServicesAPIUtil;
import com.firstrain.obj.IEntityInfo;
import com.firstrain.obj.IEntityInfoCache;
import com.firstrain.solr.client.DateCount;
import com.firstrain.solr.client.DocCatEntry;
import com.firstrain.solr.client.DocEntriesUpdator;
import com.firstrain.solr.client.DocEntry;
import com.firstrain.solr.client.DocListBucket;
import com.firstrain.solr.client.EntityEntry;
import com.firstrain.solr.client.QuoteEntry;
import com.firstrain.solr.client.SearchResult;
import com.firstrain.solr.client.SearchSpec;
import com.firstrain.solr.client.SearchTokenEntry;
import com.firstrain.solr.client.SolrSearcher;
import com.firstrain.solr.client.util.SolrServerReader;
import com.firstrain.utils.TitleUtils;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.NamedList;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.rules.RuleChain;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.firstrain.frapi.events.IEvents.EventTypeEnum.TYPE_TOTAL_WEB_COVERAGE_VOLUME_TOPIC;
import static com.firstrain.frapi.util.ContentType.WEBNEWS;
import static com.firstrain.frapi.util.FRAPIConstant.HIGHLIGHTS_SEARCH_DAYS;
import static com.firstrain.solr.client.SearchSpec.ORDER_DATE;
import static com.firstrain.solr.client.SearchSpec.SCOPE_BROAD;
import static com.firstrain.solr.client.SearchSpec.SCOPE_MEDIUM;
import static com.firstrain.solr.client.SearchSpec.SCOPE_NARROW;
import static com.firstrain.solr.client.SearchTokenEntry.Relation.DEFAULT;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.powermock.reflect.Whitebox.invokeMethod;

@PrepareForTest({
        Calendar.class,
        DocEntriesUpdator.class,
        EntityBaseServiceImpl.class,
        EntityHandler.class,
        FRCompletionService.class,
        SolrServerReader.class,
        TitleUtils.class,
        TwitterServiceImpl.class,
        SolrSearcher.class,
        QueryRequest.class,
        QueryResponse.class
})
public class EntityBaseServiceImplTestSetup {
    static final String UNABLE_TO_SUMBMIT_CALLABLE = "Unable to sumbmit Callable";
    static final String COMPANY_STR = "company";
    static final int INTEGER_VALUE_SEVENTY_SIX = 76;
    static final int INTEGER_VALUE_FIVE = 5;
    static final String FQUERY_STR = "fquery";
    static final String QUERY_STR = "query";
    static final String ENTITY_ID = "301024";
    static final String TEXT_STR = "text_str";
    static final String PERSON_STR = "person_str";
    static final String EXECUTED_IE_2 = "Executed_ie_2";
    static final String EXECUTED_ID_1 = "Executed_id_1";
    static final long LONG_ID_1 = 123L;
    static final long LONG_ID_2 = 124L;
    static final short CLASSIFICATION_ID = 1;
    static final short QUOTE_RELEVANCE = 1;
    static final short SHORT_ONE = 1;
    static final short SHORT_ZERO = 0;
    static final String TEST_TWEET_TITLE = "Test tweet title";
    static final String TEST_EXCLUDE_ARTICLE_IDS_SSV = "test-exclude-article-ids-ssv";
    static final short TEST_INDUSTRY_CLASSIFICATION_ID = 10;
    static final String TEST_EXCLUDE_SOURCE_IDS_SSV = "test-exclude-source-ids-ssv";
    static final String TEST_SEARCH_TOKEN = "test-search-token";
    static final String TEST_IMAGE_NAME = "test-image-name";
    static final String TEST_ENTITY_LABEL = "test-entity-label";
    static final String TEST_EVENT_TITLE = "Test Event Title";
    static final String TEST_URL = "/test-url";
    static final String TEST_UBER_TOPIC = "test-uber-topic";
    static final String TEST_BUSINESS_TOPIC = "test-business-topic";
    static final String TEST_REGION = "test-region";
    static final String TEST_BUSINESS_LINE = "test-business-line";
    static final String SCOPE_DIRECTIVES = "1|2|3|4";
    static final Date CURRENT_DATE = new Date();
    static final String SITEDOC_ID = "sitedocId";
    static final String BUCKET_NAME = "bucket_name";
    static final long CAT_ID = 123L;
    static final int INTEGER_VALUE_TWO = 2;
    static final int INTEGER_VALUE_ONE = 1;
    static final String CAT_ID_STR = "301024";
    static final int INT_ID = 301024;
    static final String TEXT_STR_2 = "text-str-2";
    static final String TEST_INCLUDE_SOURCE_IDS_SSV = "test-include-source-ids-ssv";
    static final String TEST_EXCEPTION_MESSAGE = "Test exception message";
    static final String TEST_SCREEN_NAME = "test-screen-name";
    int[] companyIdsArr;
    int[] tCatIds;
    int[] eventTypes;

    enum YesNoEnum {
        YES,
        NO
    }

    final EntityDetailSpec spec = new EntityDetailSpec();
    final TwitterSpec twitterSpec = new TwitterSpec();
    final SearchResult searchResult = new SearchResult();
    final ErrorCollector errorCollector;
    final ExpectedException expectedException;

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector = new ErrorCollector())
            .around(expectedException = ExpectedException.none());

    @Mock
    DocumentSimilarityUtil documentSimilarityUtil;
    @Mock
    IEntityInfoCache infoCache;
    @Mock
    IEntityInfo entityInfo;
    @Mock
    DocEntry docEntry;
    @Mock
    EntityEntry entity;
    @Mock
    SolrSearcher searcher;
    @Mock
    EntityBaseServiceRepository entityBaseServiceRepository;
    @Mock
    ExcelProcessingHelperRepository excelProcesingHelperRepository;
    @Mock
    AccelerometerServiceRepository accelerometerServiceRepository;
    @Mock
    IndustryClassificationMap industryClassificationMap;
    @Mock
    EventService eventService;
    @Mock
    ServicesAPIUtil servicesAPIUtil;
    @Mock
    RegionExcelUtilImpl regionExcelUtilImpl;
    @Mock
    QueryResponse qResponse;
    @Mock
    ThreadPoolTaskExecutor taskExecutor;
    @Mock
    ThreadPoolExecutor executor;
    @Mock
    AutoSuggestService autoSuggestService;
    @Mock
    Callable<Object[]> mockCallable;
    @InjectMocks
    EntityBaseServiceImpl serviceImpl;

    SolrServer favIconServer;
    SolrServer docImageServer;
    Entity entityPojo;
    Date testDate;
    Map<Integer, Integer> iVsDunsIndex;
    Map<Integer, SearchResult> responseMap;
    Map<String, Entity> dnbEntityMap;
    List<IEvents> iEventsList;

    @Before
    public void setUp() throws Exception {
        companyIdsArr = new int[] {1, 2, 3};
        tCatIds = new int[] {1, 2, 3};
        eventTypes = new int[] {1, 2, 3};
        mockStatic(SolrServerReader.class);
        mockStatic(SolrSearcher.class);
        when(entityBaseServiceRepository.getSearcher()).thenReturn(searcher);
        searchResult.buckets = prepareBuckets();
        searchResult.dateCount = new DateCount();
        when(searcher.search(any(SearchSpec.class))).thenReturn(searchResult);
        final SolrDocumentList docList = new SolrDocumentList();
        SolrDocument solrDocument = new SolrDocument();
        solrDocument.setField("tweetId", CAT_ID);
        solrDocument.setField("tweetCreationDate", new Date());
        solrDocument.setField("comboScore", INTEGER_VALUE_TWO);
        solrDocument.setField("groupId", CAT_ID);
        solrDocument.setField("scope", "hello World".getBytes()[0]);
        solrDocument.setField("groupSize", INTEGER_VALUE_TWO);
        final List<Integer> companyIds = new ArrayList<Integer>();
        companyIds.add(301024);
        solrDocument.setField("companyId", companyIds);
        solrDocument.setField("topicIdCoreTweet", companyIds);
        solrDocument.setField("dayId", companyIds);
        final List<Double> doubleIds = new ArrayList<Double>();
        doubleIds.add(1.1);
        solrDocument.setField("closingPrice", doubleIds);
        solrDocument.setField("openingPrice", doubleIds);

        docList.add(solrDocument);
        docList.setNumFound(INTEGER_VALUE_TWO);
        when(entityBaseServiceRepository.getEntityInfoCache()).thenReturn(infoCache);
        when(infoCache.catIdToEntity(anyString())).thenReturn(entityInfo);
        when(infoCache.companyIdToEntity(anyInt())).thenReturn(entityInfo);

        when(entityInfo.getTwitterHighCount()).thenReturn(INTEGER_VALUE_TWO);
        when(entityInfo.getType()).thenReturn(SearchTokenEntry.SEARCH_TOKEN_TAG_COMPANY);
        when(entityInfo.getSearchToken()).thenReturn(TEXT_STR);
        when(entityInfo.getId()).thenReturn(ENTITY_ID);
        entityPojo = new Entity();
        entityPojo.setId(ENTITY_ID);

        final FRCompletionService<Object[]> completionService =
                (FRCompletionService<Object[]>) mock(FRCompletionService.class);
        when(completionService.getSubmissions()).thenReturn(1);

        when(completionService.submit(any(mockCallable.getClass())))
                .thenThrow(new IllegalArgumentException(UNABLE_TO_SUMBMIT_CALLABLE));
        when(taskExecutor.getThreadPoolExecutor()).thenReturn(executor);
        whenNew(FRCompletionService.class).withAnyArguments().thenReturn(completionService);
        final QueryRequest qRequest = PowerMockito.mock(QueryRequest.class);
        whenNew(QueryRequest.class).withAnyArguments().thenReturn(qRequest);
        when(qRequest.process(any(SolrServer.class))).thenReturn(qResponse);
        final NamedList<Object> namedListResponse = new NamedList<Object>();
        final NamedList<Object> topNamedList = new NamedList<Object>();
        topNamedList.add("tweetId", CAT_ID);
        final List<NamedList<Object>> topTweets = new ArrayList<NamedList<Object>>();
        topTweets.add(topNamedList);
        topNamedList.add("roundrobin", topTweets);
        namedListResponse.add("topTweets", topNamedList);

        final NamedList<Object> summaryNamedList = new NamedList<Object>();
        namedListResponse.add("summary", summaryNamedList);
        when(qResponse.getResponse()).thenReturn(namedListResponse);

        iEventsList = new ArrayList<>();
        final EventObj eventObj = new EventObj();
        eventObj.setEventType(TYPE_TOTAL_WEB_COVERAGE_VOLUME_TOPIC);
        iEventsList.add(eventObj);
        when(eventService.getEntityEventsFromSolr(any(SolrDocumentList.class), anyMap())).thenReturn(iEventsList);
        when(eventService.applyBC(anyList(), anyBoolean(), anyInt())).thenReturn(iEventsList);
        when(eventService.getEventsDocsFromSolr(any(EventQueryCriteria.class))).thenReturn(docList);

        when(SolrServerReader.retrieveNSolrDocs(any(SolrServer.class), anyString(), anyInt(), anyInt(),
                (String[]) anyVararg())).thenReturn(docList);
        when(SolrServerReader.retrieveSolrDocsInBatches(any(SolrServer.class), anyString(), anyInt(),
                (String[]) anyVararg())).thenReturn(docList);
        when(SolrSearcher.getTimeZone()).thenReturn(TimeZone.getTimeZone("Europe/Berlin"));
    }
    void testGetContentAlgo(int argument, int expected) throws Exception {
        // Act
        final int result = invokeMethod(serviceImpl, "getContentAlgo", argument);
        // Assert
        assertEquals(expected, result);
    }

    void arrangeGetQListByScopeDirectivePrimaryEntityNullCasesCommon() {
        entityPojo.setSearchToken(TEST_SEARCH_TOKEN);
        dnbEntityMap = new HashMap<>(2);
        dnbEntityMap.put(TEST_SEARCH_TOKEN, null);
        dnbEntityMap.put(TEXT_STR, entityPojo);
    }

    void arrangeGetQListByScopeDirectivePrimaryEntityNotNullCasesCommon() {
        entityPojo.setSearchToken(TEST_SEARCH_TOKEN);
        dnbEntityMap = new HashMap<>(3);
        dnbEntityMap.put(TEST_SEARCH_TOKEN, null);
        dnbEntityMap.put(TEXT_STR, entityPojo);
        dnbEntityMap.put(TEXT_STR_2, entityPojo);
    }

    void arrangeGetScopeDirectiveCommon() {
        iVsDunsIndex = new HashMap<>(3);
        iVsDunsIndex.put(INTEGER_VALUE_ONE, INTEGER_VALUE_ONE);
        iVsDunsIndex.put(INTEGER_VALUE_TWO, INTEGER_VALUE_TWO);
        iVsDunsIndex.put(INTEGER_VALUE_FIVE, INTEGER_VALUE_FIVE);
        final DocListBucket docListBucket = new DocListBucket(entity, singletonList(docEntry),
                INTEGER_VALUE_ONE, INTEGER_VALUE_TWO);
        searchResult.buckets = new ArrayList<>(3);
        searchResult.buckets.add(docListBucket);
        searchResult.buckets.add(docListBucket);
        searchResult.buckets.add(docListBucket);
        responseMap = new HashMap<>(3);
        responseMap.put(30, searchResult);
        responseMap.put(60, searchResult);
        responseMap.put(180, searchResult);
    }

    void testGetSearchResultsResultsFoundCase(YesNoEnum includeSourceIdsSSV) throws Exception {
        // Arrange
        final String[] qMulti = prepareQueryArrayOfSize();
        final int[] scopeMulti = prepareScopArrayOfSize();
        final BaseSpec baseSpec = prepareBaseSpec();
        baseSpec.setExcludeArticleIdsSSV(TEST_EXCLUDE_ARTICLE_IDS_SSV);
        baseSpec.setIndustryClassificationId(TEST_INDUSTRY_CLASSIFICATION_ID);
        if (includeSourceIdsSSV == YesNoEnum.YES) {
            baseSpec.setIncludeSourceIdsSSV(TEST_INCLUDE_SOURCE_IDS_SSV);
        } else {
            baseSpec.setExcludeSourceIdsSSV(TEST_EXCLUDE_SOURCE_IDS_SSV);
        }
        // Act
        final SearchResult actual = serviceImpl.getSearchResult(qMulti, scopeMulti, FQUERY_STR, baseSpec,
                INTEGER_VALUE_TWO);
        // Assert
        errorCollector.checkThat(actual, equalTo(searchResult));
        verify(entityBaseServiceRepository).getSearcher();
        final ArgumentCaptor<SearchSpec> searchSpecCaptor = ArgumentCaptor.forClass(SearchSpec.class);
        verify(searcher).search(searchSpecCaptor.capture());
        final SearchSpec searchSpec = searchSpecCaptor.getValue();
        errorCollector.checkThat(searchSpec.needHighlighting, equalTo(false));
        errorCollector.checkThat(searchSpec.needHotListAll, equalTo(false));
        errorCollector.checkThat(searchSpec.needSearchSuggestion, equalTo(false));
        errorCollector.checkThat(searchSpec.useLikelySearchIntention, equalTo(false));
        errorCollector.checkThat(searchSpec.needBodyLength, equalTo(true));
        errorCollector.checkThat(searchSpec.needQuotes, equalTo(true));
        errorCollector.checkThat(searchSpec.getOrder(), equalTo(ORDER_DATE));
        errorCollector.checkThat(searchSpec.getStart(), equalTo(1));
        errorCollector.checkThat(searchSpec.getRows(), equalTo(1));
        errorCollector.checkThat(searchSpec.sectionMulti, equalTo(false));
        errorCollector.checkThat(searchSpec.days, equalTo(INTEGER_VALUE_TWO));
        errorCollector.checkThat(searchSpec.lastDay, equalTo("2014-02-14"));
        errorCollector.checkThat(searchSpec.qMulti, equalTo(qMulti));
        errorCollector.checkThat(searchSpec.scopeMulti, equalTo(scopeMulti));
        errorCollector.checkThat(searchSpec.fq, equalTo(FQUERY_STR));
        errorCollector.checkThat(searchSpec.getExcludeDocIds(), equalTo(TEST_EXCLUDE_ARTICLE_IDS_SSV));
        errorCollector.checkThat(searchSpec.getIndustryClassificationId(), equalTo(TEST_INDUSTRY_CLASSIFICATION_ID));
        if (includeSourceIdsSSV == YesNoEnum.YES) {
            errorCollector.checkThat(searchSpec.getIncludeSourceIds(), equalTo(TEST_INCLUDE_SOURCE_IDS_SSV));
        } else {
            errorCollector.checkThat(searchSpec.getExcludeSourceIds(), equalTo(TEST_EXCLUDE_SOURCE_IDS_SSV));
        }
    }

    List<Event> prepareEvents() {
        final List<Event> pojoEventList = new ArrayList<>(2);
        final Event event = new Event();
        event.setEventId(ENTITY_ID);
        event.setEntityInfo(entityPojo);
        event.setEventTypeId(INTEGER_VALUE_ONE);
        event.setUrl(TEST_URL);
        final Event event1 = new Event();
        event1.setEventId(ENTITY_ID);
        event1.setEventTypeId(350);
        event1.setEntityInfo(entityPojo);
        event1.setTitle(TEST_EVENT_TITLE);
        testDate = new Date();
        event1.setDate(testDate);
        event1.setUrl(TEST_URL);
        pojoEventList.add(event);
        pojoEventList.add(event1);
        return pojoEventList;
    }

    static SolrDocument prepareSolrDocument(Long activeId) {
        final SolrDocument solrDocument = new SolrDocument();
        solrDocument.addField("evidenceIdSiteDocActive", activeId);
        solrDocument.addField("evidenceIdSiteDocInActive", LONG_ID_2);
        solrDocument.addField("id", LONG_ID_2);
        return solrDocument;
    }

    static BaseSpec prepareBaseSpec() {
        final BaseSpec baseSpec = new BaseSpec();
        baseSpec.setCount(SHORT_ONE);
        baseSpec.setScope(SCOPE_MEDIUM);
        baseSpec.setLastDay("2014-02-14T06:04:00:00");
        baseSpec.setStart(SHORT_ONE);
        baseSpec.setNeedImage(true);
        baseSpec.setNeedPagination(false);
        return baseSpec;
    }

    BlendDunsInput prepareBlendDuns() {
        final  BlendDunsInput bdi = new BlendDunsInput();
        bdi.setBlendDUNS(true);
        dnbEntityMap = new HashMap<>();
        dnbEntityMap.put(ENTITY_ID, entityPojo);
        bdi.setDnbEntityMap(dnbEntityMap);
        return bdi;
    }

    static int[] prepareScopArrayOfSize() {
        final int[] scopeArr = new int[FRAPIConstant.SEARCHTOKEN_COUNT];
        for (int i = 0; i < FRAPIConstant.SEARCHTOKEN_COUNT; i++) {
            scopeArr[i] = i;
        }

        return scopeArr;
    }

    static String[] prepareQueryArrayOfSize() {
        final String[] qArr = new String[FRAPIConstant.SEARCHTOKEN_COUNT];
        for (int i = 0; i < FRAPIConstant.SEARCHTOKEN_COUNT; i++) {
            qArr[i] = QUERY_STR + i;
        }

        return qArr;
    }

    List<DocEntry> prepareDocEntries() {
        final List<DocEntry> executedIds = new ArrayList<>();
        final short shorta = 1;

        when(docEntry.getTitle()).thenReturn(BUCKET_NAME);
        when(docEntry.getSummary()).thenReturn(BUCKET_NAME);
        when(docEntry.getSourceEntity()).thenReturn(entity);
        when(docEntry.getInsertTime()).thenReturn(CURRENT_DATE);

        final List<DocCatEntry> docCatEntries = new ArrayList<>();
        docCatEntries.add(new DocCatEntry(entity, shorta, shorta));
        when(docEntry.getCatEntries()).thenReturn(docCatEntries);

        docEntry.matchedOthers = (ArrayList<DocCatEntry>) docCatEntries;
        docEntry.matchedTopics = (ArrayList<DocCatEntry>) docCatEntries;
        docEntry.matchedCompanies = (ArrayList<DocCatEntry>) docCatEntries;
        docEntry.matchedContentTypes = (ArrayList<DocCatEntry>) docCatEntries;
        docEntry.catEntries = (ArrayList<DocCatEntry>) docCatEntries;
        final List<QuoteEntry> value = new ArrayList<>();
        entity.id = ENTITY_ID;
        when(entity.getId()).thenReturn(ENTITY_ID);
        docCatEntries.add(new DocCatEntry(entity, QUOTE_RELEVANCE, QUOTE_RELEVANCE));
        docCatEntries.add(new DocCatEntry(entity, QUOTE_RELEVANCE, QUOTE_RELEVANCE));
        when(docEntry.getOtrQuotes()).thenReturn(value);

        docEntry.sitedocId = SITEDOC_ID;
        docEntry.insertTime = CURRENT_DATE;
        docEntry.bodyLength = INTEGER_VALUE_TWO;
        docEntry.docScore = INTEGER_VALUE_TWO;
        docEntry.title = "";

        searchResult.buckets = prepareBuckets();

        docEntry.otrQuotes = prepareQuotes();

        executedIds.add(docEntry);
        executedIds.add(docEntry);
        executedIds.add(docEntry);
        return executedIds;
    }

    List<DocListBucket> prepareSingleBuckets() {
        final List<DocEntry> entries = new ArrayList<>();
        entries.add(docEntry);
        entries.add(docEntry);
        entries.add(docEntry);
        final List<DocListBucket> buckets = new ArrayList<>();
        buckets.add(new DocListBucket(null, entries, INTEGER_VALUE_TWO, 0));
        return buckets;
    }

    List<DocListBucket> prepareBuckets() {
        final List<DocEntry> entries = new ArrayList<>();
        entries.add(docEntry);
        entries.add(docEntry);
        entries.add(docEntry);
        final List<DocListBucket> buckets = new ArrayList<>();
        buckets.add(new DocListBucket(null, entries, INTEGER_VALUE_TWO, 0));
        buckets.add(new DocListBucket(null, entries, INTEGER_VALUE_TWO, 0));
        return buckets;
    }

    static List<QuoteEntry> prepareQuotes() {
        return Collections
                .singletonList(new QuoteEntry(TEXT_STR, PERSON_STR, QUOTE_RELEVANCE, QuoteEntry.QuoteType.TYPE_NC));
    }

    static List<String> prepareStringIds() {
        List<String> executedIds = new ArrayList<>();
        executedIds.add(EXECUTED_ID_1);
        executedIds.add(EXECUTED_IE_2);
        return executedIds;
    }

    static List<Long> prepareLongIds() {
        List<Long> catIds = new ArrayList<>();
        catIds.add(LONG_ID_1);
        catIds.add(LONG_ID_2);
        return catIds;
    }

    void arrangeFavIconAndDocImageServers() {
        mockStatic(DocEntriesUpdator.class);
        favIconServer = mock(SolrServer.class);
        when(entityBaseServiceRepository.getFavIconServer()).thenReturn(favIconServer);
        docImageServer = mock(SolrServer.class);
        when(entityBaseServiceRepository.getDocImageServer()).thenReturn(docImageServer);
    }

    void actAndAssertGetHighlightsResultsCommon(String[] qArr, int[] scopeArr)
            throws Exception {
        // Act
        final DocumentSet actual = serviceImpl.gethighlightsResults(qArr, scopeArr, 3);
        // Assert
        final ArgumentCaptor<SearchSpec> searchSpecCaptor = ArgumentCaptor.forClass(SearchSpec.class);
        verify(searcher).search(searchSpecCaptor.capture());
        final SearchSpec searchSpec = searchSpecCaptor.getValue();
        errorCollector.checkThat(searchSpec.needHotListAll, equalTo(false));
        errorCollector.checkThat(searchSpec.needSearchSuggestion, equalTo(false));
        errorCollector.checkThat(searchSpec.useLikelySearchIntention, equalTo(false));
        errorCollector.checkThat(searchSpec.getOrder(), equalTo(ORDER_DATE));
        errorCollector.checkThat(searchSpec.getStart(), equalTo(0));
        errorCollector.checkThat(searchSpec.getRows(), equalTo(1));
        errorCollector.checkThat(searchSpec.days, equalTo(HIGHLIGHTS_SEARCH_DAYS));
        errorCollector.checkThat(searchSpec.sectionMulti, equalTo(true));
        errorCollector.checkThat(searchSpec.getRowsHightlight(), equalTo(3));
        errorCollector.checkThat(searchSpec.qMulti, equalTo(qArr));
        errorCollector.checkThat(searchSpec.scopeMulti, equalTo(scopeArr));
        final List<Document> documentList = actual.getDocuments();
        errorCollector.checkThat(documentList.size(), equalTo(3));
        errorCollector.checkThat(documentList.get(0).getContentType(), equalTo(WEBNEWS));
        errorCollector.checkThat(documentList.get(1).getContentType(), equalTo(WEBNEWS));
        errorCollector.checkThat(documentList.get(2).getContentType(), equalTo(WEBNEWS));
        errorCollector.checkThat(actual.getTotalCount(), equalTo(0));
        errorCollector.checkThat(actual.isHasMore(), equalTo(false));
        errorCollector.checkThat(actual.getScope(), equalTo(SCOPE_NARROW));
        errorCollector.checkThat(actual.isFiling(), equalTo(false));
        final ArgumentCaptor<List<DocEntry>> docEntryListCaptor = createDocEntryListCaptor();
        verifyStatic();
        DocEntriesUpdator.attachFavIconNDocImageDetails(eq(favIconServer), eq(docImageServer),
                docEntryListCaptor.capture(), eq(true), eq(false));
        final List<DocEntry> docEntryList = docEntryListCaptor.getValue();
        errorCollector.checkThat(docEntryList.size(), equalTo(3));
        errorCollector.checkThat(docEntryList.get(0), equalTo(docEntry));
        errorCollector.checkThat(docEntryList.get(1), equalTo(docEntry));
        errorCollector.checkThat(docEntryList.get(2), equalTo(docEntry));
        verifyStatic();
        SolrSearcher.setupMatchesForEntries(docEntryList, -1);
    }

    void testGetWebResultsForSearchDocumentsFoundCase(int scope, int expectedScope) throws Exception {
        // Arrange
        serviceImpl = spy(serviceImpl);
        final BlendDunsInput bdi = prepareBlendDuns();
        bdi.setScopeDirective(SCOPE_DIRECTIVES);
        final BaseSpec baseSpec = prepareBaseSpec();
        baseSpec.setNeedMatchedEntities(true);
        final List<DocEntry> filteredDocEntryList = singletonList(docEntry);
        when(servicesAPIUtil.filterSimilarEntries(anyList(), eq(documentSimilarityUtil),
                eq(1), eq(false))).thenReturn(filteredDocEntryList);
        arrangeFavIconAndDocImageServers();
        mockStatic(SolrSearcher.class);
        when(SolrSearcher.getTimeZone()).thenReturn(TimeZone.getTimeZone("UTC"));
        when(entityBaseServiceRepository.getDocumentSimilarityUtil()).thenReturn(documentSimilarityUtil);
        baseSpec.setScope(scope);
        // Act
        final DocumentSet actual = serviceImpl.getWebResultsForSearch(QUERY_STR, FQUERY_STR, baseSpec, bdi);
        // Assert
        errorCollector.checkThat(baseSpec.isSectionMulti(), equalTo(false));
        errorCollector.checkThat(baseSpec.getDaysCount(), equalTo(4));
        errorCollector.checkThat(baseSpec.getCount(), equalTo((short) 3));
        final ArgumentCaptor<List<DocEntry>> docEntryListCaptor = createDocEntryListCaptor();
        verify(servicesAPIUtil).filterSimilarEntries(docEntryListCaptor.capture(),
                eq(documentSimilarityUtil), eq(1), eq(false));
        final List<DocEntry> docEntryList = docEntryListCaptor.getValue();
        errorCollector.checkThat(docEntryList.size(), equalTo(3));
        errorCollector.checkThat(docEntryList.get(0), equalTo(docEntry));
        errorCollector.checkThat(docEntryList.get(1), equalTo(docEntry));
        errorCollector.checkThat(docEntryList.get(2), equalTo(docEntry));
        final ArgumentCaptor<List<Document>> docListCaptor = createDocListCaptor();
        final ArgumentCaptor<List<String>> dunsListCaptor = createStringListCaptor();
        final ArgumentCaptor<Set<String>> uberTopicListCaptor = createStringSetCaptor();
        final ArgumentCaptor<Set<String>> businessBasicListCaptor = createStringSetCaptor();
        final ArgumentCaptor<Set<String>> regionListCaptor = createStringSetCaptor();
        final ArgumentCaptor<Set<String>> businessLineListCaptor = createStringSetCaptor();
        verifyPrivate(serviceImpl).invoke("setSecondaryDunsInDocs", docListCaptor.capture(),
                dunsListCaptor.capture(), uberTopicListCaptor.capture(), businessBasicListCaptor.capture(),
                regionListCaptor.capture(), businessLineListCaptor.capture());
        final List<Document> docList = docListCaptor.getValue();
        errorCollector.checkThat(docList.size(), equalTo(1));
        final List<String> dunsList = dunsListCaptor.getValue();
        errorCollector.checkThat(dunsList.size(), equalTo(1));
        final Set<String> uberTopicList = uberTopicListCaptor.getValue();
        errorCollector.checkThat(uberTopicList.isEmpty(), equalTo(true));
        final Set<String> businessBasicList = businessBasicListCaptor.getValue();
        errorCollector.checkThat(businessBasicList.isEmpty(), equalTo(true));
        final Set<String> regionList = regionListCaptor.getValue();
        errorCollector.checkThat(regionList.isEmpty(), equalTo(true));
        final Set<String> businessLineList = businessLineListCaptor.getValue();
        errorCollector.checkThat(businessLineList.isEmpty(), equalTo(true));
        errorCollector.checkThat(actual.getDocuments(), equalTo(docList));
        errorCollector.checkThat(actual.getTotalCount(), equalTo(0));
        errorCollector.checkThat(actual.isHasMore(), equalTo(false));
        errorCollector.checkThat(actual.getScope(), equalTo(expectedScope));
        errorCollector.checkThat(actual.isFiling(), equalTo(false));
        verifyStatic();
        DocEntriesUpdator.attachFavIconNDocImageDetails(favIconServer, docImageServer,
                filteredDocEntryList, true, false);
        verifyStatic();
        SolrSearcher.setupMatchesForEntries(filteredDocEntryList, -1);
        verify(documentSimilarityUtil).clear();
    }

    void verifyGetWebVolumeGraphGenerateGraphObject(int expectedCatgId) throws Exception {
        final ArgumentCaptor<GraphQueryCriteria> queryCriteriaCaptor =
                ArgumentCaptor.forClass(GraphQueryCriteria.class);
        final ArgumentCaptor<List<CompanyVolume>> companyVolInfoCaptor = createCompanyVolListCaptor();
        verify(serviceImpl).generateGraphObject(queryCriteriaCaptor.capture(),
                companyVolInfoCaptor.capture(), eq(0));
        final GraphQueryCriteria queryCriteria = queryCriteriaCaptor.getValue();
        errorCollector.checkThat(queryCriteria.getScope(), equalTo(SCOPE_BROAD));
        errorCollector.checkThat(companyVolInfoCaptor.getValue().isEmpty(), equalTo(true));
        final List<SearchTokenEntry> searchTokenEntryList = queryCriteria.getSearchTokens();
        errorCollector.checkThat(searchTokenEntryList.size(), equalTo(1));
        final SearchTokenEntry searchTokenEntry = searchTokenEntryList.get(0);
        errorCollector.checkThat(searchTokenEntry.getSearchToken(), equalTo(TEXT_STR));
        errorCollector.checkThat(searchTokenEntry.relation, equalTo(DEFAULT));
        errorCollector.checkThat(queryCriteria.getCompanyId(), equalTo(0));
        errorCollector.checkThat(queryCriteria.getNumberOfDays(), equalTo(10));
        if (expectedCatgId != -1) {
            errorCollector.checkThat(queryCriteria.getCategoryId(), equalTo(301024));
        }
    }

    public static int getDifferenceDays(Date first, Date last) {
        long diff = last.getTime() - first.getTime();
        return (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    static ArgumentCaptor<List<DocEntry>> createDocEntryListCaptor() {
        return ArgumentCaptor.forClass((Class) List.class);
    }

    static ArgumentCaptor<List<Document>> createDocListCaptor() {
        return ArgumentCaptor.forClass((Class) List.class);
    }

    static ArgumentCaptor<List<String>> createStringListCaptor() {
        return ArgumentCaptor.forClass((Class) List.class);
    }

    static ArgumentCaptor<List<Long>> createLongListCaptor() {
        return ArgumentCaptor.forClass((Class) List.class);
    }

    static ArgumentCaptor<List<Integer>> createIntListCaptor() {
        return ArgumentCaptor.forClass((Class) List.class);
    }

    static ArgumentCaptor<Set<String>> createStringSetCaptor() {
        return ArgumentCaptor.forClass((Class) Set.class);
    }

    static ArgumentCaptor<List<CompanyVolume>> createCompanyVolListCaptor() {
        return ArgumentCaptor.forClass((Class) List.class);
    }

    static ArgumentCaptor<Map<Integer, SolrDocument>> createEventDocMapCaptor() {
        return ArgumentCaptor.forClass((Class) Map.class);
    }

    static ArgumentCaptor<Map<String, Entity>> createEntityMapCaptor() {
        return ArgumentCaptor.forClass((Class) Map.class);
    }

    static ArgumentCaptor<Callable<Object[]>> createCallableTaskReturnsObjArrayCaptor() {
        return ArgumentCaptor.forClass((Class) Callable.class);
    }

    static ArgumentCaptor<Callable<SolrDocumentList>> createCallableTaskReturnsSolrDocListCaptor() {
        return ArgumentCaptor.forClass((Class) Callable.class);
    }
}
