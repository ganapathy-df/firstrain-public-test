/*
 * This file was automatically generated by EvoSuite
 * Thu Jun 28 17:32:01 GMT 2018
 */

package com.firstrain.web.wrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.firstrain.web.pojo.EntityStandard;
import org.junit.Test;


public class EntityWrapperESTest {

	@Test(timeout = 4000)
	public void test0() throws Exception {
		EntityWrapper entityWrapper0 = new EntityWrapper();
		entityWrapper0.setName("kAm8_gq2}");
		assertEquals("kAm8_gq2}", entityWrapper0.getName());
	}

	@Test(timeout = 4000)
	public void test1() throws Exception {
		EntityWrapper entityWrapper0 = new EntityWrapper();
		entityWrapper0.setId("CAS>bbn");
		assertNull(entityWrapper0.getName());
	}

	@Test(timeout = 4000)
	public void test2() throws Exception {
		EntityWrapper entityWrapper0 = new EntityWrapper();
		entityWrapper0.setEntity(null);
		assertNull(entityWrapper0.getName());
	}

	@Test(timeout = 4000)
	public void test3() throws Exception {
		EntityWrapper entityWrapper0 = new EntityWrapper();
		String string0 = entityWrapper0.getName();
		assertNull(string0);
	}

	@Test(timeout = 4000)
	public void test4() throws Exception {
		EntityWrapper entityWrapper0 = new EntityWrapper();
		String string0 = entityWrapper0.getId();
		assertNull(string0);
	}

	@Test(timeout = 4000)
	public void test5() throws Exception {
		EntityWrapper entityWrapper0 = new EntityWrapper();
		EntityStandard entityStandard0 = entityWrapper0.getEntity();
		assertNull(entityStandard0);
	}
}
