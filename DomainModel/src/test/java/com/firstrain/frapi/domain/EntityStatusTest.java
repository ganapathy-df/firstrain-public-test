package com.firstrain.frapi.domain;

import com.firstrain.frapi.pojo.Entity;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;

@RunWith(MockitoJUnitRunner.class)
public class EntityStatusTest {

    @InjectMocks
    private EntityStatus entityStatus;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Test
    public void givenEntityWhenSetEntityThenVerifyResult() {
        // Arrange
        final Entity entity = new Entity();
        // Act
        entityStatus.setEntity(entity);
        // Assert
        errorCollector.checkThat(entityStatus.getEntity(), is(entity));
    }

    @Test
    public void givenNameWhenSetNameThenVerifyResult() {
        // Act
        entityStatus.setName("name");
        // Assert
        errorCollector.checkThat(entityStatus.getName(), is("name"));
    }

    @Test
    public void givenIdWhenSetIdThenVerifyResult() {
        // Act
        entityStatus.setId("id");
        // Assert
        errorCollector.checkThat(entityStatus.getId(), is("id"));
    }

    @Test
    public void givenEntityStatusWhenSetEntityStatusThenVerifyResult() {
         // Act
        entityStatus.setEntityStatus(true);
        // Assert
        errorCollector.checkThat(entityStatus.getEntityStatus(), is(true));
    }

}
