package com.firstrain.frapi.service;

import org.springframework.stereotype.Service;

import com.firstrain.frapi.FRService;
import com.firstrain.frapi.domain.Parameter;
import com.firstrain.frapi.pojo.EnterprisePref;
import com.firstrain.frapi.pojo.SearchAPIResponse;

@Service
public interface SearchService extends FRService {

	public SearchAPIResponse getMultiSectionWebResults(String[] qMulti, int[] scopeMulti, String fq, Parameter parameter,
			EnterprisePref enterprisePref) throws Exception;
}
