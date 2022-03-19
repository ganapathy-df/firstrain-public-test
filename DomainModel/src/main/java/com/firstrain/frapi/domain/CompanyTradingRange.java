package com.firstrain.frapi.domain;

import java.io.Serializable;

public class CompanyTradingRange implements Serializable {

	private static final long serialVersionUID = -715481898966713481L;

	public CompanyTradingRange() {
		super();
	}

	private int diffId = -1;
	private double openingPrice = 0.0f;
	private double closingPrice = 0.0f;
	private String openingPriceStr = "0";
	private String closingPriceStr = "0";

	/**
	 * @return the diffId
	 */
	public int getDiffId() {
		return this.diffId;
	}

	/**
	 * @param diffId the diffId to set
	 */
	public void setDiffId(int diffId) {
		this.diffId = diffId;
	}

	/**
	 * @return the openingPrice
	 */
	public double getOpeningPrice() {
		return this.openingPrice;
	}

	/**
	 * @param openingPrice the openingPrice to set
	 */
	public void setOpeningPrice(double openingPrice) {
		this.openingPrice = openingPrice;
	}

	/**
	 * @return the closingPrice
	 */
	public double getClosingPrice() {
		return this.closingPrice;
	}

	/**
	 * @param closingPrice the closingPrice to set
	 */
	public void setClosingPrice(double closingPrice) {
		this.closingPrice = closingPrice;
	}

	/**
	 * @return the openingPrice in string format with 2 decimals
	 */
	public String getOpeningPriceStr() {
		if (this.openingPriceStr.equals("0")) {
			this.openingPriceStr = String.valueOf(this.openingPrice);
		}
		return this.openingPriceStr;
	}

	/**
	 * @return the closingPrice in string format with 2 decimals
	 */
	public String getClosingPriceStr() {
		if (this.closingPriceStr.equals("0")) {
			this.closingPriceStr = String.valueOf(this.closingPrice);
		}
		return this.closingPriceStr;
	}

}
