package com.firstrain.frapi.service.impl;

import com.firstrain.db.obj.Accelerometer;
import com.firstrain.frapi.domain.AutoSuggest;
import com.firstrain.frapi.domain.AutoSuggestInfo;
import com.firstrain.frapi.domain.BaseSpec;
import com.firstrain.frapi.domain.BlendDunsInput;
import com.firstrain.frapi.domain.CompanyTradingRange;
import com.firstrain.frapi.domain.Document;
import com.firstrain.frapi.domain.EntityDetailSpec;
import com.firstrain.frapi.domain.MgmtTurnoverMeta;
import com.firstrain.frapi.domain.TwitterSpec;
import com.firstrain.frapi.domain.WebVolumeEventMeta;
import com.firstrain.frapi.obj.EventQueryCriteria;
import com.firstrain.frapi.pojo.Entity;
import com.firstrain.frapi.pojo.Event;
import com.firstrain.frapi.pojo.GraphNode;
import com.firstrain.frapi.pojo.wrapper.DocumentSet;
import com.firstrain.frapi.pojo.wrapper.EventSet;
import com.firstrain.frapi.pojo.wrapper.GraphNodeSet;
import com.firstrain.frapi.pojo.wrapper.TweetSet;
import com.firstrain.frapi.service.TwitterService;
import com.firstrain.frapi.util.ContentType;
import com.firstrain.frapi.util.ConvertUtil;
import com.firstrain.frapi.util.DefaultEnums;
import com.firstrain.frapi.util.FRAPIConstant;
import com.firstrain.solr.client.DocCatEntry;
import com.firstrain.solr.client.DocListBucket;
import com.firstrain.solr.client.SearchException;
import com.firstrain.solr.client.SearchSpec;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Spy;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.firstrain.frapi.events.IEvents.EventTypeEnum.TYPE_TOTAL_WEB_COVERAGE_VOLUME_TOPIC;
import static com.firstrain.frapi.util.ContentType.WEBNEWS;
import static com.firstrain.frapi.util.DefaultEnums.EventInformationEnum.MT_HIRE;
import static com.firstrain.mip.object.FR_IEvent.TYPE_DELAYED_SEC_FILING;
import static com.firstrain.mip.object.FR_IEvent.TYPE_MGMT_CHANGE_CEO_TURNOVER;
import static com.firstrain.solr.client.SearchSpec.SCOPE_BROAD;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anySet;
import static org.mockito.Matchers.anyShort;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.reflect.Whitebox.invokeMethod;

@RunWith(PowerMockRunner.class)
public class EntityBaseServiceImplPartTwoTest extends EntityBaseServiceImplTestSetup {

    @Spy
    private final TwitterService twitterService = new TwitterServiceImpl();
    @Spy
    private final ConvertUtil convertUtil = new ConvertUtil();

    @Test
    public void givenSolrDocumentFoundWhenGetCompanyTradingInfoFromIndexThenGetSameTradingRange()
            throws SolrServerException {
        // Act
        final List<CompanyTradingRange> actual = serviceImpl.getCompanyTradingInfoFromIndex(INT_ID);
        // Assert
        errorCollector.checkThat(actual.size(), equalTo(1));
        final CompanyTradingRange companyTradingRange = actual.get(0);
        errorCollector.checkThat(companyTradingRange.getOpeningPrice(), equalTo(1.1D));
        errorCollector.checkThat(companyTradingRange.getClosingPrice(), equalTo(1.1D));
        errorCollector.checkThat(companyTradingRange.getDiffId(), equalTo(INT_ID));
    }

