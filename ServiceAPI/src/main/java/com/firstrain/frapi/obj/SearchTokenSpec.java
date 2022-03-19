package com.firstrain.frapi.obj;

import java.util.List;

/**
 * Class to support various search tokens as service parameters for bringing most relevant search results.
 */
public class SearchTokenSpec {

	private List<String> searchTokens;
	private List<String> excludeSearchTokens;

	public List<String> getSearchTokens() {
		return this.searchTokens;
	}

	public SearchTokenSpec setSearchTokens(List<String> searchTokens) {
		this.searchTokens = searchTokens;
		return this;
	}

	public List<String> getExcludeSearchTokens() {
		return this.excludeSearchTokens;
	}

	public SearchTokenSpec setExcludeSearchTokens(List<String> excludeSearchTokens) {
		this.excludeSearchTokens = excludeSearchTokens;
		return this;
	}

	public boolean areSearchTokensAvailable() {
		return this.searchTokens != null && !this.searchTokens.isEmpty();
	}

	public boolean areExcludeSearchTokensAvailable() {
		return this.excludeSearchTokens != null && !this.excludeSearchTokens.isEmpty();
	}
}
