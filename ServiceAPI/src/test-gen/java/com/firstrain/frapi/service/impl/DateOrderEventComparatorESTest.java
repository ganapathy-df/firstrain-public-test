/*
 * This file was automatically generated by EvoSuite
 * Mon Jul 02 17:16:27 GMT 2018
 */

package com.firstrain.frapi.service.impl;

import static org.junit.Assert.assertEquals;

import com.firstrain.frapi.events.EventObj;
import com.firstrain.frapi.events.IEvents;
import org.evosuite.runtime.mock.java.util.MockDate;
import org.junit.Test;


public class DateOrderEventComparatorESTest {

	@Test(timeout = 4000)
	public void test0() throws Exception {
		DateOrderEventComparator dateOrderEventComparator0 = new DateOrderEventComparator();
		EventObj eventObj0 = new EventObj();
		MockDate mockDate0 = new MockDate();
		eventObj0.setDate(mockDate0);
		EventObj eventObj1 = new EventObj();
		MockDate mockDate1 = new MockDate(1, 1, 1, 0, 0);
		eventObj1.setDate(mockDate1);
		int int0 = dateOrderEventComparator0.compare(eventObj1, eventObj0);
		assertEquals(1, int0);
	}

	@Test(timeout = 4000)
	public void test1() throws Exception {
		DateOrderEventComparator dateOrderEventComparator0 = new DateOrderEventComparator();
		EventObj eventObj0 = new EventObj();
		MockDate mockDate0 = new MockDate();
		eventObj0.setDate(mockDate0);
		int int0 = dateOrderEventComparator0.compare(eventObj0, eventObj0);
		assertEquals(0, int0);
	}
}
