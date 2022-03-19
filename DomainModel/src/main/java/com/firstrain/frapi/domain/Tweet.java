package com.firstrain.frapi.domain;

import java.net.URLEncoder;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonIgnore;

import com.firstrain.frapi.pojo.Entity;
import com.firstrain.frapi.pojo.wrapper.DocumentSet;



public class Tweet extends BaseItem {

	private final Logger LOG = Logger.getLogger(Tweet.class);

	private long itemId;
	private boolean isBookmarked;
	private String tweetId;
	private Collection<Integer> companyIds;
	private Collection<Integer> topicIdCoreTweet;
	private String description;
	private Collection<String> expandedLinks;
	private Date tweetCreationDate;
	private Collection<String> links;
	private String name;
	private String screenName;
	private String tweet;
	private String coreTweet;
	private String tweetClass;
	private int comboScore;
	private int scope;
	private String userImage;
	private Collection<String> sources;
	private long groupId;
	private boolean groupLead;
	private Entity entity;
	private int groupSize;
	private String timeLabel;
	private DocumentSet relatedDocument;
	private DocumentSet document;
	private List<Tweet> tweetUsers;
	private List<Entity> industries;

	public MetaShare getVisibility() {
		return visibility;
	}

	public void setVisibility(MetaShare visibility) {
		this.visibility = visibility;
	}

	public int getComboScore() {
		return comboScore;
	}

	public void setComboScore(int comboScore) {
		this.comboScore = comboScore;
	}

	public int getScope() {
		return scope;
	}

	public void setScope(int scope) {
		this.scope = scope;
	}

	public String getTweetId() {
		return tweetId;
	}

	public void setTweetId(String tweetId) {
		this.tweetId = tweetId;
	}

	public Collection<Integer> getCompanyIds() {
		return companyIds;
	}

