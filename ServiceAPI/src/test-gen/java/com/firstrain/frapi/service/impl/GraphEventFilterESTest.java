/*
 * This file was automatically generated by EvoSuite
 * Mon Jul 02 17:24:42 GMT 2018
 */

package com.firstrain.frapi.service.impl;

import static org.junit.Assert.assertSame;

import com.firstrain.frapi.events.IEvents;
import java.util.List;
import org.junit.Test;


public class GraphEventFilterESTest {

	@Test(timeout = 4000)
	public void test0() throws Exception {
		GraphEventFilter graphEventFilter0 = new GraphEventFilter();
		List<IEvents> list0 = graphEventFilter0.filterEvents(null);
		List<IEvents> list1 = graphEventFilter0.filterEvents(list0);
		assertSame(list0, list1);
	}
}
