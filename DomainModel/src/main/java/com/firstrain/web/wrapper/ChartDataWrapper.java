package com.firstrain.web.wrapper;

import java.util.List;

import com.firstrain.web.pojo.ChartData;


public class ChartDataWrapper {
	private String title;
	private List<ChartData> nodes;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<ChartData> getNodes() {
		return nodes;
	}

	public void setNodes(List<ChartData> nodes) {
		this.nodes = nodes;
	}
}
