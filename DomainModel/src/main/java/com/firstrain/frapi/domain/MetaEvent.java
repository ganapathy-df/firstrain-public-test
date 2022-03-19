package com.firstrain.frapi.domain;

import com.firstrain.frapi.pojo.wrapper.DocumentSet;
import com.firstrain.frapi.pojo.wrapper.EventSet;

public class MetaEvent {
	private EventSet relatedEvent;
	private DocumentSet relatedDocument;

	public EventSet getRelatedEvent() {
		return relatedEvent;
	}

	public void setRelatedEvent(EventSet relatedEvent) {
		this.relatedEvent = relatedEvent;
	}

	public DocumentSet getRelatedDocument() {
		return relatedDocument;
	}

	public void setRelatedDocument(DocumentSet relatedDocument) {
		this.relatedDocument = relatedDocument;
	}

}
