package com.firstrain.frapi.util;


public class DefaultEnums {

	public enum MembershipType {
		DEFAULT, ADMIN, ANONYMOUS
	}

	public enum OwnedByType {
		USER, GROUP
	}

	public enum Status {
		ACTIVE, INACTIVE, DELETED, BOTH
	}

	public enum UserType {
		IDENTIFIED, ABU, EOU // Email Only User.
	}

	public enum UserValidationStatus {
		GROUP_DOMAIN_VALIDATION_FAILURE,
		GROUP_VALIDATION_FAILURE,
		SUB_GROUP_VALIDATION_FAILURE,
		VALIDATION_SUCCESS,
		USER_CREATED_SUCCESSFULLY,
		USER_CREATED_BUT_MAIL_FAILED,
		ACTIVATION_MAIL_FAILURE,
		USER_NOT_CREATED_BUT_VALID,
		PASSWORD_VALIDATION_FAILED;
	}

	public enum Origin {
		INTERNAL_ADMIN,
		EXTERNAL_ADMIN,
		SELF_REGISTERED,
		COMPONENT,
		ABU,
		OTHERS,
		START_HERE,
		START_HERE_CONSUMER,
		START_HERE_KNOWN,
		START_HERE_UNKNOWN,
		START_HERE_DEMO,
		FRAPI,
		THIRD_PARTY,
		PAESTUM
	}

	public enum UserItemType {
		Clipped, Favorite, Emailed, Printed, Shared, Visible
	}

	public enum UserActivityType {
		SYSTEM, // 0
		USER, // 1
		NON_UEL // 2
	}

	public enum RelevanceBand {
		HIGH("High", (short) 3), MEDIUM("Medium", (short) 2), LOW("Low", (short) 1);

		private final String name;
		private final short value;

		RelevanceBand(String name, short value) {
			this.name = name;
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public short getValue() {
			return value;
		}
	}

	public enum MatchedEntityConfidenceScore {
		VERY_HIGH("VeryHigh", (short) 4), HIGH("High", (short) 3), MEDIUM("Medium", (short) 2), LOW("Low", (short) 1);

		private final String name;
		private final short value;

		MatchedEntityConfidenceScore(String name, short value) {
			this.name = name;
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public short getValue() {
			return value;
		}
	}

	public enum TagType {
		FOLDER(1),
		FOLDER_COMPANY(2),
		FOLDER_MARKET(3),
		FOLDER_PORTFOLIO(4),
		FOLDER_CUSTOMERS(5),
		FOLDER_COMPTITOR(6),
		FOLDER_CUSTOM(7),

		TARGET_ACCOUNT_VIEW(8),
		TARGET_VENDOR_VIEW(9),
		CRITICAL_EVENTS_VIEW(10),
		TARGET_ACCOUNT_MGMT_QUOTES(11),
		TARGET_ACCOUNT_EARNING(12),
		SALES_OPPORTUNITY(13),
		SALES_OPPORTUNITY_REGION(14),
		SALES_OPPORTUNITY_COMPANIES_VERTICAL(15),
		MARKET_INSIGHT_VIEW(16),
		INDUSTRY_TOPICAL_VIEW(17),
		CONSOLIDATED_VIEW(18),
		TRENDS_VIEW(19),
		SINGLE_ACCOUNT_CUSTOMER(20),
		SEARCHED_COMPANY_COMPETITOR(21),
		COMPETITION_VERTICAL(22),
		COMPETITION_REGION(23),
		ROLE_BASED_MONITOR(24),
		VENDOR_COMPETITOR_INTERSECTION(25),
		COMPETITORS_VIEW(26),
		CUSTOMER_COMPETITOR_INTERSECTION(27),
		ACCOUNT_VIEW(28),
		TRENDS_REGION_VIEW(29);

		private final int order;

		TagType(int order) {
			this.order = order;
		}

		public int getOrder() {
			return order;
		}
	}

	public enum DateBucketingMode {
		AUTO, SMART, DATE
	}

	public enum INPUT_ENTITY_TYPE {
		COMPANY, INDUSTRY, TOPIC, REGION, SEARCH;
	}

	public enum EventInformationEnum {
		MT_HIRE, MT_DEPARTURE, MT_MOVE, PRICE_UP, PRICE_DOWN, WEB_VOLUME, SEC, DEFAULT
	}

