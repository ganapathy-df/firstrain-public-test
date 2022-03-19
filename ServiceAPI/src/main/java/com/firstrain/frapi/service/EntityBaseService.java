package com.firstrain.frapi.service;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.springframework.stereotype.Service;

import com.firstrain.content.similarity.DocumentSimilarityUtil;
import com.firstrain.frapi.FRService;
import com.firstrain.frapi.domain.BaseSpec;
import com.firstrain.frapi.domain.BlendDunsInput;
import com.firstrain.frapi.domain.CompanyTradingRange;
import com.firstrain.frapi.domain.CompanyVolume;
import com.firstrain.frapi.domain.EntityDetailSpec;
import com.firstrain.frapi.domain.TwitterSpec;
import com.firstrain.frapi.obj.GraphQueryCriteria;
import com.firstrain.frapi.pojo.Entity;
import com.firstrain.frapi.pojo.Event;
import com.firstrain.frapi.pojo.Graph;
import com.firstrain.frapi.pojo.Graph.GraphFor;
import com.firstrain.frapi.pojo.wrapper.DocumentSet;
import com.firstrain.frapi.pojo.wrapper.EventSet;
import com.firstrain.frapi.pojo.wrapper.GraphNodeSet;
import com.firstrain.frapi.pojo.wrapper.TweetSet;
import com.firstrain.frapi.util.ContentType;
import com.firstrain.solr.client.SearchException;
import com.firstrain.solr.client.SearchResult;

@Service
public interface EntityBaseService extends FRService {

	public DocumentSet getDocDetails(List<Long> docIds, EntityDetailSpec spec, Collection<String> toExcludeIds,
			short industryClassificationId) throws Exception;

	public TweetSet getTweetDetails(List<Long> tweetIds, EntityDetailSpec spec) throws Exception;

	public TweetSet getTweetList(String[] catIdsAll, TwitterSpec tSpec) throws Exception;

	public DocumentSet getWebResultsForSearch(String q, String fq, BaseSpec spec, BlendDunsInput bdi) throws Exception;

	public DocumentSet getWebResults(SearchResult sr, BaseSpec spec) throws Exception;

	public SearchResult getSearchResult(String[] qMulti, int[] scopeMulti, String fq, BaseSpec spec) throws Exception;

	public SearchResult getSearchResultForAnalystComments(String searchToken, BaseSpec spec) throws Exception;

	public EventSet getEventSetForMTEvents(List<Integer> companyIds, BaseSpec spec) throws Exception;

	public Graph getWebVolumeGraph(String catId, int[] companyIdsArr, int[] tCatIds, BaseSpec spec, GraphFor graphFor, int nDaysFromToday,
			int scope, int[] eventType, boolean needSignals) throws Exception;

	public List<CompanyVolume> getWebVolumeInfoFromSolr(GraphQueryCriteria criteria) throws SearchException;

	/**
	 * Generate graph Object From GraphCreteria And CompanyVolume
	 * 
	 * @param criteria
	 * @param compVolumeInfo
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public Graph generateGraphObject(GraphQueryCriteria criteria, List<CompanyVolume> compVolumeInfo, int nDaysFromToday) throws Exception;

	public List<CompanyTradingRange> getCompanyTradingInfoFromIndex(int companyId) throws SolrServerException;

	public EventSet getEventsTimeline(int[] companyIdsArr, int[] tCatIds, BaseSpec spec) throws Exception;

	public DocumentSet makeDocSetFromEntries(SearchResult sr, boolean filterSimilarEntries, int reqCountAfterFiltering,
			DocumentSimilarityUtil dsutil, ContentType contentType, boolean needImage, int start, boolean isSingleEntityWebResult,
			boolean needPhrase) throws Exception;

	/**
	 * Get Accelerometer for company, topic, monitor and industry
	 * 
	 * @param csCatIds
	 * @param singleEntity
	 * @param isIpad
	 * @return
	 */
	public GraphNodeSet getAccelerometerNode(String csCatIds, boolean singleEntity, boolean isIpad) throws Exception;

	public String trimCompanyEndingWord(String name);

	/**
	 * This api will attach related docuement with event
	 * 
	 * @param eventList * @param eventDocMap
	 * @param baseSpec
	 * @return EventSet
	 */
	public void attachRelatedDocumentDetails(List<Event> eventList, Map<Integer, SolrDocument> eventDocMap, BaseSpec spec) throws Exception;

	public List<Entity> autoSuggestForEntity(String tpEntityName, String entityType, int count, String dimensionCSV,
			Map<Integer, Integer> industryClassfMap) throws Exception;
	
	public DocumentSet gethighlightsResults(String[] qArr, int[] scopeArr, int highlightRows) throws SearchException, SolrServerException ;
}
