package com.firstrain.frapi.service.impl;

import com.firstrain.common.db.jpa.PersistenceProvider;
import com.firstrain.common.db.jpa.Transaction;
import com.firstrain.db.api.TagsDbAPI;
import com.firstrain.db.api.ItemsDbAPI;
import com.firstrain.db.api.EmailScheduleDbAPI;
import com.firstrain.db.api.TemplateDbAPI;
import com.firstrain.db.api.MailLogDbAPI;
import com.firstrain.db.api.UserGroupMapDbAPI;

import com.firstrain.db.obj.Items;
import com.firstrain.db.obj.BaseItem;
import com.firstrain.db.obj.UserGroupMap;
import com.firstrain.db.obj.Tags;
import com.firstrain.db.obj.Template;
import com.firstrain.db.obj.EmailSchedule;
import com.firstrain.db.obj.MailLog;

import com.firstrain.frapi.FRCompletionService;
import com.firstrain.frapi.domain.BaseSpec;
import com.firstrain.frapi.domain.SectionSpec;
import com.firstrain.frapi.domain.User;
import com.firstrain.frapi.obj.MonitorBriefDomain;
import com.firstrain.frapi.pojo.EnterprisePref;
import com.firstrain.frapi.pojo.MonitorAPIResponse;
import com.firstrain.frapi.pojo.MonitorEmailAPIResponse;
import com.firstrain.frapi.pojo.wrapper.BaseSet;
import com.firstrain.frapi.pojo.wrapper.EntityListJsonObject;
import com.firstrain.frapi.repository.EmailServiceRepository;
import com.firstrain.frapi.repository.MonitorServiceRepository;
import com.firstrain.frapi.repository.UserServiceRepository;
import com.firstrain.frapi.repository.impl.EmailServiceRepositoryImpl;
import com.firstrain.frapi.repository.impl.MonitorServiceRepositoryImpl;
import com.firstrain.frapi.repository.impl.UserServiceRepositoryImpl;
import com.firstrain.frapi.service.EntityBaseService;
import com.firstrain.frapi.service.EntityProcessingService;
import com.firstrain.frapi.service.VisualizationService;
import com.firstrain.frapi.service.RestrictContentService;

import com.firstrain.frapi.util.DefaultEnums;
import com.firstrain.frapi.util.MonitorAnalyticsUtil;
import com.firstrain.frapi.util.ServicesAPIUtil;
import com.firstrain.frapi.util.StatusCode;
import com.firstrain.obj.IEntityInfoCache;
import com.firstrain.utils.object.PerfRequestEntry;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ThreadPoolExecutor;

import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.reflect.Whitebox.setInternalState;
import static org.hamcrest.core.Is.is;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        MonitorBriefServiceImpl.class,
        MonitorServiceRepositoryImpl.class,
        PersistenceProvider.class,
        TagsDbAPI.class,
        MailLogDbAPI.class,
        EmailScheduleDbAPI.class,
        TemplateDbAPI.class,
        FRCompletionService.class,
        UserGroupMapDbAPI.class,
        ItemsDbAPI.class,
        ExecutorCompletionService.class
})
public class MonitorBriefServiceImplTest {

    private static final int TOTAL_COUNT = 100;
    private static final long OWNED_BY = 12L;
    private static final String USER_LAST_NAME = "LAST NAME";
    private static final long FR_MONITOR_ID = 23L;
    private static final int USER_FORMAT = 89;
    private static final String FLAGS = "FLAGS";
    private static final String FIRST_NAME = "FIRST NAME";
    private static final String USER_DOMAIN = "DOMAIN";
    private static final String USER_EMAIL = "mail@mail.com";
    private static final String DNB_USER_ID = "1234S";
    private static final String USER_CODE = "3423654NND";
    private static final String END_DATE_STR = "2018-01-10 PST";
    private static final String START_DATE_STR = "1990-01-10 PST";
    private static final long MONITOR_ID = 90L;
    private static final int SPEC_WIDTH = 200;
    private static final short SPEC_START = 10;
    private static final int SPEC_HEIGHT = 100;
    private static final int SEARCHES_PER_MONITOR = 352;
    private static final String PUBLIC_SOURCE_IDS_SSV = "IDS";
    private static final String PRIVATE_SOURCE_IDS_SSV = "PRIVATE IDS";
    private static final long ENTERPRISE_ID = 321464L;
    private static final short INDUSTRY_CLASS_ID = 23;
    private static final String CUSTOMIZED_CSS_FILENAME = "CSS FILENAME";
    private static final String USER_ID = "29471";
    private static final long GROUP_ID = 739L;
    private static final String CONTENT_FILTER_TOKEN = "FILTER TOKEN";
    private static final long EXECUTOR_TIMEOUT = 1000L;
    private static final String BIZ_LINE_CAT_ID_CSV = "2e12421";
    private static final int END_EVENT_TYPE = 11;
    private static final int START_EVENT_TYPE = 10;
    private static final short COUNT_PER_ENTITY = 1;
    private static final String CACHE_KEY = "CACHE KEY";

