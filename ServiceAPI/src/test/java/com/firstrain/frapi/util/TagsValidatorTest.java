package com.firstrain.frapi.util;

import static com.firstrain.frapi.util.TagsValidator.isEntityNotFound;
import static com.firstrain.frapi.util.TagsValidator.isIllegalArgument;
import static com.firstrain.frapi.util.TagsValidator.isInsufficientPrivilege;
import static com.firstrain.frapi.util.TagsValidator.userDoesNotOwnMonitor;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import com.firstrain.db.obj.BaseItem.FLAGS;
import com.firstrain.db.obj.BaseItem.OwnedByType;
import com.firstrain.db.obj.Tags;
import com.firstrain.frapi.pojo.MonitorAPIResponse;

public class TagsValidatorTest {

    private Tags tags;
    private MonitorAPIResponse monitorAPIResponse;
    private long frUserId;
    private long monitorId;
    private List<String> filtersToRemoveList;

    @Before
    public void setUp() {
        tags = mock(Tags.class);
        monitorAPIResponse = mock(MonitorAPIResponse.class);
        frUserId = 10L;
        monitorId = 1002L;
    }

    @Test
    public void givenConditionWhenIsInsufficientPrivilegeRunsThenResultIsFalse() throws Exception {
        // Arrange
        when(tags.getOwnedBy()).thenReturn(10L);

        // Act
        boolean result = isInsufficientPrivilege(tags, generateAllGroupIds(), monitorAPIResponse);

        // Assert
        assertFalse(result);
    }

    @Test
    public void givenConditionWhenIsInsufficientPrivilegeRunsThenResultIsTrue() throws Exception {
        // Arrange
        when(tags.getOwnedBy()).thenReturn(12L);

        // Act
        boolean result = isInsufficientPrivilege(tags, generateAllGroupIds(), monitorAPIResponse);

        // Assert
        assertTrue(result);
    }

    @Test
    public void givenConditionWhenUserDoesNotOwnMonitorRunsThenResultIsTrue() throws Exception {
        // Arrange
        when(tags.getOwnedByType()).thenReturn(OwnedByType.USER);

        // Act
        boolean result = userDoesNotOwnMonitor(tags, monitorAPIResponse);

        // Assert
        assertTrue(result);
    }

    @Test
    public void givenConditionWhenUserDoesNotOwnMonitorRunsThenResultIsFalse() throws Exception {
        // Arrange
        when(tags.getOwnedByType()).thenReturn(OwnedByType.GROUP);

        // Act
        boolean result = userDoesNotOwnMonitor(tags, monitorAPIResponse);

        // Assert
        assertFalse(result);
    }

    @Test
    public void givenConditionWhenUserDoesNotOwnMonitorWithFrUserIdRunsThenResultIsFalse() throws Exception {
        // Arrange
        when(tags.getOwnedBy()).thenReturn(10L);

        // Act
        boolean result = userDoesNotOwnMonitor(tags, frUserId, monitorAPIResponse);

        // Assert
        assertFalse(result);
    }

    @Test
    public void givenConditionWhenUserDoesNotOwnMonitorWithFrUserIdRunsThenResultIsTrue() throws Exception {
        // Arrange
        when(tags.getOwnedBy()).thenReturn(12L);

        // Act
        boolean result = userDoesNotOwnMonitor(tags, frUserId, monitorAPIResponse);

        // Assert
        assertTrue(result);
    }

    @Test
    public void givenConditionWhenIsEntityNotFoundRunsThenResultIsTrue() throws Exception {
        // Arrange
        when(tags.getFlags()).thenReturn(FLAGS.DELETED);

        // Act
        boolean result = isEntityNotFound(tags, monitorId, monitorAPIResponse);

        // Assert
        assertTrue(result);
    }

    @Test
    public void givenConditionWhenIsEntityNotFoundRunsThenResultIsFalse() throws Exception {
        // Arrange
        when(tags.getFlags()).thenReturn(FLAGS.ACTIVE);

        // Act
        boolean result = isEntityNotFound(tags, monitorId, monitorAPIResponse);

        // Assert
        assertFalse(result);
    }

    @Test
    public void givenFiltersToRemoveListWhenIsIllegalArgumentRunsThenResultIsTrue() throws Exception {
        // Arrange
        filtersToRemoveList = new ArrayList<String>();

        // Act
        boolean result = isIllegalArgument(filtersToRemoveList, monitorAPIResponse);

        // Assert
        assertTrue(result);
    }

    @Test
    public void givenFiltersToRemoveListWhenIsIllegalArgumentRunsThenResultIsFalse() throws Exception {
        // Arrange
        filtersToRemoveList = new ArrayList<String>();
        filtersToRemoveList.add("test");

        // Act
        boolean result = isIllegalArgument(filtersToRemoveList, monitorAPIResponse);

        // Assert
        assertFalse(result);
    }

    @Test(expected = InvocationTargetException.class)
    public void givenReflectionSetupWhenNewInstanceRunsThenInvocationTargetExceptionIsThrown() throws Exception {
        // Arrange
        Class<TagsValidator> userMembershipClass = TagsValidator.class;
        Constructor<?> constructor = userMembershipClass.getDeclaredConstructors()[0];
        constructor.setAccessible(true);

        // Act
        constructor.newInstance();
    }

    private Set<Long> generateAllGroupIds() {
        Set<Long> longs = new HashSet<Long>();
        longs.add(5L);
        longs.add(10L);
        longs.add(15L);
        return longs;
    }
}
