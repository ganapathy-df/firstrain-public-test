package com.firstrain.frapi.pojo;

import com.firstrain.frapi.domain.MgmtTurnoverMeta;
import com.firstrain.frapi.domain.MetaShare;
import com.firstrain.frapi.domain.WebVolumeEventMeta;
import com.firstrain.frapi.domain.SecEventMeta;
import com.firstrain.frapi.domain.StockPriceMeta;
import com.firstrain.frapi.util.DefaultEnums.EventInformationEnum;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.Date;

import static org.hamcrest.CoreMatchers.is;

@RunWith(MockitoJUnitRunner.class)
public class EventTest {

    private Entity oldCompany;
    private MgmtTurnoverMeta mtMeta;
    private MetaShare metaShare;
    private Date datel;

    @Mock
    private WebVolumeEventMeta wvMeta;
    @Mock
    private SecEventMeta secMeta;
    @Mock
    private StockPriceMeta stockPriceMeta;

    @InjectMocks
    private Event event;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Before
    public void setUp() {
        oldCompany = new Entity();
        mtMeta = new MgmtTurnoverMeta();
        metaShare = new MetaShare();
        datel = new Date(12343L);
    }

    @Test
    public void givenMtMetaWhenSetMtMetaThenVerifyResult() {
        // Act
        event.setMtMeta(mtMeta);
        // Assert
        errorCollector.checkThat(event.getMtMeta(), is(mtMeta));
    }

    @Test
    public void givenWvMetaWhenSetWvMetaThenVerifyResult() {
        // Act
        event.setWvMeta(wvMeta);
        // Assert
        errorCollector.checkThat(event.getWvMeta(), is(wvMeta));
    }

    @Test
    public void givenSecMetaWhenSetSecMetaThenVerifyResult() {
        // Act
        event.setSecMeta(secMeta);
        // Assert
        errorCollector.checkThat(event.getSecMeta(), is(secMeta));
    }

    @Test
    public void givenStockPriceMetaWhenSetStockPriceMetaThenVerifyResult() {
        // Act
        event.setStockPriceMeta(stockPriceMeta);
        // Assert
        errorCollector.checkThat(event.getStockPriceMeta(), is(stockPriceMeta));
    }

    @Test
    public void givenPersonImageWhenSetPersonImageThenVerifyResult() {
        // Act
        event.setPersonImage("personImage");
        // Assert
        errorCollector.checkThat(event.getPersonImage(), is("personImage"));
    }

    @Test
    public void givenRankWhenSetRankThenVerifyResult() {
        // Act
        event.setRank(7);
        // Assert
        errorCollector.checkThat(event.getRank(), is(7));
    }

    @Test
    public void givenEventTypeIdWhenSetEventTypeIdThenVerifyResult() {
        // Act
        event.setEventTypeId(8);
        // Assert
        errorCollector.checkThat(event.getEventTypeId(), is(8));
    }

    @Test
    public void givenPersonWhenSetPersonThenVerifyResult() {
        // Act
        event.setPerson("person");
        // Assert
        errorCollector.checkThat(event.getPerson(), is("person"));
    }

    @Test
    public void givenFutureEventWhenSetFutureEventThenVerifyResult() {
        // Act
        event.setFutureEvent(true);
        // Assert
        errorCollector.checkThat(event.isFutureEvent(), is(true));
    }

    @Test
    public void givenSecFormTypeWhenSetSecFormTypeThenVerifyResult() {
        // Act
        event.setSecFormType("secFormType");
        // Assert
        errorCollector.checkThat(event.getSecFormType(), is("secFormType"));
    }

    @Test
    public void givenTitleWhenSetTitleThenVerifyResult() {
        // Act
        event.setTitle("title_1");
        // Assert
        errorCollector.checkThat(event.getTitle(), is("title_1"));
    }

    @Test
    public void givenVisibilityWhenSetVisibilityThenVerifyResult() {
        // Act
        event.setVisibility(metaShare);
        // Assert
        errorCollector.checkThat(event.getVisibility(), is(metaShare));
    }

    @Test
    public void givenDayIdWhenSetDayIdThenVerifyResult() {
        // Act
        event.setDayId(6);
        // Assert
        errorCollector.checkThat(event.getDayId(), is(6));
    }

