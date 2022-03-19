package com.firstrain.web.pojo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.firstrain.frapi.util.DefaultEnums.RelevanceBand;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Entity {

	private String id;
	private String name;
	private String searchToken;

	@Setter(AccessLevel.NONE)
	private String relevanceBand;
	private Short relevanceScore;

	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private List<Location> location = new ArrayList<Location>();
	private Boolean removed;
	private Boolean added;
	private Boolean subscribed;
	private String message;
	private Integer errorCode;

	public void setRelevanceBand(Short relevanceBand) {
		if (relevanceBand == null) {
			relevanceBand = RelevanceBand.LOW.getValue();
		}
		if (relevanceBand == RelevanceBand.HIGH.getValue()) {
			this.relevanceBand = RelevanceBand.HIGH.getName();
		} else if (relevanceBand == RelevanceBand.MEDIUM.getValue()) {
			this.relevanceBand = RelevanceBand.MEDIUM.getName();
		} else {
			this.relevanceBand = RelevanceBand.LOW.getName();
		}
	}

	public List<Location> getLocation() {
		return new ArrayList<Location>(location);
	}

	public void setLocation(List<Location> location) {
		if (location == null) {
			this.location = Collections.emptyList();
		} else {
			this.location = new ArrayList<Location>(location);
		}
	}
}
