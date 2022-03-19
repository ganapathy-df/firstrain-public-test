package com.firstrain.web.pojo;

import java.util.List;

public class SearchResultInputBean {
	private List<String> q;
	private Integer scope;
	private Integer daysCount = 7;
	private Integer count = 10;
	private String type;

	private Long primaryCatId;
	private List<Long> secondaryCatIds;
	private boolean advanceSort;

	public Integer getDaysCount() {
		return daysCount;
	}

	public void setDaysCount(Integer daysCount) {
		this.daysCount = daysCount;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<String> getQ() {
		return q;
	}

	public void setQ(List<String> q) {
		this.q = q;
	}

	public Integer getScope() {
		return scope;
	}

	public void setScope(Integer scope) {
		this.scope = scope;
	}

	public Long getPrimaryCatId() {
		return primaryCatId;
	}

	public void setPrimaryCatId(Long primaryCatId) {
		this.primaryCatId = primaryCatId;
	}

	public List<Long> getSecondaryCatIds() {
		return secondaryCatIds;
	}

	public void setSecondaryCatIds(List<Long> secondaryCatIds) {
		this.secondaryCatIds = secondaryCatIds;
	}

	public boolean isAdvanceSort() {
		return advanceSort;
	}

	public void setAdvanceSort(boolean advanceSort) {
		this.advanceSort = advanceSort;
	}

}
