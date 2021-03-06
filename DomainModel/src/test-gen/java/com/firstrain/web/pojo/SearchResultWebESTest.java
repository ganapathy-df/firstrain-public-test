/*
 * This file was automatically generated by EvoSuite
 * Thu Jun 28 17:41:53 GMT 2018
 */

package com.firstrain.web.pojo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.firstrain.frapi.pojo.wrapper.DocumentSet;
import java.util.List;
import org.junit.Test;


public class SearchResultWebESTest {

	@Test(timeout = 4000)
	public void test0() throws Exception {
		SearchResultWeb searchResultWeb0 = new SearchResultWeb();
		searchResultWeb0.setTopicIdHavingDocs(null);
		assertNull(searchResultWeb0.getPrimaryRegion());
	}

	@Test(timeout = 4000)
	public void test1() throws Exception {
		SearchResultWeb searchResultWeb0 = new SearchResultWeb();
		searchResultWeb0.setPrimaryRegion("");
		assertEquals("", searchResultWeb0.getPrimaryRegion());
	}

	@Test(timeout = 4000)
	public void test2() throws Exception {
		SearchResultWeb searchResultWeb0 = new SearchResultWeb();
		searchResultWeb0.setDocumentSet(null);
		assertNull(searchResultWeb0.getPrimaryRegion());
	}

	@Test(timeout = 4000)
	public void test3() throws Exception {
		SearchResultWeb searchResultWeb0 = new SearchResultWeb();
		List<Entity> list0 = searchResultWeb0.getTopicIdHavingDocs();
		assertEquals(0, list0.size());
	}

	@Test(timeout = 4000)
	public void test4() throws Exception {
		SearchResultWeb searchResultWeb0 = new SearchResultWeb();
		String string0 = searchResultWeb0.getPrimaryRegion();
		assertNull(string0);
	}

	@Test(timeout = 4000)
	public void test5() throws Exception {
		SearchResultWeb searchResultWeb0 = new SearchResultWeb();
		DocumentSet documentSet0 = searchResultWeb0.getDocumentSet();
		assertNull(documentSet0);
	}
}
