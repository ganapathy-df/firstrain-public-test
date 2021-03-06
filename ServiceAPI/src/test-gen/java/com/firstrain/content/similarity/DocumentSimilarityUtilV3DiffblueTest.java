package com.firstrain.content.similarity;

import static org.junit.Assert.assertEquals;

import com.diffblue.deeptestutils.Reflector;
import com.firstrain.content.similarity.measures.CosineSimilarity;
import java.lang.reflect.Method;
import java.util.HashSet;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;

public class DocumentSimilarityUtilV3DiffblueTest {

  @Rule public final ExpectedException thrown = ExpectedException.none();
  @Rule public final Timeout globalTimeout = new Timeout(10_000);

  /* testedClasses: DocumentSimilarityUtilV3 */
  // Test generated by Diffblue Deeptest.

  @Test
  public void eqOrSubSequenceInputNotNullNotNullOutputFalse() throws Exception {

    // Arrange
    final DocumentSimilarityUtilV3 objectUnderTest =
        (DocumentSimilarityUtilV3)
            Reflector.getInstance("com.firstrain.content.similarity.DocumentSimilarityUtilV3");
    Reflector.setField(objectUnderTest, "algoCosineMeasure", null);
    objectUnderTest.setTitleSimilarityThreshold(0.0);
    objectUnderTest.setNoOfDays(0);
    Reflector.setField(objectUnderTest, "stopWords", null);
    Reflector.setField(objectUnderTest, "summarySimilarityThreshold", 0.0);
    Reflector.setField(objectUnderTest, "nGramSize", 0);
    Reflector.setField(objectUnderTest, "diceMeasure", null);
    final String s1 = ">7";
    final String s2 = "33";

    // Act
    final Class<?> classUnderTest =
        Reflector.forName("com.firstrain.content.similarity.DocumentSimilarityUtilV3");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod(
            "eqOrSubSequence",
            Reflector.forName("java.lang.String"),
            Reflector.forName("java.lang.String"));
    methodUnderTest.setAccessible(true);
    final boolean retval = (boolean) methodUnderTest.invoke(objectUnderTest, s1, s2);

    // Assert
    assertEquals(false, retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void eqOrSubSequenceInputNotNullNotNullOutputFalse2() throws Exception {

    // Arrange
    final DocumentSimilarityUtilV3 objectUnderTest =
        (DocumentSimilarityUtilV3)
            Reflector.getInstance("com.firstrain.content.similarity.DocumentSimilarityUtilV3");
    Reflector.setField(objectUnderTest, "algoCosineMeasure", null);
    arrangeDocumentSimilarityUtilV3(objectUnderTest);
    final String s1 =
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000?";
    final String s2 = "???????????????????????????????????????????????????????????????????";

    // Act
    final Class<?> classUnderTest =
        Reflector.forName("com.firstrain.content.similarity.DocumentSimilarityUtilV3");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod(
            "eqOrSubSequence",
            Reflector.forName("java.lang.String"),
            Reflector.forName("java.lang.String"));
    methodUnderTest.setAccessible(true);
    final boolean retval = (boolean) methodUnderTest.invoke(objectUnderTest, s1, s2);

    // Assert
    assertEquals(false, retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void eqOrSubSequenceInputNotNullNotNullOutputTrue() throws Exception {

    // Arrange
    final DocumentSimilarityUtilV3 objectUnderTest =
        (DocumentSimilarityUtilV3)
            Reflector.getInstance("com.firstrain.content.similarity.DocumentSimilarityUtilV3");
    Reflector.setField(objectUnderTest, "algoCosineMeasure", null);
    objectUnderTest.setTitleSimilarityThreshold(0.0);
    objectUnderTest.setNoOfDays(0);
    Reflector.setField(objectUnderTest, "stopWords", null);
    Reflector.setField(objectUnderTest, "summarySimilarityThreshold", 0.0);
    Reflector.setField(objectUnderTest, "nGramSize", 0);
    Reflector.setField(objectUnderTest, "diceMeasure", null);
    final String s1 = "}OOL_/X";
    final String s2 = "XZ}OOL_/X";

    // Act
    final Class<?> classUnderTest =
        Reflector.forName("com.firstrain.content.similarity.DocumentSimilarityUtilV3");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod(
            "eqOrSubSequence",
            Reflector.forName("java.lang.String"),
            Reflector.forName("java.lang.String"));
    methodUnderTest.setAccessible(true);
    final boolean retval = (boolean) methodUnderTest.invoke(objectUnderTest, s1, s2);

    // Assert
    assertEquals(true, retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void eqOrSubSequenceInputNotNullNotNullOutputTrue2() throws Exception {

    // Arrange
    final DocumentSimilarityUtilV3 objectUnderTest =
        (DocumentSimilarityUtilV3)
            Reflector.getInstance("com.firstrain.content.similarity.DocumentSimilarityUtilV3");
    final CosineSimilarity cosineSimilarity =
        (CosineSimilarity)
            Reflector.getInstance("com.firstrain.content.similarity.measures.CosineSimilarity");
    Reflector.setField(objectUnderTest, "algoCosineMeasure", cosineSimilarity);
    arrangeDocumentSimilarityUtilV3(objectUnderTest);
    final String s1 =
        "\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7";
    final String s2 =
        "\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7\u34b7";

    // Act
    final Class<?> classUnderTest =
        Reflector.forName("com.firstrain.content.similarity.DocumentSimilarityUtilV3");
    final Method methodUnderTest =
        classUnderTest.getDeclaredMethod(
            "eqOrSubSequence",
            Reflector.forName("java.lang.String"),
            Reflector.forName("java.lang.String"));
    methodUnderTest.setAccessible(true);
    final boolean retval = (boolean) methodUnderTest.invoke(objectUnderTest, s1, s2);

    // Assert
    assertEquals(true, retval);
  }

  private void arrangeDocumentSimilarityUtilV3(final DocumentSimilarityUtilV3 objectUnderTest) {
    Reflector.setField(objectUnderTest, "titleSimilarityThreshold", 0.0);
    Reflector.setField(objectUnderTest, "noOfDays", 0);
    final HashSet hashSet = new HashSet();
    Reflector.setField(objectUnderTest, "stopWords", hashSet);
    Reflector.setField(objectUnderTest, "summarySimilarityThreshold", 0.0);
    Reflector.setField(objectUnderTest, "nGramSize", 0);
    Reflector.setField(objectUnderTest, "diceMeasure", null);
  }
}
