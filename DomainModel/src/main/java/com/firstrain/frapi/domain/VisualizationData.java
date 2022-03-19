package com.firstrain.frapi.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.firstrain.frapi.pojo.wrapper.BaseSet;
import com.firstrain.utils.object.PerfActivityType;
import com.firstrain.utils.object.PerfMonitor;
import com.firstrain.utils.object.PerfRequestEntry;

public class VisualizationData extends BaseSet {

	public Map<ChartType, Graph> graphs = null;
	private Entity entity = null;

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}


	public static enum ChartType {
		TREE_MONITOR_SEARCH, TREE_COMPANY, TREE_TOPICS, ACC_METER, GEO_WORLD, GEO_US,
	}

	public static class Graph {
		public String chartId;
		public ChartType chartType;
		public List<Node> nodes;

		public Graph() {}

		public Graph(String chartId, ChartType chartType) {
			this.chartId = chartId;
			this.chartType = chartType;
		}
	}

	public static class NodeBucket {
		public int parentId;
		public int count;
		public List<Node> nodes = new ArrayList<Node>();

		public NodeBucket(int parentId) {
			this.parentId = parentId;
		}
	}

	public static class Node {
		public String id; // unique identifier
		public String label; // display caption
		public float value; // defines the size of the node into the tree map.
		public int intensity; // variation in trends
		public String smartText0; // display caption
		public String smartText; // display caption
		public String searchToken; // to show details on mouse-over/click
		public String name; // Ticker of Entity
		public String cc; // country code - for geo map
		public String query;
		public Long itemId;
		public String imageName;
		public String parent;
		public String parentToken;

		@JsonIgnore
		public int[] parents = null;// sector, segment, industry
		public List<Node> subtree = null; // sub-tree for the node

		public Node() {}

		public Node(String id, String label, float value, int intensity) {
			this.id = id;
			this.label = label;
			this.value = value;
			this.intensity = intensity;
		}
	}

	public void setPerfStats(BaseSet obj) {
		PerfRequestEntry entry = obj.getStat();
		PerfMonitor.mergeStats(entry, PerfActivityType.Others, true);
	}
}
