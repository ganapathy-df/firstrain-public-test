package com.firstrain.frapi.repository.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.firstrain.content.similarity.DocumentSimilarityUtil;
import com.firstrain.frapi.config.ServiceConfig;
import com.firstrain.frapi.config.ServiceException;
import com.firstrain.frapi.repository.EntityBaseServiceRepository;
import com.firstrain.obj.IEntityInfo;
import com.firstrain.obj.IEntityInfoCache;
import com.firstrain.solr.client.DistributedSolrSearcher;
import com.firstrain.solr.client.EntityEntry;
import com.firstrain.solr.client.EntityInfoCache;
import com.firstrain.solr.client.EntityInfoCache.CacheSearchSpec;
import com.firstrain.solr.client.EntityInfoCacheRegistry;
import com.firstrain.solr.client.HotListEntry;
import com.firstrain.solr.client.SearchResult;
import com.firstrain.solr.client.SearchSpec;
import com.firstrain.solr.client.SearchTokenEntry;
import com.firstrain.solr.client.SolrSearcher;
import com.firstrain.solr.client.util.SolrServerReader;
import com.firstrain.utils.FR_ArrayUtils;

@Service
@Qualifier("entityBaseServiceRepositoryImpl")
public class EntityBaseServiceRepositoryImpl implements EntityBaseServiceRepository {

	@Autowired
	@Qualifier("serviceConfig")
	private ServiceConfig serviceConfig;

	private IEntityInfoCache entityInfoCache;
	private SolrSearcher searcher;
	private SolrServer entitySolrServer;
	private SolrServer docSolrServer;
	private SolrServer favIconServer;
	private SolrServer docImageServer;
	private SolrServer twitterServer;
	private SolrServer twitterGroupServer;
	private SolrServer eventServer;
	private SolrServer peopleServer;
	private final int MAX_SOLR_CHUNK_SIZE = 450;
	private final int MAX_SOLR_CHUNK_SIZE_100000 = 100000;
	private final Logger LOG = Logger.getLogger(EntityBaseServiceRepositoryImpl.class);

	private static final String[] SOURCE_FILEDS_TO_RETURN = new String[] {"attrDomain", "country", "industry", "sector", "segment", "title"};

