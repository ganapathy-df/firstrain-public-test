package com.firstrain.frapi.domain;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;

import java.math.RoundingMode;
import java.text.NumberFormat;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;

@RunWith(MockitoJUnitRunner.class)
public class StockPriceMetaTest {
    @InjectMocks
    private StockPriceMeta stockPriceMeta;

    private final ErrorCollector errorCollector = new ErrorCollector();
    private final ExpectedException thrown = ExpectedException.none();

    @Rule
    public final RuleChain chain = RuleChain.outerRule(errorCollector).around(thrown);

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    private static final String AMT_50K = "50000";
    private static final String AMT_47K = "47000";
    private static final String PCT_5 = "5%";
    private static final String PCT_MINUS_5 = "-5%";
    private static final String MSG_PARM_REQUIRED = "Stock closing price";
    private static final String VALUE_N =
            "Stock closing price ($19.00) DROPPED BELOW 50-day Moving Average ($198.00)";
    private static final String VALUE_1 =
            "Stock price <span class='status-callout'><span class='stock-status--down'>CLOSED DOWN ";
    private static final String VALUE_2 = "-5.00%</span></span> at $50,000.00";
    private static final String VALUE_3 =
            "Stock price <span style='text-transform:uppercase;font-size:90%'><span style='";
    private static final String VALUE_4 = "color:#e82d2d'>CLOSED DOWN -5.00%</span></span> at $50,000.00";
    private static final String VALUE_5 = "Stock price CLOSED DOWN -5.00% at $50,000.00";

    @Before
    public void setUp() {
        stockPriceMeta.setAvg50Day("50");
        stockPriceMeta.setAvg52Week("52");
        stockPriceMeta.setAvg100Day("100");
        stockPriceMeta.setAvg200Day("200");
        stockPriceMeta.setPercentChange("200");
        stockPriceMeta.setClosingPrice("122");
        stockPriceMeta.setPreviousClosingPrice("122");
    }

    @Test
    public void givenOpeningPriceWhenSetOpeningPriceThenVerifyResult() {
        // Act
        stockPriceMeta.setOpeningPrice("openingPrice");
        // Assert
        errorCollector.checkThat(stockPriceMeta.getOpeningPrice(), is("openingPrice"));
    }

    @Test
    public void givenClosingPriceWhenSetClosingPriceThenVerifyResult() {
        // Act
        stockPriceMeta.setClosingPrice("closingPrice");
        // Assert
        errorCollector.checkThat(stockPriceMeta.getClosingPrice(), is("closingPrice"));
    }

    @Test
    public void givenPreviousClosingPriceWhenSetPreviousClosingPriceThenVerifyResult() {
        // Act
        stockPriceMeta.setPreviousClosingPrice("previousClosingPrice");
        // Assert
        errorCollector.checkThat(stockPriceMeta.getPreviousClosingPrice(), is("previousClosingPrice"));
    }

    @Test
    public void givenMinPriceWhenSetMinPriceThenVerifyResult() {
        // Act
        stockPriceMeta.setMinPrice("minPrice");
        // Assert
        errorCollector.checkThat(stockPriceMeta.getMinPrice(), is("minPrice"));
    }

    @Test
    public void givenMaxPriceWhenSetMaxPriceThenVerifyResult() {
        // Act
        stockPriceMeta.setMaxPrice("maxPrice");
        // Assert
        errorCollector.checkThat(stockPriceMeta.getMaxPrice(), is("maxPrice"));
    }

    @Test
    public void givenTradingVolumeWhenSetTradingVolumeThenVerifyResult() {
        // Act
        stockPriceMeta.setTradingVolume("tradingVolume");
        // Assert
        errorCollector.checkThat(stockPriceMeta.getTradingVolume(), is("tradingVolume"));
    }

    @Test
    public void givenPercentChangeWhenSetPercentChangeThenVerifyResult() {
        // Act
        stockPriceMeta.setPercentChange("percentChange");
        // Assert
        errorCollector.checkThat(stockPriceMeta.getPercentChange(), is("percentChange"));
    }

    @Test
    public void givenAvg50DayWhenSetAvg50DayThenVerifyResult() {
        // Act
        stockPriceMeta.setAvg50Day("avg50Day");
        // Assert
        errorCollector.checkThat(stockPriceMeta.getAvg50Day(), is("avg50Day"));
    }

