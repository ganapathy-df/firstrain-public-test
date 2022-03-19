/*
 * This file was automatically generated by EvoSuite
 * Thu Jun 28 17:10:04 GMT 2018
 */

package com.firstrain.frapi.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;
import org.junit.Test;


public class SearchESTest {

	@Test(timeout = 4000)
	public void test0() throws Exception {
		Search search0 = new Search();
		search0.setSearchQuery("YcN9dkc)v3@O_sevW");
		assertNull(search0.getSearchId());
	}

	@Test(timeout = 4000)
	public void test1() throws Exception {
		Search search0 = new Search();
		search0.setSearchName("YcN9dkc)v3@O_sevW");
		assertNull(search0.getSearchFilter());
	}

	@Test(timeout = 4000)
	public void test2() throws Exception {
		Search search0 = new Search();
		search0.setSearchId("ZZ`Q_,<M~(>*Ixg\"D$2");
		assertEquals("ZZ`Q_,<M~(>*Ixg\"D$2", search0.getSearchId());
	}

	@Test(timeout = 4000)
	public void test3() throws Exception {
		Search search0 = new Search();
		search0.setSearchFilter("Cy5m<$l20N;t-PhbpK");
		assertNull(search0.getSearchQuery());
	}

	@Test(timeout = 4000)
	public void test4() throws Exception {
		Search search0 = new Search();
		search0.setDocuments(null);
		assertNull(search0.getSearchQuery());
	}

	@Test(timeout = 4000)
	public void test5() throws Exception {
		Search search0 = new Search();
		String string0 = search0.getSearchQuery();
		assertNull(string0);
	}

	@Test(timeout = 4000)
	public void test6() throws Exception {
		Search search0 = new Search();
		String string0 = search0.getSearchName();
		assertNull(string0);
	}

	@Test(timeout = 4000)
	public void test7() throws Exception {
		Search search0 = new Search();
		String string0 = search0.getSearchId();
		assertNull(string0);
	}

	@Test(timeout = 4000)
	public void test8() throws Exception {
		Search search0 = new Search();
		String string0 = search0.getSearchFilter();
		assertNull(string0);
	}
}