/*
 * This file was automatically generated by EvoSuite
 * Thu Jun 28 17:25:12 GMT 2018
 */

package com.firstrain.frapi.pojo;

import static org.junit.Assert.assertNull;

import com.firstrain.frapi.domain.Search;
import com.firstrain.frapi.pojo.wrapper.BaseSet;
import java.util.List;
import org.junit.Test;


public class EmailDetailESTest {

	@Test(timeout = 4000)
	public void test0() throws Exception {
		EmailDetail emailDetail0 = new EmailDetail();
		emailDetail0.setSearches(null);
	}

	@Test(timeout = 4000)
	public void test1() throws Exception {
		EmailDetail emailDetail0 = new EmailDetail();
		BaseSet baseSet0 = new BaseSet();
		emailDetail0.setPerfStats(baseSet0);
		assertNull(baseSet0.getSectionType());
	}

	@Test(timeout = 4000)
	public void test2() throws Exception {
		EmailDetail emailDetail0 = new EmailDetail();
		List<Search> list0 = emailDetail0.getSearches();
		assertNull(list0);
	}
}
