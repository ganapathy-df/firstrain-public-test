package com.firstrain.frapi.repository;

import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

@Repository
public interface ExcelProcessingHelperRepository {

	Integer DEFAULTID = -99991;

	public Set<String> getCompanyEndingWordsRegex();

	public Set<String> getCompanyEndingWords();

	public Map<Integer, Integer> getPaltinumSourceVsRank();
}
