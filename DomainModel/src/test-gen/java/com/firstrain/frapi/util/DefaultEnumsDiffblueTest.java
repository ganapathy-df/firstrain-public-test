package com.firstrain.frapi.util;

import static org.junit.Assert.assertArrayEquals;

import com.firstrain.frapi.util.DefaultEnums.CoversationStarterType;
import com.firstrain.frapi.util.DefaultEnums.INPUT_ENTITY_TYPE;
import com.firstrain.frapi.util.DefaultEnums.MatchedEntityConfidenceScore;
import com.firstrain.frapi.util.DefaultEnums.MgmtServiceGroup;
import com.firstrain.frapi.util.DefaultEnums.TitleType;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;

public class DefaultEnumsDiffblueTest {

  @Rule public final ExpectedException thrown = ExpectedException.none();
  @Rule public final Timeout globalTimeout = new Timeout(10_000);

  /* testedClasses: DefaultEnums */
  // Test generated by Diffblue Deeptest.

  @Test
  public void valueOfInputNullOutputNullPointerException() {

    // Arrange
    final String name = null;

    // Act
    thrown.expect(NullPointerException.class);
    MatchedEntityConfidenceScore.valueOf(name);

    // Method is not expected to return due to exception thrown
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void valuesOutput2() {

    // Act
    final MgmtServiceGroup[] retval = MgmtServiceGroup.values();

    // Assert
    assertArrayEquals(
        new MgmtServiceGroup[] {MgmtServiceGroup.CHANGE_TYPE, MgmtServiceGroup.CHANGE_LEVEL},
        retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void valuesOutput5() {

    // Act
    final INPUT_ENTITY_TYPE[] retval = INPUT_ENTITY_TYPE.values();

    // Assert
    assertArrayEquals(
        new INPUT_ENTITY_TYPE[] {
          INPUT_ENTITY_TYPE.COMPANY, INPUT_ENTITY_TYPE.INDUSTRY,
          INPUT_ENTITY_TYPE.TOPIC, INPUT_ENTITY_TYPE.REGION,
          INPUT_ENTITY_TYPE.SEARCH
        },
        retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void valuesOutput6() {

    // Act
    final CoversationStarterType[] retval = CoversationStarterType.values();

    // Assert
    assertArrayEquals(
        new CoversationStarterType[] {
          CoversationStarterType.PEER_COMMENTARY, CoversationStarterType.LEAD_COMMENTARY,
          CoversationStarterType.CXO_COMMENTARY, CoversationStarterType.COMPANY_NEWS,
          CoversationStarterType.BUSINESS_EVENTS, CoversationStarterType.INDUSTRY_NEWS
        },
        retval);
  }

  // Test generated by Diffblue Deeptest.

  @Test
  public void valuesOutput42() {

    // Act
    final TitleType[] retval = TitleType.values();

    // Assert
    assertArrayEquals(
        new TitleType[] {TitleType.CSSINLINE, TitleType.CSSCLASS, TitleType.PLAIN, TitleType.HTML},
        retval);
  }
}
