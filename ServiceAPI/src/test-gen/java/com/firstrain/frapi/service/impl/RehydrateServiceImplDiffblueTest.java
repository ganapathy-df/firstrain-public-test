package com.firstrain.frapi.service.impl;

import static org.junit.Assert.assertNull;
import static org.mockito.AdditionalMatchers.or;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyShort;
import static org.mockito.Matchers.isA;
import static org.mockito.Matchers.isNull;

import com.diffblue.deeptestutils.Reflector;
import com.diffblue.deeptestutils.mock.DTUMemberMatcher;
import com.firstrain.frapi.domain.EntityDetailSpec;
import com.firstrain.frapi.pojo.wrapper.DocumentSet;
import com.firstrain.frapi.pojo.wrapper.TweetSet;
import com.firstrain.frapi.service.EntityBaseService;
import com.firstrain.frapi.service.EntityProcessingService;
import com.firstrain.utils.object.PerfActivityType;
import com.firstrain.utils.object.PerfMonitor;
import java.lang.reflect.Method;
import java.util.Collection;
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
public class RehydrateServiceImplDiffblueTest {

  @Rule public final ExpectedException thrown = ExpectedException.none();
  @Rule public final Timeout globalTimeout = new Timeout(10_000);

  /* testedClasses: RehydrateServiceImpl */
  // Test generated by Diffblue Deeptest.
  @PrepareForTest({
    EntityProcessingService.class, EntityBaseService.class,
    RehydrateServiceImpl.class, PerfMonitor.class
  })
  @Test
  public void getDocumentViewInputZeroNullZeroOutputNull() throws Exception {

    // Setup mocks
    final RehydrateServiceImpl objectUnderTest = arrangeData();
    arrangeDoGetDocumentViewAndAssert(objectUnderTest);
  }

  private RehydrateServiceImpl arrangeData() throws Exception {
      PowerMockito.mockStatic(PerfMonitor.class);
      
      // Arrange
      final RehydrateServiceImpl objectUnderTest = new RehydrateServiceImpl();
      final EntityProcessingService entityProcessingService =
          PowerMockito.mock(EntityProcessingService.class);
      PowerMockito.when(
              entityProcessingService.getDocumentSetWithId(
                  or(isA(DocumentSet.class), isNull(DocumentSet.class))))
          .thenReturn(null);
      Reflector.setField(objectUnderTest, "entityProcessingService", entityProcessingService);
      Reflector.setField(objectUnderTest, "LOG", null);
      final EntityBaseService entityBaseService = PowerMockito.mock(EntityBaseService.class);
      PowerMockito.when(
              entityBaseService.getDocDetails(
                  or(isA(List.class), isNull(List.class)),
                  or(isA(EntityDetailSpec.class), isNull(EntityDetailSpec.class)),
                  or(isA(Collection.class), isNull(Collection.class)),
                  anyShort()))
          .thenReturn(null);
      Reflector.setField(objectUnderTest, "entityBaseService", entityBaseService);
      return objectUnderTest;
  }

  private void arrangeDoGetDocumentViewAndAssert(final RehydrateServiceImpl objectUnderTest) throws Exception {
      final long frUserId = 0L;
      final List<Long> docIds = null;
      final short industryClassificationId = (short) 0;
      final EntityDetailSpec entityDetailSpec = PowerMockito.mock(EntityDetailSpec.class);
      Reflector.setField(entityDetailSpec, "attachGroupInfo", false);
      PowerMockito.whenNew(EntityDetailSpec.class).withNoArguments().thenReturn(entityDetailSpec);
      final Method recordStatsMethod =
          DTUMemberMatcher.method(
              PerfMonitor.class, "recordStats", long.class, PerfActivityType.class, String.class);
      PowerMockito.doReturn(0L)
          .when(PerfMonitor.class, recordStatsMethod)
          .withArguments(
              anyLong(),
              or(isA(PerfActivityType.class), isNull(PerfActivityType.class)),
              or(isA(String.class), isNull(String.class)));
      PowerMockito.doReturn(0L).when(PerfMonitor.class);
      PerfMonitor.currentTime();
      
      // Act
      final DocumentSet retval =
          objectUnderTest.getDocumentView(frUserId, docIds, industryClassificationId);
      
      // Assert
      assertNull(retval);
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest({
    EntityProcessingService.class, EntityBaseService.class,
    RehydrateServiceImpl.class, PerfMonitor.class
  })
  @Test
  public void getTweetViewInputZeroNullOutputNull() throws Exception {

    // Setup mocks
    PowerMockito.mockStatic(PerfMonitor.class);

    // Arrange
    final RehydrateServiceImpl objectUnderTest = new RehydrateServiceImpl();
    final EntityProcessingService entityProcessingService =
        PowerMockito.mock(EntityProcessingService.class);
    PowerMockito.when(
            entityProcessingService.getTweetsWithId(
                or(isA(TweetSet.class), isNull(TweetSet.class))))
        .thenReturn(null);
    Reflector.setField(objectUnderTest, "entityProcessingService", entityProcessingService);
    Reflector.setField(objectUnderTest, "LOG", null);
    final EntityBaseService entityBaseService = PowerMockito.mock(EntityBaseService.class);
    PowerMockito.when(
            entityBaseService.getTweetDetails(
                or(isA(List.class), isNull(List.class)),
                or(isA(EntityDetailSpec.class), isNull(EntityDetailSpec.class))))
        .thenReturn(null);
    Reflector.setField(objectUnderTest, "entityBaseService", entityBaseService);
    final long frUserId = 0L;
    final List<Long> tweetIds = null;
    final EntityDetailSpec entityDetailSpec = PowerMockito.mock(EntityDetailSpec.class);
    Reflector.setField(entityDetailSpec, "attachGroupInfo", false);
    PowerMockito.whenNew(EntityDetailSpec.class).withNoArguments().thenReturn(entityDetailSpec);
    final Method recordStatsMethod =
        DTUMemberMatcher.method(
            PerfMonitor.class, "recordStats", long.class, PerfActivityType.class, String.class);
    PowerMockito.doReturn(0L)
        .when(PerfMonitor.class, recordStatsMethod)
        .withArguments(
            anyLong(),
            or(isA(PerfActivityType.class), isNull(PerfActivityType.class)),
            or(isA(String.class), isNull(String.class)));
    PowerMockito.doReturn(0L).when(PerfMonitor.class);
    PerfMonitor.currentTime();

    // Act
    final TweetSet retval = objectUnderTest.getTweetView(frUserId, tweetIds);

    // Assert
    assertNull(retval);
  }
}