	public void setCompanyIds(Collection<Integer> companyIds) {
		this.companyIds = companyIds;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Collection<String> getExpandedLinks() {
		return expandedLinks;
	}

	public void setExpandedLinks(Collection<String> expandedLinks) {
		this.expandedLinks = expandedLinks;
	}

	public Date getTweetCreationDate() {
		return tweetCreationDate;
	}

	public void setTweetCreationDate(Date tweetCreationDate) {
		this.tweetCreationDate = tweetCreationDate;
	}

	public Collection<String> getLinks() {
		return links;
	}

	public void setLinks(Collection<String> links) {
		this.links = links;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getScreenName() {
		return screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public String getTweet() {
		return tweet;
	}

	public void setTweet(String tweet) {
		this.tweet = tweet;
	}

	public String getCoreTweet() {
		return coreTweet;
	}

	public void setCoreTweet(String coreTweet) {
		this.coreTweet = coreTweet;
	}

	public String getTweetClass() {
		return tweetClass;
	}

	public void setTweetClass(String tweetClass) {
		this.tweetClass = tweetClass;
	}

	public String getUserImage() {
		return userImage;
	}

	public void setUserImage(String userImage) {
		this.userImage = userImage;
	}

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	public Collection<String> getSources() {
		return sources;
	}

	public void setSources(Collection<String> sources) {
		this.sources = sources;
	}

	public long getGroupId() {
		return groupId;
	}

	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}

	public boolean isGroupLead() {
		return groupLead;
	}

	public void setGroupLead(boolean groupLead) {
		this.groupLead = groupLead;
	}

	public int getGroupSize() {
		return groupSize;
	}

	public void setGroupSize(int groupSize) {
		this.groupSize = groupSize;
	}

	public String getTimeLabel() {
		return timeLabel;
	}

	public void setTimeLabel(String timeLabel) {
		this.timeLabel = timeLabel;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public Collection<Integer> getTopicIdCoreTweet() {
		return topicIdCoreTweet;
	}

	public void setTopicIdCoreTweet(Collection<Integer> topicIdCoreTweet) {
		this.topicIdCoreTweet = topicIdCoreTweet;
	}

	public long getItemId() {
		return itemId;
	}

	public void setItemId(long itemId) {
		this.itemId = itemId;
	}

	public boolean isBookmarked() {
		return isBookmarked;
	}

	public void setBookmarked(boolean isBookmarked) {
		this.isBookmarked = isBookmarked;
	}

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	public DocumentSet getRelatedDocument() {
		return relatedDocument;
	}

	public void setRelatedDocument(DocumentSet relatedDocument) {
		this.relatedDocument = relatedDocument;
	}

	public DocumentSet getDocument() {
		return document;
	}

	public void setDocument(DocumentSet document) {
		this.document = document;
	}

	public List<Tweet> getTweetUsers() {
		return tweetUsers;
	}

	public void setTweetUsers(List<Tweet> tweetUsers) {
		this.tweetUsers = tweetUsers;
	}

	public List<Entity> getIndustries() {
		return industries;
	}

	public void setIndustries(List<Entity> industries) {
		this.industries = industries;
	}


	private static String TWITTER_URL = "https://twitter.com/";
	// private static String TWITTER_URL_MOBILE = "https://mobile.twitter.com/";
	private static String TW_SEARCHURL = TWITTER_URL + "search/?q=";

	private static String tagFrag = "[a-zA-Z0-9+_-]";
	private static Pattern rtTag = Pattern.compile("(?>[�\t]*RT[: \t]*[@]" + tagFrag + "+[�\t]*[:]*[�\t]*)");
	private static Pattern hashTag = Pattern.compile("(?>[�\t]*[#]" + tagFrag + "+[�\t]*[:]*[�\t]*)");
	private static Pattern atTag = Pattern.compile("(?>[�\t]*[@]" + tagFrag + "+[�\t]*[:]*[�\t]*)");
	private static Pattern dollarTag = Pattern.compile("(?>[�\t]*[\\$](?=[A-Za-z])" + tagFrag + "+[�\t]*[:]*[�\t]*)");

	@JsonIgnore
	public String getTweetTitlePlain() {
		return getTweetTitleHelper(true, false);
	}

	@JsonIgnore
	public String getTweetTitle(boolean inlineStyle) {
		return getTweetTitleHelper(false, inlineStyle);
	}
	
	private String getTweetTitleHelper(boolean isPlain, boolean inlineStyle) {
		String tweetTitle = tweet;
		try {
			if (coreTweet != null) {
				if (isPlain) {
					tweetTitle = coreTweet;
				} else {
					tweetTitle = tweetTitle.replace(coreTweet, ("<span>") + coreTweet + "</span>");
				}
			}
			Matcher m = hashTag.matcher(tweetTitle);
			StringBuffer sb = new StringBuffer();
			while (m.find()) {
				String tag = m.group().trim();
				if (isPlain) {
					m.appendReplacement(sb,
							" <a href='" + TW_SEARCHURL + URLEncoder.encode(tag, "UTF-8") + "'>" + tag + "</a>");
				} else {
					m.appendReplacement(sb, " <a href='" + TW_SEARCHURL + URLEncoder.encode(tag, "UTF-8") + "'>"
							+ ("<span class='hash-tag'>") + tag + "</span></a>");
				}
			}
			m.appendTail(sb);
			tweetTitle = sb.toString();

			if (links != null && !links.isEmpty()) {
				for (String link : links) {
					String link1;
					if (isPlain) {
						link1 = "<a href='" + link + "'>" + link + "</a>";
					} else {
						link1 = "<a "
								+ (inlineStyle ? " style='margin-top:0;font-size:.875em;color:#999'" : " class='meta'")
								+ " href='" + link + "'>" + link + "</a>";
					}
					tweetTitle = tweetTitle.replaceAll(link, link1);
				}
			}

			m = rtTag.matcher(tweetTitle);
			sb = new StringBuffer();
			while (m.find()) {
				String tag = m.group();
				appendReplacement(isPlain, m, sb, tag, "<span class='rt-tag'>");
			}
			m.appendTail(sb);
			tweetTitle = sb.toString();

			m = atTag.matcher(tweetTitle);
			sb = new StringBuffer();
			while (m.find()) {
				String tag = m.group();
				String personName = tag.substring(1);
				if (personName.endsWith(":")) {
					personName = personName.substring(0, personName.length() - 1);
				}
				personName = URLEncoder.encode(personName, "UTF-8");
				if (isPlain) {
					m.appendReplacement(sb, "<a href='" + TWITTER_URL + personName + "'>" + tag + "</a>");
				} else {
					m.appendReplacement(sb, "<a href='" + TWITTER_URL + personName + "'>" + ("<span class='a-tag'>")
							+ tag + "</span></a>");
				}
			}
			m.appendTail(sb);
			tweetTitle = sb.toString();

			m = dollarTag.matcher(tweetTitle);
			sb = new StringBuffer();
			while (m.find()) {
				String tag = m.group();
				tag = "\\" + tag;
				appendReplacement(isPlain, m, sb, tag, "<span class='dollar-tag'>");
			}
			m.appendTail(sb);
			tweetTitle = sb.toString();
		} catch (Exception e) {
			LOG.error("Error while constructing tweet title:" + tweetTitle, e);
		}
		return tweetTitle;
	}

	private void appendReplacement(final boolean isPlain, final Matcher m, final StringBuffer sb, final String tag, final String spanTag) {
		if (isPlain) {
			m.appendReplacement(sb, tag);
		} else {
			m.appendReplacement(sb, (spanTag) + tag + "</span>");
		}
	}
}
