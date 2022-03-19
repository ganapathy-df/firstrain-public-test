package com.firstrain.web.service.core;

import com.firstrain.frapi.domain.CompanyTradingRange;
import com.firstrain.frapi.domain.CompanyVolume;
import com.firstrain.frapi.domain.Document;
import com.firstrain.frapi.domain.EntityMap;
import com.firstrain.frapi.domain.EntityStatus;
import com.firstrain.frapi.domain.HistoricalStat;
import com.firstrain.frapi.domain.ItemDetail;
import com.firstrain.frapi.domain.MgmtTurnoverData;
import com.firstrain.frapi.domain.MonitorConfig;
import com.firstrain.frapi.domain.MonitorEmail;
import com.firstrain.frapi.domain.MonitorInfo;
import com.firstrain.frapi.domain.Search;
import com.firstrain.frapi.domain.SectionSpec;
import com.firstrain.frapi.domain.User;
import com.firstrain.frapi.domain.VisualizationData;
import com.firstrain.frapi.domain.WebVolumeEventMeta;
import com.firstrain.frapi.pojo.AuthAPIResponse;
import com.firstrain.frapi.pojo.EmailDetail;
import com.firstrain.frapi.pojo.EmailResponse;
import com.firstrain.frapi.pojo.Entity;
import com.firstrain.frapi.pojo.EntityBriefInfo;
import com.firstrain.frapi.pojo.Event;
import com.firstrain.frapi.pojo.Graph;
import com.firstrain.frapi.pojo.MonitorAPIResponse;
import com.firstrain.frapi.pojo.MonitorBriefDetail;
import com.firstrain.frapi.pojo.MonitorEmailAPIResponse;
import com.firstrain.frapi.pojo.SearchAPIResponse;
import com.firstrain.frapi.pojo.UserAPIResponse;
import com.firstrain.frapi.pojo.wrapper.BaseSet;
import com.firstrain.frapi.pojo.wrapper.DocumentSet;
import com.firstrain.frapi.pojo.wrapper.EventSet;
import com.firstrain.frapi.pojo.wrapper.TweetSet;
import com.firstrain.frapi.util.CompanyTeam;
import com.firstrain.frapi.util.ContentType;
import com.firstrain.frapi.util.DefaultEnums;
import com.firstrain.frapi.util.StatusCode;
import com.firstrain.mip.object.FR_ICategory;
import com.firstrain.utils.JSONUtility;
import com.firstrain.web.pojo.AuthKey;
import com.firstrain.web.pojo.EntityDataHtml;
import com.firstrain.web.pojo.EntityStandard;
import com.firstrain.web.pojo.MetaData;
import com.firstrain.web.pojo.Tweet;
import com.firstrain.web.response.AuthKeyResponse;
import com.firstrain.web.response.DnBEntityStatusResponse;
import com.firstrain.web.response.EntityDataResponse;
import com.firstrain.web.response.ItemWrapperResponse;
import com.firstrain.web.response.JSONResponse;
import com.firstrain.web.response.MonitorConfigResponse;
import com.firstrain.web.response.MonitorInfoResponse;
import com.firstrain.web.response.UserIdWrapperResponse;
import com.firstrain.web.util.AuthAPIResponseThreadLocal;
import com.firstrain.web.util.UserInfoThreadLocal;
import com.firstrain.web.wrapper.EntityDataWrapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import org.springframework.context.support.ResourceBundleMessageSource;

import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import static com.firstrain.web.response.JSONResponse.ResStatus.PARTIAL_SUCCESS;
import static com.firstrain.web.response.JSONResponse.ResStatus.SUCCESS;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.reflect.Whitebox.setInternalState;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        ResponseDecoratorService.class,
        ResourceBundleMessageSource.class,
        ResourceBundleMessageSource.class,
        Constant.class,
        JSONUtility.class,
        AuthAPIResponseThreadLocal.class,
        UserInfoThreadLocal.class
})
public class ResponseDecoratorServiceTest {

