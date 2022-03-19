package com.firstrain.frapi.service.impl;

import com.firstrain.frapi.FRCompletionService;
import com.firstrain.frapi.domain.BaseSpec;
import com.firstrain.frapi.domain.BlendDunsInput;
import com.firstrain.frapi.domain.CompanyVolume;
import com.firstrain.frapi.domain.Document;
import com.firstrain.frapi.domain.Tweet;
import com.firstrain.frapi.events.IEvents;
import com.firstrain.frapi.obj.EventQueryCriteria;
import com.firstrain.frapi.obj.GraphQueryCriteria;
import com.firstrain.frapi.obj.MgmtTurnoverServiceSpec;
import com.firstrain.frapi.pojo.Event;
import com.firstrain.frapi.pojo.Graph;
import com.firstrain.frapi.pojo.wrapper.DocumentSet;
import com.firstrain.frapi.pojo.wrapper.EventSet;
import com.firstrain.frapi.pojo.wrapper.TweetSet;
import com.firstrain.frapi.service.TwitterService;
import com.firstrain.frapi.util.ConvertUtil;
import com.firstrain.solr.client.DateRange;
import com.firstrain.solr.client.DocEntriesUpdator;
import com.firstrain.solr.client.DocEntry;
import com.firstrain.solr.client.QuoteEntry;
import com.firstrain.solr.client.QuoteEntry.QuoteType;
import com.firstrain.solr.client.SearchException;
import com.firstrain.solr.client.SearchResult;
import com.firstrain.solr.client.SearchSpec;
import com.firstrain.solr.client.SearchTokenEntry;
import com.firstrain.solr.client.SolrSearcher;
import com.firstrain.solr.client.util.SolrServerReader;
import com.firstrain.utils.FR_ArrayUtils;
import com.firstrain.utils.TitleUtils;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.Future;

import static com.firstrain.frapi.pojo.Graph.GraphFor.CALL_PREP;
import static com.firstrain.frapi.pojo.Graph.Range.BROAD;
import static com.firstrain.frapi.pojo.Graph.Range.MEDIUM;
import static com.firstrain.frapi.pojo.Graph.Range.NARROW;
import static com.firstrain.frapi.util.ContentType.FILINGS_10K;
import static com.firstrain.frapi.util.ContentType.WEBNEWS;
import static com.firstrain.frapi.util.DefaultEnums.EventInformationEnum.MT_HIRE;
import static com.firstrain.solr.client.DateRange.DEFAULT_DATE_FORMAT;
import static com.firstrain.solr.client.SearchSpec.ORDER_DATE;
import static com.firstrain.solr.client.SearchSpec.SCOPE_AUTO;
import static com.firstrain.solr.client.SearchSpec.SCOPE_BROAD;
import static com.firstrain.solr.client.SearchSpec.SCOPE_MEDIUM;
import static com.firstrain.solr.client.SearchSpec.SCOPE_NARROW;
import static com.firstrain.solr.client.SearchTokenEntry.Relation.DEFAULT;
import static java.util.Calendar.JANUARY;
import static java.util.Collections.nCopies;
import static java.util.Collections.singletonList;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
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
import static org.powermock.reflect.Whitebox.setInternalState;

@RunWith(PowerMockRunner.class)
public class EntityBaseServiceImplPartOneTest extends EntityBaseServiceImplTestSetup {

    @Spy
    private final ConvertUtil convertUtil = new ConvertUtil();
    @Spy
    private final TwitterService twitterService = new TwitterServiceImpl();

    @Test
    public void givenEmptyDocEntriesWhenGetDocDetailsThenReturnNull() 
            throws Exception {
        // Arrange
        final List<Long> docIds = prepareLongIds();
        final List<String> toExcludeIds = prepareStringIds();
        // Act
        final DocumentSet documentSet = serviceImpl.getDocDetails(docIds, spec, toExcludeIds, CLASSIFICATION_ID);
        // Assert
        assertNull(documentSet);
    }

    @Test
    public void givenDocumentsOfSameSizeWhenGetDocDetailsThenThrowException() throws Exception {
        // Arrange
        final List<Long> docIds = prepareLongIds();
        final List<String> toExcludeIds = prepareStringIds();
        when(searcher.fetch(FR_ArrayUtils.collectionToLongArray(docIds), spec.language, CLASSIFICATION_ID))
                .thenThrow(new IllegalArgumentException());
        // Assert
        expectedException.expect(IllegalArgumentException.class);
        // Act
        serviceImpl.getDocDetails(docIds, spec, toExcludeIds, CLASSIFICATION_ID);
    }

    @Test
    public void givenGetDocDetailsWhenGetExceptionInFoundThenReturnNull() throws Exception {
        // Arrange
        final List<Long> docIds = prepareLongIds();
        final List<String> toExcludeIds = prepareStringIds();
        final List<DocEntry> docEntryList = prepareDocEntries();
        when(searcher.fetch(FR_ArrayUtils.collectionToLongArray(docIds), spec.language, CLASSIFICATION_ID))
                .thenReturn(docEntryList);
        favIconServer = mock(SolrServer.class);
        when(entityBaseServiceRepository.getFavIconServer()).thenReturn(favIconServer);
        docImageServer = mock(SolrServer.class);
        when(entityBaseServiceRepository.getDocImageServer()).thenReturn(docImageServer);
        mockStatic(DocEntriesUpdator.class);
        // Act
        final DocumentSet documentSet =
                serviceImpl.getDocDetails(docIds, spec, toExcludeIds, CLASSIFICATION_ID);
        // Assert
        verifyStatic();
        DocEntriesUpdator.attachFavIconNDocImageDetails(favIconServer, docImageServer,
                docEntryList, true, false);
        final ArgumentCaptor<QuoteEntry> quoteEntryCaptor =
                ArgumentCaptor.forClass(QuoteEntry.class);
        verify(servicesAPIUtil, times(3)).highlightQuote(quoteEntryCaptor.capture());
        final QuoteEntry quoteEntry = quoteEntryCaptor.getValue();
        errorCollector.checkThat(quoteEntry.getText(), equalTo(TEXT_STR));
        errorCollector.checkThat(quoteEntry.getPerson(), equalTo(PERSON_STR));
        errorCollector.checkThat(quoteEntry.getRelevance(), equalTo((short) 1));
        errorCollector.checkThat(quoteEntry.getType(), equalTo(QuoteType.TYPE_NC));
        errorCollector.checkThat(documentSet.getDocuments().size(), equalTo(3));
        errorCollector.checkThat(documentSet.getDocuments().get(0).getContentType(),
                equalTo(FILINGS_10K));
        errorCollector.checkThat(documentSet.getDocuments().get(1).getContentType(),
                equalTo(FILINGS_10K));
        errorCollector.checkThat(documentSet.getDocuments().get(2).getContentType(),
                equalTo(FILINGS_10K));
    }

