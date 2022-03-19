package com.firstrain.frapi.events;

import java.io.StringReader;
import java.util.HashMap;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.log4j.Logger;

import com.firstrain.mip.object.FR_IMgmtChangeTrend;
import com.firstrain.mip.object.FR_IStockPrice;
import com.firstrain.mip.object.impl.FR_MgmtChangeTrend;
import com.firstrain.mip.object.impl.FR_StockPrice;

/**
 * @author anagpal Jun 4, 2009 4:05:00 PM
 */
public class EventConfigUtil {

	private static final Logger LOG = Logger.getLogger(EventConfigUtil.class);

	public static class Filings8KConfObj {
		private String title;
		private String titleUrl;
		private long cikCode;

		public String getTitle() {
			return title;
		}

		public String getTitleUrl() {
			return titleUrl;
		}

		void setTitle(String title) {
			this.title = title;
		}

		void setTitleUrl(String titleUrl) {
			this.titleUrl = titleUrl;
		}

		public long getCikCode() {
			return cikCode;
		}

		void setCikCode(long cikCode) {
			this.cikCode = cikCode;
		}
	}

	// TODO:Possible to abstract a super object for common properties here.
	public static class DelayedConfObj {
		private String title;
		private String titleUrl;
		private long cikCode;

		public String getTitle() {
			return title;
		}

		public String getTitleUrl() {
			return titleUrl;
		}

		void setTitle(String title) {
			this.title = title;
		}

		void setTitleUrl(String titleUrl) {
			this.titleUrl = titleUrl;
		}

		public long getCikCode() {
			return cikCode;
		}

		void setCikCode(long cikCode) {
			this.cikCode = cikCode;
		}
	}

	public static class WebVolumeConfObj {
		private int numDocsEvaluated;
		private int numDocsTagged;
		private String title;
		private String titleUrl;
		private String titleSearchUrl;

		public int getNumDocsEvaluated() {
			return numDocsEvaluated;
		}

		public int getNumDocsTagged() {
			return numDocsTagged;
		}

		public String getTitle() {
			return title;
		}

		public String getTitleUrl() {
			return titleUrl;
		}

		public String getTitleSearchUrl() {
			return titleSearchUrl;
		}

		void setNumDocsEvaluated(int numDocsEvaluated) {
			this.numDocsEvaluated = numDocsEvaluated;
		}

		void setNumDocsTagged(int numDocsTagged) {
			this.numDocsTagged = numDocsTagged;
		}

		void setTitle(String title) {
			this.title = title;
		}

		void setTitleUrl(String titleUrl) {
			this.titleUrl = titleUrl;
		}

		void setTitleSearchUrl(String titleSearchUrl) {
			this.titleSearchUrl = titleSearchUrl;
		}
	}

	private static XMLInputFactory factory = XMLInputFactory.newInstance();
	{
		factory.setProperty(XMLInputFactory.IS_COALESCING, true);
	}

	private static HashMap<String, String> getStringProperties(String config) throws XMLStreamException {
		XMLEventReader xmlreader = factory.createXMLEventReader(new StringReader(config));
		HashMap<String, String> p = new HashMap<String, String>();
		String name = null;
		QName q = new QName("n");
		while (xmlreader.hasNext()) {

			XMLEvent e = xmlreader.nextEvent();
			if (name == null && e.isStartElement()) {
				StartElement se = e.asStartElement();
				if (se.getName().getLocalPart().equals("f")) {
					name = se.getAttributeByName(q).getValue();
				}
			} else if (name != null && e.isCharacters()) {
				Characters c = e.asCharacters();
				String v = c.getData().trim();
				p.put(name, v);
				name = null;
			} else if (name != null && e.isEndElement()) {
				name = null;
			}
		}
		return p;
	}

