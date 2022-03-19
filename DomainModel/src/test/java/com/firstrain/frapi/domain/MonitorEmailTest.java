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
public class MonitorEmailTest {
    @InjectMocks
    private MonitorEmail monitorEmail;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Test
    public void givenEmailIdWhenSetEmailIdThenVerifyResult() {
        // Act
        monitorEmail.setEmailId("emailId");
        // Assert
        errorCollector.checkThat(monitorEmail.getEmailId(),is("emailId"));
    }

    @Test
    public void givenSubjectWhenSetSubjectThenVerifyResult() {
      // Act
        monitorEmail.setSubject("subject");
        // Assert
        errorCollector.checkThat(monitorEmail.getSubject(),is("subject"));
    }

    @Test
    public void givenTimeStampWhenSetTimeStampThenVerifyResult() {
       // Act
        monitorEmail.setTimeStamp("timeStamp");
        // Assert
        errorCollector.checkThat(monitorEmail.getTimeStamp(),is("timeStamp"));
    }

    @Test
    public void givenNullValueWhenEqualsThenVerifyResult() {
        // Act
        boolean actual = monitorEmail.equals(null);
        // Assert
        errorCollector.checkThat(actual,is(false));
    }

    @Test
    public void givenEmailObjectValueWhenEqualsThenVerifyResult() {
        // Act
        boolean actual = monitorEmail.equals(monitorEmail);
        // Assert
        errorCollector.checkThat(actual,is(true));
    }

    @Test
    public void givenEmailEntityObjectValueWhenEqualsThenVerifyResult() {
        // Arrange
        Entity entity = new Entity();
        // Act
        boolean actual = monitorEmail.equals(entity);
        // Assert
        errorCollector.checkThat(actual,is(false));
    }

    @Test
    public void givenMonitorEmailWhenEqualsThenVerifyResult() {
        // Arrange
        MonitorEmail monitorEmailLocal = new MonitorEmail();
        checkEmailMonitor(monitorEmailLocal, "email", false);
        errorCollector.checkThat(monitorEmail.hashCode(),is(31));
    }

    @Test
    public void givenMonitorEmailIdWhenEqualsThenVerifyResult() {
        // Arrange
        MonitorEmail monitorEmailLocal = new MonitorEmail();
        monitorEmail.setEmailId("email");
        checkEmailMonitor(monitorEmailLocal, "emai", false);
    }

    @Test
    public void givenMonitorEmailIdSameWhenEqualsThenVerifyResult() {
        // Arrange
        MonitorEmail monitorEmailLocal = new MonitorEmail();
        monitorEmail.setEmailId("email");
        checkEmailMonitor(monitorEmailLocal, "email", true);
    }

    private void checkEmailMonitor(final MonitorEmail monitorEmailLocal, final String emailId, final boolean result) {
        monitorEmailLocal.setEmailId(emailId);
        // Act
        boolean actual = monitorEmail.equals(monitorEmailLocal);
        // Assert
        errorCollector.checkThat(actual,is(result));
    }

}
