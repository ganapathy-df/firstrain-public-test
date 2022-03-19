package com.firstrain.frapi.domain;

import java.util.List;

import com.firstrain.frapi.pojo.wrapper.BaseSet.SectionType;
import com.firstrain.frapi.util.DefaultEnums.DateBucketingMode;


public class BaseSpec {
	private String cacheKey;
	private Short count;
	private Short topCompetitorCount;
	private int daysCount;
	private String[] filling;
	private SectionType type;
	private String csExcludedEventTypeGroup;
	private int startEventType;
	private int endEventType;
	private Short start;
	private Integer scope;
	private Boolean needBucket;
	private Boolean needPagination;
	private DateBucketingMode bucketMode;
	private List<Integer> eventGroup;
	private Boolean ipad;
	private Boolean needMatchedEntities;
	private String lastDay;
	private Boolean needImage;
	private Short countPerEntity = 2;
	public static final short DEFAULT_COUNT = 10;
	private String excludeArticleIdsSSV;
	private String excludeSourceIdsSSV;
	private String includeSourceIdsSSV;
	private Boolean onlyIndustry;
	private boolean needRelatedDoc;
	private boolean isCustomized;
	private String startDate;
	private String endDate;
	private boolean needPhrase;
	private boolean sectionMulti;

	public boolean isSectionMulti() {
		return sectionMulti;
	}

	public void setSectionMulti(boolean sectionMulti) {
		this.sectionMulti = sectionMulti;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public boolean isCustomized() {
		return isCustomized;
	}

	public void setCustomized(boolean isCustomized) {
		this.isCustomized = isCustomized;
	}

	public String getIncludeSourceIdsSSV() {
		return includeSourceIdsSSV;
	}

	public void setIncludeSourceIdsSSV(String includeSourceIdsSSV) {
		this.includeSourceIdsSSV = includeSourceIdsSSV;
	}

	public String getExcludeSourceIdsSSV() {
		return excludeSourceIdsSSV;
	}

	public void setExcludeSourceIdsSSV(String excludeSourceIdsSSV) {
		this.excludeSourceIdsSSV = excludeSourceIdsSSV;
	}

	private Short industryClassificationId;

	public Boolean getNeedPagination() {
		return needPagination;
	}

	public void setNeedPagination(Boolean needPagination) {
		this.needPagination = needPagination;
	}

	public int getStartEventType() {
		return startEventType;
	}

	public void setStartEventType(int startEventType) {
		this.startEventType = startEventType;
	}

	public int getEndEventType() {
		return endEventType;
	}

	public void setEndEventType(int endEventType) {
		this.endEventType = endEventType;
	}

	public String getCacheKey() {
		return cacheKey;
	}

	public void setCacheKey(String cacheKey) {
		this.cacheKey = cacheKey;
	}

	public Short getCount() {
		return count;
	}

	public void setCount(Short count) {
		this.count = count;
	}

	public Short getTopCompetitorCount() {
		return topCompetitorCount;
	}

	public void setTopCompetitorCount(Short topCompetitorCount) {
		this.topCompetitorCount = topCompetitorCount;
	}

	public String[] getFilling() {
		return filling;
	}

	public void setFilling(String[] filling) {
		this.filling = filling;
	}

	public SectionType getType() {
		return type;
	}

	public void setType(SectionType type) {
		this.type = type;
	}

	public int getDaysCount() {
		return daysCount;
	}

	public void setDaysCount(int daysCount) {
		this.daysCount = daysCount;
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

	public Boolean getNeedBucket() {
		return needBucket;
	}

	public void setNeedBucket(Boolean needBucket) {
		this.needBucket = needBucket;
	}

	public DateBucketingMode getBucketMode() {
		return bucketMode;
	}

	public void setBucketMode(DateBucketingMode bucketMode) {
		this.bucketMode = bucketMode;
	}

	public List<Integer> getEventGroup() {
		return eventGroup;
	}

	public void setEventGroup(List<Integer> eventGroup) {
		this.eventGroup = eventGroup;
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

	public Boolean getNeedImage() {
		return needImage;
	}

	public void setNeedImage(Boolean needImage) {
		this.needImage = needImage;
	}

	public Short getCountPerEntity() {
		return countPerEntity;
	}

	public void setCountPerEntity(Short countPerEntity) {
		this.countPerEntity = countPerEntity;
	}

	public String getExcludeArticleIdsSSV() {
		return excludeArticleIdsSSV;
	}

	public void setExcludeArticleIdsSSV(String excludeArticleIdsSSV) {
		this.excludeArticleIdsSSV = excludeArticleIdsSSV;
	}

	public Short getIndustryClassificationId() {
		return industryClassificationId;
	}

	public void setIndustryClassificationId(Short industryClassificationId) {
		this.industryClassificationId = industryClassificationId;
	}

	public Boolean getOnlyIndustry() {
		return onlyIndustry;
	}

	public void setOnlyIndustry(Boolean onlyIndustry) {
		this.onlyIndustry = onlyIndustry;
	}

	public boolean isNeedRelatedDoc() {
		return needRelatedDoc;
	}

	public void setNeedRelatedDoc(boolean needRelatedDoc) {
		this.needRelatedDoc = needRelatedDoc;
	}

	public boolean isNeedPhrase() {
		return needPhrase;
	}

	public void setNeedPhrase(boolean needPhrase) {
		this.needPhrase = needPhrase;
	}
}
