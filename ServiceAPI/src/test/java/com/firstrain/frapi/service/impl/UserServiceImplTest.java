package com.firstrain.frapi.service.impl;

import com.firstrain.common.db.jpa.PersistenceProvider;
import com.firstrain.common.db.jpa.Transaction;
import com.firstrain.db.api.EmailScheduleDbAPI;
import com.firstrain.db.api.EmailUserInfoDbAPI;
import com.firstrain.db.api.EmailUserSearchDbAPI;
import com.firstrain.db.api.GroupsDbAPI;
import com.firstrain.db.api.UserRecoveryDbAPI;
import com.firstrain.db.api.UsersDbAPI;
import com.firstrain.db.obj.BaseItem;
import com.firstrain.db.obj.EmailSchedule;
import com.firstrain.db.obj.EmailUserInfo;
import com.firstrain.db.obj.EmailUserSearch;
import com.firstrain.db.obj.GroupPreference;
import com.firstrain.db.obj.Groups;
import com.firstrain.db.obj.Tags;
import com.firstrain.db.obj.UserGroupMap;
import com.firstrain.db.obj.UserRecovery;
import com.firstrain.db.obj.Users;
import com.firstrain.frapi.domain.User;
import com.firstrain.frapi.pojo.UserAPIResponse;
import com.firstrain.frapi.repository.UserServiceRepository;
import com.firstrain.frapi.util.ConvertUtil;
import com.firstrain.frapi.util.FreemarkerTemplates;
import com.firstrain.frapi.util.ServicesAPIUtil;
import com.firstrain.frapi.util.StatusCode;
import freemarker.template.Template;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        UserServiceImpl.class,
        PersistenceProvider.class,
        UsersDbAPI.class,
        GroupsDbAPI.class,
        GroupPreference.class,
        UserRecoveryDbAPI.class,
        EmailUserInfoDbAPI.class,
        EmailScheduleDbAPI.class,
        EmailUserSearchDbAPI.class
})
public class UserServiceImplTest {

    private static final String USER_ID = "2432";
    private static final long OWNED_BY = 32L;
    private static final long FR_MONITOR_ID = 98L;
    private static final String FIRST_NAME = "user";
    private static final String USER_DOMAIN = "domain";
    private static final long SCHEDULE_ID = 98L;
    private static final long MAIL_ID = 68L;
    private static final long TEMPLATE_ID = 24L;
    private static final long GROUP_ID = 1L;
    private static final long FR_USER_ID = 23L;
    private static final String MESSAGE = "MESSAGE";
    private static final String LAST_NAME = "lastName";
    private static final String USER_COMPANY = "company";
    private static final String USER_NAME_VALUE = "username";
    private static final String USR_TIMEZONE = "utc";
    private static final String USER_MAIL = "user@mail.com";
    private static final String USR_COMPANY = "company";
    private static final long PARENT_GROUP_ID = 1L;
    private static final String PRODUCT_TYPE = "ptype";
    private static final long ACTOR_ID = 546L;
    private static final String SUB_GROUP_ID_CSV = "6543";
    private static final String PREF_XML = "xml";
    private static final long SCHEDULE_USER_ID = 35L;
    private static final long ENTERPRISE_ID = 768L;

    @InjectMocks
    @Spy
    private final UserServiceImpl userService = new UserServiceImpl();

    @Mock
    private UserServiceRepository userServiceRepository;
    @Mock
    private ConvertUtil convertUtil;
    @Mock
    private ServicesAPIUtil servicesAPIUtil;
    @Mock
    private Transaction transaction;
    @Mock
    private FreemarkerTemplates freemarkerTemplates;
    @Mock
    private Template template;
    @Mock
    private PoolingHttpClientConnectionManager connectionManager;

    @Captor
    private ArgumentCaptor<Users> usersArgumentCaptor;

