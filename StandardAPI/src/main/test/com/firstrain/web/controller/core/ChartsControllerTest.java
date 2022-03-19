package com.firstrain.web.controller.core;

import com.firstrain.frapi.FRCompletionService;
import com.firstrain.frapi.domain.CompanyTradingRange;
import com.firstrain.frapi.domain.CompanyVolume;
import com.firstrain.frapi.domain.HistoricalStat;
import com.firstrain.frapi.domain.MgmtChart;
import com.firstrain.frapi.domain.MgmtTurnoverData;
import com.firstrain.frapi.domain.User;
import com.firstrain.frapi.pojo.AuthAPIResponse;
import com.firstrain.frapi.pojo.EnterprisePref;
import com.firstrain.frapi.pojo.EntityBriefInfo;
import com.firstrain.frapi.pojo.Graph;
import com.firstrain.frapi.pojo.wrapper.BaseSet;
import com.firstrain.frapi.repository.EntityBaseServiceRepository;
import com.firstrain.frapi.repository.impl.DnbRepositoryImpl;
import com.firstrain.frapi.repository.impl.EntityBaseServiceRepositoryImpl;
import com.firstrain.frapi.service.impl.DnbServiceImpl;
import com.firstrain.frapi.service.impl.EntityBriefServiceImpl;
import com.firstrain.frapi.util.ConvertUtil;
import com.firstrain.frapi.util.DefaultEnums;
import com.firstrain.frapi.util.StatusCode;
import com.firstrain.obj.IEntityInfo;
import com.firstrain.obj.IEntityInfoCache;
import com.firstrain.solr.client.SearchTokenEntry;
import com.firstrain.solr.client.util.SolrServerReader;
import com.firstrain.utils.JSONUtility;
import com.firstrain.web.response.JSONResponse;
import com.firstrain.web.service.core.RequestParsingService;
import com.firstrain.web.service.core.ResponseDecoratorService;
import com.firstrain.web.util.AuthAPIResponseThreadLocal;
import com.firstrain.web.util.UserInfoThreadLocal;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
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
import org.powermock.reflect.Whitebox;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.servlet.http.HttpServletResponse;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Matchers.eq;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.powermock.reflect.internal.WhiteboxImpl.setInternalState;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        ChartsController.class,
        AuthAPIResponseThreadLocal.class,
        UserInfoThreadLocal.class,
        JSONUtility.class,
        DnbRepositoryImpl.class,
        SolrServerReader.class,
        FRCompletionService.class,
        EntityBriefServiceImpl.class
})
public class ChartsControllerTest {

