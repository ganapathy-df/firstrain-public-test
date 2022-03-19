package com.firstrain.frapi.domain;

public class ChartCountSummary {
	private String entityId;
	private int oneDayDocCount;
	private int thirtyOneDaysDocCount;
	private long searchId;
	private float value; // defines the size of the node into the tree map.
	private int intensity; // variation in trends

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public int getIntensity() {
		return intensity;
	}

	public void setIntensity(int intensity) {
		this.intensity = intensity;
	}

	public long getSearchId() {
		return searchId;
	}

	public void setSearchId(long searchId) {
		this.searchId = searchId;
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public int getOneDayDocCount() {
		return oneDayDocCount;
	}

	public void setOneDayDocCount(int oneDayDocCount) {
		this.oneDayDocCount = oneDayDocCount;
	}

	public int getThirtyOneDaysDocCount() {
		return thirtyOneDaysDocCount;
	}

	public void setThirtyOneDaysDocCount(int thirtyOneDaysDocCount) {
		this.thirtyOneDaysDocCount = thirtyOneDaysDocCount;
	}
}
