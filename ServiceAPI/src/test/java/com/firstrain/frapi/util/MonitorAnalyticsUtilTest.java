package com.firstrain.frapi.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.reflect.internal.WhiteboxImpl.setInternalState;

import com.firstrain.db.obj.Items;
import com.firstrain.frapi.obj.MonitorBriefDomain;
import com.firstrain.frapi.obj.MonitorWizardFilters;
import com.firstrain.frapi.obj.MonitorWizardFilters.Advanced;
import com.firstrain.frapi.repository.EntityBaseServiceRepository;
import com.firstrain.frapi.repository.impl.EntityBaseServiceRepositoryImpl;
import com.firstrain.frapi.util.QueryParser.ParseResult;
import com.firstrain.obj.IEntityInfo;
import com.firstrain.obj.IEntityInfoCache;
import com.firstrain.solr.client.SearchTokenEntry;
import com.firstrain.solr.client.SearchTokenEntry.Relation;
import com.firstrain.solr.client.SolrSearcher;
import com.firstrain.utils.JSONUtility;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
    JSONUtility.class,
    QueryParser.class,
    SolrSearcher.class
})
public class MonitorAnalyticsUtilTest {

  private MonitorAnalyticsUtil monitorAnalyticsUtil;
  private List<Items> itemList;
  private long monitorId;
  private IEntityInfoCache entityInfoCache;
  private String filterString;
  private String fqInput;
  private boolean needIndustryEntities = true;
  @Rule
  public final ErrorCollector collector = new ErrorCollector();

  @Before
  public void setUp() {
    monitorAnalyticsUtil = new MonitorAnalyticsUtil();
    ServicesAPIUtil servicesAPIUtil = new ServicesAPIUtil();
    setInternalState(monitorAnalyticsUtil, "servicesAPIUtil", servicesAPIUtil);
    ConvertUtil convertUtil = new ConvertUtil();
    setInternalState(monitorAnalyticsUtil, "convertUtil", convertUtil);
    EntityBaseServiceRepository entityBaseServiceRepository = new EntityBaseServiceRepositoryImpl();
    setInternalState(monitorAnalyticsUtil, "entityBaseServiceRepository", entityBaseServiceRepository);
    mockStatic(JSONUtility.class);
    mockStatic(QueryParser.class);
    mockStatic(SolrSearcher.class);
    entityInfoCache = mock(IEntityInfoCache.class);
  }

  @Test
  public void givenParametersEmptyItemListWhenGetMonitorBriefDomainFromFolderIdThenResults()
      throws Exception{
    //Act
    MonitorBriefDomain monitorBriefDomain =  monitorAnalyticsUtil.getMonitorBriefDomainFromFolderId(itemList, monitorId,
        entityInfoCache, filterString, fqInput, needIndustryEntities);

    //Assert
    Assert.assertNull(monitorBriefDomain);
  }

  @Test
  public void givenParametersOneEntryWhenGetMonitorBriefDomainFromFolderIdThenResults()
      throws Exception{
    //Arrange
    Items items = new Items();
    String data = "Data";
    items.setData(data);
    itemList = Collections.singletonList(items);
    filterString = "filter";
    fqInput = "fqInput";
    monitorId = 67L;
    MonitorWizardFilters monitorWizardFilters = new MonitorWizardFilters();
    monitorWizardFilters.advanced = new Advanced();
    monitorWizardFilters.advanced.advancedFilters = Collections.singletonList("Advanced");
    monitorWizardFilters.advanced.keywords = "Keyword";
    when(JSONUtility.deserialize(filterString, MonitorWizardFilters.class)).thenReturn(monitorWizardFilters);
    ParseResult parseResult = mock(ParseResult.class);
    final List<String> params = Arrays.asList("q", "fq", "days", "b", "tagfq", "start", "num", "order", "scope",
        "lastDay", "groupId", "lcf");
    when(parseResult.getParams()).thenReturn(params);
    when(QueryParser.parseQueryString(anyString())).thenReturn(parseResult);
    mockParamValues(parseResult, params);
    SearchTokenEntry searchTokenEntry = new SearchTokenEntry();
    searchTokenEntry.searchToken = "SearchToken";
    List<SearchTokenEntry> tokens = Collections.singletonList(searchTokenEntry);
    when(SolrSearcher.parseInput(anyString())).thenReturn(tokens);
    IEntityInfo iEntityInfo = mock(IEntityInfo.class);
    when(iEntityInfo.getCompanyId()).thenReturn(33);
    when(entityInfoCache.searchTokenToEntity(tokens.get(0).getSearchToken())).thenReturn(iEntityInfo);

    //Act
    MonitorBriefDomain monitorBriefDomain =  monitorAnalyticsUtil.getMonitorBriefDomainFromFolderId(itemList, monitorId,
        entityInfoCache, filterString, fqInput, needIndustryEntities);

    //Assert
    collector.checkThat(monitorBriefDomain.getItemList().get(0).getData(), is(data));
    collector.checkThat(monitorBriefDomain.getqList()[0], is("qValue fqValue AND fqInput"));
  }

