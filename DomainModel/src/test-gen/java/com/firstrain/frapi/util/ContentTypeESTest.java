/*
 * This file was automatically generated by EvoSuite
 * Fri Jun 29 10:54:34 GMT 2018
 */

package com.firstrain.frapi.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class ContentTypeESTest {

	@Test(timeout = 4000)
	public void test0() throws Exception {
		ContentType contentType0 = ContentType.FILINGS_10Q;
		String string0 = contentType0.getLabel();
		assertEquals("10-Q Filings", string0);
	}

	@Test(timeout = 4000)
	public void test1() throws Exception {
		ContentType contentType0 = ContentType.MEDICAL_JOURNALS;
		int int0 = contentType0.getId();
		assertEquals(358667, int0);
	}
}