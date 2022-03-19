package com.firstrain.web.controller.core;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.internal.util.reflection.Whitebox.setInternalState;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import com.firstrain.common.db.jpa.PersistenceProvider;
import com.firstrain.common.db.jpa.Transaction;
import com.firstrain.db.api.TakeDownDbAPI;
import com.firstrain.db.obj.APIArticleTakeDown;
import com.firstrain.db.obj.APIArticleTakeDown.Status;
import com.firstrain.frapi.customapirepository.impl.TakeDownRepositoryImpl;
import com.firstrain.frapi.customapiservice.TakeDownService;
import com.firstrain.frapi.customapiservice.impl.TakeDownServiceImpl;
import com.firstrain.frapi.util.StatusCode;
import com.firstrain.solr.client.DocEntry;
import com.firstrain.solr.client.EntityEntry;
import com.firstrain.solr.client.SolrSearcher;
import com.firstrain.web.response.JSONResponse;
import com.firstrain.web.response.JSONResponse.ResStatus;
import com.firstrain.web.service.core.RequestParsingService;
import com.firstrain.web.service.core.ResponseDecoratorService;
import com.firstrain.web.util.EnterpriseConfigThreadLocal;
import com.firstrain.web.util.ProjectConfig.EnterpriseConfig;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    EnterpriseConfigThreadLocal.class,
    TakeDownDbAPI.class,
    PersistenceProvider.class
})
public class DocControllerTest {

  private DocController docController;

  @Rule
  public final ErrorCollector collector = new ErrorCollector();

  @Before
  public void setUp() {
    docController = new DocController();
    mockStatic(EnterpriseConfigThreadLocal.class);
    mockStatic(TakeDownDbAPI.class);
    mockStatic(PersistenceProvider.class);
  }

  @Test
  public void givenParametersOneWhenBarAllThenResult() throws Exception {
    //Arrange
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    String docId = "sss";
    arrangeDataInvokeBarAllAndAssert(request, response, docId); 
  }

  @Test
  public void givenParametersNotNumericWhenBarAllThenResult() throws Exception {
    //Arrange
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    String docId = "sss:dddd";
    arrangeDataInvokeBarAllAndAssert(request, response, docId); 
  }
 
  private void arrangeDataInvokeBarAllAndAssert(final HttpServletRequest request, final HttpServletResponse response, final String docId) throws Exception { 
    final String message = "message"; 
    prepareDocCall(message); 
     
    //Act 
    final JSONResponse jsonResponse = docController.barAll(request, response, docId, true); 
     
    //Assert 
    collector.checkThat(jsonResponse.getErrorCode(), is(StatusCode.ILLEGAL_ARGUMENT)); 
    collector.checkThat(jsonResponse.getMessage(), is(message)); 
    collector.checkThat(jsonResponse.getStatus(), is(ResStatus.ERROR)); 
  } 

  @Test
  public void givenParametersNewStatusWhenBarAllThenResult() throws Exception {
    //Arrange
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    String docId = "977:566";
    final String message = "message";
    prepareDocCall(message);
    long enterpriseId = 67L;
    EnterpriseConfig enterpriseConfig = mock(EnterpriseConfig.class);
    when(EnterpriseConfigThreadLocal.get()).thenReturn(enterpriseConfig);
    when(enterpriseConfig.getId()).thenReturn(enterpriseId);
    String sourceIds = "ssss, gggg";
    when(TakeDownDbAPI.fetchSourceIdsCSVByEnterpriseId(enterpriseId)).thenReturn(sourceIds);
    APIArticleTakeDown apiArticleTakeDown = new APIArticleTakeDown();
    apiArticleTakeDown.setStatus(Status.NEW);
    arrangeArticleTakeDownDoBarAllAndAssert(apiArticleTakeDown, request, response, docId, message); 
  }

