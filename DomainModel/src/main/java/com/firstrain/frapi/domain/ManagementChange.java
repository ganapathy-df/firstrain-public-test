package com.firstrain.frapi.domain;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ManagementChange {
	private String newCompany;
	private String oldCompany;
	private String newDesignation;
	private String oldDesignation;
	private String newTicker;
	private String oldTicker;
	private int newCompanyId;
	private int oldCompanyId;
	private String person;
	private String changeType;
	private String oldRegion;
	private String newRegion;
	private String oldGroup;
	private String newGroup;
	private Date date;
	private boolean linkable;
	private String url;
	private boolean futureEvent;
	private long trendId;

	public enum MgmtChangeType {
		HIRE, DEPARTURE, INTERNALMOVE
	}
}