    @Test
    public void givenCaptionWhenSetCaptionThenVerifyResult() {
        // Act
        event.setCaption("caption");
        // Assert
        errorCollector.checkThat(event.getCaption(), is("caption"));
    }

    @Test
    public void givenDescriptionWhenSetDescriptionThenVerifyResult() {
        // Act
        event.setDescription("description");
        // Assert
        errorCollector.checkThat(event.getDescription(), is("description"));
    }

    @Test
    public void givenLinkWhenSetLinkThenVerifyResult() {
        // Act
        event.setLink("link");
        // Assert
        errorCollector.checkThat(event.getLink(), is("link"));
    }

    @Test
    public void givenDateWhenSetDateThenVerifyResult() {
        // Act
        event.setDate(datel);
        // Assert
        errorCollector.checkThat(event.getDate(), is(datel));
    }

    @Test
    public void givenUrlWhenSetUrlThenVerifyResult() {
        // Act
        event.setUrl("url");
        // Assert
        errorCollector.checkThat(event.getUrl(), is("url"));
    }

    @Test
    public void givenEventTypeWhenSetEventTypeThenVerifyResult() {
        // Act
        event.setEventType("eventType");
        // Assert
        errorCollector.checkThat(event.getEventType(), is("eventType"));
    }

    @Test
    public void givenPropertiesWhenSetPropertiesThenVerifyResult() {
        // Act
        event.setProperties(Collections.singletonMap("test1",(Object)"tes2"));
        // Assert
        errorCollector.checkThat(event.getProperties(), is(Collections.singletonMap("test1",(Object)"tes2")));
    }

    @Test
    public void givenFlagWhenSetFlagThenVerifyResult() {
        // Act
        event.setFlag(6);
        // Assert
        errorCollector.checkThat(event.getFlag(), is(6));
    }

    @Test
    public void givenScoreWhenSetScoreThenVerifyResult() {
        // Act
        event.setScore(3d);
        // Assert
        errorCollector.checkThat(event.getScore(), is(3d));
    }

    @Test
    public void givenEntityIdWhenSetEntityIdThenVerifyResult() {
        // Act
        event.setEntityId(5L);
        // Assert
        errorCollector.checkThat(event.getEntityId(), is(5L));
    }

    @Test
    public void givenEventIdWhenSetEventIdThenVerifyResult() {
        // Act
        event.setEventId("eventId");
        // Assert
        errorCollector.checkThat(event.getEventId(), is("eventId"));
    }

    @Test
    public void givenEntityInfoWhenSetEntityInfoThenVerifyResult() {
        // Act
        event.setEntityInfo(oldCompany);
        // Assert
        errorCollector.checkThat(event.getEntityInfo(), is(oldCompany));
    }

    @Test
    public void givenHasExpiredWhenSetHasExpiredThenVerifyResult() {
        // Act
        event.setHasExpired(true);
        // Assert
        errorCollector.checkThat(event.isHasExpired(), is(true));
    }

    @Test
    public void givenItemIdWhenSetItemIdThenVerifyResult() {
        // Act
        event.setItemId(5L);
        // Assert
        errorCollector.checkThat(event.getItemId(), is(5L));
    }

    @Test
    public void givenEventInformationEnumWhenSetEventInformationEnumThenVerifyResult() {
        // Act
        event.setEventInformationEnum(EventInformationEnum.WEB_VOLUME);
        // Assert
        errorCollector.checkThat(event.getEventInformationEnum(), is(EventInformationEnum.WEB_VOLUME));
    }

    @Test
    public void givenPrimaryEvidenceEntityIdsWhenSetPrimaryEvidenceEntityIdsThenVerifyResult() {
        // Act
        event.setPrimaryEvidenceEntityIds(Collections.singleton(3L));
        // Assert
        errorCollector.checkThat(event.getPrimaryEvidenceEntityIds(), is(Collections.singleton(3L)));
    }

    @Test
    public void givenMatchedCompaniesWhenSetMatchedCompaniesThenVerifyResult() {
        // Act
        event.setMatchedCompanies(Collections.singletonList(oldCompany));
        // Assert
        errorCollector.checkThat(event.getMatchedCompanies(), is(Collections.singletonList(oldCompany)));
    }

