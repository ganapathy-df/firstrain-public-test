package com.firstrain.frapi.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.firstrain.db.obj.Accelerometer;
import com.firstrain.db.obj.Items;
import com.firstrain.db.obj.Tags;
import com.firstrain.frapi.FRCompletionService;
import com.firstrain.frapi.config.ServiceException;
import com.firstrain.frapi.domain.ChartCountSummary;
import com.firstrain.frapi.domain.ChartDetails;
import com.firstrain.frapi.domain.ChartSpec;
import com.firstrain.frapi.domain.Entity;
import com.firstrain.frapi.domain.TrendingEntity;
import com.firstrain.frapi.domain.VisualizationData;
import com.firstrain.frapi.domain.VisualizationData.ChartType;
import com.firstrain.frapi.domain.VisualizationData.Graph;
import com.firstrain.frapi.domain.VisualizationData.Node;
import com.firstrain.frapi.domain.VisualizationData.NodeBucket;
import com.firstrain.frapi.pojo.wrapper.BaseSet;
import com.firstrain.frapi.pojo.wrapper.GraphSet;
import com.firstrain.frapi.repository.AccelerometerServiceRepository;
import com.firstrain.frapi.repository.EntityBaseServiceRepository;
import com.firstrain.frapi.repository.MonitorServiceRepository;
import com.firstrain.frapi.service.VisualizationService;
import com.firstrain.frapi.util.QueryParseUtil;
import com.firstrain.obj.IEntityInfo;
import com.firstrain.obj.IEntityInfoCache;
import com.firstrain.solr.client.DocListBucket;
import com.firstrain.solr.client.EntityEntry;
import com.firstrain.solr.client.HotListBucket;
import com.firstrain.solr.client.HotListEntry;
import com.firstrain.solr.client.QueryEntry;
import com.firstrain.solr.client.SearchException;
import com.firstrain.solr.client.SearchResult;
import com.firstrain.solr.client.SearchSpec;
import com.firstrain.solr.client.SearchTokenEntry;
import com.firstrain.solr.client.SearchTokenEntry.Relation;
import com.firstrain.solr.client.SolrSearcher;
import com.firstrain.solr.client.SuggestedSearchGroup;
import com.firstrain.utils.FR_ArrayUtils;
import com.firstrain.utils.TimeUtils;
import com.firstrain.utils.TitleUtils;
import com.firstrain.utils.object.PerfActivityType;
import com.firstrain.utils.object.PerfMonitor;
import com.firstrain.utils.object.PerfRequestEntry;

public class VisualizationServiceImpl implements VisualizationService {

	private final Logger LOG = Logger.getLogger(VisualizationServiceImpl.class);
	private final int SEARCHTOKEN_COUNT = 450;
	private final int TRENDING_COUNT = 20;
	private final int MAX_RELATED_ENTITY = 15;
	private int treeMapMinNode = 4;
	private final String FACET_SORT_COUNT = "count";
	private Map<String, ChartCountSummary> docSummaryMap = new ConcurrentHashMap<String, ChartCountSummary>();

	@Autowired(required = true)
	protected ThreadPoolTaskExecutor taskExecutor;
	@Autowired
	private MonitorServiceRepository monitorServiceRepository;
	@Autowired
	@Qualifier("entityBaseServiceRepositoryImpl")
	private EntityBaseServiceRepository entityBaseServiceRepository;
	@Autowired
	private AccelerometerServiceRepository accelerometerServiceRepository;
	@Autowired
	private RegionExcelUtilImpl regionExcelUtilImpl;
	@Autowired
	private CompanyEndingWords companyEndingWords;

	@Value("${executor.timeout}")
	protected long executorTimeout;

	@PostConstruct
	public void init() throws ServiceException {
		try {
			loadEntityDocData();
		} catch (Exception e) {
			LOG.error("Error = " + e.getMessage(), e);
		}
	}

	public void loadEntityDocData() throws Exception {
		Map<String, Long> dailyEntityIdVsCount = fetchEntityDocCounts(-1);
		Map<String, Long> monthlyEntityIdVsCount = fetchEntityDocCounts(31);

		Map<String, ChartCountSummary> docSummaryMapUpdate = new HashMap<String, ChartCountSummary>();
		for (Map.Entry<String, Long> entry : monthlyEntityIdVsCount.entrySet()) {
			ChartCountSummary dse = new ChartCountSummary();
			String key = entry.getKey();
			dse.setEntityId(key);
			Long count = dailyEntityIdVsCount.get(key);
			if (count != null) {
				dse.setOneDayDocCount(count.intValue());
			}
			count = entry.getValue();
			if (count != null) {
				dse.setThirtyOneDaysDocCount(count.intValue());
			}
			docSummaryMapUpdate.put(key, dse);
		}
		docSummaryMap.clear();
		docSummaryMap.putAll(docSummaryMapUpdate);
	}

	private Map<String, Long> fetchEntityDocCounts(int days) throws Exception {
		Map<String, Long> entityIdVsCount = new HashMap<String, Long>();
		Calendar cal = Calendar.getInstance();

		if (days == -1) {
			cal.add(Calendar.HOUR_OF_DAY, -24);
		} else {
			cal.add(Calendar.DATE, -days);
		}

		int startTime = TimeUtils.getMinuteNumber(cal.getTime());

		SolrQuery q = new SolrQuery();
		q.setQuery("minuteId:[" + startTime + " TO *]");
		q.setQueryType("standard");
		q.setRows(0);
		q.setFacet(Boolean.TRUE);
		q.setFacetMinCount(1);
		q.setFacetSort(FACET_SORT_COUNT);
		q.setFacetLimit(1000000);
		q.addFacetField("catIdSearchCompanyPassing", "catIdSearchTopicPassing");

		LOG.debug("query = " + q.toString());
		QueryRequest qRequest = new QueryRequest(q, SolrRequest.METHOD.GET);
		QueryResponse qResponse = qRequest.process(entityBaseServiceRepository.getDocSolrServer());

		List<FacetField> facetFieldList = qResponse.getFacetFields();
		for (FacetField facetField : facetFieldList) {
			List<Count> counts = facetField.getValues();
			if (counts != null) {
				for (Count count : counts) {
					entityIdVsCount.put(count.getName(), count.getCount());
				}
			}
		}
		return entityIdVsCount;
	}

	@Override
	public VisualizationData getVisualizationByMonitorId(long tagId, int nodeCount, List<ChartType> chartTypes, String filters,
			boolean isHtmlSmartText, boolean isApplyMinNodeCheck) throws Exception {
		if (isApplyMinNodeCheck) {
			treeMapMinNode = 4;
		} else {
			treeMapMinNode = 1;
		}
		ChartSpec spec = new ChartSpec();
		spec.setTreeNodeCount(nodeCount);
		spec.setFilterQuery(filters);
		spec.setMonitorId(tagId);
		spec.setChartTypes(chartTypes);
		spec.setPartitionDay1(7);
		spec.setPartitionDay2(1);

		VisualizationData graphData = null;
		boolean isSingleEntity = false;

		List<Items> itemList = monitorServiceRepository.getItemsByTagId(tagId);
		spec.setItemList(itemList);

		Set<String> catIds = new HashSet<String>();


		List<String> qList = new ArrayList<String>();
		List<Integer> scopeList = new ArrayList<Integer>();
		IEntityInfo ei = parsedSearchCriteria(qList, scopeList, catIds, filters, spec);

		String chartId = "chart_M_" + tagId;

		if (ei != null) {
			isSingleEntity = true;
		}

		FRCompletionService<BaseSet> completionService = new FRCompletionService<BaseSet>(taskExecutor.getThreadPoolExecutor());

		if (isSingleEntity && ei != null && ei.getType() == SearchTokenEntry.SEARCH_TOKEN_TAG_INDUSTRY) {
			/*
			 * 1. Fetch Search Result of Monitor 2. Get 150 co-mention Company and Topic 3. Prepare bucket of Similar Entity, Other Company,
			 * Other Topic. 4. From Bucket now run the Algo of generating the top 20 entity of each tree-map. 5. first Bucket of Similar
			 * Entity will be considered for Accelerometer
			 */
			ChartDetails cd = getIndusrtyChartDetails(ei, qList, scopeList);

			if (chartTypes.indexOf(ChartType.TREE_MONITOR_SEARCH) >= 0 || chartTypes.indexOf(ChartType.TREE_COMPANY) >= 0
					|| chartTypes.indexOf(ChartType.TREE_TOPICS) >= 0) {
				// Async Thread for BI and MD and Monitor Activity
				Callable<BaseSet> treegraphFuture = getTreeChartDataAsync(cd, spec.getTreeNodeCount(), chartId);
				completionService.submit(treegraphFuture);
			}

			if (chartTypes.indexOf(ChartType.GEO_US) >= 0 || chartTypes.indexOf(ChartType.GEO_WORLD) >= 0) {
				// Async Thread for Region Maps
				Callable<BaseSet> graphGEOFuture = getChartDataforGEOAsync(qList, scopeList, chartId);
				completionService.submit(graphGEOFuture);
			}

			getAccelerometerGraphIndustryAsyncAndSubmit(chartTypes, cd, chartId, isHtmlSmartText, completionService);

		} else {
			getChartDataforBIandMDAsyncAndSubmit(chartTypes, completionService, spec, qList, scopeList, catIds, chartId);

			if (chartTypes.indexOf(ChartType.TREE_MONITOR_SEARCH) >= 0 || chartTypes.indexOf(ChartType.GEO_US) >= 0
					|| chartTypes.indexOf(ChartType.GEO_WORLD) >= 0) {
				// Async Thread for Monitor Activity and Region Maps
				Callable<BaseSet> graphTTandGEOFuture =
						getChartDataforMonitorGEOandTTAsync(itemList, qList, scopeList, spec.getTreeNodeCount(), chartId);
				completionService.submit(graphTTandGEOFuture);
			}

			getAndSubmitData(chartTypes, catIds, chartId, isHtmlSmartText, completionService);
		}

		// collect Graph
		graphData = collectResults(completionService, chartTypes);

		Tags tag = monitorServiceRepository.getTagById(tagId);

		Entity entity = new Entity();
		entity.setId("M:" + tagId);
		entity.setName(tag.getTagName());

		graphData.setEntity(entity);

		return graphData;
	}

