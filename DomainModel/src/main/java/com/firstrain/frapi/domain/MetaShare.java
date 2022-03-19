package com.firstrain.frapi.domain;

import java.util.List;

public class MetaShare {
	private List<GroupShare> sharedWith;
	private List<UserOwner> sharedBy;

	public List<GroupShare> getSharedWith() {
		return sharedWith;
	}

	public void setSharedWith(List<GroupShare> sharedWith) {
		this.sharedWith = sharedWith;
	}

	public List<UserOwner> getSharedBy() {
		return sharedBy;
	}

	public void setSharedBy(List<UserOwner> sharedBy) {
		this.sharedBy = sharedBy;
	}

	/* Support Entities */
	public static class GroupShare {

		public String id;
		public String name;

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
	}

	/* Support Entities */
	public static class UserOwner {
		public String id;
		public String firstName;
		public String lastName;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getFirstName() {
			return firstName;
		}

		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}

		public String getLastName() {
			return lastName;
		}

		public void setLastName(String lastName) {
			this.lastName = lastName;
		}
	}
}
