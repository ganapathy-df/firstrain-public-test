package com.firstrain.frapi.service.impl;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.when;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import com.firstrain.frapi.domain.BaseSpec;
import com.firstrain.frapi.domain.BlendDunsInput;
import com.firstrain.frapi.pojo.wrapper.DocumentSet;
import com.firstrain.frapi.repository.EntityBaseServiceRepository;
import com.firstrain.frapi.repository.ExcelProcessingHelperRepository;
import com.firstrain.frapi.service.EntityBaseService;
import com.firstrain.frapi.util.ConvertUtil;
import com.firstrain.obj.IEntityInfo;
import com.firstrain.obj.IEntityInfoCache;
import com.firstrain.solr.client.DocCatEntry;
import com.firstrain.solr.client.DocEntry;
import com.firstrain.solr.client.DocListBucket;
import com.firstrain.solr.client.EntityEntry;
import com.firstrain.solr.client.HotListBucket;
import com.firstrain.solr.client.QuoteEntry;
import com.firstrain.solr.client.SearchException;
import com.firstrain.solr.client.SearchResult;
import com.firstrain.solr.client.SearchSpec;
import com.firstrain.solr.client.SolrSearcher;
import com.firstrain.web.pojo.SearchResultWeb;

@RunWith(PowerMockRunner.class)
public class EntityBreifCustomServiceImplTest {

    private static final Date CURRENT_DATE = new Date();
    private static final String SITEDOC_ID2 = "sitedocId2";
    private static final String SITEDOC_ID1 = "sitedocId1";
    private static final String SITEDOC_ID = "sitedocId";
    private static final int FIRST_INDEX = 0;
    private static final String BUCKET_NAME = "bucket_name";
    private static final Integer SCOPE = 100;
    private static final Integer COUNT = 2;
    private static final Integer DAYS_COUNT = 30;
    private static final Long PRIMARY_CAT_ID = 1l;
    private static final Long CAT_ID = 123l;
    private static final Long CAT_ID_2 = 124l;
    private static final int INTEGER_VALUE_TWO = 2;

    private SearchResult searchResult = new SearchResult();

    @Mock
    private DocEntry docEntry;
    @Mock
    private DocEntry docEntry_1;
    @Mock
    private DocEntry docEntry_2;
    @Mock
    private EntityBaseService entityBaseService;
    @Mock
    private EntityBaseServiceRepository entityBaseServiceRepository;
    @Mock
    private ExcelProcessingHelperRepository excelProcesingHelperRepository;
    @Mock
    private IEntityInfoCache infoCache;
    @Mock
    private ConvertUtil convertUtil = new ConvertUtil();
    @Mock
    private IEntityInfo entityInfo;
    @Mock
    private SolrSearcher solrSearch;
    @InjectMocks
    EntityBreifCustomServiceImpl serviceImpl = new EntityBreifCustomServiceImpl();
    @Mock
    private EntityEntry entity;
    @Rule
    public final ErrorCollector errorCollector = new ErrorCollector();
    
    @Before
    public void setUp() throws SearchException {
        when(entityBaseServiceRepository.getEntityInfoCache()).thenReturn(infoCache);
        when(infoCache.catIdToEntity(anyString())).thenReturn(entityInfo);
        when(entityBaseServiceRepository.getSearcher()).thenReturn(solrSearch);
        when(solrSearch.search(any(SearchSpec.class))).thenReturn(searchResult);
        serviceImpl.init();
    }

    @Test
    public void givenGetCoMentionCompaniesWhenInputsThenReturnBucket() throws Exception {
        // Arrange
        List<Long> catIds = prepareCatIds();
        HotListBucket bucket = new HotListBucket(BUCKET_NAME);
        searchResult.facetCompanies = bucket;
        // Act
        HotListBucket actual = serviceImpl.getCoMentionCompanies(catIds, SCOPE, COUNT, DAYS_COUNT);
        // Assert
        assertEquals(bucket, actual);
    }

    @Test
    public void givenGetWebResultsForCatIdWhenValidInputsThenReturnSearchResult() throws Exception {
        // Arrange
        List<Long> secondaryCatIds = prepareCatIds();
        DocumentSet documentSet = new DocumentSet();
        when(entityBaseService.getWebResultsForSearch(anyString(), anyString(), any(BaseSpec.class), any(BlendDunsInput.class)))
                .thenReturn(documentSet);
        // Act
        SearchResultWeb resultWeb = serviceImpl.getWebResultsForCatId(secondaryCatIds, SCOPE, COUNT, DAYS_COUNT);
        // Assert
        assertEquals(documentSet, resultWeb.getDocumentSet());
    }

    @Test
    public void givenGetWebResultsWhenNullInfoFoundThenReturnSortedSearchResult() throws Exception {
        // Arrange
        List<Long> secondaryCatIds = prepareCatIds();
        when(infoCache.catIdToEntity(anyString())).thenReturn(null);
        // Act
        SearchResultWeb searchResult =
                serviceImpl.getWebResults(PRIMARY_CAT_ID, secondaryCatIds, SCOPE, COUNT, DAYS_COUNT, false);
        // Assert
        errorCollector.checkThat(searchResult, notNullValue());
        errorCollector.checkThat(searchResult.getDocumentSet(), nullValue());
    }

