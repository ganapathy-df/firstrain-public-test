package com.firstrain.frapi.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.firstrain.frapi.events.IEvents;
import com.firstrain.frapi.events.IEvents.EventTypeEnum;
import com.firstrain.frapi.service.EventSelector;
import com.firstrain.frapi.service.EventsFilter;
import com.firstrain.frapi.service.filters.EventFilterList;
import com.firstrain.frapi.service.filters.EventTypeList;
import com.firstrain.frapi.service.filters.Selectors.WebVolumeSelector;
import com.firstrain.frapi.service.filters.Selectors.TypeSelector;
import com.firstrain.frapi.service.filters.Selectors.StockPriceChangeSelector;

public class SingleCompanyEventsFilter implements EventsFilter {
	private final int size;
	
	private final EventsFilter additionalBlacklist;

	private static final EventSelector filterPrioritizedStack[] = new EventSelector[] {
			new WebVolumeSelector(5, EventTypeEnum.TYPE_TOTAL_WEB_COVERAGE_VOLUME_TOPIC),
			new TypeSelector(EventTypeList.mgmtTurnOverRank45FilterEventTypes), new StockPriceChangeSelector(10), new WebVolumeSelector(5),
			new WebVolumeSelector(10, EventTypeEnum.TYPE_TOTAL_WEB_COVERAGE_VOLUME_TOPIC),
			new TypeSelector(EventTypeList.mgmtTurnOverRank3FilterEventTypes), new TypeSelector(EventTypeList.eightKRank34FilterEventTypes),
			new StockPriceChangeSelector(20), new TypeSelector(EventTypeEnum.TYPE_STOCK_CLOSING_PRICE_50_DAY_AVG),
			new WebVolumeSelector(10), new TypeSelector(EventTypeList.stockFilterEventTypes),
			new TypeSelector(EventTypeList.eightKRank2FilterEventTypes), new TypeSelector(EventTypeEnum.TYPE_DELAYED_SEC_FILING),
			new TypeSelector(EventTypeList.mgmtTurnOverRank2FilterEventTypes), new TypeSelector(EventTypeList.eightKRank1FilterEventTypes),
			new TypeSelector(EventTypeList.tenQFilterEventTypes), new TypeSelector(EventTypeList.tenKFilterEventTypes),
			// TODO:Since we don't know the max score of event, going with this, need to fix with event max score.
			new StockPriceChangeSelector(Integer.MAX_VALUE),
			new WebVolumeSelector(Integer.MAX_VALUE, EventTypeEnum.TYPE_TOTAL_WEB_COVERAGE_VOLUME_TOPIC),
			new WebVolumeSelector(Integer.MAX_VALUE),};

	public SingleCompanyEventsFilter(int size) {
		this(size, false);
	}

	public SingleCompanyEventsFilter(int size, boolean applySignalFilter) {
		this(size, applySignalFilter, null);
	}

	public SingleCompanyEventsFilter(int size, boolean applySignalFilter, EventsFilter additionalBlacklist) {
		this.size = size;
		this.additionalBlacklist = additionalBlacklist;
	}

	@Override
	public List<IEvents> filterEvents(List<IEvents> input) {
		List<IEvents> uniq = EventFilterList.dedupFilter.filterEvents(input);

		List<IEvents> candidates = filterAndRetrieve(uniq);

		int toDelete = candidates.size() - size;

		if (toDelete <= 0) {
			return ensureNullPropEvents(candidates);
		}

		// Apply BSA now.
		return retrieveCandidatesAndEnsureNullPropEvents(candidates, toDelete);
	}

	private List<IEvents> retrieveCandidatesAndEnsureNullPropEvents(List<IEvents> candidatesParam, int toDelete) {
		List<IEvents> candidates = candidatesParam;
		removeloop: for (EventSelector es : filterPrioritizedStack) {
			// Iterate backwards so that i is remove-stable.
			for (int i = candidates.size() - 1; i >= 0; --i) {
				if (toDelete <= 0) {
					break removeloop;
				}
				IEvents e = candidates.get(i);
				if (es.isSelected(e)) {
					candidates.remove(i);
					toDelete--;
				}
			}
		}
		
		if (candidates.size() > size) {
			candidates = candidates.subList(0, size);
		}
		
		return ensureNullPropEvents(candidates);
	}

	private List<IEvents> filterAndRetrieve(final List<IEvents> uniq) {
		List<IEvents> candidates = EventFilterList.stockEventFilter.filterEvents(uniq);
		
		candidates = EventFilterList.consecutiveDayFilter.filterEvents(candidates);
		
		candidates = EventFilterList.basicFilter.filterEvents(candidates);
		
		// if (false && applySignalFilter) {
		// candidates = EventFilterList.signalsBasicFilterSingleCompany.filterEvents(candidates);
		// }
		
		if (additionalBlacklist != null) {
			candidates = additionalBlacklist.filterEvents(candidates);
		}
		return candidates;
	}

	static List<IEvents> ensureNullPropEvents(List<IEvents> filteredList) {
		List<IEvents> nonNullEvents = new ArrayList<IEvents>();
		for (IEvents event : filteredList) {
			if (hasNullProp(event)) {
				continue;
			}
			nonNullEvents.add(event);
		}
		return nonNullEvents;
	}

	static boolean hasNullProp(IEvents event) {
		/* url should never be null for any event group other then STOCK_PRICE */
		if (event.getEventGroup() != IEvents.EventGroupEnum.GROUP_STOCK_PRICE && (event.getUrl() == null || event.getUrl().equals(""))) {
			return true;
		}

		if (event.getEventGroup() == IEvents.EventGroupEnum.GROUP_WEB_VOLUME) {
			String title = event.getCaption();
			if (title == null || title.equals("")) {
				return true;
			}
		}

		return false;
	}

	private static boolean blackListed(IEvents e) {
		for (EventSelector es : EventFilterList.basicFilterList) {
			if (es.isSelected(e)) {
				return true;
			}
		}
		return false;
	}


	static int priorityRank(IEvents e) {
		int maxRank = filterPrioritizedStack.length;
		if (blackListed(e)) {
			return maxRank + 1;
		}
		for (int i = 0; i < filterPrioritizedStack.length; ++i) {
			EventSelector es = filterPrioritizedStack[i];
			if (es.isSelected(e)) {
				return maxRank - i;
			}
		}
		return 0;
	}
}