    private static final long OWNED_BY = 12L;
    private static final int ITEM_COUNT = 100;
    private static final String MONITOR_NAME = "monitor";
    private static final long FAV_USR_ITEM_ID = 75L;
    private static final String MONITOR_ID = "45635";
    private static final String USER_ID = "654";
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
    private static final String USER_COMPANY = "userCompany";
    private static final long FR_MONITOR_ID = 97L;
    private static final String USER_LAST_NAME = "lastname";
    private static final String USER_FIRST_NAME = "firstname";
    private static final String USR_TIMEZONE = "utc";
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
    private static final String TOPIC_IDS_CSV = "345";
    private static final int DOC_SCORE = 90;
    private static final long GROUP_ID = 120L;
    private static final String DOC_CONTENT_TYPE = "application/json";
    private static final String SEARCH_TOKEN = "token";
    private static final String DOC_QUOTE_TEXT = "docQuoteText";
    private static final String DOC_QUOTE_PERSON = "docQuotePerson";
    private static final String USER_NAME_VAL = "user";
    private static final String MESSAGE_RESOURCE = "message";
    private static final String ENT_ID = "2352";
    private static final int ONE_DAY_ACCOUNT = 10;
    private static final String ENT_NAME = "entName";
    private static final String QUERY_STR = "queryStr";
    private static final String QUERY_NAME = "queryName";
    private static final short INDUSTRY_CLASS_ID = 25;
    private static final short SEC_COUNT = 20;
    private static final int SEC_HEIGHT = 70;
    private static final short SEC_START = 50;
    private static final int SEC_WIDTH = 300;
    private static final int BASE_FONTS_SIZE = 300;
    private static final String EMAIL_TEMPLATE = "emailTemplate";
    private static final String EMAIL_NAME = "emailName";
    private static final long EMAIL_ID = 8758L;
    private static final String SEARCH_FILTER = "searchFilter";
    private static final String SEARCH_NAME = "searhcName";
    private static final String SEARCH_ID = "3276";
    private static final String EMAIL_TIMEsTAMP = "567893";
    private static final String EMAIL_SUBJECT = "subject";
    private static final int COUNT = 100;
    private static final int START_VALUE = 20;
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
    private static final String PRIMARY_TICKER = "ticker";
    private static final String ENT_STATE = "state";
    private static final String ENT_ADDRESS = "addr";
    private static final String ENT_CITY = "ist";
    private static final String ENT_COUNTRY = "TR";
    private static final String ENT_WEBSITE = "www.website.com";
    private static final String ENT_ZIP = "1006";
    private static final String ENT_DOMAIN = "domain";
    private static final String COMP_LOGO = "logo";
    private static final String CHART_ID = "6475";
    private static final String WRAPPER_ID = "8789";
    private static final String HTML_BI = "546";
    private static final String HTML_FR = "2314";
    private static final String FQ_VALUE = "fq";
    private static final int ERROR_CODE = 200;
    private static final short RELEVANCE_SCORE = 5;
    private static final String RELEVANCE_BAND = "band";
    
    @Mock
    private ResourceBundleMessageSource messageSource;
    @Mock
    private com.firstrain.frapi.pojo.Entity primaryEntity;
    @Mock
    private com.firstrain.frapi.pojo.Entity statusEntity;
    @Mock
    private com.firstrain.frapi.domain.Entity entityt;
    @Mock
    private HttpServletResponse response;
    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;
    @InjectMocks
    public final ResponseDecoratorService service = new ResponseDecoratorService();
    @Rule
    public final ErrorCollector errorCollector = new ErrorCollector();

    @Before
    public void setUp() throws Exception {
        mockStatic(ResourceBundleMessageSource.class,
                Constant.class, JSONUtility.class,
                AuthAPIResponseThreadLocal.class,
                UserInfoThreadLocal.class);
        Whitebox.invokeMethod(service, "init");
        when(messageSource.getMessage(anyString(), any(Object[].class), any(Locale.class)))
                .thenReturn(MESSAGE_RESOURCE);
        when(Constant.getVersion()).thenReturn(API_VERSION);
        when(statusEntity.getType()).thenReturn(Integer.parseInt(TOPIC_IDS_CSV));
        AuthAPIResponse authAPIResponse = createAuthAPIResponse();
        when(AuthAPIResponseThreadLocal.get()).thenReturn(authAPIResponse);
        Map<String, Object> stringObjectMap = new HashMap<String, Object>();
        stringObjectMap.put(RequestParsingService.defaultSpec.TOPIC_DIMENSION_FOR_TAGGING, TOPIC_IDS_CSV);
        when(JSONUtility.deserialize(anyString(), eq(Map.class)))
                .thenReturn(stringObjectMap);
        User user = new User();
        user.setUserType(DefaultEnums.UserType.ABU);
        user.setUserId(USER_ID);
        when(UserInfoThreadLocal.get()).thenReturn(user);
    }

    @Test
    public void getAuthKeyResponse() {
        //Act
        AuthKeyResponse keyResponse = service.getAuthKeyResponse(createAuthAPIResponse());
        //Assert
        errorCollector.checkThat(keyResponse.getStatus(), is(SUCCESS));
        errorCollector.checkThat(keyResponse.getVersion(), is(API_VERSION));
        AuthKey authKey = keyResponse.getResult();
        errorCollector.checkThat(authKey.getAuthKey(), is(AUTH_KEY));
    }

    @Test
    public void getUserIdResponse() {
        //Arrange

        //Act
        UserIdWrapperResponse response = service.getUserIdResponse(createUserAPIResponse(), MSG_KEY);
        //Assert
        errorCollector.checkThat(response.getStatus(), is(SUCCESS));
        errorCollector.checkThat(response.getMessage(), is(MESSAGE_RESOURCE));
        errorCollector.checkThat(response.getVersion(), is(API_VERSION));
    }

