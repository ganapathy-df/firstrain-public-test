package com.firstrain.frapi.service.impl;

import static org.junit.Assert.assertArrayEquals;

import com.diffblue.deeptestutils.Reflector;
import com.diffblue.deeptestutils.mock.DTUMemberMatcher;
import com.firstrain.frapi.repository.EntityBaseServiceRepository;
import com.firstrain.obj.IEntityInfo;
import java.lang.reflect.Method;
import java.util.ArrayList;
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
public class IndustryBriefServiceImplDiffblueTest {

  @Rule public final ExpectedException thrown = ExpectedException.none();
  @Rule public final Timeout globalTimeout = new Timeout(10_000);

  /* testedClasses: IndustryBriefServiceImpl */
  // Test generated by Diffblue Deeptest.

  @Test
  public void processEntityInfoInput01FalseOutput0() throws Exception {

    // Arrange
    final IndustryBriefServiceImpl objectUnderTest = new IndustryBriefServiceImpl();
    Reflector.setField(objectUnderTest, "servicesAPIUtil", null);
    Reflector.setField(objectUnderTest, "entityBaseService", null);
    final EntityBaseServiceRepository entityBaseServiceRepository =
        (EntityBaseServiceRepository)
            Reflector.getInstance("com.firstrain.frapi.repository.EntityBaseServiceRepository");
    Reflector.setField(objectUnderTest, "entityBaseServiceRepository", entityBaseServiceRepository);
    Reflector.setField(objectUnderTest, "LOG", null);
    final ArrayList<IEntityInfo> entityList = new ArrayList<IEntityInfo>();
    final java.util.HashSet<String> allCategoryIds = new java.util.HashSet<String>();
    allCategoryIds.add("??");
    final boolean isCompany = false;

    // Act
    final Class<?> classUnderTest =
        Reflector.forName("com.firstrain.frapi.service.impl.IndustryBriefServiceImpl");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod(
            "processEntityInfo",
            Reflector.forName("java.util.List"),
            Reflector.forName("java.util.Set"),
            Reflector.forName("boolean"));
    methodUnderTest.setAccessible(true);
    final int[] retval =
        (int[]) methodUnderTest.invoke(objectUnderTest, entityList, allCategoryIds, isCompany);

    // Assert
    assertArrayEquals(new int[] {}, retval);
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest(IEntityInfo.class)
  @Test
  public void processEntityInfoInput11FalseOutput1() throws Exception {

    // Arrange
    final IndustryBriefServiceImpl objectUnderTest = new IndustryBriefServiceImpl();
    Reflector.setField(objectUnderTest, "servicesAPIUtil", null);
    Reflector.setField(objectUnderTest, "entityBaseService", null);
    final EntityBaseServiceRepository entityBaseServiceRepository =
        (EntityBaseServiceRepository)
            Reflector.getInstance("com.firstrain.frapi.repository.EntityBaseServiceRepository");
    Reflector.setField(objectUnderTest, "entityBaseServiceRepository", entityBaseServiceRepository);
    Reflector.setField(objectUnderTest, "LOG", null);
    final ArrayList<IEntityInfo> entityList = new ArrayList<IEntityInfo>();
    final IEntityInfo iEntityInfo = PowerMockito.mock(IEntityInfo.class);
    final Method getIdMethod = DTUMemberMatcher.method(IEntityInfo.class, "getId");
    ((PowerMockitoStubber) PowerMockito.doReturn("9").doReturn(null))
        .when(iEntityInfo, getIdMethod)
        .withNoArguments();
    entityList.add(iEntityInfo);
    final java.util.HashSet<String> allCategoryIds = new java.util.HashSet<String>();
    allCategoryIds.add(null);
    final boolean isCompany = false;

    // Act
    final Class<?> classUnderTest =
        Reflector.forName("com.firstrain.frapi.service.impl.IndustryBriefServiceImpl");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod(
            "processEntityInfo",
            Reflector.forName("java.util.List"),
            Reflector.forName("java.util.Set"),
            Reflector.forName("boolean"));
    methodUnderTest.setAccessible(true);
    final int[] retval =
        (int[]) methodUnderTest.invoke(objectUnderTest, entityList, allCategoryIds, isCompany);

    // Assert
    assertArrayEquals(new int[] {9}, retval);
  }

  // Test generated by Diffblue Deeptest.
  @PrepareForTest(IEntityInfo.class)
  @Test
  public void processEntityInfoInput11TrueOutput1() throws Exception {

    // Arrange
    final IndustryBriefServiceImpl objectUnderTest = new IndustryBriefServiceImpl();
    Reflector.setField(objectUnderTest, "servicesAPIUtil", null);
    Reflector.setField(objectUnderTest, "entityBaseService", null);
    final EntityBaseServiceRepository entityBaseServiceRepository =
        (EntityBaseServiceRepository)
            Reflector.getInstance("com.firstrain.frapi.repository.EntityBaseServiceRepository");
    Reflector.setField(objectUnderTest, "entityBaseServiceRepository", entityBaseServiceRepository);
    Reflector.setField(objectUnderTest, "LOG", null);
    final ArrayList<IEntityInfo> entityList = new ArrayList<IEntityInfo>();
    final IEntityInfo iEntityInfo = PowerMockito.mock(IEntityInfo.class);
    final Method getCompanyIdMethod = DTUMemberMatcher.method(IEntityInfo.class, "getCompanyId");
    PowerMockito.doReturn(0).when(iEntityInfo, getCompanyIdMethod).withNoArguments();
    final Method getIdMethod = DTUMemberMatcher.method(IEntityInfo.class, "getId");
    PowerMockito.doReturn(null).when(iEntityInfo, getIdMethod).withNoArguments();
    entityList.add(iEntityInfo);
    final java.util.HashSet<String> allCategoryIds = new java.util.HashSet<String>();
    allCategoryIds.add(null);
    final boolean isCompany = true;

    // Act
    final Class<?> classUnderTest =
        Reflector.forName("com.firstrain.frapi.service.impl.IndustryBriefServiceImpl");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod(
            "processEntityInfo",
            Reflector.forName("java.util.List"),
            Reflector.forName("java.util.Set"),
            Reflector.forName("boolean"));
    methodUnderTest.setAccessible(true);
    final int[] retval =
        (int[]) methodUnderTest.invoke(objectUnderTest, entityList, allCategoryIds, isCompany);

    // Assert
    assertArrayEquals(new int[] {0}, retval);
  }
}
