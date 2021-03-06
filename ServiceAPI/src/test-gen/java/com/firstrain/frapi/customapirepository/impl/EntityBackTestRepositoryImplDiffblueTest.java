package com.firstrain.frapi.customapirepository.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.AdditionalMatchers.or;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.isA;
import static org.mockito.Matchers.isNull;

import com.diffblue.deeptestutils.Reflector;
import com.diffblue.deeptestutils.mock.DTUMemberMatcher;
import com.firstrain.common.db.jpa.PersistenceProvider;
import com.firstrain.common.db.jpa.Transaction;
import com.firstrain.db.obj.EntityBacktest;
import java.lang.reflect.Method;
import org.apache.log4j.Logger;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class EntityBackTestRepositoryImplDiffblueTest {

  @Rule public final ExpectedException thrown = ExpectedException.none();
  @Rule public final Timeout globalTimeout = new Timeout(10_000);

  /* testedClasses: EntityBackTestRepositoryImpl */
  // Test generated by Diffblue Deeptest.
  @PrepareForTest(Transaction.class)
  @Test
  public void updateEntityBackTestInputNotNullZeroOutputNull() throws Exception {

    // Arrange
    final EntityBackTestRepositoryImpl objectUnderTest = new EntityBackTestRepositoryImpl();
    final Transaction txn = PowerMockito.mock(Transaction.class);
    final Method fetchMethod =
        DTUMemberMatcher.method(Transaction.class, "fetch", long.class, Class.class);
    PowerMockito.doReturn(null)
        .when(txn, fetchMethod)
        .withArguments(anyLong(), or(isA(Class.class), isNull(Class.class)));
    final long id = 0L;

    // Act
    final Class<?> classUnderTest =
        Reflector.forName(
            "com.firstrain.frapi.customapirepository.impl.EntityBackTestRepositoryImpl");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod(
            "updateEntityBackTest",
            Reflector.forName("com.firstrain.common.db.jpa.Transaction"),
            Reflector.forName("long"));
    methodUnderTest.setAccessible(true);
    final EntityBacktest retval = (EntityBacktest) methodUnderTest.invoke(objectUnderTest, txn, id);

    // Assert
    assertNull(retval);
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest({Logger.class, Transaction.class, PersistenceProvider.class})
  @Test
  public void updateStateInputNullPositiveOutputNullPointerException() throws Exception {

    // Setup mocks
    PowerMockito.mockStatic(PersistenceProvider.class);
    PowerMockito.mockStatic(Logger.class);

    // Arrange
    final EntityBackTestRepositoryImpl objectUnderTest = new EntityBackTestRepositoryImpl();
    final String state = null;
    final long id = 7L;
    final Logger logger = PowerMockito.mock(Logger.class);
    final Method getLoggerMethod = DTUMemberMatcher.method(Logger.class, "getLogger", Class.class);
    PowerMockito.doReturn(logger)
        .when(Logger.class, getLoggerMethod)
        .withArguments(or(isA(Class.class), isNull(Class.class)));
    final Transaction transaction = PowerMockito.mock(Transaction.class);
    final Method fetchMethod =
        DTUMemberMatcher.method(Transaction.class, "fetch", long.class, Class.class);
    PowerMockito.doReturn(null)
        .when(transaction, fetchMethod)
        .withArguments(anyLong(), or(isA(Class.class), isNull(Class.class)));
    final Method newTxnMethod =
        DTUMemberMatcher.method(PersistenceProvider.class, "newTxn", String.class);
    PowerMockito.doReturn(transaction)
        .when(PersistenceProvider.class, newTxnMethod)
        .withArguments(or(isA(String.class), isNull(String.class)));

    // Act
    thrown.expect(NullPointerException.class);
    objectUnderTest.updateState(state, id);

    // Method is not expected to return due to exception thrown
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest({EntityBacktest.class, Transaction.class, PersistenceProvider.class})
  @Test
  public void updateStateInputNullZeroOutputNotNull() throws Exception {

    // Setup mocks
    PowerMockito.mockStatic(PersistenceProvider.class);

    // Arrange
    final EntityBackTestRepositoryImpl objectUnderTest = new EntityBackTestRepositoryImpl();
    final String state = null;
    final long id = 0L;
    final Transaction transaction = PowerMockito.mock(Transaction.class);
    final EntityBacktest entityBacktest = PowerMockito.mock(EntityBacktest.class);
    final Method fetchMethod =
        DTUMemberMatcher.method(Transaction.class, "fetch", long.class, Class.class);
    PowerMockito.doReturn(entityBacktest)
        .when(transaction, fetchMethod)
        .withArguments(anyLong(), or(isA(Class.class), isNull(Class.class)));
    final Method newTxnMethod =
        DTUMemberMatcher.method(PersistenceProvider.class, "newTxn", String.class);
    PowerMockito.doReturn(transaction)
        .when(PersistenceProvider.class, newTxnMethod)
        .withArguments(or(isA(String.class), isNull(String.class)));

    // Act
    final EntityBacktest retval = objectUnderTest.updateState(state, id);

    // Assert
    assertNotNull(retval);
  }
}