    @Test
    public void getDeleteUserResponse() {
        //Act
        JSONResponse response = service.getDeleteUserResponse(MSG_KEY);
        //Assert
        errorCollector.checkThat(response.getStatus(), is(SUCCESS));
        errorCollector.checkThat(response.getVersion(), is(API_VERSION));

    }

    @Test
    public void givenGetAddRemoveEntityResponseWhenAddedAndRemoved() {
        //Act
        MonitorInfoResponse response = service.getAddRemoveEntityResponse(createMonitorAPIResponse(), MSG_KEY);
        //Assert
        errorCollector.checkThat(response.getVersion(), is(API_VERSION));
        errorCollector.checkThat(response.getStatus(), is(SUCCESS));
        errorCollector.checkThat(response.getResult(), is(notNullValue()));
        errorCollector.checkThat(response.getMessage(), is("messagemessage"));
    }

    @Test
    public void givenGetAddRemoveEntityResponseWhenAddedAndRemovedNullThenResponse() {
        //Arrange
        MonitorAPIResponse apiResponse = createMonitorAPIResponse();
        Entity ent = createEntity();
        ent.setRemoved(null);
        ent.setAdded(null);
        apiResponse.setEntities(Collections.singletonList(ent));
        //Act
        MonitorInfoResponse response = service.getAddRemoveEntityResponse(apiResponse, MSG_KEY);
        //Assert
        errorCollector.checkThat(response.getResult(), is(notNullValue()));
        errorCollector.checkThat(response.getMessage(), is("messagemessage"));
        errorCollector.checkThat(response.getVersion(), is(API_VERSION));
        errorCollector.checkThat(response.getStatus(), is(PARTIAL_SUCCESS));
        errorCollector.checkThat(response.getErrorCode(), is(StatusCode.PARTIAL_SUCCESS));
    }

    @Test
    public void givenGetAddRemoveEntityResponseWhenAddedFalseThenResponse() {
        //Arrange
        MonitorAPIResponse apiResponse = createMonitorAPIResponse();
        Entity ent = createEntity();
        ent.setAdded(false);
        apiResponse.setEntities(Collections.singletonList(ent));
        //Act
        MonitorInfoResponse response = service.getAddRemoveEntityResponse(apiResponse, MSG_KEY);
        //Assert
        errorCollector.checkThat(response.getResult(), is(notNullValue()));
        errorCollector.checkThat(response.getMessage(), is("messagemessage"));
        errorCollector.checkThat(response.getVersion(), is(API_VERSION));
        errorCollector.checkThat(response.getStatus(), is(PARTIAL_SUCCESS));
        errorCollector.checkThat(response.getErrorCode(), is(StatusCode.PARTIAL_SUCCESS));
    }

    @Test
    public void givenGetAddRemoveEntityResponseWhenNotRemovedThenResponse() {
        //Arrange
        MonitorAPIResponse apiResponse = createMonitorAPIResponse();
        Entity ent = createEntity();
        ent.setRemoved(false);
        apiResponse.setEntities(Collections.singletonList(ent));
        //Act
        MonitorInfoResponse response = service.getAddRemoveEntityResponse(apiResponse, MSG_KEY);
        //Assert
        errorCollector.checkThat(response.getResult(), is(notNullValue()));
        errorCollector.checkThat(response.getMessage(), is("messagemessage"));
        errorCollector.checkThat(response.getVersion(), is(API_VERSION));
        errorCollector.checkThat(response.getStatus(), is(PARTIAL_SUCCESS));
        errorCollector.checkThat(response.getErrorCode(), is(StatusCode.PARTIAL_SUCCESS));
    }

    @Test
    public void getMonitorConfigResponse() {
        //Act
        MonitorConfigResponse response = service.getMonitorConfigResponse(createMonitorConfig());
        //Assert
        errorCollector.checkThat(response.getResult(), is(notNullValue()));
        errorCollector.checkThat(response.getMessage(), is("message"));
        errorCollector.checkThat(response.getVersion(), is(API_VERSION));
        errorCollector.checkThat(response.getStatus(), is(SUCCESS));
    }

    @Test
    public void givenGetItemWrapperResponseWhenIndustryClassIdIsNotNullThenResponse() {
        //Act
        ItemWrapperResponse response = service.getItemWrapperResponse(createTweetSet(), MSG_KEY, INDUSTRY_CLASS_ID,
                false);
        //Assert
        errorCollector.checkThat(response.getResult(), is(notNullValue()));
        errorCollector.checkThat(response.getMessage(), is("message"));
        errorCollector.checkThat(response.getVersion(), is(API_VERSION));
        errorCollector.checkThat(response.getStatus(), is(SUCCESS));
    }