	private ChartDetails getIndusrtyChartDetails(IEntityInfo industryEntityInfo, List<String> qList, List<Integer> scopeList) {

		ChartDetails cd = new ChartDetails();
		try {
			if (qList == null || qList.isEmpty()) {
				return cd;
			}

			// only 7 days search
			SearchResult searchResult = getSearchResult(qList, scopeList, 7, false);

			Map<String, TrendingEntity> virtualMonitor = new LinkedHashMap<String, TrendingEntity>();
			Map<String, TrendingEntity> relCompMap = new LinkedHashMap<String, TrendingEntity>();
			Map<String, TrendingEntity> relTopicMap = new LinkedHashMap<String, TrendingEntity>();

			HotListBucket coMenCompany = searchResult.facetCompanies;
			HotListBucket coMenTopic = searchResult.facetTopics;

			if (coMenCompany != null) {
				populateComentionEntitiy(relCompMap, virtualMonitor, industryEntityInfo, coMenCompany);
			}

			if (coMenTopic != null) {
				populateComentionEntitiy(relTopicMap, virtualMonitor, industryEntityInfo, coMenTopic);
			}

			removeSubsidariesforIndustryVisualization(relCompMap);
			removeSubsidariesforIndustryVisualization(virtualMonitor);

			cd.setTrendingCompanyList(getTrendingList(relCompMap, TRENDING_COUNT));
			cd.setTrendingTopicList(getTrendingList(relTopicMap, TRENDING_COUNT));
			cd.setMonitorTrendingEntityList(getTrendingList(virtualMonitor, TRENDING_COUNT));
			cd.setAccmeterList(getTrendingList(virtualMonitor, -1));

		} catch (SearchException e) {
			LOG.error("Error While Fetching the Industry Data" + e.getMessage());
		}
		return cd;
	}

	public void removeSubsidariesforIndustryVisualization(Map<String, TrendingEntity> relEntityMap) {

		Set<Integer> parentList = new HashSet<Integer>();
		Set<String> removeList = new HashSet<String>();
		for (Entry<String, TrendingEntity> relComp : relEntityMap.entrySet()) {
			String catId = relComp.getKey();
			IEntityInfo entity = entityBaseServiceRepository.getEntityInfoCache().catIdToEntity(catId);

			if (entity == null || (entity.getType() != SearchTokenEntry.SEARCH_TOKEN_TAG_COMPANY
					&& entity.getType() != SearchTokenEntry.SEARCH_TOKEN_TAG_SUBSIDIARY)) {
				continue;
			}

			int parent = entity.getSubsidiaryOf();
			if (parent > 0) {
				if (parentList.contains(parent)) {
					removeList.add(catId);
				} else {
					parentList.add(Integer.parseInt(catId));
				}
			} else {
				parentList.add(Integer.parseInt(catId));
			}
		}
		removeItemFromEntityMap(removeList, relEntityMap);
	}

	private void populateComentionEntitiy(Map<String, TrendingEntity> relEntityMap, Map<String, TrendingEntity> virtualMonitor,
			IEntityInfo industryEntityInfo, HotListBucket coMenEntity) {

		boolean isIndustry = false, isSegment = false, isSector = false;
		int catIdtoVerify = -1;

		if (industryEntityInfo.getIndustryCatId() > 0) {
			catIdtoVerify = industryEntityInfo.getIndustryCatId();
			isIndustry = true;
		} else if (industryEntityInfo.getSegmentCatId() > 0) {
			catIdtoVerify = industryEntityInfo.getSegmentCatId();
			isSegment = true;
		} else if (industryEntityInfo.getSectorCatId() > 0) {
			catIdtoVerify = industryEntityInfo.getSectorCatId();
			isSector = true;
		}

		for (HotListEntry entry : coMenEntity.getEntries()) {

			EntityEntry entity = entry.getEntity();
			if (!entity.isValid() || entity.inPositiveQ) {
				continue;
			}

			if (entry.getDocCount() <= 2) {
				continue;
			}

			IEntityInfo ei = entityBaseServiceRepository.getEntityInfoCache().catIdToEntity(entity.getId());
			if (ei == null) {
				continue;
			}
			if (ei.isExceptional() || ei.getSectorCatId() == 695885) {
				continue;
			}

			List<Integer> industryCatIds = new ArrayList<Integer>();
			if (isIndustry) {
				industryCatIds.add(ei.getIndustryCatId());
				if (ei.getSecondaryIndustry() != null) {
					industryCatIds.addAll(ei.getSecondaryIndustry());
				}
			} else if (isSegment) {
				industryCatIds.add(ei.getSegmentCatId());
				if (ei.getSecondarySegment() != null) {
					industryCatIds.addAll(ei.getSecondarySegment());
				}
			} else if (isSector) {
				industryCatIds.add(ei.getSectorCatId());
				if (ei.getSecondarySector() != null) {
					industryCatIds.addAll(ei.getSecondarySector());
				}
			}

			if (industryCatIds.contains(catIdtoVerify)) {

				createAndPopulateTrendingEntity(virtualMonitor, entity, entry);

			} else {
				createAndPopulateTrendingEntity(relEntityMap, entity, entry);
			}
		}
	}

	private void createAndPopulateTrendingEntity(final Map<String, TrendingEntity> relEntityMap, final EntityEntry entity, final HotListEntry entry) {
		TrendingEntity obj = relEntityMap.get(entity.getId());
		if (obj == null) {
			obj = createTrendingEntity(entity);
			obj.setDocCount(entry.getDocCount());
		
			relEntityMap.put(entity.getId(), obj);
		}
	}
 
	private Graph getAccelerometerGraph(ChartType chartType, String chartId, Set<String> catIds, boolean singleEntity, boolean ipad,
			boolean isHtmlSmartText) {

		Graph graph = new Graph(chartId, chartType);
		List<Node> nodes =
				getAccelerometerNode(FR_ArrayUtils.getCSVFromCollection(catIds), singleEntity, ChartType.ACC_METER, ipad, isHtmlSmartText);

		if (nodes != null && !nodes.isEmpty()) {
			if (!singleEntity) { // multi entity
				Collections.sort(nodes, new Comparator<Node>() {
					@Override
					public int compare(Node o1, Node o2) {
						return -Float.valueOf(o1.value).compareTo(o2.value);
					}
				});
				if (nodes.size() > 6) {
					for (int i = nodes.size() - 1; i >= 6; i--) {
						nodes.remove(i);
					}
				}
				if (nodes.size() % 2 != 0) { // remove odd one, we need count as 2,4,6
					nodes.remove(nodes.size() - 1);
				}
			}
			if (!nodes.isEmpty()) {
				graph.nodes = nodes;
			}
		}
		return graph;
	}

