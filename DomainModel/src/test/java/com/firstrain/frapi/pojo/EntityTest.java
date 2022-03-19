package com.firstrain.frapi.pojo;

import com.firstrain.frapi.util.DefaultEnums.Status;
import com.firstrain.web.pojo.EntityLink;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collection;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;

@RunWith(MockitoJUnitRunner.class)
public class EntityTest {

    private Entity sector;
    private short value;
    private EntityLink entityLink;

    @InjectMocks
    private Entity entity;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Before
    public void setUp() {
        sector = new Entity();
        entityLink = new EntityLink();
        value = 4;
    }

    @Test
    public void givenValueWhenSetValueThenVerifyResult() {
        // Arrange
        entity = new Entity("name");
        // Act
        entity.setValue(3f);
        // Assert
        errorCollector.checkThat(entity.getValue(), is(3f));
    }

    @Test
    public void givenIntensityWhenSetIntensityThenVerifyResult() {
        // Act
        entity.setIntensity(5);
        // Assert
        errorCollector.checkThat(entity.getIntensity(), is(5));
    }

    @Test
    public void givenOneDayDocCountWhenSetOneDayDocCountThenVerifyResult() {
        // Act
        entity.setOneDayDocCount(7);
        // Assert
        errorCollector.checkThat(entity.getOneDayDocCount(), is(7));
    }

    @Test
    public void givenThirtyOneDaysDocCountWhenSetThirtyOneDaysDocCountThenVerifyResult() {
        // Act
        entity.setThirtyOneDaysDocCount(7);
        // Assert
        errorCollector.checkThat(entity.getThirtyOneDaysDocCount(), is(7));
    }

    @Test
    public void givenVolumeRecentWeekWhenSetVolumeRecentWeekThenVerifyResult() {
        // Act
        entity.setVolumeRecentWeek(9);
        // Assert
        errorCollector.checkThat(entity.getVolumeRecentWeek(), is(9));
    }

    @Test
    public void givenSearchTokenWhenSetSearchTokenThenVerifyResult() {
        // Act
        entity.setSearchToken("searchToken");
        // Assert
        errorCollector.checkThat(entity.getSearchToken(), is("searchToken"));
    }

    @Test
    public void givenIdWhenSetIdThenVerifyResult() {
        // Act
        entity.setId("id");
        // Assert
        errorCollector.checkThat(entity.getId(), is("id"));
    }

    @Test
    public void givenNameWhenSetNameThenVerifyResult() {
        // Act
        entity.setName("name");
        // Assert
        errorCollector.checkThat(entity.getName(), is("name"));
    }

    @Test
    public void givenTickerWhenSetTickerThenVerifyResult() {
        // Act
        entity.setTicker("ticker");
        // Assert
        errorCollector.checkThat(entity.getTicker(), is("ticker"));
    }

    @Test
    public void givenCompanyIdWhenSetCompanyIdThenVerifyResult() {
        // Act
        entity.setCompanyId(7);
        // Assert
        errorCollector.checkThat(entity.getCompanyId(), is(7));
    }

    @Test
    public void givenBandWhenSetBandThenVerifyResult() {
        // Act
        entity.setBand(8);
        // Assert
        errorCollector.checkThat(entity.getBand(), is(8));
    }

    @Test
    public void givenEntitiesListWhenSetEntitiesListThenVerifyResult() {
        // Act
        entity.setEntitiesList(Collections.singletonList(entity));
        // Assert
        errorCollector.checkThat(entity.getEntitiesList(), is(Collections.singletonList(entity)));
    }

    @Test
    public void givenIndustryClassificationIdWhenSetIndustryClassificationIdThenVerifyResult() {
        // Act
        entity.setIndustryClassificationId(7);
        // Assert
        errorCollector.checkThat(entity.getIndustryClassificationId(), is(7));
    }

    @Test
    public void givenStatusWhenSetStatusThenVerifyResult() {
        // Act
        entity.setStatus(Status.ACTIVE);
        // Assert
        errorCollector.checkThat(entity.getStatus(), is(Status.ACTIVE));
    }

