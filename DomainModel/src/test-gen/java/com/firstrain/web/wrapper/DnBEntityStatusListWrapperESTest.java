/*
 * This file was automatically generated by EvoSuite
 * Thu Jun 28 17:30:11 GMT 2018
 */

package com.firstrain.web.wrapper;

import static org.junit.Assert.assertNull;

import com.firstrain.web.pojo.DnBEntityStatus;
import java.util.List;
import org.junit.Test;


public class DnBEntityStatusListWrapperESTest {

	@Test(timeout = 4000)
	public void test0() throws Exception {
		DnBEntityStatusListWrapper dnBEntityStatusListWrapper0 = new DnBEntityStatusListWrapper();
		dnBEntityStatusListWrapper0.setEntity(null);
	}

	@Test(timeout = 4000)
	public void test1() throws Exception {
		DnBEntityStatusListWrapper dnBEntityStatusListWrapper0 = new DnBEntityStatusListWrapper();
		List<DnBEntityStatus> list0 = dnBEntityStatusListWrapper0.getEntity();
		assertNull(list0);
	}
}
