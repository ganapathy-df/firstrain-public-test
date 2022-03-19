package com.firstrain.frapi.pojo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.firstrain.frapi.util.DefaultEnums.UserItemType;


public class UserItem {

	private String refersTo;
	private String id;
	private Timestamp insertTime;
	private List<Entity> ownerList;
	private List<Visibility> visibilities;
	private UserItemType type;

	public String getRefersTo() {
		return refersTo;
	}

	public void setRefersTo(String refersTo) {
		this.refersTo = refersTo;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Timestamp getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(Timestamp insertTime) {
		this.insertTime = insertTime;
	}

	public List<Entity> getOwnerList() {
		return ownerList;
	}

	public void setOwnerList(List<Entity> ownerList) {
		this.ownerList = ownerList;
	}

	public void addOwner(Entity e) {
		if (this.ownerList == null) {
			this.ownerList = new ArrayList<Entity>();
		}
		this.ownerList.add(e);
	}

	public List<Visibility> getVisibilities() {
		return visibilities;
	}

	public void setVisibilities(List<Visibility> visibilities) {
		this.visibilities = visibilities;
	}

	public UserItemType getType() {
		return type;
	}

	public void setType(UserItemType type) {
		this.type = type;
	}

	public static class SharedDetails {
		public List<UserItem> userItems;
		public int totalCount;
	}

	public static class VisibilityRes {
		public List<Visibility> shareWith;
		public List<Visibility> unShareWith;
	}

	public static class Visibility {
		private long id;
		private Entity viewer;
		private Entity owner;

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public Entity getViewer() {
			return viewer;
		}

		public void setViewer(Entity viewer) {
			this.viewer = viewer;
		}

		public Entity getOwner() {
			return owner;
		}

		public void setOwner(Entity owner) {
			this.owner = owner;
		}
	}
}
