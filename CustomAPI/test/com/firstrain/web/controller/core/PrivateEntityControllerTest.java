package com.firstrain.web.controller.core;

import java.io.IOException;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.powermock.reflect.internal.WhiteboxImpl.setInternalState;

import com.firstrain.common.db.jpa.PersistenceProvider;
import com.firstrain.common.db.jpa.Transaction;
import com.firstrain.db.api.EntityBacktestDbAPI;
import com.firstrain.db.api.EntityHistoryDbAPI;
import com.firstrain.db.api.PrivateEntityDbAPI;
import com.firstrain.db.obj.EntityBacktest;
import com.firstrain.db.obj.EntityHistory;
import com.firstrain.db.obj.PrivateEntity;
import com.firstrain.frapi.customapirepository.impl.EntityBackTestRepositoryImpl;
import com.firstrain.frapi.customapirepository.impl.PrivateEntityRepositoryImpl;
import com.firstrain.frapi.customapiservice.TakeDownService;
import com.firstrain.frapi.customapiservice.impl.TakeDownServiceImpl;
import com.firstrain.obj.IEntityInfo;
import com.firstrain.obj.IEntityInfoCache;
import com.firstrain.solr.client.SearchTokenEntry;
import com.firstrain.utils.JSONUtility;
import com.firstrain.web.pojo.Author;
import com.firstrain.web.pojo.CategorizerObject.CatEntity;
import com.firstrain.web.pojo.CreateInputBean;
import com.firstrain.web.pojo.Doc;
import com.firstrain.web.response.JSONResponse;
import com.firstrain.web.response.JSONResponse.ResStatus;
import com.firstrain.web.service.core.EntiyBackTestService;
import com.firstrain.web.service.core.HttpClientService;
import com.firstrain.web.service.core.PWBrandMapping;
import com.firstrain.web.service.core.PrivateEntityService;
import com.firstrain.web.service.core.RequestParsingService;
import com.firstrain.web.service.core.ResponseDecoratorService;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
    JSONUtility.class,
    EntityHistoryDbAPI.class,
    PrivateEntityDbAPI.class,
    EntityBacktestDbAPI.class,
    PersistenceProvider.class,
    PrivateEntityRepositoryImpl.class,
    ResponseDecoratorService.class,
    HttpClientService.class
})
public class PrivateEntityControllerTest {

  private PrivateEntityController privateEntityController;
  private HttpServletRequest request;
  @Rule
  public final ErrorCollector collector = new ErrorCollector();
  private final static String BODY = "body";
  private final static String SEARCH_TOKEN = "token";
  private final static String TAXONOMY = "taxonomy";


  @Before
  public void setUp() {
    privateEntityController = new PrivateEntityController();
    request = mock(HttpServletRequest.class);
    mockStatic(JSONUtility.class);
    mockStatic(EntityHistoryDbAPI.class);
    mockStatic(PrivateEntityDbAPI.class);
    mockStatic(EntityBacktestDbAPI.class);
    mockStatic(PersistenceProvider.class);
  }

  @Test
  public void givenParametersNoVersionWhenGetDefinitionThenResult() throws Exception {
    //Arrange
    CreateInputBean createInputBean = new CreateInputBean();
    arrangeDeSerializeAndRequestMethod(BODY, createInputBean);
    RequestParsingService requestParsingService = new RequestParsingService();
    PrivateEntityService privateEntityService = arrangeData(requestParsingService); 
    ResourceBundleMessageSourceImpl messageSource = mock(ResourceBundleMessageSourceImpl.class);
    setInternalState(requestParsingService, "messageSource", messageSource);
    String message = "Message";
    when(messageSource.getMessageInternal(anyString(), any(Object[].class), any(Locale.class)))
        .thenReturn(message);

    //Act
    final JSONResponse definition = privateEntityController
        .getDefinition(request, BODY, SEARCH_TOKEN);

    //Assert
    collector.checkThat(definition.getErrorCode(), is(416));
    collector.checkThat(definition.getMessage(), is(message));
    collector.checkThat(definition.getStatus(), is(ResStatus.ERROR));
  }