	private List<Node> getAccelerometerNode(String catIds, boolean singleEntity, ChartType chartType, boolean ipad,
			boolean isHtmlSmartText) {
		List<Node> nodeList = new ArrayList<Node>();
		try {
			List<Accelerometer> accObjList = accelerometerServiceRepository.getAccelerometer(catIds);

			for (Accelerometer accObj : accObjList) {
				IEntityInfo entity = entityBaseServiceRepository.getEntityInfoCache().catIdToEntity(accObj.getEntityId());
				if (entity == null) {
					LOG.debug(chartType + " ignore no Entity for entity: " + accObj.getEntityId());
					continue;
				}

				int scoreTh = 0;
				if (singleEntity) {
					// Score greater than 0
					if (accObj.getScore() <= 0) {
						LOG.debug(chartType + " ignore content scores (" + accObj.getScore() + ") must be > " + scoreTh + ", for entity: "
								+ accObj.getEntityId());
						continue;
					}
				} else {
					// three day count < 4
					// Score <= 75
					if (accObj.getThreeDayCount() < 4) {
						LOG.debug(chartType + " ignore RecentWindow grouped tweet count (" + accObj.getThreeDayCount() + ")< 4 for entity: "
								+ accObj.getEntityId());
						continue;
					}

					scoreTh = 75;
					if (accObj.getScore() <= scoreTh) {
						LOG.debug(chartType + " ignore content scores (" + accObj.getScore() + ") must be > " + scoreTh + ", for entity: "
								+ accObj.getEntityId());
						continue;
					}
				}

				String label = entity.getName();
				if (entity.getType() == SearchTokenEntry.SEARCH_TOKEN_TAG_COMPANY) {
					label = companyEndingWords.trimCompanyEndingWord(label);
				}
				Node node = new Node(entity.getId(), label, accObj.getScore(), accObj.getArrow());
				node.imageName = accObj.getImageName();
				if (ipad) {
					String[] text = TitleUtils.getSmartTextAccmeteriPad(accObj.getArrow(), accObj.getScore());
					node.smartText0 = text[0];
					node.smartText = text[1];
				} else if (isHtmlSmartText) {
					node.smartText = TitleUtils.getSmartTextAccmeter(accObj.getArrow(), accObj.getScore());
				} else {
					node.smartText = TitleUtils.getSmartPlainTextAccmeter(accObj.getArrow(), accObj.getScore());
				}
				node.searchToken = entity.getSearchToken();
				node.name = entity.getName();

				nodeList.add(node);
			}
		} catch (Exception e) {
			LOG.error("Exception " + e.getMessage(), e);
		}
		return nodeList;
	}

	private List<Graph> getGeoMapGraphs(List<ChartCountSummary> regionList, List<ChartType> chartTypes, String chartId) {
		int indexWd = chartTypes.indexOf(ChartType.GEO_WORLD);
		int indexUS = chartTypes.indexOf(ChartType.GEO_US);
		boolean wd = indexWd >= 0;
		boolean us = indexUS >= 0;

		Graph graphWd = null;
		Graph graphUS = null;
		List<Graph> graphList = new ArrayList<Graph>();

		if (wd) {
			graphWd = new Graph(chartId, ChartType.GEO_WORLD);
			graphList.add(graphWd);
		}
		if (us) {
			graphUS = new Graph(chartId, ChartType.GEO_US);
			graphList.add(graphUS);
		}

		if (regionList == null || regionList.isEmpty()) {
			return graphList;
		}

		List<Node> nodesWd = new ArrayList<Node>();
		List<Node> nodesUS = new ArrayList<Node>();
		Map<String, String> regionVsCountryCode = regionExcelUtilImpl.getRegionVsCountryCode();
		Map<String, String> regionVsParentRegion = regionExcelUtilImpl.getRegionVsParentRegion();
		Map<String, ChartCountSummary> entityIdVsSummary = new HashMap<String, ChartCountSummary>();

		for (ChartCountSummary ccsObj : regionList) {
			entityIdVsSummary.put(ccsObj.getEntityId(), ccsObj);
		}
		int wdCnt = 0, usCnt = 0;
		for (Map.Entry<String, String> entry : regionVsCountryCode.entrySet()) {
			String searchToken = entry.getKey();
			IEntityInfo e = entityBaseServiceRepository.getEntityInfoCache().searchTokenToEntity(searchToken);
			if (e == null) {
				continue;
			}
			ChartCountSummary ccsObj = entityIdVsSummary.get(e.getId());
			Node n;
			if (ccsObj != null) {
				int day1Count = ccsObj.getOneDayDocCount();
				float day30Avg = getDay30Avg(ccsObj, day1Count);
				float intensity = 0;
				if (ccsObj.getThirtyOneDaysDocCount() >= 4 || day30Avg > 1) {
					intensity = (day1Count / day30Avg) * 100;
				}
				n = new Node(e.getId(), e.getName(), ccsObj.getThirtyOneDaysDocCount(), Math.round(intensity));
				n.smartText = TitleUtils.getSmartPlainTextGeomap(n.intensity);
			} else {
				n = new Node(e.getId(), e.getName(), 0, 0);
			}
			n.cc = entry.getValue();
			n.searchToken = e.getSearchToken();
			String parentToken = regionVsParentRegion.get(e.getSearchToken());

			if (parentToken != null) {
				n.parentToken = parentToken;
				IEntityInfo pe = entityBaseServiceRepository.getEntityInfoCache().searchTokenToEntity(parentToken);
				if (pe != null) {
					n.parent = pe.getName();
				}
			}

			if ("RF:UnitedStatesofAmerica".equals(parentToken)) {
				usCnt = incrementAndAdd(us, n, usCnt, nodesUS);
			} else {
				wdCnt = incrementAndAdd(wd, n, wdCnt, nodesWd);
			}
		}
		if (!nodesWd.isEmpty() && wdCnt >= 2 && graphWd != null) {
			graphWd.nodes = nodesWd;
		}
		if (!nodesUS.isEmpty() && usCnt >= 2 && graphUS != null) {
			graphUS.nodes = nodesUS;
		}

		return graphList;
	}

	private int incrementAndAdd(final boolean wd, final Node n, int wdCntParam, final List<Node> nodesWd) {
		int wdCnt = wdCntParam;
		if (wd) {
			if (n.value > 0) {
				wdCnt++;
			}
			nodesWd.add(n);
		}
		return wdCnt;
	}
 
	private Graph getTreemapGraphForIndustryMonitor(List<ChartCountSummary> entityList, int nodeCount, ChartType chartType,
			String chartId) {

		Graph graph = new Graph(chartId, chartType);
		if (entityList == null || entityList.isEmpty() || entityList.size() < treeMapMinNode) {
			LOG.debug(chartType + " ignore entity count < " + treeMapMinNode + " for monitor : " + chartId);
			return graph;
		}
		return getTreemapGraph(entityList, null, nodeCount, chartType, chartId, graph);
	}

	private Graph getTreemapGraphForMonitor(List<ChartCountSummary> entityList, List<Items> itemList, int nodeCount, ChartType chartType,
			String chartId) {

		Graph graph = new Graph(chartId, chartType);
		if (entityList == null || entityList.isEmpty() || entityList.size() < treeMapMinNode) {
			LOG.debug(chartType + " ignore entity count < " + treeMapMinNode + " for monitor : " + chartId);
			return graph;
		}
		Map<Long, Items> itemMap = new HashMap<Long, Items>();
		for (Items item : itemList) {
			itemMap.put(item.getId(), item);
		}
		return getTreemapGraph(entityList, itemMap, nodeCount, chartType, chartId, graph);
	}
	
	private Items getItemForTreemMapGraph(Map<Long, Items> itemMap, ChartCountSummary ccsObj) {
		Items items = null;
		if (itemMap == null) {
			IEntityInfo ei = entityBaseServiceRepository.getEntityInfoCache().catIdToEntity(ccsObj.getEntityId());
			if (ei != null) {
				items = new Items();
				items.setName(ei.getName());
				items.setId(-1);
				items.setData("q=" + ei.getSearchToken() + "&scope=" + ei.getScope());
			}
		} else {
			items = itemMap.get(ccsObj.getSearchId());
		}
		return items;
	}
	
	private void sortNodeList(List<Node> nodeList) {
		Collections.sort(nodeList, new Comparator<Node>() {
			@Override
			public int compare(Node o1, Node o2) {
				if (o1.intensity == o2.intensity) {
					return -Float.valueOf(o1.value).compareTo(o2.value);
				}
				return -Integer.valueOf(o1.intensity).compareTo(o2.intensity);
			}
		});
	}
	
	private Node getNodeForTreemapGraph(ChartCountSummary ccsObj, Items item, ChartType chartType) {
		final int day1Count = ccsObj.getOneDayDocCount();
		float day30Avg = getDay30Avg(ccsObj, day1Count);
		if (day1Count < 4 && day30Avg == 1) {
			LOG.debug(chartType + " skip 1-day count < 4 for entity : " + ccsObj.getSearchId());
			return null;
		}
		float intensity = (day1Count / day30Avg) * 100;
		float value = scaleData(ccsObj.getOneDayDocCount());
		String itemName = item.getName();
		long itemId = item.getId();
		String label = companyEndingWords.trimCompanyEndingWord(itemName);
		Node node = new Node(Long.toString(itemId), label, value, Math.round(intensity));
		node.smartText = TitleUtils.getSmartPlainTextTreemap(node.intensity);
		node.query = item.getData();
		node.itemId = itemId;
		node.name = itemName;
		return node;
	}
	
