package com.firstrain.frapi.domain;

public class MonitorInfo {

	private String monitorId;
	private String monitorName;
	private boolean favorite;
	private boolean monitorAdmin;
	private Long favoriteUserItemId;
	private Long ownedBy;
	private String ownedByType;
	private Integer itemCount;
	private Boolean managementTurnoverAvailable = Boolean.FALSE;
	private Boolean eventAvailable = Boolean.FALSE;
	private Boolean tweetAvailable = Boolean.FALSE;
	private Boolean mailAvailable;
	private Boolean mailBadge = Boolean.FALSE;
	private Boolean hasNew = Boolean.FALSE;

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

	public boolean isFavorite() {
		return favorite;
	}

	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}

	public boolean isMonitorAdmin() {
		return monitorAdmin;
	}

	public void setMonitorAdmin(boolean monitorAdmin) {
		this.monitorAdmin = monitorAdmin;
	}

	public Long getFavoriteUserItemId() {
		return favoriteUserItemId;
	}

	public void setFavoriteUserItemId(Long favoriteUserItemId) {
		this.favoriteUserItemId = favoriteUserItemId;
	}

	public Integer getItemCount() {
		return itemCount;
	}

	public void setItemCount(Integer itemCount) {
		this.itemCount = itemCount;
	}

	public Boolean getManagementTurnoverAvailable() {
		return managementTurnoverAvailable;
	}

	public void setManagementTurnoverAvailable(Boolean managementTurnoverAvailable) {
		this.managementTurnoverAvailable = managementTurnoverAvailable;
	}

	public Boolean getEventAvailable() {
		return eventAvailable;
	}

	public void setEventAvailable(Boolean eventAvailable) {
		this.eventAvailable = eventAvailable;
	}

	public Boolean getTweetAvailable() {
		return tweetAvailable;
	}

	public void setTweetAvailable(Boolean tweetAvailable) {
		this.tweetAvailable = tweetAvailable;
	}

	public Boolean getMailAvailable() {
		return mailAvailable;
	}

	public void setMailAvailable(Boolean mailAvailable) {
		this.mailAvailable = mailAvailable;
	}

	public Boolean getMailBadge() {
		return mailBadge;
	}

	public void setMailBadge(Boolean mailBadge) {
		this.mailBadge = mailBadge;
	}

	public Boolean getHasNew() {
		return hasNew;
	}

	public void setHasNew(Boolean hasNew) {
		this.hasNew = hasNew;
	}

	public Long getOwnedBy() {
		return ownedBy;
	}

	public void setOwnedBy(Long ownedBy) {
		this.ownedBy = ownedBy;
	}

	public String getOwnedByType() {
		return ownedByType;
	}

	public void setOwnedByType(String ownedByType) {
		this.ownedByType = ownedByType;
	}

	public static class MonitorItems {
		public long itemId;
		public String itemName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((monitorId == null) ? 0 : monitorId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MonitorInfo other = (MonitorInfo) obj;
		if (monitorId == null) {
			if (other.monitorId != null)
				return false;
		} else if (!monitorId.equals(other.monitorId))
			return false;
		return true;
	}

}
