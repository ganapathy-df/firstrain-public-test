/*
 * This file was automatically generated by EvoSuite
 * Thu Jun 28 17:39:14 GMT 2018
 */

package com.firstrain.web.pojo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;


public class VersionESTest {

	@Test(timeout = 4000)
	public void test0() throws Exception {
		Version version0 = new Version();
		version0.setName("");
		assertNull(version0.getLastModifiedBy());
	}

	@Test(timeout = 4000)
	public void test1() throws Exception {
		Version version0 = new Version();
		version0.setLastModifiedDate("");
		assertEquals("", version0.getLastModifiedDate());
	}

	@Test(timeout = 4000)
	public void test2() throws Exception {
		Version version0 = new Version();
		version0.setLastModifiedBy("cSkTip");
		assertEquals("cSkTip", version0.getLastModifiedBy());
	}

	@Test(timeout = 4000)
	public void test3() throws Exception {
		Version version0 = new Version();
		String string0 = version0.getName();
		assertNull(string0);
	}

	@Test(timeout = 4000)
	public void test4() throws Exception {
		Version version0 = new Version();
		String string0 = version0.getLastModifiedDate();
		assertNull(string0);
	}

	@Test(timeout = 4000)
	public void test5() throws Exception {
		Version version0 = new Version();
		String string0 = version0.getLastModifiedBy();
		assertNull(string0);
	}
}
