package com.firstrain.frapi.domain;

import com.firstrain.frapi.pojo.Entity;

public class EntityStatus {

	private String name;
	private String id;
	private Entity entity;
	boolean entityStatus;

	public boolean getEntityStatus() {
		return entityStatus;
	}

	public void setEntityStatus(boolean entityStatus) {
		this.entityStatus = entityStatus;
	}

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
