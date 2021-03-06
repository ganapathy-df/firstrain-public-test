/*
 * This file was automatically generated by EvoSuite
 * Mon Jul 02 18:41:41 GMT 2018
 */

package com.firstrain.web.view;

import static org.evosuite.shaded.org.mockito.Mockito.anyString;
import static org.evosuite.shaded.org.mockito.Mockito.doReturn;
import static org.evosuite.shaded.org.mockito.Mockito.mock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.firstrain.web.exception.PermissionException;
import java.sql.SQLException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.evosuite.runtime.ViolatedAssumptionAnswer;
import org.evosuite.runtime.javaee.injection.Injector;
import org.junit.Test;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.servlet.ModelAndView;

public class ExceptionViewResolverESTest {

	@Test(timeout = 4000)
	public void test0() throws Exception {
		ExceptionViewResolver exceptionViewResolver0 = new ExceptionViewResolver();
		ResourceBundleMessageSource resourceBundleMessageSource0 = new ResourceBundleMessageSource();
		Injector.inject(exceptionViewResolver0, com.firstrain.web.view.ExceptionViewResolver.class, "messageSource",
				resourceBundleMessageSource0);
		Injector.validateBean(exceptionViewResolver0,
				com.firstrain.web.view.ExceptionViewResolver.class);
		exceptionViewResolver0.setDefaultErrorView("exception");
		StringBuffer stringBuffer0 = new StringBuffer(405);
		HttpServletRequest httpServletRequest0 = mock(HttpServletRequest.class,
				new ViolatedAssumptionAnswer());
		doReturn(null).when(httpServletRequest0).getParameterMap();
		doReturn("exception").when(httpServletRequest0).getHeader(anyString());
		doReturn(stringBuffer0).when(httpServletRequest0).getRequestURL();
		HttpServletRequestWrapper httpServletRequestWrapper0 = new HttpServletRequestWrapper(
				httpServletRequest0);
		HttpServletResponse httpServletResponse0 = mock(HttpServletResponse.class,
				new ViolatedAssumptionAnswer());
		HttpServletResponseWrapper httpServletResponseWrapper0 = new HttpServletResponseWrapper(
				httpServletResponse0);
		SQLException sQLException0 = new SQLException();
		exceptionViewResolver0
				.doResolveException(httpServletRequestWrapper0, httpServletResponseWrapper0,
						resourceBundleMessageSource0, sQLException0);
		assertEquals("\nRequest Parameters:\n", stringBuffer0.toString());
		assertEquals(21, stringBuffer0.length());
	}

	@Test(timeout = 4000)
	public void test1() throws Exception {
		ExceptionViewResolver exceptionViewResolver0 = new ExceptionViewResolver();
		ResourceBundleMessageSource resourceBundleMessageSource0 = new ResourceBundleMessageSource();
		Injector.inject(exceptionViewResolver0, com.firstrain.web.view.ExceptionViewResolver.class, "messageSource",
				resourceBundleMessageSource0);
		Injector.validateBean(exceptionViewResolver0,
				com.firstrain.web.view.ExceptionViewResolver.class);
		exceptionViewResolver0.setDefaultErrorView("exception");
		HttpServletRequest httpServletRequest0 = mock(HttpServletRequest.class,
				new ViolatedAssumptionAnswer());
		doReturn("1}5k%<%\"p(").when(httpServletRequest0).getHeader(anyString());
		doReturn(null).when(httpServletRequest0).getRequestURL();
		HttpServletRequestWrapper httpServletRequestWrapper0 = new HttpServletRequestWrapper(
				httpServletRequest0);
		HttpServletResponse httpServletResponse0 = mock(HttpServletResponse.class,
				new ViolatedAssumptionAnswer());
		HttpServletResponseWrapper httpServletResponseWrapper0 = new HttpServletResponseWrapper(
				httpServletResponse0);
		MissingServletRequestParameterException missingServletRequestParameterException0 = new MissingServletRequestParameterException(
				"exception", "@");
		Object object0 = new Object();
		ModelAndView modelAndView0 = exceptionViewResolver0
				.doResolveException(httpServletRequestWrapper0, httpServletResponseWrapper0, object0,
						missingServletRequestParameterException0);
		assertFalse(modelAndView0.isEmpty());
	}

	@Test(timeout = 4000)
	public void test2() throws Exception {
		ExceptionViewResolver exceptionViewResolver0 = new ExceptionViewResolver();
		ResourceBundleMessageSource resourceBundleMessageSource0 = new ResourceBundleMessageSource();
		Injector.inject(exceptionViewResolver0, com.firstrain.web.view.ExceptionViewResolver.class, "messageSource",
				resourceBundleMessageSource0);
		Injector.validateBean(exceptionViewResolver0,
				com.firstrain.web.view.ExceptionViewResolver.class);
		exceptionViewResolver0.setDefaultErrorView("r#");
		HttpServletRequest httpServletRequest0 = mock(HttpServletRequest.class,
				new ViolatedAssumptionAnswer());
		HttpServletRequestWrapper httpServletRequestWrapper0 = new HttpServletRequestWrapper(
				httpServletRequest0);
		HttpServletResponse httpServletResponse0 = mock(HttpServletResponse.class,
				new ViolatedAssumptionAnswer());
		HttpServletResponseWrapper httpServletResponseWrapper0 = new HttpServletResponseWrapper(
				httpServletResponse0);
		PermissionException permissionException0 = new PermissionException();
		ModelAndView modelAndView0 = exceptionViewResolver0
				.doResolveException(httpServletRequestWrapper0, httpServletResponseWrapper0,
						resourceBundleMessageSource0, permissionException0);
		assertTrue(modelAndView0.isReference());
	}

	@Test(timeout = 4000)
	public void test3() throws Exception {
		ExceptionViewResolver exceptionViewResolver0 = new ExceptionViewResolver();
		ResourceBundleMessageSource resourceBundleMessageSource0 = new ResourceBundleMessageSource();
		Injector.inject(exceptionViewResolver0, com.firstrain.web.view.ExceptionViewResolver.class, "messageSource",
				resourceBundleMessageSource0);
		Injector.validateBean(exceptionViewResolver0,
				com.firstrain.web.view.ExceptionViewResolver.class);
		HttpServletRequest httpServletRequest0 = mock(HttpServletRequest.class,
				new ViolatedAssumptionAnswer());
		HttpServletRequestWrapper httpServletRequestWrapper0 = new HttpServletRequestWrapper(
				httpServletRequest0);
		HttpServletResponse httpServletResponse0 = mock(HttpServletResponse.class,
				new ViolatedAssumptionAnswer());
		HttpServletResponseWrapper httpServletResponseWrapper0 = new HttpServletResponseWrapper(
				httpServletResponse0);
		PermissionException permissionException0 = new PermissionException();
		ModelAndView modelAndView0 = exceptionViewResolver0
				.doResolveException(httpServletRequestWrapper0, httpServletResponseWrapper0,
						resourceBundleMessageSource0, permissionException0);
		assertNull(modelAndView0);
	}
}
