package com.firstrain.frapi.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.AdditionalMatchers.or;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.isA;
import static org.mockito.Matchers.isNull;

import com.diffblue.deeptestutils.Reflector;
import com.diffblue.deeptestutils.mock.DTUMemberMatcher;
import com.firstrain.frapi.domain.BaseSpec;
import com.firstrain.frapi.domain.MgmtTurnoverData;
import com.firstrain.frapi.domain.MgmtTurnoverSummary;
import com.firstrain.frapi.obj.MgmtTurnoverServiceSpec;
import com.firstrain.frapi.obj.SearchTokenSpec;
import com.firstrain.frapi.pojo.Entity;
import com.firstrain.frapi.repository.CompanyServiceRepository;
import com.firstrain.frapi.service.EntityBaseService;
import com.firstrain.frapi.service.EventService;
import com.firstrain.frapi.util.ConvertUtil;
import com.firstrain.obj.IEntityInfo;
import com.firstrain.obj.IEntityInfoCache;
import com.firstrain.utils.FR_ArrayUtils;
import com.google.common.collect.ImmutableList;
import com.google.common.net.InternetDomainName;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.solr.common.SolrDocument;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.mockito.expectation.PowerMockitoStubber;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.management.*"})
public class CompanyServiceImplDiffblueTest {

  @Rule public final ExpectedException thrown = ExpectedException.none();
  @Rule public final Timeout globalTimeout = new Timeout(10_000);

