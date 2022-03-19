package com.firstrain.frapi.service.impl;

import com.firstrain.db.api.UserGroupMapDbAPI;
import com.firstrain.db.obj.Tags;
import com.firstrain.db.obj.Users;
import com.firstrain.db.obj.BaseItem;
import com.firstrain.db.obj.UserGroupMap;
import com.firstrain.frapi.domain.User;
import com.firstrain.frapi.obj.MonitorWizardFilters;
import com.firstrain.frapi.pojo.EnterprisePref;
import com.firstrain.frapi.pojo.Entity;
import com.firstrain.frapi.pojo.MonitorAPIResponse;
import com.firstrain.frapi.util.StatusCode;
import com.firstrain.utils.JSONUtility;
import com.firstrain.utils.object.PerfActivityType;
import com.firstrain.utils.object.PerfMonitor;
import org.junit.Test;

import java.util.Collections;
import java.util.Set;
import java.util.Arrays;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.doThrow;

public class MonitorServiceImplPartTwoTest extends MonitorServiceImplTestSetUp {

    @Test
    public void givenIllegalArgumentWhenAddFiltersThenValidate() throws Exception {
        //Act
        MonitorAPIResponse response = monitorService.addFilters(FR_USER_ID, MONITOR_ID,
                Collections.<String>emptyList());

        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.ILLEGAL_ARGUMENT));
    }

    @Test
    public void givenEntityFoundValueWhenAddFiltersThenValidate() throws Exception {
        //Arrange
        doReturn(null).when(monitorServiceRepository).getTagById(anyLong());

        //Act
        MonitorAPIResponse response = monitorService.addFilters(FR_USER_ID, MONITOR_ID,
                Arrays.asList("-12 :12", "-12: 13"));

        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.ENTITY_NOT_FOUND));
    }

    @Test
    public void givenUserOwnMonitorWhenAddFiltersThenValidate() throws Exception {
        //Act
        MonitorAPIResponse response = monitorService.addFilters(MONITOR_ID, MONITOR_ID,
                Arrays.asList("-12 :12", "-12: 13"));

        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.USER_DOESNOT_OWN_MONITOR));
    }

    @Test
    public void givenExistingFilterWhenAddFiltersThenValidate() throws Exception {
        //Arrange
        initAddFilters();
        doReturn(FILTER_STRING_VALUE).when(servicesAPIUtil).addFilters(anyList(),
                any(MonitorWizardFilters.class), anyList());

        //Act
        MonitorAPIResponse response = monitorService.addFilters(FR_USER_ID, MONITOR_ID,
                Arrays.asList("-12 :12", "-12: 13"));

        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.FILTERS_ALREADY_PRESENT));
    }

    @Test
    public void givenValueNewWhenRemoveFiltersThenThrowsException() throws Exception {
        //Arrange
        doThrow(new Exception()).when(servicesAPIUtil).getKeywordsAndFilters(anyList());
        try {
            //Act
            monitorService.removeFilters(FR_USER_ID, MONITOR_ID, Arrays.asList("-12 :12", "-12: 13"));
            fail(EXCEPTION);
        } catch (Exception ex) {
            verifyStatic();
            PerfMonitor.recordStats(PARENT_GROUP_ID, PerfActivityType.ReqTime, "removeFilters");
        }
    }

    @Test
    public void givenValueNewWhenRemoveFiltersThenValidate() throws Exception {
        //Arrange
        initRemoveFilters();

        //Act
        MonitorAPIResponse response = monitorService.removeFilters(FR_USER_ID, MONITOR_ID, Arrays.asList("-12 :12", "-12: 13"));

        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.REQUEST_SUCCESS));
        errorCollector.checkThat(response.getMonitorId(), is(MONITOR_ID));
        errorCollector.checkThat(response.getMonitorName(), is(TAG_NAME));
    }

    @Test
    public void givenIllegalArgumentsWhenRmoveFiltersThenValidate() throws Exception {
        //Act
        MonitorAPIResponse response = monitorService.removeFilters
                (FR_USER_ID, MONITOR_ID, Collections.<String>emptyList());

        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.ILLEGAL_ARGUMENT));
    }

    @Test
    public void givenTagIdNullWhenRmoveFiltersThenValidate() throws Exception {
        //Arrange
        doReturn(null).when(monitorServiceRepository).getTagById(anyLong());

        //Act
        MonitorAPIResponse response = monitorService.removeFilters
                (FR_USER_ID, MONITOR_ID, Arrays.asList("-12 :12", "-12: 13"));

        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.ENTITY_NOT_FOUND));
    }

    @Test
    public void givenUserDoesnNotMonitorWhenRmoveFiltersThenValidate() throws Exception {
        //Act
        MonitorAPIResponse response = monitorService.removeFilters
                (MONITOR_ID, MONITOR_ID, Arrays.asList("-12 :12", "-12: 13"));

        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.USER_DOESNOT_OWN_MONITOR));
    }

    @Test
    public void givenExistingFiltersWhenRmoveFiltersThenValidate() throws Exception {
        //Arrange
        initRemoveFilters();
        doReturn(FILTER_STRING_VALUE).when(servicesAPIUtil).removeFilters(anyList(),
                any(MonitorWizardFilters.class));

        //Act
        MonitorAPIResponse response = monitorService.removeFilters
                (FR_USER_ID, MONITOR_ID, Arrays.asList("-12 :12", "-12: 13"));

        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.FILTERS_NOT_FOUND));
    }

    @Test
    public void givenGetMonitorDetailsWhenFlagsDeletedThenResponseThenValidate() throws Exception {
        //Arrange
        Tags tags = createTags();
        tags.setFlags(BaseItem.FLAGS.DELETED);
        doReturn(tags).when(monitorServiceRepository).getTagById(anyLong());

        //Act
        MonitorAPIResponse response = monitorService.getMonitorDetails(createUser(), MONITOR_ID);

        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.ENTITY_NOT_FOUND));
    }

    @Test
    public void givenTagNotNullWhenFlagsDeletedThenResponseThenValidate() throws Exception {
        //Arrange
        Tags tags = createTags();
        tags.setFlags(BaseItem.FLAGS.ACTIVE);
        tags.setOwnedByType(BaseItem.OwnedByType.USER);
        User user = createUser();
        user.setOwnedBy(FR_USER_ID);
        when(UserGroupMapDbAPI.getUserGroupMapByUserId(anyString(), anyLong()))
                .thenReturn(Collections.singletonList(createUserGroupMap(UserGroupMap.
                        MembershipType.ANONYMOUS)));
        doReturn(tags).when(monitorServiceRepository).getTagById(anyLong());

        //Act
        MonitorAPIResponse response = monitorService.getMonitorDetails(createUser(), MONITOR_ID);
        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.USER_DOESNOT_OWN_MONITOR));
    }

    @Test
    public void givenTagNotNullWhenGetEntityStatusThenResponseThenValidate() throws Exception {
        //Arrange
        Tags tags = createTags();
        tags.setFlags(BaseItem.FLAGS.ACTIVE);
        tags.setOwnedByType(BaseItem.OwnedByType.USER);
        User user = createUser();
        user.setOwnedBy(FR_USER_ID);
        when(UserGroupMapDbAPI.getUserGroupMapByUserId(anyString(), anyLong()))
                .thenReturn(Collections.singletonList(createUserGroupMap(UserGroupMap.
                        MembershipType.ANONYMOUS)));
        doReturn(tags).when(monitorServiceRepository).getTagById(anyLong());

        //Act
        MonitorAPIResponse response = monitorService.getEntityStatus(createUser(), MONITOR_ID, "2352");

        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.USER_DOESNOT_OWN_MONITOR));
    }

    @Test
    public void givenGetMonitorDetailsWhenFlagsIsNotDeletedThenResponseThenValidate() throws Exception {
        //Arrange
        Tags tags = createTags();
        tags.setFlags(BaseItem.FLAGS.ACTIVE);
        doReturn(tags).when(monitorServiceRepository).getTagById(anyLong());

        //Act
        MonitorAPIResponse response = monitorService.getMonitorDetails(createUser(), MONITOR_ID);

        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.INSUFFICIENT_PRIVILEGE));
    }

    @Test
    public void givenValueNewWhenGetMonitorDetailsThenThrowsException() throws Exception {
        //Arrange
        when(JSONUtility.deserialize(anyString(), eq(MonitorWizardFilters.class)))
                .thenThrow(Exception.class);

        try {
            //Act
            monitorService.getMonitorDetails(createUser(), MONITOR_ID);
            fail(EXCEPTION);
        } catch (Exception ex) {
            verifyStatic();
            PerfMonitor.recordStats(PARENT_GROUP_ID, PerfActivityType.ReqTime, "getMonitorDetails");
        }
    }

    @Test
    public void givenGetMonitorDetailsWhenFlagsIsNotDeletedAndHaveAdminPrivilegesThenResponseThenValidate() throws Exception {
        //Arrange
        Tags tags = createTags();
        tags.setFlags(BaseItem.FLAGS.ACTIVE);
        User user = createUser();
        user.setOwnedBy(FR_USER_ID);
        when(UserGroupMapDbAPI.getUserGroupMapByUserId(anyString(), anyLong()))
                .thenReturn(Collections.singletonList(createUserGroupMap(UserGroupMap.MembershipType.ADMIN)));
        doReturn(tags).when(monitorServiceRepository).getTagById(anyLong());

        //Act
        MonitorAPIResponse response = monitorService.getMonitorDetails(user, MONITOR_ID);

        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.REQUEST_SUCCESS));
        errorCollector.checkThat(response.getMonitorConfig().getMonitorName(), is(TAG_NAME));
        errorCollector.checkThat(response.getMonitorConfig().getMonitorId(), is("766"));
        errorCollector.checkThat(response.getMonitorConfig().getOwnedBy(), is(FR_USER_ID));
        errorCollector.checkThat(response.getMonitorConfig().getOwnedByType(), is("GROUP"));
        errorCollector.checkThat(response.getMonitorConfig().getFilters().size(), is(1));
        errorCollector.checkThat(response.getMonitorConfig().getFilters().get(0), is(ADVANCED_FILTERS));
        errorCollector.checkThat(response.getMonitorConfig().getQueries().size(), is(2));
        errorCollector.checkThat(response.getMonitorConfig().getQueries().get(1).getQueryName(), is(ENT_NAME));
        errorCollector.checkThat(response.getMonitorConfig().getQueries().get(0).getQueryString(), is("2352"));
    }

    @Test
    public void givenValueNewWhenGetEntityStatusThenValidate() throws Exception {
        //Arrange
        Tags tags = createTags();
        tags.setFlags(BaseItem.FLAGS.ACTIVE);
        User user = createUser();
        user.setOwnedBy(FR_USER_ID);
        when(UserGroupMapDbAPI.getUserGroupMapByUserId(anyString(), anyLong()))
                .thenReturn(Collections.singletonList(createUserGroupMap(UserGroupMap.MembershipType.ADMIN)));
        doReturn(tags).when(monitorServiceRepository).getTagById(anyLong());

        //Act
        MonitorAPIResponse response = monitorService.getEntityStatus(createUser(), MONITOR_ID, "2352");

        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.REQUEST_SUCCESS));
        errorCollector.checkThat(response.getEntityStatus().getEntityStatus(), is(true));
        errorCollector.checkThat(response.getEntityStatus().getName(), is(TAG_NAME));
        errorCollector.checkThat(response.getEntityStatus().getId(), is("766"));
        errorCollector.checkThat(response.getEntityStatus().getEntity().getName(), is(ENT_NAME));
        errorCollector.checkThat(response.getEntityStatus().getEntity().getSearchToken(), is("2352"));
    }

    @Test
    public void givenGetEntityStatusWhenUserHasItemsThenResponseThenValidate() throws Exception {
        //Arrange
        Tags tags = createTags();
        tags.setFlags(BaseItem.FLAGS.ACTIVE);
        User user = createUser();
        user.setOwnedBy(FR_USER_ID);
        when(UserGroupMapDbAPI.getUserGroupMapByUserId(anyString(), anyLong()))
                .thenReturn(Collections.singletonList(createUserGroupMap(UserGroupMap.MembershipType.ADMIN)));
        doReturn(tags).when(monitorServiceRepository).getTagById(anyLong());

        //Act
        MonitorAPIResponse response = monitorService.getEntityStatus(createUser(), MONITOR_ID, MONITOR_ENTITY);

        //Assert
        Entity entity = response.getEntityStatus().getEntity();
        errorCollector.checkThat(response.getEntityStatus().getEntityStatus(), is(false));
        errorCollector.checkThat(entity.getName(), is(MONITOR_ENTITY));
        errorCollector.checkThat(entity.getSearchToken(), is(MONITOR_ENTITY));
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.REQUEST_SUCCESS));
    }

    @Test
    public void givenGetEntityStatusWhenUserHasSufficientPrivilegesThenResponse()
            throws Exception {
        //Arrange
        Tags tags = createTags();
        tags.setFlags(BaseItem.FLAGS.ACTIVE);
        User user = createUser();
        user.setOwnedBy(FR_USER_ID);
        when(UserGroupMapDbAPI.getUserGroupMapByUserId(anyString(), anyLong()))
                .thenReturn(Collections.singletonList(createUserGroupMap(UserGroupMap.MembershipType.ADMIN)));
        doReturn(tags).when(monitorServiceRepository).getTagById(anyLong());
        doReturn(null).when(monitorServiceRepository).getItemsByTagId(anyLong());

        //Act
        MonitorAPIResponse response = monitorService.getEntityStatus(createUser(), MONITOR_ID, MONITOR_ENTITY);

        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.REQUEST_SUCCESS));
        errorCollector.checkThat(response.getMonitorId(), is(MONITOR_ID));
        errorCollector.checkThat(response.getEntityStatus().getEntity().getName(), is(MONITOR_ENTITY));
        errorCollector.checkThat(response.getEntityStatus().getEntity().getSearchToken(), is(MONITOR_ENTITY));
    }

    @Test
    public void givenGetEntityStatusWhenUserHasInsufficientPrivilegesThenResponse()
            throws Exception {
        //Arrange
        Tags tags = createTags();
        tags.setFlags(BaseItem.FLAGS.ACTIVE);
        User user = createUser();
        user.setOwnedBy(FR_USER_ID);
        when(UserGroupMapDbAPI.getUserGroupMapByUserId(anyString(), anyLong()))
                .thenReturn(Collections.singletonList(createUserGroupMap(UserGroupMap.MembershipType.ANONYMOUS)));
        doReturn(tags).when(monitorServiceRepository).getTagById(anyLong());
        doReturn(null).when(monitorServiceRepository).getItemsByTagId(anyLong());

        //Act
        MonitorAPIResponse response = monitorService.getEntityStatus(createUser(), MONITOR_ID, MONITOR_ENTITY);

        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.INSUFFICIENT_PRIVILEGE));
        errorCollector.checkThat(response.getMonitorId(), is(MONITOR_ID));
    }

    @Test
    public void givenGetEntityStatusWhenFlagsDeletedThenValidatee() throws Exception {
        //Arrange
        Tags tags = createTags();
        tags.setFlags(BaseItem.FLAGS.DELETED);
        doReturn(tags).when(monitorServiceRepository).getTagById(anyLong());

        //Act
        MonitorAPIResponse response = monitorService.getEntityStatus(createUser(), MONITOR_ID, MONITOR_ENTITY);

        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.ENTITY_NOT_FOUND));
    }

    @Test
    public void givenValueNewWhenGetEntityStatusThenThrowsException()
            throws Exception {
        //Arrange
        Tags tags = createTags();
        tags.setFlags(BaseItem.FLAGS.ACTIVE);
        User user = createUser();
        user.setOwnedBy(FR_USER_ID);
        when(UserGroupMapDbAPI.getUserGroupMapByUserId(anyString(), anyLong()))
                .thenReturn(Collections.singletonList(createUserGroupMap(UserGroupMap.MembershipType.ADMIN)));
        doReturn(tags).when(monitorServiceRepository).getTagById(anyLong());
        when(JSONUtility.deserialize(anyString(), eq(MonitorWizardFilters.class)))
                .thenThrow(Exception.class);
        try {
            //Act
            monitorService.getEntityStatus(createUser(), MONITOR_ID, MONITOR_ENTITY);
            fail(EXCEPTION);
        } catch (Exception ex) {
            verifyStatic();
            PerfMonitor.recordStats(PARENT_GROUP_ID, PerfActivityType.ReqTime, "getEntitiesStatus");
        }
    }

    @Test
    public void givenRemoveMonitorWhenUserHasInsufficientPrivilegesThenValidate() throws Exception {
        //Arrange
        Tags tags = createTags();
        tags.setFlags(BaseItem.FLAGS.ACTIVE);
        User user = createUser();
        user.setOwnedBy(FR_USER_ID);
        when(UserGroupMapDbAPI.getUserGroupMapByUserId(anyString(), anyLong()))
                .thenReturn(Collections.singletonList(createUserGroupMap(UserGroupMap.MembershipType.ADMIN)));
        doReturn(tags).when(monitorServiceRepository).getTagById(anyLong());

        //Act
        MonitorAPIResponse response = monitorService.removeMonitor(createUser(), GROUP_ID);
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.INSUFFICIENT_PRIVILEGE));
    }

    @Test
    public void givenRemoveMonitorWhenUserHasPrivilegesThenValidate() throws Exception {
        //Arrange
        Tags tags = createTags();
        tags.setOwnedBy(FR_USER_ID);
        tags.setFlags(BaseItem.FLAGS.ACTIVE);
        User user = createUser();
        user.setUserId(String.valueOf(FR_USER_ID));
        when(UserGroupMapDbAPI.getUserGroupMapByUserId(anyString(), anyLong()))
                .thenReturn(Collections.singletonList(createUserGroupMap(UserGroupMap.MembershipType.ADMIN)));
        doReturn(tags).when(monitorServiceRepository).getTagById(anyLong());

        //Act
        MonitorAPIResponse response = monitorService.removeMonitor(user, GROUP_ID);

        //Assert
        errorCollector.checkThat(response.getMonitorId(), is(TAGS_ID));
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.REQUEST_SUCCESS));
        errorCollector.checkThat(response.getMonitorName(), is(TAG_NAME));
    }

    @Test
    public void givenRemoveMonitorWhenTagsFlagIsDeletedThenValidate() throws Exception {
        //Arrange
        setRemoveTags();

        //Act
        MonitorAPIResponse response = monitorService.removeMonitor(createUser(), GROUP_ID);

        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(106));
    }

    @Test
    public void givenRemoveTagNullWhenTagsFlagIsDeletedThenValidate() throws Exception {
        //Arrange
        setRemoveTags();
        doReturn(null).when(monitorServiceRepository).getTagById(anyLong());

        //Act
        MonitorAPIResponse response = monitorService.removeMonitor(createUser(), GROUP_ID);

        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(100));
    }

    @Test
    public void givenRemoveMonitorAdminWhenTagsFlagIsDeletedThenValidate() throws Exception {
        //Arrange
        setRemoveTags();
        UserGroupMap groupMap = createUserGroupMap(UserGroupMap.MembershipType.ANONYMOUS);
        doReturn(Collections.singletonList(groupMap))
                .when(userServiceRepository).getUserGroupMapByUserId(anyLong());
        //Act
        MonitorAPIResponse response = monitorService.removeMonitor(createUser(), GROUP_ID);

        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(106));
    }

    @Test
    public void givenRemoveMonitorAdminWhenGetAllGroupIdsOfUserThenValidate() throws Exception {
        //Act
        Set<Long> result = monitorService.getAllGroupIdsOfUser(PARENT_GROUP_ID, GROUP_ID);

        //Assert
        verify(userServiceRepository).getUserGroupMapByUserId(1L);
        errorCollector.checkThat(result.isEmpty(), is(true));
    }

    @Test
    public void givenValueNewWhenRemoveMonitorThenThrowsException() throws Exception {
        //Arrange
        setRemoveTags();
        doThrow(new Exception()).when(monitorService,
                "getAllGroupIdsOfUser", anyLong(),anyLong());
        try {
            //Act
            monitorService.removeMonitor(createUser(), GROUP_ID);
            fail(EXCEPTION);
        } catch (Exception ex) {
            verifyStatic();
            PerfMonitor.recordStats(PARENT_GROUP_ID, PerfActivityType.ReqTime, "removeMonitor");
        }
    }

    @Test
    public void givenValueNewWhenCreateMonitorThenValidate() throws Exception {
        //Arrange
        setTags();
        EnterprisePref pref = new EnterprisePref();
        pref.setSearchesPerMonitor(SEARCHES_PER_MONITOR);

        //Act
        MonitorAPIResponse response = monitorService.createMonitor(FR_USER_ID, MONITOR_ENTITY,
                Collections.singletonList(MONITOR_ENTITY),
                Collections.singletonList(FILTER_STRING_VALUE), pref);
        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.REQUEST_SUCCESS));
        errorCollector.checkThat(response.getMonitorName(), is(TAG_NAME));
        errorCollector.checkThat(response.getMonitorId(), is(TAGS_ID));
    }

    @Test
    public void givenListNullWhenCreateMonitorThenValidate() throws Exception {
        //Arrange
        setTags();
        doReturn(null).when(monitorServiceRepository).validateTokensUsingEntityCache(anyList(), anyList());

        //Act
        MonitorAPIResponse response = monitorService.createMonitor(FR_USER_ID, MONITOR_ENTITY,
                Collections.singletonList(MONITOR_ENTITY),
                Collections.singletonList(FILTER_STRING_VALUE), new EnterprisePref());

        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.ILLEGAL_ARGUMENT));
    }

    @Test
    public void givenValueWhenCreateMonitorThenThrowsException() throws Exception {
        //Arrange
        doThrow(new Exception()).when(userServiceRepository).getUserById(anyLong());
        try {
            //Act
            monitorService.createMonitor(FR_USER_ID, MONITOR_ENTITY,
                    Collections.singletonList(MONITOR_ENTITY),
                    Collections.singletonList(FILTER_STRING_VALUE), new EnterprisePref());
            fail(EXCEPTION);
        } catch (Exception ex) {
            verifyStatic();
            PerfMonitor.recordStats(PARENT_GROUP_ID, PerfActivityType.ReqTime, "createMonitor");
        }
    }

    @Test
    public void givenCreateMonitorWhenEntityListIsEmptyTHenResult() throws Exception {
        //Act
        MonitorAPIResponse response = monitorService.createMonitor(FR_USER_ID, MONITOR_ENTITY,
                Collections.<String>emptyList(),
                Collections.singletonList(FILTER_STRING_VALUE), new EnterprisePref());
        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.ILLEGAL_ARGUMENT));
    }

    @Test
    public void givenCreateMonitorWhenMonitorSearchLimitExceededThenResult() throws Exception {
        //Arrange
        EnterprisePref pref = new EnterprisePref();
        pref.setSearchesPerMonitor(1);

        //Act
        MonitorAPIResponse response = monitorService.createMonitor(FR_USER_ID, MONITOR_ENTITY,
                Arrays.asList(ENT_NAME, ENT_NAME),
                Collections.singletonList(FILTER_STRING_VALUE), pref);
        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.MONITOR_SEARCH_LIMIT_EXCEEDED));
    }

    @Test
    public void givenCreateMonitorWhenDuplicateTagIsNotNullThenResult() throws Exception {
        //Arrange
        Tags tags = createTags();
        doReturn(tags).when(monitorServiceRepository).getTagsByOwnerAndTagName(anyLong(),
                any(BaseItem.OwnedByType.class), anyString());
        Users users = createUsers();
        doReturn(users).when(userServiceRepository).getUserById(anyLong());

        // Act
        MonitorAPIResponse response = monitorService.createMonitor(FR_USER_ID, MONITOR_ENTITY,
                Arrays.asList(ENT_NAME, ENT_NAME),
                Collections.singletonList(FILTER_STRING_VALUE), new EnterprisePref());

        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.MONITOR_ALREADY_EXISTS));
    }


}
