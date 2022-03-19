package com.firstrain.frapi.pojo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;

@RunWith(MockitoJUnitRunner.class)
public class ItemsTest {
    private Entity entity;

    @InjectMocks
    private Items items;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Before
    public void setUp() {
        entity = new Entity();
    }

    @Test
    public void givenDocCountWhenSetDocCountThenVerifyResult() {
        // Act
        items.setDocCount(5);
        // Assert
        errorCollector.checkThat(items.getDocCount(), is(5));
    }

    @Test
    public void givenIdWhenSetIdThenVerifyResult() {
        // Act
        items.setId(7L);
        // Assert
        errorCollector.checkThat(items.getId(), is(7L));
    }

    @Test
    public void givenNameWhenSetNameThenVerifyResult() {
        // Act
        items.setName("name");
        // Assert
        errorCollector.checkThat(items.getName(), is("name"));
    }

    @Test
    public void givenQWhenSetQThenVerifyResult() {
        // Act
        items.setQ("q");
        // Assert
        errorCollector.checkThat(items.getQ(), is("q"));
    }

    @Test
    public void givenFqWhenSetFqThenVerifyResult() {
        // Act
        items.setFq("fq");
        // Assert
        errorCollector.checkThat(items.getFq(), is("fq"));
    }

    @Test
    public void givenScopeWhenSetScopeThenVerifyResult() {
        // Act
        items.setScope(8);
        // Assert
        errorCollector.checkThat(items.getScope(), is(8));
    }

    @Test
    public void givenDataWhenSetDataThenVerifyResult() {
        // Act
        items.setData("data");
        // Assert
        errorCollector.checkThat(items.getData(), is("data"));
    }

    @Test
    public void givenEntityWhenSetEntityThenVerifyResult() {
        // Act
        items.setEntity(entity);
        // Assert
        errorCollector.checkThat(items.getEntity(), is(entity));
    }
}
