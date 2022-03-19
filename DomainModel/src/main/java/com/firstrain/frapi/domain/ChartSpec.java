package com.firstrain.frapi.domain;

import java.util.List;

import com.firstrain.db.obj.Items;
import com.firstrain.frapi.domain.VisualizationData.ChartType;

public class ChartSpec {

	long monitorId;
	long itemId;
	String filterQuery;
	private boolean isIndustry = false;
	List<Items> itemList = null;
	private int treeNodeCount = 12;
	private boolean isGraphCustomized;
	private int partitionDay1;
	private int partitionDay2;
	List<ChartType> chartTypes = null;


	public List<ChartType> getChartTypes() {
		return chartTypes;
	}

	public void setChartTypes(List<ChartType> chartTypes) {
		this.chartTypes = chartTypes;
	}

	public List<Items> getItemList() {
		return itemList;
	}

	public void setItemList(List<Items> itemList) {
		this.itemList = itemList;
	}

	public long getMonitorId() {
		return monitorId;
	}

	public void setMonitorId(long monitorId) {
		this.monitorId = monitorId;
	}

	public String getFilterQuery() {
		return filterQuery;
	}

	public void setFilterQuery(String filterQuery) {
		this.filterQuery = filterQuery;
	}

	public long getItemId() {
		return itemId;
	}

	public void setItemId(long itemId) {
		this.itemId = itemId;
	}

	public boolean isIndustry() {
		return isIndustry;
	}

	public void setIndustry(boolean isIndustry) {
		this.isIndustry = isIndustry;
	}

	public int getTreeNodeCount() {
		return treeNodeCount;
	}

	public void setTreeNodeCount(int treeNodeCount) {
		this.treeNodeCount = treeNodeCount;
	}

	public boolean isGraphCustomized() {
		return isGraphCustomized;
	}

	public void setGraphCustomized(boolean isGraphCustomized) {
		this.isGraphCustomized = isGraphCustomized;
	}

	public int getPartitionDay1() {
		return partitionDay1;
	}

	public void setPartitionDay1(int partitionDay1) {
		this.partitionDay1 = partitionDay1;
	}

	public int getPartitionDay2() {
		return partitionDay2;
	}

	public void setPartitionDay2(int partitionDay2) {
		this.partitionDay2 = partitionDay2;
	}
}
