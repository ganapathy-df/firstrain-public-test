package com.firstrain.frapi.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.firstrain.frapi.pojo.Entity;
import com.firstrain.frapi.util.CompanyTeam;
import com.firstrain.frapi.util.ContentType;
import com.firstrain.utils.FR_StringUtils;

public class Document extends BaseItem implements Serializable {

	private static final long serialVersionUID = 2714952009227294295L;
	private long itemId;
	private String url;
	private Date date;
	private String summary;
	private Long groupId;
	private Entity source;
	private String image;
	private String favicon;
	private String companyName;
	private String secFormType;
	private boolean bookmarked = false;
	private boolean loginRequired;
	private int dupCount;
	private int score;
	private DocQuote docQuote;
	private CompanyTeam companyTeam;
	private Date insertTime;
	private Integer personCatId;
	private Integer similarQuotesCount;
	private String personImageUrl;
	private Boolean companyPeople;
	private List<Tweet> relatedTweets;
	private Entity matchedCriticalEvent;
	private List<Entity> catEntries;
	private List<Entity> roleEntities;
	private List<DocNgrams> ngrams;
	private String primaryDunsMatchStr;
	private String additionalMatchQualifierStr;

	public List<Entity> getCatEntries() {
		return catEntries;
	}

	public void setCatEntries(List<Entity> catEntries) {
		this.catEntries = catEntries;
	}

	public Integer getSlot() {
		return slot;
	}

	public void setSlot(Integer slot) {
		this.slot = slot;
	}

	public Date getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(Date insertTime) {
		this.insertTime = insertTime;
	}

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	public CompanyTeam getCompanyTeam() {
		return companyTeam;
	}

	public void setCompanyTeam(CompanyTeam companyTeam) {
		this.companyTeam = companyTeam;
	}

	public Entity getPrimaryEntity() {
		return primaryEntity;
	}

