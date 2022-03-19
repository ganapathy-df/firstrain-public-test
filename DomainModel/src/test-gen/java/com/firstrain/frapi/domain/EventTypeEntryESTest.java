/*
 * This file was automatically generated by EvoSuite
 * Thu Jun 28 16:55:47 GMT 2018
 */

package com.firstrain.frapi.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import org.junit.Test;


public class EventTypeEntryESTest {

	@Test(timeout = 4000)
	public void test0() throws Exception {
		EventTypeEntry eventTypeEntry0 = new EventTypeEntry();
		eventTypeEntry0.setIncluded(false);
		assertFalse(eventTypeEntry0.isIncluded());
	}

	@Test(timeout = 4000)
	public void test1() throws Exception {
		EventTypeEntry eventTypeEntry0 = new EventTypeEntry();
		eventTypeEntry0.setGetIdStr("");
		assertEquals(0, eventTypeEntry0.getGetId());
	}

	@Test(timeout = 4000)
	public void test2() throws Exception {
		EventTypeEntry eventTypeEntry0 = new EventTypeEntry();
		eventTypeEntry0.setGetId((-1));
		assertEquals((-1), eventTypeEntry0.getGetId());
	}

	@Test(timeout = 4000)
	public void test3() throws Exception {
		EventTypeEntry eventTypeEntry0 = new EventTypeEntry();
		eventTypeEntry0.setCount(2087);
		assertEquals(2087, eventTypeEntry0.getCount());
	}

	@Test(timeout = 4000)
	public void test4() throws Exception {
		EventTypeEntry eventTypeEntry0 = new EventTypeEntry();
		boolean boolean0 = eventTypeEntry0.isIncluded();
		assertFalse(boolean0);
	}

	@Test(timeout = 4000)
	public void test5() throws Exception {
		EventTypeEntry eventTypeEntry0 = new EventTypeEntry();
		String string0 = eventTypeEntry0.getGetIdStr();
		assertNull(string0);
	}

	@Test(timeout = 4000)
	public void test6() throws Exception {
		EventTypeEntry eventTypeEntry0 = new EventTypeEntry();
		int int0 = eventTypeEntry0.getGetId();
		assertEquals(0, int0);
	}

	@Test(timeout = 4000)
	public void test7() throws Exception {
		EventTypeEntry eventTypeEntry0 = new EventTypeEntry();
		int int0 = eventTypeEntry0.getCount();
		assertEquals(0, int0);
	}
}