    @Test
    public void givenTweetsFoundWhenGetTweetDetailsThenReturnTweetSet() throws Exception {
        // Arrange
        final List<Long> tweetIds = prepareLongIds();
        arrangeTwitterService();
        spec.attachGroupInfo = true;
        mockStatic(TitleUtils.class);
        when(TitleUtils.getTweetTitleWithoutLink(null, null, null, false))
                .thenReturn(TEST_TWEET_TITLE);
        final SolrServer twitterServer = mock(SolrServer.class);
        when(entityBaseServiceRepository.getTwitterServer()).thenReturn(twitterServer);
        final SolrDocument solrDocument = new SolrDocument();
        solrDocument.addField("tweetId", LONG_ID_1);
        solrDocument.addField("screenName", TEST_SCREEN_NAME);
        solrDocument.addField("userImage", TEST_IMAGE_NAME);
        testDate = new Date(119, JANUARY, 1);
        solrDocument.addField("tweetCreationDate", testDate);
        final SolrDocumentList solrDocumentList = new SolrDocumentList();
        solrDocumentList.add(solrDocument);
        mockStatic(SolrServerReader.class);
        final String[] twitterFields =
                {"tweetId", "source", "tweet", "coreTweet", "groupId", "groupLead", "links",
                        "expandedLinks", "companyId", "topicIdCoreTweet",
                        "tweetCreationDate", "name", "screenName", "userImage",
                        "description", "tweetClass", "tweetScore", "affinityScore",
                        "categories"};
        when(SolrServerReader.retrieveNSolrDocs(twitterServer, "tweetId:(123 124)",
                0, 2, twitterFields))
                .thenReturn(solrDocumentList);
        when(SolrServerReader.retrieveNSolrDocs(twitterServer, "groupId:(0) AND -tweetId:(123)",
                0, 10, "userTweetScore", false, "screenName", "userImage"))
                .thenReturn(solrDocumentList);
        // Act
        final TweetSet actual = serviceImpl.getTweetDetails(tweetIds, spec);
        // Assert
        final List<Tweet> tweetList = actual.getTweets();
        errorCollector.checkThat(tweetList.size(), equalTo(1));
        errorCollector.checkThat(tweetList.get(0).getTitle(), equalTo(TEST_TWEET_TITLE));
        final List<Tweet> tweetUserList = tweetList.get(0).getTweetUsers();
        errorCollector.checkThat(tweetUserList.size(), equalTo(1));
        final Tweet tweetUser = tweetUserList.get(0);
        errorCollector.checkThat(tweetUser.getScreenName(), equalTo(TEST_SCREEN_NAME));
        errorCollector.checkThat(tweetUser.getUserImage(), equalTo(TEST_IMAGE_NAME));
    }

    @Test
    public void givenNullTweetIdsWhenGetTweetDetailsThenIllegalArgumentException() throws Exception {
        // Arrange
        arrangeTwitterService();
        // Assert
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Supplied tweetIds is null/empty");
        // Act
        serviceImpl.getTweetDetails(null, spec);
    }

    @Test
    public void givenTweetsFoundWhenGetTweetListThenNonEmptyTweetSet() throws Exception {
        // Arrange
        arrangeTwitterService();
        final List<String> catIdsList = prepareStringIds();
        String[] catIdsArr = new String[catIdsList.size()];
        catIdsArr = catIdsList.toArray(catIdsArr);
        mockStatic(TitleUtils.class);
        when(TitleUtils.getTweetTitleWithoutLink(null, null, null, false))
                .thenReturn(TEST_TWEET_TITLE);
        twitterSpec.setScope(1);
        // Act
        final TweetSet actual = serviceImpl.getTweetList(catIdsArr, twitterSpec);
        // Assert
        errorCollector.checkThat(twitterSpec.getStart(), equalTo((short) 0));
        final String[] catIds = twitterSpec.getCatIds();
        errorCollector.checkThat(catIds.length, equalTo(2));
        errorCollector.checkThat(catIds[0], equalTo("301024"));
        errorCollector.checkThat(catIds[1], equalTo("301024"));
        errorCollector.checkThat(actual.isHasMore(), equalTo(false));
        errorCollector.checkThat(actual.getScope(), equalTo(1));
    }

    @Test
    public void givenEmptyCatIdsWhenGetTweetListThenReturnNull() throws Exception {
        // Act
        final TweetSet actual = serviceImpl.getTweetList(new String[0], twitterSpec);
        // Assert
        assertNull(actual);
    }

    @Test
    public void givenExceptionInBodyWhenGetTweetListThenThrowException() throws Exception {
        // Arrange
        final List<String> catIdsList = prepareStringIds();
        String[] catIdsArr = new String[catIdsList.size()];
        catIdsArr = catIdsList.toArray(catIdsArr);
        when(entityBaseServiceRepository.getEntityInfoCache()).thenThrow(new IllegalArgumentException());
        // Assert
        expectedException.expect(IllegalArgumentException.class);
        // Act
        serviceImpl.getTweetList(catIdsArr, twitterSpec);
    }

