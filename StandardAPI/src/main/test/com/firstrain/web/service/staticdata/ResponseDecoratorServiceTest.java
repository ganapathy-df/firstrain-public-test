package com.firstrain.web.service.staticdata;

import com.firstrain.frapi.domain.CompanyTradingRange;
import com.firstrain.frapi.domain.CompanyVolume;
import com.firstrain.frapi.domain.Document;
import com.firstrain.frapi.domain.EntityMap;
import com.firstrain.frapi.domain.EntityStatus;
import com.firstrain.frapi.domain.HistoricalStat;
import com.firstrain.frapi.domain.ItemDetail;
import com.firstrain.frapi.domain.MgmtTurnoverData;
import com.firstrain.frapi.domain.MonitorConfig;
import com.firstrain.frapi.domain.MonitorInfo;
import com.firstrain.frapi.domain.VisualizationData;
import com.firstrain.frapi.domain.WebVolumeEventMeta;
import com.firstrain.frapi.pojo.AuthAPIResponse;
import com.firstrain.frapi.pojo.Entity;
import com.firstrain.frapi.pojo.EntityBriefInfo;
import com.firstrain.frapi.pojo.Event;
import com.firstrain.frapi.pojo.Graph;
import com.firstrain.frapi.pojo.MonitorAPIResponse;
import com.firstrain.frapi.pojo.MonitorBriefDetail;
import com.firstrain.frapi.pojo.wrapper.BaseSet;
import com.firstrain.frapi.pojo.wrapper.DocumentSet;
import com.firstrain.frapi.pojo.wrapper.EventSet;
import com.firstrain.frapi.pojo.wrapper.TweetSet;
import com.firstrain.frapi.util.CompanyTeam;
import com.firstrain.frapi.util.ContentType;
import com.firstrain.frapi.util.DefaultEnums;
import com.firstrain.frapi.util.StatusCode;
import com.firstrain.utils.JSONUtility;
import com.firstrain.web.pojo.Content;
import com.firstrain.web.response.AuthKeyResponse;
import com.firstrain.web.response.EntityDataResponse;
import com.firstrain.web.response.ItemWrapperResponse;
import com.firstrain.web.response.JSONResponse;
import com.firstrain.web.response.MonitorConfigResponse;
import com.firstrain.web.response.MonitorInfoResponse;
import com.firstrain.web.service.core.Constant;
import com.firstrain.web.util.AuthAPIResponseThreadLocal;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Date;

import static com.firstrain.frapi.util.DefaultEnums.Status.ACTIVE;
import static com.firstrain.web.response.JSONResponse.ResStatus.SUCCESS;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.reflect.Whitebox.setInternalState;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        ResponseDecoratorService.class,
        ResourceBundleMessageSource.class,
        Constant.class,
        AuthAPIResponseThreadLocal.class,
        JSONUtility.class
})
public class ResponseDecoratorServiceTest {

