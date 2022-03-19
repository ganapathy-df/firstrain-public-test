package com.firstrain.frapi.service.filters;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.diffblue.deeptestutils.Reflector;
import com.firstrain.frapi.events.IEvents;
import com.firstrain.frapi.service.filters.Selectors.DepartureSelector;
import com.firstrain.frapi.service.filters.Selectors.HireSelector;
import com.firstrain.frapi.service.filters.Selectors.InternalTurnoverSelector;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class EventsDedupingFilterDiffblueTest {

  @Rule public final ExpectedException thrown = ExpectedException.none();
  @Rule public final Timeout globalTimeout = new Timeout(10_000);

  /* testedClasses: EventsDedupingFilter */
  // Test generated by Diffblue Deeptest.

  @Test
  public void access$000OutputNotNull() throws Exception {

    // Act
    final Class<?> classUnderTest =
        Reflector.forName("com.firstrain.frapi.service.filters.EventsDedupingFilter");
    final Method methodUnderTest = classUnderTest.getDeclaredMethod("access$000");
    methodUnderTest.setAccessible(true);
    final InternalTurnoverSelector retval = (InternalTurnoverSelector) methodUnderTest.invoke(null);

    // Assert
    assertNotNull(retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void access$200OutputNotNull() throws Exception {

    // Act
    final Class<?> classUnderTest =
        Reflector.forName("com.firstrain.frapi.service.filters.EventsDedupingFilter");
    final Method methodUnderTest = classUnderTest.getDeclaredMethod("access$200");
    methodUnderTest.setAccessible(true);
    final HireSelector retval = (HireSelector) methodUnderTest.invoke(null);

    // Assert
    assertNotNull(retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void access$300OutputNotNull() throws Exception {

    // Act
    final Class<?> classUnderTest =
        Reflector.forName("com.firstrain.frapi.service.filters.EventsDedupingFilter");
    final Method methodUnderTest = classUnderTest.getDeclaredMethod("access$300");
    methodUnderTest.setAccessible(true);
    final DepartureSelector retval = (DepartureSelector) methodUnderTest.invoke(null);

    // Assert
    assertNotNull(retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void access$400InputNotNullOutputNull() throws Exception {

    // Arrange
    final Object x0 =
        Reflector.getInstance(
            "com.firstrain.frapi.service.filters.EventsDedupingFilter$EntityMTEvents$EventDetails");
    Reflector.setField(x0, "departureCount", 0);
    Reflector.setField(x0, "internalMoveRank1Count", 0);
    Reflector.setField(x0, "hireCount", 0);
    Reflector.setField(x0, "internalMoveCount", 0);
    Reflector.setField(x0, "events", null);

    // Act
    final Class<?> classUnderTest =
        Reflector.forName(
            "com.firstrain.frapi.service.filters.EventsDedupingFilter$EntityMTEvents$EventDetails");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod(
            "access$400",
            Reflector.forName(
                "com.firstrain.frapi.service.filters.EventsDedupingFilter$EntityMTEvents$EventDetails"));
    methodUnderTest.setAccessible(true);
    final List retval = (List) methodUnderTest.invoke(null, x0);

    // Assert
    assertNull(retval);
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest(IEvents.class)
  @Test
  public void addInputNotNullOutputVoid() throws Exception {

    // Arrange
    final Object objectUnderTest =
        Reflector.getInstance(
            "com.firstrain.frapi.service.filters.EventsDedupingFilter$EntityMTEvents$EventDetails");
    Reflector.setField(objectUnderTest, "departureCount", 0);
    Reflector.setField(objectUnderTest, "internalMoveRank1Count", 0);
    Reflector.setField(objectUnderTest, "hireCount", 0);
    Reflector.setField(objectUnderTest, "internalMoveCount", 0);
    final ArrayList<IEvents> arrayList = new ArrayList<IEvents>();
    Reflector.setField(objectUnderTest, "events", arrayList);
    final IEvents e = PowerMockito.mock(IEvents.class);
    PowerMockito.when(e.getEventGroup()).thenReturn(null).thenReturn(null).thenReturn(null);

    // Act
    final Class<?> classUnderTest =
        Reflector.forName(
            "com.firstrain.frapi.service.filters.EventsDedupingFilter$EntityMTEvents$EventDetails");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod(
            "add", Reflector.forName("com.firstrain.frapi.events.IEvents"));
    methodUnderTest.setAccessible(true);
    methodUnderTest.invoke(objectUnderTest, e);
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest(IEvents.class)
  @Test
  public void addInputNotNullOutputVoid2() throws Exception {

    // Arrange
    final Object objectUnderTest =
        Reflector.getInstance(
            "com.firstrain.frapi.service.filters.EventsDedupingFilter$EntityMTEvents$EventDetails");
    Reflector.setField(objectUnderTest, "departureCount", 0);
    Reflector.setField(objectUnderTest, "internalMoveRank1Count", 0);
    Reflector.setField(objectUnderTest, "hireCount", 0);
    Reflector.setField(objectUnderTest, "internalMoveCount", 0);
    final ArrayList<IEvents> arrayList = new ArrayList<IEvents>();
    Reflector.setField(objectUnderTest, "events", arrayList);
    final IEvents e = PowerMockito.mock(IEvents.class);
    PowerMockito.when(e.getEventGroup())
        .thenReturn(null)
        .thenReturn(null)
        .thenReturn(IEvents.EventGroupEnum.GROUP_MGMT_CHANGE);
    PowerMockito.when(e.getFlag()).thenReturn(8);

    // Act
    final Class<?> classUnderTest =
        Reflector.forName(
            "com.firstrain.frapi.service.filters.EventsDedupingFilter$EntityMTEvents$EventDetails");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod(
            "add", Reflector.forName("com.firstrain.frapi.events.IEvents"));
    methodUnderTest.setAccessible(true);
    methodUnderTest.invoke(objectUnderTest, e);
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest(IEvents.class)
  @Test
  public void addInputNotNullOutputVoid3() throws Exception {

    // Arrange
    final Object objectUnderTest =
        Reflector.getInstance(
            "com.firstrain.frapi.service.filters.EventsDedupingFilter$EntityMTEvents$EventDetails");
    Reflector.setField(objectUnderTest, "departureCount", 0);
    Reflector.setField(objectUnderTest, "internalMoveRank1Count", 0);
    Reflector.setField(objectUnderTest, "hireCount", 1);
    Reflector.setField(objectUnderTest, "internalMoveCount", 0);
    final ArrayList<IEvents> arrayList = new ArrayList<IEvents>();
    Reflector.setField(objectUnderTest, "events", arrayList);
    final IEvents e = PowerMockito.mock(IEvents.class);
    PowerMockito.when(e.getEventGroup())
        .thenReturn(null)
        .thenReturn(IEvents.EventGroupEnum.GROUP_MGMT_CHANGE)
        .thenReturn(IEvents.EventGroupEnum.GROUP_MGMT_CHANGE);
    PowerMockito.when(e.getFlag()).thenReturn(4).thenReturn(8);

    // Act
    final Class<?> classUnderTest =
        Reflector.forName(
            "com.firstrain.frapi.service.filters.EventsDedupingFilter$EntityMTEvents$EventDetails");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod(
            "add", Reflector.forName("com.firstrain.frapi.events.IEvents"));
    methodUnderTest.setAccessible(true);
    methodUnderTest.invoke(objectUnderTest, e);
  }
}