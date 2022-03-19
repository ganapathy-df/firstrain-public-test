package com.firstrain.frapi.service.impl;

import it.unimi.dsi.fastutil.ints.IntArrayList;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.firstrain.frapi.config.ServiceException;
import com.firstrain.frapi.domain.BaseSpec;
import com.firstrain.frapi.events.EventObj;
import com.firstrain.frapi.events.IEvents;
import com.firstrain.frapi.events.IEvents.EventEntityTypeEnum;
import com.firstrain.frapi.events.IEvents.EventGroupEnum;
import com.firstrain.frapi.events.IEvents.EventTypeEnum;
import com.firstrain.frapi.obj.EventQueryCriteria;
import com.firstrain.frapi.obj.EventQueryCriteria.EventTypeRange;
import com.firstrain.frapi.obj.MgmtTurnoverServiceSpec;
import com.firstrain.frapi.repository.EntityBaseServiceRepository;
import com.firstrain.frapi.service.EventService;
import com.firstrain.frapi.service.EventsFilter;
import com.firstrain.frapi.util.EventUtils;
import com.firstrain.frapi.util.FRAPIConstant;
import com.firstrain.mip.object.FR_IEvent;
import com.firstrain.mip.object.FR_IEventEntity;
import com.firstrain.mip.object.FR_IEventEvidence;
import com.firstrain.mip.object.FR_IMgmtChangeTrend;
import com.firstrain.mip.object.impl.FR_EventEvidence;
import com.firstrain.obj.IEntityInfo;
import com.firstrain.obj.IEntityInfoCache;
import com.firstrain.solr.client.util.SolrServerReader;
import com.firstrain.utils.FR_ArrayUtils;
import com.firstrain.utils.TimeUtils;

@Service
public class EventServiceImpl implements EventService {

	private final Logger LOG = Logger.getLogger(EventServiceImpl.class);

	@Autowired
	@Qualifier("entityBaseServiceRepositoryImpl")
	private EntityBaseServiceRepository entityBaseServiceRepository;

	private final String[] fields = new String[] {"eventId", "eventType", "eventDate", "score", "eventEntityFlag", "configXml", "companyId",
			"catId", "dayId", "evidenceIdSiteDocActive", "evidenceIdSiteDocInActive", "lastUpdatedMinuteId"};
	private final String[] MGMT_DETAIL_FIELDS = {"configXml", "eventDate", "companyId", "eventId", "eventType"};

