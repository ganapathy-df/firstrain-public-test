package com.firstrain.frapi.domain;

import com.firstrain.frapi.pojo.Entity;
import com.firstrain.utils.MgmtChangeType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;

import static org.hamcrest.CoreMatchers.is;

@RunWith(MockitoJUnitRunner.class)
public class MgmtTurnoverMetaTest {

    private Entity oldCompany;

    @InjectMocks
    private MgmtTurnoverMeta mgmtTurnoverMeta;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Before
    public void setUp() {
        oldCompany = new Entity();
        oldCompany.setName("name");
        mgmtTurnoverMeta.setNewDesignation("newDesignation");
        mgmtTurnoverMeta.setNewGroup("newGroup");
        mgmtTurnoverMeta.setNewRegion("newRegion");
        mgmtTurnoverMeta.setOldCompany(oldCompany);
        mgmtTurnoverMeta.setOldDesignation("oldDesignation");
        mgmtTurnoverMeta.setOldGroup("oldGroup");
        mgmtTurnoverMeta.setOldRegion("oldRegion");
        mgmtTurnoverMeta.setNewCompany(oldCompany);
    }

    @Test
    public void givenTrendIdWhenSetTrendIdThenVerifyResult() {
        // Act
        mgmtTurnoverMeta.setTrendId("trendId");
        // Assert
        errorCollector.checkThat(mgmtTurnoverMeta.getTrendId(), is("trendId"));
    }

    @Test
    public void givenNewCompanyWhenSetNewCompanyThenVerifyResult() {
        // Act
        mgmtTurnoverMeta.setNewCompany(oldCompany);
        // Assert
        errorCollector.checkThat(mgmtTurnoverMeta.getNewCompany(), is(oldCompany));
    }

    @Test
    public void givenOldCompanyWhenSetOldCompanyThenVerifyResult() {
        // Act
        mgmtTurnoverMeta.setOldCompany(oldCompany);
        // Assert
        errorCollector.checkThat(mgmtTurnoverMeta.getOldCompany(), is(oldCompany));
    }

    @Test
    public void givenNewDesignationWhenSetNewDesignationThenVerifyResult() {
        // Act
        mgmtTurnoverMeta.setNewDesignation("newDesignation");
        // Assert
        errorCollector.checkThat(mgmtTurnoverMeta.getNewDesignation(), is("newDesignation"));
    }

    @Test
    public void givenOldDesignationWhenSetOldDesignationThenVerifyResult() {
        // Act
        mgmtTurnoverMeta.setOldDesignation("oldDesignation");
        // Assert
        errorCollector.checkThat(mgmtTurnoverMeta.getOldDesignation(), is("oldDesignation"));
    }

    @Test
    public void givenPersonWhenSetPersonThenVerifyResult() {
        // Act
        mgmtTurnoverMeta.setPerson("person");
        // Assert
        errorCollector.checkThat(mgmtTurnoverMeta.getPerson(), is("person"));
    }

    @Test
    public void givenChangeTypeWhenSetChangeTypeThenVerifyResult() {
        // Act
        mgmtTurnoverMeta.setChangeType(MgmtChangeType.INTERNALMOVE);
        // Assert
        errorCollector.checkThat(mgmtTurnoverMeta.getChangeType(), is(MgmtChangeType.INTERNALMOVE));
    }

    @Test
    public void givenOldRegionWhenSetOldRegionThenVerifyResult() {
        // Act
        mgmtTurnoverMeta.setOldRegion("oldRegion");
        // Assert
        errorCollector.checkThat(mgmtTurnoverMeta.getOldRegion(), is("oldRegion"));
    }

    @Test
    public void givenNewRegionWhenSetNewRegionThenVerifyResult() {
        // Act
        mgmtTurnoverMeta.setNewRegion("newRegion");
        // Assert
        errorCollector.checkThat(mgmtTurnoverMeta.getNewRegion(), is("newRegion"));
    }

    @Test
    public void givenOldGroupWhenSetOldGroupThenVerifyResult() {
        // Act
        mgmtTurnoverMeta.setOldGroup("oldGroup");
        // Assert
        errorCollector.checkThat(mgmtTurnoverMeta.getOldGroup(), is("oldGroup"));
    }

    @Test
    public void givenNewGroupWhenSetNewGroupThenVerifyResult() {
        // Act
        mgmtTurnoverMeta.setNewGroup("newGroup");
        // Assert
        errorCollector.checkThat(mgmtTurnoverMeta.getNewGroup(), is("newGroup"));
    }

    @Test
    public void givenFutureEventWhenSetFutureEventThenVerifyResult() {
        // Act
        mgmtTurnoverMeta.setFutureEvent(true);
        // Assert
        errorCollector.checkThat(mgmtTurnoverMeta.isFutureEvent(), is(true));
    }

    @Test
    public void givenHireTypeWhenGetMgmtTurnoverTitleWithoutHtmlThenVerifyResult() {
        // Arrange
        testGetMgmtTurnoverTitleWithoutHtml("null JOINED as newDesignation of newGroup, newRegion from name", MgmtChangeType.HIRE);
    }

