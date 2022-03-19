package com.firstrain.frapi.pojo.wrapper;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.firstrain.frapi.pojo.Event;

public class EventSet extends BaseSet implements Serializable {

	private static final long serialVersionUID = 8427317709032886850L;

	private List<Event> events;
	private String link;
	private EventType eventType = null;
	private Boolean primaryIndustry;
	private Integer scope;
	private Integer unfilteredDataSize; // Data size before custom filtering

	public enum EventType {
		COMPETITOR_EVENTS, RELATED_COMPANIES_EVENTS, INDUSTRY_TOPIC_EVENTS
	}

	private Map<String, List<Event>> eventBuckets;


	public EventSet() {}

	public EventSet(SectionType type) {
		super(type);
	}

	public EventSet(List<Event> e) {
		this.events = e;

	}

	public List<Event> getEvents() {
		return events;
	}

	public void setEvents(List<Event> events) {
		this.events = events;
	}


	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public Map<String, List<Event>> getEventBuckets() {
		return eventBuckets;
	}

	// setting events to null in case eventBuckets are getting set. As there is no need to set both event bucket and event in any case.
	public void setEventBuckets(Map<String, List<Event>> eventBuckets) {
		this.eventBuckets = eventBuckets;
		if (this.events != null) {
			this.events = null;
		}
	}

	public EventType getEventType() {
		return eventType;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	public Boolean getPrimaryIndustry() {
		return primaryIndustry;
	}

	public void setPrimaryIndustry(Boolean primaryIndustry) {
		this.primaryIndustry = primaryIndustry;
	}

	public Integer getScope() {
		return scope;
	}

	public void setScope(Integer scope) {
		this.scope = scope;
	}

	public Integer getUnfilteredDataSize() {
		return unfilteredDataSize;
	}

	public void setUnfilteredDataSize(Integer unfilteredDataSize) {
		this.unfilteredDataSize = unfilteredDataSize;
	}
}
