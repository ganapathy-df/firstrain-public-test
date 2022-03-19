package com.firstrain.frapi.repository;

import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.firstrain.content.similarity.DocumentSimilarityUtil;
import com.firstrain.frapi.config.ServiceConfig;
import com.firstrain.obj.IEntityInfo;
import com.firstrain.obj.IEntityInfoCache;
import com.firstrain.solr.client.EntityEntry;
import com.firstrain.solr.client.SolrSearcher;

@Service
@Qualifier("entityBaseServiceRepository")
public interface EntityBaseServiceRepository {

	public ServiceConfig getServiceConfig();

	public IEntityInfoCache getEntityInfoCache();

	public SolrSearcher getSearcher();

	public SolrServer getEntitySolrServer();

	public SolrServer getFavIconServer();

	public SolrServer getDocImageServer();

	public SolrServer getTwitterServer();

	public SolrServer getTwitterGroupServer();

	public SolrServer getDocSolrServer();

	public SolrServer getEventServer();

	public SolrServer getPeopleServer();

	public SolrDocumentList getSolrDocForSourceSearchToken(String searchToken) throws Exception;

	public SolrDocumentList getSolrDocFromIndustryAndBizlineCatIds(List<String> industryCatIds, List<String> bizLineCatds,
			String[] FIELDS_TO_RETURN, boolean needAllEntities, boolean isCustomized) throws Exception;

	public void populateCompanyTopicsFromSolrDocs(SolrDocumentList docList, List<IEntityInfo> companies, List<IEntityInfo> topics,
			List<String> exchangeList) throws Exception;

	public DocumentSimilarityUtil getDocumentSimilarityUtil();

	public List<EntityEntry> getContextMatchEntities(String keywordOrPhrase, Map<Integer, Integer> industryClassfMap);
}
