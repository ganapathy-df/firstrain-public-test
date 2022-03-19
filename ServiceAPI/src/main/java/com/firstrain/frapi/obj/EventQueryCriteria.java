package com.firstrain.frapi.obj;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.firstrain.mip.object.FR_IEvent;
import com.firstrain.utils.FR_ArrayUtils;


/**
 * This class is to specify various criteria that are needed for querying events from persistent media.
 * <p>
 * Always works within the allowed date range specified by <code>DEFAULT_MAX_EVENTS_DAYS</code>.
 * </p>
 * 
 * @author Deepak
 * @see #DEFAULT_MAX_EVENTS_DAYS
 */
public class EventQueryCriteria {

	/*
	 * Param: 1. CompanyIds OR TopicIds OR Both 2. [ENdDate & -days] OR default [180 days] 3. [startEventType & EndEventType] OR [default
	 * event type range]
	 */

	private static final Logger LOG = Logger.getLogger(EventQueryCriteria.class);

	public static final int DEFAULT_MAX_EVENTS_DAYS = 180;
	public static final int DEFAULT_MAX_EVENTS = 300;

	private int[] companyIds;
	private int[] topicIds;
	private int[] excludedCatIds;
	private int days = DEFAULT_MAX_EVENTS_DAYS;
	private Date lastDay;
	private EventTypeRange eventTypeRange;

	private List<EventTypeRange> listOfEventTypeRange;
	// Default event type to exclude always.
	private int[] excludedEventTypeIds = new int[] {FR_IEvent.TYPE_EIGHT_K_EVENT_ITEM_9_01};
	private int noOfEvents = DEFAULT_MAX_EVENTS;
	private int[] eventTypeIds;
	private boolean applyEndDateRange = true;

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd H:m:s.S");

	private int start;

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	/**
	 * Operates on a default of 180 days.
	 * 
	 * @param companyIds
	 * @param topicIds
	 */
	public EventQueryCriteria(int[] companyIds, int[] topicIds) {
		this(companyIds, topicIds, EventTypeRange.getDefaultEventTypeRange());
	}

	public EventQueryCriteria(int[] companyIds, int[] topicIds, EventTypeRange range) {
		init(companyIds, topicIds, null, DEFAULT_MAX_EVENTS_DAYS, range, false);
	}

	public EventQueryCriteria(int[] companyIds, int[] topicIds, int days, EventTypeRange range) {
		init(companyIds, topicIds, null, days, range, false);
	}

	public EventQueryCriteria(int[] companyIds, int[] topicIds, EventTypeRange range, boolean futureEventsOnly) {
		this(companyIds, topicIds, range, DEFAULT_MAX_EVENTS_DAYS, futureEventsOnly);
	}

	public EventQueryCriteria(int[] companyIds, int[] topicIds, EventTypeRange range, int days, boolean futureEventsOnly) {
		if (futureEventsOnly) {
			validateAndPopulateIds(companyIds, topicIds);

			populateEventTypeRange(range); 
			
		} else {
			init(companyIds, topicIds, null, days, range, false);
		}
	}

	public EventQueryCriteria(int[] companyIds, int[] topicIds, int days) {
		this(companyIds, topicIds, null, days);
	}

	public EventQueryCriteria(int[] companyIds, int[] topicIds, Date lastDay, int days) {
		this(companyIds, topicIds, lastDay, days, EventTypeRange.getDefaultEventTypeRange());
	}

	public EventQueryCriteria(int[] companyIds, int[] topicIds, Date lastDay, int days, EventTypeRange range) {
		init(companyIds, topicIds, lastDay, days, range, false);
	}

	public EventQueryCriteria(int[] companyIds, int[] topicIds, Date lastDay, int days, EventTypeRange range, boolean ignoredaysRange) {
		init(companyIds, topicIds, lastDay, days, range, ignoredaysRange);
	}

