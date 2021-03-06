/*
 * This file was automatically generated by EvoSuite
 * Thu Jun 28 17:42:11 GMT 2018
 */

package com.firstrain.web.pojo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;
import org.junit.Test;


public class MonitorConfigESTest {

	@Test(timeout = 4000)
	public void test0() throws Exception {
		MonitorConfig monitorConfig0 = new MonitorConfig();
		monitorConfig0.setQueries(null);
		assertNull(monitorConfig0.getName());
	}

	@Test(timeout = 4000)
	public void test1() throws Exception {
		MonitorConfig monitorConfig0 = new MonitorConfig();
		monitorConfig0.setName("com.firstrain.web.pojo.MonitorConfig");
		assertNull(monitorConfig0.getId());
	}

	@Test(timeout = 4000)
	public void test2() throws Exception {
		MonitorConfig monitorConfig0 = new MonitorConfig();
		monitorConfig0.setId("com.firstrain.web.pojo.MonitorConfig");
		assertEquals("com.firstrain.web.pojo.MonitorConfig", monitorConfig0.getId());
	}

	@Test(timeout = 4000)
	public void test3() throws Exception {
		MonitorConfig monitorConfig0 = new MonitorConfig();
		monitorConfig0.setFilters(null);
		assertNull(monitorConfig0.getName());
	}

	@Test(timeout = 4000)
	public void test4() throws Exception {
		MonitorConfig monitorConfig0 = new MonitorConfig();
		List<MonitorSearch> list0 = monitorConfig0.getQueries();
		assertNull(list0);
	}

	@Test(timeout = 4000)
	public void test5() throws Exception {
		MonitorConfig monitorConfig0 = new MonitorConfig();
		String string0 = monitorConfig0.getName();
		assertNull(string0);
	}

	@Test(timeout = 4000)
	public void test6() throws Exception {
		MonitorConfig monitorConfig0 = new MonitorConfig();
		String string0 = monitorConfig0.getId();
		assertNull(string0);
	}

	@Test(timeout = 4000)
	public void test7() throws Exception {
		MonitorConfig monitorConfig0 = new MonitorConfig();
		List<String> list0 = monitorConfig0.getFilters();
		assertNull(list0);
	}
}
