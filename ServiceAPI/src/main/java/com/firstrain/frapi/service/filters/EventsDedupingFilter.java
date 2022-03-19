package com.firstrain.frapi.service.filters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.firstrain.frapi.events.IEvents;
import com.firstrain.frapi.events.IEvents.EventGroupEnum;
import com.firstrain.frapi.events.IEvents.EventTypeEnum;
import com.firstrain.frapi.service.EventsFilter;
import com.firstrain.frapi.service.filters.Selectors.DepartureSelector;
import com.firstrain.frapi.service.filters.Selectors.HireSelector;
import com.firstrain.frapi.service.filters.Selectors.InternalTurnoverSelector;

/**
 * @author Akanksha
 * 
 */

public class EventsDedupingFilter implements EventsFilter {

	// Event selectors.
	private static InternalTurnoverSelector internalTurnoverSelector = new InternalTurnoverSelector();
	private static HireSelector hireSelector = new HireSelector();
	private static DepartureSelector departureSelector = new DepartureSelector();

	private static class EntityMTEvents {

		static class EventDetails {

			private List<IEvents> events = new ArrayList<IEvents>();
			int internalMoveCount = 0;
			int hireCount = 0;
			int departureCount = 0;
			int internalMoveRank1Count = 0;

			void add(IEvents e) {
				this.events.add(e);
				if (internalTurnoverSelector.isSelected(e)) {
					this.internalMoveCount++;
					if (getMTInternalMoveEventRankWithinClass1(e) >= 0) {
						this.internalMoveRank1Count++;
					}
				}
				if (hireSelector.isSelected(e)) {
					this.hireCount++;
				}
				if (departureSelector.isSelected(e)) {
					this.departureCount++;
				}
			}
		}

		// Maintain the order of dates
		Map<Date, EventDetails> datedEventsMap = new LinkedHashMap<Date, EventDetails>();


		void collectEvent(IEvents e) {
			Date eDate = e.getDate();
			EventDetails eventDetails = this.datedEventsMap.get(eDate);
			if (eventDetails == null) {
				eventDetails = new EventDetails();
				this.datedEventsMap.put(eDate, eventDetails);
			}
			eventDetails.add(e);
		}
	}

	private static String dedupKey(IEvents e) {
		String s = ((e.getDate().toString() + e.getEventType()) + e.getScore()) + e.getEntityId();
		return s;
	}

	private static String dedupKeyShort(IEvents e) {
		// Only context of entity applies here.
		String s = ((e.getDate().toString() + e.getEventType()) + e.getScore());
		return s;
	}

	@Override
	public List<IEvents> filterEvents(List<IEvents> input) {
		// Hack alert! Papering over all sorts of NPEs.
		if (input == null) {
			return Collections.emptyList();
		}

		Map<Long, EntityMTEvents> mtEventsMap = new LinkedHashMap<Long, EventsDedupingFilter.EntityMTEvents>();

		List<IEvents> rv = new ArrayList<IEvents>(input);
		HashSet<String> keys = new HashSet<String>();
		ListIterator<IEvents> li = rv.listIterator();
		while (li.hasNext()) {
			IEvents e = li.next();

			// Keep the MT events separately, let it be in the original list as well.
			if (e.getEventGroup() == EventGroupEnum.GROUP_MGMT_CHANGE) {
				putEventInMtEventsMap(mtEventsMap, e);
				continue;
			}

			String key = dedupKey(e);
			if (!keys.add(key)) {
				li.remove();
			}
		}

		applyAndFilterEventsOnMTSpecificRules(rv, mtEventsMap);
		return rv;
	}

