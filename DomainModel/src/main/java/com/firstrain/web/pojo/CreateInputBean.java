package com.firstrain.web.pojo;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateInputBean {

	private String name;
	private String type;
	private Author author;
	private Author requester;
	private String taxonomyDirective;

	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private List<String> taxonomyDirectiveLst = new ArrayList<String>();

	private Definition definition;
	private String searchToken;
	private String version;

	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private List<Doc> docs = new ArrayList<Doc>();

	private String state;
	private String brandInitial;
	private String errorMsg;

	public List<String> getTaxonomyDirectiveLst() {
		return new ArrayList<String>(taxonomyDirectiveLst);
	}

	public void setTaxonomyDirectiveLst(List<String> taxonomyDirectiveLst) {
		if (taxonomyDirectiveLst == null) {
			this.taxonomyDirectiveLst = new ArrayList<String>();
		} else {
			this.taxonomyDirectiveLst = new ArrayList<String>(taxonomyDirectiveLst);
		}
	}

	public List<Doc> getDocs() {
		return new ArrayList<Doc>(docs);
	}

	public void setDocs(List<Doc> docs) {
		if (docs == null) {
			this.docs = new ArrayList<Doc>();
		} else {
			this.docs = new ArrayList<Doc>(docs);
		}
	}
}
