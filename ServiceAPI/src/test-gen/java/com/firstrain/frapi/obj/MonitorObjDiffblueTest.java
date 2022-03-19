package com.firstrain.frapi.obj;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.diffblue.deeptestutils.Reflector;
import com.diffblue.deeptestutils.mock.DTUMemberMatcher;
import com.firstrain.solr.client.DocCatEntry;
import com.firstrain.solr.client.EntityEntry;
import com.firstrain.solr.client.HotListEntry;
import com.firstrain.solr.client.SearchTokenEntry;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class MonitorObjDiffblueTest {

  @Rule public final ExpectedException thrown = ExpectedException.none();
  @Rule public final Timeout globalTimeout = new Timeout(10_000);

  /* testedClasses: MonitorObj */
  // Test generated by Diffblue Deeptest.
  @PrepareForTest(EntityEntry.class)
  @Test
  public void constructorInputNotNullOutputVoid2() throws Exception {

    // Arrange
    final EntityEntry entry = PowerMockito.mock(EntityEntry.class);
    final Method getNameMethod = DTUMemberMatcher.method(EntityEntry.class, "getName");
    PowerMockito.doReturn(null).when(entry, getNameMethod).withNoArguments();
    final Method getSearchTokenMethod =
        DTUMemberMatcher.method(EntityEntry.class, "getSearchToken");
    PowerMockito.doReturn(null).when(entry, getSearchTokenMethod).withNoArguments();
    Reflector.setField(entry, "inPositiveFQ", true);
    Reflector.setField(entry, "inNegativeFQ", false);

    // Act, creating object to test constructor
    final MonitorObj objectUnderTest = new MonitorObj(entry);
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest(EntityEntry.class)
  @Test
  public void constructorInputNotNullOutputVoid3() throws Exception {

    // Arrange
    final EntityEntry entry = PowerMockito.mock(EntityEntry.class);
    final Method getNameMethod = DTUMemberMatcher.method(EntityEntry.class, "getName");
    PowerMockito.doReturn(null).when(entry, getNameMethod).withNoArguments();
    final Method getSearchTokenMethod =
        DTUMemberMatcher.method(EntityEntry.class, "getSearchToken");
    PowerMockito.doReturn(null).when(entry, getSearchTokenMethod).withNoArguments();
    Reflector.setField(entry, "inPositiveFQ", true);
    Reflector.setField(entry, "inNegativeFQ", true);

    // Act, creating object to test constructor
    final MonitorObj objectUnderTest = new MonitorObj(entry);
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest(EntityEntry.class)
  @Test
  public void constructorInputNotNullOutputVoid5() throws Exception {

    // Arrange
    final DocCatEntry docEntry =
        (DocCatEntry) Reflector.getInstance("com.firstrain.solr.client.DocCatEntry");
    final EntityEntry entityEntry = PowerMockito.mock(EntityEntry.class);
    final Method getNameMethod = DTUMemberMatcher.method(EntityEntry.class, "getName");
    PowerMockito.doReturn(null).when(entityEntry, getNameMethod).withNoArguments();
    final Method getSearchTokenMethod =
        DTUMemberMatcher.method(EntityEntry.class, "getSearchToken");
    PowerMockito.doReturn(null).when(entityEntry, getSearchTokenMethod).withNoArguments();
    Reflector.setField(entityEntry, "inPositiveFQ", false);
    Reflector.setField(entityEntry, "inNegativeFQ", true);
    Reflector.setField(docEntry, "entity", entityEntry);

    // Act, creating object to test constructor
    final MonitorObj objectUnderTest = new MonitorObj(docEntry);
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest({HotListEntry.class, EntityEntry.class})
  @Test
  public void constructorInputNotNullOutputVoid6() throws Exception {

    // Arrange
    final HotListEntry entry = PowerMockito.mock(HotListEntry.class);
    final EntityEntry entityEntry = PowerMockito.mock(EntityEntry.class);
    final Method getNameMethod = DTUMemberMatcher.method(EntityEntry.class, "getName");
    PowerMockito.doReturn(null).when(entityEntry, getNameMethod).withNoArguments();
    final Method getSearchTokenMethod =
        DTUMemberMatcher.method(EntityEntry.class, "getSearchToken");
    PowerMockito.doReturn(null).when(entityEntry, getSearchTokenMethod).withNoArguments();
    Reflector.setField(entityEntry, "inPositiveFQ", false);
    Reflector.setField(entityEntry, "inNegativeFQ", true);
    final Method getEntityMethod = DTUMemberMatcher.method(HotListEntry.class, "getEntity");
    PowerMockito.doReturn(entityEntry).when(entry, getEntityMethod).withNoArguments();
    Reflector.setField(entry, "entity", null);
    Reflector.setField(entry, "docCount", 0);

    // Act, creating object to test constructor
    final MonitorObj objectUnderTest = new MonitorObj(entry);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void equalsInputNotNullOutputFalse() throws Exception {

    // Arrange
    final MonitorObj objectUnderTest = new MonitorObj();
    final MonitorObj obj = new MonitorObj();
    final Timestamp timestamp = (Timestamp) Reflector.getInstance("java.sql.Timestamp");
    obj.setDate(timestamp);
    obj.setDocumentCount(0);
    obj.setExclude(false);
    obj.setInclude(false);
    obj.setId(null);
    obj.setSelected(false);
    obj.setType(MonitorObj.Type.TYPE_TURNOVER_INTERNAL_MOVE);
    obj.setBizLines(false);
    final Token token = new Token();
    token.setIncluded(false);
    token.setSearchTerm(null);
    final SearchTokenEntry searchTokenEntry =
        (SearchTokenEntry) Reflector.getInstance("com.firstrain.solr.client.SearchTokenEntry");
    token.setToken(searchTokenEntry);
    token.setTitle(null);
    obj.setToken(token);
    obj.setDefaultURL(null);
    obj.setTitle(null);
    obj.setTokenList(null);

    // Act
    final boolean retval = objectUnderTest.equals(obj);

    // Assert
    assertEquals(false, retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void equalsInputNotNullOutputFalse2() throws Exception {

    // Arrange
    final MonitorObj objectUnderTest = new MonitorObj();
    objectUnderTest.setDate(null);
    objectUnderTest.setDocumentCount(0);
    objectUnderTest.setExclude(false);
    objectUnderTest.setInclude(false);
    objectUnderTest.setId(null);
    objectUnderTest.setSelected(false);
    objectUnderTest.setType(null);
    objectUnderTest.setBizLines(false);
    objectUnderTest.setToken(null);
    objectUnderTest.setDefaultURL("!!!!!!!!");
    objectUnderTest.setTitle("::::");
    objectUnderTest.setTokenList(null);
    final MonitorObj obj = new MonitorObj();
    final Timestamp timestamp = (Timestamp) Reflector.getInstance("java.sql.Timestamp");
    obj.setDate(timestamp);
    obj.setDocumentCount(0);
    obj.setExclude(false);
    obj.setInclude(false);
    obj.setId(null);
    obj.setSelected(false);
    obj.setType(MonitorObj.Type.TYPE_TURNOVER_INTERNAL_MOVE);
    obj.setBizLines(false);
    final Token token = new Token();
    token.setIncluded(false);
    token.setSearchTerm(null);
    final SearchTokenEntry searchTokenEntry =
        (SearchTokenEntry) Reflector.getInstance("com.firstrain.solr.client.SearchTokenEntry");
    token.setToken(searchTokenEntry);
    token.setTitle(null);
    obj.setToken(token);
    obj.setDefaultURL(null);
    obj.setTitle("");
    obj.setTokenList(null);

    // Act
    final boolean retval = objectUnderTest.equals(obj);

    // Assert
    assertEquals(false, retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void equalsInputNotNullOutputFalse3() throws Exception {

    // Arrange
    final MonitorObj objectUnderTest = new MonitorObj();
    objectUnderTest.setDate(null);
    objectUnderTest.setDocumentCount(0);
    objectUnderTest.setExclude(false);
    objectUnderTest.setInclude(false);
    objectUnderTest.setId(null);
    objectUnderTest.setSelected(false);
    objectUnderTest.setType(null);
    objectUnderTest.setBizLines(false);
    objectUnderTest.setToken(null);
    objectUnderTest.setDefaultURL("!!!!!!!!");
    objectUnderTest.setTitle("");
    objectUnderTest.setTokenList(null);
    final MonitorObj obj = new MonitorObj();
    final Timestamp timestamp = (Timestamp) Reflector.getInstance("java.sql.Timestamp");
    obj.setDate(timestamp);
    obj.setDocumentCount(0);
    obj.setExclude(false);
    obj.setInclude(false);
    obj.setId(null);
    obj.setSelected(false);
    obj.setType(MonitorObj.Type.TYPE_TURNOVER_INTERNAL_MOVE);
    obj.setBizLines(false);
    final Token token = new Token();
    token.setIncluded(false);
    token.setSearchTerm(null);
    final SearchTokenEntry searchTokenEntry =
        (SearchTokenEntry) Reflector.getInstance("com.firstrain.solr.client.SearchTokenEntry");
    token.setToken(searchTokenEntry);
    token.setTitle(null);
    obj.setToken(token);
    obj.setDefaultURL(null);
    obj.setTitle("");
    obj.setTokenList(null);

    // Act
    final boolean retval = objectUnderTest.equals(obj);

    // Assert
    assertEquals(false, retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void equalsInputNotNullOutputTrue() throws Exception {

    // Arrange
    final MonitorObj objectUnderTest = new MonitorObj();
    objectUnderTest.setDate(null);
    objectUnderTest.setDocumentCount(0);
    objectUnderTest.setExclude(false);
    objectUnderTest.setInclude(false);
    objectUnderTest.setId(null);
    objectUnderTest.setSelected(false);
    objectUnderTest.setType(null);
    objectUnderTest.setBizLines(false);
    objectUnderTest.setToken(null);
    objectUnderTest.setDefaultURL(null);
    objectUnderTest.setTitle("");
    objectUnderTest.setTokenList(null);
    final MonitorObj obj = new MonitorObj();
    final Timestamp timestamp = (Timestamp) Reflector.getInstance("java.sql.Timestamp");
    obj.setDate(timestamp);
    obj.setDocumentCount(0);
    obj.setExclude(false);
    obj.setInclude(false);
    obj.setId(null);
    obj.setSelected(false);
    obj.setType(MonitorObj.Type.TYPE_TURNOVER_INTERNAL_MOVE);
    obj.setBizLines(false);
    final Token token = new Token();
    token.setIncluded(false);
    token.setSearchTerm(null);
    final SearchTokenEntry searchTokenEntry =
        (SearchTokenEntry) Reflector.getInstance("com.firstrain.solr.client.SearchTokenEntry");
    token.setToken(searchTokenEntry);
    token.setTitle(null);
    obj.setToken(token);
    obj.setDefaultURL("");
    obj.setTitle("");
    obj.setTokenList(null);

    // Act
    final boolean retval = objectUnderTest.equals(obj);

    // Assert
    assertEquals(true, retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void equalsInputNotNullOutputTrue2() throws Exception {

    // Arrange
    final MonitorObj objectUnderTest = new MonitorObj();
    objectUnderTest.setDate(null);
    objectUnderTest.setDocumentCount(0);
    objectUnderTest.setExclude(false);
    objectUnderTest.setInclude(false);
    objectUnderTest.setId(null);
    objectUnderTest.setSelected(false);
    objectUnderTest.setType(null);
    objectUnderTest.setBizLines(false);
    objectUnderTest.setToken(null);
    objectUnderTest.setDefaultURL("");
    objectUnderTest.setTitle("");
    objectUnderTest.setTokenList(null);
    final MonitorObj obj = new MonitorObj();
    final Timestamp timestamp = (Timestamp) Reflector.getInstance("java.sql.Timestamp");
    obj.setDate(timestamp);
    obj.setDocumentCount(0);
    obj.setExclude(false);
    obj.setInclude(false);
    obj.setId(null);
    obj.setSelected(false);
    obj.setType(MonitorObj.Type.TYPE_TURNOVER_INTERNAL_MOVE);
    obj.setBizLines(false);
    final Token token = new Token();
    token.setIncluded(false);
    token.setSearchTerm(null);
    final SearchTokenEntry searchTokenEntry =
        (SearchTokenEntry) Reflector.getInstance("com.firstrain.solr.client.SearchTokenEntry");
    token.setToken(searchTokenEntry);
    token.setTitle(null);
    obj.setToken(token);
    obj.setDefaultURL("");
    obj.setTitle("");
    obj.setTokenList(null);

    // Act
    final boolean retval = objectUnderTest.equals(obj);

    // Assert
    assertEquals(true, retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void equalsInputNullOutputFalse() {

    // Arrange
    final MonitorObj objectUnderTest = new MonitorObj();
    objectUnderTest.setDate(null);
    objectUnderTest.setDocumentCount(0);
    objectUnderTest.setExclude(false);
    objectUnderTest.setInclude(false);
    objectUnderTest.setId(null);
    objectUnderTest.setSelected(false);
    objectUnderTest.setType(null);
    objectUnderTest.setBizLines(false);
    objectUnderTest.setToken(null);
    objectUnderTest.setDefaultURL("!!!!!!!!  ");
    objectUnderTest.setTitle("00000000");
    objectUnderTest.setTokenList(null);
    final Object obj = null;

    // Act
    final boolean retval = objectUnderTest.equals(obj);

    // Assert
    assertEquals(false, retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void getAddedSearchesOutputNull() {

    // Arrange
    final MonitorObj.SearchResponse objectUnderTest = new MonitorObj.SearchResponse();
    objectUnderTest.addedSearches = null;
    final ArrayList<MonitorObj> arrayList = new ArrayList<MonitorObj>();
    final MonitorObj monitorObj = new MonitorObj();
    arrayList.add(monitorObj);
    objectUnderTest.duplicateSearches = arrayList;

    // Act
    final List<MonitorObj> retval = objectUnderTest.getAddedSearches();

    // Assert
    assertNull(retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void getBlListOutputNull() {

    // Arrange
    final MonitorObj.TopicNBL objectUnderTest = new MonitorObj.TopicNBL();

    // Act
    final List<Integer> retval = objectUnderTest.getBlList();

    // Assert
    assertNull(retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void getDateOutputNull() {

    // Arrange
    final MonitorObj objectUnderTest = new MonitorObj();

    // Act
    final Timestamp retval = objectUnderTest.getDate();

    // Assert
    assertNull(retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void getDefaultURLOutputNull() {

    // Arrange
    final MonitorObj objectUnderTest = new MonitorObj();

    // Act
    final String retval = objectUnderTest.getDefaultURL();

    // Assert
    assertNull(retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void getDocCountOutputZero() {

    // Arrange
    final MonitorObj.TopicNBL objectUnderTest = new MonitorObj.TopicNBL();

    // Act
    final int retval = objectUnderTest.getDocCount();

    // Assert
    assertEquals(0, retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void getDocumentCountOutputZero() {

    // Arrange
    final MonitorObj objectUnderTest = new MonitorObj();

    // Act
    final int retval = objectUnderTest.getDocumentCount();

    // Assert
    assertEquals(0, retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void getDuplicateSearchesOutput1() {

    // Arrange
    final MonitorObj.SearchResponse objectUnderTest = new MonitorObj.SearchResponse();
    objectUnderTest.addedSearches = null;
    final ArrayList<MonitorObj> arrayList = new ArrayList<MonitorObj>();
    arrayList.add(null);
    objectUnderTest.duplicateSearches = arrayList;

    // Act
    final List<MonitorObj> retval = objectUnderTest.getDuplicateSearches();

    // Assert
    final ArrayList<MonitorObj> arrayList1 = new ArrayList<MonitorObj>();
    arrayList1.add(null);
    assertEquals(arrayList1, retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void getIdOutputNull() {

    // Arrange
    final MonitorObj objectUnderTest = new MonitorObj();

    // Act
    final String retval = objectUnderTest.getId();

    // Assert
    assertNull(retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void getSearchOutputNull() {

    // Arrange
    final MonitorObj.TopicNBL objectUnderTest = new MonitorObj.TopicNBL();

    // Act
    final String retval = objectUnderTest.getSearch();

    // Assert
    assertNull(retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void getSegmentListOutputNull() {

    // Arrange
    final MonitorObj.TopicNBL objectUnderTest = new MonitorObj.TopicNBL();

    // Act
    final Set<Integer> retval = objectUnderTest.getSegmentList();

    // Assert
    assertNull(retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void getTitleOutputNull() {

    // Arrange
    final MonitorObj objectUnderTest = new MonitorObj();

    // Act
    final String retval = objectUnderTest.getTitle();

    // Assert
    assertNull(retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void getTokenListOutputNull() {

    // Arrange
    final MonitorObj objectUnderTest = new MonitorObj();

    // Act
    final List<Token> retval = objectUnderTest.getTokenList();

    // Assert
    assertNull(retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void getTokenOutputNull() {

    // Arrange
    final MonitorObj objectUnderTest = new MonitorObj();

    // Act
    final Token retval = objectUnderTest.getToken();

    // Assert
    assertNull(retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void getTopicListOutputNull() {

    // Arrange
    final MonitorObj.TopicNBL objectUnderTest = new MonitorObj.TopicNBL();

    // Act
    final List<Integer> retval = objectUnderTest.getTopicList();

    // Assert
    assertNull(retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void getTypeOutputNull() {

    // Arrange
    final MonitorObj objectUnderTest = new MonitorObj();

    // Act
    final MonitorObj.Type retval = objectUnderTest.getType();

    // Assert
    assertNull(retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void isBizLinesOutputFalse() {

    // Arrange
    final MonitorObj objectUnderTest = new MonitorObj();

    // Act
    final boolean retval = objectUnderTest.isBizLines();

    // Assert
    assertEquals(false, retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void isExcludeOutputFalse() {

    // Arrange
    final MonitorObj objectUnderTest = new MonitorObj();

    // Act
    final boolean retval = objectUnderTest.isExclude();

    // Assert
    assertEquals(false, retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void isIncludeOutputFalse() {

    // Arrange
    final MonitorObj objectUnderTest = new MonitorObj();

    // Act
    final boolean retval = objectUnderTest.isInclude();

    // Assert
    assertEquals(false, retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void isSelectedOutputFalse() {

    // Arrange
    final MonitorObj objectUnderTest = new MonitorObj();

    // Act
    final boolean retval = objectUnderTest.isSelected();

    // Assert
    assertEquals(false, retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void updateInputNotNullOutputVoid() throws Exception {

    // Arrange
    final MonitorObj objectUnderTest = new MonitorObj();
    objectUnderTest.setDate(null);
    objectUnderTest.setDocumentCount(0);
    objectUnderTest.setExclude(false);
    objectUnderTest.setInclude(false);
    objectUnderTest.setId(null);
    objectUnderTest.setSelected(false);
    objectUnderTest.setType(null);
    objectUnderTest.setBizLines(false);
    objectUnderTest.setToken(null);
    objectUnderTest.setDefaultURL(null);
    objectUnderTest.setTitle(null);
    final ArrayList<Token> arrayList = new ArrayList<Token>();
    arrayList.add(null);
    objectUnderTest.setTokenList(arrayList);
    final EntityEntry entry =
        (EntityEntry) Reflector.getInstance("com.firstrain.solr.client.EntityEntry");
    Reflector.setField(entry, "inPositiveFQ", false);
    Reflector.setField(entry, "inNegativeFQ", true);

    // Act
    objectUnderTest.update(entry);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void updateInputNotNullOutputVoid2() throws Exception {

    // Arrange
    final MonitorObj objectUnderTest = new MonitorObj();
    objectUnderTest.setDate(null);
    objectUnderTest.setDocumentCount(0);
    objectUnderTest.setExclude(false);
    objectUnderTest.setInclude(false);
    objectUnderTest.setId(null);
    objectUnderTest.setSelected(false);
    objectUnderTest.setType(null);
    objectUnderTest.setBizLines(false);
    objectUnderTest.setToken(null);
    objectUnderTest.setDefaultURL(null);
    objectUnderTest.setTitle(null);
    final ArrayList<Token> arrayList = new ArrayList<Token>();
    arrayList.add(null);
    objectUnderTest.setTokenList(arrayList);
    final EntityEntry entry =
        (EntityEntry) Reflector.getInstance("com.firstrain.solr.client.EntityEntry");
    Reflector.setField(entry, "inPositiveFQ", true);
    Reflector.setField(entry, "inNegativeFQ", false);

    // Act
    objectUnderTest.update(entry);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void updateInputNotNullOutputVoid3() throws Exception {

    // Arrange
    final MonitorObj objectUnderTest = new MonitorObj();
    final HotListEntry entry =
        (HotListEntry) Reflector.getInstance("com.firstrain.solr.client.HotListEntry");
    final EntityEntry entityEntry =
        (EntityEntry) Reflector.getInstance("com.firstrain.solr.client.EntityEntry");
    Reflector.setField(entityEntry, "inPositiveFQ", false);
    Reflector.setField(entityEntry, "inNegativeFQ", true);
    Reflector.setField(entry, "entity", entityEntry);
    Reflector.setField(entry, "docCount", 0);

    // Act
    objectUnderTest.update(entry);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void valuesOutput28() {

    // Act
    final MonitorObj.Type[] retval = MonitorObj.Type.values();

    // Assert
    assertArrayEquals(
        new MonitorObj.Type[] {
          MonitorObj.Type.TYPE_REPORT,
          MonitorObj.Type.TYPE_SECTOR,
          MonitorObj.Type.TYPE_SEGMENT,
          MonitorObj.Type.TYPE_INDUSTRY,
          MonitorObj.Type.TYPE_ARTICLE,
          MonitorObj.Type.TYPE_TOPIC,
          MonitorObj.Type.TYPE_SOURCE,
          MonitorObj.Type.TYPE_COMPANY,
          MonitorObj.Type.TYPE_EVENT,
          MonitorObj.Type.TYPE_INDEX,
          MonitorObj.Type.TYPE_BUSINESS_LINE,
          MonitorObj.Type.TYPE_DOCUMENT,
          MonitorObj.Type.TYPE_INDEX_MEMBERSHIP,
          MonitorObj.Type.TYPE_TURNOVER_DEPARTURE,
          MonitorObj.Type.TYPE_TURNOVER_HIRE,
          MonitorObj.Type.TYPE_TURNOVER_INTERNAL_MOVE,
          MonitorObj.Type.TYPE_DEFAULT,
          MonitorObj.Type.TYPE_FILING,
          MonitorObj.Type.TYPE_FILING_10K,
          MonitorObj.Type.TYPE_FILING_10Q,
          MonitorObj.Type.TYPE_FILING_345,
          MonitorObj.Type.TYPE_FILING_8K,
          MonitorObj.Type.TYPE_PERSON,
          MonitorObj.Type.TYPE_MGMT_TURNOVER,
          MonitorObj.Type.TYPE_REGION,
          MonitorObj.Type.TYPE_CONTENT_TYPE,
          MonitorObj.Type.TYPE_MONITOR,
          MonitorObj.Type.TYPE_SECTOR_TOPIC
        },
        retval);
  }
}