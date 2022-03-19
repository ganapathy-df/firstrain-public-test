package com.firstrain.frapi.service.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import com.firstrain.db.obj.Accelerometer;
import com.firstrain.db.obj.Items;
import com.firstrain.db.obj.Tags;
import com.firstrain.frapi.FRCompletionService;
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
import com.firstrain.frapi.repository.MonitorServiceRepository;
import com.firstrain.frapi.repository.impl.AccelerometerServiceRepositoryImpl;
import com.firstrain.frapi.repository.impl.AutoSuggestServiceRepositoryImpl;
import com.firstrain.frapi.repository.impl.MonitorServiceRepositoryImpl;
import com.firstrain.frapi.util.QueryParseUtil;
import com.firstrain.obj.IEntityInfo;
import com.firstrain.obj.IEntityInfoCache;
import com.firstrain.solr.client.DocListBucket;
import com.firstrain.solr.client.EntityEntry;
import com.firstrain.solr.client.HotListBucket;
import com.firstrain.solr.client.HotListEntry;
import com.firstrain.solr.client.QueryEntry;
import com.firstrain.solr.client.SearchException;
import com.firstrain.solr.client.SearchPattern;
import com.firstrain.solr.client.SearchPattern.Pattern;
import com.firstrain.solr.client.SearchResult;
import com.firstrain.solr.client.SearchSpec;
import com.firstrain.solr.client.SearchTokenEntry;
import com.firstrain.solr.client.SolrSearcher;
import com.firstrain.solr.client.SuggestedSearchGroup;
import com.firstrain.solr.client.SuggestedSearchGroup.GroupType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
    VisualizationServiceImpl.class,
    QueryParseUtil.class,
    SolrSearcher.class
})
public class VisualizationServiceImplTest {

    private static final String TEST_STRING = "TEST";
    private static final String FACET_SORT_COUNT = "count";
    private static final String GET_INDUSTRY_CHART_DETAILS = "getIndusrtyChartDetails";
    private static final String TREE_MAP_MIN_NODE_FIELD = "treeMapMinNode";
    private static final String DOC_SUMMARY_MAP_FIELD = "docSummaryMap";
    private static final String POPULATE_ENTITY_METHOD = "populateComentionEntitiy";
    private static final String GET_ACCELEROMETER_NODE_METHOD = "getAccelerometerNode";
    private static final String GET_ACCELEROMETER_GRAPH_METHOD = "getAccelerometerGraph";
    private static final String GET_TRENDING_REGION_METHOD = "getTrendingRegion";
    private static final String GET_TRENDING_LIST_METHOD = "getTrendingList";
    private static final String GET_CHART_DATA_FOR_ACC_METHOD = "getChartDataforACCAsync";
    private static final String GET_TREE_CHART_DATA_METHOD = "getTreeChartDataAsync";
    private static final String GET_CHART_DATA_FOR_GEO_METHOD = "getChartDataforGEOAsync";
    private static final String GET_CHART_DATA_FOR_ASYNC_METHOD = "getChartDataforBIandMDAsync";
    private static final String GET_ACCELEROMETER_GRAPH_INDUSTRY_METHOD = "getAccelerometerGraphIndustryAsync";
    private static final String ADD_BUCKET_METHOD = "addBucket";
    private static final String GET_GEO_MAP_GRAPHS_METHOD = "getGeoMapGraphs";
    private static final String GET_ENTITY_METHOD = "getComentionEntitiy";
    private static final String GET_SEARCH_RESULTFOR_GEO_METHOD = "getSearchResultforGeo";
    private static final String GET_TREE_MAP_GRAPH_FOR_MONITOR_METHOD = "getTreemapGraphForMonitor";
    private static final String GET_MONITOR_GEO_AND_TT_METHOD = "getChartDataforMonitorGEOandTTAsync";
    private static final String GET_TREE_MAP_GRAPH_METHOD = "getTreemapGraph";
    private static final String GET_RELATED_ENTITY_METHOD = "getRelatedEntity";
    private static final String GET_TREE_MAP_GRAPH_FOR_INDUSTRY_MONITOR = "getTreemapGraphForIndustryMonitor";
    private static final String ID1 = "1";
    private static final String ID2 = "2";
    private static final String REMOVE_LIST_DEBUG_TXT =
        "Subsidaries which are to be removed " + " from trending list: ";
    private static final String WARN_MSG_TXT =
        "Entity id: " + ID1 + " has invalid parent company mapped in solr, "
            + "No entity found for parent company id: " + 1;
    private final static long TAG_ID = 1;

