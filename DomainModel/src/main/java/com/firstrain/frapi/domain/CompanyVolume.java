package com.firstrain.frapi.domain;

import java.io.Serializable;
import java.sql.Timestamp;

public class CompanyVolume implements Serializable {

	private static final long serialVersionUID = 3597825010521274480L;
	private int diffId = -1;
	private Timestamp date = null;
	private double total = 0;

	/**
	 * @return the date
	 */
	public Timestamp getDate() {
		return this.date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(Timestamp date) {

		this.date = date;
	}

	/**
	 * @return the total
	 */
	public double getTotal() {
		return this.total;
	}

	/**
	 * @param total the total to set
	 */
	public void setTotal(double total) {
		this.total = total;
	}

	/**
	 * @param diffId the diffId to set
	 */
	public void setDiffId(int diffId) {
		this.diffId = diffId;
	}

	/**
	 * @return the diffId
	 */
	public int getDiffId() {
		return this.diffId;
	}
}