    @Test
    public void givenQueryArrayProvidedWhenGetHighlightsResultsThenReturnHighLightRows() throws Exception {
        // Arrange
        final String[] qArr = {QUERY_STR};
        final int[] scopeArr = {1, 2, 3};
        arrangeFavIconAndDocImageServers();
        // Act & Assert
        actAndAssertGetHighlightsResultsCommon(qArr, scopeArr);
    }

    @Test
    public void givenQueryArrayProvidedWhenGetHighlightsResultsThenReturnHighLightRows111()
            throws Exception {
        // Arrange
        final String[] qArr = prepareQueryArrayOfSize();
        final int[] scopeArr = prepareScopArrayOfSize();
        arrangeFavIconAndDocImageServers();
        // Act & Assert
        actAndAssertGetHighlightsResultsCommon(qArr, scopeArr);
    }

    @Test
    public void givenDocumentsFoundWhenGetWebResultsForSearchThenDocumentSet() throws Exception {
        testGetWebResultsForSearchDocumentsFoundCase(SCOPE_MEDIUM, SCOPE_MEDIUM);
    }

    @Test
    public void givenDocumentsFoundAndScopeAutoWhenGetWebResultsForSearchThenDocumentSet() throws Exception {
        testGetWebResultsForSearchDocumentsFoundCase(SCOPE_AUTO, SCOPE_BROAD);
    }

    @Test
    public void givenGetWebResultsForSearchWhenEmptyDirectivesAndNullRunableThenException() throws Exception {
        // Arrange
        final BlendDunsInput bdi = prepareBlendDuns();
        final BaseSpec baseSpec = prepareBaseSpec();
        when(taskExecutor.getThreadPoolExecutor()).thenReturn(executor);
        // Assert
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(UNABLE_TO_SUMBMIT_CALLABLE);
        // Act
        serviceImpl.getWebResultsForSearch(QUERY_STR, FQUERY_STR, baseSpec, bdi);
    }

    @Test
    public void givenSearchAndExcludeSourceIdsSSVWhenGetSearchResultThenSearchResultNotNull() throws Exception {
        testGetSearchResultsResultsFoundCase(YesNoEnum.NO);
    }

    @Test
    public void givenSearchAndIncludeSourceIdsSSVWhenGetSearchResultThenSearchResultNotNull() throws Exception {
        testGetSearchResultsResultsFoundCase(YesNoEnum.YES);
    }

    @Test
    public void givenGetSearchResultWhenLastDayParseExceptionThenThrowException() throws Exception {
        // Arrange
        final String[] qMulti = prepareQueryArrayOfSize();
        final int[] scopeMulti = prepareScopArrayOfSize();
        final BaseSpec baseSpec = prepareBaseSpec();
        baseSpec.setLastDay(BUCKET_NAME);
        // Assert
        expectedException.expect(ParseException.class);
        expectedException.expectMessage("Unparseable date: \"bucket_name\"");
        // Act
        serviceImpl.getSearchResult(qMulti, scopeMulti, FQUERY_STR, baseSpec, INTEGER_VALUE_TWO);
    }

    @Test
    public void givenGetSearchResultWhenSearcherExceptionThenThrowException() throws Exception {
        // Arrange
        final String[] qMulti = prepareQueryArrayOfSize();
        final int[] scopeMulti = prepareScopArrayOfSize();
        final BaseSpec baseSpec = prepareBaseSpec();
        when(searcher.search(any(SearchSpec.class))).thenThrow(new SearchException());
        // Assert
        expectedException.expect(SearchException.class);
        // Act
        serviceImpl.getSearchResult(qMulti, scopeMulti, FQUERY_STR, baseSpec, INTEGER_VALUE_TWO);
    }

    @Test
    public void givenGetSearchResultWhenExceptionInBodyThenThrowException() throws Exception {
        // Arrange
        final String[] qMulti = prepareQueryArrayOfSize();
        final int[] scopeMulti = prepareScopArrayOfSize();
        final BaseSpec entityDetailSpec = prepareBaseSpec();
        when(entityBaseServiceRepository.getSearcher()).thenThrow(new IllegalArgumentException());
        // Assert
        expectedException.expect(IllegalArgumentException.class);
        // Act
        serviceImpl.getSearchResult(qMulti, scopeMulti, FQUERY_STR, entityDetailSpec, INTEGER_VALUE_TWO);
    }

    @Test
    public void givenGetWebResultsWhenSearchResultsWithBucketThenGetDocument() throws Exception {
        // Arrange
        final BaseSpec baseSpec = prepareBaseSpec();
        searchResult.buckets = prepareSingleBuckets();
        searchResult.bucketType = SearchTokenEntry.SEARCH_TOKEN_UNKNOWN;
        searchResult.searchSpec = new SearchSpec();
        searchResult.total = 2;
        arrangeFavIconAndDocImageServers();
        // Act
        final DocumentSet actual = serviceImpl.getWebResults(searchResult, baseSpec);
        // Assert
        errorCollector.checkThat(actual.getScope(), equalTo(INTEGER_VALUE_TWO));
        errorCollector.checkThat(actual.isHasMore(), equalTo(false));
        final List<Document> documentList = actual.getDocuments();
        errorCollector.checkThat(documentList.size(), equalTo(1));
        errorCollector.checkThat(documentList.get(0).getContentType(), equalTo(WEBNEWS));
        errorCollector.checkThat(actual.isFiling(), equalTo(false));
        errorCollector.checkThat(actual.getTotalCount(), equalTo(2));
        final ArgumentCaptor<List<DocEntry>> docEntryList = createDocEntryListCaptor();
        verifyStatic();
        DocEntriesUpdator.attachFavIconNDocImageDetails(eq(favIconServer), eq(docImageServer),
                docEntryList.capture(), eq(true), eq(false));
    }

