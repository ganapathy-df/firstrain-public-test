package com.firstrain.frapi.pojo.wrapper;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.firstrain.utils.object.PerfRequestEntry;


public class BaseSet {

	// private static final Logger LOG = Logger.getLogger(BaseSet.class);

	public static enum SectionType {

		/**
		 * Analyst Comments
		 */
		AC("Analyst Comments"),

		/**
		 * Events
		 */
		E("Events"),

		/**
		 * Company Info
		 */
		CI("Company Info"),

		/**
		 * Competitors
		 */
		C("Competitors"),

		/**
		 * Filings
		 */
		F("Filings"),

		/**
		 * Industry Topic Events
		 */
		ITE("Industry Topic Events"),

		/**
		 * Tweets
		 */
		FT("Tweets"),

		/**
		 * Transcripts
		 */
		T("Transcripts"),

		/**
		 * Turnover Events
		 */
		TE("Turnover Events"),

		/**
		 * Trending Entities
		 */
		TRE("Trending Entities"),

		/**
		 * Company Members
		 */
		CM("Company Members"),

		/**
		 * Management Turnover Chart
		 */
		MTC("Management Turnover Chart"),

		/**
		 * Web Volume Graph
		 */
		WV("Web Volume Graph"),

		/**
		 * Search List
		 */
		SL("Search List"),

		/**
		 * Tweet Accelerometer
		 */
		TA("Tweet Accelerometer"),

		/**
		 * Market Map
		 */
		MMP("Market Map"),

		/**
		 * Corporate Governance
		 */
		CG("Corporate Governance"),

		/**
		 * Virtual Monitor Web Results
		 */
		VMWR("Virtual Monitor Web Results"),

		/**
		 * Industry Health Indicator
		 */
		IHI("Industry Health Indicator"),

		/**
		 * Industry Transactions
		 */
		IT("Industry Transactions"),

		/**
		 * Virtual Monitor Events
		 */
		VME("Virtual Monitor Events"),

		/**
		 * Topic Events
		 */
		TOE("Topic Events"),

		/**
		 * Web Results
		 */
		FR("Web Results"),
		
		/**
		 * highlights  Results
		 */
		HR("highlights Results"),

		/**
		 * Stock and Financial Events
		 */
		SFE("Stock and Financial Events"),

		/**
		 * Quotes Documents
		 */
		QD("Quotes Documents"),

		/**
		 * Person Documents
		 */
		PD("Person Documents"),

		/**
		 * Monitor Item List
		 */
		IL("Monitor Item List"),

		/**
		 * Web Results Non English
		 */
		WRNE("Web Results Non English"),

		/**
		 * Visualisation Chart
		 */
		VIZ("VIZUALIZATION CHART"),

		/**
		 * Corporate Transactions
		 */
		CT("Corporate Transactions"),

		/**
		 * Business Influencer
		 */
		BI("BUSINESS INFLUENCERS"),

		/**
		 * Activity Trends
		 */
		TT("MONITOR ACTIVITY TRENDS"),

		/**
		 * Twitter Trends
		 */
		TWT("Business Twitter Trends"),

		/**
		 * Market Drivers
		 */
		MD("MARKET DRIVERS"),

		/**
		 * Global Lens
		 */
		GL("REGIONAL LENS: GLOBAL"),
		/**
		 * Regional Lens
		 */
		RL("REGIONAL LENS: U.S.");

		private final String name;

		private SectionType(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	};

	private SectionType sectionType;
	private Integer totalCount;
	private Boolean hasMore;

	// private AjaxResponse.ResStatus status = AjaxResponse.ResStatus.SUCCESS;

	protected transient PerfRequestEntry stat;

	public BaseSet() {}

	public BaseSet(SectionType type) {
		this.sectionType = type;
	}

	@JsonIgnore
	public SectionType getSectionType() {
		return sectionType;
	}

	public void setSectionType(SectionType type) {
		this.sectionType = type;
	}

	public Integer getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

	@JsonIgnore
	public PerfRequestEntry getStat() {
		return stat;
	}

	public void setStat(PerfRequestEntry stat) {
		this.stat = stat;
	}

	// public AjaxResponse.ResStatus getStatus() {
	// return status;
	// }
	//
	// public void setStatus(AjaxResponse.ResStatus status) {
	// this.status = status;
	// }

	// public static Map<SectionType, Parameter> getJsonMap(InputJson inputObj){
	// Map<SectionType, Parameter> map = new HashMap<SectionType, Parameter>();
	// try {
	// List<Section> jsonObjList = inputObj.getSections();
	//
	// for(int i=0;i<jsonObjList.size();i++) {
	// Section obj = jsonObjList.get(i);
	// map.put(obj.getName(), obj.getParameter());
	// }
	// } catch (Exception e) {
	// LOG.error("Exception Generating JsonMap", e);
	// }
	// return map;
	// }

	public static String[] getFillingArray(String csvString) {
		String[] arrayString = csvString.split(",");
		return arrayString;
	}

	public Boolean isHasMore() {
		return hasMore;
	}

	public void setHasMore(Boolean hasMore) {
		this.hasMore = hasMore;
	}
}
