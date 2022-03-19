package com.firstrain.web.pojo;

import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.firstrain.frapi.pojo.wrapper.GraphNodeSet;

@XmlAccessorType(XmlAccessType.FIELD)
public class Content {
	private Integer totalItemCount;
	private Integer itemOffset;
	private Integer itemCount;

	@XmlElementWrapper(name = "documents")
	@XmlElements(@XmlElement(name = "document"))
	private List<Document> documents;

	@XmlElementWrapper(name = "tweets")
	@XmlElements(@XmlElement(name = "tweet"))
	private List<Tweet> tweets;

	private List<Event> events;
	private Map<String, List<Event>> eventBuckets;
	private Map<String, List<Document>> documentBuckets;
	private GraphNodeSet tweetAccelerometer;

	public List<Document> getDocuments() {
		return documents;
	}

	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}

	public List<Tweet> getTweets() {
		return tweets;
	}

	public void setTweets(List<Tweet> tweets) {
		this.tweets = tweets;
	}

	public Integer getTotalItemCount() {
		return totalItemCount;
	}

	public void setTotalItemCount(Integer totalItemCount) {
		this.totalItemCount = totalItemCount;
	}

	public Integer getItemOffset() {
		return itemOffset;
	}

	public void setItemOffset(Integer itemOffset) {
		this.itemOffset = itemOffset;
	}

	public Integer getItemCount() {
		return itemCount;
	}

	public void setItemCount(Integer itemCount) {
		this.itemCount = itemCount;
	}

	public List<Event> getEvents() {
		return events;
	}

	public void setEvents(List<Event> events) {
		this.events = events;
	}

	public Map<String, List<Event>> getEventBuckets() {
		return eventBuckets;
	}

	public void setEventBuckets(Map<String, List<Event>> eventBuckets) {
		this.eventBuckets = eventBuckets;
	}

	public Map<String, List<Document>> getDocumentBuckets() {
		return documentBuckets;
	}

	public void setDocumentBuckets(Map<String, List<Document>> documentBuckets) {
		this.documentBuckets = documentBuckets;
	}

	@JsonIgnore
	public GraphNodeSet getTweetAccelerometer() {
		return tweetAccelerometer;
	}

	public void setTweetAccelerometer(GraphNodeSet tweetAccelerometer) {
		this.tweetAccelerometer = tweetAccelerometer;
	}
}
