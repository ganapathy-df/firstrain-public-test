/*
 * This file was automatically generated by EvoSuite
 * Mon Jul 02 17:29:27 GMT 2018
 */

package com.firstrain.frapi.service.filters;

import com.firstrain.frapi.events.IEvents;
import com.firstrain.frapi.service.EventSelector;
import java.util.List;
import org.junit.Test;


public class BasicEventsFilterESTest {

	@Test(timeout = 4000, expected = NullPointerException.class)
	public void test0() throws Exception {
		BasicEventsFilter basicEventsFilter0 = new BasicEventsFilter((EventSelector[]) null);
		// Undeclared exception!
		basicEventsFilter0.filterEvents(null);
	}
}
