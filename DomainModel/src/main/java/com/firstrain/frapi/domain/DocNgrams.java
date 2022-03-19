package com.firstrain.frapi.domain;

public class DocNgrams {

	private Integer phraseCount;
	private String phrase;
	private String firstLocation;

	public Integer getPhraseCount() {
		return phraseCount;
	}

	public void setPhraseCount(Integer phraseCount) {
		this.phraseCount = phraseCount;
	}

	public String getPhrase() {
		return phrase;
	}

	public void setPhrase(String phrase) {
		this.phrase = phrase;
	}

	public String getFirstLocation() {
		return firstLocation;
	}

	public void setFirstLocation(String firstLocation) {
		this.firstLocation = firstLocation;
	}
}
