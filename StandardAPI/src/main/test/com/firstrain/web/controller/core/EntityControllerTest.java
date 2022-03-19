package com.firstrain.web.controller.core;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyShort;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.httpclient.HttpClient;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.codehaus.jackson.JsonParseException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.Model;
import com.firstrain.content.similarity.DocumentSimilarityUtil;
import com.firstrain.frapi.domain.BaseSpec;
import com.firstrain.frapi.domain.Document;
import com.firstrain.frapi.domain.EntityMap;
import com.firstrain.frapi.domain.SectionSpec;
import com.firstrain.frapi.domain.User;
import com.firstrain.frapi.pojo.EnterprisePref;
import com.firstrain.frapi.pojo.Entity;
import com.firstrain.frapi.pojo.EntityBriefInfo;
import com.firstrain.frapi.pojo.SearchAPIResponse;
import com.firstrain.frapi.pojo.wrapper.BaseSet.SectionType;
import com.firstrain.frapi.pojo.wrapper.DocumentSet;
import com.firstrain.frapi.repository.CompanyServiceRepository;
import com.firstrain.frapi.repository.DnbRepository;
import com.firstrain.frapi.repository.SearchServiceRepository;
import com.firstrain.frapi.repository.impl.DnbRepositoryImpl;
import com.firstrain.frapi.service.CompanyService;
import com.firstrain.frapi.service.DnbService;
import com.firstrain.frapi.service.EntityBaseService;
import com.firstrain.frapi.service.EntityBriefService;
import com.firstrain.frapi.service.EntityProcessingService;
import com.firstrain.frapi.service.RestrictContentService;
import com.firstrain.frapi.service.SearchService;
import com.firstrain.frapi.service.impl.CompanyServiceImpl;
import com.firstrain.frapi.service.impl.DnbServiceImpl;
import com.firstrain.frapi.service.impl.SearchServiceImpl;
import com.firstrain.frapi.util.ConvertUtil;
import com.firstrain.frapi.util.DefaultEnums;
import com.firstrain.frapi.util.DefaultEnums.INPUT_ENTITY_TYPE;
import com.firstrain.frapi.util.ServicesAPIUtil;
import com.firstrain.obj.IEntityInfo;
import com.firstrain.obj.IEntityInfoCache;
import com.firstrain.solr.client.DocEntry;
import com.firstrain.solr.client.DocListBucket;
import com.firstrain.solr.client.SearchResult;
import com.firstrain.solr.client.util.SolrServerReader;
import com.firstrain.utils.JSONUtility;
import com.firstrain.web.pojo.ConversationStartersInputBean;
import com.firstrain.web.pojo.EntityMatchInputBean;
import com.firstrain.web.pojo.EntityMatchInputBean.EntityInput;
import com.firstrain.web.response.DnBEntityStatusResponse;
import com.firstrain.web.response.EntityDataResponse;
import com.firstrain.web.response.JSONResponse;
import com.firstrain.web.service.core.Constant;
import com.firstrain.web.service.core.ExcelProcessingHelperService;
import com.firstrain.web.service.core.FreemarkerTemplateService;
import com.firstrain.web.service.core.HttpClientService;
import com.firstrain.web.service.core.RequestParsingService;
import com.firstrain.web.service.core.ResponseDecoratorService;
import com.firstrain.web.service.core.StorageService;
import com.firstrain.web.util.UserInfoThreadLocal;
import com.firstrain.web.wrapper.EntityDataWrapper;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
    Constant.class,
    SolrServerReader.class
})
public class EntityControllerTest {

    private static final String QUERY_2 = "q2";
    private static final String QUERY_1 = "q1";
    private static final int LENGTH = 20;
    private static final int START = 0;
    private static final String REGION_NAME = "region name";
    private static final String INDUSTRY_NAME = "industry name";
    private static final String TOPIC_NAME = "topic name";
    private static final String ATTR_WEBSITE = "attrWebsite";
    private static final String ATTR_ADDRESS = "attrAddress";
    private static final String HOME_PAGE = "home page";
    private static final String COMAPNY_NAME = "comapny";
    private static final String INVALID_BODY = "invalid body";
    private static final String RESULT_CSV_WITH_H = "H";
    private static final String ENDS_DATE = "2018-09-05";
    private static final String START_DATE = "2018-09-01";
    private static final String DOCUMENT_ID = "document id";
    private static final String SECTION = "section";
    private static final String SCOPE_DIRECTIVE = "scopeDirective";
    private static final String RESULTS_CSV = "resultsCSV:DMH";
    private static final String HTML_FRAG = "htmlFragclassicFrame";
    private static final String METADATA = "metadata";
    private static final String SEARCH_TOKEN = "searchToken";
    private static final String HTML_RESPONSE = "html";
    private static final int STATUS_CODE_OK = 200;
    private static final String MSG_KEY = "gen.succ";
    private static final short INDUSTRY_CLASSIFICATION_ID = (short) 1;
    private static final String TARGET = "target";
    private static final String SHOW_HEADING = "showheading";
    private static final String LOAD_VIEW = "loadview";
    private static final String TRUE = "true";
    private static final String FALSE = "false";
    private static final String MT_SECTIONS = "{te}";
    private static final String TP_USER_INFO = "tpUserInfo";
    private static final String SECTIONS = "io:";
    private static final String FQ = "fq";
    private static final String LAYOUT = "layout";
    private static final long USET_ID_LONG = 2L;
    private static final Short COUNT = 1;