    @Test
    public void givenDocCountWhenSetDocCountThenVerifyResult() {
        // Act
        entity.setDocCount(8);
        // Assert
        errorCollector.checkThat(entity.getDocCount(), is(8));
    }

    @Test
    public void givenSelectedWhenSetSelectedThenVerifyResult() {
        // Act
        entity.setSelected(true);
        // Assert
        errorCollector.checkThat(entity.getSelected(), is(true));
    }

    @Test
    public void givenRemovedWhenSetRemovedThenVerifyResult() {
        // Act
        entity.setRemoved(true);
        // Assert
        errorCollector.checkThat(entity.getRemoved(), is(true));
    }

    @Test
    public void givenCompanyLogoUrlWhenSetCompanyLogoUrlThenVerifyResult() {
        // Act
        entity.setCompanyLogoUrl("companyLogoUrl");
        // Assert
        errorCollector.checkThat(entity.getCompanyLogoUrl(), is("companyLogoUrl"));
    }

    @Test
    public void givenDnbCompanyIdWhenSetDnbEntityIdThenVerifyResult() {
        // Act
        entity.setDnbEntityId("dnbCompanyId");
        // Assert
        errorCollector.checkThat(entity.getDnbEntityId(), is("dnbCompanyId"));
    }

    @Test
    public void givenEntityCountWhenSetEntityCountThenVerifyResult() {
        // Act
        entity.setEntityCount(8);
        // Assert
        errorCollector.checkThat(entity.getEntityCount(), is(8));
    }

    @Test
    public void givenAdditonalInfoMapWhenSetAdditonalInfoMapThenVerifyResult() {
        // Act
        entity.setAdditonalInfoMap(Collections.singletonMap("test", "test"));
        // Assert
        errorCollector.checkThat(entity.getAdditonalInfoMap(), is(Collections.singletonMap("test", "test")));
    }

    @Test
    public void givenDocumentCountWhenSetDocumentCountThenVerifyResult() {
        // Act
        entity.setDocumentCount(8);
        // Assert
        errorCollector.checkThat(entity.getDocumentCount(), is(8));
    }

    @Test
    public void givenHasTweetWhenSetHasTweetThenVerifyResult() {
        // Act
        entity.setHasTweet(true);
        // Assert
        errorCollector.checkThat(entity.getHasTweet(), is(true));
    }

    @Test
    public void givenTypeWhenSetTypeThenVerifyResult() {
        // Act
        entity.setType(7);
        // Assert
        errorCollector.checkThat(entity.getType(), is(7));
    }

    @Test
    public void givenIndustryCatIdWhenSetIndustryCatIdThenVerifyResult() {
        // Act
        entity.setIndustryCatId(8);
        // Assert
        errorCollector.checkThat(entity.getIndustryCatId(), is(8));
    }

    @Test
    public void givenRelevanceScoreWhenSetRelevanceScoreThenVerifyResult() {
        // Act
        entity.setRelevanceScore(value);
        // Assert
        errorCollector.checkThat(entity.getRelevanceScore(), is(value));
    }

    @Test
    public void givenDimensionWhenSetDimensionThenVerifyResult() {
        // Act
        entity.setDimension("dimension");
        // Assert
        errorCollector.checkThat(entity.getDimension(), is("dimension"));
    }

    @Test
    public void givenScopeWhenSetScopeThenVerifyResult() {
        // Act
        entity.setScope(value);
        // Assert
        errorCollector.checkThat(entity.getScope(), is(value));
    }

    @Test
    public void givenAddedWhenSetAddedThenVerifyResult() {
        // Act
        entity.setAdded(true);
        // Assert
        errorCollector.checkThat(entity.getAdded(), is(true));
    }

    @Test
    public void givenMatchedTypeWhenSetMatchedTypeThenVerifyResult() {
        // Act
        entity.setMatchedType("matchedType");
        // Assert
        errorCollector.checkThat(entity.getMatchedType(), is("matchedType"));
    }

    @Test
    public void givenSectorWhenSetSectorThenVerifyResult() {
        // Act
        entity.setSector(entity);
        // Assert
        errorCollector.checkThat(entity.getSector(), is(entity));
    }