    @Test
    public void givenimilarityUtilAndSearchResultsWithBucketWhenGetWebResultsThenGetDocument() throws Exception {
        // Arrange
        final BaseSpec baseSpec = prepareBaseSpec();
        searchResult.buckets = prepareSingleBuckets();
        searchResult.bucketType = SearchTokenEntry.SEARCH_TOKEN_UNKNOWN;
        searchResult.searchSpec = new SearchSpec();
        when(entityBaseServiceRepository.getDocumentSimilarityUtil()).thenReturn(documentSimilarityUtil);
        // Act
        serviceImpl.getWebResults(searchResult, baseSpec);
        // Assert
        verify(documentSimilarityUtil).clear();
    }

    @Test
    public void givenGetWebResultsWhenGotExceptionThenThrowException() throws Exception {
        // Arrange
        final BaseSpec baseSpec = prepareBaseSpec();
        searchResult.buckets = prepareSingleBuckets();
        searchResult.bucketType = SearchTokenEntry.SEARCH_TOKEN_UNKNOWN;
        searchResult.searchSpec = new SearchSpec();
        when(entityBaseServiceRepository.getFavIconServer()).thenThrow(new IllegalArgumentException());
        // Assert
        expectedException.expect(IllegalArgumentException.class);
        // Act
        serviceImpl.getWebResults(searchResult, baseSpec);
    }

    @Test
    public void givenGetSearchResultForAnalystCommentsWhenValidInputsThenGetSearch() throws Exception {
        // Arrange
        final BaseSpec baseSpec = prepareBaseSpec();
        baseSpec.setDaysCount(10);
        baseSpec.setExcludeArticleIdsSSV(TEXT_STR);
        baseSpec.setExcludeArticleIdsSSV(TEST_EXCLUDE_ARTICLE_IDS_SSV);
        // Act
        final SearchResult actual = serviceImpl.getSearchResultForAnalystComments(TEXT_STR, baseSpec);
        // Assert
        errorCollector.checkThat(searchResult, equalTo(actual));
        final ArgumentCaptor<SearchSpec> searchSpecCaptor = ArgumentCaptor.forClass(SearchSpec.class);
        verify(searcher).search(searchSpecCaptor.capture());
        final SearchSpec searchSpec = searchSpecCaptor.getValue();
        errorCollector.checkThat(searchSpec.needHighlighting, equalTo(false));
        errorCollector.checkThat(searchSpec.needHotListAll, equalTo(false));
        errorCollector.checkThat(searchSpec.needSearchSuggestion, equalTo(false));
        errorCollector.checkThat(searchSpec.useLikelySearchIntention, equalTo(false));
        errorCollector.checkThat(searchSpec.getOrder(), equalTo(ORDER_DATE));
        errorCollector.checkThat(searchSpec.start, equalTo(1));
        errorCollector.checkThat(searchSpec.getRows(), equalTo(1));
        errorCollector.checkThat(searchSpec.days, equalTo(10));
        errorCollector.checkThat(searchSpec.scope, equalTo(SCOPE_NARROW));
        errorCollector.checkThat(searchSpec.getQ(), equalTo(TEXT_STR));
        errorCollector.checkThat(searchSpec.getFq(), equalTo("T:AnalystComments OR T:AnalystRatings"));
        errorCollector.checkThat(searchSpec.getExcludeDocIds(),
                equalTo(TEST_EXCLUDE_ARTICLE_IDS_SSV));
    }

    @Test
    public void givengetSearchResultForAnalystCommentsWhenSearcherThrowExceptionThenThrowException() throws Exception {
        // Arrange
        final BaseSpec baseSpec = prepareBaseSpec();
        baseSpec.setExcludeArticleIdsSSV(TEXT_STR);
        when(searcher.search(any(SearchSpec.class))).thenThrow(new SearchException());
        // Assert
        expectedException.expect(SearchException.class);
        // Act
        serviceImpl.getSearchResultForAnalystComments(TEXT_STR, baseSpec);
    }

    @Test
    public void givenGotMgmtFromSolrWhenGetEventSetForMTEventsThenGetEntityFromSolr() throws Exception {
        // Arrange
        final BaseSpec baseSpec = prepareBaseSpec();
        baseSpec.setDaysCount(10);
        final List<Integer> companyIds = new ArrayList<>();
        final SolrDocumentList sdList = new SolrDocumentList();
        sdList.setNumFound(INTEGER_VALUE_TWO);
        sdList.add(new SolrDocument());
        final MgmtTurnoverServiceSpec mgmtSpec = mock(MgmtTurnoverServiceSpec.class);
        when(servicesAPIUtil.getMgmtSpec(1, 10, 1)).thenReturn(mgmtSpec);
        when(eventService.getMgmtFromSolr(eq(companyIds), any(MgmtTurnoverServiceSpec.class), eq(true),
                any(EventQueryCriteria.EventTypeRange.class), eq(baseSpec))).thenReturn(sdList);
        // Act
        final EventSet actual = serviceImpl.getEventSetForMTEvents(companyIds, baseSpec);
        // Assert
        final List<Event> eventList = actual.getEvents();
        errorCollector.checkThat(eventList.size(), equalTo(1));
        final Event event = eventList.get(0);
        errorCollector.checkThat(event.getEventId(), equalTo("-1"));
        errorCollector.checkThat(event.getEventInformationEnum(), equalTo(MT_HIRE));
        errorCollector.checkThat(actual.getTotalCount(), equalTo(2));
        errorCollector.checkThat(actual.getUnfilteredDataSize(), equalTo(1));
        errorCollector.checkThat(actual.isHasMore(), equalTo(false));
        final ArgumentCaptor<EventQueryCriteria.EventTypeRange> rangeCaptor =
                ArgumentCaptor.forClass(EventQueryCriteria.EventTypeRange.class);
        verify(eventService).getMgmtFromSolr(eq(companyIds), eq(mgmtSpec), eq(true),
                rangeCaptor.capture(), eq(baseSpec));
        final EventQueryCriteria.EventTypeRange range = rangeCaptor.getValue();
        errorCollector.checkThat(range.getStartEventType(), equalTo(1));
        errorCollector.checkThat(range.getEndEventType(), equalTo(299));
    }

