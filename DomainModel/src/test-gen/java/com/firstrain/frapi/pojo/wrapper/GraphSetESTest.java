/*
 * This file was automatically generated by EvoSuite
 * Thu Jun 28 17:17:20 GMT 2018
 */

package com.firstrain.frapi.pojo.wrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.firstrain.frapi.domain.VisualizationData;
import java.util.List;
import org.junit.Test;


public class GraphSetESTest {

	@Test(timeout = 4000)
	public void test0() throws Exception {
		GraphSet graphSet0 = new GraphSet();
		List<VisualizationData.Graph> list0 = graphSet0.getGraphs();
		graphSet0.setGraphs(list0);
		assertEquals(0, list0.size());
	}

	@Test(timeout = 4000)
	public void test1() throws Exception {
		GraphSet graphSet0 = new GraphSet();
		VisualizationData.ChartType visualizationData_ChartType0 = VisualizationData.ChartType.TREE_COMPANY;
		VisualizationData.Graph visualizationData_Graph0 = new VisualizationData.Graph(
				"G xjT&6)<y>>OGz3", visualizationData_ChartType0);
		graphSet0.addGraphs(visualizationData_Graph0);
		assertNull(graphSet0.getSectionType());
	}

	@Test(timeout = 4000)
	public void test2() throws Exception {
		GraphSet graphSet0 = new GraphSet();
		List<VisualizationData.Graph> list0 = graphSet0.getGraphs();
		graphSet0.addAllGraphs(list0);
		assertNull(graphSet0.isHasMore());
	}
}
