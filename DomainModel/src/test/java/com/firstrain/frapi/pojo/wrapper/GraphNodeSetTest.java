package com.firstrain.frapi.pojo.wrapper;

import com.firstrain.frapi.pojo.GraphNode;
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
public class GraphNodeSetTest {
    @InjectMocks
    private GraphNodeSet graphNodeSet;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Test
    public void givenGraphNodeListWhenSetGraphNodeListThenVerifyResult() {
        // Arrange
        GraphNode graphNode = new GraphNode();
        // Act
        graphNodeSet.setGraphNodeList(Collections.singletonList(graphNode));
        // Assert
        errorCollector.checkThat(graphNodeSet.getGraphNodeList(), is(Collections.singletonList(graphNode)));
    }

    @Test
    public void givenPrimaryIndustryWhenSetPrimaryIndustryThenVerifyResult() {
        // Act
        graphNodeSet.setPrimaryIndustry(true);
        //Assert
        errorCollector.checkThat(graphNodeSet.getPrimaryIndustry(), is(true));
    }
}
