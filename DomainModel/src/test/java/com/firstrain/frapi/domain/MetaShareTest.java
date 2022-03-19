package com.firstrain.frapi.domain;

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
public class MetaShareTest {

    @InjectMocks
    private MetaShare metaShare;

    private MetaShare.GroupShare groupShare;
    private MetaShare.UserOwner userOwner;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Before
    public void setUp() {
        groupShare = new MetaShare.GroupShare();
        userOwner = new MetaShare.UserOwner();
    }

    @Test
    public void givenSharedWithWhenSetSharedWithThenVerifyResult() {
       // Act
        metaShare.setSharedWith(Collections.singletonList(groupShare));
        // Assert
        errorCollector.checkThat(metaShare.getSharedWith(),is(Collections.singletonList(groupShare)));
    }

    @Test
    public void givenSharedByWhenSetSharedByThenVerifyResult() {
       // Act
        metaShare.setSharedBy(Collections.singletonList(userOwner));
        // Assert
        errorCollector.checkThat(metaShare.getSharedBy(),is(Collections.singletonList(userOwner)));
    }

    @Test
    public void givenSharedByWhenSetIdThenVerifyResult() {
        // Act
        groupShare.setId("id");
        // Assert
        errorCollector.checkThat(groupShare.getId(),is("id"));
    }

    @Test
    public void givenSharedByWhenSetNameThenVerifyResult() {
        // Act
        groupShare.setName("name");
        // Assert
        errorCollector.checkThat(groupShare.getName(),is("name"));
    }

    @Test
    public void givenUserOwnWhenSetIdThenVerifyResult() {
        // Act
        userOwner.setId("id");
        // Assert
        errorCollector.checkThat(userOwner.getId(),is("id"));
    }

    @Test
    public void givenUserOwnWhenSetNameThenVerifyResult() {
        // Act
        userOwner.setFirstName("firstName");
        // Assert
        errorCollector.checkThat(userOwner.getFirstName(),is("firstName"));
    }

    @Test
    public void givenUserOwnWhenSetLastNameThenVerifyResult() {
        // Act
        userOwner.setLastName("firstName");
        // Assert
        errorCollector.checkThat(userOwner.getLastName(),is("firstName"));
    }
}
