/*
 * This file was automatically generated by EvoSuite
 * Thu Jun 28 17:47:24 GMT 2018
 */

package com.firstrain.web.pojo;

import static org.junit.Assert.assertNull;

import java.util.List;
import org.junit.Test;


public class EmailDetailESTest {

	@Test(timeout = 4000)
	public void test0() throws Exception {
		EmailDetail emailDetail0 = new EmailDetail();
		emailDetail0.setSearches(null);
	}

	@Test(timeout = 4000)
	public void test1() throws Exception {
		EmailDetail emailDetail0 = new EmailDetail();
		List<Search> list0 = emailDetail0.getSearches();
		assertNull(list0);
	}
}