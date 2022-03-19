package com.firstrain.frapi.pojo.wrapper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.firstrain.frapi.domain.VisualizationData.Graph;

public class GraphSet extends BaseSet implements Serializable {

	private List<Graph> graphs = new ArrayList<Graph>();

	public List<Graph> getGraphs() {
		return graphs;
	}

	public void setGraphs(List<Graph> graphs) {
		this.graphs = graphs;
	}

	public void addGraphs(Graph graph) {
		graphs.add(graph);
	}

	public void addAllGraphs(List<Graph> graphList) {
		graphs.addAll(graphList);
	}
}
