package com.firstrain.content.similarity;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.mock;

import com.firstrain.solr.client.DocCatEntry;
import com.firstrain.solr.client.DocEntry;
import com.firstrain.solr.client.EntityEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class DocumentSimilarityUtilV3Test {

  private DocumentSimilarityUtilV3 documentSimilarityUtilV3;
  @Rule
  public final TemporaryFolder temporaryFolder = new TemporaryFolder();
  @Rule
  public final ErrorCollector collector = new ErrorCollector();

  @Before
  public void setUp() throws Exception{
    String stopWordsFilePath = temporaryFolder.newFile().getPath();
    documentSimilarityUtilV3 = new DocumentSimilarityUtilV3(stopWordsFilePath);
  }

  @Test
  public void givenParametersSizeOneWhenProcessDocumentThenResult() throws Exception{
    //Arrange
    DocEntry docEntry = new DocEntry();
    List<DocEntry> siteDocList = Collections.singletonList(docEntry);

    //Act
    final List<DocEntry> docEntries = documentSimilarityUtilV3.processDocument(siteDocList);

    //Assert
    assertEquals(docEntries, siteDocList);
  }

  @Test
  public void givenParametersSimilarGroupWhenProcessDocumentThenResult() throws Exception{
    //Arrange
    DocEntry docEntry1 = new DocEntry();
    long groupId = 7L;
    docEntry1.groupId = groupId;
    docEntry1.title = "Title1";
    DocEntry docEntry2 = new DocEntry();
    docEntry2.groupId = groupId;
    List<DocEntry> siteDocList = new ArrayList<DocEntry>();
    siteDocList.add(docEntry1);
    siteDocList.add(docEntry2);

    //Act
    final List<DocEntry> docEntries = documentSimilarityUtilV3.processDocument(siteDocList);

    //Assert
    collector.checkThat(docEntries, is(siteDocList));
    collector.checkThat(docEntries.size(), is(1));
  }

  @Test
  public void givenParametersMatchedSimilarSummaryWhenProcessDocumentThenResult() throws Exception{
    //Arrange
    final List<DocEntry> siteDocList = prepareProcessDocumentCall((short) 3, (short) 3);

    //Act
    final List<DocEntry> docEntries = documentSimilarityUtilV3.processDocument(siteDocList);

    //Assert
    assertEquals(docEntries, siteDocList);
  }

  @Test
  public void givenParametersUnMatchedSimilarSummaryWhenProcessDocumentThenResult() throws Exception{
    //Arrange
    final List<DocEntry> siteDocList = prepareProcessDocumentCall((short) 3, (short) 2);

    //Act
    final List<DocEntry> docEntries = documentSimilarityUtilV3.processDocument(siteDocList);

    //Assert
    assertEquals(docEntries, siteDocList);
  }

  @Test
  public void givenParametersUnMatchedTwoSimilarSummaryWhenProcessDocumentThenResult() throws Exception{
    //Arrange
    final List<DocEntry> siteDocList = prepareProcessDocumentCall((short) 9, (short) 10);

    //Act
    final List<DocEntry> docEntries = documentSimilarityUtilV3.processDocument(siteDocList);

    //Assert
    assertEquals(docEntries, siteDocList);
  }

  private List<DocEntry> prepareProcessDocumentCall(short band1, short band2) {
    DocEntry docEntry1 = new DocEntry();
    docEntry1.title = "Title1";
    String entityEntryId = "entryId";
    EntityEntry entityEntry1 = new EntityEntry();
    entityEntry1.id = entityEntryId;
    DocCatEntry docCatEntry1 = mock(DocCatEntry.class);
    docCatEntry1.band = band1;
    docCatEntry1.entity = entityEntry1;
    ArrayList<DocCatEntry> matchedCompanies1 = new ArrayList<DocCatEntry>();
    matchedCompanies1.add(docCatEntry1);
    ArrayList<DocCatEntry> matchedTopics1 = new ArrayList<DocCatEntry>();
    matchedTopics1.add(docCatEntry1);
    docEntry1.summary = "Summary1";
    docEntry1.matchedCompanies = matchedCompanies1;
    docEntry1.matchedTopics = matchedTopics1;
    DocEntry docEntry2 = new DocEntry();
    docEntry2.title = "Title2";
    EntityEntry entityEntry2 = new EntityEntry();
    entityEntry2.id = entityEntryId;
    DocCatEntry docCatEntry2 = mock(DocCatEntry.class);
    docCatEntry2.band = band2;
    docCatEntry2.entity = entityEntry2;
    ArrayList<DocCatEntry> matchedCompanies2 = new ArrayList<DocCatEntry>();
    ArrayList<DocCatEntry> matchedTopics2 = new ArrayList<DocCatEntry>();
    matchedCompanies2.add(docCatEntry2);
    matchedTopics2.add(docCatEntry2);
    docEntry2.summary = band1==band2?"Summary2":"Summ";
    docEntry2.matchedCompanies = matchedCompanies2;
    docEntry2.matchedTopics = matchedTopics2;
    List<DocEntry> siteDocList = new ArrayList<DocEntry>();
    siteDocList.add(docEntry1);
    siteDocList.add(docEntry2);
    return siteDocList;
  }
}
