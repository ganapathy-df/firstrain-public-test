package com.firstrain.web.domain;

import java.util.List;

import com.firstrain.web.pojo.CategorizerObject;
import com.firstrain.web.response.CategorizationServiceResponse;

public class DocCategorizePwResponse {

	private List<String> taxonomyDirective;
	private CategorizerObject serviceResponse;
	private CategorizationServiceResponse actualresponse;

	public List<String> getTaxonomyDirective() {
		return taxonomyDirective;
	}

	public void setTaxonomyDirective(List<String> taxonomyDirective) {
		this.taxonomyDirective = taxonomyDirective;
	}

	public CategorizerObject getServiceRes() {
		return serviceResponse;
	}

	public void setServiceRes(CategorizerObject serviceRes) {
		this.serviceResponse = serviceRes;
	}

	public CategorizationServiceResponse getActualresponse() {
		return actualresponse;
	}

	public void setActualresponse(CategorizationServiceResponse actualresponse) {
		this.actualresponse = actualresponse;
	}

}
