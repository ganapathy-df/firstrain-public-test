package com.firstrain.web.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.AdditionalMatchers.or;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.isA;
import static org.mockito.Matchers.isNull;

import com.diffblue.deeptestutils.Reflector;
import com.diffblue.deeptestutils.mock.DTUMemberMatcher;
import com.firstrain.frapi.domain.SectionSpec;
import com.firstrain.frapi.pojo.wrapper.BaseSet.SectionType;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.support.ResourceBundleMessageSource;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.management.*"})
public class BaseRequestParsingServiceDiffblueTest {

  @Rule public final ExpectedException thrown = ExpectedException.none();
  @Rule public final Timeout globalTimeout = new Timeout(10_000);

  /* testedClasses: BaseRequestParsingService */
  // Test generated by Diffblue Deeptest.

  @Test
  public void getRefinedReqValInputNotNullOutputNotNull() throws Exception {

    // Arrange
    final BaseRequestParsingService objectUnderTest =
        (BaseRequestParsingService)
            Reflector.getInstance("com.firstrain.web.service.BaseRequestParsingService");
    final ResourceBundleMessageSource resourceBundleMessageSource =
        (ResourceBundleMessageSource)
            Reflector.getInstance(
                "org.springframework.context.support.ResourceBundleMessageSource");
    Reflector.setField(objectUnderTest, "messageSource", resourceBundleMessageSource);
    final String val = " 1000";

    // Act
    final String retval = objectUnderTest.getRefinedReqVal(val);

    // Assert
    assertEquals("1000", retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void getRefinedReqValInputNotNullOutputNull() throws Exception {

    // Arrange
    final BaseRequestParsingService objectUnderTest =
        (BaseRequestParsingService)
            Reflector.getInstance("com.firstrain.web.service.BaseRequestParsingService");
    final ResourceBundleMessageSource resourceBundleMessageSource =
        (ResourceBundleMessageSource)
            Reflector.getInstance(
                "org.springframework.context.support.ResourceBundleMessageSource");
    Reflector.setField(objectUnderTest, "messageSource", resourceBundleMessageSource);
    final String val = "     ";

    // Act
    final String retval = objectUnderTest.getRefinedReqVal(val);

    // Assert
    assertNull(retval);
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest(
      fullyQualifiedNames = {"com.firstrain.frapi.pojo.wrapper.BaseSet$SectionType"},
      value = {SectionSpec.class, BaseRequestParsingService.class})
  @Test
  public void getSectionsSpecMapInputNullNotNullNullTrueFalseNullOutput1() throws Exception {

    // Setup mocks
    PowerMockito.mockStatic(SectionType.class);

    // Arrange
    final BaseRequestParsingService objectUnderTest =
        PowerMockito.mock(BaseRequestParsingService.class);
    final Method getSectionsSpecMapMethod =
        DTUMemberMatcher.method(
            BaseRequestParsingService.class,
            "getSectionsSpecMap",
            String.class,
            String.class,
            String.class,
            boolean.class,
            boolean.class,
            List.class);
    PowerMockito.doCallRealMethod()
        .when(objectUnderTest, getSectionsSpecMapMethod)
        .withArguments(
            or(isA(String.class), isNull(String.class)),
            or(isA(String.class), isNull(String.class)),
            or(isA(String.class), isNull(String.class)),
            anyBoolean(),
            anyBoolean(),
            or(isA(List.class), isNull(List.class)));
    final Method validateExcSectionListMethod =
        DTUMemberMatcher.method(
            BaseRequestParsingService.class,
            "validateExcSectionList",
            String.class,
            String.class,
            List.class);
    PowerMockito.doCallRealMethod()
        .when(objectUnderTest, validateExcSectionListMethod)
        .withArguments(
            or(isA(String.class), isNull(String.class)),
            or(isA(String.class), isNull(String.class)),
            or(isA(List.class), isNull(List.class)));
    final SectionSpec sectionSpec = PowerMockito.mock(SectionSpec.class);
    final Method getSectionSpecMethod =
        DTUMemberMatcher.method(BaseRequestParsingService.class, "getSectionSpec", String.class);
    PowerMockito.when(objectUnderTest, getSectionSpecMethod)
        .withArguments(or(isA(String.class), isNull(String.class)))
        .thenReturn(sectionSpec);
    final ResourceBundleMessageSource resourceBundleMessageSource =
        (ResourceBundleMessageSource)
            Reflector.getInstance(
                "org.springframework.context.support.ResourceBundleMessageSource");
    Reflector.setField(objectUnderTest, "messageSource", resourceBundleMessageSource);
    final String spec = null;
    final String section = " !";
    final String sectionParams = null;
    final boolean needPagination = true;
    final boolean needBucket = false;
    final List<String> excSectionList = null;
    final Method valueOfMethod =
        DTUMemberMatcher.method(SectionType.class, "valueOf", String.class);
    PowerMockito.doReturn(null)
        .when(SectionType.class, valueOfMethod)
        .withArguments(or(isA(String.class), isNull(String.class)));

    // Act
    final Class<?> classUnderTest =
        Reflector.forName("com.firstrain.web.service.BaseRequestParsingService");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod(
            "getSectionsSpecMap",
            Reflector.forName("java.lang.String"),
            Reflector.forName("java.lang.String"),
            Reflector.forName("java.lang.String"),
            Reflector.forName("boolean"),
            Reflector.forName("boolean"),
            Reflector.forName("java.util.List"));
    methodUnderTest.setAccessible(true);
    final Map<SectionType, SectionSpec> retval =
        (Map<SectionType, SectionSpec>)
            methodUnderTest.invoke(
                objectUnderTest,
                spec,
                section,
                sectionParams,
                needPagination,
                needBucket,
                excSectionList);

    // Assert
    assertNotNull(retval);
    assertEquals(1, retval.size());
    assertNotNull(retval.get(null));
  }
}
