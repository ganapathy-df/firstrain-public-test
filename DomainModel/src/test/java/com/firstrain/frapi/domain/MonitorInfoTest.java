package com.firstrain.frapi.domain;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;

@RunWith(MockitoJUnitRunner.class)
public class MonitorInfoTest {
    @InjectMocks
    private MonitorInfo monitorInfo;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Test
    public void givenMonitorIdWhenSetMonitorIdThenVerifyResult() {
        // Act
        monitorInfo.setMonitorId("monitorId");
        // Assert
        errorCollector.checkThat(monitorInfo.getMonitorId(), is("monitorId"));
    }

    @Test
    public void givenMonitorNameWhenSetMonitorNameThenVerifyResult() {
        // Act
        monitorInfo.setMonitorName("monitorName");
        // Assert
        errorCollector.checkThat(monitorInfo.getMonitorName(), is("monitorName"));
    }

    @Test
    public void givenFavoriteWhenSetFavoriteThenVerifyResult() {
        // Act
        monitorInfo.setFavorite(true);
        // Assert
        errorCollector.checkThat(monitorInfo.isFavorite(), is(true));
    }

    @Test
    public void givenMonitorAdminWhenSetMonitorAdminThenVerifyResult() {
        // Act
        monitorInfo.setMonitorAdmin(true);
        // Assert
        errorCollector.checkThat(monitorInfo.isMonitorAdmin(), is(true));
    }

    @Test
    public void givenFavoriteUserItemIdWhenSetFavoriteUserItemIdThenVerifyResult() {
        // Act
        monitorInfo.setFavoriteUserItemId(3L);
        // Assert
        errorCollector.checkThat(monitorInfo.getFavoriteUserItemId(), is(3L));
    }

    @Test
    public void givenItemCountWhenSetItemCountThenVerifyResult() {
        // Act
        monitorInfo.setItemCount(4);
        // Assert
        errorCollector.checkThat(monitorInfo.getItemCount(), is(4));
    }

    @Test
    public void givenManagementTurnoverAvailableWhenSetManagementTurnoverAvailableThenVerifyResult() {
        // Act
        monitorInfo.setManagementTurnoverAvailable(true);
        // Assert
        errorCollector.checkThat(monitorInfo.getManagementTurnoverAvailable(), is(true));
    }

    @Test
    public void givenEventAvailableWhenSetEventAvailableThenVerifyResult() {
        // Act
        monitorInfo.setEventAvailable(true);
        // Assert
        errorCollector.checkThat(monitorInfo.getEventAvailable(), is(true));
    }

    @Test
    public void givenTweetAvailableWhenSetTweetAvailableThenVerifyResult() {
        // Act
        monitorInfo.setTweetAvailable(true);
        // Assert
        errorCollector.checkThat(monitorInfo.getTweetAvailable(), is(true));
    }

    @Test
    public void givenMailAvailableWhenSetMailAvailableThenVerifyResult() {
        // Act
        monitorInfo.setMailAvailable(true);
        // Assert
        errorCollector.checkThat(monitorInfo.getMailAvailable(), is(true));
    }

    @Test
    public void givenMailBadgeWhenSetMailBadgeThenVerifyResult() {
        // Act
        monitorInfo.setMailBadge(true);
        // Assert
        errorCollector.checkThat(monitorInfo.getMailBadge(), is(true));
    }

    @Test
    public void givenHasNewWhenSetHasNewThenVerifyResult() {
        // Act
        monitorInfo.setHasNew(true);
        // Assert
        errorCollector.checkThat(monitorInfo.getHasNew(), is(true));
    }

    @Test
    public void givenOwnedByWhenSetOwnedByThenVerifyResult() {
        // Act
        monitorInfo.setOwnedBy(5L);
        // Assert
        errorCollector.checkThat(monitorInfo.getOwnedBy(), is(5L));
    }

    @Test
    public void givenOwnedByTypeWhenSetOwnedByTypeThenVerifyResult() {
        // Act
        monitorInfo.setOwnedByType("ownedByType");
        // Assert
        errorCollector.checkThat(monitorInfo.getOwnedByType(), is("ownedByType"));
    }

    @Test
    public void givenTypeWhenEqualsThenVerifyResult() {
        // Act
        boolean actual = monitorInfo.equals(monitorInfo);
        // Assert
        errorCollector.checkThat(actual, is(true));
    }

    @Test
    public void givenNullWhenEqualsThenVerifyResult() {
        // Act
        boolean actual =  monitorInfo.equals(null);
        // Assert
        errorCollector.checkThat(actual, is(false));
    }

    @Test
    public void givenClassWhenEqualsThenVerifyResult() {
        // Arrange
        MonitorInfo info = new MonitorInfo();
        info.setMonitorId("id");
        // Act
        boolean actual =  monitorInfo.equals(info);
        // Assert
        errorCollector.checkThat(actual, is(false));
    }

    @Test
    public void givenClassNewWhenEqualsThenVerifyResult() {
        // Arrange
        Entity info = new Entity();
        // Act
        boolean actual =  monitorInfo.equals(info);
        // Assert
        errorCollector.checkThat(actual, is(false));
    }

    @Test
    public void givenMonitorIdWhenEqualsThenVerifyResult() {
        // Arrange
        MonitorInfo info = new MonitorInfo();
        info.setMonitorId("id");
        checkMonitorEquals("id1", info, false);
        errorCollector.checkThat(monitorInfo.hashCode(),is(104085));
    }

    @Test
    public void givenMonitorIdSameWhenEqualsThenVerifyResult() {
        // Arrange
        MonitorInfo info = new MonitorInfo();
        info.setMonitorId("id");
        checkMonitorEquals("id", info, true);
    }

    private void checkMonitorEquals(final String id, final MonitorInfo info, final boolean result) {
        monitorInfo.setMonitorId(id);
        // Act
        boolean actual =  monitorInfo.equals(info);
        // Assert
        errorCollector.checkThat(actual, is(result));
    }
}