    @Mock
    private Transaction transaction;
    @Mock
    private Tags tags;
    @Mock
    private EmailSchedule emailSchedule;
    @Mock
    private Template template;
    @Mock
    private MailLog mailLog;
    @Mock
    private ThreadPoolTaskExecutor executorService;
    @Mock
    private VisualizationService visualizationService;
    @Mock
    private FRCompletionService completionService;
    @Mock
    private ExecutorCompletionService executorCompletionService;
    @Mock
    private ThreadPoolExecutor threadPoolExecutor;
    @Mock
    private Items items;
    @Mock
    private RestrictContentService restrictContentService;
    @Mock
    private ServicesAPIUtil servicesAPIUtil;
    private final MonitorServiceRepository monitorServiceRepository = new MonitorServiceRepositoryImpl();
    private final UserServiceRepository userServiceRepository = new UserServiceRepositoryImpl();
    @Mock
    private MonitorAnalyticsUtil monitorAnalyticsUtil;
    private final EntityProcessingService entityProcessingService = new EntityProcessingServiceImpl();
    private final EntityBaseService entityBaseService = new EntityBaseServiceImpl();
    private final MonitorServiceImpl monitorService = new MonitorServiceImpl();
    private final EmailServiceRepository emailServiceRepository = new EmailServiceRepositoryImpl();

    private MonitorBriefServiceImpl monitorBriefService;

    @Rule
    public final ErrorCollector errorCollector = new ErrorCollector();

    @Before
    public void setUp() throws Exception {
        mockStatic(PersistenceProvider.class, TagsDbAPI.class
                , MailLogDbAPI.class, EmailScheduleDbAPI.class, TemplateDbAPI.class
                , UserGroupMapDbAPI.class, ItemsDbAPI.class);
        monitorBriefService = new MonitorBriefServiceImpl();
        monitorBriefService.contentFilterToken = CONTENT_FILTER_TOKEN;
        monitorBriefService.executorTimeout = EXECUTOR_TIMEOUT;
        when(executorService.getThreadPoolExecutor()).thenReturn(threadPoolExecutor);
        whenNew(FRCompletionService.class).withAnyArguments().thenReturn(completionService);
        setInternalState(monitorBriefService, "monitorServiceRepository", monitorServiceRepository);
        setInternalState(monitorBriefService, "emailServiceRepository", emailServiceRepository);
        setInternalState(monitorBriefService, "entityProcessingService", entityProcessingService);
        setInternalState(monitorBriefService, "monitorAnalyticsUtil", monitorAnalyticsUtil);
        setInternalState(monitorBriefService, "userServiceRepository", userServiceRepository);
        setInternalState(monitorBriefService, "entityBaseService", entityBaseService);
        setInternalState(monitorBriefService, "executorService", executorService);
        setInternalState(monitorBriefService, "visualizationService", visualizationService);
        setInternalState(monitorBriefService, "monitorService", monitorService);
        setInternalState(monitorService, "userServiceRepository", userServiceRepository);
        setInternalState(monitorBriefService, "restrictContentService", restrictContentService);
        setInternalState(monitorBriefService, "servicesAPIUtil", servicesAPIUtil);
    }

    @Test
    public void getMonitorEmailList() throws Exception {
        //Arrange
        User newUser = createUser();
        when(mailLog.getSentTime()).thenReturn(new Timestamp(1));
        setUpStaticStubbing();
        //Act
        MonitorEmailAPIResponse emailAPIResponse = monitorBriefService.getMonitorEmailList(newUser, MONITOR_ID,
                START_DATE_STR, END_DATE_STR);
        //Assert
        errorCollector.checkThat(emailAPIResponse.getStatusCode(), is(StatusCode.REQUEST_SUCCESS));
        errorCollector.checkThat(emailAPIResponse.getMonitorId(), is(MONITOR_ID));
        errorCollector.checkThat(emailAPIResponse.getEmails().size(), is(1));
    }

