package com.firstrain.frapi.domain;

import java.io.Serializable;
import java.util.List;

import com.firstrain.frapi.pojo.Event;

public class HistoricalStat implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8663985986691728379L;
	private List<Event> signals;
	private CompanyVolume companyVolume;
	private CompanyTradingRange tradeRange;

	public List<Event> getSignals() {
		return signals;
	}

	public void setSignals(List<Event> signals) {
		this.signals = signals;
	}

	public CompanyVolume getCompanyVolume() {
		return companyVolume;
	}

	public void setCompanyVolume(CompanyVolume companyVolume) {
		this.companyVolume = companyVolume;
	}

	public CompanyTradingRange getTradeRange() {
		return tradeRange;
	}

	public void setTradeRange(CompanyTradingRange tradeRange) {
		this.tradeRange = tradeRange;
	}
}
