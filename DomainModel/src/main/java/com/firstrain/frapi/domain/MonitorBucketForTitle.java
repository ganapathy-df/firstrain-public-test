package com.firstrain.frapi.domain;


public class MonitorBucketForTitle {
	private String monitorBucket;
	private String title;

	// default constructor
	public MonitorBucketForTitle() {}

	public MonitorBucketForTitle(String bucketName, String title) {
		this.monitorBucket = bucketName;
		this.title = title;
	}

	public String getMonitorBucket() {
		return monitorBucket;
	}

	public void setMonitorBucket(String monitorBucket) {
		this.monitorBucket = monitorBucket;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
