/*
 * This file was automatically generated by EvoSuite
 * Thu Jun 28 17:30:42 GMT 2018
 */

package com.firstrain.web.wrapper;

import static org.junit.Assert.assertNull;

import com.firstrain.web.pojo.User;
import org.junit.Test;


public class UserWrapperESTest {

	@Test(timeout = 4000)
	public void test0() throws Exception {
		UserWrapper userWrapper0 = new UserWrapper();
		User user0 = new User();
		userWrapper0.setUser(user0);
		assertNull(user0.getuStatus());
	}

	@Test(timeout = 4000)
	public void test1() throws Exception {
		UserWrapper userWrapper0 = new UserWrapper();
		User user0 = userWrapper0.getUser();
		assertNull(user0);
	}
}