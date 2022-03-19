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
public class ChartCountSummaryTest {
    @InjectMocks
    private ChartCountSummary chartCountSummary;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Test
    public void givenValueWhenSetValueThenVerifyResult() {
        // Act
        chartCountSummary.setValue(3f);
        // Assert
        errorCollector.checkThat(chartCountSummary.getValue(),is(3f));
    }

    @Test
    public void givenIntensityWhenSetIntensityThenVerifyResult() {
        // Act
        chartCountSummary.setIntensity(4);
        // Assert
        errorCollector.checkThat(chartCountSummary.getIntensity(),is(4));
    }

    @Test
    public void givenSearchIdWhenSetSearchIdThenVerifyResult() {
        // Act
        chartCountSummary.setSearchId(4L);
        // Assert
        errorCollector.checkThat(chartCountSummary.getSearchId(),is(4L));
    }

    @Test
    public void givenEntityIdWhenSetEntityIdThenVerifyResult() {
        // Act
        chartCountSummary.setEntityId("entityId");
        // Assert
        errorCollector.checkThat(chartCountSummary.getEntityId(),is("entityId"));
    }

    @Test
    public void givenOneDayDocCountWhenSetOneDayDocCountThenVerifyResult() {
        // Act
        chartCountSummary.setOneDayDocCount(6);
        // Assert
        errorCollector.checkThat(chartCountSummary.getOneDayDocCount(),is(6));
    }

    @Test
    public void givenThirtyOneDaysDocCountWhenSetThirtyOneDaysDocCountThenVerifyResult() {
        // Act
        chartCountSummary.setThirtyOneDaysDocCount(7);
        // Assert
        errorCollector.checkThat(chartCountSummary.getThirtyOneDaysDocCount(),is(7));
    }
}