    @Test
    public void getMonitorEmailListWHenTagsDeletedThenResult() throws Exception {
        //Arrange
        User newUser = createUser();
        setUpStaticStubbing();
        when(tags.getFlags()).thenReturn(BaseItem.FLAGS.DELETED);
        //Act
        MonitorEmailAPIResponse emailAPIResponse = monitorBriefService.getMonitorEmailList(newUser, MONITOR_ID,
                START_DATE_STR, END_DATE_STR);
        //Assert
        errorCollector.checkThat(emailAPIResponse.getStatusCode(), is(StatusCode.ENTITY_NOT_FOUND));
    }

    @Test
    public void getMonitorEmailListWhenEmailNoScheduleThenResult() throws Exception {
        //Arrange
        User newUser = createUser();
        setUpStaticStubbing();
        when(tags.getEmailId()).thenReturn(-1L);
        //Act
        MonitorEmailAPIResponse emailAPIResponse = monitorBriefService.getMonitorEmailList(newUser, MONITOR_ID,
                START_DATE_STR, END_DATE_STR);
        //Assert
        errorCollector.checkThat(emailAPIResponse.getStatusCode(), is(StatusCode.NO_EMAIL_SCHEDULE));
    }

    @Test
    public void getMonitorEmailListWhenStartDateIsEmptyThenResult() throws Exception {
        //Arrange
        User newUser = createUser();
        setUpStaticStubbing();
        when(mailLog.getSentTime()).thenReturn(new Timestamp(1));
        when(MailLogDbAPI.fetchUserEmailLogByScheduleId(anyLong(), any(Timestamp.class), anyInt()))
                .thenReturn(null);
        //Act
        MonitorEmailAPIResponse emailAPIResponse = monitorBriefService.getMonitorEmailList(newUser, MONITOR_ID,
                "", "");
        //Assert
        errorCollector.checkThat(emailAPIResponse.getStatusCode(), is(StatusCode.NO_EMAIL_SENT));
        errorCollector.checkThat(emailAPIResponse.getMonitorId(), is(MONITOR_ID));
    }

    @Test(expected = RuntimeException.class)
    public void getMonitorEmailListWhenThrowsException() throws Exception {
        //Arrange
        User newUser = createUser();
        setUpStaticStubbing();
        when(TemplateDbAPI.getTemplateByTemplateID(anyLong())).thenThrow(new RuntimeException());
        //Act
        monitorBriefService.getMonitorEmailList(newUser, MONITOR_ID, "", "");
    }

