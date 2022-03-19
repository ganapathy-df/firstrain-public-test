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
public class ChartDataTest {

    @InjectMocks
    private ChartData chartData;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Test
    public void givenNameWhenSetNameThenVerifyResult() {
       // Act
        chartData.setName("name");
        // Assert
        errorCollector.checkThat(chartData.getName(),is("name"));
    }

    @Test
    public void givenQueryWhenSetQueryThenVerifyResult() {
       // Act
        chartData.setQuery("query");
        // Assert
        errorCollector.checkThat(chartData.getQuery(),is("query"));
    }

    @Test
    public void givenValueWhenSetValueThenVerifyResult() {
        // Act
        chartData.setValue("value");
        // Assert
        errorCollector.checkThat(chartData.getValue(),is("value"));
    }

    @Test
    public void givenIntensityWhenSetIntensityThenVerifyResult() {
       // Act
        chartData.setIntensity("intensity");
        // Assert
        errorCollector.checkThat(chartData.getIntensity(),is("intensity"));
    }

    @Test
    public void givenSmartTextWhenSetSmartTextThenVerifyResult() {
        // Act
        chartData.setSmartText("smartText");
        // Assert
        errorCollector.checkThat(chartData.getSmartText(),is("smartText"));
    }

    @Test
    public void givenSearchTokenWhenSetSearchTokenThenVerifyResult() {
       // Act
        chartData.setSearchToken("searchToken");
        // Assert
        errorCollector.checkThat(chartData.getSearchToken(),is("searchToken"));
    }

    @Test
    public void givenCountryCodeWhenSetCountryCodeThenVerifyResult() {
      // Act
        chartData.setCountryCode("countryCode");
        // Assert
        errorCollector.checkThat(chartData.getCountryCode(),is("countryCode"));
    }

    @Test
    public void givenStateCodeWhenSetStateCodeThenVerifyResult() {
        // Act
        chartData.setStateCode("stateCode");
        // Assert
        errorCollector.checkThat(chartData.getStateCode(),is("stateCode"));
    }
}
