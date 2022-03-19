package com.firstrain.web.pojo;

import com.firstrain.frapi.util.DefaultEnums.EventInformationEnum;
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
public class EventTest {

    private Source source;
    private EntityStandard oldCompany;

    @InjectMocks
    private Event event;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Before
    public void setUp() {
        oldCompany = new EntityStandard();
        source = new Source();
    }

    @Test
    public void givenEventDateWhenSetEventDateThenVerifyResult() {
      // Act
        event.setEventDate("eventDate");
        // Assert
        errorCollector.checkThat(event.getEventDate(),is("eventDate"));
    }

    @Test
    public void givenPersonFullNameWhenSetPersonFullNameThenVerifyResult() {
        // Act
        event.setPersonFullName("personFullName");
        // Assert
        errorCollector.checkThat(event.getPersonFullName(),is("personFullName"));
    }

    @Test
    public void givenOldCompanyWhenSetOldCompanyThenVerifyResult() {
     // Act
        event.setOldCompany(oldCompany);
        // Assert
        errorCollector.checkThat(event.getOldCompany(),is(oldCompany));
    }

    @Test
    public void givenNewCompanyWhenSetNewCompanyThenVerifyResult() {
       // Act
        event.setNewCompany(oldCompany);
        // Assert
        errorCollector.checkThat(event.getNewCompany(),is(oldCompany));
    }

    @Test
    public void givenOldTitleWhenSetOldTitleThenVerifyResult() {
       // Act
        event.setOldTitle("oldTitle");
        // Assert
        errorCollector.checkThat(event.getOldTitle(),is("oldTitle"));
    }

    @Test
    public void givenOldGroupWhenSetOldGroupThenVerifyResult() {
       // Act
        event.setOldGroup("oldGroup");
        // Assert
        errorCollector.checkThat(event.getOldGroup(),is("oldGroup"));
    }

    @Test
    public void givenOldRegionWhenSetOldRegionThenVerifyResult() {
       // Act
        event.setOldRegion("oldRegion");
        // Assert
        errorCollector.checkThat(event.getOldRegion(),is("oldRegion"));
    }

    @Test
    public void givenNewTitleWhenSetNewTitleThenVerifyResult() {
      // Act
        event.setNewTitle("newTitle");
        // Assert
        errorCollector.checkThat(event.getNewTitle(),is("newTitle"));
    }

    @Test
    public void givenNewGroupWhenSetNewGroupThenVerifyResult() {
      // Act
        event.setNewGroup("newGroup");
        // Assert
        errorCollector.checkThat(event.getNewGroup(),is("newGroup"));
    }

    @Test
    public void givenNewRegionWhenSetNewRegionThenVerifyResult() {
      // Act
        event.setNewRegion("newRegion");
        // Assert
        errorCollector.checkThat(event.getNewRegion(),is("newRegion"));
    }

    @Test
    public void givenIdWhenSetIdThenVerifyResult() {
      // Act
        event.setId("id");
        // Assert
        errorCollector.checkThat(event.getId(),is("id"));
    }

    @Test
    public void givenTitleWhenSetTitleThenVerifyResult() {
     // Act
        event.setTitle("title");
        // Assert
        errorCollector.checkThat(event.getTitle(),is("title"));
    }

    @Test
    public void givenLinkWhenSetLinkThenVerifyResult() {
     // Act
        event.setLink("link");
        // Assert
        errorCollector.checkThat(event.getLink(),is("link"));
    }

    @Test
    public void givenEventTypeWhenSetEventTypeThenVerifyResult() {
       // Act
        event.setEventType(EventInformationEnum.SEC);
        // Assert
        errorCollector.checkThat(event.getEventType(),is(EventInformationEnum.SEC));
    }

    @Test
    public void givenTimeStampWhenSetTimeStampThenVerifyResult() {
        // Act
        event.setTimeStamp("timeStamp");
        // Assert
        errorCollector.checkThat(event.getTimeStamp(),is("timeStamp"));
    }

    @Test
    public void givenEntityWhenSetEntityThenVerifyResult() {
       // Act
        event.setEntity(Collections.singletonList(oldCompany));
        // Assert
        errorCollector.checkThat(event.getEntity(),is(Collections.singletonList(oldCompany)));
    }

    @Test
    public void givenContentTypeWhenSetContentTypeThenVerifyResult() {
      // Act
        event.setContentType("contentType");
        // Assert
        errorCollector.checkThat(event.getContentType(),is("contentType"));
    }

    @Test
    public void givenSourceWhenSetSourceThenVerifyResult() {
      // Act
        event.setSource(source);
        // Assert
        errorCollector.checkThat(event.getSource(),is(source));
    }

    @Test
    public void givenSnippetWhenSetSnippetThenVerifyResult() {
       // Act
        event.setSnippet("snippet");
        // Assert
        errorCollector.checkThat(event.getSnippet(),is("snippet"));
    }
}
