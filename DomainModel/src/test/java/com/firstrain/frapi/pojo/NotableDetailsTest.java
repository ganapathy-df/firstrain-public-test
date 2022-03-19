package com.firstrain.frapi.pojo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;

@RunWith(MockitoJUnitRunner.class)
public class NotableDetailsTest {

    private NotableDetails.NotableDetail notableDetailNew;

    @InjectMocks
    private NotableDetails notableDetails;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Before
    public void setUp() {
        notableDetailNew = new NotableDetails.NotableDetail();
    }

    @Test
    public void givenTweetIdWhenSetTweetIdThenVerifyResult() {
        // Act
        notableDetails.setTweetId("tweetId");
        // Assert
        errorCollector.checkThat(notableDetails.getTweetId(), is("tweetId"));
    }

    @Test
    public void givenNotableDetailsWhenSetNotableDetailsThenVerifyResult() {
        // Act
        notableDetails.setNotableDetails(Collections.singletonList(notableDetailNew));
        // Assert
        errorCollector.checkThat(notableDetails.getNotableDetails(),
                is(Collections.singletonList(notableDetailNew)));
    }

    @Test
    public void givenNotableDetailsWhenSetUserImageThenVerifyResult() {
        // Act
        notableDetailNew.setUserImage("userImage");
        // Assert
        errorCollector.checkThat(notableDetailNew.getUserImage(),
                is("userImage"));
    }

    @Test
    public void givenNotableDetailsWhenSetProfileUrlThenVerifyResult() {
        // Act
        notableDetailNew.setProfileUrl("screenName");
        // Assert
        errorCollector.checkThat(notableDetailNew.getProfileUrl(),
                is("screenName"));
    }

    @Test
    public void givenNotableDetailsWhenSetScreenNameThenVerifyResult() {
        // Act
        notableDetailNew.setScreenName("profileUrl");
        // Assert
        errorCollector.checkThat(notableDetailNew.getScreenName(),
                is("profileUrl"));
    }
}
