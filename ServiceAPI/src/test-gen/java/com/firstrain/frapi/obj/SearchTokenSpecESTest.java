/*
 * This file was automatically generated by EvoSuite
 * Mon Jul 02 17:32:54 GMT 2018
 */

package com.firstrain.frapi.obj;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.List;
import org.junit.Test;


public class SearchTokenSpecESTest {

	@Test(timeout = 4000)
	public void test0() throws Exception {
		SearchTokenSpec searchTokenSpec0 = new SearchTokenSpec();
		SearchTokenSpec searchTokenSpec1 = searchTokenSpec0.setSearchTokens(null);
		assertSame(searchTokenSpec0, searchTokenSpec1);
	}

	@Test(timeout = 4000)
	public void test1() throws Exception {
		SearchTokenSpec searchTokenSpec0 = new SearchTokenSpec();
		SearchTokenSpec searchTokenSpec1 = searchTokenSpec0.setExcludeSearchTokens(null);
		assertFalse(searchTokenSpec1.areSearchTokensAvailable());
	}

	@Test(timeout = 4000)
	public void test2() throws Exception {
		SearchTokenSpec searchTokenSpec0 = new SearchTokenSpec();
		List<String> list0 = searchTokenSpec0.getSearchTokens();
		assertNull(list0);
	}

	@Test(timeout = 4000)
	public void test3() throws Exception {
		SearchTokenSpec searchTokenSpec0 = new SearchTokenSpec();
		List<String> list0 = searchTokenSpec0.getExcludeSearchTokens();
		assertNull(list0);
	}

	@Test(timeout = 4000)
	public void test4() throws Exception {
		SearchTokenSpec searchTokenSpec0 = new SearchTokenSpec();
		boolean boolean0 = searchTokenSpec0.areSearchTokensAvailable();
		assertFalse(boolean0);
	}

	@Test(timeout = 4000)
	public void test5() throws Exception {
		SearchTokenSpec searchTokenSpec0 = new SearchTokenSpec();
		boolean boolean0 = searchTokenSpec0.areExcludeSearchTokensAvailable();
		assertFalse(boolean0);
	}
}
