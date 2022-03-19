/*
 * This file was automatically generated by EvoSuite
 * Thu Jun 28 17:34:29 GMT 2018
 */

package com.firstrain.web.wrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;
import org.junit.Test;


public class EntityListWrapperDataESTest {

	@Test(timeout = 4000)
	public void test0() throws Exception {
		EntityListWrapperData entityListWrapperData0 = new EntityListWrapperData();
		entityListWrapperData0.setTaxonomyDirective(null);
		assertNull(entityListWrapperData0.getDocId());
	}

	@Test(timeout = 4000)
	public void test1() throws Exception {
		EntityListWrapperData entityListWrapperData0 = new EntityListWrapperData();
		entityListWrapperData0.setGuid("");
		assertEquals("", entityListWrapperData0.getGuid());
	}

	@Test(timeout = 4000)
	public void test2() throws Exception {
		EntityListWrapperData entityListWrapperData0 = new EntityListWrapperData();
		entityListWrapperData0.setDocId("");
		assertEquals("", entityListWrapperData0.getDocId());
	}

	@Test(timeout = 4000)
	public void test3() throws Exception {
		EntityListWrapperData entityListWrapperData0 = new EntityListWrapperData();
		List<String> list0 = entityListWrapperData0.getTaxonomyDirective();
		assertNull(list0);
	}

	@Test(timeout = 4000)
	public void test4() throws Exception {
		EntityListWrapperData entityListWrapperData0 = new EntityListWrapperData();
		String string0 = entityListWrapperData0.getGuid();
		assertNull(string0);
	}

	@Test(timeout = 4000)
	public void test5() throws Exception {
		EntityListWrapperData entityListWrapperData0 = new EntityListWrapperData();
		String string0 = entityListWrapperData0.getDocId();
		assertNull(string0);
	}

	@Test(timeout = 4000)
	public void test6() throws Exception {
		EntityListWrapperData entityListWrapperData0 = new EntityListWrapperData();
		EntityListWrapper entityListWrapper0 = entityListWrapperData0.getData();
		entityListWrapperData0.setData(entityListWrapper0);
		assertNull(entityListWrapperData0.getDocId());
	}
}
