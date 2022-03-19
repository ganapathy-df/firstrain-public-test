/*
 * This file was automatically generated by EvoSuite
 * Mon Jul 02 18:49:17 GMT 2018
 */

package com.firstrain.web.service.staticdata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.firstrain.frapi.domain.Document;
import com.firstrain.frapi.domain.EntityStatus;
import com.firstrain.frapi.domain.MonitorConfig;
import com.firstrain.frapi.domain.MonitorDetails;
import com.firstrain.frapi.pojo.AuthAPIResponse;
import com.firstrain.frapi.pojo.EntityBriefInfo;
import com.firstrain.frapi.pojo.MonitorAPIResponse;
import com.firstrain.frapi.pojo.MonitorBriefDetail;
import com.firstrain.frapi.pojo.UserAPIResponse;
import com.firstrain.frapi.pojo.wrapper.BaseSet;
import com.firstrain.frapi.pojo.wrapper.DocumentSet;
import com.firstrain.frapi.pojo.wrapper.TweetSet;
import com.firstrain.web.response.EntityDataResponse;
import com.firstrain.web.response.JSONResponse;
import com.firstrain.web.response.MonitorConfigResponse;
import com.firstrain.web.response.MonitorDetailsResponse;
import com.firstrain.web.response.MonitorInfoResponse;
import com.firstrain.web.response.MonitorWrapperResponse;
import org.dom4j.QName;
import org.dom4j.bean.BeanAttributeList;
import org.dom4j.bean.BeanElement;
import org.dom4j.bean.BeanMetaData;
import org.evosuite.runtime.javaee.injection.Injector;
import org.junit.Test;
import org.springframework.context.support.ResourceBundleMessageSource;

public class ResponseDecoratorServiceESTest {

	@Test(timeout = 4000, expected = NullPointerException.class)
	public void test00() throws Exception {
		ResponseDecoratorService responseDecoratorService0 = new ResponseDecoratorService();
		ResourceBundleMessageSource resourceBundleMessageSource0 = new ResourceBundleMessageSource();
		resourceBundleMessageSource0.setUseCodeAsDefaultMessage(true);
		Injector.inject(responseDecoratorService0,
				com.firstrain.web.service.staticdata.ResponseDecoratorService.class,
				"messageSource", resourceBundleMessageSource0);
		Injector.validateBean(responseDecoratorService0,
				com.firstrain.web.service.staticdata.ResponseDecoratorService.class);
		UserAPIResponse userAPIResponse0 = new UserAPIResponse();
		// Undeclared exception!
		responseDecoratorService0.getUserResponse(userAPIResponse0, "f,bC!l8z7ei]*");
	}

	@Test(timeout = 4000)
	public void test01() throws Exception {
		ResponseDecoratorService responseDecoratorService0 = new ResponseDecoratorService();
		ResourceBundleMessageSource resourceBundleMessageSource0 = new ResourceBundleMessageSource();
		resourceBundleMessageSource0.setUseCodeAsDefaultMessage(true);
		Injector.inject(responseDecoratorService0,
				com.firstrain.web.service.staticdata.ResponseDecoratorService.class,
				"messageSource", resourceBundleMessageSource0);
		Injector.validateBean(responseDecoratorService0,
				com.firstrain.web.service.staticdata.ResponseDecoratorService.class);
		JSONResponse jSONResponse0 = responseDecoratorService0.getSuccessMsg("errorcode.119");
		assertEquals("errorcode.119", jSONResponse0.getMessage());
	}