    @Test
    public void givenGotDocsFromSolrWhenGetEventsTimelineThenReturnEventSet() throws Exception {
        // Arrange
        serviceImpl = spy(serviceImpl);
        final BaseSpec baseSpec = prepareBaseSpec();
        baseSpec.setStart(SHORT_ZERO);
        baseSpec.setNeedRelatedDoc(true);
        baseSpec.setDaysCount(10);
        doNothing().when(serviceImpl).attachRelatedDocumentDetails(anyList(), anyMap(), eq(baseSpec));
        // Act
        final EventSet eventSet = serviceImpl.getEventsTimeline(companyIdsArr, tCatIds, baseSpec);
        // Assert
        final List<Event> pojoEventList = eventSet.getEvents();
        errorCollector.checkThat(pojoEventList.size(), equalTo(INTEGER_VALUE_ONE));
        final Event pojoEvent = pojoEventList.get(0);
        errorCollector.checkThat(pojoEvent.getEventInformationEnum(), equalTo(MT_HIRE));
        errorCollector.checkThat(pojoEvent.getEventType(), equalTo(TYPE_TOTAL_WEB_COVERAGE_VOLUME_TOPIC.name()));
        errorCollector.checkThat(eventSet.getTotalCount(), equalTo(2));
        errorCollector.checkThat(eventSet.getScope(), equalTo(SCOPE_BROAD));
        final ArgumentCaptor<EventQueryCriteria> queryCriteriaCaptor =
                ArgumentCaptor.forClass(EventQueryCriteria.class);
        verify(eventService).getEventsDocsFromSolr(queryCriteriaCaptor.capture());
        final EventQueryCriteria queryCriteria = queryCriteriaCaptor.getValue();
        errorCollector.checkThat(queryCriteria.getCompanyIds(), equalTo(companyIdsArr));
        errorCollector.checkThat(queryCriteria.getTopicIds(), equalTo(tCatIds));
        final EventQueryCriteria.EventTypeRange queryEventTypeRange = queryCriteria.getEventTypeRange();
        errorCollector.checkThat(queryEventTypeRange.getStartEventType(), equalTo(TYPE_MGMT_CHANGE_CEO_TURNOVER));
        errorCollector.checkThat(queryEventTypeRange.getEndEventType(), equalTo(TYPE_DELAYED_SEC_FILING));
        errorCollector.checkThat(queryCriteria.getDays(), equalTo(10));
        errorCollector.checkThat(queryCriteria.getNoOfEvents(), equalTo(FRAPIConstant.MAX_NO_OF_EVENTS));
        errorCollector.checkThat(queryCriteria.getStart(), equalTo(0));
        final ArgumentCaptor<Map<Integer, SolrDocument>> eventDocMapCaptor = createEventDocMapCaptor();
        verify(serviceImpl).attachRelatedDocumentDetails(eq(pojoEventList), eventDocMapCaptor.capture(),
                eq(baseSpec));
        errorCollector.checkThat(eventDocMapCaptor.getValue().isEmpty(), equalTo(true));
    }

    @Test
    public void givenTotalCountLessThanSpecCountWhenGetEventsTimelineThenReturnEventSet() throws Exception {
        // Arrange
        serviceImpl = spy(serviceImpl);
        final BaseSpec baseSpec = prepareBaseSpec();
        baseSpec.setStart(SHORT_ZERO);
        baseSpec.setNeedRelatedDoc(true);
        baseSpec.setDaysCount(10);
        baseSpec.setCount((short) 2);
        baseSpec.setScope(null);
        doNothing().when(serviceImpl).attachRelatedDocumentDetails(anyList(), anyMap(), eq(baseSpec));
        // Act
        final EventSet eventSet = serviceImpl.getEventsTimeline(companyIdsArr, tCatIds, baseSpec);
        // Assert
        final List<Event> pojoEventList = eventSet.getEvents();
        errorCollector.checkThat(pojoEventList.size(), equalTo(INTEGER_VALUE_ONE));
        final Event pojoEvent = pojoEventList.get(0);
        errorCollector.checkThat(pojoEvent.getEventInformationEnum(), equalTo(MT_HIRE));
        errorCollector.checkThat(pojoEvent.getEventType(), equalTo(TYPE_TOTAL_WEB_COVERAGE_VOLUME_TOPIC.name()));
        errorCollector.checkThat(eventSet.getTotalCount(), equalTo(2));
        errorCollector.checkThat(eventSet.getScope(), equalTo(SCOPE_BROAD));
        final ArgumentCaptor<EventQueryCriteria> queryCriteriaCaptor =
                ArgumentCaptor.forClass(EventQueryCriteria.class);
        verify(eventService).getEventsDocsFromSolr(queryCriteriaCaptor.capture());
        final EventQueryCriteria queryCriteria = queryCriteriaCaptor.getValue();
        errorCollector.checkThat(queryCriteria.getCompanyIds(), equalTo(companyIdsArr));
        errorCollector.checkThat(queryCriteria.getTopicIds(), equalTo(tCatIds));
        final EventQueryCriteria.EventTypeRange queryEventTypeRange = queryCriteria.getEventTypeRange();
        errorCollector.checkThat(queryEventTypeRange.getStartEventType(), equalTo(TYPE_MGMT_CHANGE_CEO_TURNOVER));
        errorCollector.checkThat(queryEventTypeRange.getEndEventType(), equalTo(TYPE_DELAYED_SEC_FILING));
        errorCollector.checkThat(queryCriteria.getDays(), equalTo(10));
        errorCollector.checkThat(queryCriteria.getNoOfEvents(), equalTo(FRAPIConstant.MAX_NO_OF_EVENTS));
        errorCollector.checkThat(queryCriteria.getStart(), equalTo(0));
        final ArgumentCaptor<Map<Integer, SolrDocument>> eventDocMapCaptor = createEventDocMapCaptor();
        verify(serviceImpl).attachRelatedDocumentDetails(eq(pojoEventList), eventDocMapCaptor.capture(),
                eq(baseSpec));
        errorCollector.checkThat(eventDocMapCaptor.getValue().isEmpty(), equalTo(true));
    }

