/*
 * This file was automatically generated by EvoSuite
 * Thu Jun 28 16:51:24 GMT 2018
 */

package com.firstrain.frapi.domain;

import static org.junit.Assert.assertNull;

import java.util.List;
import org.junit.Test;


public class ChartDetailsESTest {

	@Test(timeout = 4000)
	public void test0() throws Exception {
		ChartDetails chartDetails0 = new ChartDetails();
		chartDetails0.setTrendingTopicList(null);
	}

	@Test(timeout = 4000)
	public void test1() throws Exception {
		ChartDetails chartDetails0 = new ChartDetails();
		chartDetails0.setTrendingRegionList(null);
	}

	@Test(timeout = 4000)
	public void test2() throws Exception {
		ChartDetails chartDetails0 = new ChartDetails();
		chartDetails0.setTrendingCompanyList(null);
	}

	@Test(timeout = 4000)
	public void test3() throws Exception {
		ChartDetails chartDetails0 = new ChartDetails();
		chartDetails0.setMonitorTrendingEntityList(null);
	}

	@Test(timeout = 4000)
	public void test4() throws Exception {
		ChartDetails chartDetails0 = new ChartDetails();
		chartDetails0.setAccmeterList(null);
	}

	@Test(timeout = 4000)
	public void test5() throws Exception {
		ChartDetails chartDetails0 = new ChartDetails();
		List<ChartCountSummary> list0 = chartDetails0.getTrendingTopicList();
		assertNull(list0);
	}

	@Test(timeout = 4000)
	public void test6() throws Exception {
		ChartDetails chartDetails0 = new ChartDetails();
		List<ChartCountSummary> list0 = chartDetails0.getTrendingRegionList();
		assertNull(list0);
	}

	@Test(timeout = 4000)
	public void test7() throws Exception {
		ChartDetails chartDetails0 = new ChartDetails();
		List<ChartCountSummary> list0 = chartDetails0.getTrendingCompanyList();
		assertNull(list0);
	}

	@Test(timeout = 4000)
	public void test8() throws Exception {
		ChartDetails chartDetails0 = new ChartDetails();
		List<ChartCountSummary> list0 = chartDetails0.getMonitorTrendingEntityList();
		assertNull(list0);
	}

	@Test(timeout = 4000)
	public void test9() throws Exception {
		ChartDetails chartDetails0 = new ChartDetails();
		List<ChartCountSummary> list0 = chartDetails0.getAccmeterList();
		assertNull(list0);
	}
}