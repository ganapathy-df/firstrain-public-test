/*
 * This file was automatically generated by EvoSuite
 * Mon Jul 02 17:11:54 GMT 2018
 */

package com.firstrain.frapi.customapirepository.impl;

import org.junit.Test;


public class DocCategorizeRepositoryImplESTest {

	@Test(timeout = 4000, expected = NullPointerException.class)
	public void test0() throws Exception {
		DocCategorizeRepositoryImpl docCategorizeRepositoryImpl0 = new DocCategorizeRepositoryImpl();
		Long long0 = 2L;
		docCategorizeRepositoryImpl0
				.insertIntoDocCategorizeResponseHistory(long0, " eWA*/'?", " eWA*/'?", " eWA*/'?");
	}

	@Test(timeout = 4000, expected = NullPointerException.class)
	public void test1() throws Exception {
		DocCategorizeRepositoryImpl docCategorizeRepositoryImpl0 = new DocCategorizeRepositoryImpl();
		docCategorizeRepositoryImpl0.insertIntoDocCategorizeFeedback(null);
	}
}
