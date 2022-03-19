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
public class TrendingEntityTest {
    @InjectMocks
    private TrendingEntity trendingEntity;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Test
    public void given_31DayDocCountWhenSet_31DayDocCountThenVerifyResult() {
        // Act
        trendingEntity.set_31DayDocCount(4);
        // Assert
        errorCollector.checkThat(trendingEntity.get_31DayDocCount(),is(4));
    }

    @Test
    public void givenEntityIdWhenSetEntityIdThenVerifyResult() {
        // Act
        trendingEntity.setEntityId("entityId");
        // Assert
        errorCollector.checkThat(trendingEntity.getEntityId(),is("entityId"));
    }

    @Test
    public void givenEntityCountWhenSetEntityCountThenVerifyResult() {
       // Act
        trendingEntity.setEntityCount(6);
        // Assert
        errorCollector.checkThat(trendingEntity.getEntityCount(),is(6));
    }

    @Test
    public void givenDocCountWhenSetDocCountThenVerifyResult() {
        // Act
        trendingEntity.setDocCount(7);
        // Assert
        errorCollector.checkThat(trendingEntity.getDocCount(),is(7));
    }

    @Test
    public void given_1DayDocCountWhenSet_1DayDocCountThenVerifyResult() {
      // Act
        trendingEntity.set_1DayDocCount(4);
        // Assert
        errorCollector.checkThat(trendingEntity.get_1DayDocCount(),is(4));
    }
}
