package com.firstrain.frapi.domain;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.ParseException;

import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonIgnore;

public class StockPriceMeta extends MetaEvent {
	private static final Logger LOG = Logger.getLogger(StockPriceMeta.class);

	private String openingPrice;
	private String closingPrice;
	private String previousClosingPrice;
	private String minPrice;
	private String maxPrice;
	private String tradingVolume;
	private String percentChange;
	private String avg50Day;
	private String avg100Day;
	private String avg200Day;
	private String avg52Week;

	public String getOpeningPrice() {
		return openingPrice;
	}

	public void setOpeningPrice(String openingPrice) {
		this.openingPrice = openingPrice;
	}

	public String getClosingPrice() {
		return closingPrice;
	}

	public void setClosingPrice(String closingPrice) {
		this.closingPrice = closingPrice;
	}

	public String getPreviousClosingPrice() {
		return previousClosingPrice;
	}

	public void setPreviousClosingPrice(String previousClosingPrice) {
		this.previousClosingPrice = previousClosingPrice;
	}

	public String getMinPrice() {
		return minPrice;
	}

	public void setMinPrice(String minPrice) {
		this.minPrice = minPrice;
	}

	public String getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(String maxPrice) {
		this.maxPrice = maxPrice;
	}

	public String getTradingVolume() {
		return tradingVolume;
	}

	public void setTradingVolume(String tradingVolume) {
		this.tradingVolume = tradingVolume;
	}

	public String getPercentChange() {
		return percentChange;
	}

	public void setPercentChange(String percentChange) {
		this.percentChange = percentChange;
	}

	public String getAvg50Day() {
		return avg50Day;
	}

	public void setAvg50Day(String avg50Day) {
		this.avg50Day = avg50Day;
	}

	public String getAvg100Day() {
		return avg100Day;
	}

	public void setAvg100Day(String avg100Day) {
		this.avg100Day = avg100Day;
	}

	public String getAvg200Day() {
		return avg200Day;
	}

	public void setAvg200Day(String avg200Day) {
		this.avg200Day = avg200Day;
	}

	public String getAvg52Week() {
		return avg52Week;
	}

	public void setAvg52Week(String avg52Week) {
		this.avg52Week = avg52Week;
	}

	@JsonIgnore
	public String getPriceChange() {
		// to show price change on siv in fraction
		return getNumberFormatter().format(Double.parseDouble(this.closingPrice) - Double.parseDouble(this.previousClosingPrice));
	}

	private static String S_BOLD = "<span class='status-callout'>";
	private static String S_RED = "<span class='stock-status--down'>";
	private static String S_GREEN = "<span class='stock-status--up'>";
	/* Tags for InLineStyling */
	private static String S_BOLD_INLINESTYLE = "<span style='text-transform:uppercase;font-size:90%'>";
	private static String S_RED_INLINESTYLE = "<span style='color:#e82d2d'>";
	private static String S_GREEN_INLINESTYLE = "<span style='color:#32932d'>";

	@JsonIgnore
	public String getSPTitle(int eventType, boolean inLineStyle) {


		NumberFormat ROUND2PLACES = getNumberFormatter();
		StringBuilder str = new StringBuilder();
		/*
		 * if (companyName != null) { str.append((inLineStyle ? S_ORANGE_INLINESTYLE : S_ORANGE)).append(FR_Encoder.encodeXML(companyName,
		 * FR_Encoder.CHARACTER_ENCODING)).append("</span> <br/>"); }
		 */
		Number closingPriceF = 0;
		closingPriceF = getClosingPriceF(ROUND2PLACES, closingPriceF);
		String subtitle = "";
		String avg = "";
		if (eventType >= 301 && eventType <= 304) {
			if (eventType == 301) {
				avg = avg50Day;
				subtitle = "50-day Moving Average";
			} else if (eventType == 302) {
				avg = avg100Day;
				subtitle = "100-day Moving Average";
			} else if (eventType == 303) {
				avg = avg200Day;
				subtitle = "200-day Moving Average";
			} else if (eventType == 304) {
				avg = avg52Week;
				subtitle = "52-week Moving Average";
			}
			str.append("Stock closing price ").append(inLineStyle ? S_BOLD_INLINESTYLE : S_BOLD).append("($").append(closingPrice)
					.append(")");
			avg = getAvg(ROUND2PLACES, avg, closingPriceF, inLineStyle, str);
			str.append(subtitle).append("</span>").append(" ($").append(avg).append(")");
		} else if (eventType == 300) {
			str.append("Stock price ").append(inLineStyle ? S_BOLD_INLINESTYLE : S_BOLD);
			appendPercentageChange(ROUND2PLACES, inLineStyle, str);
		} else {
			str.append("Stock Price: Stock Closing Price Contra to Industry Composite by >5%");
		}
		return str.toString();
	}