	private Graph getTreemapGraph(List<ChartCountSummary> entityList, Map<Long, Items> itemMap, int nodeCount, 
			ChartType chartType, String chartId, Graph graph) {
		List<Node> nodeList = new ArrayList<Node>();
		for (ChartCountSummary ccsObj : entityList) {
			Items item = getItemForTreemMapGraph(itemMap, ccsObj);
			if (item == null) {
				continue;
			}
			Node node = getNodeForTreemapGraph(ccsObj, item, chartType);
			if (node == null) {
				continue;
			}
			nodeList.add(node);
		}
		if (nodeList.size() < treeMapMinNode) {
			LOG.debug(chartType + " ignore entity count < " + treeMapMinNode + " after filter for monitor : " + chartId);
			return graph;
		}
		sortNodeList(nodeList);
		if (nodeList.size() > nodeCount) {
			for (int i = nodeList.size() - 1; i >= nodeCount; i--) {
				nodeList.remove(i);
			}
		}
		graph.nodes = nodeList;
		return graph;
	}

	private float scaleData(float dataParam) {
		float data = dataParam;
		data = (float) Math.log(data);
		if (data < 0.5) {
			data = 0.5F;
		}
		return Math.round(data * 100) / 100F; // round it to two decimal places
	}

	private Graph getTreemapGraph(List<ChartCountSummary> entityList, int nodeCount, ChartType chartType, String chartId) {
		Graph graph = new Graph(chartId, chartType);
		if (entityList == null || entityList.isEmpty() || entityList.size() < treeMapMinNode) {
			LOG.debug("Treemap entity count < " + treeMapMinNode);
			return graph;
		}
		List<Node> nodeList = new ArrayList<Node>();
		for (ChartCountSummary ccsObj : entityList) {

			IEntityInfo e = entityBaseServiceRepository.getEntityInfoCache().catIdToEntity(ccsObj.getEntityId());
			if (e == null) {
				LOG.debug(chartType + " no entity for id : " + ccsObj.getEntityId());
				continue;
			}
			int day1Count = ccsObj.getOneDayDocCount();
			float day30Avg = getDay30Avg(ccsObj, day1Count);
			if (day1Count < 4 && day30Avg == 1) {
				LOG.debug(chartType + " skip 1-day count < 4 for entity : " + e.getId());
				continue;
			}
			float intensity = (day1Count / day30Avg) * 100;
			float value = scaleData(ccsObj.getOneDayDocCount());

			Node node = new Node(e.getId(), e.getName(), value, Math.round(intensity));
			node.searchToken = e.getSearchToken();
			node.name = e.getName();
			node.parents = new int[3];
			// verify if its a valid parent, if not just put parent as -1 (Others)
			IEntityInfo pe = entityBaseServiceRepository.getEntityInfoCache().catIdToEntity(Integer.toString(e.getSectorCatId()));
			node.parents[0] = (pe != null ? e.getSectorCatId() : -1);
			pe = entityBaseServiceRepository.getEntityInfoCache().catIdToEntity(Integer.toString(e.getSegmentCatId()));
			node.parents[1] = (pe != null ? e.getSegmentCatId() : -1);
			pe = entityBaseServiceRepository.getEntityInfoCache().catIdToEntity(Integer.toString(e.getIndustryCatId()));
			node.parents[2] = (pe != null ? e.getIndustryCatId() : -1);

			nodeList.add(node);
		}
		if (nodeList.size() < treeMapMinNode) {
			LOG.debug(chartType + " ignored after filter entity count < " + treeMapMinNode);
			return graph;
		}
		Collections.sort(nodeList, new Comparator<Node>() {
			@Override
			public int compare(Node o1, Node o2) {
				if (o1.intensity == o2.intensity) {
					return -Float.valueOf(o1.value).compareTo(o2.value);
				}
				return -Integer.valueOf(o1.intensity).compareTo(o2.intensity);
			}
		});
		if (nodeList.size() > nodeCount) {
			for (int i = nodeList.size() - 1; i >= nodeCount; i--) {
				nodeList.remove(i);
			}
		}
		List<NodeBucket> buckets = getBucketedList(nodeList);
		List<Node> nodes = new ArrayList<Node>();
		for (int i = 0; i < buckets.size(); i++) {
			NodeBucket nb = buckets.get(i);
			Node parentNode = null;
			if (i > 5) {
				parentNode = nodes.get(nodes.size() - 1);
			} else {
				String id = Integer.toString(nb.parentId);
				if (nb.parentId == -1) {
					parentNode = new Node(id, "Others", 0, 0);
				} else {
					IEntityInfo pe = entityBaseServiceRepository.getEntityInfoCache().catIdToEntity(id);
					parentNode = new Node(id, pe.getName(), 0, 0);
				}
				nodes.add(parentNode);
			}
			for (Node node : nb.nodes) {
				parentNode.value += node.value;
				node.smartText = TitleUtils.getSmartPlainTextTreemap(node.intensity);
				if (chartType == ChartType.TREE_COMPANY) {
					node.label = companyEndingWords.trimCompanyEndingWord(node.label);
				}
			}
			if (i > 5) {
				parentNode.subtree.addAll(nb.nodes);
			} else {
				parentNode.subtree = nb.nodes;
			}
		}
		graph.nodes = nodes;
		return graph;
	}

	private float getDay30Avg(final ChartCountSummary ccsObj, final int day1Count) {
		float day30Avg = (ccsObj.getThirtyOneDaysDocCount() - day1Count) / 30F;
		if (day30Avg < 1) {
			day30Avg = 1;
		}
		return day30Avg;
	}
 
	private List<NodeBucket> getBucketedList(List<Node> nodes) {
		List<NodeBucket> sectorB = new ArrayList<NodeBucket>();
		List<NodeBucket> segmentB = new ArrayList<NodeBucket>();
		List<NodeBucket> industryB = new ArrayList<NodeBucket>();
		for (Node n : nodes) {
			int[] ps = n.parents;
			addBucket(sectorB, n, ps[0]);
			addBucket(segmentB, n, ps[1]);
			addBucket(industryB, n, ps[2]);
		}
		int sec = Math.abs(6 - sectorB.size());
		int seg = Math.abs(6 - segmentB.size());
		int ind = Math.abs(6 - industryB.size());
		List<NodeBucket> buckets = null;
		if (sec < seg && sec < ind) {
			buckets = sectorB;
		} else if (seg < sec && seg < ind) {
			buckets = segmentB;
		} else {
			buckets = industryB;
		}
		Collections.sort(buckets, new Comparator<NodeBucket>() {
			@Override
			public int compare(NodeBucket o1, NodeBucket o2) {
				return -Integer.valueOf(o1.count).compareTo(o2.count);
			}
		});
		return buckets;
	}

	private void addBucket(List<NodeBucket> list, Node n, int parentId) {
		NodeBucket nb = null;
		for (int i = 0; i < list.size(); i++) {
			NodeBucket nb1 = list.get(i);
			if (nb1.parentId == parentId) {
				nb = nb1;
				break;
			}
		}
		if (nb == null) {
			nb = new NodeBucket(parentId);
			list.add(nb);
		}
		nb.count++;
		nb.nodes.add(n);
	}

	private List<ChartCountSummary> getTrendingRegion(SearchResult searchResult) {
		HotListBucket hotListBucket = searchResult.facetRegions;

		if (hotListBucket == null || hotListBucket.size() == 0) {
			return null;
		}
		List<ChartCountSummary> regionList = new ArrayList<ChartCountSummary>();
		for (HotListEntry entry : hotListBucket.getEntries()) {
			EntityEntry entity = entry.getEntity();
			ChartCountSummary ccs = new ChartCountSummary();
			ccs.setEntityId(entity.getId());
			int[] counts = entry.getPartitionCounts();
			// 31,31,1
			if (counts != null && counts.length == 3) {
				ccs.setOneDayDocCount(counts[2]);
				ccs.setThirtyOneDaysDocCount(counts[1] + counts[2]);
			}
			regionList.add(ccs);
		}
		return regionList;
	}

	private List<ChartCountSummary> getTrendingList(Map<String, TrendingEntity> entityMap, int count) {

		List<TrendingEntity> trendingEntity = new ArrayList<TrendingEntity>(entityMap.values());
		TrendingEntityComparator(trendingEntity);

		List<ChartCountSummary> trendingList = new ArrayList<ChartCountSummary>();

		for (TrendingEntity entity : trendingEntity) {
			IEntityInfo ei = entityBaseServiceRepository.getEntityInfoCache().catIdToEntity(entity.getEntityId());
			if (ei != null) {
				ChartCountSummary ccs = docSummaryMap.get(ei.getId());
				if (ccs != null) {
					trendingList.add(ccs);
				}
			}
			if (count != -1 && trendingList.size() == count) {
				break;
			}
		}
		return trendingList;
	}