    @Test
    public void getEntityDataResponse() {
        //Arrange
        SectionSpec sectionSpec = new SectionSpec();
        sectionSpec.setWidth(SEC_WIDTH);
        sectionSpec.setStart(SEC_START);
        sectionSpec.setNeedTweetAccelerometer(true);
        sectionSpec.setNeedPagination(true);
        sectionSpec.setNeedRelatedDoc(true);
        sectionSpec.setHeight(SEC_HEIGHT);
        sectionSpec.setCount(SEC_COUNT);
        Map<BaseSet.SectionType, SectionSpec> sectionsMap = new HashMap<BaseSet.SectionType, SectionSpec>();
        sectionsMap.put(BaseSet.SectionType.FR, sectionSpec);
        sectionsMap.put(BaseSet.SectionType.TT, sectionSpec);
        sectionsMap.put(BaseSet.SectionType.MD, sectionSpec);
        sectionsMap.put(BaseSet.SectionType.TWT, sectionSpec);
        sectionsMap.put(BaseSet.SectionType.GL, sectionSpec);
        sectionsMap.put(BaseSet.SectionType.RL, sectionSpec);
        sectionsMap.put(BaseSet.SectionType.FR, sectionSpec);
        sectionsMap.put(BaseSet.SectionType.BI, sectionSpec);
        //Act
        EntityDataResponse response = service.getEntityDataResponse(createMonitorAPIResponse(), MSG_KEY
                , sectionsMap, null, false);
        //Assert
        errorCollector.checkThat(response.getStatus(), is(SUCCESS));
    }

    @Test
    public void getEmailResponse() {
        //Act
        EntityDataResponse response = service.getEmailResponse(createEmailResponse(), MSG_KEY);
        //Assert
        errorCollector.checkThat(response.getStatus(), is(SUCCESS));
        errorCollector.checkThat(response.getVersion(), is(API_VERSION));
        errorCollector.checkThat(response.getMessage(), is("messagemessage"));
        EntityDataWrapper dataWrapper = response.getResult();
        errorCollector.checkThat(dataWrapper.getName(), is(EMAIL_NAME));
        errorCollector.checkThat(dataWrapper.getId(), is("EM:8758"));
    }

    @Test
    public void givenGetEntityDataResponseWhenMsgKeyAndMonitorApiResponseProvidedThenResult() {
        //Act
        EntityDataResponse response = service.getEntityDataResponse(createMonitorEmailAPIResponse(), MSG_KEY);
        //Assert
        errorCollector.checkThat(response.getStatus(), is(SUCCESS));
        errorCollector.checkThat(response.getVersion(), is(API_VERSION));
        errorCollector.checkThat(response.getMessage(), is("messagemessage"));
        EntityDataWrapper dataWrapper = response.getResult();
        errorCollector.checkThat(dataWrapper.getName(), is(MONITOR_NAME));
        errorCollector.checkThat(dataWrapper.getId(), is("M:45635"));
    }

    @Test
    public void getEntityDataResponse2() {
        //Arrange
        SectionSpec sectionSpec = new SectionSpec();
        sectionSpec.setWidth(SEC_WIDTH);
        sectionSpec.setStart(SEC_START);
        sectionSpec.setNeedTweetAccelerometer(true);
        sectionSpec.setNeedPagination(true);
        sectionSpec.setNeedRelatedDoc(true);
        sectionSpec.setHeight(SEC_HEIGHT);
        sectionSpec.setCount(SEC_COUNT);
        Map<BaseSet.SectionType, SectionSpec> sectionsMap = new HashMap<BaseSet.SectionType, SectionSpec>();
        sectionsMap.put(BaseSet.SectionType.FR, sectionSpec);
        sectionsMap.put(BaseSet.SectionType.BI, sectionSpec);
        sectionsMap.put(BaseSet.SectionType.TT, sectionSpec);
        sectionsMap.put(BaseSet.SectionType.MD, sectionSpec);
        sectionsMap.put(BaseSet.SectionType.TWT, sectionSpec);
        sectionsMap.put(BaseSet.SectionType.GL, sectionSpec);
        sectionsMap.put(BaseSet.SectionType.RL, sectionSpec);
        sectionsMap.put(BaseSet.SectionType.FR, sectionSpec);

        Map<String, Object> ftlParams = new HashMap<String, Object>();
        ftlParams.put("name2", "key");
        //Act
        EntityDataResponse response = service.getEntityDataResponse(createSearchApiResponse(), MSG_KEY, sectionsMap
                , ftlParams);
        //Assert
        errorCollector.checkThat(response.getStatus(), is(SUCCESS));
        errorCollector.checkThat(response.getVersion(), is(API_VERSION));
        errorCollector.checkThat(response.getMessage(), is("message"));
    }

    @Test
    public void getConversationStartersResponse() {
        //Arrange
        Map<String, String> leadCompany = new HashMap<String, String>();
        leadCompany.put("token", "value");
        leadCompany.put("name", "value");
        Map<String, DefaultEnums.CoversationStarterType> starterTypeMap = new HashMap<String, DefaultEnums.CoversationStarterType>();
        starterTypeMap.put("key", DefaultEnums.CoversationStarterType.INDUSTRY_NEWS);
        //Act
        EntityDataResponse response = service.getConversationStartersResponse(createSearchApiResponse(),
                leadCompany, starterTypeMap, START_VALUE, COUNT);
        //Assert
    }

