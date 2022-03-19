package com.firstrain.frapi.service.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.powermock.reflect.internal.WhiteboxImpl.setInternalState;

import com.firstrain.content.similarity.DocumentSimilarityUtil;
import com.firstrain.db.obj.APIArticleHide;
import com.firstrain.frapi.FRCompletionService;
import com.firstrain.frapi.domain.BaseSpec;
import com.firstrain.frapi.domain.SectionSpec;
import com.firstrain.frapi.domain.VisualizationData;
import com.firstrain.frapi.domain.TwitterSpec;
import com.firstrain.frapi.domain.MgmtTurnoverData;
import com.firstrain.frapi.domain.BlendDunsInput;
import com.firstrain.frapi.domain.AutoSuggest;
import com.firstrain.frapi.domain.AutoSuggestInfo;
import com.firstrain.frapi.domain.IndustryBriefDomain;
import com.firstrain.frapi.events.EventObj;
import com.firstrain.frapi.events.IEvents;
import com.firstrain.frapi.obj.EventQueryCriteria;
import com.firstrain.frapi.pojo.EnterprisePref;
import com.firstrain.frapi.pojo.Entity;
import com.firstrain.frapi.pojo.EntityBriefInfo;
import com.firstrain.frapi.pojo.wrapper.BaseSet;
import com.firstrain.frapi.pojo.wrapper.DocumentSet;
import com.firstrain.frapi.pojo.wrapper.EventSet;
import com.firstrain.frapi.pojo.wrapper.TweetSet;
import com.firstrain.frapi.repository.EntityBaseServiceRepository;
import com.firstrain.frapi.repository.ExcelProcessingHelperRepository;
import com.firstrain.frapi.repository.AccelerometerServiceRepository;
import com.firstrain.frapi.repository.CompanyServiceRepository;
import com.firstrain.frapi.repository.RestrictContentRepository;
import com.firstrain.frapi.repository.impl.CompanyServiceRepositoryImpl;
import com.firstrain.frapi.repository.impl.IndustryClassificationMap;
import com.firstrain.frapi.service.CompanyService;
import com.firstrain.frapi.service.VisualizationService;
import com.firstrain.frapi.service.TwitterService;
import com.firstrain.frapi.service.EntityBaseService;
import com.firstrain.frapi.service.IndustryBriefService;
import com.firstrain.frapi.service.EntityProcessingService;
import com.firstrain.frapi.service.EventService;
import com.firstrain.frapi.service.AutoSuggestService;
import com.firstrain.frapi.service.RestrictContentService;
import com.firstrain.frapi.service.DnbService;
import com.firstrain.frapi.util.ContentTokeHandler;
import com.firstrain.frapi.util.EntityHandler;
import com.firstrain.frapi.util.DateBucketUtils;
import com.firstrain.frapi.util.ConvertUtil;
import com.firstrain.frapi.util.SearchResultGenerator;
import com.firstrain.frapi.util.ServicesAPIUtil;
import com.firstrain.obj.IEntityInfo;
import com.firstrain.obj.IEntityInfoCache;
import com.firstrain.solr.client.SearchSpec;
import com.firstrain.solr.client.EntityEntry;
import com.firstrain.solr.client.SolrSearcher;
import com.firstrain.solr.client.DocEntry;
import com.firstrain.solr.client.SearchResult;
import com.firstrain.solr.client.DocListBucket;
import com.firstrain.solr.client.DateCount;
import com.firstrain.solr.client.HotListBucket;
import com.firstrain.solr.client.HotListEntry;
import com.firstrain.solr.client.SearchTokenEntry;
import com.firstrain.solr.client.util.SolrServerReader;
import com.google.common.collect.Sets;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Map;
import java.util.TimeZone;
import java.util.HashMap;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        SolrServerReader.class,
        SolrSearcher.class,
        EntityBriefServiceImpl.class,
        EntityHandler.class,
        ContentTokeHandler.class,
        SearchResultGenerator.class})