	private void appendPercentageChange(final NumberFormat ROUND2PLACES, final boolean inLineStyle, final StringBuilder str) {
	    try {
	    	percentChange = ROUND2PLACES.format(ROUND2PLACES.parse(percentChange));
	    } catch (ParseException e) {
	    	LOG.error(e.getMessage(), e);
	    }
	    if (percentChange.startsWith("-")) {
	    	str.append(inLineStyle ? S_RED_INLINESTYLE : S_RED).append("CLOSED DOWN ").append(percentChange).append("%</span>");
	    } else {
	    	str.append(inLineStyle ? S_GREEN_INLINESTYLE : S_GREEN).append(" CLOSED UP +").append(percentChange).append("%</span>");
	    }
	    str.append("</span>").append(" at $").append(closingPrice);
	}

	private String getAvg(final NumberFormat ROUND2PLACES, String avgParam, final Number closingPriceF, final boolean inLineStyle, final StringBuilder str) {
	    String avg = avgParam;
	    Number avgF = 0;
	    try {
	    	avgF = ROUND2PLACES.parse(avg);
	    	avg = ROUND2PLACES.format(avgF);
	    } catch (ParseException e) {
	    	LOG.error(e.getMessage(), e);
	    }
	    if (closingPriceF.floatValue() > avgF.floatValue()) {
	    	str.append(" ").append(inLineStyle ? S_GREEN_INLINESTYLE : S_GREEN).append("ROSE ABOVE</span> ");
	    } else {
	    	str.append(" ").append(inLineStyle ? S_RED_INLINESTYLE : S_RED).append("DROPPED BELOW</span> ");
	    }
	    return avg;
	}

	private Number getClosingPriceF(final NumberFormat ROUND2PLACES, Number closingPriceF) {
	    try {
	    	closingPriceF = ROUND2PLACES.parse(closingPrice);
	    	closingPrice = ROUND2PLACES.format(closingPriceF);
	    } catch (ParseException e) {
	    	LOG.error(e.getMessage(), e);
	    }
	    return closingPriceF;
	}

	@JsonIgnore
	public String getSPTitleWithoutHtml(int eventType) {

		NumberFormat ROUND2PLACES = getNumberFormatter();
		StringBuilder str = new StringBuilder();

		Number closingPriceF = getClosingPriceF(ROUND2PLACES);
		String subtitle = "";
		String avg = "";
		if (eventType >= 301 && eventType <= 304) {
			if (eventType == 301) {
				avg = avg50Day;
				subtitle = "50-day Moving Average";
			} else if (eventType == 302) {
				avg = avg100Day;
				subtitle = "100-day Moving Average";
			} else if (eventType == 303) {
				avg = avg200Day;
				subtitle = "200-day Moving Average";
			} else if (eventType == 304) {
				avg = avg52Week;
				subtitle = "52-week Moving Average";
			}
			appendData(ROUND2PLACES, avg, closingPriceF, str, subtitle);
		} else if (eventType == 300) {
			appendPercentageChange(ROUND2PLACES, str);
		} else {
			str.append("Stock Price: Stock Closing Price Contra to Industry Composite by >5%");
		}
		return str.toString();
	}

	private void appendData(final NumberFormat ROUND2PLACES, String avg, final Number closingPriceF, final StringBuilder str, final String subtitle) {
	    str.append("Stock closing price ").append("($").append(this.closingPrice).append(")");
	    Number avgF = 0;
	    try {
	    	avgF = ROUND2PLACES.parse(avg);
	    	avg = ROUND2PLACES.format(avgF);
	    } catch (ParseException e) {
	    	LOG.error(e.getMessage(), e);
	    }
	    if (closingPriceF.floatValue() > avgF.floatValue()) {
	    	str.append(" ").append("ROSE ABOVE ");
	    } else {
	    	str.append(" ").append("DROPPED BELOW ");
	    }
	    str.append(subtitle).append(" ($").append(avg).append(")");
	}

	private void appendPercentageChange(final NumberFormat ROUND2PLACES, final StringBuilder str) {
	    str.append("Stock price ");
	    try {
	    	this.percentChange = ROUND2PLACES.format(ROUND2PLACES.parse(this.percentChange));
	    } catch (ParseException e) {
	    	LOG.error(e.getMessage(), e);
	    }
	    if (this.percentChange.startsWith("-")) {
	    	str.append("CLOSED DOWN ").append(this.percentChange).append("%");
	    } else {
	    	str.append("CLOSED UP +").append(this.percentChange).append("%");
	    }
	    str.append(" at $").append(closingPrice);
	}

	private Number getClosingPriceF(final NumberFormat ROUND2PLACES) {
	    Number closingPriceF = 0;
	    try {
	    	closingPriceF = ROUND2PLACES.parse(this.closingPrice);
	    	this.closingPrice = ROUND2PLACES.format(closingPriceF);
	    } catch (ParseException e) {
	    	LOG.error(e.getMessage(), e);
	    }
	    return closingPriceF;
	}

	private NumberFormat getNumberFormatter() {
		NumberFormat ROUND2PLACES = NumberFormat.getInstance();
		ROUND2PLACES.setMinimumFractionDigits(2);
		ROUND2PLACES.setMaximumFractionDigits(2);
		ROUND2PLACES.setRoundingMode(RoundingMode.HALF_UP);
		return ROUND2PLACES;
	}
}
