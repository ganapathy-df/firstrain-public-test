/*
 * This file was automatically generated by EvoSuite
 * Mon Jul 02 17:17:41 GMT 2018
 */

package com.firstrain.frapi.service.impl;

import static org.junit.Assert.assertEquals;

import com.firstrain.frapi.events.EventObj;
import com.firstrain.frapi.events.IEvents;
import org.evosuite.runtime.mock.java.util.MockDate;
import org.junit.Test;


public class RankedEventComparatorESTest {

	@Test(timeout = 4000)
	public void test0() throws Exception {
		RankedEventComparator rankedEventComparator0 = new RankedEventComparator();
		EventObj eventObj0 = new EventObj();
		MockDate mockDate0 = new MockDate(0L);
		eventObj0.setDate(mockDate0);
		int int0 = rankedEventComparator0.compare(eventObj0, eventObj0);
		assertEquals(0, int0);
	}
}