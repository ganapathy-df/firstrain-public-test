package com.firstrain.web.pojo;

import java.math.BigInteger;
import java.sql.Timestamp;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.firstrain.frapi.util.DefaultEnums.UserActivityType;

/**
 * @author vgoyal
 */
public class UserActivity {

	private BigInteger id;
	private Integer userId;
	private String activity;
	private Timestamp activityTime; // yyyy-mm-dd hh:mm:ss
	private Timestamp insertTime;
	private String view;
	private String viewId;
	private String section;
	private String sectionId;
	private String subSection;
	private String subSectionId;
	private int enterpriseId;
	private String channel;
	private String target;
	private String targetId;
	private int responseTime = -1;
	private String userAgent;
	private String metaData;
	private String destination;
	private String userip;
	private BigInteger legacyId;
	private boolean isExternal;
	private String parsedUserAgent;
	private String legacyData;
	private String dnbUserId;
	private String str1;
	private String str2;
	private String apiVersion;
	private int type = UserActivityType.USER.ordinal();

	public Timestamp getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(Timestamp insertTime) {
		this.insertTime = insertTime;
	}

	public BigInteger getLegacyId() {
		return legacyId;
	}

	public void setLegacyId(BigInteger legacyId) {
		this.legacyId = legacyId;
	}

	public String getUserip() {
		return userip;
	}

	public void setUserip(String userip) {
		this.userip = userip;
	}

	public int getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(int enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public String getViewId() {
		return viewId;
	}

	public void setViewId(String viewId) {
		this.viewId = viewId;
	}

	public String getSectionId() {
		return sectionId;
	}

	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}

	public String getSubSection() {
		return subSection;
	}

	public void setSubSection(String subSection) {
		this.subSection = subSection;
	}

	public String getSubSectionId() {
		return subSectionId;
	}

	public void setSubSectionId(String subSectionId) {
		this.subSectionId = subSectionId;
	}

	public BigInteger getId() {
		return id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public Timestamp getActivityTime() {
		return activityTime;
	}

	public void setActivityTime(Timestamp activityTime) {
		this.activityTime = activityTime;
	}

	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	public int getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(int responseTime) {
		this.responseTime = responseTime;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public String getMetaData() {
		return metaData;
	}

	public void setMetaData(String metaData) {
		this.metaData = metaData;

	}

	@Override
	public String toString() {
		return String.format(
				"UserActivity [id=%s, userId=%s, activity=%s, activityTime=%s, view=%s, viewId=%s, section=%s, sectionId=%s, subSection=%s, subSectionId=%s, enterpriseId=%s, channel=%s, target=%s, targetId=%s, responseTime=%s, userAgent=%s, metaData=%s, destination=%s]",
				id, userId, activity, activityTime, view, viewId, section, sectionId, subSection, subSectionId, enterpriseId, channel,
				target, targetId, responseTime, userAgent, metaData, destination);
	}

	public boolean isExternal() {
		return isExternal;
	}

	public void setExternal(boolean isExternal) {
		this.isExternal = isExternal;
	}

	public String getParsedUserAgent() {
		return parsedUserAgent;
	}

	public void setParsedUserAgent(String parsedUserAgent) {
		this.parsedUserAgent = parsedUserAgent;
	}

	@JsonIgnore
	public String getLegacyData() {
		return legacyData;
	}

	public void setLegacyData(String legacyData) {
		this.legacyData = legacyData;
	}

	public String getDnbUserId() {
		return dnbUserId;
	}

	public void setDnbUserId(String dnbUserId) {
		this.dnbUserId = dnbUserId;
	}

	public String getStr1() {
		return str1;
	}

	public void setStr1(String str1) {
		this.str1 = str1;
	}

	public String getStr2() {
		return str2;
	}

	public void setStr2(String str2) {
		this.str2 = str2;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
		if (!validateUATEnumOrdinal(type)) {
			throw new IllegalArgumentException("value of `type` column is wrong user activity type enum ordinal.");
		}
	}

	private boolean validateUATEnumOrdinal(long type) {
		for (UserActivityType userActivityType : UserActivityType.values()) {
			if (userActivityType.ordinal() == type) {
				return true;
			}
		}
		return false;
	}

	public String getApiVersion() {
		return apiVersion;
	}

	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}

}
