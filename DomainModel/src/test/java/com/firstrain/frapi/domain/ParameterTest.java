package com.firstrain.frapi.domain;

import com.firstrain.frapi.util.CompanyTeam;
import com.firstrain.frapi.util.DefaultEnums.DateBucketingMode;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;

@RunWith(MockitoJUnitRunner.class)
public class ParameterTest {

    @InjectMocks
    private Parameter parameter;

    private Short value;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Before
    public void setUp() {
        value = 3;
    }

    @Test
    public void givenCompanyTeamIdWhenSetCompanyTeamIdThenVerifyResult() {
        // Act
        parameter.setCompanyTeamId(4);
        // Assert
        errorCollector.checkThat(parameter.getCompanyTeamId(), is(4));
    }

    @Test
    public void givenCompanyTeamEnumAndConditionOnLine71TrueWhenSetCompanyTeamEnumThenVerifyResult() {
        // Act
        parameter.setCompanyTeamEnum(CompanyTeam.EXECUTIVE);
        // Assert
        errorCollector.checkThat(parameter.getCompanyTeamEnum(), is(CompanyTeam.EXECUTIVE));
    }

    @Test
    public void givenCompanyTeamEWhenGetCompanyTeamEnumThenVerifyResult() {
        // Act
        CompanyTeam actual = parameter.getCompanyTeamEnum(1);
        // Assert
        errorCollector.checkThat(actual, is(CompanyTeam.BOARD_OF_DIRECTORS));
    }

    @Test
    public void givenCompanyExecTeamEWhenGetCompanyTeamEnumThenVerifyResult() {
        // Act
        CompanyTeam actual = parameter.getCompanyTeamEnum(2);
        // Assert
        errorCollector.checkThat(actual, is(CompanyTeam.EXECUTIVE));
    }

    @Test
    public void givenCompanyExec3TeamEWhenGetCompanyTeamEnumThenVerifyResult() {
        // Act
        CompanyTeam actual = parameter.getCompanyTeamEnum(3);
        // Assert
        errorCollector.checkThat(actual, is(CompanyTeam.MANAGEMENT));
    }

    @Test
    public void givenCompanyExec0TeamEWhenGetCompanyTeamEnumThenVerifyResult() {
        // Act
        CompanyTeam actual = parameter.getCompanyTeamEnum(0);
        // Assert
        errorCollector.checkThat(actual, nullValue());
    }

    @Test
    public void givenCompanyExec0TeamEWhenSetCompanyTeamEnumThenVerifyResult() {
        // Arrange
        parameter.setCompanyTeamId(1);
        // Act
        parameter.setCompanyTeamEnum(null);
        // Assert
        errorCollector.checkThat(parameter.getCompanyTeamEnum(), is(CompanyTeam.BOARD_OF_DIRECTORS));
    }

    @Test
    public void givenCountWhenSetCountThenVerifyResult() {
        // Act
        parameter.setCount(value);
        // Assert
        errorCollector.checkThat(parameter.getCount(), is(value));
    }

    @Test
    public void givenFilingTypeWhenSetFilingTypeThenVerifyResult() {
        // Act
        parameter.setFilingType("filingType");
        // Assert
        errorCollector.checkThat(parameter.getFilingType(), is("filingType"));
    }

    @Test
    public void givenTopCompetitorsWhenSetTopCompetitorsThenVerifyResult() {
        // Act
        parameter.setTopCompetitors(value);
        // Assert
        errorCollector.checkThat(parameter.getTopCompetitors(), is(value));
    }

    @Test
    public void givenNextStartIdWhenSetNextStartIdThenVerifyResult() {
        // Act
        parameter.setNextStartId(4L);
        // Assert
        errorCollector.checkThat(parameter.getNextStartId(), is(4L));
    }

    @Test
    public void givenNumberOfSubSectionsWhenSetNumberOfSubSectionsThenVerifyResult() {
        // Act
        parameter.setNumberOfSubSections(5);
        // Assert
        errorCollector.checkThat(parameter.getNumberOfSubSections(), is(5));
    }

    @Test
    public void givenDaysWhenSetDaysThenVerifyResult() {
        // Act
        parameter.setDays(8);
        // Assert
        errorCollector.checkThat(parameter.getDays(), is(8));
    }

    @Test
    public void givenFilterQueryWhenSetFilterQueryThenVerifyResult() {
        // Act
        parameter.setFilterQuery("filterQuery");
        // Assert
        errorCollector.checkThat(parameter.getFilterQuery(), is("filterQuery"));
    }

