package com.firstrain.frapi.service.impl;

import com.firstrain.db.obj.Accelerometer;
import com.firstrain.frapi.FRCompletionService;
import com.firstrain.frapi.domain.BaseSpec;
import com.firstrain.frapi.domain.CompanyTradingRange;
import com.firstrain.frapi.domain.CompanyVolume;
import com.firstrain.frapi.domain.Document;
import com.firstrain.frapi.domain.HistoricalStat;
import com.firstrain.frapi.obj.EventQueryCriteria;
import com.firstrain.frapi.obj.GraphQueryCriteria;
import com.firstrain.frapi.obj.MgmtTurnoverServiceSpec;
import com.firstrain.frapi.pojo.Entity;
import com.firstrain.frapi.pojo.Event;
import com.firstrain.frapi.pojo.Graph;
import com.firstrain.frapi.pojo.GraphNode;
import com.firstrain.frapi.pojo.wrapper.DocumentSet;
import com.firstrain.frapi.pojo.wrapper.EventSet;
import com.firstrain.frapi.service.TwitterService;
import com.firstrain.frapi.util.ConvertUtil;
import com.firstrain.frapi.util.EntityHandler;
import com.firstrain.solr.client.DateCount;
import com.firstrain.solr.client.DateRange;
import com.firstrain.solr.client.DocEntry;
import com.firstrain.solr.client.DocListBucket;
import com.firstrain.solr.client.SearchResult;
import com.firstrain.solr.client.SolrSearcher;
import com.firstrain.solr.client.util.SolrServerReader;
import it.unimi.dsi.fastutil.longs.AbstractLongList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Spy;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import static com.firstrain.frapi.pojo.Graph.Range.NARROW;
import static com.firstrain.frapi.util.ContentType.FILINGS_10K;
import static com.firstrain.frapi.util.FRAPIConstant.INITIAL_DATE;
import static com.firstrain.frapi.util.FRAPIConstant.MAX_NO_OF_EVENTS;
import static com.firstrain.solr.client.DateRange.DEFAULT_DATE_FORMAT;
import static com.firstrain.solr.client.SearchSpec.SCOPE_NARROW;
import static com.firstrain.solr.client.SearchTokenEntry.SEARCH_TOKEN_KEYWORD;
import static java.util.Calendar.DATE;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anySet;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.powermock.reflect.Whitebox.invokeMethod;
import static org.powermock.reflect.Whitebox.setInternalState;

@RunWith(PowerMockRunner.class)
public class EntityBaseServiceImplPartThreeTest extends EntityBaseServiceImplTestSetup {

    @Spy
    private final ConvertUtil convertUtil = new ConvertUtil();
    @Spy
    private final TwitterService twitterService = new TwitterServiceImpl();

    @Test
    public void givenDocumenCatEntriesWhenSetSecondaryDunsInDocsThenSet() throws Exception {
        // Arrange
        serviceImpl = spy(serviceImpl);
        final Document document = new Document();
        final List<Document> documentList = new ArrayList<>(1);
        documentList.add(document);
        final List<Entity> catEntryList = singletonList(entityPojo);
        document.setCatEntries(catEntryList);
        final List<String> dunsList = singletonList(TEXT_STR);
        final Set<String> generalStringSet = singleton(TEXT_STR);
        PowerMockito.doNothing()
                .when(serviceImpl, "setSecondaryDuns", document, catEntryList,
                        dunsList, generalStringSet, generalStringSet, generalStringSet,
                        generalStringSet);
        // Act
        invokeMethod(serviceImpl, "setSecondaryDunsInDocs", documentList,
                dunsList, generalStringSet, generalStringSet, generalStringSet, generalStringSet);
        // Assert
        verifyPrivate(serviceImpl)
                .invoke("setSecondaryDuns", document, catEntryList, dunsList,
                        generalStringSet, generalStringSet, generalStringSet, generalStringSet);
    }

    @Test
    public void givenPrimaryDunsMatchedWhenSetSecondaryDunsThenSet() throws Exception {
        // Arrange
        final Document document = new Document();
        final List<String> dunsList = singletonList(TEST_SEARCH_TOKEN);
        final Set<String> generalStringSet = singleton(TEXT_STR);
        entityPojo.setName(TEST_ENTITY_LABEL);
        entityPojo.setSearchToken(TEST_SEARCH_TOKEN);
        // Act
        invokeMethod(serviceImpl, "setSecondaryDuns",
                document, singletonList(entityPojo), dunsList,
                generalStringSet, generalStringSet, generalStringSet, generalStringSet);
        // Assert
        assertEquals(TEST_ENTITY_LABEL, document.getPrimaryDunsMatchStr());
        assertTrue(entityPojo.getPrimaryDunsMatch());
    }

