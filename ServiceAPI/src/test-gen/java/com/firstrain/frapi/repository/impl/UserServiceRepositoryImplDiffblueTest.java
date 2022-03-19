package com.firstrain.frapi.repository.impl;

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
import com.firstrain.db.api.GroupsDbAPI;
import com.firstrain.db.api.UserGroupMapDbAPI;
import com.firstrain.db.api.UsersDbAPI;
import com.firstrain.db.obj.Groups;
import com.firstrain.db.obj.UserGroupMap;
import com.firstrain.db.obj.Users;
import com.firstrain.frapi.util.MailUtil;
import java.lang.reflect.Method;
import java.util.List;
import org.apache.log4j.Logger;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.mockito.expectation.PowerMockitoStubber;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class UserServiceRepositoryImplDiffblueTest {

  @Rule public final ExpectedException thrown = ExpectedException.none();
  @Rule public final Timeout globalTimeout = new Timeout(10_000);

  /* testedClasses: UserServiceRepositoryImpl */
  // Test generated by Diffblue Deeptest.
  @PrepareForTest({UsersDbAPI.class, GroupsDbAPI.class})
  @Test
  public void getUserByEmailAndEnterpriseIdInputNullZeroOutputNull() throws Exception {

    // Setup mocks
    PowerMockito.mockStatic(GroupsDbAPI.class);
    PowerMockito.mockStatic(UsersDbAPI.class);

    // Arrange
    final UserServiceRepositoryImpl objectUnderTest = new UserServiceRepositoryImpl();
    Reflector.setField(objectUnderTest, "LOG", null);
    Reflector.setField(objectUnderTest, "mailUtil", null);
    Reflector.setField(objectUnderTest, "mailServer", null);
    Reflector.setField(objectUnderTest, "imageUrl", null);
    final String email = null;
    final long enterpriseId = 0L;
    final Method getUserByUserNameWithoutOriginMethod =
        DTUMemberMatcher.method(
            UsersDbAPI.class,
            "getUserByUserNameWithoutOrigin",
            String.class,
            String.class,
            String.class);
    PowerMockito.doReturn(null)
        .when(UsersDbAPI.class, getUserByUserNameWithoutOriginMethod)
        .withArguments(
            or(isA(String.class), isNull(String.class)),
            or(isA(String.class), isNull(String.class)),
            or(isA(String.class), isNull(String.class)));
    final Method fetchUserGroupByIdMethod =
        DTUMemberMatcher.method(GroupsDbAPI.class, "fetchUserGroupById", String.class, long.class);
    PowerMockito.doReturn(null)
        .when(GroupsDbAPI.class, fetchUserGroupByIdMethod)
        .withArguments(or(isA(String.class), isNull(String.class)), anyLong());

    // Act
    final Users retval = objectUnderTest.getUserByEmailAndEnterpriseId(email, enterpriseId);

    // Assert
    assertNull(retval);
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest({UsersDbAPI.class, Groups.class, GroupsDbAPI.class})
  @Test
  public void getUserByEmailAndEnterpriseIdInputNullZeroOutputNull2() throws Exception {

    // Setup mocks
    PowerMockito.mockStatic(GroupsDbAPI.class);
    PowerMockito.mockStatic(UsersDbAPI.class);

    // Arrange
    final UserServiceRepositoryImpl objectUnderTest = new UserServiceRepositoryImpl();
    Reflector.setField(objectUnderTest, "LOG", null);
    Reflector.setField(objectUnderTest, "mailUtil", null);
    Reflector.setField(objectUnderTest, "mailServer", null);
    Reflector.setField(objectUnderTest, "imageUrl", null);
    final String email = null;
    final long enterpriseId = 0L;
    final Method getUserByUserNameWithoutOriginMethod =
        DTUMemberMatcher.method(
            UsersDbAPI.class,
            "getUserByUserNameWithoutOrigin",
            String.class,
            String.class,
            String.class);
    PowerMockito.doReturn(null)
        .when(UsersDbAPI.class, getUserByUserNameWithoutOriginMethod)
        .withArguments(
            or(isA(String.class), isNull(String.class)),
            or(isA(String.class), isNull(String.class)),
            or(isA(String.class), isNull(String.class)));
    final Groups groups = PowerMockito.mock(Groups.class);
    final Method getProductTypeMethod = DTUMemberMatcher.method(Groups.class, "getProductType");
    PowerMockito.doReturn(null).when(groups, getProductTypeMethod).withNoArguments();
    final Method fetchUserGroupByIdMethod =
        DTUMemberMatcher.method(GroupsDbAPI.class, "fetchUserGroupById", String.class, long.class);
    PowerMockito.doReturn(groups)
        .when(GroupsDbAPI.class, fetchUserGroupByIdMethod)
        .withArguments(or(isA(String.class), isNull(String.class)), anyLong());

    // Act
    final Users retval = objectUnderTest.getUserByEmailAndEnterpriseId(email, enterpriseId);

    // Assert
    assertNull(retval);
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest({Logger.class, UsersDbAPI.class, Transaction.class, PersistenceProvider.class})
  @Test
  public void getUserByIdInputPositiveOutputNotNull() throws Exception {

    // Setup mocks
    PowerMockito.mockStatic(PersistenceProvider.class);
    PowerMockito.mockStatic(UsersDbAPI.class);

    // Arrange
    final UserServiceRepositoryImpl objectUnderTest =
        (UserServiceRepositoryImpl)
            Reflector.getInstance("com.firstrain.frapi.repository.impl.UserServiceRepositoryImpl");
    final Logger logger = PowerMockito.mock(Logger.class);
    Reflector.setField(objectUnderTest, "LOG", logger);
    Reflector.setField(objectUnderTest, "mailUtil", null);
    Reflector.setField(objectUnderTest, "mailServer", null);
    Reflector.setField(objectUnderTest, "imageUrl", null);
    final long userId = 9L;
    final Users users = (Users) Reflector.getInstance("com.firstrain.db.obj.Users");
    final Method getUserByIdMethod =
        DTUMemberMatcher.method(UsersDbAPI.class, "getUserById", Transaction.class, long.class);
    ((PowerMockitoStubber) PowerMockito.doReturn(null).doReturn(users))
        .when(UsersDbAPI.class, getUserByIdMethod)
        .withArguments(or(isA(Transaction.class), isNull(Transaction.class)), anyLong());
    final Transaction transaction = PowerMockito.mock(Transaction.class);
    final Method newTxnMethod =
        DTUMemberMatcher.method(PersistenceProvider.class, "newTxn", String.class);
    ((PowerMockitoStubber) PowerMockito.doReturn(null).doReturn(transaction))
        .when(PersistenceProvider.class, newTxnMethod)
        .withArguments(or(isA(String.class), isNull(String.class)));

    // Act
    final Users retval = objectUnderTest.getUserById(userId);

    // Assert
    assertNotNull(retval);
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest({Logger.class, UsersDbAPI.class, Transaction.class, PersistenceProvider.class})
  @Test
  public void getUserByIdInputPositiveOutputNotNull2() throws Exception {

    // Setup mocks
    PowerMockito.mockStatic(PersistenceProvider.class);
    PowerMockito.mockStatic(UsersDbAPI.class);

    // Arrange
    final UserServiceRepositoryImpl objectUnderTest =
        (UserServiceRepositoryImpl)
            Reflector.getInstance("com.firstrain.frapi.repository.impl.UserServiceRepositoryImpl");
    final Logger logger = PowerMockito.mock(Logger.class);
    Reflector.setField(objectUnderTest, "LOG", logger);
    Reflector.setField(objectUnderTest, "mailUtil", null);
    Reflector.setField(objectUnderTest, "mailServer", null);
    Reflector.setField(objectUnderTest, "imageUrl", null);
    final long userId = 9L;
    final Users users = (Users) Reflector.getInstance("com.firstrain.db.obj.Users");
    final Method getUserByIdMethod =
        DTUMemberMatcher.method(UsersDbAPI.class, "getUserById", Transaction.class, long.class);
    ((PowerMockitoStubber) PowerMockito.doReturn(null).doReturn(users))
        .when(UsersDbAPI.class, getUserByIdMethod)
        .withArguments(or(isA(Transaction.class), isNull(Transaction.class)), anyLong());
    final Transaction transaction1 = PowerMockito.mock(Transaction.class);
    final Transaction transaction = PowerMockito.mock(Transaction.class);
    final Method newTxnMethod =
        DTUMemberMatcher.method(PersistenceProvider.class, "newTxn", String.class);
    ((PowerMockitoStubber) PowerMockito.doReturn(transaction).doReturn(transaction1))
        .when(PersistenceProvider.class, newTxnMethod)
        .withArguments(or(isA(String.class), isNull(String.class)));

    // Act
    final Users retval = objectUnderTest.getUserById(userId);

    // Assert
    assertNotNull(retval);
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest({Logger.class, UsersDbAPI.class, PersistenceProvider.class})
  @Test
  public void getUserByIdInputPositiveOutputNotNull3() throws Exception {

    // Setup mocks
    PowerMockito.mockStatic(PersistenceProvider.class);
    PowerMockito.mockStatic(UsersDbAPI.class);

    // Arrange
    final UserServiceRepositoryImpl objectUnderTest =
        (UserServiceRepositoryImpl)
            Reflector.getInstance("com.firstrain.frapi.repository.impl.UserServiceRepositoryImpl");
    final Logger logger = PowerMockito.mock(Logger.class);
    Reflector.setField(objectUnderTest, "LOG", logger);
    Reflector.setField(objectUnderTest, "mailUtil", null);
    Reflector.setField(objectUnderTest, "mailServer", null);
    Reflector.setField(objectUnderTest, "imageUrl", null);
    final long userId = 9L;
    final Users users = (Users) Reflector.getInstance("com.firstrain.db.obj.Users");
    final Method getUserByIdMethod =
        DTUMemberMatcher.method(UsersDbAPI.class, "getUserById", Transaction.class, long.class);
    ((PowerMockitoStubber) PowerMockito.doReturn(null).doReturn(users))
        .when(UsersDbAPI.class, getUserByIdMethod)
        .withArguments(or(isA(Transaction.class), isNull(Transaction.class)), anyLong());
    final Method newTxnMethod =
        DTUMemberMatcher.method(PersistenceProvider.class, "newTxn", String.class);
    ((PowerMockitoStubber) PowerMockito.doReturn(null).doReturn(null))
        .when(PersistenceProvider.class, newTxnMethod)
        .withArguments(or(isA(String.class), isNull(String.class)));

    // Act
    final Users retval = objectUnderTest.getUserById(userId);

    // Assert
    assertNotNull(retval);
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest({Logger.class, UsersDbAPI.class, PersistenceProvider.class})
  @Test
  public void getUserByIdInputPositiveOutputNull() throws Exception {

    // Setup mocks
    PowerMockito.mockStatic(PersistenceProvider.class);
    PowerMockito.mockStatic(UsersDbAPI.class);

    // Arrange
    final UserServiceRepositoryImpl objectUnderTest =
        (UserServiceRepositoryImpl)
            Reflector.getInstance("com.firstrain.frapi.repository.impl.UserServiceRepositoryImpl");
    final Logger logger = PowerMockito.mock(Logger.class);
    Reflector.setField(objectUnderTest, "LOG", logger);
    Reflector.setField(objectUnderTest, "mailUtil", null);
    Reflector.setField(objectUnderTest, "mailServer", null);
    Reflector.setField(objectUnderTest, "imageUrl", null);
    final long userId = 9L;
    final Method getUserByIdMethod =
        DTUMemberMatcher.method(UsersDbAPI.class, "getUserById", Transaction.class, long.class);
    ((PowerMockitoStubber) PowerMockito.doReturn(null).doReturn(null))
        .when(UsersDbAPI.class, getUserByIdMethod)
        .withArguments(or(isA(Transaction.class), isNull(Transaction.class)), anyLong());
    final Method newTxnMethod =
        DTUMemberMatcher.method(PersistenceProvider.class, "newTxn", String.class);
    ((PowerMockitoStubber) PowerMockito.doReturn(null).doReturn(null))
        .when(PersistenceProvider.class, newTxnMethod)
        .withArguments(or(isA(String.class), isNull(String.class)));

    // Act
    final Users retval = objectUnderTest.getUserById(userId);

    // Assert
    assertNull(retval);
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest({UsersDbAPI.class, PersistenceProvider.class})
  @Test
  public void getUserByIdInputPositiveOutputNullPointerException() throws Exception {

    // Setup mocks
    PowerMockito.mockStatic(PersistenceProvider.class);
    PowerMockito.mockStatic(UsersDbAPI.class);

    // Arrange
    final UserServiceRepositoryImpl objectUnderTest =
        (UserServiceRepositoryImpl)
            Reflector.getInstance("com.firstrain.frapi.repository.impl.UserServiceRepositoryImpl");
    Reflector.setField(objectUnderTest, "LOG", null);
    Reflector.setField(objectUnderTest, "mailUtil", null);
    Reflector.setField(objectUnderTest, "mailServer", null);
    Reflector.setField(objectUnderTest, "imageUrl", null);
    final long userId = 7L;
    final Method getUserByIdMethod =
        DTUMemberMatcher.method(UsersDbAPI.class, "getUserById", Transaction.class, long.class);
    ((PowerMockitoStubber) PowerMockito.doReturn(null).doReturn(null))
        .when(UsersDbAPI.class, getUserByIdMethod)
        .withArguments(or(isA(Transaction.class), isNull(Transaction.class)), anyLong());
    final Method newTxnMethod =
        DTUMemberMatcher.method(PersistenceProvider.class, "newTxn", String.class);
    ((PowerMockitoStubber) PowerMockito.doReturn(null).doReturn(null))
        .when(PersistenceProvider.class, newTxnMethod)
        .withArguments(or(isA(String.class), isNull(String.class)));

    // Act
    thrown.expect(NullPointerException.class);
    objectUnderTest.getUserById(userId);

    // Method is not expected to return due to exception thrown
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest({UsersDbAPI.class, Transaction.class, PersistenceProvider.class})
  @Test
  public void getUserByIdInputPositiveOutputNullPointerException2() throws Exception {

    // Setup mocks
    PowerMockito.mockStatic(PersistenceProvider.class);
    PowerMockito.mockStatic(UsersDbAPI.class);

    // Arrange
    final UserServiceRepositoryImpl objectUnderTest =
        (UserServiceRepositoryImpl)
            Reflector.getInstance("com.firstrain.frapi.repository.impl.UserServiceRepositoryImpl");
    Reflector.setField(objectUnderTest, "LOG", null);
    Reflector.setField(objectUnderTest, "mailUtil", null);
    Reflector.setField(objectUnderTest, "mailServer", null);
    Reflector.setField(objectUnderTest, "imageUrl", null);
    final long userId = 7L;
    final Method getUserByIdMethod =
        DTUMemberMatcher.method(UsersDbAPI.class, "getUserById", Transaction.class, long.class);
    ((PowerMockitoStubber) PowerMockito.doReturn(null).doReturn(null))
        .when(UsersDbAPI.class, getUserByIdMethod)
        .withArguments(or(isA(Transaction.class), isNull(Transaction.class)), anyLong());
    final Transaction transaction = PowerMockito.mock(Transaction.class);
    final Method newTxnMethod =
        DTUMemberMatcher.method(PersistenceProvider.class, "newTxn", String.class);
    ((PowerMockitoStubber) PowerMockito.doReturn(null).doReturn(transaction))
        .when(PersistenceProvider.class, newTxnMethod)
        .withArguments(or(isA(String.class), isNull(String.class)));

    // Act
    thrown.expect(NullPointerException.class);
    objectUnderTest.getUserById(userId);

    // Method is not expected to return due to exception thrown
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest(GroupsDbAPI.class)
  @Test
  public void getUserGroupByIdInputZeroOutputNull() throws Exception {

    // Setup mocks
    PowerMockito.mockStatic(GroupsDbAPI.class);

    // Arrange
    final UserServiceRepositoryImpl objectUnderTest = new UserServiceRepositoryImpl();
    Reflector.setField(objectUnderTest, "LOG", null);
    Reflector.setField(objectUnderTest, "mailUtil", null);
    Reflector.setField(objectUnderTest, "mailServer", null);
    Reflector.setField(objectUnderTest, "imageUrl", null);
    final long groupId = 0L;
    final Method fetchUserGroupByIdMethod =
        DTUMemberMatcher.method(GroupsDbAPI.class, "fetchUserGroupById", String.class, long.class);
    PowerMockito.doReturn(null)
        .when(GroupsDbAPI.class, fetchUserGroupByIdMethod)
        .withArguments(or(isA(String.class), isNull(String.class)), anyLong());

    // Act
    final Groups retval = objectUnderTest.getUserGroupById(groupId);

    // Assert
    assertNull(retval);
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest({UserGroupMapDbAPI.class, Logger.class})
  @Test
  public void getUserGroupMapByUserIdInputPositiveOutputNull() throws Exception {

    // Setup mocks
    PowerMockito.mockStatic(UserGroupMapDbAPI.class);

    // Arrange
    final UserServiceRepositoryImpl objectUnderTest =
        (UserServiceRepositoryImpl)
            Reflector.getInstance("com.firstrain.frapi.repository.impl.UserServiceRepositoryImpl");
    final Logger logger = PowerMockito.mock(Logger.class);
    Reflector.setField(objectUnderTest, "LOG", logger);
    final MailUtil mailUtil = new MailUtil();
    Reflector.setField(objectUnderTest, "mailUtil", mailUtil);
    Reflector.setField(objectUnderTest, "mailServer", "");
    Reflector.setField(objectUnderTest, "imageUrl", "");
    final long userId = 8L;
    final Method getUserGroupMapByUserIdMethod =
        DTUMemberMatcher.method(
            UserGroupMapDbAPI.class, "getUserGroupMapByUserId", String.class, long.class);
    ((PowerMockitoStubber) PowerMockito.doReturn(null).doReturn(null))
        .when(UserGroupMapDbAPI.class, getUserGroupMapByUserIdMethod)
        .withArguments(or(isA(String.class), isNull(String.class)), anyLong());

    // Act
    final List<UserGroupMap> retval = objectUnderTest.getUserGroupMapByUserId(userId);

    // Assert
    assertNull(retval);
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest({UsersDbAPI.class, Logger.class})
  @Test
  public void getUsersByGrpIdAndMembershipTypeInputZeroNullOutputNull() throws Exception {

    // Setup mocks
    PowerMockito.mockStatic(UsersDbAPI.class);

    // Arrange
    final UserServiceRepositoryImpl objectUnderTest =
        (UserServiceRepositoryImpl)
            Reflector.getInstance("com.firstrain.frapi.repository.impl.UserServiceRepositoryImpl");
    final Logger logger = PowerMockito.mock(Logger.class);
    Reflector.setField(objectUnderTest, "LOG", logger);
    Reflector.setField(objectUnderTest, "mailUtil", null);
    Reflector.setField(objectUnderTest, "mailServer", null);
    Reflector.setField(objectUnderTest, "imageUrl", null);
    final long groupId = 0L;
    final UserGroupMap.MembershipType[] mt = null;
    final Method getUsersByGrpIdAndMembershipTypeMethod =
        DTUMemberMatcher.method(
            UsersDbAPI.class,
            "getUsersByGrpIdAndMembershipType",
            String.class,
            long.class,
            UserGroupMap.MembershipType[].class);
    ((PowerMockitoStubber) PowerMockito.doReturn(null).doReturn(null))
        .when(UsersDbAPI.class, getUsersByGrpIdAndMembershipTypeMethod)
        .withArguments(
            or(isA(String.class), isNull(String.class)),
            anyLong(),
            or(
                isA(UserGroupMap.MembershipType[].class),
                isNull(UserGroupMap.MembershipType[].class)));

    // Act
    final List<Users> retval = objectUnderTest.getUsersByGrpIdAndMembershipType(groupId, mt);

    // Assert
    assertNull(retval);
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest({UsersDbAPI.class, PersistenceProvider.class})
  @Test
  public void updateUserInputNullNotNullNotNullOutputNullPointerException() throws Exception {

    // Setup mocks
    PowerMockito.mockStatic(PersistenceProvider.class);
    PowerMockito.mockStatic(UsersDbAPI.class);

    // Arrange
    final UserServiceRepositoryImpl objectUnderTest =
        (UserServiceRepositoryImpl)
            Reflector.getInstance("com.firstrain.frapi.repository.impl.UserServiceRepositoryImpl");
    Reflector.setField(objectUnderTest, "LOG", null);
    Reflector.setField(objectUnderTest, "mailUtil", null);
    Reflector.setField(objectUnderTest, "mailServer", "");
    Reflector.setField(objectUnderTest, "imageUrl", null);
    final Users user = null;
    final String status = "";
    final String oldStatus = "";
    final Method newTxnMethod =
        DTUMemberMatcher.method(PersistenceProvider.class, "newTxn", String.class);
    PowerMockito.doReturn(null)
        .when(PersistenceProvider.class, newTxnMethod)
        .withArguments(or(isA(String.class), isNull(String.class)));

    // Act
    thrown.expect(NullPointerException.class);
    objectUnderTest.updateUser(user, status, oldStatus);

    // Method is not expected to return due to exception thrown
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest({UsersDbAPI.class, Transaction.class, PersistenceProvider.class})
  @Test
  public void updateUserInputNullNotNullNotNullOutputNullPointerException2() throws Exception {

    // Setup mocks
    PowerMockito.mockStatic(PersistenceProvider.class);
    PowerMockito.mockStatic(UsersDbAPI.class);

    // Arrange
    final UserServiceRepositoryImpl objectUnderTest =
        (UserServiceRepositoryImpl)
            Reflector.getInstance("com.firstrain.frapi.repository.impl.UserServiceRepositoryImpl");
    Reflector.setField(objectUnderTest, "LOG", null);
    Reflector.setField(objectUnderTest, "mailUtil", null);
    Reflector.setField(objectUnderTest, "mailServer", "");
    Reflector.setField(objectUnderTest, "imageUrl", null);
    final Users user = null;
    final String status = "";
    final String oldStatus = "";
    final Transaction transaction = PowerMockito.mock(Transaction.class);
    final Method newTxnMethod =
        DTUMemberMatcher.method(PersistenceProvider.class, "newTxn", String.class);
    PowerMockito.doReturn(transaction)
        .when(PersistenceProvider.class, newTxnMethod)
        .withArguments(or(isA(String.class), isNull(String.class)));

    // Act
    thrown.expect(NullPointerException.class);
    objectUnderTest.updateUser(user, status, oldStatus);

    // Method is not expected to return due to exception thrown
  }
}