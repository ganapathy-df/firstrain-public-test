package com.firstrain.frapi.domain;

import java.util.Map;

import com.firstrain.frapi.pojo.Entity;

public class BlendDunsInput {

	private Map<String, Entity> dnbEntityMap;
	private Boolean blendDUNS;
	private String scopeDirective;

	public String getScopeDirective() {
		return scopeDirective;
	}

	public void setScopeDirective(String scopeDirective) {
		this.scopeDirective = scopeDirective;
	}

	public Map<String, Entity> getDnbEntityMap() {
		return dnbEntityMap;
	}

	public void setDnbEntityMap(Map<String, Entity> dnbEntityMap) {
		this.dnbEntityMap = dnbEntityMap;
	}

	public Boolean getBlendDUNS() {
		return blendDUNS;
	}

	public void setBlendDUNS(Boolean blendDUNS) {
		this.blendDUNS = blendDUNS;
	}

}
