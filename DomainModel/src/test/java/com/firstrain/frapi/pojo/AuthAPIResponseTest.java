package com.firstrain.frapi.pojo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.sql.Timestamp;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;

@RunWith(MockitoJUnitRunner.class)
public class AuthAPIResponseTest {

    private Timestamp expiryTime;
    @InjectMocks
    private AuthAPIResponse authAPIResponse;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Before
    public void setUp() {
        expiryTime = new Timestamp(12345L);
    }

    @Test
    public void givenIdWhenSetIdThenVerifyResult() {
        // Act
        authAPIResponse.setId(4L);
        // Assert
        errorCollector.checkThat(authAPIResponse.getId(), is(4L));
    }

    @Test
    public void givenStatusCodeWhenSetStatusCodeThenVerifyResult() {
        // Act
        authAPIResponse.setStatusCode(6);
        // Assert
        errorCollector.checkThat(authAPIResponse.getStatusCode(), is(6));
    }

    @Test
    public void givenAuthKeyWhenSetAuthKeyThenVerifyResult() {
        // Act
        authAPIResponse.setAuthKey("authKey");
        // Assert
        errorCollector.checkThat(authAPIResponse.getAuthKey(), is("authKey"));
    }

    @Test
    public void givenExpiryTimeWhenSetExpiryTimeThenVerifyResult() {
        // Act
        authAPIResponse.setExpiryTime(expiryTime);
        // Assert
        errorCollector.checkThat(authAPIResponse.getExpiryTime(), is(expiryTime));
    }

    @Test
    public void givenEnterpriseIdWhenSetEnterpriseIdThenVerifyResult() {
        // Act
        authAPIResponse.setEnterpriseId(5L);
        // Assert
        errorCollector.checkThat(authAPIResponse.getEnterpriseId(), is(5L));
    }

    @Test
    public void givenApiVersionWhenSetApiVersionThenVerifyResult() {
        // Act
        authAPIResponse.setApiVersion("apiVersion");
        // Assert
        errorCollector.checkThat(authAPIResponse.getApiVersion(), is("apiVersion"));
    }

    @Test
    public void givenExcludedSectionListWhenSetExcludedSectionListThenVerifyResult() {
        // Act
        authAPIResponse.setExcludedSectionList(Collections.singletonList("section"));
        // Assert
        errorCollector.checkThat(authAPIResponse.getExcludedSectionList(), is(Collections.singletonList("section")));
    }

    @Test
    public void givenExcludedAPIListWhenSetExcludedAPIListThenVerifyResult() {
        // Act
        authAPIResponse.setExcludedAPIList(Collections.singletonList("api"));
        // Assert
        errorCollector.checkThat(authAPIResponse.getExcludedAPIList(), is(Collections.singletonList("api")));
    }

    @Test
    public void givenIncludedAPIListWhenSetIncludedAPIListThenVerifyResult() {
        // Act
        authAPIResponse.setIncludedAPIList(Collections.singletonList("apiNew"));
        // Assert
        errorCollector.checkThat(authAPIResponse.getIncludedAPIList(), is(Collections.singletonList("apiNew")));
    }

    @Test
    public void givenVersionStartDateWhenSetVersionStartDateThenVerifyResult() {
        // Act
        authAPIResponse.setVersionStartDate(expiryTime);
        // Assert
        errorCollector.checkThat(authAPIResponse.getVersionStartDate(), is(expiryTime));
    }

    @Test
    public void givenVersionEndDateWhenSetVersionEndDateThenVerifyResult() {
        // Act
        authAPIResponse.setVersionEndDate(expiryTime);
        // Assert
        errorCollector.checkThat(authAPIResponse.getVersionEndDate(), is(expiryTime));
    }

    @Test
    public void givenApiThresholdWhenSetApiThresholdThenVerifyResult() {
        // Act
        authAPIResponse.setApiThreshold(6);
        // Assert
        errorCollector.checkThat(authAPIResponse.getApiThreshold(), is(6));
    }

    @Test
    public void givenPrefJsonWhenSetPrefJsonThenVerifyResult() {
        // Act
        authAPIResponse.setPrefJson("prefJson");
        // Assert
        errorCollector.checkThat(authAPIResponse.getPrefJson(), is("prefJson"));
    }
}