	private void init(int[] companyIds, int[] topicIds, Date lastDay, int days, EventTypeRange range, boolean ignoredaysRange) {
		validateAndPopulateIds(companyIds, topicIds);
		// Set this to today as default
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		this.lastDay = cal.getTime();

		// Override lastDay and days only when provided.
		if (lastDay != null) {
			this.lastDay = lastDay;
			if (!ignoredaysRange && !isAllowableDateRange(this.lastDay, days)) {
				throw new IllegalArgumentException("{lastDay} " + this.lastDay + " & {days} " + days
						+ " not in allowable range, max operating range is " + DEFAULT_MAX_EVENTS_DAYS + " days in past.");
			}

		} else if (!ignoredaysRange && days > DEFAULT_MAX_EVENTS_DAYS) {
			throw new IllegalArgumentException(
					"{days}" + days + " not in allowable range, max operating range is " + DEFAULT_MAX_EVENTS_DAYS + " days in past.");
		}

		this.days = days;
		populateEventTypeRange(range); 
		
	}

	private void validateAndPopulateIds(final int[] companyIds, final int[] topicIds) {
		if ((companyIds == null || companyIds.length == 0) && (topicIds == null || topicIds.length == 0)) {
			throw new IllegalArgumentException("Either companyIds or topicIds need to be specified.");
		}
		
		this.companyIds = companyIds;
		this.topicIds = topicIds;
	}
 
	private void populateEventTypeRange(final EventTypeRange range) { 
		this.eventTypeRange = range; 
		if (this.eventTypeRange == null) { 
			this.eventTypeRange = EventTypeRange.getDefaultEventTypeRange(); 
			LOG.info("Parameter {EventTypeRange range} was null, overriding with defaults."); 
		} 
	} 
	

	public int[] getCompanyIds() {
		return this.companyIds;
	}

	public int[] getTopicIds() {
		return this.topicIds;
	}

	public int[] getExcludedCatIds() {
		return excludedCatIds;
	}

	public int getDays() {
		return this.days;
	}

	public Date getLastDay() {
		return this.lastDay;
	}

	public String getLastDayAsString() {
		return this.sdf.format(this.lastDay);
	}

	public EventTypeRange getEventTypeRange() {
		return this.eventTypeRange;
	}

	public void setNoOfEvents(int noOfEvents) {
		this.noOfEvents = noOfEvents;
	}

	public int getNoOfEvents() {
		return this.noOfEvents;
	}

	/**
	 * This overrides the previously defined EventTypeRange if any, since eventTypeIds has higher priority.
	 * 
	 * @param eventTypeIds
	 */
	public void setEventTypeIds(int[] eventTypeIds) {
		if (eventTypeIds == null || eventTypeIds.length == 0) {
			throw new IllegalArgumentException("Eventtype ids must be supplied for proper functioning.");
		}
		LOG.debug("Setting eventTypeRange to null in respect to eventTypeIds.");
		this.eventTypeRange = null;
		this.eventTypeIds = eventTypeIds;
	}

	public int[] getEventTypeIds() {
		return this.eventTypeIds;
	}

	/**
	 * Set this to exclude some event types in query(optional), default event type to exclude is {@link FR_IEvent}
	 * .TYPE_EIGHT_K_EVENT_ITEM_9_01 [#428]
	 * <p/>
	 * The resulting eventypes will be including the default event type.
	 * 
	 * @param excludedEventTypeIds the excludedEventTypeIds to set
	 */
	public void setExcludedEventTypeIds(int[] excludedEventTypeIds) {
		this.excludedEventTypeIds = FR_ArrayUtils.mergeIds(excludedEventTypeIds, this.excludedEventTypeIds);
	}

	/**
	 * Set this to exclude some event types in query(optional), default event type to exclude is {@link FR_IEvent}
	 * .TYPE_EIGHT_K_EVENT_ITEM_9_01 [#428]
	 * <p/>
	 * The resulting eventypes may include the default event type also depending on honorDefaultExcludedIds parameter.
	 * 
	 * @param excludedEventTypeIds the excludedEventTypeIds to set
	 * @param honorDefaultExcludedIds Set this to <code>true</code> if default event type ids is to be included in exclusion list.
	 * @see #setExcludedEventTypeIds(int[])
	 */
	public void setExcludedEventTypeIds(int[] excludedEventTypeIds, boolean honorDefaultExcludedIds) {
		if (honorDefaultExcludedIds) {
			setExcludedEventTypeIds(excludedEventTypeIds);
			return;
		}
		this.excludedEventTypeIds = excludedEventTypeIds;
	}

	public void setExcludedCatIds(int[] excludedCatIds) {
		this.excludedCatIds = excludedCatIds;
	}

	/**
	 * @return the excludedEventTypeIds
	 */
	public int[] getExcludedEventTypeIds() {
		return this.excludedEventTypeIds;
	}

