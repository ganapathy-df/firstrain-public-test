package com.firstrain.frapi.domain;

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;

public class MonitorEmailDiffblueTest {

  @Rule public final ExpectedException thrown = ExpectedException.none();
  @Rule public final Timeout globalTimeout = new Timeout(10_000);

  /* testedClasses: MonitorEmail */
  // Test generated by Diffblue Deeptest.

  @Test
  public void hashCodeOutputPositive() {

    // Arrange
    final MonitorEmail objectUnderTest = new MonitorEmail();
    objectUnderTest.setTimeStamp(null);
    objectUnderTest.setEmailId("");
    objectUnderTest.setSubject(null);

    // Act
    final int retval = objectUnderTest.hashCode();

    // Assert
    assertEquals(31, retval);
  }
}