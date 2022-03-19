package com.firstrain.web.service;

import com.firstrain.frapi.domain.Document;
import com.firstrain.frapi.domain.Entity;
import com.firstrain.frapi.domain.EntityStatus;
import com.firstrain.frapi.domain.MonitorBucketForTitle;
import com.firstrain.frapi.domain.MonitorInfo;
import com.firstrain.frapi.domain.User;
import com.firstrain.frapi.domain.VisualizationData;
import com.firstrain.frapi.pojo.AuthAPIResponse;
import com.firstrain.frapi.pojo.MonitorAPIResponse;
import com.firstrain.frapi.pojo.MonitorBriefDetail;
import com.firstrain.frapi.pojo.UserAPIResponse;
import com.firstrain.frapi.pojo.wrapper.BaseSet;
import com.firstrain.frapi.pojo.wrapper.DocumentSet;
import com.firstrain.frapi.pojo.wrapper.TweetSet;
import com.firstrain.frapi.util.CompanyTeam;
import com.firstrain.frapi.util.ContentType;
import com.firstrain.frapi.util.DefaultEnums;
import com.firstrain.frapi.util.StatusCode;
import com.firstrain.utils.JSONUtility;
import com.firstrain.web.pojo.Content;
import com.firstrain.web.pojo.MonitorDetails;
import com.firstrain.web.pojo.Source;
import com.firstrain.web.pojo.Tweet;
import com.firstrain.web.response.EntityDataResponse;
import com.firstrain.web.response.EntityResponse;
import com.firstrain.web.response.ItemWrapperResponse;
import com.firstrain.web.response.JSONResponse;
import com.firstrain.web.response.MonitorDetailsResponse;
import com.firstrain.web.response.MonitorInfoResponse;
import com.firstrain.web.response.MonitorWrapperResponse;
import com.firstrain.web.response.UserWrapperResponse;
import com.firstrain.web.service.core.Constant;
import com.firstrain.web.service.core.RequestParsingService;
import com.firstrain.web.service.core.ResponseDecoratorService;
import com.firstrain.web.util.AuthAPIResponseThreadLocal;
import com.firstrain.web.wrapper.ItemWrapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.support.ResourceBundleMessageSource;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.sql.Timestamp;
import static org.apache.commons.lang.SystemUtils.USER_NAME;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        BaseResponseDecoratorService.class,
        ResourceBundleMessageSource.class,
        Constant.class,
        JSONUtility.class,
        AuthAPIResponseThreadLocal.class
})
public class BaseResponseDecoratorServiceTest {

    private static final String BUCKET_TITLE = "bucket_title";
    private static final String MONITOR_BUCKET = "monitor_bucket";
    private static final long OWNED_BY = 12L;
    private static final int ITEM_COUNT = 100;
    private static final String MONITOR_NAME = "monitor";
    private static final long FAV_USR_ITEM_ID = 75L;
    private static final String MONITOR_ID = "45635";
    private static final String USER_ID = "654";
    private static final String MON_DETAILS_EMAIL = "user@mail.com";
    private static final String MON_USR_NAME = "";
    private static final String RESOURCE_MESSAGE = "resource_message";
    private static final String MONITOR_VERSION = "1.3";
    private static final String MSG_KEY = "3234";
    private static final long ITEM_ID = 452L;
    private static final String TWEET_TEXT = "tweet_text";
    private static final String TWEETS_HTML = "<p>twwet_html</p>";
    private static final String AUTHOR_TITLE = "title";
    private static final String AUTHOR_NAME = "author";
    private static final String AUTHOR_DESC = "author desc";
    private static final String AUTHOR_AVATAR = "avatr";
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
    private static final long GROUP_ID = 120L;
    private static final String DOC_CONTENT_TYPE = "application/json";
    private static final String DOC_SNIPPET = "snippet";
    private static final String DOC_GROUP_ID = "654";
    private static final String DOC_IMG = "doc_img";
    private static final String DOC_LINK = "link";
    private static final String SEARCH_TOKEN = "token";
    private static final String SOURCE_NAME = "source";
    private static final String SOURCE_URL = "source_url";
    private static final String DOC_QUOTE_TEXT = "docQuoteText";
    private static final String DOC_QUOTE_PERSON = "docQuotePerson";
    @Mock
    private Entity entity;
    @Mock
    private ResourceBundleMessageSource messageSource;
    @Mock
    private com.firstrain.frapi.pojo.Entity primaryEntity;
    @Mock
    private com.firstrain.frapi.pojo.Entity statusEntity;
    @InjectMocks
    @Spy
    private final BaseResponseDecoratorService service = new ResponseDecoratorService();
    @Rule
    public final ErrorCollector errorCollector = new ErrorCollector();

