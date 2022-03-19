package com.firstrain.frapi.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.diffblue.deeptestutils.Reflector;
import java.lang.reflect.Method;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;

public class EventObjDiffblueTest {

  @Rule public final ExpectedException thrown = ExpectedException.none();
  @Rule public final Timeout globalTimeout = new Timeout(10_000);

  /* testedClasses: EventObj */
  // Test generated by Diffblue Deeptest.

  @Test
  public void get8KItemNoInputPositiveOutputNotNull() throws Exception {

    // Arrange
    final int eventType = 401;

    // Act
    final Class<?> classUnderTest = Reflector.forName("com.firstrain.frapi.events.EventObj");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod("get8KItemNo", Reflector.forName("int"));
    methodUnderTest.setAccessible(true);
    final String retval = (String) methodUnderTest.invoke(null, eventType);

    // Assert
    assertEquals("1.03", retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void get8KItemNoInputPositiveOutputNotNull2() throws Exception {

    // Arrange
    final int eventType = 406;

    // Act
    final Class<?> classUnderTest = Reflector.forName("com.firstrain.frapi.events.EventObj");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod("get8KItemNo", Reflector.forName("int"));
    methodUnderTest.setAccessible(true);
    final String retval = (String) methodUnderTest.invoke(null, eventType);

    // Assert
    assertEquals("2.02", retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void get8KItemNoInputPositiveOutputNotNull3() throws Exception {

    // Arrange
    final int eventType = 415;

    // Act
    final Class<?> classUnderTest = Reflector.forName("com.firstrain.frapi.events.EventObj");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod("get8KItemNo", Reflector.forName("int"));
    methodUnderTest.setAccessible(true);
    final String retval = (String) methodUnderTest.invoke(null, eventType);

    // Assert
    assertEquals("3.02", retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void get8KItemNoInputPositiveOutputNotNull4() throws Exception {

    // Arrange
    final int eventType = 417;

    // Act
    final Class<?> classUnderTest = Reflector.forName("com.firstrain.frapi.events.EventObj");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod("get8KItemNo", Reflector.forName("int"));
    methodUnderTest.setAccessible(true);
    final String retval = (String) methodUnderTest.invoke(null, eventType);

    // Assert
    assertEquals("3.03", retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void get8KItemNoInputPositiveOutputNotNull5() throws Exception {

    // Arrange
    final int eventType = 413;

    // Act
    final Class<?> classUnderTest = Reflector.forName("com.firstrain.frapi.events.EventObj");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod("get8KItemNo", Reflector.forName("int"));
    methodUnderTest.setAccessible(true);
    final String retval = (String) methodUnderTest.invoke(null, eventType);

    // Assert
    assertEquals("7.01", retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void get8KItemNoInputPositiveOutputNotNull6() throws Exception {

    // Arrange
    final int eventType = 426;

    // Act
    final Class<?> classUnderTest = Reflector.forName("com.firstrain.frapi.events.EventObj");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod("get8KItemNo", Reflector.forName("int"));
    methodUnderTest.setAccessible(true);
    final String retval = (String) methodUnderTest.invoke(null, eventType);

    // Assert
    assertEquals("8.01", retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void get8KItemNoInputPositiveOutputNotNull7() throws Exception {

    // Arrange
    final int eventType = 408;

    // Act
    final Class<?> classUnderTest = Reflector.forName("com.firstrain.frapi.events.EventObj");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod("get8KItemNo", Reflector.forName("int"));
    methodUnderTest.setAccessible(true);
    final String retval = (String) methodUnderTest.invoke(null, eventType);

    // Assert
    assertEquals("2.03", retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void get8KItemNoInputPositiveOutputNotNull8() throws Exception {

    // Arrange
    final int eventType = 427;

    // Act
    final Class<?> classUnderTest = Reflector.forName("com.firstrain.frapi.events.EventObj");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod("get8KItemNo", Reflector.forName("int"));
    methodUnderTest.setAccessible(true);
    final String retval = (String) methodUnderTest.invoke(null, eventType);

    // Assert
    assertEquals("5.02", retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void get8KItemNoInputPositiveOutputNotNull9() throws Exception {

    // Arrange
    final int eventType = 403;

    // Act
    final Class<?> classUnderTest = Reflector.forName("com.firstrain.frapi.events.EventObj");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod("get8KItemNo", Reflector.forName("int"));
    methodUnderTest.setAccessible(true);
    final String retval = (String) methodUnderTest.invoke(null, eventType);

    // Assert
    assertEquals("1.02", retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void get8KItemNoInputPositiveOutputNotNull10() throws Exception {

    // Arrange
    final int eventType = 422;

    // Act
    final Class<?> classUnderTest = Reflector.forName("com.firstrain.frapi.events.EventObj");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod("get8KItemNo", Reflector.forName("int"));
    methodUnderTest.setAccessible(true);
    final String retval = (String) methodUnderTest.invoke(null, eventType);

    // Assert
    assertEquals("5.06", retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void get8KItemNoInputPositiveOutputNotNull11() throws Exception {

    // Arrange
    final int eventType = 425;

    // Act
    final Class<?> classUnderTest = Reflector.forName("com.firstrain.frapi.events.EventObj");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod("get8KItemNo", Reflector.forName("int"));
    methodUnderTest.setAccessible(true);
    final String retval = (String) methodUnderTest.invoke(null, eventType);

    // Assert
    assertEquals("6.03", retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void get8KItemNoInputPositiveOutputNotNull12() throws Exception {

    // Arrange
    final int eventType = 404;

    // Act
    final Class<?> classUnderTest = Reflector.forName("com.firstrain.frapi.events.EventObj");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod("get8KItemNo", Reflector.forName("int"));
    methodUnderTest.setAccessible(true);
    final String retval = (String) methodUnderTest.invoke(null, eventType);

    // Assert
    assertEquals("2.01", retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void get8KItemNoInputPositiveOutputNotNull13() throws Exception {

    // Arrange
    final int eventType = 419;

    // Act
    final Class<?> classUnderTest = Reflector.forName("com.firstrain.frapi.events.EventObj");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod("get8KItemNo", Reflector.forName("int"));
    methodUnderTest.setAccessible(true);
    final String retval = (String) methodUnderTest.invoke(null, eventType);

    // Assert
    assertEquals("5.01", retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void get8KItemNoInputPositiveOutputNotNull14() throws Exception {

    // Arrange
    final int eventType = 420;

    // Act
    final Class<?> classUnderTest = Reflector.forName("com.firstrain.frapi.events.EventObj");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod("get8KItemNo", Reflector.forName("int"));
    methodUnderTest.setAccessible(true);
    final String retval = (String) methodUnderTest.invoke(null, eventType);

    // Assert
    assertEquals("5.03", retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void get8KItemNoInputPositiveOutputNotNull15() throws Exception {

    // Arrange
    final int eventType = 424;

    // Act
    final Class<?> classUnderTest = Reflector.forName("com.firstrain.frapi.events.EventObj");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod("get8KItemNo", Reflector.forName("int"));
    methodUnderTest.setAccessible(true);
    final String retval = (String) methodUnderTest.invoke(null, eventType);

    // Assert
    assertEquals("6.02", retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void get8KItemNoInputPositiveOutputNotNull16() throws Exception {

    // Arrange
    final int eventType = 416;

    // Act
    final Class<?> classUnderTest = Reflector.forName("com.firstrain.frapi.events.EventObj");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod("get8KItemNo", Reflector.forName("int"));
    methodUnderTest.setAccessible(true);
    final String retval = (String) methodUnderTest.invoke(null, eventType);

    // Assert
    assertEquals("4.02", retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void get8KItemNoInputPositiveOutputNotNull17() throws Exception {

    // Arrange
    final int eventType = 423;

    // Act
    final Class<?> classUnderTest = Reflector.forName("com.firstrain.frapi.events.EventObj");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod("get8KItemNo", Reflector.forName("int"));
    methodUnderTest.setAccessible(true);
    final String retval = (String) methodUnderTest.invoke(null, eventType);

    // Assert
    assertEquals("6.01", retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void get8KItemNoInputPositiveOutputNotNull18() throws Exception {

    // Arrange
    final int eventType = 421;

    // Act
    final Class<?> classUnderTest = Reflector.forName("com.firstrain.frapi.events.EventObj");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod("get8KItemNo", Reflector.forName("int"));
    methodUnderTest.setAccessible(true);
    final String retval = (String) methodUnderTest.invoke(null, eventType);

    // Assert
    assertEquals("5.05", retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void get8KItemNoInputPositiveOutputNotNull19() throws Exception {

    // Arrange
    final int eventType = 418;

    // Act
    final Class<?> classUnderTest = Reflector.forName("com.firstrain.frapi.events.EventObj");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod("get8KItemNo", Reflector.forName("int"));
    methodUnderTest.setAccessible(true);
    final String retval = (String) methodUnderTest.invoke(null, eventType);

    // Assert
    assertEquals("4.01", retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void get8KItemNoInputPositiveOutputNotNull20() throws Exception {

    // Arrange
    final int eventType = 412;

    // Act
    final Class<?> classUnderTest = Reflector.forName("com.firstrain.frapi.events.EventObj");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod("get8KItemNo", Reflector.forName("int"));
    methodUnderTest.setAccessible(true);
    final String retval = (String) methodUnderTest.invoke(null, eventType);

    // Assert
    assertEquals("6.04", retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void get8KItemNoInputPositiveOutputNotNull21() throws Exception {

    // Arrange
    final int eventType = 411;

    // Act
    final Class<?> classUnderTest = Reflector.forName("com.firstrain.frapi.events.EventObj");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod("get8KItemNo", Reflector.forName("int"));
    methodUnderTest.setAccessible(true);
    final String retval = (String) methodUnderTest.invoke(null, eventType);

    // Assert
    assertEquals("5.04", retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void get8KItemNoInputPositiveOutputNotNull22() throws Exception {

    // Arrange
    final int eventType = 414;

    // Act
    final Class<?> classUnderTest = Reflector.forName("com.firstrain.frapi.events.EventObj");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod("get8KItemNo", Reflector.forName("int"));
    methodUnderTest.setAccessible(true);
    final String retval = (String) methodUnderTest.invoke(null, eventType);

    // Assert
    assertEquals("6.05", retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void get8KItemNoInputPositiveOutputNotNull23() throws Exception {

    // Arrange
    final int eventType = 410;

    // Act
    final Class<?> classUnderTest = Reflector.forName("com.firstrain.frapi.events.EventObj");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod("get8KItemNo", Reflector.forName("int"));
    methodUnderTest.setAccessible(true);
    final String retval = (String) methodUnderTest.invoke(null, eventType);

    // Assert
    assertEquals("2.05", retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void get8KItemNoInputPositiveOutputNotNull24() throws Exception {

    // Arrange
    final int eventType = 409;

    // Act
    final Class<?> classUnderTest = Reflector.forName("com.firstrain.frapi.events.EventObj");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod("get8KItemNo", Reflector.forName("int"));
    methodUnderTest.setAccessible(true);
    final String retval = (String) methodUnderTest.invoke(null, eventType);

    // Assert
    assertEquals("2.06", retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void get8KItemNoInputPositiveOutputNotNull25() throws Exception {

    // Arrange
    final int eventType = 402;

    // Act
    final Class<?> classUnderTest = Reflector.forName("com.firstrain.frapi.events.EventObj");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod("get8KItemNo", Reflector.forName("int"));
    methodUnderTest.setAccessible(true);
    final String retval = (String) methodUnderTest.invoke(null, eventType);

    // Assert
    assertEquals("3.01", retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void get8KItemNoInputPositiveOutputNotNull26() throws Exception {

    // Arrange
    final int eventType = 405;

    // Act
    final Class<?> classUnderTest = Reflector.forName("com.firstrain.frapi.events.EventObj");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod("get8KItemNo", Reflector.forName("int"));
    methodUnderTest.setAccessible(true);
    final String retval = (String) methodUnderTest.invoke(null, eventType);

    // Assert
    assertEquals("1.01", retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void get8KItemNoInputPositiveOutputNotNull27() throws Exception {

    // Arrange
    final int eventType = 407;

    // Act
    final Class<?> classUnderTest = Reflector.forName("com.firstrain.frapi.events.EventObj");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod("get8KItemNo", Reflector.forName("int"));
    methodUnderTest.setAccessible(true);
    final String retval = (String) methodUnderTest.invoke(null, eventType);

    // Assert
    assertEquals("2.04", retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void get8KItemNoInputZeroOutputNull() throws Exception {

    // Arrange
    final int eventType = 0;

    // Act
    final Class<?> classUnderTest = Reflector.forName("com.firstrain.frapi.events.EventObj");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod("get8KItemNo", Reflector.forName("int"));
    methodUnderTest.setAccessible(true);
    final String retval = (String) methodUnderTest.invoke(null, eventType);

    // Assert
    assertNull(retval);
  }
}