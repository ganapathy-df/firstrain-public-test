package com.firstrain.frapi.repository.impl;

import static org.junit.Assert.assertNull;

import com.diffblue.deeptestutils.Reflector;
import com.firstrain.frapi.config.ServiceException;
import java.util.Map;
import org.apache.log4j.Logger;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;

public class IndustryClassificationMapDiffblueTest {

  @Rule public final ExpectedException thrown = ExpectedException.none();
  @Rule public final Timeout globalTimeout = new Timeout(10_000);

  /* testedClasses: IndustryClassificationMap */
  // Test generated by Diffblue Deeptest.

  @Test
  public void getIndustryClassificationMapOutputNull() {

    // Arrange
    final IndustryClassificationMap objectUnderTest = new IndustryClassificationMap();
    Reflector.setField(objectUnderTest, "serviceConfig", null);
    Reflector.setField(objectUnderTest, "industryClassificationMap", null);
    Reflector.setField(objectUnderTest, "LOG", null);

    // Act
    final Map<Integer, Integer> retval = objectUnderTest.getIndustryClassificationMap();

    // Assert
    assertNull(retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void initOutputServiceException() throws Exception {

    // Arrange
    final IndustryClassificationMap objectUnderTest = new IndustryClassificationMap();
    Reflector.setField(objectUnderTest, "serviceConfig", null);
    Reflector.setField(objectUnderTest, "industryClassificationMap", null);
    final Logger logger = (Logger) Reflector.getInstance("org.apache.log4j.Logger");
    Reflector.setField(objectUnderTest, "LOG", logger);

    // Act
    thrown.expect(ServiceException.class);
    objectUnderTest.init();

    // Method is not expected to return due to exception thrown
  }
}
