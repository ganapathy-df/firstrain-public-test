package com.firstrain.frapi.pojo.wrapper;

import java.util.List;

import com.firstrain.frapi.pojo.Entity;

public class EntityListJsonObject extends BaseSet {

	private List<Entity> entities;

	/**
	 * @return the entities
	 */
	public List<Entity> getEntities() {
		return entities;
	}

	/**
	 * @param entities the entities to set
	 */
	public void setEntities(List<Entity> entities) {
		this.entities = entities;
	}
}
