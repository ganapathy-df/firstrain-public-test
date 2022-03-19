/*
 * This file was automatically generated by EvoSuite
 * Mon Jul 02 18:43:12 GMT 2018
 */

package com.firstrain.web.service.core;

import static org.evosuite.shaded.org.mockito.Mockito.mock;

import java.time.ZoneId;
import java.util.Map;
import org.apache.commons.httpclient.HttpClient;
import org.codehaus.jackson.type.TypeReference;
import org.evosuite.runtime.ViolatedAssumptionAnswer;
import org.evosuite.runtime.javaee.injection.Injector;
import org.junit.Test;


public class HttpClientServiceESTest {

	@Test(timeout = 4000, expected = IllegalArgumentException.class)
	public void test0() throws Exception {
		HttpClientService httpClientService0 = new HttpClientService();
		HttpClient httpClient0 = new HttpClient();
		Injector.inject(httpClientService0, com.firstrain.web.service.core.HttpClientService.class, "httpClient",
				httpClient0);
		Injector.validateBean(httpClientService0,
				com.firstrain.web.service.core.HttpClientService.class);
		Map<String, String> map0 = ZoneId.SHORT_IDS;
		TypeReference<Object> typeReference0 = (TypeReference<Object>) mock(TypeReference.class,
				new ViolatedAssumptionAnswer());
		httpClientService0.postDataInReqBody("com.firstrain.web.service.core.HttpClientService",
				"com.firstrain.web.service.core.HttpClientService", map0, typeReference0);
	}

	@Test(timeout = 4000, expected = IllegalArgumentException.class)
	public void test1() throws Exception {
		HttpClientService httpClientService0 = new HttpClientService();
		HttpClient httpClient0 = new HttpClient();
		Injector.inject(httpClientService0, com.firstrain.web.service.core.HttpClientService.class, "httpClient",
				httpClient0);
		Injector.validateBean(httpClientService0,
				com.firstrain.web.service.core.HttpClientService.class);
		httpClientService0.getData("http.connection-manager.class");
	}
}
