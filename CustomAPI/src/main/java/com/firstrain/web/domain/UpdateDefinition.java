package com.firstrain.web.domain;

import java.sql.Timestamp;
import java.util.List;

import com.firstrain.db.obj.EntityHistory;
import com.firstrain.db.obj.PrivateEntity;

public class UpdateDefinition {

	private List<PrivateEntity> privateEntities;
	private List<EntityHistory> entityHistory;
	private Timestamp updateTime;

	public Timestamp getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}

	public List<PrivateEntity> getPrivateEntities() {
		return privateEntities;
	}

	public void setPrivateEntities(List<PrivateEntity> privateEntities) {
		this.privateEntities = privateEntities;
	}

	public List<EntityHistory> getEntityHistory() {
		return entityHistory;
	}

	public void setEntityHistory(List<EntityHistory> entityHistory) {
		this.entityHistory = entityHistory;
	}
}
