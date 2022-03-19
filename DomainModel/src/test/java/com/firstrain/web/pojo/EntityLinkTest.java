package com.firstrain.web.pojo;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;

@RunWith(MockitoJUnitRunner.class)
public class EntityLinkTest {
    @InjectMocks
    private EntityLink entityLink;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Test
    public void givenPrimaryCompanyWhenSetPrimaryCompanyThenVerifyResult() {
        // Act
        entityLink.setPrimaryCompany("primaryCompany");
        // Assert
        errorCollector.checkThat(entityLink.getPrimaryCompany(), is("primaryCompany"));
    }

    @Test
    public void givenIdWhenSetIdThenVerifyResult() {
        // Act
        entityLink.setId("id");
        // Assert
        errorCollector.checkThat(entityLink.getId(), is("id"));
    }

    @Test
    public void givenPrimaryTopicWhenSetPrimaryTopicThenVerifyResult() {
        // Act
        entityLink.setPrimaryTopic("primaryTopic");
        // Assert
        errorCollector.checkThat(entityLink.getPrimaryTopic(), is("primaryTopic"));
    }

    @Test
    public void givenEntityLinkingScoreWhenSetEntityLinkingScoreThenVerifyResult() {
        // Act
        entityLink.setEntityLinkingScore("entityLinkingScore");
        // Assert
        errorCollector.checkThat(entityLink.getEntityLinkingScore(), is("entityLinkingScore"));
    }
}
