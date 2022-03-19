package com.firstrain.frapi.service.impl;

import com.firstrain.frapi.domain.BaseSpec;
import com.firstrain.frapi.domain.MgmtTurnoverData;
import com.firstrain.frapi.events.EventObj;
import com.firstrain.frapi.events.IEvents;
import com.firstrain.frapi.obj.EventQueryCriteria;
import com.firstrain.frapi.obj.MgmtTurnoverServiceSpec;
import com.firstrain.frapi.pojo.Entity;
import com.firstrain.frapi.pojo.EntityBriefInfo;
import com.firstrain.frapi.pojo.wrapper.EventSet;
import com.firstrain.frapi.repository.CompanyServiceRepository;
import com.firstrain.frapi.repository.impl.AutoSuggestServiceRepositoryImpl;
import com.firstrain.frapi.repository.impl.CompanyServiceRepositoryImpl;
import com.firstrain.frapi.repository.impl.EntityBaseServiceRepositoryImpl;
import com.firstrain.frapi.service.EntityBaseService;
import com.firstrain.frapi.util.ConvertUtil;
import com.firstrain.frapi.util.DefaultEnums;
import com.firstrain.frapi.util.EventUtils;
import com.firstrain.frapi.util.StatusCode;
import com.firstrain.mip.object.FR_IEvent;
import com.firstrain.obj.IEntityInfo;
import com.firstrain.obj.IEntityInfoCache;
import com.firstrain.solr.client.DocEntriesUpdator;
import com.firstrain.solr.client.DocEntry;
import com.firstrain.solr.client.EntityEntry;
import com.firstrain.solr.client.SearchTokenEntry;
import com.firstrain.solr.client.SolrSearcher;
import com.firstrain.solr.client.util.SolrServerReader;
import com.firstrain.web.pojo.EntityMatchInputBean;
import com.google.common.collect.ImmutableList;
import com.google.common.net.InternetDomainName;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.NamedList;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyShort;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyVararg;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.powermock.reflect.Whitebox.setInternalState;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        CompanyServiceImpl.class,
        CompanyServiceRepositoryImpl.class,
        EventServiceImpl.class,
        SolrServerReader.class,
        CompanyServiceRepositoryImpl.class,
        SolrSearcher.class,
        HashMap.class,
        EventUtils.class,
        EntityBaseServiceImpl.class,
        DocEntriesUpdator.class,
        AutoSuggestServiceRepositoryImpl.class,
        QueryRequest.class,
        InternetDomainName.class
})
public class CompanyServiceImplTest {

