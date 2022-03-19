package com.firstrain.frapi.pojo.wrapper;

import com.firstrain.frapi.domain.Document;
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
public class DocumentSetTest {

    private Document document;

    @InjectMocks
    private DocumentSet documentSet;

    private final ErrorCollector errorCollector = new ErrorCollector();

    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector);

    @Before
    public void setUp() {
        document = new Document();
    }

    @Test
    public void givenFilingWhenSetFilingThenVerifyResult() {
        // Act
        documentSet.setFiling(true);
        // Assert
        errorCollector.checkThat(documentSet.isFiling(), is(true));
    }

    @Test
    public void givenDocumentsWhenSetDocumentsThenVerifyResult() {
        // Act
        documentSet.setDocuments(Collections.singletonList(document));
        // Assert
        errorCollector.checkThat(documentSet.getDocuments(), is(Collections.singletonList(document)));
    }

    @Test
    public void givenCaptionWhenSetCaptionThenVerifyResult() {
        // Arrange
        documentSet = new DocumentSet(BaseSet.SectionType.FT);
        // Act
        documentSet.setCaption("caption");
        // Assert
        errorCollector.checkThat(documentSet.getCaption(), is("caption"));
    }

    @Test
    public void givenPrimaryIndustryWhenSetPrimaryIndustryThenVerifyResult() {
        // Act
        documentSet.setPrimaryIndustry(true);
        // Assert
        errorCollector.checkThat(documentSet.getPrimaryIndustry(), is(true));
    }

    @Test
    public void givenScopeWhenSetScopeThenVerifyResult() {
        // Arrange
        documentSet = new DocumentSet();
        // Act
        documentSet.setScope(5);
        // Assert
        errorCollector.checkThat(documentSet.getScope(), is(5));
    }

    @Test
    public void givenDocumentBucketWhenSetDocumentBucketThenVerifyResult() {
        // Act
        documentSet.setDocumentBucket(Collections.singletonMap("test",
                Collections.singletonList(document)));
        // Assert
        errorCollector.checkThat(documentSet.getDocumentBucket(), is(Collections.singletonMap("test",
                Collections.singletonList(document))));
    }
}