    @Test
    public void givenExceptionWhenGetEventsTimelineThenThrowException() throws Exception {
        // Arrange
        final BaseSpec baseSpec = prepareBaseSpec();
        baseSpec.setStart(SHORT_ZERO);
        when(eventService.getEventsDocsFromSolr(any(EventQueryCriteria.class)))
                .thenThrow(new IllegalArgumentException());
        // Assert
        expectedException.expect(IllegalArgumentException.class);
        // Act
        serviceImpl.getEventsTimeline(companyIdsArr, tCatIds, baseSpec);
    }

    @Test
    public void givenGetAccelerometerNodeWhenSingleEntityTrueThenGetNodeSet() throws Exception {
        // Arrange
        serviceImpl = spy(serviceImpl);
        final Accelerometer accelerometer = new Accelerometer();
        accelerometer.setImageName(TEST_IMAGE_NAME);
        accelerometer.setScore(100);
        accelerometer.setArrow((short) 50);
        when(entityInfo.getName()).thenReturn(TEST_ENTITY_LABEL);
        final List<Accelerometer> accelerometers = singletonList(accelerometer);
        when(accelerometerServiceRepository.getAccelerometer(CAT_ID_STR)).thenReturn(accelerometers);
        final InOrder inOrder = inOrder(infoCache, convertUtil);
        // Act
        final GraphNodeSet nodeSet = serviceImpl.getAccelerometerNode(CAT_ID_STR, true, true);
        // Assert
        final List<GraphNode> graphNodeList = nodeSet.getGraphNodeList();
        errorCollector.checkThat(graphNodeList.size(), equalTo(1));
        final GraphNode graphNode = graphNodeList.get(0);
        errorCollector.checkThat(graphNode.getId(), equalTo("301024"));
        errorCollector.checkThat(graphNode.getLabel(), equalTo(TEST_ENTITY_LABEL));
        errorCollector.checkThat(graphNode.getValue(), equalTo(100F));
        errorCollector.checkThat(graphNode.getIntensity(), equalTo(50));
        errorCollector.checkThat(graphNode.getImageName(), equalTo(TEST_IMAGE_NAME));
        errorCollector.checkThat(graphNode.getEntityInfo().getId(), equalTo("301024"));
        errorCollector.checkThat(graphNode.getSmartText0(), nullValue());
        errorCollector.checkThat(graphNode.getSmartText(),
                equalTo("About average<br />and <span class='bold'><span class='colorGreen'>rising</span></span>"));
        errorCollector.checkThat(graphNode.getSearchToken(), equalTo(TEXT_STR));
        errorCollector.checkThat(graphNode.getName(), equalTo(TEST_ENTITY_LABEL));
        inOrder.verify(infoCache).catIdToEntity("301024");
        inOrder.verify(convertUtil).convertEntityInfo(entityInfo);
        verifyPrivate(serviceImpl).invoke("trimAndSortGraphDataList", graphNodeList, true);
    }

