package com.firstrain.frapi.domain;

public class EventTypeEntry {

	private int getId;
	private String getIdStr;
	private int count;
	private boolean isIncluded;

	public int getGetId() {
		return getId;
	}

	public void setGetId(int getId) {
		this.getId = getId;
	}

	public String getGetIdStr() {
		return getIdStr;
	}

	public void setGetIdStr(String getIdStr) {
		this.getIdStr = getIdStr;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public boolean isIncluded() {
		return isIncluded;
	}

	public void setIncluded(boolean isIncluded) {
		this.isIncluded = isIncluded;
	}
}