    @Mock
    private Model model;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FreemarkerTemplateService freemarkerTemplateService;
    @Mock
    private RequestParsingService requestParsingService;
    @Mock
    private EnterprisePref enterprisePref;
    @Mock
    private EntityBriefService entityBriefService;
    @Mock
    private EntityBaseService entityBaseService;
    @Mock
    private EntityBriefInfo entityBriefInfo;
    @Mock
    private ResponseDecoratorService responseDecoratorService;
    @Mock
    private EntityDataResponse entityDataResponse;
    @Mock
    private Entity entity;
    @Mock
    private HttpClient httpClient;
    @Mock
    private CompanyServiceRepository companyServiceRepository;
    private HttpClientService httpClientService = new HttpClientService();
    private StorageService storageService = new StorageService();
    private CompanyService companyService = new CompanyServiceImpl();
    private SearchService searchService = new SearchServiceImpl();
    private ConvertUtil convertUtil = new ConvertUtil();
    private ServicesAPIUtil servicesAPIUtil = new ServicesAPIUtil();
    @Spy
    private DnbService dnbService = new DnbServiceImpl();

    private DnbRepository dnbRepository = new DnbRepositoryImpl();
    @Mock
    private SearchServiceRepository searchServiceRepository;
    @Mock
    private IEntityInfoCache entityInfoCache;
    @Mock
    private IEntityInfo entityInfo;
    @Mock
    private DocListBucket bucket;
    @Mock
    private RestrictContentService restrictContentService;
    @Mock
    private DocumentSimilarityUtil util;
    @Mock
    private EntityProcessingService entityProcessingService;
    @Mock
    private ExcelProcessingHelperService excelProcessingHelperService;
    @InjectMocks
    private EntityController entityController;

    @Rule
    public final ErrorCollector collector = new ErrorCollector();

    @Captor
    private ArgumentCaptor<Map<String, Object>> mapArgumentCaptor;
    @Captor
    private ArgumentCaptor<Object> objectArgumentCaptor;

    private Map<String, Object> ftlParams = null;
    private Map<SectionType, SectionSpec> sectionsMap = new HashMap<SectionType, SectionSpec>();
    private Set<SectionType> keySet;
    private boolean blendDUNS = true;
    private boolean debug = true;
    private DocEntry entry;

    @Before
    public void setUp() throws Exception {
        mockTheStaticClasses();
        populateSectionsMap();
        addUserInLocalThread();
        mockDocumentSet();
        mockRequestParsingService();
        mockEntityBriefService();
        mockResponseDecoratorService();
        when(enterprisePref.getSectionsMap()).thenReturn(sectionsMap);
        when(entity.getSearchToken()).thenReturn(SEARCH_TOKEN);
        when(freemarkerTemplateService.getHtml(anyString(), any(Map.class))).thenReturn(HTML_RESPONSE);

        EntityDataWrapper value = new EntityDataWrapper();
        when(entityDataResponse.getResult()).thenReturn(value);
        List<Entity> entities = Collections.singletonList(entity);

        when(entityBaseService.autoSuggestForEntity(anyString(), anyString(), anyInt(), anyString(), anyMap()))
                .thenReturn(entities);
        mockSolrDocumentList();
        mockForSearchResult();
        when(restrictContentService.getAllHiddenContent(anyLong(), anyString())).thenReturn(ATTR_ADDRESS);
        mockForSearchServiceRepository();
        setRequiredField();
    }

    @Test
    public void getResponseInHtml() throws Exception {
        // Arrange
        String htmlFrag = "classicFrame";
        when(requestParsingService.getSerializedMetadata(SECTIONS, null, FQ, null, htmlFrag, SEARCH_TOKEN, false, false,
                blendDUNS)).thenReturn(METADATA);

        // Act
        entityController.getResponseInHtml(model, request, response, SECTIONS, LAYOUT, htmlFrag, FQ, debug,
                SEARCH_TOKEN, TP_USER_INFO, blendDUNS);

        // Act
        validateFtlParam(SHOW_HEADING, TRUE);
        validateFtlParam("fq", FQ);
        validateFtlParam("showmore", true);
        validateFtlParam("fr_paginationcount", Constant.FR_PAGINATION_COUNT);
        validateFtlParam("ft_paginationcount", Constant.FT_PAGINATION_COUNT);
        validateFtlParam("ac_paginationcount", Constant.AC_PAGINATION_COUNT);
        validateFtlParam("te_paginationcount", Constant.TE_PAGINATION_COUNT);
        validateFtlParam("e_paginationcount", Constant.E_PAGINATION_COUNT);
        validateFtlParam("searchToken", SEARCH_TOKEN);
        validateModelAttribute("htmlContent", HTML_RESPONSE);

        validateRequestAttribute(LOAD_VIEW, true);
        validateRequestAttribute("activityType", LOAD_VIEW);
        validateRequestAttribute("targetId", SEARCH_TOKEN);
        validateRequestAttribute("view", "entitybrief" + LAYOUT);
        validateRequestAttribute("viewId", SEARCH_TOKEN);
        validateRequestAttribute(TARGET, "entitybriefwithpagingandfilteringsupport");
    }

