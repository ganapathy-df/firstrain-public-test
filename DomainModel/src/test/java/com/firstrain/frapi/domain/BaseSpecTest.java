package com.firstrain.frapi.domain;

import com.firstrain.frapi.pojo.wrapper.BaseSet.SectionType;
import com.firstrain.frapi.util.DefaultEnums.DateBucketingMode;
import org.junit.Before;
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
public class BaseSpecTest {

    private Short type;

    @InjectMocks
    private BaseSpec baseSpec;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Before
    public void setUp() {
        type = 4;
    }

    @Test
    public void givenSectionMultiWhenSetSectionMultiThenVerifyResult() {
        // Act
        baseSpec.setSectionMulti(true);
        // Assert
        errorCollector.checkThat(baseSpec.isSectionMulti(), is(true));
    }

    @Test
    public void givenStartDateWhenSetStartDateThenVerifyResult() {
        // Act
        baseSpec.setStartDate("startDate");
        // Assert
        errorCollector.checkThat(baseSpec.getStartDate(), is("startDate"));
    }

    @Test
    public void givenEndDateWhenSetEndDateThenVerifyResult() {
        // Act
        baseSpec.setEndDate("endDate");
        // Assert
        errorCollector.checkThat(baseSpec.getEndDate(), is("endDate"));
    }

    @Test
    public void givenIsCustomizedWhenSetCustomizedThenVerifyResult() {
        // Act
        baseSpec.setCustomized(true);
        // Assert
        errorCollector.checkThat(baseSpec.isCustomized(), is(true));
    }

    @Test
    public void givenIncludeSourceIdsSSVWhenSetIncludeSourceIdsSSVThenVerifyResult() {
        // Act
        baseSpec.setIncludeSourceIdsSSV("includeSourceIdsSSV");
        // Assert
        errorCollector.checkThat(baseSpec.getIncludeSourceIdsSSV(), is("includeSourceIdsSSV"));
    }

    @Test
    public void givenExcludeSourceIdsSSVWhenSetExcludeSourceIdsSSVThenVerifyResult() {
        // Act
        baseSpec.setExcludeSourceIdsSSV("excludeSourceIdsSSV");
        // Assert
        errorCollector.checkThat(baseSpec.getExcludeSourceIdsSSV(), is("excludeSourceIdsSSV"));
    }

    @Test
    public void givenNeedPaginationWhenSetNeedPaginationThenVerifyResult() {
        // Act
        baseSpec.setNeedPagination(true);
        // Assert
        errorCollector.checkThat(baseSpec.getNeedPagination(), is(true));
    }

    @Test
    public void givenStartEventTypeWhenSetStartEventTypeThenVerifyResult() {
        // Act
        baseSpec.setStartEventType(4);
        // Assert
        errorCollector.checkThat(baseSpec.getStartEventType(), is(4));
    }

    @Test
    public void givenEndEventTypeWhenSetEndEventTypeThenVerifyResult() {
        // Act
        baseSpec.setEndEventType(7);
        // Assert
        errorCollector.checkThat(baseSpec.getEndEventType(), is(7));
    }

    @Test
    public void givenCacheKeyWhenSetCacheKeyThenVerifyResult() {
        // Act
        baseSpec.setCacheKey("cacheKey");
        // Assert
        errorCollector.checkThat(baseSpec.getCacheKey(), is("cacheKey"));
    }

    @Test
    public void givenCountWhenSetCountThenVerifyResult() {
        // Act
        baseSpec.setCount(type);
        // Assert
        errorCollector.checkThat(baseSpec.getCount(), is(type));
    }

    @Test
    public void givenTopCompetitorCountWhenSetTopCompetitorCountThenVerifyResult() {
        // Act
        baseSpec.setTopCompetitorCount(type);
        // Assert
        errorCollector.checkThat(baseSpec.getTopCompetitorCount(), is(type));
    }

    @Test
    public void givenFillingWhenSetFillingThenVerifyResult() {
        // Act
        baseSpec.setFilling(new String[]{"filling"});
        // Assert
        errorCollector.checkThat(baseSpec.getFilling(), is(new String[]{"filling"}));
    }

    @Test
    public void givenTypeWhenSetTypeThenVerifyResult() {
        // Act
        baseSpec.setType(SectionType.FR);
        // Assert
        errorCollector.checkThat(baseSpec.getType(), is(SectionType.FR));
    }

    @Test
    public void givenDaysCountWhenSetDaysCountThenVerifyResult() {
        // Act
        baseSpec.setDaysCount(5);
        // Assert
        errorCollector.checkThat(baseSpec.getDaysCount(), is(5));
    }

