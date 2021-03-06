/*
 * This file was automatically generated by EvoSuite
 * Mon Jul 02 18:39:53 GMT 2018
 */

package com.firstrain.web.interceptor;

import static org.evosuite.shaded.org.mockito.Mockito.anyString;
import static org.evosuite.shaded.org.mockito.Mockito.doReturn;
import static org.evosuite.shaded.org.mockito.Mockito.mock;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.swing.JLayeredPane;
import org.evosuite.runtime.ViolatedAssumptionAnswer;
import org.junit.Test;


public class AuthInterceptorStaticAPIESTest {

	@Test(timeout = 4000)
	public void test0() throws Exception {
		AuthInterceptorStaticAPI authInterceptorStaticAPI0 = new AuthInterceptorStaticAPI();
		HttpServletRequest httpServletRequest0 = mock(HttpServletRequest.class,
				new ViolatedAssumptionAnswer());
		doReturn("Smi8E2GgBS16cl9w2q4aEIu0", "").when(httpServletRequest0).getHeader(anyString());
		doReturn("Smi8E2GgBS16cl9w2q4aEIu0").when(httpServletRequest0).getMethod();
		doReturn("Smi8E2GgBS16cl9w2q4aEIu0").when(httpServletRequest0).getRequestURI();
		HttpServletRequestWrapper httpServletRequestWrapper0 = new HttpServletRequestWrapper(
				httpServletRequest0);
		HttpServletResponse httpServletResponse0 = mock(HttpServletResponse.class,
				new ViolatedAssumptionAnswer());
		HttpServletResponseWrapper httpServletResponseWrapper0 = new HttpServletResponseWrapper(
				httpServletResponse0);
		Object object0 = new Object();
		boolean boolean0 = authInterceptorStaticAPI0
				.preHandle(httpServletRequestWrapper0, httpServletResponseWrapper0, object0);
		assertTrue(boolean0);
	}

	@Test(timeout = 4000)
	public void test1() throws Exception {
		AuthInterceptorStaticAPI authInterceptorStaticAPI0 = new AuthInterceptorStaticAPI();
		RequestDispatcher requestDispatcher0 = mock(RequestDispatcher.class,
				new ViolatedAssumptionAnswer());
		HttpServletRequest httpServletRequest0 = mock(HttpServletRequest.class,
				new ViolatedAssumptionAnswer());
		doReturn(requestDispatcher0).when(httpServletRequest0).getRequestDispatcher(anyString());
		doReturn("S16cl9w2q4aEIu0", "K%").when(httpServletRequest0).getHeader(anyString());
		doReturn("S16cl9w2q4aEIu0").when(httpServletRequest0).getMethod();
		doReturn("S16cl9w2q4aEIu0").when(httpServletRequest0).getRequestURI();
		HttpServletRequestWrapper httpServletRequestWrapper0 = new HttpServletRequestWrapper(
				httpServletRequest0);
		boolean boolean0 = authInterceptorStaticAPI0
				.preHandle(httpServletRequestWrapper0, null, null);
		assertFalse(boolean0);
	}

	@Test(timeout = 4000, expected = NullPointerException.class)
	public void test2() throws Exception {
		AuthInterceptorStaticAPI authInterceptorStaticAPI0 = new AuthInterceptorStaticAPI();
		HttpServletRequest httpServletRequest0 = mock(HttpServletRequest.class,
				new ViolatedAssumptionAnswer());
		doReturn(null).when(httpServletRequest0).getParameter(anyString());
		doReturn(null).when(httpServletRequest0).getRequestDispatcher(anyString());
		doReturn("Accept", null, "W}a)^Ch~J8\"=lAdw",
				"Cipher.PBEWITHSHA256AND192BITAES-CBC-BC").when(httpServletRequest0).getHeader(anyString());
		doReturn("W}a)^Ch~J8\"=lAdw").when(httpServletRequest0).getMethod();
		doReturn("W}a)^Ch~J8\"=lAdw").when(httpServletRequest0).getRequestURI();
		HttpServletRequestWrapper httpServletRequestWrapper0 = new HttpServletRequestWrapper(
				httpServletRequest0);
		authInterceptorStaticAPI0
				.preHandle(httpServletRequestWrapper0, null, null);
	}

	@Test(timeout = 4000)
	public void test3() throws Exception {
		AuthInterceptorStaticAPI authInterceptorStaticAPI0 = new AuthInterceptorStaticAPI();
		HttpServletRequest httpServletRequest0 = mock(HttpServletRequest.class,
				new ViolatedAssumptionAnswer());
		doReturn("6PYz?`v;9X/$(NfgenerateAuthKey").when(httpServletRequest0).getRequestURI();
		HttpServletRequestWrapper httpServletRequestWrapper0 = new HttpServletRequestWrapper(
				httpServletRequest0);
		HttpServletResponse httpServletResponse0 = mock(HttpServletResponse.class,
				new ViolatedAssumptionAnswer());
		HttpServletResponseWrapper httpServletResponseWrapper0 = new HttpServletResponseWrapper(
				httpServletResponse0);
		boolean boolean0 = authInterceptorStaticAPI0
				.preHandle(httpServletRequestWrapper0, httpServletResponseWrapper0,
						httpServletResponseWrapper0);
		assertTrue(boolean0);
	}

	@Test(timeout = 4000, expected = NullPointerException.class)
	public void test4() throws Exception {
		AuthInterceptorStaticAPI authInterceptorStaticAPI0 = new AuthInterceptorStaticAPI();
		HttpServletRequest httpServletRequest0 = mock(HttpServletRequest.class,
				new ViolatedAssumptionAnswer());
		doReturn(null, (String) null).when(httpServletRequest0).getParameter(anyString());
		doReturn(null).when(httpServletRequest0).getRequestDispatcher(anyString());
		doReturn(null, null, null, null).when(httpServletRequest0)
				.getHeader(anyString());
		doReturn(")hfy,%de7zOQt/").when(httpServletRequest0).getMethod();
		doReturn("org.springframework.web.servlet.ModelAndView").when(httpServletRequest0)
				.getRequestURI();
		HttpServletRequestWrapper httpServletRequestWrapper0 = new HttpServletRequestWrapper(
				httpServletRequest0);
		HttpServletResponse httpServletResponse0 = mock(HttpServletResponse.class,
				new ViolatedAssumptionAnswer());
		HttpServletResponseWrapper httpServletResponseWrapper0 = new HttpServletResponseWrapper(
				httpServletResponse0);
		Integer integer0 = JLayeredPane.POPUP_LAYER;
		authInterceptorStaticAPI0
				.preHandle(httpServletRequestWrapper0, httpServletResponseWrapper0, integer0);
	}
}
