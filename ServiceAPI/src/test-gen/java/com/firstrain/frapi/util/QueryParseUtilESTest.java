/*
 * This file was automatically generated by EvoSuite
 * Mon Jul 02 17:36:18 GMT 2018
 */

package com.firstrain.frapi.util;

import static org.junit.Assert.assertNull;

import com.firstrain.db.obj.Items;
import com.firstrain.solr.client.SearchSpec;
import java.util.List;
import org.junit.Test;


public class QueryParseUtilESTest {

	@Test(timeout = 4000, expected = NullPointerException.class)
	public void test0() throws Exception {
		// Undeclared exception!
		QueryParseUtil.parseSearchSpec(null, 11, 0);
	}

	@Test(timeout = 4000)
	public void test1() throws Exception {
		SearchSpec searchSpec0 = QueryParseUtil.parse(",AwuL oglkoH");
		assertNull(searchSpec0);
	}

	@Test(timeout = 4000, expected = NullPointerException.class)
	public void test2() throws Exception {
		String[] stringArray0 = new String[7];
		// Undeclared exception!
		QueryParseUtil.getMonitorSearchSpec(null, stringArray0, null);
	}

	@Test(timeout = 4000)
	public void test3() throws Exception {
		QueryParseUtil queryParseUtil0 = new QueryParseUtil();
	}
}