	@Test(timeout = 4000)
	public void test02() throws Exception {
		ResponseDecoratorService responseDecoratorService0 = new ResponseDecoratorService();
		ResourceBundleMessageSource resourceBundleMessageSource0 = new ResourceBundleMessageSource();
		resourceBundleMessageSource0.setUseCodeAsDefaultMessage(true);
		Injector.inject(responseDecoratorService0,
				com.firstrain.web.service.staticdata.ResponseDecoratorService.class,
				"messageSource", resourceBundleMessageSource0);
		Injector.validateBean(responseDecoratorService0,
				com.firstrain.web.service.staticdata.ResponseDecoratorService.class);
		MonitorAPIResponse monitorAPIResponse0 = new MonitorAPIResponse();
		MonitorWrapperResponse monitorWrapperResponse0 = responseDecoratorService0
				.getMonitorWrapperResponse(monitorAPIResponse0, "a8pRwS7");
		assertNull(monitorWrapperResponse0.getErrorStackTrace());
	}

	@Test(timeout = 4000)
	public void test03() throws Exception {
		ResponseDecoratorService responseDecoratorService0 = new ResponseDecoratorService();
		ResourceBundleMessageSource resourceBundleMessageSource0 = new ResourceBundleMessageSource();
		resourceBundleMessageSource0.setUseCodeAsDefaultMessage(true);
		Injector.inject(responseDecoratorService0,
				com.firstrain.web.service.staticdata.ResponseDecoratorService.class,
				"messageSource", resourceBundleMessageSource0);
		Injector.validateBean(responseDecoratorService0,
				com.firstrain.web.service.staticdata.ResponseDecoratorService.class);
		MonitorAPIResponse monitorAPIResponse0 = new MonitorAPIResponse();
		MonitorInfoResponse monitorInfoResponse0 = responseDecoratorService0
				.getMonitorInfoResponse(monitorAPIResponse0, "Q_w");
		assertNull(monitorInfoResponse0.getErrorCode());
	}

	@Test(timeout = 4000, expected = NullPointerException.class)
	public void test04() throws Exception {
		ResponseDecoratorService responseDecoratorService0 = new ResponseDecoratorService();
		ResourceBundleMessageSource resourceBundleMessageSource0 = new ResourceBundleMessageSource();
		resourceBundleMessageSource0.setUseCodeAsDefaultMessage(true);
		Injector.inject(responseDecoratorService0,
				com.firstrain.web.service.staticdata.ResponseDecoratorService.class,
				"messageSource", resourceBundleMessageSource0);
		Injector.validateBean(responseDecoratorService0,
				com.firstrain.web.service.staticdata.ResponseDecoratorService.class);
		EntityStatus entityStatus0 = new EntityStatus();
		// Undeclared exception!
		responseDecoratorService0.getMonitorEntityResponse(entityStatus0);
	}

	@Test(timeout = 4000, expected = ClassCastException.class)
	public void test05() throws Exception {
		ResponseDecoratorService responseDecoratorService0 = new ResponseDecoratorService();
		ResourceBundleMessageSource resourceBundleMessageSource0 = new ResourceBundleMessageSource();
		resourceBundleMessageSource0.setUseCodeAsDefaultMessage(true);
		Injector.inject(responseDecoratorService0,
				com.firstrain.web.service.staticdata.ResponseDecoratorService.class,
				"messageSource", resourceBundleMessageSource0);
		Injector.validateBean(responseDecoratorService0,
				com.firstrain.web.service.staticdata.ResponseDecoratorService.class);
		MonitorDetails monitorDetails0 = new MonitorDetails();
		BeanElement beanElement0 = new BeanElement("Q_w", resourceBundleMessageSource0);
		Class<Document> class0 = Document.class;
		BeanMetaData beanMetaData0 = new BeanMetaData(class0);
		BeanAttributeList beanAttributeList0 = beanMetaData0.createAttributeList(beanElement0);
		monitorDetails0.setTitlesForMonitorBuckets(beanAttributeList0);
		// Undeclared exception!
		responseDecoratorService0.getMonitorDetailsResponse(monitorDetails0);
	}

