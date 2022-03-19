package com.firstrain.frapi.service.impl;

import com.firstrain.db.obj.TagsInfo;
import com.firstrain.db.obj.MailingList;
import com.firstrain.db.obj.Tags;
import com.firstrain.db.obj.EmailSchedule;
import com.firstrain.db.obj.Users;
import com.firstrain.db.obj.Groups;
import com.firstrain.db.obj.BaseItem;
import com.firstrain.frapi.domain.MonitorDetails;
import com.firstrain.frapi.domain.MonitorInfo;
import com.firstrain.frapi.domain.User;
import com.firstrain.frapi.obj.MonitorWizardFilters;
import com.firstrain.frapi.pojo.EnterprisePref;
import com.firstrain.frapi.pojo.Entity;
import com.firstrain.frapi.pojo.MonitorAPIResponse;
import com.firstrain.frapi.pojo.UserItem;
import com.firstrain.frapi.util.MonitorOrderingUtils;
import com.firstrain.frapi.util.StatusCode;
import com.firstrain.utils.FR_ArrayUtils;
import com.firstrain.utils.JSONUtility;
import com.firstrain.utils.object.PerfActivityType;
import com.firstrain.utils.object.PerfMonitor;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import java.util.Collections;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.Arrays;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.doThrow;

public class MonitorServiceImplTest extends MonitorServiceImplTestSetUp {

    @Test
    public void givenValueWHenGetMonitorListByOwnerThenThrowsException() throws Exception {
        //Arrange
        User user = createUser();
        doThrow(new Exception()).when(monitorService,
                "getMonitorDetailsByUser", any(User.class), anyBoolean());
        try {
            //Act
            monitorService.getMonitorListByOwner(user, user, OWNER_TYPE);
            fail(EXCEPTION);
        } catch (Exception ex) {
            verifyStatic();
            PerfMonitor.recordStats(PARENT_GROUP_ID, PerfActivityType.ReqTime, "getMonitorListByOwner");
        }
    }