	/**
	 * Indicates whether event end date range should be applied during query, default is false.
	 * 
	 * @param applyEndDateRange the applyEndDateRange to set
	 */
	public void setApplyEndDateRange(boolean applyEndDateRange) {
		this.applyEndDateRange = applyEndDateRange;
	}

	/**
	 * Indicates whether event end date range should be applied during query.
	 * 
	 * @return the applyEndDateRange
	 */
	public boolean isApplyEndDateRange() {
		return this.applyEndDateRange;
	}

	public boolean isSetForFutureEvent() {
		return this.lastDay == null;
	}

	/**
	 * This is to specify the event type range to operate on, also provides a default allowable event type range for convenience.
	 * <p>
	 * Currently allowed event type range is FR_IEvent.TYPE_MGMT_CHANGE_CEO_TURNOVER - FR_IEvent.TYPE_DELAYED_SEC_FILING.
	 * </p>
	 * 
	 * @author Deepak
	 * @see FR_IEvent
	 */
	public static class EventTypeRange {
		private int startEventType;
		private int endEventType;

		/**
		 * Throws IllegalArgumentException if the provided event-types are not supported in current context.
		 * <p>
		 * Default runs in <strong>strictRangeCheck</strong> mode, this is the preferable way for general use.
		 * </p>
		 * 
		 * @param startEventTypeId
		 * @param endEventTypeId
		 * @throws IllegalArgumentException
		 */
		public EventTypeRange(int startEventTypeId, int endEventTypeId) throws IllegalArgumentException {
			this(startEventTypeId, endEventTypeId, true);
		}

		/**
		 * Throws IllegalArgumentException if the provided event-types are not supported in current context, depends on
		 * <code>strictRangeCheck</code> settings, if true ranges are checked to not trespass allowable limit.
		 * 
		 * @param startEventTypeId
		 * @param endEventTypeId
		 * @param strictRangeCheck
		 * @throws IllegalArgumentException
		 */
		public EventTypeRange(int startEventTypeId, int endEventTypeId, boolean strictRangeCheck) throws IllegalArgumentException {
			if (strictRangeCheck) {
				if (!isAllowableEventType(startEventTypeId)) {
					throw new IllegalArgumentException(
							"{startEventTypeId} " + startEventTypeId + " is not in allowable range " + getAllowableRangeString());
				}
				if (!isAllowableEventType(endEventTypeId)) {
					throw new IllegalArgumentException(
							"{endEventTypeId} " + endEventTypeId + " is not in allowable range " + getAllowableRangeString());
				}
			}
			this.startEventType = startEventTypeId;
			this.endEventType = endEventTypeId;
		}

		public static EventTypeRange getDefaultEventTypeRange() {
			return new EventTypeRange(FR_IEvent.TYPE_MGMT_CHANGE_CEO_TURNOVER, FR_IEvent.TYPE_DELAYED_SEC_FILING);
		}

		private static String getAllowableRangeString() {
			EventTypeRange range = getDefaultEventTypeRange();
			return "[" + range.getStartEventType() + ":" + range.getEndEventType() + "]";
		}

		public static boolean isAllowableEventType(int eventTypeId) {
			if (eventTypeId >= FR_IEvent.TYPE_MGMT_CHANGE_CEO_TURNOVER && eventTypeId <= FR_IEvent.TYPE_DELAYED_SEC_FILING) {
				return true;
			}
			return false;
		}

		public int getStartEventType() {
			return this.startEventType;
		}

		public int getEndEventType() {
			return this.endEventType;
		}

	}

	private boolean isAllowableDateRange(Date lastDay, int days) {
		// Set calendar to acceptable day in past.
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.add(Calendar.DATE, -DEFAULT_MAX_EVENTS_DAYS + 1);

		Date allowedStartDay = cal.getTime();

		// Set calendar to desired day in past.
		cal.setTime(lastDay);
		cal.add(Calendar.DATE, -days + 1);

		Date desiredStartDay = cal.getTime();

		return allowedStartDay.compareTo(desiredStartDay) <= 0;
	}

	public List<EventTypeRange> getListOfEventTypeRange() {
		return listOfEventTypeRange;
	}

	public void setListOfEventTypeRange(List<EventTypeRange> listOfEventTypeRange) {
		this.listOfEventTypeRange = listOfEventTypeRange;
	}
}