	@Test(timeout = 4000)
	public void test06() throws Exception {
		ResponseDecoratorService responseDecoratorService0 = new ResponseDecoratorService();
		ResourceBundleMessageSource resourceBundleMessageSource0 = new ResourceBundleMessageSource();
		resourceBundleMessageSource0.setUseCodeAsDefaultMessage(true);
		Injector.inject(responseDecoratorService0,
				com.firstrain.web.service.staticdata.ResponseDecoratorService.class,
				"messageSource", resourceBundleMessageSource0);
		Injector.validateBean(responseDecoratorService0,
				com.firstrain.web.service.staticdata.ResponseDecoratorService.class);
		MonitorDetails monitorDetails0 = new MonitorDetails();
		MonitorDetailsResponse monitorDetailsResponse0 = responseDecoratorService0
				.getMonitorDetailsResponse(monitorDetails0);
		assertEquals("user.monitors", monitorDetailsResponse0.getMessage());
	}

	@Test(timeout = 4000, expected = ClassCastException.class)
	public void test07() throws Exception {
		ResponseDecoratorService responseDecoratorService0 = new ResponseDecoratorService();
		ResourceBundleMessageSource resourceBundleMessageSource0 = new ResourceBundleMessageSource();
		resourceBundleMessageSource0.setUseCodeAsDefaultMessage(true);
		ClassLoader classLoader0 = ClassLoader.getSystemClassLoader();
		Injector.inject(responseDecoratorService0,
				com.firstrain.web.service.staticdata.ResponseDecoratorService.class,
				"messageSource", resourceBundleMessageSource0);
		Injector.validateBean(responseDecoratorService0,
				com.firstrain.web.service.staticdata.ResponseDecoratorService.class);
		MonitorConfig monitorConfig0 = new MonitorConfig();
		Class<Document> class0 = Document.class;
		BeanMetaData beanMetaData0 = new BeanMetaData(class0);
		QName qName0 = QName.get("Q_w");
		BeanElement beanElement0 = new BeanElement(qName0, classLoader0);
		BeanAttributeList beanAttributeList0 = beanMetaData0.createAttributeList(beanElement0);
		monitorConfig0.setQueries(beanAttributeList0);
		// Undeclared exception!
		responseDecoratorService0.getMonitorConfigResponse(monitorConfig0);
	}

	@Test(timeout = 4000)
	public void test08() throws Exception {
		ResponseDecoratorService responseDecoratorService0 = new ResponseDecoratorService();
		ResourceBundleMessageSource resourceBundleMessageSource0 = new ResourceBundleMessageSource();
		resourceBundleMessageSource0.setUseCodeAsDefaultMessage(true);
		Injector.inject(responseDecoratorService0,
				com.firstrain.web.service.staticdata.ResponseDecoratorService.class,
				"messageSource", resourceBundleMessageSource0);
		Injector.validateBean(responseDecoratorService0,
				com.firstrain.web.service.staticdata.ResponseDecoratorService.class);
		MonitorConfig monitorConfig0 = new MonitorConfig();
		MonitorConfigResponse monitorConfigResponse0 = responseDecoratorService0
				.getMonitorConfigResponse(monitorConfig0);
		assertEquals("gen.succ", monitorConfigResponse0.getMessage());
	}

	@Test(timeout = 4000, expected = NullPointerException.class)
	public void test09() throws Exception {
		ResponseDecoratorService responseDecoratorService0 = new ResponseDecoratorService();
		ResourceBundleMessageSource resourceBundleMessageSource0 = new ResourceBundleMessageSource();
		resourceBundleMessageSource0.setUseCodeAsDefaultMessage(true);
		Injector.inject(responseDecoratorService0,
				com.firstrain.web.service.staticdata.ResponseDecoratorService.class,
				"messageSource", resourceBundleMessageSource0);
		Injector.validateBean(responseDecoratorService0,
				com.firstrain.web.service.staticdata.ResponseDecoratorService.class);
		BaseSet.SectionType baseSet_SectionType0 = BaseSet.SectionType.C;
		TweetSet tweetSet0 = new TweetSet(baseSet_SectionType0);
		// Undeclared exception!
		responseDecoratorService0.getItemWrapperResponse(tweetSet0, "U:");
	}

