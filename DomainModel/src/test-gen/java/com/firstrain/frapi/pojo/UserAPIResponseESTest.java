/*
 * This file was automatically generated by EvoSuite
 * Thu Jun 28 17:24:34 GMT 2018
 */

package com.firstrain.frapi.pojo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.firstrain.frapi.domain.User;
import org.junit.Test;


public class UserAPIResponseESTest {

	@Test(timeout = 4000)
	public void test0() throws Exception {
		UserAPIResponse userAPIResponse0 = new UserAPIResponse();
		User user0 = new User();
		userAPIResponse0.setUser(user0);
		assertEquals(0L, user0.getTemplateId());
	}

	@Test(timeout = 4000)
	public void test1() throws Exception {
		UserAPIResponse userAPIResponse0 = new UserAPIResponse();
		userAPIResponse0.setStatusCode(0);
		assertEquals(0, userAPIResponse0.getStatusCode());
	}

	@Test(timeout = 4000)
	public void test2() throws Exception {
		UserAPIResponse userAPIResponse0 = new UserAPIResponse();
		User user0 = userAPIResponse0.getUser();
		assertNull(user0);
	}

	@Test(timeout = 4000)
	public void test3() throws Exception {
		UserAPIResponse userAPIResponse0 = new UserAPIResponse();
		int int0 = userAPIResponse0.getStatusCode();
		assertEquals(0, int0);
	}
}