    @Test
    public void givenPrimaryDunsDidntMatchWhenSetSecondaryDunsThenSet() throws Exception {
        // Arrange
        serviceImpl = spy(serviceImpl);
        final Document document = new Document();
        final List<String> dunsList = new ArrayList<>(2);
        dunsList.add(TEXT_STR);
        dunsList.add(TEST_SEARCH_TOKEN);
        entityPojo.setName(TEST_ENTITY_LABEL);
        entityPojo.setSearchToken(TEST_SEARCH_TOKEN);
        final Set<String> uberTopicSet = singleton(TEST_UBER_TOPIC);
        final Set<String> businessTopicSet = singleton(TEST_BUSINESS_TOPIC);
        final Set<String> regionSet = singleton(TEST_REGION);
        final Set<String> businessLineSet = singleton(TEST_BUSINESS_LINE);
        PowerMockito.doNothing().when(serviceImpl, "populateAdditionalMatchQualifier",
                eq(document), anySet(), anyMap());
        // Act
        invokeMethod(serviceImpl, "setSecondaryDuns",
                document, singletonList(entityPojo), dunsList,
                uberTopicSet, businessTopicSet, regionSet, businessLineSet);
        // Assert
        errorCollector.checkThat(document.getPrimaryDunsMatchStr(), equalTo(TEST_ENTITY_LABEL));
        errorCollector.checkThat(entityPojo.getPrimaryDunsMatch(), equalTo(true));
        final ArgumentCaptor<Map<String, Entity>> entityMapCaptor = createEntityMapCaptor();
        verifyPrivate(serviceImpl).invoke("populateAdditionalMatchQualifier",
                eq(document), eq(uberTopicSet), entityMapCaptor.capture());
        final Map<String, Entity> entityMap = entityMapCaptor.getValue();
        errorCollector.checkThat(entityMap.size(), equalTo(1));
        errorCollector.checkThat(entityMap.get(TEST_SEARCH_TOKEN), equalTo(entityPojo));
        verifyPrivate(serviceImpl).invoke("populateAdditionalMatchQualifier",
                document, businessTopicSet, entityMap);
        verifyPrivate(serviceImpl).invoke("populateAdditionalMatchQualifier",
                document, regionSet, entityMap);
        verifyPrivate(serviceImpl).invoke("populateAdditionalMatchQualifier",
                document, businessLineSet, entityMap);
    }

    @Test
    public void givenSearchTokenListWhenPopulateAdditionalMatchQualifierThenPopulate() throws Exception {
        // Arrange
        final Document document = new Document();
        entityPojo.setName(TEST_ENTITY_LABEL);
        // Act
        invokeMethod(serviceImpl, "populateAdditionalMatchQualifier",
                document, singleton(TEST_SEARCH_TOKEN), singletonMap(TEST_SEARCH_TOKEN, entityPojo));
        // Assert
        assertEquals(", " + TEST_ENTITY_LABEL, document.getAdditionalMatchQualifierStr());
        assertTrue(entityPojo.getAdditionalMatchQualifier());
    }

    @Test
    public void givenDaysArrWhenGetScopeDirectiveThenGet() throws Exception {
        // Arrange
        serviceImpl = spy(serviceImpl);
        final BaseSpec baseSpec = prepareBaseSpec();
        dnbEntityMap = singletonMap(TEST_ENTITY_LABEL, entityPojo);
        final Map<Integer, SearchResult> response = singletonMap(INTEGER_VALUE_ONE, searchResult);
        when(taskExecutor.getThreadPoolExecutor()).thenReturn(executor);
        final FRCompletionService<Object[]> completionService = mock(FRCompletionService.class);
        whenNew(FRCompletionService.class)
                .withArguments(executor)
                .thenReturn(completionService);
        PowerMockito.doReturn(response)
                .when(serviceImpl, "collectResponse", completionService);
        PowerMockito.doReturn(singletonList(QUERY_STR))
                .when(serviceImpl, "getQList", eq(dnbEntityMap), anyMap());
        PowerMockito.doReturn(SCOPE_DIRECTIVES)
                .when(serviceImpl, "getScopeDirective", eq(10), anyMap(), eq(response),
                        eq(true));
        doReturn(searchResult).when(serviceImpl)
                .getSearchResult(any(String[].class), any(int[].class),
                        eq(FQUERY_STR), eq(baseSpec), eq(10));
        // Act
        final String result = invokeMethod(serviceImpl, "getScopeDirective",
                FQUERY_STR, baseSpec, dnbEntityMap, 10);
        // Assert
        errorCollector.checkThat(result, equalTo(SCOPE_DIRECTIVES));
        errorCollector.checkThat(baseSpec.isSectionMulti(), equalTo(true));
        errorCollector.checkThat(baseSpec.getStart(), equalTo(SHORT_ZERO));
        final ArgumentCaptor<Callable<Object[]>> callableTaskCaptor =
                createCallableTaskReturnsObjArrayCaptor();
        verify(completionService, times(3)).submit(callableTaskCaptor.capture());
        final Object[] searchedResult = callableTaskCaptor.getValue().call();
        errorCollector.checkThat(searchedResult.length, equalTo(2));
        errorCollector.checkThat((int) searchedResult[0], equalTo(180));
        errorCollector.checkThat((SearchResult) searchedResult[1], equalTo(searchResult));
    }

    @Test
    public void givenIsFirstDunsOnlyWhenGetScopeDirectiveThenReturnNull() throws Exception {
        // Arrange
        arrangeGetScopeDirectiveCommon();
        // Act
        final String result = invokeMethod(serviceImpl, "getScopeDirective", 10, iVsDunsIndex,
                responseMap, true);
        // Assert
        assertNull(result);
    }

    @Test
    public void givenNotFirstDunsOnlyWhenGetScopeDirectiveThenGet() throws Exception {
        // Arrange
        arrangeGetScopeDirectiveCommon();
        // Act
        final String result = invokeMethod(serviceImpl, "getScopeDirective", 10, iVsDunsIndex,
                responseMap, false);
        // Assert
        assertEquals("1|2|0|180", result);
    }

    @Test
    public void givenEntriesCountGreaterThanDesiredWhenGetScopeDirectiveThenGet() throws Exception {
        // Arrange
        arrangeGetScopeDirectiveCommon();
        // Act
        final String result = invokeMethod(serviceImpl, "getScopeDirective", 1, iVsDunsIndex,
                responseMap, false);
        // Assert
        assertEquals("1|1|-1|30", result);
    }

