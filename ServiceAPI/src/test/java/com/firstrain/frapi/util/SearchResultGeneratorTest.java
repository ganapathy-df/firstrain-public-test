package com.firstrain.frapi.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import org.junit.Before;
import org.junit.Test;
import com.firstrain.frapi.domain.BaseSpec;
import com.firstrain.solr.client.DocEntry;
import com.firstrain.solr.client.DocListBucket;
import com.firstrain.solr.client.SearchResult;

public class SearchResultGeneratorTest {

    private Future<SearchResult> futureSearchResult60Days;
    private Future<SearchResult> futureSearchResult180Days;
    private BaseSpec baseSpec;
    private SearchResult searchResult;
    private List<DocListBucket> docListBuckets;
    private DocListBucket docListBucket;

    @Before
    public void setUp() {
        futureSearchResult60Days = mock(Future.class);
        futureSearchResult180Days = mock(Future.class);
        baseSpec = mock(BaseSpec.class);
        searchResult = mock(SearchResult.class);
        docListBuckets = mock(List.class);
        docListBucket = mock(DocListBucket.class);
        searchResult.buckets = docListBuckets;
        docListBucket.docs = generateDocEntries();
    }

    @Test
    public void givenShortValueWhenCollectSearchResultsThenResultIsNotNull() throws Exception {
        // Arrange
        when(baseSpec.getCount()).thenReturn((short) 2);
        arrangeConditionForCollectSearchResults();

        actAndAssertNullForCollectSearchResults();
    }

    @Test
    public void givenShortValueWhenCollectSearchResultsThenResultIsNull() throws Exception {
        // Arrange
        when(baseSpec.getCount()).thenReturn((short) -2);
        arrangeConditionForCollectSearchResults();

        actAndAssertNotNullForCollectSearchResults();
    }

    private void arrangeConditionForCollectSearchResults() throws Exception {
        when(docListBuckets.get(0)).thenReturn(docListBucket);
        when(futureSearchResult60Days.get()).thenReturn(searchResult);
    }

    @Test
    public void givenConditionsWhenCollectSearchResultsRunsThenResultIsNull() throws Exception {
        // Arrange
        when(docListBuckets.get(0)).thenReturn(docListBucket);
        when(futureSearchResult60Days.get()).thenReturn(searchResult);

        actAndAssertNotNullForCollectSearchResults();
    }

    @Test
    public void givenConditionsCollectSearchResultsRunsWhenThenResultIsNull() throws Exception {
        // Arrange
        futureSearchResult60Days = null;

        actAndAssertNullForCollectSearchResults();
    }

    private void actAndAssertNotNullForCollectSearchResults() throws Exception {
        // Act
        SearchResult result = SearchResultGenerator.collectSearchResults(futureSearchResult60Days,
                futureSearchResult180Days, baseSpec);

        // Assert
        assertNotNull(result);
    }

    private void actAndAssertNullForCollectSearchResults() throws Exception {
        // Act
        SearchResult result = SearchResultGenerator.collectSearchResults(futureSearchResult60Days,
                futureSearchResult180Days, baseSpec);

        // Assert
        assertNull(result);
    }

    private List<DocEntry> generateDocEntries() {
        List<DocEntry> docEntries = new ArrayList<DocEntry>();
        docEntries.add(new DocEntry());
        return docEntries;
    }
}
