package com.firstrain.web.pojo;

import java.util.List;

/**
 * @author gkhanchi
 *
 */

public class MonitorConfig {

	private String id;
	private String name;
	private List<String> filters;
	private List<MonitorSearch> queries;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getFilters() {
		return filters;
	}

	public void setFilters(List<String> filters) {
		this.filters = filters;
	}

	public List<MonitorSearch> getQueries() {
		return queries;
	}

	public void setQueries(List<MonitorSearch> queries) {
		this.queries = queries;
	}
}