    @Test
    public void givenSubmissionsWhenCollectResponseThenCollect() throws Exception {
        // Arrange
        final FRCompletionService<Object[]> completionService =
                (FRCompletionService<Object[]>) mock(FRCompletionService.class);
        when(completionService.getSubmissions()).thenReturn(1);
        final Future<Object[]> future = (Future<Object[]>) mock(Future.class);
        when(completionService.poll(1000L, MILLISECONDS)).thenReturn(future);
        final Object[] response = {10, searchResult};
        when(future.get()).thenReturn(response);
        serviceImpl.executorTimeout = 1000L;
        // Act
        final Map<Integer, SearchResult> result =
                invokeMethod(serviceImpl, "collectResponse", completionService);
        // Assert
        assertEquals(1, result.size());
        assertEquals(searchResult, result.get(10));
    }

    @Test
    public void givenDNBEntityMapEmptyWhenGetQListByScopeDirectiveThenReturnNull() throws Exception {
        // Act
        final List<String> result = invokeMethod(serviceImpl, "getQListByScopeDirective", new HashMap<>(0), null, null,
                null, null, null, null, null);
        // Assert
        assertNull(result);
    }

    @Test
    public void givenLastDunsIndexReachedAndContentAlgoIdxNegOneWhenGetQListByScopeDirectiveThenGet() throws Exception {
        // Arrange
        arrangeGetQListByScopeDirectivePrimaryEntityNullCasesCommon();
        final List<String> dunsList = new ArrayList<>(1);
        // Act
        final List<String> result = invokeMethod(serviceImpl, "getQListByScopeDirective", dnbEntityMap, 1, -1,
                dunsList, null, null, null, null);
        // Assert
        errorCollector.checkThat(result.size(), equalTo(1));
        errorCollector.checkThat(result.get(0), equalTo(TEST_SEARCH_TOKEN));
        errorCollector.checkThat(dunsList.size(), equalTo(1));
        errorCollector.checkThat(dunsList.get(0), equalTo(TEST_SEARCH_TOKEN));
    }

    @Test
    public void givenLastDunsIndexReachedAndContentAlgoIdxZeroWhenGetQListByScopeDirectiveThenGet() throws Exception {
        // Arrange
        arrangeGetQListByScopeDirectivePrimaryEntityNullCasesCommon();
        final List<String> dunsList = new ArrayList<>(1);
        // Act
        final List<String> result = invokeMethod(serviceImpl, "getQListByScopeDirective", dnbEntityMap, 1, 0,
                dunsList, null, null, null, null);
        // Assert
        errorCollector.checkThat(result.size(), equalTo(2));
        errorCollector.checkThat(result.get(0), equalTo(TEST_SEARCH_TOKEN));
        errorCollector.checkThat(result.get(1), equalTo(TEST_SEARCH_TOKEN + " AND BL:BUSINESS_LINE_NOT_FOUND"));
        errorCollector.checkThat(dunsList.size(), equalTo(1));
        errorCollector.checkThat(dunsList.get(0), equalTo(TEST_SEARCH_TOKEN));
    }

    @Test
    public void givenLastDunsIndexReachedAndContentAlgoIdxOneWhenGetQListByScopeDirectiveThenGet() throws Exception {
        // Arrange
        arrangeGetQListByScopeDirectivePrimaryEntityNotNullCasesCommon();
        final List<String> dunsList = new ArrayList<>(2);
        // Act
        final List<String> result = invokeMethod(serviceImpl, "getQListByScopeDirective", dnbEntityMap, 2, 1,
                dunsList, null, null, null, null);
        // Assert
        errorCollector.checkThat(result.size(), equalTo(3));
        errorCollector.checkThat(result.get(0), equalTo(TEST_SEARCH_TOKEN));
        errorCollector.checkThat(result.get(1),
                equalTo(TEST_SEARCH_TOKEN + " OR " + TEST_SEARCH_TOKEN + " AND BL:BUSINESS_LINE_NOT_FOUND"));
        errorCollector.checkThat(result.get(2),
                equalTo(TEST_SEARCH_TOKEN + " AND BL:BUSINESS_LINE_NOT_FOUND"));
        errorCollector.checkThat(dunsList.size(), equalTo(2));
        errorCollector.checkThat(dunsList.get(0), equalTo(TEST_SEARCH_TOKEN));
        errorCollector.checkThat(dunsList.get(1), equalTo(TEST_SEARCH_TOKEN));
    }

    @Test
    public void givenLastDunsIndexReachedAndContentAlgoIdxTwoWhenGetQListByScopeDirectiveThenGet() throws Exception {
        // Arrange
        arrangeGetQListByScopeDirectivePrimaryEntityNotNullCasesCommon();
        final List<String> dunsList = new ArrayList<>(2);
        // Act
        final List<String> result = invokeMethod(serviceImpl, "getQListByScopeDirective", dnbEntityMap, 2, 2,
                dunsList, null, null, null, null);
        // Assert
        errorCollector.checkThat(result.size(), equalTo(4));
        errorCollector.checkThat(result.get(0), equalTo(TEST_SEARCH_TOKEN));
        errorCollector.checkThat(result.get(1),
                equalTo(TEST_SEARCH_TOKEN + " OR " + TEST_SEARCH_TOKEN + " AND BL:BUSINESS_LINE_NOT_FOUND"));
        errorCollector.checkThat(result.get(2),
                equalTo(TEST_SEARCH_TOKEN + " AND BL:BUSINESS_LINE_NOT_FOUND"));
        errorCollector.checkThat(result.get(3),
                equalTo("R:REGION_NOT_FOUND"));
        errorCollector.checkThat(dunsList.size(), equalTo(2));
        errorCollector.checkThat(dunsList.get(0), equalTo(TEST_SEARCH_TOKEN));
        errorCollector.checkThat(dunsList.get(1), equalTo(TEST_SEARCH_TOKEN));
    }