	@Override
	public SolrDocumentList getMgmtFromSolr(List<Integer> cids, MgmtTurnoverServiceSpec spec, boolean sortOnReportDate,
			EventTypeRange range, BaseSpec baseSpec) throws SolrServerException {
		if (!spec.details) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(spec.lastDay);
		cal.add(Calendar.DATE, -spec.days);
		// DateRange dateRange = new DateRange(cal.getTime(), spec.lastDay, "");
		String from = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(cal.getTime());
		StringBuffer query = new StringBuffer();
		if (cids != null && !cids.isEmpty()) {
			query.append("companyId : (" + FR_ArrayUtils.getStringFromCollection(cids, " ") + ")  AND ");
		}
		if (spec.eventTypes != null && spec.eventTypes.length > 0) {
			query.append("eventType : (");
			for (int eventType : spec.eventTypes)
				query.append(eventType + " ");
			query.append(")");
		} else if (range == null) {
			query.append("eventType : [1 TO 299] ");
		} else {
			query.append("eventType : [" + range.getStartEventType() + " TO " + range.getEndEventType() + "]");
		}
		if (baseSpec.isCustomized()) {
			query.append(" AND lastUpdatedMinuteId : [" + baseSpec.getStartDate() + " TO " + baseSpec.getEndDate() + "]");
		} else {
			query.append(" AND lastUpdatedMinuteId : [" + from + " TO *]");
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("Query getMgmtFromSolr: " + query);
		}
		if (sortOnReportDate) {
			return SolrServerReader.retrieveNSolrDocs(entityBaseServiceRepository.getEventServer(), query.toString(), spec.getStart(),
					spec.getRows(), "lastUpdatedMinuteId", false, fields);
		} else {
			return SolrServerReader.retrieveNSolrDocs(entityBaseServiceRepository.getEventServer(), query.toString(), spec.getStart(),
					spec.getRows(), "dayId", false, MGMT_DETAIL_FIELDS);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IEvents> getEntityEventsFromSolr(SolrDocumentList eventSolrList, Map<Integer, SolrDocument> eventDocMap) {

		// transforming SolrDocumentList for Events to List<EventData>
		List<IEvents> eventDataList = new ArrayList<IEvents>();
		IEntityInfoCache entityCache = entityBaseServiceRepository.getEntityInfoCache();

		if (eventSolrList != null && !eventSolrList.isEmpty()) {

			for (SolrDocument eventDoc : eventSolrList) {

				int eventType = (Integer) eventDoc.getFieldValue("eventType");
				double score = ((List<Double>) eventDoc.getFieldValue("score")).get(0);
				EventTypeEnum eventEnum = EventTypeEnum.eventTypeMap.get(eventType);

				// Filter out bad web-volume events.
				if ((eventType >= 350 && eventType <= 399 && score <= 0) || eventEnum == null) {
					continue;
				}

				EventObj event = new EventObj();
				event.setEventTypeId(eventType);
				event.setScore(score);
				event.setEventType(eventEnum);

				event.setDate(getTimeStamp((Date) eventDoc.getFieldValue("eventDate")));
				if (eventDoc.getFieldValue("lastUpdatedMinuteId") != null) {
					event.setReportDate(getTimeStamp((Date) eventDoc.getFieldValue("lastUpdatedMinuteId")));
				}
				event.setDayId((Integer) eventDoc.getFieldValue("dayId"));
				event.setFlag((Integer) eventDoc.getFieldValue("eventEntityFlag"));
				event.setTrigger((event.getFlag() & FR_IEventEntity.FLAG_BITMAP_MASK_TRIGGER) == FR_IEventEntity.FLAG_BITMAP_MASK_TRIGGER);
				if (eventDoc.getFieldValue("companyId") != null) {
					event.setEntityId(((Integer) eventDoc.getFieldValue("companyId")).longValue());
					event.setEventEntityType(EventEntityTypeEnum.eventEntityTypeMap.get(FR_IEventEntity.ENTITY_TYPE_COMPANY));
				} else if (eventDoc.getFieldValue("catId") != null) {
					event.setEntityId(((Integer) eventDoc.getFieldValue("catId")).longValue());
					event.setEventEntityType(EventEntityTypeEnum.eventEntityTypeMap.get(FR_IEventEntity.ENTITY_TYPE_CATEGORY));
				}
				event.setEventId((Integer) eventDoc.getFieldValue("eventId"));
				// this map will be used to attach related document
				if (eventDocMap != null) {
					eventDocMap.put(event.getEventId(), eventDoc);
				}
				if (entityCache != null) {
					IEntityInfo ei = null;
					if (event.getEventEntityType() == EventEntityTypeEnum.TYPE_COMPANY) {
						ei = entityCache.companyIdToEntity((int) event.getEntityId());
					} else {
						ei = entityCache.catIdToEntity(String.valueOf(event.getEntityId()));
					}
					event.setEntityInfo(ei);
				}

				FR_EventEvidence eventEvidence = null;

				if (eventDoc.getFieldValue("evidenceIdSiteDocActive") != null) {
					eventEvidence = new FR_EventEvidence();
					eventEvidence.setEntityType(FR_IEventEvidence.ENTITY_TYPE_SITE_DOC);
					eventEvidence.enableFlagBits(new int[] {FR_IEventEvidence.FLAG_BIT_LINK_ACTIVE});
					eventEvidence.setEntityId(((Long) eventDoc.getFieldValue("evidenceIdSiteDocActive")).intValue());
				} else if (eventDoc.getFieldValue("evidenceIdSiteDocInActive") != null) {
					eventEvidence = new FR_EventEvidence();
					eventEvidence.setEntityType(FR_IEventEvidence.ENTITY_TYPE_SITE_DOC);
					eventEvidence.disableFlagBits(new int[] {FR_IEventEvidence.FLAG_BIT_LINK_ACTIVE});
					eventEvidence.setEntityId(((Long) eventDoc.getFieldValue("evidenceIdSiteDocInActive")).intValue());
				}

				String configXML = ((String) eventDoc.getFieldValue("configXml"));
				// parse config xml to extract corresponding information
				if (configXML != null && !configXML.isEmpty()) {
					configXML = configXML.trim();
					event.setConfigXml(configXML);

					if (eventEvidence != null) {
						event.setEventExpired(!eventEvidence.isFlagBitEnabled(FR_IEventEvidence.FLAG_BIT_LINK_ACTIVE));
						event.addPrimaryEvidence(eventEvidence.getEntityId());
					}

					// Management Change : 1-299
					if (eventType >= 1 && eventType <= 299) {
						event.setCaption(eventEnum.getLabel());
						event.setLinkable(true);
						event.setEventGroup(EventGroupEnum.GROUP_MGMT_CHANGE);
						// Special case for turnover stuff.
						// Do it in a non-lazy way.
						LinkedHashMap<String, Object> props = new LinkedHashMap<String, Object>();
						event.setProps(props);

						FR_IMgmtChangeTrend mc = EventUtils.parserMgmtConfig(configXML);

						// Company Name, Ticker, Event Date, Event Type, Old Company, Old Designation,
						// Old Group, Old Region, New Company, New Designation, New Group, New Region, Change Type,
						props.put("trendID", mc.getTrendID());
						if (mc.getPersonName() != null && mc.getPersonName().length() > 0) {
							props.put("name", mc.getPersonName());
						}
						props.put("oldCompanyID", mc.getOldCompanyID());
						IEntityInfo ei = entityCache.companyIdToEntity(mc.getOldCompanyID());
						if (ei != null) {
							props.put("oldCompanyEntity", ei);
						}
						props.put("oldDesignation", mc.getOldPositionTitle());
						props.put("oldGroup", mc.getOldGroup());
						props.put("newGroup", mc.getNewGroup());
						props.put("oldRegion", mc.getOldRegion());
						props.put("newRegion", mc.getNewRegion());
						props.put("newCompanyID", mc.getNewCompanyID());
						ei = entityCache.companyIdToEntity(mc.getNewCompanyID());
						if (ei != null) {
							props.put("newCompanyEntity", ei);
						}
						props.put("newDesignation", mc.getNewPositionTitle());

						event.setCaption(EventGroupEnum.GROUP_MGMT_CHANGE.getLabel());
						event.setUrl(mc.getSupportingURL());

					}
					// Stock Price : 300-349
					else if (eventType >= 300 && eventType <= 349) {

						event.setEventGroup(EventGroupEnum.GROUP_STOCK_PRICE);
						event.setLinkable(false);
						event.setCaption(eventEnum.getLabel());
					}
					// Web Volume : 350-399
					else if (eventType >= 350 && eventType <= 399) {
						event.setEventGroup(EventGroupEnum.GROUP_WEB_VOLUME);
						event.setLinkable(true);
					}
					// 8K Filing : 400-499
					else if (eventType >= 400 && eventType <= 499) {
						event.setEventGroup(EventGroupEnum.GROUP_8K_FILING);
						event.setLinkable(true);
						event.setCaption(eventEnum.getLabel());
					} else if (eventType >= 500 && eventType <= 549) {
						event.setEventGroup(EventGroupEnum.GROUP_DELAYED_FILING);
						event.setLinkable(true);
					}
				}

				eventDataList.add(event);
			}
		}
		return eventDataList;
	}

	private Timestamp getTimeStamp(Date date) {
		return new Timestamp(date.getTime());
	}

	@Override
	public List<IEvents> getEventsFromSolr(EventQueryCriteria criteria, Map<Integer, SolrDocument> eventDocMap) throws ServiceException {
		List<IEvents> eventData = new ArrayList<IEvents>();
		try {
			long start = System.nanoTime();
			SolrDocumentList eventSolrList = getEventsDocsFromSolr(criteria);
			long end = System.nanoTime();
			if (eventSolrList != null && !eventSolrList.isEmpty()) {
				LOG.debug("Time for getting events list: Loaded " + eventSolrList.size() + " rows in " + (end - start) / 1000000 + " ms.");
				eventData = getEntityEventsFromSolr(eventSolrList, eventDocMap);

				/* Apply Basic Cleanup */
				// applyBC(eventData, criteria.getCompanyIds().length > 1, criteria.getNoOfEvents());
			}

		} catch (Exception e) {
			LOG.error(" Error occurred while fetching events", e);
			throw new ServiceException(e);
		}
		return eventData;
	}

	@Override
	public SolrDocumentList getEventsDocsFromSolr(EventQueryCriteria criteria) throws SolrServerException {

		int start = 0;
		int rows = criteria.getNoOfEvents(); // default is DEFAULT_MAX_EVENTS (currently 300 events being extracted)
		StringBuffer query = new StringBuffer();
		query.append("(");
		// create query for companyId if it is available
		if (criteria.getCompanyIds() != null && criteria.getCompanyIds().length > 0) {
			query.append("companyId : (");
			for (int companyId : criteria.getCompanyIds()) {
				query.append(companyId + " ");
			}
			query.append(")");
		}
		// create query for topicId if it is available
		if (criteria.getTopicIds() != null && criteria.getTopicIds().length > 0) {
			// if both companyId or topicId are available then query for "OR"
			if (query.length() > 1)
				query.append(" OR ");

			query.append("catId : (");

			for (int topicId : criteria.getTopicIds()) {
				query.append(topicId + " ");
			}

			query.append(")");
		}
		query.append(")");

		int[] eventTypes = criteria.getEventTypeIds();
		if (eventTypes != null && eventTypes.length > 0) {
			appendExcludedEventTypes(query, " AND eventType : (", eventTypes);
		} else if (criteria.getListOfEventTypeRange() != null) {
			query.append(" AND (");
			for (EventTypeRange range : criteria.getListOfEventTypeRange()) {
				if (range == null)
					continue;
				query.append("eventType : [" + range.getStartEventType() + " TO " + range.getEndEventType() + "] OR ");
			}
			query.delete(query.length() - 3, query.length() - 1);
			query.append(" ) ");
		} else {
			EventTypeRange range = criteria.getEventTypeRange();
			query.append(" AND eventType : [" + range.getStartEventType() + " TO " + range.getEndEventType() + "]");
		}

		int[] excludedEventTypeIds = criteria.getExcludedEventTypeIds();
		if (excludedEventTypeIds != null && excludedEventTypeIds.length > 0) {
			appendExcludedEventTypes(query, " AND NOT eventType : (", excludedEventTypeIds);
		}

		// create query for excludedCatIds if available
		int[] excludedCatIds = criteria.getExcludedCatIds();
		if (excludedCatIds != null && excludedCatIds.length > 0) {
			appendExcludedEventTypes(query, " AND NOT catIdAll : (", excludedCatIds);
		}

		if (criteria.isSetForFutureEvent()) {
			query.append(" AND dayId : [" + (TimeUtils.getDayNumber(new Date()) + 1) + " TO *]");
		} else {
			// events will be pulled from start date to last date specified below
			int lastDayId = TimeUtils.getDayNumber(criteria.getLastDay()); // default Last Day is current date
			int startDayId = lastDayId - criteria.getDays(); // default getDays() is DEFAULT_MAX_EVENTS_DAYS(=180)
			query.append(" AND dayId : [" + startDayId + " TO ");
			if (criteria.isApplyEndDateRange()) {
				query.append(lastDayId);
			} else {
				query.append("*");
			}
			query.append("]");
		}

		LOG.debug("Events Solr Query : " + query);

		return SolrServerReader.retrieveNSolrDocs(entityBaseServiceRepository.getEventServer(), query.toString(), start, rows, "dayId",
				false, fields);
	}

	private void appendExcludedEventTypes(final StringBuffer query, final String andCondition, final int[] excludedEventTypeIds) {
		query.append(andCondition);
		for (int excludedEventType : excludedEventTypeIds)
			query.append(excludedEventType + " ");
		query.append(")");
	}

	@Override
	public List<IEvents> applyBSA(List<IEvents> input, int numEvents, boolean useMultiEntityFilter) {
		EventsFilter ef;
		if (useMultiEntityFilter) {
			ef = new MultiEntityEventsFilter(numEvents);
		} else {
			ef = new SingleCompanyEventsFilter(numEvents);
		}
		return ef.filterEvents(input);
	}

	@Override
	public List<IEvents> applyBC(List<IEvents> input, boolean useMultiEntityFilter, int numOfEvents) {
		EventsFilter ef;
		if (useMultiEntityFilter) {
			ef = new MultiEntityEventsFilter(numOfEvents + 1, false, false, null);
		} else {
			ef = new SingleCompanyEventsFilter(numOfEvents + 1);
		}
		return ef.filterEvents(input);
	}

	@Override
	public List<IEvents> applyGraphEventFilter(List<IEvents> eventList) {
		EventsFilter gef = new GraphEventFilter();
		return gef.filterEvents(eventList);
	}

	@Override
	public List<IEvents> getCompanyEvents(int companyId, String csExcludedEventTypeGroup, Map<Integer, SolrDocument> eventDocMap)
			throws ServiceException {

		IntArrayList compIds = new IntArrayList();
		compIds.add(companyId);
		EventQueryCriteria criteria = new EventQueryCriteria(compIds.toIntArray(), null, FRAPIConstant.EVENTS_DAYS);
		if (csExcludedEventTypeGroup != null && !csExcludedEventTypeGroup.isEmpty()) {
			int[] excludedGroups = FR_ArrayUtils.getIntArrFromString(csExcludedEventTypeGroup);
			List<EventTypeRange> range = getIncludedEventTypeRange(excludedGroups);
			criteria.setListOfEventTypeRange(range);
		}
		criteria.setNoOfEvents(FRAPIConstant.MAX_NO_OF_EVENTS);
		return getEventsFromSolr(criteria, eventDocMap);
	}

	@Override
	public List<IEvents> applySingleCompanyEventsFilter(List<IEvents> eventsList, int numOfEvents, boolean applySignalFilter) {
		EventsFilter ef = new SingleCompanyEventsFilter(numOfEvents, applySignalFilter);
		return ef.filterEvents(eventsList);
	}

	private List<EventTypeRange> getIncludedEventTypeRange(int[] excludedEventTypeGroups) {
		// default criteria is 1-500 event types.so initializing it to 1-500 first.
		List<EventTypeRange> includedEventTypeRange = initializeDefaultEventTypeRange();

		if (excludedEventTypeGroups == null || excludedEventTypeGroups.length == 0) {
			return includedEventTypeRange;
		}
		for (int groupId : excludedEventTypeGroups) {
			try {
				includedEventTypeRange.set(groupId - 1, null);
			} catch (Exception e) {
				LOG.error("Wrong excluded event type group provided, out of the valid range 1-5 " + groupId);
			}
		}
		return includedEventTypeRange;
	}

	private List<EventTypeRange> initializeDefaultEventTypeRange() {
		List<EventTypeRange> list = new ArrayList<EventQueryCriteria.EventTypeRange>();
		list.add(new EventTypeRange(FR_IEvent.RANGE_TYPE_MGMT_CHANGE_MIN, FR_IEvent.RANGE_TYPE_MGMT_CHANGE_MAX));
		list.add(new EventTypeRange(FR_IEvent.RANGE_TYPE_STOCK_PRICE_MIN, FR_IEvent.RANGE_TYPE_STOCK_PRICE_MAX));
		list.add(new EventTypeRange(FR_IEvent.RANGE_TYPE_WEB_VOLUME_MIN, FR_IEvent.RANGE_TYPE_WEB_VOLUME_MAX));
		list.add(new EventTypeRange(FR_IEvent.RANGE_TYPE_8K_FILING_MIN, FR_IEvent.RANGE_TYPE_8K_FILING_MAX));
		list.add(new EventTypeRange(FR_IEvent.RANGE_TYPE_DELAYED_FILING_MIN, FR_IEvent.RANGE_TYPE_DELAYED_FILING_MIN));
		return list;
	}

}
