package com.firstrain.web.pojo;

/**
 * @author vgoyal
 *
 */

public class Monitor {

	private String id;
	private String name;
	private Boolean favorite;
	private Long favoriteItemId;
	private Boolean activeMail;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean isFavorite() {
		return favorite;
	}

	public void setFavorite(Boolean favorite) {
		this.favorite = favorite;
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


}