    @Test
    public void givenGetEntityDataResponseWhenResponseExistsThenResult() {
        //Act
        EntityDataResponse dataResponse = service.getEntityDataResponse(createEntityBrief(StatusCode.REQUEST_SUCCESS)
                , MSG_KEY, createSectionSpecMap(), response, INDUSTRY_CLASS_ID, false, true
                , SCOP_DIRECTIVE);
        //Assert
        errorCollector.checkThat(dataResponse.getStatus(), is(SUCCESS));
        errorCollector.checkThat(dataResponse.getVersion(), is(API_VERSION));
        errorCollector.checkThat(dataResponse.getResult(), is(notNullValue()));
    }

    @Test
    public void getEntityDataResponse4() {
        //Act
        EntityDataResponse dataResponse = service.getEntityDataResponse(createEntityBrief(StatusCode.REQUEST_SUCCESS)
                , MSG_KEY, createSectionSpecMap(), response, INDUSTRY_CLASS_ID, false);
        //Assert
        errorCollector.checkThat(dataResponse.getStatus(), is(SUCCESS));
        errorCollector.checkThat(dataResponse.getVersion(), is(API_VERSION));
        errorCollector.checkThat(dataResponse.getResult(), is(notNullValue()));
    }

    @Test
    public void givenGetMatchedEntityDataResponseWhenInputEntityIsCompanyThenResult() {
        //Act
        EntityDataResponse response = service.getMatchedEntityDataResponse(createEntityBrief(StatusCode.REQUEST_SUCCESS)
                , DefaultEnums.INPUT_ENTITY_TYPE.COMPANY);
        //Assert
        errorCollector.checkThat(response.getStatus(), is(SUCCESS));
        errorCollector.checkThat(response.getVersion(), is(API_VERSION));
        errorCollector.checkThat(response.getResult(), is(notNullValue()));
    }

    @Test
    public void givenGetMatchedEntityDataResponseWhenInputEntityIsIndustryThenResult() {
        //Act
        EntityDataResponse response = service.getMatchedEntityDataResponse(createEntityBrief(StatusCode.REQUEST_SUCCESS)
                , DefaultEnums.INPUT_ENTITY_TYPE.INDUSTRY);
        //Assert
        errorCollector.checkThat(response.getStatus(), is(SUCCESS));
        errorCollector.checkThat(response.getVersion(), is(API_VERSION));
        errorCollector.checkThat(response.getResult(), is(notNullValue()));
    }

    @Test
    public void givenGetMatchedEntityDataResponseWhenInputEntityIsTopicThenResult() {
        //Act
        EntityDataResponse response = service.getMatchedEntityDataResponse(createEntityBrief(StatusCode.REQUEST_SUCCESS)
                , DefaultEnums.INPUT_ENTITY_TYPE.TOPIC);
        //Assert
        errorCollector.checkThat(response.getStatus(), is(SUCCESS));
        errorCollector.checkThat(response.getVersion(), is(API_VERSION));
        errorCollector.checkThat(response.getResult(), is(notNullValue()));
    }

    @Test
    public void getEntityMapResponse() {
        //Act
        EntityDataResponse response = service.getEntityMapResponse(createEntityBrief(StatusCode.REQUEST_SUCCESS)
                , MSG_KEY);
        //Assert
        errorCollector.checkThat(response.getStatus(), is(SUCCESS));
        errorCollector.checkThat(response.getVersion(), is(API_VERSION));
        errorCollector.checkThat(response.getResult(), is(notNullValue()));
    }

    @Test
    public void getEntityPeersResponse() {
        //Act
        EntityDataResponse response = service.getEntityPeersResponse(createEntityBrief(StatusCode.ENTITY_NOT_FOUND),
                MSG_KEY);
        //Assert
        errorCollector.checkThat(response.getStatus(), is(SUCCESS));
        errorCollector.checkThat(response.getVersion(), is(API_VERSION));
        errorCollector.checkThat(response.getResult(), is(notNullValue()));
    }

    @Test
    public void getDnBEntityStatusResponse() {
        //Act
        DnBEntityStatusResponse response = service.getDnBEntityStatusResponse(Collections.singletonList(createEntity()));
        //Assert
        errorCollector.checkThat(response.getStatus(), is(SUCCESS));
        errorCollector.checkThat(response.getVersion(), is(API_VERSION));
        errorCollector.checkThat(response.getResult(), is(notNullValue()));
    }

    @Test
    public void getSuccessMsg() {
        //Act
        JSONResponse response = service.getSuccessMsg(MSG_KEY);
        //Assert
        errorCollector.checkThat(response.getVersion(), is(API_VERSION));
        errorCollector.checkThat(response.getStatus(), is(SUCCESS));
    }