	public enum EventTypeEnum {
		// 8K Filing
		TYPE_EIGHT_K_EVENT_ITEM_1_03("Filed an 8K Statement on Bankruptcy or Receivership"),
		TYPE_EIGHT_K_EVENT_ITEM_3_01(
				"Filed an 8K Statement on Notice of Delisting or Failure to Satisfy a Continued Listing Rule or Standard; Transfer of Listing"),
		TYPE_EIGHT_K_EVENT_ITEM_1_02("Filed an 8K Statement on Termination of a Material Definitive Agreement"),
		TYPE_EIGHT_K_EVENT_ITEM_2_01("Filed an 8K Statement on Completion of Acquisition or Disposition of Assets"),
		TYPE_EIGHT_K_EVENT_ITEM_1_01("Filed an 8K Statement on Entry into a Material Definitive Agreement"),
		TYPE_EIGHT_K_EVENT_ITEM_2_02("Filed an 8K Statement on Results of Operations and Financial Condition"),
		TYPE_EIGHT_K_EVENT_ITEM_2_04(
				"Filed an 8K Statement on Triggering Events That Accelerate or Increase a Direct Financial Obligation or an Obligation under an Off-Balance Sheet Arrangement"),
		TYPE_EIGHT_K_EVENT_ITEM_2_03(
				"Filed an 8K Statement on Creation of a Direct Financial Obligation or an Obligation under an Off-Balance Sheet Arrangement of a Registrant"),
		TYPE_EIGHT_K_EVENT_ITEM_2_06("Filed an 8K Statement on Material Impairments"),
		TYPE_EIGHT_K_EVENT_ITEM_2_05("Filed an 8K Statement on Costs Associated with Exit or Disposal Activities"),
		TYPE_EIGHT_K_EVENT_ITEM_5_04("Filed an 8K Statement on Temporary Suspension of Trading Under Registrant's Employee Benefit Plans"),
		TYPE_EIGHT_K_EVENT_ITEM_6_04("Filed an 8K Statement on Failure to Make a Required Distribution"),
		TYPE_EIGHT_K_EVENT_ITEM_7_01("Filed an 8K Statement on Regulation FD Disclosure"),
		TYPE_EIGHT_K_EVENT_ITEM_6_05("Filed an 8K Statement on Securities Act Updating Disclosure"),
		TYPE_EIGHT_K_EVENT_ITEM_3_02("Filed an 8K Statement on Unregistered Sales of Equity Securities"),
		TYPE_EIGHT_K_EVENT_ITEM_4_02(
				"Filed an 8K Statement on Non-Reliance on Previously Issued Financial Statements or a Related Audit Report or Completed Interim Review"),
		TYPE_EIGHT_K_EVENT_ITEM_3_03("Filed an 8K Statement on Material Modification to Rights of Security Holders"),
		TYPE_EIGHT_K_EVENT_ITEM_4_01("Filed an 8K Statement on Changes in Registrant's Certifying Accountant"),
		TYPE_EIGHT_K_EVENT_ITEM_5_01("Filed an 8K Statement on Changes in Control of Registrant"),
		TYPE_EIGHT_K_EVENT_ITEM_5_03("Filed an 8K Statement on Amendments to Articles of Incorporation or Bylaws; Change in Fiscal Year"),
		TYPE_EIGHT_K_EVENT_ITEM_5_05(
				"Filed an 8K Statement on Amendment to Registrant's Code of Ethics, or Waiver of a Provision of the Code of Ethics"),
		TYPE_EIGHT_K_EVENT_ITEM_5_06("Filed an 8K Statement on Change in Shell Company Status"),
		TYPE_EIGHT_K_EVENT_ITEM_6_01("Filed an 8K Statement on ABS Informational and Computational Materials"),
		TYPE_EIGHT_K_EVENT_ITEM_6_02("Filed an 8K Statement on Change of Servicer or Trustee"),
		TYPE_EIGHT_K_EVENT_ITEM_6_03("Filed an 8K Statement on Change in Credit Enhancement or Other External Support"),
		TYPE_EIGHT_K_EVENT_ITEM_8_01("Filed an 8K Statement on Other Events"),
		TYPE_EIGHT_K_EVENT_ITEM_5_02(
				"Filed an 8K Statement on Departure of Directors or Certain Officers; Election of Directors; Appointment of Certain Officers"),
		TYPE_TEN_K_EVENT("10-K Filings"),
		TYPE_TEN_Q_EVENT("10-Q Filings"),
		TYPE_DELAYED_SEC_FILING("Delayed SEC Filings");

		EventTypeEnum(String label) {
			this.label = label;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		private String label;

	}

	public enum TitleType {
		CSSINLINE, CSSCLASS, PLAIN, HTML
	}

	public enum MgmtServiceGroup {
		CHANGE_TYPE, CHANGE_LEVEL
	}

	public enum CoversationStarterType {
		PEER_COMMENTARY("Peer Commentary"),
		LEAD_COMMENTARY("Lead Commentary"),
		CXO_COMMENTARY("CXO Commentary"),
		COMPANY_NEWS("Company News"),
		BUSINESS_EVENTS("Business Events"),
		INDUSTRY_NEWS("Industry News");

		CoversationStarterType(String label) {
			this.label = label;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		private String label;
	}
}
