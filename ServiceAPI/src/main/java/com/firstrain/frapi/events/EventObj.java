package com.firstrain.frapi.events;

import java.util.Date;
import java.util.Formatter;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.firstrain.frapi.events.EventConfigUtil.DelayedConfObj;
import com.firstrain.frapi.events.EventConfigUtil.Filings8KConfObj;
import com.firstrain.frapi.events.EventConfigUtil.WebVolumeConfObj;
import com.firstrain.mip.object.FR_IEvent;
import com.firstrain.mip.object.FR_IStockPrice;
import com.firstrain.obj.IEntityInfo;

public class EventObj implements IEvents {

	private int dayId = -1;
	private String caption;
	private String description;
	private String url;
	private Date date;
	private boolean linkable;
	private EventGroupEnum eventGroup;
	private EventTypeEnum eventType;
	private EventEntityTypeEnum eventEntityType;
	private LinkedHashMap<String, Object> props = null;
	private boolean trigger;
	private int eventTypeId;
	private int flag = -1;
	private double score = 0;
	private long entityId = -1;
	private IEntityInfo entityInfo = null;
	private int eventId = -1;
	private String configXML = null;
	private boolean eventExpired = false;
	private Set<Long> primaryEvidenceEntityIds;
	private Date reportDate;

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

	public void setEventExpired(boolean eventExpired) {
		this.eventExpired = eventExpired;
	}

	public void addPrimaryEvidence(Long entityId) {
		if (this.primaryEvidenceEntityIds == null) {
			this.primaryEvidenceEntityIds = new HashSet<Long>();
		}
		this.primaryEvidenceEntityIds.add(entityId);
	}