    private static final String SEARCH_TOKEN = "token";
    private static final String CAT_ID = "12";
    private static final long TWEET_ID = 12;
    private static final String ENT_INFO_ID = "123";
    private static final long DOC_GROUP_ID = 12L;
    private static final String USR_IMAGE = "img";
    private static final String TWEET_CLASS = "tweetClass";
    private static final String CORE_TWEET = "coreTweet";
    private static final String DOC_TWEET = "docTweet";
    private static final String SCREEN_NAME = "screenName";
    private static final String DOC_NAME = "docName";
    private static final String DOC_LINKS = "docLinks";
    private static final String DOC_DESC = "docDesc";
    private static final int LINK_ID = 22;
    private static final String DOC_SOURCE = "source";
    private static final int TOPIC_ID_CORE_TWEET = 34;
    private static final int GROUP_SIZE = 44;
    private static final int COMBO_SCORE = 483;
    private static final long NUM_FOUND = 2L;
    private static final String EXCEPTION_MESSAGE = "message";
    private static final int COMP_ID = 11;
    private static final String CS_EXCLUDED_EVENT_TYPE_GROUP = "12";
    private static final int DAY_ID = 20;
    private static final int EVENT_ENTITY_FLAG = 34;
    private static final int COMPANY_ID = 12;
    private static final int EVENT_ID = 123;
    private static final long EVIDENCE_ID_SITE_DOC_ACTIVE = 2L;
    private static final String CONFIG_XML = "xml";
    private static final String TREND_ID = "125";
    private static final String OLD_COMPANY_ID = "986";
    private static final String OLD_DESIGNATION = "oldDesgination";
    private static final String OLD_GROUP = "oldGroup";
    private static final String NEW_GROUP = "newGroup";
    private static final String OLD_REGION = "oldRegion";
    private static final String NEW_REGION = "newRegion";
    private static final String NEW_COMPANY_ID = "5464";
    private static final String PERSON_NAME = "personName";
    private static final String URL_TEXT = "http://www.sample.com";
    private static final long ENTITY_ID_VALUE = 2L;
    private static final long DOC_ID_VALUE = 33L;
    private static final String DOC_BODY_TEXT = "docBody";
    private static final String ENTRY_SITE_DOC_ID = "879";
    private static final String INPUT_NALCS = "nalcs";
    private static final String INPUT_SEDOL = "sedol";
    private static final String INPUT_TICKER = "ticker";
    private static final String INPUT_ZIP = "5436";
    private static final String INPUT_ISIN = "isin";
    private static final String HOOVERS_IC = "5647";
    private static final String INPUT_HOME_PAGE = "www.home.com/path";
    private static final String INPUT_DUNS = "duns";
    private static final String INPUT_COUNTRY = "country";
    private static final String INPUT_CITY = "city";
    private static final String CIK_CODE = "54869";
    private static final String INPUT_ADDRESS = "address";
    private static final int INPUT_COUNT = 685;
    private static final String Q_VALUE = "q";
    private static final String DNB_COMP_ID = "657";
    private static final String ATTR_COMP_ID = "12";
    private static final String INPUT_NAME = "inputName";
    private static final String SYNONYM_H = "synonym_h";
    private static final String SYNONYM = "synonym";
    private static final String CIK_H = "cikH";
    private static final String ISIN_H = "isinH";
    private static final String SEDOL_H = "sedolH";
    private static final String TYPE_COMPANY_TEXT = "company";
    private static final String FIELD_ENTITY_BASE_SERVICE_REPOSITORY = "entityBaseServiceRepository";
    private static final int DOC_COUNT = 200;
    private static final String LOCAL_NAME_TEXT = "localName";