	@Test(timeout = 4000, expected = NullPointerException.class)
	public void test10() throws Exception {
		ResponseDecoratorService responseDecoratorService0 = new ResponseDecoratorService();
		ResourceBundleMessageSource resourceBundleMessageSource0 = new ResourceBundleMessageSource();
		resourceBundleMessageSource0.setUseCodeAsDefaultMessage(true);
		Injector.inject(responseDecoratorService0,
				com.firstrain.web.service.staticdata.ResponseDecoratorService.class,
				"messageSource", resourceBundleMessageSource0);
		Injector.validateBean(responseDecoratorService0,
				com.firstrain.web.service.staticdata.ResponseDecoratorService.class);
		DocumentSet documentSet0 = new DocumentSet();
		// Undeclared exception!
		responseDecoratorService0.getItemWrapperResponse(documentSet0, "[5Q");
	}

	@Test(timeout = 4000)
	public void test11() throws Exception {
		ResponseDecoratorService responseDecoratorService0 = new ResponseDecoratorService();
		ResourceBundleMessageSource resourceBundleMessageSource0 = new ResourceBundleMessageSource();
		resourceBundleMessageSource0.setUseCodeAsDefaultMessage(true);
		Injector.inject(responseDecoratorService0,
				com.firstrain.web.service.staticdata.ResponseDecoratorService.class,
				"messageSource", resourceBundleMessageSource0);
		Injector.validateBean(responseDecoratorService0,
				com.firstrain.web.service.staticdata.ResponseDecoratorService.class);
		MonitorAPIResponse monitorAPIResponse0 = new MonitorAPIResponse();
		MonitorBriefDetail monitorBriefDetail0 = new MonitorBriefDetail();
		monitorAPIResponse0.setMonitorBriefDetail(monitorBriefDetail0);
		EntityDataResponse entityDataResponse0 = responseDecoratorService0
				.getEntityDataResponse(monitorAPIResponse0, "Q_w");
		assertNull(entityDataResponse0.getErrorStackTrace());
	}

	@Test(timeout = 4000, expected = ClassCastException.class)
	public void test12() throws Exception {
		ResponseDecoratorService responseDecoratorService0 = new ResponseDecoratorService();
		ResourceBundleMessageSource resourceBundleMessageSource0 = new ResourceBundleMessageSource();
		resourceBundleMessageSource0.setUseCodeAsDefaultMessage(true);
		Injector.inject(responseDecoratorService0,
				com.firstrain.web.service.staticdata.ResponseDecoratorService.class,
				"messageSource", resourceBundleMessageSource0);
		Injector.validateBean(responseDecoratorService0,
				com.firstrain.web.service.staticdata.ResponseDecoratorService.class);
		BeanElement beanElement0 = new BeanElement("Q_w", resourceBundleMessageSource0);
		Class<Document> class0 = Document.class;
		BeanMetaData beanMetaData0 = new BeanMetaData(class0);
		BeanAttributeList beanAttributeList0 = new BeanAttributeList(beanElement0, beanMetaData0);
		EntityBriefInfo entityBriefInfo0 = new EntityBriefInfo();
		BaseSet.SectionType baseSet_SectionType0 = BaseSet.SectionType.SL;
		TweetSet tweetSet0 = new TweetSet(beanAttributeList0, 9314, baseSet_SectionType0);
		entityBriefInfo0.setTweetList(tweetSet0);
		// Undeclared exception!
		responseDecoratorService0.getEntityDataResponse(entityBriefInfo0, "a8pRwS7");
	}

