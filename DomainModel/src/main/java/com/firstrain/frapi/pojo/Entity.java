package com.firstrain.frapi.pojo;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.firstrain.frapi.util.DefaultEnums.Status;
import com.firstrain.web.pojo.EntityLink;

public class Entity implements Serializable {

	private static final long serialVersionUID = -3345179857673139369L;
	private String id;
	private String name;
	private String ticker;
	private String searchToken;
	private String matchedType;
	private boolean markedForDeletion;
	private Boolean selected;
	private Boolean removed;
	private Boolean added;
	private List<Entity> entitiesList;
	private Entity sector;
	private Entity segment;
	private Entity industry;
	private int docCount;
	private Status status;
	private String companyLogoUrl;
	private String dnbEntityId;
	private Integer documentCount;
	private Map<String, String> additonalInfoMap;
	private Integer type;
	private int industryCatId;
	private Short relevanceScore;
	private Integer industryClassificationId;
	private List<EntityLink> entityLinks;
	private String country;
	private Collection<Integer> bizLineCatIds;

	/**
	 * 
	 * Added For trending entity calculation.
	 */
	private Integer entityCount;
	private Integer oneDayDocCount;
	private Integer thirtyOneDaysDocCount;
	private Float value; // defines the size of the node into the tree map.
	private Integer intensity; // variation in trends
	private Boolean hasTweet;
	private String dimension;
	private short scope;
	private String synonym;

	private String homePage;
	private Boolean primaryDunsMatch;
	private Boolean additionalMatchQualifier;

	public Entity(String name) {
		this.name = name;
	}

	public Entity(String name, String searchtoken) {
		this.name = name;
		this.searchToken = searchtoken;
	}

	public Entity() {
		super();
	}

	@JsonIgnore
	public Float getValue() {
		return value;
	}

	/** NOTE: @JsonIgnore */
	public void setValue(Float value) {
		this.value = value;
	}

	@JsonIgnore
	public Integer getIntensity() {
		return intensity;
	}

	/** NOTE: @JsonIgnore */
	public void setIntensity(Integer intensity) {
		this.intensity = intensity;
	}

	@JsonIgnore
	public Integer getOneDayDocCount() {
		return oneDayDocCount;
	}

	/** NOTE: @JsonIgnore */
	public void setOneDayDocCount(Integer oneDayDocCount) {
		this.oneDayDocCount = oneDayDocCount;
	}

	@JsonIgnore
	public Integer getThirtyOneDaysDocCount() {
		return thirtyOneDaysDocCount;
	}

	/** NOTE: @JsonIgnore */
	public void setThirtyOneDaysDocCount(Integer thirtyOneDaysDocCount) {
		this.thirtyOneDaysDocCount = thirtyOneDaysDocCount;
	}

	/**
	 * 
	 * Added For Fedility: Virtual Monitor Market Map Computation
	 */
	private int volumeRecentWeek;
	private Integer companyId;
	private Integer band;

	@JsonIgnore
	public int getVolumeRecentWeek() {
		return volumeRecentWeek;
	}

	/** NOTE: @JsonIgnore */
	public void setVolumeRecentWeek(int volumeRecentWeek) {
		this.volumeRecentWeek = volumeRecentWeek;
	}

	public String getSearchToken() {
		return searchToken;
	}

	public void setSearchToken(String searchToken) {
		this.searchToken = searchToken;
	}