	@Override
	public Set<Long> getPrimaryEvidenceEntityIds() {
		return this.primaryEvidenceEntityIds;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.firstrain.portal.object.Event#hasExpired()
	 */
	@Override
	public boolean hasExpired() {
		return this.eventExpired || getUrl() == null || getUrl().isEmpty();
	}

	public String getConfigXml() {
		return configXML;
	}

	public void setConfigXml(String xmlConfig) {
		this.configXML = xmlConfig;
	}

	@Override
	public long getEntityId() {
		return entityId;
	}

	public void setEntityId(long entityId) {
		this.entityId = entityId;
	}

	@Override
	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	@Override
	public boolean isTrigger() {
		return trigger;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getUrl() {
		if (eventGroup == EventGroupEnum.GROUP_8K_FILING || eventGroup == EventGroupEnum.GROUP_WEB_VOLUME
				|| eventGroup == EventGroupEnum.GROUP_MGMT_CHANGE || eventGroup == EventGroupEnum.GROUP_DELAYED_FILING) {
			ensureProperties();
		}
		return url;
	}

	@Override
	public Date getDate() {
		return date;
	}

	public String getLink() {
		return getUrl();
	}

	public boolean isLinkable() {
		return linkable;
	}

	@Override
	public int getFlag() {
		return flag;
	}

	public void setEventGroup(EventGroupEnum signalType) {
		this.eventGroup = signalType;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setLinkable(boolean linkable) {
		this.linkable = linkable;
	}

	@Override
	public String getCaption() {
		if (eventGroup == EventGroupEnum.GROUP_WEB_VOLUME) {
			ensureProperties();
		}
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public void setTrigger(boolean trigger) {
		this.trigger = trigger;
	}

	@Override
	public String toString() {
		return new Formatter().format("[%s][%s][%s][%s] %s", caption, eventEntityType, eventType, date.toString(), url).toString();
	}

	@Override
	public EventGroupEnum getEventGroup() {
		return eventGroup;
	}

	@Override
	public EventTypeEnum getEventType() {
		return eventType;
	}

	@Override
	public EventEntityTypeEnum getEventEntityType() {
		return this.eventEntityType;
	}

	@Override
	public Map<String, Object> getProperties() {
		ensureProperties();
		return props;
	}

	public void setEventType(EventTypeEnum eventType) {
		this.eventType = eventType;
	}

	public void setEventEntityType(EventEntityTypeEnum eventEntityType) {
		this.eventEntityType = eventEntityType;
	}

	public int getEventTypeId() {
		return eventTypeId;
	}

	public void setEventTypeId(int eventTypeId) {
		this.eventTypeId = eventTypeId;
	}

	public void setProps(LinkedHashMap<String, Object> props) {
		this.props = props;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public void setEventId(int eventId) {
		this.eventId = eventId;
	}

	@Override
	public int getEventId() {
		return this.eventId;
	}

	@Override
	public IEntityInfo getEntityInfo() {
		return entityInfo;
	}

	public void setEntityInfo(IEntityInfo entityInfo) {
		this.entityInfo = entityInfo;
	}

	void ensureProperties() {
		if (props != null) {
			return;
		}

		LinkedHashMap<String, Object> props = new LinkedHashMap<String, Object>();
		setProps(props);


		// Management Change : 1-299
		if (eventTypeId >= 1 && eventTypeId <= 299) {
			// This is dealt with in a greedy manner
			// during event population from db.
			// So, dont do anything here.
		}
		// Stock Price : 300-349
		else if (eventTypeId >= 300 && eventTypeId <= 349) {

			FR_IStockPrice sp = EventConfigUtil.parserStockPriceConfig(configXML);
			// Company Name, Ticker, Event Date, Event Type, Closing Price, 50 Day Average, Trading Volume, % Change ,minPrice , maxPrice ,
			// OpeningPrice , PreviousClosingPrice
			props.put("closingPrice", sp.getClosingPrice());
			props.put("avg50Day", sp.getAvg50Day());
			props.put("avg100Day", sp.getAvg100Day());
			props.put("avg200Day", sp.getAvg200Day());
			props.put("avg52Week", sp.getAvg364Day());
			props.put("tradingVolume", sp.getTradingVolume());
			props.put("percentChange", sp.getPercentageChange());
			props.put("minPrice", sp.getMinPrice());
			props.put("maxPrice", sp.getMaxPrice());
			props.put("previousClosingPrice", sp.getPreviousClosingPrice());
			props.put("openingPrice", sp.getOpeningPrice());

			if (eventTypeId >= 301 && eventTypeId <= 304) {
				props.put("eventMovingAvg", getEventMovingAvg(sp));
			}
		}
		// Web Volume : 350-399
		else if (eventTypeId >= 350 && eventTypeId <= 399) {

			WebVolumeConfObj wv = EventConfigUtil.parserWebVolumeConfig(configXML);
			setEventGroup(EventGroupEnum.GROUP_WEB_VOLUME);
			setLinkable(true);
			setCaption(wv.getTitle());
			setUrl(wv.getTitleUrl());

			// Company Name, Ticker, Event Date, Event Type, Z-Score, Title, Support URL
			props.put("Z-Score", getScore());
			props.put("title", wv.getTitle());
			props.put("titleUrl", wv.getTitleUrl());
		}
		// 8K Filing : 400-499
		else if (eventTypeId >= 400 && eventTypeId <= 499) {
			Filings8KConfObj f8k = EventConfigUtil.parser8KFilingsConfig(configXML);
			// Company Name, Ticker, Event Date, Event Type, Item #, Title string:"Entry into a Material Definitive Agreement", Doc URL
			props.put("DocTitle", f8k.getTitle());
			props.put("url", f8k.getTitleUrl());
			props.put("Item#", get8KItemNo(eventTypeId));

			// setCaption(get8KEventCaption(eventType));
			setUrl(f8k.getTitleUrl());
		} else if (eventTypeId >= 500 && eventTypeId <= 549) {
			DelayedConfObj dco = EventConfigUtil.parseDelayedSecFilingsConfig(configXML);
			// Company Name, Ticker, Event Date, Event Type, Item #, Title string:"Entry into a Material Definitive Agreement", Doc URL
			props.put("DocTitle", dco.getTitle());
			props.put("url", dco.getTitleUrl());
			props.put("CIKCODE", dco.getCikCode());
			setUrl(dco.getTitleUrl());
			setCaption(this.eventGroup.getLabel() + ": " + dco.getTitle());
		}

	}

	private double getEventMovingAvg(FR_IStockPrice sp) {
		double ema = 0d;
		switch (eventTypeId) {
			case FR_IEvent.TYPE_STOCK_CLOSING_PRICE_50_DAY_AVG:
				ema = EventConfigUtil.round(sp.getAvg50Day(), 2);
				break;
			case FR_IEvent.TYPE_STOCK_CLOSING_PRICE_100_DAY_AVG:
				ema = EventConfigUtil.round(sp.getAvg100Day(), 2);
				break;
			case FR_IEvent.TYPE_STOCK_CLOSING_PRICE_200_DAY_AVG:
				ema = EventConfigUtil.round(sp.getAvg200Day(), 2);
				break;
			case FR_IEvent.TYPE_STOCK_CLOSING_PRICE_BREAKING_52_WEEK_HIGH_LOW:
				ema = EventConfigUtil.round(sp.getAvg364Day(), 2);
				break;
			default:
				ema = EventConfigUtil.round(sp.getAvg50Day(), 2);
		}
		return ema;
	}

	private static String get8KItemNo(int eventType) {
		String str = null;
		switch (eventType) {

			case FR_IEvent.TYPE_EIGHT_K_EVENT_ITEM_1_03:
				str = "1.03";
				break;
			case FR_IEvent.TYPE_EIGHT_K_EVENT_ITEM_3_01:
				str = "3.01";
				break;
			case FR_IEvent.TYPE_EIGHT_K_EVENT_ITEM_1_02:
				str = "1.02";
				break;
			case FR_IEvent.TYPE_EIGHT_K_EVENT_ITEM_2_01:
				str = "2.01";
				break;
			case FR_IEvent.TYPE_EIGHT_K_EVENT_ITEM_1_01:
				str = "1.01";
				break;
			case FR_IEvent.TYPE_EIGHT_K_EVENT_ITEM_2_02:
				str = "2.02";
				break;
			case FR_IEvent.TYPE_EIGHT_K_EVENT_ITEM_2_04:
				str = "2.04";
				break;
			case FR_IEvent.TYPE_EIGHT_K_EVENT_ITEM_2_03:
				str = "2.03";
				break;
			case FR_IEvent.TYPE_EIGHT_K_EVENT_ITEM_2_06:
				str = "2.06";
				break;
			case FR_IEvent.TYPE_EIGHT_K_EVENT_ITEM_2_05:
				str = "2.05";
				break;
			case FR_IEvent.TYPE_EIGHT_K_EVENT_ITEM_5_04:
				str = "5.04";
				break;
			case FR_IEvent.TYPE_EIGHT_K_EVENT_ITEM_6_04:
				str = "6.04";
				break;
			case FR_IEvent.TYPE_EIGHT_K_EVENT_ITEM_7_01:
				str = "7.01";
				break;
			case FR_IEvent.TYPE_EIGHT_K_EVENT_ITEM_6_05:
				str = "6.05";
				break;
			case FR_IEvent.TYPE_EIGHT_K_EVENT_ITEM_3_02:
				str = "3.02";
				break;
			case FR_IEvent.TYPE_EIGHT_K_EVENT_ITEM_4_02:
				str = "4.02";
				break;
			case FR_IEvent.TYPE_EIGHT_K_EVENT_ITEM_3_03:
				str = "3.03";
				break;
			case FR_IEvent.TYPE_EIGHT_K_EVENT_ITEM_4_01:
				str = "4.01";
				break;
			case FR_IEvent.TYPE_EIGHT_K_EVENT_ITEM_5_01:
				str = "5.01";
				break;
			case FR_IEvent.TYPE_EIGHT_K_EVENT_ITEM_5_03:
				str = "5.03";
				break;
			case FR_IEvent.TYPE_EIGHT_K_EVENT_ITEM_5_05:
				str = "5.05";
				break;
			case FR_IEvent.TYPE_EIGHT_K_EVENT_ITEM_5_06:
				str = "5.06";
				break;
			case FR_IEvent.TYPE_EIGHT_K_EVENT_ITEM_6_01:
				str = "6.01";
				break;
			case FR_IEvent.TYPE_EIGHT_K_EVENT_ITEM_6_02:
				str = "6.02";
				break;
			case FR_IEvent.TYPE_EIGHT_K_EVENT_ITEM_6_03:
				str = "6.03";
				break;
			case FR_IEvent.TYPE_EIGHT_K_EVENT_ITEM_8_01:
				str = "8.01";
				break;
			case FR_IEvent.TYPE_EIGHT_K_EVENT_ITEM_5_02:
				str = "5.02";
				break;
			default:
				break;
		}
		return str;
	}

	@Override
	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	/*
	 * private String getWebVolumeCaption (double score) {
	 * 
	 * String str = null; if(score >= 0) { if(score >= 10) { str = "Coverage Spike: Total Coverage (Major Spike) >= 10"; } else if(score >=
	 * 5) { str = "Coverage Spike: Total Coverage (Medium Spike) >= 5"; } else { str = "Coverage Spike: Total Coverage (Low Spike) >= 0"; }
	 * } else { return null; } return str; }
	 */

}
