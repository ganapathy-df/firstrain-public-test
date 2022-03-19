package com.firstrain.frapi.pojo.wrapper;

import com.firstrain.frapi.domain.Tweet;
import com.firstrain.frapi.util.DefaultEnums.DateBucketingMode;
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

@RunWith(MockitoJUnitRunner.class)
public class TweetSetTest {

    private GraphNodeSet tweetAccelerometer;
    private Tweet tweet;
    private TweetSet.TweetGroup tweetGroup;
    private Date dateNew;

    @InjectMocks
    private TweetSet tweetSet;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Before
    public void setUp() {
        tweetAccelerometer = new GraphNodeSet();
        tweet = new Tweet();
        tweetGroup = new TweetSet.TweetGroup();
        dateNew = new Date(1234L);
    }

    @Test
    public void givenTotalWhenSetTotalThenVerifyResult() {
        // Act
        tweetSet.setTotal(4);
        // Assert
        errorCollector.checkThat(tweetSet.getTotal(), is(4));
    }

    @Test
    public void givenTweetsWhenSetTweetsThenVerifyResult() {
        // Act
        tweetSet.setTweets(Collections.singletonList(tweet));
        // Assert
        errorCollector.checkThat(tweetSet.getTweets(), is(Collections.singletonList(tweet)));
    }

    @Test
    public void givenTweetIdsWhenSetTweetIdsThenVerifyResult() {
        // Act
        tweetSet.setTweetIds(Collections.singletonList("tweetIds"));
        // Assert
        errorCollector.checkThat(tweetSet.getTweetIds(), is((Collection)
                Collections.singletonList("tweetIds")));
    }

    @Test
    public void givenBucketedTweetsWhenSetBucketedTweetsThenVerifyResult() {
        // Act
        tweetSet.setBucketedTweets(Collections.singletonMap
                ("test", Collections.singletonList(tweet)));
        // Assert
        errorCollector.checkThat(tweetSet.getBucketedTweets(), is(Collections.singletonMap
                ("test", Collections.singletonList(tweet))));
    }

    @Test
    public void givenBucketModeWhenSetBucketModeThenVerifyResult() {
        // Arrange
        tweetSet = new TweetSet(BaseSet.SectionType.FT);
        // Act
        tweetSet.setBucketMode(DateBucketingMode.AUTO);
        // Assert
        errorCollector.checkThat(tweetSet.getBucketMode(), is(DateBucketingMode.AUTO));
    }

    @Test
    public void givenPrimaryIndustryWhenSetPrimaryIndustryThenVerifyResult() {
        // Arrange
        tweetSet = new TweetSet(Collections.singletonList(tweet),1, BaseSet.SectionType.FT);
        // Act
        tweetSet.setPrimaryIndustry(true);
        // Assert
        errorCollector.checkThat(tweetSet.getPrimaryIndustry(), is(true));
    }

    @Test
    public void givenScopeWhenSetScopeThenVerifyResult() {
        // Act
        tweetSet.setScope(6);
        // Assert
        errorCollector.checkThat(tweetSet.getScope(), is(6));
    }

    @Test
    public void givenTweetAccelerometerWhenSetTweetAccelerometerThenVerifyResult() {
        // Act
        tweetSet.setTweetAccelerometer(tweetAccelerometer);
        // Assert
        errorCollector.checkThat(tweetSet.getTweetAccelerometer(), is(tweetAccelerometer));
    }

    @Test
    public void givenTweetAccelerometerWhenSetComboScoreThenVerifyResult() {
        // Act
        tweetGroup.setComboScore(3);
        // Assert
        errorCollector.checkThat(tweetGroup.getComboScore(), is(3));
    }

    @Test
    public void givenTweetAccelerometerWhenSetCreationTimeThenVerifyResult() {
        // Act
        tweetGroup.setCreationTime(dateNew);
        // Assert
        errorCollector.checkThat(tweetGroup.getCreationTime(), is(dateNew));
    }

    @Test
    public void givenTweetAccelerometerWhenSetGroupIdThenVerifyResult() {
        // Act
        tweetGroup.setGroupId(3L);
        // Assert
        errorCollector.checkThat(tweetGroup.getGroupId(), is(3L));
    }

    @Test
    public void givenTweetAccelerometerWhenSetGroupSizeThenVerifyResult() {
        // Act
        tweetGroup.setGroupSize(6);
        // Assert
        errorCollector.checkThat(tweetGroup.getGroupSize(), is(6));
    }

    @Test
    public void givenTweetAccelerometerWhenSetScopeThenVerifyResult() {
        // Arrange
        final byte value = 5;
        // Act
        tweetGroup.setScope(value);
        // Assert
        errorCollector.checkThat(tweetGroup.getScope(), is(value));
    }

    @Test
    public void givenTweetAccelerometerWhenSetTweetIdThenVerifyResult() {
        // Act
        tweetGroup.setTweetId(7L);
        // Assert
        errorCollector.checkThat(tweetGroup.getTweetId(), is(7L));
    }

    @Test
    public void givenTweetAccelerometerWhenSetCatIdThenVerifyResult() {
        // Act
        tweetGroup.setCatId(Collections.singletonList(5));
        // Assert
        errorCollector.checkThat(tweetGroup.getCatId(),
                is((Collection) Collections.singletonList(5)));
    }

    @Test
    public void givenTweetAccelerometerWhenSetAffinityScoreNormalizedThenVerifyResult() {
        // Act
        tweetGroup.setAffinityScoreNormalized(Collections.singletonList(5));
        // Assert
        errorCollector.checkThat(tweetGroup.getAffinityScoreNormalized(),
                is((Collection) Collections.singletonList(5)));
    }

    @Test
    public void givenTweetAccelerometerWhenSetAffinityScopeThenVerifyResult() {
        // Act
        tweetGroup.setAffinityScope(Collections.singletonList(5));
        // Assert
        errorCollector.checkThat(tweetGroup.getAffinityScope(),
                is((Collection) Collections.singletonList(5)));
    }
}
