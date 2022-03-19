package com.firstrain.web.wrapper;

import java.util.List;

public class EntityListWrapperData {
	private String guid;
	private String docId;
	private List<String> taxonomyDirective;
	private EntityListWrapper data;

	public EntityListWrapper getData() {
		return data;
	}

	public void setData(EntityListWrapper data) {
		this.data = data;
	}

	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

	public List<String> getTaxonomyDirective() {
		return taxonomyDirective;
	}

	public void setTaxonomyDirective(List<String> taxonomyDirective) {
		this.taxonomyDirective = taxonomyDirective;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}
}
