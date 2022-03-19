package com.firstrain.frapi.pojo;

import com.firstrain.frapi.domain.EntityStatus;
import com.firstrain.frapi.domain.MonitorConfig;
import com.firstrain.frapi.domain.MonitorDetails;
import com.firstrain.frapi.domain.MonitorInfo;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;

@RunWith(MockitoJUnitRunner.class)
public class MonitorAPIResponseTest {

    @Mock
    private MonitorInfo monitorInfo;
    @Mock
    private MonitorBriefDetail monitorBriefDetail;

    private MonitorDetails monitorDetails;
    private MonitorConfig monitorConfig;
    private EntityStatus entityStatus;
    private Entity entity;

    @InjectMocks
    private MonitorAPIResponse monitorAPIResponse;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Before
    public void setUp() {
        monitorDetails = new MonitorDetails();
        monitorConfig = new MonitorConfig();
        entityStatus = new EntityStatus();
        entity = new Entity();
    }

    @Test
    public void givenMonitorBriefDetailWhenSetMonitorBriefDetailThenVerifyResult() {
        // Act
        monitorAPIResponse.setMonitorBriefDetail(monitorBriefDetail);
        // Assert
        errorCollector.checkThat(monitorAPIResponse.getMonitorBriefDetail(), is(monitorBriefDetail));
    }

    @Test
    public void givenEntityStatusWhenSetEntityStatusThenVerifyResult() {
        // Act
        monitorAPIResponse.setEntityStatus(entityStatus);
        // Assert
        errorCollector.checkThat(monitorAPIResponse.getEntityStatus(), is(entityStatus));
    }

    @Test
    public void givenMonitorConfigWhenSetMonitorConfigThenVerifyResult() {
        // Act
        monitorAPIResponse.setMonitorConfig(monitorConfig);
        // Assert
        errorCollector.checkThat(monitorAPIResponse.getMonitorConfig(), is(monitorConfig));
    }

    @Test
    public void givenMonitorDetailsWhenSetMonitorDetailsThenVerifyResult() {
        // Act
        monitorAPIResponse.setMonitorDetails(monitorDetails);
        // Assert
        errorCollector.checkThat(monitorAPIResponse.getMonitorDetails(), is(monitorDetails));
    }

    @Test
    public void givenStatusCodeWhenSetStatusCodeThenVerifyResult() {
        // Act
        monitorAPIResponse.setStatusCode(6);
        // Assert
        errorCollector.checkThat(monitorAPIResponse.getStatusCode(), is(6));
    }

    @Test
    public void givenMonitorNameWhenSetMonitorNameThenVerifyResult() {
        // Act
        monitorAPIResponse.setMonitorName("monitorName");
        // Assert
        errorCollector.checkThat(monitorAPIResponse.getMonitorName(), is("monitorName"));
    }

    @Test
    public void givenMonitorIdWhenSetMonitorIdThenVerifyResult() {
        // Act
        monitorAPIResponse.setMonitorId(7L);
        // Assert
        errorCollector.checkThat(monitorAPIResponse.getMonitorId(), is(7L));
    }

    @Test
    public void givenMonitorInfoWhenSetMonitorInfoThenVerifyResult() {
        // Act
        monitorAPIResponse.setMonitorInfo(monitorInfo);
        // Assert
        errorCollector.checkThat(monitorAPIResponse.getMonitorInfo(), is(monitorInfo));
    }

    @Test
    public void givenEntitiesWhenSetEntitiesThenVerifyResult() {
        // Act
        monitorAPIResponse.setEntities(Collections.singletonList(entity));
        // Assert
        errorCollector.checkThat(monitorAPIResponse.getEntities(), is(Collections.singletonList(entity)));
    }
}