  @Test
  public void givenParametersNoDevDefinitionWhenGetDefinitionThenResult() throws Exception {
    //Arrange
    CreateInputBean createInputBean = new CreateInputBean();
    createInputBean.setVersion("DEV");
    arrangeDeSerializeAndRequestMethod(BODY, createInputBean);
    String message = "message";
    concludePrepareGetDefinition(message, SEARCH_TOKEN, "");

    //Act
    final JSONResponse definition = privateEntityController
        .getDefinition(request, BODY, SEARCH_TOKEN);

    //Assert
    assertResponse(definition, message); 
  }

  @Test
  public void givenParametersDevDefinitionWhenGetDefinitionThenResult() throws Exception {
    //Arrange
    CreateInputBean createInputBean = new CreateInputBean();
    createInputBean.setVersion("DEV");
    arrangeDeSerializeAndRequestMethod(BODY, createInputBean);
    String message = "message";
    String devDefinition = "Definition for Dev";
    concludePrepareGetDefinition(message, SEARCH_TOKEN, devDefinition);

    //Act
    final JSONResponse definition = privateEntityController
        .getDefinition(request, BODY, SEARCH_TOKEN);

    //Assert
    assertResponse(definition, message); 
  }

  @Test
  public void givenParametersNonEmptyBackTestWhenBackTestSubmitThenResult() throws Exception {
    //Arrange
    final String body = "request body";
    final String searchToken = "request token";
    prepareBackTestCall(body);
    String message = "message";
    concludePrepareBackTestSubmit(message, searchToken, false, false);

    //Act
    final JSONResponse definition = privateEntityController
        .backtestSubmit(request, body, searchToken);

    //Assert
    assertResponse(definition, message); 
  }

  @Test
  public void givenParametersEmptyBackTestWithIdWhenBackTestSubmitThenResult() throws Exception {
    //Arrange
    String message = arrangeMessageTxnSpyDb(); 
    EntityBacktest entityBacktest = new EntityBacktest();
    entityBacktest.setId(45L);
    whenNew(EntityBacktest.class).withAnyArguments().thenReturn(entityBacktest);

    //Act
    final JSONResponse definition = privateEntityController
        .backtestSubmit(request, BODY, SEARCH_TOKEN);

    //Assert
    assertResponse(definition, message); 
  }

  @Test
  public void givenParametersEmptyBackTestWithNoIdWhenBackTestSubmitThenResult() throws Exception {
    //Arrange
    String message = arrangeMessageTxnSpyDb(); 

    //Act
    final JSONResponse definition = privateEntityController
        .backtestSubmit(request, BODY, SEARCH_TOKEN);

    //Assert
    assertErrorCodeMessageAndStatus(definition, message);
  }
 
  private String arrangeMessageTxnSpyDb() throws Exception { 
    String message = arrangeData(true, false);
    return message; 
  } 

  @Test
  public void givenParametersWhenBacktestCheckThenResult() throws Exception {
    //Arrange
    final long logId = 9L;
    prepareBackTestCall(BODY, logId);
    String message = "message";
    concludePrepareBackTestSubmit(message, SEARCH_TOKEN, true, false);

    //Act
    final JSONResponse definition = privateEntityController.backtestCheck(request, BODY, logId);

    //Assert
    assertResponse(definition, message); 
  }

  @Test
  public void givenParametersNoEntityBackTestWhenBacktestKillThenResult() throws Exception {
    //Arrange
    final long logId = 9L;
    prepareBackTestCall(BODY, logId);
    String message = "message";
    concludePrepareBackTestSubmit(message, SEARCH_TOKEN, true, false);
    Transaction transaction = mock(Transaction.class);
    when(PersistenceProvider.newTxn(PersistenceProvider.SPY_DB)).thenReturn(transaction);
    EntityBacktest entityBacktest = new EntityBacktest();
    when(transaction.fetch(logId, EntityBacktest.class)).thenReturn(entityBacktest);

    //Act
    final JSONResponse definition = privateEntityController.backtestKill(request, BODY, logId);

    //Assert
    assertResponse(definition, message); 
  }

