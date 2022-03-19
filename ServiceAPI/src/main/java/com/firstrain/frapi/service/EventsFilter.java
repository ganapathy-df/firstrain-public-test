package com.firstrain.frapi.service;

import java.util.List;

import com.firstrain.frapi.events.IEvents;

public interface EventsFilter {
	public List<IEvents> filterEvents(List<IEvents> input);
}