    @Test
    public void givenAvg100DayWhenSetAvg100DayThenVerifyResult() {
        // Act
        stockPriceMeta.setAvg100Day("avg100Day");
        // Assert
        errorCollector.checkThat(stockPriceMeta.getAvg100Day(), is("avg100Day"));
    }

    @Test
    public void givenAvg200DayWhenSetAvg200DayThenVerifyResult() {
        // Act
        stockPriceMeta.setAvg200Day("avg200Day");
        // Assert
        errorCollector.checkThat(stockPriceMeta.getAvg200Day(), is("avg200Day"));
    }

    @Test
    public void givenAvg52WeekWhenSetAvg52WeekThenVerifyResult() {
        // Act
        stockPriceMeta.setAvg52Week("avg52Week");
        // Assert
        errorCollector.checkThat(stockPriceMeta.getAvg52Week(), is("avg52Week"));
    }

    @Test
    public void givenValueWhenGettingPriceChangeNegativeThenValidate() {
        // Act
        String result = stockPriceMeta.getPriceChange();

        // Assert
        errorCollector.checkThat(result.length() > 0, is(true));
    }

    @Test
    public void givenEventTypeOutOfRangeGettingTitleResultsPriceContra() {
        // Act
        String resultHtml = stockPriceMeta.getSPTitle(200, false);

        // Assert
        errorCollector.checkThat(resultHtml, is("Stock Price: Stock Closing Price Contra to Industry Composite by >5%"));
        errorCollector.checkThat(stockPriceMeta.getSPTitle(299, true), is
                ("Stock Price: Stock Closing Price Contra to Industry Composite by >5%"));
        errorCollector.checkThat(stockPriceMeta.getSPTitleWithoutHtml(305),
                is("Stock Price: Stock Closing Price Contra to Industry Composite by >5%"));
    }

    @Test
    public void givenNoClosingPriceGettingTitleReturnsMissingParameter() {
        // Act
        String resultHtml = stockPriceMeta.getSPTitle(300, false);

        // Assert
        errorCollector.checkThat(resultHtml,
                is("Stock price <span class='status-callout'><span class='stock-status--up'>" +
                        " CLOSED UP +200.00%</span></span> at $122.00"));
        errorCollector.checkThat(stockPriceMeta.getSPTitle(301, true), containsString(MSG_PARM_REQUIRED));
        errorCollector.checkThat(stockPriceMeta.getSPTitleWithoutHtml(302), containsString(MSG_PARM_REQUIRED));
    }

    @Test
    public void givenNoPercentChangeAndEventType300GettingTitleReturnsMissingParameter() {
        // Arrange
        stockPriceMeta.setClosingPrice(AMT_50K);

        // Act
        String resultHtml = stockPriceMeta.getSPTitle(300, false);

        // Assert
        errorCollector.checkThat(resultHtml,
                is("Stock price <span class='status-callout'><span class='stock-status--up'>" +
                        " CLOSED UP +200.00%</span></span> at $50,000.00"));
        errorCollector.checkThat(stockPriceMeta.getSPTitle(300, true),
                is("Stock price <span style='text-transform:uppercase;font-size:90%'><span style='color:#32932d'>" +
                        " CLOSED UP +200.00%</span></span> at $50,000.00"));
        errorCollector.checkThat(stockPriceMeta.getSPTitleWithoutHtml(300),
                is("Stock price CLOSED UP +200.00% at $50,000.00"));
    }

    @Test
    public void givenEventType300MinusPercentChangeGettingTitleReturnsClosedDown() {
        // Arrange
        String resultHtml = populateStockPriceMetaAndDoSPTitle(PCT_MINUS_5);

        // Assert
        errorCollector.checkThat(resultHtml,
                is(VALUE_1 + VALUE_2));
        errorCollector.checkThat(stockPriceMeta.getSPTitle(300, true),
                is(VALUE_3 + VALUE_4));
        errorCollector.checkThat(stockPriceMeta.getSPTitleWithoutHtml(300),
                is(VALUE_5));
    }

    @Test
    public void givenEventType300PlusPercentChangeGettingTitleReturnsClosedUp() {
        // Arrange
        String resultHtml = populateStockPriceMetaAndDoSPTitle(PCT_5);

        // Assert
        errorCollector.checkThat(resultHtml,
                is("Stock price <span class='status-callout'><span class='stock-status--up'>" +
                        " CLOSED UP +5.00%</span></span> at $50,000.00"));
        errorCollector.checkThat(stockPriceMeta.getSPTitle(300, true),
                is(VALUE_3 + "color:#32932d'>" + " CLOSED UP +5.00%</span></span> at $50,000.00"));
        errorCollector.checkThat(stockPriceMeta.getSPTitleWithoutHtml(300),
                is("Stock price CLOSED UP +5.00% at $50,000.00"));
    }

