/*
 * This file was automatically generated by EvoSuite
 * Thu Jun 28 17:34:45 GMT 2018
 */

package com.firstrain.web.pojo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;


public class MonitorSearchESTest {

	@Test(timeout = 4000)
	public void test0() throws Exception {
		MonitorSearch monitorSearch0 = new MonitorSearch();
		monitorSearch0.setQueryString("mkeu9}`(Rql&dZ&Gs4)");
		assertNull(monitorSearch0.getQueryName());
	}

	@Test(timeout = 4000)
	public void test1() throws Exception {
		MonitorSearch monitorSearch0 = new MonitorSearch();
		monitorSearch0.setQueryName("");
		assertEquals("", monitorSearch0.getQueryName());
	}

	@Test(timeout = 4000)
	public void test2() throws Exception {
		MonitorSearch monitorSearch0 = new MonitorSearch();
		String string0 = monitorSearch0.getQueryString();
		assertNull(string0);
	}

	@Test(timeout = 4000)
	public void test3() throws Exception {
		MonitorSearch monitorSearch0 = new MonitorSearch();
		String string0 = monitorSearch0.getQueryName();
		assertNull(string0);
	}
}
