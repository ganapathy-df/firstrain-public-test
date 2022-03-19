package com.firstrain.web.controller.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.firstrain.utils.FR_MailUtils;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.codehaus.jackson.JsonParseException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.springframework.context.support.ResourceBundleMessageSource;
import com.firstrain.db.obj.BaseItem.FLAGS;
import com.firstrain.db.obj.BaseItem.OwnedByType;
import com.firstrain.db.obj.Tags.SearchOrderType;
import com.firstrain.db.obj.UserGroupMap;
import com.firstrain.db.obj.UserGroupMap.MembershipType;
import com.firstrain.db.obj.Users;
import com.firstrain.db.obj.Users.Origin;
import com.firstrain.frapi.domain.MonitorDetails;
import com.firstrain.frapi.domain.User;
import com.firstrain.frapi.pojo.MonitorAPIResponse;
import com.firstrain.frapi.pojo.UserAPIResponse;
import com.firstrain.frapi.repository.UserServiceRepository;
import com.firstrain.frapi.service.MonitorService;
import com.firstrain.frapi.service.UserService;
import com.firstrain.frapi.service.impl.UserServiceImpl;
import com.firstrain.frapi.util.ConvertUtil;
import com.firstrain.frapi.util.DefaultEnums;
import com.firstrain.frapi.util.ServicesAPIUtil;
import com.firstrain.web.response.JSONResponse;
import com.firstrain.web.response.MonitorDetailsResponse;
import com.firstrain.web.response.UserIdWrapperResponse;
import com.firstrain.web.response.UserWrapperResponse;
import com.firstrain.web.service.core.RequestParsingService;
import com.firstrain.web.service.core.ResponseDecoratorService;
import com.firstrain.web.util.UserInfoThreadLocal;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        FR_MailUtils.class
})
public class UserControllerTest {

    private static final String USER_EMAIL = "user.name@valid.com";
    private static final String INVALID_USER_EMAIL = "invalid email";
    private static final String INVALID_BODY = "invalid body";
    private static final String REQ_BODY = "{\"uEmail\":\"" + USER_EMAIL + "\"}";
    private static final String USER_ID = "U:113";
    private static final String INVALID_USER_ID = "U:L";
    private static final long USET_ID_LONG = 2L;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private UserServiceRepository userServiceRepository;
    @Mock
    private ResourceBundleMessageSource messageSource;
    @Mock
    private RequestParsingService requestParsingService;
    @Mock
    private ResponseDecoratorService responseDecoratorService;
    @Mock
    private MonitorService monitorService;
    @Mock
    private PoolingHttpClientConnectionManager connectionManager;

    @InjectMocks
    @Spy
    private UserService userService = new UserServiceImpl();
    private ConvertUtil convertUtil = new ConvertUtil();
    private ServicesAPIUtil servicesAPIUtil = new ServicesAPIUtil();
    private UserController controller = new UserController();

    private Users users = new Users();

    @Before
    public void setUp() throws Exception {
        PowerMockito.whenNew(PoolingHttpClientConnectionManager.class)
                .withAnyArguments().thenReturn(connectionManager);
        mockStatic(FR_MailUtils.class);
        Mockito.when(FR_MailUtils.validateEmail(anyString())).thenReturn(true);

        User user = new User();
        user.setUserId(String.valueOf(USET_ID_LONG));
        user.setOwnedBy(USET_ID_LONG);
        user.setFlags(DefaultEnums.Status.ACTIVE.name());
        user.setMembershipType(com.firstrain.frapi.util.DefaultEnums.MembershipType.ADMIN);
        UserInfoThreadLocal.set(user);
        Whitebox.setInternalState(userService, "convertUtil", convertUtil);
        Whitebox.setInternalState(userService, "servicesAPIUtil", servicesAPIUtil);
        Whitebox.setInternalState(controller, "userService", userService);
        Whitebox.setInternalState(controller, "responseDecoratorService", responseDecoratorService);
        Whitebox.setInternalState(controller, "requestParsingService", requestParsingService);
        Whitebox.setInternalState(controller, "monitorService", monitorService);
        users.setFlags(FLAGS.ACTIVE);
        users.setMonitorOrderType(SearchOrderType.CUSTOM);
        users.setOwnedByType(OwnedByType.GROUP);
        users.setType(Users.Type.ABU);
        users.setOrigin(Origin.ABU);
        users.setId(USET_ID_LONG);
        users.setOwnedBy(USET_ID_LONG);
        List<UserGroupMap> groupMaps = new ArrayList<UserGroupMap>();
        UserGroupMap userGroupMap = new UserGroupMap();
        userGroupMap.setMembershipType(MembershipType.ADMIN);
        groupMaps.add(userGroupMap);
        when(userServiceRepository.getUserGroupMapByUserId(USET_ID_LONG)).thenReturn(groupMaps);
        when(userServiceRepository.getUserByEmailAndEnterpriseId(anyString(), anyLong())).thenReturn(users);
        when(userServiceRepository.getUserById(anyLong())).thenReturn(users);
    }

