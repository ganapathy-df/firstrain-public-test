/*
 * This file was automatically generated by EvoSuite
 * Mon Jul 02 15:56:48 GMT 2018
 */

package com.firstrain.web.view;

import static org.evosuite.shaded.org.mockito.Mockito.anyString;
import static org.evosuite.shaded.org.mockito.Mockito.doReturn;
import static org.evosuite.shaded.org.mockito.Mockito.mock;
import static org.junit.Assert.assertEquals;

import freemarker.ext.servlet.FreemarkerServlet;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.evosuite.runtime.ViolatedAssumptionAnswer;
import org.evosuite.runtime.javaee.injection.Injector;
import org.junit.Test;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.bind.MissingServletRequestParameterException;

public class ExceptionViewResolverESTest {

	@Test(timeout = 4000)
	public void test1() {
		ExceptionViewResolver exceptionViewResolver0 = arrangeExceptionViewResolver();
		ServletContext servletContext0 = mock(ServletContext.class, new ViolatedAssumptionAnswer());
		StringBuffer stringBuffer0 = new StringBuffer(930);
		setupAndDoAct(exceptionViewResolver0, servletContext0, stringBuffer0);
		assertEquals(21, stringBuffer0.length());
		assertEquals("\nRequest Parameters:\n", stringBuffer0.toString());
	}

	private void setupAndDoAct(final ExceptionViewResolver exceptionViewResolver0, final ServletContext servletContext0, final StringBuffer stringBuffer0) {
	    HttpServletRequest httpServletRequest0 =
	    		mock(HttpServletRequest.class, new ViolatedAssumptionAnswer());
	    doReturn(null).when(httpServletRequest0).getParameterMap();
	    doReturn(servletContext0).when(httpServletRequest0).getServletContext();
	    doReturn(null).when(httpServletRequest0).getHeader(anyString());
	    doReturn(stringBuffer0).when(httpServletRequest0).getRequestURL();
	    HttpServletRequestWrapper httpServletRequestWrapper0 =
	    		new HttpServletRequestWrapper(httpServletRequest0);
	    ServletContext servletContext1 = httpServletRequestWrapper0.getServletContext();
	    Injector.inject(
	    		exceptionViewResolver0,
	    		com.firstrain.web.view.ExceptionViewResolver.class,
	    		"servletContext",
	    		servletContext1);
	    Injector.validateBean(exceptionViewResolver0,
	    		com.firstrain.web.view.ExceptionViewResolver.class);
	    exceptionViewResolver0.setDefaultErrorView("6GPCNK,,C5QG");
	    HttpServletResponse httpServletResponse0 =
	    		mock(HttpServletResponse.class, new ViolatedAssumptionAnswer());
	    HttpServletResponseWrapper httpServletResponseWrapper0 =
	    		new HttpServletResponseWrapper(httpServletResponse0);
	    MissingServletRequestParameterException missingServletRequestParameterException0 =
	    		new MissingServletRequestParameterException("p%r]hc#^I9GtXjj", "_method");
	    FreemarkerServlet freemarkerServlet0 = new FreemarkerServlet();
	    exceptionViewResolver0.doResolveException(
	    		httpServletRequestWrapper0,
	    		httpServletResponseWrapper0,
	    		freemarkerServlet0,
	    		missingServletRequestParameterException0);
	}

	private ExceptionViewResolver arrangeExceptionViewResolver() {
	    ExceptionViewResolver exceptionViewResolver0 = new ExceptionViewResolver();
	    ResourceBundleMessageSource resourceBundleMessageSource0 = new ResourceBundleMessageSource();
	    Injector.inject(
	    		exceptionViewResolver0,
	    		com.firstrain.web.view.ExceptionViewResolver.class,
	    		"messageSource",
	    		resourceBundleMessageSource0);
	    return exceptionViewResolver0;
	}
}
