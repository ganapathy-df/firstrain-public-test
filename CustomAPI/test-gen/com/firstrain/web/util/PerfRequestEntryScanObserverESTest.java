/*
 * This file was automatically generated by EvoSuite
 * Mon Jul 02 15:59:47 GMT 2018
 */

package com.firstrain.web.util;

import static org.junit.Assert.assertEquals;

import com.firstrain.utils.object.PerfRequestEntry;
import org.junit.Test;

public class PerfRequestEntryScanObserverESTest {

	@Test(timeout = 4000)
	public void test0() {
		PerfRequestEntryScanObserver perfRequestEntryScanObserver0 = new PerfRequestEntryScanObserver();
		perfRequestEntryScanObserver0.setAlertTime((-3077));
		PerfRequestEntry perfRequestEntry0 = new PerfRequestEntry();
		perfRequestEntryScanObserver0.execute(perfRequestEntry0);
		assertEquals((-9223372036854775808L), perfRequestEntry0.getStartTime());
	}
}