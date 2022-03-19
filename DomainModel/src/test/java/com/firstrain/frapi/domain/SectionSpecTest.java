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
public class SectionSpecTest {
    @InjectMocks
    private SectionSpec sectionSpec;

    private short value;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Before
    public void setUp() {
        value = 3;
    }

    @Test
    public void givenStartWhenSetStartThenVerifyResult() {
        // Act
        sectionSpec.setStart(value);
        // Assert
        errorCollector.checkThat(sectionSpec.getStart(), is(value));
    }

    @Test
    public void givenCountWhenSetCountThenVerifyResult() {
        // Act
        sectionSpec.setCount(value);
        // Assert
        errorCollector.checkThat(sectionSpec.getCount(), is(value));
    }

    @Test
    public void givenWidthWhenSetWidthThenVerifyResult() {
        // Act
        sectionSpec.setWidth(4);
        // Assert
        errorCollector.checkThat(sectionSpec.getWidth(), is(4));
    }

    @Test
    public void givenHeightWhenSetHeightThenVerifyResult() {
        // Act
        sectionSpec.setHeight(7);
        // Assert
        errorCollector.checkThat(sectionSpec.getHeight(), is(7));
    }

    @Test
    public void givenNeedPaginationWhenSetNeedPaginationThenVerifyResult() {
        // Act
        sectionSpec.setNeedPagination(true);
        // Assert
        errorCollector.checkThat(sectionSpec.getNeedPagination(), is(true));
    }

    @Test
    public void givenNeedBucketWhenSetNeedBucketThenVerifyResult() {
        // Act
        sectionSpec.setNeedBucket(true);
        // Assert
        errorCollector.checkThat(sectionSpec.getNeedBucket(), is(true));
    }

    @Test
    public void givenNeedTweetAccelerometerWhenSetNeedTweetAccelerometerThenVerifyResult() {
        // Act
        sectionSpec.setNeedTweetAccelerometer(true);
        // Assert
        errorCollector.checkThat(sectionSpec.getNeedTweetAccelerometer(), is(true));
        errorCollector.checkThat(sectionSpec.toString(),is("SectionSpec [start=0, count=10, width=0, height=0]"));
    }

    @Test
    public void givenCallbackMethodsMapWhenSetCallbackMethodsMapThenVerifyResult() {
        // Act
        sectionSpec.setCallbackMethodsMap(Collections.singletonMap("test", "testw"));
        // Assert
        errorCollector.checkThat(sectionSpec.getCallbackMethodsMap(), is(Collections.singletonMap("test", "testw")));
    }

    @Test
    public void givenNeedRelatedDocWhenSetNeedRelatedDocThenVerifyResult() {
        // Act
        sectionSpec.setNeedRelatedDoc(true);
        // Assert
        errorCollector.checkThat(sectionSpec.isNeedRelatedDoc(), is(true));
    }
}
