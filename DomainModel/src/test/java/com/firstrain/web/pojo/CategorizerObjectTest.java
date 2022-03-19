package com.firstrain.web.pojo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;

@RunWith(MockitoJUnitRunner.class)
public class CategorizerObjectTest {

    private CategorizerObject.CatEntityWrapper data;
    private CategorizerObject.CatEntity catEntity;
    private CategorizerObject.Attribute attribute;

    @InjectMocks
    private CategorizerObject categorizerObject;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Before
    public void setUp() {
        data = new CategorizerObject.CatEntityWrapper();
        catEntity = new CategorizerObject.CatEntity();
        attribute = new CategorizerObject.Attribute();
    }

    @Test
    public void givenResponseCodeWhenSetResponseCodeThenVerifyResult() {
        // Act
        categorizerObject.setResponseCode(3);
        //Assert
        errorCollector.checkThat(categorizerObject.getResponseCode(), is(3));
    }

    @Test
    public void givenCountWhenSetCountThenVerifyResult() {
        // Act
        categorizerObject.setCount(4);
        //Assert
        errorCollector.checkThat(categorizerObject.getCount(), is(4));
    }

    @Test
    public void givenDataWhenSetDataThenVerifyResult() {
        // Act
        categorizerObject.setData(data);
        //Assert
        errorCollector.checkThat(categorizerObject.getData(), is(data));
        errorCollector.checkThat(categorizerObject.toString(),
                containsString("JSONResponse [responseCode=0, count=0"));
    }

    @Test
    public void givenDataWhenSetDocIdThenVerifyResult() {
        // Act
        data.setDocId(3L);
        //Assert
        errorCollector.checkThat(data.getDocId(), is(3L));
    }

    @Test
    public void givenDataWhenSetCategorizerResponseThenVerifyResult() {
        // Act
        data.setCategorizerResponse(Collections.singletonList(catEntity));
        //Assert
        errorCollector.checkThat(data.getCategorizerResponse(), is(Collections.singletonList(catEntity)));
    }

    @Test
    public void givenDataWhenSetRuleThenVerifyResult() {
        // Act
        data.setRule("rule");
        //Assert
        errorCollector.checkThat(data.getRule(), is("rule"));
    }

    @Test
    public void givenDataWhenSetAttrExcludeThenVerifyResult() {
        // Act
        catEntity.setAttrExclude(true);
        //Assert
        errorCollector.checkThat(catEntity.getAttrExclude(), is(true));
    }

    @Test
    public void givenDataWhenSetBandThenVerifyResult() {
        //Assert
        short value = 4;
        // Act
        catEntity.setBand(value);
        //Assert
        errorCollector.checkThat(catEntity.getBand(), is(value));
        errorCollector.checkThat(catEntity.compareTo(catEntity), is(0));
    }

    @Test
    public void givenDataWhenSetRelevanceThenVerifyResult() {
        // Act
        catEntity.setRelevance(3);
        //Assert
        errorCollector.checkThat(catEntity.getRelevance(), is(3));
    }

    @Test
    public void givenDataWhenSetTopicDimensionThenVerifyResult() {
        //Arrange
        short value = 5;
        // Act
        catEntity.setTopicDimension(value);
        //Assert
        errorCollector.checkThat(catEntity.getTopicDimension(), is(value));
    }

    @Test
    public void givenDataWhenSetCatIdThenVerifyResult() {
        // Act
        catEntity.setCatId(3L);
        //Assert
        errorCollector.checkThat(catEntity.getCatId(), is(3L));
    }

    @Test
    public void givenDataWhenSetAttributeThenVerifyResult() {
        // Act
        catEntity.setAttribute(attribute);
        //Assert
        errorCollector.checkThat(catEntity.getAttribute(), is(attribute));
    }

    @Test
    public void givenDataWhenSetCharOffsetThenVerifyResult() {
        // Act
        catEntity.setCharOffset(3L);
        //Assert
        errorCollector.checkThat(catEntity.getCharOffset(), is(3L));
    }

    @Test
    public void givenDataWhenSetScoreThenVerifyResult() {
        //Arrange
        short value = 5;
        // Act
        catEntity.setScore(value);
        //Assert
        errorCollector.checkThat(catEntity.getScore(), is(value));
    }

    @Test
    public void givenDataWhenSetCharCountThenVerifyResult() {
        // Act
        catEntity.setCharCount(6L);
        //Assert
        errorCollector.checkThat(catEntity.getCharCount(), is(6L));
    }

    @Test
    public void givenDataWhenSetFromFirstRainThenVerifyResult() {
        // Act
        catEntity.setFromFirstRain(true);
        //Assert
        errorCollector.checkThat(catEntity.getFromFirstRain(), is(true));
    }

    @Test
    public void givenEntityWhenSetDocIdThenVerifyResult() {
        // Act
        catEntity.setDocId("docId");
        //Assert
        errorCollector.checkThat(catEntity.getDocId(), is("docId"));
    }

    @Test
    public void givenEntityWhenSetTitleThenVerifyResult() {
        // Act
        catEntity.setTitle("title");
        //Assert
        errorCollector.checkThat(catEntity.getTitle(), is("title"));
    }

    @Test
    public void givenEntityWhenSetAppIdThenVerifyResult() {
        // Act
        catEntity.setBody("body");
        //Assert
        errorCollector.checkThat(catEntity.getBody(), is("body"));
    }

    @Test
    public void givenEntityWhenSetNameThenVerifyResult() {
        // Act
        attribute.setName("name");
        //Assert
        errorCollector.checkThat(attribute.getName(), is("name"));
    }

    @Test
    public void givenEntityWhenSetAttrSearchTokenThenVerifyResult() {
        // Act
        attribute.setAttrSearchToken("attrSearchToken");
        //Assert
        errorCollector.checkThat(attribute.getAttrSearchToken(), is("attrSearchToken"));
    }

    @Test
    public void givenEntityWhenSetAttrDimThenVerifyResult() {
        // Act
        attribute.setAttrDim(2);
        //Assert
        errorCollector.checkThat(attribute.getAttrDim(), is(2));
    }

    @Test
    public void givenDataWhenSetAppIdThenVerifyResult() {
        // Act
        data.setAppId("id");
        //Assert
        errorCollector.checkThat(data.getAppId(), is("id"));
    }

}
