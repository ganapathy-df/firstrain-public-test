package com.firstrain.frapi.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.PriorityQueue;
import java.util.TreeSet;

import com.firstrain.frapi.events.IEvents;
import com.firstrain.frapi.service.EventsFilter;
import com.firstrain.frapi.service.filters.EventFilterList;

public class MultiEntityEventsFilter implements EventsFilter {
	private final int size;
	private final boolean applySignalFilter;
	private final EventsFilter additionalBlaclkist;
	private final boolean applyMEEFilter;

	private static Comparator<IEvents> rankedComparator = new RankedEventComparator();
	private static Comparator<IEvents> dateComparator = new DateOrderEventComparator();

	public MultiEntityEventsFilter(int size) {
		this(size, false);
	}

	public MultiEntityEventsFilter(int size, boolean applySignalFilter) {
		this(size, applySignalFilter, null);
	}

	public MultiEntityEventsFilter(int size, boolean applySignalFilter, EventsFilter additionalBlacklist) {
		this(size, applySignalFilter, true, additionalBlacklist);
	}

	public MultiEntityEventsFilter(int size, boolean applySignalFilter, boolean applyMEEFilter, EventsFilter additionalBlacklist) {
		this.size = size;
		this.applySignalFilter = applySignalFilter;
		this.additionalBlaclkist = additionalBlacklist;
		this.applyMEEFilter = applyMEEFilter;
	}

	@Override
	public List<IEvents> filterEvents(List<IEvents> input) {
		List<IEvents> filtered = EventFilterList.dedupFilter.filterEvents(input);

		filtered = EventFilterList.stockEventFilter.filterEvents(filtered);

		filtered = EventFilterList.consecutiveDayFilter.filterEvents(filtered);

		filtered = EventFilterList.basicFilter.filterEvents(filtered);

		if (applyMEEFilter) {
			filtered = EventFilterList.multiCompanyExclusionFilter.filterEvents(filtered);
		}

		if (false && applySignalFilter) {
			filtered = EventFilterList.signalsBasicFilterMultiCompany.filterEvents(filtered);
		}

		if (additionalBlaclkist != null) {
			filtered = additionalBlaclkist.filterEvents(filtered);
		}

		if (filtered.size() <= size) {
			return SingleCompanyEventsFilter.ensureNullPropEvents(filtered);
		}

		HashMap<Long, PriorityQueue<IEvents>> groupedQueues = new HashMap<Long, PriorityQueue<IEvents>>();
		for (IEvents e : filtered) {
			insertInto(e, groupedQueues);
		}

		ArrayList<PriorityQueue<IEvents>> queues = new ArrayList<PriorityQueue<IEvents>>(groupedQueues.values());

		TreeSet<IEvents> rv = new TreeSet<IEvents>(dateComparator);
		// select round robin from the queues till done

		addLoop: while (rv.size() < size) {
			ListIterator<PriorityQueue<IEvents>> li = queues.listIterator();
			while (li.hasNext()) {
				if (rv.size() >= size) {
					break addLoop;
				}
				PriorityQueue<IEvents> pq = li.next();
				IEvents e = pq.poll();
				if (e != null) {
					// System.out.println("Prank " + SingleCompanyEventsFilter.priorityRank(e) + "|" +e);
					rv.add(e);
				} else {
					li.remove();
				}
			}
			if (queues.isEmpty()) {
				break;
			}
		}

		ArrayList<IEvents> l = new ArrayList<IEvents>(rv);
		return SingleCompanyEventsFilter.ensureNullPropEvents(l);
	}

	private static void insertInto(IEvents e, HashMap<Long, PriorityQueue<IEvents>> groupedQueues) {
		PriorityQueue<IEvents> qForEntity = groupedQueues.get(e.getEntityId());
		if (qForEntity == null) {
			qForEntity = new PriorityQueue<IEvents>(16, rankedComparator);
			groupedQueues.put(e.getEntityId(), qForEntity);
		}
		qForEntity.add(e);
	}

}


class RankedEventComparator implements Comparator<IEvents> {

	@Override
	public int compare(IEvents o1, IEvents o2) {
		int rank1 = SingleCompanyEventsFilter.priorityRank(o1);
		int rank2 = SingleCompanyEventsFilter.priorityRank(o2);
		int rv = rank1 - rank2;

		if (rv == 0) {
			rv = o2.getDate().compareTo(o1.getDate());
		}

		// This is a little bit desperate. But I dont want to drop events
		// on the floor here.
		if (rv == 0) {
			rv = o1.hashCode() - o2.hashCode();
		}

		return rv;
	}
}


class DateOrderEventComparator implements Comparator<IEvents> {
	@Override
	public int compare(IEvents o1, IEvents o2) {
		int rv = o2.getDate().compareTo(o1.getDate());
		if (rv == 0) {
			return o2.getEventId() - o1.getEventId();
		}
		return rv;
	}
}
