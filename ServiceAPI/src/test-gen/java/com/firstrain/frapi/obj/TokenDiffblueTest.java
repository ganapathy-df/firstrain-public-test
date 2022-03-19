package com.firstrain.frapi.obj;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.firstrain.solr.client.SearchTokenEntry;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;

public class TokenDiffblueTest {

  @Rule public final ExpectedException thrown = ExpectedException.none();
  @Rule public final Timeout globalTimeout = new Timeout(10_000);

  /* testedClasses: Token */
  // Test generated by Diffblue Deeptest.

  @Test
  public void getSearchTermOutputNull() {

    // Arrange
    final Token objectUnderTest = new Token();

    // Act
    final String retval = objectUnderTest.getSearchTerm();

    // Assert
    assertNull(retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void getTitleOutputNull() {

    // Arrange
    final Token objectUnderTest = new Token();

    // Act
    final String retval = objectUnderTest.getTitle();

    // Assert
    assertNull(retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void getTokenOutputNull() {

    // Arrange
    final Token objectUnderTest = new Token();

    // Act
    final SearchTokenEntry retval = objectUnderTest.getToken();

    // Assert
    assertNull(retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void isIncludedOutputFalse() {

    // Arrange
    final Token objectUnderTest = new Token();

    // Act
    final boolean retval = objectUnderTest.isIncluded();

    // Assert
    assertEquals(false, retval);
  }
}
