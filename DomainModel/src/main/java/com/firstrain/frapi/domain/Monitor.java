package com.firstrain.frapi.domain;

import com.firstrain.frapi.util.DefaultEnums;

public class Monitor {

	private long id;
	private String name;
	private DefaultEnums.TagType type;
	private EmailSchedule emailSchedule;
	private boolean mailActive;
	private DefaultEnums.OwnedByType ownedByType;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DefaultEnums.TagType getType() {
		return type;
	}

	public void setType(DefaultEnums.TagType type) {
		this.type = type;
	}

	public EmailSchedule getEmailSchedule() {
		return emailSchedule;
	}

	public void setEmailSchedule(EmailSchedule emailSchedule) {
		this.emailSchedule = emailSchedule;
	}

	public boolean isMailActive() {
		return mailActive;
	}

	public void setMailActive(boolean mailActive) {
		this.mailActive = mailActive;
	}

	public DefaultEnums.OwnedByType getOwnedByType() {
		return ownedByType;
	}

	public void setOwnedByType(DefaultEnums.OwnedByType ownedByType) {
		this.ownedByType = ownedByType;
	}
}
