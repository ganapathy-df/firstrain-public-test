package com.firstrain.web.domain;

import java.util.Set;

public class PwToken {

	private String name;
	private Set<Integer> groups;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Integer> getGroups() {
		return groups;
	}

	public void setGroups(Set<Integer> groups) {
		this.groups = groups;
	}
}