    @Test
    public void givenGetAccelerometerNodeWhenSingleEntityFalseThenGetNodeSet() throws Exception {
        // Arrange
        final Accelerometer accelerometer = new Accelerometer();
        accelerometer.setThreeDayCount(INTEGER_VALUE_FIVE);
        accelerometer.setScore(INTEGER_VALUE_SEVENTY_SIX);
        final List<Accelerometer> accelerometers = singletonList(accelerometer);
        when(accelerometerServiceRepository.getAccelerometer(CAT_ID_STR)).thenReturn(accelerometers);
        final InOrder inOrder = inOrder(infoCache, convertUtil);
        // Act
        final GraphNodeSet nodeSet = serviceImpl.getAccelerometerNode(CAT_ID_STR, false, true);
        // Assert
        final List<GraphNode> graphNodeList = nodeSet.getGraphNodeList();
        assertTrue(graphNodeList.isEmpty());
        inOrder.verify(infoCache).catIdToEntity("301024");
        inOrder.verify(convertUtil).convertEntityInfo(entityInfo);
        verifyPrivate(serviceImpl).invoke("trimAndSortGraphDataList", graphNodeList, false);
    }

    @Test
    public void givenTrimCompanyEndingWordWhenNameContainsCompanyThenReturnTrimedName() {
        // Arrange
        final Set<String> regex = singleton(COMPANY_STR);
        when(excelProcesingHelperRepository.getCompanyEndingWordsRegex()).thenReturn(regex);
        // Act
        final String actual = serviceImpl.trimCompanyEndingWord(BUCKET_NAME + COMPANY_STR);
        // Assert
        assertEquals(BUCKET_NAME, actual);
    }

    @Test
    public void givenTrimCompanyEndingWordNameNotContainsCompanyThenReturnName() {
        // Arrange
        final Set<String> regex = singleton(COMPANY_STR);
        when(excelProcesingHelperRepository.getCompanyEndingWordsRegex()).thenReturn(regex);
        // Act
        final String actual = serviceImpl.trimCompanyEndingWord(BUCKET_NAME);
        // Assert
        assertEquals(BUCKET_NAME, actual);
    }

    @Test
    public void givenTrimCompanyEndingWordWhenNameNullThenReturnNull() {
        // Arrange
        final Set<String> regex = singleton(COMPANY_STR);
        when(excelProcesingHelperRepository.getCompanyEndingWordsRegex()).thenReturn(regex);
        // Act
        final String actual = serviceImpl.trimCompanyEndingWord(null);
        // Assert
        assertNull(actual);
    }

    @Test
    public void givenNoEntityFoundWhenAutoSuggestForEntityThenReturnEmpty() throws Exception {
        // Act
        final List<Entity> actualList =
                serviceImpl.autoSuggestForEntity(TEXT_STR,
                        DefaultEnums.INPUT_ENTITY_TYPE.COMPANY.name(), 0, TEXT_STR, null);
        // Assert
        assertTrue(actualList.isEmpty());
    }

    @Test
    public void givenAutoSuggestForEntityWhenEntityTypeCompanyThenGetEntities() throws Exception {
        // Arrange
        final Map<Integer, Integer> industryClassfMap = new HashMap<>();
        AutoSuggest autoSuggest = new AutoSuggest();
        final List<AutoSuggestInfo> autoSuggestInfoList = new ArrayList<>();
        final AutoSuggestInfo autoSuggestInfo = new AutoSuggestInfo();
        autoSuggestInfo.setSynonym(TEXT_STR);
        autoSuggestInfo.setCompanyId(INT_ID);
        autoSuggestInfoList.add(autoSuggestInfo);
        autoSuggest.setAutoSuggest(autoSuggestInfoList);
        when(autoSuggestService.getAutoCompleteEntries(TEXT_STR,
                DefaultEnums.INPUT_ENTITY_TYPE.COMPANY.name(), false, 0, TEXT_STR))
                .thenReturn(autoSuggest);
        // Act
        final List<Entity> actualList = serviceImpl.autoSuggestForEntity(TEXT_STR,
                DefaultEnums.INPUT_ENTITY_TYPE.COMPANY.name(), 0,
                TEXT_STR, industryClassfMap);
        // Assert
        errorCollector.checkThat(actualList.size(), equalTo(1));
        errorCollector.checkThat(actualList.get(0).getSynonym(), equalTo(TEXT_STR));
        final ArgumentCaptor<Integer> frCompanyIdCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(infoCache).companyIdToEntity(frCompanyIdCaptor.capture());
        errorCollector.checkThat(frCompanyIdCaptor.getValue(), equalTo(INT_ID));
    }

