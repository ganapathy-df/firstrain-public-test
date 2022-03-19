package com.firstrain.web.pojo;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EntityStandard {

	private String name;
	private String searchToken;
	private String relevanceBand;
	private Short relevanceScore;
	private String confidence;
	private String matchType;
	private Boolean removed;
	private Boolean added;
	private Boolean subscribed;
	private String message;
	private Integer errorCode;
	private com.firstrain.frapi.pojo.Entity sector;
	private com.firstrain.frapi.pojo.Entity segment;
	private com.firstrain.frapi.pojo.Entity industry;
	private String homePage;
	private List<EntityLink> entityLinks;
	private Boolean primaryDUNSMatch;
	private Boolean additionalMatchQualifier;
}
