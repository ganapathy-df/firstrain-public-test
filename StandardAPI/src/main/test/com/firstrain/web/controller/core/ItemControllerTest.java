package com.firstrain.web.controller.core;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyShort;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.ui.Model;
import com.firstrain.frapi.domain.User;
import com.firstrain.frapi.pojo.wrapper.DocumentSet;
import com.firstrain.frapi.pojo.wrapper.GetBulk;
import com.firstrain.frapi.pojo.wrapper.TweetSet;
import com.firstrain.frapi.repository.EntityBaseServiceRepository;
import com.firstrain.frapi.repository.impl.IndustryClassificationMap;
import com.firstrain.frapi.service.EntityBaseService;
import com.firstrain.frapi.service.EntityProcessingService;
import com.firstrain.frapi.service.RehydrateService;
import com.firstrain.frapi.service.RestrictContentService;
import com.firstrain.frapi.service.TwitterService;
import com.firstrain.frapi.service.impl.EntityBaseServiceImpl;
import com.firstrain.frapi.service.impl.EntityProcessingServiceImpl;
import com.firstrain.frapi.service.impl.RehydrateServiceImpl;
import com.firstrain.frapi.service.impl.TwitterServiceImpl;
import com.firstrain.frapi.util.ConvertUtil;
import com.firstrain.frapi.util.DefaultEnums;
import com.firstrain.obj.IEntityInfo;
import com.firstrain.obj.IEntityInfoCache;
import com.firstrain.solr.client.DocEntry;
import com.firstrain.solr.client.QuoteEntry;
import com.firstrain.solr.client.SolrSearcher;
import com.firstrain.solr.client.util.SolrServerReader;
import com.firstrain.web.pojo.Document;
import com.firstrain.web.pojo.EntityStandard;
import com.firstrain.web.pojo.ItemData;
import com.firstrain.web.pojo.Tweet;
import com.firstrain.web.response.ItemWrapperResponse;
import com.firstrain.web.response.JSONResponse;
import com.firstrain.web.service.core.FreemarkerTemplateService;
import com.firstrain.web.service.core.HttpClientService;
import com.firstrain.web.service.core.RequestParsingService;
import com.firstrain.web.service.core.ResponseDecoratorService;
import com.firstrain.web.service.core.StorageService;
import com.firstrain.web.util.UserInfoThreadLocal;
import com.firstrain.web.wrapper.ItemWrapper;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SolrServerReader.class)
public class ItemControllerTest {

    private static final String VIEW_STR = "view";
    private static final String ERROR_MESSAGE = "error message";
    private static final String ATTRIBUTE_NAME = "errorMsg";
    private static final String DOCUMENT_ID = "1";
    private static final String RESULTS_CSV = "resultsCSVHM";
    private static final String INVALID_ITEM_ID = "itemId";
    private static final String UNPARSE_ABLE_ITEM_ID = "D:itemId";
    private static final String VALID_ID = "D:2";
    private static final String TW_ITEM_ID = "TW:2";
    private static final String VALID_ID_2 = "D:1";
    private static final Long CAT_ID = 123l;
    private static final int STATUS_CODE_OK = 200;
    private static final long USET_ID_LONG = 2L;

    @Mock
    private QuoteEntry quoteEntry;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private Model model;
    @Mock
    private RequestParsingService requestParsingService;
    @Mock
    private ResponseDecoratorService responseDecoratorService;
    @Mock
    private RestrictContentService restrictContentService;
    @Mock
    private EntityBaseServiceRepository entityBaseServiceRepository;
    @Mock
    private SolrSearcher searcher;
    @Mock
    private HttpClientService httpClientService;
    @Mock
    private FreemarkerTemplateService ftlService;
    @Mock
    private IEntityInfoCache entityInfoCache;
    @Mock
    private IEntityInfo entityInfo;
    @Mock
    private IndustryClassificationMap industryClassificationMap;

    @Spy
    private RehydrateService rehydrateService = new RehydrateServiceImpl();
    private EntityBaseService entityBaseService = new EntityBaseServiceImpl();
    private ConvertUtil convertUtil = new ConvertUtil();
    private TwitterService twitterService = new TwitterServiceImpl();
    @Spy
    private StorageService storageService = new StorageService();
    private EntityProcessingService entityProcessingService = new EntityProcessingServiceImpl();
    @InjectMocks
    private ItemController controller;
    @Mock
    private DocEntry docEntry;

    @Rule
    public final ErrorCollector errorCollector = new ErrorCollector();

