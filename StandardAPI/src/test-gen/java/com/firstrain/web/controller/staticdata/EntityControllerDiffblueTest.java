package com.firstrain.web.controller.staticdata;

import static org.junit.Assert.assertEquals;
import static org.mockito.AdditionalMatchers.or;
import static org.mockito.Matchers.isA;
import static org.mockito.Matchers.isNull;

import com.diffblue.deeptestutils.Reflector;
import com.diffblue.deeptestutils.mock.DTUMemberMatcher;
import com.firstrain.frapi.pojo.wrapper.BaseSet.SectionType;
import com.firstrain.web.service.staticdata.FreemarkerTemplateService;
import com.firstrain.web.service.staticdata.RequestParsingService;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.mockito.expectation.PowerMockitoStubber;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.management.*"})
public class EntityControllerDiffblueTest {

  @Rule public final ExpectedException thrown = ExpectedException.none();
  @Rule public final Timeout globalTimeout = new Timeout(10_000);

  /* testedClasses: EntityController */
  // Test generated by Diffblue Deeptest.
  @PrepareForTest(fullyQualifiedNames = {"com.firstrain.frapi.pojo.wrapper.BaseSet$SectionType"})
  @Test
  public void validateKeySetForHtmlInput1OutputFalse3() throws Exception {

    // Setup mocks
    PowerMockito.mockStatic(SectionType.class);

    // Arrange
    final java.util.HashSet keySet = new java.util.HashSet();
    keySet.add(null);
    final Method valueOfMethod =
        DTUMemberMatcher.method(SectionType.class, "valueOf", String.class);
    PowerMockito.doReturn(null)
        .when(SectionType.class, valueOfMethod)
        .withArguments(or(isA(String.class), isNull(String.class)));

    // Act
    final Class<?> classUnderTest =
        Reflector.forName("com.firstrain.web.controller.staticdata.EntityController");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod(
            "validateKeySetForHtml", Reflector.forName("java.util.Set"));
    methodUnderTest.setAccessible(true);
    final boolean retval = (boolean) methodUnderTest.invoke(null, keySet);

    // Assert
    assertEquals(false, retval);
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest(fullyQualifiedNames = {"com.firstrain.frapi.pojo.wrapper.BaseSet$SectionType"})
  @Test
  public void validateKeySetInput0ZeroNotNullOutputExecutionException() throws Throwable {

    // Setup mocks
    PowerMockito.mockStatic(SectionType.class);

    // Arrange
    final EntityController objectUnderTest = new EntityController();
    final RequestParsingService requestParsingService = new RequestParsingService();
    Reflector.setField(objectUnderTest, "requestParsingService", requestParsingService);
    Reflector.setField(objectUnderTest, "staticDataService", null);
    Reflector.setField(objectUnderTest, "appBaseUrl", null);
    final FreemarkerTemplateService freemarkerTemplateService = new FreemarkerTemplateService();
    Reflector.setField(objectUnderTest, "ftlService", freemarkerTemplateService);
    final java.util.HashSet keySet = new java.util.HashSet();
    final int errorCode = 0;
    final String sections = "";
    final SectionType sectionType4 = PowerMockito.mock(SectionType.class);
    final SectionType sectionType3 = PowerMockito.mock(SectionType.class);
    final SectionType sectionType2 = PowerMockito.mock(SectionType.class);
    final SectionType sectionType1 = PowerMockito.mock(SectionType.class);
    final SectionType sectionType = PowerMockito.mock(SectionType.class);
    final Method valueOfMethod =
        DTUMemberMatcher.method(SectionType.class, "valueOf", String.class);
    ((PowerMockitoStubber)
            PowerMockito.doReturn(sectionType)
                .doReturn(null)
                .doReturn(sectionType1)
                .doReturn(null)
                .doReturn(sectionType2)
                .doReturn(sectionType3)
                .doReturn(sectionType4)
                .doReturn(null))
        .when(SectionType.class, valueOfMethod)
        .withArguments(or(isA(String.class), isNull(String.class)));

    // Act
    thrown.expect(ExecutionException.class);
    try {
      final Class<?> classUnderTest =
          Reflector.forName("com.firstrain.web.controller.staticdata.EntityController");
      final Method methodUnderTest =
          classUnderTest.getDeclaredMethod(
              "validateKeySet",
              Reflector.forName("java.util.Set"),
              Reflector.forName("int"),
              Reflector.forName("java.lang.String"));
      methodUnderTest.setAccessible(true);
      methodUnderTest.invoke(objectUnderTest, keySet, errorCode, sections);
    } catch (InvocationTargetException ex) {
      throw ex.getCause();
    }

    // Method is not expected to return due to exception thrown
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest(fullyQualifiedNames = {"com.firstrain.frapi.pojo.wrapper.BaseSet$SectionType"})
  @Test
  public void validateKeySetInput0ZeroNotNullTrueOutputExecutionException() throws Throwable {

    // Setup mocks
    PowerMockito.mockStatic(SectionType.class);

    // Arrange
    final EntityController objectUnderTest = new EntityController();
    final java.util.HashSet keySet = new java.util.HashSet();
    final int errorCode = 0;
    final String sections = "";
    final boolean otherValidatedKeySet = true;
    final SectionType sectionType6 = PowerMockito.mock(SectionType.class);
    final SectionType sectionType5 = PowerMockito.mock(SectionType.class);
    final SectionType sectionType4 = PowerMockito.mock(SectionType.class);
    final SectionType sectionType3 = PowerMockito.mock(SectionType.class);
    final SectionType sectionType2 = PowerMockito.mock(SectionType.class);
    final SectionType sectionType1 = PowerMockito.mock(SectionType.class);
    final SectionType sectionType = PowerMockito.mock(SectionType.class);
    final Method valueOfMethod =
        DTUMemberMatcher.method(SectionType.class, "valueOf", String.class);
    ((PowerMockitoStubber)
            PowerMockito.doReturn(sectionType)
                .doReturn(sectionType1)
                .doReturn(null)
                .doReturn(sectionType2)
                .doReturn(sectionType3)
                .doReturn(sectionType4)
                .doReturn(sectionType5)
                .doReturn(sectionType6))
        .when(SectionType.class, valueOfMethod)
        .withArguments(or(isA(String.class), isNull(String.class)));

    // Act
    thrown.expect(ExecutionException.class);
    try {
      final Class<?> classUnderTest =
          Reflector.forName("com.firstrain.web.controller.staticdata.EntityController");
      final Method methodUnderTest =
          classUnderTest.getDeclaredMethod(
              "validateKeySet",
              Reflector.forName("java.util.Set"),
              Reflector.forName("int"),
              Reflector.forName("java.lang.String"),
              Reflector.forName("boolean"));
      methodUnderTest.setAccessible(true);
      methodUnderTest.invoke(objectUnderTest, keySet, errorCode, sections, otherValidatedKeySet);
    } catch (InvocationTargetException ex) {
      throw ex.getCause();
    }

    // Method is not expected to return due to exception thrown
  }
}
