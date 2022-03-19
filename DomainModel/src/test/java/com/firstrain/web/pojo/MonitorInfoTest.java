package com.firstrain.web.pojo;

import com.firstrain.web.wrapper.EntityWrapper;
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
public class MonitorInfoTest {

    private EntityWrapper entities;

    @InjectMocks
    private MonitorInfo monitorInfo;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Before
    public void setUp() {
        entities = new EntityWrapper();
    }

    @Test
    public void givenMonitorIdWhenSetMonitorIdThenVerifyResult() {
        // Act
        monitorInfo.setMonitorId("monitorId");
        // Assert
        errorCollector.checkThat(monitorInfo.getMonitorId(),is("monitorId"));
    }

    @Test
    public void givenMonitorNameWhenSetMonitorNameThenVerifyResult() {
        // Act
        monitorInfo.setMonitorName("monitorName");
        // Assert
        errorCollector.checkThat(monitorInfo.getMonitorName(),is("monitorName"));
    }

    @Test
    public void givenFavoriteWhenSetFavoriteThenVerifyResult() {
        // Act
        monitorInfo.setFavorite(true);
        // Assert
        errorCollector.checkThat(monitorInfo.isFavorite(),is(true));
    }

    @Test
    public void givenMonitorAdminWhenSetMonitorAdminThenVerifyResult() {
        // Act
        monitorInfo.setMonitorAdmin(true);
        // Assert
        errorCollector.checkThat(monitorInfo.isMonitorAdmin(),is(true));
    }

    @Test
    public void givenFavoriteItemIdWhenSetFavoriteItemIdThenVerifyResult() {
        // Act
        monitorInfo.setFavoriteItemId(3L);
        // Assert
        errorCollector.checkThat(monitorInfo.getFavoriteItemId(),is(3L));
    }

    @Test
    public void givenActiveMailWhenSetActiveMailThenVerifyResult() {
        // Act
        monitorInfo.setActiveMail(true);
        // Assert
        errorCollector.checkThat(monitorInfo.getActiveMail(),is(true));
    }

    @Test
    public void givenEntitiesWhenSetEntitiesThenVerifyResult() {
        // Act
        monitorInfo.setEntities(Collections.singletonList(entities));
        // Assert
        errorCollector.checkThat(monitorInfo.getEntities(),is(Collections.singletonList(entities)));
    }
}
