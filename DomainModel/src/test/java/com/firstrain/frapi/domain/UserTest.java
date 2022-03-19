package com.firstrain.frapi.domain;

import com.firstrain.frapi.pojo.wrapper.EntityListJsonObject;
import com.firstrain.frapi.util.DefaultEnums.MembershipType;
import com.firstrain.frapi.util.DefaultEnums.Origin;
import com.firstrain.frapi.util.DefaultEnums.UserType;
import com.firstrain.frapi.util.DefaultEnums.UserValidationStatus;
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
public class UserTest {

    @InjectMocks
    private User user;

    private EntityListJsonObject entityListJsonObject;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Before
    public void setUp() {
        entityListJsonObject = new EntityListJsonObject();
    }

    @Test
    public void givenUserTypeWhenSetUserTypeThenVerifyResult() {
       // Act
        user.setUserType(UserType.ABU);
        // Assert
        errorCollector.checkThat(user.getUserType(),is(UserType.ABU));
    }

    @Test
    public void givenContextPageWhenSetContextPageThenVerifyResult() {
        // Act
        user.setContextPage(true);
        // Assert
        errorCollector.checkThat(user.isContextPage(),is(true));
    }

    @Test
    public void givenUserCompanyWhenSetUserCompanyThenVerifyResult() {
        // Act
        user.setUserCompany("userCompany");
        // Assert
        errorCollector.checkThat(user.getUserCompany(),is("userCompany"));
    }

    @Test
    public void givenDnbUserIdWhenSetDnbUserIdThenVerifyResult() {
      // Act
        user.setDnbUserId("dnbUserId");
        // Assert
        errorCollector.checkThat(user.getDnbUserId(),is("dnbUserId"));
    }

    @Test
    public void givenFrMonitorIdWhenSetFrMonitorIdThenVerifyResult() {
        // Act
        user.setFrMonitorId(4L);
        // Assert
        errorCollector.checkThat(user.getFrMonitorId(),is(4L));
    }

    @Test
    public void givenUserTriggersWhenSetUserTriggersThenVerifyResult() {
        // Act
        user.setUserTriggers(entityListJsonObject);
        // Assert
        errorCollector.checkThat(user.getUserTriggers(),is(entityListJsonObject));
    }

    @Test
    public void givenCodeWhenSetCodeThenVerifyResult() {
       // Act
        user.setCode("code");
        // Assert
        errorCollector.checkThat(user.getCode(),is("code"));
    }

    @Test
    public void givenUserIdWhenSetUserIdThenVerifyResult() {
        // Act
        user.setUserId("userId");
        // Assert
        errorCollector.checkThat(user.getUserId(),is("userId"));
    }

    @Test
    public void givenFlagsWhenSetFlagsThenVerifyResult() {
        // Act
        user.setFlags("flags");
        // Assert
        errorCollector.checkThat(user.getFlags(),is("flags"));
    }

    @Test
    public void givenDomainWhenSetDomainThenVerifyResult() {
         // Act
        user.setDomain("domain");
        // Assert
        errorCollector.checkThat(user.getDomain(),is("domain"));
    }

    @Test
    public void givenTemplateIdWhenSetTemplateIdThenVerifyResult() {
        // Act
        user.setTemplateId(6L);
        // Assert
        errorCollector.checkThat(user.getTemplateId(),is(6L));
    }

    @Test
    public void givenUserNameWhenSetUserNameThenVerifyResult() {
       // Act
        user.setUserName("userName");
        // Assert
        errorCollector.checkThat(user.getUserName(),is("userName"));
    }

    @Test
    public void givenOwnedByWhenSetOwnedByThenVerifyResult() {
        // Act
        user.setOwnedBy(6L);
        // Assert
        errorCollector.checkThat(user.getOwnedBy(),is(6L));
    }

    @Test
    public void givenEmailWhenSetEmailThenVerifyResult() {
         // Act
        user.setEmail("email");
        // Assert
        errorCollector.checkThat(user.getEmail(),is("email"));
    }

    @Test
    public void givenFormatWhenSetFormatThenVerifyResult() {
        // Act
        user.setFormat(6);
        // Assert
        errorCollector.checkThat(user.getFormat(),is(6));
    }

    @Test
    public void givenOwnedByTypeWhenSetOwnedByTypeThenVerifyResult() {
          // Act
        user.setOwnedByType("ownedByType");
        // Assert
        errorCollector.checkThat(user.getOwnedByType(),is("ownedByType"));
    }

    @Test
    public void givenTimeZoneWhenSetTimeZoneThenVerifyResult() {
         // Act
        user.setTimeZone("timeZone");
        // Assert
        errorCollector.checkThat(user.getTimeZone(),is("timeZone"));
    }

    @Test
    public void givenFirstNameWhenSetFirstNameThenVerifyResult() {
         // Act
        user.setFirstName("firstName");
        // Assert
        errorCollector.checkThat(user.getFirstName(),is("firstName"));
    }

    @Test
    public void givenLastNameWhenSetLastNameThenVerifyResult() {
          // Act
        user.setLastName("lastName");
        // Assert
        errorCollector.checkThat(user.getLastName(),is("lastName"));
    }

    @Test
    public void givenMonitorOrderTypeWhenSetMonitorOrderTypeThenVerifyResult() {
        // Act
        user.setMonitorOrderType("monitorOrderType");
        // Assert
        errorCollector.checkThat(user.getMonitorOrderType(),is("monitorOrderType"));
    }

    @Test
    public void givenMembershipTypeWhenSetMembershipTypeThenVerifyResult() {
       // Act
        user.setMembershipType(MembershipType.ADMIN);
        // Assert
        errorCollector.checkThat(user.getMembershipType(),is(MembershipType.ADMIN));
    }

    @Test
    public void givenStatusWhenSetStatusThenVerifyResult() {
      // Act
        user.setStatus(UserValidationStatus.VALIDATION_SUCCESS);
        // Assert
        errorCollector.checkThat(user.getStatus(),is(UserValidationStatus.VALIDATION_SUCCESS));
    }

    @Test
    public void givenOriginWhenSetOriginThenVerifyResult() {
        // Act
        user.setOrigin(Origin.INTERNAL_ADMIN);
        // Assert
        errorCollector.checkThat(user.getOrigin(),is(Origin.INTERNAL_ADMIN));
    }
}
