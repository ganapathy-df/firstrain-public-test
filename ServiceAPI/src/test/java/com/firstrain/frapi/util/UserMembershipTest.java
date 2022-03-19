package com.firstrain.frapi.util;

import static com.firstrain.db.obj.UserGroupMap.MembershipType.ADMIN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import com.firstrain.db.obj.UserGroupMap;
import com.firstrain.db.obj.UserGroupMap.MembershipType;
import com.firstrain.frapi.repository.UserServiceRepository;

public class UserMembershipTest {

    private UserServiceRepository userServiceRepository = mock(UserServiceRepository.class);
    private long frUserId = 100L;

    @Before
    public void setUp() {
        userServiceRepository = mock(UserServiceRepository.class);
        frUserId = 100L;
    }

    @Test
    public void givenConditionWhenRetrieveMembershipTypeRunsThenMembershipTypeIsAdmin() throws Exception {
        // Arrange
        when(userServiceRepository.getUserGroupMapByUserId(frUserId)).thenReturn(generateUserGroupMaps());

        // Act
        MembershipType membershipType = UserMembership.retrieveMembershipType(userServiceRepository, frUserId);

        // Assert
        assertEquals(ADMIN, membershipType);
    }

    @Test
    public void givenConditionWhenRetrieveMembershipTypeRunsThenMembershipTypeIsNull() throws Exception {
        // Arrange
        when(userServiceRepository.getUserGroupMapByUserId(frUserId)).thenReturn(null);

        // Act
        MembershipType membershipType = UserMembership.retrieveMembershipType(userServiceRepository, frUserId);

        // Assert
        assertNull(membershipType);
    }

    @Test(expected = InvocationTargetException.class)
    public void givenReflectionSetupWhenNewInstanceRunsThenInvocationTargetExceptionIsThrown() throws Exception {
        // Arrange
        Class<UserMembership> userMembershipClass = UserMembership.class;
        Constructor<?> constructor = userMembershipClass.getDeclaredConstructors()[0];
        constructor.setAccessible(true);

        // Act
        constructor.newInstance();
    }

    private List<UserGroupMap> generateUserGroupMaps() {
        List<UserGroupMap> userGroupMaps = new ArrayList<UserGroupMap>();
        UserGroupMap userGroupMap = new UserGroupMap();
        userGroupMap.setMembershipType(ADMIN);
        userGroupMaps.add(userGroupMap);
        return userGroupMaps;
    }
}
