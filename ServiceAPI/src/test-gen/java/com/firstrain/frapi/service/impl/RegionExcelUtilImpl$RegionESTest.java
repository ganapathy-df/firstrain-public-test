/*
 * This file was automatically generated by EvoSuite
 * Mon Jul 02 17:14:27 GMT 2018
 */

package com.firstrain.frapi.service.impl;

import static org.evosuite.shaded.org.mockito.Mockito.mock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.firstrain.frapi.obj.MonitorObj;
import com.firstrain.frapi.repository.EntityBaseServiceRepository;
import java.util.List;
import org.evosuite.runtime.ViolatedAssumptionAnswer;
import org.evosuite.runtime.javaee.injection.Injector;
import org.junit.Test;


public class RegionExcelUtilImpl$RegionESTest {

	@Test(timeout = 4000)
	public void test0() throws Exception {
		RegionExcelUtilImpl regionExcelUtilImpl0 = new RegionExcelUtilImpl();
		EntityBaseServiceRepository entityBaseServiceRepository0 = mock(
				EntityBaseServiceRepository.class, new ViolatedAssumptionAnswer());
		Injector.inject(regionExcelUtilImpl0,
				com.firstrain.frapi.service.impl.RegionExcelUtilImpl.class,
				"entityBaseServiceRepository", entityBaseServiceRepository0);
		Injector.validateBean(regionExcelUtilImpl0,
				com.firstrain.frapi.service.impl.RegionExcelUtilImpl.class);
		Injector
				.executePostConstruct(regionExcelUtilImpl0,
						com.firstrain.frapi.service.impl.RegionExcelUtilImpl.class);
		RegionExcelUtilImpl.Region regionExcelUtilImpl_Region0 = regionExcelUtilImpl0.new Region(
				null);
		MonitorObj monitorObj0 = regionExcelUtilImpl_Region0.getParent();
		assertNull(monitorObj0);
	}

	@Test(timeout = 4000)
	public void test1() throws Exception {
		RegionExcelUtilImpl regionExcelUtilImpl0 = new RegionExcelUtilImpl();
		EntityBaseServiceRepository entityBaseServiceRepository0 = mock(
				EntityBaseServiceRepository.class, new ViolatedAssumptionAnswer());
		Injector.inject(regionExcelUtilImpl0,
				com.firstrain.frapi.service.impl.RegionExcelUtilImpl.class,
				"entityBaseServiceRepository", entityBaseServiceRepository0);
		Injector.validateBean(regionExcelUtilImpl0,
				com.firstrain.frapi.service.impl.RegionExcelUtilImpl.class);
		Injector
				.executePostConstruct(regionExcelUtilImpl0,
						com.firstrain.frapi.service.impl.RegionExcelUtilImpl.class);
		RegionExcelUtilImpl.Region regionExcelUtilImpl_Region0 = regionExcelUtilImpl0.new Region(
				null);
		List<MonitorObj> list0 = regionExcelUtilImpl_Region0.getChildren();
		assertEquals(0, list0.size());
	}

	@Test(timeout = 4000)
	public void test2() throws Exception {
		RegionExcelUtilImpl regionExcelUtilImpl0 = new RegionExcelUtilImpl();
		EntityBaseServiceRepository entityBaseServiceRepository0 = mock(
				EntityBaseServiceRepository.class, new ViolatedAssumptionAnswer());
		Injector.inject(regionExcelUtilImpl0,
				com.firstrain.frapi.service.impl.RegionExcelUtilImpl.class,
				"entityBaseServiceRepository", entityBaseServiceRepository0);
		Injector.validateBean(regionExcelUtilImpl0,
				com.firstrain.frapi.service.impl.RegionExcelUtilImpl.class);
		Injector
				.executePostConstruct(regionExcelUtilImpl0,
						com.firstrain.frapi.service.impl.RegionExcelUtilImpl.class);
		RegionExcelUtilImpl.Region regionExcelUtilImpl_Region0 = regionExcelUtilImpl0.new Region(
				null);
		regionExcelUtilImpl_Region0.addChild(null);
	}
}
