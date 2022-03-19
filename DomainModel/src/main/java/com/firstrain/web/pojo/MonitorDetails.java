/**
 * 
 */
package com.firstrain.web.pojo;

import java.util.LinkedHashMap;
import java.util.List;



/**
 * @author vgoyal
 *
 */
public class MonitorDetails {

	private LinkedHashMap<String, List<MonitorInfo>> monitors;
	private String user;
	private String userId;

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public LinkedHashMap<String, List<MonitorInfo>> getMonitors() {
		return monitors;
	}

	public void setMonitors(LinkedHashMap<String, List<MonitorInfo>> monitors) {
		this.monitors = monitors;
	}

}
