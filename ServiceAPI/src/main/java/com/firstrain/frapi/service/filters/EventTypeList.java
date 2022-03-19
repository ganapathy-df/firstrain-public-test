package com.firstrain.frapi.service.filters;

import com.firstrain.frapi.events.IEvents.EventTypeEnum;

/**
 * This class is to hold all the used types of events statically for events filter. All event types are grouped according to
 * EventsandSignals 2010Sep07.xls.
 * 
 * @author Deepak
 * @see EventFilterList
 */
public final class EventTypeList {

	/**
	 * To ensure that default constructor can't be invoked by others.
	 */
	private EventTypeList() {
		super();
	}

	/**
	 * Represents the selected events of following groups:
	 * <ol>
	 * <li>8K Filing Rank 4</li>
	 * <li>8K Filing Rank 3</li>
	 * <li>8K Filing Rank 2</li>
	 * </ol>
	 */
	public static final EventTypeEnum[] eightKFilterEventTypes = {
			// 8K Filing rank 4
			EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_5_02,

			// 8K Filing rank 3
			EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_3_01, EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_4_01,
			EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_5_03, EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_5_04,
			EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_6_01, EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_6_02,
			EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_6_03, EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_6_05,

			// 8K Filing rank 2
			EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_2_05, EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_3_02,
			EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_3_03, EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_5_05,
			EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_6_04, EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_8_01,};

	/**
	 * Represents 8K events of Rank 3.
	 */
	public static final EventTypeEnum[] eightKRank34FilterEventTypes = {EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_5_04,
			EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_4_01, EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_5_03,
			EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_3_01, EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_6_01,
			EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_6_02, EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_6_03,
			EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_6_05, EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_5_02,};

	/**
	 * Represents 8K events of Rank 2.
	 */
	public static final EventTypeEnum[] eightKRank2FilterEventTypes =
			{EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_2_05, EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_3_02,
					EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_3_03, EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_5_05,
					EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_6_04, EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_8_01,};

	/**
	 * Represents 8K events of Rank 1.
	 */
	public static final EventTypeEnum[] eightKRank1FilterEventTypes =
			{EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_2_01, EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_1_03,
					EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_7_01, EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_5_06,
					EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_5_01, EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_1_01,
					EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_1_02, EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_2_02,
					EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_2_03, EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_2_04,
					EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_2_06, EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_4_02,};

	public static final EventTypeEnum[] tenQFilterEventTypes = {EventTypeEnum.TYPE_TEN_Q_EVENT,};

	public static final EventTypeEnum[] tenKFilterEventTypes = {EventTypeEnum.TYPE_TEN_K_EVENT,};