  @Test
  public void givenParametersWhenBacktestKillThenResult() throws Exception {
    //Arrange
    final long logId = 9L;
    prepareBackTestCall(BODY, logId);
    String message = "message";
    concludePrepareBackTestSubmit(message, SEARCH_TOKEN, true, false);
    Transaction transaction = mock(Transaction.class);
    when(PersistenceProvider.newTxn(PersistenceProvider.SPY_DB)).thenReturn(transaction);

    //Act
    final JSONResponse definition = privateEntityController.backtestKill(request, BODY, logId);

    //Assert
    assertErrorCodeMessageAndStatus(definition, message);
  }

  @Test
  public void givenParametersNoUpdateTimeWhenUpdateStateThenResult() throws Exception {
    //Arrange
    String message = arrangeData(false, true);

    //Act
    final JSONResponse definition = privateEntityController
        .updateState(request, BODY, SEARCH_TOKEN);

    //Assert
    collector.checkThat(definition.getErrorCode(), is(423));
    collector.checkThat(definition.getMessage(), is(message));
    collector.checkThat(definition.getStatus(), is(ResStatus.ERROR));
  }

  private String arrangeData(final boolean data1, final boolean data2) throws Exception {
    prepareBackTestCall(BODY);
    String message = "message";
    concludePrepareBackTestSubmit(message, SEARCH_TOKEN, data1, data2);
    Transaction transaction = mock(Transaction.class);
    when(PersistenceProvider.newTxn(PersistenceProvider.SPY_DB)).thenReturn(transaction);
    return message;
  }

  @Test
  public void givenParametersUpdateTimeWhenUpdateStateThenResult() throws Exception {
    //Arrange
    String message = arrangeDataForControllerList(); 

    //Act
    final JSONResponse definition = privateEntityController
        .updateState(request, BODY, SEARCH_TOKEN);

    //Assert
    assertResponse(definition, message); 
  }

  @Test
  public void givenNoTaxonomyDirectiveParametersWhenListThenResult() throws Exception {
    //Arrange
    String message = arrangeDataForControllerList(); 

    //Act
    final JSONResponse definition = privateEntityController.list(request, BODY);

    //Assert
    assertErrorCodeMessageAndStatus(definition, message);
  }

  private void assertErrorCodeMessageAndStatus(final JSONResponse definition, final String message) {
    collector.checkThat(definition.getErrorCode(), is(500));
    collector.checkThat(definition.getMessage(), is(message));
    collector.checkThat(definition.getStatus(), is(ResStatus.ERROR));
  }
 
  private String arrangeDataForControllerList() throws Exception { 
    prepareBackTestCall(BODY); 
    String message = "message"; 
    concludePrepareBackTestSubmit(message, SEARCH_TOKEN, true, true); 
    Transaction transaction = mock(Transaction.class); 
    when(PersistenceProvider.newTxn(PersistenceProvider.SPY_DB)).thenReturn(transaction); 
    PrivateEntity privateEntity = new PrivateEntity(); 
    when(PrivateEntityDbAPI.updatePrivateEntity(transaction, 34L)).thenReturn(privateEntity); 
    return message; 
  } 

  @Test
  public void givenParametersWhenListThenResult() throws Exception {
    //Arrange
    String message = prepareBodyAndMessage(); 

    //Act
    final JSONResponse definition = privateEntityController.list(request, BODY);

    //Assert
    assertResponse(definition, message); 
  }

  @Test
  public void givenParametersEmptyDefWhenLookupDefinitionThenResult() throws Exception {
    //Arrange
    HttpServletResponse response = mock(HttpServletResponse.class);
    String message = prepareBodyAndMessage(); 

    //Act
    final JSONResponse definition = privateEntityController
        .lookupDefinition(request, response, BODY, SEARCH_TOKEN);

    //Assert
    collector.checkThat(definition.getErrorCode(), is(411));
    collector.checkThat(definition.getMessage(), is(SEARCH_TOKEN));
    collector.checkThat(definition.getStatus(), is(ResStatus.ERROR));
  }
 
  private String prepareBodyAndMessage() throws Exception { 
    String message = prepareDefinition("");
    return message; 
  } 

