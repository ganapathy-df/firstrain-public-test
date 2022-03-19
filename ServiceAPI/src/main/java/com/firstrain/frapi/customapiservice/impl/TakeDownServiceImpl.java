package com.firstrain.frapi.customapiservice.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.firstrain.db.obj.APIArticleTakeDown;
import com.firstrain.db.obj.APIArticleTakeDown.Status;
import com.firstrain.frapi.FRCompletionService;
import com.firstrain.frapi.config.ServiceConfig;
import com.firstrain.frapi.config.ServiceException;
import com.firstrain.frapi.customapirepository.impl.TakeDownRepositoryImpl;
import com.firstrain.frapi.customapiservice.TakeDownService;
import com.firstrain.frapi.util.StatusCode;
import com.firstrain.obj.IEntityInfo;
import com.firstrain.obj.IEntityInfoCache;
import com.firstrain.solr.client.DistributedSolrSearcher;
import com.firstrain.solr.client.DocEntry;
import com.firstrain.solr.client.EntityInfoCacheRegistry;
import com.firstrain.solr.client.SearchTokenEntry;
import com.firstrain.solr.client.SolrSearcher;
import com.firstrain.utils.object.PerfActivityType;
import com.firstrain.utils.object.PerfMonitor;
import com.firstrain.web.pojo.CreateInputBean;

@Service
public class TakeDownServiceImpl implements TakeDownService {

	private final Logger LOG = Logger.getLogger(TakeDownServiceImpl.class);

	@Autowired
	private TakeDownRepositoryImpl takeDownRepositoryImpl;
	@Autowired
	@Qualifier("serviceConfig")
	private ServiceConfig serviceConfig;
	private SolrSearcher searcher;
	@Autowired(required = true)
	protected ThreadPoolTaskExecutor taskExecutor;
	@Value("${executor.timeout}")
	protected long executorTimeout;
	private IEntityInfoCache entityInfoCache;

