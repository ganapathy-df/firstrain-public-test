package com.firstrain.web.wrapper;

import com.firstrain.web.pojo.EntityStandard;

public class EntityWrapper {

	private String name;
	private String id;
	private EntityStandard entity;

	public EntityStandard getEntity() {
		return entity;
	}

	public void setEntity(EntityStandard entity) {
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