    @Before
    public void setUp() throws Exception {
        mockStatic(SolrServerReader.class);
        addUserInLocalThread();
        setRequiredFields();
        mockSolrDocumentList();
        mockForSearchServiceRepository();
        when(entityBaseServiceRepository.getSearcher()).thenReturn(searcher);
        when(docEntry.getSitedocId()).thenReturn(DOCUMENT_ID);
        List<DocEntry> entries = new ArrayList<DocEntry>();
        List<QuoteEntry> quoteEntries = new ArrayList<QuoteEntry>();
        quoteEntries.add(quoteEntry);
        docEntry.otrQuotes = quoteEntries;
        entries.add(docEntry);
        entries.add(docEntry);
        when(searcher.fetch(any(long[].class), anyString(), anyShort())).thenReturn(entries);
        TypeReference<Map<String, GetBulk>> aab = new TypeReference<Map<String, GetBulk>>() {};
        Map<String, GetBulk> map = new HashMap<String, GetBulk>();
        GetBulk bulk = new GetBulk();
        Map<String, Map<String, String>> entityLinking = new HashMap<String, Map<String, String>>();
        Map<String, String> mapStr = new HashMap<String, String>();
        mapStr.put(VALID_ID, VALID_ID);
        mapStr.put(VALID_ID_2, VALID_ID_2);
        entityLinking.put(VALID_ID, mapStr);
        entityLinking.put(VALID_ID_2, mapStr);
        bulk.setEntityLinking(entityLinking);
        map.put(VALID_ID, bulk);
        map.put(VALID_ID_2, bulk);
        when(httpClientService.postDataInReqBody(anyString(), anyString(), anyMap(), any(aab.getClass())))
                .thenReturn(map);
    }

    @Test
    public void givenInvalidItemIdWhenRehydrateThenGetErrorResponse() {
        // Arrange
        JSONResponse expected = new JSONResponse();
        when(requestParsingService.getErrorResponse(anyInt())).thenReturn(expected);
        // Act
        JSONResponse actual = controller.rehydrate(request, response, INVALID_ITEM_ID, RESULTS_CSV, true, true);
        // Assert
        assertEquals(expected, actual);
        verify(requestParsingService).getErrorResponse(anyInt());
    }

    @Test
    public void givenUnparseAbleItemIdWhenRehydrateThenGetErrorResponse() {
        // Arrange
        JSONResponse expected = new JSONResponse();
        when(requestParsingService.getErrorResponse(anyInt())).thenReturn(expected);
        // Act
        JSONResponse actual = controller.rehydrate(request, response, UNPARSE_ABLE_ITEM_ID, RESULTS_CSV, true, true);
        // Assert
        assertEquals(expected, actual);
        verify(requestParsingService).getErrorResponse(anyInt());
    }

    @Test
    public void givenIfHiddenContentWhenRehydrateThenGetErrorResponse() throws Exception {
        // Arrange
        JSONResponse expected = new JSONResponse();
        when(requestParsingService.getErrorResponse(anyInt())).thenReturn(expected);
        when(restrictContentService.checkIfHiddenContent(anyLong(), anyString())).thenReturn(true);
        // Act
        JSONResponse actual = controller.rehydrate(request, response, VALID_ID, RESULTS_CSV, true, true);
        // Assert
        assertEquals(expected, actual);
        verify(requestParsingService).getErrorResponse(anyInt());
    }

    @Test
    public void givenDocumentItemWhenRehydrateThenGetResponse() throws Exception {
        // Arrange
        ItemWrapperResponse expected = prepareItemWrapper();
        when(responseDecoratorService.getItemWrapperResponse(any(DocumentSet.class), anyString())).thenReturn(expected);
        // Act
        JSONResponse actual = controller.rehydrate(request, response, VALID_ID, RESULTS_CSV, true, true);
        // Assert
        assertEquals(expected, actual);
        verify(requestParsingService, never()).getErrorResponse(anyInt());
    }

    @Test
    public void givenTWItemIdWhenRehydrateThenGetResponse() throws Exception {
        // Arrange
        ItemWrapperResponse expected = prepareItemWrapper();
        when(responseDecoratorService.getItemWrapperResponse(any(TweetSet.class), anyString(), anyShort(),
                anyBoolean())).thenReturn(expected);
        // Act
        JSONResponse actual = controller.rehydrate(request, response, TW_ITEM_ID, RESULTS_CSV, true, true);
        // Assert
        assertEquals(expected, actual);
        verify(requestParsingService, never()).getErrorResponse(anyInt());
    }

