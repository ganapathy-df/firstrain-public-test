package com.firstrain.frapi.service.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyListOf;
import static org.mockito.Mockito.anyList;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.reflect.Whitebox.invokeMethod;
import static org.powermock.reflect.internal.WhiteboxImpl.setInternalState;

import com.firstrain.content.similarity.DocumentSimilarityUtil;
import com.firstrain.frapi.domain.BaseSpec;
import com.firstrain.frapi.domain.SectionSpec;
import com.firstrain.frapi.domain.VisualizationData;
import com.firstrain.frapi.domain.EntityMap;
import com.firstrain.frapi.domain.MgmtTurnoverData;
import com.firstrain.frapi.domain.BlendDunsInput;
import com.firstrain.frapi.pojo.EntityBriefInfo;
import com.firstrain.frapi.pojo.Graph;
import com.firstrain.frapi.pojo.wrapper.BaseSet;
import com.firstrain.frapi.pojo.wrapper.EventSet;
import com.firstrain.frapi.pojo.wrapper.DocumentSet;
import com.firstrain.frapi.pojo.wrapper.TweetSet;
import com.firstrain.frapi.pojo.wrapper.GraphNodeSet;
import com.firstrain.frapi.pojo.wrapper.BaseSet.SectionType;
import com.firstrain.frapi.repository.impl.EntityBaseServiceRepositoryImpl;
import com.firstrain.frapi.service.CompanyService;
import com.firstrain.frapi.service.DnbService;
import com.firstrain.frapi.service.VisualizationService;
import com.firstrain.frapi.util.StatusCode;
import com.firstrain.frapi.util.SearchResultGenerator;
import com.firstrain.frapi.util.ContentType;
import com.firstrain.frapi.util.FRAPIConstant;
import com.firstrain.frapi.util.EntityHandler;
import com.firstrain.frapi.util.DefaultEnums.INPUT_ENTITY_TYPE;
import com.firstrain.obj.IEntityInfo;
import com.firstrain.solr.client.SearchResult;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.solr.common.SolrDocument;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class EntityBriefServiceImplPartTwoTest extends EntityBriefServiceImplTestSetup {

    @Test
    public void givenSearchTokenIsNullWhenGotSolrDocumentsThenReturnNull() throws Exception {
        // Act
        final EntityMap actual = serviceImpl.getEntityMapBySourceSearchToken(null, LANGUAGE);
        // Assert
        errorCollector.checkThat(actual, nullValue());
    }

    @Test
    public void givenLanguageIsNullWhenGotSolrDocumentsThenReturnNull() throws Exception {
        // Act
        final EntityMap actual = serviceImpl.getEntityMapBySourceSearchToken(SEARCH_TOKEN, null);
        // Assert
        errorCollector.checkThat(actual, nullValue());
    }

    @Test
    public void givenBaseSpecWhenGetEventsThenReturnEventSet() throws Exception {
        // Arrange
        final BaseSpec baseSpecLocal = new BaseSpec();
        baseSpecLocal.setNeedBucket(true);
        final CompanyService companyServiceLocal = mock(CompanyService.class);
        setInternalState(serviceImpl, "companyService", companyServiceLocal);
        final EventSet eventSetLocal = new EventSet();
        when(companyServiceLocal.getCompanyEvents(any(BaseSpec.class), anyMap())).thenReturn(eventSetLocal);
        // Act
        final EventSet newEventSet = invokeMethod(serviceImpl, "getEvents", baseSpecLocal);
        // Assert
        errorCollector.checkThat(newEventSet, equalTo(eventSetLocal));
        verify(entityProcessingService).getEventSetWithId(eventSetLocal);
    }

    @Test
    public void givenGetEntityMapBySourceSearchTokenWhenGotSolrDocumentsThenGetMap() throws Exception {
        // Arrange
        final EntityBaseServiceRepositoryImpl baseServiceRepositoryImpl = new EntityBaseServiceRepositoryImpl();
        setInternalState(serviceImpl, "entityBaseServiceRepository", baseServiceRepositoryImpl);
        setInternalState(baseServiceRepositoryImpl, "entityInfoCache", infoCache);
        // Act
        final EntityMap actual = serviceImpl.getEntityMapBySourceSearchToken(SEARCH_TOKEN, LANGUAGE);
        // Assert
        errorCollector.checkThat(actual.getEntity().getId(), equalTo(SEARCH_TOKEN));
        errorCollector.checkThat(actual.getIndustry().getName(), equalTo(NAME));
        errorCollector.checkThat(actual.getIndustry().getSearchToken(), equalTo(TEXT_STR));
        errorCollector.checkThat(actual.getSector().getName(), equalTo(NAME));
        errorCollector.checkThat(actual.getSector().getSearchToken(), equalTo(TEXT_STR));
        errorCollector.checkThat(actual.getSegment().getName(), equalTo(NAME));
        errorCollector.checkThat(actual.getSegment().getSearchToken(), equalTo(TEXT_STR));
        errorCollector.checkThat(actual.getCountry().getName(), equalTo(NAME));
        errorCollector.checkThat(actual.getCountry().getSearchToken(), equalTo(TEXT_STR));
        errorCollector.checkThat(actual.getLanguage().get(FIRST_INDEX), equalTo(LANGUAGE));
    }

    @Test
    public void givenGetEntityPeersWhenNullEntityThenGetStatusNotFound() throws Exception {
        // Act
        final EntityBriefInfo actual = serviceImpl.getEntityPeers(SEARCH_TOKEN, null, true);
        // Assert
        assertEquals(StatusCode.ENTITY_NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void givenGetEntityPeersWhenCompanyTokenAndNullEntityThenStatusNotFound() throws Exception {
        // Arrange
        when(infoCache.searchTokenToEntity(anyString())).thenReturn(null);
        // Act
        final EntityBriefInfo actual = serviceImpl.getEntityPeers(FRAPIConstant.COMPANY_PREFIX +
                SEARCH_TOKEN, null, true);
        // Assert
        assertEquals(StatusCode.ENTITY_NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void givenGetEntityPeersWhenTokensNotFoundThenStatusPeersNotFound() throws Exception {
        // Arrange
        PowerMockito.when(EntityHandler.generateEntity(any(DnbService.class), anyString(), any(BlendDunsInput.class)))
                .thenReturn(entityPojo);
        // Act
        final EntityBriefInfo actual = serviceImpl.getEntityPeers(SEARCH_TOKEN, prepareStringIds(), true);
        // Assert
        assertEquals(StatusCode.PEERS_NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void givenGetEntityPeersWhenOneCompetitorEntityThenGetSuccessStatus() throws Exception {
        // Arrange
        setInternalState(serviceImpl, COMPANY_SERVICE_REPOSITORY_COL, companyServiceRepositoryImpl);
        setInternalState(companyServiceRepositoryImpl, "entityInfoCache", infoCache);
        PowerMockito.when(EntityHandler.generateEntity(any(DnbService.class), anyString(), any(BlendDunsInput.class)))
                .thenReturn(entityPojo);
        // Act
        final EntityBriefInfo actual = serviceImpl.getEntityPeers(SEARCH_TOKEN, prepareStringIds(), true);
        // Assert
        assertEquals(StatusCode.REQUEST_SUCCESS, actual.getStatusCode());
    }

    @Test
    public void givenGetEntityPeersWhenOneCompetitorEntityThenGetSuccessStatus111() throws Exception {
        // Arrange
        final Map<String, List<String>> competitors = new HashMap<String, List<String>>();
        competitors.put(EXECUTED_ID_1, prepareStringIds());
        competitors.put(EXECUTED_ID_2, prepareStringIds());
        when(companyServiceRepository.getBLVsCompetitorsCatIdsFromSolr(anyInt(), anyListOf(String.class)))
                .thenReturn(competitors);
        PowerMockito.when(EntityHandler.generateEntity(any(DnbService.class), anyString(), any(BlendDunsInput.class)))
                .thenReturn(entityPojo);
        // Act
        final EntityBriefInfo actual = serviceImpl.getEntityPeers(SEARCH_TOKEN, prepareStringIds(), true);
        // Assert
        errorCollector.checkThat(actual.getStatusCode(), equalTo(StatusCode.REQUEST_SUCCESS));
        assertForMatchedEntity(actual);
    }

    @Test
    public void givenGetEntityPeersWhenGotCompetitorFromSolrThenGetSuccessStatus() throws Exception {
        // Arrange
        setInternalState(serviceImpl, COMPANY_SERVICE_REPOSITORY_COL, companyServiceRepositoryImpl);
        // Act
        final EntityBriefInfo actual = serviceImpl.getEntityPeers(FRAPIConstant.COMPANY_PREFIX +
                SEARCH_TOKEN, null, true);
        // Assert
        errorCollector.checkThat(actual.getStatusCode(), equalTo(StatusCode.REQUEST_SUCCESS));
        assertForMatchedEntity(actual);
        errorCollector.checkThat(actual.getEntity().getName(), equalTo(NAME));
        errorCollector.checkThat(actual.getEntity().getSearchToken(), equalTo(TEXT_STR));
        errorCollector.checkThat(actual.getMatchedEntity().get(2).getSearchToken(), equalTo(TEXT_STR));
    }

    @Test
    public void givenGetEntityPeersWhenGotCompetitorFromSolrThenGetSuccessStatus1() throws Exception {
        // Arrange
        setInternalState(serviceImpl, COMPANY_SERVICE_REPOSITORY_COL, companyServiceRepository);
        // Act
        final EntityBriefInfo actual = serviceImpl.getEntityPeers(FRAPIConstant.COMPANY_PREFIX +
                SEARCH_TOKEN, null, true);
        // Assert
        errorCollector.checkThat(actual.getStatusCode(), equalTo(StatusCode.PEERS_NOT_FOUND));
    }

    @Test
    public void givenGetEntityPeersWhenIsDnBldIsFalseAndEntityFoundThenSuccessStatus() throws Exception {
        // Arrange
        setInternalState(serviceImpl, COMPANY_SERVICE_REPOSITORY_COL, companyServiceRepositoryImpl);
        // Act
        final EntityBriefInfo actual = serviceImpl.getEntityPeers(SEARCH_TOKEN, null, false);
        // Assert
        assertEquals(StatusCode.REQUEST_SUCCESS, actual.getStatusCode());
    }

    @Test
    public void givenGetEntityPeersWhenIsDnBldIsFalseAndEntityNotFoundThenEntityNotFoundStatus() throws Exception {
        // Arrange
        when(infoCache.searchTokenToEntity(SEARCH_TOKEN)).thenReturn(null);
        // Act
        final EntityBriefInfo actual = serviceImpl.getEntityPeers(SEARCH_TOKEN, null, false);
        // Assert
        assertEquals(StatusCode.ENTITY_NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void givenGetEntityPeersWhenExceptionInGetCacheThenThrowException() throws Exception {
        // Assert
        expectedException.expect(IllegalArgumentException.class);
        // Arrange
        when(entityBaseServiceRepository.getEntityInfoCache()).thenThrow(new IllegalArgumentException());
        // Act
        serviceImpl.getEntityPeers(SEARCH_TOKEN, null, false);
    }

    @Test
    public void givenCompletionServiceWhenCollectResultsThenValidate() throws Exception {
        // Arrange
        final EntityBriefInfo entityBriefInfoLocal = mock(EntityBriefInfo.class);
        final Future<BaseSet> baseSetFuture = mock(Future.class);
        final BaseSet baseSetLocal = new BaseSet();
        when(completionService.poll(anyLong(), any(TimeUnit.class))).thenReturn(baseSetFuture);
        when(baseSetFuture.get()).thenReturn(baseSetLocal);
        // Act
        invokeMethod(serviceImpl, "collectResults", completionService, entityBriefInfoLocal);
        // Assert
        verify(entityBriefInfoLocal).updatePrep(baseSetLocal);
        verify(entityBriefInfoLocal).setPerfStats(baseSetLocal);
    }

    @Test
    public void givenEntityWhenSetCompanyInfoThenReturnSolrDocument() throws Exception {
        // Arrange
        final com.firstrain.frapi.domain.Entity entityLocal = new com.firstrain.frapi.domain.Entity();
        final SolrDocument solrDocumentLocal = mock(SolrDocument.class);
        when(companyServiceNew.getCompanyDocuments(anyString())).thenReturn(solrDocumentLocal);
        when(solrDocumentLocal.getFieldValue("attrStateNameAbr")).thenReturn(NAME);

        // Act
        final SolrDocument documentLocal
                = invokeMethod(serviceImpl, "setCompanyInfo", entityLocal);

        // Assert
        errorCollector.checkThat(entityLocal.getState(), equalTo(NAME));
        errorCollector.checkThat(documentLocal, equalTo(solrDocumentLocal));
    }

    @Test
    public void givenCompanyIDsWhenGetTurnOverEventsAsyncThenReturnBaseSet() throws Exception {
        // Arrange
        setInternalState(serviceImpl, ENTITY_BASE_SERVICE_COL, entityBaseService);
        final BaseSpec baseSpecLocal = new BaseSpec();
        // Act
        final Callable<BaseSet> expectedBaseSet = invokeMethod(serviceImpl,
                "getTurnOverEventsAsync", Lists.newArrayList(1), baseSpecLocal);
        // Assert
        final EventSet eventSetLocal = (EventSet) expectedBaseSet.call();
        errorCollector.checkThat(eventSetLocal.getSectionType(), equalTo(SectionType.TE));
    }

    @Test
    public void givenCompanyIDsWhenGetWebVolumeGraphAsyncThenReturnBaseSet() throws Exception {
        // Arrange
        setInternalState(serviceImpl, ENTITY_BASE_SERVICE_COL, entityBaseService);
        final BaseSpec baseSpecLocal = new BaseSpec();
        // Act
        final Callable<BaseSet> expectedBaseSet = invokeMethod(serviceImpl,
                "getWebVolumeGraphAsync", ID, new int[]{1}, new int[]{1}, baseSpecLocal,
                Graph.GraphFor.CALL_PREP);
        // Assert
        final Graph graphLocal = (Graph) expectedBaseSet.call();
        errorCollector.checkThat(graphLocal.getSectionType(), equalTo(SectionType.WV));
    }

    @Test
    public void givenBaseSpecWhenGetEventsAsyncThenReturnBaseSet() throws Exception {
        // Arrange
        setInternalState(serviceImpl, "companyService", companyService);
        final BaseSpec baseSpecLocal = new BaseSpec();
        // Act
        final Callable<BaseSet> expectedBaseSet = invokeMethod(serviceImpl,
                "getEventsAsync", baseSpecLocal);
        // Assert
        final EventSet eventSetLocal = (EventSet) expectedBaseSet.call();
        errorCollector.checkThat(eventSetLocal.getSectionType(), equalTo(SectionType.E));
    }

    @Test
    public void givenBaseSpecWhenGetMgmtTurnoverAsyncThenReturnBaseSet() throws Exception {
        // Arrange
        setInternalState(serviceImpl, COMPANY_SERVICE_REPOSITORY_COL, companyServiceRepository);
        setInternalState(serviceImpl, "companyService", companyService);
        final BaseSpec baseSpecLocal = new BaseSpec();
        // Act
        final Callable<BaseSet> expectedBaseSet = invokeMethod(serviceImpl,
                "getMgmtTurnoverAsync", baseSpecLocal);
        // Assert
        final MgmtTurnoverData eventSetLocal = (MgmtTurnoverData) expectedBaseSet.call();
        errorCollector.checkThat(eventSetLocal.getSectionType(), equalTo(SectionType.MTC));
    }

    @Test
    public void givenBaseSpecWhenGetIndustryWebResultsAsyncThenReturnBaseSet() throws Exception {
        // Arrange
        setInternalState(serviceImpl, INDUSTRY_BRIEF_SERVICE_COL, industryBriefService);
        final BaseSpec baseSpecLocal = new BaseSpec();
        // Act
        final Callable<BaseSet> expectedBaseSet = invokeMethod(serviceImpl,
                "getIndustryWebResultsAsync", Sets.newHashSet(NAME), NAME, baseSpecLocal);
        // Assert
        final DocumentSet eventSetLocal = (DocumentSet) expectedBaseSet.call();
        errorCollector.checkThat(eventSetLocal.getSectionType(), equalTo(SectionType.FR));
    }

    @Test
    public void givenBaseSpecWhenGetIndustryWebResultsThenReturnDocumentSet() throws Exception {
        // Arrange
        setInternalState(serviceImpl, INDUSTRY_BRIEF_SERVICE_COL, industryBriefService);
        // Act
        final DocumentSet documentSetLocal = invokeMethod(serviceImpl,
                "getIndustryWebResults", Sets.newHashSet(NAME), NAME, new BaseSpec());
        // Assert
        errorCollector.checkThat(documentSetLocal.getStat().getReq(), equalTo("Web Results"));
    }

    @Test
    public void givenBaseSpecWhenGetEventSetForTopicsAndCompaniesAsyncThenReturnBaseSet() throws Exception {
        // Arrange
        setInternalState(serviceImpl, ENTITY_BASE_SERVICE_COL, entityBaseService);
        BaseSpec baseSpecLocal = new BaseSpec();
        baseSpecLocal.setStart((short) 2);
        // Act
        final Callable<BaseSet> expectedBaseSet = invokeMethod(serviceImpl,
                "getEventSetForTopicsAndCompaniesAsync", new int[]{1}, new int[]{1}, baseSpecLocal);
        // Assert
        final EventSet eventSetLocal = (EventSet) expectedBaseSet.call();
        errorCollector.checkThat(eventSetLocal.getSectionType(), equalTo(SectionType.E));
    }

    @Test
    public void givenBaseSpecWhenGetTweetsAsyncThenReturnBaseSet() throws Exception {
        // Arrange
        setInternalState(serviceImpl, ENTITY_BASE_SERVICE_COL, entityBaseService);
        // Act
        final Callable<BaseSet> expectedBaseSet = invokeMethod(serviceImpl,
                "getTweetsAsync", new String[]{ID}, new SectionSpec(), SCOPE, ARTICLE_ID);
        // Assert
        final TweetSet tweetSetLocal = (TweetSet) expectedBaseSet.call();
        errorCollector.checkThat(tweetSetLocal.getSectionType(), equalTo(SectionType.FT));
    }

    @Test
    public void givenBaseSpecWhenGetWebResultsAsyncThenReturnBaseSet() throws Exception {
        // Arrange
        setInternalState(serviceImpl, ENTITY_BASE_SERVICE_COL, entityBaseService);

        // Act
        final Callable<BaseSet> expectedBaseSet = invokeMethod(serviceImpl,
                "getWebResultsAsync", NAME, NAME, new BaseSpec(), new BlendDunsInput());
        // Assert
        final DocumentSet documentSetLocal = (DocumentSet) expectedBaseSet.call();
        errorCollector.checkThat(documentSetLocal.getSectionType(), equalTo(SectionType.FR));
    }

    @Test
    public void givenBaseSpecWhenGetHighlightsResultsAsyncThenReturnBaseSet() throws Exception {
        // Arrange
        setInternalState(serviceImpl, ENTITY_BASE_SERVICE_COL, entityBaseService);
        BaseSpec baseSpecLocal = new BaseSpec();
        baseSpecLocal.setCount((short) 2);
        // Act
        final Callable<BaseSet> expectedBaseSet = invokeMethod(serviceImpl,
                "gethighlightsResultsAsync", NAME, NAME, baseSpecLocal, new BlendDunsInput());
        // Assert
        final DocumentSet documentSetLocal = (DocumentSet) expectedBaseSet.call();
        errorCollector.checkThat(documentSetLocal.getSectionType(), equalTo(SectionType.HR));
    }

    @Test
    public void givenBaseSpecWhenGetAnalystCommentsAsyncThenReturnBaseSet() throws Exception {
        // Arrange
        BaseSpec baseSpecLocal = new BaseSpec();
        baseSpecLocal.setCount((short) 2);
        setInternalState(serviceImpl, ENTITY_BASE_SERVICE_COL, entityBaseService);
        // Act
        final Callable<BaseSet> expectedBaseSet = invokeMethod(serviceImpl,
                "getAnalystCommentsAsync", NAME, baseSpecLocal, baseSpecLocal);
        // Assert
        final DocumentSet documentSetLocal = (DocumentSet) expectedBaseSet.call();
        errorCollector.checkThat(documentSetLocal.getSectionType(), equalTo(SectionType.AC));
    }

    @Test
    public void givenNeedMatchTrueWhenGetAnalystCommentsAsyncThenReturnBaseSet() throws Exception {
        // Arrange
        mockStatic(SearchResultGenerator.class);
        when(SearchResultGenerator.collectSearchResults(any(Future.class),any(Future.class),any(BaseSpec.class))).
                thenReturn(searchResult);
        BaseSpec baseSpecLocal = new BaseSpec();
        baseSpecLocal.setCount((short) 2);
        baseSpecLocal.setNeedMatchedEntities(true);
        final DocumentSet documentSetNew = new DocumentSet();
        setInternalState(serviceImpl, ENTITY_BASE_SERVICE_COL, entityBaseService);
        when(entityBaseService.makeDocSetFromEntries(any(SearchResult.class),anyBoolean(),anyInt()
                ,any(DocumentSimilarityUtil.class),any(ContentType.class),anyBoolean(),anyInt(),anyBoolean(),anyBoolean())).
                thenReturn(documentSetNew);

        // Act
        final Callable<BaseSet> expectedBaseSet = invokeMethod(serviceImpl,
                "getAnalystCommentsAsync", NAME, baseSpecLocal, baseSpecLocal);

        // Assert
        final DocumentSet documentSetLocal = (DocumentSet) expectedBaseSet.call();
        errorCollector.checkThat(documentSetLocal.getSectionType(), equalTo(SectionType.AC));
    }

    @Test
    public void givenInfoCacheWhenGetEntityByEntityInfoCacheThenReturnNull() throws Exception {
        // Act
        final com.firstrain.frapi.domain.Entity entityLocal = invokeMethod(serviceImpl,
                "getEntityByEntityInfoCache", 0, infoCache);
        // Assert
        errorCollector.checkThat(entityLocal, nullValue());
    }

    @Test
    public void givenInfoCacheAndCatIdWhenGetEntityByEntityInfoCacheThenReturnNull() throws Exception {
        // Arrange
        when(infoCache.catIdToEntity(anyString())).thenReturn(null);
        // Act
        final com.firstrain.frapi.domain.Entity entityLocal = invokeMethod(serviceImpl,
                "getEntityByEntityInfoCache", 1, infoCache);
        // Assert
        errorCollector.checkThat(entityLocal, nullValue());
    }

    @Test
    public void givenEntityInfoIsNullWhenGetInputEntityTypeThenReturnNull() throws Exception {
        // Act
        final INPUT_ENTITY_TYPE inputEntityTypeLocal = invokeMethod(serviceImpl,
                "getInputEntityType", (IEntityInfo) null);
        // Assert
        errorCollector.checkThat(inputEntityTypeLocal, nullValue());
    }

    @Test
    public void givenSearchTokenAndChartTypesWhenGetVisualizationAsyncThenValidate() throws Exception {
        // Arrange
        final VisualizationService visualizationServiceLocal = mock(VisualizationService.class);
        setInternalState(serviceImpl, "visualizationService", visualizationServiceLocal);
        final VisualizationData visualizationDataLocal = new VisualizationData();
        when(visualizationServiceLocal.getVisualizationByEntityToken(anyString(), anyInt(), anyList(), anyString(),
                anyBoolean(), anyBoolean())).thenReturn(visualizationDataLocal);

        // Act
        final Callable<BaseSet> baseSetLocal = invokeMethod(serviceImpl,
                "getVisualizationAsync", SEARCH_TOKEN, 1,
                Lists.newArrayList(VisualizationData.ChartType.ACC_METER), NAME, true);

        // Assert
        final VisualizationData visualizationData = (VisualizationData) baseSetLocal.call();
        errorCollector.checkThat(visualizationData.getSectionType(), equalTo(SectionType.VIZ));
        errorCollector.checkThat(visualizationData.getStat().getUserId(), equalTo(SEARCH_TOKEN));
    }

    @Test
    public void givenValuesAndKeyWhenGetWeightThenReturnWeight() throws Exception {
        final List<List<String>> values = new ArrayList<>();
        values.add(Lists.newArrayList(NAME));
        values.add(Lists.newArrayList(SEARCH_TOKEN));
        // Act
        final int weight = invokeMethod(serviceImpl, "getWeight", values, SEARCH_TOKEN);
        // Assert
        errorCollector.checkThat(weight, equalTo(1));
    }

    @Test
    public void givenSectionSpecAndScopeWhenGetTweetsThenValidate() throws Exception {
        // Arrange
        setInternalState(serviceImpl, ENTITY_BASE_SERVICE_COL, entityBaseService);
        sectionSpec.setCount(null);
        final String[] catIDs = {NAME};
        // Act
        final TweetSet tweetSetLocal = invokeMethod(serviceImpl, GET_TWEETS_METHOD,
                catIDs, sectionSpec, SCOPE, ARTICLE_ID);
        // Assert
        errorCollector.checkThat(sectionSpec.getCount(), equalTo(COUNT));
        verify(entityBaseService).getTweetList(eq(catIDs), twitterSpecArgumentCaptor.capture());
        errorCollector.checkThat(twitterSpecArgumentCaptor.getValue().getScope(), equalTo(SCOPE));
        errorCollector.checkThat(tweetSetLocal.getStat().getReq(), equalTo(REQUEST_TWEETS));
    }

    @Test
    public void givenSectionSpecAndScopeAndNodeWhenGetTweetsThenValidate() throws Exception {
        // Arrange
        setInternalState(serviceImpl, ENTITY_BASE_SERVICE_COL, entityBaseService);
        sectionSpec.setStart(null);
        final String[] catIDs = {NAME};
        sectionSpec.setNeedTweetAccelerometer(true);
        final GraphNodeSet graphNodeSet = new GraphNodeSet();
        when(entityBaseService.getAccelerometerNode(anyString(), anyBoolean(), anyBoolean()))
                .thenReturn(graphNodeSet);
        // Act
        final TweetSet tweetSetLocal = invokeMethod(serviceImpl, GET_TWEETS_METHOD,
                catIDs, sectionSpec, SCOPE, ARTICLE_ID);

        // Assert
        errorCollector.checkThat(tweetSetLocal.getStat().getReq(), equalTo(REQUEST_TWEETS));
        verify(entityBaseService).getAccelerometerNode(NAME, true, false);
        errorCollector.checkThat(tweetSetLocal.getTweetAccelerometer(), equalTo(graphNodeSet));
    }
}

