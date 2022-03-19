package com.firstrain.frapi.util;

import java.io.StringReader;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.log4j.Logger;

import com.firstrain.frapi.events.IEvents;
import com.firstrain.frapi.events.IEvents.EventGroupEnum;
import com.firstrain.frapi.events.IEvents.EventTypeEnum;
import com.firstrain.frapi.pojo.Event;
import com.firstrain.frapi.util.DefaultEnums.TitleType;
import com.firstrain.mip.object.FR_IEvent;
import com.firstrain.mip.object.FR_IMgmtChangeTrend;
import com.firstrain.mip.object.impl.FR_MgmtChangeTrend;
import com.firstrain.utils.TitleUtils;

/**
 * @author Akanksha
 * 
 */

public class EventUtils {

	private static final Logger LOG = Logger.getLogger(EventUtils.class);
	private static XMLInputFactory factory = XMLInputFactory.newInstance();

	@PostConstruct
	private void init() {
		factory.setProperty(XMLInputFactory.IS_COALESCING, true);
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
	 * <doc> <f n="catIds">254739 and NOT 368700, 497370, 287074, 287075, 254892, 255444</f> <f n="numDocsEvaluated">150</f> <f
	 * n="numDocsTagged">852</f> <f n="title"> Apple Introduces New Mac Pro with Nehalem Xeon Processors</f> <f n="titleUrl">
	 * http://www.macrumors.com/2009/03/03/apple-introduces-new-mac-pro-with-nehalem-xeon-processors/ </f> <f
	 * n="titleSearchUrl">http://10.10.10.144:8081/cpf1/SearchDemoFirstTitle.jsp?q=C:AppleInc%20AND%20mac%20minis%20OR%20new%20mac%20pro%
	 * 20OR%20nehalem%20OR%20xeon%20processor%20OR%20desktop%20lines%20OR%20imac%20and%20mac%20OR%20new%20mac%20mini%20OR%20twice%20the%
	 * 20memory%20OR%20idling&amp;fq=-T:ProductReviews+-T:SecFilings+-F:MarketUpdates+-F:JobPosting+-T:SoftwareUpdate+-T:
	 * ResearchReportOffering+-S%3AThestreetCom&amp;nscope=1&amp;days=1&amp;order=relevance&amp;lastDay=2009-03-03&amp;scope=3</f> </doc>
	 */
	public static void parserWebVolumeConfig(String config, Event event) {
		try {
			HashMap<String, String> p = getStringProperties(config);
			event.setTitle(p.get("title"));
			event.setCaption((p.get("titleSearchUrl")));
			event.setUrl(p.get("titleUrl"));
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	public static String getSecTitle(String companyName, String title, int eventType) {
		return getSecTitle(companyName, title, eventType, TitleType.CSSCLASS);
	}

	public static String getSecTitle(String companyName, String titleParam, int eventType, TitleType type) {
		String title = titleParam;
		boolean inlineStyle = false;
		if (TitleType.CSSINLINE == type) {
			inlineStyle = true;
		}
		String secFormType;
		if (eventType == FR_IEvent.TYPE_TEN_K_FILING) {
			secFormType = getSecFormType(title, "10-K/A", "10-K");
		} else if (eventType == FR_IEvent.TYPE_TEN_Q_FILING) {
			secFormType = getSecFormType(title, "10-Q/A", "10-Q");
		} else if (eventType == FR_IEvent.TYPE_DELAYED_SEC_FILING) {
			if (type == TitleType.PLAIN) {
				return TitleUtils.getSecDelayedTitleWithoutHtml(companyName, title, EventGroupEnum.GROUP_DELAYED_FILING.getLabel());
			} else if (type == TitleType.HTML) {
				return TitleUtils.getSecDelayedTitleWithHtml(companyName, title, EventGroupEnum.GROUP_DELAYED_FILING.getLabel());
			} else {
				return TitleUtils.getSecDelayedTitle(companyName, title, EventGroupEnum.GROUP_DELAYED_FILING.getLabel(), inlineStyle);
			}
		} else {
			secFormType = getSecFormType(title, "8-K/A", "8-K");
			EventTypeEnum e = IEvents.EventTypeEnum.eventTypeMap.get(eventType);
			if (e != null) {
				title = e.getLabel().replaceFirst("Filed an 8K Statement on ", "");
			}
		}
		String construtedTitle = "";

		if (TitleType.PLAIN == type) {
			construtedTitle = TitleUtils.getSecTitleWithoutHtml(companyName, title, secFormType);
		} else if (TitleType.HTML == type) {
			construtedTitle = TitleUtils.getSecTitleWithHtml(companyName, title, secFormType);
		} else {
			construtedTitle = TitleUtils.getSecTitle(companyName, title, secFormType, inlineStyle);
		}


		return construtedTitle;
	}

	private static String getSecFormType(final String title, final String formTypeFull, final String formType) {
		String secFormType = null;
		if (title.contains(formTypeFull)) {
			secFormType = formTypeFull;
		} else {
			secFormType = formType;
		}
		return secFormType;
	}
}

