package com.firstrain.web.service.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.stereotype.Service;

@Service
public class SearchTokenUtil {

	private static Logger LOG = Logger.getLogger(SearchTokenUtil.class);
	private static LinkedHashMap<String, String> hexCodeTranslationMap = null;

	private static final String ALPHA_NUMERIC_REGEXP = "[a-z]|[0-9]";
	private static final String NON_ALPHA_NUMERIC_REGEXP = "\\\\|\\.|,|'|ï¿½|\"|-|~|!|@|#|\\$|%|\\^|&|\\*|\\(|\\)|=|\\+|_|:|;|/|\\?|>|<|`|\\{|\\}|\\[|\\]|\\|| ";
	private static Pattern CHECK_INVALID_SEARCH_TOKEN_PATTERN = null;
	private static final Pattern ALPHA_NUMERIC_PATTERN = Pattern.compile(ALPHA_NUMERIC_REGEXP, Pattern.CASE_INSENSITIVE);
	private static final Pattern NON_ALPHA_NUMERIC_PATTERN = Pattern.compile(NON_ALPHA_NUMERIC_REGEXP, Pattern.CASE_INSENSITIVE);

	/**
	 * 
	 * @throws Exception
	 */
	private SearchTokenUtil() throws Exception {
		if (hexCodeTranslationMap == null) {
			try {
				loadHexCodeTranslationMap();
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				throw e;
			} finally {

			}
		}
	}

	private static void loadHexCodeTranslationMap()
			throws InvalidFormatException, IOException {
		hexCodeTranslationMap = null; // loadAnsiCharFile(ansiCharFile, loadFromClasspath);
		StringBuffer searchTokenRegexpFinal = new StringBuffer(ALPHA_NUMERIC_REGEXP + "|" + NON_ALPHA_NUMERIC_REGEXP);
		if (hexCodeTranslationMap != null) {
			for (String hexValue : hexCodeTranslationMap.keySet()) {
				// User should not be allowed to provide any character from SEARCH_TOKEN_NON_ALPHA_NUMERIC_REGEXP
				// Ignore such character if found provided by the user with logging (WARN)
				if (NON_ALPHA_NUMERIC_PATTERN.matcher(String.valueOf((char) Integer.parseInt(hexValue, 16))).replaceAll("").length() == 0
						|| ALPHA_NUMERIC_PATTERN.matcher(String.valueOf((char) Integer.parseInt(hexValue, 16))).replaceAll("")
								.length() == 0) {

					LOG.warn(" Character matches the Regex Pattern. Will skip.");
					continue;
				}
				searchTokenRegexpFinal.append("|").append("\\u").append(hexValue);
			}
		}
		CHECK_INVALID_SEARCH_TOKEN_PATTERN = Pattern.compile(searchTokenRegexpFinal.toString(), Pattern.CASE_INSENSITIVE);
	}

	public ResponseObj checkForUnknownCharacter(String name) {
		ResponseObj obj = new ResponseObj();
		List<Character> invalidCharsList = new ArrayList<Character>();
		if (CHECK_INVALID_SEARCH_TOKEN_PATTERN.matcher(name).replaceAll("").length() > 0) {
			for (char charStr : name.toCharArray()) {
				if (CHECK_INVALID_SEARCH_TOKEN_PATTERN.matcher(String.valueOf(charStr)).replaceAll("").length() > 0) {
					invalidCharsList.add(charStr);
				}
			}
		}

		if (!invalidCharsList.isEmpty()) {
			obj.setInvalidCharacters(invalidCharsList);
		}
		return obj;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.firstrain.util.ISearchTokenUtil#getSearchToken(int, java.lang.String)
	 */
	public ResponseObj generateSearchToken(String entityNameParam) throws Exception {
		String entityName = entityNameParam;
		ResponseObj obj = checkForUnknownCharacter(entityName);
		if (obj.getInvalidCharacters() == null) {
			entityName = replaceSpecialCharacters(entityName);
			String searchToken = induceSearchTokenName(entityName);
			obj.setSearchToken(searchToken);
		}
		return obj;
	}

	public static void main(String[] args) throws Exception {
		
		// System.out.println(SearchTokenUtil.checkDuplicateOnTokens("Madhulika À Test Company"));
		// System.out.println(SearchTokenUtil.checkDuplicateOnTokens("Madhulika %c0 Test Company"));
		// System.out.println(SearchTokenUtil.checkDuplicateOnTokenPostCall("Madhulika À Test Company"));

		SearchTokenUtil searchTokenUtil = new SearchTokenUtil();
		System.out.println(searchTokenUtil.generateSearchToken("Topic-$$$ Topic 1").getSearchToken());
	}



	private String replaceSpecialCharacters(String nameParam) {
		String name = nameParam;
		name = NON_ALPHA_NUMERIC_PATTERN.matcher(name).replaceAll("");
		if (hexCodeTranslationMap != null) {
			for (String hexValue : hexCodeTranslationMap.keySet()) {
				name = name.replaceAll("\\u" + hexValue, hexCodeTranslationMap.get(hexValue));
			}
		}
		return name;
	}

	/*
	 * public static void main(String[] args) { String name = "a|.,'\"-~!@#$%^&*()=+_:;/?><`{}[]\\p"; System.out.println(" Effect :" +
	 * induceSearchToken(name)); }
	 */

	private String induceSearchTokenName(String nameParam) {
		String name = nameParam;
		if (name == null)
			return "";
		try {
			// \ . , ' " - ~ ! @ # $ % ^ & * ( ) = + _ : ;/ ? > < ` { } [ ] |
			name = name.replaceAll("\\\\|\\.|,|'|\"|-|~|!|@|#|\\$|%|\\^|&|\\*|\\(|\\)|=|\\+|_|:|;|/|\\?|>|<|`|\\{|\\}|\\[|\\]|\\|", " ");
			String str[] = name.split(" ");
			StringBuffer sb = new StringBuffer();
			for (String temp : str) {
				if (temp.trim().length() == 0)
					continue;
				sb.append(temp);
			}
			name = sb.toString();
		} catch (Exception e) {
			LOG.error(e.getMessage() + "Name : " + name, e);
		}

		return name;
	}

	public class ResponseObj {
		private String searchToken = null;
		private List<Character> invalidCharacters = null;

		/**
		 * @return the searchToken
		 */
		public String getSearchToken() {
			return searchToken;
		}

		/**
		 * @param searchToken
		 *            the searchToken to set
		 */
		public void setSearchToken(String searchToken) {
			this.searchToken = searchToken;
		}

		/**
		 * @return the invalidCharacters
		 */
		public List<Character> getInvalidCharacters() {
			return invalidCharacters;
		}

		/**
		 * @param invalidCharacters
		 *            the invalidCharacters to set
		 */
		public void setInvalidCharacters(List<Character> invalidCharacters) {
			this.invalidCharacters = invalidCharacters;
		}

	}
}