    @Test
    public void givenAutoSuggestForEntityWhenEntityTypeTopicThenGetEntities() throws Exception {
        // Arrange
        final Map<Integer, Integer> industryClassfMap = new HashMap<>();
        final AutoSuggest autoSuggest = new AutoSuggest();
        final AutoSuggestInfo autoSuggestInfo = new AutoSuggestInfo();
        autoSuggestInfo.setSynonym(TEXT_STR);
        final List<AutoSuggestInfo> autoSuggestInfoList = singletonList(autoSuggestInfo);
        autoSuggest.setAutoSuggest(autoSuggestInfoList);
        when(autoSuggestService.getAutoCompleteEntries(TEXT_STR,
                DefaultEnums.INPUT_ENTITY_TYPE.TOPIC.name(), false, 0, TEXT_STR))
                .thenReturn(autoSuggest);
        when(entityInfo.getName()).thenReturn(TEST_ENTITY_LABEL);
        // Act
        final List<Entity> actualList = serviceImpl.autoSuggestForEntity(TEXT_STR,
                DefaultEnums.INPUT_ENTITY_TYPE.TOPIC.name(), 0,
                TEXT_STR, industryClassfMap);
        // Assert
        errorCollector.checkThat(actualList.size(), equalTo(1));
        final Entity actualEntity = actualList.get(0);
        final Entity sector = actualEntity.getSector();
        errorCollector.checkThat(sector.getSearchToken(), equalTo(TEXT_STR));
        errorCollector.checkThat(sector.getName(), equalTo(TEST_ENTITY_LABEL));
        final Entity segment = actualEntity.getSegment();
        errorCollector.checkThat(segment.getSearchToken(), equalTo(TEXT_STR));
        errorCollector.checkThat(segment.getName(), equalTo(TEST_ENTITY_LABEL));
        final Entity industry = actualEntity.getIndustry();
        errorCollector.checkThat(industry.getSearchToken(), equalTo(TEXT_STR));
        errorCollector.checkThat(industry.getName(), equalTo(TEST_ENTITY_LABEL));
    }

    @Test
    public void givenAutoSuggestForEntityWhenEntityTypeIndustryThenGetEntities() throws Exception {
        // Arrange
        final Map<Integer, Integer> industryClassfMap = emptyMap();
        final AutoSuggest autoSuggest = new AutoSuggest();
        final AutoSuggestInfo autoSuggestInfo = new AutoSuggestInfo();
        autoSuggestInfo.setSynonym(TEXT_STR);
        autoSuggestInfo.setCatId(CAT_ID_STR);
        final List<AutoSuggestInfo> autoSuggestInfoList = singletonList(autoSuggestInfo);
        autoSuggest.setAutoSuggest(autoSuggestInfoList);
        when(autoSuggestService.getAutoCompleteEntries(TEXT_STR,
                DefaultEnums.INPUT_ENTITY_TYPE.INDUSTRY.name(), false, 0, TEXT_STR))
                .thenReturn(autoSuggest);
        // Act
        final List<Entity> actualList = serviceImpl.autoSuggestForEntity(TEXT_STR,
                DefaultEnums.INPUT_ENTITY_TYPE.INDUSTRY.name(), 0,
                TEXT_STR, industryClassfMap);
        // Assert
        assertEquals(1, actualList.size());
        assertEquals("301024", actualList.get(0).getId());
        verify(infoCache).catIdToEntity(CAT_ID_STR);
    }