    @Before
    public void setUp() throws Exception {
        mockStatic(Constant.class, JSONUtility.class,
                AuthAPIResponseThreadLocal.class);
        when(statusEntity.getType()).thenReturn(Integer.parseInt(TOPIC_IDS_CSV));
        AuthAPIResponse authAPIResponse = createAuthAPIResponseThreadLocal();
        when(AuthAPIResponseThreadLocal.get()).thenReturn(authAPIResponse);
        Map<String, Object> stringObjectMap = new HashMap<String, Object>();
        stringObjectMap.put(RequestParsingService.defaultSpec.TOPIC_DIMENSION_FOR_TAGGING, TOPIC_IDS_CSV);
        when(JSONUtility.deserialize(anyString(), eq(Map.class)))
                .thenReturn(stringObjectMap);
        when(messageSource.getMessage("user.monitors", null, Locale.getDefault()))
                .thenReturn(RESOURCE_MESSAGE);
        when(Constant.getVersion()).thenReturn(MONITOR_VERSION);
    }

    @Test
    public void getMonitorDetails() {
        //Arrange
        MonitorBucketForTitle bucketForTitle = createMonitorBucketForTitle();
        LinkedHashMap<String, List<MonitorInfo>> linkedHashMap = new LinkedHashMap<String, List<MonitorInfo>>();
        linkedHashMap.put(MONITOR_BUCKET, Collections.singletonList(createMonitorInfo()));
        //Act
        MonitorDetails details = service.getMonitorDetails(Collections.singletonList(bucketForTitle), linkedHashMap);
        //Assert
        com.firstrain.web.pojo.MonitorInfo monitorInfo = details.getMonitors().get(MONITOR_BUCKET).get(0);
        errorCollector.checkThat(monitorInfo.getMonitorId(), is(MONITOR_ID));
        errorCollector.checkThat(monitorInfo.getActiveMail(), is(true));
        errorCollector.checkThat(monitorInfo.getFavoriteItemId(), is(FAV_USR_ITEM_ID));
        errorCollector.checkThat(monitorInfo.getMonitorName(), is(MONITOR_NAME));
    }

    @Test
    public void getMonitorDetailsResponse() {
        //Act
        MonitorDetailsResponse response = service.getMonitorDetailsResponse(createMonitorDetails());
        //Assert
        errorCollector.checkThat(response.getMessage(), is(RESOURCE_MESSAGE));
        errorCollector.checkThat(response.getStatus(), is(JSONResponse.ResStatus.SUCCESS));
        errorCollector.checkThat(response.getVersion(), is(MONITOR_VERSION));
    }

    @Test
    public void getItemWrapperResponse1() {
        //Act
        ItemWrapperResponse response = service.getItemWrapperResponse(createDocumentSet(), MSG_KEY);
        //Assert
        errorCollector.checkThat(response.getStatus(), is(JSONResponse.ResStatus.SUCCESS));
        errorCollector.checkThat(response.getVersion(), is(MONITOR_VERSION));
        errorCollector.checkThat(response.getResult(), is(notNullValue()));
    }

    @Test
    public void getItemWrapperResponse() {
        //Act
        ItemWrapperResponse response = service.getItemWrapperResponse(MSG_KEY);
        //Assert
        errorCollector.checkThat(response.getStatus(), is(JSONResponse.ResStatus.SUCCESS));
        errorCollector.checkThat(response.getVersion(), is(MONITOR_VERSION));
    }

    @Test
    public void getItemWrapper() {
        //Act
        ItemWrapper itemWrapper = service.getItemWrapper(createTweet());
        //Assert
        errorCollector.checkThat(itemWrapper.getData(), is(notNullValue()));
    }

    @Test
    public void getMonitorInfoResponse() {
        //Act
        MonitorInfoResponse response = service.getMonitorInfoResponse(createMonitorAPIResponse(), MSG_KEY);
        //Assert
        errorCollector.checkThat(response.getStatus(), is(JSONResponse.ResStatus.SUCCESS));
        errorCollector.checkThat(response.getVersion(), is(MONITOR_VERSION));
        errorCollector.checkThat(response.getResult(), is(notNullValue()));
    }

    @Test
    public void getMonitorWrapperResponse() {
        //Act
        MonitorWrapperResponse response = service.getMonitorWrapperResponse(createMonitorAPIResponse(), MSG_KEY);
        //Assert
        errorCollector.checkThat(response.getStatus(), is(JSONResponse.ResStatus.SUCCESS));
        errorCollector.checkThat(response.getVersion(), is(MONITOR_VERSION));
        errorCollector.checkThat(response.getResult(), is(notNullValue()));
    }