    @Test
    public void givenNullFromDecoratorServiceWhenGetThenGetNull() throws Exception {
        // Act
        JSONResponse actual = controller.get(request, response, USER_EMAIL);
        // Assert
        assertNull(actual);
    }

    @Test
    public void givenGetWhenNullUserResponseFromUserServiceThenGetErrorResponse() throws Exception {
        // Arrange
        JSONResponse expected = new JSONResponse();
        when(requestParsingService.getErrorResponse(anyInt())).thenReturn(expected);
        when(userServiceRepository.getUserById(anyLong())).thenReturn(null);
        // Act
        JSONResponse actual = controller.get(request, response, USER_ID);
        // Assert
        assertEquals(expected, actual);
        verify(requestParsingService).getErrorResponse(anyInt());
    }

    @Test
    public void givenAddWhenNullRequestBodyThenGetError() {
        // Arrange
        JSONResponse expected = new JSONResponse();
        when(requestParsingService.getErrorResponse(anyInt())).thenReturn(expected);
        // Act
        JSONResponse actual = controller.add(request, response, null);
        // Assert
        assertEquals(expected, actual);
        verify(requestParsingService).getErrorResponse(anyInt());
    }

    @Test
    public void givenAddWhenInvalidRequestBodyThenGetError() {
        // Arrange
        JSONResponse expected = new JSONResponse();
        when(requestParsingService.getErrorResponse(anyInt())).thenReturn(expected);
        // Act
        JSONResponse actual = controller.add(request, response, INVALID_BODY);
        // Assert
        assertEquals(expected, actual);
        verify(requestParsingService).getErrorResponse(anyInt());
    }

    @Test
    public void givenAddWhenValidRequestBodyAndGetNullFromParsingServiceThenGetError()
            throws JsonParseException, IOException {
        // Arrange
        JSONResponse expected = new JSONResponse();
        when(requestParsingService.getErrorResponse(anyInt())).thenReturn(expected);
        // Act
        JSONResponse actual = controller.add(request, response, REQ_BODY);
        // Assert
        assertEquals(expected, actual);
        verify(requestParsingService).getErrorResponse(anyInt());
    }

    @Test
    public void givenAddWhenInvalidEmailFromParsingServiceThenGetError() throws JsonParseException, IOException {
        // Arrange
        JSONResponse expected = new JSONResponse();
        when(requestParsingService.getErrorResponse(anyInt())).thenReturn(expected);
        when(requestParsingService.getRefinedReqVal(USER_EMAIL)).thenReturn(INVALID_USER_EMAIL);
        // Act
        JSONResponse actual = controller.add(request, response, REQ_BODY);
        // Assert
        assertEquals(expected, actual);
        verify(requestParsingService).getErrorResponse(anyInt());
    }

    @Test
    public void givenAddWhenUserAddedThenGetUserResponse() throws JsonParseException, IOException {
        // Arrange
        UserWrapperResponse expected = new UserWrapperResponse();
        when(responseDecoratorService.getUserResponse(any(UserAPIResponse.class), anyString())).thenReturn(expected);
        when(requestParsingService.getRefinedReqVal(USER_EMAIL)).thenReturn(USER_EMAIL);
        when(userServiceRepository.getUserByEmailAndEnterpriseId(anyString(), anyLong())).thenReturn(null);
        List<Users> usersList = new ArrayList<Users>();
        usersList.add(users);
        when(userServiceRepository.getUsersByGrpIdAndMembershipType(anyLong(), any(MembershipType.class)))
                .thenReturn(usersList);
        // Act
        JSONResponse actual = controller.add(request, response, REQ_BODY);
        // Assert
        assertEquals(expected, actual);
        verify(responseDecoratorService).getUserResponse(any(UserAPIResponse.class), anyString());
    }

    @Test
    public void givenUpdateWhenUnParseableUserIdThenError() {
        // Arrange
        JSONResponse expected = new JSONResponse();
        when(requestParsingService.getErrorResponse(anyInt())).thenReturn(expected);
        when(requestParsingService.getRefinedReqVal(USER_EMAIL)).thenReturn(INVALID_USER_EMAIL);
        // Act
        JSONResponse actual = controller.update(request, response, INVALID_BODY, INVALID_USER_ID);
        // Assert
        assertEquals(expected, actual);
        verify(requestParsingService).getErrorResponse(anyInt());
    }