    @Test
    public void givenLastDunsIndexReachedAndContentAlgoIdxThreeWhenGetQListByScopeDirectiveThenGet() throws Exception {
        // Arrange
        arrangeGetQListByScopeDirectivePrimaryEntityNotNullCasesCommon();
        final List<String> dunsList = new ArrayList<>(2);
        // Act
        final List<String> result = invokeMethod(serviceImpl, "getQListByScopeDirective", dnbEntityMap, 2, 3,
                dunsList, null, null, null, null);
        // Assert
        errorCollector.checkThat(result.size(), equalTo(5));
        errorCollector.checkThat(result.get(0), equalTo(TEST_SEARCH_TOKEN));
        errorCollector.checkThat(result.get(1),
                equalTo(TEST_SEARCH_TOKEN + " OR " + TEST_SEARCH_TOKEN + " AND BL:BUSINESS_LINE_NOT_FOUND"));
        errorCollector.checkThat(result.get(2),
                equalTo(TEST_SEARCH_TOKEN + " AND BL:BUSINESS_LINE_NOT_FOUND"));
        errorCollector.checkThat(result.get(3), equalTo("R:REGION_NOT_FOUND"));
        errorCollector.checkThat(result.get(4), equalTo(TEST_SEARCH_TOKEN + " AND BL:BUSINESS_LINE_NOT_FOUND"));
        errorCollector.checkThat(dunsList.size(), equalTo(2));
        errorCollector.checkThat(dunsList.get(0), equalTo(TEST_SEARCH_TOKEN));
        errorCollector.checkThat(dunsList.get(1), equalTo(TEST_SEARCH_TOKEN));
    }

    @Test
    public void givenEmptyDNBEntityMapWhenGetQListThenReturnNull() throws Exception {
        // Act
        final List<String> result = invokeMethod(serviceImpl, "getQList", emptyMap(), null);
        // Assert
        assertNull(result);
    }

    @Test
    public void givenDNBEntityMapWhenGetQListThenGet() throws Exception {
        // Arrange
        entityPojo.setCountry("PK");
        entityPojo.setBizLineCatIds(singletonList(INTEGER_VALUE_ONE));
        arrangeGetQListByScopeDirectivePrimaryEntityNotNullCasesCommon();
        iVsDunsIndex = new HashMap<>(5);
        serviceImpl.uberTopics = TEST_UBER_TOPIC;
        serviceImpl.businessBasics = TEST_BUSINESS_TOPIC;
        // Act
        final List<String> result = invokeMethod(serviceImpl, "getQList", dnbEntityMap, iVsDunsIndex);
        // Assert
        errorCollector.checkThat(result.size(), equalTo(5));
        errorCollector.checkThat(result.get(0), equalTo(TEST_SEARCH_TOKEN));
        errorCollector.checkThat(result.get(1),
                equalTo(TEST_SEARCH_TOKEN + " OR " + TEST_SEARCH_TOKEN + " AND " + TEXT_STR));
        errorCollector.checkThat(result.get(2),
                equalTo(TEST_SEARCH_TOKEN + " AND " + TEXT_STR));
        errorCollector.checkThat(result.get(3), equalTo("R:REGION_NOT_FOUND"));
        errorCollector.checkThat(result.get(4),
                equalTo(TEST_SEARCH_TOKEN + " AND " + TEXT_STR));
    }

    @Test
    public void givenSearchTokenListNotWhenGetCatIdsInORThenGet() throws Exception {
        // Arrange
        when(entityInfo.getSearchToken()).thenReturn(TEST_SEARCH_TOKEN);
        final Set<String> searchTokenSet = new HashSet<>(1);
        // Act
        final String result = invokeMethod(serviceImpl, "getCatIdsInOR",
                Arrays.toString(tCatIds), searchTokenSet);
        // Assert
        errorCollector.checkThat(result,
                equalTo(TEST_SEARCH_TOKEN + " OR " + TEST_SEARCH_TOKEN + " OR " + TEST_SEARCH_TOKEN));
        errorCollector.checkThat(searchTokenSet.size(), equalTo(1));
        errorCollector.checkThat(searchTokenSet.contains(TEST_SEARCH_TOKEN), equalTo(true));
    }

    @Test
    public void givenSingleCatIdWhenGetCatIdsInORThenReturnBLNotFound() throws Exception {
        // Arrange
        when(infoCache.catIdToEntity("1")).thenReturn(null);
        // Act
        final String result = invokeMethod(serviceImpl, "getCatIdsInOR",
                "1", null);
        // Assert
        assertEquals("BL:BUSINESS_LINE_NOT_FOUND", result);
    }

    @Test
    public void givenCountryWhenGetCountrySearchTokenThenGet() throws Exception {
        // Arrange
        final Map<String, String> regionNameVSSearchToken = singletonMap("PK", TEST_SEARCH_TOKEN);
        when(regionExcelUtilImpl.getRegionNameVsSearchToken()).thenReturn(regionNameVSSearchToken);
        final Set<String> searchTokenSet = new HashSet<>(1);
        // Act
        final String result = invokeMethod(serviceImpl, "getCountrySearchToken",
                TEXT_STR, "PK", searchTokenSet);
        // Assert
        errorCollector.checkThat(result, equalTo(TEXT_STR + " AND " + TEST_SEARCH_TOKEN));
        errorCollector.checkThat(searchTokenSet.size(), equalTo(1));
        errorCollector.checkThat(searchTokenSet.contains(TEST_SEARCH_TOKEN), equalTo(true));
    }