    @Test
    public void getResponseInHtmlWithNonClassicHtmlFrag() throws Exception {
        // Arrange
        String htmlFrag = "nonClassicFrame";
        String otherSections = "test:";
        when(requestParsingService.getSerializedMetadata(otherSections, null, FQ, null, htmlFrag, SEARCH_TOKEN, false,
                false, blendDUNS)).thenReturn(METADATA);
        when(requestParsingService.getSectionsPageSpecMap(otherSections, true, true)).thenReturn(enterprisePref);

        // Act
        entityController.getResponseInHtml(model, request, response, otherSections, LAYOUT, htmlFrag, FQ, debug,
                SEARCH_TOKEN, TP_USER_INFO, blendDUNS);

        // Act
        validateFtlParam(SHOW_HEADING, FALSE);
        validateRequestAttribute(TARGET, "entitybriefwithfilteringsupport");
    }

    @Test
    public void getResponseInHtmlWithFqNull() throws Exception {
        // Arrange
        String htmlFrag = "classicFrame";
        when(requestParsingService.getSerializedMetadata(SECTIONS, null, null, null, htmlFrag, SEARCH_TOKEN, false,
                false, blendDUNS)).thenReturn(METADATA);
        when(entityBriefService.getEntityBriefDetails(enterprisePref, SEARCH_TOKEN, null, null, blendDUNS))
                .thenReturn(entityBriefInfo);

        // Act
        entityController.getResponseInHtml(model, request, response, SECTIONS, LAYOUT, htmlFrag, null, debug,
                SEARCH_TOKEN, TP_USER_INFO, blendDUNS);

        // Act
        validateFtlParam(SHOW_HEADING, TRUE);
        validateRequestAttribute(TARGET, "entitybriefwithpagingsupport");
    }

    @Test
    public void getMTResponseInHtml() throws Exception {
        // Arrange
        String htmlFrag = "classicFrame";
        String startDate = "2012-01-31";
        String endDate = "2012-02-02";
        when(requestParsingService.getSectionsPageSpecMap(MT_SECTIONS, true, true)).thenReturn(enterprisePref);
        when(requestParsingService.getSerializedMetadata(MT_SECTIONS, null, FQ, null, htmlFrag, SEARCH_TOKEN))
                .thenReturn(METADATA);

        // Act
        entityController.getMTResponseInHtml(model, request, response, startDate, endDate, (short) 5, LAYOUT, htmlFrag,
                FQ, debug, SEARCH_TOKEN, TP_USER_INFO);

        // Act
        validateFtlParam(SHOW_HEADING, TRUE);
        validateFtlParam("fq", FQ);
        validateFtlParam("showmore", true);
        validateFtlParam("fr_paginationcount", Constant.FR_PAGINATION_COUNT);
        validateFtlParam("ft_paginationcount", Constant.FT_PAGINATION_COUNT);
        validateFtlParam("ac_paginationcount", Constant.AC_PAGINATION_COUNT);
        validateFtlParam("te_paginationcount", Constant.TE_PAGINATION_COUNT);
        validateFtlParam("e_paginationcount", Constant.E_PAGINATION_COUNT);
        validateFtlParam("searchToken", SEARCH_TOKEN);
        validateModelAttribute("htmlContent", HTML_RESPONSE);

        validateRequestAttribute(LOAD_VIEW, true);
        validateRequestAttribute("activityType", LOAD_VIEW);
        validateRequestAttribute("targetId", SEARCH_TOKEN);
        validateRequestAttribute("view", "entitybrief" + LAYOUT);
        validateRequestAttribute("viewId", SEARCH_TOKEN);
        validateRequestAttribute(TARGET, "entitybriefwithfilteringsupport");
    }

    @Test
    public void givenNullSectionsWhenGetResponseInAllFormatThenError() {
        // Arrange
        JSONResponse expected = new JSONResponse();
        when(requestParsingService.getErrorResponse(anyInt())).thenReturn(expected);
        // Act
        JSONResponse actual = entityController.getResponseInAllFormat(request, response, SEARCH_TOKEN, null, FQ, debug,
                HTML_FRAG, RESULTS_CSV, TP_USER_INFO, SCOPE_DIRECTIVE, false, false, blendDUNS);
        // Assert
        assertEquals(expected, actual);
        verify(requestParsingService).getErrorResponse(anyInt());
    }

    @Test
    public void givenNullEntityBriefDetailsFromServiceWhenGetResponseInAllFormatThenError() {
        // Arrange
        JSONResponse expected = new JSONResponse();
        when(requestParsingService.getErrorResponse(anyInt())).thenReturn(expected);
        JSONResponse actual = entityController.getResponseInAllFormat(request, response, SEARCH_TOKEN, SECTION, FQ,
                debug, HTML_FRAG, RESULTS_CSV, TP_USER_INFO, SCOPE_DIRECTIVE, false, false, blendDUNS);
        // Assert
        assertEquals(expected, actual);
        verify(requestParsingService).getErrorResponse(anyInt());
    }

    @Test
    public void givenBriefDetailsNotSuccessWhenGetResponseInAllFormatThenError() throws Exception {
        // Arrange
        JSONResponse expected = new JSONResponse();
        when(requestParsingService.getErrorResponse(anyInt())).thenReturn(expected);
        EntityBriefInfo briefInfo = new EntityBriefInfo();
        when(entityBriefService.getEntityBriefDetails(enterprisePref, SEARCH_TOKEN, FQ, SCOPE_DIRECTIVE, blendDUNS))
                .thenReturn(briefInfo);
        JSONResponse actual = entityController.getResponseInAllFormat(request, response, SEARCH_TOKEN, SECTION, FQ,
                debug, HTML_FRAG, RESULTS_CSV, TP_USER_INFO, SCOPE_DIRECTIVE, false, false, blendDUNS);
        // Assert
        assertEquals(expected, actual);
        verify(requestParsingService).getErrorResponse(anyInt());
    }

