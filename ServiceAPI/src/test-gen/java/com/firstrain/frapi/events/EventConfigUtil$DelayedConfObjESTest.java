/*
 * This file was automatically generated by EvoSuite
 * Mon Jul 02 17:43:17 GMT 2018
 */

package com.firstrain.frapi.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;


public class EventConfigUtil$DelayedConfObjESTest {

	@Test(timeout = 4000)
	public void test0() throws Exception {
		EventConfigUtil.DelayedConfObj eventConfigUtil_DelayedConfObj0 = new EventConfigUtil.DelayedConfObj();
		eventConfigUtil_DelayedConfObj0.setTitleUrl(null);
		assertNull(eventConfigUtil_DelayedConfObj0.getTitle());
	}

	@Test(timeout = 4000)
	public void test1() throws Exception {
		EventConfigUtil.DelayedConfObj eventConfigUtil_DelayedConfObj0 = new EventConfigUtil.DelayedConfObj();
		eventConfigUtil_DelayedConfObj0.setTitle(null);
		assertEquals(0L, eventConfigUtil_DelayedConfObj0.getCikCode());
	}

	@Test(timeout = 4000)
	public void test2() throws Exception {
		EventConfigUtil.DelayedConfObj eventConfigUtil_DelayedConfObj0 = new EventConfigUtil.DelayedConfObj();
		eventConfigUtil_DelayedConfObj0.setCikCode(269L);
		assertEquals(269L, eventConfigUtil_DelayedConfObj0.getCikCode());
	}

	@Test(timeout = 4000)
	public void test3() throws Exception {
		EventConfigUtil.DelayedConfObj eventConfigUtil_DelayedConfObj0 = new EventConfigUtil.DelayedConfObj();
		String string0 = eventConfigUtil_DelayedConfObj0.getTitleUrl();
		assertNull(string0);
	}

	@Test(timeout = 4000)
	public void test4() throws Exception {
		EventConfigUtil.DelayedConfObj eventConfigUtil_DelayedConfObj0 = new EventConfigUtil.DelayedConfObj();
		String string0 = eventConfigUtil_DelayedConfObj0.getTitle();
		assertNull(string0);
	}

	@Test(timeout = 4000)
	public void test5() throws Exception {
		EventConfigUtil.DelayedConfObj eventConfigUtil_DelayedConfObj0 = new EventConfigUtil.DelayedConfObj();
		long long0 = eventConfigUtil_DelayedConfObj0.getCikCode();
		assertEquals(0L, long0);
	}
}
