/*
 * This file was automatically generated by EvoSuite
 * Mon Jul 02 15:58:40 GMT 2018
 */

package com.firstrain.web.service.core;

import com.firstrain.frapi.customapirepository.impl.EntityBackTestRepositoryImpl;
import org.evosuite.runtime.javaee.injection.Injector;
import org.junit.Test;

public class EntiyBackTestServiceESTest {

	@Test(timeout = 4000, expected = NullPointerException.class)
	public void test0() throws Exception {
		EntiyBackTestService entiyBackTestService0 = new EntiyBackTestService();
		EntityBackTestRepositoryImpl entityBackTestRepositoryImpl0 = new EntityBackTestRepositoryImpl();
		Injector.inject(
				entiyBackTestService0,
				com.firstrain.web.service.core.EntiyBackTestService.class,
				"entityBackTestRepositoryImpl",
				entityBackTestRepositoryImpl0);
		Injector.validateBean(entiyBackTestService0,
				com.firstrain.web.service.core.EntiyBackTestService.class);
		entiyBackTestService0.updateState(952L);
	}
}
