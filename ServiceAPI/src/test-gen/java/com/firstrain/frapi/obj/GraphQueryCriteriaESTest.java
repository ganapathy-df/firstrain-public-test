/*
 * This file was automatically generated by EvoSuite
 * Mon Jul 02 17:34:06 GMT 2018
 */

package com.firstrain.frapi.obj;

import static org.junit.Assert.assertEquals;

import com.firstrain.solr.client.DateRange;
import com.firstrain.solr.client.SearchTokenEntry;
import java.util.List;
import org.junit.Test;


public class GraphQueryCriteriaESTest {

	@Test(timeout = 4000)
	public void test0() throws Exception {
		GraphQueryCriteria graphQueryCriteria0 = new GraphQueryCriteria();
		graphQueryCriteria0.setSearchTokens(null);
		assertEquals(180, graphQueryCriteria0.getNumberOfDays());
		assertEquals((-1), graphQueryCriteria0.getCategoryId());
		assertEquals(1, graphQueryCriteria0.getScope());
		assertEquals((-1), graphQueryCriteria0.getCompanyId());
	}

	@Test(timeout = 4000)
	public void test1() throws Exception {
		GraphQueryCriteria graphQueryCriteria0 = new GraphQueryCriteria();
		assertEquals(1, graphQueryCriteria0.getScope());

		graphQueryCriteria0.setScope(0);
		assertEquals(0, graphQueryCriteria0.getScope());
	}

	@Test(timeout = 4000, expected = IllegalArgumentException.class)
	public void test2() throws Exception {
		GraphQueryCriteria graphQueryCriteria0 = new GraphQueryCriteria();
		// Undeclared exception!
		graphQueryCriteria0.setNumberOfDays(3840);
	}

	@Test(timeout = 4000)
	public void test3() throws Exception {
		GraphQueryCriteria graphQueryCriteria0 = new GraphQueryCriteria();
		graphQueryCriteria0.setNumberOfDays((-4117));
		assertEquals((-4117), graphQueryCriteria0.getNumberOfDays());
	}

	@Test(timeout = 4000)
	public void test4() throws Exception {
		GraphQueryCriteria graphQueryCriteria0 = new GraphQueryCriteria();
		graphQueryCriteria0.setDateRange(null);
		assertEquals(1, graphQueryCriteria0.getScope());
		assertEquals((-1), graphQueryCriteria0.getCompanyId());
		assertEquals(180, graphQueryCriteria0.getNumberOfDays());
		assertEquals((-1), graphQueryCriteria0.getCategoryId());
	}

	@Test(timeout = 4000)
	public void test5() throws Exception {
		GraphQueryCriteria graphQueryCriteria0 = new GraphQueryCriteria();
		graphQueryCriteria0.setCompanyId((-415));
		assertEquals((-415), graphQueryCriteria0.getCompanyId());
	}

	@Test(timeout = 4000)
	public void test6() throws Exception {
		GraphQueryCriteria graphQueryCriteria0 = new GraphQueryCriteria();
		assertEquals((-1), graphQueryCriteria0.getCategoryId());

		graphQueryCriteria0.setCategoryId(0);
		assertEquals(0, graphQueryCriteria0.getCategoryId());
	}

	@Test(timeout = 4000)
	public void test7() throws Exception {
		GraphQueryCriteria graphQueryCriteria0 = new GraphQueryCriteria();
		String string0 = graphQueryCriteria0.toString();
		assertEquals(
				"GraphQueryCriteria [getCategoryId()=-1, getCompanyId()=-1, getDateRange()=null, getNumberOfDays()=180, getScope()=1, getSearchTokens()=null]",
				string0);
	}
}
