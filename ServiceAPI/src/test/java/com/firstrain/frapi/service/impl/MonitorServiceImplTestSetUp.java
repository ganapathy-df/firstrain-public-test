package com.firstrain.frapi.service.impl;

import com.firstrain.common.db.jpa.PersistenceProvider;
import com.firstrain.common.db.jpa.Transaction;
import com.firstrain.db.api.GroupsDbAPI;
import com.firstrain.db.api.TagsDbAPI;
import com.firstrain.db.api.ItemsDbAPI;
import com.firstrain.db.api.TagsItemsMapDbAPI;
import com.firstrain.db.api.SearchMapDbAPI;
import com.firstrain.db.api.EmailSearchScheduleMapDbAPI;
import com.firstrain.db.api.EmailUserSearchDbAPI;
import com.firstrain.db.api.UserGroupMapDbAPI;
import com.firstrain.db.api.EmailScheduleDbAPI;
import com.firstrain.db.obj.TagsInfo;
import com.firstrain.db.obj.EmailUserSearch;
import com.firstrain.db.obj.Tags;
import com.firstrain.db.obj.EmailSchedule;
import com.firstrain.db.obj.Users;
import com.firstrain.db.obj.Groups;
import com.firstrain.db.obj.BaseItem;
import com.firstrain.db.obj.ShareWith;
import com.firstrain.db.obj.Items;
import com.firstrain.db.obj.SearchMap;
import com.firstrain.db.obj.UserGroupMap;
import com.firstrain.db.obj.TagItemsMap;
import com.firstrain.db.obj.EmailSearchScheduleMap;
import com.firstrain.frapi.domain.User;
import com.firstrain.frapi.obj.MonitorWizardFilters;
import com.firstrain.frapi.pojo.EnterprisePref;
import com.firstrain.frapi.pojo.Entity;
import com.firstrain.frapi.repository.EntityBaseServiceRepository;
import com.firstrain.frapi.repository.impl.EntityBaseServiceRepositoryImpl;
import com.firstrain.frapi.repository.impl.GroupServiceRepositoryImpl;
import com.firstrain.frapi.repository.impl.MonitorServiceRepositoryImpl;
import com.firstrain.frapi.repository.impl.UserServiceRepositoryImpl;
import com.firstrain.frapi.util.ConvertUtil;
import com.firstrain.frapi.util.MonitorOrderingUtils;
import com.firstrain.frapi.util.ServicesAPIUtil;
import com.firstrain.obj.IEntityInfo;
import com.firstrain.obj.IEntityInfoCache;
import com.firstrain.utils.FR_ArrayUtils;
import com.firstrain.utils.JSONUtility;
import com.firstrain.utils.object.PerfMonitor;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;

import static com.firstrain.frapi.util.DefaultEnums.Status.ACTIVE;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.anyList;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.reflect.Whitebox.setInternalState;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        MonitorServiceImpl.class,
        GroupsDbAPI.class,
        TagsDbAPI.class,
        ItemsDbAPI.class,
        PersistenceProvider.class,
        JSONUtility.class,
        TagsItemsMapDbAPI.class,
        SearchMapDbAPI.class,
        EmailSearchScheduleMapDbAPI.class,
        EmailUserSearchDbAPI.class,
        EmailScheduleDbAPI.class,
        UserGroupMapDbAPI.class,
        FR_ArrayUtils.class,
        PerfMonitor.class,
        MonitorOrderingUtils.class
})
public abstract class MonitorServiceImplTestSetUp {