    private static final String OWNED_BY_TYPE = "USER";
    private static final String USER_COMPANY = "userCompany";
    private static final long FR_MONITOR_ID = 97L;
    private static final String USER_LAST_NAME = "lastname";
    private static final String USER_FIRST_NAME = "firstname";
    private static final String USR_DOMAIN = "domain";
    private static final String MONITOR_ORDER_TYPE = "orderType";
    private static final long TEMPLATE_ID = 99L;
    private static final Timestamp versionStartDate = new Timestamp(790L);
    private static final int API_THRE_SOLD = 99;
    private static final Timestamp versionEndDate = new Timestamp(900L);
    private static final String PREF_JSON = "json";
    private static final String INCLUDED_API = "included";
    private static final long AUTH_ID = 77L;
    private static final Timestamp EXPIRY_TIME = new Timestamp(100L);
    private static final String EXCLUDED_API = "excluded";
    private static final long ENTERPRISE_ID = 111L;
    private static final String API_VERSION = "3.5.6";
    private static final String AUTH_KEY = "auth_key";
    private static final String SEARCH_TOKEN = "222";
    private static final String INDUSTRY_CLASSIFICATION_ID_VAL = "1241";
    private static final String CUSTOMIZED_CSS_FILE_NAME_VAL = "98798";
    private static final String IS_DUNS_SUPPORTED_VAL = "true";
    private static final String PRIVATE_SOURCE_IDS_VAL = "45354";
    private static final String PUBLIC_SOURCE_IDS_VAL = "9765";
    private static final String SEARCHES_PER_MONITOR_VAL = "647";
    private static final long DOC_START = 100L;
    private static final float MAX_SCORE = 2.2f;
    private static final long NUM_FOUND = 300L;
    private static final String ATTR_COMPANY_ID = "111";
    private static final String DNB_COMP_ID = "222";
    private static final String ATTR_COUNTRY = "333";
    private static final String ENT_NAME = "entComp";
    private static final int SECTOR_CAT_ID = 98798;
    private static final String ENT_ID = "9543";
    private static final int ENT_COMPANY_ID = 777;
    private static final String PRIMARY_TICKER = "ticker";
    private static final int ENT_SCOPE = 666;
    private static final int DIFF_ID = 123;
    private static final double VOLUME_TOTAL = 12345;
    private static final double CLOSING_PRICE = 11.0;
    private static final double OPENING_PRICE = 45.0;
    private static final String SCOP_DIRECTIVE = "directive";
    private static final String ERROR = "ERROR";
    @InjectMocks
    public final ChartsController chartsController = new ChartsController();
    @Spy
    private final EntityBaseServiceRepository entityBaseServiceRepository = new EntityBaseServiceRepositoryImpl();
    @Spy
    private final RequestParsingService requestParsingService = new RequestParsingService();
    @Spy
    private final ResponseDecoratorService responseDecoratorService = new ResponseDecoratorService();
    @Spy
    private final EntityBriefServiceImpl entityBriefService = new EntityBriefServiceImpl();
    @Spy
    private final DnbServiceImpl dnbService = new DnbServiceImpl();
    @Spy
    private final DnbRepositoryImpl dnbRepository = new DnbRepositoryImpl();
    @Spy
    private final ConvertUtil convertUtil = new ConvertUtil();
    @Mock
    private HttpServletResponse response;
    @Mock
    private UserInfoThreadLocal userInfoThreadLocal;
    @Mock
    private IEntityInfoCache entityInfoCache;
    @Mock
    private IEntityInfo entityInfo;
    @Mock
    private FRCompletionService<BaseSet> completionService;
    @Mock
    protected ThreadPoolTaskExecutor taskExecutor;
    @Mock
    private ThreadPoolExecutor theadPoolExecutor;
    @Mock
    private Future<BaseSet> baseSetFuture;
    private final ErrorCollector errorCollector;
    private final ExpectedException expectedException;
    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector = new ErrorCollector())
            .around(expectedException = ExpectedException.none());

    @Before
    public void setUp() throws Exception {
        mockStatic(AuthAPIResponseThreadLocal.class,
                UserInfoThreadLocal.class,
                JSONUtility.class,
                SolrServerReader.class,
                FRCompletionService.class);
        when(SolrServerReader.retrieveNSolrDocs(any(SolrServer.class),
                anyString(), anyInt(), anyInt(), (String[]) anyVararg()))
                .thenReturn(createSolrDocuments());
        Graph graph = new Graph();
        graph.setSectionType(BaseSet.SectionType.WV);
        when(baseSetFuture.get()).thenReturn(graph);
        when(completionService.poll(anyLong(), any(TimeUnit.class)))
                .thenReturn(baseSetFuture);
        when(completionService.getSubmissions()).thenReturn(10);
        Whitebox.invokeMethod(requestParsingService, "init");
        Map<String, Object> objectMap = new HashMap<String, Object>();
        objectMap.put(RequestParsingService.defaultSpec.INDUSTRY_CLASSIFICATION_ID,
                INDUSTRY_CLASSIFICATION_ID_VAL);
        objectMap.put(RequestParsingService.defaultSpec.CUSTOMIZED_CSS_FILE_NAME,
                CUSTOMIZED_CSS_FILE_NAME_VAL);
        objectMap.put(RequestParsingService.defaultSpec.IS_DUNS_SUPPORTED,
                IS_DUNS_SUPPORTED_VAL);
        objectMap.put(RequestParsingService.defaultSpec.PRIVATE_SOURCE_IDS,
                PRIVATE_SOURCE_IDS_VAL);
        objectMap.put(RequestParsingService.defaultSpec.PUBLIC_SOURCE_IDS,
                PUBLIC_SOURCE_IDS_VAL);
        objectMap.put(RequestParsingService.defaultSpec.SEARCHES_PER_MONITOR,
                SEARCHES_PER_MONITOR_VAL);
        objectMap.put(RequestParsingService.defaultSpec.INDUSTRY_CLASSIFICATION_ID,
                INDUSTRY_CLASSIFICATION_ID_VAL);
        when(JSONUtility.deserialize(anyString(), eq(Map.class)))
                .thenReturn(objectMap);
        when(taskExecutor.getThreadPoolExecutor()).thenReturn(theadPoolExecutor);
        whenNew(FRCompletionService.class).withAnyArguments().thenReturn(completionService);
        when(entityInfo.getName()).thenReturn(ENT_NAME);
        when(entityInfo.getScope()).thenReturn(ENT_SCOPE);
        when(entityInfo.getPrimaryTicker()).thenReturn(PRIMARY_TICKER);
        when(entityInfo.getSearchToken()).thenReturn(SEARCH_TOKEN);
        when(entityInfo.getSectorCatId()).thenReturn(SECTOR_CAT_ID);
        when(entityInfo.getId()).thenReturn(ENT_ID);
        when(entityInfo.getCompanyId()).thenReturn(ENT_COMPANY_ID);
        when(entityInfo.getType()).thenReturn(SearchTokenEntry.SEARCH_TOKEN_TAG_SECTOR_TOPIC);
        when(entityInfoCache.searchTokenToEntity(anyString()))
                .thenReturn(entityInfo);
        when(entityInfoCache.companyIdToEntity(anyInt()))
                .thenReturn(entityInfo);
        doReturn(entityInfoCache).when(dnbRepository).getEntityInfoCache();
        doReturn(entityInfoCache).when(entityBaseServiceRepository).getEntityInfoCache();
        setInternalState(entityBriefService, "entityBaseServiceRepository",
                entityBaseServiceRepository);
        setInternalState(entityBriefService, "taskExecutor", taskExecutor);
        setInternalState(entityBriefService, "dnbService",
                dnbService);
        setInternalState(dnbService, "dnbRepository",
                dnbRepository);
        setInternalState(dnbRepository, "convertUtil", convertUtil);
        ArrayList<HistoricalStat> historicalStats = new ArrayList<HistoricalStat>();
        HistoricalStat stat = new HistoricalStat();
        CompanyVolume companyVolume = new CompanyVolume();
        companyVolume.setDate(new Timestamp(100L));
        companyVolume.setDiffId(DIFF_ID);
        companyVolume.setTotal(VOLUME_TOTAL);
        stat.setCompanyVolume(companyVolume);
        CompanyTradingRange tradingRange = new CompanyTradingRange();
        tradingRange.setClosingPrice(CLOSING_PRICE);
        tradingRange.setDiffId(DIFF_ID);
        tradingRange.setOpeningPrice(OPENING_PRICE);
        stat.setTradeRange(tradingRange);
        historicalStats.add(stat);
        historicalStats.add(stat);
        historicalStats.add(stat);
        setInternalState(graph, "historicalStat", historicalStats);
    }

    @Test
    public void givenGetWVChartDataWhenEntityBriefInfoIsNotNullANdNotSuccessThenResponse() throws Exception {
        EntityBriefInfo pref = createEntityBrief(StatusCode.NO_ITEMS);
        doReturn(pref).when(entityBriefService)
                .getEntityBriefDetails(any(EnterprisePref.class), anyString(), anyString());
        doReturn(new EnterprisePref()).when(requestParsingService)
                .getDefaultSpec(anyBoolean(), anyBoolean());
        JSONResponse<String> responseMsg = createJsonResponse();
        doReturn(responseMsg).when(requestParsingService).getErrorResponse(anyInt());
        //Act
        String result = chartsController.getWVChartData(response, SEARCH_TOKEN);
        //Assert
        errorCollector.checkThat(result, is(ERROR));
    }

    @Test
    public void givenGetWVChartDataWhenEntityBriefIsNullThenThrowsException() throws Exception {
        //Arrange
        doReturn(null).when(entityBriefService)
                .getEntityBriefDetails(any(EnterprisePref.class), anyString(), anyString());
        doReturn(new EnterprisePref()).when(requestParsingService)
                .getDefaultSpec(anyBoolean(), anyBoolean());
        JSONResponse<String> responseMsg = createJsonResponse();
        doReturn(responseMsg).when(requestParsingService).getErrorResponse(anyInt());
        //Act
        String result = chartsController.getWVChartData(response, SEARCH_TOKEN);
        //Assert
        assertEquals(ERROR, result);
    }

    @Test
    public void getWVChartData() {
        //Arrange
        User user = createUser();
        when(UserInfoThreadLocal.get()).thenReturn(user);
        when(AuthAPIResponseThreadLocal.get()).thenReturn(createAuthAPIResponseThreadLocal());
        //Act
        String result = chartsController.getWVChartData(response, SEARCH_TOKEN);
        //Assert
        errorCollector.checkThat(result, containsString("eventSet"));
        errorCollector.checkThat(result, endsWith("</eventSet></chart>"));
        errorCollector.checkThat(result, startsWith("<chart showFirstrainLegend=\"0\" showSliders=\"1\""));
    }

    @Test
    public void givenGetMTChartDataWhenEntityBriefInfoIsStatusNotSuccessThenResponse() throws Exception {
        //Arrange
        EntityBriefInfo pref = createEntityBrief(StatusCode.NO_ITEMS);
        doReturn(pref).when(entityBriefService)
                .getEntityBriefDetails(any(EnterprisePref.class), anyString(), anyString());
        doReturn(new EnterprisePref()).when(requestParsingService)
                .getDefaultSpec(anyBoolean(), anyBoolean());
        JSONResponse<String> responseMsg = createJsonResponse();
        doReturn(responseMsg).when(requestParsingService).getErrorResponse(anyInt());        //Act
        JSONResponse<MgmtChart> jsonResponse = chartsController.getMTChartData(response, SEARCH_TOKEN);
        //Assert
        errorCollector.checkThat(jsonResponse.getMessage(), is(ERROR));
        errorCollector.checkThat(jsonResponse.getStatus(), is(JSONResponse.ResStatus.ERROR));
    }

    @Test
    public void givenGetMTChartDataWhenEntityBriefInfoIsNotNullThenResponse() throws Exception {
        EntityBriefInfo pref = createEntityBrief(StatusCode.REQUEST_SUCCESS);
        doReturn(pref).when(entityBriefService)
                .getEntityBriefDetails(any(EnterprisePref.class), anyString(), anyString());
        doReturn(new EnterprisePref()).when(requestParsingService)
                .getDefaultSpec(anyBoolean(), anyBoolean());
        JSONResponse<String> responseMsg = createJsonResponse();
        doReturn(responseMsg).when(requestParsingService).getErrorResponse(anyInt());
        //Act
        JSONResponse<MgmtChart> jsonResponse = chartsController.getMTChartData(response, SEARCH_TOKEN);
        //Assert
        errorCollector.checkThat(jsonResponse.getStatus(), is(JSONResponse.ResStatus.SUCCESS));
    }

    @Test
    public void givenGetMTChartDataWhenEntityBriefInfoIsNullThenResponse() throws Exception {
        //Arrange
        doReturn(null).when(entityBriefService)
                .getEntityBriefDetails(any(EnterprisePref.class), anyString(), anyString());
        doReturn(new EnterprisePref()).when(requestParsingService)
                .getDefaultSpec(anyBoolean(), anyBoolean());
        JSONResponse<String> responseMsg = createJsonResponse();
        doReturn(responseMsg).when(requestParsingService).getErrorResponse(anyInt());
        //Act
        JSONResponse<MgmtChart> jsonResponse = chartsController.getMTChartData(response, SEARCH_TOKEN);
        //Assert
        errorCollector.checkThat(jsonResponse.getMessage(), is(ERROR));
        errorCollector.checkThat(jsonResponse.getStatus(), is(JSONResponse.ResStatus.ERROR));
    }

    private JSONResponse<String> createJsonResponse() {
        JSONResponse<String> responseMsg = new JSONResponse<String>();
        responseMsg.setStatus(JSONResponse.ResStatus.ERROR);
        responseMsg.setMessage(ERROR);
        return responseMsg;
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
        apiResponse.setVersionEndDate(versionEndDate);
        apiResponse.setApiThreshold(API_THRE_SOLD);
        apiResponse.setVersionStartDate(versionStartDate);
        return apiResponse;
    }

    private static User createUser() {
        User user = new User();
        user.setTemplateId(TEMPLATE_ID);
        user.setMonitorOrderType(MONITOR_ORDER_TYPE);
        user.setOwnedByType(OWNED_BY_TYPE);
        user.setDomain(USR_DOMAIN);
        user.setFirstName(USER_FIRST_NAME);
        user.setLastName(USER_LAST_NAME);
        user.setTimeZone("UTC");
        user.setFrMonitorId(FR_MONITOR_ID);
        user.setUserCompany(USER_COMPANY);
        user.setUserType(DefaultEnums.UserType.ABU);
        return user;
    }

    private static SolrDocumentList createSolrDocuments() {
        SolrDocumentList documents = new SolrDocumentList();
        documents.setNumFound(NUM_FOUND);
        documents.setMaxScore(MAX_SCORE);
        documents.setStart(DOC_START);
        SolrDocument solrDocument = new SolrDocument();
        solrDocument.setField("attrCompanyId", ATTR_COMPANY_ID);
        solrDocument.setField("dnbCompanyId", DNB_COMP_ID);
        solrDocument.setField("attrCountry", ATTR_COUNTRY);
        solrDocument.setField("bizLineCatIds", Collections.singletonList(100));
        documents.add(solrDocument);
        return documents;
    }

    private static EntityBriefInfo createEntityBrief(int statusCode) {
        EntityBriefInfo briefInfo = new EntityBriefInfo();
        briefInfo.setStatusCode(statusCode);
        briefInfo.setScopeDirective(SCOP_DIRECTIVE);
        briefInfo.setMgmtTurnoverData(new MgmtTurnoverData());
        return briefInfo;
    }
}