	/** catId of Entity */
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTicker() {
		return ticker;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public Integer getBand() {
		return band;
	}

	public void setBand(Integer band) {
		this.band = band;
	}

	public void setEntitiesList(List<Entity> entitiesList) {
		this.entitiesList = entitiesList;
	}

	public List<Entity> getEntitiesList() {
		return entitiesList;
	}

	public Integer getIndustryClassificationId() {
		return industryClassificationId;
	}

	public void setIndustryClassificationId(Integer industryClassificationId) {
		this.industryClassificationId = industryClassificationId;
	}

	@JsonIgnore
	public boolean getMarkedForDeletion() {
		return markedForDeletion;
	}

	/** NOTE: @JsonIgnore */
	public void markForDeletion() {
		this.markedForDeletion = true;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Entity other = (Entity) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("entityId=%s, entityName=%s", this.id, this.name);
	}

	@JsonIgnore
	public int getDocCount() {
		return docCount;
	}

	/** NOTE: @JsonIgnore */
	public void setDocCount(int docCount) {
		this.docCount = docCount;
	}

	public Boolean getSelected() {
		return selected;
	}

	public void setSelected(Boolean selected) {
		this.selected = selected;
	}

	public Boolean getRemoved() {
		return removed;
	}

	public void setRemoved(Boolean removed) {
		this.removed = removed;
	}

	public String getCompanyLogoUrl() {
		return companyLogoUrl;
	}

	public void setCompanyLogoUrl(String companyLogoUrl) {
		this.companyLogoUrl = companyLogoUrl;
	}

	public String getDnbEntityId() {
		return dnbEntityId;
	}

	public void setDnbEntityId(String dnbCompanyId) {
		this.dnbEntityId = dnbCompanyId;
	}

	@JsonIgnore
	public Integer getEntityCount() {
		return entityCount;
	}

	/** NOTE: @JsonIgnore */
	public void setEntityCount(Integer entityCount) {
		this.entityCount = entityCount;
	}

	public Map<String, String> getAdditonalInfoMap() {
		return additonalInfoMap;
	}

	public void setAdditonalInfoMap(Map<String, String> additonalInfoMap) {
		this.additonalInfoMap = additonalInfoMap;
	}

	public Integer getDocumentCount() {
		return documentCount;
	}

	public void setDocumentCount(Integer documentCount) {
		this.documentCount = documentCount;
	}

	public Boolean getHasTweet() {
		return hasTweet;
	}

	public void setHasTweet(Boolean hasTweet) {
		this.hasTweet = hasTweet;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@JsonIgnore
	public int getIndustryCatId() {
		return industryCatId;
	}

	public void setIndustryCatId(int industryCatId) {
		this.industryCatId = industryCatId;
	}

	public Short getRelevanceScore() {
		return relevanceScore;
	}

	public void setRelevanceScore(Short relevanceScore) {
		this.relevanceScore = relevanceScore;
	}

	@JsonIgnore
	public String getDimension() {
		return dimension;
	}

	public void setDimension(String dimension) {
		this.dimension = dimension;
	}

	@JsonIgnore
	public short getScope() {
		return scope;
	}

	public void setScope(short scope) {
		this.scope = scope;
	}

	public Boolean getAdded() {
		return added;
	}

	public void setAdded(Boolean added) {
		this.added = added;
	}

	public String getMatchedType() {
		return matchedType;
	}

	public void setMatchedType(String matchedType) {
		this.matchedType = matchedType;
	}

	public Entity getSector() {
		return sector;
	}

	public void setSector(Entity sector) {
		this.sector = sector;
	}

	public Entity getSegment() {
		return segment;
	}

	public void setSegment(Entity segment) {
		this.segment = segment;
	}

	public Entity getIndustry() {
		return industry;
	}

	public void setIndustry(Entity industry) {
		this.industry = industry;
	}

	public String getHomePage() {
		return homePage;
	}

	public void setHomePage(String homePage) {
		this.homePage = homePage;
	}

	public List<EntityLink> getEntityLinks() {
		return entityLinks;
	}

	public void setEntityLinks(List<EntityLink> entityLinks) {
		this.entityLinks = entityLinks;
	}
	
	public String getSynonym() {
		return synonym;
	}

	public void setSynonym(String synonym) {
		this.synonym = synonym;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Collection<Integer> getBizLineCatIds() {
		return bizLineCatIds;
	}

	public void setBizLineCatIds(Collection<Integer> bizLineCatIds) {
		this.bizLineCatIds = bizLineCatIds;
	}

	public Boolean getPrimaryDunsMatch() {
		return primaryDunsMatch;
	}

	public void setPrimaryDunsMatch(Boolean primaryDunsMatch) {
		this.primaryDunsMatch = primaryDunsMatch;
	}

	public Boolean getAdditionalMatchQualifier() {
		return additionalMatchQualifier;
	}

	public void setAdditionalMatchQualifier(Boolean additionalMatchQualifier) {
		this.additionalMatchQualifier = additionalMatchQualifier;
	}
}
