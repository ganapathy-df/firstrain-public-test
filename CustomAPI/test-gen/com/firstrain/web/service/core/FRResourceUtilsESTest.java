/*
 * This file was automatically generated by EvoSuite
 * Mon Jul 02 15:55:19 GMT 2018
 */

package com.firstrain.web.service.core;

import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.springframework.core.io.Resource;

public class FRResourceUtilsESTest {

	@Test(timeout = 4000)
	public void test0() {
		FRResourceUtils fRResourceUtils0 = new FRResourceUtils();
		Resource resource0 = fRResourceUtils0.getResource("");
		assertFalse(resource0.isOpen());
	}
}