    @Test
    public void givenUpdateWhenValidUserIdAndNullBodyThenError() {
        // Arrange
        JSONResponse expected = new JSONResponse();
        when(requestParsingService.getErrorResponse(anyInt())).thenReturn(expected);
        when(requestParsingService.getRefinedReqVal(USER_EMAIL)).thenReturn(INVALID_USER_EMAIL);
        // Act
        JSONResponse actual = controller.update(request, response, null, USER_ID);
        // Assert
        assertEquals(expected, actual);
        verify(requestParsingService).getErrorResponse(anyInt());
    }

    @Test
    public void givenUpdateWhenInvalidEmailFromParsingServiceThenGetError() throws JsonParseException, IOException {
        // Arrange
        JSONResponse expected = new JSONResponse();
        when(requestParsingService.getErrorResponse(anyInt())).thenReturn(expected);
        when(requestParsingService.getRefinedReqVal(USER_EMAIL)).thenReturn(USER_EMAIL);
        when(requestParsingService.getRefinedReqVal("status")).thenReturn(DefaultEnums.Status.DELETED.name());
        String requestBody = "{\"uEmail\":\"" + USER_EMAIL + "\",\"uStatus\":" + "\"status\"}";
        // Act
        JSONResponse actual = controller.update(request, response, requestBody, USER_ID);
        // Assert
        assertEquals(expected, actual);
        verify(requestParsingService).getErrorResponse(anyInt());
    }

    @Test
    public void givenUpdateWhenNullUserFromParsingServiceThenGetError() throws Exception {
        // Arrange
        JSONResponse expected = new JSONResponse();
        when(requestParsingService.getErrorResponse(anyInt())).thenReturn(expected);
        when(requestParsingService.getRefinedReqVal(USER_EMAIL)).thenReturn(USER_EMAIL);
        when(requestParsingService.getRefinedReqVal("status")).thenReturn(DefaultEnums.Status.ACTIVE.name());
        when(userServiceRepository.getUserById(anyLong())).thenReturn(null);
        String requestBody = "{\"uEmail\":\"" + USER_EMAIL + "\",\"uStatus\":" + "\"status\"}";
        // Act
        JSONResponse actual = controller.update(request, response, requestBody, USER_ID);
        // Assert
        assertEquals(expected, actual);
        verify(requestParsingService).getErrorResponse(anyInt());
    }

    @Test
    public void givenUpdateWhenUserFromServiceThenGetError() throws Exception {
        // Arrange
        when(requestParsingService.getRefinedReqVal(USER_EMAIL)).thenReturn(USER_EMAIL);
        when(requestParsingService.getRefinedReqVal("status")).thenReturn(DefaultEnums.Status.ACTIVE.name());
        when(userServiceRepository.getUserByEmailAndEnterpriseId(anyString(), anyLong())).thenReturn(null);
        String requestBody = "{\"uEmail\":\"" + USER_EMAIL + "\",\"uStatus\":" + "\"status\"}";
        UserWrapperResponse expected = new UserWrapperResponse();
        when(responseDecoratorService.getUserResponse(any(UserAPIResponse.class), anyString())).thenReturn(expected);
        // Act
        JSONResponse actual = controller.update(request, response, requestBody, USER_ID);
        // Assert
        assertEquals(expected, actual);
        verify(responseDecoratorService).getUserResponse(any(UserAPIResponse.class), anyString());
    }

    @Test
    public void givenGetIdWhenGetUserThenGetUserIdResponse() {
        // Arrange
        UserIdWrapperResponse expected = new UserIdWrapperResponse();
        when(responseDecoratorService.getUserIdResponse(any(UserAPIResponse.class), anyString())).thenReturn(expected);
        // Act
        JSONResponse actual = controller.getId(request, response, USER_EMAIL);
        // Assert
        assertEquals(expected, actual);
        verify(responseDecoratorService).getUserIdResponse(any(UserAPIResponse.class), anyString());
    }

    @Test
    public void givenDeleteWhenNullRequestBodyThenGetError() {
        // Arrange
        JSONResponse expected = new JSONResponse();
        when(requestParsingService.getErrorResponse(anyInt())).thenReturn(expected);
        // Act
        JSONResponse actual = controller.delete(request, response, null);
        // Assert
        assertEquals(expected, actual);
        verify(requestParsingService).getErrorResponse(anyInt());
    }

    @Test
    public void givenDeleteWhenInvalidRequestBodyThenGetError() {
        // Arrange
        JSONResponse expected = new JSONResponse();
        when(requestParsingService.getErrorResponse(anyInt())).thenReturn(expected);
        // Act
        JSONResponse actual = controller.delete(request, response, INVALID_BODY);
        // Assert
        assertEquals(expected, actual);
        verify(requestParsingService).getErrorResponse(anyInt());
    }

