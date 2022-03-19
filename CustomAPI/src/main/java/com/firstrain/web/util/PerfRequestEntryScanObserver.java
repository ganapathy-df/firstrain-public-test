package com.firstrain.web.util;

import org.apache.log4j.Logger;

import com.firstrain.utils.object.PerfRequestEntry;
import com.firstrain.utils.object.RequestPerformanceObserver;


public class PerfRequestEntryScanObserver implements RequestPerformanceObserver {

	private static final Logger PERF_LOG = Logger.getLogger("PerfLogging");
	private int alertTime = 5 * 1000;

	@Override
	public void execute(PerfRequestEntry entry) {

		if ((entry.getEndTime() - entry.getStartTime()) > alertTime) {
			PERF_LOG.warn("Request taking longer than " + alertTime + "ms to complete:" + entry.toStringLog());
		}
	}

	public void setAlertTime(int alertTime) {
		this.alertTime = alertTime;
	}
}