    private static final String SEARCH_TOKEN = "SEARCH TOKEN";
    private static final String FILTER_STRING_VALUE = "FILTER";
    private static final int ENT_COMPANY_ID = 99;
    private static final long OWNED_BY = 88L;
    private static final long MONITOR_ID = 554L;
    private static final String ENT_ID = "2352";
    private static final long GROUP_ID = 88L;
    private static final String CLUSTER_IDS_CSV = "345";
    private static final String ENT_NAME = "ent";
    private static final int VOL_RECENT_WEEK = 1;
    private static final String COMP_LOGO_URL = "logo";
    private static final String TICKER = "ticker";
    private static final int ONE_DAY_DOC_COUNT = 23;
    private static final String DNB_COMPANY_ID = "67uiq0";
    private static final String MATCHED_TYPE = "1";
    private static final short RELEVANCE_SCORE = 300;
    private static final int ITEM_COUNT = 100;
    private static final String MONITOR_NAME = "monitor";
    private static final long FAV_USR_ITEM_ID = 75L;
    private static final String RESOURCE_MESSAGE = "resource_message";
    private static final String MSG_KEY = "3234";
    private static final long ITEM_ID = 452L;
    private static final String TWEET_TEXT = "tweet_text";
    private static final String AUTHOR_TITLE = "title";
    private static final String ENT_STATUS_ID = "654";
    private static final String ENT_STATUS_NAME = "ent_status";
    private static final String OWNED_BY_TYPE = "USER";
    private static final int TOTAL_COUNT = 1000;
    private static final String DOC_CAPTION = "docCaption";
    private static final int DOC_SCOPE = 30;
    private static final int COMBO_SCORE = 878;
    private static final String USER_IMAGE = "img";
    private static final String SCREEN_NAME = "screen";
    private static final String TWEET_ID = "4665";
    private static final String CORE_TWEET = "core_tweet";
    private static final int TWEET_TOTAL = 68;
    private static final String DOCUMENT_ID = "456";
    private static final String DOCUMENT_SUMMARY = "docSummary";
    private static final int DOC_SLOT = 800;
    private static final String PRIMARY_DUNS_MATCHER_STR = "5667";
    private static final String DOC_URL = "www.url.com";
    private static final String ADDITIONAL_MATCH_QUALIFIER_STR = "additionalMatcherStr";
    private static final Timestamp VERSION_START_DATE = new Timestamp(790L);
    private static final int API_THRE_SOLD = 99;
    private static final Timestamp VERSION_END_DATE = new Timestamp(900L);
    private static final String PREF_JSON = "json";
    private static final String INCLUDED_API = "included";
    private static final long AUTH_ID = 77L;
    private static final Timestamp EXPIRY_TIME = new Timestamp(100L);
    private static final String EXCLUDED_API = "excluded";
    private static final long ENTERPRISE_ID = 111L;
    private static final String API_VERSION = "3.5.6";
    private static final String AUTH_KEY = "auth_key";
    private static final String TOPIC_IDS_CSV = "345";
    private static final int DOC_SCORE = 90;
    private static final String DOC_QUOTE_TEXT = "docQuoteText";
    private static final String DOC_QUOTE_PERSON = "docQuotePerson";
    private static final String QUERY_STR = "queryStr";
    private static final String QUERY_NAME = "queryName";
    private static final int RELEVANCE_BAND = 20;
    private static final String SCOP_DIRECTIVE = "directive";
    private static final int EVENT_SCOPE = 100;
    private static final String EVENTSET_LINK = "link";
    private static final int UNFILTERED_DATA_SIZE = 10;
    private static final String EVENT_ID = "4897";
    private static final String EVENT_TITLE = "title";
    private static final String EVENT_CAPTION = "caption";
    private static final String EVENT_DESC = "desc";
    private static final String EVENT_URL = "url";
    private static final double CLOSING_PRICE = 142.34;
    private static final double OPENING_PRICE = 20.3;
    private static final int DIFF_ID = 56;
    private static final int EVENT_TYPE_ID = 350;
    private static final String NEW_DESIGNATION = "newDesign";
    private static final String NEW_REGION = "newRegion";
    private static final String OLD_REGION = "oldRegion";
    private static final String PERSON_IMG = "personImg";
    private static final String SEC_FORM_TYPE = "secFormType";
    private static final double EVENT_SCORE = 4;
    private static final int EVENT_FLAG = 10;
    private static final int BASE_FONTS_SIZE = 200;
    private static final String ENT_STATE = "state";
    private static final String ENT_ADDRESS = "addr";
    private static final String ENT_CITY = "ist";
    private static final String ENT_COUNTRY = "TR";
    private static final String ENT_WEBSITE = "www.website.com";
    private static final String ENT_ZIP = "1006";
    private static final String ENT_DOMAIN = "domain";
    private static final String PRIMARY_TICKER = "ticker";
    private static final String COMP_LOGO = "logo";

