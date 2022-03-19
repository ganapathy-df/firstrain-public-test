package com.firstrain.frapi.service.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.times;
import static org.powermock.reflect.internal.WhiteboxImpl.setInternalState;

import com.firstrain.frapi.domain.BaseSpec;
import com.firstrain.frapi.domain.SectionSpec;
import com.firstrain.frapi.domain.VisualizationData;
import com.firstrain.frapi.domain.TwitterSpec;
import com.firstrain.frapi.domain.EntityMap;
import com.firstrain.frapi.pojo.EnterprisePref;
import com.firstrain.frapi.pojo.Entity;
import com.firstrain.frapi.pojo.EntityBriefInfo;
import com.firstrain.frapi.pojo.Graph;
import com.firstrain.frapi.pojo.wrapper.EventSet;
import com.firstrain.frapi.pojo.wrapper.DocumentSet;
import com.firstrain.frapi.pojo.wrapper.BaseSet.SectionType;
import com.firstrain.frapi.repository.impl.EntityBaseServiceRepositoryImpl;
import com.firstrain.frapi.util.StatusCode;
import com.firstrain.frapi.util.DefaultEnums.INPUT_ENTITY_TYPE;
import com.google.common.collect.Lists;
import org.apache.solr.common.SolrDocument;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class EntityBriefServiceImplTest extends EntityBriefServiceImplTestSetup {

    @Test
    public void givenGetEntityBriefDetailsWithBlendDUNSFalse() throws Exception {
        // Arrange
        when(infoCache.searchTokenToEntity(anyString())).thenReturn(null);
        // Act
        final EntityBriefInfo actual
                = serviceImpl.getEntityBriefDetails(enterprisePref, "ASFE", "8=H&4,Z2m_EjpFc.'n_");
        // Assert
        assertEquals(StatusCode.ENTITY_NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void givenFqValueWhenGetEntityBriefDetailsWithBlendDUNSFalse() throws Exception {
        // Arrange
        setEntityInfoLocal();
        setHandler();

        // Act
        final EntityBriefInfo actual = serviceImpl.getEntityBriefDetails(enterprisePref, "C:ASFE",
                FILTER_TOKEN, "8=H&4,Z2m_EjpFc.'n_", true);

        // Assert
        assertEquals(StatusCode.REQUEST_SUCCESS, actual.getStatusCode());
    }

    @Test
    public void givenEntityWhenGetEntityBriefThenException() throws Exception {
        // Arrange
        enterprisePref.setDnBId(false);
        when(infoCache.searchTokenToEntity(anyString())).thenReturn(entityInfo);
        PowerMockito.when(industryBriefService.getVirtualMonitor(anyString(), anyString(), anyString(), any(AtomicBoolean.class),
                anyBoolean())).thenThrow(Exception.class);
        // Act
        expectedException.expect(Exception.class);
        serviceImpl.getEntityBriefDetails(enterprisePref, "ASFE", "8=H&4,Z2m_EjpFc.'n_", "", false);
    }

    @Test
    public void givenGetEntityBriefDetailsWhenDbBIdIsTrueCompanyTokenAndEntityFoundNullThenGetStatusNotFound()
            throws Exception {
        // Arrange
        enterprisePref.setDnBId(true);
        when(infoCache.searchTokenToEntity(anyString())).thenReturn(null);
        // Act
        final EntityBriefInfo actual = serviceImpl.getEntityBriefDetails(enterprisePref, "C:ASFE", "",
                "8=H&4,Z2m_EjpFc.'n_", true);
        // Assert
        assertEquals(StatusCode.ENTITY_NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void givenGetEntityBriefDetailsWhenDbBIdIsTrueAndEntityFoundNullThenGetStatusNotFound() throws Exception {
        // Arrange
        enterprisePref.setDnBId(true);
        when(infoCache.searchTokenToEntity(anyString())).thenReturn(null);
        // Act
        final EntityBriefInfo actual = serviceImpl.getEntityBriefDetails(enterprisePref, "ASFE", "",
                "8=H&4,Z2m_EjpFc.'n_", true);
        // Assert
        assertEquals(StatusCode.ENTITY_NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void givenGetEntityBriefDetailsWhenEntityTypeIndustryThenGetStatusSuccess() throws Exception {
        // Arrange
        setEntityInfoLocal();

        // Act
        final EntityBriefInfo actual = serviceImpl.getEntityBriefDetails(enterprisePref, "C:ASFE",
                FILTER_TOKEN, "8=H&4,Z2m_EjpFc.'n_", true);

        // Assert
        errorCollector.checkThat(actual.getStatusCode(), equalTo(StatusCode.REQUEST_SUCCESS));
        errorCollector.checkThat(actual.getEntity().getSearchToken(), equalTo(TEXT_STR));
        verifySubmit(4);
        verify(servicesAPIUtil).setSourceContent(eq(true), eq(false), baseSpecArgumentCaptor.capture(),
                eq(enterprisePref));
        final BaseSpec baseSpecLocal = baseSpecArgumentCaptor.getValue();
        errorCollector.checkThat(baseSpecLocal.getStart(), equalTo(SHORT_ONE));
        errorCollector.checkThat(baseSpecLocal.getCount(), equalTo((short) 10));
        errorCollector.checkThat(baseSpecLocal.getNeedPagination(), equalTo(true));
        errorCollector.checkThat(baseSpecLocal.getNeedBucket(), equalTo(true));
    }

    @Test
    public void givenGetEntityBriefDetailsAndStartIsNullAndTweetSetWhenEntityTypeIndustryThenGetStatusSuccess()
            throws Exception {
        // Arrange
        mockEnterprisePref();
        sectionSpec.setStart(null);
        // Act
        final EntityBriefInfo actual = serviceImpl.getEntityBriefDetails(enterprisePref, "C:ASFE",
                FILTER_TOKEN, "8=H&4,Z2m_EjpFc.'n_", true);
        // Assert
        errorCollector.checkThat(actual.getStatusCode(), equalTo(StatusCode.REQUEST_SUCCESS));
    }

    @Test
    public void givenGetEntityBriefDetailsAndTweetSetWhenEntityTypeIndustryThenGetStatusSuccess() throws Exception {
        // Arrange
        mockEnterprisePref();
        // Act
        final EntityBriefInfo actual = serviceImpl.getEntityBriefDetails(enterprisePref, "C:ASFE",
                FILTER_TOKEN, "8=H&4,Z2m_EjpFc.'n_", true);

        // Assert
        assertEquals(StatusCode.REQUEST_SUCCESS, actual.getStatusCode());
        verifySubmit(4);
        verify(entityBaseService).getEventsTimeline(eq((int[]) null), eq((int[]) null),
                baseSpecArgumentCaptor.capture());
        verify(entityBaseService).getWebVolumeGraph(eq(ENTITY_ID), eq((int[]) null), eq((int[]) null),
                baseSpecArgumentCaptor.capture(), eq(Graph.GraphFor.CALL_PREP), eq(0), eq(1),
                eq((int[]) null), eq(true));
        final BaseSpec baseSpecLocal = baseSpecArgumentCaptor.getAllValues().get(0);
        errorCollector.checkThat(baseSpecLocal.getCount(), equalTo(COUNT));
        errorCollector.checkThat(baseSpecLocal.getStart(), equalTo(SHORT_ONE));
        errorCollector.checkThat(baseSpecLocal.getDaysCount(), equalTo(BASIC_WEB_RESULTS_SEARCH_DAYS));
        errorCollector.checkThat(baseSpecLocal.getNeedBucket(), equalTo(true));
        errorCollector.checkThat(baseSpecLocal.isNeedRelatedDoc(), equalTo(true));
        errorCollector.checkThat(baseSpecLocal.getIndustryClassificationId(), equalTo(SHORT_ONE));
        errorCollector.checkThat(baseSpecLocal.getCacheKey(), equalTo(ENTITY_ID));
        final BaseSpec newBaseSpec = baseSpecArgumentCaptor.getAllValues().get(1);
        errorCollector.checkThat(newBaseSpec.getCount(), equalTo(WV_EVENTS_COUNT));
        errorCollector.checkThat(newBaseSpec.getDaysCount(), equalTo(BASIC_WEB_RESULTS_SEARCH_DAYS));
        errorCollector.checkThat(newBaseSpec.getStart(), equalTo(SHORT_ONE));
        errorCollector.checkThat(newBaseSpec.isNeedRelatedDoc(), equalTo(true));
    }

    @Test
    public void givenGetEntityBriefDetailsWhenGotExceptionThenThrowException() throws Exception {
        // Assert
        expectedException.expect(IllegalArgumentException.class);
        // Arrange
        when(entityBaseServiceRepository.getEntityInfoCache()).thenThrow(new IllegalArgumentException());
        // Act
        serviceImpl.getEntityBriefDetails(enterprisePref, "ASFE", "", "8=H&4,Z2m_EjpFc.'n_", true);
    }

    @Test
    public void givenGetEntityBriefDetailsWhenEntityTypeCompanyThenValidateException() throws Exception {
        // Arrange
        setVisualizationServiceLocal();
        final BaseSpec baseSpecLocal = new BaseSpec();
        baseSpecLocal.setCount((short) 2);
        baseSpecLocal.setScope(1);
        when(servicesAPIUtil.setSourceContent(anyBoolean(), anyBoolean(), any(BaseSpec.class),
                any(EnterprisePref.class))).thenReturn(baseSpecLocal);
        mockEntityPojo();
        setInternalState(serviceImpl, ENTITY_BASE_SERVICE_COL, entityBaseService);
        setInternalState(serviceImpl, "companyService", companyService);
        // Act
        final EntityBriefInfo actual = serviceImpl.getEntityBriefDetails(enterprisePref, "ASFE",
                FILTER_TOKEN, "8=H&4,Z2m_EjpFc.'n_", true);

        // Assert
        errorCollector.checkThat(actual.getStatusCode(), equalTo(StatusCode.REQUEST_SUCCESS));
        errorCollector.checkThat(actual.getEntity(), equalTo(entityPojo));
        verifySubmitWhenTaskExecutor(9);
    }

    @Test
    public void givenHandlerWhenEntityTypeCompanyThenValidate() throws Exception {
        // Arrange
        final EventSet eventSetLocal = new EventSet();
        final DocumentSet documentSetLocal = new DocumentSet();
        when(entityBaseService.gethighlightsResults(any(String[].class), any(int[].class), anyInt()))
                .thenReturn(documentSetLocal);
        setEntityBriefParameters(eventSetLocal);
        sectionSpecLocalFinalNew.setStart(null);

        // Act
        final EntityBriefInfo actual = serviceImpl.getEntityBriefDetails(enterprisePref, "ASFE",
                FILTER_TOKEN, "8=H&4,Z2m_EjpFc.'n_", true);

        // Assert
        errorCollector.checkThat(actual.getStatusCode(), equalTo(StatusCode.REQUEST_SUCCESS));

    }

    @Test
    public void givenGetEntityBriefDetailsWhenEntityTypeCompanyThenValidate() throws Exception {
        // Arrange
        final EventSet eventSetLocal = new EventSet();
        final DocumentSet documentSetLocal = new DocumentSet();
        when(entityBaseService.gethighlightsResults(any(String[].class), any(int[].class), anyInt()))
                .thenReturn(documentSetLocal);
        setEntityBriefParameters(eventSetLocal);

        // Act
        final EntityBriefInfo actual = serviceImpl.getEntityBriefDetails(enterprisePref, "ASFE",
                FILTER_TOKEN, "8=H&4,Z2m_EjpFc.'n_", true);

        // Assert
        errorCollector.checkThat(actual.getStatusCode(), equalTo(StatusCode.REQUEST_SUCCESS));
        verifySubmitWhenTaskExecutor(9);
        verify(entityProcessingService).getDocumentSetWithId(documentSetLocal);
        verify(servicesAPIUtil, times(2)).setSourceContent(eq(true), eq(false),
                baseSpecArgumentCaptor.capture(), eq(enterprisePref));
        final BaseSpec newBaseSpec = baseSpecArgumentCaptor.getAllValues().get(0);
        errorCollector.checkThat(newBaseSpec.getCount(), equalTo(COUNT));
        errorCollector.checkThat(newBaseSpec.getStart(), equalTo(SHORT_ONE));
        errorCollector.checkThat(newBaseSpec.getDaysCount(), equalTo(1));
        errorCollector.checkThat(newBaseSpec.getScope(), equalTo(3));
        errorCollector.checkThat(newBaseSpec.getNeedPagination(), equalTo(false));
        errorCollector.checkThat(newBaseSpec.getNeedMatchedEntities(), equalTo(true));
        errorCollector.checkThat(newBaseSpec.getExcludeArticleIdsSSV(), equalTo(ARTICLE_ID));
        errorCollector.checkThat(newBaseSpec.getCountPerEntity(), equalTo((short) 2));
        errorCollector.checkThat(newBaseSpec.getIndustryClassificationId(), equalTo(SHORT_ONE));
        verify(entityBaseService, times(2)).getSearchResultForAnalystComments(eq(TEXT_STR),
                baseSpecArgumentCaptor.capture());
        final BaseSpec newSpec = baseSpecArgumentCaptor.getAllValues().get(2);
        errorCollector.checkThat(newSpec.getCount(), equalTo(COUNT));
        errorCollector.checkThat(newSpec.getStart(), equalTo(SHORT_ONE));
        errorCollector.checkThat(newSpec.getDaysCount(), equalTo(WEB_RESULTS_SEARCH_DAYS_BIMONTHLY));
        errorCollector.checkThat(newSpec.getNeedMatchedEntities(), equalTo(false));
        errorCollector.checkThat(newSpec.getExcludeArticleIdsSSV(), equalTo(ARTICLE_ID));
        final BaseSpec newSecondSpec = baseSpecArgumentCaptor.getAllValues().get(3);
        errorCollector.checkThat(newSecondSpec.getCount(), equalTo(COUNT));
        errorCollector.checkThat(newSecondSpec.getStart(), equalTo(SHORT_ONE));
        errorCollector.checkThat(newSecondSpec.getDaysCount(), equalTo(BASIC_WEB_RESULTS_SEARCH_DAYS));
        errorCollector.checkThat(newSecondSpec.getNeedMatchedEntities(), equalTo(false));
        errorCollector.checkThat(newSecondSpec.getExcludeArticleIdsSSV(), equalTo(ARTICLE_ID));
        verify(entityBaseService).getEventSetForMTEvents(eq(Lists.newArrayList(0)),
                baseSpecArgumentCaptor.capture());
        final BaseSpec spec = baseSpecArgumentCaptor.getAllValues().get(4);
        errorCollector.checkThat(spec.getCount(), equalTo(COUNT));
        errorCollector.checkThat(spec.getStart(), equalTo(SHORT_ONE));
        errorCollector.checkThat(spec.getDaysCount(), equalTo(BASIC_WEB_RESULTS_SEARCH_DAYS));
        errorCollector.checkThat(spec.getNeedBucket(), equalTo(true));
        verify(entityBaseService).getWebVolumeGraph(eq(ENTITY_ID), eq((int[]) null), eq((int[]) null),
                baseSpecArgumentCaptor.capture(), eq(Graph.GraphFor.CALL_PREP), eq(0), eq(1),
                eq((int[]) null), eq(true));
        final BaseSpec fifthBaseSpec = baseSpecArgumentCaptor.getAllValues().get(5);
        errorCollector.checkThat(fifthBaseSpec.getCount(), equalTo((short) 40));
        errorCollector.checkThat(fifthBaseSpec.getStart(), equalTo(SHORT_ONE));
        errorCollector.checkThat(fifthBaseSpec.getDaysCount(), equalTo(BASIC_WEB_RESULTS_SEARCH_DAYS));
        errorCollector.checkThat(fifthBaseSpec.getIndustryClassificationId(), equalTo(SHORT_ONE));
        errorCollector.checkThat(fifthBaseSpec.isNeedRelatedDoc(), equalTo(true));
        verify(entityProcessingService).getEventSetWithId(eventSetLocal);
        verify(entityProcessingService).getEventSetWithDateBucketing(eventSetLocal, null);
    }

    @Test
    public void givenGetEntityBriefDetailsAndEntityTypeIsIndustryWhenEntityTypeCompanyThenValidate1() throws Exception {
        // Arrange
        mockEventSet();
        final Map<SectionType, SectionSpec> sectionsMap = new HashMap<>();
        sectionsMap.put(SectionType.F, new SectionSpec());
        sectionsMap.put(SectionType.VIZ, new SectionSpec());
        sectionsMap.put(SectionType.RL, new SectionSpec());
        enterprisePref.setSectionsMap(sectionsMap);
        setVisualizationServiceLocal();
        // Act
        final EntityBriefInfo actual = serviceImpl.getEntityBriefDetails(enterprisePref, "ASFE",
                FILTER_TOKEN, SCOPE_DIRECTIVE, true);
        // Assert
        errorCollector.checkThat(actual.getStatusCode(), equalTo(StatusCode.REQUEST_SUCCESS));
        verifySubmit(1);
        verify(visualizationService).getVisualizationByEntityToken(TEXT_STR, 12,
                Lists.newArrayList(VisualizationData.ChartType.GEO_US), "", false,
                true);
    }

    @Test
    public void givenVisualizationNullWhenEntityTypeCompanyThenException() throws Exception {
        // Arrange
        mockEventSet();

        // Act
        final EntityBriefInfo actual = serviceImpl.getEntityBriefDetails(enterprisePref, "ASFE",
                FILTER_TOKEN, SCOPE_DIRECTIVE, true);

        // Assert
        verifySubmit(6);
        errorCollector.checkThat(actual.getStatusCode(), equalTo(StatusCode.REQUEST_SUCCESS));
    }

    @Test
    public void givenGetEntityBriefDetailsAndEntityTypeIsIndustryWhenEntityTypeCompanyThenValidate() throws Exception {
        // Arrange
        mockEventSet();
        setVisualizationServiceLocal();

        // Act
        final EntityBriefInfo actual = serviceImpl.getEntityBriefDetails(enterprisePref, "ASFE",
                FILTER_TOKEN, SCOPE_DIRECTIVE, true);
        // Assert
        verifySubmit(6);
        verify(entityBaseService).getEventsTimeline(eq((int[]) null), eq(new int[]{Integer.parseInt(ENTITY_ID)}),
                baseSpecArgumentCaptor.capture());
        verify(servicesAPIUtil, times(2)).setSourceContent(eq(true), eq(false),
                baseSpecArgumentCaptor.capture(), eq(enterprisePref));
        final BaseSpec newBaseSpec = baseSpecArgumentCaptor.getAllValues().get(0);
        errorCollector.checkThat(newBaseSpec.getCount(), equalTo(COUNT));
        errorCollector.checkThat(newBaseSpec.getStart(), equalTo(SHORT_ONE));
        errorCollector.checkThat(newBaseSpec.getDaysCount(), equalTo(BASIC_WEB_RESULTS_SEARCH_DAYS));
        errorCollector.checkThat(newBaseSpec.getNeedBucket(), equalTo(true));
        errorCollector.checkThat(newBaseSpec.getIndustryClassificationId(), equalTo(SHORT_ONE));
        errorCollector.checkThat(newBaseSpec.getCacheKey(), equalTo(ENTITY_ID));
        errorCollector.checkThat(actual.getStatusCode(), equalTo(StatusCode.REQUEST_SUCCESS));
        verify(completionService).poll(0L, TimeUnit.MILLISECONDS);
        final String[] catIDs = {ENTITY_ID};
        verify(entityBaseService).getTweetList(eq(catIDs), twitterSpecArgumentCaptor.capture());
        final TwitterSpec twitterSpecLocal = twitterSpecArgumentCaptor.getValue();
        errorCollector.checkThat(twitterSpecLocal.getRows(), equalTo(10));
        errorCollector.checkThat(twitterSpecLocal.getScope(), equalTo(-1));
        errorCollector.checkThat(twitterSpecLocal.getExcludeArticleIdsSSV(), equalTo(ARTICLE_ID));
        errorCollector.checkThat(twitterSpecLocal.getStart(), equalTo(SHORT_ONE));
        errorCollector.checkThat(twitterSpecLocal.getCatIds(), equalTo(catIDs));
        errorCollector.checkThat(twitterSpecLocal.getNeedBucket(), equalTo(true));
        verify(entityBaseService).getWebResultsForSearch(eq(TEXT_STR), eq(EXCLUDE_FILTERS),
                baseSpecArgumentCaptor.capture(), blendDunsInputArgumentCaptor.capture());
        errorCollector.checkThat(baseSpecArgumentCaptor.getValue().getCount(), equalTo((short) 2));
        errorCollector.checkThat(baseSpecArgumentCaptor.getValue().getNeedBucket(), equalTo(true));
        errorCollector.checkThat(blendDunsInputArgumentCaptor.getValue().getBlendDUNS(), equalTo(true));
        errorCollector.checkThat(blendDunsInputArgumentCaptor.getValue().getScopeDirective(), equalTo(SCOPE_DIRECTIVE));
        final BaseSpec newBaseSpecLocal = baseSpecArgumentCaptor.getAllValues().get(2);
        errorCollector.checkThat(newBaseSpecLocal.getStart(), equalTo(SHORT_ONE));
        errorCollector.checkThat(newBaseSpecLocal.getScope(), equalTo(SCOPE));
        errorCollector.checkThat(newBaseSpecLocal.getDaysCount(), equalTo(BASIC_WEB_RESULTS_SEARCH_DAYS));
        errorCollector.checkThat(newBaseSpecLocal.getCount(), equalTo((short) 10));
        errorCollector.checkThat(newBaseSpecLocal.getNeedPagination(), equalTo(true));
        errorCollector.checkThat(newBaseSpecLocal.getNeedBucket(), equalTo(true));
        errorCollector.checkThat(newBaseSpecLocal.getNeedMatchedEntities(), equalTo(true));
        errorCollector.checkThat(newBaseSpecLocal.getExcludeArticleIdsSSV(), equalTo(ARTICLE_ID));
        errorCollector.checkThat(newBaseSpecLocal.getIndustryClassificationId(), equalTo(SHORT_ONE));
        verify(entityProcessingService).getEventSetWithId(eventSet);
    }

    @Test
    public void givenGetEntityBriefDetailsAndEntityTypeIsIndustryAndFqWhenEntityTypeCompanyThenValidate1()
            throws Exception {
        // Arrange
        mockEventSet();
        // Act
        final EntityBriefInfo actual = serviceImpl.getEntityBriefDetails(enterprisePref, "ASFE",
                "contentFilterToken", SCOPE_DIRECTIVE, true);
        // Assert
        errorCollector.checkThat(actual.getStatusCode(), equalTo(StatusCode.REQUEST_SUCCESS));
    }

    @Test
    public void givenHandlerWhenThenGetEntityBriefDetailsThenValidate() throws Exception {
        // Arrange
        mockEventSet();
        setHandler();

        // Act
        final EntityBriefInfo actual = serviceImpl.getEntityBriefDetails(enterprisePref, "ASFE",
                "contentFilterToken", SCOPE_DIRECTIVE, true);
        // Assert
        errorCollector.checkThat(actual.getStatusCode(), equalTo(StatusCode.REQUEST_SUCCESS));
    }

    @Test
    public void givenGetEntityBriefDetailsForMTWhenNullEntityThenEntityNotFoundStatus() throws Exception {
        // Arrange
        when(infoCache.searchTokenToEntity(anyString())).thenReturn(null);
        // Act
        final EntityBriefInfo actual = serviceImpl.getEntityBriefDetailsForMT(enterprisePref, SEARCH_TOKEN, FQUERY_STR,
                FROM, TO, String.valueOf(SHORT_ONE));
        // Assert
        assertEquals(StatusCode.ENTITY_NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void givenGetEntityBriefDetailsForMTWhenEntityThenEntityNotFoundStatus() throws Exception {
        // Arrange
        mockIndustryBriefDomain();
        // Act
        final EntityBriefInfo actual = serviceImpl.getEntityBriefDetailsForMT(enterprisePref, SEARCH_TOKEN,
                FILTER_TOKEN, FROM, TO, String.valueOf(SHORT_ONE));
        // Assert
        errorCollector.checkThat(actual.getStatusCode(), equalTo(StatusCode.REQUEST_SUCCESS));
        verifySubmit(1);
        verify(entityBaseService).getEventSetForMTEvents(eq(Lists.newArrayList(1, 2)),
                baseSpecArgumentCaptor.capture());
        final BaseSpec baseSpecLocal = baseSpecArgumentCaptor.getValue();
        errorCollector.checkThat(baseSpecLocal.getDaysCount(), equalTo(BASIC_WEB_RESULTS_SEARCH_DAYS));
        errorCollector.checkThat(baseSpecLocal.isCustomized(), equalTo(true));
        errorCollector.checkThat(baseSpecLocal.getCount(), equalTo(SHORT_ONE));
        errorCollector.checkThat(baseSpecLocal.getStartDate(), equalTo(FROM));
        errorCollector.checkThat(baseSpecLocal.getEndDate(), equalTo(TO));
    }

    @Test
    public void givenGetEntityBriefDetailsForMTWhenEntityThenEntitySuccessStatus() throws Exception {
        // Arrange
        mockIndustryBriefDomain();
        // Act
        final EntityBriefInfo actual = serviceImpl.getEntityBriefDetailsForMT(enterprisePref, SEARCH_TOKEN,
                "contentFilterToken", FROM, TO, String.valueOf(SHORT_ONE));
        // Assert
        errorCollector.checkThat(actual.getStatusCode(), equalTo(StatusCode.REQUEST_SUCCESS));
    }

    @Test
    public void givenGetEntityBriefDetailsForMTWhenGotExceptionThenThrowException() throws Exception {
        // Assert
        expectedException.expect(IllegalArgumentException.class);
        // Arrange
        when(entityBaseServiceRepository.getEntityInfoCache()).thenThrow(new IllegalArgumentException());
        // Act
        serviceImpl.getEntityBriefDetailsForMT(enterprisePref, SEARCH_TOKEN, FILTER_TOKEN, FROM, TO,
                String.valueOf(SHORT_ONE));
    }

    @Test
    public void givenGetEntityMapAndDocumentWhenGotSolrDocumentThenSuccess() throws Exception {
        // Arrange
        final SolrDocument solrDocument = prepareSolrDocument();
        when(companyServiceRepository.getCompanyInfoFromIndex(anyString())).thenReturn(solrDocument);
        when(entityInfo.getBizLineCatIds()).thenReturn(Collections.<Integer>emptyList());
        when(entityInfo.getIndustryCatId()).thenReturn(0);
        when(entityInfo.getSectorCatId()).thenReturn(0);
        when(entityInfo.getSegmentCatId()).thenReturn(0);
        // Act
        final EntityBriefInfo actual = serviceImpl.getEntityMap(SEARCH_TOKEN);
        // Assert
        errorCollector.checkThat(actual.getStatusCode(), equalTo(StatusCode.REQUEST_SUCCESS));
    }

    @Test
    public void givenGetEntityMapWhenGotSolrDocumentThenSuccess() throws Exception {
        // Arrange
        final SolrDocument solrDocument = prepareSolrDocument();
        when(companyServiceRepository.getCompanyInfoFromIndex(anyString())).thenReturn(solrDocument);
        // Act
        final EntityBriefInfo actual = serviceImpl.getEntityMap(SEARCH_TOKEN);
        // Assert
        errorCollector.checkThat(actual.getStatusCode(), equalTo(StatusCode.REQUEST_SUCCESS));
        final EntityMap entityMapLocal = actual.getEntityMap();
        errorCollector.checkThat(entityMapLocal.getEntity().getId(), equalTo(TEXT_STR));
        errorCollector.checkThat(entityMapLocal.getIndustry().getSearchToken(), equalTo(TEXT_STR));
        errorCollector.checkThat(entityMapLocal.getIndustry().getName(), equalTo(NAME));
        errorCollector.checkThat(entityMapLocal.getSector().getSearchToken(), equalTo(TEXT_STR));
        errorCollector.checkThat(entityMapLocal.getSector().getName(), equalTo(NAME));
        errorCollector.checkThat(entityMapLocal.getSegment().getSearchToken(), equalTo(TEXT_STR));
        errorCollector.checkThat(entityMapLocal.getSegment().getName(), equalTo(NAME));
        errorCollector.checkThat(entityMapLocal.getEntity().getWebsite(), equalTo(ATTR_WEBSITE));
        errorCollector.checkThat(entityMapLocal.getEntity().getAddress(), equalTo(ATTR_ADDRESS));
        errorCollector.checkThat(entityMapLocal.getEntity().getCountry(), equalTo(ATTR_COUNTRY));
        errorCollector.checkThat(entityMapLocal.getEntity().getCity(), equalTo(ATTR_CITY));
        errorCollector.checkThat(entityMapLocal.getEntity().getState(), equalTo(ATTR_STATE_NAME));
        errorCollector.checkThat(entityMapLocal.getEntity().getZip(), equalTo(ATTR_ZIP));
        errorCollector.checkThat(entityMapLocal.getBizlines().size(), equalTo(3));
        errorCollector.checkThat(entityMapLocal.getBizlines().get(0).getSearchToken(), equalTo(TEXT_STR));
        errorCollector.checkThat(entityMapLocal.getBizlines().get(0).getName(), equalTo(NAME));
        errorCollector.checkThat(entityMapLocal.getBizlines().get(1).getSearchToken(), equalTo(TEXT_STR));
        errorCollector.checkThat(entityMapLocal.getBizlines().get(1).getName(), equalTo(NAME));
        errorCollector.checkThat(entityMapLocal.getBizlines().get(2).getSearchToken(), equalTo(TEXT_STR));
        errorCollector.checkThat(entityMapLocal.getBizlines().get(2).getName(), equalTo(NAME));
        errorCollector.checkThat(entityMapLocal.getCompanyLogo(), equalTo(LOGO_IMAGE_URL));
    }

    @Test
    public void givenGetEntityMapWhenEntityNotFoundThenStatusNotFound() throws Exception {
        // Arrange
        final SolrDocument solrDocument = prepareSolrDocument();
        when(companyServiceRepository.getCompanyInfoFromIndex(anyString())).thenReturn(solrDocument);
        when(infoCache.searchTokenToEntity(anyString())).thenReturn(null);
        // Act
        final EntityBriefInfo actual = serviceImpl.getEntityMap(SEARCH_TOKEN);
        // Assert
        assertEquals(StatusCode.ENTITY_NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void givenGetEntityMatchWhenEntityTypeCompanyThenGetSuccessInfo() throws Exception {
        // Arrange
        when(autoSuggestService.getAutoCompleteEntries(QUERY_SRT, INPUT_ENTITY_TYPE.COMPANY.name(), false,
                2, TEXT_STR)).thenReturn(autoSuggest);
        // Act
        final EntityBriefInfo actual
                = serviceImpl.getEntityMatch(QUERY_SRT, 2, INPUT_ENTITY_TYPE.COMPANY, TEXT_STR);
        // Assert
        errorCollector.checkThat(actual.getStatusCode(), equalTo(StatusCode.REQUEST_SUCCESS));
        errorCollector.checkThat(actual.getMatchedEntity().size(), equalTo(1));
        final Entity entityLocal = actual.getMatchedEntity().get(0);
        errorCollector.checkThat(entityLocal.getId(), equalTo(ENTITY_ID));
        errorCollector.checkThat(entityLocal.getName(), equalTo(NAME));
        errorCollector.checkThat(entityLocal.getSearchToken(), equalTo(TEXT_STR));
        errorCollector.checkThat(entityLocal.getSynonym(), equalTo(TEXT_STR));
        errorCollector.checkThat(entityLocal.getBand(), equalTo(1));
    }

    @Test
    public void givenGetEntityMatchAndSynonymWhenEntityTypeCompanyThenGetSuccessInfo() throws Exception {
        // Arrange
        when(autoSuggestService.getAutoCompleteEntries(TEXT_STR, INPUT_ENTITY_TYPE.COMPANY.name(), false,
                2, TEXT_STR)).thenReturn(autoSuggest);
        // Act
        final EntityBriefInfo actual
                = serviceImpl.getEntityMatch(TEXT_STR, 2, INPUT_ENTITY_TYPE.COMPANY, TEXT_STR);
        // Assert
        errorCollector.checkThat(actual.getMatchedEntity().size(), equalTo(1));
        final Entity entityLocal = actual.getMatchedEntity().get(0);
        errorCollector.checkThat(entityLocal.getBand(), equalTo(2));
    }

    @Test
    public void givenGetEntityMatchAndNameWhenEntityTypeCompanyThenGetSuccessInfo() throws Exception {
        // Arrange
        when(autoSuggestService.getAutoCompleteEntries(NAME, INPUT_ENTITY_TYPE.COMPANY.name(), false,
                2, TEXT_STR)).thenReturn(autoSuggest);
        // Act
        final EntityBriefInfo actual
                = serviceImpl.getEntityMatch(NAME, 2, INPUT_ENTITY_TYPE.COMPANY, TEXT_STR);
        // Assert
        errorCollector.checkThat(actual.getMatchedEntity().get(0).getBand(), equalTo(2));
    }

    @Test
    public void givenGetEntityMatchAndNameWhenEntityTypeCompanyThenGetEntityNotFound() throws Exception {
        // Arrange
        when(autoSuggestService.getAutoCompleteEntries(NAME, INPUT_ENTITY_TYPE.COMPANY.name(), false,
                2, TEXT_STR)).thenReturn(autoSuggest);
        // Act
        final EntityBriefInfo actual
                = serviceImpl.getEntityMatch(NAME, COUNT, INPUT_ENTITY_TYPE.COMPANY, TEXT_STR);
        // Assert
        errorCollector.checkThat(actual.getStatusCode(), equalTo(StatusCode.ENTITY_NOT_FOUND));
    }

    @Test
    public void givenGetEntityMatchWhenGotExceptionThenCheckException() throws Exception {
        // Arrange
        when(autoSuggestService.getAutoCompleteEntries(QUERY_SRT, INPUT_ENTITY_TYPE.COMPANY.name(), false,
                2, TEXT_STR)).thenReturn(autoSuggest);
        when(industryClassificationMap.getIndustryClassificationMap()).thenThrow(new IllegalArgumentException());
        // Act
        final EntityBriefInfo actual
                = serviceImpl.getEntityMatch(QUERY_SRT, 2, INPUT_ENTITY_TYPE.COMPANY, TEXT_STR);
        // Assert
        errorCollector.checkThat(actual.getStatusCode(), equalTo(0));
    }

    @Test
    public void givenGetEntityMatchWhenEntityTypeTopicThenGetSuccessInfo() throws Exception {
        // Arrange
        when(autoSuggestService.getAutoCompleteEntries(QUERY_SRT, INPUT_ENTITY_TYPE.TOPIC.name(), false, 2,
                TEXT_STR)).thenReturn(autoSuggest);
        // Act
        final EntityBriefInfo actual
                = serviceImpl.getEntityMatch(QUERY_SRT, 2, INPUT_ENTITY_TYPE.TOPIC, TEXT_STR);
        // Assert
        errorCollector.checkThat(actual.getStatusCode(), equalTo(StatusCode.REQUEST_SUCCESS));
        errorCollector.checkThat(actual.getMatchedEntity().get(0).getBand(), equalTo(0));
    }

    @Test
    public void givenGetEntityMatchWhenEntityTypeIndustryThenGetSuccessInfo() throws Exception {
        // Arrange
        setInternalState(serviceImpl, ENTITY_BASE_SERVICE_COL, entityBaseService);
        final EntityBaseServiceRepositoryImpl baseServiceRepositoryImpl = new EntityBaseServiceRepositoryImpl();
        setInternalState(baseServiceRepositoryImpl, "searcher", searcher);
        setInternalState(serviceImpl, "entityBaseServiceRepository", baseServiceRepositoryImpl);
        final Map<Integer, Integer> industryClassfMap =
                Collections.singletonMap(INTEGER_VALUE_TWO, INTEGER_VALUE_TWO);
        when(industryClassificationMap.getIndustryClassificationMap()).thenReturn(industryClassfMap);
        final List<Entity> entities = new ArrayList<Entity>();
        entities.add(entityPojo);
        when(entityBaseService.autoSuggestForEntity(QUERY_SRT, INPUT_ENTITY_TYPE.INDUSTRY.name(), 2,
                TEXT_STR, industryClassfMap)).thenReturn(entities);
        // Act
        final EntityBriefInfo actual
                = serviceImpl.getEntityMatch(QUERY_SRT, 2, INPUT_ENTITY_TYPE.INDUSTRY, TEXT_STR);
        // Assert
        errorCollector.checkThat(actual.getStatusCode(), equalTo(StatusCode.REQUEST_SUCCESS));
        errorCollector.checkThat(actual.getMatchedEntity().size(), equalTo(2));
        errorCollector.checkThat(actual.getMatchedEntity().get(1).getSearchToken(), equalTo("a"));
        errorCollector.checkThat(actual.getMatchedEntity().get(1).getMatchedType(), equalTo("Context Match"));
    }
}
