/*
 * This file was automatically generated by EvoSuite
 * Thu Jun 28 17:17:04 GMT 2018
 */

package com.firstrain.frapi.pojo.wrapper;

import static org.junit.Assert.assertNull;

import com.firstrain.frapi.pojo.Entity;
import java.util.List;
import org.junit.Test;


public class EntityListJsonObjectESTest {

	@Test(timeout = 4000)
	public void test0() throws Exception {
		EntityListJsonObject entityListJsonObject0 = new EntityListJsonObject();
		entityListJsonObject0.setEntities(null);
		assertNull(entityListJsonObject0.isHasMore());
	}

	@Test(timeout = 4000)
	public void test1() throws Exception {
		EntityListJsonObject entityListJsonObject0 = new EntityListJsonObject();
		List<Entity> list0 = entityListJsonObject0.getEntities();
		assertNull(list0);
	}
}
