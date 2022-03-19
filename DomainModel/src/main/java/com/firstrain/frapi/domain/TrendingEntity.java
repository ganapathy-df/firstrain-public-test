package com.firstrain.frapi.domain;

public class TrendingEntity {

	private String entityId;
	private int entityCount;
	private int docCount;
	private int _1DayDocCount;
	private int _31DayDocCount;

	public int get_31DayDocCount() {
		return _31DayDocCount;
	}

	public void set_31DayDocCount(int _31DayDocCount) {
		this._31DayDocCount = _31DayDocCount;
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public int getEntityCount() {
		return entityCount;
	}

	public void setEntityCount(int entityCount) {
		this.entityCount = entityCount;
	}

	public int getDocCount() {
		return docCount;
	}

	public void setDocCount(int docCount) {
		this.docCount = docCount;
	}

	public int get_1DayDocCount() {
		return _1DayDocCount;
	}

	public void set_1DayDocCount(int _1DayDocCount) {
		this._1DayDocCount = _1DayDocCount;
	}


}