	public static void TrendingEntityComparator(List<TrendingEntity> trendingEntity) {

		Collections.sort(trendingEntity, new Comparator<TrendingEntity>() {

			@Override
			public int compare(TrendingEntity o1, TrendingEntity o2) {
				/**
				 * c+, dc+ -> dc c-, dc+ -> dc c+ dc0 -> c ---- c- dc0
				 */
				if ((o1.getEntityCount() > 0 && o1.getDocCount() > 0) && (o2.getEntityCount() > 0 && o2.getDocCount() > 0)) {
					if (o1.getDocCount() != o2.getDocCount()) {
						return -Integer.valueOf(o1.getDocCount()).compareTo(o2.getDocCount());
					}
					if (o1.getEntityCount() != o2.getEntityCount()) {
						return -Integer.valueOf(o1.getEntityCount()).compareTo(o2.getEntityCount());
					}
					// return o1.name.compareToIgnoreCase(o2.name); un-comment if order by name needed in default case
				}
				if (o1.getEntityCount() > 0 && o1.getDocCount() > 0) {
					return -1;
				}
				if (o2.getEntityCount() > 0 && o2.getDocCount() > 0) {
					return 1;
				}
				if (o1.getDocCount() > 0 && o2.getDocCount() > 0) {
					if (o1.getDocCount() != o2.getDocCount()) {
						return -Integer.valueOf(o1.getDocCount()).compareTo(o2.getDocCount());
					}
					// return o1.name.compareToIgnoreCase(o2.name);
				}
				if (o1.getDocCount() > 0) {
					return -1;
				}
				if (o2.getDocCount() > 0) {
					return 1;
				}
				if (o1.getEntityCount() > 0 && o2.getEntityCount() > 0) {
					if (o1.getEntityCount() != o2.getEntityCount()) {
						return -Integer.valueOf(o1.getEntityCount()).compareTo(o2.getEntityCount());
					}
					// return o1.name.compareToIgnoreCase(o2.name);
				}
				if (o1.getEntityCount() > 0) {
					return -1;
				}
				if (o2.getEntityCount() > 0) {
					return 1;
				}
				// return o1.name.compareToIgnoreCase(o2.name);
				return 0;
			}
		});
	}

	public void removeSubsidaries(Map<String, TrendingEntity> relEntityMap, Set<String> entityCatIds) {

		IEntityInfoCache entityInfoCache = entityBaseServiceRepository.getEntityInfoCache();

		Set<String> removeList = new HashSet<String>();
		for (Entry<String, TrendingEntity> relComp : relEntityMap.entrySet()) {
			String catId = relComp.getKey();
			IEntityInfo entity = entityInfoCache.catIdToEntity(catId);
			// recursively check for this entity and its parent till root level and add eligible candidate to removeList
			Set<String> allHirearchyOfEntity = new HashSet<String>();
			checkIfParentPresent(entity, allHirearchyOfEntity, relEntityMap, entityCatIds, catId, removeList, entityInfoCache);
		}
		removeItemFromEntityMap(removeList, relEntityMap);
	}

	private void removeItemFromEntityMap(final Set<String> removeList, final Map<String, TrendingEntity> relEntityMap) {
		if (!removeList.isEmpty()) {
			LOG.debug("Subsidaries which are to be removed  from trending list: " + removeList.toString());
			for (String key : removeList) {
				relEntityMap.remove(key);
			}
		}
	}
 
	public void checkIfParentPresent(IEntityInfo entity, Set<String> allHirearchyOfEntity, Map<String, TrendingEntity> relEntityMap,
			Set<String> entityCatIds, String catId, Set<String> removeList, IEntityInfoCache entityInfoCache) {
		if (entity == null) {
			return;
		}
		allHirearchyOfEntity.add(entity.getId());
		int parent = entity.getSubsidiaryOf();
		if (parent > 0) {
			IEntityInfo parentEntity = entityInfoCache.companyIdToEntity(parent);
			if (parentEntity == null) {
				LOG.warn("Entity id: " + entity.getId()
						+ " has invalid parent company mapped in solr, No entity found for parent company id: " + parent);
				return;
			}
			LOG.debug("entity id:::" + entity.getId() + " , Parent id:::" + parentEntity.getId());
			if (relEntityMap.containsKey(parentEntity.getId()) || entityCatIds.contains(parentEntity.getId())) {
				// remove this entry
				LOG.debug(catId + "became eligible for removal by subsidary algo");
				removeList.add(catId);
			} else {
				if (!isCycleInRecurssion(parentEntity.getId(), allHirearchyOfEntity)) {
					checkIfParentPresent(parentEntity, allHirearchyOfEntity, relEntityMap, entityCatIds, catId, removeList,
							entityInfoCache);
				} else {
					// terminate recursion loop
					LOG.error("Detected Cycle in parent subsidary mapping of an entity, logging all catids till point cycle found:: "
							+ allHirearchyOfEntity.toString());
					return;
				}
			}
		}

	}

	private boolean isCycleInRecurssion(String parentEntity, Set<String> allHirearchyOfEntity) {
		return allHirearchyOfEntity.contains(parentEntity);
	}

	public void removeSelfAndParentSubsidaryEntity(Map<String, TrendingEntity> relEntityMap, Map<String, TrendingEntity> relTopicMap,
			Set<String> entityCatIds) {

		IEntityInfoCache entityInfoCache = entityBaseServiceRepository.getEntityInfoCache();

		for (String catId : entityCatIds) {
			relEntityMap.remove(catId);
			relTopicMap.remove(catId);
			IEntityInfo entity = entityInfoCache.catIdToEntity(catId);
			if (entity == null) {
				LOG.warn("No Entity found for cat id: " + catId);
				return;
			}
			int parent = entity.getSubsidiaryOf();
			if (parent > 0) {
				IEntityInfo subsidaryOf = entityInfoCache.companyIdToEntity(parent);
				if (subsidaryOf == null) {
					LOG.warn("Entity id: " + catId + " has invalid parent company mapped in solr, No entity found for parent company id: "
							+ parent);
					return;
				}
				String id = subsidaryOf.getId();
				if (relEntityMap.containsKey(id)) {
					relEntityMap.remove(id);
				}
				if (relTopicMap.containsKey(id)) {
					relTopicMap.remove(id);
				}

			}
		}
	}

	private void getRelatedEntity(Map<String, TrendingEntity> relCompMap, Map<String, TrendingEntity> relTopicMap,
			List<SuggestedSearchGroup> grpList) throws SolrServerException, SearchException {

		if (grpList != null) {
			String useIndustryTopics = null;
			int countCompanies = 0;
			int countTopics = 0;

			for (SuggestedSearchGroup ssGroup : grpList) {
				if (countTopics == MAX_RELATED_ENTITY && countCompanies == MAX_RELATED_ENTITY) {
					break;
				}

				List<QueryEntry> queries = ssGroup.getQueries();
				if (queries == null || queries.isEmpty()) {
					continue;
				}

				if (ssGroup.getGroupType() == SuggestedSearchGroup.GroupType.COMPANIES && countCompanies < MAX_RELATED_ENTITY) {
					for (QueryEntry queryEntry : queries) {
						addToTrendingentityMap(relCompMap, queryEntry);
						countCompanies++;
						if (countCompanies == MAX_RELATED_ENTITY) {
							break;
						}
					}
				}

				if ((ssGroup.getGroupType() == SuggestedSearchGroup.GroupType.INDUSTRY_TOPICS
						|| ssGroup.getGroupType() == SuggestedSearchGroup.GroupType.INDUSTRY_TRENDS) && countTopics < MAX_RELATED_ENTITY) {
					for (QueryEntry queryEntry : queries) {

						if (ssGroup.getGroupType() == SuggestedSearchGroup.GroupType.INDUSTRY_TOPICS && (useIndustryTopics == null
								|| useIndustryTopics.equals(SuggestedSearchGroup.GroupType.INDUSTRY_TOPICS.name()))) {
							useIndustryTopics = SuggestedSearchGroup.GroupType.INDUSTRY_TOPICS.name();
							addToTrendingentityMap(relTopicMap, queryEntry);
							countTopics++;
							if (countTopics == MAX_RELATED_ENTITY) {
								break;
							}
						} else if (ssGroup.getGroupType() == SuggestedSearchGroup.GroupType.INDUSTRY_TRENDS && (useIndustryTopics == null
								|| useIndustryTopics.equals(SuggestedSearchGroup.GroupType.INDUSTRY_TRENDS.name()))) {
							useIndustryTopics = SuggestedSearchGroup.GroupType.INDUSTRY_TRENDS.name();
							addToTrendingentityMap(relTopicMap, queryEntry);
							countTopics++;
							if (countTopics == MAX_RELATED_ENTITY) {
								break;
							}

						}
					}
				}
			}
		}

	}