    private final ErrorCollector errorCollector = new ErrorCollector();
    private final ExpectedException expectedException = ExpectedException.none();
    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector)
            .around(expectedException);

    private User user;
    private EmailUserSearch emailUserSearch;

    @Before
    public void setUp() throws Exception {
        PowerMockito.whenNew(PoolingHttpClientConnectionManager.class)
                .withAnyArguments().thenReturn(connectionManager);
        mockStatic(PersistenceProvider.class,
                UsersDbAPI.class, GroupsDbAPI.class,
                GroupPreference.class,
                UserRecoveryDbAPI.class,
                EmailUserInfoDbAPI.class, EmailScheduleDbAPI.class,
                EmailUserSearchDbAPI.class);
        PowerMockito.when(PersistenceProvider.newTxn(anyString())).thenReturn(transaction);
        PowerMockito.when(UsersDbAPI.getUserById(any(Transaction.class), anyLong()))
                .thenReturn(null).thenReturn(createDummyUsers());
        UserRecovery userRecovery = new UserRecovery();
        emailUserSearch = new EmailUserSearch();
        PowerMockito.when(UserRecoveryDbAPI.getUserRecoveryInfoByUId(anyLong()))
                .thenReturn(userRecovery);
        final GroupPreference preference = createGroupPreference();
        PowerMockito.when(GroupPreference.parseXML(anyString())).thenReturn(preference);
        when(freemarkerTemplates.getTemplate(anyString())).thenReturn(template);
        user = createDummyUser();
        when(convertUtil.convertDbUserToDomainUser(any(Users.class), any(UserGroupMap.MembershipType.class)))
                .thenReturn(user);
    }

    @Test
    public void givenUserIdAndNeedMembershipWhenGetUserByIdThenReturnUserAPIResponse() throws Exception {
        //Arrange
        setStubbingForPrivilege(UserGroupMap.MembershipType.ADMIN);
        //Act
        final UserAPIResponse response = userService.getUserById(FR_USER_ID, true);
        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.USER_NOT_FOUND));
    }

    @Test
    public void givenUserIdWhenGetUserByIdThenThrowsException() throws Exception {
        //Arrange
        doThrow(new Exception(MESSAGE)).when(userServiceRepository).getUserById(anyLong());
        assertException();
        //Act
        final UserAPIResponse response = userService.getUserById(FR_USER_ID, true);
        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.REQUEST_SUCCESS));
    }

    @Test
    public void givenGetUserByIdWhenUserRequestSuccessThenResponse() throws Exception {
        //Arrange
        setStubbingForPrivilege(UserGroupMap.MembershipType.ADMIN);
        final Users users = createDummyUsers();
        doReturn(users).when(userServiceRepository).getUserById(anyLong());
        //Act
        final UserAPIResponse response = userService.getUserById(FR_USER_ID, true);
        //Assert
        errorCollector.checkThat(response.getUser(), equalTo(user));
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.REQUEST_SUCCESS));
    }

    @Test
    public void givenUpdateUserByIdWhenIllegalArgumentThenResponse() throws Exception {
        //Arrange
        setStubbingForPrivilege(UserGroupMap.MembershipType.ADMIN);
        //Act
        final UserAPIResponse response = userService.updateUserById(user, FR_USER_ID, USER_MAIL, FIRST_NAME, LAST_NAME,
                USR_TIMEZONE, "illegal", USR_COMPANY);
        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.ILLEGAL_ARGUMENT));
    }

    @Test
    public void givenUpdateUserByIdWhenInsufficientPrivilegeThenResponse() throws Exception {
        //Arrange
        setStubbingForPrivilege(UserGroupMap.MembershipType.ANONYMOUS);
        //Act
        final UserAPIResponse response = userService.updateUserById(user, FR_USER_ID, USER_MAIL, FIRST_NAME, LAST_NAME,
                USR_TIMEZONE, BaseItem.FLAGS.ACTIVE.name(), USR_COMPANY);
        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.INSUFFICIENT_PRIVILEGE));
    }

    @Test
    public void givenUpdateUserByIdWhenUserIdNotEqualsActorIdThenResponse() throws Exception {
        //Arrange
        user.setUserId(String.valueOf(FR_USER_ID));
        setStubbingForPrivilege(UserGroupMap.MembershipType.ANONYMOUS);
        //Act
        final UserAPIResponse response = userService.updateUserById(user, FR_USER_ID, USER_MAIL, FIRST_NAME, LAST_NAME,
                USR_TIMEZONE, BaseItem.FLAGS.ACTIVE.name(), USR_COMPANY);
        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.INSUFFICIENT_PRIVILEGE));
    }

    @Test
    public void givenUpdateUserByIdWhenUserIsNullThenResponse() throws Exception {
        //Arrange
        setStubbingForPrivilege(UserGroupMap.MembershipType.ADMIN);
        doReturn(null).when(userServiceRepository).getUserById(anyLong());
        //Act
        final UserAPIResponse response = userService.updateUserById(user, FR_USER_ID, USER_MAIL, FIRST_NAME, LAST_NAME,
                USR_TIMEZONE, BaseItem.FLAGS.ACTIVE.name(), USR_COMPANY);
        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.USER_NOT_FOUND));
    }

    @Test
    public void givenUpdateUserByIdWhenEmailAlreadyExistsThenResponse() throws Exception {
        //Arrange
        final Groups groups = createGroups();
        final Users users = createDummyUsers();
        setStubbingForPrivilege(UserGroupMap.MembershipType.ADMIN);
        doReturn(users).when(userServiceRepository).getUserById(anyLong());
        PowerMockito.when(GroupsDbAPI.fetchUserGroupById(anyString(), anyLong()))
                .thenReturn(groups);
        PowerMockito.when(UsersDbAPI.getUserByUserNameWithoutOrigin(anyString(), anyString(), anyString()))
                .thenReturn(users);
        doReturn(users).when(userServiceRepository)
                .getUserByEmailAndEnterpriseId(anyString(), anyLong());
        //Act
        final UserAPIResponse response = userService.updateUserById(user, FR_USER_ID, USER_MAIL, FIRST_NAME, LAST_NAME,
                USR_TIMEZONE, BaseItem.FLAGS.ACTIVE.name(), USR_COMPANY);
        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.EMAIL_ALREADY_EXISTS));
    }

    @Test
    public void givenUpdateUserByIdAndUserIsNullWhenEmailAlreadyExistsThenResponse() throws Exception {
        //Arrange
        mockUsers();
        //Act
        final UserAPIResponse response = userService.updateUserById(user, FR_USER_ID, USER_MAIL, FIRST_NAME, LAST_NAME,
                USR_TIMEZONE, BaseItem.FLAGS.INACTIVE.name(), USR_COMPANY);
        //Assert
        verify(userServiceRepository).updateUser(usersArgumentCaptor.capture(), eq(BaseItem.FLAGS.INACTIVE.name()),
                eq(BaseItem.FLAGS.ACTIVE.name()));
        final Users usersLocal = usersArgumentCaptor.getValue();
        errorCollector.checkThat(usersLocal.getCompany(), equalTo(USER_COMPANY));
        errorCollector.checkThat(usersLocal.getFlags(), equalTo(BaseItem.FLAGS.INACTIVE));
        errorCollector.checkThat(usersLocal.getLastName(), equalTo(LAST_NAME));
        errorCollector.checkThat(usersLocal.getFirstName(), equalTo(FIRST_NAME));
        errorCollector.checkThat(usersLocal.getTimeZone(), equalTo(USR_TIMEZONE));
        errorCollector.checkThat(usersLocal.getEmail(), equalTo(USER_MAIL));
        errorCollector.checkThat(response.getStatusCode(), equalTo(StatusCode.REQUEST_SUCCESS));
        errorCollector.checkThat(response.getUser(), equalTo(user));
    }

    @Test
    public void givenUpdateUserByIdAndUserIsNullWhenEmailAlreadyExistsThenExceptionWillBeThrown() throws Exception {
        //Arrange
        mockUsers();
        doThrow(new Exception(MESSAGE)).when(userServiceRepository).getUserById(anyLong());
        //Assert
        assertException();
        //Act
        userService.updateUserById(user, FR_USER_ID, USER_MAIL, FIRST_NAME, LAST_NAME,
                USR_TIMEZONE, BaseItem.FLAGS.INACTIVE.name(), USR_COMPANY);
    }

    @Test
    public void givenUpdateUserByIdAndOwnedByWhenEmailAlreadyExistsThenResponse() throws Exception {
        //Arrange
        mockUsers();
        final User newUser = createDummyUser();
        newUser.setOwnedBy(30L);
        //Act
        final UserAPIResponse response = userService.updateUserById(newUser,
                FR_USER_ID, USER_MAIL, FIRST_NAME, LAST_NAME,
                USR_TIMEZONE, BaseItem.FLAGS.INACTIVE.name(), USR_COMPANY);
        //Assert
        errorCollector.checkThat(response.getStatusCode(), equalTo(StatusCode.INSUFFICIENT_PRIVILEGE));
    }

    @Test
    public void givenCreateUserWhenUserIsAnonymousThenResponse() throws Exception {
        //Arrange
        final Groups groups = createGroups();
        final Users users = createDummyUsers();
        setStubbingForPrivilege(UserGroupMap.MembershipType.ADMIN);
        doReturn(users).when(userServiceRepository).getUserById(anyLong());
        PowerMockito.when(GroupsDbAPI.fetchUserGroupById(anyString(), anyLong()))
                .thenReturn(groups);
        PowerMockito.when(UsersDbAPI.getUserByUserNameWithoutOrigin(anyString(), anyString(), anyString()))
                .thenReturn(null);
        //Act
        final UserAPIResponse response = userService.createUser(ACTOR_ID, USER_MAIL, FIRST_NAME, LAST_NAME,
                USR_TIMEZONE, SUB_GROUP_ID_CSV, USER_COMPANY);
        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.ANONYMOUS_USER_NOT_FOUND));
    }

    @Test
    public void givenActorIdAndNameAndTimeZoneCreateUserThenThrowsException() throws Exception {
        //Arrange
        doThrow(new Exception(MESSAGE)).when(userServiceRepository).getUserById(anyLong());
        //Assert
        assertException();
        //Act
        userService.createUser(ACTOR_ID, USER_MAIL, FIRST_NAME, LAST_NAME,
                USR_TIMEZONE, SUB_GROUP_ID_CSV, USER_COMPANY);
    }

    @Test
    public void givenCreateUserWhenUserIsAdminThenReturnUserAlreadyExists() throws Exception {
        //Arrange
        final Users users = createDummyUsers();
        setStubbingForPrivilege(UserGroupMap.MembershipType.ADMIN);
        doReturn(users).when(userServiceRepository).getUserById(anyLong());
        when(userServiceRepository.getUserByEmailAndEnterpriseId(anyString(), anyLong())).thenReturn(users);

        //Act
        final UserAPIResponse response = userService.createUser(ACTOR_ID, USER_MAIL, FIRST_NAME, LAST_NAME,
                USR_TIMEZONE, SUB_GROUP_ID_CSV, USER_COMPANY);
        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.USER_ALREADY_EXISTS));
    }

    @Test
    public void givenCreateUserWhenUserIsAdminThenSuccess() throws Exception {
        //Arrange
        final UserAPIResponse response = doCreateUserAndAssert();
        final List<Users> usersList = assertUsers();
        assertUsers(usersList);
        assertUsers(response, usersList);
        errorCollector.checkThat(response.getUser(), equalTo(user));
    }

    private List<Users> assertUsers() throws Exception {
        verify(userServiceRepository).sendActivationMail(usersArgumentCaptor.capture());
        final List<Users> usersList = usersArgumentCaptor.getAllValues();
        errorCollector.checkThat(usersList.get(0).getUserName(), equalTo(USER_MAIL));
        errorCollector.checkThat(usersList.get(0).getCompany(), equalTo(USER_COMPANY));
        errorCollector.checkThat(usersList.get(0).getEmail(), equalTo(USER_MAIL));
        errorCollector.checkThat(usersList.get(0).getFirstName(), equalTo(FIRST_NAME));
        errorCollector.checkThat(usersList.get(0).getLastName(), equalTo(LAST_NAME));
        errorCollector.checkThat(usersList.get(0).getDomain(), equalTo("mail.com"));
        errorCollector.checkThat(usersList.get(0).getSalt(), equalTo(""));
        return usersList;
    }

    private void assertUsers(final UserAPIResponse response, final List<Users> usersList) {
        errorCollector.checkThat(usersList.get(0).getInsertedBy(), equalTo(FR_USER_ID));
        errorCollector.checkThat(usersList.get(0).getUpdatedBy(), equalTo(FR_USER_ID));
        errorCollector.checkThat(usersList.get(0).getOwnedBy(), equalTo(OWNED_BY));
        errorCollector.checkThat(usersList.get(0).getOwnedByType(), equalTo(BaseItem.OwnedByType.GROUP));
        errorCollector.checkThat(usersList.get(0).getProductType(), equalTo("FR"));
        errorCollector.checkThat(usersList.get(1).getFirstName(), equalTo(FIRST_NAME));
        errorCollector.checkThat(usersList.get(2).getFirstName(), equalTo(FIRST_NAME));
        errorCollector.checkThat(response.getStatusCode(), equalTo(StatusCode.REQUEST_SUCCESS));
    }

    private void assertUsers(final List<Users> usersList) {
        errorCollector.checkThat(usersList.get(0).getTimeZone(), equalTo(USR_TIMEZONE));
        errorCollector.checkThat(usersList.get(0).getType(), equalTo(Users.Type.IDENTIFIED));
        errorCollector.checkThat(usersList.get(0).getOrigin(), equalTo(Users.Origin.FRAPI));
        errorCollector.checkThat(usersList.get(0).getFlags(), equalTo(BaseItem.FLAGS.ACTIVE));
    }

    private UserAPIResponse doCreateUserAndAssert() throws Exception {
        mockGroupPreference();
        //Act
        final UserAPIResponse response = userService.createUser(ACTOR_ID, USER_MAIL, FIRST_NAME, LAST_NAME,
                USR_TIMEZONE, SUB_GROUP_ID_CSV, USER_COMPANY);
        //Assert
        verify(servicesAPIUtil).generateRandomString(8);
        verify(userServiceRepository).createUser(usersArgumentCaptor.capture(), usersArgumentCaptor.capture(),
                eq(SUB_GROUP_ID_CSV));
        return response;
    }

    @Test
    public void givenCreateUserAndFirstNameIsNullAndTimeZoneIsNullWhenUserIsAdminThenSuccess() throws Exception {
        //Arrange
        mockGroupPreference();
        //Act
        final UserAPIResponse response = userService.createUser(ACTOR_ID, USER_MAIL, null, LAST_NAME,
                null, SUB_GROUP_ID_CSV, USER_COMPANY);
        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.REQUEST_SUCCESS));
    }

    @Test
    public void givenActorIdWhenDeleteUserThenReturnResponse() throws Exception {
        //Arrange
        final EmailSchedule emailSchedule = createEmailSchedule();
        final EmailUserInfo emailUserInfo = createEmailUserInfo();
        final Users users = createDummyUsers();
        PowerMockito.when(UsersDbAPI.getUserById(any(Transaction.class), anyLong()))
                .thenReturn(users);
        PowerMockito.when(EmailUserInfoDbAPI.fetchEmailUserInfoByEmail(anyString()))
                .thenReturn(Collections.singletonList(emailUserInfo));
        PowerMockito.when(EmailUserInfoDbAPI.fetchEmailUserInfoByUserId(any(Transaction.class), anyLong()))
                .thenReturn(emailUserInfo);
        PowerMockito.when(EmailScheduleDbAPI.fetchEmailScheduleByUserId(any(Transaction.class), anyLong()))
                .thenReturn(Collections.singletonList(emailSchedule));
        PowerMockito.when(EmailUserSearchDbAPI.fetchEmailUserSearchByUserId(anyLong()))
                .thenReturn(Collections.singletonList(emailUserSearch));
        //Act
        final UserAPIResponse response = userService.deleteUser(ACTOR_ID);
        //Assert
        verify(userServiceRepository).deleteUser(ACTOR_ID);
        errorCollector.checkThat(response.getStatusCode(), equalTo(StatusCode.REQUEST_SUCCESS));
    }

    @Test
    public void givenActorIdWhenDeleteUserThenThrowsException() throws Exception {
        //Arrange
        doThrow(new Exception(MESSAGE)).when(userServiceRepository).deleteUser(anyLong());
        //Assert
        assertException();
        //Act
        userService.deleteUser(ACTOR_ID);
    }

    @Test
    public void givenUserMailWhenGetUserByEmailThenUserNotFound() throws Exception {
        //Arrange
        doReturn(null).when(userServiceRepository)
                .getUserByEmailAndEnterpriseId(anyString(), anyLong());
        //Act
        final UserAPIResponse response = userService.getUserByEmail(USER_MAIL,
                ENTERPRISE_ID, true);
        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.USER_NOT_FOUND));
    }

    @Test
    public void givenGetUserByEmailWhenUserFoundThenResponse() throws Exception {
        //Arrange
        mockUsersGroupMap();
        //Act
        final UserAPIResponse response = userService.getUserByEmail(USER_MAIL, ENTERPRISE_ID, true);
        //Assert
        errorCollector.checkThat(response.getUser(), equalTo(user));
        errorCollector.checkThat(response.getStatusCode(), equalTo(StatusCode.REQUEST_SUCCESS));
    }

    @Test
    public void givenGetUserByEmailWhenUserFoundThenThrowsException() throws Exception {
        //Arrange
        mockUsersGroupMap();
        doThrow(new Exception(MESSAGE)).when(convertUtil)
                .convertDbUserToDomainUser(any(Users.class), any(UserGroupMap.MembershipType.class));
        //Assert
        assertException();
        //Act
        userService.getUserByEmail(USER_MAIL, ENTERPRISE_ID, true);
    }

    @Test
    public void givenUserIdWhenGetUserByIdThenReturnResponse() throws Exception {
        //Act
        final UserAPIResponse response = userService.getUserById(Long.parseLong(USER_ID));
        //Assert
        errorCollector.checkThat(response.getStatusCode(), equalTo(StatusCode.USER_NOT_FOUND));
    }

    @Test
    public void givenUserEmailAndEnterpriseIdWhenGetUserByEmailThenReturnResponse() throws Exception {
        //Act
        final UserAPIResponse response = userService.getUserByEmail(USER_MAIL, ENTERPRISE_ID);
        //Assert
        errorCollector.checkThat(response.getStatusCode(), equalTo(StatusCode.USER_NOT_FOUND));
    }

    private static User createDummyUser() {
        final User userLocal = new User();
        userLocal.setDomain(USER_DOMAIN);
        userLocal.setFirstName(FIRST_NAME);
        userLocal.setFrMonitorId(FR_MONITOR_ID);
        userLocal.setOwnedBy(OWNED_BY);
        userLocal.setLastName(LAST_NAME);
        userLocal.setUserCompany(USER_COMPANY);
        userLocal.setUserId(USER_ID);
        userLocal.setUserName(USER_NAME_VALUE);
        return userLocal;
    }

    private static Users createDummyUsers() {
        final Users userLocal = new Users();
        userLocal.setId(FR_USER_ID);
        userLocal.setOwnedBy(OWNED_BY);
        userLocal.setFirstName(FIRST_NAME);
        userLocal.setDomain(USER_DOMAIN);
        userLocal.setMigrationStatus(Users.MigrationStatus.FORCED);
        userLocal.setFlags(BaseItem.FLAGS.ACTIVE);
        userLocal.setAccessLevel(Users.AccessLevel.COMPONENT);
        userLocal.setMonitorOrderType(Tags.SearchOrderType.NAME);
        userLocal.setType(Users.Type.IDENTIFIED);
        userLocal.setOrigin(Users.Origin.COMPONENT);
        userLocal.setOwnedByType(BaseItem.OwnedByType.USER);
        userLocal.setEmail(USER_MAIL);
        return userLocal;
    }

    private static UserGroupMap createUserGroupMap(UserGroupMap.MembershipType type) {
        final UserGroupMap userGroupMapLocal = new UserGroupMap();
        userGroupMapLocal.setOwnedByType(BaseItem.OwnedByType.USER);
        userGroupMapLocal.setUserId(Long.parseLong(USER_ID));
        userGroupMapLocal.setMembershipType(type);
        userGroupMapLocal.setGroupId(GROUP_ID);
        userGroupMapLocal.setFlags(BaseItem.FLAGS.ACTIVE);
        return userGroupMapLocal;
    }

    private static Groups createGroups() {
        final Groups groupsLocal = new Groups();
        groupsLocal.setParentGroupId(PARENT_GROUP_ID);
        groupsLocal.setGroupType(Groups.GroupType.FIRSTRAIN);
        groupsLocal.setProductType(PRODUCT_TYPE);
        return groupsLocal;
    }

    private static GroupPreference createGroupPreference() {
        final GroupPreference preference = new GroupPreference();
        preference.setImportFromSF(true);
        preference.setActivationEmail(true);
        return preference;
    }

    private static EmailUserInfo createEmailUserInfo() {
        final EmailUserInfo emailUserInfoLocal = new EmailUserInfo();
        emailUserInfoLocal.setEmail(USER_MAIL);
        emailUserInfoLocal.setId(MAIL_ID);
        emailUserInfoLocal.setStatus(EmailUserInfo.EmailUserInfoEnums.ACTIVE);
        emailUserInfoLocal.setFirstName(FIRST_NAME);
        emailUserInfoLocal.setLastName(LAST_NAME);
        emailUserInfoLocal.setPrefXML(PREF_XML);
        return emailUserInfoLocal;
    }

    private static EmailSchedule createEmailSchedule() {
        final EmailSchedule emailScheduleLocal = new EmailSchedule();
        emailScheduleLocal.setUserId(SCHEDULE_USER_ID);
        emailScheduleLocal.setTemplateID(TEMPLATE_ID);
        emailScheduleLocal.setId(SCHEDULE_ID);
        return emailScheduleLocal;
    }

    private void setStubbingForPrivilege(UserGroupMap.MembershipType membershipType) {
        final UserGroupMap userGroupMapLocal = createUserGroupMap(membershipType);
        doReturn(Collections.singletonList(userGroupMapLocal))
                .when(userServiceRepository).getUserGroupMapByUserId(anyLong());
    }

    private void mockUsers() throws Exception {
        final Groups groups = createGroups();
        final Users users = createDummyUsers();
        setStubbingForPrivilege(UserGroupMap.MembershipType.ADMIN);
        doReturn(users).when(userServiceRepository).getUserById(anyLong());
        PowerMockito.when(GroupsDbAPI.fetchUserGroupById(anyString(), anyLong()))
                .thenReturn(groups);
        PowerMockito.when(UsersDbAPI.getUserByUserNameWithoutOrigin(anyString(), anyString(), anyString()))
                .thenReturn(null);
    }

    private void assertException() {
        expectedException.expect(Exception.class);
        expectedException.expectMessage(MESSAGE);
    }

    private void mockUsersGroupMap() {
        final Users users = createDummyUsers();
        final UserGroupMap userGroupMap = createUserGroupMap(UserGroupMap.MembershipType.ADMIN);
        doReturn(users).when(userServiceRepository)
                .getUserByEmailAndEnterpriseId(anyString(), anyLong());
        doReturn(Collections.singletonList(userGroupMap))
                .when(userServiceRepository).getUserGroupMapByUserId(anyLong());
    }

    private void mockGroupPreference() throws Exception {
        final Groups groups = createGroups();
        final Users users = createDummyUsers();
        setStubbingForPrivilege(UserGroupMap.MembershipType.ADMIN);
        doReturn(users).when(userServiceRepository).getUserById(anyLong());
        PowerMockito.when(GroupsDbAPI.fetchUserGroupById(anyString(), anyLong()))
                .thenReturn(groups);
        PowerMockito.when(UsersDbAPI.getUserByUserNameWithoutOrigin(anyString(), anyString(), anyString()))
                .thenReturn(null);
        doReturn(Collections.singletonList(createDummyUsers())).when(userServiceRepository)
                .getUsersByGrpIdAndMembershipType(anyLong(), any(UserGroupMap.MembershipType.class));
        when(userServiceRepository.getUserGroupById(anyLong())).thenReturn(groups);
        final GroupPreference groupPreference = createGroupPreference();
        PowerMockito.when(GroupPreference.parseXML(anyString())).thenReturn(groupPreference);
    }
}
