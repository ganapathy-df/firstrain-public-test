package com.firstrain.web.pojo;

import java.util.List;

import com.firstrain.frapi.util.DefaultEnums.EventInformationEnum;

public class Event {

	private String id;
	private String title;
	private String link;
	private EventInformationEnum eventType;
	private String timeStamp;
	private List<EntityStandard> entity;
	// related document information
	private String contentType;
	private Source source;
	private String snippet;

	private String eventDate;
	private String personFullName;
	private EntityStandard oldCompany;
	private String oldTitle;
	private String oldGroup;
	private String oldRegion;
	private EntityStandard newCompany;
	private String newTitle;
	private String newGroup;
	private String newRegion;

	public String getEventDate() {
		return eventDate;
	}

	public void setEventDate(String eventDate) {
		this.eventDate = eventDate;
	}

	public String getPersonFullName() {
		return personFullName;
	}

	public void setPersonFullName(String personFullName) {
		this.personFullName = personFullName;
	}

	public EntityStandard getOldCompany() {
		return oldCompany;
	}

	public void setOldCompany(EntityStandard oldCompany) {
		this.oldCompany = oldCompany;
	}

	public EntityStandard getNewCompany() {
		return newCompany;
	}

	public void setNewCompany(EntityStandard newCompany) {
		this.newCompany = newCompany;
	}

	public String getOldTitle() {
		return oldTitle;
	}

	public void setOldTitle(String oldTitle) {
		this.oldTitle = oldTitle;
	}

	public String getOldGroup() {
		return oldGroup;
	}

	public void setOldGroup(String oldGroup) {
		this.oldGroup = oldGroup;
	}

	public String getOldRegion() {
		return oldRegion;
	}

	public void setOldRegion(String oldRegion) {
		this.oldRegion = oldRegion;
	}

	public String getNewTitle() {
		return newTitle;
	}

	public void setNewTitle(String newTitle) {
		this.newTitle = newTitle;
	}

	public String getNewGroup() {
		return newGroup;
	}

	public void setNewGroup(String newGroup) {
		this.newGroup = newGroup;
	}

	public String getNewRegion() {
		return newRegion;
	}

	public void setNewRegion(String newRegion) {
		this.newRegion = newRegion;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public EventInformationEnum getEventType() {
		return eventType;
	}

	public void setEventType(EventInformationEnum eventType) {
		this.eventType = eventType;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public List<EntityStandard> getEntity() {
		return entity;
	}

	public void setEntity(List<EntityStandard> entity) {
		this.entity = entity;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	public String getSnippet() {
		return snippet;
	}

	public void setSnippet(String snippet) {
		this.snippet = snippet;
	}
}
