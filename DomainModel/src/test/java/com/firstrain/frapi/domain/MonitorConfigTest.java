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
public class MonitorConfigTest {
    @InjectMocks
    private MonitorConfig monitorConfig;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Test
    public void givenFiltersWhenSetFiltersThenVerifyResult() {
       // Act
        monitorConfig.setFilters(Collections.singletonList("filter"));
        // Assert
        errorCollector.checkThat(monitorConfig.getFilters(),is(Collections.singletonList("filter")));
    }

    @Test
    public void givenQueriesWhenSetQueriesThenVerifyResult() {
        // Arrange
        ItemDetail detail = new ItemDetail();
        // Act
        monitorConfig.setQueries(Collections.singletonList(detail));
        // Assert
        errorCollector.checkThat(monitorConfig.getQueries(),is(Collections.singletonList(detail)));
    }

    @Test
    public void givenMonitorIdWhenSetMonitorIdThenVerifyResult() {
        // Act
        monitorConfig.setMonitorId("monitorId");
        // Assert
        errorCollector.checkThat(monitorConfig.getMonitorId(),is("monitorId"));
    }

    @Test
    public void givenMonitorNameWhenSetMonitorNameThenVerifyResult() {
       // Act
        monitorConfig.setMonitorName("monitorName");
        // Assert
        errorCollector.checkThat(monitorConfig.getMonitorName(),is("monitorName"));
    }

    @Test
    public void givenOwnedByWhenSetOwnedByThenVerifyResult() {
        // Act
        monitorConfig.setOwnedBy(5L);
        // Assert
        errorCollector.checkThat(monitorConfig.getOwnedBy(),is(5L));
    }

    @Test
    public void givenOwnedByTypeWhenSetOwnedByTypeThenVerifyResult() {
        // Act
        monitorConfig.setOwnedByType("ownedByType");
        // Assert
        errorCollector.checkThat(monitorConfig.getOwnedByType(),is("ownedByType"));
    }
}
