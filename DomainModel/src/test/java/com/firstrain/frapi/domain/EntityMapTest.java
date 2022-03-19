package com.firstrain.frapi.domain;

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
public class EntityMapTest {

    private Entity country;

    @InjectMocks
    private EntityMap entityMap;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Before
    public void setUp() {
        country = new Entity();
    }

    @Test
    public void givenIndustryWhenSetIndustryThenVerifyResult() {
       // Act
        entityMap.setIndustry(country);
        // Assert
        errorCollector.checkThat(entityMap.getIndustry(),is(country));
    }

    @Test
    public void givenSectorWhenSetSectorThenVerifyResult() {
       // Act
        entityMap.setSector(country);
        // Assert
        errorCollector.checkThat(entityMap.getSector(),is(country));
    }

    @Test
    public void givenSegmentWhenSetSegmentThenVerifyResult() {
        // Act
        entityMap.setSegment(country);
        // Assert
        errorCollector.checkThat(entityMap.getSegment(),is(country));
    }

    @Test
    public void givenBizlinesWhenSetBizlinesThenVerifyResult() {
         // Act
        entityMap.setBizlines(Collections.singletonList(country));
        // Assert
        errorCollector.checkThat(entityMap.getBizlines(),is(Collections.singletonList(country)));
    }

    @Test
    public void givenEntityWhenSetEntityThenVerifyResult() {
        // Act
        entityMap.setEntity(country);
        // Assert
        errorCollector.checkThat(entityMap.getEntity(),is(country));
    }

    @Test
    public void givenLanguageWhenSetLanguageThenVerifyResult() {
        // Act
        entityMap.setLanguage(Collections.singletonList("language"));
        // Assert
        errorCollector.checkThat(entityMap.getLanguage(),is(Collections.singletonList("language")));
    }

    @Test
    public void givenCountryWhenSetCountryThenVerifyResult() {
        // Act
        entityMap.setCountry(country);
        // Assert
        errorCollector.checkThat(entityMap.getCountry(),is(country));
    }

    @Test
    public void givenDomainWhenSetDomainThenVerifyResult() {
        // Act
        entityMap.setDomain("domain");
        // Assert
        errorCollector.checkThat(entityMap.getDomain(),is("domain"));
    }

    @Test
    public void givenCompanyLogoWhenSetCompanyLogoThenVerifyResult() {
         // Act
        entityMap.setCompanyLogo("companyLogo");
        // Assert
        errorCollector.checkThat(entityMap.getCompanyLogo(),is("companyLogo"));
    }
}
