/*
 * This file was automatically generated by EvoSuite
 * Thu Jun 28 17:30:27 GMT 2018
 */

package com.firstrain.web.wrapper;

import static org.junit.Assert.assertNull;

import com.firstrain.web.pojo.Entity;
import java.util.List;
import org.junit.Test;


public class EntityListWrapperESTest {

	@Test(timeout = 4000)
	public void test0() throws Exception {
		EntityListWrapper entityListWrapper0 = new EntityListWrapper();
		entityListWrapper0.setEntity(null);
	}

	@Test(timeout = 4000)
	public void test1() throws Exception {
		EntityListWrapper entityListWrapper0 = new EntityListWrapper();
		entityListWrapper0.setEntities(null);
	}

	@Test(timeout = 4000)
	public void test2() throws Exception {
		EntityListWrapper entityListWrapper0 = new EntityListWrapper();
		List<Entity> list0 = entityListWrapper0.getEntity();
		assertNull(list0);
	}

	@Test(timeout = 4000)
	public void test3() throws Exception {
		EntityListWrapper entityListWrapper0 = new EntityListWrapper();
		List<Entity> list0 = entityListWrapper0.getEntities();
		assertNull(list0);
	}
}
