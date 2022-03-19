package com.firstrain.web.pojo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigInteger;
import java.sql.Timestamp;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;

@RunWith(MockitoJUnitRunner.class)
public class UserActivityTest {

    private Timestamp activityTime;

    @InjectMocks
    private UserActivity userActivity;

    private final ErrorCollector errorCollector = new ErrorCollector();
    private final ExpectedException expectedException = ExpectedException.none();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector)
            .around(expectedException);

    @Before
    public void setUp() {
        activityTime = new Timestamp(12345L);
    }

    @Test
    public void givenInsertTimeWhenSetInsertTimeThenVerifyResult() {
        // Act
        userActivity.setInsertTime(activityTime);
        //Assert
        errorCollector.checkThat(userActivity.getInsertTime(), is(activityTime));
    }

    @Test
    public void givenLegacyIdWhenSetLegacyIdThenVerifyResult() {
        // Act
        userActivity.setLegacyId(BigInteger.ONE);
        //Assert
        errorCollector.checkThat(userActivity.getLegacyId(), is(BigInteger.ONE));
    }


    @Test
    public void givenUseripWhenSetUseripThenVerifyResult() {
        // Act
        userActivity.setUserip("userip");
        //Assert
        errorCollector.checkThat(userActivity.getUserip(), is("userip"));
    }

    @Test
    public void givenEnterpriseIdWhenSetEnterpriseIdThenVerifyResult() {
        // Act
        userActivity.setEnterpriseId(3);
        //Assert
        errorCollector.checkThat(userActivity.getEnterpriseId(), is(3));
    }

    @Test
    public void givenViewIdWhenSetViewIdThenVerifyResult() {
        // Act
        userActivity.setViewId("viewId");
        //Assert
        errorCollector.checkThat(userActivity.getViewId(), is("viewId"));
    }

    @Test
    public void givenSectionIdWhenSetSectionIdThenVerifyResult() {
        // Act
        userActivity.setSectionId("sectionId");
        //Assert
        errorCollector.checkThat(userActivity.getSectionId(), is("sectionId"));
    }

    @Test
    public void givenSubSectionWhenSetSubSectionThenVerifyResult() {
        // Act
        userActivity.setSubSection("subSection");
        //Assert
        errorCollector.checkThat(userActivity.getSubSection(), is("subSection"));
    }

    @Test
    public void givenSubSectionIdWhenSetSubSectionIdThenVerifyResult() {
        // Act
        userActivity.setSubSectionId("subSectionId");
        //Assert
        errorCollector.checkThat(userActivity.getSubSectionId(), is("subSectionId"));
    }

    @Test
    public void givenIdWhenSetIdThenVerifyResult() {
        // Act
        userActivity.setId(BigInteger.TEN);
        //Assert
        errorCollector.checkThat(userActivity.getId(), is(BigInteger.TEN));
    }

    @Test
    public void givenChannelWhenSetChannelThenVerifyResult() {
        // Act
        userActivity.setChannel("channel");
        //Assert
        errorCollector.checkThat(userActivity.getChannel(), is("channel"));
    }

    @Test
    public void givenDestinationWhenSetDestinationThenVerifyResult() {
        // Act
        userActivity.setDestination("destination");
        //Assert
        errorCollector.checkThat(userActivity.getDestination(), is("destination"));
    }

    @Test
    public void givenUserIdWhenSetUserIdThenVerifyResult() {
        // Act
        userActivity.setUserId(5);
        //Assert
        errorCollector.checkThat(userActivity.getUserId(), is(5));
    }

    @Test
    public void givenTargetWhenSetTargetThenVerifyResult() {
        // Act
        userActivity.setTarget("target");
        //Assert
        errorCollector.checkThat(userActivity.getTarget(), is("target"));
    }

    @Test
    public void givenActivityWhenSetActivityThenVerifyResult() {
        // Act
        userActivity.setActivity("activity");
        //Assert
        errorCollector.checkThat(userActivity.getActivity(), is("activity"));
    }

    @Test
    public void givenActivityTimeWhenSetActivityTimeThenVerifyResult() {
        // Act
        userActivity.setActivityTime(activityTime);
        //Assert
        errorCollector.checkThat(userActivity.getActivityTime(), is(activityTime));
    }

    @Test
    public void givenViewWhenSetViewThenVerifyResult() {
        // Act
        userActivity.setView("view");
        //Assert
        errorCollector.checkThat(userActivity.getView(), is("view"));
    }

    @Test
    public void givenSectionWhenSetSectionThenVerifyResult() {
        // Act
        userActivity.setSection("section");
        //Assert
        errorCollector.checkThat(userActivity.getSection(), is("section"));
    }

    @Test
    public void givenTargetIdWhenSetTargetIdThenVerifyResult() {
        // Act
        userActivity.setTargetId("targetId");
        //Assert
        errorCollector.checkThat(userActivity.getTargetId(), is("targetId"));
    }

    @Test
    public void givenResponseTimeWhenSetResponseTimeThenVerifyResult() {
        // Act
        userActivity.setResponseTime(4);
        //Assert
        errorCollector.checkThat(userActivity.getResponseTime(), is(4));
    }

    @Test
    public void givenUserAgentWhenSetUserAgentThenVerifyResult() {
        // Act
        userActivity.setUserAgent("userAgent");
        //Assert
        errorCollector.checkThat(userActivity.getUserAgent(), is("userAgent"));
    }

    @Test
    public void givenMetaDataWhenSetMetaDataThenVerifyResult() {
      // Act
        userActivity.setMetaData("metaData");
        //Assert
        errorCollector.checkThat(userActivity.getMetaData(), is("metaData"));
    }

    @Test
    public void givenIsExternalWhenSetExternalThenVerifyResult() {
      // Act
        userActivity.setExternal(true);
        //Assert
        errorCollector.checkThat(userActivity.isExternal(), is(true));
        errorCollector.checkThat(userActivity.toString(), containsString
                ("UserActivity [id=null, userId=null, activity=null, activityTime=null, view=null, " +
                        "viewId=null, section=null,"));
    }

    @Test
    public void givenParsedUserAgentWhenSetParsedUserAgentThenVerifyResult() {
      // Act
        userActivity.setParsedUserAgent("parsedUserAgent");
        //Assert
        errorCollector.checkThat(userActivity.getParsedUserAgent(), is("parsedUserAgent"));
    }

    @Test
    public void givenLegacyDataWhenSetLegacyDataThenVerifyResult() {
      // Act
        userActivity.setLegacyData("legacyData");
        //Assert
        errorCollector.checkThat(userActivity.getLegacyData(), is("legacyData"));
    }

    @Test
    public void givenDnbUserIdWhenSetDnbUserIdThenVerifyResult() {
      // Act
        userActivity.setDnbUserId("dnbUserId");
        //Assert
        errorCollector.checkThat(userActivity.getDnbUserId(), is("dnbUserId"));
    }

    @Test
    public void givenStr1WhenSetStr1ThenVerifyResult() {
        // Act
        userActivity.setStr1("str1");
        //Assert
        errorCollector.checkThat(userActivity.getStr1(), is("str1"));
    }

    @Test
    public void givenStr2WhenSetStr2ThenVerifyResult() {
       // Act
        userActivity.setStr2("str2");
        //Assert
        errorCollector.checkThat(userActivity.getStr2(), is("str2"));
    }

    @Test
    public void givenTypeAndConditionOnLine277TrueWhenSetTypeThenVerifyResult() {
        // Act
        userActivity.setType(2);
        //Assert
        errorCollector.checkThat(userActivity.getType(), is(2));
    }

    @Test
    public void givenTypeAndConditionOnLine277TrueWhenSetTypeThenThrowException() {
        // Act
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("value of `type` column is wrong user activity type enum ordinal.");
        userActivity.setType(6);
    }

    @Test
    public void givenApiVersionWhenSetApiVersionThenVerifyResult() {
        // Act
        userActivity.setApiVersion("apiVersion");
        //Assert
        errorCollector.checkThat(userActivity.getApiVersion(), is("apiVersion"));
    }
}
