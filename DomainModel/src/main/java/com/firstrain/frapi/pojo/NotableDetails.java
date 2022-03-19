package com.firstrain.frapi.pojo;

import java.util.List;

/**
 * @author GKhanchi
 */

public class NotableDetails {
	private String tweetId;
	private List<NotableDetail> notableDetails;

	public String getTweetId() {
		return tweetId;
	}

	public void setTweetId(String tweetId) {
		this.tweetId = tweetId;
	}

	public List<NotableDetail> getNotableDetails() {
		return notableDetails;
	}

	public void setNotableDetails(List<NotableDetail> notableDetails) {
		this.notableDetails = notableDetails;
	}

	public static class NotableDetail {
		private String userImage;
		private String screenName;
		private String profileUrl;

		public String getUserImage() {
			return userImage;
		}

		public void setUserImage(String userImage) {
			this.userImage = userImage;
		}

		public String getScreenName() {
			return screenName;
		}

		public void setScreenName(String screenName) {
			this.screenName = screenName;
		}

		public String getProfileUrl() {
			return profileUrl;
		}

		public void setProfileUrl(String profileUrl) {
			this.profileUrl = profileUrl;
		}
	}
}
