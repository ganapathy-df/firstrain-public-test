package com.firstrain.feed.obj;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import com.firstrain.mip.object.FR_IDocCategory;
import com.firstrain.mip.object.FR_IQuote;
import com.firstrain.mip.object.FR_ISiteDocument;
import com.firstrain.mip.object.impl.FR_DocCategory;
import com.firstrain.mip.object.impl.FR_SiteDocument;
import com.firstrain.mip.object.impl.FR_SiteDocumentScore;
import com.firstrain.solr.client.DocCatEntry;
import com.firstrain.solr.client.DocEntry;
import com.firstrain.solr.client.EntityEntry;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class DocEntrySiteDocumentTest {

    private static final String DOCUMENT_TITLE = "documentTitle";
    private static final String DOCUMENT_SUMMARY = "documentSummary";
    private static final long SITE_DOC_ID = 1L;
    private static final long TIME_MILLIS = System.currentTimeMillis();
    private static final long GROUP_ID = 2L;
    private static final int ENTITY_ID = 3;
    private static final String CAT_STRING = "catString";
    private static final long CLUSTER_ID = 4L;
    private static final String DOCUMENT_COMMENTS = "documentComments";
    private static final String SOURCE_NAME = "sourceName";
    private static final String LINK = "link";
    private static final long DOCUMENT_SIZE = 5L;
    private static final long DOCUMENT_TITLE_HASH_ID = 6L;
    private static final String CACHED_PAGE = "cachedPage";
    private static final String CHANGED_XML = "changedXml";
    private static final String DOCUMENT_ANCHOR_TEXT = "documentAnchorText";
    private static final String DOCUMENT_BODY = "documentBody";
    private static final int DOCUMENT_ID = 7;
    private static final String D_SERVER_DOCUMENT_ID = "dServerDocumentId";
    private static final String DOCUMENT_EXTRA = "documentExtra";
    private static final String FEED_NAME = "feedName";
    private static final String REMOTE_URL = "remoteUrl";
    private static final String DOCUMENT_VERSION = "documentVersion";
    private static final String FR_I_KEY = "frIKey";
    private static final String FR_I_VALUE = "frIValue";

    private DocEntrySiteDocument docEntrySiteDocument;
    private DocEntry docEntry;
    @Mock
    private DocCatEntry docCatEntry;
    @Mock
    private EntityEntry entityEntry;

    @Test
    public void givenWhenSetGetTitle() throws Exception {
        // Act
        docEntrySiteDocument.setTitle(DOCUMENT_TITLE);
        // Assert
        assertEquals(DOCUMENT_TITLE, docEntrySiteDocument.getTitle());
    }

    @Test
    public void givenWhenSetGetSiteDocID() throws Exception {
        // Act
        docEntrySiteDocument.setSiteDocID(SITE_DOC_ID);
        // Assert
        assertEquals(SITE_DOC_ID, docEntrySiteDocument.getSiteDocID());
    }

    @Test
    public void givenWhenSetGetGroupID() throws Exception {
        // Act
        docEntrySiteDocument.setGroupID(GROUP_ID);
        // Assert
        assertEquals(GROUP_ID, docEntrySiteDocument.getGroupID());
    }

    @Test
    public void givenWhenSetGetOriginalDocEntry() throws Exception {
        // Act
        docEntrySiteDocument.setOriginalDocEntry(docEntry);
        // Assert
        assertEquals(docEntry, docEntrySiteDocument.getOriginalDocEntry());
    }

    @Test
    public void givenWhenGetAttributes() throws Exception {
        // Assert
        assertTrue(docEntrySiteDocument.attributes().isEmpty());
    }

    @Test
    public void givenWhenAddDocCatObj() throws Exception {
        // Arrange
        final FR_IDocCategory docCategory = new FR_DocCategory();
        // Act
        docEntrySiteDocument.addDocCatObj(docCategory);
        // Assert
        assertTrue(docEntrySiteDocument.getDocCatObjs().isEmpty());
    }

    @Test
    public void givenWhenAddRefSiteDocID() throws Exception {
        // Arrange
        final FR_ISiteDocument siteDocument = new FR_SiteDocument();
        // Act
        docEntrySiteDocument.addRefSiteDocID(siteDocument);
        // Assert
        assertNull(docEntrySiteDocument.getRefSiteDocIDs());
    }

    @Test
    public void givenWhenDisableBits() throws Exception {
        // Act
        docEntrySiteDocument.disableBits(new int[] {1});
        // Assert
        assertEquals(0, docEntrySiteDocument.getBitMap());
    }

    @Test
    public void givenWhenEnableBits() throws Exception {
        // Act
        docEntrySiteDocument.enableBits(new int[] {1});
        // Assert
        assertEquals(0, docEntrySiteDocument.getBitMap());
    }

    @Test
    public void givenWhenGetArchive() throws Exception {
        // Assert
        assertEquals(0, docEntrySiteDocument.getArchive());
    }

    @Test
    public void givenWhenGetAttachmentCount() throws Exception {
        // Assert
        assertEquals(0, docEntrySiteDocument.getAttachmentCount());
    }

    @Test
    public void givenWhenGetAttachments() throws Exception {
        // Assert
        assertNull(docEntrySiteDocument.getAttachments());
    }

    @Test
    public void givenWhenGetCached() throws Exception {
        // Assert
        assertEquals(0, docEntrySiteDocument.getCached());
    }

    @Test
    public void givenWhenGetCatIDs() throws Exception {
        // Assert
        assertNull(docEntrySiteDocument.getCatIDs());
    }

    @Test
    public void givenWhenGetCatString() throws Exception {
        // Assert
        assertNull(docEntrySiteDocument.getCatString());
    }

    @Test
    public void givenWhenGetClusterID() throws Exception {
        // Assert
        assertEquals(0L, docEntrySiteDocument.getClusterID());
    }

    @Test
    public void givenWhenGetClusterPublishedDocCount() throws Exception {
        // Assert
        assertEquals(0, docEntrySiteDocument.getClusterPublishedDocCount());
    }

    @Test
    public void givenWhenGetClusterUpdateTime() throws Exception {
        // Assert
        assertNull(docEntrySiteDocument.getClusterUpdateTime());
    }

    @Test
    public void givenWhenGetComments() throws Exception {
        // Assert
        assertNull(docEntrySiteDocument.getComments());
    }

    @Test
    public void givenWhenGetComparator() throws Exception {
        // Assert
        assertNull(docEntrySiteDocument.getComparator(1));
    }

    @Test
    public void givenWhenGetDefMIPSourceID() throws Exception {
        // Assert
        assertEquals(0, docEntrySiteDocument.getDefMIPSourceID());
    }

    @Test
    public void givenWhenGetDefMIPSourceName() throws Exception {
        // Assert
        assertNull(docEntrySiteDocument.getDefMIPSourceName());
    }

    @Test
    public void givenWhenGetDiscussionCount() throws Exception {
        // Assert
        assertEquals(0, docEntrySiteDocument.getDiscussionCount());
    }

    @Test
    public void givenWhenGetDocBodyWithoutTitle() throws Exception {
        // Assert
        assertNull(docEntrySiteDocument.getDocBodyWithoutTitle());
    }

    @Test
    public void givenWhenSetGetFlagDeleted() throws Exception {
        // Act
        docEntrySiteDocument.setFlagDeleted(true);
        // Assert
        assertFalse(docEntrySiteDocument.getFlagDeleted());
    }

    @Test
    public void givenWhenSetGetFlagInput() throws Exception {
        // Act
        docEntrySiteDocument.setFlagInput(1);
        // Assert
        assertEquals(0, docEntrySiteDocument.getFlagInput());
    }

    @Test
    public void givenWhenSetGetFlagPublished() throws Exception {
        // Act
        docEntrySiteDocument.setFlagPublished(1);
        // Assert
        assertEquals(0, docEntrySiteDocument.getFlagPublished());
    }

    @Test
    public void givenWhenGetFolderType() throws Exception {
        // Assert
        assertEquals(0, docEntrySiteDocument.getFolderType());
    }

    @Test
    public void givenWhenSetGetGroupStatus() throws Exception {
        // Act
        docEntrySiteDocument.setGroupStatus(1);
        // Assert
        assertEquals(0, docEntrySiteDocument.getGroupStatus());
    }

    @Test
    public void givenWhenSetGetLink1() throws Exception {
        // Act
        docEntrySiteDocument.setLink1(LINK);
        // Assert
        assertNull(docEntrySiteDocument.getLink1());
    }

    @Test
    public void givenWhenSetGetLink2() throws Exception {
        // Act
        docEntrySiteDocument.setLink2(LINK);
        // Assert
        assertNull(docEntrySiteDocument.getLink2());
    }

    @Test
    public void givenWhenGetOverallScore() throws Exception {
        // Assert
        assertEquals(0, docEntrySiteDocument.getOverallScore());
    }

    @Test
    public void givenTimestampWhenGetOverallScore() throws Exception {
        // Assert
        assertEquals(0, docEntrySiteDocument.getOverallScore(new Timestamp(TIME_MILLIS)));
    }

    @Test
    public void givenWhenSetGetPageChange() throws Exception {
        // Act
        docEntrySiteDocument.setPageChange(1);
        // Assert
        assertEquals(0, docEntrySiteDocument.getPageChange());
    }

    @Test
    public void givenWhenSetGetPermanent() throws Exception {
        // Act
        docEntrySiteDocument.setPermanent(true);
        // Assert
        assertFalse(docEntrySiteDocument.getPermanent());
    }

    @Test
    public void givenWhenSetGetPersonalFolderIDs() throws Exception {
        // Act
        docEntrySiteDocument.setPersonalFolderIDs(new ArrayList());
        // Assert
        assertNull(docEntrySiteDocument.getPersonalFolderIDs());
    }

    @Test
    public void givenWhenSetGetPublishDate() throws Exception {
        // Act
        docEntrySiteDocument.setPublishDate(new Date(TIME_MILLIS));
        // Assert
        assertNull(docEntrySiteDocument.getPublishDate());
    }

    @Test
    public void givenWhenSetGetQualityLevel() throws Exception {
        // Act
        docEntrySiteDocument.setQualityLevel(1);
        // Assert
        assertEquals(0, docEntrySiteDocument.getQualityLevel());
    }

    @Test
    public void givenWhenSetGetQuotes() throws Exception {
        // Act
        docEntrySiteDocument.setQuotes(new ArrayList<FR_IQuote>());
        // Assert
        assertNull(docEntrySiteDocument.getQuotes());
    }

    @Test
    public void givenWhenSetGetRefSiteDocCount() throws Exception {
        docEntrySiteDocument.setRefSiteDocCount(1);
        // Assert
        assertEquals(0, docEntrySiteDocument.getRefSiteDocCount());
    }

    @Test
    public void givenWhenSetGetRefSiteDocIDs() throws Exception {
        // Act
        docEntrySiteDocument.setRefSiteDocIDs(new ArrayList());
        // Assert
        assertNull(docEntrySiteDocument.getRefSiteDocIDs());
    }

    @Test
    public void givenWhenSetGetRelatedDocCount() throws Exception {
        // Act
        docEntrySiteDocument.setRelatedDocCount(1);
        // Assert
        assertEquals(0, docEntrySiteDocument.getRelatedDocCount());
    }

    @Test
    public void givenWhenGetSentenceMarkedDocBodyWithoutTitle() throws Exception {
        // Assert
        assertNull(docEntrySiteDocument.getSentenceMarkedDocBodyWithoutTitle());
    }

    @Test
    public void givenWhenGetSentenceMarkedSummary() throws Exception {
        // Assert
        assertNull(docEntrySiteDocument.getSentenceMarkedSummary());
    }

    @Test
    public void givenWhenGetSharable() throws Exception {
        // Assert
        assertEquals(0, docEntrySiteDocument.getSharable());
    }

    @Test
    public void givenWhenSetGetSiteDocInsertTime() throws Exception {
        // Act
        docEntrySiteDocument.setSiteDocInsertTime(new Timestamp(TIME_MILLIS));
        // Assert
        assertNull(docEntrySiteDocument.getSiteDocInsertTime());
    }

    @Test
    public void givenWhenSetGetSiteDocSubCatIDList() throws Exception {
        // Act
        docEntrySiteDocument.setSiteDocSubCatIDList(new ArrayList());
        // Assert
        assertNull(docEntrySiteDocument.getSiteDocSubCatIDList());
    }

    @Test
    public void givenWhenSetGetSiteDocUpdateTime() throws Exception {
        // Act
        docEntrySiteDocument.setSiteDocUpdateTime(new Timestamp(TIME_MILLIS));
        // Assert
        assertNull(docEntrySiteDocument.getSiteDocUpdateTime());
    }

    @Test
    public void givenWhenSetGetSiteDocumentScore() throws Exception {
        // Act
        docEntrySiteDocument.setSiteDocumentScore(new FR_SiteDocumentScore());
        // Assert
        assertNull(docEntrySiteDocument.getSiteDocumentScore());
    }

    @Test
    public void givenWhenSetGetSiteHistoryInsertTime() throws Exception {
        // Act
        docEntrySiteDocument.setSiteHistoryInsertTime(new Timestamp(TIME_MILLIS));
        // Assert
        assertNull(docEntrySiteDocument.getSiteHistoryInsertTime());
    }

    @Test
    public void givenWhenSetGetSiteID() throws Exception {
        // Act
        docEntrySiteDocument.setSiteID(1);
        // Assert
        assertEquals(0, docEntrySiteDocument.getSiteID());
    }

    @Test
    public void givenWhenGetSize() throws Exception {
        // Act
        docEntrySiteDocument.setSize(DOCUMENT_SIZE);
        // Assert
        assertEquals(0L, docEntrySiteDocument.getSize());
    }

    @Test
    public void givenWhenSetGetSummary() throws Exception {
        // Act
        docEntrySiteDocument.setSummary(DOCUMENT_SUMMARY);
        // Assert
        assertEquals(DOCUMENT_SUMMARY, docEntrySiteDocument.getSummary());
    }

    @Test
    public void givenWhenSetGetTitleHashID() throws Exception {
        // Act
        docEntrySiteDocument.setTitleHashID(DOCUMENT_TITLE_HASH_ID);
        // Assert
        assertEquals(0L, docEntrySiteDocument.getTitleHashID());
    }

    @Test
    public void givenWhenSetGetUserEntityPrefBitMap() throws Exception {
        // Act
        docEntrySiteDocument.setUserEntityPrefBitMap(1);
        // Assert
        assertEquals(0, docEntrySiteDocument.getUserEntityPrefBitMap());
    }

    @Test
    public void givenWhenSetGetUserID() throws Exception {
        // Act
        docEntrySiteDocument.setUserID(1);
        // Assert
        assertEquals(0, docEntrySiteDocument.getUserID());
    }

    @Test
    public void givenWhenHasChanged() throws Exception {
        // Assert
        assertFalse(docEntrySiteDocument.hasChanged());
    }

    @Test
    public void givenWhenIsArchived() throws Exception {
        // Assert
        assertFalse(docEntrySiteDocument.isArchived());
    }

    @Test
    public void givenWhenIsBitEnabled() throws Exception {
        // Assert
        assertFalse(docEntrySiteDocument.isBitEnabled(1));
    }

    @Test
    public void givenWhenIsCached() throws Exception {
        // Assert
        assertFalse(docEntrySiteDocument.isCached());
    }

    @Test
    public void givenWhenSetIsSentInDigest() throws Exception {
        // Assert
        docEntrySiteDocument.setSentInDigest(true);
        // Assert
        assertFalse(docEntrySiteDocument.isSentInDigest());
    }

    @Test
    public void givenWhenSetIsSharable() throws Exception {
        // Act
        docEntrySiteDocument.setSharable(1);
        // Assert
        assertFalse(docEntrySiteDocument.isSharable());
    }

    @Test
    public void givenWhenSetGetArchive() throws Exception {
        // Act
        docEntrySiteDocument.setArchive(1);
        // Assert
        assertEquals(0, docEntrySiteDocument.getArchive());
    }

    @Test
    public void givenWhenSetGetAttachmentCount() throws Exception {
        // Act
        docEntrySiteDocument.setAttachmentCount(1);
        // Assert
        assertEquals(0, docEntrySiteDocument.getAttachmentCount());
    }

    @Test
    public void givenWhenSetGetAttachments() throws Exception {
        // Act
        docEntrySiteDocument.setAttachments(new ArrayList());
        // Assert
        assertNull(docEntrySiteDocument.getAttachments());
    }

    @Test
    public void givenWhenSetGetBitMap() throws Exception {
        // Act
        docEntrySiteDocument.setBitMap(1);
        // Assert
        assertEquals(0, docEntrySiteDocument.getBitMap());
    }

    @Test
    public void givenWhenSetGetCached() throws Exception {
        // Act
        docEntrySiteDocument.setCached(1);
        // Assert
        assertEquals(0, docEntrySiteDocument.getCached());
    }

    @Test
    public void givenWhenSetGetCatIDs() throws Exception {
        // Act
        docEntrySiteDocument.setCatIDs(new ArrayList());
        // Assert
        assertNull(docEntrySiteDocument.getCatIDs());
    }

    @Test
    public void givenWhenSetGetCatString() throws Exception {
        // Act
        docEntrySiteDocument.setCatString(CAT_STRING);
        // Act
        assertNull(docEntrySiteDocument.getCatString());
    }

    @Test
    public void givenWhenSetGetClusterID() throws Exception {
        // Act
        docEntrySiteDocument.setClusterID(CLUSTER_ID);
        // Assert
        assertEquals(0L, docEntrySiteDocument.getClusterID());
    }

    @Test
    public void givenWhenSetGetClusterPublishedDocCount() throws Exception {
        // Act
        docEntrySiteDocument.setClusterPublishedDocCount(1);
        // Assert
        assertEquals(0, docEntrySiteDocument.getClusterPublishedDocCount());
    }

    @Test
    public void givenWhenSetGetClusterUpdateTime() throws Exception {
        // Act
        docEntrySiteDocument.setClusterUpdateTime(new Timestamp(TIME_MILLIS));
        // Assert
        assertNull(docEntrySiteDocument.getClusterUpdateTime());
    }

    @Test
    public void givenWhenSetGetComments() throws Exception {
        // Act
        docEntrySiteDocument.setComments(DOCUMENT_COMMENTS);
        // Assert
        assertNull(docEntrySiteDocument.getComments());
    }

    @Test
    public void givenWhenSetGetDefMIPSourceID() throws Exception {
        // Act
        docEntrySiteDocument.setDefMIPSourceID(1);
        // Assert
        assertEquals(0, docEntrySiteDocument.getDefMIPSourceID());
    }

    @Test
    public void givenWhenSetGetDefMIPSourceName() throws Exception {
        // Act
        docEntrySiteDocument.setDefMIPSourceName(SOURCE_NAME);
        // Assert
        assertNull(docEntrySiteDocument.getDefMIPSourceName());
    }

    @Test
    public void givenWhenSetGetDiscussionCount() throws Exception {
        // Act
        docEntrySiteDocument.setDiscussionCount(1);
        // Assert
        assertEquals(0, docEntrySiteDocument.getDiscussionCount());
    }

    @Test
    public void givenWhenSetGetDocCatObjs() throws Exception {
        // Act
        docEntrySiteDocument.setDocCatObjs(null);
        // Assert
        assertNotNull(docEntrySiteDocument.getDocCatObjs());
    }

    @Test
    public void givenWhenSetGetAccessType() throws Exception {
        // Act
        docEntrySiteDocument.setAccessType(1);
        // Assert
        assertEquals(0, docEntrySiteDocument.getAccessType());
    }

    @Test
    public void givenWhenSetGetCachedPage() throws Exception {
        // Act
        docEntrySiteDocument.setCachedPage(CACHED_PAGE);
        // Assert
        assertNull(docEntrySiteDocument.getCachedPage());
    }

    @Test
    public void givenWhenSetGetChangedXml() throws Exception {
        // Act
        docEntrySiteDocument.setChangedXml(CHANGED_XML);
        // Assert
        assertNull(docEntrySiteDocument.getChangedXml());
    }

    @Test
    public void givenWhenSetGetDocAnchorText() throws Exception {
        // Act
        docEntrySiteDocument.setDocAnchorText(DOCUMENT_ANCHOR_TEXT);
        // Assert
        assertNull(docEntrySiteDocument.getDocAnchorText());
    }

    @Test
    public void givenWhenSetGetDocBody() throws Exception {
        // Act
        docEntrySiteDocument.setDocBody(DOCUMENT_BODY);
        // Assert
        assertNull(docEntrySiteDocument.getDocBody());
    }

    @Test
    public void givenWhenSetGetDocID() throws Exception {
        // Act
        docEntrySiteDocument.setDocID(DOCUMENT_ID);
        // Assert
        assertEquals(0, docEntrySiteDocument.getDocID());
    }

    @Test
    public void givenWhenSetGetDserverDocID() throws Exception {
        // Act
        docEntrySiteDocument.setDserverDocID(D_SERVER_DOCUMENT_ID);
        // Assert
        assertNull(docEntrySiteDocument.getDserverDocID());
    }

    @Test
    public void givenWhenSetGetExtra1() throws Exception {
        // Act
        docEntrySiteDocument.setExtra1(DOCUMENT_EXTRA);
        // Assert
        assertNull(docEntrySiteDocument.getExtra1());
    }

    @Test
    public void givenWhenSetGetExtra2() throws Exception {
        // Act
        docEntrySiteDocument.setExtra2(DOCUMENT_EXTRA);
        // Assert
        assertNull(docEntrySiteDocument.getExtra2());
    }

    @Test
    public void givenWhenSetGetFeedID() throws Exception {
        // Act
        docEntrySiteDocument.setFeedID(1);
        // Assert
        assertEquals(0, docEntrySiteDocument.getFeedID());
    }

    @Test
    public void givenWhenSetGetFeedName() throws Exception {
        // Act
        docEntrySiteDocument.setFeedName(FEED_NAME);
        // Assert
        assertNull(docEntrySiteDocument.getFeedName());
    }

    @Test
    public void givenWhenGetInsertTime() throws Exception {
        // Assert
        assertEquals(TIME_MILLIS, docEntrySiteDocument.getInsertTime().getTime());
    }

    @Test
    public void givenWhenSetGetMimeType() throws Exception {
        // Act
        docEntrySiteDocument.setMimeType(1);
        // Assert
        assertEquals(0, docEntrySiteDocument.getMimeType());
    }

    @Test
    public void givenWhenSetGetRemoteUrl() throws Exception {
        // Act
        docEntrySiteDocument.setRemoteUrl(REMOTE_URL);
        // Assert
        assertNull(docEntrySiteDocument.getRemoteUrl());
    }

    @Test
    public void givenWhenGetSentenceMarkedDocBody() throws Exception {
        // Assert
        assertNull(docEntrySiteDocument.getSentenceMarkedDocBody());
    }

    @Test
    public void givenWhenSetGetSourceTypeID() throws Exception {
        // Act
        docEntrySiteDocument.setSourceTypeID(1);
        // Assert
        assertEquals(0, docEntrySiteDocument.getSourceTypeID());
    }

    @Test
    public void givenWhenGetSetStartUrlIDs() throws Exception {
        // Act
        docEntrySiteDocument.setStartUrlIDs(new int[] {1});
        // Assert
        assertNull(docEntrySiteDocument.getStartUrlIDs());
    }

    @Test
    public void givenWhenGetSetUpdateTime() throws Exception {
        // Act
        docEntrySiteDocument.setUpdateTime(new Timestamp(TIME_MILLIS));
        // Assert
        assertNull(docEntrySiteDocument.getUpdateTime());
    }

    @Test
    public void givenWhenSetGetUrlHashID() throws Exception {
        // Act
        docEntrySiteDocument.setUrlHashID(1);
        // Assert
        assertEquals(0, docEntrySiteDocument.getUrlHashID());
    }

    @Test
    public void givenWhenGetSetVersion1() throws Exception {
        // Act
        docEntrySiteDocument.setVersion1(DOCUMENT_VERSION);
        // Assert
        assertNull(docEntrySiteDocument.getVersion1());
    }

    @Test
    public void givenWhenGetSetVersion2() throws Exception {
        // Act
        docEntrySiteDocument.setVersion2(DOCUMENT_VERSION);
        // Assert
        assertNull(docEntrySiteDocument.getVersion2());
    }

    @Test
    public void givenWhenAddClearGet() throws Exception {
        // Arrange
        docEntrySiteDocument.add(FR_I_KEY, FR_I_VALUE);
        // Act
        docEntrySiteDocument.clear();
        // Assert
        assertNull(docEntrySiteDocument.get(FR_I_KEY));
    }

    @Test
    public void givenWhenSetDocCatEntries() throws Exception {
        // Act
        docEntrySiteDocument.setDocCatEntries(singletonList(docCatEntry));
        // Assert
        assertFalse(docEntrySiteDocument.getDocCatObjs().isEmpty());
    }

    @Test
    public void givenEntityNullWhenSetDocCatEntries() throws Exception {
        // Arrange
        when(docCatEntry.getEntity()).thenReturn(null);
        // Act
        docEntrySiteDocument.setDocCatEntries(singletonList(docCatEntry));
        // Assert
        assertTrue(docEntrySiteDocument.getDocCatObjs().isEmpty());
    }

    @Test
    public void givenInvalidEntityWhenSetDocCatEntries() throws Exception {
        // Arrange
        when(entityEntry.isValid()).thenReturn(false);
        // Act
        docEntrySiteDocument.setDocCatEntries(singletonList(docCatEntry));
        // Assert
        assertTrue(docEntrySiteDocument.getDocCatObjs().isEmpty());
    }

    @Test
    public void givenEntityExcludedWhenSetDocCatEntries() throws Exception {
        // Arrange
        when(entityEntry.isExcluded()).thenReturn(true);
        // Act
        docEntrySiteDocument.setDocCatEntries(singletonList(docCatEntry));
        // Assert
        assertTrue(docEntrySiteDocument.getDocCatObjs().isEmpty());
    }

    @Test
    public void givenEmptyListWhenSetDocCatEntries() throws Exception {
        // Act
        docEntrySiteDocument.setDocCatEntries(new ArrayList<DocCatEntry>());
        // Assert
        assertTrue(docEntrySiteDocument.getDocCatObjs().isEmpty());
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        docEntry = new DocEntry();
        docEntry.title = DOCUMENT_TITLE;
        docEntry.summary = DOCUMENT_SUMMARY;
        docEntry.sitedocId = String.valueOf(SITE_DOC_ID);
        docEntry.insertTime = new Date(TIME_MILLIS);
        docEntry.groupId = GROUP_ID;
        docEntry.catEntries.add(docCatEntry);
        docEntrySiteDocument = new DocEntrySiteDocument(docEntry);

        docCatEntry.entity = entityEntry;
        when(docCatEntry.getEntity()).thenReturn(entityEntry);
        when(entityEntry.isValid()).thenReturn(true);
        when(entityEntry.isExcluded()).thenReturn(false);
        when(entityEntry.getId()).thenReturn(String.valueOf(ENTITY_ID));
    }

}