    @Test
    public void givenBriefSuccessAndDataResponseWhenGetResponseInAllFormatThenGetResponseData() throws Exception {
        // Arrange
        JSONResponse expected = new JSONResponse();
        when(requestParsingService.getErrorResponse(anyInt())).thenReturn(expected);
        EntityBriefInfo briefInfo = new EntityBriefInfo();
        briefInfo.setStatusCode(STATUS_CODE_OK);
        when(entityBriefService.getEntityBriefDetails(enterprisePref, SEARCH_TOKEN, FQ, SCOPE_DIRECTIVE, blendDUNS))
                .thenReturn(entityBriefInfo);

        JSONResponse actual = entityController.getResponseInAllFormat(request, response, SEARCH_TOKEN, SECTION, FQ,
                debug, HTML_FRAG, RESULTS_CSV, TP_USER_INFO, SCOPE_DIRECTIVE, true, false, blendDUNS);
        // Assert
        assertEquals(entityDataResponse, actual);
    }

    @Test
    public void givenStartAndEndsDateWhenGetMTResponseInAllFormatThenGetResponse() {
        // Arrange
        when(requestParsingService.getSerializedMetadata(MT_SECTIONS, null, FQ, RESULT_CSV_WITH_H, HTML_FRAG))
                .thenReturn(METADATA);
        // Act
        JSONResponse actual = entityController.getMTResponseInAllFormat(request, response, SEARCH_TOKEN, START_DATE,
                ENDS_DATE, COUNT, FQ, debug, HTML_FRAG, RESULT_CSV_WITH_H, TP_USER_INFO);
        // Assert
        assertEquals(entityDataResponse, actual);
    }

    @Test
    public void givenEndsDateAndEmptyStartDateWhenGetMTResponseInAllFormatThenGetResponse() {
        // Arrange
        when(requestParsingService.getSerializedMetadata(MT_SECTIONS, null, FQ, RESULT_CSV_WITH_H, HTML_FRAG))
                .thenReturn(METADATA);
        // Act
        JSONResponse actual = entityController.getMTResponseInAllFormat(request, response, SEARCH_TOKEN, "", ENDS_DATE,
                COUNT, FQ, debug, HTML_FRAG, RESULT_CSV_WITH_H, TP_USER_INFO);
        // Assert
        assertEquals(entityDataResponse, actual);
    }

    @Test
    public void givenStartDateAndEmptyEndsDateWhenGetMTResponseInAllFormatThenGetResponse() {
        // Arrange
        when(requestParsingService.getSerializedMetadata(MT_SECTIONS, null, FQ, RESULT_CSV_WITH_H, HTML_FRAG))
                .thenReturn(METADATA);
        // Act
        JSONResponse actual = entityController.getMTResponseInAllFormat(request, response, SEARCH_TOKEN, START_DATE,
                null, COUNT, FQ, debug, HTML_FRAG, RESULT_CSV_WITH_H, TP_USER_INFO);
        // Assert
        assertEquals(entityDataResponse, actual);
    }

    @Test
    public void givenNullEndsDateAndStartDateWhenGetMTResponseInAllFormatThenGetResponse() {
        // Arrange
        when(requestParsingService.getSerializedMetadata(MT_SECTIONS, null, FQ, RESULT_CSV_WITH_H, HTML_FRAG))
                .thenReturn(METADATA);
        // Act
        JSONResponse actual = entityController.getMTResponseInAllFormat(request, response, SEARCH_TOKEN, null, null,
                COUNT, FQ, debug, HTML_FRAG, RESULT_CSV_WITH_H, TP_USER_INFO);
        // Assert
        assertEquals(entityDataResponse, actual);
    }

    @Test
    public void givenNullBodyWhenGetEntityMatchThenErrorResponse() {
        // Arrange
        JSONResponse expected = new JSONResponse();
        when(requestParsingService.getErrorResponse(anyInt())).thenReturn(expected);
        // Act
        JSONResponse actual = entityController.getEntityMatch(request, response, null);
        // Assert
        assertEquals(expected, actual);
        verify(requestParsingService).getErrorResponse(anyInt());
    }

    @Test
    public void givenInvalidBodyWhenGetEntityMatchThenErrorResponse() {
        // Arrange
        JSONResponse expected = new JSONResponse();
        when(requestParsingService.getErrorResponse(anyInt())).thenReturn(expected);
        // Act
        JSONResponse actual = entityController.getEntityMatch(request, response, INVALID_BODY);
        // Assert
        assertEquals(expected, actual);
        verify(requestParsingService).getErrorResponse(anyInt());
    }

    @Test
    public void givenExceptionInGetQWhenGetEntityMatchThenErrorResponse() throws Exception {
        // Arrange
        JSONResponse expected = new JSONResponse();
        when(requestParsingService.getErrorResponse(anyInt())).thenReturn(expected);
        when(requestParsingService.getQForEntityMatch(any(EntityMatchInputBean.class))).thenThrow(new Exception());
        EntityMatchInputBean matchInputBean = new EntityMatchInputBean();
        EntityInput entityInput = new EntityInput();
        entityInput.setName(COMAPNY_NAME);
        matchInputBean.setCompany(entityInput);
        String reqBody = JSONUtility.serialize(matchInputBean);
        // Act
        JSONResponse actual = entityController.getEntityMatch(request, response, reqBody);
        // Assert
        assertEquals(expected, actual);
        verify(requestParsingService).getErrorResponse(anyInt());
        verify(requestParsingService).getQForEntityMatch(any(EntityMatchInputBean.class));
    }

