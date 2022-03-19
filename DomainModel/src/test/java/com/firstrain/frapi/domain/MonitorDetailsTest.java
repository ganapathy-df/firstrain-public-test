package com.firstrain.frapi.domain;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;

@RunWith(MockitoJUnitRunner.class)
public class MonitorDetailsTest {
    @InjectMocks
    private MonitorDetails monitorDetails;

    private MonitorInfo monitorInfo;
    private MonitorBucketForTitle monitorBucketForTitle;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Before
    public void setUp() {
        monitorInfo = new MonitorInfo();
        monitorBucketForTitle = new MonitorBucketForTitle();
    }

    @Test
    public void givenUserIdWhenSetUserIdThenVerifyResult() {
        // Act
        monitorDetails.setUserId("userId");
        // Assert
        errorCollector.checkThat(monitorDetails.getUserId(), is("userId"));
    }

    @Test
    public void givenUserNameWhenSetUserNameThenVerifyResult() {
        // Act
        monitorDetails.setUserName("userName");
        // Assert
        errorCollector.checkThat(monitorDetails.getUserName(), is("userName"));
    }

    @Test
    public void givenEmailWhenSetEmailThenVerifyResult() {
        // Act
        monitorDetails.setEmail("email");
        // Assert
        errorCollector.checkThat(monitorDetails.getEmail(), is("email"));
    }

    @Test
    public void givenMyMonitorListWhenSetMyMonitorListThenVerifyResult() {
        // Act
        monitorDetails.setMyMonitorList(Collections.singletonList(monitorInfo));
        // Assert
        errorCollector.checkThat(monitorDetails.getMyMonitorList(), is(Collections.singletonList(monitorInfo)));
    }

    @Test
    public void givenGroupMonitorListWhenSetGroupMonitorListThenVerifyResult() {
        // Act
        monitorDetails.setGroupMonitorList(Collections.singletonList(monitorInfo));
        // Assert
        errorCollector.checkThat(monitorDetails.getGroupMonitorList(), is(Collections.singletonList(monitorInfo)));
    }

    @Test
    public void givenMonitorsWhenSetMonitorsThenVerifyResult() {
        // Arrange
        Map<String, List<MonitorInfo>> linkMap = new LinkedHashMap<String, List<MonitorInfo>>();
        linkMap.put("test", Collections.singletonList(monitorInfo));
        // Act
        monitorDetails.setMonitors((LinkedHashMap)linkMap);
        // Assert
        errorCollector.checkThat(monitorDetails.getMonitors(), is(linkMap));
    }

    @Test
    public void givenTopMonitorListWhenSetTopMonitorListThenVerifyResult() {
        // Act
        monitorDetails.setTopMonitorList(Collections.singletonList(monitorInfo));
        // Assert
        errorCollector.checkThat(monitorDetails.getTopMonitorList(), is(Collections.singletonList(monitorInfo)));
    }

    @Test
    public void givenTitlesForMonitorBucketsWhenSetTitlesForMonitorBucketsThenVerifyResult() {
        // Act
        monitorDetails.setTitlesForMonitorBuckets(Collections.singletonList(monitorBucketForTitle));
        // Assert
        errorCollector.checkThat(monitorDetails.getTitlesForMonitorBuckets(), is(Collections.singletonList(monitorBucketForTitle)));
    }
}
