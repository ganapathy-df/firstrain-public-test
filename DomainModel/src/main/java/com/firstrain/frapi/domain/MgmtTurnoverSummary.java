package com.firstrain.frapi.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.firstrain.frapi.util.DefaultEnums.MgmtServiceGroup;

public class MgmtTurnoverSummary {

	private Map<String, MgmtTurnoverData> mgmtTurnoverDataMap = new HashMap<String, MgmtTurnoverData>();
	private Map<MgmtServiceGroup, List<EventTypeEntry>> lhsEntriesMap = new HashMap<MgmtServiceGroup, List<EventTypeEntry>>();
	private List<ManagementChange> details;
	private int maxMth;
	private int maxQtr;

	public MgmtTurnoverData getMgmtTurnoverDataFor(String searchToken) {
		return this.mgmtTurnoverDataMap.get(searchToken);
	}

	public List<EventTypeEntry> getLHSEntries(MgmtServiceGroup group) {
		return lhsEntriesMap.get(group);
	}

	public List<ManagementChange> getDetails() {
		return this.details;
	}

	public void addMgmtTurnoverData(String searchToken, MgmtTurnoverData mgmtTurnoverData) {
		this.mgmtTurnoverDataMap.put(searchToken, mgmtTurnoverData);
	}

	public void addLHSEntry(MgmtServiceGroup group, List<EventTypeEntry> lhsEntries) {
		this.lhsEntriesMap.put(group, lhsEntries);
	}

	public void setDetails(List<ManagementChange> details) {
		this.details = details;
	}

	public int getMaxMth() {
		return this.maxMth;
	}

	public int getMaxQtr() {
		return this.maxQtr;
	}

	public void setMaxMth(int maxMth) {
		this.maxMth = maxMth;
	}

	public void setMaxQtr(int maxQtr) {
		this.maxQtr = maxQtr;
	}
}
