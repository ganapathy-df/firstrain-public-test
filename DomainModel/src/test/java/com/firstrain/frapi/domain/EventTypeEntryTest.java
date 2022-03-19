package com.firstrain.frapi.domain;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;

@RunWith(MockitoJUnitRunner.class)
public class EventTypeEntryTest {
    @InjectMocks
    private EventTypeEntry eventTypeEntry;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Test
    public void givenGetIdWhenSetGetIdThenVerifyResult() {
         // Act
        eventTypeEntry.setGetId(1);
        // Assert
        errorCollector.checkThat(eventTypeEntry.getGetId(),is(1));
    }

    @Test
    public void givenGetIdStrWhenSetGetIdStrThenVerifyResult() {
        // Act
        eventTypeEntry.setGetIdStr("getIdStr");
        // Assert
        errorCollector.checkThat(eventTypeEntry.getGetIdStr(),is("getIdStr"));
    }

    @Test
    public void givenCountWhenSetCountThenVerifyResult() {
        // Act
        eventTypeEntry.setCount(3);
        // Assert
        errorCollector.checkThat(eventTypeEntry.getCount(),is(3));
    }

    @Test
    public void givenIsIncludedWhenSetIncludedThenVerifyResult() {
        // Act
        eventTypeEntry.setIncluded(true);
        // Assert
        errorCollector.checkThat(eventTypeEntry.isIncluded(),is(true));
    }
}
