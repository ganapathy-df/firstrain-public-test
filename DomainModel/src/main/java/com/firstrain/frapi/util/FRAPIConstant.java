package com.firstrain.frapi.util;

public interface FRAPIConstant {

	/**
	 * Indicates topic dimension 'Fundamentals' or 'Company Topic'.
	 */
	int TOPIC_DIMENSION_FUNDAMENTALS = 2;

	/**
	 * Indicates topic dimension 'Content Type'
	 */
	int TOPIC_DIMENSION_CONTENT_TYPE = 10;

	/**
	 * Indicates topic dimension 'Region'
	 */
	int TOPIC_DIMENSION_REGION = 14;

	public static final int BASIC_WEB_RESULTS_SEARCH_DAYS = 6 * 30;
	public static final int WEB_RESULTS_SEARCH_DAYS_BIMONTHLY = 2 * 30;
	public static final int SEARCHTOKEN_COUNT = 450;
	public static final int WEBRESULT_SINGLEENTITY = 500;
	public static final int MAX_ENTITY_COUNT_FOR_INDUSTRY = 400;
	public static final int MAX_TWEET_COUNT = 150;
	public static final String INITIAL_DATE = "2001-01-01";
	public static final int SOLR_CHUNK = 500;
	public static final int MAX_NO_OF_EVENTS = 300;
	public static final int WV_EVENTS_COUNT = 40;
	public static final int EVENTS_DAYS = 6 * 30;
	public static final int HIGHLIGHTS_SEARCH_DAYS = 1;

	String MONITOR_PREFIX = "M:";
	String COMPANY_PREFIX = "C:";
	String TOPIC_PREFIX = "T:";
	String INDUSTRY_PREFIX = "I:";
	String USER_ID_PREFIX = "U:";
	String GROUP_ID_PREFIX = "G:";
	String DOCUMENT_PREFIX = "D:";
	String SEC_FILING_PREFIX = "SEC:";
	String TWEET_PREFIX = "TW:";
	String MT_PREFIX = "MT:";
	String EVENTS_PREFIX = "E:";
	String SEC_PREFIX = "SECE:";
	String SEARCH_PREFIX = "S:";
	String EMAIL_PREFIX = "EM:";

}