    @Test
    public void makeTweetsFieldsNullable() {
        //Arrange
        Tweet tweetLocal = new Tweet();
        tweetLocal.setBookmarked(true);
        tweetLocal.setItemId(ITEM_ID);
        tweetLocal.setUserName(USER_ID);
        tweetLocal.setTitle(AUTHOR_TITLE);
        //Act
        Void result = service.makeTweetsFieldsNullable(Collections.singletonList(tweetLocal));
        //Assert
        assertNull(result);
    }

    @Test
    public void setChartDataForWebVolume() {
        //Act
        service.setChartDataForWebVolume(createEntityDataResponse(), CHART_ID, createSectionSpec(), true, response);
        //Assert
    }

    @Test
    public void setChartDataForHtml() {
        //Arrange
        Set<BaseSet.SectionType> sectionTypes = new HashSet<BaseSet.SectionType>();
        sectionTypes.add(BaseSet.SectionType.TT);
        sectionTypes.add(BaseSet.SectionType.BI);
        sectionTypes.add(BaseSet.SectionType.MD);
        sectionTypes.add(BaseSet.SectionType.TWT);
        sectionTypes.add(BaseSet.SectionType.GL);
        sectionTypes.add(BaseSet.SectionType.RL);
        //Act
        service.setChartDataForHtml(sectionTypes, createEntityDataResponse(),
                CHART_ID, createSectionSpecMap(), true, FQ_VALUE, response);
        //Assert
        verify(response, atLeastOnce()).setHeader(stringArgumentCaptor.capture(),
                stringArgumentCaptor.capture());
        errorCollector.checkThat(stringArgumentCaptor.getAllValues(),
                is(Arrays.asList("frContentTt", "false", "frContentBi", "false", "frContentMd", "false", "frContentTwt",
                        "false", "frContentGl", "false", "frContentRl", "false")));
    }

    @Test
    public void getMetaData() {
        //Arrange
        com.firstrain.web.pojo.Document documentLocal = new com.firstrain.web.pojo.Document();
        documentLocal.setDate(new Date());
        documentLocal.setTitle(AUTHOR_TITLE);
        EntityStandard standard = new EntityStandard();
        standard.setName(ENT_NAME);
        standard.setSearchToken(SEARCH_TOKEN);
        standard.setAdded(true);
        standard.setRemoved(true);
        standard.setRelevanceBand(RELEVANCE_BAND);
        standard.setRelevanceScore(RELEVANCE_SCORE);
        standard.setErrorCode(ERROR_CODE);
        documentLocal.setEntity(Collections.singletonList(standard));
        Tweet tweetLocal = new Tweet();
        tweetLocal.setTitle(AUTHOR_TITLE);
        tweetLocal.setUserName(USER_FIRST_NAME);
        tweetLocal.setItemId(ITEM_ID);
        tweetLocal.setBookmarked(true);
        tweetLocal.setTweetText(TWEET_TEXT);
        tweetLocal.setEntity(standard);
        com.firstrain.web.pojo.Event eventLocal = new com.firstrain.web.pojo.Event();
        eventLocal.setLink(EVENTSET_LINK);
        eventLocal.setContentType(DOC_CONTENT_TYPE);
        eventLocal.setId(EVENT_ID);
        eventLocal.setEventType(DefaultEnums.EventInformationEnum.MT_DEPARTURE);
        eventLocal.setNewRegion(NEW_REGION);
        eventLocal.setEntity(Collections.singletonList(standard));
        //Act
        MetaData metaData = service.getMetaData(Collections.singletonList(documentLocal)
                , Collections.singletonList(tweetLocal), Collections.singletonList(eventLocal));
        //Assert
        errorCollector.checkThat(metaData.getFt(), is(ENT_NAME));
        errorCollector.checkThat(metaData.getFr(), is(ENT_NAME));
        errorCollector.checkThat(metaData.getE(), is(ENT_NAME));
    }

    @Test
    public void givenExcludeTweetInfoWhenTweetExclusionCsvIsNullThenTrue() {
        //Act
        boolean result = service.excludeTweetInfo(ENTERPRISE_ID);
        //Assert
        assertTrue(result);
    }

    @Test
    public void givenExcludeTweetInfoWhenTweetExclusionCsvIsNotNullThenTrue() {
        //Arrange
        setInternalState(service, "tweetExclusionCSV", "1,2,3");
        //Act
        boolean result = service.excludeTweetInfo(ENTERPRISE_ID);
        //Assert
        assertTrue(result);
    }

    @Test
    public void givenExcludeTweetInfoWhenTweetExclusionCsvIsNotNullAnEtIdMatchesThenTrue() {
        //Arrange
        setInternalState(service, "tweetExclusionCSV", "" + ENTERPRISE_ID + ",2,3");
        //Act
        boolean result = service.excludeTweetInfo(ENTERPRISE_ID);
        //Assert
        assertFalse(result);
    }

