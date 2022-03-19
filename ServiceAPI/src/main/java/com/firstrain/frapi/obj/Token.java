package com.firstrain.frapi.obj;

import com.firstrain.solr.client.SearchTokenEntry;

/**
 * @author Akanksha
 * 
 */

public class Token {

	private String title;
	private String searchTerm;
	private boolean included;
	private SearchTokenEntry token;

	public Token() {}

	public Token(String token, String title) {
		this.title = title;
		this.searchTerm = token;
	}

	public Token(SearchTokenEntry token) {
		this.token = token;
		if (token.getName() == null) {
			setTitle(token + "");
		} else {
			setTitle(token.getName());
		}
		searchTerm = token.getSearchToken();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSearchTerm() {
		return searchTerm;
	}

	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}


	public boolean isIncluded() {
		return included;
	}

	public void setIncluded(boolean included) {
		this.included = included;
	}

	public SearchTokenEntry getToken() {
		return token;
	}

	public void setToken(SearchTokenEntry token) {
		this.token = token;
	}
}