    private String populateStockPriceMetaAndDoSPTitle(final String PCT_5) {
        stockPriceMeta.setClosingPrice(AMT_50K);
        stockPriceMeta.setPercentChange(PCT_5);
        
        // Act
        String resultHtml = stockPriceMeta.getSPTitle(300, false);
        return resultHtml;
    }

    @Test
    public void givenNo50DayAvgAndEventType301GettingTitleReturnsMissingParameter() {
        // Arrange
        stockPriceMeta.setClosingPrice(AMT_50K);

        // Act
        String resultHtml = stockPriceMeta.getSPTitle(301, false);

        // Assert
        errorCollector.checkThat(resultHtml,
                is("Stock closing price <span class='status-callout'>($50,000.00) <span class='stock-status--up" +
                        "'>ROSE ABOVE</span> 50-day Moving Average</span> ($50.00)"));
        errorCollector.checkThat(stockPriceMeta.getSPTitle(301, true),
                is("Stock closing price <span style='text-transform:uppercase;font-size:90%'>($50,000.00) " +
                        "<span style='color:#32932d'>ROSE ABOVE</span> 50-day Moving Average</span> ($50.00)"));
        errorCollector.checkThat(stockPriceMeta.getSPTitleWithoutHtml(301),
                is("Stock closing price ($50,000.00) ROSE ABOVE 50-day Moving Average ($50.00)"));
    }

    @Test
    public void givenNo100DayAvgAndEventType302GettingTitleReturnsMissingParameter() {
        // Arrange
        stockPriceMeta.setClosingPrice(AMT_50K);

        // Act
        String resultHtml = stockPriceMeta.getSPTitle(302, false);

        // Assert
        errorCollector.checkThat(resultHtml,
                is("Stock closing price <span class='status-callout'>($50,000.00) <span class='stock-status--up'" +
                        ">ROSE ABOVE</span> 100-day Moving Average</span> ($100.00)"));
        errorCollector.checkThat(stockPriceMeta.getSPTitle(302, true),
                is("Stock closing price <span style='text-transform:uppercase;font-size:90%'>($50,000.00) " +
                        "<span style='color:#32932d'>ROSE ABOVE</span> 100-day Moving Average</span> ($100.00)"));
        errorCollector.checkThat(stockPriceMeta.getSPTitleWithoutHtml(302),
                is("Stock closing price ($50,000.00) ROSE ABOVE 100-day Moving Average ($100.00)"));
    }

    @Test
    public void givenNo200DayAvgAndEventType303GettingTitleReturnsMissingParameter() {
        // Arrange
        stockPriceMeta.setClosingPrice(AMT_50K);

        // Act
        String actual = stockPriceMeta.getSPTitle(303, false);

        // Assert
        errorCollector.checkThat(actual,
                is("Stock closing price <span class='status-callout'>($50,000.00) <span class='stock-status--up" +
                        "'>ROSE ABOVE</span> 200-day Moving Average</span> ($200.00)"));
        errorCollector.checkThat(stockPriceMeta.getSPTitle(303, true),
                is("Stock closing price <span style='text-transform:uppercase;font-size:90%'>($50,000.00) <span" +
                        " style='color:#32932d'>ROSE ABOVE</span> 200-day Moving Average</span> ($200.00)"));
        errorCollector.checkThat(stockPriceMeta.getSPTitleWithoutHtml(303),
                is("Stock closing price ($50,000.00) ROSE ABOVE 200-day Moving Average ($200.00)"));
    }

    @Test
    public void givenNo52WeekAvgAndEventType304GettingTitleReturnsMissingParameter() {
        // Arrange
        stockPriceMeta.setClosingPrice(AMT_50K);

        // Act
        String actual = stockPriceMeta.getSPTitle(304, false);

        // Assert
        errorCollector.checkThat(actual, containsString(MSG_PARM_REQUIRED));
        errorCollector.checkThat(stockPriceMeta.getSPTitle(304, true), containsString(MSG_PARM_REQUIRED));
        errorCollector.checkThat(stockPriceMeta.getSPTitleWithoutHtml(304), containsString(MSG_PARM_REQUIRED));
    }

