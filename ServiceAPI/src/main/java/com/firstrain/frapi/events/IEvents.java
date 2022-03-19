package com.firstrain.frapi.events;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.firstrain.mip.object.FR_IEvent;
import com.firstrain.mip.object.FR_IEventEntity;
import com.firstrain.obj.IEntityInfo;

public interface IEvents {

	// Type of event groups available
	public static enum EventGroupEnum {
		GROUP_MGMT_CHANGE(FR_IEvent.GROUP_MGMT_CHANGE, "Management Change"),
		GROUP_STOCK_PRICE(FR_IEvent.GROUP_STOCK_PRICE, "Stock Price"),
		GROUP_WEB_VOLUME(FR_IEvent.GROUP_WEB_VOLUME, "Web Volume"),
		GROUP_8K_FILING(FR_IEvent.GROUP_8K_FILING, "8K Filing"),
		GROUP_DELAYED_FILING(FR_IEvent.GROUP_DELAYED_FILING, "SEC Delay"),
		GROUP_MGMT_POSITIVE_NEGATIVE_COMMENTS(FR_IEvent.GROUP_MGMT_POSITIVE_NEGATIVE_COMMENTS, "Management Comments"),
		GROUP_PUBLIC_OFFERING(FR_IEvent.GROUP_PUBLIC_OFFERING, "Public Offering"),
		GROUP_PRIVATE_PLACEMENT(FR_IEvent.GROUP_PRIVATE_PLACEMENT, "Private Placement"),
		GROUP_SPINOFF_SPILTOFF(FR_IEvent.GROUP_SPINOFF_SPILTOFF, "Spin-off/Split-off"),
		GROUP_MERGER_ACQUISITION(FR_IEvent.GROUP_MERGER_ACQUISITION, "Merger & Acquisition"),
		GROUP_SHARE_BUYBACK(FR_IEvent.GROUP_SHARE_BUYBACK, "Share Buyback"),
		GROUP_SHELF_REGISTRATIONS(FR_IEvent.GROUP_SHELF_REGISTRATIONS, "Shelf Registrations");

		EventGroupEnum(int id, String label) {
			this.id = id;
			this.label = label;
		}

		private int id;
		private String label;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

	};

