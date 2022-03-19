package com.firstrain.frapi.domain;

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
public class EmailScheduleTest {
    @InjectMocks
    private EmailSchedule emailSchedule;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Test
    public void givenIdWhenSetIdThenVerifyResult() {
        // Act
        emailSchedule.setId(3L);
        // Assert
        errorCollector.checkThat(emailSchedule.getId(),is(3L));
    }

    @Test
    public void givenHoursWhenSetHoursThenVerifyResult() {
        // Act
        emailSchedule.setHours(Collections.singletonList(6));
        // Assert
        errorCollector.checkThat(emailSchedule.getHours(),is(Collections.singletonList(6)));
    }

    @Test
    public void givenDaysWhenSetDaysThenVerifyResult() {
        // Act
        emailSchedule.setDays(Collections.singletonList(6));
        // Assert
        errorCollector.checkThat(emailSchedule.getDays(),is(Collections.singletonList(6)));
    }

    @Test
    public void givenMinuteWhenSetMinuteThenVerifyResult() {
         // Act
        emailSchedule.setMinute(5);
        // Assert
        errorCollector.checkThat(emailSchedule.getMinute(),is(5));
    }
}