    @Test
    public void getEntityDataResponse() {
        //Act
        EntityDataResponse response = service.getEntityDataResponse(createMonitorAPIResponse(), MSG_KEY, true);
        //Assert
        errorCollector.checkThat(response.getStatus(), is(JSONResponse.ResStatus.SUCCESS));
        errorCollector.checkThat(response.getVersion(), is(MONITOR_VERSION));
        errorCollector.checkThat(response.getResult(), is(nullValue()));
    }

    @Test
    public void getDocumentsContent() {
        //Act
        Content content = service.getDocumentsContent(createMonitorBriefDetail());
        //Assert
        errorCollector.checkThat(content.getItemCount(), is(1));
        errorCollector.checkThat(content.getDocuments().size(), is(1));
    }

    @Test
    public void getTweetLst() {
        //Act
        List<com.firstrain.frapi.domain.Tweet> tweets = service.getTweetLst(createTweetSet());
        //Assert
        errorCollector.checkThat(tweets.size(), is(1));
        com.firstrain.frapi.domain.Tweet tweet = tweets.get(0);
        errorCollector.checkThat(tweet.getTweetId(), is(TWEET_ID));
        errorCollector.checkThat(tweet.getScreenName(), is(SCREEN_NAME));
        errorCollector.checkThat(tweet.getComboScore(), is(COMBO_SCORE));
        errorCollector.checkThat(tweet.getCoreTweet(), is(CORE_TWEET));
        errorCollector.checkThat(tweet.getTweet(), is(TWEET_TEXT));
        errorCollector.checkThat(tweet.getTitle(), is(AUTHOR_TITLE));
        errorCollector.checkThat(tweet.getUserImage(), is(USER_IMAGE));
    }

    @Test
    public void getUserResponse() {
        //Act
        UserWrapperResponse response = service.getUserResponse(createUserAPIResponse(), MSG_KEY);
        //Assert
        errorCollector.checkThat(response.getStatus(), is(JSONResponse.ResStatus.SUCCESS));
        errorCollector.checkThat(response.getVersion(), is(MONITOR_VERSION));
        errorCollector.checkThat(response.getResult(), is(notNullValue()));
    }

    @Test
    public void getMonitorEntityResponse() {
        //Act
        EntityResponse response = service.getMonitorEntityResponse(createEntityStatus());
        //Assert
        errorCollector.checkThat(response.getStatus(), is(JSONResponse.ResStatus.SUCCESS));
        errorCollector.checkThat(response.getVersion(), is(MONITOR_VERSION));
        errorCollector.checkThat(response.getResult(), is(notNullValue()));
    }

    @Test
    public void getTopicDimensions() {
        //Act
        List<Integer> topicDimensions = service.getTopicDimensions();
        //Assert
        errorCollector.checkThat(topicDimensions.size(), is(1));
    }

    @Test
    public void mustIgnoreEntityOtherSector() {

        doReturn(Arrays.asList(1, 2, 3)).when(service).getTopicDimensions();

        com.firstrain.frapi.domain.Document documentSource = new com.firstrain.frapi.domain.Document();
        com.firstrain.web.pojo.Document documentTarget = new com.firstrain.web.pojo.Document();

        com.firstrain.frapi.pojo.Entity entitySource = new com.firstrain.frapi.pojo.Entity();
        entitySource.setName("Other Sector");
        entitySource.setType(1);

        documentSource.setCatEntries(Collections.singletonList(entitySource));

        service.setEntitiesInDocument(documentSource, documentTarget);

        errorCollector.checkThat(documentTarget.getEntity(), nullValue());
    }

