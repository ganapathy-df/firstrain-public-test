package com.firstrain.web.pojo;

import java.util.ArrayList;
import java.util.List;

import com.firstrain.frapi.pojo.wrapper.DocumentSet;

public class SearchResultWeb {
	private List<Entity> topicIdHavingDocs = new ArrayList<Entity>();

	private DocumentSet documentSet;
	private String primaryRegion;

	public List<Entity> getTopicIdHavingDocs() {
		return topicIdHavingDocs;
	}

	public void setTopicIdHavingDocs(List<Entity> topicIdHavingDocs) {
		this.topicIdHavingDocs = topicIdHavingDocs;
	}

	public String getPrimaryRegion() {
		return primaryRegion;
	}

	public void setPrimaryRegion(String primaryRegion) {
		this.primaryRegion = primaryRegion;
	}

	public DocumentSet getDocumentSet() {
		return documentSet;
	}

	public void setDocumentSet(DocumentSet documentSet) {
		this.documentSet = documentSet;
	}

}
