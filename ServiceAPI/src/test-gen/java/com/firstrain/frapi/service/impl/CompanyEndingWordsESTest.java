/*
 * This file was automatically generated by EvoSuite
 * Mon Jul 02 17:27:17 GMT 2018
 */

package com.firstrain.frapi.service.impl;

import static org.junit.Assert.assertEquals;

import java.util.Set;
import org.junit.Test;


public class CompanyEndingWordsESTest {

	@Test(timeout = 4000)
	public void test0() throws Exception {
		String[] stringArray0 = new String[4];
		CompanyEndingWords.main(stringArray0);
		assertEquals(4, stringArray0.length);
	}

	@Test(timeout = 4000)
	public void test1() throws Exception {
		CompanyEndingWords companyEndingWords0 = new CompanyEndingWords();
		String string0 = companyEndingWords0.trimCompanyEndingWord("/");
		assertEquals("/", string0);
	}

	@Test(timeout = 4000)
	public void test2() throws Exception {
		CompanyEndingWords companyEndingWords0 = new CompanyEndingWords();
		Set<String> set0 = companyEndingWords0.getCompanyEndingWords();
		assertEquals(0, set0.size());
	}
}