  @Test
  public void givenMultipleParametersOneEntryWhenGetMonitorBriefDomainFromFolderIdThenResults()
      throws Exception{
    //Arrange
    Items items = new Items();
    String data = "Data";
    items.setData(data);
    itemList = Collections.singletonList(items);
    filterString = "filter";
    fqInput = "fqInput";
    monitorId = 67L;
    MonitorWizardFilters monitorWizardFilters = new MonitorWizardFilters();
    monitorWizardFilters.advanced = new Advanced();
    monitorWizardFilters.advanced.advancedFilters = Collections.singletonList("Advanced");
    monitorWizardFilters.advanced.keywords = "Keyword";
    when(JSONUtility.deserialize(filterString, MonitorWizardFilters.class)).thenReturn(monitorWizardFilters);
    ParseResult parseResult = mock(ParseResult.class);
    final List<String> params = Arrays.asList("q", "fq", "days", "b", "tagfq", "start", "num", "order", "scope",
        "lastDay", "groupId", "lcf");
    when(parseResult.getParams()).thenReturn(params);
    when(QueryParser.parseQueryString(anyString())).thenReturn(parseResult);
    mockParamValues(parseResult, params);
    List<SearchTokenEntry> tokens = mockSearchTokenList();
    when(SolrSearcher.parseInput(anyString())).thenReturn(tokens);

    //Act
    MonitorBriefDomain monitorBriefDomain =  monitorAnalyticsUtil.getMonitorBriefDomainFromFolderId(itemList, monitorId,
        entityInfoCache, filterString, fqInput, needIndustryEntities);

    //Assert
    collector.checkThat(monitorBriefDomain.getqList()[0], is("qValue fqValue AND fqInput"));
    collector.checkThat(monitorBriefDomain.getCompanyIdsArr()[0], is(1));
    collector.checkThat(monitorBriefDomain.getBizlineCatIdCSV(), is(nullValue()));
    collector.checkThat(monitorBriefDomain.getItemList().get(0).getData(), is(data));
    collector.checkThat(monitorBriefDomain.getIndustryCatId(), is(90));
    collector.checkThat(monitorBriefDomain.getScopeList()[0], is(65));
  }

