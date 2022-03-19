package com.firstrain.frapi.pojo;

import com.firstrain.frapi.domain.VisualizationData;
import com.firstrain.frapi.pojo.wrapper.BaseSet;
import com.firstrain.frapi.pojo.wrapper.DocumentSet;
import com.firstrain.frapi.pojo.wrapper.TweetSet;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;

@RunWith(MockitoJUnitRunner.class)
public class MonitorBriefDetailTest {

    private TweetSet tweetList;
    private DocumentSet webResults;
    private VisualizationData visualizationData;
    private BaseSet baseSet;

    @InjectMocks
    private MonitorBriefDetail monitorBriefDetail;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Before
    public void setUp() {
        tweetList = new TweetSet();
        webResults = new DocumentSet();
        visualizationData = new VisualizationData();
        baseSet = new BaseSet();
    }

    @Test
    public void givenMonitorIdWhenSetMonitorIdThenVerifyResult() {
        // Act
        monitorBriefDetail.setMonitorId("monitorId");
        // Assert
        errorCollector.checkThat(monitorBriefDetail.getMonitorId(), is("monitorId"));
    }

    @Test
    public void givenMonitorNameWhenSetMonitorNameThenVerifyResult() {
        // Act
        monitorBriefDetail.setMonitorName("monitorName");
        // Assert
        errorCollector.checkThat(monitorBriefDetail.getMonitorName(), is("monitorName"));
    }

    @Test
    public void givenOwnedByWhenSetOwnedByThenVerifyResult() {
        // Act
        monitorBriefDetail.setOwnedBy(5L);
        // Assert
        errorCollector.checkThat(monitorBriefDetail.getOwnedBy(), is(5L));
    }

    @Test
    public void givenOwnedByTypeWhenSetOwnedByTypeThenVerifyResult() {
        // Act
        monitorBriefDetail.setOwnedByType("ownedByType");
        // Assert
        errorCollector.checkThat(monitorBriefDetail.getOwnedByType(), is("ownedByType"));
    }

    @Test
    public void givenTweetListWhenSetTweetListThenVerifyResult() {
        // Act
        monitorBriefDetail.setTweetList(tweetList);
        // Assert
        errorCollector.checkThat(monitorBriefDetail.getTweetList(), is(tweetList));
    }

    @Test
    public void givenWebResultsWhenSetWebResultsThenVerifyResult() {
        // Act
        monitorBriefDetail.setWebResults(webResults);
        // Assert
        errorCollector.checkThat(monitorBriefDetail.getWebResults(), is(webResults));
    }

    @Test
    public void givenVisualizationDataWhenSetVisualizationDataThenVerifyResult() {
        // Act
        monitorBriefDetail.setVisualizationData(visualizationData);
        // Assert
        errorCollector.checkThat(monitorBriefDetail.getVisualizationData(), is(visualizationData));
    }

    @Test
    public void givenBaseSetAndSwitchOnLine79FTWhenUpdatePrepThenVerifyResult() {
        // Arrange
        tweetList.setSectionType(BaseSet.SectionType.FT);
        // Act
        monitorBriefDetail.updatePrep(tweetList);
        // Assert
        errorCollector.checkThat(monitorBriefDetail.getTweetList(), is(tweetList));
    }

    @Test
    public void givenBaseSetFRWhenUpdatePrepThenVerifyResult() {
        // Arrange
        webResults.setSectionType(BaseSet.SectionType.FR);
        // Act
        monitorBriefDetail.updatePrep(webResults);
        // Assert
        errorCollector.checkThat(monitorBriefDetail.getWebResults(), is(webResults));
    }

    @Test
    public void givenBaseSetVIZWhenUpdatePrepThenVerifyResult() {
        // Arrange
        visualizationData.setSectionType(BaseSet.SectionType.VIZ);
        // Act
        monitorBriefDetail.updatePrep(visualizationData);
        // Assert
        errorCollector.checkThat(monitorBriefDetail.getVisualizationData(), is(visualizationData));
    }

    @Test
    public void givenBaseSetWhenSetPerfStatsThenVerifyResult() {
        // Act
        monitorBriefDetail.setPerfStats(baseSet);
        // Assert
        errorCollector.checkThat(monitorBriefDetail.getMonitorName(), nullValue());
    }
}
