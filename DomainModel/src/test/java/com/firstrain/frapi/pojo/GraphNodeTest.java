package com.firstrain.frapi.pojo;

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
public class GraphNodeTest {

    private Entity entityInfo;
    @InjectMocks
    private GraphNode graphNode;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Before
    public void setUp() {
        entityInfo = new Entity();
    }

    @Test
    public void givenSmartText0WhenSetSmartText0ThenVerifyResult() {
        // Act
        graphNode.setSmartText0("smartText0");
        // Assert
        errorCollector.checkThat(graphNode.getSmartText0(),is("smartText0"));
    }

    @Test
    public void givenCcWhenSetCcThenVerifyResult() {
        // Act
        graphNode.setCc("cc");
        // Assert
        errorCollector.checkThat(graphNode.getCc(),is("cc"));
    }

    @Test
    public void givenParentTokenWhenSetParentTokenThenVerifyResult() {
        // Act
        graphNode.setParentToken("parentToken");
        // Assert
        errorCollector.checkThat(graphNode.getParentToken(),is("parentToken"));
    }

    @Test
    public void givenSubtreeWhenSetSubtreeThenVerifyResult() {
         // Act
        graphNode.setSubtree(Collections.singletonList(graphNode));
        // Assert
        errorCollector.checkThat(graphNode.getSubtree(),is(Collections.singletonList(graphNode)));
    }

    @Test
    public void givenIdWhenSetIdThenVerifyResult() {
          // Act
        graphNode.setId("id");
        // Assert
        errorCollector.checkThat(graphNode.getId(),is("id"));
    }

    @Test
    public void givenLabelWhenSetLabelThenVerifyResult() {
         // Act
        graphNode.setLabel("label");
        // Assert
        errorCollector.checkThat(graphNode.getLabel(),is("label"));
    }

    @Test
    public void givenValueWhenSetValueThenVerifyResult() {
         // Act
        graphNode.setValue(3f);
        // Assert
        errorCollector.checkThat(graphNode.getValue(),is(3f));
    }

    @Test
    public void givenIntensityWhenSetIntensityThenVerifyResult() {
        // Act
        graphNode.setIntensity(6);
        // Assert
        errorCollector.checkThat(graphNode.getIntensity(),is(6));
    }

    @Test
    public void givenSmartTextWhenSetSmartTextThenVerifyResult() {
       // Act
        graphNode.setSmartText("smartText");
        // Assert
        errorCollector.checkThat(graphNode.getSmartText(),is("smartText"));
    }

    @Test
    public void givenSearchTokenWhenSetSearchTokenThenVerifyResult() {
        // Act
        graphNode.setSearchToken("searchToken");
        // Assert
        errorCollector.checkThat(graphNode.getSearchToken(),is("searchToken"));
    }

    @Test
    public void givenNameWhenSetNameThenVerifyResult() {
        // Arrange
        graphNode = new GraphNode("id","label",4f,1);
       // Act
        graphNode.setName("name");
        // Assert
        errorCollector.checkThat(graphNode.getName(),is("name"));
    }

    @Test
    public void givenQueryWhenSetQueryThenVerifyResult() {
        // Act
        graphNode.setQuery("query");
        // Assert
        errorCollector.checkThat(graphNode.getQuery(),is("query"));
    }

    @Test
    public void givenItemIdWhenSetItemIdThenVerifyResult() {
         // Act
        graphNode.setItemId(6L);
        // Assert
        errorCollector.checkThat(graphNode.getItemId(),is(6L));
    }

    @Test
    public void givenImageNameWhenSetImageNameThenVerifyResult() {
        // Act
        graphNode.setImageName("imageName");
        // Assert
        errorCollector.checkThat(graphNode.getImageName(),is("imageName"));
    }

    @Test
    public void givenParentsWhenSetParentsThenVerifyResult() {
         // Act
        graphNode.setParents(new int[]{4});
        // Assert
        errorCollector.checkThat(graphNode.getParents(),is(new int[]{4}));
    }

    @Test
    public void givenParentWhenSetParentThenVerifyResult() {
        // Act
        graphNode.setParent("parent");
        // Assert
        errorCollector.checkThat(graphNode.getParent(),is("parent"));
    }

    @Test
    public void givenEntityInfoWhenSetEntityInfoThenVerifyResult() {
        // Act
        graphNode.setEntityInfo(entityInfo);
        // Assert
        errorCollector.checkThat(graphNode.getEntityInfo(),is(entityInfo));
    }
}
