/*
 * This file was automatically generated by EvoSuite
 * Thu Jun 28 17:04:29 GMT 2018
 */

package com.firstrain.frapi.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.firstrain.frapi.util.DefaultEnums;
import org.junit.Test;


public class MonitorESTest {

	@Test(timeout = 4000)
	public void test00() throws Exception {
		Monitor monitor0 = new Monitor();
		DefaultEnums.TagType defaultEnums_TagType0 = DefaultEnums.TagType.FOLDER_CUSTOM;
		monitor0.setType(defaultEnums_TagType0);
		assertEquals(0L, monitor0.getId());
	}

	@Test(timeout = 4000)
	public void test01() throws Exception {
		Monitor monitor0 = new Monitor();
		DefaultEnums.OwnedByType defaultEnums_OwnedByType0 = DefaultEnums.OwnedByType.GROUP;
		monitor0.setOwnedByType(defaultEnums_OwnedByType0);
		assertNull(monitor0.getName());
	}

	@Test(timeout = 4000)
	public void test02() throws Exception {
		Monitor monitor0 = new Monitor();
		monitor0.setName("TYPE_EIGHT_K_EVENT_ITEM_5_05");
		assertNull(monitor0.getType());
	}

	@Test(timeout = 4000)
	public void test03() throws Exception {
		Monitor monitor0 = new Monitor();
		monitor0.setMailActive(true);
		assertTrue(monitor0.isMailActive());
	}

	@Test(timeout = 4000)
	public void test04() throws Exception {
		Monitor monitor0 = new Monitor();
		monitor0.setId(0L);
		assertFalse(monitor0.isMailActive());
	}

	@Test(timeout = 4000)
	public void test05() throws Exception {
		Monitor monitor0 = new Monitor();
		EmailSchedule emailSchedule0 = new EmailSchedule();
		monitor0.setEmailSchedule(emailSchedule0);
		assertNull(monitor0.getType());
	}

	@Test(timeout = 4000)
	public void test06() throws Exception {
		Monitor monitor0 = new Monitor();
		boolean boolean0 = monitor0.isMailActive();
		assertFalse(boolean0);
	}

	@Test(timeout = 4000)
	public void test07() throws Exception {
		Monitor monitor0 = new Monitor();
		monitor0.getType();
	}

	@Test(timeout = 4000)
	public void test08() throws Exception {
		Monitor monitor0 = new Monitor();
		monitor0.getOwnedByType();
	}

	@Test(timeout = 4000)
	public void test09() throws Exception {
		Monitor monitor0 = new Monitor();
		String string0 = monitor0.getName();
		assertNull(string0);
	}

	@Test(timeout = 4000)
	public void test10() throws Exception {
		Monitor monitor0 = new Monitor();
		long long0 = monitor0.getId();
		assertEquals(0L, long0);
	}

	@Test(timeout = 4000)
	public void test11() throws Exception {
		Monitor monitor0 = new Monitor();
		EmailSchedule emailSchedule0 = monitor0.getEmailSchedule();
		assertNull(emailSchedule0);
	}
}
