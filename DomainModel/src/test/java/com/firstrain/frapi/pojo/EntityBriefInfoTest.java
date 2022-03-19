package com.firstrain.frapi.pojo;

import com.firstrain.frapi.domain.EntityMap;
import com.firstrain.frapi.domain.MgmtTurnoverData;
import com.firstrain.frapi.domain.VisualizationData;
import com.firstrain.frapi.pojo.wrapper.BaseSet;
import com.firstrain.frapi.pojo.wrapper.DocumentSet;
import com.firstrain.frapi.pojo.wrapper.EventSet;
import com.firstrain.frapi.pojo.wrapper.TweetSet;
import com.firstrain.utils.object.PerfRequestEntry;
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
import static org.hamcrest.CoreMatchers.nullValue;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class EntityBriefInfoTest {

    private DocumentSet webResults;
    private TweetSet tweetList;
    private EventSet mgmtChangeEvents;
    private Graph webVolumeGraph;
    private MgmtTurnoverData mgmtTurnoverData;
    private VisualizationData visualizationData;
    private Entity entity;
    private EntityMap entityMap;
    private BaseSet baseSet;

    @InjectMocks
    private EntityBriefInfo entityBriefInfo;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Before
    public void setUp() {
        webResults = new DocumentSet();
        tweetList = new TweetSet();
        mgmtChangeEvents = new EventSet();
        webVolumeGraph = new Graph();
        mgmtTurnoverData = new MgmtTurnoverData();
        visualizationData = new VisualizationData();
        entity = new Entity();
        entityMap = new EntityMap();
        baseSet = new BaseSet();
    }

    @Test
    public void givenWebResultsWhenSetWebResultsThenVerifyResult() {
        // Act
        entityBriefInfo.setWebResults(webResults);
        // Assert
        errorCollector.checkThat(entityBriefInfo.getWebResults(), is(webResults));
    }

    @Test
    public void givenTweetListWhenSetTweetListThenVerifyResult() {
        // Act
        entityBriefInfo.setTweetList(tweetList);
        // Assert
        errorCollector.checkThat(entityBriefInfo.getTweetList(), is(tweetList));
    }

    @Test
    public void givenAnalystCommentsWhenSetAnalystCommentsThenVerifyResult() {
        // Act
        entityBriefInfo.setAnalystComments(webResults);
        // Assert
        errorCollector.checkThat(entityBriefInfo.getAnalystComments(), is(webResults));
    }

    @Test
    public void givenMgmtChangeEventsWhenSetMgmtChangeEventsThenVerifyResult() {
        // Act
        entityBriefInfo.setMgmtChangeEvents(mgmtChangeEvents);
        // Assert
        errorCollector.checkThat(entityBriefInfo.getMgmtChangeEvents(), is(mgmtChangeEvents));
    }

    @Test
    public void givenWebVolumeGraphWhenSetWebVolumeGraphThenVerifyResult() {
        // Act
        entityBriefInfo.setWebVolumeGraph(webVolumeGraph);
        // Assert
        errorCollector.checkThat(entityBriefInfo.getWebVolumeGraph(), is(webVolumeGraph));
    }

    @Test
    public void givenEventsTimelineWhenSetEventsTimelineThenVerifyResult() {
        // Act
        entityBriefInfo.setEventsTimeline(mgmtChangeEvents);
        // Assert
        errorCollector.checkThat(entityBriefInfo.getEventsTimeline(), is(mgmtChangeEvents));
    }

    @Test
    public void givenMgmtTurnoverDataWhenSetMgmtTurnoverDataThenVerifyResult() {
        // Act
        entityBriefInfo.setMgmtTurnoverData(mgmtTurnoverData);
        // Assert
        errorCollector.checkThat(entityBriefInfo.getMgmtTurnoverData(), is(mgmtTurnoverData));
    }

    @Test
    public void givenVisualizationDataWhenSetVisualizationDataThenVerifyResult() {
        // Act
        entityBriefInfo.setVisualizationData(visualizationData);
        // Assert
        errorCollector.checkThat(entityBriefInfo.getVisualizationData(), is(visualizationData));
    }

    @Test
    public void givenEntityWhenSetEntityThenVerifyResult() {
        // Act
        entityBriefInfo.setEntity(entity);
        // Assert
        errorCollector.checkThat(entityBriefInfo.getEntity(), is(entity));
    }

    @Test
    public void givenMatchedEntityWhenSetMatchedEntityThenVerifyResult() {
        // Act
        entityBriefInfo.setMatchedEntity(Collections.singletonList(entity));
        // Assert
        errorCollector.checkThat(entityBriefInfo.getMatchedEntity(), is(Collections.singletonList(entity)));
    }

    @Test
    public void givenStatusCodeWhenSetStatusCodeThenVerifyResult() {
        // Act
        entityBriefInfo.setStatusCode(2);
        // Assert
        errorCollector.checkThat(entityBriefInfo.getStatusCode(), is(2));
    }

    @Test
    public void givenBaseSetAndSwitchOnLine166HRWhenUpdatePrepThenVerifyResult() {
        // Arrange
        webResults.setSectionType(BaseSet.SectionType.HR);
        // Act
        entityBriefInfo.updatePrep(webResults);
        // Assert
        errorCollector.checkThat(entityBriefInfo.getWebResults(), is(webResults));
    }

    @Test
    public void givenBaseSetSectionTypeFRWhenUpdatePrepThenVerifyResult() {
        // Arrange
        webResults.setSectionType(BaseSet.SectionType.FR);
        // Act
        entityBriefInfo.updatePrep(webResults);
        // Assert
        errorCollector.checkThat(entityBriefInfo.getWebResults(), is(webResults));
    }

    @Test
    public void givenBaseSetSectionTypeFTWhenUpdatePrepThenVerifyResult() {
        // Arrange
        tweetList.setSectionType(BaseSet.SectionType.FT);
        // Act
        entityBriefInfo.updatePrep(tweetList);
        // Assert
        errorCollector.checkThat(entityBriefInfo.getTweetList(), is(tweetList));
    }

    @Test
    public void givenBaseSetSectionTypeACWhenUpdatePrepThenVerifyResult() {
        // Arrange
        webResults.setSectionType(BaseSet.SectionType.AC);
        // Act
        entityBriefInfo.updatePrep(webResults);
        // Assert
        errorCollector.checkThat(entityBriefInfo.getAnalystComments(), is(webResults));
    }

    @Test
    public void givenBaseSetSectionTypeTEWhenUpdatePrepThenVerifyResult() {
        // Arrange
        mgmtChangeEvents.setSectionType(BaseSet.SectionType.TE);
        // Act
        entityBriefInfo.updatePrep(mgmtChangeEvents);
        // Assert
        errorCollector.checkThat(entityBriefInfo.getMgmtChangeEvents(), is(mgmtChangeEvents));
    }

    @Test
    public void givenBaseSetSectionTypeWVWhenUpdatePrepThenVerifyResult() {
        // Arrange
        webVolumeGraph.setSectionType(BaseSet.SectionType.WV);
        // Act
        entityBriefInfo.updatePrep(webVolumeGraph);
        // Assert
        errorCollector.checkThat(entityBriefInfo.getWebVolumeGraph(), is(webVolumeGraph));
    }

    @Test
    public void givenBaseSetSectionTypeEWhenUpdatePrepThenVerifyResult() {
        // Arrange
        mgmtChangeEvents.setSectionType(BaseSet.SectionType.E);
        // Act
        entityBriefInfo.updatePrep(mgmtChangeEvents);
        // Assert
        errorCollector.checkThat(entityBriefInfo.getEventsTimeline(), is(mgmtChangeEvents));
    }

    @Test
    public void givenBaseSetSectionTypeMTCWhenUpdatePrepThenVerifyResult() {
        // Arrange
        mgmtTurnoverData.setSectionType(BaseSet.SectionType.MTC);
        // Act
        entityBriefInfo.updatePrep(mgmtTurnoverData);
        // Assert
        errorCollector.checkThat(entityBriefInfo.getMgmtTurnoverData(), is(mgmtTurnoverData));
    }

    @Test
    public void givenBaseSetSectionTypeVIZWhenUpdatePrepThenVerifyResult() {
        // Arrange
        visualizationData.setSectionType(BaseSet.SectionType.VIZ);
        // Act
        entityBriefInfo.updatePrep(visualizationData);
        // Assert
        errorCollector.checkThat(entityBriefInfo.getVisualizationData(), is(visualizationData));
    }

    @Test
    public void givenObjWhenSetPerfStatsThenVerifyResult() {
        // Arrange
        PerfRequestEntry entryPre = mock(PerfRequestEntry.class);
        baseSet.setStat(entryPre);
        // Act
        entityBriefInfo.setPerfStats(baseSet);
        // Assert
        errorCollector.checkThat(baseSet.getSectionType(), nullValue());
    }

    @Test
    public void givenEntityMapWhenSetEntityMapThenVerifyResult() {
        // Act
        entityBriefInfo.setEntityMap(entityMap);
        // Assert
        errorCollector.checkThat(entityBriefInfo.getEntityMap(), is(entityMap));
    }

    @Test
    public void givenScopeDirectiveWhenSetScopeDirectiveThenVerifyResult() {
        // Act
        entityBriefInfo.setScopeDirective("scopeDirective");
        // Assert
        errorCollector.checkThat(entityBriefInfo.getScopeDirective(), is("scopeDirective"));
    }
}
