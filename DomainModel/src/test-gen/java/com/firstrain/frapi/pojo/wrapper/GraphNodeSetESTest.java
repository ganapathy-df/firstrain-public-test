/*
 * This file was automatically generated by EvoSuite
 * Thu Jun 28 17:16:48 GMT 2018
 */

package com.firstrain.frapi.pojo.wrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.firstrain.frapi.pojo.GraphNode;
import java.util.List;
import org.junit.Test;


public class GraphNodeSetESTest {

	@Test(timeout = 4000)
	public void test0() throws Exception {
		BaseSet.SectionType baseSet_SectionType0 = BaseSet.SectionType.VMWR;
		GraphNodeSet graphNodeSet0 = new GraphNodeSet(baseSet_SectionType0);
		Boolean boolean0 = Boolean.FALSE;
		graphNodeSet0.setPrimaryIndustry(boolean0);
		assertNull(graphNodeSet0.getTotalCount());
	}

	@Test(timeout = 4000)
	public void test1() throws Exception {
		BaseSet.SectionType baseSet_SectionType0 = BaseSet.SectionType.VMWR;
		GraphNodeSet graphNodeSet0 = new GraphNodeSet(baseSet_SectionType0);
		graphNodeSet0.setGraphNodeList(null);
		assertEquals(BaseSet.SectionType.VMWR, graphNodeSet0.getSectionType());
	}

	@Test(timeout = 4000)
	public void test2() throws Exception {
		BaseSet.SectionType baseSet_SectionType0 = BaseSet.SectionType.VMWR;
		GraphNodeSet graphNodeSet0 = new GraphNodeSet(baseSet_SectionType0);
		Boolean boolean0 = graphNodeSet0.getPrimaryIndustry();
		assertNull(boolean0);
	}

	@Test(timeout = 4000)
	public void test3() throws Exception {
		BaseSet.SectionType baseSet_SectionType0 = BaseSet.SectionType.VMWR;
		GraphNodeSet graphNodeSet0 = new GraphNodeSet(baseSet_SectionType0);
		List<GraphNode> list0 = graphNodeSet0.getGraphNodeList();
		assertNull(list0);
	}

	@Test(timeout = 4000)
	public void test4() throws Exception {
		GraphNodeSet graphNodeSet0 = new GraphNodeSet();
		assertNull(graphNodeSet0.getTotalCount());
	}
}