    @Test
    public void givenValueWhenGetMonitorListByOwnerThenValidate() throws Exception {
        //Arrange
        setMonitorOwner();

        //Act
        MonitorAPIResponse response = monitorService.getMonitorListByOwner(createUser(), createUser()
                , OWNER_TYPE);
        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.REQUEST_SUCCESS));
        MonitorDetails monitorDetails = response.getMonitorDetails();
        errorCollector.checkThat(monitorDetails.getUserName(), is(USER_NAME_VALUE));
        errorCollector.checkThat(monitorDetails.getMonitors().size(), is(1));
        errorCollector.checkThat(monitorDetails.getTitlesForMonitorBuckets().size(), is(1));
    }

    @Test
    public void givenMonitorEmptyWhenGetMonitorListByOwnerThenValidate() throws Exception {
        //Arrange
        LinkedHashMap<String, List<MonitorInfo>> monitorsMap = new LinkedHashMap<>();
        MonitorDetails monitorDetailsLocal = new MonitorDetails();
        monitorDetailsLocal.setMonitors(monitorsMap);
        doReturn(monitorDetailsLocal).when(monitorService,
                "getMonitorDetailsByUser", any(User.class), anyBoolean());
        //Act
        MonitorAPIResponse response = monitorService.getMonitorListByOwner(createUser(), createUser()
                , OWNER_TYPE);

        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.NO_MONITORS));
    }

    @Test
    public void givenValueWhenRemoveEntitiesThenThrowsException() throws Exception {
        //Arrange
        when(JSONUtility.deserialize(anyString(), eq(MonitorWizardFilters.class)))
                .thenThrow(Exception.class);
        try {
            //Act
            monitorService.removeEntities(FR_USER_ID, MONITOR_ID, Collections.singletonList("12: 12"));
            fail(EXCEPTION);
        } catch (Exception ex) {
            verifyStatic();
            PerfMonitor.recordStats(PARENT_GROUP_ID, PerfActivityType.ReqTime, "removeEntities");
        }
    }

    @Test
    public void givenValueWhenRemoveEntitiesThenValidate() throws Exception {
        //Arrange
        initRemoveEntities();

        //Act
        MonitorAPIResponse response = monitorService.removeEntities(FR_USER_ID, MONITOR_ID,
                Collections.singletonList("12: 12"));

        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.REQUEST_SUCCESS));
        errorCollector.checkThat(response.getMonitorId(), is(MONITOR_ID));
        errorCollector.checkThat(response.getMonitorName(), is(TAG_NAME));
        errorCollector.checkThat(response.getEntities().size(), is(1));
        errorCollector.checkThat(response.getEntities().get(0).getRemoved(), is(true));
        verify(monitorServiceRepository).removeEntities(tagsArgumentCaptor.capture(), listLongArgumentCaptor.capture());
        errorCollector.checkThat(tagsArgumentCaptor.getValue().getOwnedBy(), equalTo(FR_USER_ID));
        errorCollector.checkThat(listLongArgumentCaptor.getValue().size(), equalTo(1));
        errorCollector.checkThat(listLongArgumentCaptor.getValue().get(0), equalTo(-1L));
    }

    @Test
    public void givenEntityEmptyWhenRemoveEntitiesThenValidate() throws Exception {
        //Act
        MonitorAPIResponse response = monitorService.removeEntities(FR_USER_ID, MONITOR_ID,
                Collections.<String>emptyList());
        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.ILLEGAL_ARGUMENT));
    }

    @Test
    public void givenEntityNotFoundWhenRemoveEntitiesThenValidate() throws Exception {
        //Arrange
        doReturn(null).when(monitorServiceRepository).getTagById(anyLong());

        //Act
        MonitorAPIResponse response = monitorService.removeEntities(FR_USER_ID, MONITOR_ID,
                Collections.singletonList("12: 12"));

        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.ENTITY_NOT_FOUND));
    }

    @Test
    public void givenMonitorIdFoundWhenRemoveEntitieThenValidates() throws Exception {
        //Arrange
        initRemoveEntities();
        doReturn(null).when(monitorServiceRepository).getItemsByTagId(anyLong());

        //Act
        MonitorAPIResponse response = monitorService.removeEntities(FR_USER_ID, MONITOR_ID,
                Collections.singletonList("12: 12"));

        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.NO_ITEMS));
    }

    @Test
    public void givenItemListWhenRemoveEntitiesThenValidate() throws Exception {
        //Arrange
        initRemoveEntities();

        //Act
        MonitorAPIResponse response = monitorService.removeEntities(MONITOR_ID, MONITOR_ID,
                Collections.singletonList("12: 12"));

        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.USER_DOESNOT_OWN_MONITOR));
    }

    @Test
    public void givenEntryListWhenRemoveEntitiesThenValidate() throws Exception {
        //Arrange
        Entity entityNew = new Entity();
        entityNew.setId("-1L");
        initRemoveEntities();
        doReturn(Collections.singletonMap(TOKEN_ENTRY_SECTOR, entityNew)).when(monitorService,
                "getEntitiesMap", anyList(), anyList(), any(MonitorWizardFilters.class));
        //Act
        MonitorAPIResponse response = monitorService.removeEntities(FR_USER_ID, MONITOR_ID,
                Collections.singletonList("12: 12"));

        //Assert
        errorCollector.checkThat(response.getEntities().size(), is(1));
        errorCollector.checkThat(response.getEntities().get(0).getRemoved(), is(false));
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.REQUEST_SUCCESS));
    }

    @Test
    public void givenEntryListSizeOneWhenRemoveEntitiesThenValidate() throws Exception {
        //Arrange
        Entity entityNew = new Entity();
        entityNew.setId("1");
        initRemoveEntities();
        doReturn(Collections.singletonMap(TOKEN_ENTRY_SECTOR, entityNew)).when(monitorService,
                "getEntitiesMap", anyList(), anyList(), any(MonitorWizardFilters.class));
        doReturn(Collections.singletonList(createItems())).when(monitorServiceRepository).getItemsByTagId(anyLong());

        //Act
        MonitorAPIResponse response = monitorService.removeEntities(FR_USER_ID, MONITOR_ID,
                Collections.singletonList("12: 12"));

        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.NO_ITEMS_IN_MONITOR));
    }

    @Test
    public void givenValueNewWhenPrepareSearchNameThenValidate() throws Exception {
        //Act
        String result = Whitebox.invokeMethod(monitorService,
                "prepareSearchName", "entity");

        //Assert
        errorCollector.checkThat(result, equalTo("entity"));
    }

    @Test
    public void givenValueNewWhenGetEntitiesMapThenValidate() throws Exception {
        //Act
        Map<String, Entity> result = Whitebox.invokeMethod(monitorService, "getEntitiesMap",
                Collections.singletonList(createItems()), Collections.singletonList(SEARCH_TOKEN),
                monitorWizardFiltersFinal);

        //Assert
        errorCollector.checkThat(result.size(), equalTo(1));
        errorCollector.checkThat(result.get(SEARCH_TOKEN).getId(), equalTo("-1L"));
        errorCollector.checkThat(result.get(SEARCH_TOKEN).getSearchToken(), equalTo(SEARCH_TOKEN));
        errorCollector.checkThat(result.get(SEARCH_TOKEN).getName(), equalTo(SEARCH_TOKEN));
    }

    @Test
    public void givenValueNewWhenGetMonitorDetailsThenValidate() throws Exception {
        //Arrange
        TagsInfo infoTagLocal = new TagsInfo();
        infoTagLocal.setTag(createTags());
        infoTagLocal.favoriteUserItemId = "1";
        MonitorInfo monitorInfoLocal = new MonitorInfo();
        MailingList mailingList = new MailingList();
        mailingList.setFlags(MailingList.Flags.ACTIVE);
        infoTagLocal.mailingList = mailingList;
        infoTagLocal.favorite = true;
        infoTagLocal.monitorAdmin = true;

        //Act
        Whitebox.invokeMethod(monitorService, "getMonitorDetails",
                1L, infoTagLocal, monitorInfoLocal);

        //Assert
        errorCollector.checkThat(monitorInfoLocal.getMonitorId(), equalTo("M:766"));
        errorCollector.checkThat(monitorInfoLocal.getMonitorName(), equalTo(TAG_NAME));
        errorCollector.checkThat(monitorInfoLocal.isFavorite(), equalTo(true));
        errorCollector.checkThat(monitorInfoLocal.isMonitorAdmin(), equalTo(true));
        errorCollector.checkThat(monitorInfoLocal.getMailAvailable(), equalTo(true));
        errorCollector.checkThat(monitorInfoLocal.getFavoriteUserItemId(), equalTo(1L));
    }

    @Test
    public void givenValueWhenGetMonitorDetailsThenValidate() throws Exception {
        //Arrange
        Tags tagLocal = new Tags();
        TagsInfo infoTagLocal = new TagsInfo();
        infoTagLocal.setTag(tagLocal);
        MonitorInfo monitorInfoLocal = new MonitorInfo();
        EmailSchedule emailSchedule = new EmailSchedule();
        emailSchedule.setStatus(EmailSchedule.EmailScheduleEnums.ACTIVE);
        infoTagLocal.setEmailSchedule(emailSchedule);
        infoTagLocal.emailSchedule = emailSchedule;

        //Act
        Whitebox.invokeMethod(monitorService, "getMonitorDetails",
                1L, infoTagLocal, monitorInfoLocal);

        //Assert
        errorCollector.checkThat(monitorInfoLocal.getMailAvailable(), equalTo(true));
    }

    @Test
    public void givenValueNewWhenGetOrderedMonitorsThenValidate() throws Exception {
        //Arrange
        doReturn(null).when(monitorServiceRepository).getTagsByOwner(
                any(BaseItem.OwnedByType.class), anyLong());

        //Act
        MonitorOrderingUtils.OrderedMonitors result = Whitebox.invokeMethod(monitorService, "getOrderedMonitors",
                1L, 1L, true, true);

        //Assert
        verify(userServiceRepository).getUserGroupMapByUserId(1L);
        errorCollector.checkThat(result, Matchers.<MonitorOrderingUtils.OrderedMonitors>instanceOf(MonitorOrderingUtils.OrderedMonitors.class));
    }

    @Test
    public void givenValueNewWhenGetMonitorDetailsByUserThenValidate() throws Exception {
        //Arrange
        TagsInfo tagsInfo = new TagsInfo();
        tagsInfo.setGroupName(TAG_NAME);
        tagsInfo.setTag(createTags());
        MonitorOrderingUtils.OrderedMonitors orderLocal = new MonitorOrderingUtils.OrderedMonitors();
        orderLocal.grpIdVsTagsInfoList = Collections.singletonMap(1L, Collections.singletonList(tagsInfo));
        doReturn(orderLocal).when(monitorService,
                "getOrderedMonitors", anyLong(), anyLong(), anyBoolean(), anyBoolean());

        //Act
        MonitorDetails result = Whitebox.invokeMethod(monitorService, "getMonitorDetailsByUser",
                createUser(), true);

        //Assert
        errorCollector.checkThat(result.getUserId(), equalTo("U:68790"));
        errorCollector.checkThat(result.getEmail(), equalTo(EMAIL));
        errorCollector.checkThat(result.getUserName(), equalTo(USER_NAME_VALUE));
    }

    @Test
    public void givenValueWhenPrepareSearchNameThenValidate() throws Exception {
        //Arrange
        mockStatic(FR_ArrayUtils.class);
        when(FR_ArrayUtils.getListBySplitString(anyString(), anyString())).thenReturn(stringList);

        //Act
        String result = Whitebox.invokeMethod(monitorService, "prepareSearchName", "entity");

        //Assert
        errorCollector.checkThat(result, equalTo(""));
    }

    @Test
    public void givenValueNewWhenValidateAllTokensInKeywordSearchThenValidate() throws Exception {
        // Arrange
        doReturn(Collections.singletonList(createEntity())).when(monitorServiceRepository).validateTokensUsingEntityCache(anyList(), anyList());

        //Act
        List<Entity> result = Whitebox.invokeMethod(monitorService, "validateAllTokensInKeywordSearch",
                stringList, stringList);

        //Assert
        errorCollector.checkThat(result.size(), equalTo(1));
        errorCollector.checkThat(result.get(0).getName(), equalTo(ENT_NAME));
    }

    @Test
    public void givenValueNewWhenCategorizeEntitiesThenValidate() throws Exception {
        //Act
        List<List<String>> result = Whitebox.invokeMethod(monitorService,
                "categorizeEntities", stringList);

        //Assert
        errorCollector.checkThat(result.size(), equalTo(3));
        errorCollector.checkThat(result.get(0).size(), equalTo(1));
        errorCollector.checkThat(result.get(0).get(0), equalTo("OR"));
        errorCollector.checkThat(result.get(1).size(), equalTo(0));
        errorCollector.checkThat(result.get(2).size(), equalTo(1));
        errorCollector.checkThat(result.get(2).get(0), equalTo(""));
    }

    @Test
    public void givenValuegetValidEntityThenReturnNull() throws Exception {
        //Act
        Entity result = Whitebox.invokeMethod(monitorService,
                "getValidEntity", (List<Entity>) null, null);

        //Assert
        errorCollector.checkThat(result, equalTo(null));
    }

    @Test
    public void givenValueWhenGetValidEntityThenValidate() throws Exception {
        //Act
        Entity result = Whitebox.invokeMethod(monitorService,
                "getValidEntity", Collections.singletonList(createEntity()), null);

        //Assert
        errorCollector.checkThat(result, nullValue());
    }

    @Test
    public void givenValueEntityWhenGetValidEntityThenValidate() throws Exception {
        //Act
        Entity result = Whitebox.invokeMethod(monitorService,
                "getValidEntity", Collections.singletonList(createEntity()), SEARCH_TOKEN);

        //Assert
        errorCollector.checkThat(result.getId(), equalTo("345"));
        errorCollector.checkThat(result.getName(), equalTo("ent"));
    }

    @Test
    public void givenValueEntityGetMapOfGroupVsTagsThenValidate() throws Exception {
        //Arrange
        Tags tagsLocal = new Tags();
        tagsLocal.setOwnedBy(GROUP_ID);

        //Act
        Map<Groups, List<Tags>> result = Whitebox.invokeMethod(monitorService,
                "getMapOfGroupVsTags",
                Collections.singleton(GROUP_ID), Collections.singletonList(tagsLocal), GROUP_ID);

        //Assert
        errorCollector.checkThat(result.size(), equalTo(1));
        Groups groupsLocal = (Groups) result.keySet().toArray()[0];
        errorCollector.checkThat(result.get(groupsLocal).size(), equalTo(1));
        errorCollector.checkThat(result.get(groupsLocal).get(0).getOwnedBy(), equalTo(GROUP_ID));
        verify(groupServiceRepository).getGroupsByIds(Collections.singleton(GROUP_ID));
    }

    @Test
    public void givenValueParentIdEntityGetMapOfGroupVsTagsThenValidate() throws Exception {
        //Arrange
        Tags tagsLocal = new Tags();
        tagsLocal.setOwnedBy(OWNED_BY);

        //Act
        Map<Groups, List<Tags>> result = Whitebox.invokeMethod(monitorService,
                "getMapOfGroupVsTags",
                Collections.singleton(GROUP_ID), Collections.singletonList(tagsLocal), 1L);

        //Assert
        errorCollector.checkThat(result.size(), equalTo(2));
        Groups groupsLocal = (Groups) result.keySet().toArray()[0];
        Groups groupsLocalNew = (Groups) result.keySet().toArray()[1];
        errorCollector.checkThat(result.get(groupsLocalNew).size(), equalTo(0));
        errorCollector.checkThat(result.get(groupsLocal).size(), equalTo(1));
        errorCollector.checkThat(result.get(groupsLocal).get(0).getOwnedBy(), equalTo(OWNED_BY));
    }

    @Test
    public void givenValueWhenGetOrderedTagInfoListThenValidate() throws Exception {
        //Arrange
        mockStatic(MonitorOrderingUtils.class);
        MonitorOrderingUtils.OrderedMonitors orderLocal = new MonitorOrderingUtils.OrderedMonitors();
        UserItem userItemLocal = new UserItem();
        Users usersLocal = new Users();

        //Act
        List<TagsInfo> result = Whitebox.invokeMethod(monitorService,
                "getOrderedTagInfoList",
                orderLocal, Collections.singletonList(createTags()),
                Collections.singletonMap(GROUP_ID, userItemLocal), createGroups(),
                Collections.singleton(GROUP_ID), true, usersLocal);

        //Assert
        errorCollector.checkThat(result.size(), equalTo(1));
        errorCollector.checkThat(result.get(0).groupName, equalTo(USER_NAME_VALUE));
        errorCollector.checkThat(result.get(0).groupType, equalTo(Groups.GroupType.FIRSTRAIN));
        errorCollector.checkThat(result.get(0).monitorAdmin, equalTo(true));
        verifyStatic();
        MonitorOrderingUtils.sortGroupMonitorByOrderType(eq(createGroups()), tagCaptor.capture());
        errorCollector.checkThat(tagCaptor.getValue().size(), equalTo(1));
        errorCollector.checkThat(tagCaptor.getValue().get(0).monitorAdmin, equalTo(true));
    }

    @Test
    public void givenValueGroupNullWhenGetOrderedTagInfoListThenValidate() throws Exception {
        //Arrange
        MonitorOrderingUtils.OrderedMonitors orderLocal = new MonitorOrderingUtils.OrderedMonitors();
        UserItem userItemLocal = new UserItem();
        Users usersLocal = new Users();
        Tags tagLocal = new Tags();

        //Act
        List<TagsInfo> result = Whitebox.invokeMethod(monitorService,
                "getOrderedTagInfoList",
                orderLocal, Collections.singletonList(tagLocal),
                Collections.singletonMap(GROUP_ID, userItemLocal), createGroups(),
                Collections.singleton(GROUP_ID), true, usersLocal);

        //Assert
        errorCollector.checkThat(result.size(), equalTo(1));
        errorCollector.checkThat(result.get(0).monitorAdmin, equalTo(true));
    }

    @Test
    public void givenValueNullWhenGetOrderedTagInfoListThenValidate() throws Exception {
        //Arrange
        mockStatic(MonitorOrderingUtils.class);
        MonitorOrderingUtils.OrderedMonitors orderLocal = new MonitorOrderingUtils.OrderedMonitors();
        UserItem userItemLocal = new UserItem();
        Users usersLocal = new Users();

        //Act
        List<TagsInfo> result = Whitebox.invokeMethod(monitorService,
                "getOrderedTagInfoList",
                orderLocal, null,
                Collections.singletonMap(GROUP_ID, userItemLocal), null,
                Collections.singleton(GROUP_ID), true, usersLocal);

        //Assert
        verifyStatic();
        MonitorOrderingUtils.sortTagListByName(Collections.<TagsInfo>emptyList());
        errorCollector.checkThat(result.isEmpty(), equalTo(true));
    }

    @Test
    public void givenValueAddEntitiesThenThrowsException() throws Exception {
        //Arrange
        EnterprisePref pref = new EnterprisePref();
        pref.setSearchesPerMonitor(SEARCHES_PER_MONITOR);
        when(JSONUtility.deserialize(anyString(), eq(MonitorWizardFilters.class)))
                .thenThrow(Exception.class);
        try {
            //Act
            monitorService.addEntities(FR_USER_ID, MONITOR_ID, Collections.singletonList("12: 12"), pref);
            fail(EXCEPTION);
        } catch (Exception ex) {
            verifyStatic();
            PerfMonitor.recordStats(PARENT_GROUP_ID, PerfActivityType.ReqTime, "addEntities");
        }
    }

    @Test
    public void givenValueNewWhenAddEntitiesThenValidate() throws Exception {
        //Arrange
        initAddEntities();

        //Act
        MonitorAPIResponse response = monitorService.addEntities(FR_USER_ID, MONITOR_ID, Collections.singletonList("12: 12"),
                enterprisePrefFinal);

        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.REQUEST_SUCCESS));
        errorCollector.checkThat(response.getMonitorId(), is(MONITOR_ID));
        errorCollector.checkThat(response.getMonitorName(), is(TAG_NAME));
        errorCollector.checkThat(response.getEntities().size(), is(1));
        errorCollector.checkThat(response.getEntities().get(0).getAdded(), is(false));
        verify(monitorServiceRepository).addEntities(tagsArgumentCaptor.capture(), entListArgumentCaptor.capture(),
                strListArgumentCaptor.capture(), strListArgumentCaptor.capture());
        errorCollector.checkThat(tagsArgumentCaptor.getValue().getOwnedBy(), equalTo(12L));
        errorCollector.checkThat(entListArgumentCaptor.getValue().size(), equalTo(1));
        errorCollector.checkThat(entListArgumentCaptor.getValue().get(0).getName(), equalTo("ent"));
        errorCollector.checkThat(strListArgumentCaptor.getAllValues().size(), equalTo(2));
        errorCollector.checkThat(strListArgumentCaptor.getAllValues().get(0).isEmpty(), equalTo(true));
        errorCollector.checkThat(strListArgumentCaptor.getAllValues().get(1).isEmpty(), equalTo(true));
    }

    @Test
    public void givenListEmptyWhenAddEntitiesThenValidate() throws Exception {
        //Act
        MonitorAPIResponse response = monitorService.addEntities
                (FR_USER_ID, MONITOR_ID, Collections.<String>emptyList(), enterprisePrefFinal);

        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.ILLEGAL_ARGUMENT));
    }

    @Test
    public void givenTagNullWhenAddEntitiesThenValidate() throws Exception {
        //Arrange
        doReturn(null).when(monitorServiceRepository).getTagById(anyLong());

        //Act
        MonitorAPIResponse response = monitorService.addEntities
                (FR_USER_ID, MONITOR_ID, Collections.singletonList("12: 12"), enterprisePrefFinal);

        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.ENTITY_NOT_FOUND));
    }

    @Test
    public void givenMonitorWhenAddEntities() throws Exception {
        //Act
        MonitorAPIResponse response = monitorService.addEntities
                (MONITOR_ID, MONITOR_ID, Collections.singletonList("12: 12"), enterprisePrefFinal);

        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.USER_DOESNOT_OWN_MONITOR));
    }

    @Test
    public void givenEnterPriseRefWhenAddEntitiesThenValidate() throws Exception {
        //Arrange
        enterprisePrefFinal.setSearchesPerMonitor(1);

        //Act
        MonitorAPIResponse response = monitorService.addEntities
                (FR_USER_ID, MONITOR_ID, Collections.singletonList("12: 12"), enterprisePrefFinal);

        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.MONITOR_SEARCH_LIMIT_EXCEEDED));
    }

    @Test
    public void givenEntityMapWhenAddEntitiesThenValidate() throws Exception {
        //Arrange
        List<List<String>> categoryList = new ArrayList<>();
        categoryList.add(Collections.singletonList(SEARCH_TOKEN));
        categoryList.add(Collections.singletonList(TOKEN_ENTRY_SECTOR));
        categoryList.add(Collections.singletonList(SEARCH_TOKEN));
        doReturn(categoryList).when(monitorService, "categorizeEntities", anyList());
        initCategory();

        //Act
        MonitorAPIResponse response = monitorService.addEntities(FR_USER_ID, MONITOR_ID, Collections.singletonList("12: 12"),
                enterprisePrefFinal);

        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.REQUEST_SUCCESS));
        errorCollector.checkThat(response.getMonitorId(), is(MONITOR_ID));
        errorCollector.checkThat(response.getMonitorName(), is(TAG_NAME));
        errorCollector.checkThat(response.getEntities().size(), is(1));
        errorCollector.checkThat(response.getEntities().get(0).getAdded(), is(true));
        errorCollector.checkThat(response.getEntities().get(0).getName(), is(ENT_NAME));
        errorCollector.checkThat(response.getEntities().get(0).getSearchToken(), is(SEARCH_TOKEN));
    }

    @Test
    public void givenEntityMapEntitiesWhenAddEntitiesThenValidate() throws Exception {
        //Arrange
        List<List<String>> categoryList = new ArrayList<>();
        categoryList.add(Collections.singletonList(SEARCH_TOKEN));
        categoryList.add(Collections.singletonList(SEARCH_TOKEN));
        categoryList.add(Collections.singletonList(SEARCH_TOKEN));
        doReturn(categoryList).when(monitorService, "categorizeEntities", anyList());
        initCategory();

        //Act
        MonitorAPIResponse response = monitorService.addEntities(FR_USER_ID, MONITOR_ID, Collections.singletonList("12: 12"),
                enterprisePrefFinal);

        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.REQUEST_SUCCESS));
        errorCollector.checkThat(response.getMonitorId(), is(MONITOR_ID));
        errorCollector.checkThat(response.getMonitorName(), is(TAG_NAME));
        errorCollector.checkThat(response.getEntities().size(), is(1));
        errorCollector.checkThat(response.getEntities().get(0).getAdded(), equalTo(null));
    }

    @Test
    public void givenEntityMapValiditiesWhenAddEntitiesThenValidate() throws Exception {
        //Arrange
        List<List<String>> categoryList = new ArrayList<>();
        categoryList.add(Collections.singletonList(SEARCH_TOKEN));
        categoryList.add(Collections.singletonList(TOKEN_ENTRY_SECTOR));
        categoryList.add(Collections.singletonList(SEARCH_TOKEN));
        doReturn(categoryList).when(monitorService, "categorizeEntities", anyList());
        initCategory();
        doReturn(null).when(monitorServiceRepository).validateTokensUsingEntityCache(anyList(), anyList());

        //Act
        MonitorAPIResponse response = monitorService.addEntities(FR_USER_ID, MONITOR_ID, Collections.singletonList("12: 12"),
                enterprisePrefFinal);

        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.REQUEST_SUCCESS));
        errorCollector.checkThat(response.getMonitorId(), is(MONITOR_ID));
        errorCollector.checkThat(response.getMonitorName(), is(TAG_NAME));
        errorCollector.checkThat(response.getEntities().size(), is(1));
        errorCollector.checkThat(response.getEntities().get(0).getAdded(), is(true));
        errorCollector.checkThat(response.getEntities().get(0).getName(), is(SEARCH_TOKEN));
    }

    @Test
    public void ivenValueWhenAddFiltersThenThrowsException() throws Exception {
        //Arrange
        doThrow(new Exception()).when(servicesAPIUtil).addFilters(anyList(), any(MonitorWizardFilters.class),
                anyList());
        try {
            //Act
            monitorService.addFilters(FR_USER_ID, MONITOR_ID, Arrays.asList("-12 :12", "-12: 13"));
            fail(EXCEPTION);
        } catch (Exception ex) {
            verifyStatic();
            PerfMonitor.recordStats(PARENT_GROUP_ID, PerfActivityType.ReqTime, "addFilters");
        }
    }

    @Test
    public void givenValueWhenAddFiltersThenValidate() throws Exception {
        //Arrange
        initAddFilters();

        //Act
        MonitorAPIResponse response = monitorService.addFilters(FR_USER_ID, MONITOR_ID, Arrays.asList("-12 :12", "-12: 13"));

        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.REQUEST_SUCCESS));
        errorCollector.checkThat(response.getMonitorName(), is(TAG_NAME));
        verify(monitorServiceRepository).updateTagFiltersAndSearches(eq(12L), tagsArgumentCaptor.capture(),
                eq((String) null), monitorWizardFiltersArgumentCaptor.capture()
                , monitorWizardFiltersArgumentCaptor.capture());
        errorCollector.checkThat(tagsArgumentCaptor.getValue().getOwnedBy(), equalTo(12L));
        errorCollector.checkThat(monitorWizardFiltersArgumentCaptor.getAllValues().size(), equalTo(2));
        errorCollector.checkThat(monitorWizardFiltersArgumentCaptor.getAllValues().get(0).advanced.keywords
                , equalTo("keyword1,keyword2 -12 :12 -12: 13"));
        errorCollector.checkThat(monitorWizardFiltersArgumentCaptor.getAllValues().get(1).advanced.keywords
                , equalTo("keyword1,keyword2 -12 :12 -12: 13"));
    }
}