    private static MonitorBucketForTitle createMonitorBucketForTitle() {
        MonitorBucketForTitle bucketForTitle = new MonitorBucketForTitle();
        bucketForTitle.setMonitorBucket(MONITOR_BUCKET);
        bucketForTitle.setTitle(BUCKET_TITLE);
        return bucketForTitle;
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

    private static com.firstrain.frapi.domain.MonitorDetails createMonitorDetails() {
        com.firstrain.frapi.domain.MonitorDetails details = new com.firstrain.frapi.domain.MonitorDetails();
        details.setEmail(MON_DETAILS_EMAIL);
        details.setUserId(USER_ID);
        details.setUserName(MON_USR_NAME);
        details.setTopMonitorList(Collections.singletonList(createMonitorInfo()));
        details.setGroupMonitorList(Collections.singletonList(createMonitorInfo()));
        return details;
    }

    private static Tweet createTweet() {
        Tweet tweet = new Tweet();
        tweet.setAuthorAvatar(AUTHOR_AVATAR);
        tweet.setAuthorDescription(AUTHOR_DESC);
        tweet.setAuthorName(AUTHOR_NAME);
        tweet.setTitle(AUTHOR_TITLE);
        tweet.setTweetHtml(TWEETS_HTML);
        tweet.setTweetText(TWEET_TEXT);
        tweet.setUserName(USER_NAME);
        tweet.setItemId(ITEM_ID);
        tweet.setBookmarked(true);
        return tweet;
    }

    private static MonitorAPIResponse createMonitorAPIResponse() {
        MonitorAPIResponse response = new MonitorAPIResponse();
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
        return response;
    }

    private MonitorBriefDetail createMonitorBriefDetail() {
        MonitorBriefDetail briefDetail = new MonitorBriefDetail();
        briefDetail.setMonitorId(MONITOR_ID);
        briefDetail.setMonitorName(MONITOR_NAME);
        BaseSet baseSet = new BaseSet();
        baseSet.setHasMore(true);
        baseSet.setSectionType(BaseSet.SectionType.AC);
        baseSet.setTotalCount(TOTAL_COUNT);
        briefDetail.setPerfStats(baseSet);
        briefDetail.setOwnedBy(OWNED_BY);
        briefDetail.setOwnedByType(OWNED_BY_TYPE);
        VisualizationData data = new VisualizationData();
        data.setEntity(entity);
        briefDetail.setVisualizationData(data);
        briefDetail.setWebResults(createDocumentSet());
        briefDetail.setTweetList(createTweetSet());
        return briefDetail;
    }

    private DocumentSet createDocumentSet() {
        DocumentSet documentSet = new DocumentSet();
        documentSet.setFiling(true);
        documentSet.setCaption(DOC_CAPTION);
        documentSet.setScope(DOC_SCOPE);
        documentSet.setPrimaryIndustry(true);
        documentSet.setDocuments(Collections.singletonList(createDocument()));
        return documentSet;
    }

    private com.firstrain.web.pojo.Document createDocumentPojo() {
        com.firstrain.web.pojo.Document document = new com.firstrain.web.pojo.Document();
        document.setSnippet(DOC_SNIPPET);
        document.setContentType(DOC_CONTENT_TYPE);
        document.setId(DOCUMENT_ID);
        document.setGroupId(DOC_GROUP_ID);
        document.setImage(DOC_IMG);
        document.setLink(DOC_LINK);
        Source source = new Source();
        source.setSearchToken(SEARCH_TOKEN);
        source.setName(SOURCE_NAME);
        document.setSource(source);
        document.setItemId(ITEM_ID);
        document.setSourceUrl(SOURCE_URL);
        document.setBookmarked(true);
        document.setAdditionalMatchQualifierStr(ADDITIONAL_MATCH_QUALIFIER_STR);
        document.setDate(new Date());
        document.setTitle(AUTHOR_TITLE);
        return document;
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
        document.setCatEntries(Collections.singletonList(statusEntity));
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

    private TweetSet createTweetSet() {
        TweetSet tweetSet = new TweetSet();
        tweetSet.setTotal(TWEET_TOTAL);
        tweetSet.setTweets(Collections.singletonList(createDomainTweet()));
        tweetSet.setSectionType(BaseSet.SectionType.FR);
        return tweetSet;
    }

    private com.firstrain.frapi.domain.Tweet createDomainTweet() {
        com.firstrain.frapi.domain.Tweet tweet = new com.firstrain.frapi.domain.Tweet();
        tweet.setTweet(TWEET_TEXT);
        tweet.setCoreTweet(CORE_TWEET);
        tweet.setTweetId(TWEET_ID);
        tweet.setScreenName(SCREEN_NAME);
        tweet.setTitle(AUTHOR_TITLE);
        tweet.setUserImage(USER_IMAGE);
        tweet.setComboScore(COMBO_SCORE);
        tweet.setBookmarked(true);
        return tweet;
    }

    private UserAPIResponse createUserAPIResponse() {
        UserAPIResponse response = new UserAPIResponse();
        response.setStatusCode(StatusCode.USER_DOESNOT_OWN_MONITOR);
        User user = new User();
        user.setUserId(USER_ID);
        user.setMembershipType(DefaultEnums.MembershipType.ADMIN);
        user.setStatus(DefaultEnums.UserValidationStatus.VALIDATION_SUCCESS);
        user.setOwnedBy(OWNED_BY);
        user.setUserName(USER_NAME);
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

    private EntityStatus createEntityStatus() {
        EntityStatus entityStatus = new EntityStatus();
        entityStatus.setEntityStatus(true);
        entityStatus.setName(ENT_STATUS_NAME);
        entityStatus.setId(ENT_STATUS_ID);
        entityStatus.setEntity(statusEntity);
        return entityStatus;
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
}
