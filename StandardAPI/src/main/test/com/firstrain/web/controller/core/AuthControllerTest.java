package com.firstrain.web.controller.core;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.firstrain.common.db.jpa.PersistenceProvider;
import com.firstrain.common.db.jpa.Transaction;
import com.firstrain.db.api.FRAPIAuthDbAPI;
import com.firstrain.db.obj.APIAccount;
import com.firstrain.db.obj.APIAuthKey;
import com.firstrain.frapi.pojo.AuthAPIResponse;
import com.firstrain.frapi.repository.impl.AuthServiceRepositoryImpl;
import com.firstrain.frapi.service.AuthService;
import com.firstrain.frapi.service.impl.AuthServiceImpl;
import com.firstrain.frapi.util.StatusCode;
import com.firstrain.utils.JSONUtility;
import com.firstrain.web.response.AuthKeyResponse;
import com.firstrain.web.response.JSONResponse;
import com.firstrain.web.service.core.AuthKeyCacheManager;
import com.firstrain.web.service.core.Constant;
import com.firstrain.web.service.core.RequestParsingService;
import com.firstrain.web.service.core.ResponseDecoratorService;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
	Constant.class,
	FRAPIAuthDbAPI.class,
	PersistenceProvider.class
})
public class AuthControllerTest {

	private static final String AUTH_PASSWORD = "authPassword";
	private static final String AUTH_NAME = "authName";
	private static final String ENCRYPTED_PASSWD = "wtgrKBCpdcBp4R+7XaMy6VMywBhugVuT2+OEB8w46bg=";
	private static final String AUTH_KEY = "auth Key";
	private static final String SALT_KEY = "saltKey";
	private static final String REQ_BODY_EMPTY_MAP = "{}";
	private static final String EMPTY_REQ_BODY = "";
	private static final String INCORRECT_REQ_BODY = "Incorrect";

	private final JSONResponse jsonResponse = new JSONResponse();
	private final AuthKeyResponse authKeyResponse = new AuthKeyResponse();
	@Mock
	private HttpServletRequest request;
	@Mock
	private HttpServletResponse response;
	@Mock
	private RequestParsingService requestParsingService;
	@Mock
	private Transaction txn;
	@Mock
	private ResponseDecoratorService responseDecoratorService;
	@Mock
	private AuthKeyCacheManager authKeyCacheManager;
	@InjectMocks
	private AuthController controller;

	private AuthService authService = new AuthServiceImpl();
	private AuthServiceRepositoryImpl apiAuthRepository = new AuthServiceRepositoryImpl();

	@Rule
	public final ErrorCollector collector = new ErrorCollector();

	@Captor
	private ArgumentCaptor<Integer> integerArgumentCaptor;

	@Before
	public void setUp() throws Exception {
		PowerMockito.mockStatic(FRAPIAuthDbAPI.class);
		PowerMockito.mockStatic(Constant.class);
		PowerMockito.mockStatic(PersistenceProvider.class);
		APIAccount apiAccount = new APIAccount();
		apiAccount.setSalt(SALT_KEY);
		apiAccount.setAuthPassword(ENCRYPTED_PASSWD);
		when(FRAPIAuthDbAPI.getAccountDetails(anyString(), anyString())).thenReturn(apiAccount);
		when(PersistenceProvider.newTxn(PersistenceProvider.EMAIL_DATABASE)).thenReturn(txn);
		when(requestParsingService.getErrorResponse(anyInt())).thenReturn(jsonResponse);
		when(requestParsingService.getRefinedReqVal(AUTH_NAME)).thenReturn("userName");
		when(requestParsingService.getRefinedReqVal(AUTH_PASSWORD)).thenReturn(AUTH_PASSWORD);
		when(responseDecoratorService.getAuthKeyResponse(any(AuthAPIResponse.class))).thenReturn(authKeyResponse);
		Whitebox.setInternalState(controller, "authService", authService);
		apiAuthRepository.init();
		Whitebox.setInternalState(authService, "apiAuthRepository", apiAuthRepository);
	}

	@Test
	public void givenEmptyRequestBodyWhenGenerateAuthKeyThenErrorResponse() {
		// Act
		controller.generateAuthKey(request, response, EMPTY_REQ_BODY);
		// Assert
		verify(requestParsingService).getErrorResponse(integerArgumentCaptor.capture());
		collector.checkThat(integerArgumentCaptor.getValue(), is(StatusCode.INSUFFICIENT_ARGUMENT));
	}

	@Test
	public void givenIncorrectRequestBodyWhenGenerateAuthKeyThenErrorResponse() {
		// Act
		controller.generateAuthKey(request, response, INCORRECT_REQ_BODY);
		// Assert
		verify(requestParsingService).getErrorResponse(integerArgumentCaptor.capture());
		collector.checkThat(integerArgumentCaptor.getValue(), is(StatusCode.ILLEGAL_ARGUMENT));
	}

	@Test
	public void givenEmptyMapRequestBodyWhenGenerateAuthKeyThenErrorResponse() {
		// Act
		controller.generateAuthKey(request, response, REQ_BODY_EMPTY_MAP);
		// Assert
		verify(requestParsingService).getErrorResponse(integerArgumentCaptor.capture());
		collector.checkThat(integerArgumentCaptor.getValue(), is(StatusCode.INSUFFICIENT_ARGUMENT));
	}

	@Test
	public void givenMapRequestBodyWhenGenerateAuthKeyThenResponse() throws Exception {
		// Arrange
		Map<String, String> requestBody = new HashMap<String, String>();
		requestBody.put(AUTH_NAME, AUTH_NAME);
		requestBody.put(AUTH_PASSWORD, AUTH_PASSWORD);
		// Act
		JSONResponse actual = controller.generateAuthKey(request, response, JSONUtility.serialize(requestBody));
		// Assert
		verify(requestParsingService, never()).getErrorResponse(anyInt());
		assertEquals(authKeyResponse, actual);
	}

	@Test
	public void givenAuthKeyWhenGetAuthKeyThenResponseApiAuthKey() {
		// Arrange
		APIAuthKey apiAuthKey = new APIAuthKey();
		apiAuthKey.setAccountId(1);
		apiAuthKey.setExpiryTime(new Timestamp(System.currentTimeMillis()));
		when(FRAPIAuthDbAPI.getAuthKeyDetails(AUTH_KEY)).thenReturn(apiAuthKey);
		APIAccount apiAccount = new APIAccount();
		when(FRAPIAuthDbAPI.getAccountById(any(Transaction.class), anyInt())).thenReturn(apiAccount);
		// Act
		JSONResponse actual = controller.getAuthKey(request, response, AUTH_KEY);
		// Assert
		verify(requestParsingService, never()).getErrorResponse(anyInt());
		assertEquals(authKeyResponse, actual);
	}

	@Test
	public void givenEmptyRequestBodyWhenGetAuthKeyThenErrorResponse() {
		// Act
		controller.getAuthKey(request, response, EMPTY_REQ_BODY);
		// Assert
		verify(requestParsingService).getErrorResponse(integerArgumentCaptor.capture());
		collector.checkThat(integerArgumentCaptor.getValue(), is(StatusCode.INSUFFICIENT_ARGUMENT));
	}
}
