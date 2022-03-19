package com.firstrain.frapi.obj;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.firstrain.mip.object.FR_IDocCategory;
import com.firstrain.mip.object.FR_IQuote;
import com.firstrain.mip.object.FR_ISiteDocument;
import com.firstrain.mip.object.FR_ISiteDocumentScore;
import com.firstrain.solr.client.DocEntry;

public class DocEntrySiteDocument implements FR_ISiteDocument {

	private String title;
	private long siteDocID;
	private DocEntry originalDocEntry;
	private Timestamp insertTime;


	public DocEntrySiteDocument(DocEntry docEntry) {
		this.title = docEntry.title;
		this.siteDocID = Long.valueOf(docEntry.sitedocId);
		this.originalDocEntry = docEntry;
		this.setInsertTime(new Timestamp(docEntry.getInsertTime().getTime()));
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public long getSiteDocID() {
		return siteDocID;
	}

	@Override
	public void setSiteDocID(long siteDocID) {
		this.siteDocID = siteDocID;
	}

	@Override
	public Map attributes() {
		return Collections.EMPTY_MAP;
	}

	@Override
	public void addDocCatObj(FR_IDocCategory frIDocCategory) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addRefSiteDocID(FR_ISiteDocument frISiteDocument) {
		// TODO Auto-generated method stub

	}

	@Override
	public void disableBits(int[] bits) {
		// TODO Auto-generated method stub

	}

	@Override
	public void enableBits(int[] bits) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getArchive() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getAttachmentCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ArrayList getAttachments() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getBitMap() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getCached() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ArrayList getCatIDs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCatString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getClusterID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getClusterPublishedDocCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Timestamp getClusterUpdateTime() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getComments() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Comparator getComparator(int sortType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getDefMIPSourceID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getDefMIPSourceName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getDiscussionCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getDocBodyWithoutTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList getDocCatObjs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getFlagDeleted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getFlagInput() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getFlagPublished() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getFolderType() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getGroupID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getGroupStatus() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getLink1() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLink2() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getOverallScore() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getOverallScore(Timestamp timestamp) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getPageChange() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean getPermanent() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List getPersonalFolderIDs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getPublishDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getQualityLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<FR_IQuote> getQuotes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getRefSiteDocCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ArrayList getRefSiteDocIDs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getRelatedDocCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getSentenceMarkedDocBodyWithoutTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSentenceMarkedSummary() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getSharable() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Timestamp getSiteDocInsertTime() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList getSiteDocSubCatIDList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Timestamp getSiteDocUpdateTime() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FR_ISiteDocumentScore getSiteDocumentScore() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Timestamp getSiteHistoryInsertTime() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getSiteID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getSummary() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getTitleHashID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getUserEntityPrefBitMap() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getUserID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean hasChanged() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isArchived() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isBitEnabled(int bit) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCached() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSentInDigest() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSharable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setArchive(int archive) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setAttachmentCount(int cnt) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setAttachments(ArrayList attachments) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setBitMap(int bitMap) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setCached(int cached) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setCatIDs(ArrayList catIDs) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setCatString(String catString) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setClusterID(long clusterID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setClusterPublishedDocCount(int count) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setClusterUpdateTime(Timestamp clusterUpdateTime) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setComments(String comments) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDefMIPSourceID(int defMIPSourceID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDefMIPSourceName(String defMIPSourceName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDiscussionCount(int discussionCount) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDocCatObjs(ArrayList docCatObjs) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFlagDeleted(boolean flagDeleted) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFlagInput(int flagInput) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFlagPublished(int flagPublished) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setGroupID(long groupID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setGroupStatus(int groupStatus) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setLink1(String link1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setLink2(String link2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPageChange(int pageChange) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPermanent(boolean permanent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPersonalFolderIDs(List personalFolderIDs) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPublishDate(Date publishDate) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setQualityLevel(int qualityLevel) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setQuotes(List<FR_IQuote> quotesList) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setRefSiteDocCount(int cnt) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setRefSiteDocIDs(ArrayList refSiteDocIDs) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setRelatedDocCount(int relatedDocCount) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSentInDigest(boolean sentInDigest) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSharable(int sharable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSiteDocInsertTime(Timestamp siteDocInsertTime) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSiteDocSubCatIDList(ArrayList siteDocSubCatIDList) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSiteDocUpdateTime(Timestamp siteDocUpdateTime) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSiteDocumentScore(FR_ISiteDocumentScore frISiteDocumentScore) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSiteHistoryInsertTime(Timestamp siteHistoryInsertTime) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSiteID(int siteID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSize(long size) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSummary(String summary) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTitleHashID(long titleHashID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setUserEntityPrefBitMap(int userEntityPrefBitMap) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setUserID(int userID) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getAccessType() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCachedPage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getChangedXml() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDocAnchorText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDocBody() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getDocID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getDserverDocID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getExtra1() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getExtra2() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getFeedID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getFeedName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Timestamp getInsertTime() {
		// TODO Auto-generated method stub
		return insertTime;
	}

	@Override
	public int getMimeType() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getRemoteUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSentenceMarkedDocBody() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getSourceTypeID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int[] getStartUrlIDs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Timestamp getUpdateTime() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getUrlHashID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getVersion1() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getVersion2() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAccessType(int accesType) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setCachedPage(String cachedPage) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setChangedXml(String changedXml) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDocAnchorText(String docAnchorText) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDocBody(String docBody) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDocID(long docID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDserverDocID(String serverDocID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setExtra1(String extra1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setExtra2(String extra2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFeedID(int feedID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFeedName(String feedName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setInsertTime(Timestamp insertTime) {
		// TODO Auto-generated method stub
		this.insertTime = insertTime;
	}

	@Override
	public void setMimeType(int mimeType) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setRemoteUrl(String remoteUrl) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSourceTypeID(int sourceTypeID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setStartUrlIDs(int[] startUrlIDs) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setUpdateTime(Timestamp updateTime) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setUrlHashID(long urlHashID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setVersion1(String version1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setVersion2(String version2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void add(String frIKey, Object value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	@Override
	public Object get(String frIKey) {
		// TODO Auto-generated method stub
		return null;
	}

	public DocEntry getOriginalDocEntry() {
		return originalDocEntry;
	}

	public void setOriginalDocEntry(DocEntry originalDocEntry) {
		this.originalDocEntry = originalDocEntry;
	}

}
