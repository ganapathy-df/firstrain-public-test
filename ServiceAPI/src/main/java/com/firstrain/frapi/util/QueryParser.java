package com.firstrain.frapi.util;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public final class QueryParser {

	private static final Logger LOG = Logger.getLogger(QueryParser.class);

	public static class ParseResult {
		private String input;
		private Map<String, List<String>> knownParamValuesMap = new LinkedHashMap<String, List<String>>();
		private Map<String, List<String>> unknownParamValuesMap = new LinkedHashMap<String, List<String>>(1);

		private static final List<String> _blankList = Collections.unmodifiableList(new ArrayList<String>(0));

		ParseResult(String input) {
			this.input = input;
		}

		public List<String> getParams() {
			List<String> list = new ArrayList<String>();
			list.addAll(this.knownParamValuesMap.keySet());
			list.addAll(this.unknownParamValuesMap.keySet());
			return list;
		}

		public List<String> getParamValues(String paramName) {
			if (paramName == null || paramName.trim().isEmpty()) {
				return _blankList;
			}

			String key = paramName.toLowerCase();
			List<String> list = this.knownParamValuesMap.get(key);
			if (list == null) {
				list = this.unknownParamValuesMap.get(key);
			}

			if (list == null) {
				list = _blankList;
			}
			return list;
		}

		public String getFirstParamValue(String paramName) {
			List<String> list = getParamValues(paramName);
			if (!list.isEmpty()) {
				return list.get(0);
			}
			return "";
		}

		public String getInput() {
			return this.input;
		}
	}

	private static class ParamHint {
		ParamHint() {
			super();
		}

		ParamInfo pInfo;
		String matchOn;
		int pStartPos = -1, pEndPos = -1;
		int vStartPos, vEndPos;
		String matchVal;
	}

	private static class ParamInfo {
		String name, nameV1, nameV2;

		ParamInfo(String name) {
			this.name = name;
			this.nameV1 = name + "=";
			this.nameV2 = "&" + this.nameV1;
			this.minLen = this.nameV1.length();
			this.maxLen = this.nameV2.length();
		}

		int minLen, maxLen;
	}

	private QueryParser() {
		super();
	}

	/*
	 * [PASS 1]
	 * 
	 * 1. Loop on paramInfoMap keys(known parameter names) against the input string.
	 * 
	 * 2. Note down positions of each paramInfo separately in input text following below logic: a.) Check for paramInfo.nameV2 availability
	 * and record its boundary, if available. b.) Check for paramInfo.nameV1 availability, ignore if it falls under start boundary of
	 * paramInfo.nameV2, record otherwise. [Case : q=aapl&q=Hello]
	 * 
	 * A particular ParamHint created from paramInfo would have only 1 final positions recording. This'll effectively show up the start
	 * positions of each param in input string.
	 * 
	 * 3. Now we need to find out each parameter end boundary points for extracting the values. Store all start positions + end position in
	 * array and sort it asc.
	 * 
	 * 4. Loop on paramHints for identifying end boundary line of each entry.
	 * 
	 * TODO: [PASS 2] 5. Identify unknown parameter from values of already identified name-value pairs.
	 */

	private static Map<String, ParamInfo> knownParamInfoMap = new LinkedHashMap<String, QueryParser.ParamInfo>();

	static {
		knownParamInfoMap.put("q", new ParamInfo("q"));
		knownParamInfoMap.put("fq", new ParamInfo("fq"));
		knownParamInfoMap.put("scope", new ParamInfo("scope"));
		knownParamInfoMap.put("days", new ParamInfo("days"));
		knownParamInfoMap.put("lastDay", new ParamInfo("lastDay"));
		knownParamInfoMap.put("start", new ParamInfo("start"));
		knownParamInfoMap.put("order", new ParamInfo("order"));
	}

	@SuppressWarnings("synthetic-access")
	public static ParseResult parseQueryString(String queryString) {
		if (queryString == null || queryString.trim().isEmpty()) {
			return new ParseResult(queryString);
		}

		boolean scanAtStartBoundary = true;

		Map<String, List<ParamHint>> knownParamValuesMap = new LinkedHashMap<String, List<ParamHint>>();

		String fullInput = queryString;

		// 1.
		for (Entry<String, ParamInfo> entry : knownParamInfoMap.entrySet()) {
			List<ParamHint> hintList = new ArrayList<QueryParser.ParamHint>();

			ParamInfo pInfo = entry.getValue();

			// 2. Handling multiple occurrences of nameV2 appearance.
			detectParamMultipleOccurrences(fullInput, hintList, pInfo);

			if (scanAtStartBoundary) {
				// 2. Handling single occurrence of nameV1 appearance.
				ParamHint pHint = detectParamMatchAtStart(fullInput, pInfo);
				if (pHint != null) {
					hintList.add(pHint);
					scanAtStartBoundary = false;
				}
			}

			if (!hintList.isEmpty()) {
				knownParamValuesMap.put(pInfo.name, hintList);
			}
		}

		// 3. Get ordered position indexes.
		Integer[] posArray = getSortedPositionsArray(knownParamValuesMap, fullInput.length());

		// 4. Determine the match positions as well as value matches.
		fillInValueMatches(knownParamValuesMap, fullInput, posArray);

		// 5. Find unknown parameter value pairs now.

		// Print now
		printResults(queryString, knownParamValuesMap);

		ParseResult parseResult = new ParseResult(queryString);
		fillResults(parseResult.knownParamValuesMap, knownParamValuesMap);
		fillResults(parseResult.unknownParamValuesMap, null);

		return parseResult;
	}

	private static ParamHint detectParamMatchAtStart(String fullInput, ParamInfo pInfo) {
		ParamHint pHint = null;
		int v1Idx = fullInput.indexOf(pInfo.nameV1);
		if (v1Idx == 0) {
			pHint = new ParamHint();
			pHint.pInfo = pInfo;
			// Different and only 1 param @ start boundary
			pHint.matchOn = pInfo.nameV1;
			pHint.pStartPos = v1Idx;
			pHint.pEndPos += v1Idx + pHint.matchOn.length();
		}
		// Other cases already being covered earlier during scanning of nameV2.
		return pHint;
	}

	private static void detectParamMultipleOccurrences(String fullInput, List<ParamHint> hintList, ParamInfo pInfo) {
		int v2Idx = 0;
		while (v2Idx != -1) {
			v2Idx = fullInput.indexOf(pInfo.nameV2, v2Idx - 1);
			if (v2Idx != -1) {
				ParamHint pHint = new ParamHint();
				pHint.pInfo = pInfo;
				pHint.matchOn = pInfo.nameV2;
				pHint.pStartPos = v2Idx;
				pHint.pEndPos += v2Idx + pHint.matchOn.length();
				hintList.add(pHint);
			}
			// Only scan if got a hit at first step.
			if (v2Idx != -1) {
				v2Idx = fullInput.indexOf(pInfo.nameV2, v2Idx + pInfo.nameV2.length());
				if (v2Idx + 1 >= fullInput.length()) {
					v2Idx = -1;
				}
			}
		}
	}

	private static void fillInValueMatches(Map<String, List<ParamHint>> knownParamValuesMap, String fullInput, Integer[] posArray) {
		for (Entry<String, List<ParamHint>> entry : knownParamValuesMap.entrySet()) {
			for (ParamHint hint : entry.getValue()) {
				int valueEndPosIdx = posArray[Arrays.binarySearch(posArray, hint.pStartPos) + 1];
				hint.vStartPos = hint.pStartPos + hint.matchOn.length();
				hint.vEndPos = valueEndPosIdx;
				hint.matchVal = fullInput.substring(hint.vStartPos, hint.vEndPos);
			}
		}
	}

	private static void fillResults(Map<String, List<String>> knownParamValuesMap, Map<String, List<ParamHint>> map) {
		if (map == null || map.isEmpty()) {
			return;
		}

		for (Entry<String, List<ParamHint>> entry : map.entrySet()) {
			List<String> list = new ArrayList<String>();
			List<ParamHint> value = entry.getValue();
			for (ParamHint paramHint : value) {
				list.add(paramHint.matchVal);
			}
			knownParamValuesMap.put(entry.getKey(), list);
		}
	}

	private static Integer[] getSortedPositionsArray(Map<String, List<ParamHint>> pHintMap, int endPos) {
		List<Integer> posList = new ArrayList<Integer>(pHintMap.size() * 2);
		posList.add(endPos);

		for (Entry<String, List<ParamHint>> entry : pHintMap.entrySet()) {
			for (ParamHint hint : entry.getValue()) {
				posList.add(hint.pStartPos);
			}
		}

		Integer[] iArr = new Integer[posList.size()];
		posList.toArray(iArr);
		Arrays.sort(iArr);
		return iArr;
	}

	private static void printResults(String input, Map<String, List<ParamHint>> rMap) {
		if (!LOG.isTraceEnabled()) {
			return;
		}
		StringBuilder sb = new StringBuilder(input).append(" -> ");

		for (Entry<String, List<ParamHint>> entry : rMap.entrySet()) {
			sb.append("(");
			sb.append("[").append(entry.getKey()).append("]");
			sb.append(" matches ");

			List<ParamHint> values = entry.getValue();
			for (ParamHint paramHint : values) {
				sb.append("[{").append(paramHint.matchOn).append("},").append("{").append(paramHint.pStartPos).append(",")
						.append(paramHint.pEndPos).append("},").append("{").append(paramHint.vStartPos).append(",")
						.append(paramHint.vEndPos).append("},").append(input.substring(paramHint.vStartPos, paramHint.vEndPos)).append("]");
				sb.append(";");
			}
			sb.append(")");
		}
		LOG.trace(sb.toString());
	}

}