    @Test
    public void givenInvalidItemIdWhenRehydrateHtmlThenGetErrorResponse() {
        // Arrange
        when(requestParsingService.getErrorHtmlResponse(anyInt())).thenReturn(ERROR_MESSAGE);
        // Act
        String actual = controller.rehydrateHtml(model, request, response, INVALID_ITEM_ID);
        // Assert
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(model).addAttribute(captor.capture(), captor.capture());
        List<String> args = captor.getAllValues();
        errorCollector.checkThat(args.get(0), is(ATTRIBUTE_NAME));
        errorCollector.checkThat(args.get(1), is(ERROR_MESSAGE));
        errorCollector.checkThat(actual, is(VIEW_STR));
        verify(requestParsingService).getErrorHtmlResponse(anyInt());
    }

    @Test
    public void givenUnparseAbleItemIdWhenRehydrateHtmlThenGetErrorResponse() {
        // Arrange
        when(requestParsingService.getErrorHtmlResponse(anyInt())).thenReturn(ERROR_MESSAGE);
        // Act
        String actual = controller.rehydrateHtml(model, request, response, UNPARSE_ABLE_ITEM_ID);
        // Assert
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(model).addAttribute(captor.capture(), captor.capture());
        List<String> args = captor.getAllValues();
        errorCollector.checkThat(args.get(0), is(ATTRIBUTE_NAME));
        errorCollector.checkThat(args.get(1), is(ERROR_MESSAGE));
        errorCollector.checkThat(actual, is(VIEW_STR));
        verify(requestParsingService).getErrorHtmlResponse(anyInt());
    }

    @Test
    public void givenIfHiddenContentWhenRehydrateHtmlThenGetErrorResponse() throws Exception {
        // Arrange
        when(requestParsingService.getErrorHtmlResponse(anyInt())).thenReturn(ERROR_MESSAGE);
        when(restrictContentService.checkIfHiddenContent(anyLong(), anyString())).thenReturn(true);
        // Act
        String actual = controller.rehydrateHtml(model, request, response, VALID_ID);
        // Assert
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(model).addAttribute(captor.capture(), captor.capture());
        List<String> args = captor.getAllValues();
        errorCollector.checkThat(args.get(0), is(ATTRIBUTE_NAME));
        errorCollector.checkThat(args.get(1), is(ERROR_MESSAGE));
        errorCollector.checkThat(actual, is(VIEW_STR));
        verify(requestParsingService).getErrorHtmlResponse(anyInt());
    }

    @Test
    public void givenDocumentItemWhenRehydrateHtmlThenGetResponse() throws Exception {
        // Arrange
        ItemWrapperResponse expected = prepareItemWrapper();
        when(responseDecoratorService.getItemWrapperResponse(any(DocumentSet.class), anyString())).thenReturn(expected);
        // Act
        String actual = controller.rehydrateHtml(model, request, response, VALID_ID);
        // Assert
        assertEquals(VIEW_STR, actual);
        verify(requestParsingService, never()).getErrorHtmlResponse(anyInt());
    }

    @Test
    public void givenTWItemIdWhenRehydrateHtmlThenGetResponse() throws Exception {
        // Arrange
        ItemWrapperResponse expected = prepareItemWrapper();
        when(responseDecoratorService.getItemWrapperResponse(any(TweetSet.class), anyString(), anyShort(),
                anyBoolean())).thenReturn(expected);
        // Act
        String actual = controller.rehydrateHtml(model, request, response, TW_ITEM_ID);
        // Assert
        assertEquals(VIEW_STR, actual);
        verify(requestParsingService, never()).getErrorHtmlResponse(anyInt());
    }

    @Test
    public void givenUnparseAbleItemIdWhenBarMeThenGetErrorResponse() {
        // Arrange
        JSONResponse expected = new JSONResponse();
        when(requestParsingService.getErrorResponse(anyInt())).thenReturn(expected);
        // Act
        JSONResponse actual = controller.barMe(request, response, UNPARSE_ABLE_ITEM_ID);
        // Assert
        assertEquals(expected, actual);
        verify(requestParsingService).getErrorResponse(anyInt());
    }

    @Test
    public void givenInvalidItemIdWhenBarMeThenGetErrorResponse() {
        // Arrange
        JSONResponse expected = new JSONResponse();
        when(requestParsingService.getErrorResponse(anyInt())).thenReturn(expected);
        // Act
        JSONResponse actual = controller.barMe(request, response, INVALID_ITEM_ID);
        // Assert
        assertEquals(expected, actual);
        verify(requestParsingService).getErrorResponse(anyInt());
    }