    @Test
    public void givenDepartureTypeWhenGetMgmtTurnoverTitleWithoutHtmlThenVerifyResult() {
        // Arrange
        testGetMgmtTurnoverTitleWithoutHtml("null, oldDesignation of oldGroup, oldRegion LEFT for name", MgmtChangeType.DEPARTURE);
    }

    private void testGetMgmtTurnoverTitleWithoutHtml(final String data, final MgmtChangeType changeType) {
        mgmtTurnoverMeta.setChangeType(changeType);
        // Act
        String actual = mgmtTurnoverMeta.getMgmtTurnoverTitleWithoutHtml();
        // Assert
        errorCollector.checkThat(actual, is(data));
    }

    @Test
    public void givenInternalMoveTypeWhenGetMgmtTurnoverTitleWithoutHtmlThenVerifyResult() {
        // Arrange
        mgmtTurnoverMeta.setChangeType(MgmtChangeType.INTERNALMOVE);
        // Act
        String actual = mgmtTurnoverMeta.getMgmtTurnoverTitleWithoutHtml();
        // Assert
        errorCollector.checkThat(actual, is("null, oldDesignation of oldGroup," +
                " oldRegion MOVED to newDesignation of newGroup, newRegion"));
    }

    @Test
    public void givenHireTypeWhenGetMgmtTurnoverTitleThenVerifyResult() {
        // Arrange
        mgmtTurnoverMeta.setChangeType(MgmtChangeType.HIRE);
        // Act
        String actual = mgmtTurnoverMeta.getMgmtTurnoverTitle();
        // Assert
        errorCollector.checkThat(actual, is("<strong>null</strong> <span class='status-callout" +
                " mgmt-status--joined'>JOINED</span> as newDesignation of newGroup, newRegion from name"));
    }

    @Test
    public void givenDepartureTypeWhenGetMgmtTurnoverTitleThenVerifyResult() {
        // Arrange
        checkGetMgmtTurnoverTitle("<strong>null</strong>, oldDesignation of oldGroup, oldRegion <span class='status-callout mgmt-status--left'>LEFT</span></span> for name", MgmtChangeType.DEPARTURE);
    }

    @Test
    public void givenInternalMoveTypeWhenGetMgmtTurnoverTitleThenVerifyResult() {
        // Arrange
        checkGetMgmtTurnoverTitle("<strong>null</strong>, oldDesignation of oldGroup, oldRegion <span class='status-callout mgmt-status--move'>MOVED</span></span> to newDesignation of newGroup, newRegion", MgmtChangeType.INTERNALMOVE);
    }

    private void checkGetMgmtTurnoverTitle(final String changeType, final MgmtChangeType mgmtChangeType) {
        mgmtTurnoverMeta.setChangeType(mgmtChangeType);
        // Act
        String actual = mgmtTurnoverMeta.getMgmtTurnoverTitle();
        // Assert
        errorCollector.checkThat(actual, is(changeType));
    }

    @Test
    public void givenHireTypeWhenGetMgmtTurnoverTitleInlineHtmlThenVerifyResult() {
        // Arrange
        mgmtTurnoverMeta.setChangeType(MgmtChangeType.HIRE);
        // Act
        String actual = mgmtTurnoverMeta.getMgmtTurnoverTitleInlineHtml();
        // Assert
        errorCollector.checkThat(actual, is("<strong>null</strong> <span style='color:#32932d; text-transform:uppercase;font-size:90%'>JOINED</span>  as newDesignation of newGroup, newRegion from entityId=null, entityName=name"));
    }

    @Test
    public void givenDepartureTypeWhenGetMgmtTurnoverTitleInlineHtmlThenVerifyResult() {
        // Arrange
        testgetMgmtTurnoverTitleInlineHtml("<span style='color:#e82d2d; text-transform:uppercase;font-size:90%'>LEFT</span>", " for entityId=null, entityName=name", MgmtChangeType.DEPARTURE);
    }

    @Test
    public void givenInternalMoveTypeWhenGetMgmtTurnoverTitleInlineHtmlThenVerifyResult() {
        // Arrange
        testgetMgmtTurnoverTitleInlineHtml("<strong><span style='color:#005596; text-transform:uppercase;font-size:90%'>MOVED<strong>", " to newDesignation of newGroup, newRegion", MgmtChangeType.INTERNALMOVE);
    }

    private void testgetMgmtTurnoverTitleInlineHtml(final String data1, final String data2, final MgmtChangeType changeType) {
        mgmtTurnoverMeta.setChangeType(changeType);
        // Act
        String actual = mgmtTurnoverMeta.getMgmtTurnoverTitleInlineHtml();
        // Assert
        errorCollector.checkThat(actual, is("<strong>null</strong>, oldDesignation of oldGroup, oldRegion " +
                data1 +
                data2));
    }

    @Test
    public void givenInternalMoveTypeWhenGetRefinedStrTThenVerifyResult() throws Exception  {
        // Act
        String actual = Whitebox.invokeMethod(mgmtTurnoverMeta,"getRefinedStr","test",false);
        // Assert
        errorCollector.checkThat(actual, is("test"));
    }
}
