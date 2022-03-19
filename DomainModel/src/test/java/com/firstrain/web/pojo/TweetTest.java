package com.firstrain.web.pojo;

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
public class TweetTest {

    private EntityStandard entity;

    @InjectMocks
    private Tweet tweet;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Before
    public void setUp() {
        entity = new EntityStandard();
    }

    @Test
    public void givenTimeLabelWhenSetTimeLabelThenVerifyResult() {
        // Act
        tweet.setTimeLabel("timeLabel");
        // Assert
        errorCollector.checkThat(tweet.getTimeLabel(),is("timeLabel"));
    }

    @Test
    public void givenTweetIdWhenSetTweetIdThenVerifyResult() {
        // Act
        tweet.setTweetId("tweetId");
        // Assert
        errorCollector.checkThat(tweet.getTweetId(),is("tweetId"));
    }

    @Test
    public void givenExpandedLinksWhenSetExpandedLinksThenVerifyResult() {
      // Act
        tweet.setExpandedLinks(Collections.singletonList("link"));
        // Assert
        errorCollector.checkThat(tweet.getExpandedLinks(),is(Collections.singletonList("link")));
    }

    @Test
    public void givenEntityWhenSetEntityThenVerifyResult() {
        // Act
        tweet.setEntity(entity);
        // Assert
        errorCollector.checkThat(tweet.getEntity(),is(entity));
    }

    @Test
    public void givenTitleWhenSetTitleThenVerifyResult() {
        // Act
        tweet.setTitle("title");
        // Assert
        errorCollector.checkThat(tweet.getTitle(),is("title"));
    }

    @Test
    public void givenTweetTextWhenSetTweetTextThenVerifyResult() {
      // Act
        tweet.setTweetText("tweetText");
        // Assert
        errorCollector.checkThat(tweet.getTweetText(),is("tweetText"));
    }

    @Test
    public void givenLinkWhenSetLinkThenVerifyResult() {
       // Act
        tweet.setLink(Collections.singletonList("link"));
        // Assert
        errorCollector.checkThat(tweet.getLink(),is(Collections.singletonList("link")));
    }

    @Test
    public void givenBookmarkedWhenSetBookmarkedThenVerifyResult() {
       // Act
        tweet.setBookmarked(true);
        // Assert
        errorCollector.checkThat(tweet.getBookmarked(),is(true));
    }

    @Test
    public void givenItemIdWhenSetItemIdThenVerifyResult() {
      // Act
        tweet.setItemId(4L);
        // Assert
        errorCollector.checkThat(tweet.getItemId(),is(4L));
    }

    @Test
    public void givenTimeStampWhenSetTimeStampThenVerifyResult() {
      // Act
        tweet.setTimeStamp("timeStamp");
        // Assert
        errorCollector.checkThat(tweet.getTimeStamp(),is("timeStamp"));
    }

    @Test
    public void givenAuthorNameWhenSetAuthorNameThenVerifyResult() {
      // Act
        tweet.setAuthorName("authorName");
        // Assert
        errorCollector.checkThat(tweet.getAuthorName(),is("authorName"));
    }

    @Test
    public void givenUserNameWhenSetUserNameThenVerifyResult() {
        // Act
        tweet.setUserName("userName");
        // Assert
        errorCollector.checkThat(tweet.getUserName(),is("userName"));
    }

    @Test
    public void givenTweetTextWhenSetTweetHtmlThenVerifyResult() {
       // Act
        tweet.setTweetHtml("tweetText");
        // Assert
        errorCollector.checkThat(tweet.getTweetHtml(),is("tweetText"));
    }

    @Test
    public void givenAuthorDescriptionWhenSetAuthorDescriptionThenVerifyResult() {
       // Act
        tweet.setAuthorDescription("authorDescription");
        // Assert
        errorCollector.checkThat(tweet.getAuthorDescription(),is("authorDescription"));
    }

    @Test
    public void givenAuthorAvatarWhenSetAuthorAvatarThenVerifyResult() {
      // Act
        tweet.setAuthorAvatar("authorAvatar");
        // Assert
        errorCollector.checkThat(tweet.getAuthorAvatar(),is("authorAvatar"));
    }

    @Test
    public void givenGroupSizeWhenSetGroupSizeThenVerifyResult() {
       // Act
        tweet.setGroupSize(6);
        // Assert
        errorCollector.checkThat(tweet.getGroupSize(),is(6));
    }

    @Test
    public void givenGroupIdWhenSetGroupIdThenVerifyResult() {
        // Act
        tweet.setGroupId(3L);
        // Assert
        errorCollector.checkThat(tweet.getGroupId(),is(3L));
    }

    @Test
    public void givenIndustryWhenSetIndustryThenVerifyResult() {
      // Act
        tweet.setIndustry(Collections.singletonList(entity));
        // Assert
        errorCollector.checkThat(tweet.getIndustry(),is(Collections.singletonList(entity)));
    }
}
