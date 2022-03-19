/*
 * This file was automatically generated by EvoSuite
 * Thu Jun 28 17:01:49 GMT 2018
 */

package com.firstrain.frapi.domain;

import static org.junit.Assert.assertNull;

import com.firstrain.frapi.pojo.wrapper.DocumentSet;
import com.firstrain.frapi.pojo.wrapper.EventSet;
import org.junit.Test;


public class MetaEventESTest {

	@Test(timeout = 4000)
	public void test0() throws Exception {
		MetaEvent metaEvent0 = new MetaEvent();
		metaEvent0.setRelatedEvent(null);
	}

	@Test(timeout = 4000)
	public void test1() throws Exception {
		MetaEvent metaEvent0 = new MetaEvent();
		DocumentSet documentSet0 = new DocumentSet();
		metaEvent0.setRelatedDocument(documentSet0);
		assertNull(documentSet0.getTotalCount());
	}

	@Test(timeout = 4000)
	public void test2() throws Exception {
		MetaEvent metaEvent0 = new MetaEvent();
		EventSet eventSet0 = metaEvent0.getRelatedEvent();
		assertNull(eventSet0);
	}

	@Test(timeout = 4000)
	public void test3() throws Exception {
		MetaEvent metaEvent0 = new MetaEvent();
		DocumentSet documentSet0 = metaEvent0.getRelatedDocument();
		assertNull(documentSet0);
	}
}