    @Mock
    private ResourceBundleMessageSource messageSource;
    @Mock
    private com.firstrain.frapi.domain.Entity entityt;
    @Mock
    private Entity primaryEntity;
    @Mock
    private Entity statusEntity;
    
    @InjectMocks
    private final ResponseDecoratorService service = new ResponseDecoratorService();
    @Rule
    public final ErrorCollector errorCollector = new ErrorCollector();

    @Before
    public void setUp() throws Exception {
        mockStatic(Constant.class, AuthAPIResponseThreadLocal.class
                , JSONUtility.class);
        AuthAPIResponse authAPIResponse = createAuthAPIResponseThreadLocal();
        Map<String, Object> stringObjectMap = new HashMap<String, Object>();
        stringObjectMap.put(RequestParsingService.defaultSpec.TOPIC_DIMENSION_FOR_TAGGING, TOPIC_IDS_CSV);
        when(JSONUtility.deserialize(anyString(), eq(Map.class)))
                .thenReturn(stringObjectMap);
        when(AuthAPIResponseThreadLocal.get()).thenReturn(authAPIResponse);
        when(messageSource.getMessage(anyString(), any(Object[].class), eq(Locale.getDefault())))
                .thenReturn(RESOURCE_MESSAGE);
    }

    @Test
    public void getAuthKeyResponse() {
        //Act
        AuthKeyResponse response = service.getAuthKeyResponse(createAuthAPIResponseThreadLocal());
        //Assert
        errorCollector.checkThat(response.getVersion(), is(API_VERSION));
        errorCollector.checkThat(response.getStatus(), is(SUCCESS));
        errorCollector.checkThat(response.getMessage(), is(RESOURCE_MESSAGE));
        errorCollector.checkThat(response.getResult(), is(not(nullValue())));
    }

    @Test
    public void givenGetAddRemoveEntityResponseWhenRemovedIsFalseThenResponse() {
        //Arrange
        MonitorAPIResponse apiResponse = createMonitorAPIResponse();
        Entity entityLocal = createEntity();
        entityLocal.setRemoved(false);
        apiResponse.setEntities(Collections.singletonList(entityLocal));
        //Act
        MonitorInfoResponse response = service.getAddRemoveEntityResponse(apiResponse, MSG_KEY);
        //Assert
        errorCollector.checkThat(response.getStatus(), is(JSONResponse.ResStatus.PARTIAL_SUCCESS));
        errorCollector.checkThat(response.getErrorCode(), is(StatusCode.PARTIAL_SUCCESS));
        errorCollector.checkThat(response.getMessage(), is(RESOURCE_MESSAGE));
        errorCollector.checkThat(response.getResult(), is(not(nullValue())));
    }

    @Test
    public void givenGetAddRemoveEntityResponseWhenRemovedAndAddedNullThenResponse() {
        //Arrange
        MonitorAPIResponse apiResponse = createMonitorAPIResponse();
        Entity entityLocal = createEntity();
        entityLocal.setRemoved(null);
        entityLocal.setAdded(null);
        apiResponse.setEntities(Collections.singletonList(entityLocal));
        //Act
        MonitorInfoResponse response = service.getAddRemoveEntityResponse(apiResponse, MSG_KEY);
        //Assert
        errorCollector.checkThat(response.getStatus(), is(JSONResponse.ResStatus.PARTIAL_SUCCESS));
        errorCollector.checkThat(response.getErrorCode(), is(StatusCode.PARTIAL_SUCCESS));
        errorCollector.checkThat(response.getMessage(), is(RESOURCE_MESSAGE));
        errorCollector.checkThat(response.getResult(), is(not(nullValue())));
    }

