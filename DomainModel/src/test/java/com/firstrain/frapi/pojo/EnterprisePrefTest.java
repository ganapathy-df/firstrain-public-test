package com.firstrain.frapi.pojo;

import com.firstrain.frapi.domain.SectionSpec;
import com.firstrain.frapi.pojo.wrapper.BaseSet.SectionType;
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
public class EnterprisePrefTest {
    @InjectMocks
    private EnterprisePref enterprisePref;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Test
    public void givenEnterpriseIdWhenSetEnterpriseIdThenVerifyResult() {
        // Act
        enterprisePref.setEnterpriseId(4L);
        // Assert
        errorCollector.checkThat(enterprisePref.getEnterpriseId(), is(4L));
    }

    @Test
    public void givenIndustryClassificationIdWhenSetIndustryClassificationIdThenVerifyResult() {
        // Arrange
        short value = 4;
        // Act
        enterprisePref.setIndustryClassificationId(value);
        // Assert
        errorCollector.checkThat(enterprisePref.getIndustryClassificationId(), is(value));
    }

    @Test
    public void givenPrivateSourceIdsSSVWhenSetPrivateSourceIdsSSVThenVerifyResult() {
        // Act
        enterprisePref.setPrivateSourceIdsSSV("privateSourceIdsSSV");
        // Assert
        errorCollector.checkThat(enterprisePref.getPrivateSourceIdsSSV(), is("privateSourceIdsSSV"));
    }

    @Test
    public void givenPublicSourceIdsSSVWhenSetPublicSourceIdsSSVThenVerifyResult() {
        // Act
        enterprisePref.setPublicSourceIdsSSV("publicSourceIdsSSV");
        // Assert
        errorCollector.checkThat(enterprisePref.getPublicSourceIdsSSV(), is("publicSourceIdsSSV"));
    }

    @Test
    public void givenSectionsMapWhenSetSectionsMapThenVerifyResult() {
        // Arrange
        SectionSpec sectionSpec = new SectionSpec();
        // Act
        enterprisePref.setSectionsMap
                (Collections.singletonMap(SectionType.FR, sectionSpec));
        // Assert
        errorCollector.checkThat(enterprisePref.getSectionsMap(), is(Collections.singletonMap
                (SectionType.FR, sectionSpec)));
    }

    @Test
    public void givenSearchesPerMonitorWhenSetSearchesPerMonitorThenVerifyResult() {
        // Act
        enterprisePref.setSearchesPerMonitor(4);
        // Assert
        errorCollector.checkThat(enterprisePref.getSearchesPerMonitor(), is(4));
    }

    @Test
    public void givenCustomizedCssFileNameWhenSetCustomizedCssFileNameThenVerifyResult() {
        // Act
        enterprisePref.setCustomizedCssFileName("customizedCssFileName");
        // Assert
        errorCollector.checkThat(enterprisePref.getCustomizedCssFileName(), is("customizedCssFileName"));
    }

    @Test
    public void givenIsDnBIdWhenSetDnBIdThenVerifyResult() {
        // Act
        enterprisePref.setDnBId(true);
        // Assert
        errorCollector.checkThat(enterprisePref.isDnBId(), is(true));
    }

    @Test
    public void givenApplyMinNodeCheckInVisualizationWhenSetApplyMinNodeCheckInVisualizationThenVerifyResult() {
        // Act
        enterprisePref.setApplyMinNodeCheckInVisualization(true);
        // Assert
        errorCollector.checkThat(enterprisePref.isApplyMinNodeCheckInVisualization(), is(true));
    }
}
