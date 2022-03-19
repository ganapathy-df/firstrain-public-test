package com.firstrain.frapi.service.filters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.firstrain.frapi.events.EventObj;
import com.firstrain.frapi.events.IEvents;
import com.firstrain.frapi.events.IEvents.EventGroupEnum;
import com.firstrain.frapi.events.IEvents.EventTypeEnum;
import com.firstrain.frapi.service.EventsFilter;


/**
 * This filter is to clean-up same-day stock events on following priority: Keep on the highest priority stock event for a given day as
 * follows
 * <ol>
 * <li>Stock Price :: Priority</li>
 * <li>Stock Price: Stock Closing Price Breaking Through 52 Week High / Low :: 1</li>
 * <li>Stock Price: Stock Closing Price Breaking Through 200-day Moving Average :: 2</li>
 * <li>Stock Price: Stock Closing Price Breaking Through 100-day Moving Average :: 3</li>
 * <li>Stock Price: Stock Closing Price Breaking Through 50-day Moving Average :: 4</li>
 * <li>Stock Price: Stock Price Change > 5% :: 5</li>
 * <li>Stock Price: Stock Closing Price Contra to Industry Composite by >5% :: Undefined</li>
 * </ol>
 * This is supported for multiple companies stock events also.
 * 
 * @author Deepak
 *
 */
public class EventStockFilter implements EventsFilter {

	private static final EventTypeEnum[] prioritizedEvents =
			{EventTypeEnum.TYPE_STOCK_CLOSING_PRICE_BREAKING_52_WEEK_HIGH_LOW, EventTypeEnum.TYPE_STOCK_CLOSING_PRICE_200_DAY_AVG,
					EventTypeEnum.TYPE_STOCK_CLOSING_PRICE_100_DAY_AVG, EventTypeEnum.TYPE_STOCK_CLOSING_PRICE_50_DAY_AVG,
					EventTypeEnum.TYPE_STOCK_PRICE_CHANGE, EventTypeEnum.TYPE_STOCK_CLOSING_PRICE_CONTRA_TO_INDUSTRY_COMPOSITE};

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.firstrain.portal.services.EventsFilter#filterEvents(java.util.List)
	 */
	@Override
	public List<IEvents> filterEvents(List<IEvents> input) {

		/*
		 * Algorithm is 1. Prepare a map of EID vs Events. 2. Go for each EID events for getting its included set of events. 3. Prepare a
		 * cumulative list of events from Step 2. 4. Finally exclude events, other than Step 3, from the original list.
		 */

		Collection<IEvents> toInclude = new ArrayList<IEvents>();
		Map<Long, List<IEvents>> eIdVsEventsMap = getEntityIdVsEventsMap(input);
		Set<Entry<Long, List<IEvents>>> mapEntrySet = eIdVsEventsMap.entrySet();

		for (Entry<Long, List<IEvents>> entry : mapEntrySet) {
			List<IEvents> eventsList = entry.getValue();
			toInclude.addAll(getSingleCompanyIncludedEvents(eventsList));
		}

		List<IEvents> events = new ArrayList<IEvents>(input);
		Iterator<IEvents> ei = events.iterator();
		while (ei.hasNext()) {
			IEvents ev = ei.next();
			if (ev.getEventGroup() != EventGroupEnum.GROUP_STOCK_PRICE || toInclude.contains(ev)) {
				continue;
			}
			ei.remove();
		}
		toInclude.clear();
		return events;
	}

	/**
	 * Returns the list of events that are finalized for being included from supplied events list.
	 * 
	 * @param input List of events that needs inspection.
	 * @return Collection of events that are selected for inclusion.
	 * @author Deepak
	 */
	private Collection<IEvents> getSingleCompanyIncludedEvents(List<IEvents> input) {
		Map<Integer, IEvents> map = new HashMap<Integer, IEvents>();
		for (IEvents event : input) {
			if (event.getEventGroup() != EventGroupEnum.GROUP_STOCK_PRICE) {
				continue;
			}
			int dayId = ((EventObj) event).getDayId();
			IEvents e = map.get(dayId);
			if (e == null || getIndexInArray(e) > getIndexInArray(event)) {
				map.put(dayId, event);
			}
		}
		return map.values();
	}

	private int getIndexInArray(IEvents event) {
		int l = prioritizedEvents.length;
		/*
		 * ALERT: This is knowingly set to a higher number to take care of any events that are out of our filtering scope, ensures that
		 * those are at least pushed back to provide room for covered event types.
		 */
		int index = l * 2;
		for (int i = 0; i < l; i++) {
			if (event.getEventType() == prioritizedEvents[i]) {
				index = i;
				break;
			}
		}
		return index;
	}

	public static Map<Long, List<IEvents>> getEntityIdVsEventsMap(List<IEvents> input) {
		Map<Long, List<IEvents>> eIdVsEventsMap = new HashMap<Long, List<IEvents>>();
		for (IEvents event : input) {
			long entityId = event.getEntityId();
			List<IEvents> eList = eIdVsEventsMap.get(entityId);
			if (eList == null) {
				eList = new ArrayList<IEvents>();
				eIdVsEventsMap.put(entityId, eList);
			}
			eList.add(event);
		}
		return eIdVsEventsMap;
	}
}