    @Test
    public void givenSolrServerExceptionWhenGetEventSetForMTEventsThenThrowException() throws Exception {
        // Arrange
        final BaseSpec baseSpec = prepareBaseSpec();
        baseSpec.setDaysCount(10);
        final List<Integer> companyIds = new ArrayList<>();
        final MgmtTurnoverServiceSpec mgmtSpec = mock(MgmtTurnoverServiceSpec.class);
        when(servicesAPIUtil.getMgmtSpec(1, 10, 1)).thenReturn(mgmtSpec);
        when(eventService.getMgmtFromSolr(eq(companyIds), any(MgmtTurnoverServiceSpec.class), eq(true),
                any(EventQueryCriteria.EventTypeRange.class), eq(baseSpec)))
                .thenThrow(new SolrServerException(TEST_EXCEPTION_MESSAGE));
        // Assert
        expectedException.expect(SolrServerException.class);
        expectedException.expectMessage(TEST_EXCEPTION_MESSAGE);
        // Act
        serviceImpl.getEventSetForMTEvents(companyIds, baseSpec);
    }

    @Test
    public void givenGotMgmtFromSolrAndBaseSpecCustomizedWhenGetEventSetForMTEventsThenGetEntityFromSolr() throws Exception {
        // Arrange
        serviceImpl = spy(serviceImpl);
        final BaseSpec baseSpec = prepareBaseSpec();
        baseSpec.setDaysCount(10);
        final List<Integer> companyIds = new ArrayList<>(nCopies(1001, INTEGER_VALUE_ONE));
        final SolrDocumentList sdList = new SolrDocumentList();
        sdList.setNumFound(INTEGER_VALUE_TWO);
        sdList.add(new SolrDocument());
        final MgmtTurnoverServiceSpec mgmtSpec = mock(MgmtTurnoverServiceSpec.class);
        when(servicesAPIUtil.getMgmtSpec(1, 10, 1)).thenReturn(mgmtSpec);
        when(taskExecutor.getThreadPoolExecutor()).thenReturn(executor);
        serviceImpl.executorTimeout = 1000L;
        baseSpec.setCustomized(true);
        final FRCompletionService<SolrDocumentList> completionService =
                (FRCompletionService<SolrDocumentList>) mock(FRCompletionService.class);
        whenNew(FRCompletionService.class)
                .withArguments(executor)
                .thenReturn(completionService);
        final Future<SolrDocumentList> solrDocumentListFuture = (Future<SolrDocumentList>) mock(Future.class);
        when(solrDocumentListFuture.get()).thenReturn(sdList);
        when(completionService.poll(1000L, MILLISECONDS)).thenReturn(solrDocumentListFuture);
        PowerMockito.doNothing()
                .when(serviceImpl, "makeSolrCallParallel",
                        eq(baseSpec), any(EventQueryCriteria.EventTypeRange.class),
                        eq(mgmtSpec), anyList(), eq(completionService));
        // Act
        final EventSet actual = serviceImpl.getEventSetForMTEvents(companyIds, baseSpec);
        // Assert
        final List<Event> eventList = actual.getEvents();
        errorCollector.checkThat(eventList.size(), equalTo(1));
        final Event event = eventList.get(0);
        errorCollector.checkThat(event.getEventId(), equalTo("-1"));
        errorCollector.checkThat(event.getEventInformationEnum(), equalTo(MT_HIRE));
        errorCollector.checkThat(actual.getTotalCount(), equalTo(1));
        errorCollector.checkThat(actual.getUnfilteredDataSize(), equalTo(2));
        errorCollector.checkThat(actual.isHasMore(), equalTo(false));
        final ArgumentCaptor<EventQueryCriteria.EventTypeRange> rangeCaptor =
                ArgumentCaptor.forClass(EventQueryCriteria.EventTypeRange.class);
        final ArgumentCaptor<List<Integer>> partionCaptor = createIntListCaptor();
        verifyPrivate(serviceImpl, times(2))
                .invoke("makeSolrCallParallel",
                        eq(baseSpec), rangeCaptor.capture(), eq(mgmtSpec), partionCaptor.capture(),
                        eq(completionService));
        final EventQueryCriteria.EventTypeRange range = rangeCaptor.getValue();
        errorCollector.checkThat(range.getStartEventType(), equalTo(1));
        errorCollector.checkThat(range.getEndEventType(), equalTo(299));
        final List<List<Integer>> partitionList = partionCaptor.getAllValues();
        errorCollector.checkThat(partitionList.get(0).size(), equalTo(1000));
        errorCollector.checkThat(partitionList.get(1).size(), equalTo(1));
    }