    private static final long TAGS_EMAIL_ID = 111L;
    static final int TAGS_ORDER = 374;
    static final long REFERS_TO = 987;
    static final String TOKEN_ENTRY_SECTOR = "TOKEN ENTRY SECTOR";
    static final String SEARCH_TOKEN = "SEARCH TOKEN";
    static final long DOC_GROUP_ID = 648L;
    static final String ADV_KEYWORDS = "keyword1,keyword2";
    static final String FILTER_STRING_VALUE = "FILTER";
    static final String EXCEPTION = "MESSAGE";
    static final int ENT_COMPANY_ID = 99;
    static final long OWNED_BY = 88L;
    static final long MONITOR_ID = 554L;
    static final long ITEMS_ID = 67L;
    static final String USER_COMPANY = "user_company";
    static final String LAST_NAME = "lastName";
    static final long FR_MONITOR_ID = 98L;
    static final String FIRST_NAME = "firstname";
    static final String USER_DOMAIN = "userDOmain";
    static final String OWNER_TYPE = "ADMIN";
    static final String USER_NAME_VALUE = "usernamevalue";
    static final String USER_ID = "68790";
    static final long GROUP_ID = 88L;
    static final long PARENT_GROUP_ID = 1L;
    static final long FR_USER_ID = 12L;
    static final int RESULT_SIZE = 546;
    static final String ITEMS_DATA = "q=items_data&fq45&q=2352";
    static final long MAIL_SEARCH_ID = 12L;
    static final long SEARCH_TAG_ID = 456L;
    static final long UI_SEARCH_ID = 99L;
    static final long SEARCH_ID = 123L;
    static final String TAG_NAME = "tag";
    static final int SEARCHES_PER_MONITOR = -1;
    static final String CLUSTER_IDS_CSV = "345";
    static final String ENT_NAME = "ent";
    static final int VOL_RECENT_WEEK = 1;
    static final String COMP_LOGO_URL = "logo";
    static final String TICKER = "ticker";
    static final int ONE_DAY_DOC_COUNT = 23;
    static final String DNB_COMPANY_ID = "67uiq0";
    static final String MATCHED_TYPE = "1";
    static final short RELEVANCE_SCORE = 300;
    static final long ITEMS_TAG_ID = 890L;
    static final long SCHEDULE_ID = 34L;
    static final long TEMPLATE_ID = 56L;
    static final long SCHEDULE_USER_ID = 875L;
    static final String ADVANCED_FILTERS = "filter";
    static final String MONITOR_ENTITY = "entity: value";
    static final String EMAIL = "stringEmail";
    static final long EMAIL_USER_SEARCH_ID = 6392L;
    static final long TAGS_ID = 766L;
    private static final String KEYWORD_STRING = "keyword";

    final MonitorServiceRepositoryImpl monitorServiceRepository = spy(new MonitorServiceRepositoryImpl());
    final GroupServiceRepositoryImpl groupServiceRepository = spy(new GroupServiceRepositoryImpl());
    final UserServiceRepositoryImpl userServiceRepository = spy(new UserServiceRepositoryImpl());
    final ConvertUtil convertUtil = spy(new ConvertUtil());
    final ServicesAPIUtil servicesAPIUtil = spy(new ServicesAPIUtil());
    final EntityBaseServiceRepository entityBaseServiceRepository = spy(new EntityBaseServiceRepositoryImpl());

    @InjectMocks
    final MonitorServiceImpl monitorService = spy(new MonitorServiceImpl());
    @Mock
    IEntityInfoCache entityInfoCache;
    @Mock
    IEntityInfo iEntityInfo;
    @Mock
    Transaction transaction;

    @Captor
    ArgumentCaptor<Tags> tagsArgumentCaptor;

    @Captor
    ArgumentCaptor<List<TagsInfo>> tagCaptor;

    @Captor
    ArgumentCaptor<List<Long>> listLongArgumentCaptor;

    @Captor
    ArgumentCaptor<List<Entity>> entListArgumentCaptor;

    @Captor
    ArgumentCaptor<List<String>> strListArgumentCaptor;

    @Captor
    ArgumentCaptor<MonitorWizardFilters> monitorWizardFiltersArgumentCaptor;

    EmailUserSearch emailUserSearch;
    EnterprisePref enterprisePrefFinal;
    MonitorWizardFilters monitorWizardFiltersFinal;
    List<String> stringList;

