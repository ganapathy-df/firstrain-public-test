package com.firstrain.frapi.pojo;

import com.firstrain.frapi.domain.MonitorEmail;
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
public class MonitorEmailAPIResponseTest {
    @InjectMocks
    private MonitorEmailAPIResponse monitorEmailAPIResponse;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Test
    public void givenStatusCodeWhenSetStatusCodeThenVerifyResult() {
        // Act
        monitorEmailAPIResponse.setStatusCode(5);
        // Assert
        errorCollector.checkThat(monitorEmailAPIResponse.getStatusCode(), is(5));
    }

    @Test
    public void givenEmailTemplateWhenSetEmailTemplateThenVerifyResult() {
        // Act
        monitorEmailAPIResponse.setEmailTemplate("emailTemplate");
        // Assert
        errorCollector.checkThat(monitorEmailAPIResponse.getEmailTemplate(), is("emailTemplate"));
    }

    @Test
    public void givenMonitorNameWhenSetMonitorNameThenVerifyResult() {
        // Act
        monitorEmailAPIResponse.setMonitorName("monitorName");
        // Assert
        errorCollector.checkThat(monitorEmailAPIResponse.getMonitorName(), is("monitorName"));
    }

    @Test
    public void givenMonitorIdWhenSetMonitorIdThenVerifyResult() {
        // Act
        monitorEmailAPIResponse.setMonitorId(9L);
        // Assert
        errorCollector.checkThat(monitorEmailAPIResponse.getMonitorId(), is(9L));
    }

    @Test
    public void givenEmailsWhenSetEmailsThenVerifyResult() {
        // Arrange
        MonitorEmail monitorEmailNew = new MonitorEmail();
        // Act
        monitorEmailAPIResponse.setEmails(Collections.singletonList(monitorEmailNew));
        // Assert
        errorCollector.checkThat(monitorEmailAPIResponse.getEmails(),
                is(Collections.singletonList(monitorEmailNew)));
    }
}
