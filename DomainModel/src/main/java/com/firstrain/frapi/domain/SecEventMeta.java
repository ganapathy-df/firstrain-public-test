package com.firstrain.frapi.domain;

import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonIgnore;

import com.firstrain.frapi.util.DefaultEnums.EventTypeEnum;
import com.firstrain.utils.FR_Encoder;

public class SecEventMeta extends MetaEvent {
	private static final Logger LOG = Logger.getLogger(SecEventMeta.class);

	private String secId;
	private String originalTitle;
	private String secFormType;

	public String getSecId() {
		return secId;
	}

	public void setSecId(String secId) {
		this.secId = secId;
	}

	public String getOriginalTitle() {
		return originalTitle;
	}

	public void setOriginalTitle(String originalTitle) {
		this.originalTitle = originalTitle;
	}

	public String getSecFormType() {
		return secFormType;
	}

	public void setSecFormType(String secFormType) {
		this.secFormType = secFormType;
	}

	@JsonIgnore
	public String getSeceTitle(String eventType) {
		if (EventTypeEnum.TYPE_DELAYED_SEC_FILING.name().equals(eventType)) {
			return getSecDelayedTitle("", originalTitle, "SEC Delay", true);
		}

		String secFormTypeString = getSecFormType(eventType);

		return getSecTitle(originalTitle, secFormTypeString);
	}

	@JsonIgnore
	public String getSeceTitleWithoutHtml(String eventType) {
		if (EventTypeEnum.TYPE_DELAYED_SEC_FILING.name().equals(eventType)) {
			return getSecDelayedTitleWithoutHtml("", originalTitle, "Delayed SEC Filings");
		}

		String secFormTypeString = getSecFormType(eventType);

		return getSecTitleWithoutHtml(originalTitle, secFormTypeString);
	}

	@JsonIgnore
	public String getSecTitle() {
		return getSecTitle(originalTitle, secFormType);
	}

	private String getSecFormType(String eventType) {
		String secFormType;

		if (EventTypeEnum.TYPE_TEN_K_EVENT.name().equals(eventType)) {
			secFormType = originalTitle.contains("10-K/A") ? "10-K/A" : "10-K";
		} else if (EventTypeEnum.TYPE_TEN_Q_EVENT.name().equals(eventType)) {
			secFormType = originalTitle.contains("10-Q/A") ? "10-Q/A" : "10-Q";
		} else {
			secFormType = originalTitle.contains("8-K/A") ? "8-K/A" : "8-K";
			EventTypeEnum e = EventTypeEnum.valueOf(EventTypeEnum.class, eventType);
			originalTitle = e.getLabel().replaceFirst("Filed an 8K Statement on ", "");
		}

		return secFormType;
	}

	private String getSecTitle(String originalTitleParam, String secFormType) {
		String originalTitle = originalTitleParam;
		String constructedTitle = "";
		int index = originalTitle.indexOf(":");
		if (index != -1) {
			originalTitle = originalTitle.substring(index + 1);
		}
		String[] subTitles = originalTitle.split(","); // we have company name before :
		String st = null;
		if (subTitles.length > 1) {
			st = subTitles[1].trim();
		}
		if ("10-K".equals(secFormType)) {
			// Filed an Annual Report for FY <XXXX>
			constructedTitle += String.format("Filed an " + ("<strong>") + "Annual Report</strong> for FY %s", st.replaceFirst("FY", ""));
		} else if ("10-K/A".equals(secFormType)) {
			// Filed an Annual Report for FY <XXXX>
			constructedTitle +=
					String.format("Amended the " + ("<strong>") + "Annual Report</strong> for FY %s", st.replaceFirst("FY", ""));
		} else if ("10-Q".equals(secFormType)) {
			// Filed a Quarterly Report for <YY>, FY <XXXX>
			constructedTitle += String.format("Filed a " + ("<strong>") + "Quarterly Report</strong> for %s, FY %s", st,
					subTitles[2].trim().replaceFirst("FY", ""));
		} else if ("10-Q/A".equals(secFormType)) {
			// Filed a Quarterly Report for <YY>, FY <XXXX>
			constructedTitle += String.format("Amended the " + ("<strong>") + "Quarterly Report</strong> for %s, FY %s", st,
					subTitles[2].trim().replaceFirst("FY", ""));
		} else if ("8-K".equals(secFormType)) {
			// Filed an 8K Statement on <ABC>.
			constructedTitle = getFormattedTitle(st, constructedTitle, "Filed an ", subTitles);
		} else if ("8-K/A".equals(secFormType)) {
			// Filed an 8K Statement on <ABC>.
			constructedTitle = getFormattedTitle(st, constructedTitle, "Amended the ", subTitles);
		}
		return constructedTitle;
	}

