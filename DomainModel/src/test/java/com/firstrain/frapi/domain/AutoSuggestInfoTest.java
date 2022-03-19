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
public class AutoSuggestInfoTest {
    @InjectMocks
    private AutoSuggestInfo autoSuggestInfo;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Test
    public void givenSynonymWhenSetSynonymThenVerifyResult() {
        // Act
        autoSuggestInfo.setSynonym("synonym");
        // Assert
        errorCollector.checkThat(autoSuggestInfo.getSynonym(), is("synonym"));
    }

    @Test
    public void givenSearchTokenWhenSetSearchTokenThenVerifyResult() {
        // Act
        autoSuggestInfo.setSearchToken("searchToken");
        // Assert
        errorCollector.checkThat(autoSuggestInfo.getSearchToken(), is("searchToken"));
    }

    @Test
    public void givenDisplaySynonymWhenSetDisplaySynonymThenVerifyResult() {
        // Act
        autoSuggestInfo.setDisplaySynonym("displaySynonym");
        // Assert
        errorCollector.checkThat(autoSuggestInfo.getDisplaySynonym(), is("displaySynonym"));
    }

    @Test
    public void givenDisplayCikWhenSetDisplayCikThenVerifyResult() {
        // Act
        autoSuggestInfo.setDisplayCik("displayCik");
        // Assert
        errorCollector.checkThat(autoSuggestInfo.getDisplayCik(), is("displayCik"));
    }

    @Test
    public void givenDisplaySedolWhenSetDisplaySedolThenVerifyResult() {
        // Act
        autoSuggestInfo.setDisplaySedol("displaySedol");
        // Assert
        errorCollector.checkThat(autoSuggestInfo.getDisplaySedol(), is("displaySedol"));
    }

    @Test
    public void givenDisplayIsinWhenSetDisplayIsinThenVerifyResult() {
        // Act
        autoSuggestInfo.setDisplayIsin("displayIsin");
        // Assert
        errorCollector.checkThat(autoSuggestInfo.getDisplayIsin(), is("displayIsin"));
    }

    @Test
    public void givenNameWhenSetNameThenVerifyResult() {
        // Act
        autoSuggestInfo.setName("name");
        // Assert
        errorCollector.checkThat(autoSuggestInfo.getName(), is("name"));
    }

    @Test
    public void givenTypeWhenSetTypeThenVerifyResult() {
        // Act
        autoSuggestInfo.setType("type");
        // Assert
        errorCollector.checkThat(autoSuggestInfo.getType(), is("type"));
    }

    @Test
    public void givenDisplayNameWhenSetDisplayNameThenVerifyResult() {
        // Act
        autoSuggestInfo.setDisplayName("displayName");
        // Assert
        errorCollector.checkThat(autoSuggestInfo.getDisplayName(), is("displayName"));
    }

    @Test
    public void givenReplaceOffsetWhenSetReplaceOffsetThenVerifyResult() {
        // Act
        autoSuggestInfo.setReplaceOffset(5);
        // Assert
        errorCollector.checkThat(autoSuggestInfo.getReplaceOffset(), is(5));
    }

    @Test
    public void givenReplaceLengthWhenSetReplaceLengthThenVerifyResult() {
        // Act
        autoSuggestInfo.setReplaceLength(8);
        // Assert
        errorCollector.checkThat(autoSuggestInfo.getReplaceLength(), is(8));
    }

    @Test
    public void givenDisplayTickerWhenSetDisplayTickerThenVerifyResult() {
        // Act
        autoSuggestInfo.setDisplayTicker("displayTicker");
        // Assert
        errorCollector.checkThat(autoSuggestInfo.getDisplayTicker(), is("displayTicker"));
    }

    @Test
    public void givenCompanyIdWhenSetCompanyIdThenVerifyResult() {
        // Act
        autoSuggestInfo.setCompanyId(8);
        // Assert
        errorCollector.checkThat(autoSuggestInfo.getCompanyId(), is(8));
    }

    @Test
    public void givenNameHWhenSetNameHThenVerifyResult() {
        // Act
        autoSuggestInfo.setNameH("nameH");
        // Assert
        errorCollector.checkThat(autoSuggestInfo.getNameH(), is("nameH"));
    }

    @Test
    public void givenTickerHWhenSetTickerHThenVerifyResult() {
        // Act
        autoSuggestInfo.setTickerH("tickerH");
        // Assert
        errorCollector.checkThat(autoSuggestInfo.getTickerH(), is("tickerH"));
    }

    @Test
    public void givenCatIdWhenSetCatIdThenVerifyResult() {
        // Act
        autoSuggestInfo.setCatId("catId");
        // Assert
        errorCollector.checkThat(autoSuggestInfo.getCatId(), is("catId"));
    }

    @Test
    public void givenDocCountWhenSetDocCountThenVerifyResult() {
        // Act
        autoSuggestInfo.setDocCount(8);
        // Assert
        errorCollector.checkThat(autoSuggestInfo.getDocCount(), is(8));
    }
}