    @Test
    public void givenEntityTypeIsTagCompanyWhenGetWebVolumeGraphThenGetGraph() throws Exception {
        // Arrange
        serviceImpl = spy(serviceImpl);
        final BaseSpec baseSpec = prepareBaseSpec();
        baseSpec.setDaysCount(10);
        when(entityInfo.getSearchToken()).thenReturn(TEST_SEARCH_TOKEN);
        // Act
        final Graph graph = serviceImpl.getWebVolumeGraph(CAT_ID_STR, companyIdsArr, tCatIds, baseSpec, CALL_PREP,
                INTEGER_VALUE_TWO, SCOPE_NARROW, eventTypes, false);
        // Assert
        errorCollector.checkThat(graph.getCurrentRange(), equalTo(NARROW));
        errorCollector.checkThat(graph.getGraphFor(), equalTo(CALL_PREP));
        final Date startDate = graph.getsDate();
        final Date endDate = graph.geteDate();
        final int rangeInYears = getDifferenceDays(startDate, endDate);
        errorCollector.checkThat(rangeInYears, equalTo(10));
        verify(servicesAPIUtil).mapHistoricalStatsToTriggers(graph, null);
        final ArgumentCaptor<GraphQueryCriteria> queryCriteriaCaptor =
                ArgumentCaptor.forClass(GraphQueryCriteria.class);
        final ArgumentCaptor<List<CompanyVolume>> companyVolInfoCaptor = createCompanyVolListCaptor();
        verify(serviceImpl).generateGraphObject(queryCriteriaCaptor.capture(), companyVolInfoCaptor.capture(),
                eq(3));
        final GraphQueryCriteria queryCriteria = queryCriteriaCaptor.getValue();
        final DateRange dateRange = queryCriteria.getDateRange();
        errorCollector.checkThat(dateRange.getDays(), equalTo(10));
        errorCollector.checkThat(queryCriteria.getScope(), equalTo(SCOPE_NARROW));
        final List<SearchTokenEntry> searchTokenEntryList = queryCriteria.getSearchTokens();
        errorCollector.checkThat(searchTokenEntryList.size(), equalTo(1));
        final SearchTokenEntry searchTokenEntry = searchTokenEntryList.get(0);
        errorCollector.checkThat(searchTokenEntry.getSearchToken(), equalTo(TEST_SEARCH_TOKEN));
        errorCollector.checkThat(searchTokenEntry.relation, equalTo(DEFAULT));
        errorCollector.checkThat(queryCriteria.getCompanyId(), equalTo(0));
        errorCollector.checkThat(queryCriteria.getNumberOfDays(), equalTo(10));
    }

    @Test
    public void givenEntityTypeIsTagIndustryWhenGetWebVolumeGraphThenGetGraph() throws Exception {
        // Arrange
        serviceImpl = spy(serviceImpl);
        final BaseSpec baseSpec = prepareBaseSpec();
        baseSpec.setDaysCount(10);
        when(entityInfo.getType()).thenReturn(SearchTokenEntry.SEARCH_TOKEN_TAG_INDUSTRY);
        // Act
        final Graph graph = serviceImpl.getWebVolumeGraph(CAT_ID_STR, companyIdsArr, tCatIds, baseSpec, CALL_PREP,
                INTEGER_VALUE_TWO, SCOPE_MEDIUM, eventTypes, false);
        // Assert
        errorCollector.checkThat(graph.getCurrentRange(), equalTo(MEDIUM));
        errorCollector.checkThat(graph.getGraphFor(), equalTo(CALL_PREP));
        final Date startDate = graph.getsDate();
        final Date endDate = graph.geteDate();
        final int rangeInYears = getDifferenceDays(startDate, endDate);
        errorCollector.checkThat(rangeInYears, equalTo(10));
        verify(servicesAPIUtil).mapHistoricalStatsToTriggers(graph, null);
        final ArgumentCaptor<GraphQueryCriteria> queryCriteriaCaptor =
                ArgumentCaptor.forClass(GraphQueryCriteria.class);
        final ArgumentCaptor<List<CompanyVolume>> companyVolInfoCaptor = createCompanyVolListCaptor();
        verify(serviceImpl).generateGraphObject(queryCriteriaCaptor.capture(), companyVolInfoCaptor.capture(),
                eq(3));
        final GraphQueryCriteria queryCriteria = queryCriteriaCaptor.getValue();
        final DateRange dateRange = queryCriteria.getDateRange();
        errorCollector.checkThat(dateRange.getDays(), equalTo(10));
        errorCollector.checkThat(queryCriteria.getScope(), equalTo(SCOPE_MEDIUM));
        final List<SearchTokenEntry> searchTokenEntryList = queryCriteria.getSearchTokens();
        errorCollector.checkThat(searchTokenEntryList.size(), equalTo(1));
        final SearchTokenEntry searchTokenEntry = searchTokenEntryList.get(0);
        errorCollector.checkThat(searchTokenEntry.getSearchToken(), equalTo(TEXT_STR));
        errorCollector.checkThat(searchTokenEntry.relation, equalTo(DEFAULT));
        errorCollector.checkThat(queryCriteria.getCompanyId(), equalTo(0));
        errorCollector.checkThat(queryCriteria.getNumberOfDays(), equalTo(10));
    }

    @Test
    public void givenDateRangeParseExceptionWhenGetWebVolumeGraphThenGet() throws Exception {
        // Arrange
        serviceImpl = spy(serviceImpl);
        final BaseSpec baseSpec = prepareBaseSpec();
        baseSpec.setDaysCount(10);
        mockStatic(SolrSearcher.class);
        final TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");
        when(SolrSearcher.getTimeZone()).thenReturn(utcTimeZone);
        when(entityInfo.getType()).thenReturn(SearchTokenEntry.SEARCH_TOKEN_TAG_INDUSTRY);
        final DateRange dateRange = new DateRange(10, utcTimeZone);
        whenNew(DateRange.class).withArguments(10, utcTimeZone)
                .thenAnswer(new Answer<DateRange>() {
                    @Override
                    public DateRange answer(InvocationOnMock invocationOnMock) {
                        return dateRange;
                    }
                });
        whenNew(DateRange.class)
                .withArguments(anyString(), eq(DEFAULT_DATE_FORMAT), eq(10), eq(utcTimeZone))
                .thenThrow(new ParseException(TEST_EXCEPTION_MESSAGE, 0));
        final CompanyVolume companyVolume = new CompanyVolume();
        final List<CompanyVolume> companyVolInfo = singletonList(companyVolume);
        doReturn(companyVolInfo)
                .when(serviceImpl).getWebVolumeInfoFromSolr(any(GraphQueryCriteria.class));
        final Graph expectedGraphData = new Graph();
        doReturn(expectedGraphData)
                .when(serviceImpl)
                .generateGraphObject(any(GraphQueryCriteria.class), eq(companyVolInfo),
                        eq(3));
        // Act
        final Graph graph = serviceImpl.getWebVolumeGraph(CAT_ID_STR, companyIdsArr, tCatIds, baseSpec, CALL_PREP,
                INTEGER_VALUE_TWO, SCOPE_MEDIUM, eventTypes, false);
        // Assert
        errorCollector.checkThat(graph, equalTo(expectedGraphData));
        errorCollector.checkThat(graph.getGraphFor(), equalTo(CALL_PREP));
        final ArgumentCaptor<GraphQueryCriteria> graphQueryCriteriaCaptor =
                ArgumentCaptor.forClass(GraphQueryCriteria.class);
        verify(serviceImpl).getWebVolumeInfoFromSolr(graphQueryCriteriaCaptor.capture());
        final GraphQueryCriteria graphQueryCriteria = graphQueryCriteriaCaptor.getValue();
        errorCollector.checkThat(graphQueryCriteria.getDateRange(), equalTo(dateRange));
        errorCollector.checkThat(graphQueryCriteria.getScope(), equalTo(SCOPE_MEDIUM));
        verify(serviceImpl).generateGraphObject(graphQueryCriteria, companyVolInfo, 3);
    }