    @Test
    public void givenGetMonitorBriefDetailsWhenItemsNotNullThenResponse() throws Exception {
        //Arrange
        User newUser = createUser();
        setUpStaticStubbing();
        when(ItemsDbAPI.getItemsByTagId(eq(PersistenceProvider.EMAIL_DATABASE_READ),
                anyLong(), any(Items.Type.class), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(items));
        //Act
        MonitorAPIResponse response = monitorBriefService.getMonitorBriefDetails(newUser,
                MONITOR_ID, createEnterprisePref(), CONTENT_FILTER_TOKEN);
        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.REQUEST_SUCCESS));
    }

    @Test
    public void givenGetMonitorBriefDetailsWhenItemsNullThenResponse() throws Exception {
        //Arrange
        User newUser = createUser();
        setUpStaticStubbing();
        when(ItemsDbAPI.getItemsByTagId(eq(PersistenceProvider.EMAIL_DATABASE_READ),
                anyLong(), any(Items.Type.class), anyInt(), anyInt()))
                .thenReturn(null);
        //Act
        MonitorAPIResponse response = monitorBriefService.getMonitorBriefDetails(newUser,
                MONITOR_ID, createEnterprisePref(), CONTENT_FILTER_TOKEN);
        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.NO_ITEMS_IN_MONITOR));
    }

    @Test
    public void givenGetMonitorBriefDetailsWhenItemsNotNullAndFqInputDashContentFilterToken() throws Exception {
        //Arrange

        User newUser = createUser();
        setUpStaticStubbing();
        when(ItemsDbAPI.getItemsByTagId(eq(PersistenceProvider.EMAIL_DATABASE_READ),
                anyLong(), any(Items.Type.class), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(items));
        //Act
        MonitorAPIResponse response = monitorBriefService.getMonitorBriefDetails(newUser,
                MONITOR_ID, createEnterprisePref(), "-" + CONTENT_FILTER_TOKEN);
        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.REQUEST_SUCCESS));
    }

    @Test(expected = RuntimeException.class)
    public void givenGetMonitorBriefDetailsWhenThrowsException() throws Exception {
        //Arrange

        User newUser = createUser();
        setUpStaticStubbing();
        when(ItemsDbAPI.getItemsByTagId(eq(PersistenceProvider.EMAIL_DATABASE_READ),
                anyLong(), any(Items.Type.class), anyInt(), anyInt()))
                .thenThrow(new RuntimeException());
        //Act
        monitorBriefService.getMonitorBriefDetails(newUser,
                MONITOR_ID, createEnterprisePref(), "-" + CONTENT_FILTER_TOKEN);
    }

    private BaseSpec createBaseSpec() {
        BaseSpec baseSpec = new BaseSpec();
        baseSpec.setBucketMode(DefaultEnums.DateBucketingMode.AUTO);
        baseSpec.setEndEventType(END_EVENT_TYPE);
        baseSpec.setBucketMode(DefaultEnums.DateBucketingMode.DATE);
        baseSpec.setNeedBucket(true);
        baseSpec.setStartEventType(START_EVENT_TYPE);
        baseSpec.setCountPerEntity(COUNT_PER_ENTITY);
        baseSpec.setCsExcludedEventTypeGroup("");
        baseSpec.setCacheKey(CACHE_KEY);
        baseSpec.setNeedRelatedDoc(true);
        baseSpec.setNeedPhrase(true);
        baseSpec.setNeedMatchedEntities(true);
        baseSpec.setNeedImage(true);
        baseSpec.setNeedPagination(true);
        return baseSpec;
    }

    private User createUser() {
        User user = new User();
        user.setCode(USER_CODE);
        user.setContextPage(true);
        user.setDnbUserId(DNB_USER_ID);
        user.setUserId(USER_ID);
        user.setEmail(USER_EMAIL);
        user.setUserType(DefaultEnums.UserType.ABU);
        user.setOrigin(DefaultEnums.Origin.INTERNAL_ADMIN);
        user.setDomain(USER_DOMAIN);
        user.setFirstName(FIRST_NAME);
        user.setFlags(FLAGS);
        user.setFormat(USER_FORMAT);
        user.setFrMonitorId(FR_MONITOR_ID);
        user.setLastName(USER_LAST_NAME);
        user.setMembershipType(DefaultEnums.MembershipType.ADMIN);
        user.setStatus(DefaultEnums.UserValidationStatus.USER_CREATED_SUCCESSFULLY);
        user.setOwnedBy(OWNED_BY);
        EntityListJsonObject entityListJsonObject = new EntityListJsonObject();
        entityListJsonObject.setHasMore(true);
        entityListJsonObject.setSectionType(BaseSet.SectionType.AC);
        entityListJsonObject.setTotalCount(TOTAL_COUNT);
        PerfRequestEntry perfRequestEntry = new PerfRequestEntry();
        entityListJsonObject.setStat(perfRequestEntry);
        user.setUserTriggers(entityListJsonObject);
        return user;
    }

    private EnterprisePref createEnterprisePref() {
        EnterprisePref pref = new EnterprisePref();
        pref.setApplyMinNodeCheckInVisualization(true);
        pref.setCustomizedCssFileName(CUSTOMIZED_CSS_FILENAME);
        pref.setDnBId(true);
        pref.setIndustryClassificationId(INDUSTRY_CLASS_ID);
        pref.setEnterpriseId(ENTERPRISE_ID);
        pref.setPrivateSourceIdsSSV(PRIVATE_SOURCE_IDS_SSV);
        pref.setPublicSourceIdsSSV(PUBLIC_SOURCE_IDS_SSV);
        pref.setSearchesPerMonitor(SEARCHES_PER_MONITOR);
        Map<BaseSet.SectionType, SectionSpec> sectionSpecMap = new HashMap<BaseSet.SectionType, SectionSpec>();
        sectionSpecMap.put(BaseSet.SectionType.FT, createSectionSpec());
        sectionSpecMap.put(BaseSet.SectionType.FR, createSectionSpec());
        sectionSpecMap.put(BaseSet.SectionType.TT, createSectionSpec());
        sectionSpecMap.put(BaseSet.SectionType.BI, createSectionSpec());
        sectionSpecMap.put(BaseSet.SectionType.AC, createSectionSpec());
        sectionSpecMap.put(BaseSet.SectionType.F, createSectionSpec());
        sectionSpecMap.put(BaseSet.SectionType.C, createSectionSpec());
        sectionSpecMap.put(BaseSet.SectionType.CG, createSectionSpec());
        sectionSpecMap.put(BaseSet.SectionType.GL, createSectionSpec());
        sectionSpecMap.put(BaseSet.SectionType.CT, createSectionSpec());
        sectionSpecMap.put(BaseSet.SectionType.WV, createSectionSpec());
        sectionSpecMap.put(BaseSet.SectionType.RL, createSectionSpec());
        sectionSpecMap.put(BaseSet.SectionType.VMWR, createSectionSpec());
        sectionSpecMap.put(BaseSet.SectionType.VME, createSectionSpec());
        sectionSpecMap.put(BaseSet.SectionType.MD, createSectionSpec());
        sectionSpecMap.put(BaseSet.SectionType.TWT, createSectionSpec());
        sectionSpecMap.put(BaseSet.SectionType.TT, createSectionSpec());
        sectionSpecMap.put(BaseSet.SectionType.TRE, createSectionSpec());
        sectionSpecMap.put(BaseSet.SectionType.TOE, createSectionSpec());
        sectionSpecMap.put(BaseSet.SectionType.TE, createSectionSpec());
        sectionSpecMap.put(BaseSet.SectionType.VIZ, createSectionSpec());
        pref.setSectionsMap(sectionSpecMap);
        return pref;
    }

    private MonitorBriefDomain createMonitorBriefDomain() {
        MonitorBriefDomain briefDomain = new MonitorBriefDomain();
        briefDomain.setBizlineCatIdCSV(BIZ_LINE_CAT_ID_CSV);
        briefDomain.setCompanyCatIds(Sets.newHashSet("11", "22"));
        briefDomain.setCompanyIdsArr(new int[]{13, 14, 132});
        briefDomain.setIndustryOnly(true);
        return briefDomain;
    }

    private UserGroupMap createUserGroupMap() {
        UserGroupMap userGroupMap = new UserGroupMap();
        userGroupMap.setGroupId(GROUP_ID);
        userGroupMap.setMembershipType(UserGroupMap.MembershipType.ADMIN);
        userGroupMap.setUserId(11L);
        userGroupMap.setFlags(BaseItem.FLAGS.ACTIVE);
        userGroupMap.setOwnedByType(BaseItem.OwnedByType.GROUP);
        return userGroupMap;
    }

    private SectionSpec createSectionSpec() {
        SectionSpec sectionSpec = new SectionSpec();
        sectionSpec.setCount((short) 10);
        sectionSpec.setHeight(SPEC_HEIGHT);
        sectionSpec.setNeedBucket(true);
        sectionSpec.setNeedPagination(true);
        sectionSpec.setNeedRelatedDoc(true);
        sectionSpec.setNeedTweetAccelerometer(true);
        sectionSpec.setStart(SPEC_START);
        sectionSpec.setWidth(SPEC_WIDTH);
        return sectionSpec;
    }

    private void setUpStaticStubbing() throws Exception {
        MonitorBriefDomain briefDomain = createMonitorBriefDomain();
        when(monitorAnalyticsUtil.getMonitorBriefDomainFromFolderId(anyList(), anyLong(),
                any(IEntityInfoCache.class), anyString(), anyString(), anyBoolean()))
                .thenReturn(briefDomain);
        BaseSpec baseSpec = createBaseSpec();
        when(servicesAPIUtil.setSourceContent(anyBoolean(), anyBoolean(), any(BaseSpec.class), any(EnterprisePref.class)))
                .thenReturn(baseSpec);
        UserGroupMap userGroupMap = createUserGroupMap();
        when(PersistenceProvider.newTxn(anyString())).thenReturn(transaction);
        whenNew(ExecutorCompletionService.class).withAnyArguments().thenReturn(executorCompletionService);
        when(TagsDbAPI.getTagById(any(Transaction.class), anyLong()))
                .thenReturn(null).thenReturn(tags);
        when(MailLogDbAPI.fetchUserEmailLogByScheduleId(anyLong(), any(Timestamp.class), anyInt()))
                .thenReturn(null).thenReturn(Collections.singletonList(mailLog));
        when(EmailScheduleDbAPI.fetchEmailScheduleById(any(Transaction.class), anyLong()))
                .thenReturn(null).thenReturn(emailSchedule);
        when(TemplateDbAPI.getTemplateByTemplateID(anyLong()))
                .thenReturn(null).thenReturn(template);
        when(UserGroupMapDbAPI.getUserGroupMapByUserId(anyString(), anyLong()))
                .thenReturn(null)
                .thenReturn(Collections.singletonList(userGroupMap));
        when(restrictContentService.getAllHiddenContent(anyLong(), anyString()))
                .thenReturn("12414");
        when(tags.getFlags()).thenReturn(BaseItem.FLAGS.ACTIVE);
    }
}
