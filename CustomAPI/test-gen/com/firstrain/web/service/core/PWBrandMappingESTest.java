/*
 * This file was automatically generated by EvoSuite
 * Mon Jul 02 15:58:35 GMT 2018
 */

package com.firstrain.web.service.core;

import java.io.PipedInputStream;
import org.evosuite.runtime.javaee.injection.Injector;
import org.junit.Test;

public class PWBrandMappingESTest {

	@Test(timeout = 4000, expected = RuntimeException.class)
	public void test0() {
		PWBrandMapping pWBrandMapping0 = arrangeBrandMapping();
		PipedInputStream pipedInputStream0 = new PipedInputStream();
		pWBrandMapping0.load(pipedInputStream0);
		Injector.validateBean(pWBrandMapping0, com.firstrain.web.service.core.PWBrandMapping.class);
		// Undeclared exception!
		Injector.executePostConstruct(pWBrandMapping0,
				com.firstrain.web.service.core.PWBrandMapping.class);
	}

	@Test(timeout = 4000, expected = RuntimeException.class)
	public void test1() throws Exception {
		PWBrandMapping pWBrandMapping0 = arrangeBrandMapping();
		Injector.validateBean(pWBrandMapping0, com.firstrain.web.service.core.PWBrandMapping.class);
		pWBrandMapping0.load();
		// Undeclared exception!
		Injector.executePostConstruct(pWBrandMapping0,
				com.firstrain.web.service.core.PWBrandMapping.class);
	}

	@Test(timeout = 4000, expected = RuntimeException.class)
	public void test2() {
		PWBrandMapping pWBrandMapping0 = new PWBrandMapping();
		LoadConfigurationComponentByExternalUrl loadConfigurationComponentByExternalUrl0 = arrangeData(); 
		pWBrandMapping0.getPwKey("GMT-08:00");
		Injector.inject(
				pWBrandMapping0,
				com.firstrain.web.service.core.PWBrandMapping.class,
				"loadConfigurationComponentByExternalUrl",
				loadConfigurationComponentByExternalUrl0);
		Injector.validateBean(pWBrandMapping0, com.firstrain.web.service.core.PWBrandMapping.class);
		// Undeclared exception!
		Injector.executePostConstruct(pWBrandMapping0,
				com.firstrain.web.service.core.PWBrandMapping.class);
	}

	@Test(timeout = 4000, expected = NullPointerException.class)
	public void test3() {
		PWBrandMapping pWBrandMapping0 = arrangeBrandMapping();
		Injector.validateBean(pWBrandMapping0, com.firstrain.web.service.core.PWBrandMapping.class);
		// Undeclared exception!
		pWBrandMapping0.getPwBrandInitials(null);
	}

	@Test(timeout = 4000, expected = RuntimeException.class)
	public void test4() {
		PWBrandMapping pWBrandMapping0 = arrangeBrandMapping();
		pWBrandMapping0.getBrand("}e*\"");
		Injector.validateBean(pWBrandMapping0, com.firstrain.web.service.core.PWBrandMapping.class);
		// Undeclared exception!
		Injector.executePostConstruct(pWBrandMapping0,
				com.firstrain.web.service.core.PWBrandMapping.class);
	}

	private PWBrandMapping arrangeBrandMapping() {
		PWBrandMapping pWBrandMapping0 = new PWBrandMapping();
		LoadConfigurationComponentByExternalUrl loadConfigurationComponentByExternalUrl0 = arrangeData(); 
		Injector.inject(
				pWBrandMapping0,
				com.firstrain.web.service.core.PWBrandMapping.class,
				"loadConfigurationComponentByExternalUrl",
				loadConfigurationComponentByExternalUrl0);
		return pWBrandMapping0;
	}
 
	private LoadConfigurationComponentByExternalUrl arrangeData() { 
		LoadConfigurationComponentByExternalUrl loadConfigurationComponentByExternalUrl0 = 
				new LoadConfigurationComponentByExternalUrl(); 
		FRResourceUtils fRResourceUtils0 = new FRResourceUtils(); 
		Injector.inject( 
				loadConfigurationComponentByExternalUrl0, 
				com.firstrain.web.service.core.LoadConfigurationComponentByExternalUrl.class, 
				"frResourceUtils", 
				fRResourceUtils0); 
		Injector.validateBean( 
				loadConfigurationComponentByExternalUrl0, 
				com.firstrain.web.service.core.LoadConfigurationComponentByExternalUrl.class); 
		return loadConfigurationComponentByExternalUrl0; 
	} 
}
