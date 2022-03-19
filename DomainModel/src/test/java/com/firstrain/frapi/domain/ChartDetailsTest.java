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

import static org.hamcrest.CoreMatchers.is;

@RunWith(MockitoJUnitRunner.class)
public class ChartDetailsTest {
    @InjectMocks
    private ChartDetails chartDetails;

    private ChartCountSummary chartCountSummary;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Before
    public void setUp() {
        chartCountSummary = new ChartCountSummary();
    }

    @Test
    public void givenTrendingCompanyListWhenSetTrendingCompanyListThenVerifyResult() {
       // Act
        chartDetails.setTrendingCompanyList(Collections.singletonList(chartCountSummary));
        // Assert
        errorCollector.checkThat(chartDetails.getTrendingCompanyList(),is(Collections.singletonList(chartCountSummary)));
    }

    @Test
    public void givenTrendingTopicListWhenSetTrendingTopicListThenVerifyResult() {
        // Act
        chartDetails.setTrendingTopicList(Collections.singletonList(chartCountSummary));
        // Assert
        errorCollector.checkThat(chartDetails.getTrendingTopicList(),is(Collections.singletonList(chartCountSummary)));
    }

    @Test
    public void givenMonitorTrendingEntityListWhenSetMonitorTrendingEntityListThenVerifyResult() {
        // Act
        chartDetails.setMonitorTrendingEntityList(Collections.singletonList(chartCountSummary));
        // Assert
        errorCollector.checkThat(chartDetails.getMonitorTrendingEntityList(),is(Collections.singletonList(chartCountSummary)));
    }

    @Test
    public void givenTrendingRegionListWhenSetTrendingRegionListThenVerifyResult() {
        // Act
        chartDetails.setTrendingRegionList(Collections.singletonList(chartCountSummary));
        // Assert
        errorCollector.checkThat(chartDetails.getTrendingRegionList(),is(Collections.singletonList(chartCountSummary)));
    }

    @Test
    public void givenAccmeterListWhenSetAccmeterListThenVerifyResult() {
       // Act
        chartDetails.setAccmeterList(Collections.singletonList(chartCountSummary));
        // Assert
        errorCollector.checkThat(chartDetails.getAccmeterList(),is(Collections.singletonList(chartCountSummary)));
    }
}
