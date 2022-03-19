package com.firstrain.web.domain;

import java.util.Set;

public class Topic {

	private Set<String> pwTokens;
	private Set<Integer> groups;

	public Set<String> getPwTokens() {
		return pwTokens;
	}

	public void setPwTokens(Set<String> pwTokens) {
		this.pwTokens = pwTokens;
	}

	public Set<Integer> getGroups() {
		return groups;
	}

	public void setGroups(Set<Integer> groups) {
		this.groups = groups;
	}


}