    @Test
    public void givenDeleteWhenInvalidActorUserThenGetError() {
        // Arrange
        JSONResponse expected = new JSONResponse();
        when(requestParsingService.getErrorResponse(anyInt())).thenReturn(expected);

        when(requestParsingService.getRefinedReqVal(USER_EMAIL)).thenReturn(USER_EMAIL);
        String requestBody = "{\"uEmail\":\"" + USER_EMAIL + "\",\"uId\":" + "\"uId\"}";
        // Act
        JSONResponse actual = controller.delete(request, response, requestBody);
        // Assert
        assertEquals(expected, actual);
        verify(requestParsingService).getErrorResponse(anyInt());
    }

    @Test
    public void givenDeleteWhenInvalidUserIdNotParseableThenError() {
        // Arrange
        JSONResponse expected = new JSONResponse();
        when(requestParsingService.getErrorResponse(anyInt())).thenReturn(expected);

        when(requestParsingService.getRefinedReqVal(USER_EMAIL)).thenReturn(null);
        when(requestParsingService.getRefinedReqVal("uId")).thenReturn(INVALID_USER_ID);
        String requestBody = "{\"uEmail\":\"" + USER_EMAIL + "\",\"uId\":" + "\"uId\"}";
        // Act
        JSONResponse actual = controller.delete(request, response, requestBody);
        // Assert
        assertEquals(expected, actual);
        verify(requestParsingService).getErrorResponse(anyInt());
    }

    @Test
    public void givenDeleteWhenValidUserIdThenDeleteAndGetDeleteReponse() {
        // Arrange
        JSONResponse expected = new JSONResponse();
        when(responseDecoratorService.getDeleteUserResponse(anyString())).thenReturn(expected);
        when(requestParsingService.getErrorResponse(anyInt())).thenReturn(expected);
        when(requestParsingService.getRefinedReqVal(USER_EMAIL)).thenReturn(null);
        when(requestParsingService.getRefinedReqVal("uId")).thenReturn(USER_ID);
        String requestBody = "{\"uEmail\":\"" + USER_EMAIL + "\",\"uId\":" + "\"uId\"}";
        // Act
        JSONResponse actual = controller.delete(request, response, requestBody);
        // Assert
        assertEquals(expected, actual);
        verify(responseDecoratorService).getDeleteUserResponse(anyString());
    }

    @Test
    public void givenMonitorDetailsWhenInvalidIdThenError() {
        // Arrange
        JSONResponse expected = new JSONResponse();
        when(requestParsingService.getErrorResponse(anyInt())).thenReturn(expected);
        // Act
        JSONResponse actual = controller.monitorDetails(request, response, INVALID_USER_ID);
        // Assert
        assertEquals(expected, actual);
        verify(requestParsingService).getErrorResponse(anyInt());
    }

    @Test
    public void givenMonitorDetailsWhenMonitorServiceResponseIsNotSuccessThenError() throws Exception {
        // Arrange
        JSONResponse expected = new JSONResponse();
        when(requestParsingService.getErrorResponse(anyInt())).thenReturn(expected);
        MonitorAPIResponse apiRes = new MonitorAPIResponse();
        apiRes.setStatusCode(0);
        when(monitorService.getMonitorListByOwner(any(User.class), any(User.class), anyString())).thenReturn(apiRes);
        // Act
        JSONResponse actual = controller.monitorDetails(request, response, USER_ID);
        // Assert
        assertEquals(expected, actual);
        verify(requestParsingService).getErrorResponse(anyInt());
    }

    @Test
    public void givenMonitorDetailsWhenMonitorDetailsAreNullThenError() throws Exception {
        // Arrange
        JSONResponse expected = new JSONResponse();
        when(requestParsingService.getErrorResponse(anyInt())).thenReturn(expected);
        MonitorAPIResponse apiRes = new MonitorAPIResponse();
        apiRes.setStatusCode(200);
        when(monitorService.getMonitorListByOwner(any(User.class), any(User.class), anyString())).thenReturn(apiRes);
        // Act
        JSONResponse actual = controller.monitorDetails(request, response, USER_ID);
        // Assert
        assertEquals(expected, actual);
        verify(requestParsingService).getErrorResponse(anyInt());
    }

    @Test
    public void givenMonitorDetailsWhenMonitorDetailsThenGetMonitorResponse() throws Exception {
        // Arrange
        MonitorDetailsResponse expected = new MonitorDetailsResponse();
        when(responseDecoratorService.getMonitorDetailsResponse(any(MonitorDetails.class))).thenReturn(expected);
        MonitorAPIResponse apiRes = new MonitorAPIResponse();
        apiRes.setStatusCode(200);
        apiRes.setMonitorDetails(new MonitorDetails());
        when(monitorService.getMonitorListByOwner(any(User.class), any(User.class), anyString())).thenReturn(apiRes);
        // Act
        JSONResponse actual = controller.monitorDetails(request, response, USER_ID);
        // Assert
        assertEquals(expected, actual);
        verify(responseDecoratorService).getMonitorDetailsResponse(any(MonitorDetails.class));
    }
}
