package com.firstrain.frapi.domain;

import com.firstrain.frapi.pojo.Entity;
import com.firstrain.frapi.pojo.wrapper.DocumentSet;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;

@RunWith(MockitoJUnitRunner.class)
public class TweetTest {

    private Entity entity;
    private DocumentSet relatedDocument;
    private MetaShare metaShare;
    private Date date;

    @InjectMocks
    private Tweet tweet;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Before
    public void setUp() {
        entity = new Entity();
        relatedDocument = new DocumentSet();
        metaShare = new MetaShare();
        date = new Date(1235L);
    }

    @Test
    public void givenVisibilityWhenSetVisibilityThenVerifyResult() {
         // Act
        tweet.setVisibility(metaShare);
        // Assert
        errorCollector.checkThat(tweet.getVisibility(),is(metaShare));
    }

    @Test
    public void givenComboScoreWhenSetComboScoreThenVerifyResult() {
       // Act
        tweet.setComboScore(2);
        // Assert
        errorCollector.checkThat(tweet.getComboScore(),is(2));
    }

    @Test
    public void givenScopeWhenSetScopeThenVerifyResult() {
       // Act
        tweet.setScope(1);
        // Assert
        errorCollector.checkThat(tweet.getScope(),is(1));
    }

    @Test
    public void givenTweetIdWhenSetTweetIdThenVerifyResult() {
        // Act
        tweet.setTweetId("tweetId");
        // Assert
        errorCollector.checkThat(tweet.getTweetId(),is("tweetId"));
    }

    @Test
    public void givenCompanyIdsWhenSetCompanyIdsThenVerifyResult() {
        // Act
        tweet.setCompanyIds(Lists.newArrayList(1));
        // Assert
        errorCollector.checkThat(tweet.getCompanyIds(),is((Collection<Integer>)
                Lists.newArrayList(1)));
    }

    @Test
    public void givenDescriptionWhenSetDescriptionThenVerifyResult() {
       // Act
        tweet.setDescription("description");
        // Assert
        errorCollector.checkThat(tweet.getDescription(),is("description"));
    }

    @Test
    public void givenExpandedLinksWhenSetExpandedLinksThenVerifyResult() {
        // Act
        tweet.setExpandedLinks(Collections.singletonList("expandedLinks"));
        // Assert
        errorCollector.checkThat(tweet.getExpandedLinks(),is((Collection<String>)
                Collections.singletonList("expandedLinks")));
    }

    @Test
    public void givenTweetCreationDateWhenSetTweetCreationDateThenVerifyResult() {
        // Act
        tweet.setTweetCreationDate(date);
        // Assert
        errorCollector.checkThat(tweet.getTweetCreationDate(),is(date));
    }

    @Test
    public void givenLinksWhenSetLinksThenVerifyResult() {
        // Act
        tweet.setLinks( Collections.singletonList("expandedLinks"));
        // Assert
        errorCollector.checkThat(tweet.getLinks(),is((Collection<String>)
                Collections.singletonList("expandedLinks")));
    }

    @Test
    public void givenNameWhenSetNameThenVerifyResult() {
        // Act
        tweet.setName("name");
        // Assert
        errorCollector.checkThat(tweet.getName(),is("name"));
    }

    @Test
    public void givenScreenNameWhenSetScreenNameThenVerifyResult() {
        // Act
        tweet.setScreenName("screenName");
        // Assert
        errorCollector.checkThat(tweet.getScreenName(),is("screenName"));
    }

    @Test
    public void givenTweetWhenSetTweetThenVerifyResult() {
      // Act
        tweet.setTweet("tweet_1");
        // Assert
        errorCollector.checkThat(tweet.getTweet(),is("tweet_1"));
    }

    @Test
    public void givenCoreTweetWhenSetCoreTweetThenVerifyResult() {
        // Act
        tweet.setCoreTweet("coreTweet");
        // Assert
        errorCollector.checkThat(tweet.getCoreTweet(),is("coreTweet"));
    }

    @Test
    public void givenTweetClassWhenSetTweetClassThenVerifyResult() {
         // Act
        tweet.setTweetClass("tweetClass");
        // Assert
        errorCollector.checkThat(tweet.getTweetClass(),is("tweetClass"));
    }

    @Test
    public void givenUserImageWhenSetUserImageThenVerifyResult() {
        // Act
        tweet.setUserImage("userImage");
        // Assert
        errorCollector.checkThat(tweet.getUserImage(),is("userImage"));
    }

    @Test
    public void givenEntityWhenSetEntityThenVerifyResult() {
        // Act
        tweet.setEntity(entity);
        // Assert
        errorCollector.checkThat(tweet.getEntity(),is(entity));
    }

