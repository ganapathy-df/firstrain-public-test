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
public class TwitterSpecTest {
    @InjectMocks
    private TwitterSpec twitterSpec;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Test
    public void givenRowsWhenSetRowsThenVerifyResult() {
        // Act
        twitterSpec.setRows(4);
        // Assert
        errorCollector.checkThat(twitterSpec.getRows(), is(4));
    }

    @Test
    public void givenCatIdsWhenSetCatIdsThenVerifyResult() {
        // Act
        twitterSpec.setCatIds(new String[]{"catIds"});
        // Assert
        errorCollector.checkThat(twitterSpec.getCatIds(), is(new String[]{"catIds"}));
    }

    @Test
    public void givenListViewWhenSetListViewThenVerifyResult() {
        // Act
        twitterSpec.setListView(true);
        // Assert
        errorCollector.checkThat(twitterSpec.isListView(), is(true));
    }

    @Test
    public void givenScopeWhenGetScopeThenVerifyResult() {
        // Arrange
        twitterSpec.setScope(null);
        // Act
        int actual = twitterSpec.getScope();
        // Assert
        errorCollector.checkThat(actual, is(-1));
    }

    @Test
    public void givenListViewWhenGetScopeThenVerifyResult() {
        // Arrange
        twitterSpec.setScope(2);
        // Act
        int actual = twitterSpec.getScope();
        // Assert
        errorCollector.checkThat(actual, is(2));
    }
}
