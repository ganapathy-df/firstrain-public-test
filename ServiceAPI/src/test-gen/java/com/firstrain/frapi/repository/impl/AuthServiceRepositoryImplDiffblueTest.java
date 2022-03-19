package com.firstrain.frapi.repository.impl;

import static org.junit.Assert.assertNull;
import static org.mockito.AdditionalMatchers.or;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.isA;
import static org.mockito.Matchers.isNull;

import com.diffblue.deeptestutils.Reflector;
import com.diffblue.deeptestutils.mock.DTUMemberMatcher;
import com.firstrain.common.db.jpa.PersistenceProvider;
import com.firstrain.common.db.jpa.Transaction;
import com.firstrain.db.api.FRAPIAuthDbAPI;
import com.firstrain.db.obj.APIAccount;
import com.firstrain.db.obj.APIAuthKey;
import com.firstrain.db.obj.APIDefinition;
import java.lang.reflect.Method;
import java.security.SecureRandom;
import java.util.List;
import org.apache.log4j.Logger;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.management.*"})
public class AuthServiceRepositoryImplDiffblueTest {

  @Rule public final ExpectedException thrown = ExpectedException.none();
  @Rule public final Timeout globalTimeout = new Timeout(10_000);

  /* testedClasses: AuthServiceRepositoryImpl */
  // Test generated by Diffblue Deeptest.
  @PrepareForTest(Logger.class)
  @Test
  public void constructorOutputVoid() throws Exception {

    // Setup mocks
    PowerMockito.mockStatic(Logger.class);

    // Arrange
    final Method getLoggerMethod = DTUMemberMatcher.method(Logger.class, "getLogger", Class.class);
    PowerMockito.doReturn(null)
        .when(Logger.class, getLoggerMethod)
        .withArguments(or(isA(Class.class), isNull(Class.class)));

    // Act, creating object to test constructor
    final AuthServiceRepositoryImpl objectUnderTest = new AuthServiceRepositoryImpl();
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest({FRAPIAuthDbAPI.class, PersistenceProvider.class})
  @Test
  public void getAccountByIdInputZeroOutputNull() throws Exception {

    // Setup mocks
    PowerMockito.mockStatic(PersistenceProvider.class);
    PowerMockito.mockStatic(FRAPIAuthDbAPI.class);

    // Arrange
    final AuthServiceRepositoryImpl objectUnderTest = new AuthServiceRepositoryImpl();
    Reflector.setField(objectUnderTest, "sr", null);
    Reflector.setField(objectUnderTest, "LOG", null);
    Reflector.setField(objectUnderTest, "MULTI_HASH_COUNT", 0);
    final long accountId = 0L;
    final Method getAccountByIdMethod =
        DTUMemberMatcher.method(
            FRAPIAuthDbAPI.class, "getAccountById", Transaction.class, long.class);
    PowerMockito.doReturn(null)
        .when(FRAPIAuthDbAPI.class, getAccountByIdMethod)
        .withArguments(or(isA(Transaction.class), isNull(Transaction.class)), anyLong());
    final Method newTxnMethod =
        DTUMemberMatcher.method(PersistenceProvider.class, "newTxn", String.class);
    PowerMockito.doReturn(null)
        .when(PersistenceProvider.class, newTxnMethod)
        .withArguments(or(isA(String.class), isNull(String.class)));

    // Act
    final APIAccount retval = objectUnderTest.getAccountById(accountId);

    // Assert
    assertNull(retval);
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest({FRAPIAuthDbAPI.class, Transaction.class, PersistenceProvider.class})
  @Test
  public void getAccountByIdInputZeroOutputNull2() throws Exception {

    // Setup mocks
    PowerMockito.mockStatic(PersistenceProvider.class);
    PowerMockito.mockStatic(FRAPIAuthDbAPI.class);

    // Arrange
    final AuthServiceRepositoryImpl objectUnderTest = new AuthServiceRepositoryImpl();
    Reflector.setField(objectUnderTest, "sr", null);
    Reflector.setField(objectUnderTest, "LOG", null);
    Reflector.setField(objectUnderTest, "MULTI_HASH_COUNT", 0);
    final long accountId = 0L;
    final Method getAccountByIdMethod =
        DTUMemberMatcher.method(
            FRAPIAuthDbAPI.class, "getAccountById", Transaction.class, long.class);
    PowerMockito.doReturn(null)
        .when(FRAPIAuthDbAPI.class, getAccountByIdMethod)
        .withArguments(or(isA(Transaction.class), isNull(Transaction.class)), anyLong());
    final Transaction transaction = PowerMockito.mock(Transaction.class);
    final Method newTxnMethod =
        DTUMemberMatcher.method(PersistenceProvider.class, "newTxn", String.class);
    PowerMockito.doReturn(transaction)
        .when(PersistenceProvider.class, newTxnMethod)
        .withArguments(or(isA(String.class), isNull(String.class)));

    // Act
    final APIAccount retval = objectUnderTest.getAccountById(accountId);

    // Assert
    assertNull(retval);
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest(FRAPIAuthDbAPI.class)
  @Test
  public void getAccountInputNullNullNullOutputNull() throws Exception {

    // Setup mocks
    PowerMockito.mockStatic(FRAPIAuthDbAPI.class);

    // Arrange
    final AuthServiceRepositoryImpl objectUnderTest = new AuthServiceRepositoryImpl();
    final SecureRandom secureRandom =
        (SecureRandom) Reflector.getInstance("java.security.SecureRandom");
    Reflector.setField(objectUnderTest, "sr", secureRandom);
    Reflector.setField(objectUnderTest, "LOG", null);
    Reflector.setField(objectUnderTest, "MULTI_HASH_COUNT", 0);
    final String authName = null;
    final String authPassword = null;
    final String apiVersion = null;
    final Method getAccountDetailsMethod =
        DTUMemberMatcher.method(
            FRAPIAuthDbAPI.class, "getAccountDetails", String.class, String.class);
    PowerMockito.doReturn(null)
        .when(FRAPIAuthDbAPI.class, getAccountDetailsMethod)
        .withArguments(
            or(isA(String.class), isNull(String.class)),
            or(isA(String.class), isNull(String.class)));

    // Act
    final APIAccount retval = objectUnderTest.getAccount(authName, authPassword, apiVersion);

    // Assert
    assertNull(retval);
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest(FRAPIAuthDbAPI.class)
  @Test
  public void getAPIDefinitionListOutputNull() throws Exception {

    // Setup mocks
    PowerMockito.mockStatic(FRAPIAuthDbAPI.class);

    // Arrange
    final AuthServiceRepositoryImpl objectUnderTest = new AuthServiceRepositoryImpl();
    Reflector.setField(objectUnderTest, "sr", null);
    Reflector.setField(objectUnderTest, "LOG", null);
    Reflector.setField(objectUnderTest, "MULTI_HASH_COUNT", 0);
    PowerMockito.doReturn(null).when(FRAPIAuthDbAPI.class);
    FRAPIAuthDbAPI.getAPIDefinitionList();

    // Act
    final List<APIDefinition> retval = objectUnderTest.getAPIDefinitionList();

    // Assert
    assertNull(retval);
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest(FRAPIAuthDbAPI.class)
  @Test
  public void getAuthKeyDetailsInputNullOutputNull() throws Exception {

    // Setup mocks
    PowerMockito.mockStatic(FRAPIAuthDbAPI.class);

    // Arrange
    final AuthServiceRepositoryImpl objectUnderTest = new AuthServiceRepositoryImpl();
    Reflector.setField(objectUnderTest, "sr", null);
    Reflector.setField(objectUnderTest, "LOG", null);
    Reflector.setField(objectUnderTest, "MULTI_HASH_COUNT", 0);
    final String authKey = null;
    final Method getAuthKeyDetailsMethod =
        DTUMemberMatcher.method(FRAPIAuthDbAPI.class, "getAuthKeyDetails", String.class);
    PowerMockito.doReturn(null)
        .when(FRAPIAuthDbAPI.class, getAuthKeyDetailsMethod)
        .withArguments(or(isA(String.class), isNull(String.class)));

    // Act
    final APIAuthKey retval = objectUnderTest.getAuthKeyDetails(authKey);

    // Assert
    assertNull(retval);
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest({FRAPIAuthDbAPI.class, PersistenceProvider.class})
  @Test
  public void persistAuthKeyInputNullOutputNullPointerException() throws Exception {

    // Setup mocks
    PowerMockito.mockStatic(PersistenceProvider.class);
    PowerMockito.mockStatic(FRAPIAuthDbAPI.class);

    // Arrange
    final AuthServiceRepositoryImpl objectUnderTest =
        (AuthServiceRepositoryImpl)
            Reflector.getInstance("com.firstrain.frapi.repository.impl.AuthServiceRepositoryImpl");
    Reflector.setField(objectUnderTest, "sr", null);
    Reflector.setField(objectUnderTest, "LOG", null);
    Reflector.setField(objectUnderTest, "MULTI_HASH_COUNT", 0);
    final APIAuthKey apiAuthKey = null;
    final Method newTxnMethod =
        DTUMemberMatcher.method(PersistenceProvider.class, "newTxn", String.class);
    PowerMockito.doReturn(null)
        .when(PersistenceProvider.class, newTxnMethod)
        .withArguments(or(isA(String.class), isNull(String.class)));

    // Act
    thrown.expect(NullPointerException.class);
    objectUnderTest.persistAuthKey(apiAuthKey);

    // Method is not expected to return due to exception thrown
  }
}