    @Test
    public void givenSourcesWhenSetSourcesThenVerifyResult() {
       // Act
        tweet.setSources(Collections.singletonList("sources"));
        // Assert
        errorCollector.checkThat(tweet.getSources(),is((Collection<String>)
                Collections.singletonList("sources")));
    }

    @Test
    public void givenGroupIdWhenSetGroupIdThenVerifyResult() {
       // Act
        tweet.setGroupId(2L);
        // Assert
        errorCollector.checkThat(tweet.getGroupId(),is(2L));
    }

    @Test
    public void givenGroupLeadWhenSetGroupLeadThenVerifyResult() {
        // Act
        tweet.setGroupLead(true);
        // Assert
        errorCollector.checkThat(tweet.isGroupLead(),is(true));
    }

    @Test
    public void givenGroupSizeWhenSetGroupSizeThenVerifyResult() {
         // Act
        tweet.setGroupSize(4);
        // Assert
        errorCollector.checkThat(tweet.getGroupSize(),is(4));
    }

    @Test
    public void givenTimeLabelWhenSetTimeLabelThenVerifyResult() {
       // Act
        tweet.setTimeLabel("timeLabel");
        // Assert
        errorCollector.checkThat(tweet.getTimeLabel(),is("timeLabel"));
    }

    @Test
    public void givenTitleWhenSetTitleThenVerifyResult() {
         // Act
        tweet.setTitle("title");
        // Assert
        errorCollector.checkThat(tweet.getTitle(),is("title"));
    }

    @Test
    public void givenTopicIdCoreTweetWhenSetTopicIdCoreTweetThenVerifyResult() {
        // Act
        tweet.setTopicIdCoreTweet(Collections.singletonList(2));
        // Assert
        errorCollector.checkThat(tweet.getTopicIdCoreTweet(),is((Collection<Integer>)Collections.singletonList(2)));
    }

    @Test
    public void givenItemIdWhenSetItemIdThenVerifyResult() {
        // Act
        tweet.setItemId(2L);
        // Assert
        errorCollector.checkThat(tweet.getItemId(),is(2L));
    }

    @Test
    public void givenIsBookmarkedWhenSetBookmarkedThenVerifyResult() {
        // Act
        tweet.setBookmarked(true);
        // Assert
        errorCollector.checkThat(tweet.isBookmarked(),is(true));
    }

    @Test
    public void givenRankWhenSetRankThenVerifyResult() {
       // Act
        tweet.setRank(5);
        // Assert
        errorCollector.checkThat(tweet.getRank(),is(5));
    }

    @Test
    public void givenRelatedDocumentWhenSetRelatedDocumentThenVerifyResult() {
        // Act
        tweet.setRelatedDocument(relatedDocument);
        // Assert
        errorCollector.checkThat(tweet.getRelatedDocument(),is(relatedDocument));
    }

    @Test
    public void givenDocumentWhenSetDocumentThenVerifyResult() {
       // Act
        tweet.setDocument(relatedDocument);
        // Assert
        errorCollector.checkThat(tweet.getDocument(),is(relatedDocument));
    }

    @Test
    public void givenTweetUsersWhenSetTweetUsersThenVerifyResult() {
         // Act
        tweet.setTweetUsers(Collections.singletonList(tweet));
        // Assert
        errorCollector.checkThat(tweet.getTweetUsers(),is(Collections.singletonList(tweet)));
    }

    @Test
    public void givenIndustriesWhenSetIndustriesThenVerifyResult() {
       // Act
        tweet.setIndustries(Collections.singletonList(entity));
        // Assert
        errorCollector.checkThat(tweet.getIndustries(),is(Collections.singletonList(entity)));
    }

    @Test
    public void givenTweetUsersWhenGetTweetTitlePlainThenVerifyResult() {
        // Arrange
        tweet.setTweet("t55#yuu888");
        tweet.setCoreTweet("t55#yuu888");
        // Act
        String result = tweet.getTweetTitlePlain();
        // Assert
        errorCollector.checkThat(result,is
                ("t55 <a href='https://twitter.com/search/?q=%23yuu888'>#yuu888</a>"));
    }

    @Test
    public void givenTweetWhenGetTweetTitlePlainThenVerifyResult() {
        // Arrange
        tweet.setTweet("55RT:@#yuu8$88");
        tweet.setLinks(Collections.singletonList("test"));
        // Act
        String result = tweet.getTweetTitlePlain();
        // Assert
        errorCollector.checkThat(result,is
                ( "55RT:@ <a href='https://twitter.com/search/?q=%23yuu8'>#yuu8</a>$88"));
    }