    @Test
    public void givenEntityTypeIsTagSectorTopicWhenGetWebVolumeGraphThenGetGraph() throws Exception {
        // Arrange
        serviceImpl = spy(serviceImpl);
        final BaseSpec baseSpec = prepareBaseSpec();
        baseSpec.setDaysCount(10);
        when(entityInfo.getType()).thenReturn(SearchTokenEntry.SEARCH_TOKEN_TAG_SECTOR_TOPIC);
        // Act
        final Graph graph = serviceImpl.getWebVolumeGraph(CAT_ID_STR, companyIdsArr, tCatIds, baseSpec, CALL_PREP,
                INTEGER_VALUE_TWO, SCOPE_BROAD, eventTypes, false);
        // Assert
        errorCollector.checkThat(graph.getCurrentRange(), equalTo(BROAD));
        errorCollector.checkThat(graph.getGraphFor(), equalTo(CALL_PREP));
        final Date startDate = graph.getsDate();
        final Date endDate = graph.geteDate();
        final int rangeInYears = getDifferenceDays(startDate, endDate);
        errorCollector.checkThat(rangeInYears, equalTo(10));
        verify(servicesAPIUtil).mapHistoricalStatsToTriggers(graph, null);
        final ArgumentCaptor<GraphQueryCriteria> queryCriteriaCaptor =
                ArgumentCaptor.forClass(GraphQueryCriteria.class);
        final ArgumentCaptor<List<CompanyVolume>> companyVolInfoCaptor = createCompanyVolListCaptor();
        verify(serviceImpl).generateGraphObject(queryCriteriaCaptor.capture(), companyVolInfoCaptor.capture(),
                eq(3));
        final GraphQueryCriteria queryCriteria = queryCriteriaCaptor.getValue();
        final DateRange dateRange = queryCriteria.getDateRange();
        errorCollector.checkThat(dateRange.getDays(), equalTo(10));
        errorCollector.checkThat(queryCriteria.getScope(), equalTo(SCOPE_BROAD));
        final List<SearchTokenEntry> searchTokenEntryList = queryCriteria.getSearchTokens();
        errorCollector.checkThat(searchTokenEntryList.size(), equalTo(1));
        final SearchTokenEntry searchTokenEntry = searchTokenEntryList.get(0);
        errorCollector.checkThat(searchTokenEntry.getSearchToken(), equalTo(TEXT_STR));
        errorCollector.checkThat(searchTokenEntry.relation, equalTo(DEFAULT));
        errorCollector.checkThat(queryCriteria.getCompanyId(), equalTo(0));
        errorCollector.checkThat(queryCriteria.getNumberOfDays(), equalTo(10));
    }

    @Test
    public void givenEntityTypeIsTagPeopleWhenGetWebVolumeGraphThenReturnNull() throws Exception {
        // Arrange
        serviceImpl = spy(serviceImpl);
        final BaseSpec baseSpec = prepareBaseSpec();
        baseSpec.setDaysCount(10);
        when(entityInfo.getType()).thenReturn(SearchTokenEntry.SEARCH_TOKEN_TAG_PEOPLE);
        // Act
        final Graph graph = serviceImpl.getWebVolumeGraph(CAT_ID_STR, companyIdsArr, tCatIds, baseSpec, CALL_PREP,
                INTEGER_VALUE_TWO, SCOPE_BROAD, eventTypes, false);
        // Assert
        assertNull(graph);
    }

    @Test
    public void givenSearchExceptionWhenGetWebVolumeGraphThenThrowException() throws Exception {
        // Arrange
        final BaseSpec baseSpec = prepareBaseSpec();
        Mockito.when(entityBaseServiceRepository.getEntityInfoCache())
                .thenThrow(SearchException.class);
        // Assert
        expectedException.expect(SearchException.class);
        // Act
        serviceImpl.getWebVolumeGraph(CAT_ID_STR, companyIdsArr, tCatIds, baseSpec, CALL_PREP,
                INTEGER_VALUE_TWO, SCOPE_BROAD, eventTypes, false);
    }

    @Test
    public void givenNDaysFromTodayIsLesserThenZeroAndEntityTypeIsTagCompanyWhenGetWebVolumeGraphThenGetGraph()
            throws Exception {
        // Arrange
        serviceImpl = spy(serviceImpl);
        final BaseSpec baseSpec = prepareBaseSpec();
        baseSpec.setDaysCount(10);
        when(entityInfo.getType()).thenReturn(SearchTokenEntry.SEARCH_TOKEN_TAG_COMPANY);
        // Act
        final Graph graph = serviceImpl.getWebVolumeGraph(CAT_ID_STR, companyIdsArr, tCatIds, baseSpec, CALL_PREP,
                0, SCOPE_BROAD, eventTypes, false);
        // Assert
        errorCollector.checkThat(graph.getCurrentRange(), equalTo(BROAD));
        errorCollector.checkThat(graph.getGraphFor(), equalTo(CALL_PREP));
        verify(servicesAPIUtil).mapHistoricalStatsToTriggers(graph, null);
        verifyGetWebVolumeGraphGenerateGraphObject(-1);
    }