    @Test
    public void givenSearchTokenKnownWhenMakeDocSetFromEntriesThenReturnNull() throws Exception {
        // Arrange
        searchResult.bucketType = SEARCH_TOKEN_KEYWORD;
        // Act
        final DocumentSet result =
                serviceImpl.makeDocSetFromEntries(searchResult, false, 0,
                        documentSimilarityUtil, null, false, 0, false, false);
        // Assert
        assertNull(result);
    }

    @Test
    public void givenNullEntriesWhenMakeDocSetFromEntriesThenReturnNull() throws Exception {
        // Arrange
        searchResult.buckets = new ArrayList<>(1);
        final DocListBucket docListBucket = new DocListBucket(entity,
                null, 0, 0);
        searchResult.buckets.add(docListBucket);
        // Act
        final DocumentSet result =
                serviceImpl.makeDocSetFromEntries(searchResult, false, 0,
                        documentSimilarityUtil, null, false, 0, false, false);
        // Assert
        assertNull(result);
    }

    @Test
    public void givenSingleEntityWebResultWhenMakeDocSetFromEntriesThenMake() throws Exception {
        // Arrange
        arrangeTwitterService();
        final List<DocEntry> docEntryList = prepareDocEntries();
        searchResult.buckets = new ArrayList<>(1);
        final DocListBucket docListBucket = new DocListBucket(entity,
                docEntryList, 0, 0);
        searchResult.buckets.add(docListBucket);
        // Act
        final DocumentSet result =
                serviceImpl.makeDocSetFromEntries(searchResult, false, 1,
                        documentSimilarityUtil, null, false, 2, true, false);
        // Assert
        final List<Document> documentList = result.getDocuments();
        errorCollector.checkThat(documentList.size(), equalTo(1));
        final Document document = documentList.get(0);
        errorCollector.checkThat(document.getContentType(), equalTo(FILINGS_10K));
        errorCollector.checkThat(result.isFiling(), equalTo(false));
        errorCollector.checkThat(result.getTotalCount(), equalTo(3));
    }

    @Test
    public void givenEntityInfoNullWhenGetWebVolumeGraphThenReturnNull() throws Exception {
        // Arrange
        final BaseSpec baseSpec = new BaseSpec();
        when(infoCache.catIdToEntity(ENTITY_ID)).thenReturn(null);
        // Act
        final Graph result = serviceImpl.getWebVolumeGraph(ENTITY_ID, null, null,
                baseSpec, null, 0, 0, null, false);
        // Assert
        assertNull(result);
    }

    @Test
    public void givenSignalListNullWhenGetEventSetWithIdThenReturnNull() throws Exception {
        // Act
        final List<Event> result = serviceImpl.getEventSetWithId(null);
        // Assert
        assertNull(result);
    }

    @Test
    public void givenSignalListWhenGetEventSetWithIdThenGet() throws Exception {
        // Arrange
        final List<Event> signalList = prepareEvents();
        mockStatic(EntityHandler.class);
        // Act
        final List<Event> result = serviceImpl.getEventSetWithId(signalList);
        // Assert
        assertEquals(signalList, result);
        verifyStatic();
        EntityHandler.addId(signalList.get(0));
        verifyStatic();
        EntityHandler.addId(signalList.get(1));
    }

    @Test
    public void givenDateCountNullWhenToCompanyVolumeInfoThenReturnNull()
            throws Exception {
        // Act
        final List<CompanyVolume> result = invokeMethod(serviceImpl, "toCompanyVolumeInfo",
                (DateCount) null, 0);
        // Assert
        assertNull(result);
    }

    @Test
    public void givenDateCountsWhenToCompanyVolumeInfoThenGetCompanyVolInfo() throws Exception {
        // Arrange
        final DateCount dateCount = mock(DateCount.class);
        dateCount.start = 2L;
        final AbstractLongList longArrayList = new LongArrayList();
        longArrayList.add(1L);
        when(dateCount.getCounts()).thenReturn((LongArrayList) longArrayList);

        mockStatic(SolrSearcher.class);
        when(SolrSearcher.getTimeZone()).thenReturn(TimeZone.getTimeZone("UTC"));
        final DateFormat dateFormat = mock(SimpleDateFormat.class);
        whenNew(SimpleDateFormat.class)
                .withArguments(DEFAULT_DATE_FORMAT)
                .thenReturn((SimpleDateFormat) dateFormat);
        when(dateFormat.parse(INITIAL_DATE)).thenThrow(ParseException.class);
        testDate = new Date();
        whenNew(Date.class).withNoArguments().thenReturn(testDate);
        whenNew(Date.class).withArguments(2L).thenReturn(testDate);
        // Act
        final List<CompanyVolume> result = invokeMethod(serviceImpl, "toCompanyVolumeInfo",
                dateCount, 0);
        // Assert
        errorCollector.checkThat(result.size(), equalTo(1));
        final CompanyVolume companyVolume = result.get(0);
        errorCollector.checkThat(companyVolume.getDiffId(), equalTo(0));
        errorCollector.checkThat(companyVolume.getTotal(), equalTo(1D));
        errorCollector.checkThat(companyVolume.getDate(), CoreMatchers.any(Timestamp.class));
    }

