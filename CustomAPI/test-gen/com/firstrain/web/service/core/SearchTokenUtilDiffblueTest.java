package com.firstrain.web.service.core;

import static org.junit.Assert.assertEquals;

import com.diffblue.deeptestutils.Reflector;
import java.lang.reflect.Method;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;

public class SearchTokenUtilDiffblueTest {

  @Rule public final ExpectedException thrown = ExpectedException.none();
  @Rule public final Timeout globalTimeout = new Timeout(10_000);

  /* testedClasses: SearchTokenUtil */
  // Test generated by Diffblue Deeptest.

  @Test
  public void induceSearchTokenNameInputNullOutputNotNull() throws Exception {

    // Arrange
    final SearchTokenUtil objectUnderTest =
        (SearchTokenUtil) Reflector.getInstance("com.firstrain.web.service.core.SearchTokenUtil");
    final String name = null;

    // Act
    final Class<?> classUnderTest =
        Reflector.forName("com.firstrain.web.service.core.SearchTokenUtil");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod(
            "induceSearchTokenName", Reflector.forName("java.lang.String"));
    methodUnderTest.setAccessible(true);
    final String retval = (String) methodUnderTest.invoke(objectUnderTest, name);

    // Assert
    assertEquals("", retval);
  }
}