    @Test
    public void givenDocumentItemIdAndSuccessHideContentWhenBarMeThenGetResponse() throws Exception {
        // Arrange
        JSONResponse expected = new JSONResponse();
        when(restrictContentService.hideContent(anyLong(), anyLong(), anyString())).thenReturn(STATUS_CODE_OK);
        when(responseDecoratorService.getSuccessMsg(anyString())).thenReturn(expected);
        // Act
        JSONResponse actual = controller.barMe(request, response, VALID_ID);
        // Assert
        assertEquals(expected, actual);
        verify(responseDecoratorService).getSuccessMsg(anyString());
    }

    @Test
    public void givenDocumentItemIdAndNotSuccessHideContentWhenBarMeThenGetErrorResponse() throws Exception {
        // Arrange
        JSONResponse expected = new JSONResponse();
        when(requestParsingService.getErrorResponse(anyInt())).thenReturn(expected);
        // Act
        JSONResponse actual = controller.barMe(request, response, VALID_ID);
        // Assert
        assertEquals(expected, actual);
        verify(requestParsingService).getErrorResponse(anyInt());
    }

    private ItemWrapperResponse prepareItemWrapper() {
        ItemWrapperResponse expected = new ItemWrapperResponse();
        ItemWrapper itemData = new ItemWrapper();
        ItemData item = new ItemData();
        Document document = new Document();
        List<EntityStandard> entity = Collections.singletonList(new EntityStandard());
        document.setEntity(entity);
        item.setDocument(document);
        itemData.setData(item);
        expected.setResult(itemData);

        Tweet tweet = new Tweet();
        tweet.setEntity(new EntityStandard());
        item.setTweet(tweet);

        return expected;
    }

    private void addUserInLocalThread() {
        User user = new User();
        user.setUserId(String.valueOf(USET_ID_LONG));
        user.setOwnedBy(USET_ID_LONG);
        user.setFlags(DefaultEnums.Status.ACTIVE.name());
        user.setMembershipType(com.firstrain.frapi.util.DefaultEnums.MembershipType.ADMIN);
        UserInfoThreadLocal.set(user);
    }

    private void setRequiredFields() {
        Whitebox.setInternalState(entityBaseService, "entityBaseServiceRepository", entityBaseServiceRepository);
        Whitebox.setInternalState(entityBaseService, "convertUtil", convertUtil);
        Whitebox.setInternalState(rehydrateService, "entityBaseService", entityBaseService);
        Whitebox.setInternalState(rehydrateService, "entityProcessingService", entityProcessingService);
        Whitebox.setInternalState(storageService, "httpClientService", httpClientService);
        Whitebox.setInternalState(entityBaseService, "twitterService", twitterService);
        Whitebox.setInternalState(twitterService, "entityBaseServiceRepository", entityBaseServiceRepository);
        Whitebox.setInternalState(twitterService, "industryClassificationMap", industryClassificationMap);
    }

    private void mockSolrDocumentList() throws SolrServerException, Exception {
        SolrDocumentList docList = new SolrDocumentList();
        SolrDocument solrDocument = new SolrDocument();
        solrDocument.setField("tweetId", CAT_ID);
        solrDocument.setField("tweetCreationDate", new Date());
        solrDocument.setField("comboScore", 2);
        solrDocument.setField("groupId", CAT_ID);
        solrDocument.setField("scope", "hello World".getBytes()[0]);
        solrDocument.setField("groupSize", 2);
        List<Integer> companyIds = new ArrayList<Integer>();
        companyIds.add(301024);
        solrDocument.setField("companyId", companyIds);
        solrDocument.setField("topicIdCoreTweet", companyIds);
        solrDocument.setField("dayId", companyIds);
        List<Double> doubleIds = new ArrayList<Double>();
        doubleIds.add(1.1);
        solrDocument.setField("closingPrice", doubleIds);
        solrDocument.setField("openingPrice", doubleIds);

        docList.add(solrDocument);
        docList.setNumFound(2);

        when(SolrServerReader.retrieveNSolrDocs(any(SolrServer.class), anyString(), anyInt(), anyInt(),
                (String[]) anyVararg())).thenReturn(docList);
    }

    private void mockForSearchServiceRepository() {
        when(entityInfo.getId()).thenReturn("1");
        when(entityInfoCache.searchTokenToEntity(anyString())).thenReturn(entityInfo);
        when(entityInfoCache.companyIdToEntity(anyInt())).thenReturn(entityInfo);
        when(entityBaseServiceRepository.getEntityInfoCache()).thenReturn(entityInfoCache);
    }
}
