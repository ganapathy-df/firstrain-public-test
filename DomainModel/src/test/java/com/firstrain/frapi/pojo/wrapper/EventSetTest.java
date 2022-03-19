package com.firstrain.frapi.pojo.wrapper;

import com.firstrain.frapi.pojo.Event;
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
import static org.hamcrest.CoreMatchers.nullValue;

@RunWith(MockitoJUnitRunner.class)
public class EventSetTest {

    @InjectMocks
    private EventSet eventSet;

    private Event event;
    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Before
    public void setUp() {
        event = new Event();
    }

    @Test
    public void givenEventsWhenSetEventsThenVerifyResult() {
        // Act
        eventSet.setEvents(Collections.singletonList(event));
        // Assert
        errorCollector.checkThat(eventSet.getEvents(),is(Collections.singletonList(event)));
    }

    @Test
    public void givenLinkWhenSetLinkThenVerifyResult() {
        // Act
        eventSet.setLink("link");
        // Assert
        errorCollector.checkThat(eventSet.getLink(),is("link"));
    }

    @Test
    public void givenEventBucketsAndConditionOnLine62TrueWhenSetEventBucketsThenVerifyResult() {
        // Act
        eventSet.setEventBuckets(Collections.singletonMap
                ("test",Collections.singletonList(event)));
        // Assert
        errorCollector.checkThat(eventSet.getEventBuckets(),
                is(Collections.singletonMap("test",Collections.singletonList(event))));
    }

    @Test
    public void givenEventBucketsTrueWhenSetEventBucketsThenVerifyResult() {
        // Arrange
        eventSet.setEvents(Collections.singletonList(event));
        // Act
        eventSet.setEventBuckets(null);
        // Assert
        errorCollector.checkThat(eventSet.getEvents(), nullValue());
    }

    @Test
    public void givenEventTypeWhenSetEventTypeThenVerifyResult() {
        // Act
        eventSet.setEventType(EventSet.EventType.RELATED_COMPANIES_EVENTS);
        // Assert
        errorCollector.checkThat(eventSet.getEventType(),is(EventSet.EventType.RELATED_COMPANIES_EVENTS));
    }

    @Test
    public void givenPrimaryIndustryWhenSetPrimaryIndustryThenVerifyResult() {
        // Act
        eventSet.setPrimaryIndustry(true);
        // Assert
        errorCollector.checkThat(eventSet.getPrimaryIndustry(),is(true));
    }

    @Test
    public void givenScopeWhenSetScopeThenVerifyResult() {
        // Arrange
        eventSet = new EventSet(BaseSet.SectionType.FT);
        // Act
        eventSet.setScope(6);
        // Assert
        errorCollector.checkThat(eventSet.getScope(),is(6));
    }

    @Test
    public void givenUnfilteredDataSizeWhenSetUnfilteredDataSizeThenVerifyResult() {
        // Act
        eventSet.setUnfilteredDataSize(66);
        // Assert
        errorCollector.checkThat(eventSet.getUnfilteredDataSize(),is(66));
    }
}
