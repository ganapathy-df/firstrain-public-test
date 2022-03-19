/*
 * This file was automatically generated by EvoSuite
 * Thu Jun 28 17:45:11 GMT 2018
 */

package com.firstrain.web.pojo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class CategorizerEntityObjectESTest {

	@Test(timeout = 4000)
	public void test0() throws Exception {
		CategorizerEntityObject.CatEntityObjectWrapper categorizerEntityObject_CatEntityObjectWrapper0 = new CategorizerEntityObject.CatEntityObjectWrapper();
		categorizerEntityObject_CatEntityObjectWrapper0.setValid(true);
		assertTrue(categorizerEntityObject_CatEntityObjectWrapper0.isValid());
	}

	@Test(timeout = 4000)
	public void test1() throws Exception {
		CategorizerEntityObject.CatEntityObjectWrapper categorizerEntityObject_CatEntityObjectWrapper0 = new CategorizerEntityObject.CatEntityObjectWrapper();
		categorizerEntityObject_CatEntityObjectWrapper0.setDetailedMessage("`!TppLu3m5oQ");
		assertFalse(categorizerEntityObject_CatEntityObjectWrapper0.isValid());
	}

	@Test(timeout = 4000)
	public void test2() throws Exception {
		CategorizerEntityObject.CatEntityObjectWrapper categorizerEntityObject_CatEntityObjectWrapper0 = new CategorizerEntityObject.CatEntityObjectWrapper();
		boolean boolean0 = categorizerEntityObject_CatEntityObjectWrapper0.isValid();
		assertFalse(boolean0);
	}

	@Test(timeout = 4000)
	public void test3() throws Exception {
		CategorizerEntityObject.CatEntityObjectWrapper categorizerEntityObject_CatEntityObjectWrapper0 = new CategorizerEntityObject.CatEntityObjectWrapper();
		String string0 = categorizerEntityObject_CatEntityObjectWrapper0.getDetailedMessage();
		assertNull(string0);
	}

	@Test(timeout = 4000)
	public void test4() throws Exception {
		CategorizerEntityObject categorizerEntityObject0 = new CategorizerEntityObject();
		String string0 = categorizerEntityObject0.toString();
		assertEquals("CategorizerEntityObject [responseCode=0, data=null]", string0);
	}

	@Test(timeout = 4000)
	public void test5() throws Exception {
		CategorizerEntityObject categorizerEntityObject0 = new CategorizerEntityObject();
		categorizerEntityObject0.setResponseCode((-23));
		assertEquals((-23), categorizerEntityObject0.getResponseCode());
	}

	@Test(timeout = 4000)
	public void test6() throws Exception {
		CategorizerEntityObject categorizerEntityObject0 = new CategorizerEntityObject();
		CategorizerEntityObject.CatEntityObjectWrapper categorizerEntityObject_CatEntityObjectWrapper0 = new CategorizerEntityObject.CatEntityObjectWrapper();
		categorizerEntityObject0.setData(categorizerEntityObject_CatEntityObjectWrapper0);
		assertFalse(categorizerEntityObject_CatEntityObjectWrapper0.isValid());
	}

	@Test(timeout = 4000)
	public void test7() throws Exception {
		CategorizerEntityObject categorizerEntityObject0 = new CategorizerEntityObject();
		int int0 = categorizerEntityObject0.getResponseCode();
		assertEquals(0, int0);
	}

	@Test(timeout = 4000)
	public void test8() throws Exception {
		CategorizerEntityObject categorizerEntityObject0 = new CategorizerEntityObject();
		CategorizerEntityObject.CatEntityObjectWrapper categorizerEntityObject_CatEntityObjectWrapper0 = categorizerEntityObject0
				.getData();
		assertNull(categorizerEntityObject_CatEntityObjectWrapper0);
	}
}