	@PostConstruct
	public void init() throws ServiceException {
		if (serviceConfig == null) {
			throw new ServiceException("Can't initialize CompanyService due to null configuration.");
		}
		try {
			if (serviceConfig.getDistributedSearchConfig() != null) {
				this.searcher = new DistributedSolrSearcher(serviceConfig.getDistributedSearchConfig());
				this.setDocSolrServer(serviceConfig.getDistributedSearchConfig().docServers[0]);
			} else {
				this.searcher = new SolrSearcher();
				this.searcher.setDocSolrServer(serviceConfig.getDocSolrServer());
				this.setDocSolrServer(this.searcher.getDocSolrServer());
			}

			this.searcher.setEntitySolrServer(serviceConfig.getEntitySolrServer());
			this.entitySolrServer = serviceConfig.getEntitySolrServer();
			this.favIconServer = serviceConfig.getFaviconSolrServer();
			this.docImageServer = serviceConfig.getDocImageSolrServer();
			this.twitterServer = serviceConfig.getTwitterSolrServer();
			this.twitterGroupServer = serviceConfig.getTweetGroupSolrServer();
			this.eventServer = serviceConfig.getEventSolrServer();
			this.peopleServer = serviceConfig.getPeopleSolrServer();

			CacheSearchSpec ccs = new EntityInfoCache.CacheSearchSpec();
			ccs.entitySearchString = CacheSearchSpec.ENTITY_ALL_CATEGORY_AND_SOURCE;
			this.entityInfoCache = EntityInfoCacheRegistry.getGlobalEntityInfoCache(entitySolrServer, true, ccs);

			// this.entityInfoCache = EntityInfoCacheRegistry.getGlobalEntityInfoCache(entitySolrServer, true);

		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public SolrDocumentList getSolrDocForSourceSearchToken(String searchToken) throws Exception {
		SolrDocumentList solrDocumentList = null;
		if (StringUtils.isEmpty(searchToken)) {
			return null;
		}

		String query = "attrSearchToken:\"" + searchToken + "\"";
		if (LOG.isDebugEnabled()) {
			LOG.debug("Solr Query : " + query);
		}
		return SolrServerReader.retrieveNSolrDocs(this.getEntitySolrServer(), query, 0, 1, SOURCE_FILEDS_TO_RETURN);
	}

	@Override
	public SolrDocumentList getSolrDocFromIndustryAndBizlineCatIds(List<String> industryCatIds, List<String> bizLineCatds,
			String[] FIELDS_TO_RETURN, boolean needAllEntities, boolean isCustomized) throws Exception {
		SolrDocumentList solrDocumentList = null;
		if ((industryCatIds == null || industryCatIds.isEmpty()) && (bizLineCatds == null || bizLineCatds.isEmpty())) {
			return null;
		}

		StringBuilder catIdsIndustry = new StringBuilder();
		StringBuilder catIdsSegment = new StringBuilder();
		StringBuilder catIdsSector = new StringBuilder();

		if (industryCatIds != null && !industryCatIds.isEmpty()) {
			for (String catId : industryCatIds) {
				IEntityInfo entityInfo = entityInfoCache.catIdToEntity(catId);
				if (entityInfo != null) {
					if (entityInfo.getIndustryCatId() > 0) {
						catIdsIndustry.append(catId).append(" ");
					} else if (entityInfo.getSegmentCatId() > 0) {
						catIdsSegment.append(catId).append(" ");
					} else if (entityInfo.getSectorCatId() > 0) {
						catIdsSector.append(catId).append(" ");
					} else if (isCustomized) {
						// MT support only for I:
						catIdsSector.append(catId).append(" ");
					}
				}
			}
		}
		// Sample query: (type:Company AND -attrExclude:all AND industry:512365 AND docCount:[0 TO *]) OR (type:Company AND -attrExclude:all
		// AND bizLineCatIds:514702 AND docCount:[0 TO *])
		String type = "type:Company";
		StringBuilder finalQuery = new StringBuilder();
		if (bizLineCatds != null && !bizLineCatds.isEmpty()) {
			appendFinalQuery(finalQuery, type, " AND ");
			finalQuery.append(" bizLineCatIds:(" + FR_ArrayUtils.getStringFromCollection(bizLineCatds, " ") + "))");
		}

		if (bizLineCatds != null && !bizLineCatds.isEmpty() && industryCatIds != null && !industryCatIds.isEmpty()) {
			finalQuery.append(" OR ");
		}

		if (catIdsIndustry.length() > 0 || catIdsSector.length() > 0 || catIdsSegment.length() > 0) {
			appendFinalQuery(finalQuery, type, " AND (");

			// Lets process For Industry
			if (catIdsIndustry.length() > 0) {
				finalQuery.append(" industry:(" + catIdsIndustry.toString() + ") OR secondaryIndustry:(" + catIdsIndustry.toString() + ")");
			}

			// Lets process For Sector
			if (catIdsSector.length() > 0) {
				if (catIdsIndustry.length() > 0) {
					finalQuery.append(" OR ");
				}
				finalQuery.append(" sector:(" + catIdsSector.toString() + ") OR secondarySector:(" + catIdsSector.toString() + ")");
			}

			// Lets process For Segment
			if (catIdsSegment.length() > 0) {
				if (catIdsIndustry.length() > 0 || catIdsSector.length() > 0) {
					finalQuery.append(" OR ");
				}
				finalQuery.append(" segment:(" + catIdsSegment.toString() + ") OR secondarySegment:(" + catIdsSegment.toString() + ")");
			}
			finalQuery.append(") )");
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("getSolrDocFromIndustryAndBizlineCatIds " + finalQuery.toString());
		}

		int chunkSize = MAX_SOLR_CHUNK_SIZE;
		if (isCustomized) {
			chunkSize = MAX_SOLR_CHUNK_SIZE_100000;
		}

		solrDocumentList = SolrServerReader.retrieveNSolrDocs(this.getEntitySolrServer(), finalQuery.toString(), 0, chunkSize,
				"attrVolumeRecentWeek", false, FIELDS_TO_RETURN);
		if (needAllEntities || solrDocumentList == null || solrDocumentList.size() < chunkSize) {
			finalQuery.replace(1, type.length() + 1, "type:Topic");
			LOG.debug("Query for topic getSolrDocFromIndustryAndBizlineCatIds " + finalQuery.toString());
			SolrDocumentList topicSolrDocumentList = SolrServerReader.retrieveNSolrDocs(this.getEntitySolrServer(), finalQuery.toString(), 0,
					chunkSize, "attrVolumeRecentWeek", false, FIELDS_TO_RETURN);
			if (topicSolrDocumentList != null) {
				if (solrDocumentList == null) {
					solrDocumentList = topicSolrDocumentList;
				} else {
					solrDocumentList.addAll(topicSolrDocumentList);
				}
			}
		}

		return solrDocumentList;
	}

	private void appendFinalQuery(final StringBuilder finalQuery, final String type, final String condition) {
		finalQuery.append("(").append(type);
		
		finalQuery.append(" AND docCount:[* TO *] AND -attrExclude:all");
		finalQuery.append(condition);
	}

	@Override
	public List<EntityEntry> getContextMatchEntities(String keywordOrPhrase, Map<Integer, Integer> industryClassfMap) {
		List<EntityEntry> resList = new ArrayList<EntityEntry>();
		try {
			SearchSpec searchSpec = new SearchSpec();
			searchSpec.q = keywordOrPhrase;
			searchSpec.useLikelySearchIntention = true;
			searchSpec.optimalScopeAndDays = true;
			searchSpec.needHotListAll = false;
			searchSpec.setOrder(SearchSpec.ORDER_DATE);
			searchSpec.needHotListIndustry = true;
			searchSpec.setIndustryClassificationId((short) 1);

			SearchResult result = this.getSearcher().search(searchSpec);
			if (result != null && result.facetIndustries != null && result.facetIndustries.entries != null) {
				// sort on the basis of strength - doccount
				List<HotListEntry> entryList = result.facetIndustries.entries;
				Collections.sort(entryList, new Comparator<HotListEntry>() {
					@Override
					public int compare(HotListEntry o1, HotListEntry o2) {
						return Integer.valueOf(o2.docCount).compareTo(o1.docCount);
					}
				});
				for (HotListEntry entry : entryList) {
					EntityEntry entEntry = entry.getEntity();
					String token = entEntry.getSearchToken().toUpperCase();
					Integer industryClassificationId = industryClassfMap.get(Integer.parseInt(entEntry.getId()));
					if ((industryClassificationId == null || industryClassificationId <= 1) && !token.contains("SECTOR")
							&& !token.contains("SEGMENT")) {
						if (resList.size() >= 10) {
							break;
						}
						resList.add(entEntry);
					}
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return resList;
	}

	@Override
	public DocumentSimilarityUtil getDocumentSimilarityUtil() {
		ServiceConfig serviceConfig = this.getServiceConfig();
		return serviceConfig.getDocumentSimilarityUtil();
	}

	@Override
	public void populateCompanyTopicsFromSolrDocs(SolrDocumentList docList, List<IEntityInfo> companies, List<IEntityInfo> topics,
			List<String> exchangeList) throws Exception {
		if (docList == null) {
			return;
		}
		for (SolrDocument doc : docList) {
			String attrCatId = (String) doc.getFieldValue("attrCatId");
			IEntityInfo ei = entityInfoCache.catIdToEntity(attrCatId);
			if (ei == null) {
				LOG.warn("Entity for catId " + attrCatId + " not available on index");
				continue;
			}
			if (ei.getType() == SearchTokenEntry.SEARCH_TOKEN_TAG_COMPANY) {
				if (exchangeList == null || exchangeList.contains(ei.getPrimaryExchange())) {
					companies.add(ei);
				}
			} else if (ei.getType() == SearchTokenEntry.SEARCH_TOKEN_TAG_SECTOR_TOPIC) {
				topics.add(ei);
			}
		}
	}

	@Override
	public ServiceConfig getServiceConfig() {
		return serviceConfig;
	}

	@Override
	public IEntityInfoCache getEntityInfoCache() {
		return entityInfoCache;
	}

	@Override
	public SolrSearcher getSearcher() {
		return searcher;
	}

	@Override
	public SolrServer getEntitySolrServer() {
		return entitySolrServer;
	}

	@Override
	public SolrServer getFavIconServer() {
		return favIconServer;
	}

	@Override
	public SolrServer getDocImageServer() {
		return docImageServer;
	}

	@Override
	public SolrServer getTwitterServer() {
		return twitterServer;
	}

	@Override
	public SolrServer getTwitterGroupServer() {
		return twitterGroupServer;
	}

	@Override
	public SolrServer getDocSolrServer() {
		return docSolrServer;
	}

	public void setDocSolrServer(SolrServer docSolrServer) {
		this.docSolrServer = docSolrServer;
	}

	@Override
	public SolrServer getEventServer() {
		return eventServer;
	}

	@Override
	public SolrServer getPeopleServer() {
		return peopleServer;
	}
}
