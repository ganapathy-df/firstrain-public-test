/*
 * This file was automatically generated by EvoSuite
 * Thu Jun 28 16:55:31 GMT 2018
 */

package com.firstrain.frapi.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;
import org.junit.Test;


public class EmailScheduleESTest {

	@Test(timeout = 4000)
	public void test0() throws Exception {
		EmailSchedule emailSchedule0 = new EmailSchedule();
		emailSchedule0.setMinute(0);
		assertEquals(0, emailSchedule0.getMinute());
	}

	@Test(timeout = 4000)
	public void test1() throws Exception {
		EmailSchedule emailSchedule0 = new EmailSchedule();
		emailSchedule0.setId((-4462L));
		assertEquals((-4462L), emailSchedule0.getId());
	}

	@Test(timeout = 4000)
	public void test2() throws Exception {
		EmailSchedule emailSchedule0 = new EmailSchedule();
		emailSchedule0.setHours(null);
		assertEquals(0L, emailSchedule0.getId());
	}

	@Test(timeout = 4000)
	public void test3() throws Exception {
		EmailSchedule emailSchedule0 = new EmailSchedule();
		emailSchedule0.setDays(null);
		assertEquals(0L, emailSchedule0.getId());
	}

	@Test(timeout = 4000)
	public void test4() throws Exception {
		EmailSchedule emailSchedule0 = new EmailSchedule();
		int int0 = emailSchedule0.getMinute();
		assertEquals(0, int0);
	}

	@Test(timeout = 4000)
	public void test5() throws Exception {
		EmailSchedule emailSchedule0 = new EmailSchedule();
		long long0 = emailSchedule0.getId();
		assertEquals(0L, long0);
	}

	@Test(timeout = 4000)
	public void test6() throws Exception {
		EmailSchedule emailSchedule0 = new EmailSchedule();
		List<Integer> list0 = emailSchedule0.getHours();
		assertNull(list0);
	}

	@Test(timeout = 4000)
	public void test7() throws Exception {
		EmailSchedule emailSchedule0 = new EmailSchedule();
		List<Integer> list0 = emailSchedule0.getDays();
		assertNull(list0);
	}
}
