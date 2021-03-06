/*
 * This file was automatically generated by EvoSuite
 * Mon Jul 02 18:38:40 GMT 2018
 */

package com.firstrain.web.controller.staticdata;

import static org.evosuite.shaded.org.mockito.Mockito.mock;

import com.firstrain.web.service.staticdata.FreemarkerTemplateService;
import com.firstrain.web.service.staticdata.RequestParsingService;
import com.firstrain.web.service.staticdata.StaticDataService;
import freemarker.template.Configuration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.evosuite.runtime.ViolatedAssumptionAnswer;
import org.evosuite.runtime.javaee.injection.Injector;
import org.junit.Test;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.ui.ExtendedModelMap;

public class EntityControllerESTest {

	@Test(timeout = 4000, expected = RuntimeException.class)
	public void test0() throws Exception {
		EntityController entityController0 = new EntityController();
		FreemarkerTemplateService freemarkerTemplateService0 = new FreemarkerTemplateService();
		Configuration configuration0 = new Configuration();
		Injector
				.inject(freemarkerTemplateService0,
						com.firstrain.web.service.staticdata.FreemarkerTemplateService.class, "ftlConfig",
						configuration0);
		Injector.validateBean(freemarkerTemplateService0,
				com.firstrain.web.service.staticdata.FreemarkerTemplateService.class);
		Injector.inject(entityController0,
				com.firstrain.web.controller.staticdata.EntityController.class, "ftlService",
				freemarkerTemplateService0);
		RequestParsingService requestParsingService0 = new RequestParsingService();
		ResourceBundleMessageSource resourceBundleMessageSource0 = new ResourceBundleMessageSource();
		Injector.inject(requestParsingService0,
				com.firstrain.web.service.staticdata.RequestParsingService.class, "messageSource",
				resourceBundleMessageSource0);
		Injector.validateBean(requestParsingService0,
				com.firstrain.web.service.staticdata.RequestParsingService.class);
		Injector.inject(entityController0,
				com.firstrain.web.controller.staticdata.EntityController.class, "requestParsingService",
				requestParsingService0);
		StaticDataService staticDataService0 = new StaticDataService();
		Injector.inject(entityController0,
				com.firstrain.web.controller.staticdata.EntityController.class, "staticDataService",
				staticDataService0);
		Injector.validateBean(entityController0,
				com.firstrain.web.controller.staticdata.EntityController.class);
		ExtendedModelMap extendedModelMap0 = new ExtendedModelMap();
		HttpServletRequest httpServletRequest0 = mock(HttpServletRequest.class,
				new ViolatedAssumptionAnswer());
		HttpServletRequestWrapper httpServletRequestWrapper0 = new HttpServletRequestWrapper(
				httpServletRequest0);
		HttpServletResponse httpServletResponse0 = mock(HttpServletResponse.class,
				new ViolatedAssumptionAnswer());
		HttpServletResponseWrapper httpServletResponseWrapper0 = new HttpServletResponseWrapper(
				httpServletResponse0);
		// Undeclared exception!
		entityController0.getResponseInHtml(extendedModelMap0, httpServletRequestWrapper0,
				httpServletResponseWrapper0, null, "showWV", "", "time_zone", "");
	}

	@Test(timeout = 4000, expected = RuntimeException.class)
	public void test1() throws Exception {
		EntityController entityController0 = new EntityController();
		FreemarkerTemplateService freemarkerTemplateService0 = new FreemarkerTemplateService();
		Configuration configuration0 = Configuration.getDefaultConfiguration();
		Injector
				.inject(freemarkerTemplateService0,
						com.firstrain.web.service.staticdata.FreemarkerTemplateService.class, "ftlConfig",
						configuration0);
		Injector.validateBean(freemarkerTemplateService0,
				com.firstrain.web.service.staticdata.FreemarkerTemplateService.class);
		Injector.inject(entityController0,
				com.firstrain.web.controller.staticdata.EntityController.class, "ftlService",
				freemarkerTemplateService0);
		RequestParsingService requestParsingService0 = new RequestParsingService();
		ResourceBundleMessageSource resourceBundleMessageSource0 = new ResourceBundleMessageSource();
		Injector.inject(requestParsingService0,
				com.firstrain.web.service.staticdata.RequestParsingService.class, "messageSource",
				resourceBundleMessageSource0);
		Injector.validateBean(requestParsingService0,
				com.firstrain.web.service.staticdata.RequestParsingService.class);
		Injector.inject(entityController0,
				com.firstrain.web.controller.staticdata.EntityController.class, "requestParsingService",
				requestParsingService0);
		StaticDataService staticDataService0 = new StaticDataService();
		Injector.inject(entityController0,
				com.firstrain.web.controller.staticdata.EntityController.class, "staticDataService",
				staticDataService0);
		Injector.validateBean(entityController0,
				com.firstrain.web.controller.staticdata.EntityController.class);
		ExtendedModelMap extendedModelMap0 = new ExtendedModelMap();
		// Undeclared exception!
		entityController0
				.getResponseInHtml(extendedModelMap0, null, null,
						"classic_compatible", "time_format", "<%63Qpi{,LbkzFx|X", "default_encoding",
						"bb<pa#6Wr");
	}

