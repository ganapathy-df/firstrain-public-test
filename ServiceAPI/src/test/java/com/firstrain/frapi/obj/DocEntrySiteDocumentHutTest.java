package com.firstrain.frapi.obj;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import com.firstrain.mip.object.FR_IQuote;
import com.firstrain.mip.object.impl.FR_DocCategory;
import com.firstrain.mip.object.impl.FR_SiteDocument;
import com.firstrain.mip.object.impl.FR_SiteDocumentScore;
import com.firstrain.solr.client.DocEntry;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class DocEntrySiteDocumentHutTest {

    private static final long TIME_MILLIS = System.currentTimeMillis();
    private static final String DOCUMENT_TITLE = "documentTitle";
    private static final long SITE_DOC_ID = 1L;
    private static final String CAT_STRING = "catString";
    private static final long CLUSTER_ID = 2L;
    private static final String COMMENTS = "comments";
    private static final String SOURCE_NAME = "sourceName";
    private static final long GROUP_ID = 3L;
    private static final String LINK = "link";
    private static final int SITE_ID = 4;
    private static final String SUMMARY = "summary";
    private static final String CACHED_PAGE = "cachedPage";
    private static final String CHANGED_XML = "changedXml";
    private static final String DOCUMENT_ANCHOR_TEXT = "documentAnchorText";
    private static final String DOCUMENT_BODY = "documentBody";
    private static final String SERVER_DOC_ID = "serverDocId";
    private static final String EXTRA = "extra";
    private static final String FEED_NAME = "feedName";
    private static final String URL = "url";
    private static final String VERSION = "version";
    private static final String KEY = "key";
    private static final String VALUE = "value";

    private DocEntrySiteDocument docEntrySiteDocument;
    private DocEntry docEntry;

    @Test
    public void givenWhenSetGetTitle() {
        // Act
        docEntrySiteDocument.setTitle(DOCUMENT_TITLE);
        // Assert
        assertEquals(DOCUMENT_TITLE, docEntrySiteDocument.getTitle());
    }

    @Test
    public void givenWhenSetGetSiteDocID() {
        // Act
        docEntrySiteDocument.setSiteDocID(SITE_DOC_ID);
        // Assert
        assertEquals(SITE_DOC_ID, docEntrySiteDocument.getSiteDocID());
    }

    @Test
    public void givenWhenAttributes() {
        // Assert
        assertEquals(Collections.EMPTY_MAP, docEntrySiteDocument.attributes());
    }

    @Test
    public void givenWhenSetOriginalDocEntry() {
        // Act
        docEntrySiteDocument.setOriginalDocEntry(docEntry);
        // Assert
        assertSame(docEntry, docEntrySiteDocument.getOriginalDocEntry());
    }

    @Test
    public void givenWhenAddDocCatObj() {
        // Act
        docEntrySiteDocument.addDocCatObj(new FR_DocCategory());
        // Assert
        assertNull(docEntrySiteDocument.getDocCatObjs());
    }

    @Test
    public void givenWhenSetDocCatObjs() {
        // Act
        docEntrySiteDocument.setDocCatObjs(new ArrayList());
        // Assert
        assertNull(docEntrySiteDocument.getDocCatObjs());
    }

    @Test
    public void givenWhenAddRefSiteDocID() {
        // Act
        docEntrySiteDocument.addRefSiteDocID(new FR_SiteDocument());
        // Assert
        assertNull(docEntrySiteDocument.getRefSiteDocIDs());
    }

    @Test
    public void givenWhenDisableBits() {
        // Act
        docEntrySiteDocument.disableBits(new int[] {1});
        // Assert
        assertEquals(0, docEntrySiteDocument.getBitMap());
    }

    @Test
    public void givenWhenEnableBits() {
        // Act
        docEntrySiteDocument.enableBits(new int[] {1});
        // Assert
        assertEquals(0, docEntrySiteDocument.getBitMap());
    }

    @Test
    public void givenWhenSetGetBitMap() {
        // Act
        docEntrySiteDocument.setBitMap(1);
        // Assert
        assertEquals(0, docEntrySiteDocument.getBitMap());
    }

    @Test
    public void givenWhenSetGetArchive() {
        // Act
        docEntrySiteDocument.setArchive(1);
        // Assert
        assertEquals(0, docEntrySiteDocument.getArchive());
    }

    @Test
    public void givenWhenSetGetAttachmentCount() {
        // Act
        docEntrySiteDocument.setAttachmentCount(1);
        // Assert
        assertEquals(0, docEntrySiteDocument.getAttachmentCount());
    }

    @Test
    public void givenWhenSetGetAttachments() {
        // Act
        docEntrySiteDocument.setAttachments(new ArrayList());
        // Assert
        assertNull(docEntrySiteDocument.getAttachments());
    }

    @Test
    public void givenWhenSetGetCached() {
        // Act
        docEntrySiteDocument.setCached(1);
        // Assert
        assertEquals(0, docEntrySiteDocument.getCached());
    }

    @Test
    public void givenWhenSetGetCatIDs() {
        // Act
        docEntrySiteDocument.setCatIDs(new ArrayList());
        // Assert
        assertNull(docEntrySiteDocument.getCatIDs());
    }

    @Test
    public void givenWhenSetGetCatString() {
        // Act
        docEntrySiteDocument.setCatString(CAT_STRING);
        // Assert
        assertNull(docEntrySiteDocument.getCatString());
    }

    @Test
    public void givenWhenSetGetClusterID() {
        // Act
        docEntrySiteDocument.setClusterID(CLUSTER_ID);
        // Assert
        assertEquals(0, docEntrySiteDocument.getClusterID());
    }

    @Test
    public void givenWhenSetGetClusterPublishedDocCount() {
        // Act
        docEntrySiteDocument.setClusterPublishedDocCount(1);
        // Assert
        assertEquals(0, docEntrySiteDocument.getClusterPublishedDocCount());
    }

    @Test
    public void givenWhenSetGetClusterUpdateTime() {
        // Act
        docEntrySiteDocument.setClusterUpdateTime(new Timestamp(TIME_MILLIS));
        // Assert
        assertNull(docEntrySiteDocument.getClusterUpdateTime());
    }

    @Test
    public void givenWhenSetGetComments() {
        // Act
        docEntrySiteDocument.setComments(COMMENTS);
        // Assert
        assertNull(docEntrySiteDocument.getComments());
    }

    @Test
    public void givenWhenGetComparator() {
        // Assert
        assertNull(docEntrySiteDocument.getComparator(1));
    }

    @Test
    public void givenWhenSetGetDefMIPSourceID() {
        // Act
        docEntrySiteDocument.setDefMIPSourceID(1);
        // Assert
        assertEquals(0, docEntrySiteDocument.getDefMIPSourceID());
    }

    @Test
    public void givenWhenSetGetDefMIPSourceName() {
        // Act
        docEntrySiteDocument.setDefMIPSourceName(SOURCE_NAME);
        // Assert
        assertNull(docEntrySiteDocument.getDefMIPSourceName());
    }

    @Test
    public void givenWhenSetGetDiscussionCount() {
        // Act
        docEntrySiteDocument.setDiscussionCount(1);
        // Assert
        assertEquals(0, docEntrySiteDocument.getDiscussionCount());
    }

    @Test
    public void givenWhenGetDocBodyWithoutTitle() {
        // Assert
        assertNull(docEntrySiteDocument.getDocBodyWithoutTitle());
    }

    @Test
    public void givenWhenSetGetFlagDeleted() {
        // Act
        docEntrySiteDocument.setFlagDeleted(true);
        // Assert
        assertFalse(docEntrySiteDocument.getFlagDeleted());
    }

    @Test
    public void givenWhenSetGetFlagInput() {
        // Act
        docEntrySiteDocument.setFlagInput(1);
        // Assert
        assertEquals(0, docEntrySiteDocument.getFlagInput());
    }

    @Test
    public void givenWhenGetFlagPublished() {
        // Act
        docEntrySiteDocument.setFlagPublished(1);
        // Assert
        assertEquals(0, docEntrySiteDocument.getFlagPublished());
    }

    @Test
    public void givenWhenGetFolderType() {
        // Assert
        assertEquals(0, docEntrySiteDocument.getFolderType());
    }

    @Test
    public void givenWhenSetGetGroupID() {
        // Act
        docEntrySiteDocument.setGroupID(GROUP_ID);
        // Assert
        assertEquals(0L, docEntrySiteDocument.getGroupID());
    }

    @Test
    public void givenWhenGetGroupStatus() {
        // Act
        docEntrySiteDocument.setGroupStatus(1);
        // Assert
        assertEquals(0, docEntrySiteDocument.getGroupStatus());
    }

    @Test
    public void givenWhenSetGetLink1() {
        // Act
        docEntrySiteDocument.setLink1(LINK);
        // Assert
        assertNull(docEntrySiteDocument.getLink1());
    }

    @Test
    public void givenWhenSetGetLink2() {
        // Act
        docEntrySiteDocument.setLink2(LINK);
        // Assert
        assertNull(docEntrySiteDocument.getLink2());
    }

    @Test
    public void givenWhenGetOverallScore() {
        // Assert
        assertEquals(0, docEntrySiteDocument.getOverallScore());
    }

    @Test
    public void givenTimestampWhenGetOverallScore() {
        // Assert
        assertEquals(0, docEntrySiteDocument.getOverallScore(new Timestamp(TIME_MILLIS)));
    }

    @Test
    public void givenWhenSetGetPageChange() {
        // Act
        docEntrySiteDocument.setPageChange(1);
        // Assert
        assertEquals(0, docEntrySiteDocument.getPageChange());
    }

    @Test
    public void givenWhenSetGetPermanent() {
        // Act
        docEntrySiteDocument.setPermanent(true);
        // Assert
        assertFalse(docEntrySiteDocument.getPermanent());
    }

    @Test
    public void givenWhenSetGetPersonalFolderIDs() {
        // Act
        docEntrySiteDocument.setPersonalFolderIDs(new ArrayList());
        // Assert
        assertNull(docEntrySiteDocument.getPersonalFolderIDs());
    }

    @Test
    public void givenWhenSetGetPublishDate() {
        // Act
        docEntrySiteDocument.setPublishDate(new Date(TIME_MILLIS));
        // Assert
        assertNull(docEntrySiteDocument.getPublishDate());
    }

    @Test
    public void givenWhenSetGetQualityLevel() {
        // Act
        docEntrySiteDocument.setQualityLevel(1);
        // Assert
        assertEquals(0, docEntrySiteDocument.getQualityLevel());
    }

    @Test
    public void givenWhenSetGetQuotes() {
        // Act
        docEntrySiteDocument.setQuotes(Collections.<FR_IQuote>emptyList());
        // Assert
        assertNull(docEntrySiteDocument.getQuotes());
    }

    @Test
    public void givenWhenSetGetRefSiteDocCount() {
        // Act
        docEntrySiteDocument.setRefSiteDocCount(1);
        // Assert
        assertEquals(0, docEntrySiteDocument.getRefSiteDocCount());
    }

    @Test
    public void givenWhenSetGetRefSiteDocIDs() {
        // Act
        docEntrySiteDocument.setRefSiteDocIDs(new ArrayList());
        // Assert
        assertNull(docEntrySiteDocument.getRefSiteDocIDs());
    }

    @Test
    public void givenWhenSetGetRelatedDocCount() {
        // Act
        docEntrySiteDocument.setRelatedDocCount(1);
        // Assert
        assertEquals(0, docEntrySiteDocument.getRelatedDocCount());
    }

    @Test
    public void givenWhenGetSentenceMarkedDocBodyWithoutTitle() {
        // Assert
        assertNull(docEntrySiteDocument.getSentenceMarkedDocBodyWithoutTitle());
    }

    @Test
    public void givenWhenGetSentenceMarkedSummary() {
        // Assert
        assertNull(docEntrySiteDocument.getSentenceMarkedSummary());
    }

    @Test
    public void givenWhenGetSharable() {
        // Assert
        assertEquals(0, docEntrySiteDocument.getSharable());
    }

    @Test
    public void givenWhenSetGetSiteDocInsertTime() {
        // Act
        docEntrySiteDocument.setSiteDocInsertTime(new Timestamp(TIME_MILLIS));
        // Assert
        assertNull(docEntrySiteDocument.getSiteDocInsertTime());
    }

    @Test
    public void givenWhenSetGetSiteDocSubCatIDList() {
        // Act
        docEntrySiteDocument.setSiteDocSubCatIDList(new ArrayList());
        // Assert
        assertNull(docEntrySiteDocument.getSiteDocSubCatIDList());
    }

    @Test
    public void givenWhenSetGetSiteDocUpdateTime() {
        // Act
        docEntrySiteDocument.setSiteDocUpdateTime(new Timestamp(TIME_MILLIS));
        // Assert
        assertNull(docEntrySiteDocument.getSiteDocUpdateTime());
    }

    @Test
    public void givenWhenSetGetSiteDocumentScore() {
        // Act
        docEntrySiteDocument.setSiteDocumentScore(new FR_SiteDocumentScore());
        // Assert
        assertNull(docEntrySiteDocument.getSiteDocumentScore());
    }

    @Test
    public void givenWhenSetGetSiteHistoryInsertTime() {
        // Act
        docEntrySiteDocument.setSiteHistoryInsertTime(new Timestamp(TIME_MILLIS));
        // Assert
        assertNull(docEntrySiteDocument.getSiteHistoryInsertTime());
    }

    @Test
    public void givenWhenSetGetSiteID() {
        // Act
        docEntrySiteDocument.setSiteID(SITE_ID);
        // Assert
        assertEquals(0, docEntrySiteDocument.getSiteID());
    }

    @Test
    public void givenWhenSetGetSize() {
        // Act
        docEntrySiteDocument.setSize(1L);
        // Assert
        assertEquals(0, docEntrySiteDocument.getSize());
    }

    @Test
    public void givenWhenSetGetSummary() {
        // Act
        docEntrySiteDocument.setSummary(SUMMARY);
        // Assert
        assertNull(docEntrySiteDocument.getSummary());
    }

    @Test
    public void givenWhenSetGetTitleHashID() {
        // Act
        docEntrySiteDocument.setTitleHashID(1L);
        // Assert
        assertEquals(0L, docEntrySiteDocument.getTitleHashID());
    }

    @Test
    public void givenWhenSetGetUserEntityPrefBitMap() {
        // Act
        docEntrySiteDocument.setUserEntityPrefBitMap(1);
        // Assert
        assertEquals(0, docEntrySiteDocument.getUserEntityPrefBitMap());
    }

    @Test
    public void givenWhenSetGetUserID() {
        // Act
        docEntrySiteDocument.setUserID(1);
        // Assert
        assertEquals(0, docEntrySiteDocument.getUserID());
    }

    @Test
    public void givenWhenHasChanged() {
        // Assert
        assertFalse(docEntrySiteDocument.hasChanged());
    }

    @Test
    public void givenWhenIsArchived() {
        // Assert
        assertFalse(docEntrySiteDocument.isArchived());
    }

    @Test
    public void givenWhenIsBitEnabled() {
        // Assert
        assertFalse(docEntrySiteDocument.isBitEnabled(1));
    }

    @Test
    public void givenWhenIsCached() {
        // Assert
        assertFalse(docEntrySiteDocument.isCached());
    }

    @Test
    public void givenWhenSetIsSentInDigest() {
        // Act
        docEntrySiteDocument.setSentInDigest(true);
        // Assert
        assertFalse(docEntrySiteDocument.isSentInDigest());
    }

    @Test
    public void givenWhenSetIsSharable() {
        // Act
        docEntrySiteDocument.setSharable(1);
        // Assert
        assertFalse(docEntrySiteDocument.isSharable());
    }

    @Test
    public void givenWhenSetGetAccessType() {
        // Act
        docEntrySiteDocument.setAccessType(1);
        // Assert
        assertEquals(0, docEntrySiteDocument.getAccessType());
    }

    @Test
    public void givenWhenSetGetCachedPage() {
        // Act
        docEntrySiteDocument.setCachedPage(CACHED_PAGE);
        // Assert
        assertNull(docEntrySiteDocument.getCachedPage());
    }

    @Test
    public void givenWhenSetGetChangedXml() {
        // Act
        docEntrySiteDocument.setChangedXml(CHANGED_XML);
        // Assert
        assertNull(docEntrySiteDocument.getChangedXml());
    }

    @Test
    public void givenWhenSetGetDocAnchorText() {
        // Act
        docEntrySiteDocument.setDocAnchorText(DOCUMENT_ANCHOR_TEXT);
        // Assert
        assertNull(docEntrySiteDocument.getDocAnchorText());
    }

    @Test
    public void givenWhenSetGetDocBody() {
        // Act
        docEntrySiteDocument.setDocBody(DOCUMENT_BODY);
        // Assert
        assertNull(docEntrySiteDocument.getDocBody());
    }

    @Test
    public void givenWhenSetGetDocID() {
        // Act
        docEntrySiteDocument.setDocID(SITE_DOC_ID);
        // Assert
        assertEquals(0L, docEntrySiteDocument.getDocID());
    }

    @Test
    public void givenWhenSetGetDserverDocID() {
        // Act
        docEntrySiteDocument.setDserverDocID(SERVER_DOC_ID);
        // Assert
        assertNull(docEntrySiteDocument.getDserverDocID());
    }

    @Test
    public void givenWhenSetGetExtra1() {
        // Act
        docEntrySiteDocument.setExtra1(EXTRA);
        // Assert
        assertNull(docEntrySiteDocument.getExtra1());
    }

    @Test
    public void givenWhenSetGetExtra2() {
        // Act
        docEntrySiteDocument.setExtra2(EXTRA);
        // Assert
        assertNull(docEntrySiteDocument.getExtra2());
    }

    @Test
    public void givenWhenSetGetFeedID() {
        // Act
        docEntrySiteDocument.setFeedID(1);
        // Assert
        assertEquals(0, docEntrySiteDocument.getFeedID());
    }

    @Test
    public void givenWhenSetGetFeedName() {
        // Act
        docEntrySiteDocument.setFeedName(FEED_NAME);
        // Assert
        assertNull(docEntrySiteDocument.getFeedName());
    }

    @Test
    public void givenWhenSetGetInsertTime() {
        // Act
        docEntrySiteDocument.setInsertTime(new Timestamp(TIME_MILLIS));
        // Assert
        assertEquals(TIME_MILLIS, docEntrySiteDocument.getInsertTime().getTime());
    }

    @Test
    public void givenWhenSetGetMimeType() {
        // Act
        docEntrySiteDocument.setMimeType(1);
        // Assert
        assertEquals(0, docEntrySiteDocument.getMimeType());
    }

    @Test
    public void givenWhenSetGetRemoteUrl() {
        // Act
        docEntrySiteDocument.setRemoteUrl(URL);
        // Assert
        assertNull(docEntrySiteDocument.getRemoteUrl());
    }

    @Test
    public void givenWhenGetSentenceMarkedDocBody() {
        // Assert
        assertNull(docEntrySiteDocument.getSentenceMarkedDocBody());
    }

    @Test
    public void givenWhenSetGetSourceTypeID() {
        // Act
        docEntrySiteDocument.setSourceTypeID(1);
        // Assert
        assertEquals(0, docEntrySiteDocument.getSourceTypeID());
    }

    @Test
    public void givenWhenSetGetStartUrlIDs() {
        // Act
        docEntrySiteDocument.setStartUrlIDs(new int[] {1});
        // Assert
        assertNull(docEntrySiteDocument.getStartUrlIDs());
    }

    @Test
    public void givenWhenSetGetUpdateTime() {
        // Act
        docEntrySiteDocument.setUpdateTime(new Timestamp(TIME_MILLIS));
        // Assert
        assertNull(docEntrySiteDocument.getUpdateTime());
    }

    @Test
    public void givenWhenSetGetUrlHashID() {
        // Act
        docEntrySiteDocument.setUrlHashID(1L);
        // Assert
        assertEquals(0L, docEntrySiteDocument.getUrlHashID());
    }

    @Test
    public void givenWhenSetGetVersion1() {
        // Act
        docEntrySiteDocument.setVersion1(VERSION);
        // Assert
        assertNull(docEntrySiteDocument.getVersion1());
    }

    @Test
    public void givenWhenSetGetVersion2() {
        // Act
        docEntrySiteDocument.setVersion2(VERSION);
        // Assert
        assertNull(docEntrySiteDocument.getVersion2());
    }

    @Test
    public void givenWhenClearAddGet() {
        // Arrange
        docEntrySiteDocument.clear();
        // Act
        docEntrySiteDocument.add(KEY, VALUE);
        // Assert
        assertNull(docEntrySiteDocument.get(KEY));
    }

    @Before
    public void setUp() {
        docEntry = new DocEntry();
        docEntry.sitedocId = String.valueOf(SITE_DOC_ID);
        docEntry.insertTime = new Date(TIME_MILLIS);
        docEntrySiteDocument = new DocEntrySiteDocument(docEntry);
    }

}