  @Test
  public void givenParametersWhenLookupDefinitionThenResult() throws Exception {
    //Arrange
    HttpServletResponse response = mock(HttpServletResponse.class);
    String message = prepareDefinition("definition");
    GetMethod getMethod = mock(GetMethod.class);
    whenNew(GetMethod.class).withAnyArguments().thenReturn(getMethod);
    InputStream inputStream = mock(InputStream.class);
    when(getMethod.getResponseBodyAsStream()).thenReturn(inputStream);
    when(inputStream.read(any(byte[].class))).thenReturn(-2);
    ByteArrayOutputStream byteArrayOutputStream = mock(ByteArrayOutputStream.class);
    whenNew(ByteArrayOutputStream.class).withAnyArguments().thenReturn(byteArrayOutputStream);
    byte[] bytes = {};
    when(byteArrayOutputStream.toByteArray()).thenReturn(bytes);

    //Act
    final JSONResponse definition = privateEntityController
        .lookupDefinition(request, response, BODY, SEARCH_TOKEN);

    //Assert
    assertResponse(definition, message); 
  }

  private String prepareDefinition(final String definitionDev) throws Exception {
    prepareBackTestCall(BODY);
    String message = "message";
    concludePrepareGetDefinition(message, SEARCH_TOKEN, definitionDev);
    return message;
  }
 
  private void assertResponse(final JSONResponse definition, final String message) { 
    collector.checkThat(definition.getErrorCode(), is(nullValue(Integer.class))); 
    collector.checkThat(definition.getMessage(), is(message)); 
    collector.checkThat(definition.getStatus(), is(ResStatus.SUCCESS)); 
  } 

