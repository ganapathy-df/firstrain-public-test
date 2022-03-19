package com.firstrain.frapi.domain;

import com.firstrain.frapi.util.DefaultEnums.EventTypeEnum;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

@RunWith(MockitoJUnitRunner.class)
public class SecEventMetaTest {
    @InjectMocks
    private SecEventMeta secEventMeta;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Before
    public void setUp() {
        secEventMeta.setOriginalTitle("10-K/A,10-K/A");
    }

    @Test
    public void givenSecIdWhenSetSecIdThenVerifyResult() {
        // Act
        secEventMeta.setSecId("secId");
        // Assert
        errorCollector.checkThat(secEventMeta.getSecId(),is("secId"));
    }

    @Test
    public void givenOriginalTitleWhenSetOriginalTitleThenVerifyResult() {
        // Act
        secEventMeta.setOriginalTitle("originalTitle");
        // Assert
        errorCollector.checkThat(secEventMeta.getOriginalTitle(),is("originalTitle"));
    }

    @Test
    public void givenSecFormTypeWhenSetSecFormTypeThenVerifyResult() {
       // Act
        secEventMeta.setSecFormType("secFormType");
        // Assert
        errorCollector.checkThat(secEventMeta.getSecFormType(),is("secFormType"));
    }

    @Test
    public void givenEventTypeAndConditionOnLine70TrueWhenGetSeceTitleThenVerifyResult() {
        // Act
        final String result = secEventMeta.getSeceTitle(EventTypeEnum.TYPE_DELAYED_SEC_FILING.name());

        // Assert
        errorCollector.checkThat(result, equalTo("<span></span> <br/>SEC Delay: 10-K/A,10-K/A"));
    }

    @Test
    public void givenEventTypeWhenGetSeceTitleThenVerifyResult() {
        // Act
        final String result = secEventMeta.getSeceTitle(EventTypeEnum.TYPE_TEN_K_EVENT.name());

        // Assert
        errorCollector.checkThat(result, equalTo("Amended the <strong>Annual Report</strong> for FY 10-K/A"));
    }

    @Test
    public void givenEventTypeEventWhenGetSeceTitleThenVerifyResult() {
        // Arrange
        secEventMeta.setOriginalTitle("10-Q10-Q,10-Q,10-Q");

        // Act
        final String result = secEventMeta.getSeceTitle(EventTypeEnum.TYPE_TEN_Q_EVENT.name());

        // Assert
        errorCollector.checkThat(result, equalTo("Filed a <strong>Quarterly Report</strong> for 10-Q, FY 10-Q"));
    }

    @Test
    public void givenEventTypeAndConditionOnLine70TrueWhenGetSeceTitleWithoutHtmlThenVerifyResult() {
        // Act
        final String result = secEventMeta.getSeceTitleWithoutHtml(EventTypeEnum.TYPE_DELAYED_SEC_FILING.name());

        // Assert
        errorCollector.checkThat(result, equalTo("Delayed SEC Filings : 10-K/A,10-K/A"));
    }

    @Test
    public void givenEventTypeTrueWhenGetSecTitleWithoutHtmlThenVerifyResult() {
        // Act
        final String result = secEventMeta.getSecTitleWithoutHtml();

        // Assert
        errorCollector.checkThat(result, equalTo(""));
    }

    @Test
    public void givenEventTypeTrueWhenGetSecTitleThenVerifyResult() {
        // Act
        final String result = secEventMeta.getSecTitle();

        // Assert
        errorCollector.checkThat(result, equalTo(""));
    }

    @Test
    public void givenEventTypeTimeWhenGetSeceTitleThenVerifyResult() {
        // Arrange
        secEventMeta.setOriginalTitle("8-K/A,8-K/A");

        // Act
        final String result = secEventMeta.getSeceTitle(EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_1_01.name());

        // Assert
        errorCollector.checkThat(result, equalTo
                ("Amended the <strong>8K Statement</strong> on Entry into a Material Definitive Agreement"));
    }


    @Test
    public void givenEventTypeWhenGetSecDelayedTitleWithoutHtmlThenVerifyResult() {
        // Act
        final String result = secEventMeta.getSeceTitleWithoutHtml(EventTypeEnum.TYPE_TEN_K_EVENT.name());

        // Assert
        errorCollector.checkThat(result, equalTo("Amended the Annual Report for FY 10-K/A"));
    }

    @Test
    public void givenEventTypeEventWhenGetSecDelayedTitleWithoutHtmlThenVerifyResult() {
        // Arrange
        testGetSeceTitleWithoutHtml("10-Q10-Q,10-Q,10-Q", "Filed a Quarterly Report for 10-Q, FY 10-Q", EventTypeEnum.TYPE_TEN_Q_EVENT);
    }

    @Test
    public void givenEventTypeTimeWhenGetSecDelayedTitleWithoutHtmlThenVerifyResult() {
        // Arrange
        testGetSeceTitleWithoutHtml("8-K/A,8-K/A", "Amended the 8K Statement on Entry into a Material Definitive Agreement", EventTypeEnum.TYPE_EIGHT_K_EVENT_ITEM_1_01);
    }

