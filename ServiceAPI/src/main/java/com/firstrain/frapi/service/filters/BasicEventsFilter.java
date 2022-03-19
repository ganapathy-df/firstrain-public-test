package com.firstrain.frapi.service.filters;

import java.util.ArrayList;
import java.util.List;

import com.firstrain.frapi.events.IEvents;
import com.firstrain.frapi.service.EventSelector;
import com.firstrain.frapi.service.EventsFilter;

/**
 * @author Akanksha
 * 
 */

public class BasicEventsFilter implements EventsFilter {
	private final EventSelector[] selectors;

	public BasicEventsFilter(EventSelector... selectors) {
		this.selectors = selectors;
	}

	private boolean isFiltered(IEvents e) {
		for (EventSelector es : this.selectors) {
			if (es.isSelected(e)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<IEvents> filterEvents(List<IEvents> input) {

		ArrayList<IEvents> rv = new ArrayList<IEvents>();
		for (IEvents e : input) {
			if (!isFiltered(e)) {
				rv.add(e);
			}
		}

		return rv;
	}
}