	public void setPrimaryEntity(Entity primaryEntity) {
		this.primaryEntity = primaryEntity;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getSecFormType() {
		return secFormType;
	}

	public void setSecFormType(String secFormType) {
		this.secFormType = secFormType;
	}

	public int getDupCount() {
		return dupCount;
	}

	public void setDupCount(int dupCount) {
		this.dupCount = dupCount;
	}

	public MetaShare getVisibility() {
		return visibility;
	}

	public void setVisibility(MetaShare visibility) {
		this.visibility = visibility;
	}

	public static class DocQuote implements Serializable {

		private static final long serialVersionUID = 4686013762698435708L;

		// picked from FR_IQuote
		char QUOTE_MARKER_CHAR = 0x25ab;
		String QUOTE_MARKER = " " + QUOTE_MARKER_CHAR + " ";
		public static final char markerChar = 0x25aa;
		public static final String SENTENCE_MARKER = " " + markerChar + " ";

		private String text;
		private String person;

		public DocQuote() {

		}

		public DocQuote(String quoteStr, String quoteStrPerson) {
			text = quoteStr;
			person = quoteStrPerson;
		}

		@JsonIgnore
		public String getHighlightedQuote() {
			String contextBody = FR_StringUtils.replace(this.text, SENTENCE_MARKER, "", false);

			int firstOccur = contextBody.indexOf(QUOTE_MARKER);
			if (firstOccur > 0) {
				if (contextBody.charAt(firstOccur - 1) == ' ' || contextBody.charAt(firstOccur + 1) == ' ') {
					contextBody = FR_StringUtils.replace(contextBody, QUOTE_MARKER, "<b>", false, 1);
				} else {
					contextBody = FR_StringUtils.replace(contextBody, QUOTE_MARKER, " <b>", false, 1);
				}
			}
			int lastOccur = contextBody.indexOf(QUOTE_MARKER);
			if (lastOccur > 0 && lastOccur < (contextBody.length() - 1)) {
				if (contextBody.charAt(lastOccur - 1) == ' ' || contextBody.charAt(lastOccur + 1) == ' ') {
					contextBody = FR_StringUtils.replace(contextBody, QUOTE_MARKER, "</b>", false);
				} else {
					contextBody = FR_StringUtils.replace(contextBody, QUOTE_MARKER, "</b> ", false);
				}
			}
			return contextBody;
		}

		public void setText(String text) {
			this.text = text;
		}

		public void setPerson(String person) {
			this.person = person;
		}

		public String getText() {
			return text;
		}

		public String getPerson() {
			return person;
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public Entity getSource() {
		return source;
	}

	public void setSource(Entity source) {
		this.source = source;
	}

	public List<Entity> getMatchedCompanies() {
		return matchedCompanies;
	}

	public void setMatchedCompanies(List<Entity> matchedCompanies) {
		this.matchedCompanies = matchedCompanies;
	}

	public List<Entity> getMatchedTopics() {
		return matchedTopics;
	}

	public void setMatchedTopics(List<Entity> matchedTopics) {
		this.matchedTopics = matchedTopics;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getFavicon() {
		return favicon;
	}

	public void setFavicon(String favicon) {
		this.favicon = favicon;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public long getItemId() {
		return itemId;
	}

	public void setItemId(long itemId) {
		this.itemId = itemId;
	}

	public boolean isBookmarked() {
		return bookmarked;
	}

	public void setBookmarked(boolean bookmarked) {
		this.bookmarked = bookmarked;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public DocQuote getDocQuote() {
		return docQuote;
	}

	public void setDocQuote(DocQuote docQuote) {
		this.docQuote = docQuote;
	}

	public ContentType getContentType() {
		return contentType;
	}

	public void setContentType(ContentType contentType) {
		this.contentType = contentType;
	}

	public boolean isLoginRequired() {
		return loginRequired;
	}

	public void setLoginRequired(boolean loginRequired) {
		this.loginRequired = loginRequired;
	}

	public Integer getPersonCatId() {
		return personCatId;
	}

	public void setPersonCatId(Integer personCatId) {
		this.personCatId = personCatId;
	}

	public Integer getSimilarQuotesCount() {
		return similarQuotesCount;
	}

	public void setSimilarQuotesCount(Integer similarQuotesCount) {
		this.similarQuotesCount = similarQuotesCount;
	}

	public String getPersonImageUrl() {
		return personImageUrl;
	}

	public void setPersonImageUrl(String personImageUrl) {
		this.personImageUrl = personImageUrl;
	}

	public Boolean getCompanyPeople() {
		return companyPeople;
	}

	public void setCompanyPeople(Boolean companyPeople) {
		this.companyPeople = companyPeople;
	}

	public List<Tweet> getRelatedTweets() {
		return relatedTweets;
	}

	public void setRelatedTweets(List<Tweet> relatedTweets) {
		this.relatedTweets = relatedTweets;
	}

	public Entity getMatchedCriticalEvent() {
		return matchedCriticalEvent;
	}

	public void setMatchedCriticalEvent(Entity matchedCriticalEvent) {
		this.matchedCriticalEvent = matchedCriticalEvent;
	}

	public List<Entity> getRoleEntities() {
		return roleEntities;
	}

	public void setRoleEntities(List<Entity> roleEntities) {
		this.roleEntities = roleEntities;
	}

	public List<DocNgrams> getNgrams() {
		return ngrams;
	}

	public void setNgrams(List<DocNgrams> ngrams) {
		this.ngrams = ngrams;
	}

	public String getPrimaryDunsMatchStr() {
		return primaryDunsMatchStr;
	}

	public void setPrimaryDunsMatchStr(String primaryDunsMatchStr) {
		this.primaryDunsMatchStr = primaryDunsMatchStr;
	}

	public String getAdditionalMatchQualifierStr() {
		return additionalMatchQualifierStr;
	}

	public void setAdditionalMatchQualifierStr(String additionalMatchQualifierStr) {
		this.additionalMatchQualifierStr = additionalMatchQualifierStr;
	}
}
