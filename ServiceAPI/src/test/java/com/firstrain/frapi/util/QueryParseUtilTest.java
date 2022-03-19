package com.firstrain.frapi.util;

import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import com.firstrain.db.obj.Items;
import com.firstrain.frapi.util.QueryParser.ParseResult;
import com.firstrain.solr.client.SearchResult;
import com.firstrain.solr.client.SearchSpec;
import com.firstrain.solr.client.SolrSearcher;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
    QueryParser.class,
    QueryParseUtil.class
})
public class QueryParseUtilTest {

  @Before
  public void setUp() {
    mockStatic(QueryParser.class);
  }

  @Test
  public void givenParametersWhenParseSearchSpecThenResult() throws Exception{
    //Arrange
    Items items = new Items();
    items.setData("Data");
    List<Items> itemList = Collections.singletonList(items);
    int start = 0;
    int chunkSize = 0;
    ParseResult parseResult = mock(ParseResult.class);
    final List<String> params = Arrays.asList("q", "fq", "days", "b", "tagfq", "start", "num", "order", "scope",
        "lastDay", "groupId", "lcf");
    when(parseResult.getParams()).thenReturn(params);
    when(QueryParser.parseQueryString(anyString())).thenReturn(parseResult);
    MonitorAnalyticsUtilTest.mockParamValues(parseResult, params);
    SolrSearcher solrSearcher = new SolrSearcher();
    whenNew(SolrSearcher.class).withAnyArguments().thenReturn(solrSearcher);
    SearchSpec searchSpec = mock(SearchSpec.class);
    whenNew(SearchSpec.class).withAnyArguments().thenReturn(searchSpec);

    //Act
    final SearchResult searchResult = QueryParseUtil.parseSearchSpec(itemList, start, chunkSize);

    //Assert
    Assert.assertEquals(searchSpec, searchResult.searchSpec);
  }
}
