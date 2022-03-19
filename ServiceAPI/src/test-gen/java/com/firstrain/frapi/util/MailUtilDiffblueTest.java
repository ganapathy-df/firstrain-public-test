package com.firstrain.frapi.util;

import static org.junit.Assert.assertEquals;
import static org.mockito.AdditionalMatchers.or;
import static org.mockito.Matchers.isA;
import static org.mockito.Matchers.isNull;

import com.diffblue.deeptestutils.Reflector;
import com.diffblue.deeptestutils.mock.DTUMemberMatcher;
import freemarker.template.Configuration;
import freemarker.template.Template;
import java.lang.reflect.Method;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class MailUtilDiffblueTest {

  @Rule public final ExpectedException thrown = ExpectedException.none();
  @Rule public final Timeout globalTimeout = new Timeout(10_000);

  /* testedClasses: MailUtil */
  // Test generated by Diffblue Deeptest.

  @Test
  public void getHtmlToTransmitInputNullNullOutputIllegalStateException() throws Exception {

    // Arrange
    final MailUtil objectUnderTest = new MailUtil();
    final FreemarkerTemplates freemarkerTemplates = new FreemarkerTemplates();
    Reflector.setField(freemarkerTemplates, "ftlTemplatePath", null);
    Reflector.setField(freemarkerTemplates, "LOG", null);
    Reflector.setField(freemarkerTemplates, "config", null);
    objectUnderTest.freemarkerTemplates = freemarkerTemplates;
    final Map<String, Object> userMap = null;
    final String ftl = null;

    // Act
    thrown.expect(IllegalStateException.class);
    objectUnderTest.getHtmlToTransmit(userMap, ftl);

    // Method is not expected to return due to exception thrown
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest({Configuration.class, Template.class})
  @Test
  public void getHtmlToTransmitInputNullNullOutputNotNull() throws Exception {

    // Arrange
    final MailUtil objectUnderTest = new MailUtil();
    final FreemarkerTemplates freemarkerTemplates = new FreemarkerTemplates();
    Reflector.setField(freemarkerTemplates, "ftlTemplatePath", null);
    Reflector.setField(freemarkerTemplates, "LOG", null);
    final Configuration configuration = PowerMockito.mock(Configuration.class);
    final Template template = PowerMockito.mock(Template.class);
    final Method getTemplateMethod =
        DTUMemberMatcher.method(Configuration.class, "getTemplate", String.class);
    PowerMockito.doReturn(template)
        .when(configuration, getTemplateMethod)
        .withArguments(or(isA(String.class), isNull(String.class)));
    Reflector.setField(freemarkerTemplates, "config", configuration);
    objectUnderTest.freemarkerTemplates = freemarkerTemplates;
    final Map<String, Object> userMap = null;
    final String ftl = null;

    // Act
    final String retval = objectUnderTest.getHtmlToTransmit(userMap, ftl);

    // Assert
    assertEquals("", retval);
  }
}