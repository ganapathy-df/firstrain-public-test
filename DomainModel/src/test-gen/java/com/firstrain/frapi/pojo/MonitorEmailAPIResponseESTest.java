/*
 * This file was automatically generated by EvoSuite
 * Thu Jun 28 17:19:51 GMT 2018
 */

package com.firstrain.frapi.pojo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.firstrain.frapi.domain.MonitorEmail;
import java.util.List;
import org.junit.Test;


public class MonitorEmailAPIResponseESTest {

	@Test(timeout = 4000)
	public void test0() throws Exception {
		MonitorEmailAPIResponse monitorEmailAPIResponse0 = new MonitorEmailAPIResponse();
		monitorEmailAPIResponse0.setStatusCode(0);
		assertEquals(0, monitorEmailAPIResponse0.getStatusCode());
	}

	@Test(timeout = 4000)
	public void test1() throws Exception {
		MonitorEmailAPIResponse monitorEmailAPIResponse0 = new MonitorEmailAPIResponse();
		monitorEmailAPIResponse0.setMonitorName(null);
		assertEquals(0L, monitorEmailAPIResponse0.getMonitorId());
	}

	@Test(timeout = 4000)
	public void test2() throws Exception {
		MonitorEmailAPIResponse monitorEmailAPIResponse0 = new MonitorEmailAPIResponse();
		monitorEmailAPIResponse0.setMonitorId((-1L));
		assertEquals((-1L), monitorEmailAPIResponse0.getMonitorId());
	}

	@Test(timeout = 4000)
	public void test3() throws Exception {
		MonitorEmailAPIResponse monitorEmailAPIResponse0 = new MonitorEmailAPIResponse();
		monitorEmailAPIResponse0.setEmails(null);
		assertEquals(0, monitorEmailAPIResponse0.getStatusCode());
	}

	@Test(timeout = 4000)
	public void test4() throws Exception {
		MonitorEmailAPIResponse monitorEmailAPIResponse0 = new MonitorEmailAPIResponse();
		monitorEmailAPIResponse0.setEmailTemplate(null);
		assertNull(monitorEmailAPIResponse0.getEmailTemplate());
	}

	@Test(timeout = 4000)
	public void test5() throws Exception {
		MonitorEmailAPIResponse monitorEmailAPIResponse0 = new MonitorEmailAPIResponse();
		int int0 = monitorEmailAPIResponse0.getStatusCode();
		assertEquals(0, int0);
	}

	@Test(timeout = 4000)
	public void test6() throws Exception {
		MonitorEmailAPIResponse monitorEmailAPIResponse0 = new MonitorEmailAPIResponse();
		String string0 = monitorEmailAPIResponse0.getMonitorName();
		assertNull(string0);
	}

	@Test(timeout = 4000)
	public void test7() throws Exception {
		MonitorEmailAPIResponse monitorEmailAPIResponse0 = new MonitorEmailAPIResponse();
		long long0 = monitorEmailAPIResponse0.getMonitorId();
		assertEquals(0L, long0);
	}

	@Test(timeout = 4000)
	public void test8() throws Exception {
		MonitorEmailAPIResponse monitorEmailAPIResponse0 = new MonitorEmailAPIResponse();
		List<MonitorEmail> list0 = monitorEmailAPIResponse0.getEmails();
		assertNull(list0);
	}

	@Test(timeout = 4000)
	public void test9() throws Exception {
		MonitorEmailAPIResponse monitorEmailAPIResponse0 = new MonitorEmailAPIResponse();
		String string0 = monitorEmailAPIResponse0.getEmailTemplate();
		assertNull(string0);
	}
}