  /* testedClasses: CompanyServiceImpl */
  // Test generated by Diffblue Deeptest.
  @PrepareForTest(Entity.class)
  @Test
  public void compareInputNotNullNotNullOutputZero() throws Exception {

    // Arrange
    final Object objectUnderTest =
        Reflector.getInstance("com.firstrain.frapi.service.impl.CompanyServiceImpl$1");
    Reflector.setField(objectUnderTest, "this$0", null);
    final Entity o1 = PowerMockito.mock(Entity.class);
    final Method getBandMethod = DTUMemberMatcher.method(Entity.class, "getBand");
    PowerMockito.doReturn(0).when(o1, getBandMethod).withNoArguments();
    final Entity o2 = PowerMockito.mock(Entity.class);
    final Method getBandMethod1 = DTUMemberMatcher.method(Entity.class, "getBand");
    PowerMockito.doReturn(0).when(o2, getBandMethod1).withNoArguments();

    // Act
    final Class<?> classUnderTest =
        Reflector.forName("com.firstrain.frapi.service.impl.CompanyServiceImpl$1");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod(
            "compare",
            Reflector.forName("com.firstrain.frapi.pojo.Entity"),
            Reflector.forName("com.firstrain.frapi.pojo.Entity"));
    methodUnderTest.setAccessible(true);
    final int retval = (int) methodUnderTest.invoke(objectUnderTest, o1, o2);

    // Assert
    assertEquals(0, retval);
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest(CompanyServiceRepository.class)
  @Test
  public void getCompanyDocumentsInputNullOutputNull() throws Exception {

    // Arrange
    final CompanyServiceImpl objectUnderTest = new CompanyServiceImpl();
    Reflector.setField(objectUnderTest, "convertUtil", null);
    Reflector.setField(objectUnderTest, "entityBaseService", null);
    Reflector.setField(objectUnderTest, "eventService", null);
    Reflector.setField(objectUnderTest, "LOG", null);
    final CompanyServiceRepository companyServiceRepository =
        PowerMockito.mock(CompanyServiceRepository.class);
    PowerMockito.when(
            companyServiceRepository.getCompanyInfoFromIndex(
                or(isA(String.class), isNull(String.class))))
        .thenReturn(null);
    Reflector.setField(objectUnderTest, "companyServiceRepository", companyServiceRepository);
    final String searchToken = null;

    // Act
    final SolrDocument retval = objectUnderTest.getCompanyDocuments(searchToken);

    // Assert
    assertNull(retval);
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest({
    ConvertUtil.class, IEntityInfoCache.class, CompanyServiceRepository.class,
    Entity.class, IEntityInfo.class, SolrDocument.class
  })
  @Test
  public void getEntityFromSolrDocInputNotNullZeroOutputNotNull() throws Exception {

    // Arrange
    final CompanyServiceImpl objectUnderTest = new CompanyServiceImpl();
    final ConvertUtil convertUtil = new ConvertUtil();
    Reflector.setField(convertUtil, "MIN_SUMMARY_LENGTH", 0);
    Reflector.setField(convertUtil, "SUMMARY_TRIM_FACTOR", 0.0f);
    Reflector.setField(convertUtil, "MAX_SUMMARY_LENGTH", 0);
    final Logger logger = (Logger) Reflector.getInstance("org.apache.log4j.Logger");
    Reflector.setField(convertUtil, "LOG", logger);
    Reflector.setField(objectUnderTest, "convertUtil", convertUtil);
    Reflector.setField(objectUnderTest, "entityBaseService", null);
    final EventService eventService =
        (EventService) Reflector.getInstance("com.firstrain.frapi.service.EventService");
    Reflector.setField(objectUnderTest, "eventService", eventService);
    Reflector.setField(objectUnderTest, "LOG", null);
    final CompanyServiceRepository companyServiceRepository =
        PowerMockito.mock(CompanyServiceRepository.class);
    final IEntityInfoCache iEntityInfoCache = PowerMockito.mock(IEntityInfoCache.class);
    final IEntityInfo iEntityInfo = PowerMockito.mock(IEntityInfo.class);
    final Method getNameMethod = DTUMemberMatcher.method(IEntityInfo.class, "getName");
    PowerMockito.doReturn(null).when(iEntityInfo, getNameMethod).withNoArguments();
    final Method getCompanyIdMethod = DTUMemberMatcher.method(IEntityInfo.class, "getCompanyId");
    ((PowerMockitoStubber) PowerMockito.doReturn(0).doReturn(0))
        .when(iEntityInfo, getCompanyIdMethod)
        .withNoArguments();
    final Method getIdMethod = DTUMemberMatcher.method(IEntityInfo.class, "getId");
    PowerMockito.doReturn("!!!!!").when(iEntityInfo, getIdMethod).withNoArguments();
    final Method getDocCountMethod = DTUMemberMatcher.method(IEntityInfo.class, "getDocCount");
    PowerMockito.doReturn(0).when(iEntityInfo, getDocCountMethod).withNoArguments();
    final Method getScopeMethod = DTUMemberMatcher.method(IEntityInfo.class, "getScope");
    ((PowerMockitoStubber) PowerMockito.doReturn(0).doReturn(0))
        .when(iEntityInfo, getScopeMethod)
        .withNoArguments();
    final Method getSearchTokenMethod =
        DTUMemberMatcher.method(IEntityInfo.class, "getSearchToken");
    PowerMockito.doReturn(null).when(iEntityInfo, getSearchTokenMethod).withNoArguments();
    final Method getPrimaryTickerMethod =
        DTUMemberMatcher.method(IEntityInfo.class, "getPrimaryTicker");
    PowerMockito.doReturn(null).when(iEntityInfo, getPrimaryTickerMethod).withNoArguments();
    final Method companyIdToEntityMethod =
        DTUMemberMatcher.method(IEntityInfoCache.class, "companyIdToEntity", int.class);
    PowerMockito.doReturn(iEntityInfo)
        .when(iEntityInfoCache, companyIdToEntityMethod)
        .withArguments(anyInt());
    final Method getEntityInfoCacheMethod =
        DTUMemberMatcher.method(CompanyServiceRepository.class, "getEntityInfoCache");
    PowerMockito.doReturn(iEntityInfoCache)
        .when(companyServiceRepository, getEntityInfoCacheMethod)
        .withNoArguments();
    Reflector.setField(objectUnderTest, "companyServiceRepository", companyServiceRepository);
    final SolrDocument doc = PowerMockito.mock(SolrDocument.class);
    final Method getFieldValueMethod =
        DTUMemberMatcher.method(SolrDocument.class, "getFieldValue", String.class);
    ((PowerMockitoStubber) PowerMockito.doReturn("9").doReturn(""))
        .when(doc, getFieldValueMethod)
        .withArguments(or(isA(String.class), isNull(String.class)));
    final int confidence = 0;
    final Entity entity = PowerMockito.mock(Entity.class);
    PowerMockito.whenNew(Entity.class).withNoArguments().thenReturn(entity);

    // Act
    final Class<?> classUnderTest =
        Reflector.forName("com.firstrain.frapi.service.impl.CompanyServiceImpl");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod(
            "getEntityFromSolrDoc",
            Reflector.forName("org.apache.solr.common.SolrDocument"),
            Reflector.forName("int"));
    methodUnderTest.setAccessible(true);
    final Entity retval = (Entity) methodUnderTest.invoke(objectUnderTest, doc, confidence);

    // Assert
    assertNotNull(retval);
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest({
    Logger.class, FR_ArrayUtils.class, MgmtTurnoverSummary.class,
    CompanyServiceRepository.class, CompanyServiceImpl.class, IEntityInfoCache.class,
    MgmtTurnoverData.class, BaseSpec.class, IEntityInfo.class
  })
  @Test
  public void getMgmtTurnoverDataInputNullNotNullOutputNotNull() throws Exception {

    // Setup mocks
    PowerMockito.mockStatic(FR_ArrayUtils.class);

    // Arrange
    final CompanyServiceImpl objectUnderTest =
        (CompanyServiceImpl)
            Reflector.getInstance("com.firstrain.frapi.service.impl.CompanyServiceImpl");
    final ConvertUtil convertUtil =
        (ConvertUtil) Reflector.getInstance("com.firstrain.frapi.util.ConvertUtil");
    Reflector.setField(convertUtil, "MIN_SUMMARY_LENGTH", 0);
    Reflector.setField(convertUtil, "SUMMARY_TRIM_FACTOR", 0.0f);
    Reflector.setField(convertUtil, "MAX_SUMMARY_LENGTH", 0);
    Reflector.setField(convertUtil, "LOG", null);
    Reflector.setField(objectUnderTest, "convertUtil", convertUtil);
    Reflector.setField(objectUnderTest, "entityBaseService", null);
    final EventService eventService =
        (EventService) Reflector.getInstance("com.firstrain.frapi.service.EventService");
    Reflector.setField(objectUnderTest, "eventService", eventService);
    final Logger logger = PowerMockito.mock(Logger.class);
    Reflector.setField(objectUnderTest, "LOG", logger);
    final CompanyServiceRepository companyServiceRepository =
        PowerMockito.mock(CompanyServiceRepository.class);
    final IEntityInfoCache iEntityInfoCache1 = PowerMockito.mock(IEntityInfoCache.class);
    final IEntityInfo iEntityInfo1 = PowerMockito.mock(IEntityInfo.class);
    final Method getTypeMethod = DTUMemberMatcher.method(IEntityInfo.class, "getType");
    PowerMockito.doReturn(768).when(iEntityInfo1, getTypeMethod).withNoArguments();
    final Method searchTokenToEntityMethod =
        DTUMemberMatcher.method(IEntityInfoCache.class, "searchTokenToEntity", String.class);
    PowerMockito.doReturn(iEntityInfo1)
        .when(iEntityInfoCache1, searchTokenToEntityMethod)
        .withArguments(or(isA(String.class), isNull(String.class)));
    final IEntityInfoCache iEntityInfoCache = PowerMockito.mock(IEntityInfoCache.class);
    final IEntityInfo iEntityInfo = PowerMockito.mock(IEntityInfo.class);
    final Method getSearchTokenMethod =
        DTUMemberMatcher.method(IEntityInfo.class, "getSearchToken");
    ((PowerMockitoStubber) PowerMockito.doReturn("").doReturn(null))
        .when(iEntityInfo, getSearchTokenMethod)
        .withNoArguments();
    final Method getCompanyIdMethod = DTUMemberMatcher.method(IEntityInfo.class, "getCompanyId");
    PowerMockito.doReturn(0).when(iEntityInfo, getCompanyIdMethod).withNoArguments();
    final Method catIdToEntityMethod =
        DTUMemberMatcher.method(IEntityInfoCache.class, "catIdToEntity", String.class);
    PowerMockito.doReturn(iEntityInfo)
        .when(iEntityInfoCache, catIdToEntityMethod)
        .withArguments(or(isA(String.class), isNull(String.class)));
    final Method getEntityInfoCacheMethod =
        DTUMemberMatcher.method(CompanyServiceRepository.class, "getEntityInfoCache");
    ((PowerMockitoStubber) PowerMockito.doReturn(iEntityInfoCache).doReturn(iEntityInfoCache1))
        .when(companyServiceRepository, getEntityInfoCacheMethod)
        .withNoArguments();
    Reflector.setField(objectUnderTest, "companyServiceRepository", companyServiceRepository);
    final MgmtTurnoverServiceSpec mtmtSpec = null;
    final BaseSpec spec = PowerMockito.mock(BaseSpec.class);
    final Method getCacheKeyMethod = DTUMemberMatcher.method(BaseSpec.class, "getCacheKey");
    PowerMockito.doReturn(null).when(spec, getCacheKeyMethod).withNoArguments();
    final MgmtTurnoverSummary mgmtTurnoverSummary = PowerMockito.mock(MgmtTurnoverSummary.class);
    final MgmtTurnoverData mgmtTurnoverData = PowerMockito.mock(MgmtTurnoverData.class);
    final Method getMgmtTurnoverDataForMethod =
        DTUMemberMatcher.method(MgmtTurnoverSummary.class, "getMgmtTurnoverDataFor", String.class);
    PowerMockito.doReturn(mgmtTurnoverData)
        .when(mgmtTurnoverSummary, getMgmtTurnoverDataForMethod)
        .withArguments(or(isA(String.class), isNull(String.class)));
    PowerMockito.whenNew(MgmtTurnoverSummary.class)
        .withNoArguments()
        .thenReturn(mgmtTurnoverSummary);

    // Act
    final MgmtTurnoverData retval = objectUnderTest.getMgmtTurnoverData(mtmtSpec, spec);

    // Assert
    assertNotNull(retval);
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest({BaseSpec.class, IEntityInfoCache.class, CompanyServiceRepository.class})
  @Test
  public void getMgmtTurnoverDataInputNullNotNullOutputNull() throws Exception {

    // Arrange
    final CompanyServiceImpl objectUnderTest = new CompanyServiceImpl();
    final ConvertUtil convertUtil = new ConvertUtil();
    Reflector.setField(convertUtil, "MIN_SUMMARY_LENGTH", 0);
    Reflector.setField(convertUtil, "SUMMARY_TRIM_FACTOR", 0.0f);
    Reflector.setField(convertUtil, "MAX_SUMMARY_LENGTH", 0);
    Reflector.setField(convertUtil, "LOG", null);
    Reflector.setField(objectUnderTest, "convertUtil", convertUtil);
    Reflector.setField(objectUnderTest, "entityBaseService", null);
    final EventService eventService =
        (EventService) Reflector.getInstance("com.firstrain.frapi.service.EventService");
    Reflector.setField(objectUnderTest, "eventService", eventService);
    final Logger logger = (Logger) Reflector.getInstance("org.apache.log4j.Logger");
    Reflector.setField(objectUnderTest, "LOG", logger);
    final CompanyServiceRepository companyServiceRepository =
        PowerMockito.mock(CompanyServiceRepository.class);
    final IEntityInfoCache iEntityInfoCache = PowerMockito.mock(IEntityInfoCache.class);
    final Method catIdToEntityMethod =
        DTUMemberMatcher.method(IEntityInfoCache.class, "catIdToEntity", String.class);
    PowerMockito.doReturn(null)
        .when(iEntityInfoCache, catIdToEntityMethod)
        .withArguments(or(isA(String.class), isNull(String.class)));
    final Method getEntityInfoCacheMethod =
        DTUMemberMatcher.method(CompanyServiceRepository.class, "getEntityInfoCache");
    PowerMockito.doReturn(iEntityInfoCache)
        .when(companyServiceRepository, getEntityInfoCacheMethod)
        .withNoArguments();
    Reflector.setField(objectUnderTest, "companyServiceRepository", companyServiceRepository);
    final MgmtTurnoverServiceSpec mtmtSpec = null;
    final BaseSpec spec = PowerMockito.mock(BaseSpec.class);
    final Method getCacheKeyMethod = DTUMemberMatcher.method(BaseSpec.class, "getCacheKey");
    PowerMockito.doReturn(null).when(spec, getCacheKeyMethod).withNoArguments();

    // Act
    final MgmtTurnoverData retval = objectUnderTest.getMgmtTurnoverData(mtmtSpec, spec);

    // Assert
    assertNull(retval);
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest({
    IEntityInfo.class, Logger.class, IEntityInfoCache.class,
    CompanyServiceImpl.class, CompanyServiceRepository.class, FR_ArrayUtils.class
  })
  @Test
  public void getMgmtTurnoverInput1NullOutputNotNull() throws Exception {

    // Setup mocks
    PowerMockito.mockStatic(FR_ArrayUtils.class);

    // Arrange
    final CompanyServiceImpl objectUnderTest =
        (CompanyServiceImpl)
            Reflector.getInstance("com.firstrain.frapi.service.impl.CompanyServiceImpl");
    Reflector.setField(objectUnderTest, "convertUtil", null);
    Reflector.setField(objectUnderTest, "entityBaseService", null);
    Reflector.setField(objectUnderTest, "eventService", null);
    final Logger logger = PowerMockito.mock(Logger.class);
    Reflector.setField(objectUnderTest, "LOG", logger);
    final CompanyServiceRepository companyServiceRepository =
        PowerMockito.mock(CompanyServiceRepository.class);
    final IEntityInfoCache iEntityInfoCache = PowerMockito.mock(IEntityInfoCache.class);
    final IEntityInfo iEntityInfo = PowerMockito.mock(IEntityInfo.class);
    final Method getTypeMethod = DTUMemberMatcher.method(IEntityInfo.class, "getType");
    PowerMockito.doReturn(768).when(iEntityInfo, getTypeMethod).withNoArguments();
    final Method searchTokenToEntityMethod =
        DTUMemberMatcher.method(IEntityInfoCache.class, "searchTokenToEntity", String.class);
    PowerMockito.doReturn(iEntityInfo)
        .when(iEntityInfoCache, searchTokenToEntityMethod)
        .withArguments(or(isA(String.class), isNull(String.class)));
    final Method getEntityInfoCacheMethod =
        DTUMemberMatcher.method(CompanyServiceRepository.class, "getEntityInfoCache");
    PowerMockito.doReturn(iEntityInfoCache)
        .when(companyServiceRepository, getEntityInfoCacheMethod)
        .withNoArguments();
    Reflector.setField(objectUnderTest, "companyServiceRepository", companyServiceRepository);
    final ArrayList<String> searchTokens = new ArrayList<String>();
    searchTokens.add(null);
    final MgmtTurnoverServiceSpec spec = null;
    final MgmtTurnoverSummary mgmtTurnoverSummary = PowerMockito.mock(MgmtTurnoverSummary.class);
    PowerMockito.whenNew(MgmtTurnoverSummary.class)
        .withNoArguments()
        .thenReturn(mgmtTurnoverSummary);

    // Act
    final Class<?> classUnderTest =
        Reflector.forName("com.firstrain.frapi.service.impl.CompanyServiceImpl");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod(
            "getMgmtTurnover",
            Reflector.forName("java.util.List"),
            Reflector.forName("com.firstrain.frapi.obj.MgmtTurnoverServiceSpec"));
    methodUnderTest.setAccessible(true);
    final MgmtTurnoverSummary retval =
        (MgmtTurnoverSummary) methodUnderTest.invoke(objectUnderTest, searchTokens, spec);

    // Assert
    assertNotNull(retval);
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest(CompanyServiceRepository.class)
  @Test
  public void getMgmtTurnoverInputNotNullNullOutputIllegalArgumentException() throws Throwable {

    // Arrange
    final CompanyServiceImpl objectUnderTest = new CompanyServiceImpl();
    final ConvertUtil convertUtil = new ConvertUtil();
    Reflector.setField(convertUtil, "MIN_SUMMARY_LENGTH", 0);
    Reflector.setField(convertUtil, "SUMMARY_TRIM_FACTOR", 0.0f);
    Reflector.setField(convertUtil, "MAX_SUMMARY_LENGTH", 0);
    final Logger logger = (Logger) Reflector.getInstance("org.apache.log4j.Logger");
    Reflector.setField(convertUtil, "LOG", logger);
    Reflector.setField(objectUnderTest, "convertUtil", convertUtil);
    final EntityBaseService entityBaseService =
        (EntityBaseService) Reflector.getInstance("com.firstrain.frapi.service.EntityBaseService");
    Reflector.setField(objectUnderTest, "entityBaseService", entityBaseService);
    final EventService eventService =
        (EventService) Reflector.getInstance("com.firstrain.frapi.service.EventService");
    Reflector.setField(objectUnderTest, "eventService", eventService);
    Reflector.setField(objectUnderTest, "LOG", null);
    final CompanyServiceRepository companyServiceRepository =
        PowerMockito.mock(CompanyServiceRepository.class);
    final Method getEntityInfoCacheMethod =
        DTUMemberMatcher.method(CompanyServiceRepository.class, "getEntityInfoCache");
    PowerMockito.doReturn(null)
        .when(companyServiceRepository, getEntityInfoCacheMethod)
        .withNoArguments();
    Reflector.setField(objectUnderTest, "companyServiceRepository", companyServiceRepository);
    final SearchTokenSpec stSpec = new SearchTokenSpec();
    final MgmtTurnoverServiceSpec spec = null;

    // Act
    thrown.expect(IllegalArgumentException.class);
    try {
      final Class<?> classUnderTest =
          Reflector.forName("com.firstrain.frapi.service.impl.CompanyServiceImpl");
      final Method methodUnderTest =
          classUnderTest.getDeclaredMethod(
              "getMgmtTurnover",
              Reflector.forName("com.firstrain.frapi.obj.SearchTokenSpec"),
              Reflector.forName("com.firstrain.frapi.obj.MgmtTurnoverServiceSpec"));
      methodUnderTest.setAccessible(true);
      methodUnderTest.invoke(objectUnderTest, stSpec, spec);
    } catch (InvocationTargetException ex) {
      throw ex.getCause();
    }

    // Method is not expected to return due to exception thrown
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest(CompanyServiceRepository.class)
  @Test
  public void getMgmtTurnoverInputNullNullOutputIllegalArgumentException() throws Throwable {

    // Arrange
    final CompanyServiceImpl objectUnderTest = new CompanyServiceImpl();
    Reflector.setField(objectUnderTest, "convertUtil", null);
    Reflector.setField(objectUnderTest, "entityBaseService", null);
    Reflector.setField(objectUnderTest, "eventService", null);
    Reflector.setField(objectUnderTest, "LOG", null);
    final CompanyServiceRepository companyServiceRepository =
        PowerMockito.mock(CompanyServiceRepository.class);
    final Method getEntityInfoCacheMethod =
        DTUMemberMatcher.method(CompanyServiceRepository.class, "getEntityInfoCache");
    PowerMockito.doReturn(null)
        .when(companyServiceRepository, getEntityInfoCacheMethod)
        .withNoArguments();
    Reflector.setField(objectUnderTest, "companyServiceRepository", companyServiceRepository);
    final List<String> searchTokens = null;
    final MgmtTurnoverServiceSpec spec = null;

    // Act
    thrown.expect(IllegalArgumentException.class);
    try {
      final Class<?> classUnderTest =
          Reflector.forName("com.firstrain.frapi.service.impl.CompanyServiceImpl");
      final Method methodUnderTest =
          classUnderTest.getDeclaredMethod(
              "getMgmtTurnover",
              Reflector.forName("java.util.List"),
              Reflector.forName("com.firstrain.frapi.obj.MgmtTurnoverServiceSpec"));
      methodUnderTest.setAccessible(true);
      methodUnderTest.invoke(objectUnderTest, searchTokens, spec);
    } catch (InvocationTargetException ex) {
      throw ex.getCause();
    }

    // Method is not expected to return due to exception thrown
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest(CompanyServiceRepository.class)
  @Test
  public void getMgmtTurnoverInputNullNullOutputIllegalArgumentException2() throws Throwable {

    // Arrange
    final CompanyServiceImpl objectUnderTest = new CompanyServiceImpl();
    final ConvertUtil convertUtil = new ConvertUtil();
    Reflector.setField(convertUtil, "MIN_SUMMARY_LENGTH", 0);
    Reflector.setField(convertUtil, "SUMMARY_TRIM_FACTOR", 0.0f);
    Reflector.setField(convertUtil, "MAX_SUMMARY_LENGTH", 0);
    final Logger logger = (Logger) Reflector.getInstance("org.apache.log4j.Logger");
    Reflector.setField(convertUtil, "LOG", logger);
    Reflector.setField(objectUnderTest, "convertUtil", convertUtil);
    final EntityBaseService entityBaseService =
        (EntityBaseService) Reflector.getInstance("com.firstrain.frapi.service.EntityBaseService");
    Reflector.setField(objectUnderTest, "entityBaseService", entityBaseService);
    final EventService eventService =
        (EventService) Reflector.getInstance("com.firstrain.frapi.service.EventService");
    Reflector.setField(objectUnderTest, "eventService", eventService);
    Reflector.setField(objectUnderTest, "LOG", null);
    final CompanyServiceRepository companyServiceRepository =
        PowerMockito.mock(CompanyServiceRepository.class);
    final Method getEntityInfoCacheMethod =
        DTUMemberMatcher.method(CompanyServiceRepository.class, "getEntityInfoCache");
    PowerMockito.doReturn(null)
        .when(companyServiceRepository, getEntityInfoCacheMethod)
        .withNoArguments();
    Reflector.setField(objectUnderTest, "companyServiceRepository", companyServiceRepository);
    final SearchTokenSpec stSpec = null;
    final MgmtTurnoverServiceSpec spec = null;

    // Act
    thrown.expect(IllegalArgumentException.class);
    try {
      final Class<?> classUnderTest =
          Reflector.forName("com.firstrain.frapi.service.impl.CompanyServiceImpl");
      final Method methodUnderTest =
          classUnderTest.getDeclaredMethod(
              "getMgmtTurnover",
              Reflector.forName("com.firstrain.frapi.obj.SearchTokenSpec"),
              Reflector.forName("com.firstrain.frapi.obj.MgmtTurnoverServiceSpec"));
      methodUnderTest.setAccessible(true);
      methodUnderTest.invoke(objectUnderTest, stSpec, spec);
    } catch (InvocationTargetException ex) {
      throw ex.getCause();
    }

    // Method is not expected to return due to exception thrown
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest(InternetDomainName.class)
  @Test
  public void hasCountrySuffixInputNullOutputFalse() throws Exception {

    // Setup mocks
    PowerMockito.mockStatic(InternetDomainName.class);

    // Arrange
    final CompanyServiceImpl objectUnderTest = new CompanyServiceImpl();
    Reflector.setField(objectUnderTest, "convertUtil", null);
    Reflector.setField(objectUnderTest, "entityBaseService", null);
    Reflector.setField(objectUnderTest, "eventService", null);
    Reflector.setField(objectUnderTest, "LOG", null);
    Reflector.setField(objectUnderTest, "companyServiceRepository", null);
    final String url = null;
    final InternetDomainName internetDomainName = PowerMockito.mock(InternetDomainName.class);
    final Method publicSuffixMethod =
        DTUMemberMatcher.method(InternetDomainName.class, "publicSuffix");
    PowerMockito.doReturn(null).when(internetDomainName, publicSuffixMethod).withNoArguments();
    final Method fromMethod =
        DTUMemberMatcher.method(InternetDomainName.class, "from", String.class);
    PowerMockito.doReturn(internetDomainName)
        .when(InternetDomainName.class, fromMethod)
        .withArguments(or(isA(String.class), isNull(String.class)));

    // Act
    final Class<?> classUnderTest =
        Reflector.forName("com.firstrain.frapi.service.impl.CompanyServiceImpl");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod("hasCountrySuffix", Reflector.forName("java.lang.String"));
    methodUnderTest.setAccessible(true);
    final boolean retval = (boolean) methodUnderTest.invoke(objectUnderTest, url);

    // Assert
    assertEquals(false, retval);
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest({ImmutableList.class, InternetDomainName.class})
  @Test
  public void hasCountrySuffixInputNullOutputTrue() throws Exception {

    // Setup mocks
    PowerMockito.mockStatic(InternetDomainName.class);

    // Arrange
    final CompanyServiceImpl objectUnderTest = new CompanyServiceImpl();
    Reflector.setField(objectUnderTest, "convertUtil", null);
    Reflector.setField(objectUnderTest, "entityBaseService", null);
    Reflector.setField(objectUnderTest, "eventService", null);
    Reflector.setField(objectUnderTest, "LOG", null);
    Reflector.setField(objectUnderTest, "companyServiceRepository", null);
    final String url = null;
    final InternetDomainName internetDomainName = PowerMockito.mock(InternetDomainName.class);
    final InternetDomainName internetDomainName1 = PowerMockito.mock(InternetDomainName.class);
    final ImmutableList immutableList = PowerMockito.mock(ImmutableList.class);
    final Method sizeMethod = DTUMemberMatcher.method(ImmutableList.class, "size");
    PowerMockito.doReturn(2).when(immutableList, sizeMethod).withNoArguments();
    final Method partsMethod = DTUMemberMatcher.method(InternetDomainName.class, "parts");
    PowerMockito.doReturn(immutableList).when(internetDomainName1, partsMethod).withNoArguments();
    final Method publicSuffixMethod =
        DTUMemberMatcher.method(InternetDomainName.class, "publicSuffix");
    PowerMockito.doReturn(internetDomainName1)
        .when(internetDomainName, publicSuffixMethod)
        .withNoArguments();
    final Method fromMethod =
        DTUMemberMatcher.method(InternetDomainName.class, "from", String.class);
    PowerMockito.doReturn(internetDomainName)
        .when(InternetDomainName.class, fromMethod)
        .withArguments(or(isA(String.class), isNull(String.class)));

    // Act
    final Class<?> classUnderTest =
        Reflector.forName("com.firstrain.frapi.service.impl.CompanyServiceImpl");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod("hasCountrySuffix", Reflector.forName("java.lang.String"));
    methodUnderTest.setAccessible(true);
    final boolean retval = (boolean) methodUnderTest.invoke(objectUnderTest, url);

    // Assert
    assertEquals(true, retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void isHomePageMatchedInputNotNullNotNullOutputFalse() throws Exception {

    // Arrange
    final CompanyServiceImpl objectUnderTest = new CompanyServiceImpl();
    Reflector.setField(objectUnderTest, "convertUtil", null);
    Reflector.setField(objectUnderTest, "entityBaseService", null);
    final EventService eventService =
        (EventService) Reflector.getInstance("com.firstrain.frapi.service.EventService");
    Reflector.setField(objectUnderTest, "eventService", eventService);
    Reflector.setField(objectUnderTest, "LOG", null);
    Reflector.setField(objectUnderTest, "companyServiceRepository", null);
    final String homePageInput = "   \"      ";
    final String homePage = "      ";

    // Act
    final Class<?> classUnderTest =
        Reflector.forName("com.firstrain.frapi.service.impl.CompanyServiceImpl");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod(
            "isHomePageMatched",
            Reflector.forName("java.lang.String"),
            Reflector.forName("java.lang.String"));
    methodUnderTest.setAccessible(true);
    final boolean retval =
        (boolean) methodUnderTest.invoke(objectUnderTest, homePageInput, homePage);

    // Assert
    assertEquals(false, retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void isZIPMatchedInputNotNullNotNullOutputFalse() throws Exception {

    // Arrange
    final CompanyServiceImpl objectUnderTest = new CompanyServiceImpl();
    Reflector.setField(objectUnderTest, "convertUtil", null);
    Reflector.setField(objectUnderTest, "entityBaseService", null);
    Reflector.setField(objectUnderTest, "eventService", null);
    final Logger logger = (Logger) Reflector.getInstance("org.apache.log4j.Logger");
    Reflector.setField(objectUnderTest, "LOG", logger);
    Reflector.setField(objectUnderTest, "companyServiceRepository", null);
    final String inputZIP = ";-66----";
    final String zip = ",#--,,,,-.";

    // Act
    final Class<?> classUnderTest =
        Reflector.forName("com.firstrain.frapi.service.impl.CompanyServiceImpl");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod(
            "isZIPMatched",
            Reflector.forName("java.lang.String"),
            Reflector.forName("java.lang.String"));
    methodUnderTest.setAccessible(true);
    final boolean retval = (boolean) methodUnderTest.invoke(objectUnderTest, inputZIP, zip);

    // Assert
    assertEquals(false, retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void isZIPMatchedInputNotNullNotNullOutputFalse2() throws Exception {

    // Arrange
    final CompanyServiceImpl objectUnderTest = new CompanyServiceImpl();
    Reflector.setField(objectUnderTest, "convertUtil", null);
    Reflector.setField(objectUnderTest, "entityBaseService", null);
    Reflector.setField(objectUnderTest, "eventService", null);
    Reflector.setField(objectUnderTest, "LOG", null);
    final CompanyServiceRepository companyServiceRepository =
        (CompanyServiceRepository)
            Reflector.getInstance("com.firstrain.frapi.repository.CompanyServiceRepository");
    Reflector.setField(objectUnderTest, "companyServiceRepository", companyServiceRepository);
    final String inputZIP = "a";
    final String zip = "[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[";

    // Act
    final Class<?> classUnderTest =
        Reflector.forName("com.firstrain.frapi.service.impl.CompanyServiceImpl");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod(
            "isZIPMatched",
            Reflector.forName("java.lang.String"),
            Reflector.forName("java.lang.String"));
    methodUnderTest.setAccessible(true);
    final boolean retval = (boolean) methodUnderTest.invoke(objectUnderTest, inputZIP, zip);

    // Assert
    assertEquals(false, retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void isZIPMatchedInputNotNullNotNullOutputTrue() throws Exception {

    // Arrange
    final CompanyServiceImpl objectUnderTest = new CompanyServiceImpl();
    Reflector.setField(objectUnderTest, "convertUtil", null);
    Reflector.setField(objectUnderTest, "entityBaseService", null);
    Reflector.setField(objectUnderTest, "eventService", null);
    final Logger logger = (Logger) Reflector.getInstance("org.apache.log4j.Logger");
    Reflector.setField(objectUnderTest, "LOG", logger);
    Reflector.setField(objectUnderTest, "companyServiceRepository", null);
    final String inputZIP = "--5&--4=";
    final String zip = "--5&--4=";

    // Act
    final Class<?> classUnderTest =
        Reflector.forName("com.firstrain.frapi.service.impl.CompanyServiceImpl");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod(
            "isZIPMatched",
            Reflector.forName("java.lang.String"),
            Reflector.forName("java.lang.String"));
    methodUnderTest.setAccessible(true);
    final boolean retval = (boolean) methodUnderTest.invoke(objectUnderTest, inputZIP, zip);

    // Assert
    assertEquals(true, retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void isZIPMatchedInputNullNullOutputFalse() throws Exception {

    // Arrange
    final CompanyServiceImpl objectUnderTest = new CompanyServiceImpl();
    Reflector.setField(objectUnderTest, "convertUtil", null);
    Reflector.setField(objectUnderTest, "entityBaseService", null);
    Reflector.setField(objectUnderTest, "eventService", null);
    final Logger logger = (Logger) Reflector.getInstance("org.apache.log4j.Logger");
    Reflector.setField(objectUnderTest, "LOG", logger);
    Reflector.setField(objectUnderTest, "companyServiceRepository", null);
    final String inputZIP = null;
    final String zip = null;

    // Act
    final Class<?> classUnderTest =
        Reflector.forName("com.firstrain.frapi.service.impl.CompanyServiceImpl");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod(
            "isZIPMatched",
            Reflector.forName("java.lang.String"),
            Reflector.forName("java.lang.String"));
    methodUnderTest.setAccessible(true);
    final boolean retval = (boolean) methodUnderTest.invoke(objectUnderTest, inputZIP, zip);

    // Assert
    assertEquals(false, retval);
  }
}