    @Test
    public void givenCompanyIdWhenGenerateGrahObjectThenGenerate() throws Exception {
        // Arrange
        serviceImpl = spy(serviceImpl);
        final GraphQueryCriteria queryCriteria = new GraphQueryCriteria();
        queryCriteria.setCompanyId(INTEGER_VALUE_ONE);
        queryCriteria.setNumberOfDays(10);
        queryCriteria.setScope(SCOPE_NARROW);
        final DateRange dateRange = mock(DateRange.class);
        testDate = new Date();
        when(dateRange.getStart()).thenReturn(testDate);
        final Date testDateTwo = new Date();
        when(dateRange.getEnd()).thenReturn(testDateTwo);
        queryCriteria.setDateRange(dateRange);
        final CompanyTradingRange companyTradingRange = new CompanyTradingRange();
        final List<CompanyTradingRange> companyTradingInfo =
                singletonList(companyTradingRange);
        doReturn(companyTradingInfo)
                .when(serviceImpl).getCompanyTradingInfoFromIndex(INTEGER_VALUE_ONE);
        final Map<Integer, CompanyTradingRange> companyTradingMap =
                singletonMap(INTEGER_VALUE_ONE, companyTradingRange);
        PowerMockito.doReturn(companyTradingMap)
                .when(serviceImpl, "getCompanyTradingMap",
                        companyTradingInfo);
        final CompanyVolume companyVolume = new CompanyVolume();
        final List<CompanyVolume> companyVolInfo = singletonList(companyVolume);
        final Graph expectedGraphObj = new Graph();
        PowerMockito.doReturn(expectedGraphObj)
                .when(serviceImpl, "generateHistoricalStats",
                        companyVolInfo, companyTradingMap, 10, 100);
        // Act
        final Graph result =
                serviceImpl.generateGraphObject(queryCriteria, companyVolInfo, 100);
        // Assert
        errorCollector.checkThat(result, equalTo(expectedGraphObj));
        errorCollector.checkThat(result.getCurrentRange(), equalTo(NARROW));
        errorCollector.checkThat(result.getsDate(), equalTo(testDate));
        errorCollector.checkThat(result.geteDate(), equalTo(testDateTwo));
    }

    @Test
    public void givenCompanyTradingInfoWhenGetCompanyTradingMapThenGet() throws Exception {
        // Arrange
        final CompanyTradingRange companyTradingRange = new CompanyTradingRange();
        companyTradingRange.setDiffId(10);
        // Act
        final Map<Integer, CompanyTradingRange> result =
                invokeMethod(serviceImpl, "getCompanyTradingMap", singletonList(companyTradingRange));
        // Assert
        assertEquals(1, result.size());
        assertEquals(companyTradingRange, result.get(10));
    }

    @Test
    public void givenCompanyVolListWhenGenerateHistoricalStatsThenGenerate() throws Exception {
        // Arrange
        final CompanyVolume companyVolume = new CompanyVolume();
        companyVolume.setDiffId(INTEGER_VALUE_ONE);
        final List<CompanyVolume> companyVolumeList = singletonList(companyVolume);
        final CompanyTradingRange companyTradingRangeOne = new CompanyTradingRange();
        final CompanyTradingRange companyTradingRangeTwo = new CompanyTradingRange();
        final CompanyTradingRange companyTradingRangeThree = new CompanyTradingRange();
        final Map<Integer, CompanyTradingRange> companyTradingMap =
                new HashMap<>(3);
        companyTradingMap.put(-1, companyTradingRangeOne);
        companyTradingMap.put(INTEGER_VALUE_ONE, companyTradingRangeTwo);
        companyTradingMap.put(INTEGER_VALUE_FIVE, companyTradingRangeThree);
        final DateFormat dateFormat = mock(SimpleDateFormat.class);
        whenNew(SimpleDateFormat.class)
                .withArguments(DEFAULT_DATE_FORMAT)
                .thenReturn((SimpleDateFormat) dateFormat);
        final Calendar calendar = Calendar.getInstance();
        calendar.add(DATE, -1);
        final Date oneDayBefore = new Date(calendar.getTimeInMillis());
        when((dateFormat.parse(INITIAL_DATE))).thenReturn(oneDayBefore);
        // Act
        final Graph result = invokeMethod(serviceImpl, "generateHistoricalStats",
                companyVolumeList, companyTradingMap, INTEGER_VALUE_TWO, 1);
        // Assert
        final List<HistoricalStat> historicalStatList = result.getHistoricalStat();
        errorCollector.checkThat(historicalStatList.size(), equalTo(2));
        final HistoricalStat historicalStatOne = historicalStatList.get(0);
        final CompanyVolume historicalStatCompanyVolumeOne = historicalStatOne.getCompanyVolume();
        errorCollector.checkThat(historicalStatCompanyVolumeOne.getDiffId(), equalTo(0));
        errorCollector.checkThat(historicalStatCompanyVolumeOne.getTotal(), equalTo(0D));
        errorCollector.checkThat(historicalStatCompanyVolumeOne.getDate(), CoreMatchers.any(Timestamp.class));
        errorCollector.checkThat(historicalStatOne.getTradeRange(), equalTo(companyTradingRangeOne));
        final HistoricalStat historicalStatTwo = historicalStatList.get(1);
        final CompanyVolume historicalStatCompanyVolumeTwo = historicalStatTwo.getCompanyVolume();
        errorCollector.checkThat(historicalStatCompanyVolumeTwo.getDiffId(), equalTo(1));
        errorCollector.checkThat(historicalStatCompanyVolumeTwo.getTotal(), equalTo(0D));
        errorCollector.checkThat(historicalStatCompanyVolumeTwo.getDate(), nullValue());
        errorCollector.checkThat(historicalStatTwo.getTradeRange(), equalTo(companyTradingRangeTwo));
    }