    @Test
    public void givenCsExcludedEventTypeGroupWhenSetCsExcludedEventTypeGroupThenVerifyResult() {
        // Act
        baseSpec.setCsExcludedEventTypeGroup("csExcludedEventTypeGroup");
        // Assert
        errorCollector.checkThat(baseSpec.getCsExcludedEventTypeGroup(), is("csExcludedEventTypeGroup"));
    }

    @Test
    public void givenStartWhenSetStartThenVerifyResult() {
        // Act
        baseSpec.setStart(type);
        // Assert
        errorCollector.checkThat(baseSpec.getStart(), is(type));
    }

    @Test
    public void givenScopeWhenSetScopeThenVerifyResult() {
        // Act
        baseSpec.setScope(4);
        // Assert
        errorCollector.checkThat(baseSpec.getScope(), is(4));
    }

    @Test
    public void givenNeedBucketWhenSetNeedBucketThenVerifyResult() {
        // Act
        baseSpec.setNeedBucket(true);
        // Assert
        errorCollector.checkThat(baseSpec.getNeedBucket(), is(true));
    }

    @Test
    public void givenBucketModeWhenSetBucketModeThenVerifyResult() {
        // Act
        baseSpec.setBucketMode(DateBucketingMode.AUTO);
        // Assert
        errorCollector.checkThat(baseSpec.getBucketMode(), is(DateBucketingMode.AUTO));
    }

    @Test
    public void givenEventGroupWhenSetEventGroupThenVerifyResult() {
        // Act
        baseSpec.setEventGroup(Collections.singletonList(3));
        // Assert
        errorCollector.checkThat(baseSpec.getEventGroup(), is(Collections.singletonList(3)));
    }

    @Test
    public void givenIpadWhenSetIpadThenVerifyResult() {
        // Act
        baseSpec.setIpad(true);
        // Assert
        errorCollector.checkThat(baseSpec.getIpad(), is(true));
    }

    @Test
    public void givenNeedMatchedEntitiesWhenSetNeedMatchedEntitiesThenVerifyResult() {
        // Act
        baseSpec.setNeedMatchedEntities(true);
        // Assert
        errorCollector.checkThat(baseSpec.getNeedMatchedEntities(), is(true));
    }

    @Test
    public void givenLastDayWhenSetLastDayThenVerifyResult() {
        // Act
        baseSpec.setLastDay("lastDay");
        // Assert
        errorCollector.checkThat(baseSpec.getLastDay(), is("lastDay"));
    }

    @Test
    public void givenNeedImageWhenSetNeedImageThenVerifyResult() {
        // Act
        baseSpec.setNeedImage(true);
        // Assert
        errorCollector.checkThat(baseSpec.getNeedImage(), is(true));
    }

    @Test
    public void givenCountPerEntityWhenSetCountPerEntityThenVerifyResult() {
        // Act
        baseSpec.setCountPerEntity(type);
        // Assert
        errorCollector.checkThat(baseSpec.getCountPerEntity(), is(type));
    }

    @Test
    public void givenExcludeArticleIdsSSVWhenSetExcludeArticleIdsSSVThenVerifyResult() {
        // Act
        baseSpec.setExcludeArticleIdsSSV("excludeArticleIdsSSV");
        // Assert
        errorCollector.checkThat(baseSpec.getExcludeArticleIdsSSV(), is("excludeArticleIdsSSV"));
    }

    @Test
    public void givenIndustryClassificationIdWhenSetIndustryClassificationIdThenVerifyResult() {
        // Act
        baseSpec.setIndustryClassificationId(type);
        // Assert
        errorCollector.checkThat(baseSpec.getIndustryClassificationId(), is(type));
    }

    @Test
    public void givenOnlyIndustryWhenSetOnlyIndustryThenVerifyResult() {
        // Act
        baseSpec.setOnlyIndustry(true);
        // Assert
        errorCollector.checkThat(baseSpec.getOnlyIndustry(), is(true));
    }

    @Test
    public void givenNeedRelatedDocWhenSetNeedRelatedDocThenVerifyResult() {
        // Act
        baseSpec.setNeedRelatedDoc(true);
        // Assert
        errorCollector.checkThat(baseSpec.isNeedRelatedDoc(), is(true));
    }

    @Test
    public void givenNeedPhraseWhenSetNeedPhraseThenVerifyResult() {
        // Act
        baseSpec.setNeedPhrase(true);
        // Assert
        errorCollector.checkThat(baseSpec.isNeedPhrase(), is(true));
    }
}
