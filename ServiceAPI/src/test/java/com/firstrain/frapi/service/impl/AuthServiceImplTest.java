package com.firstrain.frapi.service.impl;

import com.firstrain.common.db.jpa.PersistenceProvider;
import com.firstrain.common.db.jpa.Transaction;
import com.firstrain.db.api.FRAPIAuthDbAPI;
import com.firstrain.db.obj.APIAccount;
import com.firstrain.db.obj.APIAuthKey;
import com.firstrain.db.obj.APIDefinition;
import com.firstrain.frapi.pojo.APIDefinitionPojo;
import com.firstrain.frapi.pojo.AuthAPIResponse;
import com.firstrain.frapi.repository.impl.AuthServiceRepositoryImpl;
import com.firstrain.frapi.util.StatusCode;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.reflect.Whitebox.setInternalState;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        AuthServiceImpl.class,
        FRAPIAuthDbAPI.class,
        AuthServiceRepositoryImpl.class,
        PersistenceProvider.class

})
public class AuthServiceImplTest {

    private static final String AUTH_SERVICE_NAME = "service";
    private static final String API_VERSION = "1.2.3";
    private static final String AUTH_PASSWORD = "abc";
    private static final String API_SALT = "salt";
    private static final String PREF_JSON = "json";
    private static final long ENTERPRISE_ID = 123L;
    private static final int API_THRE_SOLD = 4563;
    private static final String AUTH_KEY = "auth";
    private static final long ACC_ID = 97865432L;
    private static final String CSV_VALUE = "1,2,3,4,5";
    private static final String API_DESC = "apiDesc";
    private static final String API_SIGNATURE = "apiSignature";
    private static final long API_ID = 44L;
    @InjectMocks
    private final AuthServiceImpl authService = new AuthServiceImpl();
    @Spy
    private final AuthServiceRepositoryImpl apiAuthRepository = new AuthServiceRepositoryImpl();
    @Mock
    private Transaction txn;
    private ErrorCollector errorCollector;
    private ExpectedException expectedException;
    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector = new ErrorCollector())
            .around(expectedException = ExpectedException.none());

    @Before
    public void setUp() throws Exception {
        mockStatic(FRAPIAuthDbAPI.class, PersistenceProvider.class);
        when(PersistenceProvider.newTxn(anyString())).thenReturn(txn);
        APIAccount account = createApiAccount();
        when(FRAPIAuthDbAPI.getAccountDetails(anyString(), anyString()))
                .thenReturn(account);
        setInternalState(apiAuthRepository, "sr", new SecureRandom());
    }

    @Test
    public void generateAuthKey() throws Exception {
        //Arrange
        doReturn(AUTH_PASSWORD).when(apiAuthRepository, "getEncryptedPassword", AUTH_PASSWORD, API_SALT);
        //Act
        AuthAPIResponse response = authService.generateAuthKey(AUTH_SERVICE_NAME, AUTH_PASSWORD, API_VERSION);
        //Assert
        errorCollector.checkThat(response.getEnterpriseId(), is(ENTERPRISE_ID));
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.REQUEST_SUCCESS));
        errorCollector.checkThat(response.getPrefJson(), is(PREF_JSON));
    }

    @Test
    public void givenGenerateAuthKeyThenThrowsException() throws Exception {
        //Arrange
        doThrow(new RuntimeException("MESSAGE")).when(apiAuthRepository).getAccount(anyString(),
                anyString(), anyString());
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("MESSAGE");
        //Act
        authService.generateAuthKey(AUTH_SERVICE_NAME, AUTH_PASSWORD, API_VERSION);
    }

    @Test
    public void givenGetAuthKeyDetailsWhenApiKeyIsNullThenResponse() throws Exception {
        //Arrange
        doReturn(null).when(apiAuthRepository).getAuthKeyDetails(anyString());
        //Act
        AuthAPIResponse response = authService.getAuthKeyDetails(AUTH_KEY);
        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.INVALID_AUTHKEY));
    }

    @Test
    public void givenGetAuthKeyDetailsThenThrowsException() throws Exception {
        //Arrange
        doThrow(new RuntimeException("MESSAGE")).when(apiAuthRepository).getAuthKeyDetails(anyString());
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("MESSAGE");
        //Act
        authService.getAuthKeyDetails(AUTH_KEY);
    }

    @Test
    public void getAuthKeyDetails() throws Exception {
        //Arrange
        APIAccount account = createApiAccount();
        doReturn(AUTH_PASSWORD).when(apiAuthRepository, "getEncryptedPassword", AUTH_PASSWORD, API_SALT);
        APIAuthKey apiAuthKey = createApiKey();
        when(FRAPIAuthDbAPI.getAuthKeyDetails(anyString()))
                .thenReturn(apiAuthKey);
        when(FRAPIAuthDbAPI.getAccountById(any(Transaction.class), anyLong()))
                .thenReturn(account);
        //Act
        AuthAPIResponse response = authService.getAuthKeyDetails(AUTH_KEY);
        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.EXPIRED_AUTHKEY));
        errorCollector.checkThat(response.getPrefJson(), is(PREF_JSON));
        errorCollector.checkThat(response.getEnterpriseId(), is(ENTERPRISE_ID));
        errorCollector.checkThat(response.getAuthKey(), is(AUTH_KEY));
    }

    private APIAuthKey createApiKey() {
        APIAuthKey apiAuthKey = new APIAuthKey();
        apiAuthKey.setAccountId(ACC_ID);
        apiAuthKey.setAuthKey(AUTH_KEY);
        apiAuthKey.setExpiryTime(new Timestamp(324252));
        return apiAuthKey;
    }

    @Test
    public void csvToArrayList() {
        //Act
        ArrayList<String> items = authService.csvToArrayList(CSV_VALUE);
        //Assert
        errorCollector.checkThat(items.size(), is(5));
    }

    @Test
    public void givenGetAPIDefinitionsThenThrowsException() throws Exception {
        //Arrange
        doThrow(new RuntimeException("MESSAGE")).when(apiAuthRepository).getAPIDefinitionList();
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("MESSAGE");
        //Act
        authService.getAPIDefinitions();
    }

    @Test
    public void getAPIDefinitions() throws Exception {
        //Arrange
        APIDefinition definition = createApiDefinition();
        when(FRAPIAuthDbAPI.getAPIDefinitionList())
                .thenReturn(Collections.singletonList(definition));
        //Act
        List<APIDefinitionPojo> pojoList = authService.getAPIDefinitions();
        APIDefinitionPojo definitionPojo = pojoList.get(0);
        //Assert
        errorCollector.checkThat(definitionPojo.getApiVersion(), is(API_VERSION));
        errorCollector.checkThat(definitionPojo.getApiSignature(), is(API_SIGNATURE));
        errorCollector.checkThat(definitionPojo.getId(), is(API_ID));
    }

    private static APIAccount createApiAccount() {
        APIAccount account = spy(new APIAccount());
        account.setApiVersion(API_VERSION);
        account.setApiThreshold(API_THRE_SOLD);
        account.setEnterpriseId(ENTERPRISE_ID);
        account.setPrefJson(PREF_JSON);
        account.setSalt(API_SALT);
        account.setAuthPassword(AUTH_PASSWORD);
        return account;
    }

    private static APIDefinition createApiDefinition() {
        APIDefinition apiDefinition = new APIDefinition();
        apiDefinition.setApiSignature(API_SIGNATURE);
        apiDefinition.setApiVersion(API_VERSION);
        apiDefinition.setApiDesc(API_DESC);
        apiDefinition.setId(API_ID);
        return apiDefinition;
    }
}
