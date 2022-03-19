package com.firstrain.frapi.obj;

import java.util.Calendar;
import java.util.Date;

import com.firstrain.frapi.util.CompanyTeam;
import com.firstrain.solr.client.ISearchSpec;
import com.firstrain.utils.MgmtChangeType;

/**
 * @author Akanksha
 * 
 */

public class MgmtTurnoverServiceSpec implements ISearchSpec {

	public MgmtChangeType changeTypes[] = MgmtChangeType.values();
	public CompanyTeam changeLevels[] = CompanyTeam.values();
	public int days;
	public Date lastDay;
	public boolean lhs = true;
	public boolean monthlyCount = true;
	public boolean quarterlyCount = true;
	public boolean details = true;
	public int monthlyCountForMonth = 24;
	public int[] eventTypes;

	private int start = 0;
	private int rows = 30;
	private long total = 0;

	public int getMonthlyCountForMonth() {
		return monthlyCountForMonth;
	}

	void setMonthlyCountForMonth(int monthlyCountForMonth) {
		this.monthlyCountForMonth = monthlyCountForMonth;
	}

	public MgmtTurnoverServiceSpec() {
		super();
		setDefaultDateRange();
	}

	public MgmtTurnoverServiceSpec(ISearchSpec spec) {
		this.start = spec.getStart();
		this.rows = spec.getRows();
		setDefaultDateRange();
	}

	private void setDefaultDateRange() {
		this.lastDay = new Date();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, -2);
		this.days = (int) ((lastDay.getTime() - cal.getTimeInMillis()) / (1000 * 60 * 60 * 24)) + 1;
	}

	@Override
	public MgmtTurnoverServiceSpec cloneSpec() {
		return new MgmtTurnoverServiceSpec(this);
	}

	@Override
	public int getRows() {
		return rows;
	}

	@Override
	public int getStart() {
		return start;
	}

	@Override
	public ISearchSpec setRows(int rows) {
		this.rows = rows;
		return this;
	}

	@Override
	public ISearchSpec setStart(int start) {
		this.start = start;
		return this;
	}

	@Override
	public String toUrlString(ISearchSpec defaultSpec, String jspUrl, boolean appendCfq) {
		return null;
	}

	public MgmtChangeType[] getChangeTypes() {
		return changeTypes;
	}

	public void setChangeTypes(MgmtChangeType... changeTypes) {
		this.changeTypes = changeTypes;
	}

	public CompanyTeam[] getChangeLevels() {
		return changeLevels;
	}

	public void setChangeLevels(CompanyTeam... changeLevels) {
		this.changeLevels = changeLevels;
	}

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}

	public Date getLastDay() {
		return lastDay;
	}

	public void setLastDay(Date lastDay) {
		this.lastDay = lastDay;
	}

	public boolean isLhs() {
		return lhs;
	}

	public void setLhs(boolean lhs) {
		this.lhs = lhs;
	}

	public boolean isMonthlyCount() {
		return monthlyCount;
	}

	public void setMonthlyCount(boolean monthlyCount) {
		this.monthlyCount = monthlyCount;
	}

	public boolean isQuarterlyCount() {
		return quarterlyCount;
	}

	public void setQuarterlyCount(boolean quarterlyCount) {
		this.quarterlyCount = quarterlyCount;
	}

	public boolean isDetails() {
		return details;
	}

	public void setDetails(boolean details) {
		this.details = details;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}
}

