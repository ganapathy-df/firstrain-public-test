package com.firstrain.frapi.pojo;

import java.util.List;

import com.firstrain.frapi.domain.Search;
import com.firstrain.frapi.pojo.wrapper.BaseSet;
import com.firstrain.utils.object.PerfActivityType;
import com.firstrain.utils.object.PerfMonitor;
import com.firstrain.utils.object.PerfRequestEntry;

public class EmailDetail {

	private List<Search> searches;

	public List<Search> getSearches() {
		return searches;
	}

	public void setSearches(List<Search> searches) {
		this.searches = searches;
	}

	public void setPerfStats(BaseSet baseSet) {
		PerfRequestEntry entry = baseSet.getStat();
		PerfMonitor.mergeStats(entry, PerfActivityType.Others, true);
	}
}
