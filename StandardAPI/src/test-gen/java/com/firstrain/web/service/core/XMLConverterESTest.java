/*
 * This file was automatically generated by EvoSuite
 * Mon Jul 02 18:44:18 GMT 2018
 */

package com.firstrain.web.service.core;

import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;

public class XMLConverterESTest {

	@Test(timeout = 4000)
	public void test0() throws Exception {
		XMLConverter xMLConverter0 = new XMLConverter();
		xMLConverter0.setUnmarshaller(null);
	}

	@Test(timeout = 4000)
	public void test2() throws Exception {
		XMLConverter xMLConverter0 = new XMLConverter();
		Unmarshaller unmarshaller0 = xMLConverter0.getUnmarshaller();
		assertNull(unmarshaller0);
	}

	@Test(timeout = 4000)
	public void test3() throws Exception {
		XMLConverter xMLConverter0 = new XMLConverter();
		Marshaller marshaller0 = xMLConverter0.getMarshaller();
		assertNull(marshaller0);
	}
}