	/*
	 * <doc> <f n="stockID">195180</f> <f n="companyID">10186</f> <f n="openingPrice">137.92</f> <f n="closingPrice">139.35</f> <f
	 * n="previousClosingPrice">135.81</f> <f n="minPrice">136.0</f> <f n="maxPrice">139.99</f> <f n="tradingVolume">16166700</f> <f
	 * n="percentChange">2.607</f> <f n="avg50Day">121.038431372549</f> <f n="avg100Day">106.3906930693069</f> <f
	 * n="avg200Day">110.82059701492533</f> <f n="avg52Week">122.67563999999997</f> </doc>
	 */
	public static FR_IStockPrice parserStockPriceConfig(String config) {
		FR_IStockPrice sp = new FR_StockPrice();
		try {
			HashMap<String, String> p = getStringProperties(config);

			sp.setOpeningPrice(round(Double.valueOf(p.get("openingPrice")), 2));
			sp.setClosingPrice(round(Double.valueOf(p.get("closingPrice")), 2));
			sp.setPreviousClosingPrice(round(Double.valueOf(p.get("previousClosingPrice")), 2));
			sp.setMinPrice(round(Double.valueOf(p.get("minPrice")), 2));
			sp.setMaxPrice(round(Double.valueOf(p.get("maxPrice")), 2));
			sp.setTradingVolume(Long.valueOf(p.get("tradingVolume")));
			sp.setPercentageChange(Double.valueOf(p.get("percentChange")));
			sp.setAvg50Day(Double.valueOf(p.get("avg50Day")));
			sp.setAvg100Day(Double.valueOf(p.get("avg100Day")));
			sp.setAvg200Day(Double.valueOf(p.get("avg200Day")));
			sp.setAvg364Day(Double.valueOf(p.get("avg52Week")));
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return sp;
	}

	/*
	 * <doc> <f n="trendID">2</f> <f n="oldCompanyID">3896</f> <f n="oldDesignation">Vice President</f> <f n="oldGroup">Marketing</f> <f
	 * n="newCompanyID">3671</f> <f n="newDesignation">Chief Executive Officer</f> <f
	 * n="url">http://www.theregister.co.uk/2005/11/08/borland_nielson_ceo/</f> </doc>
	 */
	public static FR_IMgmtChangeTrend parserMgmtConfig(String config) {
		FR_IMgmtChangeTrend mc = new FR_MgmtChangeTrend();
		try {
			HashMap<String, String> p = getStringProperties(config);

			mc.setTrendID(Integer.valueOf(p.get("trendID")));
			mc.setOldCompanyID(Integer.valueOf(p.get("oldCompanyID")));
			mc.setOldPositionTitle(p.get("oldDesignation"));
			mc.setOldGroup(p.get("oldGroup"));
			mc.setNewGroup(p.get("newGroup"));
			mc.setOldRegion(p.get("oldRegion"));
			mc.setNewRegion(p.get("newRegion"));
			mc.setNewCompanyID(Integer.valueOf(p.get("newCompanyID")));
			mc.setNewPositionTitle(p.get("newDesignation"));
			mc.setSupportingURL(p.get("url"));
			mc.setPersonName(p.get("person_name"));

		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return mc;
	}

	/*
	 * <doc> <f n="catIds">254739 and NOT 368700, 497370, 287074, 287075, 254892, 255444</f> <f n="numDocsEvaluated">150</f> <f
	 * n="numDocsTagged">852</f> <f n="title"> Apple Introduces New Mac Pro with Nehalem Xeon Processors</f> <f n="titleUrl">
	 * http://www.macrumors.com/2009/03/03/apple-introduces-new-mac-pro-with-nehalem-xeon-processors/ </f> <f
	 * n="titleSearchUrl">http://10.10.10.144:8081/cpf1/SearchDemoFirstTitle.jsp?q=C:AppleInc%20AND%20mac%20minis%20OR%20new%20mac%20pro%
	 * 20OR%20nehalem%20OR%20xeon%20processor%20OR%20desktop%20lines%20OR%20imac%20and%20mac%20OR%20new%20mac%20mini%20OR%20twice%20the%
	 * 20memory%20OR%20idling&amp;fq=-T:ProductReviews+-T:SecFilings+-F:MarketUpdates+-F:JobPosting+-T:SoftwareUpdate+-T:
	 * ResearchReportOffering+-S%3AThestreetCom&amp;nscope=1&amp;days=1&amp;order=relevance&amp;lastDay=2009-03-03&amp;scope=3</f> </doc>
	 */
	public static WebVolumeConfObj parserWebVolumeConfig(String config) {
		WebVolumeConfObj wv = new WebVolumeConfObj();
		try {
			HashMap<String, String> p = getStringProperties(config);

			wv.setNumDocsEvaluated(Integer.valueOf(p.get("numDocsEvaluated")));
			wv.setTitle(p.get("title"));
			wv.setTitleSearchUrl(p.get("titleSearchUrl"));
			wv.setTitleUrl(p.get("titleUrl"));
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return wv;
	}

	/*
	 * <doc> <f n="url">http://www.sec.gov/Archives/edgar/data/883980/000118143108056590/rrd220619.htm</f> <f n="CIKCODE">883980</f> <f
	 * n="DocTitle">FIRST DATA CORP: 8-K Filing 2008-10-08 01:00:00.000</f> </doc>
	 */
	public static Filings8KConfObj parser8KFilingsConfig(String config) {
		Filings8KConfObj f8k = new Filings8KConfObj();
		try {
			HashMap<String, String> p = getStringProperties(config);

			f8k.setTitle(p.get("DocTitle"));
			f8k.setTitleUrl(p.get("url"));
			String cikCode = p.get("CIKCODE");
			if (cikCode != null) {
				f8k.setCikCode(Long.valueOf(cikCode));
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return f8k;
	}

	/*
	 * <doc> <f n="url">http://www.tradingmarkets.com/.site/news/Stock%20News/2402750/</f> <f n="CIKCODE"></f> <f n="DocTitle">Industrias
	 * Bachoco Announces Filing of Form 20-F/A with the SEC</f> </doc>
	 */
	public static DelayedConfObj parseDelayedSecFilingsConfig(String config) {
		DelayedConfObj dsobj = new DelayedConfObj();
		try {
			HashMap<String, String> p = getStringProperties(config);
			dsobj.setTitle(p.get("DocTitle"));
			dsobj.setTitleUrl(p.get("url"));
			String cikCode = p.get("CIKCODE");
			if (cikCode != null) {
				dsobj.setCikCode(Long.valueOf(cikCode));
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return dsobj;
	}

	/**
	 * Utility method to get a no rounded up to specified place.
	 * 
	 * @param val Value of data-type double.
	 * @param place No of places to round off.
	 * @return Closest rounded value.
	 * @see Math#round(double) TODO:This should go to fr-common-utils, but I couldn't find appropriate target.
	 */
	public static double round(double val, int place) {
		double p = Math.pow(10, place);
		double tmp = Math.round(val * p);
		return tmp / p;
	}

}
