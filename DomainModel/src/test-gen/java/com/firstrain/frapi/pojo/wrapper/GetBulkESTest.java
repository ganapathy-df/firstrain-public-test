/*
 * This file was automatically generated by EvoSuite
 * Thu Jun 28 17:18:54 GMT 2018
 */

package com.firstrain.frapi.pojo.wrapper;

import static org.junit.Assert.assertNull;

import com.firstrain.frapi.domain.Ng;
import java.util.Map;
import org.junit.Test;


public class GetBulkESTest {

	@Test(timeout = 4000)
	public void test0() throws Exception {
		GetBulk getBulk0 = new GetBulk();
		Ng ng0 = new Ng();
		getBulk0.setNg(ng0);
	}

	@Test(timeout = 4000)
	public void test1() throws Exception {
		GetBulk getBulk0 = new GetBulk();
		getBulk0.setEntityLinking(null);
	}

	@Test(timeout = 4000)
	public void test2() throws Exception {
		GetBulk getBulk0 = new GetBulk();
		Ng ng0 = getBulk0.getNg();
		assertNull(ng0);
	}

	@Test(timeout = 4000)
	public void test3() throws Exception {
		GetBulk getBulk0 = new GetBulk();
		Map<String, Map<String, String>> map0 = getBulk0.getEntityLinking();
		assertNull(map0);
	}
}
