package com.firstrain.frapi.pojo;

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
public class EmailResponseTest {

    private EmailDetail emailDetail;

    @InjectMocks
    private EmailResponse emailResponse;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Before
    public void setUp() {
        emailDetail = new EmailDetail();
    }

    @Test
    public void givenStatusCodeWhenSetStatusCodeThenVerifyResult() {
       // Act
        emailResponse.setStatusCode(5);
        // Assert
        errorCollector.checkThat(emailResponse.getStatusCode(),is(5));
    }

    @Test
    public void givenEmailTemplateWhenSetEmailTemplateThenVerifyResult() {
        // Act
        emailResponse.setEmailTemplate("emailTemplate");
        // Assert
        errorCollector.checkThat(emailResponse.getEmailTemplate(),is("emailTemplate"));
    }

    @Test
    public void givenEmailNameWhenSetEmailNameThenVerifyResult() {
       // Act
        emailResponse.setEmailName("emailName");
        // Assert
        errorCollector.checkThat(emailResponse.getEmailName(),is("emailName"));
    }

    @Test
    public void givenEmailIdWhenSetEmailIdThenVerifyResult() {
      // Act
        emailResponse.setEmailId(6L);
        // Assert
        errorCollector.checkThat(emailResponse.getEmailId(),is(6L));
    }

    @Test
    public void givenEmailDetailWhenSetEmailDetailThenVerifyResult() {
       // Act
        emailResponse.setEmailDetail(emailDetail);
        // Assert
        errorCollector.checkThat(emailResponse.getEmailDetail(),is(emailDetail));
    }
}
