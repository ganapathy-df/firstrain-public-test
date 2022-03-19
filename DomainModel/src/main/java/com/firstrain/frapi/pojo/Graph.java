package com.firstrain.frapi.pojo;

import java.io.Serializable;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonIgnore;

import com.firstrain.frapi.domain.HistoricalStat;
import com.firstrain.frapi.pojo.wrapper.BaseSet;
import com.firstrain.frapi.util.DefaultEnums.EventInformationEnum;
import com.firstrain.utils.FR_Encoder;
import com.firstrain.utils.JSONUtility;

public class Graph extends BaseSet implements Serializable {

	private static final long serialVersionUID = 1788481808440286931L;
	private static final Logger LOG = Logger.getLogger(Graph.class);
	private static final String NUMBER_PATTERN = "^[-+]?[0]*\\.?[0]+$";
	private static final int MAX_ALLOWED_UNAVAILABILITY_DAYS = 7;

	public static enum GraphFor {
		CALL_PREP, SEARCH_PAGE, EVENTS_PAGE, INDUSTRY_PAGE
	}

	public static enum Range {
		BROAD("broad"), MEDIUM("medium"), NARROW("narrow");

		private final String attrname;

		Range(String name) {
			this.attrname = name;
		}

		public String attrname() {
			return attrname;
		}
	}

	private Range currentRange = Range.MEDIUM;
	private Date sDate;
	private Date eDate;
	private ArrayList<HistoricalStat> historicalStat;
	private GraphFor graphFor;
	private String imgBaseURL = null;
	private String dataSetColor = "a6cae1";
	private int plotspacepercent = 25;
	private String canvasbgcolor = "fffffff";
	private int charttopmargin = 10;
	private int chartbottommargin = 10;
	private String pyAxisName = "Web Volume";
	private String syAxisName = "Price";
	private int canvasborderthickness = 2;
	private int chartrightmargin = 15;
	private int chartleftmargin = 1;
	private int basefontsize = 9;
	private Entity entity;

	public Graph() {}

	public Graph(SectionType type) {
		this(null, null, type);
	}

	private static class CompanyStat {
		private Date dt;
		private int dtDiff;
		private String volumeALL = "0";
		private String closingPrice = "0";
	}

	private static class CompanyEvent {
		private Date dt;
		private int dtDiff;
		private String eventType;
		private EventInformationEnum eventGroup;
		private String headline = "";
		private String supportLink = "";
		private boolean expired;
		private String summary = "";
		private String associatedWith = "";
		private String moreLink = "";
		private List<Entity> companies;
		private List<Entity> topics;
	}

	private static class GraphJson {
		public String title;
		public String defaultURL;
	}

