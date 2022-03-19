package com.firstrain.frapi.pojo;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

public class GraphNode {
	private String id; // unique identifier
	private String label; // display caption
	private float value; // defines the size of the node into the tree map.
	private int intensity; // variation in trends
	private String smartText0; // display caption
	private String smartText; // display caption
	private String searchToken; // to show details on mouse-over/click
	private String name; // Ticker of Entity
	private String cc; // country code - for geo map
	private String query;
	private Long itemId;
	private String imageName;
	private String parent;
	private String parentToken;
	private int[] parents = null;// sector, segment, industry
	private List<GraphNode> subtree = null; // sub-tree for the node
	private Entity entityInfo;

	public GraphNode() {

	}

	public GraphNode(String id, String label, float value, int intensity) {
		this.id = id;
		this.label = label;
		this.value = value;
		this.intensity = intensity;
	}

	public String getSmartText0() {
		return smartText0;
	}

	public void setSmartText0(String smartText0) {
		this.smartText0 = smartText0;
	}

	public String getCc() {
		return cc;
	}

	public void setCc(String cc) {
		this.cc = cc;
	}

	public String getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public float getValue() {
		return value;
	}

	public int getIntensity() {
		return intensity;
	}

	public String getSmartText() {
		return smartText;
	}

	public String getSearchToken() {
		return searchToken;
	}

	public String getName() {
		return name;
	}

	public String getQuery() {
		return query;
	}

	public Long getItemId() {
		return itemId;
	}

	public String getImageName() {
		return imageName;
	}

	public String getParentToken() {
		return parentToken;
	}

	public void setParentToken(String parentToken) {
		this.parentToken = parentToken;
	}

	@JsonIgnore
	public int[] getParents() {
		return parents;
	}

	public List<GraphNode> getSubtree() {
		return subtree;
	}

	public void setSubtree(List<GraphNode> subtree) {
		this.subtree = subtree;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public void setIntensity(int intensity) {
		this.intensity = intensity;
	}

	public void setSmartText(String smartText) {
		this.smartText = smartText;
	}

	public void setSearchToken(String searchToken) {
		this.searchToken = searchToken;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public void setParents(int[] parents) {
		this.parents = parents;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public Entity getEntityInfo() {
		return entityInfo;
	}

	public void setEntityInfo(Entity entityInfo) {
		this.entityInfo = entityInfo;
	}
}