    @Rule
    public final ErrorCollector errorCollector = new ErrorCollector();

    @Before
    public void setUp() throws Exception {
        mockStatic(GroupsDbAPI.class, TagsDbAPI.class,
                ItemsDbAPI.class, PersistenceProvider.class,
                JSONUtility.class, TagsItemsMapDbAPI.class,
                SearchMapDbAPI.class, EmailSearchScheduleMapDbAPI.class,
                EmailUserSearchDbAPI.class, EmailScheduleDbAPI.class,
                UserGroupMapDbAPI.class, PerfMonitor.class);
        stringList = new ArrayList<>();
        stringList.add("OR");
        monitorWizardFiltersFinal = new MonitorWizardFilters();
        enterprisePrefFinal = new EnterprisePref();
        emailUserSearch = new EmailUserSearch();
        emailUserSearch.setId(EMAIL_USER_SEARCH_ID);
        when(entityInfoCache.searchTokenToEntity(anyString())).thenReturn(iEntityInfo);
        when(entityBaseServiceRepository.getEntityInfoCache()).thenReturn(entityInfoCache);
        setInternalState(servicesAPIUtil, "entityBaseServiceRepository", entityBaseServiceRepository);
        setInternalState(monitorServiceRepository, "servicesAPIUtil", servicesAPIUtil);
        setInternalState(monitorServiceRepository, "convertUtil", convertUtil);
        when(GroupsDbAPI.getAllParentGroupIdsByGroupId(anyString(), anyLong(), anyLong()))
                .thenReturn(Sets.newHashSet(GROUP_ID));
        when(TagsDbAPI.addTag(any(Transaction.class), anyString(), any(Tags.TagType.class)
                , anyString(), anyLong(), any(BaseItem.RefersToType.class), anyLong(), any(BaseItem.OwnedByType.class)
                , anyLong(), any(Tags.SearchOrderType.class), any(ShareWith.class)))
                .thenReturn(createTags());
        when(TagsDbAPI.getTagsByOwner(anyString(), any(BaseItem.OwnedByType.class), anyLong()))
                .thenReturn(null).thenReturn(Collections.singletonList(createTags()));
        when(TagsDbAPI.getTagById(any(Transaction.class), anyLong()))
                .thenReturn(null).thenReturn(createTags());
        when(GroupsDbAPI.getGroupByIds(anyString(), any(Collection.class)))
                .thenReturn(Collections.singletonList(createGroups()));
        when(ItemsDbAPI.getItemsByTagId(anyString(), anyLong(), any(Items.Type.class), anyInt(), anyInt()))
                .thenReturn(null).
                thenReturn(Arrays.asList(createItems(), createItems()));
        when(PersistenceProvider.newTxn(anyString())).thenReturn(transaction);
        when(SearchMapDbAPI.getSearchMapByUISearchIDAndTagID(anyString(), anyLong(), anyLong()))
                .thenReturn(createSearchMap());
        when(TagsItemsMapDbAPI.getTagsItemsMapByTagID(anyString(), anyLong()))
                .thenReturn(null).thenReturn(Collections.singletonList(createTagItemsMap()));
        when(ItemsDbAPI.addItem(any(Transaction.class), anyString(), anyString(), any(Items.Type.class), anyLong()
                , anyInt(), anyLong(), any(BaseItem.RefersToType.class), anyLong(), any(BaseItem.OwnedByType.class)
                , anyInt())).thenReturn(createItems());
        when(EmailScheduleDbAPI.fetchEmailScheduleById(any(Transaction.class), anyLong()))
                .thenReturn(createEmailSchedule());
        when(EmailUserSearchDbAPI.fetchEmailUserSearchById(any(Transaction.class), anyLong()))
                .thenReturn(emailUserSearch);
        when(EmailSearchScheduleMapDbAPI
                .fetchSearchScheduleMapByScheduleIdSearchStatus(anyLong()))
                .thenReturn(Collections.singletonList(createEmailSearchScheduleMap()));
        when(JSONUtility.deserialize(anyString(), eq(MonitorWizardFilters.class)))
                .thenReturn(createWizardFilters());
        when(transaction.fetch(anyLong(), eq(Tags.class))).thenReturn(createTags());
        when(transaction.fetch(anyLong(), eq(EmailUserSearch.class))).thenReturn(emailUserSearch);
        doReturn(entityInfoCache).when(monitorServiceRepository).getEntityInfoCache();
        when(PerfMonitor.currentTime()).thenReturn(PARENT_GROUP_ID);
    }

