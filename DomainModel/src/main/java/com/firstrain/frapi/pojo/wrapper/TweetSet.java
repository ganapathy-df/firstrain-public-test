package com.firstrain.frapi.pojo.wrapper;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.firstrain.frapi.domain.Tweet;
import com.firstrain.frapi.util.DefaultEnums.DateBucketingMode;


public class TweetSet extends BaseSet {

	private int total;

	private Collection<String> tweetIds;
	private List<Tweet> tweets;
	private Map<String, List<Tweet>> bucketedTweets;
	private DateBucketingMode bucketMode;
	private Boolean primaryIndustry;
	private Integer scope;
	private GraphNodeSet tweetAccelerometer;

	public TweetSet() {}

	public TweetSet(SectionType type) {
		super(type);
	}

	public TweetSet(List<Tweet> tweets, int total, SectionType type) {
		super(type);
		this.tweets = tweets;
		this.total = total;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<Tweet> getTweets() {
		return tweets;
	}

	public void setTweets(List<Tweet> tweets) {
		this.tweets = tweets;
	}

	@JsonIgnore
	public Collection<String> getTweetIds() {
		return tweetIds;
	}

	public void setTweetIds(Collection<String> tweetIds) {
		this.tweetIds = tweetIds;
	}

	public Map<String, List<Tweet>> getBucketedTweets() {
		return bucketedTweets;
	}

	public void setBucketedTweets(Map<String, List<Tweet>> bucketedTweets) {
		this.bucketedTweets = bucketedTweets;
	}

	public DateBucketingMode getBucketMode() {
		return bucketMode;
	}

	public void setBucketMode(DateBucketingMode bucketMode) {
		this.bucketMode = bucketMode;
	}

	public Boolean getPrimaryIndustry() {
		return primaryIndustry;
	}

	public void setPrimaryIndustry(Boolean primaryIndustry) {
		this.primaryIndustry = primaryIndustry;
	}

	public Integer getScope() {
		return scope;
	}

	public void setScope(Integer scope) {
		this.scope = scope;
	}

	public GraphNodeSet getTweetAccelerometer() {
		return tweetAccelerometer;
	}

	public void setTweetAccelerometer(GraphNodeSet tweetAccelerometer) {
		this.tweetAccelerometer = tweetAccelerometer;
	}

	public static class TweetGroup {
		private int comboScore;
		private Collection<Integer> affinityScoreNormalized;
		private Collection<Integer> affinityScope;
		private Date creationTime;
		private long groupId;
		private int groupSize;
		private byte scope;
		private long tweetId;
		private Collection<Integer> catId;

		public int getComboScore() {
			return comboScore;
		}

		public void setComboScore(int comboScore) {
			this.comboScore = comboScore;
		}

		public Date getCreationTime() {
			return creationTime;
		}

		public void setCreationTime(Date creationTime) {
			this.creationTime = creationTime;
		}

		public long getGroupId() {
			return groupId;
		}

		public void setGroupId(long groupId) {
			this.groupId = groupId;
		}

		public int getGroupSize() {
			return groupSize;
		}

		public void setGroupSize(int groupSize) {
			this.groupSize = groupSize;
		}

		public byte getScope() {
			return scope;
		}

		public void setScope(byte scope) {
			this.scope = scope;
		}

		public long getTweetId() {
			return tweetId;
		}

		public void setTweetId(long tweetId) {
			this.tweetId = tweetId;
		}

		public Collection<Integer> getCatId() {
			return catId;
		}

		public void setCatId(Collection<Integer> catId) {
			this.catId = catId;
		}

		public Collection<Integer> getAffinityScoreNormalized() {
			return affinityScoreNormalized;
		}

		public void setAffinityScoreNormalized(Collection<Integer> affinityScoreNormalized) {
			this.affinityScoreNormalized = affinityScoreNormalized;
		}

		public Collection<Integer> getAffinityScope() {
			return affinityScope;
		}

		public void setAffinityScope(Collection<Integer> affinityScope) {
			this.affinityScope = affinityScope;
		}
	}
}