	private void applyAndFilterEventsOnMTSpecificRules(List<IEvents> rv, Map<Long, EntityMTEvents> mtEventsMap) {
		/*
		 * Steps: 1. Loop on mtEventsMap on per entity basis. 2. Now check per day events for the following: a.) If a day has only 1 event,
		 * leave it. b.) If a day has more than 1 events and any of them is a non-internal move then remove all internal moves. c.) If there
		 * are internal moves left after above b.) then apply class 1 ranking rule and remove non-class 1 events. 4. Now apply the general
		 * de-dupe rule & trendID rule on rest of the events now. For the above process if any event(of type IM typically) is disqualified,
		 * remove it from original list as well.
		 */
		Map<Long, IEvents> trendMap = new HashMap<Long, IEvents>();

		Set<Entry<Long, EntityMTEvents>> es = mtEventsMap.entrySet();
		// Go entity-wise first.
		for (Entry<Long, EntityMTEvents> entry : es) {
			EntityMTEvents mtEvents = entry.getValue();

			Set<String> uniqEventKeys = new HashSet<String>();

			// Then inspect events on per day basis.
			Set<Entry<Date, EventsDedupingFilter.EntityMTEvents.EventDetails>> es2 = mtEvents.datedEventsMap.entrySet();
			for (Entry<Date, EventsDedupingFilter.EntityMTEvents.EventDetails> entry2 : es2) {
				EventsDedupingFilter.EntityMTEvents.EventDetails datedEventDetails = entry2.getValue();
				List<IEvents> datedEvents = datedEventDetails.events;

				// 2.a Do nothing iff there is 1 event.
				if (datedEvents.size() == 1) {
					continue;
				}

				int imCnt = datedEventDetails.internalMoveCount;
				int depCnt = datedEventDetails.departureCount;
				int hireCnt = datedEventDetails.hireCount;

				// 2.b See if there is any IM and non-IM events combination, remove all IM events.
				if (imCnt > 0 && (depCnt > 0 || hireCnt > 0)) {
					for (Iterator<IEvents> iterator = datedEvents.iterator(); iterator.hasNext();) {
						IEvents event = iterator.next();
						if (internalTurnoverSelector.isSelected(event)) {
							iterator.remove();
							rv.remove(event);
							imCnt--;
						}
					}
				}

				// 2.c See if we still got more than 1 IM events, remove those of not class 1 unique ranking.
				if (imCnt > 1 && datedEventDetails.internalMoveRank1Count > 0) {
					// But should it keep the events a/c to "Unique ranking" column also?
					for (Iterator<IEvents> iterator = datedEvents.iterator(); iterator.hasNext();) {
						IEvents event = iterator.next();
						if (internalTurnoverSelector.isSelected(event) && getMTInternalMoveEventRankWithinClass1(event) < 0) {
							// Remove non rank1 IM event from this and main list.
							iterator.remove();
							rv.remove(event);
							imCnt--;
						}
					}
				}

				// Now remove the duplicate events and scan on 'trendID' factor as well and keep the most-recent trendId data.
				for (Iterator<IEvents> iterator = datedEvents.iterator(); iterator.hasNext();) {
					IEvents e = iterator.next();

					String key = dedupKeyShort(e);
					if (!uniqEventKeys.add(key)) {
						iterator.remove();
						rv.remove(e);
						continue;
					}

					if (internalTurnoverSelector.isSelected(e)) {
						Long trendId = (Long) e.getProperties().get("trendID");
						IEvents lastEvent = trendMap.get(trendId);
						if (lastEvent != null) {
							iterator.remove();
							rv.remove(e);
						} else {
							trendMap.put(trendId, e);
						}
					}
				}

				// datedEvents is still up-to-date and usable.
			}
		}
	}

	private static int getMTInternalMoveEventRankWithinClass1(IEvents e) {
		int rank = -1;
		for (int i = 0; i < EventTypeList.mgmtTurnOverRank1FilterEventTypes.length; i++) {
			EventTypeEnum et = EventTypeList.mgmtTurnOverRank1FilterEventTypes[i];
			if (e.getEventType() == et) {
				rank = i;
				break;
			}
		}
		return rank;
	}

	private void putEventInMtEventsMap(Map<Long, EntityMTEvents> mtEventsMap, IEvents e) {
		long eId = e.getEntityId();
		EntityMTEvents entityMtEvents = mtEventsMap.get(eId);
		if (entityMtEvents == null) {
			entityMtEvents = new EntityMTEvents();
			mtEventsMap.put(eId, entityMtEvents);
		}
		entityMtEvents.collectEvent(e);
	}
}
