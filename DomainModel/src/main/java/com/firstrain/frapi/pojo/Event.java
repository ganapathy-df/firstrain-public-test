package com.firstrain.frapi.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.firstrain.frapi.domain.BaseItem;
import com.firstrain.frapi.domain.MetaShare;
import com.firstrain.frapi.domain.MgmtTurnoverMeta;
import com.firstrain.frapi.domain.SecEventMeta;
import com.firstrain.frapi.domain.StockPriceMeta;
import com.firstrain.frapi.domain.WebVolumeEventMeta;
import com.firstrain.frapi.util.DefaultEnums.EventInformationEnum;

public class Event extends BaseItem implements Serializable {

	private static final long serialVersionUID = -9045701677803695295L;

	private EventInformationEnum eventInformationEnum = EventInformationEnum.WEB_VOLUME;
	private long itemId;
	private String caption;
	private boolean bookmarked = false;
	private String description;
	private boolean linkable;
	private String link;
	private Date date;
	private boolean trigger;
	private String url;
	private String eventType;
	private int dayId = -1;
	private Date reportDate;
	// FIXME: cleanup needed to remove fields specific to particular type of event and move them to field extra.
	private Map<String, Object> properties;
	private int flag;
	private double score;
	private long entityId;
	private String eventId;
	private Entity entityInfo;
	private Entity newCompany;
	private Entity oldCompany;
	private boolean hasExpired;
	private Set<Long> primaryEvidenceEntityIds;
	private String secFormType;

	private boolean futureEvent;
	private String person;
	private String firstName;
	private String lastName;
	private String oldDesignation;
	private int oldDesignationCode;
	private String oldRegion;
	private String oldDivision;
	private String newDesignation;
	private int newDesignationCode;
	private String newRegion;
	private String newDivision;
	private int eventTypeId;
	private String personImage;
	private Boolean dummyImage;
	private MgmtTurnoverMeta mtMeta;
	private WebVolumeEventMeta wvMeta;
	private SecEventMeta secMeta;
	private StockPriceMeta stockPriceMeta;
	// private HCMSEventType hcmsEventType;

	public MgmtTurnoverMeta getMtMeta() {
		return mtMeta;
	}

	public void setMtMeta(MgmtTurnoverMeta mtMeta) {
		this.mtMeta = mtMeta;
	}

	public WebVolumeEventMeta getWvMeta() {
		return wvMeta;
	}

	public void setWvMeta(WebVolumeEventMeta wvMeta) {
		this.wvMeta = wvMeta;
	}

	public SecEventMeta getSecMeta() {
		return secMeta;
	}

	public void setSecMeta(SecEventMeta secMeta) {
		this.secMeta = secMeta;
	}

	public StockPriceMeta getStockPriceMeta() {
		return stockPriceMeta;
	}

	public void setStockPriceMeta(StockPriceMeta stockPriceMeta) {
		this.stockPriceMeta = stockPriceMeta;
	}

	public String getPersonImage() {
		return personImage;
	}

	public void setPersonImage(String personImage) {
		this.personImage = personImage;
	}

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	public int getEventTypeId() {
		return eventTypeId;
	}

