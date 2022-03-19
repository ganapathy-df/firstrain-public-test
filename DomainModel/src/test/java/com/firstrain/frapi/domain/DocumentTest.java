package com.firstrain.frapi.domain;

import com.firstrain.frapi.pojo.Entity;
import com.firstrain.frapi.util.CompanyTeam;
import com.firstrain.frapi.util.ContentType;
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

    private Entity source;
    private Document.DocQuote docQuote;
    private Date dateUnit;
    private MetaShare metaShare;
    private Tweet tweet;
    private DocNgrams docNgrams;

    @InjectMocks
    private Document document;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Before
    public void setUp() {
        source = new Entity();
        docQuote = new Document.DocQuote();
        dateUnit = new Date(12345L);
        metaShare = new MetaShare();
        tweet = new Tweet();
        docNgrams = new DocNgrams();
        document.setAdditionalMatchQualifierStr("additionalMatchQualifierStr");
    }

    @Test
    public void givenCatEntriesWhenSetCatEntriesThenVerifyResult() {
        // Act
        document.setCatEntries(Collections.singletonList(source));
        // Assert
        errorCollector.checkThat(document.getCatEntries(),is(Collections.singletonList(source)));
    }

    @Test
    public void givenSlotWhenSetSlotThenVerifyResult() {
       // Act
        document.setSlot(4);
        // Assert
        errorCollector.checkThat(document.getSlot(),is(4));
    }

    @Test
    public void givenInsertTimeWhenSetInsertTimeThenVerifyResult() {
        // Act
        document.setInsertTime(dateUnit);
        // Assert
        errorCollector.checkThat(document.getInsertTime(),is(dateUnit));
    }

    @Test
    public void givenRankWhenSetRankThenVerifyResult() {
        // Act
        document.setRank(3);
        // Assert
        errorCollector.checkThat(document.getRank(),is(3));
    }

    @Test
    public void givenCompanyTeamWhenSetCompanyTeamThenVerifyResult() {
       // Act
        document.setCompanyTeam(CompanyTeam.EXECUTIVE);
        // Assert
        errorCollector.checkThat(document.getCompanyTeam(),is(CompanyTeam.EXECUTIVE));
    }

    @Test
    public void givenPrimaryEntityWhenSetPrimaryEntityThenVerifyResult() {
        // Act
        document.setPrimaryEntity(source);
        // Assert
        errorCollector.checkThat(document.getPrimaryEntity(),is(source));
    }

    @Test
    public void givenCompanyNameWhenSetCompanyNameThenVerifyResult() {
       // Act
        document.setCompanyName("companyName");
        // Assert
        errorCollector.checkThat(document.getCompanyName(),is("companyName"));
    }

    @Test
    public void givenSecFormTypeWhenSetSecFormTypeThenVerifyResult() {
       // Act
        document.setSecFormType("secFormType");
        // Assert
        errorCollector.checkThat(document.getSecFormType(),is("secFormType"));
    }

    @Test
    public void givenDupCountWhenSetDupCountThenVerifyResult() {
        // Act
        document.setDupCount(5);
        // Assert
        errorCollector.checkThat(document.getDupCount(),is(5));
    }

    @Test
    public void givenVisibilityWhenSetVisibilityThenVerifyResult() {
       // Act
        document.setVisibility(metaShare);
        // Assert
        errorCollector.checkThat(document.getVisibility(),is(metaShare));
    }

    @Test
    public void givenIdWhenSetIdThenVerifyResult() {
      // Act
        document.setId("id_1");
        // Assert
        errorCollector.checkThat(document.getId(),is("id_1"));
    }

    @Test
    public void givenTitleWhenSetTitleThenVerifyResult() {
       // Act
        document.setTitle("title_1");
        // Assert
        errorCollector.checkThat(document.getTitle(),is("title_1"));
    }

    @Test
    public void givenUrlWhenSetUrlThenVerifyResult() {
        // Act
        document.setUrl("url");
        // Assert
        errorCollector.checkThat(document.getUrl(),is("url"));
    }

    @Test
    public void givenDateWhenSetDateThenVerifyResult() {
       // Act
        document.setDate(dateUnit);
        // Assert
        errorCollector.checkThat(document.getDate(),is(dateUnit));
    }

    @Test
    public void givenSummaryWhenSetSummaryThenVerifyResult() {
       // Act
        document.setSummary("summary");
        // Assert
        errorCollector.checkThat(document.getSummary(),is("summary"));
    }

    @Test
    public void givenSourceWhenSetSourceThenVerifyResult() {
       // Act
        document.setSource(source);
        // Assert
        errorCollector.checkThat(document.getSource(),is(source));
    }

    @Test
    public void givenMatchedCompaniesWhenSetMatchedCompaniesThenVerifyResult() {
        // Act
        document.setMatchedCompanies(Collections.singletonList(source));
        // Assert
        errorCollector.checkThat(document.getMatchedCompanies(),is(Collections.singletonList(source)));
    }

    @Test
    public void givenMatchedTopicsWhenSetMatchedTopicsThenVerifyResult() {
        // Act
        document.setMatchedTopics(Collections.singletonList(source));
        // Assert
        errorCollector.checkThat(document.getMatchedTopics(),is(Collections.singletonList(source)));
    }

    @Test
    public void givenImageWhenSetImageThenVerifyResult() {
       // Act
        document.setImage("image");
        // Assert
        errorCollector.checkThat(document.getImage(),is("image"));
    }

    @Test
    public void givenFaviconWhenSetFaviconThenVerifyResult() {
      // Act
        document.setFavicon("favicon");
        // Assert
        errorCollector.checkThat(document.getFavicon(),is("favicon"));
    }

    @Test
    public void givenGroupIdWhenSetGroupIdThenVerifyResult() {
        // Act
        document.setGroupId(4L);
        // Assert
        errorCollector.checkThat(document.getGroupId(),is(4L));
    }

    @Test
    public void givenItemIdWhenSetItemIdThenVerifyResult() {
       // Act
        document.setItemId(1L);
        // Assert
        errorCollector.checkThat(document.getItemId(),is(1L));
    }

    @Test
    public void givenBookmarkedWhenSetBookmarkedThenVerifyResult() {
        // Act
        document.setBookmarked(true);
        // Assert
        errorCollector.checkThat(document.isBookmarked(),is(true));
    }

    @Test
    public void givenScoreWhenSetScoreThenVerifyResult() {
        // Act
        document.setScore(6);
        // Assert
        errorCollector.checkThat(document.getScore(),is(6));
    }

    @Test
    public void givenDocQuoteWhenSetDocQuoteThenVerifyResult() {
        // Act
        document.setDocQuote(docQuote);
        // Assert
        errorCollector.checkThat(document.getDocQuote(),is(docQuote));
    }

    @Test
    public void givenContentTypeWhenSetContentTypeThenVerifyResult() {
       // Act
        document.setContentType(ContentType.WEBNEWS);
        // Assert
        errorCollector.checkThat(document.getContentType(),is(ContentType.WEBNEWS));
    }

    @Test
    public void givenLoginRequiredWhenSetLoginRequiredThenVerifyResult() {
        // Act
        document.setLoginRequired(true);
        // Assert
        errorCollector.checkThat(document.isLoginRequired(),is(true));
    }

    @Test
    public void givenPersonCatIdWhenSetPersonCatIdThenVerifyResult() {
       // Act
        document.setPersonCatId(6);
        // Assert
        errorCollector.checkThat(document.getPersonCatId(),is(6));
    }

    @Test
    public void givenSimilarQuotesCountWhenSetSimilarQuotesCountThenVerifyResult() {
      // Act
        document.setSimilarQuotesCount(7);
        // Assert
        errorCollector.checkThat(document.getSimilarQuotesCount(),is(7));
    }

    @Test
    public void givenPersonImageUrlWhenSetPersonImageUrlThenVerifyResult() {
        // Act
        document.setPersonImageUrl("personImageUrl");
        // Assert
        errorCollector.checkThat(document.getPersonImageUrl(),is("personImageUrl"));
    }

    @Test
    public void givenCompanyPeopleWhenSetCompanyPeopleThenVerifyResult() {
       // Act
        document.setCompanyPeople(true);
        // Assert
        errorCollector.checkThat(document.getCompanyPeople(),is(true));
    }

    @Test
    public void givenRelatedTweetsWhenSetRelatedTweetsThenVerifyResult() {
       // Act
        document.setRelatedTweets(Collections.singletonList(tweet));
        // Assert
        errorCollector.checkThat(document.getRelatedTweets(),is(Collections.singletonList(tweet)));
    }

    @Test
    public void givenMatchedCriticalEventWhenSetMatchedCriticalEventThenVerifyResult() {
        // Act
        document.setMatchedCriticalEvent(source);
        // Assert
        errorCollector.checkThat(document.getMatchedCriticalEvent(),is(source));
    }

    @Test
    public void givenRoleEntitiesWhenSetRoleEntitiesThenVerifyResult() {
        // Act
        document.setRoleEntities(Collections.singletonList(source));
        // Assert
        errorCollector.checkThat(document.getRoleEntities(),is(Collections.singletonList(source)));
    }

    @Test
    public void givenNgramsWhenSetNgramsThenVerifyResult() {
        // Act
        document.setNgrams(Collections.singletonList(docNgrams));
        // Assert
        errorCollector.checkThat(document.getNgrams(),is(Collections.singletonList(docNgrams)));
        errorCollector.checkThat(document.getAdditionalMatchQualifierStr(),is("additionalMatchQualifierStr"));
    }

    @Test
    public void givenPrimaryDunsMatchStrWhenSetPrimaryDunsMatchStrThenVerifyResult() {
       // Act
        document.setPrimaryDunsMatchStr("primaryDunsMatchStr");
        // Assert
        errorCollector.checkThat(document.getPrimaryDunsMatchStr(),is("primaryDunsMatchStr"));
    }

    @Test
    public void givenPrimaryDunsMatchStrWhenGetHighlightedQuoteThenVerifyResult() {
        // Arrange
        final char quoteMark = 0x25ab;
        final char quoteMak = ' ';
        docQuote = new Document.DocQuote("tex"+quoteMak+" " + quoteMark + " "+"tex"+quoteMak+" " + quoteMark + " "
                ,"quote");
        // Act
        String actual = docQuote.getHighlightedQuote();
        // Assert
        errorCollector.checkThat(actual,is("tex <b>tex </b>"));
    }

    @Test
    public void givenPrimaryWhenGetHighlightedQuoteThenVerifyResult() {
        // Arrange
        final char quoteMark = 0x25ab;
        docQuote = new Document.DocQuote("text"+" " + quoteMark + " "+"text"+" " + quoteMark + " "
                ,"quote");
        // Act
        String actual = docQuote.getHighlightedQuote();
        // Assert
        errorCollector.checkThat(actual,is("text <b>text</b> "));
    }

    @Test
    public void givenPrimaryDunsMatchStrWhenSetTextThenVerifyResult() {
        // Act
        docQuote.setText("primaryDunsMatchStr");
        // Assert
        errorCollector.checkThat(docQuote.getText(),is("primaryDunsMatchStr"));
    }

    @Test
    public void givenPrimaryDunsMatchStrWhenSetPersonThenVerifyResult() {
        // Act
        docQuote.setPerson("person");
        // Assert
        errorCollector.checkThat(docQuote.getPerson(),is("person"));
    }

}
