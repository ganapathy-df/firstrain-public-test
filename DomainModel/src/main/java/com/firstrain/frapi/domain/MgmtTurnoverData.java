package com.firstrain.frapi.domain;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonIgnore;

import com.firstrain.frapi.pojo.wrapper.BaseSet;
import com.firstrain.utils.JSONUtility;

public class MgmtTurnoverData extends BaseSet implements Serializable {

	private static final long serialVersionUID = 238189435954298905L;
	private static final Logger LOG = Logger.getLogger(MgmtTurnoverData.class);

	private List<MgmtSummary> monthlySummary;
	private int totalTurnoverMth;
	private float averageTurnoverMth;
	private int companyId;

	public MgmtTurnoverData(SectionType type) {
		super(type);
	}

	public MgmtTurnoverData() {}

	public int getCompanyId() {
		return companyId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	public List<MgmtSummary> getMonthlySummary() {
		return monthlySummary;
	}

	public void setMonthlySummary(List<MgmtSummary> monthlySummary) {
		this.monthlySummary = monthlySummary;
	}

	public int getTotalTurnoverMth() {
		return totalTurnoverMth;
	}

	public void setTotalTurnoverMth(int totalTurnoverMth) {
		this.totalTurnoverMth = totalTurnoverMth;
	}

	public float getAverageTurnoverMth() {
		return averageTurnoverMth;
	}

	public void setAverageTurnoverMth(float averageTurnoverMth) {
		this.averageTurnoverMth = averageTurnoverMth;
	}

	@JsonIgnore
	public MgmtChart getMgmtChart() {
		MgmtChart chart = new MgmtChart();
		if (monthlySummary != null && !monthlySummary.isEmpty()) {
			int monthIndex = Calendar.getInstance().get(Calendar.MONTH) + 1;
			StringBuilder labels = new StringBuilder();
			StringBuilder hires = new StringBuilder();
			StringBuilder departures = new StringBuilder();
			StringBuilder moves = new StringBuilder();
			int max = 0;
			labels.append("|");
			for (MgmtSummary monthSummary : monthlySummary) {
				if (monthIndex == 12) {
					monthIndex = 0;
				}
				labels.append(MONTHS[monthIndex++]).append("|");
				max = getMax(departures, hires, max, monthSummary, moves);
			}
			populateLengthValueAndMax(chart, departures, hires, labels, max, moves);
		}
		return chart;
	}

	private int getMax(final StringBuilder departures, final StringBuilder hires, int maxParam, final MgmtSummary monthSummary, final StringBuilder moves) {
	    int max = maxParam;
	    int totalHireCount = monthSummary.getTotalHire();
	    int totalDepartureCount = monthSummary.getTotalDeparture();
	    int totalInternalMoveCount = monthSummary.getTotalInternalMove();
	    hires.append(totalHireCount).append(",");
	    if (max < totalHireCount) {
	    	max = totalHireCount;
	    }
	    departures.append("-").append(totalDepartureCount).append(",");
	    if (max < totalDepartureCount) {
	    	max = totalDepartureCount;
	    }
	    moves.append(totalInternalMoveCount).append(",");
	    if (max < totalInternalMoveCount) {
	    	max = totalInternalMoveCount;
	    }
	    return max;
	}

	private void populateLengthValueAndMax(final MgmtChart chart, final StringBuilder departures, final StringBuilder hires, final StringBuilder labels, final int max, final StringBuilder moves) {
	    hires.setLength(hires.length() - 1);
	    departures.setLength(departures.length() - 1);
	    moves.setLength(moves.length() - 1);
	    labels.setLength(labels.length() - 1);
	    String valueAsString = departures.append("|").append(hires).append("|").append(moves).toString();
	    chart.setValuesAsString(valueAsString);
	    chart.setLabelAsString(labels.toString());
	    chart.setMax(max);
	}

	@JsonIgnore
	public MgmtChart getMgmtRGraphChart() {
		MgmtChart chart = new MgmtChart();
		if (monthlySummary != null && !monthlySummary.isEmpty()) {
			try {
				int length = monthlySummary.size();
				String[] monthArr = new String[length];
				int[][] mgmtArr = new int[length][3];
				int max = 0;
				for (int i = 0; i < length; i++) {
					MgmtSummary ms = monthlySummary.get(i);
					monthArr[i] = MONTHS[ms.getReportMonth() - 1];
					int totalHireCount = ms.getTotalHire();
					int totalDepartureCount = ms.getTotalDeparture();
					int totalInternalMoveCount = ms.getTotalInternalMove();
					mgmtArr[i][0] = -totalDepartureCount;
					if (max < totalDepartureCount) {
						max = totalDepartureCount;
					}
					mgmtArr[i][1] = totalHireCount;
					if (max < totalHireCount) {
						max = totalHireCount;
					}
					mgmtArr[i][2] = totalInternalMoveCount;
					if (max < totalInternalMoveCount) {
						max = totalInternalMoveCount;
					}
				}
				chart.setValuesAsString(JSONUtility.serialize(mgmtArr));
				chart.setLabelAsString(JSONUtility.serialize(monthArr));
				chart.setMax(max);
			} catch (Exception e) {
				LOG.error("Exception while generating data for Management RGraph Chart : " + e.getMessage(), e);
			}
		}
		return chart;
	}

	private static final String[] MONTHS = new String[] {"J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D"};
}
