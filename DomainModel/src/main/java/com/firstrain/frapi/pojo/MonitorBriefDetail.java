package com.firstrain.frapi.pojo;

import com.firstrain.frapi.domain.VisualizationData;
import com.firstrain.frapi.pojo.wrapper.BaseSet;
import com.firstrain.frapi.pojo.wrapper.DocumentSet;
import com.firstrain.frapi.pojo.wrapper.TweetSet;
import com.firstrain.utils.object.PerfActivityType;
import com.firstrain.utils.object.PerfMonitor;
import com.firstrain.utils.object.PerfRequestEntry;

public class MonitorBriefDetail {

	private String monitorId;
	private String monitorName;
	private Long ownedBy;
	private String ownedByType;
	private TweetSet tweetList;
	private DocumentSet webResults;
	private VisualizationData visualizationData;

	public String getMonitorId() {
		return monitorId;
	}

	public void setMonitorId(String monitorId) {
		this.monitorId = monitorId;
	}

	public String getMonitorName() {
		return monitorName;
	}

	public void setMonitorName(String monitorName) {
		this.monitorName = monitorName;
	}

	public Long getOwnedBy() {
		return ownedBy;
	}

	public void setOwnedBy(Long ownedBy) {
		this.ownedBy = ownedBy;
	}

	public String getOwnedByType() {
		return ownedByType;
	}

	public void setOwnedByType(String ownedByType) {
		this.ownedByType = ownedByType;
	}

	public TweetSet getTweetList() {
		return tweetList;
	}

	public void setTweetList(TweetSet tweetList) {
		this.tweetList = tweetList;
	}

	public DocumentSet getWebResults() {
		return webResults;
	}

	public void setWebResults(DocumentSet webResults) {
		this.webResults = webResults;
	}

	public VisualizationData getVisualizationData() {
		return visualizationData;
	}

	public void setVisualizationData(VisualizationData visualizationData) {
		this.visualizationData = visualizationData;
	}

	public void updatePrep(BaseSet baseSet) {
		switch (baseSet.getSectionType()) {
			case FT:
				this.setTweetList((TweetSet) baseSet);
				break;
			case FR:
				this.setWebResults((DocumentSet) baseSet);
				break;
			case VIZ:
				this.setVisualizationData((VisualizationData) baseSet);
				break;
			default:
				break;
		}
	}

	public void setPerfStats(BaseSet baseSet) {
		PerfRequestEntry entry = baseSet.getStat();
		PerfMonitor.mergeStats(entry, PerfActivityType.Others, true);
	}
}
