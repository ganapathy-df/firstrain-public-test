package com.firstrain.frapi.obj;

import java.util.List;

public class MonitorWizardFilters {

	public List<String> bb;
	public List<String> ct;
	public List<String> rg;
	public Advanced advanced;

	public static class Advanced {
		public String keywords;
		public List<String> advancedFilters;

		public Advanced(String keywords, List<String> advancedFilters) {
			this.keywords = keywords;
			this.advancedFilters = advancedFilters;
		}

		public Advanced() {}
	}
}