    @Test
    public void givenGetWebVolumeGraphWhenNDaysFromTodayIsLesserThenZeroAndEntityTypeIsTagSectorTopicThenGetGraph()
            throws Exception {
        // Arrange
        serviceImpl = spy(serviceImpl);
        final BaseSpec baseSpec = prepareBaseSpec();
        baseSpec.setDaysCount(10);
        when(entityInfo.getType()).thenReturn(SearchTokenEntry.SEARCH_TOKEN_TAG_SECTOR_TOPIC);
        // Act
        final Graph graph = serviceImpl.getWebVolumeGraph(CAT_ID_STR, companyIdsArr, tCatIds, baseSpec, CALL_PREP,
                0, SCOPE_BROAD, eventTypes, false);
        // Assert
        errorCollector.checkThat(graph.getCurrentRange(), equalTo(BROAD));
        errorCollector.checkThat(graph.getGraphFor(), equalTo(CALL_PREP));
        verify(servicesAPIUtil).mapHistoricalStatsToTriggers(graph, null);
        verifyGetWebVolumeGraphGenerateGraphObject(-1);
    }

    @Test
    public void givenGetWebVolumeGraphWhenNDaysFromTodayIsLesserThenZeroAndEntityTypeIsTagIndustryThenGetGraph()
            throws Exception {
        // Arrange
        serviceImpl = spy(serviceImpl);
        final BaseSpec baseSpec = prepareBaseSpec();
        baseSpec.setDaysCount(10);
        baseSpec.setNeedRelatedDoc(true);
        when(entityInfo.getType()).thenReturn(SearchTokenEntry.SEARCH_TOKEN_TAG_INDUSTRY);
        final IEvents eventFromSolr = mock(IEvents.class);
        final List<IEvents> eventsFromSolrList = singletonList(eventFromSolr);
        when(eventService.getEventsFromSolr(any(EventQueryCriteria.class), anyMap()))
                .thenReturn(eventsFromSolrList);
        final IEvents eventBeforeBC = mock(IEvents.class);
        final List<IEvents> graphFilteredEventList = singletonList(eventBeforeBC);
        when(eventService.applyGraphEventFilter(eventsFromSolrList))
                .thenReturn(graphFilteredEventList);
        final IEvents eventAfterBC = mock(IEvents.class);
        final List<IEvents> eventsAfterBCList = singletonList(eventAfterBC);
        when(eventService.applyBC(graphFilteredEventList, false, 300))
                .thenReturn(eventsAfterBCList);
        final IEvents eventAfterBSA = mock(IEvents.class);
        final List<IEvents> eventsAfterBSAList = singletonList(eventAfterBSA);
        when(eventService.applyBSA(eventsAfterBCList, INTEGER_VALUE_ONE, false))
                .thenReturn(eventsAfterBSAList);
        final Event signal = new Event();
        final List<Event> signalList = singletonList(signal);
        doReturn(signalList).when(convertUtil).convertToBaseTypeForGraph(eventsAfterBSAList,
                false);
        doNothing().when(convertUtil).attachPropertiesForExpiredEvents(searcher, signalList);
        doReturn(signalList).when(serviceImpl).getEventSetWithId(signalList);

        // Act
        final Graph graph = serviceImpl.getWebVolumeGraph(CAT_ID_STR, companyIdsArr, tCatIds, baseSpec, CALL_PREP,
                0, SCOPE_BROAD, eventTypes, true);
        // Assert
        errorCollector.checkThat(graph.getCurrentRange(), equalTo(BROAD));
        errorCollector.checkThat(graph.getGraphFor(), equalTo(CALL_PREP));
        verify(servicesAPIUtil).mapHistoricalStatsToTriggers(graph, signalList);
        verifyGetWebVolumeGraphGenerateGraphObject(301024);
        final ArgumentCaptor<EventQueryCriteria> criteriaCaptor =
                ArgumentCaptor.forClass(EventQueryCriteria.class);
        final ArgumentCaptor<Map<Integer, SolrDocument>> eventDocMapCaptor =
                createEventDocMapCaptor();
        verify(eventService).getEventsFromSolr(criteriaCaptor.capture(), eventDocMapCaptor.capture());
        final EventQueryCriteria eventQueryCriteria = criteriaCaptor.getValue();
        errorCollector.checkThat(eventQueryCriteria.getCompanyIds(), equalTo(companyIdsArr));
        errorCollector.checkThat(eventQueryCriteria.getTopicIds(), equalTo(tCatIds));
        errorCollector.checkThat(eventQueryCriteria.getDays(), equalTo(10));
        errorCollector.checkThat(eventDocMapCaptor.getValue().isEmpty(), equalTo(true));
        verify(convertUtil).attachPropertiesForExpiredEvents(searcher, signalList);
        verify(serviceImpl).attachRelatedDocumentDetails(signalList, eventDocMapCaptor.getValue(), baseSpec);
        errorCollector.checkThat(eventQueryCriteria.getStart(), equalTo(1));
        errorCollector.checkThat(eventQueryCriteria.getEventTypeIds().length, equalTo(3));
        errorCollector.checkThat(eventQueryCriteria.getEventTypeIds()[0], equalTo(1));
        errorCollector.checkThat(eventQueryCriteria.getEventTypeIds()[1], equalTo(2));
        errorCollector.checkThat(eventQueryCriteria.getEventTypeIds()[2], equalTo(3));
    }

    private void arrangeTwitterService() {
        setInternalState(twitterService, "entityBaseServiceRepository", entityBaseServiceRepository);
        setInternalState(twitterService, "industryClassificationMap", industryClassificationMap);
        setInternalState(twitterService, "convertUtil", convertUtil);
        setInternalState(serviceImpl, "twitterService", twitterService);
    }
}
