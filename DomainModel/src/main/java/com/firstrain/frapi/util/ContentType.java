package com.firstrain.frapi.util;

public enum ContentType {
	FILINGS_10K(301024, "10-K Filings"),
	FILINGS_10Q(301025, "10-Q Filings"),
	FILINGS_SEC345(315016, "SEC Form 3,4,5"),
	FILINGS_8K(496457, "8-K Filings"),
	CALL_TRANSCRIPT(325837, "Call Transcripts"),
	BLOGS(276206, "Blogs"),
	PRESS_RELEASE(276204, "Press Releases"),
	WIRENEWS(276205, "News Wires"),
	MEDICAL_JOURNALS(358667, "Medical Journals"),
	INDUSTRY_SOURCES(288557, "Industry Sources"),
	WEBNEWS(500840, "News and Web");

	ContentType(int id, String label) {
		this.id = id;
		this.label = label;
	}

	private int id;
	private String label;

	public int getId() {
		return this.id;
	}

	public String getLabel() {
		return this.label;
	}
}
