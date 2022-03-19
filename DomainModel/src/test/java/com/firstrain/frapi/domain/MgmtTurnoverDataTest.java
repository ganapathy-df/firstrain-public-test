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
public class MgmtTurnoverDataTest {

    private MgmtSummary mgmtSummary;

    @InjectMocks
    private MgmtTurnoverData mgmtTurnoverData;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Before
    public void setUp() {
        mgmtSummary = new MgmtSummary();
        mgmtSummary.setExecHireCount(12);
        mgmtSummary.setExecDepartureCount(13);
        mgmtSummary.setExecInternalMoveCount(14);
        mgmtTurnoverData.setMonthlySummary(Collections.singletonList(mgmtSummary));
    }

    @Test
    public void givenCompanyIdWhenSetCompanyIdThenVerifyResult() {
       // Act
        mgmtTurnoverData.setCompanyId(5);
        // Assert
        errorCollector.checkThat(mgmtTurnoverData.getCompanyId(),is(5));
    }

    @Test
    public void givenMonthlySummaryWhenSetMonthlySummaryThenVerifyResult() {
        // Act
        mgmtTurnoverData.setMonthlySummary(Collections.singletonList(mgmtSummary));
        // Assert
        errorCollector.checkThat(mgmtTurnoverData.getMonthlySummary(),is(Collections.singletonList(mgmtSummary)));
    }

    @Test
    public void givenTotalTurnoverMthWhenSetTotalTurnoverMthThenVerifyResult() {
        // Act
        mgmtTurnoverData.setTotalTurnoverMth(5);
        // Assert
        errorCollector.checkThat(mgmtTurnoverData.getTotalTurnoverMth(),is(5));
    }

    @Test
    public void givenAverageTurnoverMthWhenSetAverageTurnoverMthThenVerifyResult() {
        // Arrange
        mgmtTurnoverData = new MgmtTurnoverData();
        final float valueNew =2f;
        // Act
        mgmtTurnoverData.setAverageTurnoverMth(valueNew);
        // Assert
        errorCollector.checkThat(mgmtTurnoverData.getAverageTurnoverMth(),is(valueNew));
    }

    @Test
    public void givenAverageTurnoverMthWhenGetMgmtChartThenVerifyResult() {
        // Act
        doGetMgmtChartAndAssert("-13|12|14", 14);
    }

    @Test
    public void givenAverageTurnoverNewMthWhenGetMgmtChartThenVerifyResult() {
        // Arrange
        mgmtSummary.setExecHireCount(-1);
        mgmtSummary.setExecDepartureCount(-1);
        mgmtSummary.setExecInternalMoveCount(-1);
        // Act
        doGetMgmtChartAndAssert("--1|-1|-1", 0);    
    }

    private void doGetMgmtChartAndAssert(final String data, final int maxValue) {
        MgmtChart actual = mgmtTurnoverData.getMgmtChart();
        // Assert
        errorCollector.checkThat(actual.getValuesAsString(),is( data));
        errorCollector.checkThat(actual.getMax(),is(maxValue));
    }

    @Test
    public void givenAverageTurnoverMthWhenGetMgmtRGraphChartThenException() {
         // Act
        MgmtChart actual = mgmtTurnoverData.getMgmtRGraphChart();
        // Assert
        errorCollector.checkThat(actual.getMax(),is( 0));
    }

    @Test
    public void givenAverageTurnoverMthWhenGetMgmtRGraphChartThenVerifyResult() {
        // Arrange
        mgmtSummary.setExecDepartureCount(11);
        mgmtSummary.setExecHireCount(12);
        mgmtSummary.setReportMonth(10);
        // Act
        MgmtChart actual = doGetMgmtRGraphChartAndAssert("[[-11,12,14]]");
        errorCollector.checkThat(actual.getMax(),is(14));
    }

    @Test
    public void givenAverageTurnoverLessMthWhenGetMgmtRGraphChartThenVerifyResult() {
        // Arrange
        mgmtSummary.setReportMonth(1);
        mgmtSummary.setExecDepartureCount(-1);
        mgmtSummary.setExecHireCount(-1);
        mgmtSummary.setExecInternalMoveCount(-1);

        // Act
        MgmtChart actual = doGetMgmtRGraphChartAndAssert("[[1,-1,-1]]");
        errorCollector.checkThat(actual.getMax(),is(0));
    }

    private MgmtChart doGetMgmtRGraphChartAndAssert(final String data) {
        MgmtChart actual = mgmtTurnoverData.getMgmtRGraphChart();
        // Assert
        errorCollector.checkThat(actual.getValuesAsString(),is( data));
        errorCollector.checkThat(actual.getLabelAsString().length()>0,is(true));
        return actual;
    }
}