    static Users createUsers() {
        Users users = new Users();
        users.setFirstName(FIRST_NAME);
        users.setId(FR_USER_ID);
        return users;
    }

    static User createUser() {
        User user = new User();
        user.setDomain(USER_DOMAIN);
        user.setFirstName(FIRST_NAME);
        user.setFrMonitorId(FR_MONITOR_ID);
        user.setOwnedBy(OWNED_BY);
        user.setLastName(LAST_NAME);
        user.setUserCompany(USER_COMPANY);
        user.setUserId(USER_ID);
        user.setUserName(USER_NAME_VALUE);
        user.setEmail(EMAIL);
        return user;
    }

    static UserGroupMap createUserGroupMap(UserGroupMap.MembershipType type) {
        UserGroupMap userGroupMap = new UserGroupMap();
        userGroupMap.setOwnedByType(BaseItem.OwnedByType.USER);
        userGroupMap.setUserId(Long.parseLong(USER_ID));
        userGroupMap.setMembershipType(type);
        userGroupMap.setGroupId(GROUP_ID);
        userGroupMap.setFlags(BaseItem.FLAGS.ACTIVE);
        return userGroupMap;
    }


    static Tags createTags() {
        Tags tagsLocal = new Tags();
        tagsLocal.setEmailId(TAGS_EMAIL_ID);
        tagsLocal.setOrder(TAGS_ORDER);
        tagsLocal.setRefersTo(REFERS_TO);
        tagsLocal.setRefersToType(BaseItem.RefersToType.DOCUMENT);
        tagsLocal.setSearchOrderType(Tags.SearchOrderType.CUSTOM);
        tagsLocal.setFilterString(FILTER_STRING_VALUE);
        tagsLocal.setTagType(Tags.TagType.COMPETITION_REGION);
        tagsLocal.setOwnedBy(FR_USER_ID);
        tagsLocal.setOwnedByType(BaseItem.OwnedByType.GROUP);
        tagsLocal.setTagName(TAG_NAME);
        tagsLocal.setId(TAGS_ID);
        return tagsLocal;
    }

    static Groups createGroups() {
        Groups groups = new Groups();
        groups.setId(GROUP_ID);
        groups.setGroupType(Groups.GroupType.FIRSTRAIN);
        groups.setParentGroupId(PARENT_GROUP_ID);
        groups.setGroupName(USER_NAME_VALUE);
        return groups;
    }

    static Items createItems() {
        Items items = new Items();
        items.setResultSize(RESULT_SIZE);
        items.setDocGroupId(DOC_GROUP_ID);
        items.setData(ITEMS_DATA);
        items.setId(-1L);
        items.setName(ENT_NAME);
        return items;
    }

    static MonitorWizardFilters createWizardFilters() {
        MonitorWizardFilters monitorWizardFilters = new MonitorWizardFilters();
        MonitorWizardFilters.Advanced advanced = new MonitorWizardFilters.Advanced();
        advanced.keywords = ADV_KEYWORDS;
        advanced.advancedFilters = Collections.singletonList(ADVANCED_FILTERS);
        monitorWizardFilters.advanced = advanced;
        return monitorWizardFilters;
    }

