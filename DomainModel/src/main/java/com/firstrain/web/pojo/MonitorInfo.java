package com.firstrain.web.pojo;

import java.util.List;

import com.firstrain.web.wrapper.EntityWrapper;

/**
 * @author vgoyal
 *
 */

public class MonitorInfo {

	private String monitorId;
	private String monitorName;
	private Boolean favorite;
	private Boolean monitorAdmin;
	private Long favoriteItemId;
	private Boolean activeMail;
	private List<EntityWrapper> entities;

	public String getMonitorId() {
		return monitorId;
	}

	public void setMonitorId(String monitorId) {
		this.monitorId = monitorId;
	}

	public String getMonitorName() {
		return monitorName;
	}

	public void setMonitorName(String monitorName) {
		this.monitorName = monitorName;
	}

	public Boolean isFavorite() {
		return favorite;
	}

	public void setFavorite(Boolean favorite) {
		this.favorite = favorite;
	}

	public Boolean isMonitorAdmin() {
		return monitorAdmin;
	}

	public void setMonitorAdmin(Boolean monitorAdmin) {
		this.monitorAdmin = monitorAdmin;
	}

	public Long getFavoriteItemId() {
		return favoriteItemId;
	}

	public void setFavoriteItemId(Long favoriteItemId) {
		this.favoriteItemId = favoriteItemId;
	}

	public Boolean getActiveMail() {
		return activeMail;
	}

	public void setActiveMail(Boolean activeMail) {
		this.activeMail = activeMail;
	}

	public List<EntityWrapper> getEntities() {
		return entities;
	}

	public void setEntities(List<EntityWrapper> entities) {
		this.entities = entities;
	}
}