	/**
	 * Represents Internal Management Turnover events of Rank 4 & 5.
	 */
	public static final EventTypeEnum[] mgmtTurnOverRank45FilterEventTypes =
			{EventTypeEnum.TYPE_MGMT_CHANGE_EXECUTIVE_DIRECTOR, EventTypeEnum.TYPE_MGMT_CHANGE_SENIORVP_TURNOVER,
					EventTypeEnum.TYPE_MGMT_CHANGE_VP_TURNOVER, EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_RISK_OFFICER,
					EventTypeEnum.TYPE_MGMT_CHANGE_JOINT_MANAGING_DIRECTOR, EventTypeEnum.TYPE_MGMT_CHANGE_CORPORATE_VICE_PRESIDENT,
					EventTypeEnum.TYPE_MGMT_CHANGE_DIVISIONAL_VICE_PRESIDENT, EventTypeEnum.TYPE_MGMT_CHANGE_GLOBAL_VICE_PRESIDENT,

					EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_BUSINESS_DEVELOPMENT_OFFICER,
					EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_BUSINESS_OFFICER, EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_COMMERCIAL_OFFICER,
					EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_COMMUNICATIONS_OFFICER, EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_COMPLIANCE_OFFICER,
					EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_CREATIVE_OFFICER, EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_DATA_OFFICER,
					EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_DEVELOPMENT_OFFICER, EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_DIVERSITY_OFFICER,
					EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_HUMAN_RESOURCE_OFFICER, EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_KNOWLEDGE_OFFICER,
					EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_PROCESS_OFFICER, EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_SALES_OFFICER,
					EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_SCIENTIFIC_AND_TECHNICAL_OFFICER,
					EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_SECURITY_OFFICER, EventTypeEnum.TYPE_MGMT_CHANGE_CO_OPERATING_OFFICER,
					EventTypeEnum.TYPE_MGMT_CHANGE_GLOBAL_EXECUTIVE_OFFICER, EventTypeEnum.TYPE_MGMT_CHANGE_GLOBAL_OFFICER,
					EventTypeEnum.TYPE_MGMT_CHANGE_GLOBAL_OPERATING_OFFICER, EventTypeEnum.TYPE_MGMT_CHANGE_GROUP_EXECUTIVE_OFFICER,
					EventTypeEnum.TYPE_MGMT_CHANGE_INTERIM_ADMINISTRATIVE_OFFICER, EventTypeEnum.TYPE_MGMT_CHANGE_INTERIM_EXECUTIVE_OFFICER,
					EventTypeEnum.TYPE_MGMT_CHANGE_INTERIM_FINANCIAL_OFFICER, EventTypeEnum.TYPE_MGMT_CHANGE_JOINT_EXECUTIVE_OFFICER,
					EventTypeEnum.TYPE_MGMT_CHANGE_SENIOR_EXECUTIVE_OFFICER, EventTypeEnum.TYPE_MGMT_CHANGE_SENIOR_MARKETING_OFFICER,
					EventTypeEnum.TYPE_MGMT_CHANGE_SENIOR_OPERATING_OFFICER, EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_ADVISOR,
					EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_ARCHITECT, EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_COUNSEL,
					EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_DIRECTOR, EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_ECONOMIST,
					EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_EQUITY_MARKET_STRATEGIST, EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_GENERAL_MANAGER,
					EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_HEAD, EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_OPERATIONS_MANAGER,
					EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_SALES_MANAGER, EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_SCIENTIST,
					EventTypeEnum.TYPE_MGMT_CHANGE_CO_PRESIDENT, EventTypeEnum.TYPE_MGMT_CHANGE_COUNTRY_DIRECTOR,
					EventTypeEnum.TYPE_MGMT_CHANGE_COUNTRY_EXECUTIVE, EventTypeEnum.TYPE_MGMT_CHANGE_COUNTRY_SALES_MANAGER,
					EventTypeEnum.TYPE_MGMT_CHANGE_EXECUTIVE, EventTypeEnum.TYPE_MGMT_CHANGE_EXECUTIVE_ADVISOR,
					EventTypeEnum.TYPE_MGMT_CHANGE_EXECUTIVE_EQUITY_MARKET_STRATEGIST,
					EventTypeEnum.TYPE_MGMT_CHANGE_EXECUTIVE_GENERAL_MANAGER, EventTypeEnum.TYPE_MGMT_CHANGE_EXECUTIVE_HEAD,
					EventTypeEnum.TYPE_MGMT_CHANGE_EXECUTIVE_LEADER, EventTypeEnum.TYPE_MGMT_CHANGE_EXECUTIVE_MANAGER,
					EventTypeEnum.TYPE_MGMT_CHANGE_EXECUTIVE_MANAGING_DIRECTOR, EventTypeEnum.TYPE_MGMT_CHANGE_EXECUTIVE_PRESIDENT,
					EventTypeEnum.TYPE_MGMT_CHANGE_EXECUTIVE_TREASURER, EventTypeEnum.TYPE_MGMT_CHANGE_GLOBAL_MANAGING_DIRECTOR,
					EventTypeEnum.TYPE_MGMT_CHANGE_GROUP_MANAGING_DIRECTOR, EventTypeEnum.TYPE_MGMT_CHANGE_INTERIM_MANAGING_DIRECTOR,
					EventTypeEnum.TYPE_MGMT_CHANGE_AREA_VICE_PRESIDENT, EventTypeEnum.TYPE_MGMT_CHANGE_ASSISTANT_VICE_PRESIDENT,
					EventTypeEnum.TYPE_MGMT_CHANGE_ASSOCIATE_VICE_PRESIDENT, EventTypeEnum.TYPE_MGMT_CHANGE_GROUP_VICE_PRESIDENT,
					EventTypeEnum.TYPE_MGMT_CHANGE_NATIONAL_VICE_PRESIDENT, EventTypeEnum.TYPE_MGMT_CHANGE_REGIONAL_VICE_PRESIDENT,
					EventTypeEnum.TYPE_MGMT_CHANGE_AREA_GENERAL_MANAGER, EventTypeEnum.TYPE_MGMT_CHANGE_ASSISTANT_GENERAL_MANAGER,
					EventTypeEnum.TYPE_MGMT_CHANGE_ASSOCIATE_GENERAL_MANAGER, EventTypeEnum.TYPE_MGMT_CHANGE_CORPORATE_GENERAL_MANAGER,
					EventTypeEnum.TYPE_MGMT_CHANGE_DIVISIONAL_GENERAL_MANAGER, EventTypeEnum.TYPE_MGMT_CHANGE_GROUP_GENERAL_MANAGER,
					EventTypeEnum.TYPE_MGMT_CHANGE_SENIOR_GENERAL_MANAGER,

			};