	private void addToTrendingentityMap(Map<String, TrendingEntity> relTopicMap, QueryEntry queryEntry) {
		String id = null;
		id = queryEntry.searchTokens.get(0).id;
		TrendingEntity Obj = relTopicMap.get(id);
		if (Obj != null) {
			Obj.setEntityCount(Obj.getEntityCount() + 1);
			LOG.debug("Entity already found in comention map so increasing entity count:: " + id);
		} else {
			Obj = createTrendingEntityObj(id);
			LOG.debug("Added related entity to trending entity list:: " + id);
		}
		relTopicMap.put(id, Obj);
	}

	private TrendingEntity createTrendingEntityObj(String id) {
		TrendingEntity Obj;
		Obj = new TrendingEntity();
		Obj.setEntityId(id);
		Obj.setEntityCount(1);
		return Obj;
	}


	private void getComentionEntitiy(Map<String, TrendingEntity> relCompMap, HotListBucket coMenEntity, int count) throws Exception {

		int i = 0;
		for (HotListEntry entry : coMenEntity.getEntries()) {

			EntityEntry entity = entry.getEntity();
			if (!entity.isValid() || entity.inPositiveQ) {
				continue;
			}
			IEntityInfo ei = entityBaseServiceRepository.getEntityInfoCache().catIdToEntity(entity.getId());
			if (ei == null) {
				continue;
			}
			if (ei.isExceptional() || ei.getSectorCatId() == 695885) {
				continue;
			}

			int docCount = entry.getDocCount(); // doc counts
			LOG.debug("entity == " + entity.getSearchToken() + " counts == " + docCount);
			if (docCount <= 2) {
				continue;
			}

			TrendingEntity obj = relCompMap.get(entity.getId());
			if (obj == null) {
				obj = createTrendingEntity(entity);
				relCompMap.put(entity.getId(), obj);
			}
			obj.setDocCount(docCount);
			i++;
			if (i == count) {
				break;
			}
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("List of added comention entities :: " + relCompMap.keySet().toString());
		}
	}

	private TrendingEntity createTrendingEntity(final EntityEntry entity) {
		TrendingEntity obj = new TrendingEntity();
		obj.setEntityId(entity.getId());
		obj.setEntityCount(0);
		return obj;
	}

	private SearchResult getSearchResult(List<String> qList, List<Integer> scopeList, int days, boolean needSearchSuggestion)
			throws SearchException {

		SearchSpec ss = new SearchSpec(new SearchSpec().setTagExclusionScope(SearchSpec.SCOPE_BROAD));
		ss.optimalScopeAndDays = false;
		ss.useLikelySearchIntention = false;
		ss.needSearchSuggestion = needSearchSuggestion;
		ss.needHighlighting = false;
		ss.needHotListAll = false;
		ss.needHotListCompany = true;
		ss.needHotListTopic = true;
		ss.needHotListRegion = false;
		ss.hotListRows = 30;
		ss.hotlistScope = SearchSpec.SCOPE_MEDIUM;
		ss.setOrder(SearchSpec.ORDER_DATE);
		ss.days = days;
		ss.setStart(0);
		ss.setRows(1);

		ss.qMulti = FR_ArrayUtils.collectionToStringArray(qList);
		ss.scopeMulti = FR_ArrayUtils.collectionToIntArray(scopeList);

		return entityBaseServiceRepository.getSearcher().search(ss);
	}

