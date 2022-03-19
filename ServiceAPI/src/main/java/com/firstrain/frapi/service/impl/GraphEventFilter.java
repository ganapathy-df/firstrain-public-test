package com.firstrain.frapi.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.firstrain.frapi.events.EventObj;
import com.firstrain.frapi.events.IEvents;
import com.firstrain.frapi.events.IEvents.EventTypeEnum;
import com.firstrain.frapi.service.EventsFilter;

/**
 * This filter is to select unique events for graph according to "Unique Ranking" column of the sheet.
 * <p>
 * This is according to EventsandSignals 2010Sep07.xls.
 * 
 * @author Deepak
 */
public class GraphEventFilter implements EventsFilter {

	private static final int EVENTTYPE_WEB_VOLUME_HIGH_COVERAGE = -999;
	private static final int EVENTTYPE_WEB_VOLUME_TOPIC_HIGH_COVERAGE = -998;
	private static final int EVENTTYPE_WEB_VOLUME_MEDIUM_COVERAGE = -997;
	private static final int EVENTTYPE_WEB_VOLUME_TOPIC_MEDIUM_COVERAGE = -996;
	private static final int EVENTTYPE_WEB_VOLUME_LOW_COVERAGE = -995;
	private static final int EVENTTYPE_WEB_VOLUME_TOPIC_LOW_COVERAGE = -994;
	private static final int EVENTTYPE_HIGH_DAILY_STOCK_PRICE_CHANGE = -993;
	private static final int EVENTTYPE_MEDIUM_DAILY_STOCK_PRICE_CHANGE = -992;
	private static final int EVENTTYPE_LOW_DAILY_STOCK_PRICE_CHANGE = -991;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.firstrain.portal.services.EventsFilter#filterEvents(java.util.List)
	 */
	@Override
	public List<IEvents> filterEvents(List<IEvents> input) {
		if (input == null) {
			return Collections.emptyList();
		}
		if (input.size() <= 1) {
			return input;
		}
		List<IEvents> events = new ArrayList<IEvents>(input);
		List<IEvents> rv = new ArrayList<IEvents>();
		List<IEvents> dailyEvents = new ArrayList<IEvents>();
		// Qualify first event by default.
		int dayId = ((EventObj) events.get(0)).getDayId();
		dailyEvents.add(events.get(0));
		for (int i = 1; i < events.size(); i++) {
			IEvents event = events.get(i);
			if (dayId != ((EventObj) event).getDayId()) {
				dayId = ((EventObj) event).getDayId();
				IEvents selectedEvent = getQualifiedEventForGraph(dailyEvents);
				rv.add(selectedEvent);
				dailyEvents.clear();
			}
			dailyEvents.add(event);
		}
		IEvents selectedEvent = getQualifiedEventForGraph(dailyEvents);
		rv.add(selectedEvent);
		events.clear();
		dailyEvents.clear();
		return rv;
	}

	private IEvents getQualifiedEventForGraph(List<IEvents> dailyEvents) {
		if (dailyEvents.size() == 1) {
			return dailyEvents.get(0);
		}
		IEvents e = dailyEvents.get(0);
		for (int i = 1; i < dailyEvents.size(); i++) {
			IEvents ev = dailyEvents.get(i);
			if (getIndexInArray(e) > getIndexInArray(ev)) {
				e = ev;
			}
		}
		return e;
	}

	private int getIndexInArray(IEvents event) {
		int l = prioritizedUniqueEvents.length;
		int index = l * 2;
		int eTypeId = getEventTypeId(event);
		for (int i = 0; i < l; i++) {
			if (eTypeId == prioritizedUniqueEvents[i]) {
				index = i;
				break;
			}
		}
		return index;
	}

