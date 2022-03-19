package com.firstrain.web.pojo;

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
public class SearchResultInputBeanTest {

    @InjectMocks
    private SearchResultInputBean searchResultInputBean;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Test
    public void givenDaysCountWhenSetDaysCountThenVerifyResult() {
        // Act
        searchResultInputBean.setDaysCount(4);
        // Assert
        errorCollector.checkThat(searchResultInputBean.getDaysCount(),is(4));
    }

    @Test
    public void givenCountWhenSetCountThenVerifyResult() {
       // Act
        searchResultInputBean.setCount(7);
        // Assert
        errorCollector.checkThat(searchResultInputBean.getCount(),is(7));
    }

    @Test
    public void givenTypeWhenSetTypeThenVerifyResult() {
       // Act
        searchResultInputBean.setType("type");
        // Assert
        errorCollector.checkThat(searchResultInputBean.getType(),is("type"));
    }

    @Test
    public void givenQWhenSetQThenVerifyResult() {
       // Act
        searchResultInputBean.setQ(Collections.singletonList("q"));
        // Assert
        errorCollector.checkThat(searchResultInputBean.getQ(),is(Collections.singletonList("q")));
    }

    @Test
    public void givenScopeWhenSetScopeThenVerifyResult() {
       // Act
        searchResultInputBean.setScope(8);
        // Assert
        errorCollector.checkThat(searchResultInputBean.getScope(),is(8));
    }

    @Test
    public void givenPrimaryCatIdWhenSetPrimaryCatIdThenVerifyResult() {
       // Act
        searchResultInputBean.setPrimaryCatId(5L);
        // Assert
        errorCollector.checkThat(searchResultInputBean.getPrimaryCatId(),is(5L));
    }

    @Test
    public void givenSecondaryCatIdsWhenSetSecondaryCatIdsThenVerifyResult() {
       // Act
        searchResultInputBean.setSecondaryCatIds(Collections.singletonList(1L));
        // Assert
        errorCollector.checkThat(searchResultInputBean.getSecondaryCatIds(),is(Collections.singletonList(1L)));
    }

    @Test
    public void givenAdvanceSortWhenSetAdvanceSortThenVerifyResult() {
       // Act
        searchResultInputBean.setAdvanceSort(true);
        // Assert
        errorCollector.checkThat(searchResultInputBean.isAdvanceSort(),is(true));
    }
}
