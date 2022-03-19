package com.firstrain.frapi.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.AdditionalMatchers.or;
import static org.mockito.Matchers.isA;
import static org.mockito.Matchers.isNull;

import com.diffblue.deeptestutils.Reflector;
import com.firstrain.frapi.events.IEvents;
import com.firstrain.frapi.service.EventsFilter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class DateOrderEventComparatorDiffblueTest {

  @Rule public final ExpectedException thrown = ExpectedException.none();
  @Rule public final Timeout globalTimeout = new Timeout(10_000);

  /* testedClasses: DateOrderEventComparator */
  // Test generated by Diffblue Deeptest.
  @PrepareForTest({Date.class, IEvents.class})
  @Test
  public void compareInputNotNullNotNullOutputNegative() throws Exception {

    // Arrange
    final DateOrderEventComparator objectUnderTest = new DateOrderEventComparator();
    final IEvents o1 = PowerMockito.mock(IEvents.class);
    final Date date = PowerMockito.mock(Date.class);
    Reflector.setField(date, "fastTime", 0L);
    PowerMockito.when(o1.getDate()).thenReturn(date);
    final IEvents o2 = PowerMockito.mock(IEvents.class);
    final Date date1 = PowerMockito.mock(Date.class);
    PowerMockito.when(date1.compareTo(or(isA(Date.class), isNull(Date.class)))).thenReturn(-1);
    Reflector.setField(date1, "fastTime", 0L);
    PowerMockito.when(o2.getDate()).thenReturn(date1);

    // Act
    final int retval = objectUnderTest.compare(o1, o2);

    // Assert
    assertEquals(-1, retval);
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest({Date.class, IEvents.class})
  @Test
  public void compareInputNotNullNotNullOutputZero() throws Exception {

    // Arrange
    final DateOrderEventComparator objectUnderTest = new DateOrderEventComparator();
    final IEvents o1 = PowerMockito.mock(IEvents.class);
    final Date date = PowerMockito.mock(Date.class);
    Reflector.setField(date, "fastTime", 0L);
    PowerMockito.when(o1.getDate()).thenReturn(date);
    PowerMockito.when(o1.getEventId()).thenReturn(0);
    final IEvents o2 = PowerMockito.mock(IEvents.class);
    final Date date1 = PowerMockito.mock(Date.class);
    PowerMockito.when(date1.compareTo(or(isA(Date.class), isNull(Date.class)))).thenReturn(0);
    Reflector.setField(date1, "fastTime", 0L);
    PowerMockito.when(o2.getDate()).thenReturn(date1);
    PowerMockito.when(o2.getEventId()).thenReturn(0);

    // Act
    final int retval = objectUnderTest.compare(o1, o2);

    // Assert
    assertEquals(0, retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void constructorInputZeroFalseNullOutputVoid() {

    // Arrange
    final int size = 0;
    final boolean applySignalFilter = false;
    final EventsFilter additionalBlacklist = null;

    // Act, creating object to test constructor
    final MultiEntityEventsFilter objectUnderTest =
        new MultiEntityEventsFilter(size, applySignalFilter, additionalBlacklist);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void constructorInputZeroFalseOutputVoid() {

    // Arrange
    final int size = 0;
    final boolean applySignalFilter = false;

    // Act, creating object to test constructor
    final MultiEntityEventsFilter objectUnderTest =
        new MultiEntityEventsFilter(size, applySignalFilter);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void constructorInputZeroOutputVoid() {

    // Arrange
    final int size = 0;

    // Act, creating object to test constructor
    final MultiEntityEventsFilter objectUnderTest = new MultiEntityEventsFilter(size);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void insertIntoInputNullNullOutputNullPointerException() throws Throwable {

    // Arrange
    final IEvents e = null;
    final HashMap groupedQueues = null;

    // Act
    thrown.expect(NullPointerException.class);
    try {
      final Class<?> classUnderTest =
          Reflector.forName("com.firstrain.frapi.service.impl.MultiEntityEventsFilter");
      final Method methodUnderTest =
          classUnderTest.getDeclaredMethod(
              "insertInto",
              Reflector.forName("com.firstrain.frapi.events.IEvents"),
              Reflector.forName("java.util.HashMap"));
      methodUnderTest.setAccessible(true);
      methodUnderTest.invoke(null, e, groupedQueues);
    } catch (InvocationTargetException ex) {
      throw ex.getCause();
    }

    // Method is not expected to return due to exception thrown
  }
}