    @Test
    public void givenGetWebResultsWhenInfoFoundThenReturnNewSearchResult() throws Exception {
        // Arrange
        List<Long> secondaryCatIds = prepareCatIds();
        when(infoCache.catIdToEntity(String.valueOf(secondaryCatIds.get(FIRST_INDEX)))).thenReturn(null);
        arrangeForgetWebResults();

        // Act
        SearchResultWeb searchResult =
                serviceImpl.getWebResults(PRIMARY_CAT_ID, secondaryCatIds, SCOPE, FIRST_INDEX, DAYS_COUNT, true);
        // Assert
        errorCollector.checkThat(searchResult, notNullValue());
        errorCollector.checkThat(searchResult.getDocumentSet(), notNullValue());
    }

    @Test
    public void givenGetWebResultsWhenInfoFoundThenReturnSearchResultNotStorted() throws Exception {
        // Arrange
        List<Long> secondaryCatIds = prepareCatIds();
        when(infoCache.catIdToEntity(String.valueOf(secondaryCatIds.get(FIRST_INDEX)))).thenReturn(null);
        arrangeForgetWebResults();
        // Act
        SearchResultWeb searchResult =
                serviceImpl.getWebResults(PRIMARY_CAT_ID, secondaryCatIds, SCOPE, 1, DAYS_COUNT, false);
        // Assert
        assertNotNull(searchResult);
        assertNotNull(searchResult.getDocumentSet());
        errorCollector.checkThat(searchResult, notNullValue());
        errorCollector.checkThat(searchResult.getDocumentSet(), notNullValue());
    }

    private void arrangeForgetWebResults() {
        short shorta = 1;
        when(docEntry.getTitle()).thenReturn(BUCKET_NAME);
        when(docEntry_1.getTitle()).thenReturn(BUCKET_NAME);
        when(docEntry_2.getTitle()).thenReturn(BUCKET_NAME);
        when(docEntry.getSummary()).thenReturn(BUCKET_NAME);
        when(docEntry_1.getSummary()).thenReturn(BUCKET_NAME);
        when(docEntry_2.getSummary()).thenReturn(BUCKET_NAME);
        when(docEntry.getSourceEntity()).thenReturn(entity);
        when(docEntry_1.getSourceEntity()).thenReturn(entity);
        when(docEntry_2.getSourceEntity()).thenReturn(entity);
        when(entity.getId()).thenReturn("2");
        when(docEntry.getInsertTime()).thenReturn(CURRENT_DATE);
        when(docEntry_1.getInsertTime()).thenReturn(CURRENT_DATE);
        when(docEntry_2.getInsertTime()).thenReturn(CURRENT_DATE);

        ArrayList<DocCatEntry> docCatEntries = new ArrayList();
        docCatEntries.add(new DocCatEntry(entity, shorta, shorta));
        when(docEntry.getCatEntries()).thenReturn(docCatEntries);
        when(docEntry_1.getCatEntries()).thenReturn(docCatEntries);
        when(docEntry_2.getCatEntries()).thenReturn(docCatEntries);

        docEntry.matchedOthers = docCatEntries;
        docEntry_1.matchedOthers = docCatEntries;
        docEntry_2.matchedOthers = docCatEntries;
        List<QuoteEntry> value = new ArrayList();
        value.add(new QuoteEntry(BUCKET_NAME, BUCKET_NAME, shorta, null));
        when(docEntry.getOtrQuotes()).thenReturn(value);
        when(docEntry_1.getOtrQuotes()).thenReturn(value);
        when(docEntry_2.getOtrQuotes()).thenReturn(value);
        List<DocListBucket> buckets = new ArrayList();
        List<DocEntry> entries = new ArrayList();
        docEntry.sitedocId = SITEDOC_ID;
        docEntry_1.sitedocId = SITEDOC_ID1;
        docEntry_2.sitedocId = SITEDOC_ID2;
        docEntry.insertTime = CURRENT_DATE;
        docEntry_1.insertTime = CURRENT_DATE;
        docEntry_2.insertTime = CURRENT_DATE;
        docEntry.bodyLength = INTEGER_VALUE_TWO;
        docEntry_1.bodyLength = INTEGER_VALUE_TWO;
        docEntry_2.bodyLength = INTEGER_VALUE_TWO;
        docEntry.docScore = INTEGER_VALUE_TWO;
        docEntry_1.docScore = INTEGER_VALUE_TWO;
        docEntry_2.docScore = INTEGER_VALUE_TWO;
        docEntry.title = "";
        docEntry_1.title = "";
        docEntry_2.title = "";

        entries.add(docEntry);
        entries.add(docEntry_1);
        entries.add(docEntry_2);
        buckets.add(new DocListBucket(null, entries, INTEGER_VALUE_TWO, 0));
        buckets.add(new DocListBucket(null, entries, INTEGER_VALUE_TWO, 0));
        searchResult.buckets = buckets;
    }

    private List<Long> prepareCatIds() {
        List<Long> catIds = new ArrayList();
        catIds.add(CAT_ID);
        catIds.add(CAT_ID_2);
        return catIds;
    }

}
