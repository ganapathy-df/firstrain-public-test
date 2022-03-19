/*
 * This file was automatically generated by EvoSuite
 * Mon Jul 02 17:44:01 GMT 2018
 */

package com.firstrain.frapi.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class IEventsESTest {

	@Test(timeout = 4000)
	public void test00() throws Exception {
		IEvents.EventTypeEnum iEvents_EventTypeEnum0 = IEvents.EventTypeEnum.TYPE_MGMT_CHANGE_ASSOCIATE_VICE_PRESIDENT;
		iEvents_EventTypeEnum0.setLabel("");
		//  // Unstable assertion: assertEquals(1, iEvents_EventTypeEnum0.getId());
	}

	@Test(timeout = 4000)
	public void test01() throws Exception {
		IEvents.EventTypeEnum iEvents_EventTypeEnum0 = IEvents.EventTypeEnum.TYPE_MGMT_CHANGE_ASSOCIATE_VICE_PRESIDENT;
		assertEquals(124, iEvents_EventTypeEnum0.getId());

		iEvents_EventTypeEnum0.setId(0);
		assertEquals(0, iEvents_EventTypeEnum0.getId());
	}

	@Test(timeout = 4000)
	public void test02() throws Exception {
		IEvents.EventTypeEnum iEvents_EventTypeEnum0 = IEvents.EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_ARCHITECT;
		IEvents.EventGroupEnum iEvents_EventGroupEnum0 = IEvents.EventGroupEnum.GROUP_MGMT_CHANGE;
		iEvents_EventTypeEnum0.setGroup(iEvents_EventGroupEnum0);
		assertEquals(IEvents.EventGroupEnum.GROUP_MGMT_CHANGE, iEvents_EventTypeEnum0.getGroup());
	}

	@Test(timeout = 4000)
	public void test03() throws Exception {
		IEvents.EventTypeEnum iEvents_EventTypeEnum0 = IEvents.EventTypeEnum.TYPE_MGMT_CHANGE_PRESIDENT_TURNOVER;
		boolean boolean0 = iEvents_EventTypeEnum0.isNonExecTurnover();
		assertFalse(boolean0);
	}

	@Test(timeout = 4000)
	public void test04() throws Exception {
		IEvents.EventTypeEnum iEvents_EventTypeEnum0 = IEvents.EventTypeEnum.TYPE_MGMT_CHANGE_SENIORVP_TURNOVER;
		boolean boolean0 = iEvents_EventTypeEnum0.isNonExecTurnover();
		assertTrue(boolean0);
	}

	@Test(timeout = 4000)
	public void test05() throws Exception {
		IEvents.EventTypeEnum iEvents_EventTypeEnum0 = IEvents.EventTypeEnum.TYPE_MGMT_CHANGE_SENIORVP_TURNOVER;
		boolean boolean0 = iEvents_EventTypeEnum0.isHighRankedInternalMgmtTurnover();
		assertFalse(boolean0);
	}

	@Test(timeout = 4000)
	public void test06() throws Exception {
		IEvents.EventTypeEnum iEvents_EventTypeEnum0 = IEvents.EventTypeEnum.TYPE_MGMT_CHANGE_PRESIDENT_TURNOVER;
		boolean boolean0 = iEvents_EventTypeEnum0.isHighRankedInternalMgmtTurnover();
		assertTrue(boolean0);
	}

	@Test(timeout = 4000)
	public void test07() throws Exception {
		IEvents.EventTypeEnum iEvents_EventTypeEnum0 = IEvents.EventTypeEnum.TYPE_MGMT_CHANGE_CORPORATE_GENERAL_MANAGER;
		String string0 = iEvents_EventTypeEnum0.getLabel();
		assertEquals("Executive Turnover  - Corporate General Manager", string0);
	}

	@Test(timeout = 4000)
	public void test08() throws Exception {
		IEvents.EventTypeEnum iEvents_EventTypeEnum0 = IEvents.EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_ARCHITECT;
		int int0 = iEvents_EventTypeEnum0.getId();
		assertEquals(95, int0);
	}

	@Test(timeout = 4000)
	public void test09() throws Exception {
		IEvents.EventTypeEnum iEvents_EventTypeEnum0 = IEvents.EventTypeEnum.TYPE_MGMT_CHANGE_AREA_VICE_PRESIDENT;
		IEvents.EventGroupEnum iEvents_EventGroupEnum0 = iEvents_EventTypeEnum0.getGroup();
		assertEquals(IEvents.EventGroupEnum.GROUP_MGMT_CHANGE, iEvents_EventGroupEnum0);
	}

	@Test(timeout = 4000)
	public void test10() throws Exception {
		IEvents.EventGroupEnum iEvents_EventGroupEnum0 = IEvents.EventGroupEnum.GROUP_DELAYED_FILING;
		iEvents_EventGroupEnum0.setLabel("Executive Turnover  - Vice Chairman Of Board");
		assertEquals(5, iEvents_EventGroupEnum0.getId());
	}

	@Test(timeout = 4000)
	public void test11() throws Exception {
		IEvents.EventGroupEnum iEvents_EventGroupEnum0 = IEvents.EventGroupEnum.GROUP_WEB_VOLUME;
		iEvents_EventGroupEnum0.setId(191);
		assertEquals(191, iEvents_EventGroupEnum0.getId());
	}

	@Test(timeout = 4000)
	public void test12() throws Exception {
		IEvents.EventGroupEnum iEvents_EventGroupEnum0 = IEvents.EventGroupEnum.GROUP_DELAYED_FILING;
		String string0 = iEvents_EventGroupEnum0.getLabel();
		//  // Unstable assertion: assertEquals("SEC Delay", string0);
	}

	@Test(timeout = 4000)
	public void test13() throws Exception {
		IEvents.EventGroupEnum iEvents_EventGroupEnum0 = IEvents.EventGroupEnum.GROUP_MGMT_CHANGE;
		int int0 = iEvents_EventGroupEnum0.getId();
		assertEquals(1, int0);
	}

	@Test(timeout = 4000)
	public void test14() throws Exception {
		IEvents.EventEntityTypeEnum iEvents_EventEntityTypeEnum0 = IEvents.EventEntityTypeEnum.TYPE_TOPIC;
		String string0 = iEvents_EventEntityTypeEnum0.getLabel();
		assertEquals("Topic", string0);
	}

	@Test(timeout = 4000)
	public void test15() throws Exception {
		IEvents.EventEntityTypeEnum iEvents_EventEntityTypeEnum0 = IEvents.EventEntityTypeEnum.TYPE_COMPANY;
		int int0 = iEvents_EventEntityTypeEnum0.getId();
		assertEquals(1, int0);
	}
}