package com.firstrain.frapi.domain;

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
public class IndustryBriefDomainTest {
    @InjectMocks
    private IndustryBriefDomain industryBriefDomain;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Test
    public void givenCategoryIdsSetWhenSetCategoryIdsSetThenVerifyResult() {
       // Act
        industryBriefDomain.setCategoryIdsSet(Collections.singleton("categoryIdsSet"));
        //Assert
        errorCollector.checkThat(industryBriefDomain.getCategoryIdsSet(),
                is(Collections.singleton("categoryIdsSet")));
    }

    @Test
    public void givenTopicIdsArrayWhenSetTopicIdsArrayThenVerifyResult() {
        // Act
        industryBriefDomain.setTopicIdsArray(new int[]{3});
        //Assert
        errorCollector.checkThat(industryBriefDomain.getTopicIdsArray(),is(new int[]{3}));
    }

    @Test
    public void givenCompanyIdsArrayWhenSetCompanyIdsArrayThenVerifyResult() {
        // Act
        industryBriefDomain.setCompanyIdsArray(new int[]{4});
        //Assert
        errorCollector.checkThat(industryBriefDomain.getCompanyIdsArray(),is(new int[]{4}));
    }

    @Test
    public void givenTopicIdWhenSetTopicIdThenVerifyResult() {
       // Act
        industryBriefDomain.setTopicId(6);
        //Assert
        errorCollector.checkThat(industryBriefDomain.getTopicId(),is(6));
    }

    @Test
    public void givenTopicTokenWhenSetTopicTokenThenVerifyResult() {
       // Act
        industryBriefDomain.setTopicToken("topicToken");
        //Assert
        errorCollector.checkThat(industryBriefDomain.getTopicToken(),is("topicToken"));
    }
}
