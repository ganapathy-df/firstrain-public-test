package com.firstrain.web.service.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.AdditionalMatchers.or;
import static org.mockito.Matchers.isA;
import static org.mockito.Matchers.isNull;

import com.diffblue.deeptestutils.Reflector;
import com.diffblue.deeptestutils.mock.DTUMemberMatcher;
import com.firstrain.utils.FR_Loader;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
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
public class ExcelProcessingHelperServiceDiffblueTest {

  @Rule public final ExpectedException thrown = ExpectedException.none();
  @Rule public final Timeout globalTimeout = new Timeout(10_000);

  /* testedClasses: ExcelProcessingHelperService */
  // Test generated by Diffblue Deeptest.

  @Test
  public void getCXOForSectorInputNegativeOutputNull() throws Exception {

    // Arrange
    final ExcelProcessingHelperService objectUnderTest = new ExcelProcessingHelperService();
    final AtomicLong atomicLong =
        (AtomicLong) Reflector.getInstance("java.util.concurrent.atomic.AtomicLong");
    Reflector.setField(objectUnderTest, "sectorCxoModifiedTime", atomicLong);
    Reflector.setField(objectUnderTest, "sourceMetaDataModifiedTime", null);
    Reflector.setField(objectUnderTest, "httpClient", null);
    final AtomicLong atomicLong1 =
        (AtomicLong) Reflector.getInstance("java.util.concurrent.atomic.AtomicLong");
    Reflector.setField(objectUnderTest, "jobTitlesModifiedTime", atomicLong1);
    final HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
    hashMap.put(-318_242_815, null);
    objectUnderTest.setSectorVsCXO(hashMap);
    Reflector.setField(objectUnderTest, "SOURCE_METADATA_FILE", null);
    objectUnderTest.setJobVsTitles(null);
    Reflector.setField(objectUnderTest, "sourceMetaDataMap", null);
    final int sectorId = -318_234_624;

    // Act
    final String retval = objectUnderTest.getCXOForSector(sectorId);

    // Assert
    assertNull(retval);
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest({Logger.class, FR_Loader.class})
  @Test
  public void getFileFromPathInputNotNullNullOutputNullPointerException() throws Exception {

    // Setup mocks
    PowerMockito.mockStatic(FR_Loader.class);
    PowerMockito.mockStatic(Logger.class);

    // Arrange
    final ExcelProcessingHelperService objectUnderTest =
        (ExcelProcessingHelperService)
            Reflector.getInstance("com.firstrain.web.service.core.ExcelProcessingHelperService");
    Reflector.setField(objectUnderTest, "sectorCxoModifiedTime", null);
    Reflector.setField(objectUnderTest, "sourceMetaDataModifiedTime", null);
    Reflector.setField(objectUnderTest, "httpClient", null);
    Reflector.setField(objectUnderTest, "jobTitlesModifiedTime", null);
    Reflector.setField(objectUnderTest, "sectorVsCXO", null);
    Reflector.setField(objectUnderTest, "SOURCE_METADATA_FILE", null);
    final HashMap<String, String> hashMap = new HashMap<String, String>();
    Reflector.setField(objectUnderTest, "jobVsTitles", hashMap);
    Reflector.setField(objectUnderTest, "sourceMetaDataMap", null);
    final String fileName = "";
    final AtomicLong lastModifiedTime = null;
    final Method getLoggerMethod = DTUMemberMatcher.method(Logger.class, "getLogger", Class.class);
    PowerMockito.doReturn(null)
        .when(Logger.class, getLoggerMethod)
        .withArguments(or(isA(Class.class), isNull(Class.class)));
    final Method getResourceMethod =
        DTUMemberMatcher.method(FR_Loader.class, "getResource", String.class);
    PowerMockito.doReturn(null)
        .when(FR_Loader.class, getResourceMethod)
        .withArguments(or(isA(String.class), isNull(String.class)));

    // Act
    thrown.expect(NullPointerException.class);
    objectUnderTest.getFileFromPath(fileName, lastModifiedTime);

    // Method is not expected to return due to exception thrown
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void getJobVsTitlesOutputNull() {

    // Arrange
    final ExcelProcessingHelperService objectUnderTest = new ExcelProcessingHelperService();
    Reflector.setField(objectUnderTest, "sectorCxoModifiedTime", null);
    Reflector.setField(objectUnderTest, "sourceMetaDataModifiedTime", null);
    Reflector.setField(objectUnderTest, "httpClient", null);
    Reflector.setField(objectUnderTest, "jobTitlesModifiedTime", null);
    objectUnderTest.setSectorVsCXO(null);
    Reflector.setField(objectUnderTest, "SOURCE_METADATA_FILE", null);
    objectUnderTest.setJobVsTitles(null);
    Reflector.setField(objectUnderTest, "sourceMetaDataMap", null);

    // Act
    final Map<String, String> retval = objectUnderTest.getJobVsTitles();

    // Assert
    assertNull(retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void getSectorVsCXOOutputNull() {

    // Arrange
    final ExcelProcessingHelperService objectUnderTest = new ExcelProcessingHelperService();
    Reflector.setField(objectUnderTest, "sectorCxoModifiedTime", null);
    Reflector.setField(objectUnderTest, "sourceMetaDataModifiedTime", null);
    Reflector.setField(objectUnderTest, "httpClient", null);
    Reflector.setField(objectUnderTest, "jobTitlesModifiedTime", null);
    objectUnderTest.setSectorVsCXO(null);
    Reflector.setField(objectUnderTest, "SOURCE_METADATA_FILE", null);
    objectUnderTest.setJobVsTitles(null);
    Reflector.setField(objectUnderTest, "sourceMetaDataMap", null);

    // Act
    final Map<Integer, String> retval = objectUnderTest.getSectorVsCXO();

    // Assert
    assertNull(retval);
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest(StringUtils.class)
  @Test
  public void getSourceMetaDataInputNotNullOutputNotNull() throws Exception {

    // Setup mocks
    PowerMockito.mockStatic(StringUtils.class);

    // Arrange
    final ExcelProcessingHelperService objectUnderTest = new ExcelProcessingHelperService();
    final AtomicLong atomicLong =
        (AtomicLong) Reflector.getInstance("java.util.concurrent.atomic.AtomicLong");
    Reflector.setField(objectUnderTest, "sectorCxoModifiedTime", atomicLong);
    Reflector.setField(objectUnderTest, "sourceMetaDataModifiedTime", null);
    Reflector.setField(objectUnderTest, "httpClient", null);
    Reflector.setField(objectUnderTest, "jobTitlesModifiedTime", null);
    objectUnderTest.setSectorVsCXO(null);
    Reflector.setField(objectUnderTest, "SOURCE_METADATA_FILE", null);
    objectUnderTest.setJobVsTitles(null);
    final HashMap<String, String> hashMap = new HashMap<String, String>();
    Reflector.setField(objectUnderTest, "sourceMetaDataMap", hashMap);
    final String key = "\"!!!!!!#";
    final Method isEmptyMethod =
        DTUMemberMatcher.method(StringUtils.class, "isEmpty", String.class);
    PowerMockito.doReturn(true)
        .when(StringUtils.class, isEmptyMethod)
        .withArguments(or(isA(String.class), isNull(String.class)));

    // Act
    final String retval = objectUnderTest.getSourceMetaData(key);

    // Assert
    assertEquals("English", retval);
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest(StringUtils.class)
  @Test
  public void getSourceMetaDataInputNotNullOutputNull() throws Exception {

    // Setup mocks
    PowerMockito.mockStatic(StringUtils.class);

    // Arrange
    final ExcelProcessingHelperService objectUnderTest = new ExcelProcessingHelperService();
    final AtomicLong atomicLong =
        (AtomicLong) Reflector.getInstance("java.util.concurrent.atomic.AtomicLong");
    Reflector.setField(objectUnderTest, "sectorCxoModifiedTime", atomicLong);
    Reflector.setField(objectUnderTest, "sourceMetaDataModifiedTime", null);
    Reflector.setField(objectUnderTest, "httpClient", null);
    Reflector.setField(objectUnderTest, "jobTitlesModifiedTime", null);
    objectUnderTest.setSectorVsCXO(null);
    Reflector.setField(objectUnderTest, "SOURCE_METADATA_FILE", null);
    objectUnderTest.setJobVsTitles(null);
    final HashMap<String, String> hashMap = new HashMap<String, String>();
    Reflector.setField(objectUnderTest, "sourceMetaDataMap", hashMap);
    final String key = "!!!!!!!!";
    final Method isEmptyMethod =
        DTUMemberMatcher.method(StringUtils.class, "isEmpty", String.class);
    PowerMockito.doReturn(false)
        .when(StringUtils.class, isEmptyMethod)
        .withArguments(or(isA(String.class), isNull(String.class)));

    // Act
    final String retval = objectUnderTest.getSourceMetaData(key);

    // Assert
    assertNull(retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void getTitlesForJobInputNotNullOutputNull() throws Exception {

    // Arrange
    final ExcelProcessingHelperService objectUnderTest = new ExcelProcessingHelperService();
    Reflector.setField(objectUnderTest, "sectorCxoModifiedTime", null);
    final AtomicLong atomicLong =
        (AtomicLong) Reflector.getInstance("java.util.concurrent.atomic.AtomicLong");
    Reflector.setField(objectUnderTest, "sourceMetaDataModifiedTime", atomicLong);
    Reflector.setField(objectUnderTest, "httpClient", null);
    Reflector.setField(objectUnderTest, "jobTitlesModifiedTime", null);
    objectUnderTest.setSectorVsCXO(null);
    Reflector.setField(objectUnderTest, "SOURCE_METADATA_FILE", null);
    final HashMap<String, String> hashMap = new HashMap<String, String>();
    hashMap.put("/.......", null);
    objectUnderTest.setJobVsTitles(hashMap);
    final HashMap<String, String> hashMap1 = new HashMap<String, String>();
    Reflector.setField(objectUnderTest, "sourceMetaDataMap", hashMap1);
    final String job = ".";

    // Act
    final String retval = objectUnderTest.getTitlesForJob(job);

    // Assert
    assertNull(retval);
  }
}