	private int getEventTypeId(IEvents event) {
		int eTypeId = event.getEventType().getId();
		if (event.getEventType() == EventTypeEnum.TYPE_TOTAL_WEB_COVERAGE_VOLUME_COMPANY) {
			eTypeId = retrieveTypeId(event, EVENTTYPE_WEB_VOLUME_HIGH_COVERAGE, EVENTTYPE_WEB_VOLUME_MEDIUM_COVERAGE, EVENTTYPE_WEB_VOLUME_LOW_COVERAGE); 
			
		} else if (event.getEventType() == EventTypeEnum.TYPE_TOTAL_WEB_COVERAGE_VOLUME_TOPIC) {
			eTypeId = retrieveTypeId(event, EVENTTYPE_WEB_VOLUME_TOPIC_HIGH_COVERAGE, EVENTTYPE_WEB_VOLUME_TOPIC_MEDIUM_COVERAGE, EVENTTYPE_WEB_VOLUME_TOPIC_LOW_COVERAGE); 
			
		} else if (event.getEventType() == EventTypeEnum.TYPE_STOCK_PRICE_CHANGE) {
			double pctChange = getStockPriceChange(event);
			if (pctChange >= 20) {
				eTypeId = EVENTTYPE_HIGH_DAILY_STOCK_PRICE_CHANGE;
			} else if (pctChange >= 10) {
				eTypeId = EVENTTYPE_MEDIUM_DAILY_STOCK_PRICE_CHANGE;
			} else {
				eTypeId = EVENTTYPE_LOW_DAILY_STOCK_PRICE_CHANGE;
			}
		}
		return eTypeId;
	}
 
	private int retrieveTypeId(final IEvents event, final int EVENTTYPE_WEB_VOLUME_TOPIC_HIGH_COVERAGE, final int EVENTTYPE_WEB_VOLUME_TOPIC_MEDIUM_COVERAGE, final int EVENTTYPE_WEB_VOLUME_TOPIC_LOW_COVERAGE) { 
		int eTypeId = 0; 
		if (event.getScore() >= 10) { 
			eTypeId = EVENTTYPE_WEB_VOLUME_TOPIC_HIGH_COVERAGE; 
		} else if (event.getScore() >= 5) { 
			eTypeId = EVENTTYPE_WEB_VOLUME_TOPIC_MEDIUM_COVERAGE; 
		} else { 
			eTypeId = EVENTTYPE_WEB_VOLUME_TOPIC_LOW_COVERAGE; 
		} 
		return eTypeId; 
	} 
	

	private double getStockPriceChange(IEvents e) {
		double pctChange = 0.0;
		Object changeObj = e.getProperties().get("percentChange");
		if (changeObj == null) {
			return pctChange;
		}

		if (changeObj instanceof Number) {
			pctChange = ((Number) changeObj).doubleValue();
		} else if (changeObj instanceof String) {
			String changeStr = ((String) changeObj).trim();
			try {
				pctChange = Double.parseDouble(changeStr);
			} catch (NumberFormatException ex) {
				// Bad event
				pctChange = 0.0;
			}
		}
		return pctChange;
	}