    @Test
    public void givenEntityMatchInputBeanWithCompanyWhenGetEntityMatchThenGetDataResponse() throws Exception {
        // Arrange
        EntityMatchInputBean matchInputBean = new EntityMatchInputBean();
        EntityInput entityInput = new EntityInput();
        entityInput.setName(COMAPNY_NAME);
        entityInput.setHomePage(HOME_PAGE);
        entityInput.setAddress(ATTR_ADDRESS);
        matchInputBean.setCompany(entityInput);
        String reqBody = JSONUtility.serialize(matchInputBean);
        // Act
        JSONResponse actual = entityController.getEntityMatch(request, response, reqBody);
        // Assert
        assertEquals(entityDataResponse, actual);
        verify(requestParsingService, never()).getErrorResponse(anyInt());
    }

    @Test
    public void givenEntityMatchInputBeanWithTopicWhenGetEntityMatchThenGetDataResponse()
            throws JsonParseException, IOException {
        // Arrange
        EntityMatchInputBean matchInputBean = new EntityMatchInputBean();
        EntityInput entityInput = new EntityInput();
        entityInput.setName(TOPIC_NAME);
        entityInput.setHomePage(HOME_PAGE);
        entityInput.setAddress(ATTR_ADDRESS);
        matchInputBean.setTopic(entityInput);
        String reqBody = JSONUtility.serialize(matchInputBean);
        // Act
        JSONResponse actual = entityController.getEntityMatch(request, response, reqBody);
        // Assert
        assertEquals(entityDataResponse, actual);
        verify(requestParsingService, never()).getErrorResponse(anyInt());
    }

    @Test
    public void givenEntityMatchInputBeanWithIndustryWhenGetEntityMatchThenGetDataResponse()
            throws JsonParseException, IOException {
        // Arrange
        EntityMatchInputBean matchInputBean = new EntityMatchInputBean();
        EntityInput entityInput = new EntityInput();
        entityInput.setName(INDUSTRY_NAME);
        entityInput.setHomePage(HOME_PAGE);
        entityInput.setAddress(ATTR_ADDRESS);
        matchInputBean.setIndustry(entityInput);
        String reqBody = JSONUtility.serialize(matchInputBean);
        // Act
        JSONResponse actual = entityController.getEntityMatch(request, response, reqBody);
        // Assert
        assertEquals(entityDataResponse, actual);
        verify(requestParsingService, never()).getErrorResponse(anyInt());
    }

    @Test
    public void givenEntityMatchInputBeanWithRegionWhenGetEntityMatchThenGetDataResponse()
            throws JsonParseException, IOException {
        // Arrange
        EntityMatchInputBean matchInputBean = new EntityMatchInputBean();
        EntityInput entityInput = new EntityInput();
        entityInput.setName(REGION_NAME);
        entityInput.setHomePage(HOME_PAGE);
        entityInput.setAddress(ATTR_ADDRESS);
        matchInputBean.setRegion(entityInput);
        String reqBody = JSONUtility.serialize(matchInputBean);
        // Act
        JSONResponse actual = entityController.getEntityMatch(request, response, reqBody);
        // Assert
        assertEquals(entityDataResponse, actual);
        verify(requestParsingService, never()).getErrorResponse(anyInt());
    }

    @Test
    public void givenNullRequestBodyWhenGetCSResponseThenErrorResponse() {
        // Arrange
        JSONResponse expected = new JSONResponse();
        when(requestParsingService.getErrorResponse(anyInt())).thenReturn(expected);
        // Act
        JSONResponse actual = entityController.getCSResponse(request, response, null, START, LENGTH);
        // Assert
        assertEquals(expected, actual);
        verify(requestParsingService).getErrorResponse(anyInt());
    }

    @Test
    public void givenInvalidRequestBodyWhenGetCSResponseThenErrorResponse() {
        // Arrange
        JSONResponse expected = new JSONResponse();
        when(requestParsingService.getErrorResponse(anyInt())).thenReturn(expected);
        // Act
        JSONResponse actual = entityController.getCSResponse(request, response, INVALID_BODY, START, LENGTH);
        // Assert
        assertEquals(expected, actual);
        verify(requestParsingService).getErrorResponse(anyInt());
    }

    @Test
    public void givenRequestBodyWhenGetCSResponseThenResponse() throws Exception {
        when(responseDecoratorService.getConversationStartersResponse(any(SearchAPIResponse.class), anyMap(), anyMap(),
                anyInt(), anyInt())).thenReturn(entityDataResponse);
        // Arrange
        String[] qMulti = {"value"};
        when(requestParsingService.getQMultiFromReqBody(any(ConversationStartersInputBean.class), anyMap()))
                .thenReturn(qMulti);
        ConversationStartersInputBean bean = new ConversationStartersInputBean();
        String reqBody = JSONUtility.serialize(bean);
        // Act
        JSONResponse actual = entityController.getCSResponse(request, response, reqBody, START, LENGTH);
        // Assert
        assertEquals(entityDataResponse, actual);
        verify(requestParsingService, never()).getErrorResponse(anyInt());
    }

