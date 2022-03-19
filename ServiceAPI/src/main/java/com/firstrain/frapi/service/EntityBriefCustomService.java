package com.firstrain.frapi.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.firstrain.frapi.FRService;
import com.firstrain.solr.client.HotListBucket;
import com.firstrain.web.pojo.SearchResultWeb;

@Service
public interface EntityBriefCustomService extends FRService {

	public HotListBucket getCoMentionCompanies(List<Long> list, Integer scope, Integer count, Integer daysCount) throws Exception;

	public SearchResultWeb getWebResults(Long primaryCatId, List<Long> secondaryCatIds, Integer scope, Integer count, Integer daysCount,
			boolean advanceSort) throws Exception;

	public SearchResultWeb getWebResultsForCatId(List<Long> catIds, Integer scope, Integer count, Integer daysCount) throws Exception;
}