    @Test
    public void given50DayAvgEventType301AndPositiveChangeGettingTitleOk() {
        // Arrange
        stockPriceMeta.setClosingPrice(AMT_50K);

        // Act
        stockPriceMeta.setAvg50Day(AMT_47K);

        // Assert
        errorCollector.checkThat(stockPriceMeta.getSPTitle(301, false),
                is("Stock closing price <span class='status-callout'>($50,000.00) <span class='stock-status--up'>" +
                        "ROSE ABOVE</span> 50-day Moving Average</span> ($47,000.00)"));
        errorCollector.checkThat(stockPriceMeta.getSPTitle(301, true),
                is("Stock closing price <span style='text-transform:uppercase;font-size:90%'>($50,000.00) " +
                        "<span style='color:#32932d'>ROSE ABOVE</span> 50-day Moving Average</span> ($47,000.00)"));
        errorCollector.checkThat(stockPriceMeta.getSPTitleWithoutHtml(301),
                is("Stock closing price ($50,000.00) ROSE ABOVE 50-day Moving Average ($47,000.00)"));
    }

    @Test
    public void given50DayWhenGetSPTitleThenValidate() {
        // Arrange
        stockPriceMeta.setClosingPrice("19");
        stockPriceMeta.setAvg50Day("198");

        // Act
        String actual = stockPriceMeta.getSPTitle(301, true);

        // Assert
        errorCollector.checkThat(actual,
                is("Stock closing price <span style='text-transform:uppercase;font-size:90%'>($19.00)" +
                        " <span style='color:#e82d2d'>DROPPED BELOW</span> 50-day Moving Average</span> ($198.00)"));
    }

    @Test
    public void given50DayWhenGetSPTitleWithoutHtmlThenValidate() {
        // Arrange
        stockPriceMeta.setClosingPrice("19");
        stockPriceMeta.setAvg50Day("198");

        // Act
        String actual = stockPriceMeta.getSPTitleWithoutHtml(301);

        // Assert
        errorCollector.checkThat(actual, is(VALUE_N));
    }

    @Test
    public void given50DayWhenGetSPTitleWithoutHtmlThenException() {
        // Arrange
        stockPriceMeta.setAvg50Day("test");

        // Act
        stockPriceMeta.getSPTitleWithoutHtml(301);

        // Assert
        errorCollector.checkThat(stockPriceMeta.getAvg50Day(), is("test"));
    }

    @Test
    public void given300DayWhenGetSPTitleWithoutHtmlThenException() {
        // Arrange
        stockPriceMeta.setPercentChange("test");

        // Act
        stockPriceMeta.getSPTitleWithoutHtml(300);

        // Assert
        errorCollector.checkThat(stockPriceMeta.getPercentChange(), is("test"));
    }

    @Test
    public void givenPriceyWhenGetSPTitleWithoutHtmlThenException() {
        // Arrange
        stockPriceMeta.setClosingPrice("test");

        // Act
        stockPriceMeta.getSPTitleWithoutHtml(301);

        // Assert
        errorCollector.checkThat(stockPriceMeta.getClosingPrice(), is("test"));
    }

    @Test
    public void given50DayWhenGetSPTitleThenException() {
        // Arrange
        stockPriceMeta.setAvg50Day("test");

        // Act
        stockPriceMeta.getSPTitle(301, true);

        // Assert
        errorCollector.checkThat(stockPriceMeta.getAvg50Day(), is("test"));
    }

    @Test
    public void given300DayWhenGetSPTitleeThenException() {
        // Arrange
        stockPriceMeta.setPercentChange("test");

        // Act
        stockPriceMeta.getSPTitle(300, true);

        // Assert
        errorCollector.checkThat(stockPriceMeta.getPercentChange(), is("test"));
    }

    @Test
    public void givenPriceyWhenGetSPTitleeThenException() {
        // Arrange
        stockPriceMeta.setClosingPrice("test");

        // Act
        stockPriceMeta.getSPTitle(301, true);

        // Assert
        errorCollector.checkThat(stockPriceMeta.getClosingPrice(), is("test"));
    }

    @Test
    public void given50DayWhenGetNumberFormatterThenValidate() throws Exception {
        // Act
        NumberFormat actual = Whitebox.invokeMethod(stockPriceMeta, "getNumberFormatter");

        // Assert
        errorCollector.checkThat(actual.getMinimumFractionDigits(), is(2));
        errorCollector.checkThat(actual.getRoundingMode(), is(RoundingMode.HALF_UP));
    }
}
