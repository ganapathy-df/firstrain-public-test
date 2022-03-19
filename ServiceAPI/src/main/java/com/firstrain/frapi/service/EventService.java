package com.firstrain.frapi.service;

import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.stereotype.Service;

import com.firstrain.frapi.FRService;
import com.firstrain.frapi.config.ServiceException;
import com.firstrain.frapi.domain.BaseSpec;
import com.firstrain.frapi.events.IEvents;
import com.firstrain.frapi.obj.EventQueryCriteria;
import com.firstrain.frapi.obj.EventQueryCriteria.EventTypeRange;
import com.firstrain.frapi.obj.MgmtTurnoverServiceSpec;


@Service
public interface EventService extends FRService {

	public SolrDocumentList getMgmtFromSolr(List<Integer> cids, MgmtTurnoverServiceSpec spec, boolean sortOnReportDate,
			EventTypeRange range, BaseSpec baseSpec) throws SolrServerException;

	public List<IEvents> getEntityEventsFromSolr(SolrDocumentList eventSolrList, Map<Integer, SolrDocument> eventDocMap);

	public List<IEvents> getEventsFromSolr(EventQueryCriteria criteria, Map<Integer, SolrDocument> eventDocMap) throws ServiceException;

	public SolrDocumentList getEventsDocsFromSolr(EventQueryCriteria criteria) throws SolrServerException;

	public List<IEvents> applyBSA(List<IEvents> input, int numEvents, boolean useMultiEntityFilter);

	public List<IEvents> applyBC(List<IEvents> input, boolean useMultiEntityFilter, int numOfEvents);

	public List<IEvents> applyGraphEventFilter(List<IEvents> eventList);

	public List<IEvents> getCompanyEvents(int companyId, String csExcludedEventTypeGroup, Map<Integer, SolrDocument> eventDocMap)
			throws ServiceException;

	public List<IEvents> applySingleCompanyEventsFilter(List<IEvents> eventsList, int numOfEvents, boolean applySignalFilter);

}