    @Test
    public void givenNeedBucketWhenSetNeedBucketThenVerifyResult() {
        // Act
        parameter.setNeedBucket(true);
        // Assert
        errorCollector.checkThat(parameter.getNeedBucket(), is(true));
    }

    @Test
    public void givenNDaysFromTodayWhenSetnDaysFromTodayThenVerifyResult() {
        // Act
        parameter.setnDaysFromToday(6);
        // Assert
        errorCollector.checkThat(parameter.getnDaysFromToday(), is(6));
    }

    @Test
    public void givenCsExcludedEventTypeGroupWhenSetCsExcludedEventTypeGroupThenVerifyResult() {
        // Act
        parameter.setCsExcludedEventTypeGroup("csExcludedEventTypeGroup");
        // Assert
        errorCollector.checkThat(parameter.getCsExcludedEventTypeGroup(), is("csExcludedEventTypeGroup"));
    }

    @Test
    public void givenStartWhenSetStartThenVerifyResult() {
        // Act
        parameter.setStart(value);
        // Assert
        errorCollector.checkThat(parameter.getStart(), is(value));
    }

    @Test
    public void givenScopeWhenSetScopeThenVerifyResult() {
        // Act
        parameter.setScope(6);
        // Assert
        errorCollector.checkThat(parameter.getScope(), is(6));
    }

    @Test
    public void givenBucketModeWhenSetBucketModeThenVerifyResult() {
        // Act
        parameter.setBucketMode(DateBucketingMode.AUTO);
        // Assert
        errorCollector.checkThat(parameter.getBucketMode(), is(DateBucketingMode.AUTO));
    }

    @Test
    public void givenNeedPaginationWhenSetNeedPaginationThenVerifyResult() {
        // Act
        parameter.setNeedPagination(true);
        // Assert
        errorCollector.checkThat(parameter.getNeedPagination(), is(true));
    }

    @Test
    public void givenCsEventsTypeWhenSetCsEventsTypeThenVerifyResult() {
        // Act
        parameter.setCsEventsType("csEventsType");
        // Assert
        errorCollector.checkThat(parameter.getCsEventsType(), is("csEventsType"));
    }

    @Test
    public void givenNeedLogoWhenSetNeedLogoThenVerifyResult() {
        // Act
        parameter.setNeedLogo(true);
        // Assert
        errorCollector.checkThat(parameter.getNeedLogo(), is(true));
    }

    @Test
    public void givenChartTypeWhenSetChartTypeThenVerifyResult() {
        // Act
        parameter.setChartType("chartType");
        // Assert
        errorCollector.checkThat(parameter.getChartType(), is("chartType"));
    }

    @Test
    public void givenNeedImageWhenSetNeedImageThenVerifyResult() {
        // Act
        parameter.setNeedImage(true);
        // Assert
        errorCollector.checkThat(parameter.getNeedImage(), is(true));
    }

    @Test
    public void givenIpadWhenSetIpadThenVerifyResult() {
        // Act
        parameter.setIpad(true);
        // Assert
        errorCollector.checkThat(parameter.getIpad(), is(true));
    }

    @Test
    public void givenNeedMatchedEntitiesWhenSetNeedMatchedEntitiesThenVerifyResult() {
        // Act
        parameter.setNeedMatchedEntities(true);
        // Assert
        errorCollector.checkThat(parameter.getNeedMatchedEntities(), is(true));
    }

    @Test
    public void givenLastDayWhenSetLastDayThenVerifyResult() {
        // Act
        parameter.setLastDay("lastDay");
        // Assert
        errorCollector.checkThat(parameter.getLastDay(), is("lastDay"));
    }

    @Test
    public void givenPerEntityItemWhenSetPerEntityItemThenVerifyResult() {
        // Act
        parameter.setPerEntityItem(value);
        // Assert
        errorCollector.checkThat(parameter.getPerEntityItem(), is(value));
    }

    @Test
    public void givenNeedCriticalEventTaggingWhenSetNeedCriticalEventTaggingThenVerifyResult() {
        // Act
        parameter.setNeedCriticalEventTagging(true);
        // Assert
        errorCollector.checkThat(parameter.getNeedCriticalEventTagging(), is(true));
    }

    @Test
    public void givenCsEventGroupWhenSetCsEventGroupThenVerifyResult() {
      // Act
        parameter.setCsEventGroup("csEventGroup");
        // Assert
        errorCollector.checkThat(parameter.getCsEventGroup(), is("csEventGroup"));
    }
}