    @InjectMocks
    private final CompanyServiceImpl companyService = new CompanyServiceImpl();
    @Spy
    private final AutoSuggestServiceImpl autoSuggestService = new AutoSuggestServiceImpl();
    @Spy
    private final CompanyServiceRepository companyServiceRepository = new CompanyServiceRepositoryImpl();
    @Spy
    private final EventServiceImpl eventService = new EventServiceImpl();
    @Spy
    private final ConvertUtil convertUtil = new ConvertUtil();
    @Spy
    private final EntityBaseService entityBaseService = new EntityBaseServiceImpl();
    @Spy
    private final AutoSuggestServiceRepositoryImpl autoSuggestServiceRepository = new AutoSuggestServiceRepositoryImpl();
    @Spy
    private final EntityBaseServiceRepositoryImpl entityBaseServiceRepository = new EntityBaseServiceRepositoryImpl();
    @Mock
    private IEntityInfoCache entityInfoCache;
    @Mock
    private IEntityInfo entityInfo;
    @Mock
    private XMLInputFactory factory;
    @Mock
    private SolrSearcher searcher;
    private ErrorCollector errorCollector;
    private ExpectedException expectedException;
    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector = new ErrorCollector())
            .around(expectedException = ExpectedException.none());
    @Mock
    private NamedList<Object> objectNamedList;
    @Mock
    private SolrDocument solrDocument;
    @Mock
    private XMLEventReader xmlEventReader;
    @Mock
    private Entity entity;
    @Mock
    private EntityEntry entityEntry;
    @Mock
    private QueryRequest queryRequest;
    @Mock
    private QueryResponse queryResponse;
    @Mock
    private InternetDomainName fullDomainName;

    @Before
    public void setUp() throws Exception {
        mockStatic(SolrServerReader.class, SolrSearcher.class
                , HashMap.class, DocEntriesUpdator.class, InternetDomainName.class);
        when(SolrServerReader.retrieveSolrDocsInBatches(any(SolrServer.class)
                , anyString(), anyInt(), (String[]) anyVararg()))
                .thenReturn(createSolrDocuments());
        NamedList<Object> entries = new NamedList<Object>();
        entries.add("key", objectNamedList);
        when(objectNamedList.get("internalMoves")).thenReturn(Collections.singletonList(123));
        when(objectNamedList.get("departures")).thenReturn(Collections.singletonList(324));
        when(objectNamedList.get("hires")).thenReturn(Collections.singletonList(546));
        when(objectNamedList.get("result")).thenReturn(entries);
        when(queryResponse.getResponse()).thenReturn(objectNamedList);
        when(SolrSearcher.runQueryRequest(any(SolrServer.class), any(SolrQuery.class), anyBoolean()))
                .thenReturn(queryResponse);
        when(entityInfo.getId()).thenReturn(ENT_INFO_ID);
        when(entityInfo.getCompanyId()).thenReturn(COMP_ID);
        when(entityInfoCache.catIdToEntity(anyString())).thenReturn(entityInfo);
        when(entityInfoCache.companyIdToEntity(anyInt())).thenReturn(entityInfo);
        when(entityBaseServiceRepository.getEntityInfoCache()).thenReturn(entityInfoCache);
        doReturn(entityInfoCache).when(companyServiceRepository).getEntityInfoCache();
    }

    @Test
    public void givenGetCompanyDocumentsThenThrowsException() throws Exception {
        //Arrange
        expectedException.expectMessage(EXCEPTION_MESSAGE);
        expectedException.expect(RuntimeException.class);
        doThrow(new RuntimeException(EXCEPTION_MESSAGE))
                .when(companyServiceRepository).getCompanyInfoFromIndex(anyString());
        //Act
        companyService.getCompanyDocuments(SEARCH_TOKEN);
    }

    @Test
    public void getCompanyDocuments() throws Exception {
        //Act
        SolrDocument document = companyService.getCompanyDocuments(SEARCH_TOKEN);
        //Assert
        errorCollector.checkThat(document.size(), is(36));
    }

    @Test
    public void getMgmtTurnoverData() throws Exception {
        //Arrange
        MgmtTurnoverServiceSpec serviceSpec = new MgmtTurnoverServiceSpec();
        serviceSpec.setDetails(true);
        BaseSpec spec = new BaseSpec();
        spec.setNeedPagination(true);
        when(entityInfo.getSearchToken()).thenReturn(SEARCH_TOKEN);
        when(entityInfoCache.searchTokenToEntity(anyString()))
                .thenReturn(entityInfo);
        when(entityInfo.getType()).thenReturn(SearchTokenEntry.SEARCH_TOKEN_TAG_COMPANY);

        //Act
        MgmtTurnoverData data = companyService.getMgmtTurnoverData(serviceSpec, spec);

        //Assert
        System.out.println();
        errorCollector.checkThat(data.getMonthlySummary().size(),is(1));
        errorCollector.checkThat(data.getAverageTurnoverMth(),is(993.0f));
        errorCollector.checkThat(data.getCompanyId(),is(COMP_ID));
    }

    @Test
    public void givenGetCompanyAutoSuggestListWhenEntityListIsNull() throws Exception {
        //Arrange
        Map<Integer, SolrDocument> solrDocumentMap = new HashMap<Integer, SolrDocument>();
        solrDocumentMap.put(12, solrDocument);
        doReturn(null).when(entityBaseService).autoSuggestForEntity(anyString(), anyString(), anyInt()
                , anyString(), anyMap());
        SolrDocumentList documents = createSolrDocuments();
        doReturn(documents).when(companyServiceRepository).getCompanySolrDocsForQuery(anyString());
        when(fullDomainName.parts()).thenReturn(ImmutableList.of("1", "2"));
        when(fullDomainName.publicSuffix()).thenReturn(fullDomainName);
        when(InternetDomainName.from(anyString())).thenReturn(fullDomainName);
        //Act
        EntityBriefInfo briefInfo = companyService.getCompanyAutoSuggestList(null, createEntityMatchInputBean());
        //Assert
        errorCollector.checkThat(briefInfo.getStatusCode(),is(StatusCode.ENTITY_NOT_FOUND));
    }

    @Test
    public void givenGetCompanyEventsWhenEntityListIsNotNull() throws Exception {
        //Arrange
        Map<Integer, SolrDocument> solrDocumentMap = new HashMap<Integer, SolrDocument>();
        solrDocumentMap.put(12, solrDocument);
        BaseSpec spec = new BaseSpec();
        spec.setNeedPagination(true);
        short start = 0;
        short count = 1;
        spec.setStart(start);
        spec.setCount(count);
        spec.setNeedRelatedDoc(true);
        SolrDocumentList documents = createSolrDocuments();
        EventQueryCriteria criteria = new EventQueryCriteria(new int[]{123, 324}
                , new int[]{345, 213});
        doReturn(documents).when(eventService).getEventsDocsFromSolr(criteria);
        when(SolrServerReader.retrieveNSolrDocs(any(SolrServer.class), anyString(), anyInt(),
                anyInt(), anyString(), anyBoolean(), (String[]) anyVararg()))
                .thenReturn(createSolrDocuments());
        when(factory.createXMLEventReader(any(StringReader.class))).thenReturn(xmlEventReader);
        setInternalState(eventService, FIELD_ENTITY_BASE_SERVICE_REPOSITORY , entityBaseServiceRepository);
        setInternalState(EventUtils.class, "factory", factory);
        HashMap<String, String> newHashMap = new HashMap<String, String>();
        newHashMap.put("trendID", TREND_ID);
        newHashMap.put("oldCompanyID", OLD_COMPANY_ID);
        newHashMap.put("oldDesignation", OLD_DESIGNATION);
        newHashMap.put("oldGroup", OLD_GROUP);
        newHashMap.put("newGroup", NEW_GROUP);
        newHashMap.put("oldRegion", OLD_REGION);
        newHashMap.put("newRegion", NEW_REGION);
        newHashMap.put("newCompanyID", NEW_COMPANY_ID);
        newHashMap.put("url", URL_TEXT);
        newHashMap.put("person_name", PERSON_NAME);
        whenNew(HashMap.class).withNoArguments().thenReturn(newHashMap);
        when(companyServiceRepository.getSearcher()).thenReturn(searcher);
        EventObj eventObjLocal = new EventObj();
        eventObjLocal.setEventId(EVENT_ID);
        eventObjLocal.setDayId(DAY_ID);
        eventObjLocal.setFlag(EVENT_ENTITY_FLAG);
        eventObjLocal.setEntityInfo(entityInfo);
        eventObjLocal.setEventType(IEvents.EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_MRKT_OFFICER_TURNOVER);
        eventObjLocal.setCaption(DefaultEnums.EventTypeEnum.TYPE_DELAYED_SEC_FILING.getLabel());
        eventObjLocal.setLinkable(true);
        eventObjLocal.setScore(COMBO_SCORE);
        eventObjLocal.addPrimaryEvidence(ENTITY_ID_VALUE);
        doReturn(Collections.singletonList(eventObjLocal))
                .when(eventService).applySingleCompanyEventsFilter(anyListOf(IEvents.class),
                anyInt(), anyBoolean());
        when(searcher.fetch(any(long[].class), anyString(), anyShort()))
                .thenReturn(Collections.singletonList(createDocEntry()));
        when(searcher.fetchDocs(any(SolrQuery.class))).thenReturn(createSolrDocuments());
        doReturn(searcher).when(entityBaseServiceRepository).getSearcher();
        setInternalState(entityBaseService, "convertUtil", convertUtil);
        setInternalState(entityBaseService, FIELD_ENTITY_BASE_SERVICE_REPOSITORY , entityBaseServiceRepository);

        //Act
        EventSet eventSetResult = companyService.getCompanyEvents(spec, CS_EXCLUDED_EVENT_TYPE_GROUP, solrDocumentMap);
        //Assert
        errorCollector.checkThat(eventSetResult.getEvents().size(),is(1));
        errorCollector.checkThat(eventSetResult.getTotalCount(),is(1));
    }

    @Test
    public void getCompanyAutoSuggestList() throws Exception {
        //Arrange
        when(SolrServerReader.retrieveNSolrDocs(any(SolrServer.class), anyString(), anyInt()
                , anyInt(), (String[]) anyVararg())).thenReturn(createSolrDocuments());
        setInternalState(companyServiceRepository, "convertUtil", convertUtil);
        //Act
        EntityBriefInfo briefInfo =  companyService.getCompanyAutoSuggestList(Q_VALUE, createEntityMatchInputBean());
        //Assert
        errorCollector.checkThat(briefInfo.getStatusCode(),is(StatusCode.REQUEST_SUCCESS));
    }

    @Test
    public void givenGetCompanyAutoSuggestListWhenAutoSuggestIsTrueThenEntrityBriefInfo()
            throws Exception {
        //Arrange
        when(SolrServerReader.retrieveNSolrDocs(any(SolrServer.class), anyString(), anyInt()
                , anyInt(), (String[]) anyVararg())).thenReturn(createSolrDocuments());
        setInternalState(companyServiceRepository, "convertUtil", convertUtil);
        setInternalState(entityBaseService, "convertUtil", convertUtil);
        setInternalState(entityBaseService, "autoSuggestService", autoSuggestService);
        setInternalState(entityBaseService, FIELD_ENTITY_BASE_SERVICE_REPOSITORY , entityBaseServiceRepository);
        setInternalState(autoSuggestService, "repository", autoSuggestServiceRepository);
        NamedList mainNamedList = new NamedList();
        NamedList namedListLocal = new NamedList();
        namedListLocal.add("synonymH", SYNONYM_H);
        namedListLocal.add("synonym", SYNONYM);
        namedListLocal.add("cikH", CIK_H);
        namedListLocal.add("sedolH", SEDOL_H);
        namedListLocal.add("isinH", ISIN_H);
        namedListLocal.add("name", LOCAL_NAME_TEXT);
        namedListLocal.add("type", TYPE_COMPANY_TEXT);
        namedListLocal.add("repOff", 12);
        namedListLocal.add("repLen", 11);
        namedListLocal.add("nameH", SYNONYM_H);
        namedListLocal.add("ticker", SYNONYM_H);
        namedListLocal.add("tickerh", SYNONYM_H);

        mainNamedList.add("synonym", namedListLocal);
        when(objectNamedList.get("entities")).thenReturn(mainNamedList);
        when(queryResponse.getResponse()).thenReturn(objectNamedList);
        when(queryRequest.process(any(SolrServer.class))).thenReturn(queryResponse);
        whenNew(QueryRequest.class).withAnyArguments().thenReturn(queryRequest);
        when(entityInfo.getId()).thenReturn(ENT_INFO_ID);
        when(entityInfo.getCompanyId()).thenReturn(COMPANY_ID);
        when(entityInfo.getDocCount()).thenReturn(DOC_COUNT);
        when(entityInfoCache.searchTokenToEntity(anyString())).thenReturn(entityInfo);
        when(entityInfoCache.companyIdToEntity(anyInt())).thenReturn(entityInfo);
        doReturn(entityInfoCache).when(autoSuggestServiceRepository).getEntityInfoCache();
        doReturn(entityInfoCache).when(entityBaseServiceRepository).getEntityInfoCache();
        //Act
       EntityBriefInfo briefInfo = companyService.getCompanyAutoSuggestList(null, createEntityMatchInputBean());
        //Assert
        errorCollector.checkThat(briefInfo.getStatusCode(),is(StatusCode.REQUEST_SUCCESS));
        errorCollector.checkThat(briefInfo.getMatchedEntity().size(),is(1));
    }

    private static SolrDocumentList createSolrDocuments() {
        SolrDocument document = new SolrDocument();
        document.setField("tweetId", TWEET_ID);
        document.setField("companyId", Collections.singletonList(123));
        document.setField("description", DOC_DESC);
        document.setField("expandedLinks", Collections.singletonList(LINK_ID));
        document.setField("tweetCreationDate", new Date());
        document.setField("links", DOC_LINKS);
        document.setField("name", DOC_NAME);
        document.setField("screenName", SCREEN_NAME);
        document.setField("tweet", DOC_TWEET);
        document.setField("coreTweet", CORE_TWEET);
        document.setField("tweetClass", TWEET_CLASS);
        document.setField("userImage", USR_IMAGE);
        document.setField("source", Collections.singletonList(DOC_SOURCE));
        document.setField("groupId", DOC_GROUP_ID);
        document.setField("groupLead", true);
        document.setField("attrWebsite", "www.website.com");
        document.setField("scope", new Byte("1"));
        document.setField("comboScore", COMBO_SCORE);
        document.setField("creationTime", new Date());
        document.setField("affinityScope", Collections.singletonList(89));
        document.setField("groupSize", GROUP_SIZE);
        document.setField("catId", CAT_ID);
        document.setField("id", DOC_ID_VALUE);
        document.setField("eventDate", new Date());
        document.setField("companyId", COMPANY_ID);
        document.setField("lastUpdatedMinuteId", new Date());
        document.setField("dayId", DAY_ID);
        document.setField("configXml", CONFIG_XML);
        document.setField("attrCompanyId", ATTR_COMP_ID);
        document.setField("dnbCompanyId", DNB_COMP_ID);
        document.setField("eventId", EVENT_ID);
        document.setField("eventEntityFlag", EVENT_ENTITY_FLAG);
        document.setField("affinityScoreNormalized", (short) 199);
        document.setField("evidenceIdSiteDocActive", EVIDENCE_ID_SITE_DOC_ACTIVE);
        List<Double> doubles = new ArrayList<Double>();
        doubles.add(1.2);
        document.setField("score", doubles);
        document.setField("eventType", FR_IEvent.TYPE_MGMT_CHANGE_CEO_TURNOVER);
        document.setField("topicIdCoreTweet", Collections.singletonList(TOPIC_ID_CORE_TWEET));
        SolrDocumentList documents = new SolrDocumentList();
        documents.add(document);
        documents.setNumFound(NUM_FOUND);
        return documents;
    }

    private DocEntry createDocEntry() {
        DocEntry entry = new DocEntry();
        entry.setDocBody(DOC_BODY_TEXT);
        entry.sourceEntity = entityEntry;
        entry.sitedocId = ENTRY_SITE_DOC_ID;
        return entry;
    }

    private static EntityMatchInputBean createEntityMatchInputBean() {
        EntityMatchInputBean inputBean = new EntityMatchInputBean();
        EntityMatchInputBean.EntityInput input = new EntityMatchInputBean.EntityInput();
        input.setAddress(INPUT_ADDRESS);
        input.setCikCode(CIK_CODE);
        input.setCity(INPUT_CITY);
        input.setCountry(INPUT_COUNTRY);
        input.setDuns(INPUT_DUNS);
        input.setHomePage(INPUT_HOME_PAGE);
        input.setHooversIC(HOOVERS_IC);
        input.setIsin(INPUT_ISIN);
        input.setZip(INPUT_ZIP);
        input.setTicker(INPUT_TICKER);
        input.setSedol(INPUT_SEDOL);
        input.setNaics(INPUT_NALCS);
        input.setName(INPUT_NAME);
        inputBean.setCompany(input);
        inputBean.setCount(INPUT_COUNT);
        inputBean.setIndustry(input);
        inputBean.setRegion(input);
        inputBean.setTopic(input);
        return inputBean;
    }
}
