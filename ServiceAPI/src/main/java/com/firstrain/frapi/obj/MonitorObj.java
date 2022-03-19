package com.firstrain.frapi.obj;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.firstrain.db.obj.Items;
import com.firstrain.solr.client.DocCatEntry;
import com.firstrain.solr.client.EntityEntry;
import com.firstrain.solr.client.HotListEntry;

/**
 * @author Akanksha
 * 
 */

public class MonitorObj {

	public static enum Type {
		TYPE_REPORT,
		TYPE_SECTOR,
		TYPE_SEGMENT,
		TYPE_INDUSTRY,
		TYPE_ARTICLE,
		TYPE_TOPIC,
		TYPE_SOURCE,
		TYPE_COMPANY,
		TYPE_EVENT,
		TYPE_INDEX,
		TYPE_BUSINESS_LINE,
		TYPE_DOCUMENT,
		TYPE_INDEX_MEMBERSHIP,
		TYPE_TURNOVER_DEPARTURE,
		TYPE_TURNOVER_HIRE,
		TYPE_TURNOVER_INTERNAL_MOVE,
		TYPE_DEFAULT,
		TYPE_FILING,
		TYPE_FILING_10K,
		TYPE_FILING_10Q,
		TYPE_FILING_345,
		TYPE_FILING_8K,
		TYPE_PERSON,
		TYPE_MGMT_TURNOVER,
		TYPE_REGION,
		TYPE_CONTENT_TYPE,
		TYPE_MONITOR,
		TYPE_SECTOR_TOPIC;
	}

	private String url;
	private String title;
	private Type type;
	private boolean include, exclude, selected;
	private int documentCount;
	private Token token;
	private List<Token> tokenList;
	private Timestamp date;
	private String id;
	private boolean bizLines;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDefaultURL() {
		return url;
	}

	public void setDefaultURL(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public List<Token> getTokenList() {
		return tokenList;
	}

	public void setTokenList(List<Token> tokenList) {
		this.tokenList = tokenList;
	}

	public Timestamp getDate() {
		return date;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}

	public boolean isInclude() {
		return include;
	}

	public void setInclude(boolean include) {
		this.include = include;
	}

	public boolean isExclude() {
		return exclude;
	}

	public void setExclude(boolean exclude) {
		this.exclude = exclude;
	}

	public Token getToken() {
		return token;
	}

	public void setToken(Token token) {
		this.token = token;
	}

	public int getDocumentCount() {
		return documentCount;
	}

	public void setDocumentCount(int documentCount) {
		this.documentCount = documentCount;
	}

	public void update(HotListEntry entry) {
		documentCount = entry.docCount;
		update(entry.entity);
	}

	public void update(EntityEntry entry) {
		exclude = entry.inNegativeFQ;
		include = entry.inPositiveFQ;
		selected = (entry.inNegativeFQ || entry.inPositiveFQ);
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isBizLines() {
		return bizLines;
	}

	public void setBizLines(boolean bizLines) {
		this.bizLines = bizLines;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MonitorObj) {
			if (getTitle() != null && getDefaultURL() == null)
				return getTitle().equals(((MonitorObj) obj).getTitle());
			else if (getTitle() != null && getDefaultURL() != null)
				return (getTitle().equals(((MonitorObj) obj).getTitle()) && getDefaultURL().equals(((MonitorObj) obj).getDefaultURL()));
		}
		return super.equals(obj);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		String objectProperties = getTitle() + getDefaultURL();
		return objectProperties.hashCode();
	}

	public MonitorObj() {

	}

	public MonitorObj(Items folder) {
		String folderId = String.valueOf(folder.getId());
		setTitle(folder.getName());
		setDefaultURL(folderId);
		Token token = new Token(folder.getData(), folder.getName());
		setToken(token);
	}

	public MonitorObj(DocCatEntry docEntry) {
		this(docEntry.entity);
	}

	public MonitorObj(EntityEntry entry) {
		setTitle(entry.getName());
		exclude = entry.inNegativeFQ;
		include = entry.inPositiveFQ;
		setDefaultURL(entry.getSearchToken());
		selected = (entry.inNegativeFQ || entry.inPositiveFQ);
	}

	public MonitorObj(HotListEntry entry) {
		this(entry.getEntity());
		documentCount = entry.docCount;
	}

	public MonitorObj(String companyName, String companyToken) {
		setTitle(companyName);
		setDefaultURL(companyToken);
	}

	public static class SearchResponse {
		public List<MonitorObj> duplicateSearches = new ArrayList<MonitorObj>();
		public List<MonitorObj> addedSearches = new ArrayList<MonitorObj>();

		public List<MonitorObj> getDuplicateSearches() {
			return duplicateSearches;
		}

		public List<MonitorObj> getAddedSearches() {
			return addedSearches;
		}
	}

	public static class TopicNBL {

		String search;
		int docCount;
		List<Integer> blList = null;
		List<Integer> topicList = null;
		Set<Integer> segmentList = null;

		public String getSearch() {
			return search;
		}

		public void setSearch(String search) {
			this.search = search;
		}

		public int getDocCount() {
			return docCount;
		}

		public void setDocCount(int docCount) {
			this.docCount = docCount;
		}

		public List<Integer> getBlList() {
			return blList;
		}

		public void setBlList(List<Integer> blList) {
			this.blList = blList;
		}

		public List<Integer> getTopicList() {
			return topicList;
		}

		public void setTopicList(List<Integer> topicList) {
			this.topicList = topicList;
		}

		public Set<Integer> getSegmentList() {
			return segmentList;
		}

		public void setSegmentList(Set<Integer> segmentList) {
			this.segmentList = segmentList;
		}
	}

	public static class RelatedInfo {
		public boolean company, topic, industry, people, region, bizLine;
		public List<MonitorObj> companies = new ArrayList<MonitorObj>();
		public List<MonitorObj> topics = new ArrayList<MonitorObj>();
		public List<MonitorObj> industries = new ArrayList<MonitorObj>();
		public List<MonitorObj> peoples = new ArrayList<MonitorObj>();
		public List<MonitorObj> regions = new ArrayList<MonitorObj>();
		public List<MonitorObj> bizLines = new ArrayList<MonitorObj>();
		public long docCount;

		public RelatedInfo(boolean company, boolean topic, boolean industry, boolean people, boolean region, boolean bizLine) {
			this.company = company;
			this.topic = topic;
			this.industry = industry;
			this.people = people;
			this.region = region;
			this.bizLine = bizLine;
		}

		public List<MonitorObj> getPeoples() {
			return peoples;
		}

		public void setPeoples(List<MonitorObj> peoples) {
			this.peoples = peoples;
		}

		public List<MonitorObj> getCompanies() {
			return companies;
		}

		public List<MonitorObj> getTopics() {
			return topics;
		}

		public List<MonitorObj> getIndustries() {
			return industries;
		}

		public List<MonitorObj> getRegions() {
			return regions;
		}

		public List<MonitorObj> getBizLines() {
			return bizLines;
		}
	}
}


