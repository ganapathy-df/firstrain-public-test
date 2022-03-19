package com.firstrain.frapi.domain;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnore;

public class MgmtSummary implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6876218829902583437L;
	private int boardDepartureCount;
	private int boardHireCount;
	private int boardInternalMoveCount;
	private int execDepartureCount;
	private int execHireCount;
	private int execInternalMoveCount;
	private int othersDepartureCount;
	private int othersHireCount;
	private int othersInternalMoveCount;
	private int reportYear;
	private int reportMonth;
	private int reportQuarter;

	public int getBoardDepartureCount() {
		return boardDepartureCount;
	}

	public void setBoardDepartureCount(int boardDepartureCount) {
		this.boardDepartureCount = boardDepartureCount;
	}

	public int getBoardHireCount() {
		return boardHireCount;
	}

	public void setBoardHireCount(int boardHireCount) {
		this.boardHireCount = boardHireCount;
	}

	public int getBoardInternalMoveCount() {
		return boardInternalMoveCount;
	}

	public void setBoardInternalMoveCount(int boardInternalMoveCount) {
		this.boardInternalMoveCount = boardInternalMoveCount;
	}

	public int getExecDepartureCount() {
		return execDepartureCount;
	}

	public void setExecDepartureCount(int execDepartureCount) {
		this.execDepartureCount = execDepartureCount;
	}

	public int getExecHireCount() {
		return execHireCount;
	}

	public void setExecHireCount(int execHireCount) {
		this.execHireCount = execHireCount;
	}

	public int getExecInternalMoveCount() {
		return execInternalMoveCount;
	}

	public void setExecInternalMoveCount(int execInternalMoveCount) {
		this.execInternalMoveCount = execInternalMoveCount;
	}

	public int getOthersDepartureCount() {
		return othersDepartureCount;
	}

	public void setOthersDepartureCount(int othersDepartureCount) {
		this.othersDepartureCount = othersDepartureCount;
	}

	public int getOthersHireCount() {
		return othersHireCount;
	}

	public void setOthersHireCount(int othersHireCount) {
		this.othersHireCount = othersHireCount;
	}

	public int getOthersInternalMoveCount() {
		return othersInternalMoveCount;
	}

	public void setOthersInternalMoveCount(int othersInternalMoveCount) {
		this.othersInternalMoveCount = othersInternalMoveCount;
	}

	public int getReportYear() {
		return reportYear;
	}

	public void setReportYear(int reportYear) {
		this.reportYear = reportYear;
	}

	public int getReportMonth() {
		return reportMonth;
	}

	public void setReportMonth(int reportMonth) {
		this.reportMonth = reportMonth;
	}

	public int getReportQuarter() {
		return reportQuarter;
	}

	public void setReportQuarter(int reportQuarter) {
		this.reportQuarter = reportQuarter;
	}

	@JsonIgnore
	public int getTotalHire() {
		return execHireCount + boardHireCount + othersHireCount;
	}

	@JsonIgnore
	public int getTotalDeparture() {
		return execDepartureCount + boardDepartureCount + othersDepartureCount;
	}

	@JsonIgnore
	public int getTotalInternalMove() {
		return execInternalMoveCount + boardInternalMoveCount + othersInternalMoveCount;
	}
}
