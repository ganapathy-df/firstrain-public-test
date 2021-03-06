/*
 * This file was automatically generated by EvoSuite
 * Mon Jul 02 15:52:53 GMT 2018
 */

package com.firstrain.web.controller.core;

import static org.evosuite.shaded.org.mockito.Mockito.mock;

import com.firstrain.web.service.core.HttpClientService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.xmlbeans.XmlSimpleList;
import org.dom4j.QName;
import org.dom4j.bean.BeanAttributeList;
import org.dom4j.bean.BeanElement;
import org.dom4j.bean.BeanMetaData;
import org.evosuite.runtime.ViolatedAssumptionAnswer;
import org.evosuite.runtime.javaee.injection.Injector;
import org.junit.Test;

public class CategorizerPwControllerESTest {

	@Test(timeout = 4000, expected = NullPointerException.class)
	public void test0() {
		CategorizerPwController categorizerPwController0 = new CategorizerPwController();
		QName qName0 = QName.get("PM*Pk(gz,jMrJ_", ">=");
		BeanElement beanElement0 = new BeanElement(qName0, "PM*Pk(gz,jMrJ_");
		Class<String> class0 = String.class;
		BeanMetaData beanMetaData0 = new BeanMetaData(class0);
		BeanAttributeList beanAttributeList0 = new BeanAttributeList(beanElement0, beanMetaData0);
		XmlSimpleList xmlSimpleList0 = new XmlSimpleList(beanAttributeList0);
		// Undeclared exception!
		categorizerPwController0.getTokens(null, xmlSimpleList0, ">=");
	}

	@Test(timeout = 4000, expected = NullPointerException.class)
	public void test1() {
		CategorizerPwController categorizerPwController0 = new CategorizerPwController();
		HttpClientService httpClientService0 = new HttpClientService();
		HttpClientParams httpClientParams0 = new HttpClientParams();
		HttpClient httpClient0 = new HttpClient(httpClientParams0);
		Injector.inject(
				httpClientService0, com.firstrain.web.service.core.HttpClientService.class,
				"httpClient",
				httpClient0);
		Injector.validateBean(httpClientService0,
				com.firstrain.web.service.core.HttpClientService.class);
		Injector.inject(
				categorizerPwController0,
				com.firstrain.web.controller.core.CategorizerPwController.class,
				"httpClientService",
				httpClientService0);
		HttpServletRequest httpServletRequest0 =
				mock(HttpServletRequest.class, new ViolatedAssumptionAnswer());
		HttpServletResponse httpServletResponse0 =
				mock(HttpServletResponse.class, new ViolatedAssumptionAnswer());
		// Undeclared exception!
		categorizerPwController0.doc(
				httpServletRequest0, httpServletResponse0, "org.dom4j.tree.AbstractElement");
	}
}
