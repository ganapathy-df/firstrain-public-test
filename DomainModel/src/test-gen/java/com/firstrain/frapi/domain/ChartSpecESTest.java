/*
 * This file was automatically generated by EvoSuite
 * Thu Jun 28 17:04:50 GMT 2018
 */

package com.firstrain.frapi.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.firstrain.db.obj.Items;
import java.util.List;
import org.junit.Test;


public class ChartSpecESTest {

	@Test(timeout = 4000)
	public void test00() throws Exception {
		ChartSpec chartSpec0 = new ChartSpec();
		chartSpec0.setTreeNodeCount((-2334));
		assertEquals((-2334), chartSpec0.getTreeNodeCount());
	}

	@Test(timeout = 4000)
	public void test01() throws Exception {
		ChartSpec chartSpec0 = new ChartSpec();
		chartSpec0.setPartitionDay2((-2334));
		assertEquals((-2334), chartSpec0.getPartitionDay2());
	}

	@Test(timeout = 4000)
	public void test02() throws Exception {
		ChartSpec chartSpec0 = new ChartSpec();
		chartSpec0.setPartitionDay1((-2334));
		assertEquals((-2334), chartSpec0.getPartitionDay1());
	}

	@Test(timeout = 4000)
	public void test03() throws Exception {
		ChartSpec chartSpec0 = new ChartSpec();
		chartSpec0.setMonitorId(890L);
		assertEquals(890L, chartSpec0.getMonitorId());
	}

	@Test(timeout = 4000)
	public void test04() throws Exception {
		ChartSpec chartSpec0 = new ChartSpec();
		chartSpec0.setItemList(null);
		assertEquals(12, chartSpec0.getTreeNodeCount());
		assertFalse(chartSpec0.isIndustry());
	}

	@Test(timeout = 4000)
	public void test05() throws Exception {
		ChartSpec chartSpec0 = new ChartSpec();
		chartSpec0.setItemId(890);
		assertEquals(890L, chartSpec0.getItemId());
	}

	@Test(timeout = 4000)
	public void test06() throws Exception {
		ChartSpec chartSpec0 = new ChartSpec();
		chartSpec0.setIndustry(false);
		assertFalse(chartSpec0.isIndustry());
		assertEquals(12, chartSpec0.getTreeNodeCount());
	}

	@Test(timeout = 4000)
	public void test07() throws Exception {
		ChartSpec chartSpec0 = new ChartSpec();
		chartSpec0.setGraphCustomized(true);
		assertTrue(chartSpec0.isGraphCustomized());
	}

	@Test(timeout = 4000)
	public void test08() throws Exception {
		ChartSpec chartSpec0 = new ChartSpec();
		chartSpec0.setFilterQuery("");
		assertEquals(12, chartSpec0.getTreeNodeCount());
		assertFalse(chartSpec0.isIndustry());
	}

	@Test(timeout = 4000)
	public void test09() throws Exception {
		ChartSpec chartSpec0 = new ChartSpec();
		chartSpec0.setChartTypes(null);
		assertEquals(12, chartSpec0.getTreeNodeCount());
		assertFalse(chartSpec0.isIndustry());
	}

	@Test(timeout = 4000)
	public void test10() throws Exception {
		ChartSpec chartSpec0 = new ChartSpec();
		boolean boolean0 = chartSpec0.isIndustry();
		assertEquals(12, chartSpec0.getTreeNodeCount());
		assertFalse(boolean0);
	}

	@Test(timeout = 4000)
	public void test11() throws Exception {
		ChartSpec chartSpec0 = new ChartSpec();
		chartSpec0.isGraphCustomized();
		assertFalse(chartSpec0.isIndustry());
		assertEquals(12, chartSpec0.getTreeNodeCount());
	}

	@Test(timeout = 4000)
	public void test12() throws Exception {
		ChartSpec chartSpec0 = new ChartSpec();
		int int0 = chartSpec0.getTreeNodeCount();
		assertEquals(12, int0);
		assertFalse(chartSpec0.isIndustry());
	}

	@Test(timeout = 4000)
	public void test13() throws Exception {
		ChartSpec chartSpec0 = new ChartSpec();
		int int0 = chartSpec0.getPartitionDay2();
		assertEquals(12, chartSpec0.getTreeNodeCount());
		assertFalse(chartSpec0.isIndustry());
		assertEquals(0, int0);
	}

	@Test(timeout = 4000)
	public void test14() throws Exception {
		ChartSpec chartSpec0 = new ChartSpec();
		int int0 = chartSpec0.getPartitionDay1();
		assertEquals(12, chartSpec0.getTreeNodeCount());
		assertEquals(0, int0);
		assertFalse(chartSpec0.isIndustry());
	}

	@Test(timeout = 4000)
	public void test15() throws Exception {
		ChartSpec chartSpec0 = new ChartSpec();
		chartSpec0.getMonitorId();
		assertEquals(12, chartSpec0.getTreeNodeCount());
		assertFalse(chartSpec0.isIndustry());
	}

	@Test(timeout = 4000)
	public void test16() throws Exception {
		ChartSpec chartSpec0 = new ChartSpec();
		chartSpec0.getItemList();
		assertEquals(12, chartSpec0.getTreeNodeCount());
		assertFalse(chartSpec0.isIndustry());
	}

	@Test(timeout = 4000)
	public void test17() throws Exception {
		ChartSpec chartSpec0 = new ChartSpec();
		chartSpec0.getItemId();
		assertEquals(12, chartSpec0.getTreeNodeCount());
		assertFalse(chartSpec0.isIndustry());
	}

	@Test(timeout = 4000)
	public void test18() throws Exception {
		ChartSpec chartSpec0 = new ChartSpec();
		chartSpec0.getFilterQuery();
		assertFalse(chartSpec0.isIndustry());
		assertEquals(12, chartSpec0.getTreeNodeCount());
	}

	@Test(timeout = 4000)
	public void test19() throws Exception {
		ChartSpec chartSpec0 = new ChartSpec();
		chartSpec0.getChartTypes();
		assertEquals(12, chartSpec0.getTreeNodeCount());
		assertFalse(chartSpec0.isIndustry());
	}
}
