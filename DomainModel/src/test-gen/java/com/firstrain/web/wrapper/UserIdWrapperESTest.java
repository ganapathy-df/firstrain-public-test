/*
 * This file was automatically generated by EvoSuite
 * Thu Jun 28 17:32:16 GMT 2018
 */

package com.firstrain.web.wrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;


public class UserIdWrapperESTest {

	@Test(timeout = 4000)
	public void test0() throws Exception {
		UserIdWrapper userIdWrapper0 = new UserIdWrapper();
		userIdWrapper0.setUserId("*\"UA]$&J#59");
		assertEquals("*\"UA]$&J#59", userIdWrapper0.getUserId());
	}

	@Test(timeout = 4000)
	public void test1() throws Exception {
		UserIdWrapper userIdWrapper0 = new UserIdWrapper();
		String string0 = userIdWrapper0.getUserId();
		assertNull(string0);
	}
}