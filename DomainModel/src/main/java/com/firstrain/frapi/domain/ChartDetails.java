package com.firstrain.frapi.domain;

import java.util.List;

public class ChartDetails {

	private List<ChartCountSummary> monitorTrendingEntityList;
	private List<ChartCountSummary> trendingCompanyList;
	private List<ChartCountSummary> trendingTopicList;
	private List<ChartCountSummary> trendingRegionList;
	private List<ChartCountSummary> accmeterList;

	public List<ChartCountSummary> getTrendingCompanyList() {
		return trendingCompanyList;
	}

	public void setTrendingCompanyList(List<ChartCountSummary> trendingCompanyList) {
		this.trendingCompanyList = trendingCompanyList;
	}

	public List<ChartCountSummary> getTrendingTopicList() {
		return trendingTopicList;
	}

	public void setTrendingTopicList(List<ChartCountSummary> trendingTopicList) {
		this.trendingTopicList = trendingTopicList;
	}

	public List<ChartCountSummary> getMonitorTrendingEntityList() {
		return monitorTrendingEntityList;
	}

	public void setMonitorTrendingEntityList(List<ChartCountSummary> monitorTrendingEntityList) {
		this.monitorTrendingEntityList = monitorTrendingEntityList;
	}

	public List<ChartCountSummary> getTrendingRegionList() {
		return trendingRegionList;
	}

	public void setTrendingRegionList(List<ChartCountSummary> trendingRegionList) {
		this.trendingRegionList = trendingRegionList;
	}

	public List<ChartCountSummary> getAccmeterList() {
		return accmeterList;
	}

	public void setAccmeterList(List<ChartCountSummary> accmeterList) {
		this.accmeterList = accmeterList;
	}


}
