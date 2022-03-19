package com.firstrain.frapi.domain;

import java.util.Map;

public class SectionSpec {

	public static final Short DEFAULT_COUNT = 10;
	private Short start = 0;
	private Short count = DEFAULT_COUNT;
	private int width;
	private int height;
	private Boolean needPagination;
	private Boolean needBucket;
	private Boolean needTweetAccelerometer;
	private boolean needRelatedDoc;
	private Map<String, String> callbackMethodsMap;

	public Short getStart() {
		return start;
	}

	public void setStart(Short start) {
		this.start = start;
	}

	public Short getCount() {
		return count;
	}

	public void setCount(Short count) {
		this.count = count;
	}

	public int getWidth() {
		return this.width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return this.height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public Boolean getNeedPagination() {
		return needPagination;
	}

	public void setNeedPagination(Boolean needPagination) {
		this.needPagination = needPagination;
	}

	public Boolean getNeedBucket() {
		return needBucket;
	}

	public void setNeedBucket(Boolean needBucket) {
		this.needBucket = needBucket;
	}

	public Boolean getNeedTweetAccelerometer() {
		return needTweetAccelerometer;
	}

	public void setNeedTweetAccelerometer(Boolean needTweetAccelerometer) {
		this.needTweetAccelerometer = needTweetAccelerometer;
	}

	public Map<String, String> getCallbackMethodsMap() {
		return callbackMethodsMap;
	}

	public void setCallbackMethodsMap(Map<String, String> callbackMethodsMap) {
		this.callbackMethodsMap = callbackMethodsMap;
	}

	public boolean isNeedRelatedDoc() {
		return needRelatedDoc;
	}

	public void setNeedRelatedDoc(boolean needRelatedDoc) {
		this.needRelatedDoc = needRelatedDoc;
	}

	@Override
	public String toString() {
		return String.format("SectionSpec [start=%s, count=%s, width=%s, height=%s]", this.start, this.count, this.width, this.height);
	}
}