    private EntityDataResponse createEntityDataResponse() {
        EntityDataResponse dataResponse = new EntityDataResponse();
        dataResponse.setStatus(SUCCESS);
        dataResponse.setVersion(API_VERSION);
        EntityDataWrapper dataWrapper = new EntityDataWrapper();
        dataWrapper.setId(WRAPPER_ID);
        EntityDataHtml dataHtml = new EntityDataHtml();
        dataHtml.setBi(HTML_BI);
        dataHtml.setFr(HTML_FR);
        dataHtml.setFrContentBi(true);
        dataHtml.setFrContentMd(true);
        dataHtml.setFrContentGl(true);
        dataHtml.setFrContentRl(true);
        dataHtml.setFrContentTt(true);
        dataHtml.setFrContentTwt(true);
        dataHtml.setFrContentWv(true);
        dataWrapper.setHtmlFrag(dataHtml);
        dataResponse.setResult(dataWrapper);
        return dataResponse;
    }

    private SectionSpec createSectionSpec() {
        SectionSpec sectionSpec = new SectionSpec();
        sectionSpec.setWidth(SEC_WIDTH);
        sectionSpec.setStart(SEC_START);
        sectionSpec.setNeedTweetAccelerometer(true);
        sectionSpec.setNeedPagination(true);
        sectionSpec.setNeedRelatedDoc(true);
        sectionSpec.setHeight(SEC_HEIGHT);
        sectionSpec.setCount(SEC_COUNT);
        Map<String, String> callBackMethod = new HashMap<String, String>();
        callBackMethod.put("key", "value");
        sectionSpec.setCallbackMethodsMap(callBackMethod);
        return sectionSpec;
    }