	@Test(timeout = 4000)
	public void test13() throws Exception {
		ResponseDecoratorService responseDecoratorService0 = new ResponseDecoratorService();
		ResourceBundleMessageSource resourceBundleMessageSource0 = new ResourceBundleMessageSource();
		resourceBundleMessageSource0.setUseCodeAsDefaultMessage(true);
		Injector.inject(responseDecoratorService0,
				com.firstrain.web.service.staticdata.ResponseDecoratorService.class,
				"messageSource", resourceBundleMessageSource0);
		Injector.validateBean(responseDecoratorService0,
				com.firstrain.web.service.staticdata.ResponseDecoratorService.class);
		EntityBriefInfo entityBriefInfo0 = new EntityBriefInfo();
		EntityDataResponse entityDataResponse0 = responseDecoratorService0
				.getEntityDataResponse(entityBriefInfo0, "8RS7");
		assertNull(entityDataResponse0.getVersion());
	}

	@Test(timeout = 4000, expected = NullPointerException.class)
	public void test14() throws Exception {
		ResponseDecoratorService responseDecoratorService0 = new ResponseDecoratorService();
		ResourceBundleMessageSource resourceBundleMessageSource0 = new ResourceBundleMessageSource();
		resourceBundleMessageSource0.setUseCodeAsDefaultMessage(true);
		Injector.inject(responseDecoratorService0,
				com.firstrain.web.service.staticdata.ResponseDecoratorService.class,
				"messageSource", resourceBundleMessageSource0);
		Injector.validateBean(responseDecoratorService0,
				com.firstrain.web.service.staticdata.ResponseDecoratorService.class);
		AuthAPIResponse authAPIResponse0 = new AuthAPIResponse();
		// Undeclared exception!
		responseDecoratorService0.getAuthKeyResponse(authAPIResponse0);
	}

	@Test(timeout = 4000, expected = ClassCastException.class)
	public void test15() throws Exception {
		ResponseDecoratorService responseDecoratorService0 = new ResponseDecoratorService();
		ResourceBundleMessageSource resourceBundleMessageSource0 = new ResourceBundleMessageSource();
		resourceBundleMessageSource0.setUseCodeAsDefaultMessage(true);
		Injector.inject(responseDecoratorService0,
				com.firstrain.web.service.staticdata.ResponseDecoratorService.class,
				"messageSource", resourceBundleMessageSource0);
		Injector.validateBean(responseDecoratorService0,
				com.firstrain.web.service.staticdata.ResponseDecoratorService.class);
		MonitorAPIResponse monitorAPIResponse0 = new MonitorAPIResponse();
		BeanElement beanElement0 = new BeanElement("Q_w", resourceBundleMessageSource0);
		BeanAttributeList beanAttributeList0 = new BeanAttributeList(beanElement0);
		monitorAPIResponse0.setEntities(beanAttributeList0);
		// Undeclared exception!
		responseDecoratorService0.getAddRemoveEntityResponse(monitorAPIResponse0, "8RS7");
	}

	@Test(timeout = 4000)
	public void test16() throws Exception {
		ResponseDecoratorService responseDecoratorService0 = new ResponseDecoratorService();
		ResourceBundleMessageSource resourceBundleMessageSource0 = new ResourceBundleMessageSource();
		resourceBundleMessageSource0.setUseCodeAsDefaultMessage(true);
		Injector.inject(responseDecoratorService0,
				com.firstrain.web.service.staticdata.ResponseDecoratorService.class,
				"messageSource", resourceBundleMessageSource0);
		Injector.validateBean(responseDecoratorService0,
				com.firstrain.web.service.staticdata.ResponseDecoratorService.class);
		MonitorAPIResponse monitorAPIResponse0 = new MonitorAPIResponse();
		MonitorInfoResponse monitorInfoResponse0 = responseDecoratorService0
				.getAddRemoveEntityResponse(monitorAPIResponse0, "8RS7");
		assertNull(monitorInfoResponse0.getVersion());
	}
}