public class EntityBriefServiceImplTestSetup {
    static final String LANGUAGE = "language_urdu";
    static final String LOGO_IMAGE_URL = "logoImageUrl";
    private static final String ATTR_STATE_NAME_ABR = "attrStateNameAbr";
    static final String ATTR_STATE_NAME = "attrStateName";
    static final String ATTR_ZIP = "attrZip";
    static final String ATTR_WEBSITE = "attrWebsite";
    static final String ATTR_CITY = "attrCity";
    static final String ATTR_ADDRESS = "attrAddress";
    static final String ATTR_COUNTRY = "attrCountry";
    static final String SEARCH_TOKEN = "searchToken";
    static final String COMPANY_SERVICE_REPOSITORY_COL = "companyServiceRepository";
    static final String FQUERY_STR = "fquery";
    static final String QUERY_SRT = "query";
    static final String ENTITY_ID = "301024";
    static final String EXCLUDE_FILTERS = "excludeFilters";
    static final String SCOPE_DIRECTIVE = "8=H&4,Z2m_EjpFc.'n_";
    static final String TEXT_STR = "text_str";
    static final String FILTER_TOKEN = "-contentFilterToken";
    static final String NAME = "name";
    static final String ID = "I234";
    static final String EXECUTED_ID_2 = "Executed_ie_2";
    static final String EXECUTED_ID_1 = "Executed_id_1";
    static final String FROM = "from";
    static final String TO = "to";
    private static final Long LONG_ID_1 = 123L;
    private static final Long LONG_ID_2 = 124L;
    static final short SHORT_ONE = 1;
    static final short COUNT = 10;
    static final int BASIC_WEB_RESULTS_SEARCH_DAYS = 6 * 30;
    static final int WEB_RESULTS_SEARCH_DAYS_BIMONTHLY = 2 * 30;
    static final short WV_EVENTS_COUNT = 40;
    private static final String BUCKET_NAME = "bucket_name";
    static final String ARTICLE_ID = "2";
    static final String INDUSTRY_BRIEF_SERVICE_COL = "industryBriefService";
    static final String ENTITY_BASE_SERVICE_COL = "entityBaseService";
    static final String GET_TWEETS_METHOD = "getTweets";
    static final String REQUEST_TWEETS = "Tweets";
    static final int INTEGER_VALUE_TWO = 2;
    static final int SCOPE = 5;
    private static final int INT_ENTITY_ID = 301024;
    static final int FIRST_INDEX = 0;
    private static final int SEARCH_TOKEN_TAG_SECTOR_TOPIC = 771;

    @Mock
    ExecutorService executorService;
    @Mock
    DocumentSimilarityUtil documentSimilarityUtil;
    @Mock
    IEntityInfoCache infoCache;
    @Mock
    IEntityInfo entityInfo;
    @Mock
    VisualizationService visualizationService;
    @Mock
    EntityEntry entity;
    @Mock
    SolrSearcher searcher;
    @Mock
    CompanyService companyService;
    @Mock
    DateBucketUtils dateBucketUtils;
    @Mock
    EntityBaseServiceRepository entityBaseServiceRepository;
    @Mock
    ExcelProcessingHelperRepository excelProcesingHelperRepository;
    @Mock
    TwitterService twitterService;
    @Mock
    AccelerometerServiceRepository accelerometerServiceRepository;
    @Mock
    IndustryClassificationMap industryClassificationMap;
    @Mock
    EntityBaseService entityBaseService;
    @Mock
    IndustryBriefService industryBriefService;
    @Mock
    EntityProcessingService entityProcessingService;
    @Mock
    EventService eventService;
    @Mock
    final ConvertUtil convertUtil = new ConvertUtil();
    @Mock
    ServicesAPIUtil servicesAPIUtil;
    @Mock
    RegionExcelUtilImpl regionExcelUtilImpl;
    @Mock
    ThreadPoolTaskExecutor taskExecutor;
    @Mock
    ThreadPoolExecutor executor;
    @Mock
    AutoSuggestService autoSuggestService;
    @Mock
    FRCompletionService<BaseSet> completionService;
    @Mock
    CompanyServiceRepository companyServiceRepository;
    @Mock
    IEvents iEvents;
    @Mock
    RestrictContentRepository restrictContentRepository;

