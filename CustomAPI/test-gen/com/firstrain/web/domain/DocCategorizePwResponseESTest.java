/*
 * This file was automatically generated by EvoSuite
 * Mon Jul 02 16:00:50 GMT 2018
 */

package com.firstrain.web.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.firstrain.web.pojo.CategorizerObject;
import com.firstrain.web.response.CategorizationServiceResponse;
import java.util.List;
import org.junit.Test;

public class DocCategorizePwResponseESTest {

	@Test(timeout = 4000)
	public void test0() {
		DocCategorizePwResponse docCategorizePwResponse0 = new DocCategorizePwResponse();
		docCategorizePwResponse0.setTaxonomyDirective(null);
		assertNull(docCategorizePwResponse0.getTaxonomyDirective());
	}

	@Test(timeout = 4000)
	public void test1() {
		DocCategorizePwResponse docCategorizePwResponse0 = new DocCategorizePwResponse();
		CategorizerObject categorizerObject0 = new CategorizerObject();
		docCategorizePwResponse0.setServiceRes(categorizerObject0);
		assertEquals(0, categorizerObject0.getResponseCode());
	}

	@Test(timeout = 4000)
	public void test2() {
		DocCategorizePwResponse docCategorizePwResponse0 = new DocCategorizePwResponse();
		List<String> list0 = docCategorizePwResponse0.getTaxonomyDirective();
		assertNull(list0);
	}

	@Test(timeout = 4000)
	public void test3() {
		DocCategorizePwResponse docCategorizePwResponse0 = new DocCategorizePwResponse();
		CategorizerObject categorizerObject0 = docCategorizePwResponse0.getServiceRes();
		assertNull(categorizerObject0);
	}

	@Test(timeout = 4000)
	public void test4() {
		DocCategorizePwResponse docCategorizePwResponse0 = new DocCategorizePwResponse();
		CategorizationServiceResponse categorizationServiceResponse0 =
				docCategorizePwResponse0.getActualresponse();
		docCategorizePwResponse0.setActualresponse(categorizationServiceResponse0);
	}
}