    @Test
    public void givenMatchedTopicsWhenSetMatchedTopicsThenVerifyResult() {
        // Act
        event.setMatchedTopics(Collections.singletonList(oldCompany));
        // Assert
        errorCollector.checkThat(event.getMatchedTopics(), is(Collections.singletonList(oldCompany)));
    }

    @Test
    public void givenBookmarkedWhenSetBookmarkedThenVerifyResult() {
        // Act
        event.setBookmarked(true);
        // Assert
        errorCollector.checkThat(event.isBookmarked(), is(true));
    }

    @Test
    public void givenLinkableWhenSetLinkableThenVerifyResult() {
        // Act
        event.setLinkable(true);
        // Assert
        errorCollector.checkThat(event.isLinkable(), is(true));
    }

    @Test
    public void givenTriggerWhenSetTriggerThenVerifyResult() {
        // Act
        event.setTrigger(true);
        // Assert
        errorCollector.checkThat(event.isTrigger(), is(true));
    }

    @Test
    public void givenReportDateWhenSetReportDateThenVerifyResult() {
        // Act
        event.setReportDate(datel);
        // Assert
        errorCollector.checkThat(event.getReportDate(), is(datel));
    }

    @Test
    public void givenDummyImageWhenSetDummyImageThenVerifyResult() {
        // Act
        event.setDummyImage(true);
        // Assert
        errorCollector.checkThat(event.getDummyImage(), is(true));
    }

    @Test
    public void givenOldCompanyWhenSetOldCompanyThenVerifyResult() {
        // Act
        event.setOldCompany(oldCompany);
        // Assert
        errorCollector.checkThat(event.getOldCompany(), is(oldCompany));
    }

    @Test
    public void givenNewCompanyWhenSetNewCompanyThenVerifyResult() {
        // Act
        event.setNewCompany(oldCompany);
        // Assert
        errorCollector.checkThat(event.getNewCompany(), is(oldCompany));
    }

    @Test
    public void givenFirstNameWhenSetFirstNameThenVerifyResult() {
        // Act
        event.setFirstName("firstName");
        // Assert
        errorCollector.checkThat(event.getFirstName(), is("firstName"));
    }

    @Test
    public void givenLastNameWhenSetLastNameThenVerifyResult() {
        // Act
        event.setLastName("lastName");
        // Assert
        errorCollector.checkThat(event.getLastName(), is("lastName"));
    }

    @Test
    public void givenOldDesignationWhenSetOldDesignationThenVerifyResult() {
        // Act
        event.setOldDesignation("oldDesignation");
        // Assert
        errorCollector.checkThat(event.getOldDesignation(), is("oldDesignation"));
    }

    @Test
    public void givenNewDesignationWhenSetNewDesignationThenVerifyResult() {
        // Act
        event.setNewDesignation("newDesignation");
        // Assert
        errorCollector.checkThat(event.getNewDesignation(), is("newDesignation"));
    }

    @Test
    public void givenOldDesignationCodeWhenSetOldDesignationCodeThenVerifyResult() {
        // Act
        event.setOldDesignationCode(6);
        // Assert
        errorCollector.checkThat(event.getOldDesignationCode(), is(6));
    }

    @Test
    public void givenOldRegionWhenSetOldRegionThenVerifyResult() {
        // Act
        event.setOldRegion("oldRegion");
        // Assert
        errorCollector.checkThat(event.getOldRegion(), is("oldRegion"));
    }

    @Test
    public void givenOldDivisionWhenSetOldDivisionThenVerifyResult() {
        // Act
        event.setOldDivision("oldDivision");
        // Assert
        errorCollector.checkThat(event.getOldDivision(), is("oldDivision"));
    }

    @Test
    public void givenNewDesignationCodeWhenSetNewDesignationCodeThenVerifyResult() {
        // Act
        event.setNewDesignationCode(7);
        // Assert
        errorCollector.checkThat(event.getNewDesignationCode(), is(7));
    }

    @Test
    public void givenNewRegionWhenSetNewRegionThenVerifyResult() {
        // Act
        event.setNewRegion("newRegion");
        // Assert
        errorCollector.checkThat(event.getNewRegion(), is("newRegion"));
    }

    @Test
    public void givenNewDivisionWhenSetNewDivisionThenVerifyResult() {
        // Act
        event.setNewDivision("newDivision");
        // Assert
        errorCollector.checkThat(event.getNewDivision(), is("newDivision"));
    }
}
