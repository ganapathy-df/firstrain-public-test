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
public class CompanyTradingRangeTest {

    @InjectMocks
    private CompanyTradingRange companyTradingRange;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Test
    public void givenDiffIdWhenSetDiffIdThenVerifyResult() {
        // Act
        companyTradingRange.setDiffId(3);
        // Assert
        errorCollector.checkThat(companyTradingRange.getDiffId(),is(3));
    }

    @Test
    public void givenOpeningPriceWhenSetOpeningPriceThenVerifyResult() {
        // Act
        companyTradingRange.setOpeningPrice(2d);
        // Assert
        errorCollector.checkThat(companyTradingRange.getOpeningPrice(),is(2d));
    }

    @Test
    public void givenClosingPriceWhenSetClosingPriceThenVerifyResult() {
        // Act
        companyTradingRange.setClosingPrice(3d);
        // Assert
        errorCollector.checkThat(companyTradingRange.getClosingPrice(),is(3d));
    }

    @Test
    public void givenClosingPriceWhenGetOpeningPriceStrThenVerifyResult() {
        // Act
        String actual =companyTradingRange.getOpeningPriceStr();
        // Assert
        errorCollector.checkThat(actual,is("0.0"));
    }

    @Test
    public void givenClosingPriceWhenGetClosingPriceStrThenVerifyResult() {
        // Act
        String actual =companyTradingRange.getClosingPriceStr();
        // Assert
        errorCollector.checkThat(actual,is("0.0"));
    }

}