    @Test
    public void givenTweetUsersFalseWhenGetTweetTitlePlainThenVerifyResult() {
        // Arrange
        tweet.setTweet("t55#yuu888");
        tweet.setCoreTweet("t55#yuu888");
        // Act
        String result = tweet.getTweetTitle(true);
        // Assert
        errorCollector.checkThat(result,is("<span>t55 <a href='https://twitter.com/search/?q=%23yuu888'><span " +
                "class='hash-tag'>#yuu888</span></a></span>"));
    }

    @Test
    public void givenTweetFalseWhenGetTweetTitlePlainThenVerifyResult() {
        // Arrange
        tweet.setTweet("55RT:@#yuu8$88");
        tweet.setLinks(Collections.singletonList("test"));
        // Act
        String result = tweet.getTweetTitle(true);
        // Assert
        errorCollector.checkThat(result,is(
                "55RT:@ <a href='https://twitter.com/search/?q=%23yuu8'><span class=" +
                        "'hash-tag'>#yuu8</span></a>$88"));
    }

    @Test
    public void givenTweetUsersWhenGetTweetTitleThenVerifyResult() {
        // Arrange
        populateTweetCoreTweetAndLinks();
        // Act
        String result = tweet.getTweetTitle(true);
        // Assert
        errorCollector.checkThat(result,is(
                "<span>t55 <a href='https://twitter.com/search/?q=%23yuu888'><span class=" +
                        "'hash-tag'>#yuu888</span></a></span>"));
    }

    @Test
    public void givenNewTweetUsersWhenGetTweetTitleThenVerifyResult() {
        // Arrange
        populateTweetCoreTweetAndLinks();
        // Act
        String result = tweet.getTweetTitlePlain();
        // Assert
        errorCollector.checkThat(result,is(
                "t55 <a href='https://twitter.com/search/?q=%23yuu888'>#yuu888</a>"));
    }

    private void populateTweetCoreTweetAndLinks() {
        tweet.setTweet("t55#yuu888");
        tweet.setCoreTweet("t55#yuu888");
        tweet.setLinks(Collections.singletonList("test"));
    }

    @Test
    public void givenTweetRtTagWhenGetTweetTitlePlainThenVerifyResult() {
        // Arrange
        tweet.setTweet("-\tRT: \t@aDf9:\"—\t:—\t :");
        String result = populateTweetAndGetTweetTitle();
        // Assert
        errorCollector.checkThat(result,is(
                "-<span class='rt-tag'>\tRT: <a href='https://twitter.com/%40aDf9'><span class='a-tag" +
                        "'>\t@aDf9:</span></a></span>\"—\t:—\t :"));
    }

    @Test
    public void givenTweetRtTagFalseWhenGetTweetTitlePlainThenVerifyResult() {
        // Arrange
        tweet.setTweet("-\tRT: \t@aDf9:\"—\t:—\t :");
        tweet.setCoreTweet(null);
        tweet.setLinks(null);
        // Act
        String result = tweet.getTweetTitlePlain();
        // Assert
        errorCollector.checkThat(result,is( "-\tRT: <a href='https://twitter.com/%40aDf9'>\t" +
                "@aDf9:</a>\"—\t:—\t :"));
    }

    @Test
    public void givenTweetDollarTagWhenGetTweetTitlePlainThenVerifyResult() {
        // Arrange
        tweet.setTweet("-\t\\$Ty\"+\"+aDf9\"—\t:—\t ");
        String result = populateTweetAndGetTweetTitle();
        // Assert
        errorCollector.checkThat(result,is( "-\t\\<span class='dollar-tag'>$Ty</span>\"+\"+aDf9\"—\t:—\t "));
    }

    private String populateTweetAndGetTweetTitle() {
        tweet.setCoreTweet(null);
        tweet.setLinks(null);
        // Act
        String result = tweet.getTweetTitle(true);
        return result;
    }

    @Test
    public void givenTweetFalseDollarTagWhenGetTweetTitlePlainThenVerifyResult() {
        // Arrange
        tweet.setTweet("-\t\\$Ty\"+\"+aDf9\"—\t:—\t ");
        tweet.setCoreTweet(null);
        tweet.setLinks(null);
        // Act
        String result = tweet.getTweetTitlePlain();
        // Assert
        errorCollector.checkThat(result,is( "-\t\\$Ty\"+\"+aDf9\"—\t:—\t "));
    }

    @Test
    public void givenTweetFalseDollarTagWhenGetTweetTitlePlainThenException() {
        // Act
        String result = tweet.getTweetTitlePlain();
        // Assert
        errorCollector.checkThat(result,nullValue());
    }
}
