package com.firstrain.frapi.repository;

import org.springframework.stereotype.Repository;

import com.firstrain.frapi.domain.BaseSpec;
import com.firstrain.solr.client.SearchResult;

@Repository
public interface SearchServiceRepository extends EntityBaseServiceRepository {

	public SearchResult getMultiSectionSearchResults(String[] qMulti, int[] scopeMulti, String fq, BaseSpec baseSpec) throws Exception;
}