	/**
	 * Represents Internal Management Turnover events of Rank 3.
	 */
	public static final EventTypeEnum[] mgmtTurnOverRank3FilterEventTypes =
			{EventTypeEnum.TYPE_MGMT_CHANGE_COO_TURNOVER, EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_OFFICER,
					EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_MRKT_OFFICER_TURNOVER, EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_INFO_OFFICER_TURNOVER,
					EventTypeEnum.TYPE_MGMT_CHANGE_EXEC_VICE_PRESIDENT_TURNOVER, EventTypeEnum.TYPE_MGMT_CHANGE_BOARD_MEMBER,
					EventTypeEnum.TYPE_MGMT_CHANGE_MEMBER_OF_BOARD, EventTypeEnum.TYPE_MGMT_CHANGE_INTERIM_CHAIRMAN_OF_BOARD,
					EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_ADMINISTRATIVE_OFFICER, EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_CREDIT_OFFICER,
					EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_INVESTMENT_OFFICER, EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_LEGAL_OFFICER,
					EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_MEDICAL_OFFICER, EventTypeEnum.TYPE_MGMT_CHANGE_COUNTRY_GENERAL_MANAGER,
					EventTypeEnum.TYPE_MGMT_CHANGE_COUNTRY_HEAD_MANAGER, EventTypeEnum.TYPE_MGMT_CHANGE_COUNTRY_MANAGER,
					EventTypeEnum.TYPE_MGMT_CHANGE_COUNTRY_MANAGING_DIRECTOR, EventTypeEnum.TYPE_MGMT_CHANGE_COUNTRY_PRESIDENT,
					EventTypeEnum.TYPE_MGMT_CHANGE_GENERAL_MANAGER, EventTypeEnum.TYPE_MGMT_CHANGE_GLOBAL_GENERAL_MANAGER,
					EventTypeEnum.TYPE_MGMT_CHANGE_NATIONAL_GENERAL_MANAGER, EventTypeEnum.TYPE_MGMT_CHANGE_REGIONAL_GENERAL_MANAGER,

			};