    private final int nodeCount = 1;
    private final ChartSpec spec = spy(new ChartSpec());
    private final AutoSuggestServiceRepositoryImpl entityBaseServiceRepository = spy(
        new AutoSuggestServiceRepositoryImpl());
    private final MonitorServiceRepository monitorServiceRepository = spy(
        new MonitorServiceRepositoryImpl());
    private final SolrSearcher solrSearcher = spy(new SolrSearcher());
    private final SearchResult searchResult = spy(new SearchResult());
    private final List<FacetField> facetFieldList = new ArrayList<FacetField>();
    private final FacetField facetField = spy(new FacetField(TEST_STRING));
    private final List<Count> counts = new ArrayList<Count>();
    private final List<String> stringList = new ArrayList<String>();
    private final Set<String> stringSet = new HashSet<String>();
    private final List<Integer> integerList = new ArrayList<Integer>();
    private final Count count = new Count(facetField, TEST_STRING, 1L);
    private final List<ChartType> chartTypes = new ArrayList<ChartType>();
    private final List<Items> itemList = new ArrayList<Items>();
    private final List<SearchTokenEntry> tokens = new ArrayList<SearchTokenEntry>();
    private final List<Accelerometer> accObjList = new ArrayList<Accelerometer>();
    private final List<Node> nodes = spy(new ArrayList<Node>());
    private final List<SuggestedSearchGroup> suggestedSearchGroups = spy(
        new ArrayList<SuggestedSearchGroup>());
    private final List<ChartCountSummary> regionList = spy(new ArrayList<ChartCountSummary>());
    private final List<NodeBucket> nodeBuckets = spy(new ArrayList<NodeBucket>());
    private final List<QueryEntry> queryEntries = spy(new ArrayList<QueryEntry>());
    private final List<DocListBucket> buckets = spy(new ArrayList<DocListBucket>());
    private final Items item = new Items();
    private final SearchTokenEntry searchTokenEntry = new SearchTokenEntry();
    private final SolrQuery solrQuery = spy(new SolrQuery());
    private final SearchSpec searchSpec = spy(new SearchSpec());
    private final Tags tags = spy(new Tags());
    private final Entity entity = spy(new Entity());
    private final VisualizationData graphData = spy(new VisualizationData());
    private final HotListBucket hotListBucket = spy(new HotListBucket(TEST_STRING));
    private final ChartDetails chartDetails = spy(new ChartDetails());
    private final TrendingEntity trendingEntity = spy(new TrendingEntity());
    private final Accelerometer accObj = spy(new Accelerometer());
    private final RegionExcelUtilImpl regionExcelUtilImpl = spy(new RegionExcelUtilImpl());
    private final Node node = spy(new Node());
    private final NodeBucket nodeBucket = spy(new NodeBucket(1));
    private final Graph graph = new Graph();
    private final CompanyEndingWords companyEndingWords = spy(new CompanyEndingWords());
    private final ChartCountSummary chartCountSummary = spy(new ChartCountSummary());
    private final SearchPattern searchPattern = spy(new SearchPattern(Pattern.COMPANY_REGION));
    private final QueryEntry queryEntry = spy(new QueryEntry(ID1, tokens));
    private final ChartDetails cd = spy(new ChartDetails());
    private final SuggestedSearchGroup suggestedSearchGroup = spy(
        new SuggestedSearchGroup(searchPattern, TEST_STRING, GroupType.COMPANIES));
    private final AccelerometerServiceRepository accelerometerServiceRepository = spy(
        new AccelerometerServiceRepositoryImpl());
    private final Map<String, TrendingEntity> relEntityMap = spy(
        new HashMap<String, TrendingEntity>());
    private final Map<String, String> stringMap = new HashMap<String, String>();
    private final Map<String, ChartCountSummary> docSummaryMap =
        new ConcurrentHashMap<String, ChartCountSummary>();
    private final ErrorCollector collector = new ErrorCollector();
    private final ExpectedException thrown = ExpectedException.none();
    private final VisualizationServiceImpl visualizationService = spy(
        new VisualizationServiceImpl());

    @Rule
    public final RuleChain chain = RuleChain.outerRule(collector).around(thrown);

