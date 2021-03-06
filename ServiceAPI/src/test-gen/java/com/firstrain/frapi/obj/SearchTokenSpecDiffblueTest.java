package com.firstrain.frapi.obj;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.diffblue.deeptestutils.Reflector;
import java.util.ArrayList;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;

public class SearchTokenSpecDiffblueTest {

  @Rule public final ExpectedException thrown = ExpectedException.none();
  @Rule public final Timeout globalTimeout = new Timeout(10_000);

  /* testedClasses: SearchTokenSpec */
  // Test generated by Diffblue Deeptest.

  @Test
  public void areExcludeSearchTokensAvailableOutputFalse() {

    // Arrange
    final SearchTokenSpec objectUnderTest = new SearchTokenSpec();

    // Act
    final boolean retval = objectUnderTest.areExcludeSearchTokensAvailable();

    // Assert
    assertEquals(false, retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void areExcludeSearchTokensAvailableOutputFalse2() {

    // Arrange
    final SearchTokenSpec objectUnderTest = new SearchTokenSpec();
    final ArrayList<String> arrayList = new ArrayList<String>();
    Reflector.setField(objectUnderTest, "excludeSearchTokens", arrayList);
    Reflector.setField(objectUnderTest, "searchTokens", null);

    // Act
    final boolean retval = objectUnderTest.areExcludeSearchTokensAvailable();

    // Assert
    assertEquals(false, retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void areExcludeSearchTokensAvailableOutputTrue() {

    // Arrange
    final SearchTokenSpec objectUnderTest = new SearchTokenSpec();
    final ArrayList<String> arrayList = new ArrayList<String>();
    arrayList.add("");
    Reflector.setField(objectUnderTest, "excludeSearchTokens", arrayList);
    Reflector.setField(objectUnderTest, "searchTokens", null);

    // Act
    final boolean retval = objectUnderTest.areExcludeSearchTokensAvailable();

    // Assert
    assertEquals(true, retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void areSearchTokensAvailableOutputFalse() {

    // Arrange
    final SearchTokenSpec objectUnderTest = new SearchTokenSpec();

    // Act
    final boolean retval = objectUnderTest.areSearchTokensAvailable();

    // Assert
    assertEquals(false, retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void areSearchTokensAvailableOutputFalse2() {

    // Arrange
    final SearchTokenSpec objectUnderTest = new SearchTokenSpec();
    Reflector.setField(objectUnderTest, "excludeSearchTokens", null);
    final ArrayList<String> arrayList = new ArrayList<String>();
    Reflector.setField(objectUnderTest, "searchTokens", arrayList);

    // Act
    final boolean retval = objectUnderTest.areSearchTokensAvailable();

    // Assert
    assertEquals(false, retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void areSearchTokensAvailableOutputTrue() {

    // Arrange
    final SearchTokenSpec objectUnderTest = new SearchTokenSpec();
    Reflector.setField(objectUnderTest, "excludeSearchTokens", null);
    final ArrayList<String> arrayList = new ArrayList<String>();
    arrayList.add("");
    Reflector.setField(objectUnderTest, "searchTokens", arrayList);

    // Act
    final boolean retval = objectUnderTest.areSearchTokensAvailable();

    // Assert
    assertEquals(true, retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void getExcludeSearchTokensOutputNull() {

    // Arrange
    final SearchTokenSpec objectUnderTest = new SearchTokenSpec();

    // Act
    final List<String> retval = objectUnderTest.getExcludeSearchTokens();

    // Assert
    assertNull(retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void getSearchTokensOutputNull() {

    // Arrange
    final SearchTokenSpec objectUnderTest = new SearchTokenSpec();

    // Act
    final List<String> retval = objectUnderTest.getSearchTokens();

    // Assert
    assertNull(retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void setExcludeSearchTokensInputNullOutputNotNull() {

    // Arrange
    final SearchTokenSpec objectUnderTest = new SearchTokenSpec();
    final List<String> excludeSearchTokens = null;

    // Act
    final SearchTokenSpec retval = objectUnderTest.setExcludeSearchTokens(excludeSearchTokens);

    // Assert
    assertNotNull(retval);
    assertNull(Reflector.getInstanceField(retval, "excludeSearchTokens"));
    assertNull(Reflector.getInstanceField(retval, "searchTokens"));
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void setSearchTokensInputNullOutputNotNull() {

    // Arrange
    final SearchTokenSpec objectUnderTest = new SearchTokenSpec();
    final List<String> searchTokens = null;

    // Act
    final SearchTokenSpec retval = objectUnderTest.setSearchTokens(searchTokens);

    // Assert
    assertNotNull(retval);
    assertNull(Reflector.getInstanceField(retval, "excludeSearchTokens"));
    assertNull(Reflector.getInstanceField(retval, "searchTokens"));
  }
}
