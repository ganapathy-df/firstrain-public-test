package com.firstrain.frapi.domain;

import java.util.List;

public class EmailSchedule {

	private long id;
	private List<Integer> hours;
	private List<Integer> days;
	private int minute;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public List<Integer> getHours() {
		return hours;
	}

	public void setHours(List<Integer> hours) {
		this.hours = hours;
	}

	public List<Integer> getDays() {
		return days;
	}

	public void setDays(List<Integer> days) {
		this.days = days;
	}

	public int getMinute() {
		return minute;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}
}