    @Test
    public void givenDnbEntitiesFoundByIdsWhenGetDnBEntityStatusThenGetResponse()
            throws JsonParseException, IOException {
        // Arrange
        DnBEntityStatusResponse expected = new DnBEntityStatusResponse();
        when(responseDecoratorService.getDnBEntityStatusResponse(anyList())).thenReturn(expected);
        when(requestParsingService.getErrorResponse(anyInt())).thenReturn(expected);
        List<Entity> entityList = new ArrayList<Entity>();
        Entity newEntity = new Entity();
        entityList.add(newEntity);
        String dnbIds = JSONUtility.serialize(entityList);
        // Act
        JSONResponse actual = entityController.getDnBEntityStatus(request, response, "id1~id2");
        // Assert
        assertEquals(expected, actual);
        verify(requestParsingService, never()).getErrorResponse(anyInt());
    }

    @Test
    public void givenExceptionInGetDnbEntitiesWhenGetDnBEntityStatusThenGetErrorResponse() throws Exception {
        // Arrange
        JSONResponse expected = new JSONResponse();
        when(requestParsingService.getErrorResponse(anyInt())).thenReturn(expected);
        Exception expectedExcep = new Exception();
        when(dnbService.getDnbEntities(anyString())).thenThrow(expectedExcep);

        List<Entity> entityList = new ArrayList<Entity>();
        Entity newEntity = new Entity();
        entityList.add(newEntity);
        String dnbIds = JSONUtility.serialize(entityList);
        // Act
        JSONResponse actual = entityController.getDnBEntityStatus(request, response, "id1~id2");
        // Assert
        assertEquals(expected, actual);
        verify(requestParsingService).getErrorResponse(anyInt());

    }

    @Test
    public void givenEntityBriefApiReturnedNullWhenLoadMoreThenErrorResponse() {
        // Arrange
        String expected = "expected error";
        when(requestParsingService.getErrorHtmlResponse(anyInt())).thenReturn(expected);
        when(requestParsingService.getDefaultSpec(anyBoolean(), anyBoolean())).thenReturn(enterprisePref);
        // Act
        JSONResponse<String> actual = entityController.loadMore(request, response, SEARCH_TOKEN, LAYOUT, ENDS_DATE,
                SectionType.E.toString(), SCOPE_DIRECTIVE, COUNT, COUNT, FQ);
        // Assert
        assertEquals(expected, actual.getResult());
        verify(requestParsingService).getErrorHtmlResponse(anyInt());
    }

    @Test
    public void givenEntityBriefWhenLoadMoreThenErrorResponse() throws Exception {
        String expected = "html";
        // Arrange
        when(entityBriefService.getEntityBriefDetails(any(enterprisePref.getClass()), anyString(), anyString(),
                anyString(), anyBoolean())).thenReturn(entityBriefInfo);
        // Arrange
        when(requestParsingService.getDefaultSpec(anyBoolean(), anyBoolean())).thenReturn(enterprisePref);
        // Act
        JSONResponse<String> actual = entityController.loadMore(request, response, SEARCH_TOKEN, LAYOUT, ENDS_DATE,
                SectionType.E.toString(), SCOPE_DIRECTIVE, COUNT, COUNT, FQ);
        // Assert
        assertEquals(expected, actual.getResult());
        verify(requestParsingService, never()).getErrorHtmlResponse(anyInt());
    }

    @Test
    public void givenDefaultSpecWhenCrossSectionThenGetResponse() {
        // Arrange
        String expected = "html";
        when(requestParsingService.getDefaultSpec()).thenReturn(enterprisePref);
        // Act
        JSONResponse<String> actual = entityController.crossSection(request, QUERY_1, QUERY_2, TOPIC_NAME, FQ);
        // Assert
        assertEquals(expected, actual.getResult());
        verify(requestParsingService, never()).getErrorHtmlResponse(anyInt());
    }

    @Test
    public void givenNullTokenWhenGetEntityMapThenGetErrorResponse() {
        // Arrange
        JSONResponse expected = new JSONResponse();
        when(requestParsingService.getErrorResponse(anyInt())).thenReturn(expected);
        // Act
        JSONResponse actual = entityController.getEntityMap(request, null);
        // Assert
        assertEquals(expected, actual);
        verify(requestParsingService).getErrorResponse(anyInt());
    }

    @Test
    public void givenSearchTokenWhenGetEntityMapThenGetErrorResponse() throws Exception {
        // Arrange
        when(responseDecoratorService.getEntityMapResponse(any(entityBriefInfo.getClass()), anyString()))
                .thenReturn(entityDataResponse);
        when(entityBriefService.getEntityMap(SEARCH_TOKEN)).thenReturn(entityBriefInfo);
        // Act
        JSONResponse actual = entityController.getEntityMap(request, SEARCH_TOKEN);
        // Assert
        assertEquals(entityDataResponse, actual);
        verify(requestParsingService, never()).getErrorHtmlResponse(anyInt());
    }

    @Test
    public void givenNullSearchTokenWhenGetEntityPeersThenErrorResponse() {
        // Arrange
        JSONResponse expected = new JSONResponse();
        when(requestParsingService.getErrorResponse(anyInt())).thenReturn(expected);
        // Act
        JSONResponse actual = entityController.getEntityPeers(request, null, null);
        // Assert
        assertEquals(expected, actual);
        verify(requestParsingService).getErrorResponse(anyInt());
    }

    @Test
    public void givenNullRequestBodyWhenGetEntityPeersThenErrorResponse() {
        // Arrange
        JSONResponse expected = new JSONResponse();
        when(requestParsingService.getErrorResponse(anyInt())).thenReturn(expected);
        // Act
        JSONResponse actual = entityController.getEntityPeers(request, SEARCH_TOKEN, null);
        // Assert
        assertEquals(expected, actual);
        verify(requestParsingService).getErrorResponse(anyInt());
    }