	protected String getEventLink(CompanyEvent event, boolean detachJSForExpiredEvent) {
		String link = null;
		try {
			link = URLEncoder.encode(event.supportLink, "UTF-8");
		} catch (Exception e) {
			LOG.error("Unsupported encoding?" + e.getMessage(), e);
		}

		if (event.eventGroup == EventInformationEnum.WEB_VOLUME && !detachJSForExpiredEvent) {
			try {
				if (event.expired) {
					String companiesString = convertToJsonString(event.companies);
					String topicsString = convertToJsonString(event.topics);
					link = "javascript:displayEventDetails('" + StringEscapeUtils.escapeJavaScript(event.supportLink) + "', '"
							+ StringEscapeUtils.escapeJavaScript(event.headline) + "', '"
							+ StringEscapeUtils.escapeJavaScript(event.summary) + "'"
							+ (companiesString != null ? ", " + companiesString : "") + (topicsString != null ? ", " + topicsString : "")
							+ ");";
				}
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		}

		return FR_Encoder.encodeXML(link, "UTF-8");
	}

	@JsonIgnore
	public String getGraphXml() {
		boolean detachJSForExpiredEvent = false;
		StringBuffer resXML = new StringBuffer();
		try {
			Map<String, CompanyStat> cmpStats = new TreeMap<String, CompanyStat>();
			ArrayList<CompanyEvent> cmpEvents = new ArrayList<CompanyEvent>();
			SimpleDateFormat sdf01 = new SimpleDateFormat("yyyyMMdd");
			boolean hasStockPrice = hasTradeRange();
			String lastClosingPriceStr = "0";

			int stockPriceUnavailablityInDays = 0;
			boolean considerStockPriceUnavialability = false;

			List<CompanyStat> tempStorage = new ArrayList<CompanyStat>(MAX_ALLOWED_UNAVAILABILITY_DAYS);
			for (HistoricalStat stats : historicalStat) {
				CompanyStat cs = new CompanyStat();
				cs.dt = stats.getCompanyVolume().getDate();
				cs.dtDiff = stats.getCompanyVolume().getDiffId();
				cs.volumeALL = stats.getCompanyVolume().getTotal() + "";
				/*
				 * Nullify closing price data if unavailable for 7 consecutive days, derived by MAX_ALLOWED_UNAVAILABILITY_DAYS. Closing
				 * price tag in chart xml should not be generated further when null closing price is encountered.
				 */
				if (stockPriceUnavailablityInDays < MAX_ALLOWED_UNAVAILABILITY_DAYS) {
					if (stats.getTradeRange() != null) {
						lastClosingPriceStr = cs.closingPrice = stats.getTradeRange().getClosingPriceStr();
						considerStockPriceUnavialability = true;
						stockPriceUnavailablityInDays = 0;
						if (!tempStorage.isEmpty()) {
							tempStorage.clear();
						}
					} else {
						if (considerStockPriceUnavialability) {
							tempStorage.add(cs);
							stockPriceUnavailablityInDays++;
						}
						cs.closingPrice = lastClosingPriceStr;
					}
				} else {
					if (lastClosingPriceStr != null) {
						// Null last MAX_ALLOWED_UNAVAILABILITY_DAYS days data for further usage.
						LOG.info("Clearing closingPrice data for " + MAX_ALLOWED_UNAVAILABILITY_DAYS + " days and afterwards starting from "
								+ tempStorage.get(0).dt);
						for (CompanyStat cstat : tempStorage) {
							cstat.closingPrice = null;
						}
						tempStorage.clear();
						lastClosingPriceStr = null;
					}
					cs.closingPrice = null;
				}

				String key = sdf01.format(cs.dt);
				cmpStats.put(key, cs);

				if (stats.getSignals() != null) {
					List<Event> events = stats.getSignals();
					if (events != null && !events.isEmpty()) {
						for (Event event : events) {
							CompanyEvent ce = getCompanyEvent(cs, event);
							cmpEvents.add(ce);
						}
					}
				}
			}

			// Set Calendar to Current Date
			Calendar c = Calendar.getInstance();
			Date endDate = this.getHistoricalStat().get(this.getHistoricalStat().size() - 1).getCompanyVolume().getDate();

			Date startDate = this.getHistoricalStat().get(0).getCompanyVolume().getDate();
			ArrayList<CompanyStat> cStats = new ArrayList<CompanyStat>();

			int i = 0;
			for (Iterator<Map.Entry<String, CompanyStat>> iter = cmpStats.entrySet().iterator(); iter.hasNext(); i++) {
				Map.Entry<String, CompanyStat> e = iter.next();

				final CompanyStat value = e.getValue();
				CompanyStat cs = value;
				if (cs == null) {
					cs = new CompanyStat();
					c.setTime(startDate);
					c.add(Calendar.DATE, i);
					cs.dt = c.getTime();
				}
				cStats.add(cs);
			}

			Date stDate = this.sDate == null ? startDate : this.sDate;
			// FIXME: need to sync date range throughout system
			if (graphFor != GraphFor.EVENTS_PAGE) {
				Calendar c1 = Calendar.getInstance();
				c1.setTime(stDate);
				c1.add(Calendar.DATE, 1);
				stDate = c1.getTime();
			}
			String TagStartDate = sdf01.format(stDate);
			String TagEndDate = this.eDate == null ? sdf01.format(endDate) : sdf01.format(this.eDate);

			resXML.append(
					"<chart showFirstrainLegend=\"0\" showSliders=\"1\" exportShowMenuItem=\"0\" exportEnabled=\"1\" exportCallback=\"PDF.onExportComplete\" showExportDialog=\"0\" exportAtClient=\"0\" exportAction=\"save\" exportHandler=\"/ajax_FCExporter.jsp?p=true\" captionpadding='3' caption='Select Timeframe and Results Quality' subcaption='' xaxisname='' PYaxisname='"
							+ getPyAxisName() + "' SYAxisName='" + getSyAxisName()
							+ "' sNumberPrefix='$' numberSuffix='' sNumberSuffix='' setAdaptiveSYMin='1' showPlotBorder='0' useRoundEdges='0' plotFillRatio='98' decimals='10' showlegend='0' basefontsize='"
							+ getBasefontsize() + "' plotspacepercent='" + getPlotspacepercent() + "' chartrightmargin='"
							+ getChartrightmargin() + "' chartleftmargin='" + getChartleftmargin() + "' charttopmargin='"
							+ getCharttopmargin() + "' chartbottommargin='" + getChartbottommargin()
							+ "' hovercapbordercolor='5d5e5d' hovercapbgcolor='fcfbe7' showhovercap='1' showborder='0' showplotborder='0' legendpadding='0' canvasborderthickness='"
							+ getCanvasborderthickness() + "' canvasbgalpha='100' canvasbgangle='270' canvasbgratio='95' canvasbgcolor='"
							+ getCanvasbgcolor()
							+ "' setadaptiveymin='1' adjustdiv='0' animation='0' canvasbordercolor='dddddd' bgswfalpha='100' bgalpha='0' bgcolor='ffffff' showtooltip='1' showlimits='0' showyaxisvalues='0' showvalues='0' showlabels='0' rollcolor='' showsecondarylimits='1' numdivlines='0' yaxisnamepadding='0' secondaryyaxisnamepadding='-15' showtooltipshadow='1' tooltipsepchar=': '");
			if (graphFor == GraphFor.CALL_PREP || graphFor == GraphFor.INDUSTRY_PAGE) {
				resXML.append(" readonly='1' ");
			}
			resXML.append(getRangeAttribute());
			resXML.append(">");

			resXML.append("\t<startdate>" + TagStartDate + "</startdate>");
			resXML.append("\t<enddate>" + TagEndDate + "</enddate>");

			// Category Tag
			SimpleDateFormat sdf02 = new SimpleDateFormat("MMMM dd, yyyy");
			resXML.append("\t<categories font='Arial' fontSize='8' fontColor='000000'>");
			for (CompanyStat cs : cStats) {
				resXML.append("\t\t<category label='" + sdf02.format(cs.dt) + "' />");
			}
			resXML.append("\t</categories>");

			// Data Set Tag
			resXML.append("\t<dataset>");

			resXML.append("\t\t<dataSet seriesName='all' color='" + getDataSetColor() + "' showValues='0'>");
			for (CompanyStat cs : cStats) {
				resXML.append("\t\t\t<set value='" + cs.volumeALL + "' />");
			}
			resXML.append("\t\t</dataSet>");
			resXML.append("\t</dataset>");

			// Stock Price Tag
			if (hasStockPrice) {
				boolean begin = true;
				resXML.append("\t<lineSet seriesname='Price' showValues='0' lineThickness='2' anchorRadius='1' color='bd250e' >");
				for (CompanyStat cs : cStats) {
					if (begin && cs.closingPrice.matches(NUMBER_PATTERN)) {
						resXML.append("\t\t<set />");
						continue;
					}
					begin = false;
					if (cs.closingPrice == null) {
						// Nothing left to do further, stop now.
						break;
					}
					resXML.append("\t\t<set value='" + cs.closingPrice + "' />");
				}
				resXML.append("\t</lineSet>");
			}

			// Events
			resXML.append("\t<eventSet seriesname=\"Management Change\" image=\"" + buildImgUrl("event1.png") + "\" >");

			for (CompanyEvent ce : cmpEvents) {
				if (ce.eventGroup == EventInformationEnum.MT_HIRE || ce.eventGroup == EventInformationEnum.MT_DEPARTURE
						|| ce.eventGroup == EventInformationEnum.MT_MOVE) {
					resXML.append("\t\t<set date=\"" + sdf01.format(ce.dt) + "\" headline=\"" + FR_Encoder.encodeXML(ce.headline, "UTF-8")
							+ "\" supplink=\"" + getEventLink(ce, detachJSForExpiredEvent) + "\" morelink=\"" + ce.moreLink + "\" />");
				}
			}
			resXML.append("\t</eventSet>");

			resXML.append("\t<eventSet seriesname=\"Web Event\" image=\"" + buildImgUrl("event2.png") + "\" >");
			for (CompanyEvent ce : cmpEvents) {
				appendDate(ce, resXML, sdf01, detachJSForExpiredEvent, EventInformationEnum.WEB_VOLUME);
			}
			resXML.append("\t</eventSet>");

			resXML.append("\t<eventSet seriesname=\"SEC Filing\" image=\"" + buildImgUrl("event3.png") + "\" >");
			for (CompanyEvent ce : cmpEvents) {
				appendDate(ce, resXML, sdf01, detachJSForExpiredEvent, EventInformationEnum.SEC);
			}
			resXML.append("\t</eventSet>");

			if (hasStockPrice) {
				resXML.append("\t<eventSet seriesname=\"Stock Price\" image=\"" + buildImgUrl("event4.png") + "\" isfor=\"stock\">");
			} else {
				resXML.append("\t<eventSet seriesname=\"Stock Price\" image=\"" + buildImgUrl("event4.png") + "\">");
			}
			for (CompanyEvent ce : cmpEvents) {
				if (ce.eventGroup == EventInformationEnum.PRICE_UP || ce.eventGroup == EventInformationEnum.PRICE_DOWN) {
					resXML.append("\t\t<set date=\"" + sdf01.format(ce.dt) + "\" headline=\"" + FR_Encoder.encodeXML(ce.headline, "UTF-8")
							+ "\" supplink=\"" + getEventLink(ce, detachJSForExpiredEvent) + "\" morelink=\"" + ce.moreLink + "\" />");
				}
			}
			resXML.append("\t</eventSet>");
			resXML.append("</chart>");
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return null; // In case of error return null;
		}
		return resXML.toString();
	}

	private void appendDate(final CompanyEvent ce, final StringBuffer resXML, final SimpleDateFormat sdf01, final boolean detachJSForExpiredEvent, final EventInformationEnum eventInfo) {
		if (ce.eventGroup == eventInfo) {
			resXML.append("\t\t<set date=\"" + sdf01.format(ce.dt) + "\" headline=\"" + FR_Encoder.encodeXML(ce.headline, "UTF-8")
					+ "\" supplink=\"" + getEventLink(ce, detachJSForExpiredEvent) + "\" morelink=\"" + ce.moreLink + "\" />");
		}
	}

	private String buildImgUrl(String imgName) {
		if (imgBaseURL != null) {
			return imgBaseURL + "/" + imgName;
		}
		return "/images/" + imgName;
	}

	private boolean hasTradeRange() {
		boolean hasTradeRange = false;
		List<HistoricalStat> stats = historicalStat;
		if (stats.size() > 2) {
			for (HistoricalStat stat : stats) {
				if (stat.getTradeRange() != null) {
					hasTradeRange = true;
					break;
				}
			}
		}
		return hasTradeRange;
	}

	private static CompanyEvent getCompanyEvent(CompanyStat cs, Event event) {
		CompanyEvent ce = new CompanyEvent();
		ce.dt = cs.dt;
		if (event.isHasExpired()) {
			ce.companies = event.getMatchedCompanies();
			ce.topics = event.getMatchedTopics();
			ce.expired = true;
			String desc = event.getDescription();
			if (desc != null) {
				ce.summary = desc;
			}
			ce.associatedWith = event.getEntityInfo().getName();
		}
		ce.dtDiff = cs.dtDiff;
		ce.headline = event.getTitle();

		if (event.getUrl() != null) {
			ce.supportLink = event.getUrl();
		}
		ce.eventType = event.getEventType();
		ce.eventGroup = event.getEventInformationEnum();
		return ce;
	}

	private String getRangeAttribute() {
		return " resultsrange=\"" + currentRange.attrname() + "\" ";
	}

	public Graph(Date sDate, Date eDate, SectionType type) {
		super(type);
		this.sDate = sDate;
		this.eDate = eDate;
	}

	public void setSDate(Date date) {
		sDate = date;
	}

	public void setEDate(Date date) {
		eDate = date;
	}

	public Date getsDate() {
		return this.sDate;
	}

	public Date geteDate() {
		return this.eDate;
	}

	public ArrayList<HistoricalStat> getHistoricalStat() {
		return historicalStat;
	}

	public void setHistoricalStat(ArrayList<HistoricalStat> historicalStat) {
		this.historicalStat = historicalStat;
	}

	public void setsDate(Date sDate) {
		this.sDate = sDate;
	}

	public void seteDate(Date eDate) {
		this.eDate = eDate;
	}

	public Range getCurrentRange() {
		return currentRange;
	}

	public void setCurrentRange(Range currentRange) {
		this.currentRange = currentRange;
	}

	public GraphFor getGraphFor() {
		return graphFor;
	}

	public void setGraphFor(GraphFor graphFor) {
		this.graphFor = graphFor;
	}

	public void setImgBaseURL(String imgBaseURL) {
		this.imgBaseURL = StringEscapeUtils.escapeXml(imgBaseURL);
	}

	public String getDataSetColor() {
		return dataSetColor;
	}

	public void setDataSetColor(String dataSetColor) {
		this.dataSetColor = dataSetColor;
	}

	public int getPlotspacepercent() {
		return plotspacepercent;
	}

	public void setPlotspacepercent(int plotspacepercent) {
		this.plotspacepercent = plotspacepercent;
	}

	public String getCanvasbgcolor() {
		return canvasbgcolor;
	}

	public void setCanvasbgcolor(String canvasbgcolor) {
		this.canvasbgcolor = canvasbgcolor;
	}

	public int getCharttopmargin() {
		return charttopmargin;
	}

	public void setCharttopmargin(int charttopmargin) {
		this.charttopmargin = charttopmargin;
	}

	public int getChartbottommargin() {
		return chartbottommargin;
	}

	public void setChartbottommargin(int chartbottommargin) {
		this.chartbottommargin = chartbottommargin;
	}

	public String getPyAxisName() {
		return pyAxisName;
	}

	public void setPyAxisName(String pYaxisname) {
		pyAxisName = pYaxisname;
	}

	public String getSyAxisName() {
		return syAxisName;
	}

	public void setSyAxisName(String sYAxisName) {
		syAxisName = sYAxisName;
	}

	public int getCanvasborderthickness() {
		return canvasborderthickness;
	}

	public void setCanvasborderthickness(int canvasborderthickness) {
		this.canvasborderthickness = canvasborderthickness;
	}

	public int getChartrightmargin() {
		return chartrightmargin;
	}

	public void setChartrightmargin(int chartrightmargin) {
		this.chartrightmargin = chartrightmargin;
	}

	public int getChartleftmargin() {
		return chartleftmargin;
	}

	public void setChartleftmargin(int chartleftmargin) {
		this.chartleftmargin = chartleftmargin;
	}

	public int getBasefontsize() {
		return basefontsize;
	}

	public void setBasefontsize(int basefontsize) {
		this.basefontsize = basefontsize;
	}

	public static String convertToJsonString(List<Entity> entity) {
		String jsonString = null;
		try {
			if (entity != null && !entity.isEmpty()) {
				List<GraphJson> graphJsonList = new ArrayList<GraphJson>();
				GraphJson gj = null;
				for (Entity et : entity) {
					gj = new GraphJson();
					gj.title = et.getName();
					gj.defaultURL = et.getSearchToken();
					graphJsonList.add(gj);
				}
				jsonString = JSONUtility.serialize(graphJsonList);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return jsonString;
	}

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}
}