	/**
	 * Represents Internal Management Turnover events of Rank 2.
	 */
	public static final EventTypeEnum[] mgmtTurnOverRank2FilterEventTypes =
			{EventTypeEnum.TYPE_MGMT_CHANGE_CFO_TURNOVER, EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_TECHNOLOGY_OFFICER_TURNOVER,
					EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_ACCOUNTING_OFFICER, EventTypeEnum.TYPE_MGMT_CHANGE_BOARD_OF_DIRECTORS,
					EventTypeEnum.TYPE_MGMT_CHANGE_CO_CHAIRMAN, EventTypeEnum.TYPE_MGMT_CHANGE_CO_CHAIRMAN_OF_BOARD,
					EventTypeEnum.TYPE_MGMT_CHANGE_DEPUTY_CHAIRMAN, EventTypeEnum.TYPE_MGMT_CHANGE_DEPUTY_CHAIRMAN_OF_BOARD,
					EventTypeEnum.TYPE_MGMT_CHANGE_GROUP_CHAIRMAN, EventTypeEnum.TYPE_MGMT_CHANGE_SENIOR_VICE_CHAIRMAN,
					EventTypeEnum.TYPE_MGMT_CHANGE_VICE_CHAIRMAN, EventTypeEnum.TYPE_MGMT_CHANGE_VICE_CHAIRMAN_OF_BOARD,
					EventTypeEnum.TYPE_MGMT_CHANGE_EXECUTIVE_BOARD_OF_DIRECTORS, EventTypeEnum.TYPE_MGMT_CHANGE_SENIOR_BOARD_OF_DIRECTORS,
					EventTypeEnum.TYPE_MGMT_CHANGE_EXECUTIVE_VICE_CHAIRMAN, EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_STRATEGY_OFFICER,
					EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_TECHNICAL_OFFICER, EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_OPERATIONAL_OFFICER,
					EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_STRATEGIST, EventTypeEnum.TYPE_MGMT_CHANGE_FOUNDER,
					EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_FINANCIAL_CONTROLLER, EventTypeEnum.TYPE_MGMT_CHANGE_CO_FOUNDER,
					EventTypeEnum.TYPE_MGMT_CHANGE_GLOBAL_PRESIDENT, EventTypeEnum.TYPE_MGMT_CHANGE_SENIOR_PARTNER,
					EventTypeEnum.TYPE_MGMT_CHANGE_SENIOR_PRESIDENT, EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_OPERATIONS_OFFICER,

			};

	/**
	 * Represents Internal Management Turnover events of Rank 1.
	 */
	public static final EventTypeEnum[] mgmtTurnOverRank1FilterEventTypes =
			{EventTypeEnum.TYPE_MGMT_CHANGE_CEO_TURNOVER, EventTypeEnum.TYPE_MGMT_CHANGE_MANAGING_DIRECTOR,
					EventTypeEnum.TYPE_MGMT_CHANGE_PRESIDENT_TURNOVER, EventTypeEnum.TYPE_MGMT_CHANGE_CHAIRMAN,
					EventTypeEnum.TYPE_MGMT_CHANGE_CHAIRMAN_OF_BOARD, EventTypeEnum.TYPE_MGMT_CHANGE_EXECUTIVE_CHAIRMAN_OF_BOARD,
					EventTypeEnum.TYPE_MGMT_CHANGE_CO_EXECUTIVE_OFFICER, EventTypeEnum.TYPE_MGMT_CHANGE_CHIEF_EXECUTIVE,
					EventTypeEnum.TYPE_MGMT_CHANGE_SENIOR_MANAGING_DIRECTOR, EventTypeEnum.TYPE_MGMT_CHANGE_EXECUTIVE_CHAIRMAN,

			};

	/**
	 * Represents selected stock filter events.
	 */
	public static final EventTypeEnum[] stockFilterEventTypes = {EventTypeEnum.TYPE_STOCK_CLOSING_PRICE_100_DAY_AVG,
			EventTypeEnum.TYPE_STOCK_CLOSING_PRICE_200_DAY_AVG, EventTypeEnum.TYPE_STOCK_CLOSING_PRICE_BREAKING_52_WEEK_HIGH_LOW,};
}