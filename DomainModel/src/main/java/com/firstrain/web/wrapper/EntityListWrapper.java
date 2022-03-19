package com.firstrain.web.wrapper;

import java.util.List;

import com.firstrain.web.pojo.Entity;

public class EntityListWrapper {

	private List<Entity> entity;
	private List<Entity> entities;

	public List<Entity> getEntity() {
		return entity;
	}

	public void setEntity(List<Entity> entity) {
		this.entity = entity;
	}

	public List<Entity> getEntities() {
		return entities;
	}

	public void setEntities(List<Entity> entities) {
		this.entities = entities;
	}

}
