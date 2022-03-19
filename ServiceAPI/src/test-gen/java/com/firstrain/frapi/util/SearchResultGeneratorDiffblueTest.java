package com.firstrain.frapi.util;

import static org.junit.Assert.assertNull;

import com.diffblue.deeptestutils.Reflector;
import com.firstrain.frapi.domain.BaseSpec;
import com.firstrain.solr.client.SearchResult;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Future;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;

public class SearchResultGeneratorDiffblueTest {

  @Rule public final ExpectedException thrown = ExpectedException.none();
  @Rule public final Timeout globalTimeout = new Timeout(10_000);

  /* testedClasses: SearchResultGenerator */
  // Test generated by Diffblue Deeptest.

  @Test
  public void collectSearchResultsInputNullNullNullOutputNull() throws Exception {

    // Arrange
    final Future<SearchResult> analystCommentsFuture60Days = null;
    final Future<SearchResult> analystCommentsFuture180Days = null;
    final BaseSpec baseSpec = null;

    // Act
    final SearchResult retval =
        SearchResultGenerator.collectSearchResults(
            analystCommentsFuture60Days, analystCommentsFuture180Days, baseSpec);

    // Assert
    assertNull(retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void constructorOutputUnsupportedOperationException() throws Throwable {

    // Act, creating object to test constructor
    thrown.expect(UnsupportedOperationException.class);
    try {
      final Class<?> classUnderTest =
          Reflector.forName("com.firstrain.frapi.util.SearchResultGenerator");
      final Constructor<?> ctor = classUnderTest.getDeclaredConstructor();
      ctor.setAccessible(true);
      final Object objectUnderTest = ctor.newInstance();
    } catch (InvocationTargetException ex) {
      throw ex.getCause();
    }

    // Method is not expected to return due to exception thrown
  }
}