	public static enum EventTypeEnum {
		//// Management Change
		TYPE_MGMT_CHANGE_CEO_TURNOVER(
				FR_IEvent.TYPE_MGMT_CHANGE_CEO_TURNOVER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover - CEO"),
		TYPE_MGMT_CHANGE_COO_TURNOVER(
				FR_IEvent.TYPE_MGMT_CHANGE_COO_TURNOVER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Chief Operating Officer"),
		TYPE_MGMT_CHANGE_CFO_TURNOVER(
				FR_IEvent.TYPE_MGMT_CHANGE_CFO_TURNOVER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Chief Financial Officer"),
		TYPE_MGMT_CHANGE_SENIORVP_TURNOVER(
				FR_IEvent.TYPE_MGMT_CHANGE_SENIORVP_TURNOVER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Senior Vice President"),
		TYPE_MGMT_CHANGE_CHIEF_TECHNOLOGY_OFFICER_TURNOVER(
				FR_IEvent.TYPE_MGMT_CHANGE_CHIEF_TECHNOLOGY_OFFICER_TURNOVER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Chief Technology Officer"),
		TYPE_MGMT_CHANGE_CHIEF_MRKT_OFFICER_TURNOVER(
				FR_IEvent.TYPE_MGMT_CHANGE_CHIEF_MRKT_OFFICER_TURNOVER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Chief Marketing Officer"),
		TYPE_MGMT_CHANGE_CHIEF_INFO_OFFICER_TURNOVER(
				FR_IEvent.TYPE_MGMT_CHANGE_CHIEF_INFO_OFFICER_TURNOVER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Chief Information Officer"),
		TYPE_MGMT_CHANGE_EXEC_VICE_PRESIDENT_TURNOVER(
				FR_IEvent.TYPE_MGMT_CHANGE_EXEC_VICE_PRESIDENT_TURNOVER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Executive Vice President"),
		TYPE_MGMT_CHANGE_PRESIDENT_TURNOVER(
				FR_IEvent.TYPE_MGMT_CHANGE_PRESIDENT_TURNOVER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - President"),
		TYPE_MGMT_CHANGE_VP_TURNOVER(
				FR_IEvent.TYPE_MGMT_CHANGE_VP_TURNOVER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Vice President"),
		TYPE_MGMT_CHANGE_CHIEF_ACCOUNTING_OFFICER(
				FR_IEvent.TYPE_MGMT_CHANGE_CHIEF_ACCOUNTING_OFFICER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Chief Accounting Officer"),
		TYPE_MGMT_CHANGE_CHIEF_OFFICER(
				FR_IEvent.TYPE_MGMT_CHANGE_CHIEF_OFFICER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Chief Officer"),
		TYPE_MGMT_CHANGE_EXECUTIVE_DIRECTOR(
				FR_IEvent.TYPE_MGMT_CHANGE_EXECUTIVE_DIRECTOR,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Executive Director"),
		TYPE_MGMT_CHANGE_MANAGING_DIRECTOR(
				FR_IEvent.TYPE_MGMT_CHANGE_MANAGING_DIRECTOR,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Managing Director"),

		TYPE_MGMT_CHANGE_CHAIRMAN(FR_IEvent.TYPE_MGMT_CHANGE_CHAIRMAN, EventGroupEnum.GROUP_MGMT_CHANGE, "Executive Turnover  - Chairman"),
		TYPE_MGMT_CHANGE_CHAIRMAN_OF_BOARD(
				FR_IEvent.TYPE_MGMT_CHANGE_CHAIRMAN_OF_BOARD,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Chairman Of Board"),
		TYPE_MGMT_CHANGE_EXECUTIVE_CHAIRMAN_OF_BOARD(
				FR_IEvent.TYPE_MGMT_CHANGE_EXECUTIVE_CHAIRMAN_OF_BOARD,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Executive Chairman Of Board"),
		TYPE_MGMT_CHANGE_CO_EXECUTIVE_OFFICER(
				FR_IEvent.TYPE_MGMT_CHANGE_CO_EXECUTIVE_OFFICER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Co Executive Officer"),
		TYPE_MGMT_CHANGE_CHIEF_EXECUTIVE(
				FR_IEvent.TYPE_MGMT_CHANGE_CHIEF_EXECUTIVE,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Chief Executive"),
		TYPE_MGMT_CHANGE_SENIOR_MANAGING_DIRECTOR(
				FR_IEvent.TYPE_MGMT_CHANGE_SENIOR_MANAGING_DIRECTOR,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Senior Managing Director"),
		TYPE_MGMT_CHANGE_EXECUTIVE_CHAIRMAN(
				FR_IEvent.TYPE_MGMT_CHANGE_EXECUTIVE_CHAIRMAN,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Executive Chairman"),

		TYPE_MGMT_CHANGE_BOARD_OF_DIRECTORS(
				FR_IEvent.TYPE_MGMT_CHANGE_BOARD_OF_DIRECTORS,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Board Of Directors"),
		TYPE_MGMT_CHANGE_CO_CHAIRMAN(
				FR_IEvent.TYPE_MGMT_CHANGE_CO_CHAIRMAN,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Co Chairman"),
		TYPE_MGMT_CHANGE_CO_CHAIRMAN_OF_BOARD(
				FR_IEvent.TYPE_MGMT_CHANGE_CO_CHAIRMAN_OF_BOARD,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Co Chairman Of Board"),
		TYPE_MGMT_CHANGE_DEPUTY_CHAIRMAN(
				FR_IEvent.TYPE_MGMT_CHANGE_DEPUTY_CHAIRMAN,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Deputy Chairman"),
		TYPE_MGMT_CHANGE_DEPUTY_CHAIRMAN_OF_BOARD(
				FR_IEvent.TYPE_MGMT_CHANGE_DEPUTY_CHAIRMAN_OF_BOARD,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Deputy Chairman Of Board"),
		TYPE_MGMT_CHANGE_GROUP_CHAIRMAN(
				FR_IEvent.TYPE_MGMT_CHANGE_GROUP_CHAIRMAN,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Group Chairman"),
		TYPE_MGMT_CHANGE_SENIOR_VICE_CHAIRMAN(
				FR_IEvent.TYPE_MGMT_CHANGE_SENIOR_VICE_CHAIRMAN,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Senior Vice Chairman"),
		TYPE_MGMT_CHANGE_VICE_CHAIRMAN(
				FR_IEvent.TYPE_MGMT_CHANGE_VICE_CHAIRMAN,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Vice Chairman"),
		TYPE_MGMT_CHANGE_VICE_CHAIRMAN_OF_BOARD(
				FR_IEvent.TYPE_MGMT_CHANGE_VICE_CHAIRMAN_OF_BOARD,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Vice Chairman Of Board"),
		TYPE_MGMT_CHANGE_EXECUTIVE_BOARD_OF_DIRECTORS(
				FR_IEvent.TYPE_MGMT_CHANGE_EXECUTIVE_BOARD_OF_DIRECTORS,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Executive Board Of Directors"),
		TYPE_MGMT_CHANGE_SENIOR_BOARD_OF_DIRECTORS(
				FR_IEvent.TYPE_MGMT_CHANGE_SENIOR_BOARD_OF_DIRECTORS,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Senior Board Of Directors"),
		TYPE_MGMT_CHANGE_EXECUTIVE_VICE_CHAIRMAN(
				FR_IEvent.TYPE_MGMT_CHANGE_EXECUTIVE_VICE_CHAIRMAN,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Executive Vice Chairman"),
		TYPE_MGMT_CHANGE_CHIEF_STRATEGY_OFFICER(
				FR_IEvent.TYPE_MGMT_CHANGE_CHIEF_STRATEGY_OFFICER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Chief Strategy Officer"),
		TYPE_MGMT_CHANGE_CHIEF_TECHNICAL_OFFICER(
				FR_IEvent.TYPE_MGMT_CHANGE_CHIEF_TECHNICAL_OFFICER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Chief Technical Officer"),
		TYPE_MGMT_CHANGE_CHIEF_OPERATIONAL_OFFICER(
				FR_IEvent.TYPE_MGMT_CHANGE_CHIEF_OPERATIONAL_OFFICER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Chief Operational Officer"),
		TYPE_MGMT_CHANGE_CHIEF_STRATEGIST(
				FR_IEvent.TYPE_MGMT_CHANGE_CHIEF_STRATEGIST,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Chief Strategist"),
		TYPE_MGMT_CHANGE_FOUNDER(FR_IEvent.TYPE_MGMT_CHANGE_FOUNDER, EventGroupEnum.GROUP_MGMT_CHANGE, "Executive Turnover  - Founder"),
		TYPE_MGMT_CHANGE_CHIEF_FINANCIAL_CONTROLLER(
				FR_IEvent.TYPE_MGMT_CHANGE_CHIEF_FINANCIAL_CONTROLLER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Chief Financial Controller"),
		TYPE_MGMT_CHANGE_CO_FOUNDER(
				FR_IEvent.TYPE_MGMT_CHANGE_CO_FOUNDER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Co Founder"),
		TYPE_MGMT_CHANGE_GLOBAL_PRESIDENT(
				FR_IEvent.TYPE_MGMT_CHANGE_GLOBAL_PRESIDENT,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Global President"),
		TYPE_MGMT_CHANGE_SENIOR_PARTNER(
				FR_IEvent.TYPE_MGMT_CHANGE_SENIOR_PARTNER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Senior Partner"),
		TYPE_MGMT_CHANGE_SENIOR_PRESIDENT(
				FR_IEvent.TYPE_MGMT_CHANGE_SENIOR_PRESIDENT,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Senior President"),
		TYPE_MGMT_CHANGE_CHIEF_OPERATIONS_OFFICER(
				FR_IEvent.TYPE_MGMT_CHANGE_CHIEF_OPERATIONS_OFFICER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Chief Operations Officer"),

		TYPE_MGMT_CHANGE_BOARD_MEMBER(
				FR_IEvent.TYPE_MGMT_CHANGE_BOARD_MEMBER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Board Member"),
		TYPE_MGMT_CHANGE_MEMBER_OF_BOARD(
				FR_IEvent.TYPE_MGMT_CHANGE_MEMBER_OF_BOARD,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Member Of Board"),
		TYPE_MGMT_CHANGE_INTERIM_CHAIRMAN_OF_BOARD(
				FR_IEvent.TYPE_MGMT_CHANGE_INTERIM_CHAIRMAN_OF_BOARD,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Interim Chairman Of Board"),
		TYPE_MGMT_CHANGE_CHIEF_ADMINISTRATIVE_OFFICER(
				FR_IEvent.TYPE_MGMT_CHANGE_CHIEF_ADMINISTRATIVE_OFFICER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Chief Administrative Officer"),
		TYPE_MGMT_CHANGE_CHIEF_CREDIT_OFFICER(
				FR_IEvent.TYPE_MGMT_CHANGE_CHIEF_CREDIT_OFFICER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Chief Credit Officer"),
		TYPE_MGMT_CHANGE_CHIEF_INVESTMENT_OFFICER(
				FR_IEvent.TYPE_MGMT_CHANGE_CHIEF_INVESTMENT_OFFICER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Chief Investment Officer"),
		TYPE_MGMT_CHANGE_CHIEF_LEGAL_OFFICER(
				FR_IEvent.TYPE_MGMT_CHANGE_CHIEF_LEGAL_OFFICER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Chief Legal Officer"),
		TYPE_MGMT_CHANGE_CHIEF_MEDICAL_OFFICER(
				FR_IEvent.TYPE_MGMT_CHANGE_CHIEF_MEDICAL_OFFICER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Chief Medical Officer"),
		TYPE_MGMT_CHANGE_COUNTRY_GENERAL_MANAGER(
				FR_IEvent.TYPE_MGMT_CHANGE_COUNTRY_GENERAL_MANAGER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Country General Manager"),
		TYPE_MGMT_CHANGE_COUNTRY_HEAD_MANAGER(
				FR_IEvent.TYPE_MGMT_CHANGE_COUNTRY_HEAD_MANAGER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Country Head"),
		TYPE_MGMT_CHANGE_COUNTRY_MANAGER(
				FR_IEvent.TYPE_MGMT_CHANGE_COUNTRY_MANAGER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Country Manager"),
		TYPE_MGMT_CHANGE_COUNTRY_MANAGING_DIRECTOR(
				FR_IEvent.TYPE_MGMT_CHANGE_COUNTRY_MANAGING_DIRECTOR,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Country Managing Director"),
		TYPE_MGMT_CHANGE_COUNTRY_PRESIDENT(
				FR_IEvent.TYPE_MGMT_CHANGE_COUNTRY_PRESIDENT,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Country President"),
		TYPE_MGMT_CHANGE_GENERAL_MANAGER(
				FR_IEvent.TYPE_MGMT_CHANGE_GENERAL_MANAGER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - General Manager"),
		TYPE_MGMT_CHANGE_GLOBAL_GENERAL_MANAGER(
				FR_IEvent.TYPE_MGMT_CHANGE_GLOBAL_GENERAL_MANAGER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Global General Manager"),
		TYPE_MGMT_CHANGE_NATIONAL_GENERAL_MANAGER(
				FR_IEvent.TYPE_MGMT_CHANGE_NATIONAL_GENERAL_MANAGER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - National General Manager"),
		TYPE_MGMT_CHANGE_REGIONAL_GENERAL_MANAGER(
				FR_IEvent.TYPE_MGMT_CHANGE_REGIONAL_GENERAL_MANAGER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Regional General Manager"),

		TYPE_MGMT_CHANGE_CHIEF_RISK_OFFICER(
				FR_IEvent.TYPE_MGMT_CHANGE_CHIEF_RISK_OFFICER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Chief Risk Officer"),
		TYPE_MGMT_CHANGE_JOINT_MANAGING_DIRECTOR(
				FR_IEvent.TYPE_MGMT_CHANGE_JOINT_MANAGING_DIRECTOR,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Joint Managing Director"),
		TYPE_MGMT_CHANGE_CORPORATE_VICE_PRESIDENT(
				FR_IEvent.TYPE_MGMT_CHANGE_CORPORATE_VICE_PRESIDENT,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Corporate Vice President"),
		TYPE_MGMT_CHANGE_DIVISIONAL_VICE_PRESIDENT(
				FR_IEvent.TYPE_MGMT_CHANGE_DIVISIONAL_VICE_PRESIDENT,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Divisional Vice President"),
		TYPE_MGMT_CHANGE_GLOBAL_VICE_PRESIDENT(
				FR_IEvent.TYPE_MGMT_CHANGE_GLOBAL_VICE_PRESIDENT,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Global Vice President"),

		TYPE_MGMT_CHANGE_CHIEF_BUSINESS_DEVELOPMENT_OFFICER(
				FR_IEvent.TYPE_MGMT_CHANGE_CHIEF_BUSINESS_DEVELOPMENT_OFFICER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Chief Business Development Officer"),
		TYPE_MGMT_CHANGE_CHIEF_BUSINESS_OFFICER(
				FR_IEvent.TYPE_MGMT_CHANGE_CHIEF_BUSINESS_OFFICER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Chief Business Officer"),
		TYPE_MGMT_CHANGE_CHIEF_COMMERCIAL_OFFICER(
				FR_IEvent.TYPE_MGMT_CHANGE_CHIEF_COMMERCIAL_OFFICER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Chief Commercial Officer"),
		TYPE_MGMT_CHANGE_CHIEF_COMMUNICATIONS_OFFICER(
				FR_IEvent.TYPE_MGMT_CHANGE_CHIEF_COMMUNICATIONS_OFFICER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Chief Communications Officer"),
		TYPE_MGMT_CHANGE_CHIEF_COMPLIANCE_OFFICER(
				FR_IEvent.TYPE_MGMT_CHANGE_CHIEF_COMPLIANCE_OFFICER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Chief Compliance Officer"),
		TYPE_MGMT_CHANGE_CHIEF_CREATIVE_OFFICER(
				FR_IEvent.TYPE_MGMT_CHANGE_CHIEF_CREATIVE_OFFICER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Chief Creative Officer"),
		TYPE_MGMT_CHANGE_CHIEF_DATA_OFFICER(
				FR_IEvent.TYPE_MGMT_CHANGE_CHIEF_DATA_OFFICER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Chief Data Officer"),
		TYPE_MGMT_CHANGE_CHIEF_DEVELOPMENT_OFFICER(
				FR_IEvent.TYPE_MGMT_CHANGE_CHIEF_DEVELOPMENT_OFFICER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Chief Development Officer"),
		TYPE_MGMT_CHANGE_CHIEF_DIVERSITY_OFFICER(
				FR_IEvent.TYPE_MGMT_CHANGE_CHIEF_DIVERSITY_OFFICER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Chief Diversity Officer"),
		TYPE_MGMT_CHANGE_CHIEF_HUMAN_RESOURCE_OFFICER(
				FR_IEvent.TYPE_MGMT_CHANGE_CHIEF_HUMAN_RESOURCE_OFFICER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Chief Human Resource Officer"),
		TYPE_MGMT_CHANGE_CHIEF_KNOWLEDGE_OFFICER(
				FR_IEvent.TYPE_MGMT_CHANGE_CHIEF_KNOWLEDGE_OFFICER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Chief Knowledge Officer"),
		TYPE_MGMT_CHANGE_CHIEF_PROCESS_OFFICER(
				FR_IEvent.TYPE_MGMT_CHANGE_CHIEF_PROCESS_OFFICER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Chief Process Officer"),
		TYPE_MGMT_CHANGE_CHIEF_SALES_OFFICER(
				FR_IEvent.TYPE_MGMT_CHANGE_CHIEF_SALES_OFFICER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Chief Sales Officer"),
		TYPE_MGMT_CHANGE_CHIEF_SCIENTIFIC_AND_TECHNICAL_OFFICER(
				FR_IEvent.TYPE_MGMT_CHANGE_CHIEF_SCIENTIFIC_AND_TECHNICAL_OFFICER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Chief Scientific And Technical Officer"),
		TYPE_MGMT_CHANGE_CHIEF_SECURITY_OFFICER(
				FR_IEvent.TYPE_MGMT_CHANGE_CHIEF_SECURITY_OFFICER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Chief Security Officer"),
		TYPE_MGMT_CHANGE_CO_OPERATING_OFFICER(
				FR_IEvent.TYPE_MGMT_CHANGE_CO_OPERATING_OFFICER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Co Operating Officer"),
		TYPE_MGMT_CHANGE_GLOBAL_EXECUTIVE_OFFICER(
				FR_IEvent.TYPE_MGMT_CHANGE_GLOBAL_EXECUTIVE_OFFICER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Global Executive Officer"),
		TYPE_MGMT_CHANGE_GLOBAL_OFFICER(
				FR_IEvent.TYPE_MGMT_CHANGE_GLOBAL_OFFICER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Global Officer"),
		TYPE_MGMT_CHANGE_GLOBAL_OPERATING_OFFICER(
				FR_IEvent.TYPE_MGMT_CHANGE_GLOBAL_OPERATING_OFFICER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Global Operating Officer"),
		TYPE_MGMT_CHANGE_GROUP_EXECUTIVE_OFFICER(
				FR_IEvent.TYPE_MGMT_CHANGE_GROUP_EXECUTIVE_OFFICER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Group Executive Officer"),
		TYPE_MGMT_CHANGE_INTERIM_ADMINISTRATIVE_OFFICER(
				FR_IEvent.TYPE_MGMT_CHANGE_INTERIM_ADMINISTRATIVE_OFFICER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Interim Administrative Officer"),
		TYPE_MGMT_CHANGE_INTERIM_EXECUTIVE_OFFICER(
				FR_IEvent.TYPE_MGMT_CHANGE_INTERIM_EXECUTIVE_OFFICER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Interim Executive Officer"),
		TYPE_MGMT_CHANGE_INTERIM_FINANCIAL_OFFICER(
				FR_IEvent.TYPE_MGMT_CHANGE_INTERIM_FINANCIAL_OFFICER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Interim Financial Officer"),
		TYPE_MGMT_CHANGE_JOINT_EXECUTIVE_OFFICER(
				FR_IEvent.TYPE_MGMT_CHANGE_JOINT_EXECUTIVE_OFFICER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Joint Executive Officer"),
		TYPE_MGMT_CHANGE_SENIOR_EXECUTIVE_OFFICER(
				FR_IEvent.TYPE_MGMT_CHANGE_SENIOR_EXECUTIVE_OFFICER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Senior Executive Officer"),
		TYPE_MGMT_CHANGE_SENIOR_MARKETING_OFFICER(
				FR_IEvent.TYPE_MGMT_CHANGE_SENIOR_MARKETING_OFFICER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Senior Marketing Officer"),
		TYPE_MGMT_CHANGE_SENIOR_OPERATING_OFFICER(
				FR_IEvent.TYPE_MGMT_CHANGE_SENIOR_OPERATING_OFFICER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Senior Operating Officer"),
		TYPE_MGMT_CHANGE_CHIEF_ADVISOR(
				FR_IEvent.TYPE_MGMT_CHANGE_CHIEF_ADVISOR,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Chief Advisor"),
		TYPE_MGMT_CHANGE_CHIEF_ARCHITECT(
				FR_IEvent.TYPE_MGMT_CHANGE_CHIEF_ARCHITECT,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Chief Architect"),
		TYPE_MGMT_CHANGE_CHIEF_COUNSEL(
				FR_IEvent.TYPE_MGMT_CHANGE_CHIEF_COUNSEL,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Chief Counsel"),
		TYPE_MGMT_CHANGE_CHIEF_DIRECTOR(
				FR_IEvent.TYPE_MGMT_CHANGE_CHIEF_DIRECTOR,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Chief Director"),
		TYPE_MGMT_CHANGE_CHIEF_ECONOMIST(
				FR_IEvent.TYPE_MGMT_CHANGE_CHIEF_ECONOMIST,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Chief Economist"),
		TYPE_MGMT_CHANGE_CHIEF_EQUITY_MARKET_STRATEGIST(
				FR_IEvent.TYPE_MGMT_CHANGE_CHIEF_EQUITY_MARKET_STRATEGIST,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Chief Equity Market Strategist"),
		TYPE_MGMT_CHANGE_CHIEF_GENERAL_MANAGER(
				FR_IEvent.TYPE_MGMT_CHANGE_CHIEF_GENERAL_MANAGER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Chief General Manager"),
		TYPE_MGMT_CHANGE_CHIEF_HEAD(
				FR_IEvent.TYPE_MGMT_CHANGE_CHIEF_HEAD,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Chief Head"),
		TYPE_MGMT_CHANGE_CHIEF_OPERATIONS_MANAGER(
				FR_IEvent.TYPE_MGMT_CHANGE_CHIEF_OPERATIONS_MANAGER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Chief Operations Manager"),
		TYPE_MGMT_CHANGE_CHIEF_SALES_MANAGER(
				FR_IEvent.TYPE_MGMT_CHANGE_CHIEF_SALES_MANAGER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Chief Sales Manager"),
		TYPE_MGMT_CHANGE_CHIEF_SCIENTIST(
				FR_IEvent.TYPE_MGMT_CHANGE_CHIEF_SCIENTIST,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Chief Scientist"),
		TYPE_MGMT_CHANGE_CO_PRESIDENT(
				FR_IEvent.TYPE_MGMT_CHANGE_CO_PRESIDENT,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Co President"),
		TYPE_MGMT_CHANGE_COUNTRY_DIRECTOR(
				FR_IEvent.TYPE_MGMT_CHANGE_COUNTRY_DIRECTOR,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Country Director"),
		TYPE_MGMT_CHANGE_COUNTRY_EXECUTIVE(
				FR_IEvent.TYPE_MGMT_CHANGE_COUNTRY_EXECUTIVE,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Country Executive"),
		TYPE_MGMT_CHANGE_COUNTRY_SALES_MANAGER(
				FR_IEvent.TYPE_MGMT_CHANGE_COUNTRY_SALES_MANAGER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Country Sales Manager"),
		TYPE_MGMT_CHANGE_EXECUTIVE(
				FR_IEvent.TYPE_MGMT_CHANGE_EXECUTIVE,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Executive"),
		TYPE_MGMT_CHANGE_EXECUTIVE_ADVISOR(
				FR_IEvent.TYPE_MGMT_CHANGE_EXECUTIVE_ADVISOR,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Executive Advisor"),
		TYPE_MGMT_CHANGE_EXECUTIVE_EQUITY_MARKET_STRATEGIST(
				FR_IEvent.TYPE_MGMT_CHANGE_EXECUTIVE_EQUITY_MARKET_STRATEGIST,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Executive Equity Market Strategist"),
		TYPE_MGMT_CHANGE_EXECUTIVE_GENERAL_MANAGER(
				FR_IEvent.TYPE_MGMT_CHANGE_EXECUTIVE_GENERAL_MANAGER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Executive General Manager"),
		TYPE_MGMT_CHANGE_EXECUTIVE_HEAD(
				FR_IEvent.TYPE_MGMT_CHANGE_EXECUTIVE_HEAD,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Executive Head"),
		TYPE_MGMT_CHANGE_EXECUTIVE_LEADER(
				FR_IEvent.TYPE_MGMT_CHANGE_EXECUTIVE_LEADER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Executive Leader"),
		TYPE_MGMT_CHANGE_EXECUTIVE_MANAGER(
				FR_IEvent.TYPE_MGMT_CHANGE_EXECUTIVE_MANAGER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Executive Manager"),
		TYPE_MGMT_CHANGE_EXECUTIVE_MANAGING_DIRECTOR(
				FR_IEvent.TYPE_MGMT_CHANGE_EXECUTIVE_MANAGING_DIRECTOR,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Executive Managing Director"),
		TYPE_MGMT_CHANGE_EXECUTIVE_PRESIDENT(
				FR_IEvent.TYPE_MGMT_CHANGE_EXECUTIVE_PRESIDENT,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Executive President"),
		TYPE_MGMT_CHANGE_EXECUTIVE_TREASURER(
				FR_IEvent.TYPE_MGMT_CHANGE_EXECUTIVE_TREASURER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Executive Treasurer"),
		TYPE_MGMT_CHANGE_GLOBAL_MANAGING_DIRECTOR(
				FR_IEvent.TYPE_MGMT_CHANGE_GLOBAL_MANAGING_DIRECTOR,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Global Managing Director"),
		TYPE_MGMT_CHANGE_GROUP_MANAGING_DIRECTOR(
				FR_IEvent.TYPE_MGMT_CHANGE_GROUP_MANAGING_DIRECTOR,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Group Managing Director"),
		TYPE_MGMT_CHANGE_INTERIM_MANAGING_DIRECTOR(
				FR_IEvent.TYPE_MGMT_CHANGE_INTERIM_MANAGING_DIRECTOR,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Interim Managing Director"),
		TYPE_MGMT_CHANGE_AREA_VICE_PRESIDENT(
				FR_IEvent.TYPE_MGMT_CHANGE_AREA_VICE_PRESIDENT,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Area Vice President"),
		TYPE_MGMT_CHANGE_ASSISTANT_VICE_PRESIDENT(
				FR_IEvent.TYPE_MGMT_CHANGE_ASSISTANT_VICE_PRESIDENT,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Assistant Vice President"),
		TYPE_MGMT_CHANGE_ASSOCIATE_VICE_PRESIDENT(
				FR_IEvent.TYPE_MGMT_CHANGE_ASSOCIATE_VICE_PRESIDENT,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Associate Vice President"),
		TYPE_MGMT_CHANGE_GROUP_VICE_PRESIDENT(
				FR_IEvent.TYPE_MGMT_CHANGE_GROUP_VICE_PRESIDENT,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Group Vice President"),
		TYPE_MGMT_CHANGE_NATIONAL_VICE_PRESIDENT(
				FR_IEvent.TYPE_MGMT_CHANGE_NATIONAL_VICE_PRESIDENT,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - National Vice President"),
		TYPE_MGMT_CHANGE_REGIONAL_VICE_PRESIDENT(
				FR_IEvent.TYPE_MGMT_CHANGE_REGIONAL_VICE_PRESIDENT,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Regional Vice President"),
		TYPE_MGMT_CHANGE_AREA_GENERAL_MANAGER(
				FR_IEvent.TYPE_MGMT_CHANGE_AREA_GENERAL_MANAGER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Area General Manager"),
		TYPE_MGMT_CHANGE_ASSISTANT_GENERAL_MANAGER(
				FR_IEvent.TYPE_MGMT_CHANGE_ASSISTANT_GENERAL_MANAGER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Assistant General Manager"),
		TYPE_MGMT_CHANGE_ASSOCIATE_GENERAL_MANAGER(
				FR_IEvent.TYPE_MGMT_CHANGE_ASSOCIATE_GENERAL_MANAGER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Associate General Manager"),
		TYPE_MGMT_CHANGE_CORPORATE_GENERAL_MANAGER(
				FR_IEvent.TYPE_MGMT_CHANGE_CORPORATE_GENERAL_MANAGER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Corporate General Manager"),
		TYPE_MGMT_CHANGE_DIVISIONAL_GENERAL_MANAGER(
				FR_IEvent.TYPE_MGMT_CHANGE_DIVISIONAL_GENERAL_MANAGER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Divisional General Manager"),
		TYPE_MGMT_CHANGE_GROUP_GENERAL_MANAGER(
				FR_IEvent.TYPE_MGMT_CHANGE_GROUP_GENERAL_MANAGER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Group General Manager"),
		TYPE_MGMT_CHANGE_SENIOR_GENERAL_MANAGER(
				FR_IEvent.TYPE_MGMT_CHANGE_SENIOR_GENERAL_MANAGER,
				EventGroupEnum.GROUP_MGMT_CHANGE,
				"Executive Turnover  - Senior General Manager"),


		// Stock Price
		TYPE_STOCK_PRICE_CHANGE(FR_IEvent.TYPE_STOCK_PRICE_CHANGE, EventGroupEnum.GROUP_STOCK_PRICE, "Stock price change > 5%"),
		TYPE_STOCK_CLOSING_PRICE_50_DAY_AVG(
				FR_IEvent.TYPE_STOCK_CLOSING_PRICE_50_DAY_AVG,
				EventGroupEnum.GROUP_STOCK_PRICE,
				"Stock closing price crosses 50-day moving average"),
		TYPE_STOCK_CLOSING_PRICE_100_DAY_AVG(
				FR_IEvent.TYPE_STOCK_CLOSING_PRICE_100_DAY_AVG,
				EventGroupEnum.GROUP_STOCK_PRICE,
				"Stock closing price crosses 100-day moving average"),
		TYPE_STOCK_CLOSING_PRICE_200_DAY_AVG(
				FR_IEvent.TYPE_STOCK_CLOSING_PRICE_200_DAY_AVG,
				EventGroupEnum.GROUP_STOCK_PRICE,
				"Stock closing price crosses 200-day moving average"),
		TYPE_STOCK_CLOSING_PRICE_BREAKING_52_WEEK_HIGH_LOW(
				FR_IEvent.TYPE_STOCK_CLOSING_PRICE_BREAKING_52_WEEK_HIGH_LOW,
				EventGroupEnum.GROUP_STOCK_PRICE,
				"Stock closing price crosses 52 Week High / Low"),
		TYPE_STOCK_CLOSING_PRICE_CONTRA_TO_INDUSTRY_COMPOSITE(
				FR_IEvent.TYPE_STOCK_CLOSING_PRICE_CONTRA_TO_INDUSTRY_COMPOSITE,
				EventGroupEnum.GROUP_STOCK_PRICE,
				"Stock Price: Stock Closing Price Contra to Industry Composite by >5%"),

		// 8K Filing
		TYPE_EIGHT_K_EVENT_ITEM_1_03(
				FR_IEvent.TYPE_EIGHT_K_EVENT_ITEM_1_03,
				EventGroupEnum.GROUP_8K_FILING,
				"Filed an 8K Statement on Bankruptcy or Receivership"),
		TYPE_EIGHT_K_EVENT_ITEM_3_01(
				FR_IEvent.TYPE_EIGHT_K_EVENT_ITEM_3_01,
				EventGroupEnum.GROUP_8K_FILING,
				"Filed an 8K Statement on Notice of Delisting or Failure to Satisfy a Continued Listing Rule or Standard; Transfer of Listing"),
		TYPE_EIGHT_K_EVENT_ITEM_1_02(
				FR_IEvent.TYPE_EIGHT_K_EVENT_ITEM_1_02,
				EventGroupEnum.GROUP_8K_FILING,
				"Filed an 8K Statement on Termination of a Material Definitive Agreement"),
		TYPE_EIGHT_K_EVENT_ITEM_2_01(
				FR_IEvent.TYPE_EIGHT_K_EVENT_ITEM_2_01,
				EventGroupEnum.GROUP_8K_FILING,
				"Filed an 8K Statement on Completion of Acquisition or Disposition of Assets"),
		TYPE_EIGHT_K_EVENT_ITEM_1_01(
				FR_IEvent.TYPE_EIGHT_K_EVENT_ITEM_1_01,
				EventGroupEnum.GROUP_8K_FILING,
				"Filed an 8K Statement on Entry into a Material Definitive Agreement"),
		TYPE_EIGHT_K_EVENT_ITEM_2_02(
				FR_IEvent.TYPE_EIGHT_K_EVENT_ITEM_2_02,
				EventGroupEnum.GROUP_8K_FILING,
				"Filed an 8K Statement on Results of Operations and Financial Condition"),
		TYPE_EIGHT_K_EVENT_ITEM_2_04(
				FR_IEvent.TYPE_EIGHT_K_EVENT_ITEM_2_04,
				EventGroupEnum.GROUP_8K_FILING,
				"Filed an 8K Statement on Triggering Events That Accelerate or Increase a Direct Financial Obligation or an Obligation under an Off-Balance Sheet Arrangement"),
		TYPE_EIGHT_K_EVENT_ITEM_2_03(
				FR_IEvent.TYPE_EIGHT_K_EVENT_ITEM_2_03,
				EventGroupEnum.GROUP_8K_FILING,
				"Filed an 8K Statement on Creation of a Direct Financial Obligation or an Obligation under an Off-Balance Sheet Arrangement of a Registrant"),
		TYPE_EIGHT_K_EVENT_ITEM_2_06(
				FR_IEvent.TYPE_EIGHT_K_EVENT_ITEM_2_06,
				EventGroupEnum.GROUP_8K_FILING,
				"Filed an 8K Statement on Material Impairments"),
		TYPE_EIGHT_K_EVENT_ITEM_2_05(
				FR_IEvent.TYPE_EIGHT_K_EVENT_ITEM_2_05,
				EventGroupEnum.GROUP_8K_FILING,
				"Filed an 8K Statement on Costs Associated with Exit or Disposal Activities"),
		TYPE_EIGHT_K_EVENT_ITEM_5_04(
				FR_IEvent.TYPE_EIGHT_K_EVENT_ITEM_5_04,
				EventGroupEnum.GROUP_8K_FILING,
				"Filed an 8K Statement on Temporary Suspension of Trading Under Registrant's Employee Benefit Plans"),
		TYPE_EIGHT_K_EVENT_ITEM_6_04(
				FR_IEvent.TYPE_EIGHT_K_EVENT_ITEM_6_04,
				EventGroupEnum.GROUP_8K_FILING,
				"Filed an 8K Statement on Failure to Make a Required Distribution"),
		TYPE_EIGHT_K_EVENT_ITEM_7_01(
				FR_IEvent.TYPE_EIGHT_K_EVENT_ITEM_7_01,
				EventGroupEnum.GROUP_8K_FILING,
				"Filed an 8K Statement on Regulation FD Disclosure"),
		TYPE_EIGHT_K_EVENT_ITEM_6_05(
				FR_IEvent.TYPE_EIGHT_K_EVENT_ITEM_6_05,
				EventGroupEnum.GROUP_8K_FILING,
				"Filed an 8K Statement on Securities Act Updating Disclosure"),
		TYPE_EIGHT_K_EVENT_ITEM_3_02(
				FR_IEvent.TYPE_EIGHT_K_EVENT_ITEM_3_02,
				EventGroupEnum.GROUP_8K_FILING,
				"Filed an 8K Statement on Unregistered Sales of Equity Securities"),
		TYPE_EIGHT_K_EVENT_ITEM_4_02(
				FR_IEvent.TYPE_EIGHT_K_EVENT_ITEM_4_02,
				EventGroupEnum.GROUP_8K_FILING,
				"Filed an 8K Statement on Non-Reliance on Previously Issued Financial Statements or a Related Audit Report or Completed Interim Review"),
		TYPE_EIGHT_K_EVENT_ITEM_3_03(
				FR_IEvent.TYPE_EIGHT_K_EVENT_ITEM_3_03,
				EventGroupEnum.GROUP_8K_FILING,
				"Filed an 8K Statement on Material Modification to Rights of Security Holders"),
		TYPE_EIGHT_K_EVENT_ITEM_4_01(
				FR_IEvent.TYPE_EIGHT_K_EVENT_ITEM_4_01,
				EventGroupEnum.GROUP_8K_FILING,
				"Filed an 8K Statement on Changes in Registrant's Certifying Accountant"),
		TYPE_EIGHT_K_EVENT_ITEM_5_01(
				FR_IEvent.TYPE_EIGHT_K_EVENT_ITEM_5_01,
				EventGroupEnum.GROUP_8K_FILING,
				"Filed an 8K Statement on Changes in Control of Registrant"),
		TYPE_EIGHT_K_EVENT_ITEM_5_03(
				FR_IEvent.TYPE_EIGHT_K_EVENT_ITEM_5_03,
				EventGroupEnum.GROUP_8K_FILING,
				"Filed an 8K Statement on Amendments to Articles of Incorporation or Bylaws; Change in Fiscal Year"),
		TYPE_EIGHT_K_EVENT_ITEM_5_05(
				FR_IEvent.TYPE_EIGHT_K_EVENT_ITEM_5_05,
				EventGroupEnum.GROUP_8K_FILING,
				"Filed an 8K Statement on Amendment to Registrant's Code of Ethics, or Waiver of a Provision of the Code of Ethics"),
		TYPE_EIGHT_K_EVENT_ITEM_5_06(
				FR_IEvent.TYPE_EIGHT_K_EVENT_ITEM_5_06,
				EventGroupEnum.GROUP_8K_FILING,
				"Filed an 8K Statement on Change in Shell Company Status"),
		TYPE_EIGHT_K_EVENT_ITEM_6_01(
				FR_IEvent.TYPE_EIGHT_K_EVENT_ITEM_6_01,
				EventGroupEnum.GROUP_8K_FILING,
				"Filed an 8K Statement on ABS Informational and Computational Materials"),
		TYPE_EIGHT_K_EVENT_ITEM_6_02(
				FR_IEvent.TYPE_EIGHT_K_EVENT_ITEM_6_02,
				EventGroupEnum.GROUP_8K_FILING,
				"Filed an 8K Statement on Change of Servicer or Trustee"),
		TYPE_EIGHT_K_EVENT_ITEM_6_03(
				FR_IEvent.TYPE_EIGHT_K_EVENT_ITEM_6_03,
				EventGroupEnum.GROUP_8K_FILING,
				"Filed an 8K Statement on Change in Credit Enhancement or Other External Support"),
		TYPE_EIGHT_K_EVENT_ITEM_8_01(
				FR_IEvent.TYPE_EIGHT_K_EVENT_ITEM_8_01,
				EventGroupEnum.GROUP_8K_FILING,
				"Filed an 8K Statement on Other Events"),
		TYPE_EIGHT_K_EVENT_ITEM_5_02(
				FR_IEvent.TYPE_EIGHT_K_EVENT_ITEM_5_02,
				EventGroupEnum.GROUP_8K_FILING,
				"Filed an 8K Statement on Departure of Directors or Certain Officers; Election of Directors; Appointment of Certain Officers"),
		TYPE_TEN_K_EVENT(FR_IEvent.TYPE_TEN_K_FILING, EventGroupEnum.GROUP_8K_FILING, "10-K Filings"),
		TYPE_TEN_Q_EVENT(FR_IEvent.TYPE_TEN_Q_FILING, EventGroupEnum.GROUP_8K_FILING, "10-Q Filings"),

		TYPE_DELAYED_SEC_FILING(FR_IEvent.TYPE_DELAYED_SEC_FILING, EventGroupEnum.GROUP_DELAYED_FILING, "Delayed SEC Filings"),

		// Web Volume Company
		TYPE_TOTAL_WEB_COVERAGE_VOLUME_COMPANY(
				FR_IEvent.TYPE_TOTAL_WEB_COVERAGE_VOLUME_COMPANY,
				EventGroupEnum.GROUP_WEB_VOLUME,
				"Web Volume Company"),


		// Web Volume
		TYPE_TOTAL_WEB_COVERAGE_VOLUME_TOPIC(
				FR_IEvent.TYPE_TOTAL_WEB_COVERAGE_VOLUME_TOPIC,
				EventGroupEnum.GROUP_WEB_VOLUME,
				"Web Volume Topic"),
		// TODO need to add a event type FR_IEvent.TYPE_TRANSACTION - combination of ["T:BuybackTransactions", "T:BankruptcyTransactions",
		// "T:SpinoffsTransactions", "T:ShelfRegistrationTransactions", "T:PublicOfferingTransactions",
		// "T:MergerandAcquisitionTransactions"]
		TYPE_TRANSACTION(-1, EventGroupEnum.GROUP_WEB_VOLUME, "Transaction"),

		// TODO: Below items for future use
		/*
		 * TYPE_BLOG_COVERAGE_VOLUME (FR_IEvent.TYPE_BLOG_COVERAGE_VOLUME, null),
		 * 
		 * // Bankruptcy TYPE_BANKRUPTCY (FR_IEvent.TYPE_BANKRUPTCY, null),
		 * 
		 * // Mgmt. Positive/Negative Comments TYPE_MGMT_POSITIVE_NEGATIVE_COMMENTS (FR_IEvent.TYPE_MGMT_POSITIVE_NEGATIVE_COMMENTS, null),
		 * 
		 * // Public Offering TYPE_PUBLIC_OFFERING (FR_IEvent.TYPE_PUBLIC_OFFERING, null),
		 * 
		 * // Private placement TYPE_PRIVATE_PLACEMENT (FR_IEvent.TYPE_PRIVATE_PLACEMENT, null),
		 * 
		 * // Spin-off / split-off TYPE_SPINOFF_SPLITOFF (FR_IEvent.TYPE_SPINOFF_SPLITOFF, null),
		 * 
		 * // Merger & acquisition: TYPE_MERGER_ACQUISITION (FR_IEvent.TYPE_MERGER_ACQUISITION, null),
		 * 
		 * // Share buyback TYPE_SHARE_BUYBACK (FR_IEvent.TYPE_SHARE_BUYBACK, null),
		 * 
		 * // Shelf registrations TYPE_SHELF_REGISTRATIONS (FR_IEvent.TYPE_SHELF_REGISTRATIONS, null)
		 */ ;

		EventTypeEnum(int id, EventGroupEnum group, String label) {
			this.id = id;
			this.group = group;
			this.label = label;
		}

		public static final Map<Integer, EventTypeEnum> eventTypeMap = new HashMap<Integer, EventTypeEnum>();

		// Initialize EventTypeEnumMap
		static {
			for (EventTypeEnum e : EventTypeEnum.values()) {
				eventTypeMap.put(e.id, e);
			}
		}

		private int id;
		private EventGroupEnum group;
		private String label;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public EventGroupEnum getGroup() {
			return this.group;
		}

		public void setGroup(EventGroupEnum group) {
			this.group = group;
		}

		public boolean isNonExecTurnover() {
			switch (this) {
				case TYPE_MGMT_CHANGE_SENIORVP_TURNOVER:
				case TYPE_MGMT_CHANGE_EXEC_VICE_PRESIDENT_TURNOVER:
				case TYPE_MGMT_CHANGE_VP_TURNOVER:
				case TYPE_MGMT_CHANGE_EXECUTIVE_DIRECTOR:
					return true;
				default:
					return false;
			}
		}

		public boolean isHighRankedInternalMgmtTurnover() {
			switch (this) {
				case TYPE_MGMT_CHANGE_PRESIDENT_TURNOVER:
				case TYPE_MGMT_CHANGE_MANAGING_DIRECTOR:
				case TYPE_MGMT_CHANGE_CEO_TURNOVER:
				case TYPE_MGMT_CHANGE_CHAIRMAN:
				case TYPE_MGMT_CHANGE_CHAIRMAN_OF_BOARD:
				case TYPE_MGMT_CHANGE_EXECUTIVE_CHAIRMAN_OF_BOARD:
				case TYPE_MGMT_CHANGE_CO_EXECUTIVE_OFFICER:
				case TYPE_MGMT_CHANGE_CHIEF_EXECUTIVE:
				case TYPE_MGMT_CHANGE_SENIOR_MANAGING_DIRECTOR:
				case TYPE_MGMT_CHANGE_EXECUTIVE_CHAIRMAN:
					return true;
				default:
					return false;
			}
		}
	}
	public static enum EventEntityTypeEnum {
		TYPE_COMPANY(FR_IEventEntity.ENTITY_TYPE_COMPANY, "Company"), TYPE_TOPIC(FR_IEventEntity.ENTITY_TYPE_CATEGORY, "Topic");

		EventEntityTypeEnum(int id, String label) {
			this.id = id;
			this.label = label;
		}

		public static final Map<Integer, EventEntityTypeEnum> eventEntityTypeMap = new HashMap<Integer, EventEntityTypeEnum>();

		static {
			for (EventEntityTypeEnum e : EventEntityTypeEnum.values()) {
				eventEntityTypeMap.put(e.id, e);
			}
		}

		private int id;
		private String label;

		public int getId() {
			return this.id;
		}

		public String getLabel() {
			return this.label;
		}
	}

	/**
	 * Represents the display type of this event i.e.; normal/plain, bold, bold large.
	 */
	public static enum EventDisplayType {
		PLAIN, BOLD, BOLD_LARGE
	}

	String getCaption();

	Date getDate();

	String getDescription();

	EventGroupEnum getEventGroup();

	boolean isTrigger();

	String getUrl();

	EventTypeEnum getEventType();

	EventEntityTypeEnum getEventEntityType();

	Map<String, Object> getProperties();

	int getFlag();

	double getScore();

	long getEntityId();

	int getEventId();

	Date getReportDate();

	Set<Long> getPrimaryEvidenceEntityIds();

	IEntityInfo getEntityInfo();

	/**
	 * Indicates whether this event title url has been expired.
	 * <p>
	 * If so caller may take appropriate actions by using {@link #getEntityId()} for fetching summary etc from other locations such as
	 * index.
	 */
	boolean hasExpired();

}