    @Test
    public void givenOpeningPricesNullWhenGetCompanyTradingInfoFromIndexThenReturnNull() throws Exception {
        // Arrange
        final SolrServer entitySolrServer = mock(SolrServer.class);
        when(entityBaseServiceRepository.getEntitySolrServer()).thenReturn(entitySolrServer);
        final SolrDocumentList solrDocList = new SolrDocumentList();
        final SolrDocument solrDocument = mock(SolrDocument.class);
        solrDocList.add(solrDocument);
        mockStatic(SolrServerReader.class);
        when(SolrServerReader.retrieveNSolrDocs(entitySolrServer, "attrCompanyId:10",
                0, 1, "openingPrice", "closingPrice", "dayId"))
                .thenReturn(solrDocList);
        // Act
        final List<CompanyTradingRange> result =
                serviceImpl.getCompanyTradingInfoFromIndex(10);
        // Assert
        assertNull(result);
    }

    @Test
    public void givenCompanyIdsArrNullWhenGetEventsTimelineThenReturnNull() throws Exception {
        // Act
        final EventSet result = serviceImpl.getEventsTimeline(null, null, null);
        // Assert
        assertNull(result);
    }

    @Test
    public void givenEventAfterApplyBCNullWhenGetEventsTimelineThenReturnNull() throws Exception {
        // Arrange
        final BaseSpec baseSpec = prepareBaseSpec();
        when(eventService.applyBC(iEventsList, true, MAX_NO_OF_EVENTS))
                .thenReturn(null);
        // Act
        final EventSet result = serviceImpl.getEventsTimeline(companyIdsArr,
                tCatIds, baseSpec);
        // Assert
        assertNull(result);
    }

    @Test
    public void givenEventAfterApplyBCNotNullWhenGetEventsTimelineThenGetEventSetInstance() throws Exception {
        // Arrange
        final BaseSpec baseSpec = prepareBaseSpec();
        // Act
        final EventSet result = serviceImpl.getEventsTimeline(companyIdsArr,
                tCatIds, baseSpec);
        // Assert
        assertThat(result, new ReflectionEquals(new EventSet()));
    }

    @Test
    public void givenEntityInfoNullWhenGetNodesThenReturnNull() throws Exception {
        // Arrange
        final Accelerometer accelerometer = new Accelerometer();
        accelerometer.setEntityId(ENTITY_ID);
        when(infoCache.catIdToEntity(ENTITY_ID)).thenReturn(null);
        // Act
        final GraphNode result =
                invokeMethod(serviceImpl, "getNodes", accelerometer, false, false);
        // Assert
        assertNull(result);
    }

    @Test
    public void givenAccelerometerThreeDayCountLessThan4WhenGetNodesThenReturnNull() throws Exception {
        // Arrange
        final Accelerometer accelerometer = new Accelerometer();
        accelerometer.setEntityId(ENTITY_ID);
        accelerometer.setThreeDayCount(INTEGER_VALUE_ONE);
        when(infoCache.catIdToEntity(ENTITY_ID)).thenReturn(entityInfo);
        // Act
        final GraphNode result =
                invokeMethod(serviceImpl, "getNodes", accelerometer, false, false);
        // Assert
        assertNull(result);
    }

    @Test
    public void givenAccelerometerScoreEqScoreThWhenGetNodesThenReturnNull() throws Exception {
        // Arrange
        final Accelerometer accelerometer = new Accelerometer();
        accelerometer.setEntityId(ENTITY_ID);
        accelerometer.setThreeDayCount(INTEGER_VALUE_FIVE);
        accelerometer.setScore(75);
        when(infoCache.catIdToEntity(ENTITY_ID)).thenReturn(entityInfo);
        // Act
        final GraphNode result =
                invokeMethod(serviceImpl, "getNodes", accelerometer, false, false);
        // Assert
        assertNull(result);
    }

    @Test
    public void givenNotIpadWhenGetNodesThenGet() throws Exception {
        // Arrange
        final Accelerometer accelerometer = new Accelerometer();
        accelerometer.setEntityId(ENTITY_ID);
        accelerometer.setThreeDayCount(INTEGER_VALUE_FIVE);
        accelerometer.setScore(INTEGER_VALUE_SEVENTY_SIX);
        accelerometer.setArrow(SHORT_ONE);
        accelerometer.setImageName(TEST_IMAGE_NAME);
        when(infoCache.catIdToEntity(ENTITY_ID)).thenReturn(entityInfo);
        when(entityInfo.getId()).thenReturn(ENTITY_ID);
        when(entityInfo.getName()).thenReturn(TEST_ENTITY_LABEL);
        when(entityInfo.getSearchToken()).thenReturn(TEST_SEARCH_TOKEN);
        // Act
        final GraphNode result = invokeMethod(serviceImpl, "getNodes", accelerometer, false, false);
        // Assert
        errorCollector.checkThat(result.getImageName(), equalTo(TEST_IMAGE_NAME));
        errorCollector.checkThat(result.getSearchToken(), equalTo(TEST_SEARCH_TOKEN));
        errorCollector.checkThat(result.getName(), equalTo(TEST_ENTITY_LABEL));
        errorCollector.checkThat(result.getSmartText(),
                equalTo("<span class='floatleft'><span class='bold'><span class='colorRed'><span class='font14'>24%</span></span></span></span><span class='analytic_tooltip_smartText'><span class='bold'><span class='colorRed'>below average</span></span><br />and <span class='bold'><span class='colorGreen'>rising</span></span></span>"));
    }