    @Test
    public void givenGetAddRemoveEntityResponseWhenAddedFalseThenResponse() {
        //Arrange
        MonitorAPIResponse apiResponse = createMonitorAPIResponse();
        Entity entityLocal = createEntity();
        entityLocal.setAdded(false);
        apiResponse.setEntities(Collections.singletonList(entityLocal));
        //Act
        MonitorInfoResponse response = service.getAddRemoveEntityResponse(apiResponse, MSG_KEY);
        //Assert
        errorCollector.checkThat(response.getStatus(), is(JSONResponse.ResStatus.PARTIAL_SUCCESS));
        errorCollector.checkThat(response.getErrorCode(), is(StatusCode.PARTIAL_SUCCESS));
    }

    @Test
    public void getMonitorConfigResponse() {
        //Act
        MonitorConfigResponse response = service.getMonitorConfigResponse(createMonitorConfig());
        //Assert
        errorCollector.checkThat(response.getStatus(), is(SUCCESS));
        errorCollector.checkThat(response.getMessage(), is(RESOURCE_MESSAGE));
        com.firstrain.web.pojo.MonitorConfig config = response.getResult();
        errorCollector.checkThat(config.getId(), is("M:554"));
        errorCollector.checkThat(config.getName(), is(MONITOR_NAME));
        errorCollector.checkThat(config.getFilters(), is(Collections.singletonList(FILTER_STRING_VALUE)));
    }

    @Test
    public void setRelevanceBand() {

        //Act
        service.setRelevanceBand(RELEVANCE_BAND);
    }

    @Test
    public void getEntityDataResponse() throws Exception {
        //Arrange
        Content contentLocal = new Content();
        contentLocal.setItemCount(ITEM_COUNT);
        contentLocal.setTotalItemCount(TOTAL_COUNT);
        when(Constant.getVersion()).thenReturn(API_VERSION);
        //Act
        EntityDataResponse response = service.getEntityDataResponse(createMonitorAPIResponse(), MSG_KEY);
        //Assert
        errorCollector.checkThat(response.getStatus(), is(JSONResponse.ResStatus.SUCCESS));
        errorCollector.checkThat(response.getMessage(), is(RESOURCE_MESSAGE));
        errorCollector.checkThat(response.getResult(), is(not(nullValue())));
    }

    @Test
    public void getItemWrapperResponse() {
        //Act
        ItemWrapperResponse response = service.getItemWrapperResponse(createTweetSet(), MSG_KEY);
        //Assert
        errorCollector.checkThat(response.getStatus(), is(JSONResponse.ResStatus.SUCCESS));
        errorCollector.checkThat(response.getMessage(), is(RESOURCE_MESSAGE));
        errorCollector.checkThat(response.getResult(), is(not(nullValue())));
    }

    @Test
    public void givenGetEntityDataResponseWhenInputIsEntityBriefThenResponse() {
        //Act
        EntityDataResponse response = service.getEntityDataResponse(createEntityBrief(StatusCode.REQUEST_SUCCESS),MSG_KEY);
        //Assert
        errorCollector.checkThat(response.getStatus(), is(SUCCESS));
        errorCollector.checkThat(response.getMessage(), is(RESOURCE_MESSAGE));
    }

    @Test
    public void getSuccessMsg() {
        //Arrange
        setInternalState(service,"version",API_VERSION);
        //Act
        JSONResponse response = service.getSuccessMsg(MSG_KEY);
        //Assert
        errorCollector.checkThat(response.getStatus(),is(SUCCESS));
        errorCollector.checkThat(response.getVersion(),is(API_VERSION));
    }


    private AuthAPIResponse createAuthAPIResponseThreadLocal() {
        AuthAPIResponse apiResponse = new AuthAPIResponse();
        apiResponse.setStatusCode(StatusCode.NO_ITEMS);
        apiResponse.setAuthKey(AUTH_KEY);
        apiResponse.setApiVersion(API_VERSION);
        apiResponse.setEnterpriseId(ENTERPRISE_ID);
        apiResponse.setExcludedAPIList(Collections.singletonList(EXCLUDED_API));
        apiResponse.setExpiryTime(EXPIRY_TIME);
        apiResponse.setId(AUTH_ID);
        apiResponse.setIncludedAPIList(Collections.singletonList(INCLUDED_API));
        apiResponse.setPrefJson(PREF_JSON);
        apiResponse.setVersionEndDate(VERSION_END_DATE);
        apiResponse.setApiThreshold(API_THRE_SOLD);
        apiResponse.setVersionStartDate(VERSION_START_DATE);
        return apiResponse;
    }


