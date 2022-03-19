package com.firstrain.web.pojo;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

public class Tweet {

	private String tweetId;
	private String timeStamp;
	private String timeLabel;
	private List<String> expandedLinks;
	private String authorName;
	private String userName;
	private EntityStandard entity;
	private List<EntityStandard> industry;
	private String title;
	private String tweetText;
	private String tweetHtml;
	private String authorDescription;
	private String authorAvatar;
	private List<String> link;
	private Boolean bookmarked;
	private Long itemId;
	private Integer groupSize;
	private Long groupId;

	public String getTweetId() {
		return tweetId;
	}

	public void setTweetId(String tweetId) {
		this.tweetId = tweetId;
	}

	public String getTimeLabel() {
		return timeLabel;
	}

	public void setTimeLabel(String timeLabel) {
		this.timeLabel = timeLabel;
	}

	public List<String> getExpandedLinks() {
		return expandedLinks;
	}

	public void setExpandedLinks(List<String> expandedLinks) {
		this.expandedLinks = expandedLinks;
	}

	public EntityStandard getEntity() {
		return entity;
	}

	public void setEntity(EntityStandard entity) {
		this.entity = entity;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTweetText() {
		return this.tweetText;
	}

	public void setTweetText(String tweetText) {
		this.tweetText = tweetText;
	}

	public List<String> getLink() {
		return link;
	}

	public void setLink(List<String> link) {
		this.link = link;
	}

	public Boolean getBookmarked() {
		return bookmarked;
	}

	public void setBookmarked(Boolean bookmarked) {
		this.bookmarked = bookmarked;
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@JsonIgnore
	public String getTweetHtml() {
		return tweetHtml;
	}

	public void setTweetHtml(String tweetText) {
		this.tweetHtml = tweetText;
	}

	public String getAuthorDescription() {
		return authorDescription;
	}

	public void setAuthorDescription(String authorDescription) {
		this.authorDescription = authorDescription;
	}

	public String getAuthorAvatar() {
		return authorAvatar;
	}

	public void setAuthorAvatar(String authorAvatar) {
		this.authorAvatar = authorAvatar;
	}

	@JsonIgnore
	public Integer getGroupSize() {
		return groupSize;
	}

	public void setGroupSize(Integer groupSize) {
		this.groupSize = groupSize;
	}

	@JsonIgnore
	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public List<EntityStandard> getIndustry() {
		return industry;
	}

	public void setIndustry(List<EntityStandard> industry) {
		this.industry = industry;
	}
}
