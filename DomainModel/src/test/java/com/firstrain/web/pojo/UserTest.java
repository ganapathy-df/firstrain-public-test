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
public class UserTest {

    @InjectMocks
    private User user;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Test
    public void givenUserIdWhenSetUserIdThenVerifyResult() {
       // Act
        user.setUserId("userId");
        // Assert
        errorCollector.checkThat(user.getUserId(),is("userId"));
    }

    @Test
    public void givenUStatusWhenSetuStatusThenVerifyResult() {
       // Act
        user.setuStatus("uStatus");
        // Assert
        errorCollector.checkThat(user.getuStatus(),is("uStatus"));
    }

    @Test
    public void givenUEmailWhenSetuEmailThenVerifyResult() {
        // Act
        user.setuEmail("uEmail");
        // Assert
        errorCollector.checkThat(user.getuEmail(),is("uEmail"));
    }

    @Test
    public void givenUFirstNameWhenSetuFirstNameThenVerifyResult() {
        // Act
        user.setuFirstName("uFirstName");
        // Assert
        errorCollector.checkThat(user.getuFirstName(),is("uFirstName"));
    }

    @Test
    public void givenULastNameWhenSetuLastNameThenVerifyResult() {
        // Act
        user.setuLastName("uLastName");
        // Assert
        errorCollector.checkThat(user.getuLastName(),is("uLastName"));
    }

    @Test
    public void givenUTimezoneWhenSetuTimezoneThenVerifyResult() {
       // Act
        user.setuTimezone("uTimezone");
        // Assert
        errorCollector.checkThat(user.getuTimezone(),is("uTimezone"));
    }

    @Test
    public void givenUCompanyWhenSetuCompanyThenVerifyResult() {
      // Act
        user.setuCompany("uCompany");
        // Assert
        errorCollector.checkThat(user.getuCompany(),is("uCompany"));
    }
}
