package com.firstrain.web.controller.core;

import java.io.IOException; 

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.internal.util.reflection.Whitebox.setInternalState;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import com.firstrain.frapi.util.StatusCode;
import com.firstrain.utils.JSONUtility;
import com.firstrain.web.pojo.CategorizerObject;
import com.firstrain.web.pojo.CategorizerObject.Attribute;
import com.firstrain.web.pojo.CategorizerObject.CatEntity;
import com.firstrain.web.pojo.CategorizerObject.CatEntityWrapper;
import com.firstrain.web.response.JSONResponse;
import com.firstrain.web.response.JSONResponse.ResStatus;
import com.firstrain.web.service.core.HttpClientService;
import com.firstrain.web.service.core.RequestParsingService;
import com.firstrain.web.service.core.ResponseDecoratorService;
import com.firstrain.web.util.EnterpriseConfigThreadLocal;
import com.firstrain.web.util.ProjectConfig.EnterpriseConfig;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
    JSONUtility.class,
    EnterpriseConfigThreadLocal.class
})
public class CategorizerControllerTest {

  private CategorizerController categorizerController;
  private HttpServletRequest request;
  private HttpServletResponse response;
  private String reqBody = "";

  @Rule
  public final ErrorCollector collector = new ErrorCollector();

  @Before
  public void setUp() {
    categorizerController = new CategorizerController();
    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);
    mockStatic(JSONUtility.class);
    mockStatic(EnterpriseConfigThreadLocal.class);
  }

  @Test
  public void givenParametersInvalidJSONWhenDocThenResult() throws Exception {
    //Arrange
    String guid = "";
    when(JSONUtility.deserialize(reqBody, Map.class)).thenThrow(IllegalArgumentException.class);
    setupInvokeDocAndAssert(guid, StatusCode.ILLEGAL_ARGUMENT);
  }

  @Test
  public void givenParametersNullMapValueWhenDocThenResult() throws Exception {
    //Arrange
    String guid = "";
    Map<String, String> reqParam = new HashMap<String, String>();
    when(JSONUtility.deserialize(reqBody, Map.class)).thenReturn(reqParam);
    setupInvokeDocAndAssert(guid, StatusCode.INSUFFICIENT_ARGUMENT);
  }

  private void setupInvokeDocAndAssert(final String guid, final int statusCode) throws Exception {
    String message = "message";
    prepareDocCall(message);
    
    //Act
    final JSONResponse jsonResponse = categorizerController.doc(request, response, reqBody, guid);
    
    //Assert
    collector.checkThat(jsonResponse.getErrorCode(), is(statusCode));
    collector.checkThat(jsonResponse.getMessage(), is(message));
    collector.checkThat(jsonResponse.getStatus(), is(ResStatus.ERROR));
  }

  @Test
  public void givenParametersWhenDocThenResult() throws Exception {
    //Arrange
    String guid = "";
    final String message = arrangeParamGetMessage(); 
    CategorizerObject categorizerObject = prepareDocCall(message);
    when(categorizerObject.getResponseCode()).thenReturn(StatusCode.REQUEST_SUCCESS);
    CatEntityWrapper catEntityWrapper = mock(CatEntityWrapper.class);
    when(categorizerObject.getData()).thenReturn(catEntityWrapper);
    CatEntity catEntity = new CatEntity();
    catEntity.setAttrExclude(false);
    Attribute attribute = new Attribute();
    attribute.setName("Name");
    catEntity.setAttribute(attribute);
    attribute.setAttrSearchToken("SearchToken");
    List<CatEntity> catEntities = Collections.singletonList(catEntity);
    when(catEntityWrapper.getCategorizerResponse()).thenReturn(catEntities);

    //Act
    final JSONResponse jsonResponse = categorizerController.doc(request, response, reqBody, guid);

    //Assert
    collector.checkThat(jsonResponse.getMessage(), is(message));
    collector.checkThat(jsonResponse.getStatus(), is(ResStatus.SUCCESS));
  }

  @Test
  public void givenParametersWhenFeedbackThenResult() throws Exception {
    //Arrange
    final String message = arrangeParamGetMessage(); 
    prepareDocCall(message);

    //Act
    final JSONResponse jsonResponse = categorizerController.feedback(request, response, reqBody);

    //Assert
    collector.checkThat(jsonResponse.getMessage(), is(message));
    collector.checkThat(jsonResponse.getStatus(), is(ResStatus.SUCCESS));
  }
 
  private String arrangeParamGetMessage() throws IOException { 
    Map<String, String> reqParam = new HashMap<String, String>(); 
    reqParam.put("title", "Title"); 
    reqParam.put("body", "Body"); 
    when(JSONUtility.deserialize(reqBody, Map.class)).thenReturn(reqParam); 
    final String message = "message"; 
    return message; 
  } 

  private CategorizerObject prepareDocCall(String message) throws Exception {
    RequestParsingService requestParsingService = new RequestParsingService();
    setInternalState(categorizerController, "requestParsingService", requestParsingService);
    ResourceBundleMessageSourceImpl messageSource = mock(ResourceBundleMessageSourceImpl.class);
    when(messageSource.getMessageInternal(anyString(), any(Object[].class), any(Locale.class)))
        .thenReturn(message);
    setInternalState(requestParsingService, "messageSource", messageSource);
    setInternalState(categorizerController, "messageSource", messageSource);
    EnterpriseConfig enterpriseConfig = mock(EnterpriseConfig.class);
    when(EnterpriseConfigThreadLocal.get()).thenReturn(enterpriseConfig);
    List<Integer> integerList = Arrays.asList(7, 8);
    when(enterpriseConfig.getTopicdimensionList()).thenReturn(integerList);
    HttpClientService httpClientService = mock(HttpClientService.class);
    setInternalState(categorizerController, "httpClientService", httpClientService);
    CategorizerObject categorizerObject = mock(CategorizerObject.class);
    when(httpClientService.postDataInReqBody(anyString(), anyString(), anyMap(),
        any(CategorizerObject.class.getClass()))).thenReturn(categorizerObject);
    setInternalState(requestParsingService, "messageSource", messageSource);
    ResponseDecoratorService responseDecoratorService = new ResponseDecoratorService();
    setInternalState(categorizerController, "responseDecoratorService", responseDecoratorService);
    setInternalState(responseDecoratorService, "messageSource", messageSource);
    when(JSONUtility.serialize(anyMap())).thenReturn("json");
    return categorizerObject;
  }
}
