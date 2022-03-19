package com.firstrain.frapi.pojo.wrapper;

import java.util.List;

import com.firstrain.frapi.pojo.GraphNode;

/**
 * @author Akanksha
 */

public class GraphNodeSet extends BaseSet {

	public GraphNodeSet() {}

	private Boolean primaryIndustry;

	// FIXME Why we need this constructor with every pojo object.
	public GraphNodeSet(SectionType type) {
		super(type);
	}

	private List<GraphNode> graphNodeList;

	public List<GraphNode> getGraphNodeList() {
		return graphNodeList;
	}

	public void setGraphNodeList(List<GraphNode> graphNodeList) {
		this.graphNodeList = graphNodeList;
	}

	public Boolean getPrimaryIndustry() {
		return primaryIndustry;
	}

	public void setPrimaryIndustry(Boolean primaryIndustry) {
		this.primaryIndustry = primaryIndustry;
	}
}