	private SearchResult getSearchResultforGeo(List<String> qList, List<Integer> scopeList) throws SearchException {
		int days = 31;
		int partitionDays = 1;

		SearchSpec ss = new SearchSpec(new SearchSpec().setTagExclusionScope(SearchSpec.SCOPE_BROAD));
		ss.optimalScopeAndDays = false;
		ss.useLikelySearchIntention = false;
		ss.needSearchSuggestion = false;
		ss.needHighlighting = false;
		ss.needHotListAll = false;
		ss.needHotListRegion = true;
		ss.hotListRows = 150;
		ss.hotlistScope = SearchSpec.SCOPE_MEDIUM;
		/* to get the Highlights and section infor */
		ss.sectionMulti = true;
		ss.setOrder(SearchSpec.ORDER_DATE);
		ss.days = days;
		ss.setStart(0);
		ss.setRows(1);

		ss.qMulti = FR_ArrayUtils.collectionToStringArray(qList);
		ss.scopeMulti = FR_ArrayUtils.collectionToIntArray(scopeList);
		Arrays.fill(ss.scopeMulti, SearchSpec.SCOPE_BROAD);

		ss.insertTimeMarkers = new Date[] {new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(days)),
				new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(partitionDays)),};
		ss.facetInsertTimeMarkers = ss.insertTimeMarkers;
		return entityBaseServiceRepository.getSearcher().search(ss);
	}

	private IEntityInfo parsedSearchCriteria(List<String> qListParam, List<Integer> scopeList, Set<String> catIds, String filters,
			ChartSpec spec) {

		List<String> qList = qListParam;
		int entityCount = 0;
		IEntityInfo selentity = null;
		String filterQuery = "";
		if (filters != null) {
			filterQuery = filters.trim();
		}

		for (Items item : spec.getItemList()) {
			SearchSpec searchSpec = QueryParseUtil.parse(item.getData());
			if (searchSpec != null) {
				String fq = searchSpec.getFq();
				if (fq == null) {
					fq = filterQuery;
				} else {
					fq = fq + " " + filterQuery;
				}
				// custom graph is specific to customer and if search is filtered on fq then less or no doc count will be there
				if (spec.isGraphCustomized()) {
					qList.add(searchSpec.getQ().trim());
				} else {
					qList.add((searchSpec.getQ() + " " + fq).trim());
				}
				scopeList.add(searchSpec.scope);

				List<SearchTokenEntry> tokens = SolrSearcher.parseInput(searchSpec.getQ());

				for (SearchTokenEntry token : tokens) {
					if (token.relation != Relation.MUST_NOT_HAVE) {
						IEntityInfo entity = entityBaseServiceRepository.getEntityInfoCache().searchTokenToEntity(token.searchToken);
						if (entity != null) {
							if (entity.getType() == SearchTokenEntry.SEARCH_TOKEN_TAG_COMPANY
									|| entity.getType() == SearchTokenEntry.SEARCH_TOKEN_TAG_SECTOR_TOPIC) {
								entityCount++;
								selentity = entity;
								catIds.add(entity.getId());
							} else if (entity.getType() == SearchTokenEntry.SEARCH_TOKEN_TAG_INDUSTRY) {
								entityCount++;
								selentity = entity;
							} else if (entity.getType() == SearchTokenEntry.SEARCH_TOKEN_TAG_PEOPLE) {
								entityCount++;
							}
						} else {
							entityCount++;
						}
					}
				}
			}
		}

		if (qList.size() >= SEARCHTOKEN_COUNT) {
			qList = qList.subList(0, SEARCHTOKEN_COUNT);
			scopeList = scopeList.subList(0, SEARCHTOKEN_COUNT);
		}

		if (entityCount > 1) {
			selentity = null;
		}
		return selentity;
	}

	@Override
	public VisualizationData getVisualizationByEntityToken(String searchToken, int nodeCount, List<ChartType> chartTypes, String filters,
			boolean isHtmlSmartText, boolean isApplyMinNodeCheck) throws Exception {

		IEntityInfo ei = entityBaseServiceRepository.getEntityInfoCache().searchTokenToEntity(searchToken);

		if (ei == null) {
			return null;
		}
		// currently in json format we return nodelist even if we have 1 node, for html the count of 4 nodes will be still applicable
		if (isApplyMinNodeCheck) {
			treeMapMinNode = 4;
		} else {
			treeMapMinNode = 1;
		}

		ChartSpec spec = new ChartSpec();
		spec.setTreeNodeCount(nodeCount);
		spec.setFilterQuery(filters);
		spec.setChartTypes(chartTypes);
		spec.setPartitionDay1(31);
		spec.setPartitionDay2(1);

		String chartId = "chart_";
		chartId += searchToken.replaceAll("[\"'.: ]", "_");

		Set<String> catIds = new HashSet<String>();
		catIds.add(ei.getId());

		List<String> qList = new ArrayList<String>();
		List<Integer> scopeList = new ArrayList<Integer>();

		if (filters == null || filters.isEmpty()) {
			qList.add(ei.getSearchToken());
		} else {
			qList.add((ei.getSearchToken() + " " + filters).trim());
		}
		scopeList.add(ei.getScope());

		FRCompletionService<BaseSet> completionService = new FRCompletionService<BaseSet>(taskExecutor.getThreadPoolExecutor());

		if (ei.getType() == SearchTokenEntry.SEARCH_TOKEN_TAG_INDUSTRY) {
			/*
			 * 1. Fetch Search Result of Monitor 2. Get 150 co-mention Company and Topic 3. Prepare bucket of Similar Entity, Other Company,
			 * Other Topic. 4. From Bucket now run the Algo of generating the top 20 entity of each tree-map. 5. first Bucket of Similar
			 * Entity will be considered for Accelerometer
			 */

			ChartDetails cd = getIndusrtyChartDetails(ei, qList, scopeList);

			if (chartTypes.indexOf(ChartType.TREE_MONITOR_SEARCH) >= 0 || chartTypes.indexOf(ChartType.TREE_COMPANY) >= 0
					|| chartTypes.indexOf(ChartType.TREE_TOPICS) >= 0) {
				// Async Thread for BI and MD and Monitor Activity
				Callable<BaseSet> treegraphFuture = getTreeChartDataAsync(cd, spec.getTreeNodeCount(), chartId);
				completionService.submit(treegraphFuture);
			}

			getAccelerometerGraphIndustryAsyncAndSubmit(chartTypes, cd, chartId, isHtmlSmartText, completionService);
		} else {
			getChartDataforBIandMDAsyncAndSubmit(chartTypes, completionService, spec, qList, scopeList, catIds, chartId);

			getAndSubmitData(chartTypes, catIds, chartId, isHtmlSmartText, completionService);
		}

		if (chartTypes.indexOf(ChartType.GEO_US) >= 0 || chartTypes.indexOf(ChartType.GEO_WORLD) >= 0) {
			// Async Thread for Region Maps
			Callable<BaseSet> graphTTandGEOFuture = getChartDataforGEOAsync(qList, scopeList, chartId);
			completionService.submit(graphTTandGEOFuture);
		}

		// collect Graph
		VisualizationData graphData = collectResults(completionService, chartTypes);
		Entity entity = new Entity();
		entity.setSearchToken(ei.getSearchToken());
		entity.setName(ei.getName());
		graphData.setEntity(entity);

		return graphData;
	}

	private void getChartDataforBIandMDAsyncAndSubmit(final List<ChartType> chartTypes, final FRCompletionService<BaseSet> completionService, final ChartSpec spec, final List<String> qList, final List<Integer> scopeList, final Set<String> catIds, final String chartId) {
		if (chartTypes.indexOf(ChartType.TREE_COMPANY) >= 0 || chartTypes.indexOf(ChartType.TREE_TOPICS) >= 0) {
			// Async Thread for BI and MD
			Callable<BaseSet> graphBIandMDFuture = getChartDataforBIandMDAsync(spec, qList, scopeList, catIds, chartId);
			completionService.submit(graphBIandMDFuture);
		}
	}

	private void getAccelerometerGraphIndustryAsyncAndSubmit(final List<ChartType> chartTypes, final ChartDetails cd, final String chartId, final boolean isHtmlSmartText, final FRCompletionService<BaseSet> completionService) {
		if (chartTypes.indexOf(ChartType.ACC_METER) >= 0) {
			// Async Thread for Accelerometer Graph
			Callable<BaseSet> graphACCFuture =
					getAccelerometerGraphIndustryAsync(cd.getAccmeterList(), chartId, false, isHtmlSmartText);
			completionService.submit(graphACCFuture);
		}
	}

	private void getAndSubmitData(final List<ChartType> chartTypes, final Set<String> catIds, final String chartId, final boolean isHtmlSmartText, final FRCompletionService<BaseSet> completionService) {
		if (chartTypes.indexOf(ChartType.ACC_METER) >= 0) {
			// Async Thread for Accelerometer Graph
			Callable<BaseSet> graphACCFuture = getChartDataforACCAsync(catIds, chartId, isHtmlSmartText);
			completionService.submit(graphACCFuture);
		}
	}
 
	private VisualizationData collectResults(FRCompletionService<BaseSet> completionService, final List<ChartType> chartTypes)
			throws InterruptedException, ExecutionException {

		VisualizationData graphData = new VisualizationData();

		int submissions = completionService.getSubmissions();

		List<Graph> graphList = new ArrayList<Graph>();

		for (int i = 0; i < submissions; i++) {
			BaseSet obj = null;
			Future<BaseSet> f = completionService.poll(this.executorTimeout, TimeUnit.MILLISECONDS);
			if (f != null) {
				obj = f.get();
				if (obj != null) {
					graphList.addAll(((GraphSet) obj).getGraphs());
					graphData.setPerfStats(obj);
				}
			} else {
				LOG.warn("service not responded for the given timeout " + this.executorTimeout);
			}
		}

		graphData.graphs = new TreeMap<ChartType, Graph>(new Comparator<ChartType>() {
			@Override
			public int compare(ChartType o1, ChartType o2) {
				Integer index1 = chartTypes.indexOf(o1);
				Integer index2 = chartTypes.indexOf(o2);
				return index1.compareTo(index2);
			}
		});

		for (Graph graph : graphList) {
			graphData.graphs.put(graph.chartType, graph);
		}

		return graphData;
	}


	private Callable<BaseSet> getTreeChartDataAsync(final ChartDetails cd, final int nodeCount, final String chartId) {
		return new Callable<BaseSet>() {
			@Override
			public GraphSet call() throws Exception {
				GraphSet graphSet = getTreeChartData(cd, nodeCount, chartId);
				return graphSet;
			}
		};
	}

	private GraphSet getTreeChartData(ChartDetails cd, int nodeCount, String chartId) {
		PerfMonitor.startRequest("", "treeMap");
		long start = PerfMonitor.currentTime();
		GraphSet graphSet = new GraphSet();
		try {
			Graph graphBI = getTreemapGraph(cd.getTrendingCompanyList(), nodeCount, ChartType.TREE_COMPANY, chartId);
			graphSet.addGraphs(graphBI);

			Graph graphMD = getTreemapGraph(cd.getTrendingTopicList(), nodeCount, ChartType.TREE_TOPICS, chartId);
			graphSet.addGraphs(graphMD);

			Graph graph =
					getTreemapGraphForIndustryMonitor(cd.getMonitorTrendingEntityList(), nodeCount, ChartType.TREE_MONITOR_SEARCH, chartId);
			graphSet.addGraphs(graph);

		} catch (Exception e) {
			LOG.error("Exception Fetching getTreeChartData!", e);
		} finally {
			PerfMonitor.recordStats(start, PerfActivityType.ReqTime);
			PerfRequestEntry stat = PerfMonitor.endRequest();
			graphSet.setStat(stat);
		}
		return graphSet;
	}

	private Callable<BaseSet> getAccelerometerGraphIndustryAsync(final List<ChartCountSummary> entityList, final String chartId,
			final boolean ipad, final boolean isHtmlSmartText) {
		return new Callable<BaseSet>() {
			@Override
			public GraphSet call() throws Exception {
				GraphSet graphSet = getAccelerometerGraphIndustry(entityList, chartId, ipad, isHtmlSmartText);
				return graphSet;
			}
		};
	}

	private GraphSet getAccelerometerGraphIndustry(List<ChartCountSummary> entityList, String chartId, boolean ipad,
			boolean isHtmlSmartText) {

		PerfMonitor.startRequest("", "AccChart");
		long start = PerfMonitor.currentTime();
		GraphSet graphSet = new GraphSet();
		try {
			Set<String> catIds = new HashSet<String>();
			for (ChartCountSummary ccs : entityList) {
				catIds.add(ccs.getEntityId());
			}
			LOG.debug("catIds == " + catIds.size() + " == " + catIds.toString());

			Graph graph = new Graph(chartId, ChartType.ACC_METER);

			List<Node> nodes =
					getAccelerometerNode(FR_ArrayUtils.getCSVFromCollection(catIds), false, ChartType.ACC_METER, ipad, isHtmlSmartText);
			if (nodes != null && !nodes.isEmpty()) {
				Collections.sort(nodes, new Comparator<Node>() {
					@Override
					public int compare(Node o1, Node o2) {
						return -Float.valueOf(o1.value).compareTo(o2.value);
					}
				});
				if (nodes.size() > 6) {
					for (int i = nodes.size() - 1; i >= 6; i--) {
						nodes.remove(i);
					}
				}
				if (nodes.size() % 2 != 0) { // remove odd one, we need count as 2,4,6
					nodes.remove(nodes.size() - 1);
				}

				if (!nodes.isEmpty()) {
					graph.nodes = nodes;
				}
			}
			graphSet.addGraphs(graph);
		} catch (Exception e) {
			LOG.error("Exception Fetching getAccelerometerGraphIndustry!", e);
		} finally {
			PerfMonitor.recordStats(start, PerfActivityType.ReqTime);
			PerfRequestEntry stat = PerfMonitor.endRequest();
			graphSet.setStat(stat);
		}
		return graphSet;
	}

	private Callable<BaseSet> getChartDataforACCAsync(final Set<String> catIds, final String chartId, final boolean isHtmlSmartText) {
		return new Callable<BaseSet>() {
			@Override
			public GraphSet call() throws Exception {
				GraphSet graphSet = getChartDataforACC(catIds, chartId, isHtmlSmartText);
				return graphSet;
			}
		};
	}

	private GraphSet getChartDataforACC(Set<String> catIds, String chartId, boolean isHtmlSmartText) {
		PerfMonitor.startRequest("", "AccChart");
		long start = PerfMonitor.currentTime();
		GraphSet graphSet = new GraphSet();
		try {
			boolean isSingleEntity = false;
			if (catIds.size() == 1) {
				isSingleEntity = true;
			}
			Graph graph = getAccelerometerGraph(ChartType.ACC_METER, chartId, catIds, isSingleEntity, false, isHtmlSmartText);
			graphSet.addGraphs(graph);

		} catch (Exception e) {
			LOG.error("Exception Fetching getChartDataforACC!", e);
		} finally {
			PerfMonitor.recordStats(start, PerfActivityType.ReqTime);
			PerfRequestEntry stat = PerfMonitor.endRequest();
			graphSet.setStat(stat);
		}
		return graphSet;
	}

	private Callable<BaseSet> getChartDataforBIandMDAsync(final ChartSpec spec, final List<String> qList, final List<Integer> scopeList,
			final Set<String> catIds, final String chartId) {
		return new Callable<BaseSet>() {
			@Override
			public GraphSet call() throws Exception {
				GraphSet graphSet = getChartDataforBIandMD(spec, qList, scopeList, catIds, chartId);
				return graphSet;
			}
		};
	}


	private Callable<BaseSet> getChartDataforMonitorGEOandTTAsync(final List<Items> itemList, final List<String> qList,
			final List<Integer> scopeList, final int nodeCount, final String chartId) {
		return new Callable<BaseSet>() {
			@Override
			public GraphSet call() throws Exception {
				GraphSet graphSet = getChartDataforMonitorGEOandTT(itemList, qList, scopeList, nodeCount, chartId);
				return graphSet;
			}
		};
	}

	private Callable<BaseSet> getChartDataforGEOAsync(final List<String> qList, final List<Integer> scopeList, final String chartId) {
		return new Callable<BaseSet>() {
			@Override
			public GraphSet call() throws Exception {
				GraphSet graphSet = getChartDataforGEO(qList, scopeList, chartId);
				return graphSet;
			}
		};
	}

	private GraphSet getChartDataforGEO(List<String> qList, List<Integer> scopeList, String chartId) {
		PerfMonitor.startRequest("", "GEO");
		long start = PerfMonitor.currentTime();
		GraphSet graphSet = new GraphSet();
		try {
			if (qList != null && !qList.isEmpty()) {
				SearchResult searchResult = getSearchResultforGeo(qList, scopeList);

				List<ChartType> chartTypes = new ArrayList<ChartType>();
				chartTypes.add(ChartType.GEO_US);
				chartTypes.add(ChartType.GEO_WORLD);

				List<Graph> graphList = getGeoMapGraphs(getTrendingRegion(searchResult), chartTypes, chartId);
				graphSet.addAllGraphs(graphList);

			}
		} catch (Exception e) {
			LOG.error("Exception Fetching getChartDataforGEO!", e);
		} finally {
			PerfMonitor.recordStats(start, PerfActivityType.ReqTime);
			PerfRequestEntry stat = PerfMonitor.endRequest();
			graphSet.setStat(stat);
		}
		return graphSet;
	}

	private GraphSet getChartDataforMonitorGEOandTT(List<Items> itemList, List<String> qList, List<Integer> scopeList, int nodeCount,
			String chartId) {
		PerfMonitor.startRequest("", "GEO&TT");
		long start = PerfMonitor.currentTime();
		GraphSet graphSet = new GraphSet();
		try {
			if (qList != null && !qList.isEmpty()) {
				SearchResult searchResult = getSearchResultforGeo(qList, scopeList);

				List<ChartType> chartTypes = new ArrayList<ChartType>();
				chartTypes.add(ChartType.GEO_US);
				chartTypes.add(ChartType.GEO_WORLD);

				List<Graph> graphList = getGeoMapGraphs(getTrendingRegion(searchResult), chartTypes, chartId);
				graphSet.addAllGraphs(graphList);

				List<DocListBucket> ls = searchResult.buckets.subList(1, searchResult.buckets.size());

				List<ChartCountSummary> searchsummaryList = new ArrayList<ChartCountSummary>();
				int i = 0;
				for (Items item : itemList) {
					ChartCountSummary dse = new ChartCountSummary();
					dse.setSearchId(item.getId());

					DocListBucket bucket = ls.get(i);
					List<Integer> counts = bucket.getPartitionCounts();
					// 31,31,1 for actual graph
					if (counts != null) {
						dse.setThirtyOneDaysDocCount(counts.get(1) + counts.get(2));
						dse.setOneDayDocCount(counts.get(2));
					}

					LOG.debug("thirtyOne Day Count = " + dse.getThirtyOneDaysDocCount() + " One Day Count == " + dse.getOneDayDocCount());
					searchsummaryList.add(dse);
					i++;
					if (i == SEARCHTOKEN_COUNT) {
						break;
					}
				}

				Graph graph = getTreemapGraphForMonitor(searchsummaryList, itemList, nodeCount, ChartType.TREE_MONITOR_SEARCH, chartId);
				graphSet.addGraphs(graph);

			}
		} catch (Exception e) {
			LOG.error("Exception Fetching getChartDataforGEO!", e);
		} finally {
			PerfMonitor.recordStats(start, PerfActivityType.ReqTime);
			PerfRequestEntry stat = PerfMonitor.endRequest();
			graphSet.setStat(stat);
		}
		return graphSet;
	}


	private GraphSet getChartDataforBIandMD(ChartSpec spec, List<String> qList, List<Integer> scopeList, Set<String> catIds,
			String chartId) {
		PerfMonitor.startRequest("", "BI&MD");
		long start = PerfMonitor.currentTime();
		GraphSet graphSet = new GraphSet();
		try {
			if (qList != null && !qList.isEmpty()) {

				boolean needSearchSuggestion = false;
				int count = 30;
				if (catIds.size() == 1) {
					count = 15;
					needSearchSuggestion = true;
				}

				// SearchResult searchResult = getSearchResult(qList, scopeList, spec.getPartitionDay1(), spec.getPartitionDay2(), false,
				// fetchSSearch, false);

				// only 7 days search
				SearchResult searchResult = getSearchResult(qList, scopeList, 7, needSearchSuggestion);

				Map<String, TrendingEntity> relCompMap = new LinkedHashMap<String, TrendingEntity>();
				Map<String, TrendingEntity> relTopicMap = new LinkedHashMap<String, TrendingEntity>();

				HotListBucket coMenCompany = searchResult.facetCompanies;
				HotListBucket coMenTopic = searchResult.facetTopics;

				if (coMenCompany != null) {
					getComentionEntitiy(relCompMap, coMenCompany, count);
				}
				if (coMenTopic != null) {
					getComentionEntitiy(relTopicMap, coMenTopic, count);
				}

				if (needSearchSuggestion) {
					getRelatedEntity(relCompMap, relTopicMap, searchResult.suggestedSearchGroups);
				}

				removeSelfAndParentSubsidaryEntity(relCompMap, relTopicMap, catIds);
				removeSubsidaries(relCompMap, catIds);
				removeSubsidaries(relTopicMap, catIds);

				List<ChartCountSummary> companyList = getTrendingList(relCompMap, TRENDING_COUNT);
				List<ChartCountSummary> topicList = getTrendingList(relTopicMap, TRENDING_COUNT);

				Graph graphBI = getTreemapGraph(companyList, spec.getTreeNodeCount(), ChartType.TREE_COMPANY, chartId);
				graphSet.addGraphs(graphBI);

				Graph graphMD = getTreemapGraph(topicList, spec.getTreeNodeCount(), ChartType.TREE_TOPICS, chartId);
				graphSet.addGraphs(graphMD);
			}
		} catch (Exception e) {
			LOG.error("Exception Fetching getChartDataforBIandMD!", e);
		} finally {
			PerfMonitor.recordStats(start, PerfActivityType.ReqTime);
			PerfRequestEntry stat = PerfMonitor.endRequest();
			graphSet.setStat(stat);
		}
		return graphSet;
	}

}
