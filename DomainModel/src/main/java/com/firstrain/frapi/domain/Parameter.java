package com.firstrain.frapi.domain;

import com.firstrain.frapi.util.CompanyTeam;
import com.firstrain.frapi.util.DefaultEnums.DateBucketingMode;


public class Parameter {

	public static final short DEFAULT_COUNT = 10;
	public static final short DEFAULT_TOP_COMPETITORS_COUNT = 40;
	public static final short DAYS_DEFAULT_COUNT = 180;
	public static final int DEFAULT_N_DAY_FROM_TODAY = 0;
	public static final int CORPORATE_TRANSACTION_DEFAULT_COUNT = 28;
	public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

	private Short count = DEFAULT_COUNT;
	private Short start = 0;
	private Short topCompetitors = DEFAULT_TOP_COMPETITORS_COUNT;

	private String filingType;
	private String filterQuery;

	private Boolean needBucket;
	private Boolean needPagination;

	private String csExcludedEventTypeGroup;
	private Integer scope;
	private DateBucketingMode bucketMode;

	private String csEventGroup;
	private String csEventsType;
	private Boolean needLogo;
	private String chartType;
	private Boolean needImage;
	private Boolean ipad;
	private Boolean needMatchedEntities;
	private Boolean needCriticalEventTagging = false;
	private String lastDay;
	private Short perEntityItem = 2;

	/*
	 * 1---->BOARD_OF_DIRECTORS 2---->EXECUTIVE 3---->MANAGEMENT 4---->OFFICERS
	 * 
	 * 0----> All the Data
	 */
	private Integer companyTeamId;
	private Long nextStartId;
	private Integer numberOfSubSections;
	// TODO:We need this if Portal Search API needs to RUN by This
	// private GraphFor graphFor = GraphFor.CALL_PREP;
	private Integer days;
	private Integer nDaysFromToday;

	public Integer getCompanyTeamId() {
		return companyTeamId;
	}

	public void setCompanyTeamId(Integer companyTeamId) {
		this.companyTeamId = companyTeamId;
		CompanyTeam companyTeamEnum = this.getCompanyTeamEnum(companyTeamId);
		this.setCompanyTeamEnum(companyTeamEnum);
	}

	private CompanyTeam companyTeamEnum;

	public CompanyTeam getCompanyTeamEnum() {
		return companyTeamEnum;
	}

	public void setCompanyTeamEnum(CompanyTeam companyTeamEnum) {
		if (companyTeamEnum == null) {
			this.companyTeamEnum = this.getCompanyTeamEnum(this.companyTeamId);
		} else {
			this.companyTeamEnum = companyTeamEnum;
		}
	}

	public CompanyTeam getCompanyTeamEnum(Integer companyTeam) {
		switch (companyTeam) {
			case 1:
				companyTeamEnum = CompanyTeam.BOARD_OF_DIRECTORS;
				break;
			case 2:
				companyTeamEnum = CompanyTeam.EXECUTIVE;
				break;
			case 3:
				companyTeamEnum = CompanyTeam.MANAGEMENT;
				break;
			case 4:
				companyTeamEnum = CompanyTeam.OFFICERS;
				break;
			case 0:
				companyTeamEnum = null;
				break;
			default:
				break;
		}
		return companyTeamEnum;
	}

	public Short getCount() {
		return count;
	}

	public void setCount(Short count) {
		this.count = count;
	}

	public String getFilingType() {
		return filingType;
	}

	public void setFilingType(String filingType) {
		this.filingType = filingType;
	}

	public Short getTopCompetitors() {
		return topCompetitors;
	}

	public void setTopCompetitors(Short topCompetitors) {
		this.topCompetitors = topCompetitors;
	}

	public Long getNextStartId() {
		return nextStartId;
	}

	public void setNextStartId(Long nextStartId) {
		this.nextStartId = nextStartId;
	}

	public Integer getNumberOfSubSections() {
		return numberOfSubSections;
	}

	public void setNumberOfSubSections(Integer numberOfSubSections) {
		this.numberOfSubSections = numberOfSubSections;
	}

	public Integer getDays() {
		return days;
	}

	public void setDays(Integer days) {
		this.days = days;
	}

	public String getFilterQuery() {
		return filterQuery;
	}

	public void setFilterQuery(String filterQuery) {
		this.filterQuery = filterQuery;
	}

	public Boolean getNeedBucket() {
		return needBucket;
	}

	public void setNeedBucket(Boolean needBucket) {
		this.needBucket = needBucket;
	}

	public Integer getnDaysFromToday() {
		return nDaysFromToday;
	}

	// In case nDaysFromToday is set to value > 0 then days param should be <= 180 - nDaysFromToday as We have max 180 days data in event
	// solr.
	public void setnDaysFromToday(Integer nDaysFromToday) {
		this.nDaysFromToday = nDaysFromToday;
	}

	public String getCsExcludedEventTypeGroup() {
		return csExcludedEventTypeGroup;
	}

	public void setCsExcludedEventTypeGroup(String csExcludedEventTypeGroup) {
		this.csExcludedEventTypeGroup = csExcludedEventTypeGroup;
	}

	public Short getStart() {
		return start;
	}

	public void setStart(Short start) {
		this.start = start;
	}

	public Integer getScope() {
		return scope;
	}

	public void setScope(Integer scope) {
		this.scope = scope;
	}

	public DateBucketingMode getBucketMode() {
		return bucketMode;
	}

	public void setBucketMode(DateBucketingMode bucketMode) {
		this.bucketMode = bucketMode;
	}

	public Boolean getNeedPagination() {
		return needPagination;
	}

	public void setNeedPagination(Boolean needPagination) {
		this.needPagination = needPagination;
	}

	public String getCsEventGroup() {
		return csEventGroup;
	}

	public void setCsEventGroup(String csEventGroup) {
		this.csEventGroup = csEventGroup;
	}

	public String getCsEventsType() {
		return csEventsType;
	}

	public void setCsEventsType(String csEventsType) {
		this.csEventsType = csEventsType;
	}

	public Boolean getNeedLogo() {
		return needLogo;
	}

	public void setNeedLogo(Boolean needLogo) {
		this.needLogo = needLogo;
	}

	/**
	 * @return the chartType
	 */
	public String getChartType() {
		return chartType;
	}

	/**
	 * @param chartType the chartType to set
	 */
	public void setChartType(String chartType) {
		this.chartType = chartType;
	}

	public Boolean getNeedImage() {
		return needImage;
	}

	public void setNeedImage(Boolean needImage) {
		this.needImage = needImage;
	}

	public Boolean getIpad() {
		return ipad;
	}

	public void setIpad(Boolean ipad) {
		this.ipad = ipad;
	}

	public Boolean getNeedMatchedEntities() {
		return needMatchedEntities;
	}

	public void setNeedMatchedEntities(Boolean needMatchedEntities) {
		this.needMatchedEntities = needMatchedEntities;
	}

	public String getLastDay() {
		return lastDay;
	}

	public void setLastDay(String lastDay) {
		this.lastDay = lastDay;
	}

	public Short getPerEntityItem() {
		return perEntityItem;
	}

	public void setPerEntityItem(Short perEntityItem) {
		this.perEntityItem = perEntityItem;
	}

	public Boolean getNeedCriticalEventTagging() {
		return needCriticalEventTagging;
	}

	public void setNeedCriticalEventTagging(Boolean needCriticalEventTagging) {
		this.needCriticalEventTagging = needCriticalEventTagging;
	}
}
