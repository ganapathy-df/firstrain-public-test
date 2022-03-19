package com.firstrain.frapi.domain;

public class EntityDetailSpec {

	public int highlightCount = 10;
	public int catId = -1;

	public int relatedTweetCount = 2;
	public int tweetMatchedDocCount = 1;
	public int relatedDocCount = 2;

	public int relatedCompany = 3;
	public int relatedTopic = 3;

	public boolean twitter;
	public boolean ipad;

	public boolean includeCompany = true;
	public boolean includeEncoding = true;

	public boolean attachGroupInfo;
	public boolean attachAlsoTweetedBy = true;

	public boolean needSmartSummary = true;
	public boolean needRelatedDoc = true;
	public boolean needRelatedEvents = true;
	public boolean needExpandedLinksAndDocForTweet = true;

	public String language = "en";

	public boolean needPhrase;
}
