/*
 * This file was automatically generated by EvoSuite
 * Thu Jun 28 17:24:50 GMT 2018
 */

package com.firstrain.frapi.pojo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;


public class APIDefinitionPojoESTest {

	@Test(timeout = 4000)
	public void test0() throws Exception {
		APIDefinitionPojo aPIDefinitionPojo0 = new APIDefinitionPojo();
		aPIDefinitionPojo0.setId(1L);
		assertEquals(1L, aPIDefinitionPojo0.getId());
	}

	@Test(timeout = 4000)
	public void test1() throws Exception {
		APIDefinitionPojo aPIDefinitionPojo0 = new APIDefinitionPojo();
		aPIDefinitionPojo0.setApiVersion(null);
		assertNull(aPIDefinitionPojo0.getApiVersion());
	}

	@Test(timeout = 4000)
	public void test2() throws Exception {
		APIDefinitionPojo aPIDefinitionPojo0 = new APIDefinitionPojo();
		aPIDefinitionPojo0.setApiSignature("");
		assertNull(aPIDefinitionPojo0.getApiVersion());
	}

	@Test(timeout = 4000)
	public void test3() throws Exception {
		APIDefinitionPojo aPIDefinitionPojo0 = new APIDefinitionPojo();
		long long0 = aPIDefinitionPojo0.getId();
		assertEquals(0L, long0);
	}

	@Test(timeout = 4000)
	public void test4() throws Exception {
		APIDefinitionPojo aPIDefinitionPojo0 = new APIDefinitionPojo();
		String string0 = aPIDefinitionPojo0.getApiVersion();
		assertNull(string0);
	}

	@Test(timeout = 4000)
	public void test5() throws Exception {
		APIDefinitionPojo aPIDefinitionPojo0 = new APIDefinitionPojo();
		String string0 = aPIDefinitionPojo0.getApiSignature();
		assertNull(string0);
	}
}