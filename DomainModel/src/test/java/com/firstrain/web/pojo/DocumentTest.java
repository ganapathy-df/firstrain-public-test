package com.firstrain.web.pojo;

import com.firstrain.frapi.domain.DocNgrams;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.Date;

import static org.hamcrest.CoreMatchers.is;

@RunWith(MockitoJUnitRunner.class)
public class DocumentTest {

    private Source source;
    private EntityStandard entityStandard;
    private Date date;
    private DocNgrams docNgrams;

    @InjectMocks
    private Document document;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Before
    public void setUp() {
        source = new Source();
        entityStandard = new EntityStandard();
        date = new Date(1234L);
        docNgrams = new DocNgrams();
    }

    @Test
    public void givenSnippetWhenSetSnippetThenVerifyResult() {
        // Act
        document.setSnippet("snippet");
        // Assert
        errorCollector.checkThat(document.getSnippet(),is("snippet"));
    }

    @Test
    public void givenIdWhenSetIdThenVerifyResult() {
       // Act
        document.setId("id");
        // Assert
        errorCollector.checkThat(document.getId(),is("id"));
    }

    @Test
    public void givenContentTypeWhenSetContentTypeThenVerifyResult() {
       // Act
        document.setContentType("contentType");
        // Assert
        errorCollector.checkThat(document.getContentType(),is("contentType"));
    }

    @Test
    public void givenSourceWhenSetSourceThenVerifyResult() {
         // Act
        document.setSource(source);
        // Assert
        errorCollector.checkThat(document.getSource(),is(source));
    }

    @Test
    public void givenEntityWhenSetEntityThenVerifyResult() {
         // Act
        document.setEntity(Collections.singletonList(entityStandard));
        // Assert
        errorCollector.checkThat(document.getEntity(),is(Collections.singletonList(entityStandard)));
    }

    @Test
    public void givenTitleWhenSetTitleThenVerifyResult() {
        // Act
        document.setTitle("title");
        // Assert
        errorCollector.checkThat(document.getTitle(),is("title"));
    }

    @Test
    public void givenSourceUrlWhenSetSourceUrlThenVerifyResult() {
       // Act
        document.setSourceUrl("sourceUrl");
        // Assert
        errorCollector.checkThat(document.getSourceUrl(),is("sourceUrl"));
    }

    @Test
    public void givenLinkWhenSetLinkThenVerifyResult() {
        // Act
        document.setLink("link");
        // Assert
        errorCollector.checkThat(document.getLink(),is("link"));
    }

    @Test
    public void givenBookmarkedWhenSetBookmarkedThenVerifyResult() {
        // Act
        document.setBookmarked(true);
        // Assert
        errorCollector.checkThat(document.getBookmarked(),is(true));
    }

    @Test
    public void givenItemIdWhenSetItemIdThenVerifyResult() {
        // Act
        document.setItemId(4L);
        // Assert
        errorCollector.checkThat(document.getItemId(),is(4L));
    }

    @Test
    public void givenTimeStampWhenSetTimeStampThenVerifyResult() {
       // Act
        document.setTimeStamp("timeStamp");
        // Assert
        errorCollector.checkThat(document.getTimeStamp(),is("timeStamp"));
    }

    @Test
    public void givenQuotesWhenSetQuotesThenVerifyResult() {
       // Act
        document.setQuotes("quotes");
        // Assert
        errorCollector.checkThat(document.getQuotes(),is("quotes"));
    }

    @Test
    public void givenImageWhenSetImageThenVerifyResult() {
       // Act
        document.setImage("image");
        // Assert
        errorCollector.checkThat(document.getImage(),is("image"));
    }

    @Test
    public void givenGroupIdWhenSetGroupIdThenVerifyResult() {
       // Act
        document.setGroupId("groupId");
        // Assert
        errorCollector.checkThat(document.getGroupId(),is("groupId"));
    }

    @Test
    public void givenConversationStarterTypeWhenSetConversationStarterTypeThenVerifyResult() {
        // Act
        document.setConversationStarterType(Collections.singletonList("value"));
        // Assert
        errorCollector.checkThat(document.getConversationStarterType(),is(Collections.singletonList("value")));
    }

    @Test
    public void givenDateWhenSetDateThenVerifyResult() {
      // Act
        document.setDate(date);
        // Assert
        errorCollector.checkThat(document.getDate(),is(date));
    }

    @Test
    public void givenNgramsWhenSetNgramsThenVerifyResult() {
       // Act
        document.setNgrams(Collections.singletonList(docNgrams));
        // Assert
        errorCollector.checkThat(document.getNgrams(),is(Collections.singletonList(docNgrams)));
    }

    @Test
    public void givenPrimaryDUNSMatchStrWhenSetPrimaryDUNSMatchStrThenVerifyResult() {
         // Act
        document.setPrimaryDUNSMatchStr("primaryDUNSMatchStr");
        // Assert
        errorCollector.checkThat(document.getPrimaryDUNSMatchStr(),is("primaryDUNSMatchStr"));
    }

    @Test
    public void givenAdditionalMatchQualifierStrWhenSetAdditionalMatchQualifierStrThenVerifyResult() {
       // Act
        document.setAdditionalMatchQualifierStr("additionalMatchQualifierStr");
        // Assert
        errorCollector.checkThat(document.getAdditionalMatchQualifierStr(),is("additionalMatchQualifierStr"));
    }
}
