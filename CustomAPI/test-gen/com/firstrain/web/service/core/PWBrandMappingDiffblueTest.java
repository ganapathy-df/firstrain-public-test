package com.firstrain.web.service.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.diffblue.deeptestutils.Reflector;
import com.diffblue.deeptestutils.mock.DTUMemberMatcher;
import com.firstrain.web.domain.PwToken;
import java.lang.reflect.Method;
import java.util.HashMap;
import org.apache.poi.ss.usermodel.Cell;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.management.*"})
public class PWBrandMappingDiffblueTest {

  @Rule public final ExpectedException thrown = ExpectedException.none();
  @Rule public final Timeout globalTimeout = new Timeout(10_000);

  /* testedClasses: PWBrandMapping */
  // Test generated by Diffblue Deeptest.
  @PrepareForTest(Cell.class)
  @Test
  public void getCellValueInputNotNullOutputNotNull() throws Exception {

    // Arrange
    final PWBrandMapping objectUnderTest = createAndPopulatePWBrandMapping(); 
    final Cell cell = PowerMockito.mock(Cell.class);
    final Method getCellTypeMethod = DTUMemberMatcher.method(Cell.class, "getCellType");
    PowerMockito.doReturn(1).when(cell, getCellTypeMethod).withNoArguments();
    final Method getStringCellValueMethod =
        DTUMemberMatcher.method(Cell.class, "getStringCellValue");
    PowerMockito.doReturn("").when(cell, getStringCellValueMethod).withNoArguments();

    // Act
    final Class<?> classUnderTest =
        Reflector.forName("com.firstrain.web.service.core.PWBrandMapping");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod(
            "getCellValue", Reflector.forName("org.apache.poi.ss.usermodel.Cell"));
    methodUnderTest.setAccessible(true);
    final String retval = (String) methodUnderTest.invoke(objectUnderTest, cell);

    // Assert
    assertEquals("", retval);
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest(Cell.class)
  @Test
  public void getCellValueInputNotNullOutputNull() throws Exception {

    // Arrange
    final PWBrandMapping objectUnderTest = createAndPopulatePWBrandMapping(); 
    final Cell cell = PowerMockito.mock(Cell.class);
    final Method getCellTypeMethod = DTUMemberMatcher.method(Cell.class, "getCellType");
    PowerMockito.doReturn(7).when(cell, getCellTypeMethod).withNoArguments();

    // Act
    final Class<?> classUnderTest =
        Reflector.forName("com.firstrain.web.service.core.PWBrandMapping");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod(
            "getCellValue", Reflector.forName("org.apache.poi.ss.usermodel.Cell"));
    methodUnderTest.setAccessible(true);
    final String retval = (String) methodUnderTest.invoke(objectUnderTest, cell);

    // Assert
    assertNull(retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void getCellValueInputNullOutputNull() throws Exception {

    // Arrange
    final PWBrandMapping objectUnderTest = createAndPopulatePWBrandMapping(); 
    final Cell cell = null;

    // Act
    final Class<?> classUnderTest =
        Reflector.forName("com.firstrain.web.service.core.PWBrandMapping");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod(
            "getCellValue", Reflector.forName("org.apache.poi.ss.usermodel.Cell"));
    methodUnderTest.setAccessible(true);
    final String retval = (String) methodUnderTest.invoke(objectUnderTest, cell);

    // Assert
    assertNull(retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void getPwBrandInitialsInputNotNullOutputNull() {

    // Arrange
    final PWBrandMapping objectUnderTest = new PWBrandMapping();
    Reflector.setField(objectUnderTest, "pwBrandFilePath", null);
    final HashMap<String, String> hashMap = new HashMap<String, String>();
    Reflector.setField(objectUnderTest, "pwKeyVsBrandInitials", hashMap);
    Reflector.setField(objectUnderTest, "pwKeyVsBrand", null);
    Reflector.setField(objectUnderTest, "loadConfigurationComponentByExternalUrl", null);
    final HashMap<String, String> hashMap1 = new HashMap<String, String>();
    Reflector.setField(objectUnderTest, "pwKeyMapping", hashMap1);
    objectUnderTest.setFilePath(null);
    objectUnderTest.setLastModifiedTime(0L);
    final String key = "@@@@@@@@@@";

    // Act
    final String retval = objectUnderTest.getPwBrandInitials(key);

    // Assert
    assertNull(retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void populatePwTokenMapInputNullNotNullPositive0OutputVoid() throws Exception {

    // Arrange
    final PWBrandMapping objectUnderTest = createAndPopulatePWBrandMapping(); 
    final String pwEntity = null;
    final String pwToken = "!!";
    final int group = 1_367_392_254;
    final HashMap<String, PwToken> pwTokenMap = new HashMap<String, PwToken>();

    // Act
    final Class<?> classUnderTest =
        Reflector.forName("com.firstrain.web.service.core.PWBrandMapping");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod(
            "populatePwTokenMap",
            Reflector.forName("java.lang.String"),
            Reflector.forName("java.lang.String"),
            Reflector.forName("int"),
            Reflector.forName("java.util.Map"));
    methodUnderTest.setAccessible(true);
    methodUnderTest.invoke(objectUnderTest, pwEntity, pwToken, group, pwTokenMap);
  }
 
  private PWBrandMapping createAndPopulatePWBrandMapping() { 
    final PWBrandMapping objectUnderTest = new PWBrandMapping(); 
    Reflector.setField(objectUnderTest, "pwBrandFilePath", null); 
    Reflector.setField(objectUnderTest, "pwKeyVsBrandInitials", null); 
    Reflector.setField(objectUnderTest, "pwKeyVsBrand", null); 
    Reflector.setField(objectUnderTest, "loadConfigurationComponentByExternalUrl", null); 
    Reflector.setField(objectUnderTest, "pwKeyMapping", null); 
    objectUnderTest.setFilePath(null); 
    objectUnderTest.setLastModifiedTime(0L); 
    return objectUnderTest; 
  } 
}