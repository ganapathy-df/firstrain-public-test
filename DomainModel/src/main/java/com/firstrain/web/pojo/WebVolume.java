package com.firstrain.web.pojo;


public class WebVolume {
	private String date;
	private Double value;
	private String stockprice;
	private Event event;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public String getStockprice() {
		return stockprice;
	}

	public void setStockprice(String stockprice) {
		this.stockprice = stockprice;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}
}
