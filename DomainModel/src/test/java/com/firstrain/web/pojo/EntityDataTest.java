package com.firstrain.web.pojo;

import com.firstrain.frapi.domain.MonitorEmail;
import com.firstrain.web.wrapper.ChartDataWrapper;
import com.firstrain.web.wrapper.EntityMap;
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
public class EntityDataTest {

    private Content content;
    private WebVolumeGraph webVolumeGraph;
    private ChartDataWrapper chartDataWrapper;
    private EmailDetail emailDetail;
    private EntityMap entityMap;
    private MonitorEmail monitorEmail;
    private Document document;
    private EntityStandard entityStandard;

    @InjectMocks
    private EntityData entityData;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Before
    public void setUp() {
        emailDetail = new EmailDetail();
        content = new Content();
        emailDetail = new EmailDetail();
        entityMap = new EntityMap();
        entityStandard = new EntityStandard();
        document = new Document();
        webVolumeGraph = new WebVolumeGraph();
        chartDataWrapper = new ChartDataWrapper();
        monitorEmail = new MonitorEmail();
    }

    @Test
    public void givenEmailTemplateWhenSetEmailTemplateThenVerifyResult() {
        // Act
        entityData.setEmailTemplate("emailTemplate");
        //Assert
        errorCollector.checkThat(entityData.getEmailTemplate(), is("emailTemplate"));
    }

    @Test
    public void givenEmailDetailWhenSetEmailDetailThenVerifyResult() {
        // Act
        entityData.setEmailDetail(emailDetail);
        //Assert
        errorCollector.checkThat(entityData.getEmailDetail(), is(emailDetail));
    }

    @Test
    public void givenEmailsWhenSetEmailsThenVerifyResult() {
        // Act
        entityData.setEmails(Collections.singletonList(monitorEmail));
        //Assert
        errorCollector.checkThat(entityData.getEmails(), is(Collections.singletonList(monitorEmail)));
    }

    @Test
    public void givenFrWhenSetFrThenVerifyResult() {
        // Act
        entityData.setFr(content);
        //Assert
        errorCollector.checkThat(entityData.getFr(), is(content));
    }

    @Test
    public void givenFtWhenSetFtThenVerifyResult() {
        // Act
        entityData.setFt(content);
        //Assert
        errorCollector.checkThat(entityData.getFt(), is(content));
    }

    @Test
    public void givenTwtWhenSetTwtThenVerifyResult() {
        // Act
        entityData.setTwt(chartDataWrapper);
        //Assert
        errorCollector.checkThat(entityData.getTwt(), is(chartDataWrapper));
    }

    @Test
    public void givenBiWhenSetBiThenVerifyResult() {
        // Act
        entityData.setBi(chartDataWrapper);
        //Assert
        errorCollector.checkThat(entityData.getBi(), is(chartDataWrapper));
    }

    @Test
    public void givenMdWhenSetMdThenVerifyResult() {
        // Act
        entityData.setMd(chartDataWrapper);
        //Assert
        errorCollector.checkThat(entityData.getMd(), is(chartDataWrapper));
    }

    @Test
    public void givenGlWhenSetGlThenVerifyResult() {
        // Act
        entityData.setGl(chartDataWrapper);
        //Assert
        errorCollector.checkThat(entityData.getGl(), is(chartDataWrapper));
    }

    @Test
    public void givenRlWhenSetRlThenVerifyResult() {
        // Act
        entityData.setRl(chartDataWrapper);
        //Assert
        errorCollector.checkThat(entityData.getRl(), is(chartDataWrapper));
    }

    @Test
    public void givenTtWhenSetTtThenVerifyResult() {
        // Act
        entityData.setTt(chartDataWrapper);
        //Assert
        errorCollector.checkThat(entityData.getTt(), is(chartDataWrapper));
    }

    @Test
    public void givenAcWhenSetAcThenVerifyResult() {
        // Act
        entityData.setAc(content);
        //Assert
        errorCollector.checkThat(entityData.getAc(), is(content));
    }

    @Test
    public void givenEWhenSetEThenVerifyResult() {
        // Act
        entityData.setE(content);
        //Assert
        errorCollector.checkThat(entityData.getE(), is(content));
    }

    @Test
    public void givenTeWhenSetTeThenVerifyResult() {
        // Act
        entityData.setTe(content);
        //Assert
        errorCollector.checkThat(entityData.getTe(), is(content));
    }

    @Test
    public void givenCsWhenSetCsThenVerifyResult() {
        // Act
        entityData.setCs(content);
        //Assert
        errorCollector.checkThat(entityData.getCs(), is(content));
    }

    @Test
    public void givenWvWhenSetWvThenVerifyResult() {
        // Act
        entityData.setWv(webVolumeGraph);
        //Assert
        errorCollector.checkThat(entityData.getWv(), is(webVolumeGraph));
    }

    @Test
    public void givenBucketedEventsWhenSetBucketedEventsThenVerifyResult() {
        // Act
        entityData.setBucketedEvents(content);
        //Assert
        errorCollector.checkThat(entityData.getBucketedEvents(), is(content));
    }

    @Test
    public void givenTotalItemCountWhenSetTotalItemCountThenVerifyResult() {
        // Act
        entityData.setTotalItemCount(5);
        //Assert
        errorCollector.checkThat(entityData.getTotalItemCount(), is(5));
    }

    @Test
    public void givenItemOffsetWhenSetItemOffsetThenVerifyResult() {
        // Act
        entityData.setItemOffset(6);
        //Assert
        errorCollector.checkThat(entityData.getItemOffset(), is(6));
    }

    @Test
    public void givenItemCountWhenSetItemCountThenVerifyResult() {
        // Act
        entityData.setItemCount(7);
        //Assert
        errorCollector.checkThat(entityData.getItemCount(), is(7));
    }

    @Test
    public void givenConversationStartersWhenSetConversationStartersThenVerifyResult() {
        // Act
        entityData.setConversationStarters(Collections.singletonList(document));
        //Assert
        errorCollector.checkThat(entityData.getConversationStarters(), is(Collections.singletonList(document)));
    }

    @Test
    public void givenEntityMapWhenSetEntityMapThenVerifyResult() {
        // Act
        entityData.setEntityMap(entityMap);
        //Assert
        errorCollector.checkThat(entityData.getEntityMap(), is(entityMap));
    }

    @Test
    public void givenEntityWhenSetEntityThenVerifyResult() {
        // Act
        entityData.setEntity(Collections.singletonList(entityStandard));
        //Assert
        errorCollector.checkThat(entityData.getEntity(), is(Collections.singletonList(entityStandard)));
    }

    @Test
    public void givenPeersWhenSetPeersThenVerifyResult() {
        // Act
        entityData.setPeers(Collections.singletonList(entityStandard));
        //Assert
        errorCollector.checkThat(entityData.getPeers(), is(Collections.singletonList(entityStandard)));
    }

    @Test
    public void givenScopeDirectiveWhenSetScopeDirectiveThenVerifyResult() {
        // Act
        entityData.setScopeDirective("scopeDirective");
        //Assert
        errorCollector.checkThat(entityData.getScopeDirective(), is("scopeDirective"));
    }
}