    private void testGetSeceTitleWithoutHtml(final String title, final String expected, final EventTypeEnum eventTypeEnum) {
        secEventMeta.setOriginalTitle(title);
        
        // Act
        final String result = secEventMeta.getSeceTitleWithoutHtml(eventTypeEnum.name());
        
        // Assert
        errorCollector.checkThat(result, equalTo(
                expected));
    }

    @Test
    public void givenEventTypeTimeWhenGetSecTitleThenVerifyResult() throws Exception {
        // Act
        final String result = Whitebox.invokeMethod(secEventMeta,"getSecTitle",
                "8-K/A,8-K/A:8-K/A,8-K/A","10-K");

        // Assert
        errorCollector.checkThat(result, equalTo("Filed an <strong>Annual Report</strong> for FY 8-K/A"));
    }

    @Test
    public void givenEventType10QWhenGetSecTitleThenVerifyResult() throws Exception {
        // Act
        final String result = Whitebox.invokeMethod(secEventMeta,"getSecTitle",
                "8-K/A,8-K/A:8-K/A,8-K/A,8-K/A","10-Q/A");

        // Assert
        errorCollector.checkThat(result, equalTo
                ("Amended the <strong>Quarterly Report</strong> for 8-K/A, FY 8-K/A"));
    }

    @Test
    public void givenEventType8KWhenGetSecTitleThenVerifyResult() throws Exception {
        // Act
        final String result = Whitebox.invokeMethod(secEventMeta,"getSecTitle",
                "8-K/A,8-K/A:8-K/A,8-K/A,8-K/A","8-K");

        // Assert
        errorCollector.checkThat(result, equalTo("Filed an <strong>8K Statement</strong> on 8-K/A, 8-K/A"));
    }

    @Test
    public void givenStringNull8KWhenGetSecTitleThenVerifyResult() throws Exception {
        // Act
        final String result = Whitebox.invokeMethod(secEventMeta,"getSecTitle",
                "8-K/A","8-K");

        // Assert
        errorCollector.checkThat(result, equalTo("Filed an <strong>8K Statement</strong> on 8-K/A"));
    }

    @Test
    public void givenStringNull8KAWhenGetSecTitleThenVerifyResult() throws Exception {
        // Act
        final String result = Whitebox.invokeMethod(secEventMeta,"getSecTitle",
                "8-K/A,8-K/A:8-K/A,8-K/A,8-K/A","8-K/A");

        // Assert
        errorCollector.checkThat(result, equalTo("Amended the <strong>8K Statement</strong> on 8-K/A, 8-K/A"));
    }

    @Test
    public void givenEventTypeTimeWhenGetSecTitleWithoutHtmlThenVerifyResult() throws Exception {
        // Act
        final String result = Whitebox.invokeMethod(secEventMeta,"getSecTitleWithoutHtml",
                "8-K/A,8-K/A:8-K/A,8-K/A","10-K");

        // Assert
        errorCollector.checkThat(result, equalTo("Filed an Annual Report for FY 8-K/A"));
    }

    @Test
    public void givenEventType10QWhenGetSecTitleWithoutHtmlThenVerifyResult() throws Exception {
        // Act
        final String result = Whitebox.invokeMethod(secEventMeta,"getSecTitleWithoutHtml",
                "8-K/A,8-K/A:8-K/A,8-K/A,8-K/A","10-Q/A");

        // Assert
        errorCollector.checkThat(result, equalTo("Amended the Quarterly Report for 8-K/A, FY 8-K/A"));
    }

    @Test
    public void givenEventType8KWhenGetSecTitleWithoutHtmlThenVerifyResult() throws Exception {
        // Act
        final String result = Whitebox.invokeMethod(secEventMeta,"getSecTitleWithoutHtml",
                "8-K/A,8-K/A:8-K/A,8-K/A,8-K/A","8-K");

        // Assert
        errorCollector.checkThat(result, equalTo("Filed an 8K Statement on 8-K/A, 8-K/A"));
    }

    @Test
    public void givenStringNull8KWhenGetSecTitleWithoutHtmlThenVerifyResult() throws Exception {
        // Act
        final String result = Whitebox.invokeMethod(secEventMeta,"getSecTitleWithoutHtml",
                "8-K/A","8-K");

        // Assert
        errorCollector.checkThat(result, equalTo("Filed an 8K Statement on 8-K/A"));
    }

    @Test
    public void givenStringNull8KAWhenGetSecTitleWithoutHtmlThenVerifyResult() throws Exception {
        // Act
        final String result = Whitebox.invokeMethod(secEventMeta,"getSecTitleWithoutHtml",
                "8-K/A,8-K/A:8-K/A,8-K/A,8-K/A","8-K/A");

        // Assert
        errorCollector.checkThat(result, equalTo("Amended the 8K Statement on 8-K/A, 8-K/A"));
    }

    @Test
    public void givenStringNull8KAWhenGetSecTitleWithoutHtmlThenException() throws Exception {
        // Act
        final String result = Whitebox.invokeMethod(secEventMeta,"getSecTitleWithoutHtml",
                "8-K/A","10-K");

        // Assert
        errorCollector.checkThat(result, equalTo("8-K/A"));
    }
}
