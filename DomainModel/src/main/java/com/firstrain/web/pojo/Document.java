package com.firstrain.web.pojo;

import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.firstrain.frapi.domain.DocNgrams;

public class Document {

	private String id;
	private String timeStamp;
	private Date date;
	private String contentType;
	private Source source;
	private List<EntityStandard> entity;
	private List<String> conversationStarterType;
	private String title;
	private String snippet;
	private String sourceUrl;
	private String link;
	private Boolean bookmarked;
	private Long itemId;
	private String groupId;
	private String quotes;
	private String image;
	private List<DocNgrams> ngrams;
	private String primaryDUNSMatchStr;
	private String additionalMatchQualifierStr;

	public String getSnippet() {
		return snippet;
	}

	public void setSnippet(String snippet) {
		this.snippet = snippet;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	public List<EntityStandard> getEntity() {
		return entity;
	}

	public void setEntity(List<EntityStandard> entity) {
		this.entity = entity;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSourceUrl() {
		return sourceUrl;
	}

	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
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

	public String getQuotes() {
		return quotes;
	}

	public void setQuotes(String quotes) {
		this.quotes = quotes;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public List<String> getConversationStarterType() {
		return conversationStarterType;
	}

	public void setConversationStarterType(List<String> conversationStarterType) {
		this.conversationStarterType = conversationStarterType;
	}

	@JsonIgnore
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public List<DocNgrams> getNgrams() {
		return ngrams;
	}

	public void setNgrams(List<DocNgrams> ngrams) {
		this.ngrams = ngrams;
	}

	@JsonIgnore
	public String getPrimaryDUNSMatchStr() {
		return primaryDUNSMatchStr;
	}

	public void setPrimaryDUNSMatchStr(String primaryDUNSMatchStr) {
		this.primaryDUNSMatchStr = primaryDUNSMatchStr;
	}

	@JsonIgnore
	public String getAdditionalMatchQualifierStr() {
		return additionalMatchQualifierStr;
	}

	public void setAdditionalMatchQualifierStr(String additionalMatchQualifierStr) {
		this.additionalMatchQualifierStr = additionalMatchQualifierStr;
	}
}