    private Map<BaseSet.SectionType, SectionSpec> createSectionSpecMap() {
        SectionSpec sectionSpec = new SectionSpec();
        sectionSpec.setWidth(SEC_WIDTH);
        sectionSpec.setStart(SEC_START);
        sectionSpec.setNeedTweetAccelerometer(true);
        sectionSpec.setNeedPagination(true);
        sectionSpec.setNeedRelatedDoc(true);
        sectionSpec.setHeight(SEC_HEIGHT);
        sectionSpec.setCount(SEC_COUNT);
        Map<String, String> callBackMethod = new HashMap<String, String>();
        callBackMethod.put("key", "value");
        sectionSpec.setCallbackMethodsMap(callBackMethod);
        Map<BaseSet.SectionType, SectionSpec> sectionsMap = new HashMap<BaseSet.SectionType, SectionSpec>();
        sectionsMap.put(BaseSet.SectionType.FR, sectionSpec);
        sectionsMap.put(BaseSet.SectionType.BI, sectionSpec);
        sectionsMap.put(BaseSet.SectionType.TT, sectionSpec);
        sectionsMap.put(BaseSet.SectionType.MD, sectionSpec);
        sectionsMap.put(BaseSet.SectionType.TWT, sectionSpec);
        sectionsMap.put(BaseSet.SectionType.GL, sectionSpec);
        sectionsMap.put(BaseSet.SectionType.RL, sectionSpec);
        sectionsMap.put(BaseSet.SectionType.FR, sectionSpec);
        return sectionsMap;
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

    private SearchAPIResponse createSearchApiResponse() {
        SearchAPIResponse apiResponse = new SearchAPIResponse();
        apiResponse.setStatusCode(StatusCode.REQUEST_SUCCESS);
        apiResponse.setWebResults(createDocumentSet());
        Map<String, Entity> entityMap = new HashMap<String, Entity>();
        entityMap.put("key", createEntity());
        apiResponse.setTokenVsEntityMap(entityMap);
        return apiResponse;
    }

    private TweetSet createTweetSet() {
        TweetSet tweetSet = new TweetSet();
        tweetSet.setTotal(TWEET_TOTAL);
        tweetSet.setTweets(Collections.singletonList(createDomainTweet()));
        tweetSet.setSectionType(BaseSet.SectionType.FR);
        return tweetSet;
    }

    private static Entity createEntity() {
        Entity ent = new Entity();
        ent.setId(ENT_ID);
        ent.setOneDayDocCount(ONE_DAY_ACCOUNT);
        ent.setSelected(true);
        ent.setSearchToken(SEARCH_TOKEN);
        ent.setRemoved(true);
        ent.setAdded(true);
        ent.setRemoved(true);
        ent.setName(ENT_NAME);
        ent.setType(FR_ICategory.TOPIC_DIMENSION_JUNK_TYPE);
        return ent;
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

    private EntityBriefInfo createEntityBrief(int statusCode) {
        EntityBriefInfo briefInfo = createEntityBriefInfo(statusCode);
        return arrangeEntityBriefInfo(briefInfo);
    }

    private EntityBriefInfo arrangeEntityBriefInfo(final EntityBriefInfo briefInfo) {
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

    private static MonitorConfig createMonitorConfig() {
        com.firstrain.frapi.domain.MonitorConfig monitorConfig = new com.firstrain.frapi.domain.MonitorConfig();
        monitorConfig.setMonitorId(MONITOR_ID);
        monitorConfig.setMonitorName(MONITOR_NAME);
        monitorConfig.setOwnedByType(OWNED_BY_TYPE);
        monitorConfig.setOwnedBy(OWNED_BY);
        ItemDetail detail = new ItemDetail();
        detail.setQueryName(QUERY_NAME);
        detail.setQueryString(QUERY_STR);
        monitorConfig.setQueries(Collections.singletonList(detail));
        return monitorConfig;
    }

    private static AuthAPIResponse createAuthAPIResponse() {
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

    private static UserAPIResponse createUserAPIResponse() {
        UserAPIResponse response = new UserAPIResponse();
        response.setStatusCode(StatusCode.USER_DOESNOT_OWN_MONITOR);
        User user = new User();
        user.setUserId(USER_ID);
        user.setMembershipType(DefaultEnums.MembershipType.ADMIN);
        user.setStatus(DefaultEnums.UserValidationStatus.VALIDATION_SUCCESS);
        user.setOwnedBy(OWNED_BY);
        user.setUserName(USER_NAME_VAL);
        user.setUserCompany(USER_COMPANY);
        user.setFrMonitorId(FR_MONITOR_ID);
        user.setLastName(USER_LAST_NAME);
        user.setFirstName(USER_FIRST_NAME);
        user.setTimeZone(USR_TIMEZONE);
        user.setDomain(USR_DOMAIN);
        user.setOwnedByType(OWNED_BY_TYPE);
        user.setMonitorOrderType(MONITOR_ORDER_TYPE);
        user.setTemplateId(TEMPLATE_ID);
        response.setUser(user);
        return response;
    }

    private MonitorAPIResponse createMonitorAPIResponse() {
        MonitorAPIResponse response = new MonitorAPIResponse();
        response.setEntities(Collections.singletonList(createEntity()));
        response.setStatusCode(StatusCode.NO_ITEMS_IN_MONITOR);
        response.setMonitorId(Long.parseLong(MONITOR_ID));
        response.setMonitorName(MONITOR_NAME);
        EntityStatus entityStatus = new EntityStatus();
        entityStatus.setEntityStatus(true);
        entityStatus.setId(ENT_STATUS_ID);
        entityStatus.setName(ENT_STATUS_NAME);
        response.setEntityStatus(entityStatus);
        com.firstrain.frapi.domain.MonitorConfig monitorConfig = new com.firstrain.frapi.domain.MonitorConfig();
        monitorConfig.setMonitorId(MONITOR_ID);
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
        monitorInfo.setMonitorId(MONITOR_ID);
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

    private MonitorEmailAPIResponse createMonitorEmailAPIResponse() {
        MonitorEmailAPIResponse emailAPIResponse = new MonitorEmailAPIResponse();
        emailAPIResponse.setStatusCode(StatusCode.NO_ITEMS_IN_MONITOR);
        emailAPIResponse.setEmailTemplate(EMAIL_TEMPLATE);
        emailAPIResponse.setMonitorId(Long.parseLong(MONITOR_ID));
        emailAPIResponse.setMonitorName(MONITOR_NAME);
        MonitorEmail email = new MonitorEmail();
        email.setEmailId(String.valueOf(EMAIL_ID));
        email.setSubject(EMAIL_SUBJECT);
        email.setTimeStamp(EMAIL_TIMEsTAMP);
        emailAPIResponse.setEmails(Collections.singletonList(email));
        return emailAPIResponse;
    }

    private MonitorBriefDetail createMonitorBriefDetail() {
        MonitorBriefDetail briefDetail = new MonitorBriefDetail();
        briefDetail.setMonitorId(MONITOR_ID);
        briefDetail.setMonitorName(MONITOR_NAME);
        BaseSet baseSet = createBaseSet();
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

    private BaseSet createBaseSet() {
        BaseSet baseSet = new BaseSet();
        baseSet.setHasMore(true);
        baseSet.setSectionType(BaseSet.SectionType.AC);
        baseSet.setTotalCount(TOTAL_COUNT);
        return baseSet;
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

    private EmailResponse createEmailResponse() {
        EmailResponse response = new EmailResponse();
        response.setStatusCode(StatusCode.NO_ITEMS_IN_MONITOR);
        response.setEmailId(EMAIL_ID);
        response.setEmailName(EMAIL_NAME);
        response.setEmailTemplate(EMAIL_TEMPLATE);
        EmailDetail detail = new EmailDetail();
        BaseSet set = new BaseSet();
        set.setSectionType(BaseSet.SectionType.FR);
        detail.setPerfStats(set);
        Search searchLocal = new Search();
        searchLocal.setSearchFilter(SEARCH_FILTER);
        searchLocal.setSearchName(SEARCH_NAME);
        searchLocal.setSearchId(SEARCH_ID);
        searchLocal.setSearchFilter(SEARCH_FILTER);
        searchLocal.setDocuments(Collections.singletonList(createDocument()));
        detail.setSearches(Collections.singletonList(searchLocal));
        response.setEmailDetail(detail);
        return response;
    }
}
