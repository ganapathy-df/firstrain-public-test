package com.firstrain.frapi.pojo.wrapper;

import com.firstrain.frapi.domain.VisualizationData.Graph;
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
public class GraphSetTest {
    @InjectMocks
    private GraphSet graphSet;

    @Mock
    private Graph graph;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Test
    public void givenGraphsWhenSetGraphsThenVerifyResult() {
       // Act
        graphSet.setGraphs(Collections.singletonList(graph));
        // Assert
        errorCollector.checkThat(graphSet.getGraphs(),is(Collections.singletonList(graph)));
    }

    @Test
    public void givenGraphListWhenAddAllGraphsThenVerifyResult() {
        // Act
        graphSet.addGraphs(graph);
        // Assert
        errorCollector.checkThat(graphSet.getGraphs(),is(Collections.singletonList(graph)));
    }

    @Test
    public void givenGraphWhenAddGraphsThenVerifyResult() {
       // Act
        graphSet.addAllGraphs(Collections.singletonList(graph));
        // Assert
        errorCollector.checkThat(graphSet.getGraphs(),is(Collections.singletonList(graph)));
    }
}