	public void setEventTypeId(int eventTypeId) {
		this.eventTypeId = eventTypeId;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public boolean isFutureEvent() {
		return futureEvent;
	}

	public void setFutureEvent(boolean futureEvent) {
		this.futureEvent = futureEvent;
	}

	public String getSecFormType() {
		return secFormType;
	}

	public void setSecFormType(String secFormType) {
		this.secFormType = secFormType;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public MetaShare getVisibility() {
		return visibility;
	}

	public void setVisibility(MetaShare visibility) {
		this.visibility = visibility;
	}

	/**
	 * @return the dayId
	 */
	public int getDayId() {
		return dayId;
	}

	/**
	 * @param dayId the dayId to set
	 */
	public void setDayId(int dayId) {
		this.dayId = dayId;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	@JsonIgnore
	public Map<String, Object> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public long getEntityId() {
		return entityId;
	}

	public void setEntityId(long entityId) {
		this.entityId = entityId;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public Entity getEntityInfo() {
		return entityInfo;
	}

	public void setEntityInfo(Entity entityInfo) {
		this.entityInfo = entityInfo;
	}

	public boolean isHasExpired() {
		return hasExpired;
	}

	public void setHasExpired(boolean hasExpired) {
		this.hasExpired = hasExpired;
	}

	public long getItemId() {
		return itemId;
	}

	public void setItemId(long itemId) {
		this.itemId = itemId;
	}

	public EventInformationEnum getEventInformationEnum() {
		return eventInformationEnum;
	}

	public void setEventInformationEnum(EventInformationEnum eventInformationEnum) {
		this.eventInformationEnum = eventInformationEnum;
	}

	public Set<Long> getPrimaryEvidenceEntityIds() {
		return primaryEvidenceEntityIds;
	}

	public void setPrimaryEvidenceEntityIds(Set<Long> primaryEvidenceEntityIds) {
		this.primaryEvidenceEntityIds = primaryEvidenceEntityIds;
	}

	public List<Entity> getMatchedCompanies() {
		return matchedCompanies;
	}

	public void setMatchedCompanies(List<Entity> matchedCompanies) {
		this.matchedCompanies = matchedCompanies;
	}

	public List<Entity> getMatchedTopics() {
		return matchedTopics;
	}

	public void setMatchedTopics(List<Entity> matchedTopics) {
		this.matchedTopics = matchedTopics;
	}

	public boolean isBookmarked() {
		return bookmarked;
	}

	public void setBookmarked(boolean bookmarked) {
		this.bookmarked = bookmarked;
	}

	public boolean isLinkable() {
		return linkable;
	}

	public void setLinkable(boolean linkable) {
		this.linkable = linkable;
	}

	public boolean isTrigger() {
		return trigger;
	}

	public void setTrigger(boolean trigger) {
		this.trigger = trigger;
	}

	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public Boolean getDummyImage() {
		return dummyImage;
	}

	public void setDummyImage(Boolean dummyImage) {
		this.dummyImage = dummyImage;
	}

	public Entity getOldCompany() {
		return oldCompany;
	}

	public void setOldCompany(Entity oldCompany) {
		this.oldCompany = oldCompany;
	}

	public Entity getNewCompany() {
		return newCompany;
	}

	public void setNewCompany(Entity newCompany) {
		this.newCompany = newCompany;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getOldDesignation() {
		return oldDesignation;
	}

	public void setOldDesignation(String oldDesignation) {
		this.oldDesignation = oldDesignation;
	}

	public String getNewDesignation() {
		return newDesignation;
	}

	public void setNewDesignation(String newDesignation) {
		this.newDesignation = newDesignation;
	}

	public int getOldDesignationCode() {
		return oldDesignationCode;
	}

	public void setOldDesignationCode(int oldDesignationCode) {
		this.oldDesignationCode = oldDesignationCode;
	}

	public String getOldRegion() {
		return oldRegion;
	}

	public void setOldRegion(String oldRegion) {
		this.oldRegion = oldRegion;
	}

	public String getOldDivision() {
		return oldDivision;
	}

	public void setOldDivision(String oldDivision) {
		this.oldDivision = oldDivision;
	}

	public int getNewDesignationCode() {
		return newDesignationCode;
	}

	public void setNewDesignationCode(int newDesignationCode) {
		this.newDesignationCode = newDesignationCode;
	}

	public String getNewRegion() {
		return newRegion;
	}

	public void setNewRegion(String newRegion) {
		this.newRegion = newRegion;
	}

	public String getNewDivision() {
		return newDivision;
	}

	public void setNewDivision(String newDivision) {
		this.newDivision = newDivision;
	}
	// public HCMSEventType getHcmsEventType() {
	// return hcmsEventType;
	// }
	// public void setHcmsEventType(HCMSEventType hcmsEventType) {
	// this.hcmsEventType = hcmsEventType;
	// }
}
