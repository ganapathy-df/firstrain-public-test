/*
 * This file was automatically generated by EvoSuite
 * Thu Jun 28 17:37:02 GMT 2018
 */

package com.firstrain.web.pojo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.util.List;
import org.junit.Test;


public class EntityESTest {

	@Test(timeout = 4000)
	public void test00() throws Exception {
		Entity entity0 = new Entity();
		entity0.setSubscribed(null);
		assertNull(entity0.getAdded());
	}

	@Test(timeout = 4000)
	public void test01() throws Exception {
		Entity entity0 = new Entity();
		entity0.setSearchToken(
				"Filed an 8K Statement on Amendments to Articles of Incorporation or Bylaws; Change in Fiscal Year");
		assertNull(entity0.getMessage());
	}

	@Test(timeout = 4000)
	public void test02() throws Exception {
		Entity entity0 = new Entity();
		entity0.setRemoved(null);
		assertNull(entity0.getMessage());
	}

	@Test(timeout = 4000)
	public void test03() throws Exception {
		Entity entity0 = new Entity();
		Short short0 = (short) 22;
		entity0.setRelevanceScore(short0);
		assertNull(entity0.getName());
	}

	@Test(timeout = 4000)
	public void test04() throws Exception {
		Entity entity0 = new Entity();
		Short short0 = (short) 2;
		entity0.setRelevanceBand(short0);
		assertEquals("Medium", entity0.getRelevanceBand());
	}

	@Test(timeout = 4000)
	public void test05() throws Exception {
		Entity entity0 = new Entity();
		Short short0 = (short) 3;
		entity0.setRelevanceBand(short0);
		assertEquals("High", entity0.getRelevanceBand());
	}

	@Test(timeout = 4000)
	public void test06() throws Exception {
		Entity entity0 = new Entity();
		entity0.setRelevanceBand(null);
		assertEquals("Low", entity0.getRelevanceBand());
	}

	@Test(timeout = 4000)
	public void test07() throws Exception {
		Entity entity0 = new Entity();
		entity0.setName(null);
		assertNull(entity0.getRelevanceScore());
	}

	@Test(timeout = 4000)
	public void test08() throws Exception {
		Entity entity0 = new Entity();
		entity0.setMessage(null);
		assertNull(entity0.getId());
	}

	@Test(timeout = 4000)
	public void test09() throws Exception {
		Entity entity0 = new Entity();
		entity0.setLocation(null);
		assertNull(entity0.getRemoved());
	}

	@Test(timeout = 4000)
	public void test10() throws Exception {
		Entity entity0 = new Entity();
		entity0.setId("{\"9.H]7}");
		assertNull(entity0.getRemoved());
	}

	@Test(timeout = 4000)
	public void test11() throws Exception {
		Entity entity0 = new Entity();
		Integer integer0 = (int) (short) 22;
		entity0.setErrorCode(integer0);
		assertNull(entity0.getRelevanceBand());
	}

	@Test(timeout = 4000)
	public void test12() throws Exception {
		Entity entity0 = new Entity();
		Boolean boolean0 = Boolean.FALSE;
		entity0.setAdded(boolean0);
		assertFalse(entity0.getAdded());
	}

	@Test(timeout = 4000)
	public void test13() throws Exception {
		Entity entity0 = new Entity();
		Boolean boolean0 = entity0.getSubscribed();
		assertNull(boolean0);
	}

	@Test(timeout = 4000)
	public void test14() throws Exception {
		Entity entity0 = new Entity();
		String string0 = entity0.getSearchToken();
		assertNull(string0);
	}

	@Test(timeout = 4000)
	public void test15() throws Exception {
		Entity entity0 = new Entity();
		Boolean boolean0 = entity0.getRemoved();
		assertNull(boolean0);
	}

	@Test(timeout = 4000)
	public void test16() throws Exception {
		Entity entity0 = new Entity();
		Short short0 = entity0.getRelevanceScore();
		assertNull(short0);
	}

	@Test(timeout = 4000)
	public void test17() throws Exception {
		Entity entity0 = new Entity();
		String string0 = entity0.getRelevanceBand();
		assertNull(string0);
	}

	@Test(timeout = 4000)
	public void test18() throws Exception {
		Entity entity0 = new Entity();
		String string0 = entity0.getName();
		assertNull(string0);
	}

	@Test(timeout = 4000)
	public void test19() throws Exception {
		Entity entity0 = new Entity();
		String string0 = entity0.getMessage();
		assertNull(string0);
	}

	@Test(timeout = 4000)
	public void test21() throws Exception {
		Entity entity0 = new Entity();
		String string0 = entity0.getId();
		assertNull(string0);
	}

	@Test(timeout = 4000)
	public void test22() throws Exception {
		Entity entity0 = new Entity();
		Integer integer0 = entity0.getErrorCode();
		assertNull(integer0);
	}

	@Test(timeout = 4000)
	public void test23() throws Exception {
		Entity entity0 = new Entity();
		Boolean boolean0 = entity0.getAdded();
		assertNull(boolean0);
	}
}
