package com.firstrain.frapi.service.filters;

import static org.junit.Assert.assertEquals;

import com.diffblue.deeptestutils.Reflector;
import com.firstrain.frapi.events.IEvents;
import com.firstrain.frapi.service.filters.Selectors.DepartureSelector;
import com.firstrain.frapi.service.filters.Selectors.GroupSelector;
import com.firstrain.frapi.service.filters.Selectors.HireSelector;
import com.firstrain.frapi.service.filters.Selectors.InternalTurnoverSelector;
import com.firstrain.frapi.service.filters.Selectors.LowRankedInternalMgmtTurnoverSelector;
import com.firstrain.frapi.service.filters.Selectors.NonexecTurnoverSelector;
import com.firstrain.frapi.service.filters.Selectors.StockPriceChangeSelector;
import com.firstrain.frapi.service.filters.Selectors.TypeSelector;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class Selectors_DepartureSelectorDiffblueTest {

  @Rule public final ExpectedException thrown = ExpectedException.none();
  @Rule public final Timeout globalTimeout = new Timeout(10_000);

  /* testedClasses: Selectors_DepartureSelector */
  // Test generated by Diffblue Deeptest.

  @Test
  public void constructorInputZeroOutputVoid() {

    // Arrange
    final double threshold = 0.0;

    // Act, creating object to test constructor
    final StockPriceChangeSelector objectUnderTest = new StockPriceChangeSelector(threshold);
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest(IEvents.class)
  @Test
  public void isSelectedInputNotNullOutputFalse() throws Exception {

    // Arrange
    final DepartureSelector objectUnderTest = new DepartureSelector();
    final IEvents e = PowerMockito.mock(IEvents.class);
    PowerMockito.when(e.getEventGroup()).thenReturn(IEvents.EventGroupEnum.GROUP_WEB_VOLUME);

    // Act
    final boolean retval = objectUnderTest.isSelected(e);

    // Assert
    assertEquals(false, retval);
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest(IEvents.class)
  @Test
  public void isSelectedInputNotNullOutputFalse2() throws Exception {

    // Arrange
    final DepartureSelector objectUnderTest = new DepartureSelector();
    final IEvents e = PowerMockito.mock(IEvents.class);
    PowerMockito.when(e.getEventGroup()).thenReturn(IEvents.EventGroupEnum.GROUP_MGMT_CHANGE);
    PowerMockito.when(e.getFlag()).thenReturn(0);

    // Act
    final boolean retval = objectUnderTest.isSelected(e);

    // Assert
    assertEquals(false, retval);
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest(IEvents.class)
  @Test
  public void isSelectedInputNotNullOutputFalse3() throws Exception {

    // Arrange
    final GroupSelector objectUnderTest =
        (GroupSelector)
            Reflector.getInstance("com.firstrain.frapi.service.filters.Selectors$GroupSelector");
    Reflector.setField(objectUnderTest, "group", null);
    final IEvents e = PowerMockito.mock(IEvents.class);
    PowerMockito.when(e.getEventGroup())
        .thenReturn(IEvents.EventGroupEnum.GROUP_SHELF_REGISTRATIONS);

    // Act
    final boolean retval = objectUnderTest.isSelected(e);

    // Assert
    assertEquals(false, retval);
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest(IEvents.class)
  @Test
  public void isSelectedInputNotNullOutputFalse4() throws Exception {

    // Arrange
    final HireSelector objectUnderTest = new HireSelector();
    final IEvents e = PowerMockito.mock(IEvents.class);
    PowerMockito.when(e.getEventGroup()).thenReturn(IEvents.EventGroupEnum.GROUP_MGMT_CHANGE);
    PowerMockito.when(e.getFlag()).thenReturn(0);

    // Act
    final boolean retval = objectUnderTest.isSelected(e);

    // Assert
    assertEquals(false, retval);
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest(IEvents.class)
  @Test
  public void isSelectedInputNotNullOutputFalse5() throws Exception {

    // Arrange
    final HireSelector objectUnderTest = new HireSelector();
    final IEvents e = PowerMockito.mock(IEvents.class);
    PowerMockito.when(e.getEventGroup()).thenReturn(IEvents.EventGroupEnum.GROUP_WEB_VOLUME);

    // Act
    final boolean retval = objectUnderTest.isSelected(e);

    // Assert
    assertEquals(false, retval);
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest(IEvents.class)
  @Test
  public void isSelectedInputNotNullOutputFalse6() throws Exception {

    // Arrange
    final InternalTurnoverSelector objectUnderTest = new InternalTurnoverSelector();
    final IEvents e = PowerMockito.mock(IEvents.class);
    PowerMockito.when(e.getEventGroup()).thenReturn(IEvents.EventGroupEnum.GROUP_MGMT_CHANGE);
    PowerMockito.when(e.getFlag()).thenReturn(0);

    // Act
    final boolean retval = objectUnderTest.isSelected(e);

    // Assert
    assertEquals(false, retval);
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest(IEvents.class)
  @Test
  public void isSelectedInputNotNullOutputFalse7() throws Exception {

    // Arrange
    final InternalTurnoverSelector objectUnderTest = new InternalTurnoverSelector();
    final IEvents e = PowerMockito.mock(IEvents.class);
    PowerMockito.when(e.getEventGroup()).thenReturn(IEvents.EventGroupEnum.GROUP_WEB_VOLUME);

    // Act
    final boolean retval = objectUnderTest.isSelected(e);

    // Assert
    assertEquals(false, retval);
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest(IEvents.class)
  @Test
  public void isSelectedInputNotNullOutputFalse8() throws Exception {

    // Arrange
    final LowRankedInternalMgmtTurnoverSelector objectUnderTest =
        new LowRankedInternalMgmtTurnoverSelector();
    final IEvents e = PowerMockito.mock(IEvents.class);
    PowerMockito.when(e.getEventGroup()).thenReturn(null);

    // Act
    final boolean retval = objectUnderTest.isSelected(e);

    // Assert
    assertEquals(false, retval);
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest(IEvents.class)
  @Test
  public void isSelectedInputNotNullOutputFalse9() throws Exception {

    // Arrange
    final NonexecTurnoverSelector objectUnderTest = new NonexecTurnoverSelector();
    final IEvents e = PowerMockito.mock(IEvents.class);
    PowerMockito.when(e.getEventGroup()).thenReturn(null);

    // Act
    final boolean retval = objectUnderTest.isSelected(e);

    // Assert
    assertEquals(false, retval);
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest(IEvents.class)
  @Test
  public void isSelectedInputNotNullOutputFalse10() throws Exception {

    // Arrange
    final LowRankedInternalMgmtTurnoverSelector objectUnderTest =
        new LowRankedInternalMgmtTurnoverSelector();
    final IEvents e = PowerMockito.mock(IEvents.class);
    PowerMockito.when(e.getEventGroup()).thenReturn(IEvents.EventGroupEnum.GROUP_MGMT_CHANGE);
    PowerMockito.when(e.getFlag()).thenReturn(0);

    // Act
    final boolean retval = objectUnderTest.isSelected(e);

    // Assert
    assertEquals(false, retval);
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest(IEvents.class)
  @Test
  public void isSelectedInputNotNullOutputTrue() throws Exception {

    // Arrange
    final DepartureSelector objectUnderTest = new DepartureSelector();
    final IEvents e = PowerMockito.mock(IEvents.class);
    PowerMockito.when(e.getEventGroup()).thenReturn(IEvents.EventGroupEnum.GROUP_MGMT_CHANGE);
    PowerMockito.when(e.getFlag()).thenReturn(8);

    // Act
    final boolean retval = objectUnderTest.isSelected(e);

    // Assert
    assertEquals(true, retval);
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest(IEvents.class)
  @Test
  public void isSelectedInputNotNullOutputTrue2() throws Exception {

    // Arrange
    final GroupSelector objectUnderTest =
        (GroupSelector)
            Reflector.getInstance("com.firstrain.frapi.service.filters.Selectors$GroupSelector");
    Reflector.setField(objectUnderTest, "group", null);
    final IEvents e = PowerMockito.mock(IEvents.class);
    PowerMockito.when(e.getEventGroup()).thenReturn(null);

    // Act
    final boolean retval = objectUnderTest.isSelected(e);

    // Assert
    assertEquals(true, retval);
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest(IEvents.class)
  @Test
  public void isSelectedInputNotNullOutputTrue3() throws Exception {

    // Arrange
    final HireSelector objectUnderTest = new HireSelector();
    final IEvents e = PowerMockito.mock(IEvents.class);
    PowerMockito.when(e.getEventGroup()).thenReturn(IEvents.EventGroupEnum.GROUP_MGMT_CHANGE);
    PowerMockito.when(e.getFlag()).thenReturn(4);

    // Act
    final boolean retval = objectUnderTest.isSelected(e);

    // Assert
    assertEquals(true, retval);
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest(IEvents.class)
  @Test
  public void isSelectedInputNotNullOutputTrue4() throws Exception {

    // Arrange
    final InternalTurnoverSelector objectUnderTest = new InternalTurnoverSelector();
    final IEvents e = PowerMockito.mock(IEvents.class);
    PowerMockito.when(e.getEventGroup()).thenReturn(IEvents.EventGroupEnum.GROUP_MGMT_CHANGE);
    PowerMockito.when(e.getFlag()).thenReturn(2);

    // Act
    final boolean retval = objectUnderTest.isSelected(e);

    // Assert
    assertEquals(true, retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void isSelectedInputNullOutputFalse() {

    // Arrange
    final IEvents.EventTypeEnum[] eventTypeEnumArray = {};
    final TypeSelector objectUnderTest = new TypeSelector(eventTypeEnumArray);
    final IEvents e = null;

    // Act
    final boolean retval = objectUnderTest.isSelected(e);

    // Assert
    assertEquals(false, retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void isSelectedInputNullOutputNullPointerException() {

    // Arrange
    final StockPriceChangeSelector objectUnderTest = new StockPriceChangeSelector(0.0, false);
    final IEvents e = null;

    // Act
    thrown.expect(NullPointerException.class);
    objectUnderTest.isSelected(e);

    // Method is not expected to return due to exception thrown
  }
}