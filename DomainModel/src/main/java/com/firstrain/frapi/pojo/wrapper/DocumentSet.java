package com.firstrain.frapi.pojo.wrapper;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.firstrain.frapi.domain.Document;

public class DocumentSet extends BaseSet implements Serializable {

	private static final long serialVersionUID = 1L;
	private List<Document> documents;
	private Map<String, List<Document>> documentBucket;
	private String caption = null;
	private boolean filing = false;
	private Boolean primaryIndustry;
	private Integer scope;

	public boolean isFiling() {
		return filing;
	}

	public void setFiling(boolean filing) {
		this.filing = filing;
	}

	public DocumentSet() {}

	public DocumentSet(SectionType type) {
		this(null, type);
	}

	public DocumentSet(List<Document> l, SectionType type) {
		super(type);
		this.documents = l;
	}

	public List<Document> getDocuments() {
		return documents;
	}

	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public Boolean getPrimaryIndustry() {
		return primaryIndustry;
	}

	public void setPrimaryIndustry(Boolean primaryIndustry) {
		this.primaryIndustry = primaryIndustry;
	}

	public Integer getScope() {
		return scope;
	}

	public void setScope(Integer scope) {
		this.scope = scope;
	}

	public Map<String, List<Document>> getDocumentBucket() {
		return documentBucket;
	}

	public void setDocumentBucket(Map<String, List<Document>> documentBucket) {
		this.documentBucket = documentBucket;
	}

}
