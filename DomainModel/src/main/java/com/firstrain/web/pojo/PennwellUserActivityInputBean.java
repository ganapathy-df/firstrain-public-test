package com.firstrain.web.pojo;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PennwellUserActivityInputBean {

	private String name;
	private String type;
	private Author author;
	private Author requester;
	private String taxonomyDirective;

	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private List<String> taxonomyDirectiveLst = new ArrayList<String>();

	private String searchToken;
	private String version;
	private String state;
	private String brandInitial;

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
}
