/*
 * This file was automatically generated by EvoSuite
 * Thu Jun 28 18:01:28 GMT 2018
 */

package com.firstrain.test.util;

import static org.junit.Assert.assertEquals;

import java.util.List;
import org.junit.Test;


public class PojoTestUtilESTest {

	@Test(timeout = 4000)
	public void test0() throws Exception {
		List<Class> list0 = PojoTestUtil.getAllClasses("Zx'N@n(*^{");
		assertEquals(0, list0.size());
	}

	@Test(timeout = 4000)
	public void test2() throws Exception {
		List<Class> list0 = PojoTestUtil.getAllClasses("");
		assertEquals(0, list0.size());
	}
}
