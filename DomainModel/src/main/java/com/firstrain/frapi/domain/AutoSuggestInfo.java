package com.firstrain.frapi.domain;

/**
 * @author GKhanchi
 */

public class AutoSuggestInfo {
	private String searchToken;
	private String displaySynonym;
	private String synonym;
	private String displayCik;
	private String displaySedol;
	private String displayIsin;
	private String name;
	private String nameH;
	private String type;
	private String displayName;
	private int replaceOffset;
	private int replaceLength;
	private String displayTicker;
	private String tickerH;
	private int companyId;
	private String catId;
	private int docCount;

	public String getSynonym() {
		return synonym;
	}

	public void setSynonym(String synonym) {
		this.synonym = synonym;
	}
	
	public String getSearchToken() {
		return searchToken;
	}

	public void setSearchToken(String searchToken) {
		this.searchToken = searchToken;
	}

	public String getDisplaySynonym() {
		return displaySynonym;
	}

	public void setDisplaySynonym(String displaySynonym) {
		this.displaySynonym = displaySynonym;
	}

	public String getDisplayCik() {
		return displayCik;
	}

	public void setDisplayCik(String displayCik) {
		this.displayCik = displayCik;
	}

	public String getDisplaySedol() {
		return displaySedol;
	}

	public void setDisplaySedol(String displaySedol) {
		this.displaySedol = displaySedol;
	}

	public String getDisplayIsin() {
		return displayIsin;
	}

	public void setDisplayIsin(String displayIsin) {
		this.displayIsin = displayIsin;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public int getReplaceOffset() {
		return replaceOffset;
	}

	public void setReplaceOffset(int replaceOffset) {
		this.replaceOffset = replaceOffset;
	}

	public int getReplaceLength() {
		return replaceLength;
	}

	public void setReplaceLength(int replaceLength) {
		this.replaceLength = replaceLength;
	}

	public String getDisplayTicker() {
		return displayTicker;
	}

	public void setDisplayTicker(String displayTicker) {
		this.displayTicker = displayTicker;
	}

	public int getCompanyId() {
		return companyId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	public String getNameH() {
		return nameH;
	}

	public void setNameH(String nameH) {
		this.nameH = nameH;
	}

	public String getTickerH() {
		return tickerH;
	}

	public void setTickerH(String tickerH) {
		this.tickerH = tickerH;
	}

	public String getCatId() {
		return catId;
	}

	public void setCatId(String catId) {
		this.catId = catId;
	}

	public int getDocCount() {
		return docCount;
	}

	public void setDocCount(int docCount) {
		this.docCount = docCount;
	}

}
