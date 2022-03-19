package com.firstrain.frapi.domain;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author Akanksha
 */

public class MonitorDetails {
	private String userId;
	private String userName;
	private String email;
	private List<MonitorInfo> myMonitorList;
	private List<MonitorInfo> groupMonitorList;
	// private List<MonitorInfo> favoriteMonitorList;
	private LinkedHashMap<String, List<MonitorInfo>> monitors;
	private List<MonitorInfo> topMonitorList;
	private List<MonitorBucketForTitle> titlesForMonitorBuckets;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<MonitorInfo> getMyMonitorList() {
		return myMonitorList;
	}

	public void setMyMonitorList(List<MonitorInfo> myMonitorList) {
		this.myMonitorList = myMonitorList;
	}

	public List<MonitorInfo> getGroupMonitorList() {
		return groupMonitorList;
	}

	public void setGroupMonitorList(List<MonitorInfo> groupMonitorList) {
		this.groupMonitorList = groupMonitorList;
	}

	// public List<MonitorInfo> getFavoriteMonitorList() {
	// return favoriteMonitorList;
	// }
	// public void setFavoriteMonitorList(List<MonitorInfo> favoriteMonitorList) {
	// this.favoriteMonitorList = favoriteMonitorList;
	// }
	public LinkedHashMap<String, List<MonitorInfo>> getMonitors() {
		return monitors;
	}

	public void setMonitors(LinkedHashMap<String, List<MonitorInfo>> monitors) {
		this.monitors = monitors;
	}

	public List<MonitorInfo> getTopMonitorList() {
		return topMonitorList;
	}

	public void setTopMonitorList(List<MonitorInfo> topMonitorList) {
		this.topMonitorList = topMonitorList;
	}

	public List<MonitorBucketForTitle> getTitlesForMonitorBuckets() {
		return titlesForMonitorBuckets;
	}

	public void setTitlesForMonitorBuckets(List<MonitorBucketForTitle> titlesForMonitorBuckets) {
		this.titlesForMonitorBuckets = titlesForMonitorBuckets;
	}
}
