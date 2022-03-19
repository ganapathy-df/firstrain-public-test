package com.firstrain.frapi.service;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.stereotype.Service;

import com.firstrain.frapi.FRService;
import com.firstrain.frapi.domain.BaseSpec;
import com.firstrain.frapi.domain.IndustryBriefDomain;
import com.firstrain.frapi.pojo.wrapper.DocumentSet;

@Service
public interface IndustryBriefService extends FRService {

	/**
	 * Get VirtualMonitor
	 * 
	 * @param catIdsStringCSV
	 * @param primaryExcahngeCSV
	 * @param topicIdsCSV
	 * @param isCustomized
	 * @return
	 */

	public IndustryBriefDomain getVirtualMonitor(String catIdsStringCSV, String primaryExcahngeCSV, String topicIdsCSV,
			AtomicBoolean onlyIndustry, Boolean isCustomized) throws Exception;

	/**
	 * Get WebResults
	 * 
	 * @param catIdsSet
	 * @param numberOfDocs
	 * @param days
	 * @return
	 */

	public DocumentSet getWebResults(Set<String> catIdsSet, String fq, BaseSpec baseSpec) throws Exception;
}
