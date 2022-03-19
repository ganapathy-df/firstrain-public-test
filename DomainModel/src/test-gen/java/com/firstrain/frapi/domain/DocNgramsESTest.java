/*
 * This file was automatically generated by EvoSuite
 * Thu Jun 28 16:51:06 GMT 2018
 */

package com.firstrain.frapi.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;


public class DocNgramsESTest {

	@Test(timeout = 4000)
	public void test0() throws Exception {
		DocNgrams docNgrams0 = new DocNgrams();
		Integer integer0 = (-1448);
		docNgrams0.setPhraseCount(integer0);
		assertNull(docNgrams0.getFirstLocation());
	}

	@Test(timeout = 4000)
	public void test1() throws Exception {
		DocNgrams docNgrams0 = new DocNgrams();
		docNgrams0.setPhrase("8q0vzu]g9NpYM");
		assertNull(docNgrams0.getPhraseCount());
	}

	@Test(timeout = 4000)
	public void test2() throws Exception {
		DocNgrams docNgrams0 = new DocNgrams();
		docNgrams0.setFirstLocation("8q0vzu]g9NpYM");
		assertEquals("8q0vzu]g9NpYM", docNgrams0.getFirstLocation());
	}

	@Test(timeout = 4000)
	public void test3() throws Exception {
		DocNgrams docNgrams0 = new DocNgrams();
		Integer integer0 = docNgrams0.getPhraseCount();
		assertNull(integer0);
	}

	@Test(timeout = 4000)
	public void test4() throws Exception {
		DocNgrams docNgrams0 = new DocNgrams();
		String string0 = docNgrams0.getPhrase();
		assertNull(string0);
	}

	@Test(timeout = 4000)
	public void test5() throws Exception {
		DocNgrams docNgrams0 = new DocNgrams();
		String string0 = docNgrams0.getFirstLocation();
		assertNull(string0);
	}
}