	private String getFormattedTitle(final String st, String constructedTitle, final String message1, final String[] subTitles) {
		if (st != null) {
			constructedTitle += String.format(message1 + ("<strong>") + "8K Statement</strong> on %s, %s",
					subTitles[0].trim().replaceFirst("8-K ", "").replaceFirst("- ", ""), st);
		} else {
			constructedTitle += String.format(message1 + ("<strong>") + "8K Statement</strong> on %s",
					subTitles[0].trim().replaceFirst("8-K ", "").replaceFirst("- ", ""));
		}
		return constructedTitle;
	}

	@JsonIgnore
	public String getSecDelayedTitle(String companyNameParam, String title, String prefIx, boolean inLineStyle) {
		String companyName = companyNameParam;
		String constructedTitle = "";
		if (companyName != null) {
			companyName = FR_Encoder.encodeXML(companyName, FR_Encoder.CHARACTER_ENCODING);
			constructedTitle += inLineStyle ? "<span>" + companyName + "</span> <br/>" : "<span>" + companyName + "</span> <br/>";
		}
		constructedTitle += prefIx + ": " + title;
		return constructedTitle;
	}

	@JsonIgnore
	public String getSecDelayedTitleWithoutHtml(String companyNameParam, String title, String prefIx) {
		String companyName = companyNameParam;
		String constructedTitle = "";
		if (companyName != null) {
			companyName = FR_Encoder.encodeXML(companyName, FR_Encoder.CHARACTER_ENCODING);
			constructedTitle += companyName;
		}
		constructedTitle += prefIx + " : " + title;
		return constructedTitle;
	}

	@JsonIgnore
	public String getSecTitleWithoutHtml() {
		return getSecTitleWithoutHtml(originalTitle, secFormType);
	}

	private String getSecTitleWithoutHtml(String titleParam, String secFormType) {
		String title = titleParam;
		String orgTitle = title;
		String constructedTitle = "";
		try {
			int index = title.indexOf(":");
			if (index != -1) {
				title = title.substring(index + 1);
			}
			String[] subTitles = title.split(","); // we have company name before :
			String st = null;
			if (subTitles != null && subTitles.length > 1) {
				st = subTitles[1].trim();
			}
			if ("10-K".equals(secFormType)) {
				// Filed an Annual Report for FY <XXXX>
				constructedTitle += String.format("Filed an Annual Report for FY %s", st.replaceFirst("FY", ""));
			} else if ("10-K/A".equals(secFormType)) {
				// Filed an Annual Report for FY <XXXX>
				constructedTitle += String.format("Amended the Annual Report for FY %s", st.replaceFirst("FY", ""));
			} else if ("10-Q".equals(secFormType)) {
				// Filed a Quarterly Report for <YY>, FY <XXXX>
				constructedTitle += String.format("Filed a Quarterly Report for %s, FY %s", st, subTitles[2].trim().replaceFirst("FY", ""));
			} else if ("10-Q/A".equals(secFormType)) {
				// Filed a Quarterly Report for <YY>, FY <XXXX>
				constructedTitle +=
						String.format("Amended the Quarterly Report for %s, FY %s", st, subTitles[2].trim().replaceFirst("FY", ""));
			} else if ("8-K".equals(secFormType)) {
				// Filed an 8K Statement on <ABC>.
				constructedTitle = getTitle(st, constructedTitle, "Filed an 8K Statement on %s, %s", subTitles, "Filed an 8K Statement on %s");
			} else if ("8-K/A".equals(secFormType)) {
				// Filed an 8K Statement on <ABC>.
				constructedTitle = getTitle(st, constructedTitle, "Amended the 8K Statement on %s, %s", subTitles, "Amended the 8K Statement on %s");
			}
		} catch (Exception e) {
			LOG.error("Error for title: " + orgTitle, e);
			return orgTitle;
		}
		return constructedTitle;
	}

	private String getTitle(final String st, String constructedTitle, final String message1, final String[] subTitles, final String message2) {
		if (st != null) {
			constructedTitle += String.format(message1,
					subTitles[0].trim().replaceFirst("8-K ", "").replaceFirst("- ", ""), st);
		} else {
			constructedTitle += String.format(message2,
					subTitles[0].trim().replaceFirst("8-K ", "").replaceFirst("- ", ""));
		}
		return constructedTitle;
	}
}
