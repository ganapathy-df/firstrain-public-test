/*
 * This file was automatically generated by EvoSuite
 * Thu Jun 28 17:48:05 GMT 2018
 */

package com.firstrain.web.pojo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import org.junit.Test;


public class SearchResultInputBeanESTest {

	@Test(timeout = 4000)
	public void test00() throws Exception {
		SearchResultInputBean searchResultInputBean0 = new SearchResultInputBean();
		searchResultInputBean0.setType(null);
		assertEquals(10, (int) searchResultInputBean0.getCount());
		assertEquals(7, (int) searchResultInputBean0.getDaysCount());
	}

	@Test(timeout = 4000)
	public void test01() throws Exception {
		SearchResultInputBean searchResultInputBean0 = new SearchResultInputBean();
		searchResultInputBean0.setSecondaryCatIds(null);
		assertEquals(10, (int) searchResultInputBean0.getCount());
		assertEquals(7, (int) searchResultInputBean0.getDaysCount());
	}

	@Test(timeout = 4000)
	public void test02() throws Exception {
		SearchResultInputBean searchResultInputBean0 = new SearchResultInputBean();
		Integer integer0 = 0;
		searchResultInputBean0.setScope(integer0);
		assertEquals(7, (int) searchResultInputBean0.getDaysCount());
		assertEquals(10, (int) searchResultInputBean0.getCount());
	}

	@Test(timeout = 4000)
	public void test03() throws Exception {
		SearchResultInputBean searchResultInputBean0 = new SearchResultInputBean();
		searchResultInputBean0.setQ(null);
		assertEquals(10, (int) searchResultInputBean0.getCount());
		assertEquals(7, (int) searchResultInputBean0.getDaysCount());
	}

	@Test(timeout = 4000)
	public void test04() throws Exception {
		SearchResultInputBean searchResultInputBean0 = new SearchResultInputBean();
		Long long0 = 0L;
		searchResultInputBean0.setPrimaryCatId(long0);
		assertEquals(7, (int) searchResultInputBean0.getDaysCount());
		assertEquals(10, (int) searchResultInputBean0.getCount());
	}

	@Test(timeout = 4000)
	public void test05() throws Exception {
		SearchResultInputBean searchResultInputBean0 = new SearchResultInputBean();
		assertEquals(7, (int) searchResultInputBean0.getDaysCount());

		Integer integer0 = (-1355);
		searchResultInputBean0.setDaysCount(integer0);
		assertEquals(10, (int) searchResultInputBean0.getCount());
	}

	@Test(timeout = 4000)
	public void test06() throws Exception {
		SearchResultInputBean searchResultInputBean0 = new SearchResultInputBean();
		assertEquals(10, (int) searchResultInputBean0.getCount());

		Integer integer0 = 0;
		searchResultInputBean0.setCount(integer0);
		assertEquals(7, (int) searchResultInputBean0.getDaysCount());
	}

	@Test(timeout = 4000)
	public void test07() throws Exception {
		SearchResultInputBean searchResultInputBean0 = new SearchResultInputBean();
		searchResultInputBean0.setAdvanceSort(true);
		assertTrue(searchResultInputBean0.isAdvanceSort());
	}

	@Test(timeout = 4000)
	public void test08() throws Exception {
		SearchResultInputBean searchResultInputBean0 = new SearchResultInputBean();
		searchResultInputBean0.isAdvanceSort();
		assertEquals(7, (int) searchResultInputBean0.getDaysCount());
		assertEquals(10, (int) searchResultInputBean0.getCount());
	}

	@Test(timeout = 4000)
	public void test09() throws Exception {
		SearchResultInputBean searchResultInputBean0 = new SearchResultInputBean();
		searchResultInputBean0.getType();
		assertEquals(10, (int) searchResultInputBean0.getCount());
		assertEquals(7, (int) searchResultInputBean0.getDaysCount());
	}

	@Test(timeout = 4000)
	public void test10() throws Exception {
		SearchResultInputBean searchResultInputBean0 = new SearchResultInputBean();
		searchResultInputBean0.getSecondaryCatIds();
		assertEquals(10, (int) searchResultInputBean0.getCount());
		assertEquals(7, (int) searchResultInputBean0.getDaysCount());
	}

	@Test(timeout = 4000)
	public void test11() throws Exception {
		SearchResultInputBean searchResultInputBean0 = new SearchResultInputBean();
		searchResultInputBean0.getScope();
		assertEquals(7, (int) searchResultInputBean0.getDaysCount());
		assertEquals(10, (int) searchResultInputBean0.getCount());
	}

	@Test(timeout = 4000)
	public void test12() throws Exception {
		SearchResultInputBean searchResultInputBean0 = new SearchResultInputBean();
		searchResultInputBean0.getQ();
		assertEquals(7, (int) searchResultInputBean0.getDaysCount());
		assertEquals(10, (int) searchResultInputBean0.getCount());
	}

	@Test(timeout = 4000)
	public void test13() throws Exception {
		SearchResultInputBean searchResultInputBean0 = new SearchResultInputBean();
		searchResultInputBean0.getPrimaryCatId();
		assertEquals(10, (int) searchResultInputBean0.getCount());
		assertEquals(7, (int) searchResultInputBean0.getDaysCount());
	}

	@Test(timeout = 4000)
	public void test14() throws Exception {
		SearchResultInputBean searchResultInputBean0 = new SearchResultInputBean();
		Integer integer0 = searchResultInputBean0.getDaysCount();
		assertEquals(7, (int) integer0);
		assertEquals(10, (int) searchResultInputBean0.getCount());
	}

	@Test(timeout = 4000)
	public void test15() throws Exception {
		SearchResultInputBean searchResultInputBean0 = new SearchResultInputBean();
		Integer integer0 = searchResultInputBean0.getCount();
		assertEquals(7, (int) searchResultInputBean0.getDaysCount());
		assertEquals(10, (int) integer0);
	}
}