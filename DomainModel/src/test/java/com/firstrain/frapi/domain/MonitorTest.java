package com.firstrain.frapi.domain;

import com.firstrain.frapi.util.DefaultEnums;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;

@RunWith(MockitoJUnitRunner.class)
public class MonitorTest {

    private EmailSchedule emailSchedule;

    @InjectMocks
    private Monitor monitor;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Before
    public void setUp() {
        emailSchedule = new EmailSchedule();
    }

    @Test
    public void givenIdWhenSetIdThenVerifyResult() {
        // Act
        monitor.setId(1L);
        // Assert
        errorCollector.checkThat(monitor.getId(), is(1L));
    }

    @Test
    public void givenNameWhenSetNameThenVerifyResult() {
        // Act
        monitor.setName("name");
        // Assert
        errorCollector.checkThat(monitor.getName(), is("name"));
    }

    @Test
    public void givenNameWhenSetTypeThenVerifyResult() {
        // Act
        monitor.setType(DefaultEnums.TagType.FOLDER);
        // Assert
        errorCollector.checkThat(monitor.getType(), is(DefaultEnums.TagType.FOLDER));
    }

    @Test
    public void givenEmailScheduleWhenSetEmailScheduleThenVerifyResult() {
        // Act
        monitor.setEmailSchedule(emailSchedule);
        // Assert
        errorCollector.checkThat(monitor.getEmailSchedule(), is(emailSchedule));
    }

    @Test
    public void givenEmailScheduleWhenSetMailActiveThenVerifyResult() {
        // Act
        monitor.setOwnedByType(DefaultEnums.OwnedByType.GROUP);
        // Assert
        errorCollector.checkThat(monitor.getOwnedByType(), is(DefaultEnums.OwnedByType.GROUP));
    }

    @Test
    public void givenMailActiveWhenSetMailActiveThenVerifyResult() {
        // Act
        monitor.setMailActive(true);
        // Assert
        errorCollector.checkThat(monitor.isMailActive(), is(true));
    }
}