    private MonitorAPIResponse createMonitorAPIResponse() {
        MonitorAPIResponse response = new MonitorAPIResponse();
        response.setStatusCode(StatusCode.NO_ITEMS_IN_MONITOR);
        response.setMonitorId(MONITOR_ID);
        response.setMonitorName(MONITOR_NAME);
        response.setEntities(Collections.singletonList(createEntity()));
        EntityStatus entityStatus = new EntityStatus();
        entityStatus.setEntityStatus(true);
        entityStatus.setId(ENT_STATUS_ID);
        entityStatus.setName(ENT_STATUS_NAME);
        response.setEntityStatus(entityStatus);
        com.firstrain.frapi.domain.MonitorConfig monitorConfig = new com.firstrain.frapi.domain.MonitorConfig();
        monitorConfig.setMonitorId(String.valueOf(MONITOR_ID));
        monitorConfig.setMonitorName(MONITOR_NAME);
        monitorConfig.setOwnedByType(OWNED_BY_TYPE);
        monitorConfig.setOwnedBy(OWNED_BY);
        response.setMonitorConfig(monitorConfig);
        response.setMonitorInfo(createMonitorInfo());
        response.setMonitorBriefDetail(createMonitorBriefDetail());
        return response;
    }

    private static MonitorInfo createMonitorInfo() {
        MonitorInfo monitorInfo = new MonitorInfo();
        monitorInfo.setMailAvailable(true);
        monitorInfo.setMonitorId(String.valueOf(MONITOR_ID));
        monitorInfo.setFavorite(true);
        monitorInfo.setFavoriteUserItemId(FAV_USR_ITEM_ID);
        monitorInfo.setMonitorAdmin(true);
        monitorInfo.setMonitorName(MONITOR_NAME);
        monitorInfo.setEventAvailable(true);
        monitorInfo.setHasNew(true);
        monitorInfo.setItemCount(ITEM_COUNT);
        monitorInfo.setMailBadge(true);
        monitorInfo.setManagementTurnoverAvailable(true);
        monitorInfo.setTweetAvailable(true);
        monitorInfo.setOwnedBy(OWNED_BY);
        monitorInfo.setFavorite(true);
        return monitorInfo;
    }

    private static Entity createEntity() {
        Entity ent = new Entity();
        ent.setId(CLUSTER_IDS_CSV);
        ent.setName(ENT_NAME);
        ent.setCompanyId(ENT_COMPANY_ID);
        ent.setVolumeRecentWeek(VOL_RECENT_WEEK);
        ent.setStatus(ACTIVE);
        ent.setCompanyLogoUrl(COMP_LOGO_URL);
        ent.setHasTweet(true);
        ent.setSearchToken(SEARCH_TOKEN);
        ent.setAdditionalMatchQualifier(true);
        ent.setTicker(TICKER);
        ent.setAdded(true);
        ent.setMatchedType(MATCHED_TYPE);
        ent.setRelevanceScore(RELEVANCE_SCORE);
        ent.setDnbEntityId(DNB_COMPANY_ID);
        ent.setSelected(true);
        ent.setOneDayDocCount(ONE_DAY_DOC_COUNT);
        return ent;
    }

    private MonitorConfig createMonitorConfig() {
        MonitorConfig config = new MonitorConfig();
        config.setOwnedBy(OWNED_BY);
        config.setOwnedByType(OWNED_BY_TYPE);
        config.setMonitorName(MONITOR_NAME);
        config.setMonitorId(String.valueOf(MONITOR_ID));
        config.setFilters(Collections.singletonList(FILTER_STRING_VALUE));
        config.setQueries(Collections.singletonList(createItemDetail()));
        return config;
    }