	private static final int[] prioritizedUniqueEvents = {EventTypeEnum.TYPE_MGMT_CHANGE_CEO_TURNOVER.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_MANAGING_DIRECTOR.getId(), EventTypeEnum.TYPE_MGMT_CHANGE_PRESIDENT_TURNOVER.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_CHAIRMAN.getId(), EventTypeEnum.TYPE_MGMT_CHANGE_CHAIRMAN_OF_BOARD.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_EXECUTIVE_CHAIRMAN_OF_BOARD.getId(), EventTypeEnum.TYPE_MGMT_CHANGE_CO_EXECUTIVE_OFFICER.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_EXECUTIVE.getId(), EventTypeEnum.TYPE_MGMT_CHANGE_SENIOR_MANAGING_DIRECTOR.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_EXECUTIVE_CHAIRMAN.getId(),
			// Web Volume : Coverage Spike: Total Coverage (Big Spike) >= 10
			EVENTTYPE_WEB_VOLUME_HIGH_COVERAGE,
			// Web Volume (Topic) : Coverage Spike: Total Coverage (Big Spike) >= 10
			EVENTTYPE_WEB_VOLUME_TOPIC_HIGH_COVERAGE, EVENTTYPE_HIGH_DAILY_STOCK_PRICE_CHANGE, EventTypeEnum.TYPE_TEN_K_EVENT.getId(),
			EventTypeEnum.TYPE_TEN_Q_EVENT.getId(), EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_1_03.getId(),
			EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_2_01.getId(), EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_7_01.getId(),
			EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_5_06.getId(), EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_5_01.getId(),
			EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_1_01.getId(), EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_1_02.getId(),
			EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_2_02.getId(), EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_2_03.getId(),
			EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_2_04.getId(), EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_2_06.getId(),
			EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_4_02.getId(), EventTypeEnum.TYPE_DELAYED_SEC_FILING.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_CFO_TURNOVER.getId(), EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_TECHNOLOGY_OFFICER_TURNOVER.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_ACCOUNTING_OFFICER.getId(), EventTypeEnum.TYPE_MGMT_CHANGE_BOARD_OF_DIRECTORS.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_CO_CHAIRMAN.getId(), EventTypeEnum.TYPE_MGMT_CHANGE_CO_CHAIRMAN_OF_BOARD.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_DEPUTY_CHAIRMAN.getId(), EventTypeEnum.TYPE_MGMT_CHANGE_DEPUTY_CHAIRMAN_OF_BOARD.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_GROUP_CHAIRMAN.getId(), EventTypeEnum.TYPE_MGMT_CHANGE_SENIOR_VICE_CHAIRMAN.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_VICE_CHAIRMAN.getId(), EventTypeEnum.TYPE_MGMT_CHANGE_VICE_CHAIRMAN_OF_BOARD.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_EXECUTIVE_BOARD_OF_DIRECTORS.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_SENIOR_BOARD_OF_DIRECTORS.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_EXECUTIVE_VICE_CHAIRMAN.getId(), EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_STRATEGY_OFFICER.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_TECHNICAL_OFFICER.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_OPERATIONAL_OFFICER.getId(), EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_STRATEGIST.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_FOUNDER.getId(), EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_FINANCIAL_CONTROLLER.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_CO_FOUNDER.getId(), EventTypeEnum.TYPE_MGMT_CHANGE_GLOBAL_PRESIDENT.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_SENIOR_PARTNER.getId(), EventTypeEnum.TYPE_MGMT_CHANGE_SENIOR_PRESIDENT.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_OPERATIONS_OFFICER.getId(), EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_2_05.getId(),
			EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_3_02.getId(), EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_3_03.getId(),
			EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_5_05.getId(), EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_6_04.getId(),
			EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_8_01.getId(), EventTypeEnum.TYPE_STOCK_CLOSING_PRICE_BREAKING_52_WEEK_HIGH_LOW.getId(),
			EventTypeEnum.TYPE_STOCK_CLOSING_PRICE_200_DAY_AVG.getId(), EventTypeEnum.TYPE_STOCK_CLOSING_PRICE_100_DAY_AVG.getId(),
			// Web Volume : Coverage Spike: Total Coverage (Medium Spike) 5-9
			EVENTTYPE_WEB_VOLUME_MEDIUM_COVERAGE, EventTypeEnum.TYPE_STOCK_CLOSING_PRICE_50_DAY_AVG.getId(),
			EVENTTYPE_MEDIUM_DAILY_STOCK_PRICE_CHANGE, EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_5_04.getId(),
			EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_4_01.getId(), EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_5_03.getId(),
			EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_3_01.getId(), EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_6_01.getId(),
			EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_6_02.getId(), EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_6_03.getId(),
			EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_6_05.getId(), EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_5_02.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_OFFICER.getId(), EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_MRKT_OFFICER_TURNOVER.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_INFO_OFFICER_TURNOVER.getId(), EventTypeEnum.TYPE_MGMT_CHANGE_COO_TURNOVER.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_EXEC_VICE_PRESIDENT_TURNOVER.getId(), EventTypeEnum.TYPE_MGMT_CHANGE_BOARD_MEMBER.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_MEMBER_OF_BOARD.getId(), EventTypeEnum.TYPE_MGMT_CHANGE_INTERIM_CHAIRMAN_OF_BOARD.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_ADMINISTRATIVE_OFFICER.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_CREDIT_OFFICER.getId(), EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_INVESTMENT_OFFICER.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_LEGAL_OFFICER.getId(), EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_MEDICAL_OFFICER.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_COUNTRY_GENERAL_MANAGER.getId(), EventTypeEnum.TYPE_MGMT_CHANGE_COUNTRY_HEAD_MANAGER.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_COUNTRY_MANAGER.getId(), EventTypeEnum.TYPE_MGMT_CHANGE_COUNTRY_MANAGING_DIRECTOR.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_COUNTRY_PRESIDENT.getId(), EventTypeEnum.TYPE_MGMT_CHANGE_GENERAL_MANAGER.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_GLOBAL_GENERAL_MANAGER.getId(), EventTypeEnum.TYPE_MGMT_CHANGE_NATIONAL_GENERAL_MANAGER.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_REGIONAL_GENERAL_MANAGER.getId(),
			// Web Volume (Topic) : Coverage Spike: Total Coverage (Medium Spike) 5-9
			EVENTTYPE_WEB_VOLUME_TOPIC_MEDIUM_COVERAGE,
			// Web Volume : Coverage Spike: Total Coverage (Low Spike) 0 - 4
			EVENTTYPE_WEB_VOLUME_LOW_COVERAGE, EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_RISK_OFFICER.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_JOINT_MANAGING_DIRECTOR.getId(), EventTypeEnum.TYPE_MGMT_CHANGE_CORPORATE_VICE_PRESIDENT.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_DIVISIONAL_VICE_PRESIDENT.getId(), EventTypeEnum.TYPE_MGMT_CHANGE_GLOBAL_VICE_PRESIDENT.getId(),
			EVENTTYPE_LOW_DAILY_STOCK_PRICE_CHANGE, EventTypeEnum.TYPE_MGMT_CHANGE_EXECUTIVE_DIRECTOR.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_SENIORVP_TURNOVER.getId(), EventTypeEnum.TYPE_MGMT_CHANGE_VP_TURNOVER.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_BUSINESS_DEVELOPMENT_OFFICER.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_BUSINESS_OFFICER.getId(), EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_COMMERCIAL_OFFICER.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_COMMUNICATIONS_OFFICER.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_COMPLIANCE_OFFICER.getId(), EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_CREATIVE_OFFICER.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_DATA_OFFICER.getId(), EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_DEVELOPMENT_OFFICER.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_DIVERSITY_OFFICER.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_HUMAN_RESOURCE_OFFICER.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_KNOWLEDGE_OFFICER.getId(), EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_PROCESS_OFFICER.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_SALES_OFFICER.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_SCIENTIFIC_AND_TECHNICAL_OFFICER.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_SECURITY_OFFICER.getId(), EventTypeEnum.TYPE_MGMT_CHANGE_CO_OPERATING_OFFICER.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_GLOBAL_EXECUTIVE_OFFICER.getId(), EventTypeEnum.TYPE_MGMT_CHANGE_GLOBAL_OFFICER.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_GLOBAL_OPERATING_OFFICER.getId(), EventTypeEnum.TYPE_MGMT_CHANGE_GROUP_EXECUTIVE_OFFICER.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_INTERIM_ADMINISTRATIVE_OFFICER.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_INTERIM_EXECUTIVE_OFFICER.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_INTERIM_FINANCIAL_OFFICER.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_JOINT_EXECUTIVE_OFFICER.getId(), EventTypeEnum.TYPE_MGMT_CHANGE_SENIOR_EXECUTIVE_OFFICER.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_SENIOR_MARKETING_OFFICER.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_SENIOR_OPERATING_OFFICER.getId(), EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_ADVISOR.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_ARCHITECT.getId(), EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_COUNSEL.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_DIRECTOR.getId(), EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_ECONOMIST.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_EQUITY_MARKET_STRATEGIST.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_GENERAL_MANAGER.getId(), EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_HEAD.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_OPERATIONS_MANAGER.getId(), EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_SALES_MANAGER.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_SCIENTIST.getId(), EventTypeEnum.TYPE_MGMT_CHANGE_CO_PRESIDENT.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_COUNTRY_DIRECTOR.getId(), EventTypeEnum.TYPE_MGMT_CHANGE_COUNTRY_EXECUTIVE.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_COUNTRY_SALES_MANAGER.getId(), EventTypeEnum.TYPE_MGMT_CHANGE_EXECUTIVE.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_EXECUTIVE_ADVISOR.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_EXECUTIVE_EQUITY_MARKET_STRATEGIST.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_EXECUTIVE_GENERAL_MANAGER.getId(), EventTypeEnum.TYPE_MGMT_CHANGE_EXECUTIVE_HEAD.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_EXECUTIVE_LEADER.getId(), EventTypeEnum.TYPE_MGMT_CHANGE_EXECUTIVE_MANAGER.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_EXECUTIVE_MANAGING_DIRECTOR.getId(), EventTypeEnum.TYPE_MGMT_CHANGE_EXECUTIVE_PRESIDENT.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_EXECUTIVE_TREASURER.getId(), EventTypeEnum.TYPE_MGMT_CHANGE_GLOBAL_MANAGING_DIRECTOR.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_GROUP_MANAGING_DIRECTOR.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_INTERIM_MANAGING_DIRECTOR.getId(), EventTypeEnum.TYPE_MGMT_CHANGE_AREA_VICE_PRESIDENT.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_ASSISTANT_VICE_PRESIDENT.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_ASSOCIATE_VICE_PRESIDENT.getId(), EventTypeEnum.TYPE_MGMT_CHANGE_GROUP_VICE_PRESIDENT.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_NATIONAL_VICE_PRESIDENT.getId(), EventTypeEnum.TYPE_MGMT_CHANGE_REGIONAL_VICE_PRESIDENT.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_AREA_GENERAL_MANAGER.getId(), EventTypeEnum.TYPE_MGMT_CHANGE_ASSISTANT_GENERAL_MANAGER.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_ASSOCIATE_GENERAL_MANAGER.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_CORPORATE_GENERAL_MANAGER.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_DIVISIONAL_GENERAL_MANAGER.getId(), EventTypeEnum.TYPE_MGMT_CHANGE_GROUP_GENERAL_MANAGER.getId(),
			EventTypeEnum.TYPE_MGMT_CHANGE_SENIOR_GENERAL_MANAGER.getId(),
			// Web Volume (Topic): Coverage Spike: Total Coverage (Low Spike) < 4
			EVENTTYPE_WEB_VOLUME_TOPIC_LOW_COVERAGE,
			// Drug Trial Stage Approvals
			// SEC Forms 13, 14
			// SEC Forms 3,4,5
			// New product Introductions (Announcements)
			// Sentiment: Positive / Negative comments from Management
			EventTypeEnum.TYPE_STOCK_CLOSING_PRICE_CONTRA_TO_INDUSTRY_COMPOSITE.getId(),
			// Transaction: Bankruptcy
			// Transaction: Merger and Acquisition
			// Transaction: Private Placement
			// Transaction: Public Offering
			// Transaction: Share Buyback
			// Transaction: Shelf Registrations
			// Transaction: Spin-off / Split-off
	};
}
