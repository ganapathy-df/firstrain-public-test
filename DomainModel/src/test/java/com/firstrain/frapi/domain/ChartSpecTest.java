package com.firstrain.frapi.domain;

import com.firstrain.db.obj.Items;
import com.firstrain.frapi.domain.VisualizationData.ChartType;
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
public class ChartSpecTest {

    @InjectMocks
    private ChartSpec chartSpec;

    @Mock
    private Items items;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Test
    public void givenChartTypesWhenSetChartTypesThenVerifyResult() {
        // Act
        chartSpec.setChartTypes(Collections.singletonList(ChartType.GEO_US));
        // Assert
        errorCollector.checkThat(chartSpec.getChartTypes(),is(Collections.singletonList(ChartType.GEO_US)));
    }

    @Test
    public void givenItemListWhenSetItemListThenVerifyResult() {
        // Act
        chartSpec.setItemList(Collections.singletonList(items));
        // Assert
        errorCollector.checkThat(chartSpec.getItemList(),is(Collections.singletonList(items)));
    }

    @Test
    public void givenMonitorIdWhenSetMonitorIdThenVerifyResult() {
        // Act
        chartSpec.setMonitorId(4L);
        // Assert
        errorCollector.checkThat(chartSpec.getMonitorId(),is(4L));
    }

    @Test
    public void givenFilterQueryWhenSetFilterQueryThenVerifyResult() {
        // Act
        chartSpec.setFilterQuery("filterQuery");
        // Assert
        errorCollector.checkThat(chartSpec.getFilterQuery(),is("filterQuery"));
    }

    @Test
    public void givenItemIdWhenSetItemIdThenVerifyResult() {
        // Act
        chartSpec.setItemId(3L);
        // Assert
        errorCollector.checkThat(chartSpec.getItemId(),is(3L));
    }

    @Test
    public void givenIsIndustryWhenSetIndustryThenVerifyResult() {
        // Act
        chartSpec.setIndustry(true);
        // Assert
        errorCollector.checkThat(chartSpec.isIndustry(),is(true));
    }

    @Test
    public void givenTreeNodeCountWhenSetTreeNodeCountThenVerifyResult() {
        // Act
        chartSpec.setTreeNodeCount(4);
        // Assert
        errorCollector.checkThat(chartSpec.getTreeNodeCount(),is(4));
    }

    @Test
    public void givenIsGraphCustomizedWhenSetGraphCustomizedThenVerifyResult() {
        // Act
        chartSpec.setGraphCustomized(true);
        // Assert
        errorCollector.checkThat(chartSpec.isGraphCustomized(),is(true));
    }

    @Test
    public void givenPartitionDay1WhenSetPartitionDay1ThenVerifyResult() {
        // Act
        chartSpec.setPartitionDay1(6);
        // Assert
        errorCollector.checkThat(chartSpec.getPartitionDay1(),is(6));
    }

    @Test
    public void givenPartitionDay2WhenSetPartitionDay2ThenVerifyResult() {
        // Act
        chartSpec.setPartitionDay2(4);
        // Assert
        errorCollector.checkThat(chartSpec.getPartitionDay2(),is(4));
    }
}
