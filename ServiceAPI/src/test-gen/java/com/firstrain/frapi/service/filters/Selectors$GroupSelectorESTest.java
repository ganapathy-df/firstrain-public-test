/*
 * This file was automatically generated by EvoSuite
 * Mon Jul 02 17:30:28 GMT 2018
 */

package com.firstrain.frapi.service.filters;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.firstrain.frapi.events.EventObj;
import com.firstrain.frapi.events.IEvents;
import org.junit.Test;


public class Selectors$GroupSelectorESTest {

	@Test(timeout = 4000)
	public void test0() throws Exception {
		IEvents.EventGroupEnum iEvents_EventGroupEnum0 = IEvents.EventGroupEnum.GROUP_PRIVATE_PLACEMENT;
		Selectors.GroupSelector selectors_GroupSelector0 = new Selectors.GroupSelector(
				iEvents_EventGroupEnum0);
		EventObj eventObj0 = new EventObj();
		eventObj0.setEventGroup(iEvents_EventGroupEnum0);
		boolean boolean0 = selectors_GroupSelector0.isSelected(eventObj0);
		assertTrue(boolean0);
	}

	@Test(timeout = 4000)
	public void test1() throws Exception {
		IEvents.EventGroupEnum iEvents_EventGroupEnum0 = IEvents.EventGroupEnum.GROUP_MGMT_CHANGE;
		Selectors.GroupSelector selectors_GroupSelector0 = new Selectors.GroupSelector(
				iEvents_EventGroupEnum0);
		EventObj eventObj0 = new EventObj();
		boolean boolean0 = selectors_GroupSelector0.isSelected(eventObj0);
		assertFalse(boolean0);
	}
}