    static SearchMap createSearchMap() {
        SearchMap searchMap = new SearchMap();
        searchMap.setId(SEARCH_ID);
        searchMap.setUiSearchID(UI_SEARCH_ID);
        searchMap.setTagID(SEARCH_TAG_ID);
        searchMap.setMailSearchID(MAIL_SEARCH_ID);
        return searchMap;
    }

    static Entity createEntity() {
        Entity entityFinal = new Entity();
        entityFinal.setId(CLUSTER_IDS_CSV);
        entityFinal.setName(ENT_NAME);
        entityFinal.setCompanyId(ENT_COMPANY_ID);
        entityFinal.setVolumeRecentWeek(VOL_RECENT_WEEK);
        entityFinal.setStatus(ACTIVE);
        entityFinal.setCompanyLogoUrl(COMP_LOGO_URL);
        entityFinal.setHasTweet(true);
        entityFinal.setSearchToken(SEARCH_TOKEN);
        entityFinal.setAdditionalMatchQualifier(true);
        entityFinal.setTicker(TICKER);
        entityFinal.setAdded(true);
        entityFinal.setMatchedType(MATCHED_TYPE);
        entityFinal.setRelevanceScore(RELEVANCE_SCORE);
        entityFinal.setDnbEntityId(DNB_COMPANY_ID);
        entityFinal.setSelected(true);
        entityFinal.setOneDayDocCount(ONE_DAY_DOC_COUNT);
        return entityFinal;
    }

    static TagItemsMap createTagItemsMap() {
        TagItemsMap itemsMap = new TagItemsMap();
        itemsMap.setTagId(ITEMS_TAG_ID);
        itemsMap.setItemType(TagItemsMap.ItemType.ITEM);
        itemsMap.setId(ITEMS_ID);
        return itemsMap;
    }

    static EmailSchedule createEmailSchedule() {
        EmailSchedule emailSchedule = new EmailSchedule();
        emailSchedule.setUserId(SCHEDULE_USER_ID);
        emailSchedule.setTemplateID(TEMPLATE_ID);
        emailSchedule.setId(SCHEDULE_ID);
        return emailSchedule;
    }

    EmailSearchScheduleMap createEmailSearchScheduleMap() {
        EmailSearchScheduleMap searchScheduleMap = new EmailSearchScheduleMap();
        searchScheduleMap.setEmailSchedule(createEmailSchedule());
        searchScheduleMap.setEmailUserSearch(emailUserSearch);
        return searchScheduleMap;
    }

    void setTags() throws Exception {
        Tags tags = createTags();
        tags.setOwnedBy(FR_USER_ID);
        tags.setFlags(BaseItem.FLAGS.ACTIVE);
        User user = createUser();
        user.setUserId(String.valueOf(FR_USER_ID));
        when(UserGroupMapDbAPI.getUserGroupMapByUserId(anyString(), anyLong()))
                .thenReturn(Collections.singletonList(createUserGroupMap(UserGroupMap.MembershipType.ADMIN)));
        doReturn(tags).when(monitorServiceRepository).getTagById(anyLong());
        doReturn(null).when(monitorServiceRepository).getTagsByOwnerAndTagName(anyLong(),
                any(BaseItem.OwnedByType.class), anyString());
        Users users = createUsers();
        doReturn(users).when(userServiceRepository).getUserById(anyLong());
        List<Entity> entities = Collections.singletonList(createEntity());
        doReturn(entities).when(monitorServiceRepository).validateTokensUsingEntityCache(anyList(), anyList());
        doReturn(KEYWORD_STRING).when(servicesAPIUtil).prepareFq(anyListOf(String.class));
    }

    void setRemoveTags() throws Exception {
        Tags tags = createTags();
        tags.setFlags(BaseItem.FLAGS.ACTIVE);
        User user = createUser();
        user.setOwnedBy(FR_USER_ID);
        when(UserGroupMapDbAPI.getUserGroupMapByUserId(anyString(), anyLong()))
                .thenReturn(Collections.singletonList(createUserGroupMap(UserGroupMap.MembershipType.ADMIN)));
        doReturn(tags).when(monitorServiceRepository).getTagById(anyLong());
    }

