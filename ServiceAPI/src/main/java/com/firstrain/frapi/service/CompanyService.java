package com.firstrain.frapi.service;

import java.util.Map;

import org.apache.solr.common.SolrDocument;
import org.springframework.stereotype.Service;

import com.firstrain.frapi.FRService;
import com.firstrain.frapi.domain.BaseSpec;
import com.firstrain.frapi.domain.MgmtTurnoverData;
import com.firstrain.frapi.obj.MgmtTurnoverServiceSpec;
import com.firstrain.frapi.pojo.EntityBriefInfo;
import com.firstrain.frapi.pojo.wrapper.EventSet;
import com.firstrain.web.pojo.EntityMatchInputBean;

@Service
public interface CompanyService extends FRService {

	public MgmtTurnoverServiceSpec getDefaultMonthlySpec() throws Exception;

	public MgmtTurnoverData getMgmtTurnoverData(MgmtTurnoverServiceSpec mtmtSpec, BaseSpec spec) throws Exception;

	public EventSet getCompanyEvents(BaseSpec spec, Map<Integer, SolrDocument> eventDocMap) throws Exception;

	public EventSet getCompanyEvents(BaseSpec spec, String csExcludedEventTypeGroup, Map<Integer, SolrDocument> eventDocMap)
			throws Exception;

	public EntityBriefInfo getCompanyAutoSuggestList(String q, EntityMatchInputBean entityMatchInputBean) throws Exception;

	public SolrDocument getCompanyDocuments(String searchToken) throws Exception;
}
