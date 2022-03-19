package com.firstrain.frapi.repository.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.firstrain.frapi.config.ServiceConfig;
import com.firstrain.frapi.config.ServiceException;
import com.firstrain.solr.client.util.SolrServerReader;

@Service
public class IndustryClassificationMap {

	private Map<Integer, Integer> industryClassificationMap = new ConcurrentHashMap<Integer, Integer>();
	private final Logger LOG = Logger.getLogger(IndustryClassificationMap.class);
	@Autowired
	@Qualifier("serviceConfig")
	private ServiceConfig serviceConfig;

	@PostConstruct
	public void init() throws ServiceException {
		if (serviceConfig == null) {
			throw new ServiceException("Can't initialize CompanyService due to null configuration.");
		}
		populateIndustryClassificationMapping();
	}

	@Scheduled(fixedDelay = 43200000)
	private void populateIndustryClassificationMapping() {
		String q = "classificationId:[* TO *]";
		SolrDocumentList solrDocumentList;
		try {
			solrDocumentList =
					SolrServerReader.retrieveNSolrDocs(serviceConfig.getEntitySolrServer(), q, 0, 1000, "attrCatId,classificationId");
			if (solrDocumentList != null && !solrDocumentList.isEmpty()) {
				industryClassificationMap.clear();
				for (SolrDocument doc : solrDocumentList) {
					String catIdStr = (String) doc.getFieldValue("attrCatId");
					int classificationId = (Integer) doc.getFieldValue("classificationId");
					industryClassificationMap.put(Integer.parseInt(catIdStr), classificationId);
				}
			}
		} catch (SolrServerException e) {
			LOG.error("Error loading industryClassificationMapping - " + e.getMessage(), e);
		}
		LOG.debug("Refreshed classification mapping, rows loaded: " + industryClassificationMap.size());
	}

	public Map<Integer, Integer> getIndustryClassificationMap() {
		return industryClassificationMap;
	}
}