    private ItemDetail createItemDetail() {
        ItemDetail detail = new ItemDetail();
        detail.setQueryString(QUERY_STR);
        detail.setQueryName(QUERY_NAME);
        return detail;
    }

    private MonitorBriefDetail createMonitorBriefDetail() {
        MonitorBriefDetail briefDetail = new MonitorBriefDetail();
        BaseSet baseSet = createBaseSet(briefDetail);
        arrangeData(baseSet, briefDetail);
        briefDetail.setTweetList(createTweetSet());
        return briefDetail;
    }

    private void arrangeData(final BaseSet baseSet, final MonitorBriefDetail briefDetail) {
        briefDetail.setPerfStats(baseSet);
        briefDetail.setOwnedBy(OWNED_BY);
        briefDetail.setOwnedByType(OWNED_BY_TYPE);
        VisualizationData data = new VisualizationData();
        data.setEntity(entityt);
        VisualizationData.Graph graphLocal = new VisualizationData.Graph();
        VisualizationData.Node nodeLocal = new VisualizationData.Node();
        VisualizationData.Node nodeSubTree = new VisualizationData.Node();
        nodeSubTree.id = "14";
        nodeSubTree.intensity = 100;
        nodeSubTree.value = 1;
        nodeSubTree.label = "label";
        nodeLocal.subtree = Collections.singletonList(nodeSubTree);
        graphLocal.nodes = Collections.singletonList(nodeLocal);
        Map<VisualizationData.ChartType, VisualizationData.Graph> graphMap = new
                HashMap<VisualizationData.ChartType, VisualizationData.Graph>();
        graphMap.put(VisualizationData.ChartType.TREE_MONITOR_SEARCH, graphLocal);
        graphMap.put(VisualizationData.ChartType.TREE_COMPANY, graphLocal);
        graphMap.put(VisualizationData.ChartType.TREE_TOPICS, graphLocal);
        graphMap.put(VisualizationData.ChartType.ACC_METER, graphLocal);
        graphMap.put(VisualizationData.ChartType.GEO_WORLD, graphLocal);
        graphMap.put(VisualizationData.ChartType.GEO_US, graphLocal);
        data.graphs = graphMap;
        briefDetail.setVisualizationData(data);
        briefDetail.setWebResults(createDocumentSet());
    }

    private BaseSet createBaseSet(final MonitorBriefDetail briefDetail) {
        briefDetail.setMonitorId(String.valueOf(MONITOR_ID));
        briefDetail.setMonitorName(MONITOR_NAME);
        BaseSet baseSet = new BaseSet();
        baseSet.setHasMore(true);
        baseSet.setSectionType(BaseSet.SectionType.AC);
        baseSet.setTotalCount(TOTAL_COUNT);
        return baseSet;
    }

    private com.firstrain.frapi.domain.Tweet createDomainTweet() {
        com.firstrain.frapi.domain.Tweet tweet = new com.firstrain.frapi.domain.Tweet();
        tweet.setTweet(TWEET_TEXT);
        tweet.setIndustries(Collections.singletonList(createEntity()));
        tweet.setCoreTweet(CORE_TWEET);
        tweet.setTweetId(TWEET_ID);
        tweet.setScreenName(SCREEN_NAME);
        tweet.setTitle(AUTHOR_TITLE);
        tweet.setUserImage(USER_IMAGE);
        tweet.setComboScore(COMBO_SCORE);
        tweet.setBookmarked(true);
        tweet.setEntity(createEntity());
        tweet.setTweetCreationDate(new Date());
        return tweet;
    }

