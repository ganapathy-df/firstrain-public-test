package com.firstrain.web.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonPropertyOrder;

@Setter
@Getter
@JsonPropertyOrder({"searchId", "searchName", "searchQuery", "searchFilter", "documents"})
public class Search implements Serializable {

	private static final long serialVersionUID = -3345179857673122369L;
	private String searchId;
	private String searchName;
	private String searchQuery;
	private String searchFilter;

	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private List<Document> documents = new ArrayList<Document>();

	public List<Document> getDocuments() {
		return new ArrayList<Document>(documents);
	}

	public void setDocuments(List<Document> documents) {
		if (documents == null) {
			this.documents = Collections.emptyList();
		} else {
			this.documents = new ArrayList<Document>(documents);
		}
	}
}
