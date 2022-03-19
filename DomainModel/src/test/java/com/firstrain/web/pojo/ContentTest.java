package com.firstrain.web.pojo;

import com.firstrain.frapi.pojo.wrapper.GraphNodeSet;
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
public class ContentTest {

    private Document document;
    private Tweet tweet;
    private Event event;
    private GraphNodeSet graphNodeSet;

    @InjectMocks
    private Content content;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Before
    public void setUp() {
        document = new Document();
        tweet = new Tweet();
        event = new Event();
        graphNodeSet = new GraphNodeSet();
    }

    @Test
    public void givenDocumentsWhenSetDocumentsThenVerifyResult() {
        // Act
        content.setDocuments(Collections.singletonList(document));
        // Assert
        errorCollector.checkThat(content.getDocuments(), is(Collections.singletonList(document)));
    }

    @Test
    public void givenTweetsWhenSetTweetsThenVerifyResult() {
        // Act
        content.setTweets(Collections.singletonList(tweet));
        // Assert
        errorCollector.checkThat(content.getTweets(), is(Collections.singletonList(tweet)));
    }

    @Test
    public void givenTotalItemCountWhenSetTotalItemCountThenVerifyResult() {
        // Act
        content.setTotalItemCount(6);
        // Assert
        errorCollector.checkThat(content.getTotalItemCount(), is(6));
    }

    @Test
    public void givenItemOffsetWhenSetItemOffsetThenVerifyResult() {
        // Act
        content.setItemOffset(7);
        // Assert
        errorCollector.checkThat(content.getItemOffset(), is(7));
    }

    @Test
    public void givenItemCountWhenSetItemCountThenVerifyResult() {
        // Act
        content.setItemCount(3);
        // Assert
        errorCollector.checkThat(content.getItemCount(), is(3));
    }

    @Test
    public void givenEventsWhenSetEventsThenVerifyResult() {
        // Act
        content.setEvents(Collections.singletonList(event));
        // Assert
        errorCollector.checkThat(content.getEvents(), is(Collections.singletonList(event)));
    }

    @Test
    public void givenEventBucketsWhenSetEventBucketsThenVerifyResult() {
        // Act
        content.setEventBuckets(Collections.singletonMap("key",Collections.singletonList(event)));
        // Assert
        errorCollector.checkThat(content.getEventBuckets(),
                is(Collections.singletonMap("key",Collections.singletonList(event))));
    }

    @Test
    public void givenDocumentBucketsWhenSetDocumentBucketsThenVerifyResult() {
        // Act
        content.setDocumentBuckets(Collections.singletonMap("key",Collections.singletonList(document)));
        // Assert
        errorCollector.checkThat(content.getDocumentBuckets(),
                is(Collections.singletonMap("key",Collections.singletonList(document))));
    }

    @Test
    public void givenTweetAccelerometerWhenSetTweetAccelerometerThenVerifyResult() {
        // Act
        content.setTweetAccelerometer(graphNodeSet);
        // Assert
        errorCollector.checkThat(content.getTweetAccelerometer(), is(graphNodeSet));
    }
}
