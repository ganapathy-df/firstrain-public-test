package com.firstrain.frapi.service.filters;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.firstrain.frapi.events.EventObj;
import com.firstrain.frapi.events.IEvents;
import com.firstrain.frapi.events.IEvents.EventGroupEnum;
import com.firstrain.frapi.service.EventsFilter;

/**
 * This filter is to cleanup stock events of same type of consecutive days, NOTE that Monday and Friday are also considered as consecutive
 * days.
 * 
 * @author Deepak
 */

public class EventConsecutiveDayFilter implements EventsFilter {

	public static final int MON_TO_FRI_DAYS_DIFFERENCE = 3;

	@Override
	public List<IEvents> filterEvents(List<IEvents> input) {

		/*
		 * Algorithm is 1. Prepare a map of EID vs Events. 2. Go for each EID events for getting its excluded set of events. 3. Prepare a
		 * cumulative list of events from Step 2. 4. Finally filter original list from excluded events in Step 3.
		 */

		Collection<IEvents> toExclude = new ArrayList<IEvents>();

		Map<Long, List<IEvents>> eIdVsEventsMap = EventStockFilter.getEntityIdVsEventsMap(input);
		Set<Entry<Long, List<IEvents>>> mapEntrySet = eIdVsEventsMap.entrySet();

		for (Entry<Long, List<IEvents>> entry : mapEntrySet) {
			List<IEvents> eventsList = entry.getValue();
			toExclude.addAll(getSingleCompanyExcludedEvents(eventsList));
		}

		List<IEvents> events = new ArrayList<IEvents>(input);
		events.removeAll(toExclude);
		toExclude.clear();
		return events;
	}

	/**
	 * Returns the list of events that are finalized for being excluded from supplied events list.
	 * 
	 * @param input List of events that needs inspection.
	 * @return Collection of events that are selected for exclusion.
	 * @author Deepak
	 */
	private Collection<IEvents> getSingleCompanyExcludedEvents(List<IEvents> events) {
		Collection<IEvents> toExclude = new ArrayList<IEvents>();
		int totalEvents = events.size();

		IEvents prevEvent = events.get(totalEvents - 1);
		Calendar c = Calendar.getInstance();

		for (int i = totalEvents - 2; i > -1; i--) {
			IEvents currentEvent = events.get(i);
			/* Check event group first. */
			if (currentEvent.getEventGroup() != EventGroupEnum.GROUP_STOCK_PRICE) {
				continue;
			}
			/* If events are of same type */
			if (currentEvent.getEventType() == prevEvent.getEventType()) {
				/* If dayIds diff is 1, then is consecutive */
				int dayIdDiff = ((EventObj) currentEvent).getDayId() - ((EventObj) prevEvent).getDayId();
				if (dayIdDiff == 1) {
					toExclude.add(prevEvent);
				} else {
					/*
					 * Check consecutive days for FRI-MON. Check for another combinations need to integrate here.
					 */
					if (dayIdDiff == MON_TO_FRI_DAYS_DIFFERENCE) {
						c.setTime(currentEvent.getDate());
						int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
						if (dayOfWeek == Calendar.MONDAY) {
							toExclude.add(prevEvent);
						}
					}
				}
			}
			prevEvent = currentEvent;
		}
		return toExclude;
	}
}
