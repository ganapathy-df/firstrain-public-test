package com.firstrain.frapi.pojo;

import com.firstrain.frapi.util.DefaultEnums.UserItemType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.sql.Timestamp;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;

@RunWith(MockitoJUnitRunner.class)
public class UserItemTest {

    private Timestamp insertTime;
    private Entity entity;
    private UserItem.Visibility visibility;
    private UserItem.SharedDetails sharedDetails;
    private UserItem.VisibilityRes visibilityRes;

    @InjectMocks
    private UserItem userItem;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Before
    public void setUp() {
        insertTime = new Timestamp(1234L);
        entity = new Entity();
        visibility = new UserItem.Visibility();
        sharedDetails = new UserItem.SharedDetails();
        visibility = new UserItem.Visibility();
        visibilityRes = new UserItem.VisibilityRes();
    }

    @Test
    public void givenRefersToWhenSetRefersToThenVerifyResult() {
        // Act
        userItem.setRefersTo("refersTo");
        // Assert
        errorCollector.checkThat(userItem.getRefersTo(), is("refersTo"));
    }

    @Test
    public void givenIdWhenSetIdThenVerifyResult() {
        // Arrange
        sharedDetails.userItems = Collections.singletonList(userItem);
        sharedDetails.totalCount = 1;
        // Act
        userItem.setId("id");
        // Assert
        errorCollector.checkThat(userItem.getId(), is("id"));
    }

    @Test
    public void givenInsertTimeWhenSetInsertTimeThenVerifyResult() {
        // Arrange
        visibilityRes.shareWith = Collections.singletonList(visibility);
        visibilityRes.unShareWith = Collections.singletonList(visibility);
        // Act
        userItem.setInsertTime(insertTime);
        // Assert
        errorCollector.checkThat(userItem.getInsertTime(), is(insertTime));
    }

    @Test
    public void givenOwnerListWhenSetOwnerListThenVerifyResult() {
        // Act
        userItem.setOwnerList(Collections.singletonList(entity));
        // Assert
        errorCollector.checkThat(userItem.getOwnerList(), is(Collections.singletonList(entity)));
    }

    @Test
    public void givenOwnerListWhenSetInsertTimeThenVerifyResult() {
        // Act
        userItem.setInsertTime(insertTime);
        // Assert
        errorCollector.checkThat(userItem.getInsertTime(), is(insertTime));
    }

    @Test
    public void givenEAndConditionOnLine52TrueWhenAddOwnerThenVerifyResult() {
        // Act
        userItem.addOwner(entity);
        // Assert
        errorCollector.checkThat(userItem.getOwnerList(), is(Collections.singletonList(entity)));
    }

    @Test
    public void givenVisibilitiesWhenSetVisibilitiesThenVerifyResult() {
        // Act
        userItem.setVisibilities(Collections.singletonList(visibility));
        // Assert
        errorCollector.checkThat(userItem.getVisibilities(), is(Collections.singletonList(visibility)));
    }

    @Test
    public void givenTypeWhenSetTypeThenVerifyResult() {
        // Act
        userItem.setType(UserItemType.Printed);
        // Assert
        errorCollector.checkThat(userItem.getType(), is(UserItemType.Printed));
    }

    @Test
    public void givenTypeWhenSetIdThenVerifyResult() {
        // Act
        visibility.setId(1L);
        // Assert
        errorCollector.checkThat(visibility.getId(), is(1L));
    }

    @Test
    public void givenTypeWhenSetViewerThenVerifyResult() {
        // Act
        visibility.setViewer(entity);
        // Assert
        errorCollector.checkThat(visibility.getViewer(), is(entity));
    }

    @Test
    public void givenTypeWhenSetOwnerThenVerifyResult() {
        // Act
        visibility.setOwner(entity);
        // Assert
        errorCollector.checkThat(visibility.getOwner(), is(entity));
    }

}