    @Test
    public void givenNodesListSizeMoreThanMaxWhenTrimAndSortGraphDataListThenTrimAndSort() throws Exception {
        // Arrange
        final GraphNode graphNodeOne = new GraphNode();
        graphNodeOne.setValue(8F);
        final GraphNode graphNodeTwo = new GraphNode();
        graphNodeTwo.setValue(7F);
        final GraphNode graphNodeThree = new GraphNode();
        graphNodeThree.setValue(6F);
        final GraphNode graphNodeFour = new GraphNode();
        graphNodeFour.setValue(5F);
        final GraphNode graphNodeFive = new GraphNode();
        graphNodeFive.setValue(4F);
        final GraphNode graphNodeSix = new GraphNode();
        graphNodeSix.setValue(3F);
        final GraphNode graphNodeSeven = new GraphNode();
        graphNodeSeven.setValue(2F);
        final GraphNode graphNodeEight = new GraphNode();
        graphNodeEight.setValue(1F);
        final List<GraphNode> nodeList = new ArrayList<>(8);
        nodeList.add(graphNodeOne);
        nodeList.add(graphNodeTwo);
        nodeList.add(graphNodeThree);
        nodeList.add(graphNodeFour);
        nodeList.add(graphNodeFive);
        nodeList.add(graphNodeSix);
        nodeList.add(graphNodeSeven);
        nodeList.add(graphNodeEight);
        // Act
        invokeMethod(serviceImpl, "trimAndSortGraphDataList",
                nodeList, false);
        // Assert
        errorCollector.checkThat(nodeList.size(), equalTo(6));
        errorCollector.checkThat(nodeList.get(0), equalTo(graphNodeOne));
        errorCollector.checkThat(nodeList.get(1), equalTo(graphNodeTwo));
        errorCollector.checkThat(nodeList.get(2), equalTo(graphNodeThree));
        errorCollector.checkThat(nodeList.get(3), equalTo(graphNodeFour));
        errorCollector.checkThat(nodeList.get(4), equalTo(graphNodeFive));
        errorCollector.checkThat(nodeList.get(5), equalTo(graphNodeSix));
    }

    @Test
    public void givenOneWhenGetContentAlgoThenReturnOne() throws Exception {
        testGetContentAlgo(1, 1);
    }

    @Test
    public void givenFourWhenGetContentAlgoThenReturnOne() throws Exception {
        testGetContentAlgo(4, 1);
    }

    @Test
    public void givenSevenWhenGetContentAlgoThenReturnOne() throws Exception {
        testGetContentAlgo(7, 1);
    }

    @Test
    public void givenTwoWhenGetContentAlgoThenReturnTwo() throws Exception {
        testGetContentAlgo(2, 2);
    }

    @Test
    public void givenFiveWhenGetContentAlgoThenReturnTwo() throws Exception {
        testGetContentAlgo(5, 2);
    }

    @Test
    public void givenEightWhenGetContentAlgoThenReturnTwo() throws Exception {
        testGetContentAlgo(8, 2);
    }

    @Test
    public void givenThreeWhenGetContentAlgoThenReturnThree() throws Exception {
        testGetContentAlgo(3, 3);
    }

    @Test
    public void givenSixWhenGetContentAlgoThenReturnThree() throws Exception {
        testGetContentAlgo(6, 3);
    }

    @Test
    public void givenNineWhenGetContentAlgoThenReturnThree() throws Exception {
        testGetContentAlgo(9, 3);
    }

    @Test
    public void givenIntMoreThanNineWhenGetContentAlgoThenReturnNegTwo() throws Exception {
        testGetContentAlgo(10, -2);
    }

    @Test
    public void givenSolrDocsWhenMakeSolrCallParallelThenGetMgmtFromSolr() throws Exception {
        // Arrange
        final BaseSpec baseSpec = prepareBaseSpec();
        final EventQueryCriteria.EventTypeRange eventTypeRange =
                new EventQueryCriteria.EventTypeRange(INTEGER_VALUE_ONE, INTEGER_VALUE_FIVE);
        final MgmtTurnoverServiceSpec mgmtTurnoverServiceSpec = new MgmtTurnoverServiceSpec();
        final List<Integer> partition = singletonList(INTEGER_VALUE_ONE);
        final FRCompletionService<SolrDocumentList> completionService =
                (FRCompletionService<SolrDocumentList>) mock(FRCompletionService.class);
        // Act
        invokeMethod(serviceImpl, "makeSolrCallParallel",
                baseSpec, eventTypeRange, mgmtTurnoverServiceSpec, partition,
                completionService);
        // Assert
        final ArgumentCaptor<Callable<SolrDocumentList>> callableTaskCaptor =
                createCallableTaskReturnsSolrDocListCaptor();
        verify(completionService).submit(callableTaskCaptor.capture());
        callableTaskCaptor.getValue().call();
        verify(eventService).getMgmtFromSolr(partition, mgmtTurnoverServiceSpec, true, eventTypeRange,
                baseSpec);
    }

    private void arrangeTwitterService() {
        setInternalState(twitterService, "entityBaseServiceRepository", entityBaseServiceRepository);
        setInternalState(twitterService, "industryClassificationMap", industryClassificationMap);
        setInternalState(twitterService, "convertUtil", convertUtil);
        setInternalState(serviceImpl, "twitterService", twitterService);
    }
}
