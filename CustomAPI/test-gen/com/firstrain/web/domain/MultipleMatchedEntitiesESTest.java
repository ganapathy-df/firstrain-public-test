/*
 * This file was automatically generated by EvoSuite
 * Mon Jul 02 15:59:55 GMT 2018
 */

package com.firstrain.web.domain;

import static org.junit.Assert.assertNull;

import com.firstrain.web.pojo.CategorizerObject;
import java.util.Map;
import org.junit.Test;

public class MultipleMatchedEntitiesESTest {

	@Test(timeout = 4000)
	public void test0() {
		MultipleMatchedEntities multipleMatchedEntities0 = new MultipleMatchedEntities();
		multipleMatchedEntities0.setPeTopicMap(null);
	}

	@Test(timeout = 4000)
	public void test1() {
		MultipleMatchedEntities multipleMatchedEntities0 = new MultipleMatchedEntities();
		multipleMatchedEntities0.setPeCompanyMap(null);
	}

	@Test(timeout = 4000)
	public void test2() {
		MultipleMatchedEntities multipleMatchedEntities0 = new MultipleMatchedEntities();
		multipleMatchedEntities0
				.setMatchedTaxonomyMap(null);
	}

	@Test(timeout = 4000)
	public void test3() {
		MultipleMatchedEntities multipleMatchedEntities0 = new MultipleMatchedEntities();
		multipleMatchedEntities0.setCompanyMap(null);
	}

	@Test(timeout = 4000)
	public void test4() {
		MultipleMatchedEntities multipleMatchedEntities0 = new MultipleMatchedEntities();
		Map<String, CategorizerObject.CatEntity> map0 = multipleMatchedEntities0.getPeTopicMap();
		assertNull(map0);
	}

	@Test(timeout = 4000)
	public void test5() {
		MultipleMatchedEntities multipleMatchedEntities0 = new MultipleMatchedEntities();
		Map<String, CategorizerObject.CatEntity> map0 = multipleMatchedEntities0.getPeCompanyMap();
		assertNull(map0);
	}

	@Test(timeout = 4000)
	public void test6() {
		MultipleMatchedEntities multipleMatchedEntities0 = new MultipleMatchedEntities();
		Map<String, CategorizerObject.CatEntity> map0 =
				multipleMatchedEntities0.getMatchedTaxonomyMap();
		assertNull(map0);
	}

	@Test(timeout = 4000)
	public void test7() {
		MultipleMatchedEntities multipleMatchedEntities0 = new MultipleMatchedEntities();
		Map<String, CategorizerObject.CatEntity> map0 = multipleMatchedEntities0.getCompanyMap();
		assertNull(map0);
	}
}
