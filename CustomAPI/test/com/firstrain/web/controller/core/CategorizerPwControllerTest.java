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
import com.firstrain.web.domain.Brand;
import com.firstrain.web.domain.Group;
import com.firstrain.web.domain.PwToken;
import com.firstrain.web.domain.Topic;
import com.firstrain.web.pojo.CategorizerObject;
import com.firstrain.web.pojo.CategorizerObject.Attribute;
import com.firstrain.web.pojo.CategorizerObject.CatEntity;
import com.firstrain.web.pojo.CategorizerObject.CatEntityWrapper;
import com.firstrain.web.response.JSONResponse;
import com.firstrain.web.response.JSONResponse.ResStatus;
import com.firstrain.web.service.core.HttpClientService;
import com.firstrain.web.service.core.PWBrandMapping;
import com.firstrain.web.service.core.RequestParsingService;
import com.firstrain.web.service.core.ResponseDecoratorService;
import com.firstrain.web.util.EnterpriseConfigThreadLocal;
import com.firstrain.web.util.ProjectConfig.EnterpriseConfig;
import java.util.ArrayList;
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
public class CategorizerPwControllerTest {

  private CategorizerPwController categorizerPwController;
  private HttpServletRequest request;
  private HttpServletResponse response;
  private String reqBody = "";
  @Rule
  public final ErrorCollector collector = new ErrorCollector();
  private PWBrandMapping pwBrandMapping;