    private TweetSet createTweetSet() {
        TweetSet tweetSet = new TweetSet();
        tweetSet.setTotal(TWEET_TOTAL);
        tweetSet.setTweets(Collections.singletonList(createDomainTweet()));
        tweetSet.setSectionType(BaseSet.SectionType.FR);
        return tweetSet;
    }

    private DocumentSet createDocumentSet() {
        DocumentSet documentSet = new DocumentSet();
        documentSet.setFiling(true);
        documentSet.setCaption(DOC_CAPTION);
        documentSet.setScope(DOC_SCOPE);
        documentSet.setPrimaryIndustry(true);
        Map<String, List<Document>> listMap = new HashMap<String, List<Document>>();
        listMap.put("key", Collections.singletonList(createDocument()));
        documentSet.setDocumentBucket(listMap);
        documentSet.setDocuments(Collections.singletonList(createDocument()));
        return documentSet;
    }

    private Document createDocument() {
        Document document = new Document();
        Document.DocQuote docQuote = new Document.DocQuote();
        docQuote.setText(DOC_QUOTE_TEXT);
        docQuote.setPerson(DOC_QUOTE_PERSON);
        document.setDocQuote(docQuote);
        document.setCompanyTeam(CompanyTeam.EXECUTIVE);
        document.setLoginRequired(true);
        document.setCompanyPeople(true);
        document.setCatEntries(Collections.singletonList(createEntity()));
        document.setDate(new Date());
        document.setGroupId(GROUP_ID);
        document.setScore(DOC_SCORE);
        document.setBookmarked(true);
        document.setContentType(ContentType.WEBNEWS);
        document.setId(DOCUMENT_ID);
        document.setCompanyTeam(CompanyTeam.EXECUTIVE);
        document.setItemId(ITEM_ID);
        document.setPrimaryEntity(primaryEntity);
        document.setTitle(AUTHOR_TITLE);
        document.setSummary(DOCUMENT_SUMMARY);
        document.setSlot(DOC_SLOT);
        document.setUrl(DOC_URL);
        document.setSource(statusEntity);
        document.setAdditionalMatchQualifierStr(ADDITIONAL_MATCH_QUALIFIER_STR);
        document.setPrimaryDunsMatchStr(PRIMARY_DUNS_MATCHER_STR);
        return document;
    }

    private EntityBriefInfo createEntityBrief(int statusCode) {
        EntityBriefInfo briefInfo = createEntityBriefInfo(statusCode);
        return arrangeData(briefInfo);
    }

    private EntityBriefInfo arrangeData(final EntityBriefInfo briefInfo) {
        briefInfo.setTweetList(createTweetSet());
        briefInfo.setWebResults(createDocumentSet());
        briefInfo.setEventsTimeline(createEvenSet());
        briefInfo.setAnalystComments(createDocumentSet());
        briefInfo.setMgmtChangeEvents(createEvenSet());
        Graph graphLocal = new Graph();
        graphLocal.setBasefontsize(BASE_FONTS_SIZE);
        HistoricalStat stat = new HistoricalStat();
        CompanyTradingRange range = new CompanyTradingRange();
        range.setOpeningPrice(OPENING_PRICE);
        range.setDiffId(DIFF_ID);
        range.setClosingPrice(CLOSING_PRICE);
        stat.setTradeRange(range);
        CompanyVolume volume = new CompanyVolume();
        volume.setTotal(DIFF_ID);
        volume.setDate(new Timestamp(802238L));
        stat.setCompanyVolume(volume);
        ArrayList<HistoricalStat> stats = new ArrayList<HistoricalStat>();
        stats.add(stat);
        graphLocal.setHistoricalStat(stats);
        graphLocal.setEDate(new Date());
        graphLocal.setSectionType(BaseSet.SectionType.FR);
        briefInfo.setWebVolumeGraph(graphLocal);
        return briefInfo;
    }