    @Test
    public void givenRequestBodyAndGetEntityPeersNullWhenGetEntityPeersThenResponse() {
        // Arrange
        JSONResponse expected = new JSONResponse();
        when(requestParsingService.getErrorResponse(anyInt())).thenReturn(expected);
        // Act
        JSONResponse actual = entityController.getEntityPeers(request, SEARCH_TOKEN, "{}");
        // Assert
        assertEquals(expected, actual);
        verify(requestParsingService).getErrorResponse(anyInt());
    }

    @Test
    public void givenRequestBodyAndGetEntityPeersWhenGetEntityPeersThenResponse() throws Exception {
        // Arrange
        when(entityBriefService.getEntityPeers(anyString(), anyList(), anyBoolean())).thenReturn(entityBriefInfo);
        when(responseDecoratorService.getEntityPeersResponse(any(entityBriefInfo.getClass()), anyString()))
                .thenReturn(entityDataResponse);
        // Act
        JSONResponse actual = entityController.getEntityPeers(request, SEARCH_TOKEN, "{}");
        // Assert
        assertEquals(entityDataResponse, actual);
        verify(requestParsingService, never()).getErrorResponse(anyInt());
    }

    @Test
    public void givenNullSearchTokenWhenMetaDataThenErrorResponse() {
        // Arrange
        JSONResponse expected = new JSONResponse();
        when(requestParsingService.getErrorResponse(anyInt())).thenReturn(expected);
        // Act
        JSONResponse actual = entityController.metaData(request, null);
        // Assert
        assertEquals(expected, actual);
        verify(requestParsingService).getErrorResponse(anyInt());
    }

    @Test
    public void givenRequestBodyAndGetEntityMapWhenmetaDataThenResponse() throws Exception {
        // Arrange
        EntityMap map = new EntityMap();
        when(entityBriefService.getEntityMapBySourceSearchToken(anyString(), anyString())).thenReturn(map);
        when(responseDecoratorService.getEntityMapResponse(any(entityBriefInfo.getClass()), anyString()))
                .thenReturn(entityDataResponse);
        // Act
        JSONResponse actual = entityController.metaData(request, SEARCH_TOKEN);
        // Assert
        assertEquals(entityDataResponse, actual);
        verify(requestParsingService, never()).getErrorResponse(anyInt());
    }

    @Test
    public void givenRequestBodyAndEntityMapWhenMetaDataThenResponse() throws Exception {
        // Arrange
        JSONResponse expected = new JSONResponse();
        when(requestParsingService.getErrorResponse(anyInt())).thenReturn(expected);
        // Act
        JSONResponse actual = entityController.metaData(request, SEARCH_TOKEN);
        // Assert
        assertEquals(expected, actual);
        verify(requestParsingService).getErrorResponse(anyInt());
    }

    private void validateRequestAttribute(String attribute, Object value) {
        verify(request).setAttribute(eq(attribute), objectArgumentCaptor.capture());
        collector.checkThat(objectArgumentCaptor.getValue(), is(value));
    }

    private void validateModelAttribute(String attribute, Object value) {
        verify(model).addAttribute(eq(attribute), objectArgumentCaptor.capture());
        collector.checkThat(objectArgumentCaptor.getValue(), is(value));
    }

    private void validateFtlParam(String param, Object value) throws Exception {
        if (ftlParams == null) {
            verify(freemarkerTemplateService).getHtml(anyString(), mapArgumentCaptor.capture());
            ftlParams = mapArgumentCaptor.getValue();
        }
        collector.checkThat(ftlParams.containsKey(param), is(true));
        collector.checkThat(ftlParams.get(param), is(value));
    }

    private void mockForSearchServiceRepository() {
        when(entityInfoCache.searchTokenToEntity(anyString())).thenReturn(entityInfo);
        when(entityInfoCache.companyIdToEntity(anyInt())).thenReturn(entityInfo);
        when(searchServiceRepository.getEntityInfoCache()).thenReturn(entityInfoCache);
        when(searchServiceRepository.getDocumentSimilarityUtil()).thenReturn(util);
    }

    private void mockResponseDecoratorService() {
        when(responseDecoratorService.excludeTweetInfo(anyLong())).thenReturn(true);
        when(responseDecoratorService.getEntityDataResponse(entityBriefInfo, MSG_KEY, sectionsMap, response,
                INDUSTRY_CLASSIFICATION_ID, false)).thenReturn(entityDataResponse);
        when(responseDecoratorService.getEntityDataResponse(any(EntityBriefInfo.class), anyString(), anyMap(),
                any(response.getClass()), anyShort(), anyBoolean())).thenReturn(entityDataResponse);
        when(responseDecoratorService.getMatchedEntityDataResponse(any(entityBriefInfo.getClass()),
                any(INPUT_ENTITY_TYPE.class))).thenReturn(entityDataResponse);
    }

    private void mockRequestParsingService() throws Exception {
        keySet = sectionsMap.keySet();
        when(requestParsingService.getSectionsPageSpecMap(SECTIONS, true, true)).thenReturn(enterprisePref);
        when(requestParsingService.getSectionsPageSpecMap(anyString())).thenReturn(enterprisePref);
        when(requestParsingService.getAllSectionIDs()).thenReturn(keySet);
        when(requestParsingService.intersectSets(keySet, keySet)).thenReturn(keySet);
    }

