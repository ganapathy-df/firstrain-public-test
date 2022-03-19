package com.firstrain.frapi.domain;

import com.firstrain.frapi.pojo.wrapper.EntityListJsonObject;
import com.firstrain.frapi.util.DefaultEnums.MembershipType;
import com.firstrain.frapi.util.DefaultEnums.Origin;
import com.firstrain.frapi.util.DefaultEnums.UserType;
import com.firstrain.frapi.util.DefaultEnums.UserValidationStatus;

public class User {

	private String userId;
	private String flags;
	private String domain;
	private long templateId;
	private String userName;
	private long ownedBy;
	private String email;
	private String ownedByType;
	private int format;
	private String timeZone;
	private String firstName;
	private String lastName;
	private String monitorOrderType;
	private String code;
	private MembershipType membershipType = MembershipType.DEFAULT;
	private UserValidationStatus status;
	private String dnbUserId;
	private long frMonitorId;
	private EntityListJsonObject userTriggers;
	// used in DnB for user company
	private String userCompany;
	private UserType UserType;
	private boolean contextPage;
	private Origin origin;

	public UserType getUserType() {
		return UserType;
	}

	public void setUserType(UserType userType) {
		UserType = userType;
	}

	public boolean isContextPage() {
		return contextPage;
	}

	public void setContextPage(boolean contextPage) {
		this.contextPage = contextPage;
	}

	public String getUserCompany() {
		return userCompany;
	}

	public void setUserCompany(String userCompany) {
		this.userCompany = userCompany;
	}

	public String getDnbUserId() {
		return dnbUserId;
	}

	public void setDnbUserId(String dnbUserId) {
		this.dnbUserId = dnbUserId;
	}

	public long getFrMonitorId() {
		return frMonitorId;
	}

	public void setFrMonitorId(long frMonitorId) {
		this.frMonitorId = frMonitorId;
	}

	public EntityListJsonObject getUserTriggers() {
		return userTriggers;
	}

	public void setUserTriggers(EntityListJsonObject userTriggers) {
		this.userTriggers = userTriggers;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getFlags() {
		return flags;
	}

	public void setFlags(String flags) {
		this.flags = flags;
	}

	public String getDomain() {
		return this.domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public long getTemplateId() {
		return this.templateId;
	}

	public void setTemplateId(long templateId) {
		this.templateId = templateId;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public long getOwnedBy() {
		return this.ownedBy;
	}

	public void setOwnedBy(long ownedBy) {
		this.ownedBy = ownedBy;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getFormat() {
		return this.format;
	}

	public void setFormat(int format) {
		this.format = format;
	}

	public String getOwnedByType() {
		return ownedByType;
	}

	public void setOwnedByType(String ownedByType) {
		this.ownedByType = ownedByType;
	}

	public String getTimeZone() {
		return this.timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMonitorOrderType() {
		return monitorOrderType;
	}

	public void setMonitorOrderType(String monitorOrderType) {
		this.monitorOrderType = monitorOrderType;
	}

	public MembershipType getMembershipType() {
		return membershipType;
	}

	public void setMembershipType(MembershipType membershipType) {
		this.membershipType = membershipType;
	}

	public UserValidationStatus getStatus() {
		return status;
	}

	public void setStatus(UserValidationStatus status) {
		this.status = status;
	}

	public Origin getOrigin() {
		return this.origin;
	}

	public void setOrigin(Origin origin) {
		this.origin = origin;
	}
}
