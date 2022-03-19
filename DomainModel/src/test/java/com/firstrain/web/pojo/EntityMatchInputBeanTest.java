package com.firstrain.web.pojo;

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
public class EntityMatchInputBeanTest {

    private EntityMatchInputBean.EntityInput company;


    @InjectMocks
    private EntityMatchInputBean entityMatchInputBean;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);


    @Before
    public void setUp() {
        company = new EntityMatchInputBean.EntityInput();
    }

    @Test
    public void givenCompanyWhenSetCompanyThenVerifyResult() {
        // Act
        entityMatchInputBean.setCompany(company);
        //Assert
        errorCollector.checkThat(entityMatchInputBean.getCompany(),is(company));
    }

    @Test
    public void givenTopicWhenSetTopicThenVerifyResult() {
       // Act
        entityMatchInputBean.setTopic(company);
        //Assert
        errorCollector.checkThat(entityMatchInputBean.getTopic(),is(company));
    }

    @Test
    public void givenIndustryWhenSetIndustryThenVerifyResult() {
        // Act
        entityMatchInputBean.setIndustry(company);
        //Assert
        errorCollector.checkThat(entityMatchInputBean.getIndustry(),is(company));
    }

    @Test
    public void givenRegionWhenSetRegionThenVerifyResult() {
       // Act
        entityMatchInputBean.setRegion(company);
        //Assert
        errorCollector.checkThat(entityMatchInputBean.getRegion(),is(company));
    }

    @Test
    public void givenCountWhenSetCountThenVerifyResult() {
        // Act
        entityMatchInputBean.setCount(2);
        //Assert
        errorCollector.checkThat(entityMatchInputBean.getCount(),is(2));
    }

    @Test
    public void givenCountWhenSetNameThenVerifyResult() {
        // Act
        company.setName("name");
        //Assert
        errorCollector.checkThat(company.getName(),is("name"));
    }

    @Test
    public void givenCountWhenSetDunsThenVerifyResult() {
        // Act
        company.setDuns("duns");
        //Assert
        errorCollector.checkThat(company.getDuns(),is("duns"));
    }

    @Test
    public void givenCountWhenSetTickerThenVerifyResult() {
        // Act
        company.setTicker("ticker");
        //Assert
        errorCollector.checkThat(company.getTicker(),is("ticker"));
    }

    @Test
    public void givenCountWhenSetCikCodeThenVerifyResult() {
        // Act
        company.setCikCode("cikCode");
        //Assert
        errorCollector.checkThat(company.getCikCode(),is("cikCode"));
    }

    @Test
    public void givenCountWhenSetSedolThenVerifyResult() {
        // Act
        company.setSedol("sedol");
        //Assert
        errorCollector.checkThat(company.getSedol(),is("sedol"));
    }

    @Test
    public void givenCountWhenSetIsinThenVerifyResult() {
        // Act
        company.setIsin("isin");
        //Assert
        errorCollector.checkThat(company.getIsin(),is("isin"));
    }

    @Test
    public void givenCountWhenSetValeronThenVerifyResult() {
        // Act
        company.setValeron("valeron");
        //Assert
        errorCollector.checkThat(company.getValeron(),is("valeron"));
    }

    @Test
    public void givenCountWhenSetHomePageThenVerifyResult() {
        // Act
        company.setHomePage("homePage");
        //Assert
        errorCollector.checkThat(company.getHomePage(),is("homePage"));
    }

    @Test
    public void givenCountWhenSetAddressThenVerifyResult() {
        // Act
        company.setAddress("address");
        //Assert
        errorCollector.checkThat(company.getAddress(),is("address"));
    }

    @Test
    public void givenCountWhenSetCountryThenVerifyResult() {
        // Act
        company.setCountry("country");
        //Assert
        errorCollector.checkThat(company.getCountry(),is("country"));
    }

    @Test
    public void givenCountWhenSetStateThenVerifyResult() {
        // Act
        company.setState("state");
        //Assert
        errorCollector.checkThat(company.getState(),is("state"));
    }

    @Test
    public void givenCountWhenSetCityThenVerifyResult() {
        // Act
        company.setCity("city");
        //Assert
        errorCollector.checkThat(company.getCity(),is("city"));
    }

    @Test
    public void givenCountWhenSetZipThenVerifyResult() {
        // Act
        company.setZip("zip");
        //Assert
        errorCollector.checkThat(company.getZip(),is("zip"));
    }

    @Test
    public void givenCountWhenSetHemscottICThenVerifyResult() {
        // Act
        company.setHemscottIC("hemscottIC");
        //Assert
        errorCollector.checkThat(company.getHemscottIC(),is("hemscottIC"));
    }

    @Test
    public void givenCountWhenSetHooversICThenVerifyResult() {
        // Act
        company.setHooversIC("hooversIC");
        //Assert
        errorCollector.checkThat(company.getHooversIC(),is("hooversIC"));
    }

    @Test
    public void givenCountWhenSetSicThenVerifyResult() {
        // Act
        company.setSic("sic");
        //Assert
        errorCollector.checkThat(company.getSic(),is("sic"));
    }

    @Test
    public void givenCountWhenSetNaicsThenVerifyResult() {
        // Act
        company.setNaics("naics");
        //Assert
        errorCollector.checkThat(company.getNaics(),is("naics"));
    }

}