    private void mockEntityBriefService() throws Exception {
        when(entityBriefService.getEntityBriefDetails(enterprisePref, SEARCH_TOKEN, FQ, null, blendDUNS))
                .thenReturn(entityBriefInfo);
        when(entityBriefService.getEntityBriefDetailsForMT(any(enterprisePref.getClass()), anyString(), anyString(),
                anyString(), anyString(), anyString())).thenReturn(entityBriefInfo);
        when(entityBriefInfo.getStatusCode()).thenReturn(STATUS_CODE_OK);
        when(entityBriefInfo.getEntity()).thenReturn(entity);

        when(entityBriefService.getEntityMatch(anyString(), anyInt(), any(INPUT_ENTITY_TYPE.class), anyString()))
                .thenReturn(entityBriefInfo);
    }

    private void mockForSearchResult() throws Exception {
        SearchResult sr = new SearchResult();
        List<DocListBucket> buckets = new ArrayList<DocListBucket>();
        buckets.add(bucket);
        buckets.add(bucket);
        sr.buckets = buckets;
        List<DocEntry> entries = new ArrayList<DocEntry>();
        entries.add(entry);
        entries.add(entry);
        bucket.docs = entries;
        when(searchServiceRepository.getMultiSectionSearchResults(any(String[].class), any(int[].class), anyString(),
                any(BaseSpec.class))).thenReturn(sr);
    }

    private void mockSolrDocumentList() throws SolrServerException, Exception {
        SolrDocumentList solrDocumentList = new SolrDocumentList();
        SolrDocument solrDocument = new SolrDocument();
        solrDocument.setField("attrCompanyId", "0");
        solrDocument.setField(ATTR_ADDRESS, ATTR_ADDRESS);
        solrDocument.setField(ATTR_WEBSITE, ATTR_WEBSITE);
        solrDocument.setField("dnbCompanyId", "dnbCompanyId");
        solrDocument.setField("attrCountry", "Country");
        List<Integer> bizLineCatIds = new ArrayList<Integer>();
        solrDocument.setField("bizLineCatIds", bizLineCatIds);

        solrDocumentList.add(solrDocument);
        when(SolrServerReader.retrieveNSolrDocs(any(SolrServer.class), anyString(), anyInt(), anyInt(), anyString()))
                .thenReturn(solrDocumentList);
        when(companyServiceRepository.getCompanyDetailsFromCompanyIds(anyString(), anyInt()))
                .thenReturn(solrDocumentList);
    }

    private void setRequiredField() {
        ReflectionTestUtils.setField(entityController, "lastNDays", 7);
        ReflectionTestUtils.setField(entityController, "storageService", storageService);
        ReflectionTestUtils.setField(dnbRepository, "convertUtil", convertUtil);
        ReflectionTestUtils.setField(dnbRepository, "entityInfoCache", entityInfoCache);
        ReflectionTestUtils.setField(dnbService, "dnbRepository", dnbRepository);
        ReflectionTestUtils.setField(entityController, "dnbService", dnbService);
        ReflectionTestUtils.setField(searchService, "searchServiceRepository", searchServiceRepository);
        ReflectionTestUtils.setField(searchService, "convertUtil", convertUtil);
        ReflectionTestUtils.setField(searchService, "restrictContentService", restrictContentService);
        ReflectionTestUtils.setField(searchService, "entityProcessingService", entityProcessingService);
        ReflectionTestUtils.setField(searchService, "servicesAPIUtil", servicesAPIUtil);
        ReflectionTestUtils.setField(entityController, "searchService", searchService);
        ReflectionTestUtils.setField(storageService, "httpClientService", httpClientService);
        ReflectionTestUtils.setField(httpClientService, "httpClient", httpClient);
        ReflectionTestUtils.setField(entityController, "companyService", companyService);
        ReflectionTestUtils.setField(companyService, "companyServiceRepository", companyServiceRepository);
        ReflectionTestUtils.setField(companyService, "entityBaseService", entityBaseService);
    }

    private void mockDocumentSet() {
        Document document = new Document();
        document.setId(DOCUMENT_ID);
        List<Document> documentList = Collections.singletonList(document);
        DocumentSet documentSet = new DocumentSet();
        documentSet.setDocuments(documentList);
        when(entityBriefInfo.getWebResults()).thenReturn(documentSet);
        when(entityBriefInfo.getAnalystComments()).thenReturn(documentSet);
    }

    private void addUserInLocalThread() {
        User user = new User();
        user.setUserId(String.valueOf(USET_ID_LONG));
        user.setOwnedBy(USET_ID_LONG);
        user.setFlags(DefaultEnums.Status.ACTIVE.name());
        user.setMembershipType(com.firstrain.frapi.util.DefaultEnums.MembershipType.ADMIN);
        UserInfoThreadLocal.set(user);
    }

    private void populateSectionsMap() {
        sectionsMap.put(SectionType.valueOf("FR"), new SectionSpec());
        sectionsMap.put(SectionType.valueOf("FT"), new SectionSpec());
        sectionsMap.put(SectionType.valueOf("E"), new SectionSpec());
        sectionsMap.put(SectionType.valueOf("AC"), new SectionSpec());
        sectionsMap.put(SectionType.valueOf("WV"), new SectionSpec());
    }

    private void mockTheStaticClasses() {
        mockStatic(Constant.class);
        mockStatic(SolrServerReader.class);
    }
}