    @Mock
    private QueryRequest qRequest;
    @Mock
    private QueryResponse qResponse;
    @Mock
    private Logger logger;
    @Mock
    private ThreadPoolTaskExecutor taskExecutor;
    @Mock
    private ThreadPoolExecutor threadPoolExecutor;
    @Mock
    private FRCompletionService<BaseSet> completionService;
    @Mock
    private IEntityInfoCache iEntityInfoCache;
    @Mock
    private IEntityInfo iEntityInfo;
    @Mock
    private DocListBucket docListBucket;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(QueryParseUtil.class, SolrSearcher.class);
        Whitebox.setInternalState(visualizationService, "entityBaseServiceRepository",
            entityBaseServiceRepository);
        Whitebox.setInternalState(visualizationService, "monitorServiceRepository",
            monitorServiceRepository);
        Whitebox.setInternalState(visualizationService, "accelerometerServiceRepository",
            accelerometerServiceRepository);
        Whitebox.setInternalState(visualizationService, "regionExcelUtilImpl",
            regionExcelUtilImpl);
        Whitebox.setInternalState(visualizationService, "companyEndingWords",
            companyEndingWords);
        Whitebox.setInternalState(visualizationService, DOC_SUMMARY_MAP_FIELD,
            docSummaryMap);
        Whitebox.setInternalState(visualizationService, "taskExecutor", taskExecutor);
        Whitebox.setInternalState(visualizationService, "LOG", logger);
        facetFieldList.add(facetField);
        counts.add(count);
        chartTypes.add(ChartType.GEO_WORLD);
        chartTypes.add(ChartType.TREE_TOPICS);
        chartTypes.add(ChartType.ACC_METER);
        itemList.add(item);
        tokens.add(searchTokenEntry);
        stringList.add(TEST_STRING);
        integerList.add(1);
        relEntityMap.put(ID1, trendingEntity);
        accObjList.add(accObj);
        nodes.add(node);
        regionList.add(chartCountSummary);
        nodeBuckets.add(nodeBucket);
        suggestedSearchGroups.add(suggestedSearchGroup);
        queryEntries.add(queryEntry);
        buckets.add(docListBucket);
        tags.setTagName(TEST_STRING);
        accObj.setEntityId(ID1);
        whenNew(QueryRequest.class).withAnyArguments().thenReturn(qRequest);
        whenNew(SolrQuery.class).withNoArguments().thenReturn(solrQuery);
        whenNew(ChartSpec.class).withNoArguments().thenReturn(spec);
        whenNew(Entity.class).withNoArguments().thenReturn(entity);
        whenNew(VisualizationData.class).withNoArguments().thenReturn(graphData);
        whenNew(FRCompletionService.class).withArguments(threadPoolExecutor)
            .thenReturn(completionService);
        whenNew(SearchSpec.class).withAnyArguments().thenReturn(searchSpec);
        whenNew(ChartDetails.class).withNoArguments().thenReturn(chartDetails);
        whenNew(Graph.class).withAnyArguments().thenReturn(graph);
        when(qRequest.process(entityBaseServiceRepository.getDocSolrServer()))
            .thenReturn(qResponse);
        when(qResponse.getFacetFields()).thenReturn(facetFieldList);
        when(facetField.getValues()).thenReturn(counts);
        when(taskExecutor.getThreadPoolExecutor()).thenReturn(threadPoolExecutor);
        when(QueryParseUtil.parse(item.getData())).thenReturn(searchSpec);
        when(entityBaseServiceRepository.getEntityInfoCache()).thenReturn(iEntityInfoCache);
        when(SolrSearcher.parseInput(searchSpec.getQ())).thenReturn(tokens);
        when(iEntityInfoCache.searchTokenToEntity(searchTokenEntry.searchToken))
            .thenReturn(iEntityInfo);
        when(entityBaseServiceRepository.getSearcher()).thenReturn(solrSearcher);
    }

    @Test
    public void givenVisualizationServiceWhenInitThenLoadEntityDocData() throws Exception {
        //Act
        visualizationService.init();
        //Assert
        collector.checkThat(solrQuery.getQueryType(), equalTo("standard"));
        collector.checkThat(solrQuery.getRows(), equalTo(0));
        collector.checkThat(solrQuery.getFacetSort(), equalTo(Boolean.TRUE));
        collector.checkThat(solrQuery.getFacetSortString(), equalTo(FACET_SORT_COUNT));
        verify(visualizationService).loadEntityDocData();
    }

    @Test
    public void givenVisualizationServiceWhenInitAndExceptionThenLogError() throws Exception {
        //Arrange
        when(qResponse.getFacetFields()).thenThrow(Exception.class);
        //Act
        visualizationService.init();
        //Assert
        verify(logger).error(any(String.class), any(Exception.class));
    }

    @Test
    public void givenTagIdAndNodeCountWhenGetVisualizationByMonitorIdThenSetSpec()
        throws Exception {
        //Arrange
        doReturn(itemList).when(monitorServiceRepository).getItemsByTagId(TAG_ID);
        doReturn(tags).when(monitorServiceRepository).getTagById(TAG_ID);
        when(iEntityInfo.getType()).thenReturn(SearchTokenEntry.SEARCH_TOKEN_TAG_SECTOR_TOPIC);
        //Act
        visualizationService
            .getVisualizationByMonitorId(TAG_ID, nodeCount, chartTypes, TEST_STRING, true, true);
        int actualTreeMapMinNode = Whitebox.getInternalState(visualizationService,
            TREE_MAP_MIN_NODE_FIELD);
        //Assert
        collector.checkThat(entity.getId(), equalTo("M:" + TAG_ID));
        collector.checkThat(entity.getName(), equalTo(TEST_STRING));
        collector.checkThat(graphData.getEntity(), equalTo(entity));
        collector.checkThat(spec.getTreeNodeCount(), equalTo(nodeCount));
        collector.checkThat(spec.getFilterQuery(), equalTo(TEST_STRING));
        collector.checkThat(spec.getMonitorId(), equalTo(TAG_ID));
        collector.checkThat(spec.getPartitionDay1(), equalTo(7));
        collector.checkThat(spec.getPartitionDay2(), equalTo(1));
        collector.checkThat(actualTreeMapMinNode, equalTo(4));
    }

    @Test
    public void givenTagIdAndNodeCountWhenTypeIsSearchTokenTagIndustryThenSetSearchSpec()
        throws Exception {
        //Arrange
        doReturn(itemList).when(monitorServiceRepository).getItemsByTagId(TAG_ID);
        doReturn(tags).when(monitorServiceRepository).getTagById(TAG_ID);
        doReturn(searchResult).when(solrSearcher).search(searchSpec);
        searchResult.facetCompanies = hotListBucket;
        searchResult.facetTopics = hotListBucket;
        when(iEntityInfo.getType()).thenReturn(SearchTokenEntry.SEARCH_TOKEN_TAG_INDUSTRY);
        //Act
        visualizationService
            .getVisualizationByMonitorId(TAG_ID, nodeCount, chartTypes, TEST_STRING, true, false);
        int actualTreeMapMinNode = Whitebox.getInternalState(visualizationService,
            TREE_MAP_MIN_NODE_FIELD);
        when(iEntityInfo.getIndustryCatId()).thenThrow(SearchException.class);
        Whitebox.invokeMethod(visualizationService,
            GET_INDUSTRY_CHART_DETAILS, iEntityInfo, stringList, integerList);
        //Assert
        collector.checkThat(actualTreeMapMinNode, equalTo(1));
        collector.checkThat(searchSpec.optimalScopeAndDays, equalTo(false));
        collector.checkThat(searchSpec.useLikelySearchIntention, equalTo(false));
        collector.checkThat(searchSpec.needHighlighting, equalTo(false));
        collector.checkThat(searchSpec.needHotListAll, equalTo(false));
        collector.checkThat(searchSpec.needHotListCompany, equalTo(true));
        collector.checkThat(searchSpec.needHotListTopic, equalTo(true));
        collector.checkThat(searchSpec.needHotListRegion, equalTo(false));
        collector.checkThat(searchSpec.hotListRows, equalTo(30));
        collector.checkThat(searchSpec.hotlistScope, equalTo(SearchSpec.SCOPE_MEDIUM));
        collector.checkThat(searchSpec.getOrder(), equalTo(SearchSpec.ORDER_DATE));
        collector.checkThat(searchSpec.getStart(), equalTo(0));
        collector.checkThat(searchSpec.getRows(), equalTo(1));
        verify(chartDetails).setTrendingCompanyList(Mockito.anyList());
        verify(chartDetails).setTrendingTopicList(Mockito.anyList());
        verify(chartDetails).setMonitorTrendingEntityList(Mockito.anyList());
        verify(chartDetails).setAccmeterList(Mockito.anyList());
    }

    @Test
    public void givenVisualizationServiceWhenGetIndusrtyChartDetailsThenReturn() throws Exception {
        //Arrange
        stringList.clear();
        //Act
        ChartDetails actual = Whitebox.invokeMethod(visualizationService,
            GET_INDUSTRY_CHART_DETAILS, iEntityInfo, stringList, integerList);
        whenNew(ChartDetails.class).withNoArguments().thenThrow(SearchException.class);
        //Assert
        collector.checkThat(actual, equalTo(chartDetails));
    }

    @Test
    public void givenRelEntityMapWhenRemoveIndustryVisualizationThenRemove() throws Exception {
        //Arrange
        relEntityMap.put(ID2, trendingEntity);
        when(iEntityInfoCache.catIdToEntity(Mockito.anyString())).thenReturn(iEntityInfo);
        when(iEntityInfo.getType()).thenReturn(SearchTokenEntry.SEARCH_TOKEN_TAG_INDUSTRY,
            SearchTokenEntry.SEARCH_TOKEN_TAG_COMPANY);
        when(iEntityInfo.getSubsidiaryOf()).thenReturn(0, 1);
        HashSet<Integer> parentList = spy(new HashSet());
        HashSet<String> removeList = spy(new HashSet());
        parentList.add(1);
        //Act
        visualizationService.removeSubsidariesforIndustryVisualization(relEntityMap);
        visualizationService.removeSubsidariesforIndustryVisualization(relEntityMap);
        whenNew(HashSet.class).withNoArguments().thenReturn(parentList).thenReturn(removeList);
        visualizationService.removeSubsidariesforIndustryVisualization(relEntityMap);
        //Assert
        collector.checkThat(parentList.contains(1), equalTo(true));
        verify(parentList).add(1);
        verify(removeList).add(ID1);
        verify(relEntityMap).remove(ID1);
        verify(logger).debug(REMOVE_LIST_DEBUG_TXT + removeList.toString());
    }

    @Test
    public void givenRelEntityMapWhenCallPopulateEntityThenSetTrendingEntity() throws Exception {
        //Arrange
        prepareHotListEntryList();
        whenNew(TrendingEntity.class).withNoArguments().thenReturn(trendingEntity);
        when(iEntityInfo.getIndustryCatId()).thenReturn(1, 0);
        when(iEntityInfo.getSegmentCatId()).thenReturn(1, 0);
        when(iEntityInfo.getSectorCatId()).thenReturn(1);
        //Act
        Whitebox.invokeMethod(visualizationService,
            POPULATE_ENTITY_METHOD, relEntityMap, relEntityMap, iEntityInfo, hotListBucket);
        Whitebox.invokeMethod(visualizationService,
            POPULATE_ENTITY_METHOD, relEntityMap, relEntityMap, iEntityInfo, hotListBucket);
        Whitebox.invokeMethod(visualizationService,
            POPULATE_ENTITY_METHOD, relEntityMap, relEntityMap, iEntityInfo, hotListBucket);
        //Assert
        collector.checkThat(trendingEntity.getEntityId(), equalTo(ID2));
        collector.checkThat(trendingEntity.getEntityCount(), equalTo(0));
        collector.checkThat(trendingEntity.getDocCount(), equalTo(3));
    }

    @Test
    public void givenRelEntityMapWhenCallPopulateEntityAndIndustryCatNotContainIdThenSetEntity()
        throws Exception {
        //Arrange
        prepareHotListEntryList();
        whenNew(TrendingEntity.class).withNoArguments().thenReturn(trendingEntity);
        //Act
        Whitebox.invokeMethod(visualizationService,
            POPULATE_ENTITY_METHOD, relEntityMap, relEntityMap, iEntityInfo, hotListBucket);
        //Assert
        collector.checkThat(trendingEntity.getEntityId(), equalTo(ID2));
        collector.checkThat(trendingEntity.getEntityCount(), equalTo(0));
        collector.checkThat(trendingEntity.getDocCount(), equalTo(3));
    }

    @Test
    public void givenChartTypeAndChartIdWhenGetAccelerometerGraphWhenCallThenGetGraph()
        throws Exception {
        //Arrange
        prepareNodesList();
        //Act
        VisualizationData.Graph actual = Whitebox.invokeMethod(visualizationService,
            GET_ACCELEROMETER_GRAPH_METHOD, ChartType.GEO_WORLD, ID1, stringSet, false, true, true);
        //Assert
        collector.checkThat(actual.nodes, equalTo(nodes));
        collector.checkThat(actual, equalTo(graph));
    }

    @Test
    public void givenChartTypeAndChartIdWhenGetAccelerometerNodeWhenSingleEntityThenLog()
        throws Exception {
        //Arrange
        accObjList.add(accObj);
        doReturn(accObjList).when(accelerometerServiceRepository).getAccelerometer(anyString());
        when(iEntityInfoCache.catIdToEntity(accObj.getEntityId()))
            .thenReturn(null, iEntityInfo);
        //Act
        Whitebox.invokeMethod(visualizationService,
            GET_ACCELEROMETER_NODE_METHOD, ID1, true, ChartType.ACC_METER, true, true);
        String debugMsg1 =
            ChartType.ACC_METER + " ignore no Entity for entity: " + accObj.getEntityId();
        String debugMsg2 = ChartType.ACC_METER + " ignore content scores (" + accObj.
            getScore() + ") must be > " + 0 + ", for entity: " + accObj.getEntityId();
        //Assert
        verify(logger).debug(debugMsg1);
        verify(logger).debug(debugMsg2);
    }

    @Test
    public void givenChartTypeAndChartIdWhenGetAccelerometerNodeWhenNotSingleEntityThenLog()
        throws Exception {
        //Arrange
        accObjList.add(accObj);
        doReturn(accObjList).when(accelerometerServiceRepository).getAccelerometer(anyString());
        when(iEntityInfoCache.catIdToEntity(accObj.getEntityId()))
            .thenReturn(iEntityInfo);
        when(accObj.getThreeDayCount()).thenReturn(1, 5);
        //Act
        Whitebox.invokeMethod(visualizationService,
            GET_ACCELEROMETER_NODE_METHOD, ID1, false, ChartType.ACC_METER, true, true);
        String debugMsg1 =
            ChartType.ACC_METER + " ignore RecentWindow grouped tweet count (" + accObj
                .getThreeDayCount() + ")< 4 for entity: " + accObj.getEntityId();
        String debugMsg2 = ChartType.ACC_METER + " ignore content scores (" + accObj.getScore()
            + ") must be > " + 75 + ", for entity: " + accObj.getEntityId();
        //Assert
        verify(logger).debug(debugMsg1);
        verify(logger).debug(debugMsg2);
    }

    @Test
    public void givenChartTypeWhenGetAccelerometerNodeWhenScoreMoreThan75ThenReturnNodeList()
        throws Exception {
        //Arrange
        accObj.setScore(100);
        doReturn(accObjList).when(accelerometerServiceRepository).getAccelerometer(anyString());
        when(iEntityInfoCache.catIdToEntity(accObj.getEntityId()))
            .thenReturn(iEntityInfo);
        when(iEntityInfo.getType()).thenReturn(SearchTokenEntry.SEARCH_TOKEN_TAG_COMPANY);
        when(accObj.getThreeDayCount()).thenReturn(5);
        when(iEntityInfo.getName()).thenReturn(TEST_STRING);
        when(iEntityInfo.getSearchToken()).thenReturn(TEST_STRING);
        //Act
        Whitebox.invokeMethod(visualizationService,
            GET_ACCELEROMETER_NODE_METHOD, ID1, false, ChartType.ACC_METER, true, true);
        Whitebox.invokeMethod(visualizationService,
            GET_ACCELEROMETER_NODE_METHOD, ID1, false, ChartType.ACC_METER, false, true);
        List<Node> actual = Whitebox.invokeMethod(visualizationService,
            GET_ACCELEROMETER_NODE_METHOD, ID1, false, ChartType.ACC_METER, false, false);
        //Assert
        Node actualNode = actual.get(0);
        collector.checkThat(actual.size(), equalTo(1));
        collector.checkThat(actualNode.name, equalTo(TEST_STRING));
        collector.checkThat(actualNode.searchToken, equalTo(TEST_STRING));
    }

    @Test
    public void givenSummaryListWhenGetGeoMapGraphsEmptyChartTypesThenReturnEmptyGraphList()
        throws Exception {
        //Arrange
        chartTypes.clear();
        regionList.clear();
        //Act
        List<Graph> actual = Whitebox.invokeMethod(visualizationService,
            GET_GEO_MAP_GRAPHS_METHOD, regionList, chartTypes, ID1);
        //Assert
        collector.checkThat(actual.isEmpty(), equalTo(true));
    }

    @Test
    public void givenChartCountSummaryListWhenGetGeoMapGraphsThenReturnGraphList()
        throws Exception {
        //Arrange
        Collections.addAll(chartTypes, ChartType.GEO_WORLD, ChartType.GEO_US);
        chartCountSummary.setThirtyOneDaysDocCount(5);
        stringMap.put(ID2, TEST_STRING);
        stringMap.put(TEST_STRING, TEST_STRING);
        stringMap.put(ID1, "RF:UnitedStatesofAmerica");
        node.value = 1;
        when(iEntityInfoCache.searchTokenToEntity(Mockito.anyString()))
            .thenReturn(null, iEntityInfo);
        when(iEntityInfo.getSearchToken()).thenReturn(ID1, ID1, ID2);
        when(regionExcelUtilImpl.getRegionVsCountryCode()).thenReturn(stringMap);
        when(regionExcelUtilImpl.getRegionVsParentRegion()).thenReturn(stringMap);
        //Act
        List<Graph> actual = Whitebox.invokeMethod(visualizationService,
            GET_GEO_MAP_GRAPHS_METHOD, regionList, chartTypes, ID1);
        //Assert
        collector.checkThat(actual.isEmpty(), equalTo(false));
        collector.checkThat(actual.size(), equalTo(2));
    }

    @Test
    public void givenChartCountSummaryListLess4WhenGetTreeMapGraphForIndustryThenReturnGraph()
        throws Exception {
        //Act
        Graph actualWithoutItemList = Whitebox.invokeMethod(visualizationService,
            GET_TREE_MAP_GRAPH_FOR_INDUSTRY_MONITOR, regionList, 1, ChartType.GEO_WORLD, ID1);
        Graph actualWithItemList = Whitebox.invokeMethod(visualizationService,
            GET_TREE_MAP_GRAPH_FOR_MONITOR_METHOD, regionList, itemList, 1, ChartType.GEO_WORLD,
            ID1);
        Whitebox.invokeMethod(visualizationService,
            GET_TREE_MAP_GRAPH_METHOD, regionList, 1, ChartType.GEO_WORLD, ID1);
        //Assert
        collector.checkThat(actualWithoutItemList, equalTo(graph));
        collector.checkThat(actualWithItemList, equalTo(graph));
    }

    @Test
    public void givenChartCountSummaryListWhenGetTreeMapGraphForIndustryThenReturnGraph()
        throws Exception {
        //Arrange
        ChartCountSummary ccsObj = new ChartCountSummary();
        ccsObj.setOneDayDocCount(5);
        ccsObj.setSearchId(1);
        item.setId(1);
        item.setData(TEST_STRING);
        chartCountSummary.setSearchId(1);
        Collections.addAll(regionList, chartCountSummary, chartCountSummary, ccsObj);
        when(iEntityInfoCache.catIdToEntity(Mockito.anyString())).thenReturn(null, iEntityInfo);
        prepareNodesList();
        //Act
        Graph actualWithoutItemList = Whitebox.invokeMethod(visualizationService,
            GET_TREE_MAP_GRAPH_FOR_INDUSTRY_MONITOR, regionList, 1, ChartType.GEO_WORLD, ID1);
        Graph actualWithItemList = Whitebox.invokeMethod(visualizationService,
            GET_TREE_MAP_GRAPH_FOR_MONITOR_METHOD, regionList, itemList, 1, ChartType.GEO_WORLD,
            ID1);
        Whitebox.invokeMethod(visualizationService,
            GET_TREE_MAP_GRAPH_FOR_INDUSTRY_MONITOR, regionList, 1, ChartType.GEO_WORLD, ID1);
        Whitebox.invokeMethod(visualizationService,
            GET_TREE_MAP_GRAPH_FOR_MONITOR_METHOD, regionList, itemList, 1, ChartType.GEO_WORLD,
            ID1);
        //Assert
        collector.checkThat(actualWithoutItemList, equalTo(graph));
        collector.checkThat(actualWithItemList, equalTo(graph));
    }

    @Test
    public void givenChartCountSummaryListWhengetTreeMapGraphThenReturnGraph()
        throws Exception {
        //Arrange
        ChartCountSummary ccsObj = spy(new ChartCountSummary());
        ccsObj.setOneDayDocCount(5);
        ccsObj.setSearchId(1);
        Collections.addAll(regionList, chartCountSummary, chartCountSummary, ccsObj);
        when(iEntityInfoCache.catIdToEntity(Mockito.anyString())).thenReturn(null, iEntityInfo);
        Whitebox.setInternalState(visualizationService, TREE_MAP_MIN_NODE_FIELD, 5);
        //Act
        Graph actual = Whitebox.invokeMethod(visualizationService,
            GET_TREE_MAP_GRAPH_METHOD, regionList, 1, ChartType.GEO_WORLD, ID1);
        Whitebox.setInternalState(visualizationService, TREE_MAP_MIN_NODE_FIELD, 0);
        Whitebox.invokeMethod(visualizationService,
            GET_TREE_MAP_GRAPH_METHOD, regionList, 1, ChartType.GEO_WORLD, ID1);
        Whitebox.invokeMethod(visualizationService, ADD_BUCKET_METHOD, nodeBuckets, node, 1);
        //Assert
        collector.checkThat(actual, equalTo(graph));
        verify(ccsObj, times(2)).getOneDayDocCount();
        verify(logger).debug("Treemap entity count < " + 5);
    }

    @Test
    public void givenSearchResultWhenGetTrendingRegionThenReturnChartCountSummaryList()
        throws Exception {
        //Arrange
        prepareHotListEntryList();
        whenNew(ArrayList.class).withNoArguments().thenReturn((ArrayList) regionList);
        //Act
        List<ChartCountSummary> actualWhenEmptyHotBucketList = Whitebox
            .invokeMethod(visualizationService, GET_TRENDING_REGION_METHOD, searchResult);
        searchResult.facetRegions = hotListBucket;
        List<ChartCountSummary> actualWhenNotEmptyHotBucketList = Whitebox
            .invokeMethod(visualizationService, GET_TRENDING_REGION_METHOD, searchResult);
        //Assert
        collector.checkThat(actualWhenEmptyHotBucketList, equalTo(null));
        collector.checkThat(actualWhenNotEmptyHotBucketList, equalTo(regionList));
    }

    @Test
    public void givenEntityMapWhenGetTrendingListThenReturnChartCountSummaryList()
        throws Exception {
        //Arrange
        prepareRelEntityMap();
        when(iEntityInfoCache.catIdToEntity(Mockito.anyString())).thenReturn(iEntityInfo);
        when(iEntityInfo.getId()).thenReturn(ID1);
        docSummaryMap.put(ID1, chartCountSummary);
        //Act
        List<ChartCountSummary> actual = Whitebox.invokeMethod(visualizationService,
            GET_TRENDING_LIST_METHOD, relEntityMap, 1);
        //Assert
        collector.checkThat(actual.contains(chartCountSummary), equalTo(true));
        collector.checkThat(actual.size(), equalTo(1));
        verify(entityBaseServiceRepository).getEntityInfoCache();
    }

    @Test
    public void givenEntityMapWhenRemoveSubIndustriesThenCheckIfParentPresent() {
        //Arrange
        when(iEntityInfoCache.catIdToEntity(Mockito.anyString())).thenReturn(iEntityInfo);
        when(iEntityInfoCache.companyIdToEntity(1)).thenReturn(iEntityInfo);
        when(iEntityInfo.getId()).thenReturn(ID1);
        when(iEntityInfo.getSubsidiaryOf()).thenReturn(1);
        //Act
        visualizationService.removeSubsidaries(relEntityMap, stringSet);
        //Assert
        verify(entityBaseServiceRepository).getEntityInfoCache();
        verify(visualizationService)
            .checkIfParentPresent(eq(iEntityInfo), Mockito.anySetOf(String.class), eq(relEntityMap),
                eq(stringSet), eq(ID1), Mockito.anySetOf(String.class), eq(iEntityInfoCache));
    }

    @Test
    public void givenEntityMapAndEntityWhenCheckIfParentPresentWhenCallThenLog() {
        //Arrange
        when(iEntityInfoCache.catIdToEntity(Mockito.anyString())).thenReturn(iEntityInfo);
        when(iEntityInfo.getId()).thenReturn(ID1);
        when(iEntityInfo.getSubsidiaryOf()).thenReturn(1);
        Set<String> entityCatIds = new HashSet<String>();
        Set<String> allHirearchyOfEntity = new HashSet<String>();
        //Act
        visualizationService.checkIfParentPresent(null, stringSet, relEntityMap, stringSet,
            ID1, stringSet, iEntityInfoCache);
        visualizationService.checkIfParentPresent(iEntityInfo, stringSet, relEntityMap, stringSet,
            ID1, stringSet, iEntityInfoCache);
        when(iEntityInfoCache.companyIdToEntity(1)).thenReturn(iEntityInfo);
        visualizationService.checkIfParentPresent(iEntityInfo, stringSet, relEntityMap, stringSet,
            ID1, stringSet, iEntityInfoCache);
        when(iEntityInfo.getId()).thenReturn(ID2);
        visualizationService.checkIfParentPresent(iEntityInfo, stringSet, relEntityMap,
            entityCatIds, ID1, stringSet, iEntityInfoCache);
        when(iEntityInfo.getId()).thenReturn(ID1, ID2);
        visualizationService.checkIfParentPresent(iEntityInfo, allHirearchyOfEntity, relEntityMap,
            entityCatIds, ID1, stringSet, iEntityInfoCache);
        visualizationService
            .removeSelfAndParentSubsidaryEntity(relEntityMap, relEntityMap, stringSet);
        //Assert
        String debugMsg = "entity id:::" + ID1 + " , Parent id:::" + ID1;
        collector.checkThat(iEntityInfoCache.companyIdToEntity(1), equalTo(iEntityInfo));
        verify(logger).warn(WARN_MSG_TXT);
        verify(logger).debug(debugMsg);
    }

    @Test
    public void givenEntityMapAndEntityWhenRemoveSelfAndParentEntity() {
        //Arrange
        when(iEntityInfoCache.catIdToEntity(Mockito.anyString())).thenReturn(null, iEntityInfo);
        when(iEntityInfo.getId()).thenReturn(ID1);
        when(iEntityInfo.getSubsidiaryOf()).thenReturn(1);
        stringSet.add(ID1);
        //Act
        visualizationService
            .removeSelfAndParentSubsidaryEntity(relEntityMap, relEntityMap, stringSet);
        visualizationService
            .removeSelfAndParentSubsidaryEntity(relEntityMap, relEntityMap, stringSet);
        when(iEntityInfoCache.companyIdToEntity(1)).thenReturn(iEntityInfo);
        visualizationService
            .removeSelfAndParentSubsidaryEntity(relEntityMap, relEntityMap, stringSet);
        //Assert
        collector.checkThat(relEntityMap.containsKey(ID1), equalTo(false));
        verify(logger).warn(WARN_MSG_TXT);
    }

    @Test
    public void givenEntityMapAndEntityWhenGetRelatedEntityThenLog() throws Exception {
        //Arrange
        SuggestedSearchGroup suggestedSearchGroupLocal = spy(new SuggestedSearchGroup(searchPattern,
            TEST_STRING, GroupType.INDUSTRY_TRENDS));
        suggestedSearchGroups.add(suggestedSearchGroupLocal);
        when(suggestedSearchGroup.getQueries()).thenReturn(queryEntries);
        //Act
        Whitebox.invokeMethod(visualizationService, GET_RELATED_ENTITY_METHOD
            , relEntityMap, relEntityMap, suggestedSearchGroups);
        when(suggestedSearchGroup.getGroupType())
            .thenReturn(SuggestedSearchGroup.GroupType.INDUSTRY_TRENDS);
        Whitebox.invokeMethod(visualizationService, GET_RELATED_ENTITY_METHOD
            , relEntityMap, relEntityMap, suggestedSearchGroups);
        when(suggestedSearchGroup.getGroupType())
            .thenReturn(SuggestedSearchGroup.GroupType.INDUSTRY_TOPICS);
        Whitebox.invokeMethod(visualizationService, GET_RELATED_ENTITY_METHOD
            , relEntityMap, relEntityMap, suggestedSearchGroups);
        //Assert
        collector.checkThat(suggestedSearchGroupLocal.getGroupType(),
            equalTo(GroupType.INDUSTRY_TRENDS));
        verify(logger, times(2)).debug(
            "Entity already found in comention map so increasing entity count:: "
                + searchTokenEntry.id);
        verify(logger).debug("Added related entity to trending entity list:: "
            + searchTokenEntry.id);
    }

    @Test
    public void givenEntityMapAndEntityWhenCallGetResultForGeoThenSetSearchSpec() throws Exception {
        //Arrange
        prepareHotListEntryList();
        when(logger.isDebugEnabled()).thenReturn(true);
        when(entityBaseServiceRepository.getSearcher()).thenReturn(solrSearcher);
        doReturn(searchResult).when(solrSearcher).search(searchSpec);
        //Act
        Whitebox.invokeMethod(visualizationService, GET_ENTITY_METHOD
            , relEntityMap, hotListBucket, 1);
        SearchResult actualResult = Whitebox
            .invokeMethod(visualizationService, GET_SEARCH_RESULTFOR_GEO_METHOD
                , stringList, integerList);
        //Assert
        collector.checkThat(actualResult, equalTo(searchResult));
        collector.checkThat(searchSpec.optimalScopeAndDays, equalTo(false));
        collector.checkThat(searchSpec.useLikelySearchIntention, equalTo(false));
        collector.checkThat(searchSpec.needHighlighting, equalTo(false));
        collector.checkThat(searchSpec.needHotListRegion, equalTo(true));
        collector.checkThat(searchSpec.hotListRows, equalTo(150));
        collector.checkThat(searchSpec.hotlistScope, equalTo(SearchSpec.SCOPE_MEDIUM));
    }

    @Test
    public void givenEntityMapAndEntityWhenGetVisualizationByEntityTokenThenReturnData()
        throws Exception {
        //Arrange
        Future<BaseSet> futureTask = PowerMockito.mock(FutureTask.class);
        GraphSet graphSet = mock(GraphSet.class);
        doReturn(itemList).when(monitorServiceRepository).getItemsByTagId(TAG_ID);
        doReturn(tags).when(monitorServiceRepository).getTagById(TAG_ID);
        when(iEntityInfo.getType()).thenReturn(SearchTokenEntry.SEARCH_TOKEN_TAG_SECTOR_TOPIC);
        when(completionService.getSubmissions()).thenReturn(4);
        when(completionService.poll(Mockito.anyLong(), eq(TimeUnit.MILLISECONDS)))
            .thenReturn(null, futureTask);
        when(futureTask.get()).thenReturn(graphSet);
        //Act
        VisualizationData actualWhenSearchIsEmpty = visualizationService
            .getVisualizationByEntityToken(ID1, nodeCount, chartTypes, TEST_STRING, true, true);
        when(iEntityInfoCache.searchTokenToEntity(ID1)).thenReturn(iEntityInfo);
        VisualizationData actualWhenSearchNotEmpty = visualizationService
            .getVisualizationByEntityToken(ID1, nodeCount, chartTypes, TEST_STRING, true, true);
        when(iEntityInfo.getType()).thenReturn(SearchTokenEntry.SEARCH_TOKEN_TAG_INDUSTRY);
        VisualizationData actualWhenFilterEmpty = visualizationService
            .getVisualizationByEntityToken(ID1, nodeCount, chartTypes, null, true, false);
        Callable<BaseSet> getTreeChartDataCallback = Whitebox
            .invokeMethod(visualizationService, GET_TREE_CHART_DATA_METHOD, cd, 1, ID1);
        getTreeChartDataCallback.call();
        //Assert
        collector.checkThat(actualWhenSearchIsEmpty, equalTo(null));
        collector.checkThat(actualWhenSearchNotEmpty, equalTo(graphData));
        collector.checkThat(actualWhenFilterEmpty, equalTo(graphData));
    }

    @Test
    public void givenVisualizationServiceWhenCallGetAsyncMethodsThenReturnCallBacks()
        throws Exception {
        //Arrange
        prepareNodesList();
        stringSet.add(TEST_STRING);
        doReturn(searchResult).when(solrSearcher).search(searchSpec);
        Callable<BaseSet> accGraphCallback = Whitebox.invokeMethod(visualizationService,
            GET_ACCELEROMETER_GRAPH_INDUSTRY_METHOD, regionList, ID1, true, true);
        Callable<BaseSet> chartDataForAccCallBack = Whitebox.invokeMethod(visualizationService,
            GET_CHART_DATA_FOR_ACC_METHOD, stringSet, ID1, true);
        Callable<BaseSet> chartDataCallback = Whitebox.invokeMethod(visualizationService,
            GET_CHART_DATA_FOR_ASYNC_METHOD, new ChartSpec(), stringList, integerList, stringSet,
            ID1);
        Callable<BaseSet> chartDataForGEO = Whitebox.invokeMethod(visualizationService,
            GET_CHART_DATA_FOR_GEO_METHOD, stringList, integerList, ID1);
        accGraphCallback.call();
        chartDataForAccCallBack.call();
        chartDataForGEO.call();
        //Act
        Whitebox.invokeMethod(visualizationService, GET_CHART_DATA_FOR_GEO_METHOD,
            stringList, integerList, ID1);
        chartDataCallback.call();
        //Assert
        collector.checkThat(graph.nodes, equalTo(nodes));
        collector.checkThat(nodes.isEmpty(), equalTo(false));
        collector.checkThat(nodes.contains(ChartType.GEO_US), equalTo(true));
        collector.checkThat(nodes.contains(ChartType.GEO_WORLD), equalTo(true));
    }

    @Test
    public void givenItemListAndScopeListWhenGetChartDataForGEOThenSetChartSummery()
        throws Exception {
        //Arrange
        List<Integer> countsList = spy(new ArrayList<Integer>());
        Collections.addAll(countsList, 1, 2, 3, 4);
        whenNew(ChartCountSummary.class).withNoArguments().thenReturn(chartCountSummary);
        doReturn(searchResult).when(solrSearcher).search(searchSpec);
        when(docListBucket.getPartitionCounts()).thenReturn(countsList);
        Callable<BaseSet> chartDataForGEOAndIT = Whitebox.invokeMethod(visualizationService,
            GET_MONITOR_GEO_AND_TT_METHOD, itemList, stringList, integerList, 1, ID1);
        //Act
        chartDataForGEOAndIT.call();
        buckets.add(docListBucket);
        buckets.add(docListBucket);
        searchResult.buckets = buckets;
        when(solrSearcher.search(searchSpec)).thenReturn(searchResult);
        chartDataForGEOAndIT.call();
        //Assert
        verify(chartCountSummary).setThirtyOneDaysDocCount(countsList.get(1) + countsList.get(2));
        verify(chartCountSummary).setOneDayDocCount(countsList.get(2));
    }

    private void prepareRelEntityMap() {
        relEntityMap.clear();
        TrendingEntity trendingEntity1 = new TrendingEntity();
        TrendingEntity trendingEntity2 = new TrendingEntity();
        TrendingEntity trendingEntity3 = new TrendingEntity();
        TrendingEntity trendingEntity4 = new TrendingEntity();
        trendingEntity1.setEntityCount(1);
        trendingEntity1.setDocCount(1);
        trendingEntity2.setDocCount(3);
        trendingEntity2.setEntityCount(3);
        relEntityMap.put("1", trendingEntity1);
        relEntityMap.put("2", trendingEntity2);
        relEntityMap.put("3", trendingEntity3);
        relEntityMap.put("4", trendingEntity4);
        relEntityMap.put("5", trendingEntity1);
        relEntityMap.put("6", trendingEntity2);
    }

    private void prepareNodesList() throws Exception {
        Collections.addAll(nodes, node, node, node, node, node, node);
        whenNew(ArrayList.class).withNoArguments().thenReturn((ArrayList) nodes);
    }

    private void prepareHotListEntryList() {
        final EntityEntry entityEntry1 = spy(new EntityEntry());
        final HotListEntry hotListEntry1 = spy(new HotListEntry(entityEntry1, 1));
        final EntityEntry entityEntry2 = spy(new EntityEntry());
        final HotListEntry hotListEntry2 = spy(new HotListEntry(entityEntry2, 1));
        final EntityEntry entityEntry3 = spy(new EntityEntry());
        final HotListEntry hotListEntry3 = spy(new HotListEntry(entityEntry3, 1));
        final EntityEntry entityEntry4 = spy(new EntityEntry());
        final HotListEntry hotListEntry4 = spy(new HotListEntry(entityEntry4, 1));
        final int[] countsArray = {1, 2, 3};
        when(iEntityInfoCache.catIdToEntity(ID2)).thenReturn(iEntityInfo);
        entityEntry2.valid = true;
        entityEntry3.valid = true;
        when(hotListEntry3.getDocCount()).thenReturn(3);
        entityEntry4.valid = true;
        when(hotListEntry4.getDocCount()).thenReturn(3);
        when(entityEntry4.getId()).thenReturn(ID2);
        when(hotListEntry4.getPartitionCounts()).thenReturn(countsArray);
        hotListBucket.add(hotListEntry1);
        hotListBucket.add(hotListEntry2);
        hotListBucket.add(hotListEntry3);
        hotListBucket.add(hotListEntry4);
    }
}
