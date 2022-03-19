package com.firstrain.frapi.pojo.wrapper;

import java.util.Map;

import com.firstrain.frapi.domain.Ng;

public class GetBulk {

	private Ng ng;
	private Map<String, Map<String, String>> entityLinking;

	public Map<String, Map<String, String>> getEntityLinking() {
		return entityLinking;
	}

	public void setEntityLinking(Map<String, Map<String, String>> entityLinking) {
		this.entityLinking = entityLinking;
	}

	public Ng getNg() {
		return ng;
	}

	public void setNg(Ng ng) {
		this.ng = ng;
	}

}