    @Test
    public void givenAttachRelatedDocumentDetailsWhenNullDocumentFromSearcherThenNotattachFavIconNDocImageDetails()
            throws Exception {
        // Arrange
        serviceImpl = spy(serviceImpl);
        final List<Event> pojoEventList = prepareEvents();
        final SolrDocument solrDocument = prepareSolrDocument(null);
        final Map<Integer, SolrDocument> eventDocMap = singletonMap(Integer.valueOf(ENTITY_ID),
                solrDocument);
        final BaseSpec baseSpec = prepareBaseSpec();
        baseSpec.setIndustryClassificationId(SHORT_ONE);
        final SolrDocumentList solrDocumentList = new SolrDocumentList();
        solrDocumentList.add(solrDocument);
        when(searcher.fetchDocs(any(SolrQuery.class))).thenReturn(solrDocumentList);
        final DocumentSet documentDetails = new DocumentSet();
        final Document document = new Document();
        documentDetails.setDocuments(singletonList(document));
        doReturn(documentDetails)
                .when(serviceImpl)
                .getDocDetails(anyList(), any(EntityDetailSpec.class),
                        (Collection<String>) eq(null), eq(SHORT_ONE));
        // Act
        serviceImpl.attachRelatedDocumentDetails(pojoEventList, eventDocMap, baseSpec);
        // Assert
        final ArgumentCaptor<SolrQuery> solrQueryCaptor = ArgumentCaptor.forClass(SolrQuery.class);
        verify(searcher).fetchDocs(solrQueryCaptor.capture());
        final SolrQuery solrQuery = solrQueryCaptor.getValue();
        errorCollector.checkThat(solrQuery.getQuery(), equalTo("url:\"/test-url\""));
        errorCollector.checkThat(solrQuery.get("fl"), equalTo("id,groupId"));
        errorCollector.checkThat(solrQuery.getStart(), equalTo(0));
        errorCollector.checkThat(solrQuery.getRows(), equalTo(1));
        errorCollector.checkThat(solrQuery.getQueryType(), equalTo("standard"));
        final Event eventOne = pojoEventList.get(0);
        final MgmtTurnoverMeta mtMeta = eventOne.getMtMeta();
        errorCollector.checkThat(mtMeta.getRelatedDocument(), equalTo(documentDetails));
        final ArgumentCaptor<List<Long>> siteDocIdsCaptor = createLongListCaptor();
        final ArgumentCaptor<EntityDetailSpec> spec1Captor =
                ArgumentCaptor.forClass(EntityDetailSpec.class);
        verify(serviceImpl).getDocDetails(siteDocIdsCaptor.capture(), spec1Captor.capture(),
                (Collection<String>) eq(null), eq(SHORT_ONE));
        final List<Long> siteDocIds = siteDocIdsCaptor.getValue();
        errorCollector.checkThat(siteDocIds.size(), equalTo(1));
        errorCollector.checkThat(siteDocIds.get(0), equalTo(124L));
        final EntityDetailSpec spec1 = spec1Captor.getValue();
        errorCollector.checkThat(spec1.ipad, equalTo(false));
        errorCollector.checkThat(spec1.twitter, equalTo(false));
        errorCollector.checkThat(spec1.needPhrase, equalTo(false));
        final Event eventTwo = pojoEventList.get(1);
        final WebVolumeEventMeta webVolumeEventMeta = eventTwo.getWvMeta();
        final DocumentSet wvMetaDocumentSet = webVolumeEventMeta.getRelatedDocument();
        final List<Document> wvMetaDocumentList = wvMetaDocumentSet.getDocuments();
        errorCollector.checkThat(wvMetaDocumentList.size(), equalTo(1));
        final Document wvMetaDocument = wvMetaDocumentList.get(0);
        errorCollector.checkThat(wvMetaDocument.getUrl(), equalTo(TEST_URL));
        errorCollector.checkThat(wvMetaDocument.getTitle(), equalTo(TEST_EVENT_TITLE));
        errorCollector.checkThat(wvMetaDocument.getDate(), equalTo(testDate));
        errorCollector.checkThat(wvMetaDocument.getContentType(), equalTo(WEBNEWS));
    }

    @Test
    public void givenAttachRelatedDocumentDetailsWhenExceptionFromSearcherThenThrowException() throws Exception {
        // Arrange
        final List<Event> pojoEventList = prepareEvents();
        final SolrDocument solrDocument = prepareSolrDocument(LONG_ID_1);
        final Map<Integer, SolrDocument> eventDocMap = singletonMap(Integer.valueOf(ENTITY_ID),
                solrDocument);
        final SolrDocumentList documentList = new SolrDocumentList();
        documentList.add(solrDocument);
        when(searcher.fetchDocs(any(SolrQuery.class))).thenReturn(documentList);
        when(searcher.fetch(any(long[].class), anyString(), anyShort())).thenThrow(new SearchException());
        // Assert
        expectedException.expect(SearchException.class);
        // Act
        serviceImpl.attachRelatedDocumentDetails(pojoEventList, eventDocMap, prepareBaseSpec());
    }