  private List<SearchTokenEntry> mockSearchTokenList() {
    SearchTokenEntry searchTokenEntry1 = new SearchTokenEntry();
    searchTokenEntry1.searchToken = "SearchToken1";
    searchTokenEntry1.relation = Relation.MUST_HAVE;
    IEntityInfo iEntityInfo1 = mock(IEntityInfo.class);
    when(iEntityInfo1.getType()).thenReturn(SearchTokenEntry.SEARCH_TOKEN_TAG_COMPANY);
    when(iEntityInfo1.getId()).thenReturn("ientity1");
    when(iEntityInfo1.getCompanyId()).thenReturn(1);
    when(iEntityInfo1.getIndustryCatId()).thenReturn(13);
    when(iEntityInfo1.getSecondaryIndustry()).thenReturn(Collections.singleton(90));
    when(iEntityInfo1.getSecondarySegment()).thenReturn(Collections.singleton(34));
    when(iEntityInfo1.getSecondarySector()).thenReturn(Collections.singleton(32));
    when(iEntityInfo1.getSegmentCatId()).thenReturn(6);
    when(iEntityInfo1.getSectorCatId()).thenReturn(5);
    when(entityInfoCache.searchTokenToEntity(searchTokenEntry1.searchToken)).thenReturn(iEntityInfo1);
    SearchTokenEntry searchTokenEntry3 = new SearchTokenEntry();
    searchTokenEntry3.searchToken = "SearchToken3";
    searchTokenEntry3.relation = Relation.MUST_HAVE;
    IEntityInfo iEntityInfo3 = mock(IEntityInfo.class);
    when(iEntityInfo3.getType()).thenReturn(SearchTokenEntry.SEARCH_TOKEN_TAG_INDUSTRY);
    when(iEntityInfo3.getId()).thenReturn("ientity3");
    when(iEntityInfo3.getIndustryCatId()).thenReturn(13);
    when(iEntityInfo3.getSegmentCatId()).thenReturn(6);
    when(iEntityInfo3.getSecondaryIndustry()).thenReturn(Collections.singleton(90));
    when(iEntityInfo3.getSecondarySegment()).thenReturn(Collections.singleton(34));
    when(iEntityInfo3.getSecondarySector()).thenReturn(Collections.singleton(32));
    when(iEntityInfo3.getSectorCatId()).thenReturn(8);
    when(entityInfoCache.searchTokenToEntity(searchTokenEntry3.searchToken)).thenReturn(iEntityInfo3);
    SearchTokenEntry searchTokenEntry4 = new SearchTokenEntry();
    searchTokenEntry4.searchToken = "SearchToken4";
    searchTokenEntry4.relation = Relation.MUST_HAVE;
    IEntityInfo iEntityInfo4 = mock(IEntityInfo.class);
    when(iEntityInfo4.getType()).thenReturn(SearchTokenEntry.SEARCH_TOKEN_TAG_BIZLINE);
    when(iEntityInfo4.getId()).thenReturn("ientity4");
    when(entityInfoCache.searchTokenToEntity(searchTokenEntry4.searchToken)).thenReturn(iEntityInfo4);
    List<SearchTokenEntry> tokens = new ArrayList<SearchTokenEntry>();
    tokens.add(searchTokenEntry1);
    tokens.add(searchTokenEntry3);
    tokens.add(searchTokenEntry4);
    return tokens;
  }

  public static void mockParamValues(ParseResult parseResult, List<String> params) {
    final List<String> qparamValues = Collections.singletonList("qValue");
    when(parseResult.getParamValues(params.get(0))).thenReturn(qparamValues);
    final List<String> fqparamValues = Collections.singletonList("fqValue");
    when(parseResult.getParamValues(params.get(1))).thenReturn(fqparamValues);
    final List<String> daysparamValues = Collections.singletonList("6");
    when(parseResult.getParamValues(params.get(2))).thenReturn(daysparamValues);
    final List<String> bucketStrValues = Collections.singletonList("bucketStrValue");
    when(parseResult.getParamValues(params.get(3))).thenReturn(bucketStrValues);
    final List<String> tagfqValues = Collections.singletonList("tagfqValue");
    when(parseResult.getParamValues(params.get(4))).thenReturn(tagfqValues);
    final List<String> startValues = Collections.singletonList("5");
    when(parseResult.getParamValues(params.get(5))).thenReturn(startValues);
    final List<String> numValues = Collections.singletonList("9");
    when(parseResult.getParamValues(params.get(6))).thenReturn(numValues);
    final List<String> orderValues = Collections.singletonList("date");
    when(parseResult.getParamValues(params.get(7))).thenReturn(orderValues);
    final List<String> scopeValues = Collections.singletonList("65");
    when(parseResult.getParamValues(params.get(8))).thenReturn(scopeValues);
    final List<String> lastDayValues = Collections.singletonList("Monday");
    when(parseResult.getParamValues(params.get(9))).thenReturn(lastDayValues);
    final List<String> groupIdValues = Collections.singletonList("51");
    when(parseResult.getParamValues(params.get(10))).thenReturn(groupIdValues);
    final List<String> lcfValues = Collections.singletonList("63");
    when(parseResult.getParamValues(params.get(11))).thenReturn(lcfValues);
  }
}