    void setMonitorOwner() {
        UserGroupMap groupMap = createUserGroupMap(UserGroupMap.MembershipType.ADMIN);
        doReturn(Collections.singletonList(groupMap))
                .when(userServiceRepository).getUserGroupMapByUserId(anyLong());
    }

    void initRemoveEntities() throws Exception {
        when(JSONUtility.deserialize(anyString(), eq(MonitorWizardFilters.class)))
                .thenReturn(createWizardFilters());
        when(entityInfoCache.searchTokenToEntity(anyString())).thenReturn(null);
        doReturn(entityInfoCache).when(monitorServiceRepository).getEntityInfoCache();
        Map<String, List<String>> listMap = new HashMap();
        listMap.put("q", Arrays.asList("12: 12", "13: 14"));
        listMap.put("z", Arrays.asList("12: 12", "13: 14"));
        doReturn(listMap).when(servicesAPIUtil).prepareQueryMap(anyString());
    }

    void initAddEntities() throws Exception {
        when(JSONUtility.deserialize(anyString(), eq(MonitorWizardFilters.class)))
                .thenReturn(createWizardFilters());
        when(entityInfoCache.searchTokenToEntity(anyString())).thenReturn(null);
        doReturn(entityInfoCache).when(monitorServiceRepository).getEntityInfoCache();
        Map<String, List<String>> listMap = new HashMap();
        listMap.put("q", Arrays.asList("12: 12", "13: 14"));
        listMap.put("z", Arrays.asList("12: 12", "13: 14"));
        doReturn(listMap).when(servicesAPIUtil).prepareQueryMap(anyString());
        List<Entity> entities = Collections.singletonList(createEntity());
        doReturn(entities).when(monitorServiceRepository).validateTokensUsingEntityCache(anyList(), anyList());
        enterprisePrefFinal.setSearchesPerMonitor(SEARCHES_PER_MONITOR);
    }

    void initCategory() throws Exception {
        initAddEntities();
        Entity entityNew = new Entity();
        entityNew.setId("-1L");
        entityNew.setSearchToken(SEARCH_TOKEN);
        doReturn(Collections.singletonMap(SEARCH_TOKEN, entityNew)).when(monitorService,
                "getEntitiesMap", anyList(), anyList(), any(MonitorWizardFilters.class));

    }

    void initAddFilters() {
        when(entityInfoCache.searchTokenToEntity(anyString())).thenReturn(null);
        doReturn(entityInfoCache).when(monitorServiceRepository).getEntityInfoCache();
        Map<String, List<String>> listMap = new HashMap();
        listMap.put("q", Arrays.asList("12: 12", "13: 14"));
        listMap.put("z", Arrays.asList("12: 12", "13: 14"));
        doReturn(listMap).when(servicesAPIUtil).prepareQueryMap(anyString());
        List<Entity> entities = Collections.singletonList(createEntity());
        doReturn(entities).when(monitorServiceRepository).validateTokensUsingEntityCache(anyList(), anyList());
    }

    void initRemoveFilters() {
        when(entityInfoCache.searchTokenToEntity(anyString())).thenReturn(null);
        doReturn(entityInfoCache).when(monitorServiceRepository).getEntityInfoCache();
        Map<String, List<String>> listMap = new HashMap();
        listMap.put("q", Arrays.asList("12: 12", "13: 14"));
        listMap.put("z", Arrays.asList("12: 12", "13: 14"));
        doReturn(listMap).when(servicesAPIUtil).prepareQueryMap(anyString());
        List<Entity> entities = Collections.singletonList(createEntity());
        doReturn(entities).when(monitorServiceRepository).validateTokensUsingEntityCache(anyList(), anyList());
    }
}

