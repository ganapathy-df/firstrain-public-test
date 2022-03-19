package com.firstrain.frapi.service.filters;

import com.firstrain.frapi.events.IEvents.EventTypeEnum;
import com.firstrain.frapi.service.EventSelector;
import com.firstrain.frapi.service.EventsFilter;
import com.firstrain.frapi.service.filters.Selectors.LowRankedInternalMgmtTurnoverSelector;
import com.firstrain.frapi.service.filters.Selectors.TypeSelector;
import com.firstrain.frapi.service.filters.Selectors.WebVolumeSelector;
import com.firstrain.frapi.service.impl.GraphEventFilter;

/**
 * This class is to hold all the event selectors statically for events filter. All event filters are grouped according to EventsandSignals
 * 2010Sep07.xls.
 * 
 * @author Deepak
 * @see EventTypeList
 */
public final class EventFilterList {

	/**
	 * To ensure that default constructor can't be invoked by others.
	 */
	private EventFilterList() {
		super();
	}

	/**
	 * This selector consist of the following events for signals basic filter of single company.
	 * <ol>
	 * <li>TYPE of Internal Management Turnover events of Rank 4 & 5.</li>
	 * <li>Web volume company selector of threshold <strong>5</strong>.</li>
	 * <li>Web volume topic selector of threshold <strong>5</strong>.</li>
	 * </ol>
	 */
	public static final EventSelector[] signalsBasicFilterlistSingleCompany =
			new EventSelector[] {new TypeSelector(EventTypeList.mgmtTurnOverRank45FilterEventTypes), new WebVolumeSelector(5),
					new WebVolumeSelector(5, EventTypeEnum.TYPE_TOTAL_WEB_COVERAGE_VOLUME_TOPIC),};

	/**
	 * This selector consist of the following events for signals basic filter of multi company.
	 * <ol>
	 * <li>Web volume company selector of threshold <strong>5</strong>.</li>
	 * <li>Web volume topic selector of threshold <strong>5</strong>.</li>
	 * </ol>
	 */
	public static final EventSelector[] signalsBasicFilterlistMultiCompany =
			new EventSelector[] {new WebVolumeSelector(5), new WebVolumeSelector(5, EventTypeEnum.TYPE_TOTAL_WEB_COVERAGE_VOLUME_TOPIC),};

	/**
	 * This selector consist of the following events for exclusion filter of multi company a.k.a <strong>MCE</strong>.
	 * <ol>
	 * <li>TYPE of Internal Management Turnover events of Rank 4 & 5.</li>
	 * <li>Web volume company selector of threshold <strong>5</strong>.</li>
	 * <li>Web volume topic selector of threshold <strong>5</strong>.</li> <!--
	 * <li>Group selector of <strong>'Group 8k Filing'</strong>.</li> -->
	 * </ol>
	 */
	public static final EventSelector[] multiCompanyExclusionSelectorList =
			new EventSelector[] {new TypeSelector(EventTypeList.mgmtTurnOverRank45FilterEventTypes), new WebVolumeSelector(5),
					new WebVolumeSelector(5, EventTypeEnum.TYPE_TOTAL_WEB_COVERAGE_VOLUME_TOPIC),
			// new GroupSelector(EventGroupEnum.GROUP_8K_FILING)
			};

	/**
	 * This filter list consist of the following:
	 * <ol>
	 * <li>TYPE 8K events of 5.02 category.</li>
	 * <li>Low Ranked Management Turnover Selector.</li>
	 * </ol>
	 */
	public static final EventSelector basicFilterList[] = new EventSelector[] {new TypeSelector(EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_5_02),
			new LowRankedInternalMgmtTurnoverSelector(),};

	/**
	 * This filter consist of the following event selectors
	 * <ol>
	 * <li>TYPE 8K events of 5.02 category.</li>
	 * <li>Low Ranked Management Turnover Selector.</li>
	 * </ol>
	 */
	public static final EventsFilter basicFilter = new BasicEventsFilter(EventFilterList.basicFilterList);

	/**
	 * This is basic signals filter for single company.
	 * 
	 * @see #signalsBasicFilterlistSingleCompany
	 */
	public static final EventsFilter signalsBasicFilterSingleCompany = new BasicEventsFilter(signalsBasicFilterlistSingleCompany);

	/**
	 * This is basic signals filter for multi company.
	 * 
	 * @see #signalsBasicFilterlistMultiCompany
	 */
	public static final EventsFilter signalsBasicFilterMultiCompany = new BasicEventsFilter(signalsBasicFilterlistMultiCompany);

	/**
	 * @see EventConsecutiveDayFilter
	 */
	public static final EventsFilter consecutiveDayFilter = new EventConsecutiveDayFilter();

	/**
	 * @see EventsDedupingFilter
	 */
	public static final EventsFilter dedupFilter = new EventsDedupingFilter();

	/**
	 * @see EventStockFilter
	 */
	public static final EventsFilter stockEventFilter = new EventStockFilter();

	/**
	 * @see GraphEventFilter
	 */
	public static final EventsFilter graphEventFilter = new GraphEventFilter();

	/**
	 * This is exclusion filter for multi company.
	 * 
	 * @see #multiCompanyExclusionSelectorList
	 */
	public static final EventsFilter multiCompanyExclusionFilter = new BasicEventsFilter(multiCompanyExclusionSelectorList);

}