	@Test(timeout = 4000, expected = RuntimeException.class)
	public void test2() throws Exception {
		EntityController entityController0 = new EntityController();
		FreemarkerTemplateService freemarkerTemplateService0 = new FreemarkerTemplateService();
		Configuration configuration0 = Configuration.getDefaultConfiguration();
		Injector
				.inject(freemarkerTemplateService0,
						com.firstrain.web.service.staticdata.FreemarkerTemplateService.class, "ftlConfig",
						configuration0);
		Injector.validateBean(freemarkerTemplateService0,
				com.firstrain.web.service.staticdata.FreemarkerTemplateService.class);
		Injector.inject(entityController0,
				com.firstrain.web.controller.staticdata.EntityController.class, "ftlService",
				freemarkerTemplateService0);
		RequestParsingService requestParsingService0 = new RequestParsingService();
		ResourceBundleMessageSource resourceBundleMessageSource0 = new ResourceBundleMessageSource();
		Injector.inject(requestParsingService0,
				com.firstrain.web.service.staticdata.RequestParsingService.class, "messageSource",
				resourceBundleMessageSource0);
		Injector.validateBean(requestParsingService0,
				com.firstrain.web.service.staticdata.RequestParsingService.class);
		Injector.inject(entityController0,
				com.firstrain.web.controller.staticdata.EntityController.class, "requestParsingService",
				requestParsingService0);
		StaticDataService staticDataService0 = new StaticDataService();
		Injector.inject(entityController0,
				com.firstrain.web.controller.staticdata.EntityController.class, "staticDataService",
				staticDataService0);
		Injector.validateBean(entityController0,
				com.firstrain.web.controller.staticdata.EntityController.class);
		ExtendedModelMap extendedModelMap0 = new ExtendedModelMap();
		HttpServletRequest httpServletRequest0 = mock(HttpServletRequest.class,
				new ViolatedAssumptionAnswer());
		HttpServletRequestWrapper httpServletRequestWrapper0 = new HttpServletRequestWrapper(
				httpServletRequest0);
		HttpServletResponse httpServletResponse0 = mock(HttpServletResponse.class,
				new ViolatedAssumptionAnswer());
		HttpServletResponseWrapper httpServletResponseWrapper0 = new HttpServletResponseWrapper(
				httpServletResponse0);
		// Undeclared exception!
		entityController0.getResponseInHtml(extendedModelMap0, httpServletRequestWrapper0,
				httpServletResponseWrapper0, "", "strict_syntax", "locale", "", "WO");
	}

	@Test(timeout = 4000, expected = RuntimeException.class)
	public void test3() throws Exception {
		EntityController entityController0 = new EntityController();
		FreemarkerTemplateService freemarkerTemplateService0 = new FreemarkerTemplateService();
		Configuration configuration0 = Configuration.getDefaultConfiguration();
		Injector
				.inject(freemarkerTemplateService0,
						com.firstrain.web.service.staticdata.FreemarkerTemplateService.class, "ftlConfig",
						configuration0);
		Injector.validateBean(freemarkerTemplateService0,
				com.firstrain.web.service.staticdata.FreemarkerTemplateService.class);
		Injector.inject(entityController0,
				com.firstrain.web.controller.staticdata.EntityController.class, "ftlService",
				freemarkerTemplateService0);
		RequestParsingService requestParsingService0 = new RequestParsingService();
		ResourceBundleMessageSource resourceBundleMessageSource0 = new ResourceBundleMessageSource();
		Injector.inject(requestParsingService0,
				com.firstrain.web.service.staticdata.RequestParsingService.class, "messageSource",
				resourceBundleMessageSource0);
		Injector.validateBean(requestParsingService0,
				com.firstrain.web.service.staticdata.RequestParsingService.class);
		Injector.inject(entityController0,
				com.firstrain.web.controller.staticdata.EntityController.class, "requestParsingService",
				requestParsingService0);
		StaticDataService staticDataService0 = new StaticDataService();
		Injector.inject(entityController0,
				com.firstrain.web.controller.staticdata.EntityController.class, "staticDataService",
				staticDataService0);
		Injector.validateBean(entityController0,
				com.firstrain.web.controller.staticdata.EntityController.class);
		HttpServletRequest httpServletRequest0 = mock(HttpServletRequest.class,
				new ViolatedAssumptionAnswer());
		HttpServletRequestWrapper httpServletRequestWrapper0 = new HttpServletRequestWrapper(
				httpServletRequest0);
		HttpServletResponse httpServletResponse0 = mock(HttpServletResponse.class,
				new ViolatedAssumptionAnswer());
		HttpServletResponseWrapper httpServletResponseWrapper0 = new HttpServletResponseWrapper(
				httpServletResponse0);
		// Undeclared exception!
		entityController0
				.getResponseInAllFormat(httpServletRequestWrapper0, httpServletResponseWrapper0,
						"output_encoding", null, "*2", "whitespace_stripping", "ma^//qZc-Pe@!JLglE1");
	}

