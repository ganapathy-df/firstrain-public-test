package com.firstrain.frapi.service;

import org.springframework.stereotype.Service;

import com.firstrain.frapi.FRService;
import com.firstrain.frapi.domain.AutoSuggest;

/**
 * @author GKhanchi
 */

@Service
public interface AutoSuggestService extends FRService {
	public AutoSuggest getAutoCompleteEntries(String input, String typeStr, boolean link, int count, String dimensionCSV) throws Exception;
}