    @Test
    public void givenSegmentWhenSetSegmentThenVerifyResult() {
        // Act
        entity.setSegment(entity);
        // Assert
        errorCollector.checkThat(entity.getSegment(), is(entity));
    }

    @Test
    public void givenIndustryWhenSetIndustryThenVerifyResult() {
        // Act
        entity.setIndustry(entity);
        // Assert
        errorCollector.checkThat(entity.getIndustry(), is(entity));
    }

    @Test
    public void givenHomePageWhenSetHomePageThenVerifyResult() {
        // Act
        entity.setHomePage("homePage");
        // Assert
        errorCollector.checkThat(entity.getHomePage(), is("homePage"));
    }

    @Test
    public void givenEntityLinksWhenSetEntityLinksThenVerifyResult() {
        // Act
        entity.setEntityLinks(Collections.singletonList(entityLink));
        // Assert
        errorCollector.checkThat(entity.getEntityLinks(), is(Collections.singletonList(entityLink)));
    }

    @Test
    public void givenSynonymWhenSetSynonymThenVerifyResult() {
        // Act
        entity.setSynonym("entityLink");
        // Assert
        errorCollector.checkThat(entity.getSynonym(), is("entityLink"));
    }

    @Test
    public void givenCountryWhenSetCountryThenVerifyResult() {
        // Act
        entity.setCountry("country");
        // Assert
        errorCollector.checkThat(entity.getCountry(), is("country"));
    }

    @Test
    public void givenBizLineCatIdsWhenSetBizLineCatIdsThenVerifyResult() {
        // Act
        entity.setBizLineCatIds(Collections.singletonList(7));
        // Assert
        errorCollector.checkThat(entity.getBizLineCatIds(), is((Collection) Collections.singletonList(7)));
    }

    @Test
    public void givenPrimaryDunsMatchWhenSetPrimaryDunsMatchThenVerifyResult() {
        // Act
        entity.setPrimaryDunsMatch(true);
        // Assert
        errorCollector.checkThat(entity.getPrimaryDunsMatch(), is(true));
    }

    @Test
    public void givenAdditionalMatchQualifierWhenSetAdditionalMatchQualifierThenVerifyResult() {
        // Act
        entity.setAdditionalMatchQualifier(true);
        // Assert
        errorCollector.checkThat(entity.getAdditionalMatchQualifier(), is(true));
    }

    @Test
    public void givenRemovedWhenMarkForDeletionThenVerifyResult() {
        // Act
        entity.markForDeletion();
        // Assert
        errorCollector.checkThat(entity.getMarkedForDeletion(), is(true));
    }

    @Test
    public void givenRemovedWhenMarkequalsThenVerifyResult() {
        // Act
        boolean actual = entity.equals(entity);
        // Assert
        errorCollector.checkThat(actual, is(true));
    }

    @Test
    public void givenNullWhenMarkequalsThenVerifyResult() {
        // Act
        boolean actual = entity.equals(null);
        // Assert
        errorCollector.checkThat(actual, is(false));
    }

    @Test
    public void givenSameClassWhenMarkequalsThenVerifyResult() {
        // Arrange
        Event event = new Event();
        // Act
        boolean actual = entity.equals(event);
        // Assert
        errorCollector.checkThat(actual, is(false));
        errorCollector.checkThat(entity.toString(), is("entityId=null, entityName=null"));
    }

    @Test
    public void givenClassWhenMarkequalsThenVerifyResult() {
        // Arrange
        sector.setId("id");
        // Act
        boolean actual = entity.equals(sector);
        // Assert
        errorCollector.checkThat(actual, is(false));
    }

    @Test
    public void givenIdWhenMarkequalsThenVerifyResult() {
        // Arrange
        sector.setId("id");
        entity.setId("is2");
        // Act
        boolean actual = entity.equals(sector);
        // Assert
        errorCollector.checkThat(actual, is(false));
        errorCollector.checkThat(entity.hashCode(), is(104551));
    }

    @Test
    public void givenIdaSameWhenMarkequalsThenVerifyResult() {
        // Arrange
        sector.setId("id");
        entity.setId("id");
        // Act
        boolean actual = entity.equals(sector);
        // Assert
        errorCollector.checkThat(actual, is(true));
    }

}
