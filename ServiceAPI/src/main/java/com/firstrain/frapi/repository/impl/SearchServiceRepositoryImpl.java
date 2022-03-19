package com.firstrain.frapi.repository.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.firstrain.frapi.domain.BaseSpec;
import com.firstrain.frapi.repository.SearchServiceRepository;
import com.firstrain.solr.client.SearchResult;
import com.firstrain.solr.client.SearchSpec;

@Repository
@Qualifier("searchServiceRepositoryImpl")
public class SearchServiceRepositoryImpl extends EntityBaseServiceRepositoryImpl implements SearchServiceRepository {

	@Override
	public SearchResult getMultiSectionSearchResults(String[] qMulti, int[] scopeMulti, String fq, BaseSpec baseSpec) throws Exception {

		SearchSpec searchSpec = new SearchSpec(new SearchSpec().setTagExclusionScope(SearchSpec.SCOPE_BROAD));
		searchSpec.needHighlighting = false;
		searchSpec.needHotListAll = false;
		searchSpec.needQuotes = true;
		searchSpec.needBodyLength = true;
		searchSpec.needSearchSuggestion = false;
		searchSpec.useLikelySearchIntention = false;
		searchSpec.setOrder(SearchSpec.ORDER_DATE);
		searchSpec.setStart(baseSpec.getStart());
		searchSpec.setRows(baseSpec.getCount());
		searchSpec.setDays(baseSpec.getDaysCount());
		searchSpec.qMulti = qMulti;
		searchSpec.scopeMulti = scopeMulti;
		searchSpec.sectionMulti = true;
		if (fq != null && !fq.isEmpty()) {
			searchSpec.fq = fq;
		}

		if (baseSpec.getExcludeArticleIdsSSV() != null && !baseSpec.getExcludeArticleIdsSSV().isEmpty()) {
			searchSpec.setExcludeDocIds(baseSpec.getExcludeArticleIdsSSV());
		}
		return this.getSearcher().search(searchSpec);
	}
}
