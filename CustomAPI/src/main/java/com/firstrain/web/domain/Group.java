package com.firstrain.web.domain;

import java.util.Set;

public class Group {

	private String frTopic;
	private String frToken;
	private Set<String> tokenSet;

	public String getFrTopic() {
		return frTopic;
	}

	public void setFrTopic(String frTopic) {
		this.frTopic = frTopic;
	}

	public String getFrToken() {
		return frToken;
	}

	public void setFrToken(String frToken) {
		this.frToken = frToken;
	}

	public Set<String> getTokenSet() {
		return tokenSet;
	}

	public void setTokenSet(Set<String> tokenSet) {
		this.tokenSet = tokenSet;
	}
}
