/*
 * This file was automatically generated by EvoSuite
 * Mon Jul 02 17:43:39 GMT 2018
 */

package com.firstrain.frapi.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;


public class EventConfigUtil$Filings8KConfObjESTest {

	@Test(timeout = 4000)
	public void test0() throws Exception {
		EventConfigUtil.Filings8KConfObj eventConfigUtil_Filings8KConfObj0 = new EventConfigUtil.Filings8KConfObj();
		eventConfigUtil_Filings8KConfObj0.setTitleUrl("iShS]M!(y{*2");
		assertEquals("iShS]M!(y{*2", eventConfigUtil_Filings8KConfObj0.getTitleUrl());
	}

	@Test(timeout = 4000)
	public void test1() throws Exception {
		EventConfigUtil.Filings8KConfObj eventConfigUtil_Filings8KConfObj0 = new EventConfigUtil.Filings8KConfObj();
		eventConfigUtil_Filings8KConfObj0.setTitle("");
		assertEquals(0L, eventConfigUtil_Filings8KConfObj0.getCikCode());
	}

	@Test(timeout = 4000)
	public void test2() throws Exception {
		EventConfigUtil.Filings8KConfObj eventConfigUtil_Filings8KConfObj0 = new EventConfigUtil.Filings8KConfObj();
		eventConfigUtil_Filings8KConfObj0.setCikCode(0L);
		assertNull(eventConfigUtil_Filings8KConfObj0.getTitleUrl());
	}

	@Test(timeout = 4000)
	public void test3() throws Exception {
		EventConfigUtil.Filings8KConfObj eventConfigUtil_Filings8KConfObj0 = new EventConfigUtil.Filings8KConfObj();
		String string0 = eventConfigUtil_Filings8KConfObj0.getTitleUrl();
		assertNull(string0);
	}

	@Test(timeout = 4000)
	public void test4() throws Exception {
		EventConfigUtil.Filings8KConfObj eventConfigUtil_Filings8KConfObj0 = new EventConfigUtil.Filings8KConfObj();
		String string0 = eventConfigUtil_Filings8KConfObj0.getTitle();
		assertNull(string0);
	}

	@Test(timeout = 4000)
	public void test5() throws Exception {
		EventConfigUtil.Filings8KConfObj eventConfigUtil_Filings8KConfObj0 = new EventConfigUtil.Filings8KConfObj();
		long long0 = eventConfigUtil_Filings8KConfObj0.getCikCode();
		assertEquals(0L, long0);
	}
}