	@PostConstruct
	public void init() throws ServiceException {
		if (serviceConfig == null) {
			throw new ServiceException("Can't initialize TakeDownRepositoryImpl due to null configuration.");
		}
		try {
			if (serviceConfig.getDistributedSearchConfig() != null) {
				this.searcher = new DistributedSolrSearcher(serviceConfig.getDistributedSearchConfig());
			} else {
				this.searcher = new SolrSearcher();
				this.searcher.setDocSolrServer(serviceConfig.getDocSolrServer());
			}
			this.searcher.setEntitySolrServer(serviceConfig.getEntitySolrServer());
			// to load entity info cache - used when fetch doc to check source
			this.entityInfoCache = EntityInfoCacheRegistry.getGlobalEntityInfoCache(serviceConfig.getEntitySolrServer(), true);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	private String fetchSourceIdsCSVByEnterpriseId(long enterpriseId) {
		return takeDownRepositoryImpl.fetchSourceIdsCSVByEnterpriseId(enterpriseId);
	}

	@Override
	public int takeDownContent(long enterpriseId, long docId) throws Exception {
		long startTime = PerfMonitor.currentTime();
		try {
			FRCompletionService<Object> completionService = new FRCompletionService<Object>(taskExecutor.getThreadPoolExecutor());
			Callable<Object> docFuture = getDocumentAsync(docId);
			completionService.submit(docFuture);

			Callable<Object> sourceIdsFuture = getSourceIdsAsync(enterpriseId);
			completionService.submit(sourceIdsFuture);

			List<Object> resList = new ArrayList<Object>();
			collectResults(completionService, resList);

			DocEntry doc = null;
			String allSrcIdsCSV = null;

			if (resList != null) {
				for (Object obj : resList) {
					if (obj instanceof DocEntry) {
						doc = (DocEntry) obj;
					} else if (obj instanceof String) {
						allSrcIdsCSV = obj.toString();
					}
				}
			}

			if (doc == null) {
				return StatusCode.ITEM_DOES_NOT_EXIST;
			}

			String docSource = ((DocEntry) doc).getSourceEntity().getId();

			if (allSrcIdsCSV != null && !allSrcIdsCSV.contains(docSource)) {
				LOG.debug("Doc source mismatch. Doc Source from SOLR: " + docSource);
				return StatusCode.ITEM_DOES_NOT_EXIST;
			} else {
				APIArticleTakeDown article = takeDownRepositoryImpl.getTakeDownArticle(docId);
				if (article != null) {
					// if(persistIfDoesNotExist){
					// user is trying to take down a doc which is already submitted.
					if (article.getStatus().equals(Status.NEW) || article.getStatus().equals(Status.PROCESSED)) {
						return StatusCode.ARTICLE_UNDER_PROCESSING;
					} else {
						return StatusCode.ARTICLE_TAKED_DOWN;
					}
					// }
					// else{
					// // user is requesting status of a doc which is already submitted.
					// if(article.getStatus().equals(Status.NEW) || article.getStatus().equals(Status.PROCESSED)){
					// return StatusCode.ARTICLE_UNDER_PROCESSING;
					// }
					// else{
					// return StatusCode.REQUEST_SUCCESS;
					// }
					// }
				} else {
					// if(persistIfDoesNotExist){
					takeDownRepositoryImpl.persistTakeDownArticle(docId);
					return StatusCode.REQUEST_SUCCESS;
					// }
					// else{
					// //TODO : User is checking status of doc which does not exist
					// return StatusCode.ITEM_DOES_NOT_EXIST;
					// }
				}
			}
		} finally {
			PerfMonitor.recordStats(startTime, PerfActivityType.ReqTime, "takeDownContent");
		}
	}

	@Override
	public int takeDownContentForced(long enterpriseId, long docId) throws Exception {
		long startTime = PerfMonitor.currentTime();
		try {
			APIArticleTakeDown article = takeDownRepositoryImpl.getTakeDownArticle(docId);
			if (article != null) {
				if (article.getStatus().equals(Status.NEW) || article.getStatus().equals(Status.PROCESSED)) {
					return StatusCode.ARTICLE_UNDER_PROCESSING;
				} else {
					return StatusCode.ARTICLE_TAKED_DOWN;
				}
			} else {
				takeDownRepositoryImpl.persistTakeDownArticle(docId);
				return StatusCode.REQUEST_SUCCESS;
			}
			
		} finally {
			PerfMonitor.recordStats(startTime, PerfActivityType.ReqTime, "takeDownContentForced");
		}
	}
	private Callable<Object> getDocumentAsync(final long docId) {
		return new Callable<Object>() {
			@Override
			public DocEntry call() throws Exception {
				DocEntry doc = null;
				List<DocEntry> docEntryList = searcher.fetch(docId);
				LOG.debug("No. of results for doc id " + docId + " from solr are " + (docEntryList != null ? docEntryList.size() : 0));
				if (docEntryList != null && !docEntryList.isEmpty()) {
					doc = docEntryList.get(0);
				}
				return doc;
			}
		};
	}

	private Callable<Object> getSourceIdsAsync(final long enterpriseId) {
		return new Callable<Object>() {
			@Override
			public String call() throws Exception {
				return fetchSourceIdsCSVByEnterpriseId(enterpriseId);
			}
		};
	}

	private void collectResults(FRCompletionService<Object> completionService, List<Object> resList)
			throws InterruptedException, ExecutionException {
		int submissions = completionService.getSubmissions();
		for (int i = 0; i < submissions; i++) {
			Object obj = null;
			Future<Object> f = completionService.poll(this.executorTimeout, TimeUnit.MILLISECONDS);
			if (f != null) {
				obj = f.get();
				if (obj != null) {
					resList.add(obj);
				}
			} else {
				LOG.warn("service not responded for the given timeout " + this.executorTimeout);
			}
		}
	}

	@Override
	public Boolean isValidSearchToken(String searchToken, boolean isTopicOnly, CreateInputBean inputBean) {

		if (StringUtils.isEmpty(searchToken)) {
			return Boolean.FALSE;
		}

		if (entityInfoCache == null) {
			return Boolean.FALSE;
		}

		IEntityInfo catIdToEntity = entityInfoCache.searchTokenToEntity(searchToken);

		if (catIdToEntity == null) {
			LOG.info("searchToken is not present in entityInfoCache : " + searchToken);
			return Boolean.FALSE;
		}

		int type = catIdToEntity.getType();
		if (isTopicOnly) {
			if (type != SearchTokenEntry.SEARCH_TOKEN_TAG_SECTOR_TOPIC) {
				LOG.info("searchtoken doesn't fall in attrDim 3, searchtoken : " + searchToken + ", type : " + type);
				return Boolean.FALSE;
			}
		} else {
			if (type != SearchTokenEntry.SEARCH_TOKEN_TAG_COMPANY && type != SearchTokenEntry.SEARCH_TOKEN_TAG_SECTOR_TOPIC
					&& type != SearchTokenEntry.SEARCH_TOKEN_TAG_REGION && type != SearchTokenEntry.SEARCH_TOKEN_TAG_CONTENT_TYPE
					&& type != SearchTokenEntry.SEARCH_TOKEN_TAG_FUNDAMENTAL && type != SearchTokenEntry.SEARCH_TOKEN_TAG_REGIONAL_ENTITY) {
				LOG.info("searchtoken doesn't fall in attrDim 1, 2, 3, 10, 13 or 14, searchtoken : " + searchToken + ", type : " + type);
				return Boolean.FALSE;
			}
		}
		if (inputBean != null) {
			inputBean.setName(catIdToEntity.getName());
		}

		return Boolean.TRUE;
	}
}
