/*
 * This file was automatically generated by EvoSuite
 * Fri Jun 29 10:55:51 GMT 2018
 */

package com.firstrain.frapi.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;


public class DefaultEnumsESTest {

	@Test(timeout = 4000)
	public void test0() throws Exception {
		DefaultEnums.TagType defaultEnums_TagType0 = DefaultEnums.TagType.SINGLE_ACCOUNT_CUSTOMER;
		int int0 = defaultEnums_TagType0.getOrder();
		assertEquals(20, int0);
	}

	@Test(timeout = 4000)
	public void test1() throws Exception {
		DefaultEnums.RelevanceBand defaultEnums_RelevanceBand0 = DefaultEnums.RelevanceBand.HIGH;
		short short0 = defaultEnums_RelevanceBand0.getValue();
		assertEquals((short) 3, short0);
	}

	@Test(timeout = 4000)
	public void test2() throws Exception {
		DefaultEnums.RelevanceBand defaultEnums_RelevanceBand0 = DefaultEnums.RelevanceBand.HIGH;
		String string0 = defaultEnums_RelevanceBand0.getName();
		assertEquals("High", string0);
	}

	@Test(timeout = 4000)
	public void test3() throws Exception {
		DefaultEnums.MatchedEntityConfidenceScore defaultEnums_MatchedEntityConfidenceScore0 = DefaultEnums.MatchedEntityConfidenceScore.VERY_HIGH;
		short short0 = defaultEnums_MatchedEntityConfidenceScore0.getValue();
		assertEquals((short) 4, short0);
	}

	@Test(timeout = 4000)
	public void test4() throws Exception {
		DefaultEnums.MatchedEntityConfidenceScore defaultEnums_MatchedEntityConfidenceScore0 = DefaultEnums.MatchedEntityConfidenceScore.VERY_HIGH;
		String string0 = defaultEnums_MatchedEntityConfidenceScore0.getName();
		assertEquals("VeryHigh", string0);
	}

	@Test(timeout = 4000)
	public void test5() throws Exception {
		DefaultEnums.EventTypeEnum defaultEnums_EventTypeEnum0 = DefaultEnums.EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_5_04;
		defaultEnums_EventTypeEnum0.setLabel(null);
		assertNull(defaultEnums_EventTypeEnum0.getLabel());
	}

	@Test(timeout = 4000)
	public void test6() throws Exception {
		DefaultEnums.EventTypeEnum defaultEnums_EventTypeEnum0 = DefaultEnums.EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_5_03;
		String string0 = defaultEnums_EventTypeEnum0.getLabel();
		assertEquals(
				"Filed an 8K Statement on Amendments to Articles of Incorporation or Bylaws; Change in Fiscal Year",
				string0);
	}

	@Test(timeout = 4000)
	public void test7() throws Exception {
		DefaultEnums.CoversationStarterType defaultEnums_CoversationStarterType0 = DefaultEnums.CoversationStarterType.INDUSTRY_NEWS;
		defaultEnums_CoversationStarterType0.setLabel("");
		assertEquals("", defaultEnums_CoversationStarterType0.getLabel());
	}

	@Test(timeout = 4000)
	public void test8() throws Exception {
		DefaultEnums.CoversationStarterType defaultEnums_CoversationStarterType0 = DefaultEnums.CoversationStarterType.LEAD_COMMENTARY;
		String string0 = defaultEnums_CoversationStarterType0.getLabel();
		assertEquals("Lead Commentary", string0);
	}

	@Test(timeout = 4000)
	public void test9() throws Exception {
		DefaultEnums defaultEnums0 = new DefaultEnums();
	}
}
