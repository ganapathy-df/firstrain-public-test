package com.firstrain.frapi.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.firstrain.frapi.domain.AutoSuggest;

/**
 * @author GKhanchi
 */

@Repository
@Qualifier("autoSuggestServiceRepository")
public interface AutoSuggestServiceRepository extends EntityBaseServiceRepository {
	public AutoSuggest getAutoCompleteEntries(String input, String typeStr, boolean link, int count, String dimensionCSV) throws Exception;
}
