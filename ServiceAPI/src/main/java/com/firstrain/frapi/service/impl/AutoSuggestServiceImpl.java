package com.firstrain.frapi.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.firstrain.frapi.domain.AutoSuggest;
import com.firstrain.frapi.repository.AutoSuggestServiceRepository;
import com.firstrain.frapi.service.AutoSuggestService;

/**
 * @author GKhanchi
 */
@Service
public class AutoSuggestServiceImpl implements AutoSuggestService {

	private static final Logger LOG = Logger.getLogger(AutoSuggestServiceImpl.class);

	@Autowired
	private AutoSuggestServiceRepository repository;

	@Override
	public AutoSuggest getAutoCompleteEntries(String input, String typeStr, boolean link, int count, String dimensionCSV) throws Exception {
		AutoSuggest obj = null;
		try {
			obj = repository.getAutoCompleteEntries(input, typeStr, link, count, dimensionCSV);
		} catch (Exception e) {
			LOG.error("Error Processing request", e);
			throw e;
		}
		return obj;
	}
}