    private EntityBriefInfo createEntityBriefInfo(final int statusCode) {
        EntityBriefInfo briefInfo = new EntityBriefInfo();
        briefInfo.setEntity(createEntity());
        briefInfo.setEntityMap(createEntityMap());
        briefInfo.setMatchedEntity(Collections.singletonList(createEntity()));
        briefInfo.setStatusCode(statusCode);
        briefInfo.setScopeDirective(SCOP_DIRECTIVE);
        briefInfo.setMgmtTurnoverData(new MgmtTurnoverData());
        return briefInfo;
    }

    private EntityMap createEntityMap() {
        com.firstrain.frapi.domain.Entity entLocal = new com.firstrain.frapi.domain.Entity();
        entLocal.searchToken = SEARCH_TOKEN;
        entLocal.id = ENT_ID;
        entLocal.ticker = PRIMARY_TICKER;
        entLocal.setName(ENT_NAME);
        entLocal.setId(ENT_ID);
        entLocal.setState(ENT_STATE);
        entLocal.setAddress(ENT_ADDRESS);
        entLocal.setCity(ENT_CITY);
        entLocal.setAddress(ENT_ADDRESS);
        entLocal.setCountry(ENT_COUNTRY);
        entLocal.setWebsite(ENT_WEBSITE);
        entLocal.setZip(ENT_ZIP);
        entLocal.setTicker(PRIMARY_TICKER);
        entLocal.setDomain(ENT_DOMAIN);
        EntityMap map = new EntityMap();
        map.setEntity(entLocal);
        map.setIndustry(entLocal);
        map.setSector(entLocal);
        map.setSegment(entLocal);
        map.setBizlines(Collections.singletonList(entLocal));
        map.setCountry(entLocal);
        map.setCompanyLogo(COMP_LOGO);
        map.setDomain(ENT_DOMAIN);
        return map;
    }

    private Event createEvent() {
        Event event = new Event();
        event.setEntityInfo(createEntity());
        event.setEntityId(Long.parseLong(ENT_ID));
        event.setEventTypeId(EVENT_TYPE_ID);
        event.setSecFormType(SEC_FORM_TYPE);
        event.setBookmarked(true);
        event.setReportDate(new Date());
        event.setPersonImage(PERSON_IMG);
        event.setOldRegion(OLD_REGION);
        event.setOldCompany(createEntity());
        event.setNewRegion(NEW_REGION);
        event.setNewDesignation(NEW_DESIGNATION);
        event.setEventInformationEnum(DefaultEnums.EventInformationEnum.DEFAULT);
        event.setEventId(EVENT_ID);
        event.setTitle(EVENT_TITLE);
        event.setCaption(EVENT_CAPTION);
        event.setDescription(EVENT_DESC);
        event.setTrigger(true);
        event.setDate(new Date());
        event.setUrl(EVENT_URL);
        event.setFlag(EVENT_FLAG);
        event.setEntityInfo(createEntity());
        event.setScore(EVENT_SCORE);
        event.setLinkable(true);
        event.setLink(EVENTSET_LINK);
        event.setEntityId(Long.parseLong(ENT_ID));
        WebVolumeEventMeta eventMeta = new WebVolumeEventMeta();
        eventMeta.setRelatedDocument(createDocumentSet());
        event.setWvMeta(eventMeta);
        return event;
    }

    private EventSet createEvenSet() {
        EventSet set = new EventSet();
        set.setScope(EVENT_SCOPE);
        set.setEventType(EventSet.EventType.RELATED_COMPANIES_EVENTS);
        set.setPrimaryIndustry(true);
        set.setLink(EVENTSET_LINK);
        set.setUnfilteredDataSize(UNFILTERED_DATA_SIZE);
        Event eventLocal = createEvent();
        List<Event> events = new ArrayList<Event>();
        events.add(eventLocal);
        set.setEvents(events);
        Map<String, List<Event>> listMap = new HashMap<String, List<Event>>();
        listMap.put("key", events);
        set.setEventBuckets(listMap);
        return set;
    }
}