    IndustryBriefService industryBriefServiceFinal;
    CompanyService companyServiceNew;
    DocEntry docEntry;
    RestrictContentService restrictContentService;
    EntityBaseService entityBaseServiceFinal;

    @InjectMocks
    final EntityBriefServiceImpl serviceImpl = new EntityBriefServiceImpl();

    final ErrorCollector errorCollector = new ErrorCollector();
    final ExpectedException expectedException = ExpectedException.none();
    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector)
            .around(expectedException);

    EnterprisePref enterprisePref;
    AutoSuggest autoSuggest;
    private List<AutoSuggestInfo> autoSuggestInfoList;
    private AutoSuggestInfo autoSuggestInfo;
    CompanyServiceRepositoryImpl companyServiceRepositoryImpl;
    private TweetSet tweetSet;
    private IndustryBriefDomain industryBriefDomain;
    SearchResult searchResult;
    private SolrDocument solrDocument;
    SectionSpec sectionSpecLocalFinalNew;

    @Captor
    ArgumentCaptor<Callable<BaseSet>> callableArgumentCaptor;
    @Captor
    ArgumentCaptor<Callable<SearchResult>> searchResultArgumentCaptor;
    @Captor
    ArgumentCaptor<BaseSpec> baseSpecArgumentCaptor;
    @Captor
    ArgumentCaptor<TwitterSpec> twitterSpecArgumentCaptor;
    @Captor
    ArgumentCaptor<BlendDunsInput> blendDunsInputArgumentCaptor;

    EventSet eventSet;
    SectionSpec sectionSpec;
    Entity entityPojo;

    @Before
    public void setUp() throws Exception {
        sectionSpecLocalFinalNew = new SectionSpec();
        industryBriefServiceFinal = new IndustryBriefServiceImpl();
        companyServiceNew = new CompanyServiceImpl();
        docEntry = new DocEntry();
        restrictContentService = new RestrictContentServiceImpl();
        entityBaseServiceFinal = new EntityBaseServiceImpl();
        searchResult = new SearchResult();
        mockStatic(SolrServerReader.class, SolrSearcher.class, EntityHandler.class);
        when(entityBaseServiceRepository.getSearcher()).thenReturn(searcher);
        searchResult.buckets = prepareBuckets(docEntry);
        searchResult.dateCount = new DateCount();
        final HotListBucket hotListBucket = new HotListBucket(BUCKET_NAME);
        final List<HotListEntry> hotListEntries = new ArrayList<HotListEntry>();
        final HotListEntry hotListEntry = new HotListEntry();
        entityPojo = mock(Entity.class);
        hotListEntry.entity = entity;
        when(entity.getSearchToken()).thenReturn("a");
        when(entity.getId()).thenReturn(ENTITY_ID);
        hotListEntries.add(hotListEntry);
        hotListBucket.entries = hotListEntries;
        searchResult.facetIndustries = hotListBucket;
        when(searcher.search(any(SearchSpec.class))).thenReturn(searchResult);
        solrDocument = new SolrDocument();
        solrDocument.setField("sector", INTEGER_VALUE_TWO);
        solrDocument.setField("segment", INTEGER_VALUE_TWO);
        solrDocument.setField("industry", INTEGER_VALUE_TWO);
        solrDocument.setField("country", INTEGER_VALUE_TWO);
        solrDocument.setField("scope", "hello World".getBytes()[0]);
        solrDocument.setField("groupSize", INTEGER_VALUE_TWO);
        final List<Integer> companyIds = Collections.singletonList(301_024);
        solrDocument.setField("companyId", companyIds);
        solrDocument.setField("topicIdCoreTweet", companyIds);
        solrDocument.setField("dayId", companyIds);
        final List<Double> doubleIds = new ArrayList<Double>();
        doubleIds.add(1.1D);
        solrDocument.setField("closingPrice", doubleIds);
        solrDocument.setField("openingPrice", doubleIds);
        final List<String> compIds = prepareStringIds();
        solrDocument.addField("attrCompetitorCatId", compIds);
        solrDocument.addField("bizLineCatIds", prepareIntList());
        solrDocument.addField("competitorListPerBl", compIds);
        setServicesRepoNew();
    }

    private void setServicesRepoNew() throws Exception {
        final SolrDocumentList docList = new SolrDocumentList();
        docList.add(solrDocument);
        docList.setNumFound(INTEGER_VALUE_TWO);
        when(entityBaseServiceRepository.getEntityInfoCache()).thenReturn(infoCache);
        when(infoCache.catIdToEntity(anyString())).thenReturn(entityInfo);
        when(infoCache.companyIdToEntity(anyInt())).thenReturn(entityInfo);
        when(infoCache.searchTokenToEntity(anyString())).thenReturn(entityInfo);
        when(entityInfo.getTwitterHighCount()).thenReturn(INTEGER_VALUE_TWO);
        when(entityInfo.getType()).thenReturn(SearchTokenEntry.SEARCH_TOKEN_TAG_COMPANY);
        when(entityInfo.getSearchToken()).thenReturn(TEXT_STR);
        when(entityInfo.getId()).thenReturn(ENTITY_ID);
        when(entityInfo.getBizLineCatIds()).thenReturn(prepareIntList());
        when(entityInfo.getIndustryCatId()).thenReturn(1);
        when(entityInfo.getSectorCatId()).thenReturn(1);
        when(entityInfo.getSegmentCatId()).thenReturn(1);
        when(entityInfo.getName()).thenReturn(NAME);
        when(entityPojo.getId()).thenReturn(ENTITY_ID);
        when(entityPojo.getSearchToken()).thenReturn(TEXT_STR);
        final List<IEvents> iEventsList = new ArrayList<IEvents>();
        iEventsList.add(new EventObj());
        when(eventService.getEntityEventsFromSolr(any(SolrDocumentList.class), anyMap())).thenReturn(iEventsList);
        when(eventService.applyBC(anyListOf(IEvents.class), anyBoolean(), anyInt())).thenReturn(iEventsList);
        when(eventService.getEventsDocsFromSolr(any(EventQueryCriteria.class))).thenReturn(docList);
        PowerMockito.when(SolrServerReader.retrieveNSolrDocs(any(SolrServer.class), anyString(), anyInt(), anyInt(),
                (String[]) anyVararg())).thenReturn(docList);
        PowerMockito.when(SolrServerReader.retrieveSolrDocsInBatches(any(SolrServer.class), anyString(), anyInt(),
                (String[]) anyVararg())).thenReturn(docList);
        PowerMockito.when(SolrSearcher.getTimeZone()).thenReturn(TimeZone.getTimeZone("Europe/Berlin"));
        when(entityBaseServiceRepository.getSolrDocFromIndustryAndBizlineCatIds(anyListOf(String.class),
                anyListOf(String.class), any(String[].class), anyBoolean(), anyBoolean())).thenReturn(docList);
        setServicesParameter();
    }

    static SolrDocument prepareSolrDocument() {
        SolrDocument solrDocumentLocal = new SolrDocument();
        solrDocumentLocal.addField("evidenceIdSiteDocActive", LONG_ID_1);
        solrDocumentLocal.addField("evidenceIdSiteDocInActive", LONG_ID_2);
        solrDocumentLocal.addField("id", LONG_ID_2);
        solrDocumentLocal.addField(ATTR_COUNTRY, ATTR_COUNTRY);
        solrDocumentLocal.addField(ATTR_ADDRESS, ATTR_ADDRESS);
        solrDocumentLocal.addField(ATTR_CITY, ATTR_CITY);
        solrDocumentLocal.addField(ATTR_WEBSITE, ATTR_WEBSITE);
        solrDocumentLocal.addField(ATTR_ZIP, ATTR_ZIP);
        solrDocumentLocal.addField(ATTR_STATE_NAME, ATTR_STATE_NAME);
        solrDocumentLocal.addField(ATTR_STATE_NAME_ABR, ATTR_STATE_NAME_ABR);
        solrDocumentLocal.addField(LOGO_IMAGE_URL, LOGO_IMAGE_URL);
        return solrDocumentLocal;
    }

    static List<DocListBucket> prepareBuckets(DocEntry docEntryLocal) {
        final List<DocEntry> entriesLocal = new ArrayList<>();
        entriesLocal.add(docEntryLocal);
        entriesLocal.add(docEntryLocal);
        entriesLocal.add(docEntryLocal);
        List<DocListBucket> buckets = new ArrayList<>();
        buckets.add(new DocListBucket(null, entriesLocal, INTEGER_VALUE_TWO, 0));
        buckets.add(new DocListBucket(null, entriesLocal, INTEGER_VALUE_TWO, 0));
        return buckets;
    }

    static List<String> prepareStringIds() {
        final List<String> executedIds = new ArrayList<>();
        executedIds.add(EXECUTED_ID_1);
        executedIds.add(EXECUTED_ID_2);
        executedIds.add(ENTITY_ID);
        return executedIds;
    }

    Map<BaseSet.SectionType, SectionSpec> prepareMapForCompanyType() {
        final Map<BaseSet.SectionType, SectionSpec> sectionsMap = new HashMap<>();
        sectionSpecLocalFinalNew.setStart(SHORT_ONE);
        sectionSpecLocalFinalNew.setNeedBucket(true);
        sectionSpecLocalFinalNew.setNeedRelatedDoc(true);
        sectionSpecLocalFinalNew.setNeedPagination(true);
        sectionsMap.put(BaseSet.SectionType.FR, sectionSpecLocalFinalNew);
        sectionsMap.put(BaseSet.SectionType.FT, sectionSpecLocalFinalNew);
        sectionsMap.put(BaseSet.SectionType.HR, sectionSpecLocalFinalNew);
        sectionsMap.put(BaseSet.SectionType.AC, sectionSpecLocalFinalNew);
        sectionsMap.put(BaseSet.SectionType.TE, sectionSpecLocalFinalNew);
        sectionsMap.put(BaseSet.SectionType.WV, sectionSpecLocalFinalNew);
        sectionsMap.put(BaseSet.SectionType.E, sectionSpecLocalFinalNew);
        sectionsMap.put(BaseSet.SectionType.MTC, sectionSpecLocalFinalNew);
        sectionsMap.put(BaseSet.SectionType.VIZ, sectionSpecLocalFinalNew);
        sectionsMap.put(BaseSet.SectionType.TT, sectionSpecLocalFinalNew);
        sectionsMap.put(BaseSet.SectionType.BI, sectionSpecLocalFinalNew);
        sectionsMap.put(BaseSet.SectionType.MD, sectionSpecLocalFinalNew);
        sectionsMap.put(BaseSet.SectionType.TWT, sectionSpecLocalFinalNew);
        sectionsMap.put(BaseSet.SectionType.GL, sectionSpecLocalFinalNew);
        sectionsMap.put(BaseSet.SectionType.RL, sectionSpecLocalFinalNew);
        return sectionsMap;
    }

    static List<Integer> prepareIntList() {
        final List<Integer> integers = new ArrayList<>();
        integers.add(1);
        integers.add(2);
        integers.add(INT_ENTITY_ID);
        return integers;
    }

    void mockEntityInfo() {
        enterprisePref.setDnBId(true);
        final Map<BaseSet.SectionType, SectionSpec> sectionsMap = new HashMap<>();
        sectionsMap.put(BaseSet.SectionType.FR, sectionSpec);
        sectionsMap.put(BaseSet.SectionType.FT, sectionSpec);
        sectionsMap.put(BaseSet.SectionType.WV, sectionSpec);
        sectionsMap.put(BaseSet.SectionType.E, sectionSpec);
        sectionSpec.setNeedTweetAccelerometer(true);
        sectionSpec.setStart(SHORT_ONE);
        sectionSpec.setNeedPagination(true);
        sectionSpec.setNeedBucket(true);
        sectionSpec.setNeedRelatedDoc(true);
        enterprisePref.setSectionsMap(sectionsMap);
        when(entityInfo.getType()).thenReturn(SearchTokenEntry.SEARCH_TOKEN_TAG_INDUSTRY);
    }

    void verifySubmit(int numberOfInvocations) throws Exception {
        verify(completionService, times(numberOfInvocations)).submit(callableArgumentCaptor.capture());
        for (int i = 0; i < numberOfInvocations; i++) {
            callableArgumentCaptor.getAllValues().get(i).call();
        }
    }

    void verifySubmitWhenTaskExecutor(int numberOfInvocations) throws Exception {
        verify(completionService, times(numberOfInvocations)).submit(
                callableArgumentCaptor.capture());
        for (int i = 0; i < numberOfInvocations; i++) {
            callableArgumentCaptor.getAllValues().get(i).call();
            if (callableArgumentCaptor.getAllValues().get(i) instanceof MgmtTurnoverData) {
                final MgmtTurnoverData tweetSetLocal = (MgmtTurnoverData)
                        callableArgumentCaptor.getAllValues().get(i);
                errorCollector.checkThat(tweetSetLocal.getSectionType(), equalTo(BaseSet.SectionType.MTC));
            }
        }
        verify(taskExecutor, times(2)).submit(searchResultArgumentCaptor.capture());
        searchResultArgumentCaptor.getAllValues().get(0).call();
        searchResultArgumentCaptor.getAllValues().get(1).call();
    }

    void mockEntityPojo() {
        enterprisePref.setDnBId(true);
        final Map<BaseSet.SectionType, SectionSpec> sectionsMap = prepareMapForCompanyType();
        enterprisePref.setSectionsMap(sectionsMap);
        when(entityInfo.getType()).thenReturn(SearchTokenEntry.SEARCH_TOKEN_TAG_COMPANY);
        PowerMockito.when(EntityHandler.generateEntity(any(DnbService.class), anyString(), any(BlendDunsInput.class)))
                .thenReturn(entityPojo);
    }

    void mockSourceContent() {
        final BaseSpec baseSpecLocal = new BaseSpec();
        baseSpecLocal.setCount((short) 2);
        baseSpecLocal.setNeedBucket(true);
        when(servicesAPIUtil.setSourceContent(anyBoolean(), anyBoolean(), any(BaseSpec.class),
                any(EnterprisePref.class))).thenReturn(baseSpecLocal);
    }

    void assertForMatchedEntity(EntityBriefInfo actual) {
        errorCollector.checkThat(actual.getMatchedEntity().size(), equalTo(3));
        errorCollector.checkThat(actual.getMatchedEntity().get(0).getName(), equalTo(NAME));
        errorCollector.checkThat(actual.getMatchedEntity().get(0).getSearchToken(), equalTo(TEXT_STR));
        errorCollector.checkThat(actual.getMatchedEntity().get(1).getName(), equalTo(NAME));
        errorCollector.checkThat(actual.getMatchedEntity().get(1).getSearchToken(), equalTo(TEXT_STR));
        errorCollector.checkThat(actual.getMatchedEntity().get(2).getName(), equalTo(NAME));
        errorCollector.checkThat(actual.getMatchedEntity().get(2).getSearchToken(), equalTo(TEXT_STR));
    }

    void mockEventSet() throws Exception {
        mockEntityPojo();
        setInternalState(serviceImpl, ENTITY_BASE_SERVICE_COL, entityBaseService);
        final DocumentSet documentSetLocal = new DocumentSet();
        when(entityBaseService.gethighlightsResults(any(String[].class), any(int[].class), anyInt()))
                .thenReturn(documentSetLocal);
        final BaseSpec baseSpecLocal = new BaseSpec();
        baseSpecLocal.setCount((short) 2);
        baseSpecLocal.setNeedBucket(true);
        when(servicesAPIUtil.setSourceContent(anyBoolean(), anyBoolean(), any(BaseSpec.class),
                any(EnterprisePref.class))).thenReturn(baseSpecLocal);
        enterprisePref.setIndustryClassificationId(SHORT_ONE);
        when(companyServiceRepository.getEntityInfoCache()).thenReturn(infoCache);
        when(infoCache.catIdToEntity(anyString())).thenReturn(entityInfo);
        when(entityInfo.getType()).thenReturn(SEARCH_TOKEN_TAG_SECTOR_TOPIC);
        when(entityInfo.getId()).thenReturn(ENTITY_ID);
        when(entityInfo.getScope()).thenReturn(SCOPE);
        when(entityBaseService.getWebResultsForSearch(anyString(), anyString(), any(BaseSpec.class),
                any(BlendDunsInput.class))).thenReturn(documentSetLocal);
        when(entityBaseService.getEventsTimeline(any(int[].class), any(int[].class), any(BaseSpec.class)))
                .thenReturn(eventSet);
    }

    void mockIndustryBriefDomain() throws Exception {
        setInternalState(serviceImpl, ENTITY_BASE_SERVICE_COL, entityBaseService);
        when(entityInfo.getType()).thenReturn(SearchTokenEntry.SEARCH_TOKEN_TAG_INDUSTRY);
        PowerMockito.when(EntityHandler.generateEntity(any(DnbService.class), anyString(), any(BlendDunsInput.class)))
                .thenReturn(entityPojo);
        final Map<BaseSet.SectionType, SectionSpec> sectionsMap = new HashMap<>();
        final SectionSpec sectionSpecLocal = new SectionSpec();
        sectionsMap.put(BaseSet.SectionType.TE, sectionSpecLocal);
        enterprisePref.setSectionsMap(sectionsMap);
        setInternalState(serviceImpl, INDUSTRY_BRIEF_SERVICE_COL, industryBriefService);
        when(industryBriefService.getVirtualMonitor(anyString(), anyString(), anyString(), any(AtomicBoolean.class),
                anyBoolean())).thenReturn(industryBriefDomain);
        industryBriefDomain.setCompanyIdsArray(new int[]{1, 2});
    }

    void mockEnterprisePref() throws Exception {
        setInternalState(serviceImpl, ENTITY_BASE_SERVICE_COL, entityBaseService);
        when(entityBaseService.getTweetList(any(String[].class), any(TwitterSpec.class)))
                .thenReturn(tweetSet);
        setInternalState(serviceImpl, INDUSTRY_BRIEF_SERVICE_COL, industryBriefService);
        when(industryBriefService.getVirtualMonitor(anyString(), anyString(), anyString(), any(AtomicBoolean.class),
                anyBoolean())).thenReturn(industryBriefDomain);
        industryBriefDomain.setCategoryIdsSet(Sets.newHashSet("cat1", "cat2"));
        mockEntityInfo();
        final Future<BaseSet> baseSetFuture = mock(Future.class);
        final BaseSet baseSetLocal = new BaseSet();
        when(completionService.poll(anyLong(), any(TimeUnit.class))).thenReturn(baseSetFuture);
        when(baseSetFuture.get()).thenReturn(baseSetLocal);
        baseSetLocal.setSectionType(BaseSet.SectionType.T);
        mockSourceContent();
        enterprisePref.setIndustryClassificationId(SHORT_ONE);
    }

    void setServicesParameter() throws Exception {
        arrangeData();
        arrangeData2();
        createData();
    }

    private void arrangeData2() {
        setInternalState(serviceImpl, "restrictContentService", restrictContentService);
        setInternalState(entityBaseServiceFinal, "autoSuggestService", autoSuggestService);
        setInternalState(entityBaseServiceFinal, "entityBaseServiceRepository",
                entityBaseServiceRepository);
        setInternalState(entityBaseServiceFinal, "convertUtil", convertUtil);
        setInternalState(serviceImpl, ENTITY_BASE_SERVICE_COL, entityBaseServiceFinal);
        setInternalState(restrictContentService, "restrictContentRepository",
                restrictContentRepository);
        final List<APIArticleHide> articleHides = new ArrayList<APIArticleHide>();
        final APIArticleHide articleHide = new APIArticleHide();
        articleHide.setArticleId("1:2");
        articleHides.add(articleHide);
        when(restrictContentRepository.getAllHiddenArticles(anyLong(), anyString())).thenReturn(articleHides);
        enterprisePref = new EnterprisePref();
        autoSuggest = new AutoSuggest();
        autoSuggestInfoList = new ArrayList<>();
        autoSuggestInfo = new AutoSuggestInfo();
        autoSuggestInfo.setSynonym(TEXT_STR);
        autoSuggestInfoList.add(autoSuggestInfo);
        autoSuggest.setAutoSuggest(autoSuggestInfoList);
        companyServiceRepositoryImpl = new CompanyServiceRepositoryImpl();
    }

    private void arrangeData() throws Exception {
        when(completionService.submit(any(Callable.class))).thenReturn(null);
        when(completionService.getSubmissions()).thenReturn(1);
        when(completionService.poll(anyLong(), any(TimeUnit.class))).thenReturn(null);
        when(taskExecutor.getThreadPoolExecutor()).thenReturn(executor);
        whenNew(FRCompletionService.class).withAnyArguments().thenReturn(completionService);
        setInternalState(serviceImpl, EXCLUDE_FILTERS, EXCLUDE_FILTERS);
        serviceImpl.contentFilterToken = "contentFilterToken";
        setInternalState(serviceImpl, INDUSTRY_BRIEF_SERVICE_COL, industryBriefServiceFinal);
        setInternalState(serviceImpl, "companyService", companyServiceNew);
        setInternalState(companyServiceNew, COMPANY_SERVICE_REPOSITORY_COL, companyServiceRepository);
        setInternalState(industryBriefServiceFinal, "entityBaseServiceRepository",
                entityBaseServiceRepository);
    }

    private void createData() {
        tweetSet = new TweetSet();
        industryBriefDomain = new IndustryBriefDomain();
        eventSet = new EventSet();
        sectionSpec = new SectionSpec();
    }

    void setEntityInfoLocal() throws Exception {
        mockEntityInfo();
        mockSourceContent();
        when(entityBaseService.getSearchResult(any(String[].class), any(int[].class), anyString()
                , any(BaseSpec.class))).thenReturn(searchResult);
        setInternalState(industryBriefServiceFinal, "entityBaseService", entityBaseService);
        setInternalState(entityBaseServiceFinal, "accelerometerServiceRepository", accelerometerServiceRepository);
        setInternalState(entityBaseServiceFinal, "twitterService", twitterService);
        setInternalState(entityBaseServiceFinal, "servicesAPIUtil", servicesAPIUtil);
        setInternalState(industryBriefServiceFinal, "servicesAPIUtil", servicesAPIUtil);
    }

    void setHandler() {
        mockStatic(ContentTokeHandler.class);
        when(ContentTokeHandler.processContent(anyString(), anyString())).thenReturn(ATTR_WEBSITE);
        sectionSpec.setStart(null);
        sectionSpecLocalFinalNew.setStart(null);
    }

    void setVisualizationServiceLocal() throws Exception {
        final VisualizationData visualizationDataLocal = new VisualizationData();
        when(visualizationService.getVisualizationByEntityToken(anyString(), anyInt(), anyList(), anyString(),
                anyBoolean(), anyBoolean())).thenReturn(visualizationDataLocal);
    }

     void setEntityBriefParameters(EventSet eventSetLocal) throws Exception{
         setVisualizationServiceLocal();
         setInternalState(serviceImpl, "companyService", companyServiceNew);
         setInternalState(companyServiceNew, "eventService", eventService);
         mockEntityPojo();
         setInternalState(serviceImpl, ENTITY_BASE_SERVICE_COL, entityBaseService);
         final BaseSpec baseSpecLocal = new BaseSpec();
         baseSpecLocal.setCount((short) 2);
         baseSpecLocal.setNeedBucket(true);
         when(servicesAPIUtil.setSourceContent(anyBoolean(), anyBoolean(), any(BaseSpec.class),
                 any(EnterprisePref.class))).thenReturn(baseSpecLocal);
         enterprisePref.setIndustryClassificationId(SHORT_ONE);
         when(companyServiceRepository.getEntityInfoCache()).thenReturn(infoCache);
         when(infoCache.catIdToEntity(anyString())).thenReturn(entityInfo);
         when(entityBaseService.getEventSetForMTEvents(anyList(), any(BaseSpec.class)))
                 .thenReturn(eventSetLocal);
    }
}