    @Test
    public void givenToExcludeIdsListHasMatchedTopicIdWhenGetContentTypeAndFilterOutCTThenFilterOut()
            throws Exception {
        // Arrange
        final DocCatEntry matchedTopic = new DocCatEntry(entity, SHORT_ONE, SHORT_ONE);
        entity.id = ENTITY_ID;
        docEntry.matchedTopics = new ArrayList<>(1);
        docEntry.matchedTopics.add(matchedTopic);
        // Act
        final ContentType contentType = invokeMethod(serviceImpl, "getContentTypeAndFilterOutCT",
                docEntry, singletonList(ENTITY_ID));
        // Assert
        assertEquals(WEBNEWS, contentType);
        assertTrue(docEntry.matchedTopics.isEmpty());
    }

    @Test
    public void givenToExcludeIdsListHasMatchedCompanyIdWhenGetContentTypeAndFilterOutCTThenFilterOut()
            throws Exception {
        // Arrange
        final DocCatEntry matchedCompany = new DocCatEntry(entity, SHORT_ONE, SHORT_ONE);
        entity.id = ENTITY_ID;
        docEntry.matchedCompanies = new ArrayList<>(1);
        docEntry.matchedCompanies.add(matchedCompany);
        // Act
        final ContentType contentType = invokeMethod(serviceImpl, "getContentTypeAndFilterOutCT",
                docEntry, singletonList(ENTITY_ID));
        // Assert
        assertEquals(WEBNEWS, contentType);
        assertTrue(docEntry.matchedCompanies.isEmpty());
    }

    @Test
    public void givenTweetSetNullWhenGetTweetDetailsThenReturnNull() throws Exception {
        // Arrange
        spec.attachGroupInfo = true;
        doReturn(null).when(twitterService).getTweets((String[]) eq(null), eq(true),
                any(TwitterSpec.class), eq(LONG_ID_1));
        // Act
        final TweetSet tweetSet = serviceImpl.getTweetDetails(singletonList(LONG_ID_1), spec);
        // Assert
        assertNull(tweetSet);
    }

    @Test
    public void givenNoTweetsWhenGetTweetDetailsThenReturnNull() throws Exception {
        // Arrange
        spec.attachGroupInfo = true;
        final TweetSet tweetSet = new TweetSet();
        doReturn(tweetSet).when(twitterService).getTweets((String[]) eq(null), eq(true),
                any(TwitterSpec.class), eq(LONG_ID_1));
        // Act
        final TweetSet result = serviceImpl.getTweetDetails(singletonList(LONG_ID_1), spec);
        // Assert
        assertNull(result);
    }

    @Test
    public void givenNullWhenGetHighlightsResultThenReturnNull() throws Exception {
        // Arrange
        when(searcher.search(any(SearchSpec.class))).thenReturn(null);
        // Act
        final DocumentSet documentSet = serviceImpl.gethighlightsResults(new String[] {},
                new int[] {}, 1);
        // Assert
        assertNull(documentSet);
    }

    @Test
    public void givenEmptyScopeDirectiveWhenGetWebResultsForSearchThenGet() throws Exception {
        // Arrange
        serviceImpl = spy(serviceImpl);
        final BaseSpec baseSpec = prepareBaseSpec();
        final BlendDunsInput blendDunsInput = prepareBlendDuns();
        dnbEntityMap = blendDunsInput.getDnbEntityMap();
        PowerMockito.doReturn("").when(serviceImpl, "getScopeDirective",
                FQUERY_STR, baseSpec, dnbEntityMap, INTEGER_VALUE_ONE);
        PowerMockito.doReturn(singletonList(QUERY_STR))
                .when(serviceImpl, "getQListByScopeDirective", eq(dnbEntityMap), eq(-1), eq(-1), anyList(),
                        anySet(), anySet(), anySet(), anySet());
        final DocListBucket docListBucket = new DocListBucket(entity, null,
                INTEGER_VALUE_ONE, INTEGER_VALUE_TWO);
        searchResult.buckets = singletonList(docListBucket);
        // Act
        final DocumentSet documentSet = serviceImpl.getWebResultsForSearch(QUERY_STR, FQUERY_STR, baseSpec,
                blendDunsInput);
        // Assert
        errorCollector.checkThat(documentSet, nullValue());
        errorCollector.checkThat(blendDunsInput.getScopeDirective(), equalTo(""));
        errorCollector.checkThat(baseSpec.getCount(), equalTo((short) 3));
    }
}
