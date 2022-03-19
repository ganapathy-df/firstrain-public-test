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
public class MgmtSummaryTest {
    @InjectMocks
    private MgmtSummary mgmtSummary;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Test
    public void givenBoardDepartureCountWhenSetBoardDepartureCountThenVerifyResult() {
        // Act
        mgmtSummary.setBoardDepartureCount(3);
        // Assert
        errorCollector.checkThat(mgmtSummary.getBoardDepartureCount(), is(3));
    }

    @Test
    public void givenBoardHireCountWhenSetBoardHireCountThenVerifyResult() {
        // Act
        mgmtSummary.setBoardHireCount(2);
        // Assert
        errorCollector.checkThat(mgmtSummary.getBoardHireCount(), is(2));
    }

    @Test
    public void givenBoardInternalMoveCountWhenSetBoardInternalMoveCountThenVerifyResult() {
        // Act
        mgmtSummary.setBoardInternalMoveCount(4);
        // Assert
        errorCollector.checkThat(mgmtSummary.getBoardInternalMoveCount(), is(4));
    }

    @Test
    public void givenExecDepartureCountWhenSetExecDepartureCountThenVerifyResult() {
        // Act
        mgmtSummary.setExecDepartureCount(6);
        // Assert
        errorCollector.checkThat(mgmtSummary.getExecDepartureCount(), is(6));
    }

    @Test
    public void givenExecHireCountWhenSetExecHireCountThenVerifyResult() {
        // Act
        mgmtSummary.setExecHireCount(5);
        // Assert
        errorCollector.checkThat(mgmtSummary.getExecHireCount(), is(5));
    }

    @Test
    public void givenExecInternalMoveCountWhenSetExecInternalMoveCountThenVerifyResult() {
        // Act
        mgmtSummary.setExecInternalMoveCount(8);
        // Assert
        errorCollector.checkThat(mgmtSummary.getExecInternalMoveCount(), is(8));
    }

    @Test
    public void givenOthersDepartureCountWhenSetOthersDepartureCountThenVerifyResult() {
        // Act
        mgmtSummary.setOthersDepartureCount(9);
        // Assert
        errorCollector.checkThat(mgmtSummary.getOthersDepartureCount(), is(9));
    }

    @Test
    public void givenOthersHireCountWhenSetOthersHireCountThenVerifyResult() {
        // Act
        mgmtSummary.setOthersHireCount(7);
        // Assert
        errorCollector.checkThat(mgmtSummary.getOthersHireCount(), is(7));
    }

    @Test
    public void givenOthersInternalMoveCountWhenSetOthersInternalMoveCountThenVerifyResult() {
        // Act
        mgmtSummary.setOthersInternalMoveCount(2);
        // Assert
        errorCollector.checkThat(mgmtSummary.getOthersInternalMoveCount(), is(2));
    }

    @Test
    public void givenReportYearWhenSetReportYearThenVerifyResult() {
        // Act
        mgmtSummary.setReportYear(5);
        // Assert
        errorCollector.checkThat(mgmtSummary.getReportYear(), is(5));
    }

    @Test
    public void givenReportMonthWhenSetReportMonthThenVerifyResult() {
        // Act
        mgmtSummary.setReportMonth(9);
        // Assert
        errorCollector.checkThat(mgmtSummary.getReportMonth(), is(9));
    }

    @Test
    public void givenReportQuarterWhenSetReportQuarterThenVerifyResult() {
        // Act
        mgmtSummary.setReportQuarter(7);
        // Assert
        errorCollector.checkThat(mgmtSummary.getReportQuarter(), is(7));
    }

    @Test
    public void givenReportQuarterWhenGetTotalHireThenVerifyResult() {
        // Arrange
        mgmtSummary.setExecHireCount(3);
        mgmtSummary.setBoardHireCount(6);
        mgmtSummary.setOthersHireCount(7);
        // Act
        int actual = mgmtSummary.getTotalHire();
        // Assert
        errorCollector.checkThat(actual, is(16));
    }

    @Test
    public void givenReportQuarterWhenGetTotalDepartureThenVerifyResult() {
        // Arrange
        mgmtSummary.setExecDepartureCount(3);
        mgmtSummary.setBoardDepartureCount(4);
        mgmtSummary.setOthersDepartureCount(9);
        // Act
        int actual = mgmtSummary.getTotalDeparture();
        // Assert
        errorCollector.checkThat(actual, is(16));
    }

    @Test
    public void givenReportQuarterWhenGetTotalInternalMoveThenVerifyResult() {
        // Arrange
        mgmtSummary.setExecInternalMoveCount(3);
        mgmtSummary.setBoardInternalMoveCount(1);
        mgmtSummary.setOthersInternalMoveCount(2);
        // Act
        int actual = mgmtSummary.getTotalInternalMove();
        // Assert
        errorCollector.checkThat(actual, is(6));
    }
}