  private void concludePrepareGetDefinition(String message, String searchToken,
      String definitionDev) throws Exception {
    RequestParsingService requestParsingService = new RequestParsingService();
    ResponseDecoratorService responseDecoratorService = new ResponseDecoratorService();
    PrivateEntityService privateEntityService = arrangeData(requestParsingService); 
    setInternalState(privateEntityController, "responseDecoratorService", responseDecoratorService);
    PWBrandMapping pwBrandMapping = new PWBrandMapping();
    setInternalState(requestParsingService, "pwBrandMapping", pwBrandMapping);
    TakeDownService takeDownService = new TakeDownServiceImpl();
    setInternalState(requestParsingService, "takeDownService", takeDownService);
    IEntityInfoCache entityInfoCache = mock(IEntityInfoCache.class);
    setInternalState(takeDownService, "entityInfoCache", entityInfoCache);
    IEntityInfo iEntityInfo = mock(IEntityInfo.class);
    if (!StringUtils.isEmpty(definitionDev)) {
      when(iEntityInfo.getType()).thenReturn(SearchTokenEntry.SEARCH_TOKEN_TAG_SECTOR_TOPIC);
    } else {
      when(iEntityInfo.getType()).thenReturn(SearchTokenEntry.SEARCH_TOKEN_TAG_BIZLINE);
    }
    when(entityInfoCache.searchTokenToEntity(searchToken)).thenReturn(iEntityInfo);
    Map<String, String> pwKeyMapping = new ConcurrentHashMap<String, String>();
    pwKeyMapping.put(TAXONOMY + "topics", TAXONOMY + "_Mapping1");
    pwKeyMapping.put(TAXONOMY + "companies", TAXONOMY + "_Mapping2");
    setInternalState(pwBrandMapping, "pwKeyMapping", pwKeyMapping);
    Map<String, String> pwKeyVsBrandInitials = new ConcurrentHashMap<String, String>();
    pwKeyVsBrandInitials.put(TAXONOMY + "topics", TAXONOMY + "_BrandInitials1");
    pwKeyVsBrandInitials.put(TAXONOMY + "companies", TAXONOMY + "_BrandInitials2");
    setInternalState(pwBrandMapping, "pwKeyVsBrandInitials", pwKeyMapping);
    ResourceBundleMessageSourceImpl messageSource = createMessageSource(requestParsingService, responseDecoratorService); 
    HttpClientService httpClientService = new HttpClientService();
    setInternalState(privateEntityController, "httpClientService", httpClientService);
    setInternalState(privateEntityController, "catServiceURL", "http://host/catservice");
    setInternalState(httpClientService, "httpClient", mock(HttpClient.class));
    when(messageSource.getMessageInternal(anyString(), any(Object[].class), any(Locale.class)))
        .thenReturn(message);
    setInternalState(privateEntityService, "privateEntityRepositoryImpl",
        new PrivateEntityRepositoryImpl());
    arrangeEntityHistory(searchToken);
    PrivateEntity privateEntity = new PrivateEntity();
    privateEntity.setName("Name");
    privateEntity.setSearchToken(searchToken);
    privateEntity.setDefinition_dev(definitionDev);
    final List<PrivateEntity> privateEntities = Collections.singletonList(privateEntity);
    when(PrivateEntityDbAPI.getPrivateEntity(searchToken)).thenReturn(privateEntities);
    ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1, 1,
        TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(1));
    arrangeData(threadPoolTaskExecutor, threadPoolExecutor, privateEntityService, responseDecoratorService);
  }
 
  private PrivateEntityService arrangeData(final RequestParsingService requestParsingService) { 
    setInternalState(privateEntityController, "requestParsingService", requestParsingService); 
    PrivateEntityService privateEntityService = new PrivateEntityService(); 
    setInternalState(privateEntityController, "privateEntityService", privateEntityService); 
    return privateEntityService; 
  } 

  private void concludePrepareBackTestSubmit(String message, String searchToken,
      boolean emptyBackTestList, boolean addHistory) throws Exception {
    PrivateEntityService privateEntityService = new PrivateEntityService();
    setInternalState(privateEntityController, "privateEntityService", privateEntityService);
    RequestParsingService requestParsingService = new RequestParsingService();
    ResponseDecoratorService responseDecoratorService = new ResponseDecoratorService();
    setInternalState(privateEntityController, "requestParsingService", requestParsingService);
    setInternalState(privateEntityController, "responseDecoratorService", responseDecoratorService);
    setInternalState(privateEntityService, "privateEntityRepositoryImpl",
        new PrivateEntityRepositoryImpl());
    ResourceBundleMessageSourceImpl messageSource = createMessageSource(requestParsingService, responseDecoratorService); 
    when(messageSource.getMessageInternal(anyString(), any(Object[].class), any(Locale.class)))
        .thenReturn(message);
    PrivateEntity privateEntity = mock(PrivateEntity.class);
    when(privateEntity.getName()).thenReturn("Name");
    when(privateEntity.getSearchToken()).thenReturn(searchToken);
    when(privateEntity.getId_dev()).thenReturn(70L);
    when(privateEntity.getId()).thenReturn(34L);
    when(privateEntity.getDefinition_dev()).thenReturn("DF_DEV");
    when(privateEntity.getStatus()).thenReturn("DEVLIVE");
    when(privateEntity.getUpdateTime()).thenReturn(new Timestamp(System.currentTimeMillis()));
    final List<PrivateEntity> privateEntities = Collections.singletonList(privateEntity);
    when(PrivateEntityDbAPI.getPrivateEntity(searchToken)).thenReturn(privateEntities);
    EntityBacktest entityBacktest = new EntityBacktest();
    entityBacktest.setId(12L);
    final List<EntityBacktest> entityBacktests = emptyBackTestList
        ? Collections.<EntityBacktest>emptyList()
        : Collections.singletonList(entityBacktest);
    if (addHistory) {
      when(EntityBacktestDbAPI.getEntityBacktest(searchToken)).thenReturn(entityBacktests);
      arrangeEntityHistory(searchToken);
    } else {
      when(EntityBacktestDbAPI.getEntityBacktestWithOutDocsJson(searchToken))
          .thenReturn(entityBacktests);
    }
    ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1, 1,
        TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(3));
    arrangeData(threadPoolTaskExecutor, threadPoolExecutor, privateEntityService, responseDecoratorService);
    EntiyBackTestService entityBackTestService = new EntiyBackTestService();
    setInternalState(privateEntityController, "entityBackTestService", entityBackTestService);
    EntityBackTestRepositoryImpl entityBackTestRepositoryImpl = new EntityBackTestRepositoryImpl();
    setInternalState(entityBackTestService, "entityBackTestRepositoryImpl",
        entityBackTestRepositoryImpl);
  }

  private void arrangeData(final ThreadPoolTaskExecutor threadPoolTaskExecutor, final ThreadPoolExecutor threadPoolExecutor, final PrivateEntityService privateEntityService, final ResponseDecoratorService responseDecoratorService) throws IOException {
    setInternalState(threadPoolTaskExecutor, "threadPoolExecutor", threadPoolExecutor);
    setInternalState(privateEntityService, "taskExecutor", threadPoolTaskExecutor);
    PrivateEntityService privateEntityService1 = mock(PrivateEntityService.class);
    setInternalState(responseDecoratorService, "privateEntityService", privateEntityService1);
    JsonNode jsonNode = mock(JsonNode.class);
    when(privateEntityService1.getJsonNodeRes(anyString())).thenReturn(jsonNode);
  }

  private void arrangeEntityHistory(final String searchToken) throws Exception {
    EntityHistory entityHistory = mock(EntityHistory.class);
    entityHistory.setEmail("Email Address");
    when(entityHistory.getUpdateTime()).thenReturn(new Timestamp(System.currentTimeMillis()));
    final List<EntityHistory> entityHistories = Collections.singletonList(entityHistory);
    when(EntityHistoryDbAPI.getEntityHistoryBySearchToken(searchToken))
        .thenReturn(entityHistories);
  }
 
  private ResourceBundleMessageSourceImpl createMessageSource(final RequestParsingService requestParsingService, final ResponseDecoratorService responseDecoratorService) { 
    ResourceBundleMessageSourceImpl messageSource = mock(ResourceBundleMessageSourceImpl.class); 
    setInternalState(requestParsingService, "messageSource", messageSource); 
    setInternalState(responseDecoratorService, "messageSource", messageSource); 
    return messageSource; 
  } 

  private void prepareBackTestCall(String body) throws Exception {
    CreateInputBean createInputBean = createCreateInputBean(); 
    createInputBean.setState("ACTIVE");
    Author author = new Author();
    author.setEmail("Email");
    author.setName("Name");
    createInputBean.setAuthor(author);
    createInputBean.setTaxonomyDirective(TAXONOMY);
    arrangeDeSerializeAndRequestMethod(body, createInputBean);
  }

  private void prepareBackTestCall(String body, long jobId) throws Exception {
    CreateInputBean createInputBean = createCreateInputBean(); 
    arrangeDeSerializeAndRequestMethod(body, createInputBean);
    EntityBacktest entityBacktest = new EntityBacktest();
    entityBacktest.setId(12L);
    entityBacktest.setStatus("COMPLETED");
    String resultJson = "JSON";
    entityBacktest.setResult_json(resultJson);
    final List<EntityBacktest> entityBacktests = Collections.singletonList(entityBacktest);
    when(EntityBacktestDbAPI.getEntityBacktestById(jobId)).thenReturn(entityBacktests);
    CatEntity catEntity1 = new CatEntity();
    ArrayList<CatEntity> catEntities = new ArrayList<CatEntity>();
    catEntities.add(catEntity1);
    CatEntity catEntity2 = new CatEntity();
    catEntity2.setCharCount(9L);
    catEntities.add(catEntity2);
    when(JSONUtility.deserialize(anyString(), any(TypeReference.class))).thenReturn(catEntities);
  }

  private void arrangeDeSerializeAndRequestMethod(final String body, final CreateInputBean createInputBean) throws IOException {
    when(JSONUtility.deserialize(body, CreateInputBean.class)).thenReturn(createInputBean);
    String requestMethod = "";
    when(request.getMethod()).thenReturn(requestMethod);
  }
 
  private CreateInputBean createCreateInputBean() { 
    CreateInputBean createInputBean = new CreateInputBean(); 
    Doc doc = new Doc(); 
    doc.setBody("docBody"); 
    doc.setDocId("dociD"); 
    doc.setTitle("docTitle"); 
    List<Doc> docs = Collections.singletonList(doc); 
    createInputBean.setDocs(docs); 
    return createInputBean; 
  } 
}