  @Test
  public void givenParametersProcessedStatusWhenBarAllThenResult() throws Exception {
    //Arrange
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    String docId = "977:566";
    final String message = "message";
    prepareDocCall(message);
    long enterpriseId = 67L;
    EnterpriseConfig enterpriseConfig = mock(EnterpriseConfig.class);
    when(EnterpriseConfigThreadLocal.get()).thenReturn(enterpriseConfig);
    when(enterpriseConfig.getId()).thenReturn(enterpriseId);
    String sourceIds = "ssss, gggg";
    when(TakeDownDbAPI.fetchSourceIdsCSVByEnterpriseId(enterpriseId)).thenReturn(sourceIds);
    APIArticleTakeDown apiArticleTakeDown = new APIArticleTakeDown();
    apiArticleTakeDown.setStatus(Status.COMPLETED);
    arrangeArticleTakeDownDoBarAllAndAssert(apiArticleTakeDown, request, response, docId, message); 
  }
 
  private void arrangeArticleTakeDownDoBarAllAndAssert(final APIArticleTakeDown apiArticleTakeDown, final HttpServletRequest request, final HttpServletResponse response, final String docId, final String message) { 
    when(TakeDownDbAPI.getTakeDownArticle(566)).thenReturn(apiArticleTakeDown); 
     
    //Act 
    doBarAllAndAssert(request, response, docId, message);
  } 

  @Test
  public void givenParametersNullArticlesWhenBarAllThenResult() throws Exception {
    //Arrange
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    String docId = "977:566";
    final String message = "message";
    prepareDocCall(message);
    long enterpriseId = 67L;
    EnterpriseConfig enterpriseConfig = mock(EnterpriseConfig.class);
    when(EnterpriseConfigThreadLocal.get()).thenReturn(enterpriseConfig);
    when(enterpriseConfig.getId()).thenReturn(enterpriseId);
    String sourceIds = "ssss, gggg";
    when(TakeDownDbAPI.fetchSourceIdsCSVByEnterpriseId(enterpriseId)).thenReturn(sourceIds);
    when(TakeDownDbAPI.getTakeDownArticle(566)).thenReturn(null);
    when(PersistenceProvider.newTxn(PersistenceProvider.SPY_DB))
        .thenReturn(mock(Transaction.class));

    //Act
    doBarAllAndAssert(request, response, docId, message);
  }

  private void doBarAllAndAssert(final HttpServletRequest request, final HttpServletResponse response, final String docId, final String message) {
    final JSONResponse jsonResponse = docController.barAll(request, response, docId, true);
    
    //Assert
    collector.checkThat(jsonResponse.getMessage(), is(message));
    collector.checkThat(jsonResponse.getStatus(), is(ResStatus.SUCCESS));
  }

  private void prepareDocCall(String message) throws Exception {
    TakeDownService takeDownService = new TakeDownServiceImpl();
    setInternalState(docController, "takeDownService", takeDownService);
    ResourceBundleMessageSourceImpl messageSource = mock(ResourceBundleMessageSourceImpl.class);
    when(messageSource.getMessageInternal(anyString(), any(Object[].class), any(Locale.class)))
        .thenReturn(message);
    ResponseDecoratorService responseDecoratorService = new ResponseDecoratorService();
    setInternalState(docController, "responseDecoratorService", responseDecoratorService);
    RequestParsingService requestParsingService = new RequestParsingService();
    setInternalState(docController, "requestParsingService", requestParsingService);
    setInternalState(responseDecoratorService, "messageSource", messageSource);
    setInternalState(requestParsingService, "messageSource", messageSource);
    ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
    setInternalState(takeDownService, "taskExecutor", threadPoolTaskExecutor);
    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1, 1,
        TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(1));
    setInternalState(threadPoolTaskExecutor, "threadPoolExecutor", threadPoolExecutor);
    SolrSearcher solrSearcher = mock(SolrSearcher.class);
    setInternalState(takeDownService, "searcher", solrSearcher);
    DocEntry docEntry = new DocEntry();
    EntityEntry entityEntry = new EntityEntry();
    entityEntry.id = "ssss";
    docEntry.sourceEntity = entityEntry;
    docEntry.sitedocId = "siteDoId";
    List<DocEntry> docEntries = Collections.singletonList(docEntry);
    when(solrSearcher.fetch(566)).thenReturn(docEntries);
    TakeDownRepositoryImpl takeDownRepositoryImpl = new TakeDownRepositoryImpl();
    setInternalState(takeDownService, "takeDownRepositoryImpl", takeDownRepositoryImpl);
    setInternalState(takeDownService, "executorTimeout", 678);
  }
}
