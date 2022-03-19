package com.firstrain.frapi.domain;

import com.firstrain.frapi.util.DefaultEnums.MgmtServiceGroup;
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
public class MgmtTurnoverSummaryTest {
    @InjectMocks
    private MgmtTurnoverSummary mgmtTurnoverSummary;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Test
    public void givenDetailsWhenSetDetailsThenVerifyResult() {
        // Arrange
        ManagementChange managementChange = new ManagementChange();
        // Act
        mgmtTurnoverSummary.setDetails(Collections.singletonList(managementChange));
        // Assert
        errorCollector.checkThat(mgmtTurnoverSummary.getDetails(),
                is(Collections.singletonList(managementChange)));
    }

    @Test
    public void givenMaxMthWhenSetMaxMthThenVerifyResult() {
        // Act
        mgmtTurnoverSummary.setMaxMth(5);
        // Assert
        errorCollector.checkThat(mgmtTurnoverSummary.getMaxMth(), is(5));
    }

    @Test
    public void givenMaxQtrWhenSetMaxQtrThenVerifyResult() {
        // Act
        mgmtTurnoverSummary.setMaxQtr(7);
        // Assert
        errorCollector.checkThat(mgmtTurnoverSummary.getMaxQtr(), is(7));
    }

    @Test
    public void givenMaxQtrWhenAddLHSEntryThenVerifyResult() {
        // Arrange
        EventTypeEntry entry = new EventTypeEntry();
        // Act
        mgmtTurnoverSummary.addLHSEntry(MgmtServiceGroup.CHANGE_TYPE,
                Collections.singletonList(entry));
        // Assert
        errorCollector.checkThat(mgmtTurnoverSummary.getLHSEntries(MgmtServiceGroup.CHANGE_TYPE)
                , is(Collections.singletonList(entry)));
    }

    @Test
    public void givenMaxQtrWhenAddMgmtTurnoverDataThenVerifyResult() {
        // Arrange
        MgmtTurnoverData data = new MgmtTurnoverData();
        // Act
        mgmtTurnoverSummary.addMgmtTurnoverData("searchToken",
                data);
        // Assert
        errorCollector.checkThat(mgmtTurnoverSummary.getMgmtTurnoverDataFor("searchToken")
                , is(data));
    }
}