	@Test(timeout = 4000, expected = RuntimeException.class)
	public void test4() throws Exception {
		EntityController entityController0 = new EntityController();
		FreemarkerTemplateService freemarkerTemplateService0 = new FreemarkerTemplateService();
		Configuration configuration0 = new Configuration();
		Injector
				.inject(freemarkerTemplateService0,
						com.firstrain.web.service.staticdata.FreemarkerTemplateService.class, "ftlConfig",
						configuration0);
		Injector.validateBean(freemarkerTemplateService0,
				com.firstrain.web.service.staticdata.FreemarkerTemplateService.class);
		Injector.inject(entityController0,
				com.firstrain.web.controller.staticdata.EntityController.class, "ftlService",
				freemarkerTemplateService0);
		RequestParsingService requestParsingService0 = new RequestParsingService();
		ResourceBundleMessageSource resourceBundleMessageSource0 = new ResourceBundleMessageSource();
		Injector.inject(requestParsingService0,
				com.firstrain.web.service.staticdata.RequestParsingService.class, "messageSource",
				resourceBundleMessageSource0);
		Injector.validateBean(requestParsingService0,
				com.firstrain.web.service.staticdata.RequestParsingService.class);
		Injector.inject(entityController0,
				com.firstrain.web.controller.staticdata.EntityController.class, "requestParsingService",
				requestParsingService0);
		StaticDataService staticDataService0 = new StaticDataService();
		Injector.inject(entityController0,
				com.firstrain.web.controller.staticdata.EntityController.class, "staticDataService",
				staticDataService0);
		Injector.validateBean(entityController0,
				com.firstrain.web.controller.staticdata.EntityController.class);
		HttpServletRequest httpServletRequest0 = mock(HttpServletRequest.class,
				new ViolatedAssumptionAnswer());
		HttpServletRequestWrapper httpServletRequestWrapper0 = new HttpServletRequestWrapper(
				httpServletRequest0);
		HttpServletResponse httpServletResponse0 = mock(HttpServletResponse.class,
				new ViolatedAssumptionAnswer());
		HttpServletResponseWrapper httpServletResponseWrapper0 = new HttpServletResponseWrapper(
				httpServletResponse0);
		// Undeclared exception!
		entityController0
				.getResponseInAllFormat(httpServletRequestWrapper0, httpServletResponseWrapper0,
						"strict_bean_models", "time_format", "auto_import", "strict_syntax", "time_zone");
	}

	@Test(timeout = 4000, expected = RuntimeException.class)
	public void test5() throws Exception {
		EntityController entityController0 = new EntityController();
		FreemarkerTemplateService freemarkerTemplateService0 = new FreemarkerTemplateService();
		Configuration configuration0 = Configuration.getDefaultConfiguration();
		Injector
				.inject(freemarkerTemplateService0,
						com.firstrain.web.service.staticdata.FreemarkerTemplateService.class, "ftlConfig",
						configuration0);
		Injector.validateBean(freemarkerTemplateService0,
				com.firstrain.web.service.staticdata.FreemarkerTemplateService.class);
		Injector.inject(entityController0,
				com.firstrain.web.controller.staticdata.EntityController.class, "ftlService",
				freemarkerTemplateService0);
		RequestParsingService requestParsingService0 = new RequestParsingService();
		ResourceBundleMessageSource resourceBundleMessageSource0 = new ResourceBundleMessageSource();
		Injector.inject(requestParsingService0,
				com.firstrain.web.service.staticdata.RequestParsingService.class, "messageSource",
				resourceBundleMessageSource0);
		Injector.validateBean(requestParsingService0,
				com.firstrain.web.service.staticdata.RequestParsingService.class);
		Injector.inject(entityController0,
				com.firstrain.web.controller.staticdata.EntityController.class, "requestParsingService",
				requestParsingService0);
		StaticDataService staticDataService0 = new StaticDataService();
		Injector.inject(entityController0,
				com.firstrain.web.controller.staticdata.EntityController.class, "staticDataService",
				staticDataService0);
		Injector.validateBean(entityController0,
				com.firstrain.web.controller.staticdata.EntityController.class);
		HttpServletRequest httpServletRequest0 = mock(HttpServletRequest.class,
				new ViolatedAssumptionAnswer());
		HttpServletRequestWrapper httpServletRequestWrapper0 = new HttpServletRequestWrapper(
				httpServletRequest0);
		HttpServletResponse httpServletResponse0 = mock(HttpServletResponse.class,
				new ViolatedAssumptionAnswer());
		HttpServletResponseWrapper httpServletResponseWrapper0 = new HttpServletResponseWrapper(
				httpServletResponse0);
		// Undeclared exception!
		entityController0
				.getResponseInAllFormat(httpServletRequestWrapper0, httpServletResponseWrapper0,
						"template_update_delay", "", "", "cache_storage", "template_update_delay");
	}
}