  @Before
  public void setUp() {
    categorizerPwController = new CategorizerPwController();
    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);
    mockStatic(JSONUtility.class);
    mockStatic(EnterpriseConfigThreadLocal.class);
    pwBrandMapping = mock(PWBrandMapping.class);
  }

  @Test
  public void givenParametersInvalidJSONWhenDocThenResult() throws Exception {
    //Arrange
    when(JSONUtility.deserialize(reqBody, Map.class)).thenThrow(IllegalArgumentException.class);
    String message = "message";
    prepareDocCall(message);

    //Act
    doControllerDocAndAssert(message); 
  }

  @Test
  public void givenParametersNullMapValueWhenDocThenResult() throws Exception {
    //Arrange
    Map<String, String> reqParam = new HashMap<String, String>();
    when(JSONUtility.deserialize(reqBody, Map.class)).thenReturn(reqParam);
    String message = "message";
    prepareDocCall(message);

    //Act
    final JSONResponse jsonResponse = categorizerPwController.doc(request, response, reqBody);

    //Assert
    collector.checkThat(jsonResponse.getErrorCode(), is(StatusCode.INSUFFICIENT_ARGUMENT));
    collector.checkThat(jsonResponse.getMessage(), is(message));
    collector.checkThat(jsonResponse.getStatus(), is(ResStatus.ERROR));
  }

  @Test
  public void givenParametersEmptyBrandKeyWhenDocThenResult() throws Exception {
    //Arrange
    final String message = arrangeDataForController();
    prepareDocCall(message);
    when(pwBrandMapping.getPwKey(anyString())).thenReturn("");

    //Act
    final JSONResponse jsonResponse = categorizerPwController.doc(request, response, reqBody);

    //Assert
    collector.checkThat(jsonResponse.getErrorCode(), is(400));
    collector.checkThat(jsonResponse.getMessage(), is("TaxonomyDirective is incorrect."));
    collector.checkThat(jsonResponse.getStatus(), is(ResStatus.ERROR));
  }

  @Test
  public void givenParametersNoBrandWhenDocThenResult() throws Exception {
    //Arrange
    final String message = arrangeDataForController();
    final String brandKey = "brand key";
    prepareDocCall(message);
    when(pwBrandMapping.getPwKey(anyString())).thenReturn(brandKey);
    when(pwBrandMapping.getBrand(brandKey)).thenReturn(null);

    //Act
    doControllerDocAndAssert(message); 
  }

  private String arrangeDataForController() throws IOException {
    Map<String, Object> reqParam = createParamMap();
    List<String> taxonomyDirectivesList = Arrays.asList("taxonomy");
    final String message = arrangeDataAndGetMessage(reqParam, taxonomyDirectivesList);
    return message;
  }
 
  private void doControllerDocAndAssert(final String message) { 
    final JSONResponse jsonResponse = categorizerPwController.doc(request, response, reqBody); 
     
    //Assert 
    collector.checkThat(jsonResponse.getErrorCode(), is(StatusCode.ILLEGAL_ARGUMENT)); 
    collector.checkThat(jsonResponse.getMessage(), is(message)); 
    collector.checkThat(jsonResponse.getStatus(), is(ResStatus.ERROR)); 
  } 

  @Test
  public void givenParametersBrandWhenDocThenResult() throws Exception {
    //Arrange
    Map<String, Object> reqParam = createParamMap();
    final String brandKey = "brand key";
    List<String> taxonomyDirectivesList = Arrays.asList("taxonomy", brandKey);
    final String message = arrangeDataAndGetMessage(reqParam, taxonomyDirectivesList);
    CategorizerObject categorizerObject = prepareDocCall(message);
    when(pwBrandMapping.getPwKey(anyString())).thenReturn(brandKey);
    when(pwBrandMapping.getPwBrandInitials(brandKey)).thenReturn("s.o");
    Brand brand = new Brand();
    Map<String, Topic> topicMap = new HashMap<String, Topic>();
    Topic topic = new Topic();
    String topicKey = "C:xxxx";
    topic.setPwTokens(Collections.singleton(topicKey));
    topicMap.put(topicKey, topic);
    Topic topic1 = new Topic();
    String topicKey1 = "D:xxxx";
    topic1.setPwTokens(Collections.singleton(topicKey1));
    topicMap.put(topicKey1, topic1);
    Topic topic2 = new Topic();
    String topicKey2 = "I:PWCompanies";
    topic2.setPwTokens(Collections.singleton(topicKey2));
    topicMap.put(topicKey2, topic2);
    brand.setTopicMap(topicMap);
    Map<String, List<String>> excludeTopicMap = new HashMap<String, List<String>>();
    excludeTopicMap.put(topicKey, Arrays.asList("C-S.O:"));
    excludeTopicMap.put(topicKey + "_", Arrays.asList("llll"));
    brand.setExcludeTopicMap(excludeTopicMap);
    Map<String, List<String>> andTopicMap = new HashMap<String, List<String>>();
    andTopicMap.put(topicKey1, Arrays.asList("T-S.O:"));
    andTopicMap.put(topicKey1 + "_", Arrays.asList("kkkkk"));
    brand.setAndTopicMap(andTopicMap);
    Map<Integer, Group> groupMap = new HashMap<Integer, Group>();
    int gMapInt = 34;
    Group group = new Group();
    group.setTokenSet(Collections.singleton(topicKey1));
    groupMap.put(gMapInt, group);
    brand.setGroupMap(groupMap);
    Map<String, PwToken> pwTokenHashMap = new HashMap<String, PwToken>();
    PwToken pwToken = new PwToken();
    pwToken.setGroups(Collections.singleton(gMapInt));
    pwTokenHashMap.put("D:xxxx", pwToken);
    brand.setPwTokenMap(pwTokenHashMap);
    when(pwBrandMapping.getBrand(brandKey)).thenReturn(brand);
    when(categorizerObject.getResponseCode()).thenReturn(StatusCode.REQUEST_SUCCESS);
    CatEntityWrapper catEntityWrapper = mock(CatEntityWrapper.class);
    when(categorizerObject.getData()).thenReturn(catEntityWrapper);
    List<CatEntity> catEntities = mockCatList();
    when(catEntityWrapper.getCategorizerResponse()).thenReturn(catEntities);

    //Act
    final JSONResponse jsonResponse = categorizerPwController.doc(request, response, reqBody);

    //Assert
    collector.checkThat(jsonResponse.getMessage(), is(message));
    collector.checkThat(jsonResponse.getStatus(), is(ResStatus.SUCCESS));
  }

  private String arrangeDataAndGetMessage(final Map<String, Object> reqParam, final List<String> taxonomyDirectivesList) throws IOException {
    reqParam.put("taxonomyDirective", taxonomyDirectivesList);
    List<String> targetSearchTokensList = Arrays.asList("token1");
    reqParam.put("targetSearchTokens", targetSearchTokensList);
    when(JSONUtility.deserialize(reqBody, Map.class)).thenReturn(reqParam);
    final String message = "message";
    return message;
  }

  private Map<String, Object> createParamMap() {
    Map<String, Object> reqParam = new HashMap<String, Object>();
    reqParam.put("title", "Title");
    reqParam.put("body", "Body");
    reqParam.put("docId", "DocId");
    return reqParam;
  }

  private static List<CatEntity> mockCatList() {
    List<CatEntity> catEntities = new ArrayList<CatEntity>();
    Attribute attribute1 = new Attribute();
    attribute1.setName("Name");
    attribute1.setAttrSearchToken("C-S.O:");
    CatEntity catEntity1 = new CatEntity();
    catEntity1.setAttrExclude(false);
    catEntity1.setFromFirstRain(false);
    catEntity1.setAttribute(attribute1);
    catEntities.add(catEntity1);
    Attribute attribute2 = new Attribute();
    attribute2.setName("Name");
    attribute2.setAttrSearchToken("T-S.O:");
    CatEntity catEntity2 = new CatEntity();
    catEntity2.setAttrExclude(false);
    catEntity2.setFromFirstRain(false);
    catEntity2.setAttribute(attribute2);
    catEntities.add(catEntity2);
    Attribute attribute3 = new Attribute();
    attribute3.setName("Name");
    attribute3.setAttrSearchToken("xxx:");
    CatEntity catEntity3 = new CatEntity();
    catEntity3.setAttrExclude(false);
    catEntity3.setFromFirstRain(false);
    catEntity3.setAttribute(attribute3);
    catEntities.add(catEntity3);
    Attribute attribute4 = new Attribute();
    attribute4.setName("Name");
    attribute4.setAttrSearchToken("C:xxxx");
    CatEntity catEntity4 = new CatEntity();
    catEntity4.setAttrExclude(false);
    catEntity4.setFromFirstRain(true);
    catEntity4.setAttribute(attribute4);
    catEntities.add(catEntity4);
    Attribute attribute5 = new Attribute();
    attribute5.setName("Name");
    attribute5.setAttrSearchToken("D:xxxx");
    CatEntity catEntity5 = new CatEntity();
    catEntity5.setAttrExclude(false);
    catEntity5.setFromFirstRain(true);
    catEntity5.setBand((short) 45);
    catEntity5.setAttribute(attribute5);
    catEntity5.setScore((short) 45);
    catEntities.add(catEntity5);
    Attribute attribute6 = new Attribute();
    attribute6.setName("Name");
    attribute6.setAttrSearchToken("I:PWCompanies");
    CatEntity catEntity6 = new CatEntity();
    catEntity6.setAttrExclude(false);
    catEntity6.setFromFirstRain(true);
    catEntity6.setAttribute(attribute6);
    catEntity6.setBand((short) 78);
    catEntity6.setScore((short) 65);
    catEntities.add(catEntity6);
    return catEntities;
  }

  private CategorizerObject prepareDocCall(String message) throws Exception {
    RequestParsingService requestParsingService = new RequestParsingService();
    setInternalState(categorizerPwController, "requestParsingService", requestParsingService);
    ResourceBundleMessageSourceImpl messageSource = mock(ResourceBundleMessageSourceImpl.class);
    when(messageSource.getMessageInternal(anyString(), any(Object[].class), any(Locale.class)))
        .thenReturn(message);
    setInternalState(requestParsingService, "messageSource", messageSource);
    EnterpriseConfig enterpriseConfig = mock(EnterpriseConfig.class);
    when(EnterpriseConfigThreadLocal.get()).thenReturn(enterpriseConfig);
    List<Integer> integerList = Arrays.asList(7, 8);
    when(enterpriseConfig.getTopicdimensionList()).thenReturn(integerList);
    HttpClientService httpClientService = mock(HttpClientService.class);
    setInternalState(categorizerPwController, "httpClientService", httpClientService);
    CategorizerObject categorizerObject = mock(CategorizerObject.class);
    when(httpClientService.postDataInReqBody(anyString(), anyString(), anyMap(),
        any(CategorizerObject.class.getClass()))).thenReturn(categorizerObject);
    setInternalState(requestParsingService, "messageSource", messageSource);
    ResponseDecoratorService responseDecoratorService = new ResponseDecoratorService();
    setInternalState(categorizerPwController, "responseDecoratorService", responseDecoratorService);
    setInternalState(responseDecoratorService, "messageSource", messageSource);
    setInternalState(categorizerPwController, "pwBrandMapping", pwBrandMapping);
    return categorizerObject;
  }
}
