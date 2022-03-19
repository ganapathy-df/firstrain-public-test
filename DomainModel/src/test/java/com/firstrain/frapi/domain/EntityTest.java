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
public class EntityTest {

    @InjectMocks
    private Entity entity;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Test
    public void givenSearchTokenWhenSetSearchTokenThenVerifyResult() {
       // Act
        entity.setSearchToken("searchToken");
        // Assert
        errorCollector.checkThat(entity.getSearchToken(),is("searchToken"));
    }

    @Test
    public void givenIdWhenSetIdThenVerifyResult() {
         // Act
        entity.setId("id");
        // Assert
        errorCollector.checkThat(entity.getId(),is("id"));
    }

    @Test
    public void givenNameWhenSetNameThenVerifyResult() {
       // Act
        entity.setName("name");
        // Assert
        errorCollector.checkThat(entity.getName(),is("name"));
    }

    @Test
    public void givenTickerWhenSetTickerThenVerifyResult() {
       // Act
        entity.setTicker("ticker");
        // Assert
        errorCollector.checkThat(entity.getTicker(),is("ticker"));
    }

    @Test
    public void givenDomainWhenSetDomainThenVerifyResult() {
        // Act
        entity.setDomain("domain");
        // Assert
        errorCollector.checkThat(entity.getDomain(),is("domain"));
    }

    @Test
    public void givenWebsiteWhenSetWebsiteThenVerifyResult() {
        // Act
        entity.setWebsite("website");
        // Assert
        errorCollector.checkThat(entity.getWebsite(),is("website"));
    }

    @Test
    public void givenAddressWhenSetAddressThenVerifyResult() {
      // Act
        entity.setAddress("address");
        // Assert
        errorCollector.checkThat(entity.getAddress(),is("address"));
    }

    @Test
    public void givenCityWhenSetCityThenVerifyResult() {
        // Act
        entity.setCity("city");
        // Assert
        errorCollector.checkThat(entity.getCity(),is("city"));
    }

    @Test
    public void givenStateWhenSetStateThenVerifyResult() {
        // Act
        entity.setState("state");
        // Assert
        errorCollector.checkThat(entity.getState(),is("state"));
    }

    @Test
    public void givenZipWhenSetZipThenVerifyResult() {
       // Act
        entity.setZip("zip");
        // Assert
        errorCollector.checkThat(entity.getZip(),is("zip"));
    }

    @Test
    public void givenCountryWhenSetCountryThenVerifyResult() {
        // Act
        entity.setCountry("country");
        // Assert
        errorCollector.checkThat(entity.getCountry(),is("country"));
    }
}
