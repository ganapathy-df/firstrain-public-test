package com.firstrain.web.pojo;

import java.util.List;

import org.apache.commons.lang.ObjectUtils;

public class CategorizerObject {

	private int responseCode;
	private int count;
	private CatEntityWrapper data;

	public CategorizerObject() {}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public CatEntityWrapper getData() {
		return data;
	}

	public void setData(CatEntityWrapper data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return String.format("JSONResponse [responseCode=%s, count=%s, data=%s]", this.responseCode, this.count, this.data);
	}

	public static class CatEntityWrapper {
		private String appId;
		private Long docId;
		private List<CatEntity> categorizerResponse;
		private String rule;

		public String getAppId() {
			return appId;
		}

		public void setAppId(String appId) {
			this.appId = appId;
		}

		public Long getDocId() {
			return docId;
		}

		public void setDocId(Long docId) {
			this.docId = docId;
		}

		public List<CatEntity> getCategorizerResponse() {
			return categorizerResponse;
		}

		public void setCategorizerResponse(List<CatEntity> data) {
			this.categorizerResponse = data;
		}

		public String getRule() {
			return rule;
		}

		public void setRule(String rule) {
			this.rule = rule;
		}
	}

	public static class CatEntity implements Comparable<CatEntity> {
		private Attribute attribute;
		private Short band;
		private Integer relevance;
		private Short topicDimension;
		private Long catId;
		private Short score;
		private Boolean attrExclude;
		private Long charOffset;
		private Long charCount;
		private Boolean fromFirstRain;
		private String docId;
		private String title;
		private String body;

		public Boolean getAttrExclude() {
			return attrExclude;
		}

		public void setAttrExclude(Boolean attrExclude) {
			this.attrExclude = attrExclude;
		}

		public Short getBand() {
			return band;
		}

		public void setBand(Short band) {
			this.band = band;
		}

		public Integer getRelevance() {
			return relevance;
		}

		public void setRelevance(Integer relevance) {
			this.relevance = relevance;
		}

		public Short getTopicDimension() {
			return topicDimension;
		}

		public void setTopicDimension(Short topicDimension) {
			this.topicDimension = topicDimension;
		}

		public Long getCatId() {
			return catId;
		}

		public void setCatId(Long catId) {
			this.catId = catId;
		}

		public Attribute getAttribute() {
			return attribute;
		}

		public void setAttribute(Attribute attribute) {
			this.attribute = attribute;
		}

		public Short getScore() {
			return score;
		}

		public void setScore(Short score) {
			this.score = score;
		}

		public Long getCharOffset() {
			return charOffset;
		}

		public void setCharOffset(Long charOffset) {
			this.charOffset = charOffset;
		}

		public Long getCharCount() {
			return charCount;
		}

		public void setCharCount(Long charCount) {
			this.charCount = charCount;
		}

		@Override
		public int compareTo(CatEntity o) {
			return ObjectUtils.compare(o.score, this.score);
		}

		public Boolean getFromFirstRain() {
			return fromFirstRain;
		}

		public void setFromFirstRain(Boolean fromFirstRain) {
			this.fromFirstRain = fromFirstRain;
		}

		public String getDocId() {
			return docId;
		}

		public void setDocId(String docId) {
			this.docId = docId;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getBody() {
			return body;
		}

		public void setBody(String body) {
			this.body = body;
		}

	}
	public static class Attribute {
		private String name;
		private String attrSearchToken;
		private Integer attrDim;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getAttrSearchToken() {
			return attrSearchToken;
		}

		public void setAttrSearchToken(String attrSearchToken) {
			this.attrSearchToken = attrSearchToken;
		}

		public Integer getAttrDim() {
			return attrDim;
		}

		public void setAttrDim(Integer attrDim) {
			this.attrDim = attrDim;
		}
	}
}
