/*
 * Copyright (c) 2014, FirstRain, Inc. All Rights Reserved The contents of this file are the property of FirstRain, Inc. and are subject to
 * a License agreement with FirstRain; you may not use this file except in compliance with that License. You may obtain a copy of that
 * license from: Legal Department FirstRain, Inc. 1510 Fashion Island Blvd Suite 120 San Mateo, CA 94404 This software is distributed under
 * the License and is provided on an "AS IS" basis, without warranty of any kind, either express or implied, unless otherwise provided in
 * the License. See the License for governing rights and limitations under the License.
 */
package com.firstrain.feed.processor;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.solr.client.solrj.SolrServer;

import com.firstrain.content.pipeline.DefaultProcessor;
import com.firstrain.content.pipeline.PipelineComponent;
import com.firstrain.content.pipeline.PipelineDocumentContext;
import com.firstrain.content.pipeline.PipelineProcessor;
import com.firstrain.content.similarity.DocumentSimilarityUtil;
import com.firstrain.content.util.ProgressMark;
import com.firstrain.content.util.ProgressMarkStore;
import com.firstrain.feed.utils.FeedUtils;
import com.firstrain.frapi.domain.BaseSpec;
import com.firstrain.frapi.util.ContentType;
import com.firstrain.frapi.util.FRAPIConstant;
import com.firstrain.obj.IEntityInfoCache;
import com.firstrain.solr.client.DocCatEntry;
import com.firstrain.solr.client.DocEntriesUpdator;
import com.firstrain.solr.client.DocEntry;
import com.firstrain.solr.client.EntityEntry;
import com.firstrain.solr.client.EntityInfoCacheRegistry;
import com.firstrain.solr.client.SearchException;
import com.firstrain.solr.client.SearchResult;
import com.firstrain.solr.client.SearchSpec;
import com.firstrain.solr.client.SearchTokenEntry;
import com.firstrain.solr.client.SolrSearcher;
import com.firstrain.solr.client.util.SolrServerReader;
import com.firstrain.utils.FR_ArrayUtils;
import com.firstrain.utils.FR_DateUtils;
import com.firstrain.utils.JSONUtility;
import com.firstrain.utils.TimeUtils;
import com.firstrain.web.pojo.Content;
import com.firstrain.web.pojo.Document;
import com.firstrain.web.pojo.EntityData;
import com.firstrain.web.response.EntityDataResponse;
import com.firstrain.web.response.JSONResponse.ResStatus;
import com.firstrain.web.wrapper.EntityDataWrapper;

/**
 * @author Gkhanchi
 * 
 */
public class DocumentsFeedProcessor extends DefaultProcessor {
	private static final Logger LOG = Logger.getLogger(DocumentsFeedProcessor.class);
	private String mailTo = "";
	private String mailFrom = "";
	private String mailHost = "";
	private String feedfilename = "";
	private int startMinuteId = 0;
	private int chunkSize = 100;
	private long entityIndexVersion = -1;
	private String destLocation = null;
	private String tempLocation = null;
	private String pipeLineId = "";
	private String fileNameFormat = "yyyyMMddHHmm";
	private static SolrSearcher searcher = null;
	private static volatile IEntityInfoCache eCache = null;
	private Set<Integer> companyCatIdsSet = null;
	private Map<String, Set<Integer>> companiesToDisplay = null;
	private Set<Integer> narrowBandCatSet = null;
	private Set<Integer> mediumBandCatSet = null;
	private Set<Integer> broadBandCatSet = null;
	private static volatile Set<Integer> reltopicCatIdsSet = null;
	private SolrServer entitySolrServer = null;
	private SolrServer docSolrServer = null;
	private String docSolrServerURL = null;
	private String entitySolrServerURL = null;
	private String imageServiceURLsCSV = null;
	private SolrServer docImageServer = null;
	private String progressPath = "";
	private ProgressMarkStore progressStore = null;
	private String excludeSourceIds = null;
	private short industryClassificationId;
	private short webResultsTotalCount;
	private String docImageServerURL;
	private List<Integer> topicDim;
	private List<String> regionsSearchTokensList;
	private List<Integer> regionsBandList;
	private String topicDimensionsCSV;
	private String regionsSearchTokensCSV;
	private String regionsBandCSV;
	private String relTopicsFilePath;
	private String compFiltersFilePath;
	private String entitySearchToken;
	private String filterSearchToken;
	private static final int DISTANCE_THRESHHOLD = 50;
	private DocumentSimilarityUtil documentSimilarityUtil;

	/*
	 * The following flag covers the case wherein we try to save the progress marker. without getting it in sync with current runtime
	 * progress marker or previously saved progress marker.
	 */
	private boolean progressMarkerUpdated = false;
	private String docSimilarityPropPath;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.firstrain.content.pipeline.PipelineProcessor#process(com.firstrain.content.pipeline.PipelineDocumentContext)
	 */
	@Override
	public void process(PipelineDocumentContext ctx) throws Throwable {
		Statistics stats = (Statistics) ctx.getStatistics();
		try {
			List<DocEntry> docList = null;
			// -1 is for database connection error while loading progress mark
			if (this.startMinuteId != -1) {
				docList = getWebResults(entitySearchToken);
				LOG.info("Items Produced: " + (docList != null ? docList.size() : 0));
				docList = createDocEntryList(docList);
			} else {
				stats.statDelta("Error", 1);
			}

			if (docList != null && !docList.isEmpty()) {
				// documents are order by date in descending -> changing to ascending order to save progress mark between chunks
				processAndSave(docList, stats);
			} else {
				// make empty json file
				processContent(null, 0, 0, stats);
				LOG.warn("Nothing fetched for this cycle.");
				stats.statDelta("empty", 1);
			}
			stats.statDelta("success", 1);
			LOG.info("Cycle Completed...");
		} catch (Throwable th) {
			stats.statDelta("Error", 1);
			FeedUtils.alertOperator("Exception in process method: " + th.getMessage(), mailTo, mailFrom, mailHost);
			throw th;
		}
	}

	private void processAndSave(final List<DocEntry> docList, final Statistics stats) throws Exception {
	    Collections.reverse(docList);
	    List<List<DocEntry>> subLists = FR_ArrayUtils.splitListIntoSmallChunks(docList, chunkSize);
	    // iterate on chunksize to generate files
	    int i = 1;
	    for (List<DocEntry> sublist : subLists) {
	    	LOG.info("size : " + sublist.size());
	    	List<Document> resDocList = getDocDetails(sublist);
	    	processContent(resDocList, i, docList.size(), stats);
	    	i = i + chunkSize;
	    	// it is safe to say that now we can save this marker
	    	if (!sublist.isEmpty()) {
	    		this.startMinuteId = sublist.get(sublist.size() - 1).getMinuteId();
	    		progressMarkerUpdated = true;
	    		int backlog = 0;
	    		if (sublist.size() > i) {
	    			backlog = sublist.size() - i;
	    		}
	    		saveProgress(backlog);
	    	}
	    }
	}

	private List<DocEntry> createDocEntryList(List<DocEntry> docListParam) {
	    List<DocEntry> docList = docListParam;
	    Set<Integer> topicDim123Set = new HashSet<Integer>();
	    // filter web results on the basis of company filters and region filters
	    docList = filterWebResults(docList, topicDim123Set);
	    LOG.info("After filtering Items size: " + (docList != null ? docList.size() : 0));
	    documentSimilarityUtil.setDimensions(topicDim123Set);
	    // apply document title similarity exclusion
	    docList = FeedUtils.filterSimilarEntries(docList, this.documentSimilarityUtil, -1, true);
	    LOG.info("After document title and summary similarity exclusion, Items size: " + (docList != null ? docList.size() : 0));
	    // apply document summary similarity exclusion
	    // docList = FeedUtils.filterSimilarEntries(docList);
	    // LOG.info("After document summary similarity exclusion, Items size: " + (docList != null ? docList.size() : 0));
	    
	    LOG.info("chunkSize : " + chunkSize);
	    return docList;
	}

	/**
	 * @param htmlContent
	 * @param stats
	 * @throws Exception
	 */
	private void processContent(List<Document> docList, int start, int totalItemCount, Statistics stats) throws Exception {
		LOG.info("Doc list size : " + (docList != null ? docList.size() : 0));
		LOG.info("Processing Document ...");

		// Date Format to name json File
		SimpleDateFormat sdf = new SimpleDateFormat(this.fileNameFormat);
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		String currentDate = sdf.format(new Date());

		String FeedFileName = feedfilename + currentDate;
		FeedFileName = FeedUtils.getUniqueFileName(destLocation, FeedFileName);
		boolean state = false;
		String jsonfilename = FeedFileName + ".json";
		// String ZipFileName = FeedFileName+".zip";
		String jsontempLocation = tempLocation + File.separator + jsonfilename;
		// String ZiptempLocation = tempLocation + "\\" + ZipFileName;

		EntityDataResponse edr = getEntityDataResponse(docList, jsonfilename, start, totalItemCount);
		String jsonResult = JSONUtility.serialize(edr);

		// Create JSON file
		state = FeedUtils.createJSONFile(jsonResult, jsontempLocation);
		LOG.info("JSON File Name : " + jsonfilename);
		if (state == false) {
			FeedUtils.alertOperator(pipeLineId + " :: JSON File Creation Failed", mailTo, mailFrom, mailHost);
			LOG.error("JSON File Creation Failed");
		}
		// Output ZIP File
		/*
		 * LOG.info("Zip File Name : "+ZipFileName); state = FeedUtils.createZipfile(jsontempLocation, jsonfilename, ZiptempLocation); if
		 * (state == false){ FeedUtils.alertOperator(pipeLineId+" :: Zip File Creation Failed",mailTo,mailFrom,mailHost); LOG.error(
		 * "Zip File Creation Failed"); }
		 */

		String destLoc = destLocation;
		// deleting old files -> older than backupDays
		// FeedUtils.deleteOldFiles(destLoc, this.fileNameFormat, this.backupDays);
		// LOG.info("old file deleted.");

		// FTP Server File Repository Path
		if (!destLoc.equals(""))
			destLoc = destLoc + File.separator + jsonfilename;

		// Copy File to FTP Locations
		LOG.info("Copy JSON file From " + jsontempLocation + " To " + destLoc);
		try {
			state = FeedUtils.copy(jsontempLocation, destLoc);
		} catch (Exception e) {
			LOG.error("Error in DocumentFeedProcessor while copying JSON file" + e.getMessage(), e);
			stats.statDelta("Error", 1);
		}
		if (!state) {
			if (!destLoc.equals("")) {
				FeedUtils.alertOperator(pipeLineId + " :: File Copy Failed", mailTo, mailFrom, mailHost);
			}
			LOG.error("JSON Copy Failed");
		} else {
			// On Complete success of the cycle, delete the JSON file from local locations.
			File jsonFile = new File(jsontempLocation);
			if (jsonFile.exists()) {
				jsonFile.delete();
			}
		}

		/*
		 * File src = new File(ZiptempLocation); if(src.exists()){ src.delete(); }
		 */

		stats.statDelta("processed", (docList != null ? docList.size() : 0));

	}

	private List<DocEntry> getWebResults(String q) throws Exception {
		List<DocEntry> entries = null;
		try {
			BaseSpec spec = new BaseSpec();

			String filter = "";
			if (filterSearchToken != null && !filterSearchToken.trim().isEmpty()) {
				filter = " AND " + filterSearchToken.trim();
			}
			// I:SWIndustry AND RF:UnitedStatesofAmerica -F:SageworkExclusionFilterTopic -F:10QFilings -F:8KFilings -F:10KFilings
			// -F:SECForm345 -F:CallTranscripts
			String[] qMulti = new String[] {q + filter};
			int[] scopeMulti = new int[] {SearchSpec.SCOPE_BROAD};

			spec.setIndustryClassificationId(this.industryClassificationId);
			spec.setCount(this.webResultsTotalCount);

			SearchResult searchResult = getSearchResult(qMulti, scopeMulti, null, spec);
			entries = searchResult.buckets.get(0).docs;
			if (entries == null || entries.isEmpty()) {
				return null;
			}
			try {
				DocEntriesUpdator.attachFavIconNDocImageDetails(null, docImageServer, entries);
			} catch (Exception e) {
				LOG.error("Exception attching doc image: " + e.getMessage(), e);
				FeedUtils.alertOperator("Exception attching doc image: " + e.getMessage(), mailTo, mailFrom, mailHost);
			}
			SolrSearcher.setupMatchesForEntries(entries, -1);
		} catch (Exception e) {
			LOG.error("Exception Getting Serach Results for entity brief Service", e);
			throw e;
		}
		return entries;
	}

	private SearchResult getSearchResult(String[] qMulti, int[] scopeMulti, String fq, BaseSpec spec) throws Exception {
		SearchResult sr = null;
		try {

			SearchSpec searchSpec = new SearchSpec(new SearchSpec().setTagExclusionScope(SearchSpec.SCOPE_BROAD));

			searchSpec.needHighlighting = false;
			searchSpec.needHotListAll = false;
			searchSpec.needSearchSuggestion = false;
			searchSpec.useLikelySearchIntention = false;
			searchSpec.needBodyLength = true;
			searchSpec.needQuotes = true;
			searchSpec.setOrder(SearchSpec.ORDER_DATE);
			searchSpec.start = 0;
			searchSpec.setRows(spec.getCount());
			searchSpec.setStartMinuteId(this.startMinuteId);

			if (qMulti.length >= FRAPIConstant.SEARCHTOKEN_COUNT) {
				String[] arr = new String[FRAPIConstant.SEARCHTOKEN_COUNT];
				int[] sArr = new int[FRAPIConstant.SEARCHTOKEN_COUNT];

				System.arraycopy(qMulti, 0, arr, 0, FRAPIConstant.SEARCHTOKEN_COUNT);
				System.arraycopy(scopeMulti, 0, sArr, 0, FRAPIConstant.SEARCHTOKEN_COUNT);

				searchSpec.qMulti = arr;
				searchSpec.scopeMulti = sArr;
			} else {
				searchSpec.qMulti = qMulti;
				searchSpec.scopeMulti = scopeMulti;
			}

			if (fq != null && !fq.isEmpty()) {
				searchSpec.fq = fq;
			}

			if (spec.getExcludeArticleIdsSSV() != null && !spec.getExcludeArticleIdsSSV().isEmpty()) {
				searchSpec.setExcludeDocIds(spec.getExcludeArticleIdsSSV());
			}
			if (spec.getIndustryClassificationId() != null) {
				searchSpec.setIndustryClassificationId(spec.getIndustryClassificationId());
			}
			if (spec.getExcludeSourceIdsSSV() != null && !spec.getExcludeSourceIdsSSV().isEmpty()) {
				searchSpec.setExcludeSourceIds(spec.getExcludeSourceIdsSSV());
			} else if (spec.getIncludeSourceIdsSSV() != null && !spec.getIncludeSourceIdsSSV().isEmpty()) {
				searchSpec.setIncludeSourceIds(spec.getIncludeSourceIdsSSV());
			}

			sr = searcher.search(searchSpec);

		} catch (SearchException e) {
			LOG.error("Exception Getting Web Results From Searcher!", e);
			throw e;
		}
		return sr;
	}

	private List<Document> getDocDetails(List<DocEntry> docEntryList) throws Exception {
		List<Document> documentList = new ArrayList<Document>();
		try {
			LOG.debug("No. of results from solr are " + (docEntryList != null ? docEntryList.size() : 0));
			if (docEntryList == null || docEntryList.isEmpty()) {
				return documentList;
			}
			FeedUtils.attachDocSummaryImageAndHighlightQuote(docEntryList, docImageServer);

			for (DocEntry entry : docEntryList) {
				ContentType contentType = FeedUtils.getContentTypeAndFilterOutCT(entry, null);
				Document docPOJO = FeedUtils.convertDocumentPOJOFromDocEntry(entry, this.topicDim,
						this.companiesToDisplay.get(entry.getSitedocId()), reltopicCatIdsSet);
				docPOJO.setContentType(contentType.name());
				documentList.add(docPOJO);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw e;
		}
		return documentList;
	}

	private List<DocEntry> filterWebResults(List<DocEntry> docList, Set<Integer> topicDim123Set) {
		if (docList == null) {
			return null;
		}
		List<DocEntry> resList = new ArrayList<DocEntry>();
		this.companiesToDisplay = new HashMap<String, Set<Integer>>();
		// iterate on each document to apply company and region filtering
		for (DocEntry entry : docList) {
			List<DocCatEntry> docCatList = entry.getCatEntries();
			if (docCatList != null) {
				boolean catFilterPassed = false;
				boolean regionFilterPassed = false;
				Set<Integer> companyCatIds = new HashSet<Integer>();
				Set<Integer> topicIds = new HashSet<Integer>();
				// iterate on all doc categories to apply filter on the basis of Band
				for (DocCatEntry catEntry : docCatList) {
					EntityEntry entity = catEntry.getEntity();
					if (entity.isValid() && !entity.isExcluded()) {
						// apply filter for Regions
						if (entity.getType() == SearchTokenEntry.SEARCH_TOKEN_TAG_REGION) {
							// Only regions with band in excludedRegionBandList should be the part of feed
							if (regionsSearchTokensList.contains(entity.searchToken)
									&& regionsBandList.contains((int) catEntry.getBand())) {
								regionFilterPassed = true;
							}
						} else if (entity.getType() == SearchTokenEntry.SEARCH_TOKEN_TAG_COMPANY) { // apply filter for Companies
							int entityId = Integer.parseInt(entity.getId());
							if (narrowBandCatSet.contains(entityId)) {
								if (catEntry.getBand() == DocCatEntry.BAND_SELECTIVE) {
									companyCatIds.add(entityId);
									catFilterPassed = true;
								}
							} else if (mediumBandCatSet.contains(entityId)) {
								if (catEntry.getBand() == DocCatEntry.BAND_SELECTIVE || catEntry.getBand() == DocCatEntry.BAND_GOOD) {
									companyCatIds.add(entityId);
									catFilterPassed = true;
								}
							} else if (broadBandCatSet.contains(entityId)) {
								companyCatIds.add(entityId);
								catFilterPassed = true;
							}
						} else if (entity.getType() == SearchTokenEntry.SEARCH_TOKEN_TAG_FUNDAMENTAL
								|| entity.getType() == SearchTokenEntry.SEARCH_TOKEN_TAG_SECTOR_TOPIC) {
							topicIds.add(Integer.parseInt(entity.getId()));
						}
					}
				}
				// add document in the filtered doc list
				if (catFilterPassed && regionFilterPassed) {
					resList.add(entry);
					companiesToDisplay.put(entry.getSitedocId(), companyCatIds);
					topicDim123Set.addAll(companyCatIds);
					topicDim123Set.addAll(topicIds);
				} else {
					LOG.debug("Filtering document id: " + entry + " catFilterPassed : " + catFilterPassed + " regionFilterPassed: "
							+ regionFilterPassed);
				}
			}
		}
		return resList;
	}

	private void loadFilterCatIdsSet(String filePath) throws Exception {
		narrowBandCatSet = new HashSet<Integer>();
		mediumBandCatSet = new HashSet<Integer>();
		try {
			XSSFWorkbook wb = new XSSFWorkbook(filePath);
			// load narrow band company catIds
			XSSFSheet sheet = wb.getSheet("Narrow");
			if (sheet != null) {
				int rows = sheet.getPhysicalNumberOfRows();
				for (int r = 1; r < rows; r++) {
					XSSFRow row = sheet.getRow(r);
					if (row == null) {
						continue;
					}
					if (row.getCell(1) != null && row.getCell(1).getCellType() != Cell.CELL_TYPE_BLANK) {
						narrowBandCatSet.add((int) row.getCell(1).getNumericCellValue());
					}
				}
			}

			// load medium band company catIds
			sheet = wb.getSheet("Medium");
			if (sheet != null) {
				int rows = sheet.getPhysicalNumberOfRows();
				for (int r = 1; r < rows; r++) {
					XSSFRow row = sheet.getRow(r);
					if (row == null) {
						continue;
					}
					if (row.getCell(1) != null && row.getCell(1).getCellType() != Cell.CELL_TYPE_BLANK) {
						mediumBandCatSet.add((int) row.getCell(1).getNumericCellValue());
					}
				}
			}

			LOG.info("narrowBandCatSet size: " + narrowBandCatSet.size() + " , mediumBandCatSet size: " + mediumBandCatSet.size()
					+ ", loaded from file: " + filePath);
		} catch (Exception e) {
			LOG.error("Error while loading company filters from: " + filePath, e);
			throw e;
		}
		broadBandCatSet = new HashSet<Integer>(this.companyCatIdsSet);
		broadBandCatSet.removeAll(narrowBandCatSet);
		broadBandCatSet.removeAll(mediumBandCatSet);
	}

	private EntityDataResponse getEntityDataResponse(List<Document> documentList, String fileName, int start, int totalItemCount)
			throws Exception {

		EntityDataResponse res = new EntityDataResponse();
		res.setStatus(ResStatus.SUCCESS);
		res.setMessage("Data populated successfully.");

		EntityDataWrapper entityDataWrapper = new EntityDataWrapper();
		EntityData entityData = new EntityData();
		// set monitorId and name
		entityDataWrapper.setId(fileName);
		entityDataWrapper.setName(fileName);

		Content content = new Content();
		content.setItemOffset(start);
		content.setTotalItemCount(totalItemCount);
		content.setItemCount(0);
		entityData.setFr(content);

		// set Content Document
		if (documentList != null && !documentList.isEmpty()) {
			content.setDocuments(documentList);
			content.setItemCount(documentList.size());
		} else {
			content.setDocuments(new ArrayList<Document>());// for empty json file
		}
		entityDataWrapper.setData(entityData);
		res.setResult(entityDataWrapper);
		return res;
	}


	@Override
	public PipelineProcessor refreshConfiguration(Delta delta) throws Throwable {
		try {
			if (entitySolrServer == null) {
				entitySolrServer = SolrServerReader.createSolrServer(this.entitySolrServerURL);
			}
			if (docSolrServer == null) {
				docSolrServer = SolrServerReader.createSolrServer(this.docSolrServerURL);
			}
			if (eCache == null) {
				synchronized (DocumentsFeedProcessor.class) {
					if (eCache == null) {
						eCache = EntityInfoCacheRegistry.getGlobalEntityInfoCache(entitySolrServer, false);
					}
				}
			}
			if (docImageServer == null) {
				docImageServer = SolrServerReader.createSolrServer(this.docImageServerURL);
				DocEntriesUpdator.addLocalServerBaseUrlsCSV(this.imageServiceURLsCSV);
			}
			if (searcher == null) {
				createSearcher();
			}
			long indexCurrentVersion = SolrServerReader.getIndexCurrentVersion(this.getEntitySolrServerURL());
			if (this.companyCatIdsSet == null || this.entityIndexVersion != indexCurrentVersion) {
				this.companyCatIdsSet = FeedUtils.getCompanyCatIDsOfIndustry(this.entitySearchToken, entitySolrServer);
				this.entityIndexVersion = indexCurrentVersion;
			}
			if (reltopicCatIdsSet == null) {
				synchronized (DocumentsFeedProcessor.class) {
					if (reltopicCatIdsSet == null) {
						reltopicCatIdsSet = FeedUtils.getReltopicCatIdsSet(this.relTopicsFilePath);
					}
				}
			}
			if (narrowBandCatSet == null || mediumBandCatSet == null) {
				loadFilterCatIdsSet(this.compFiltersFilePath);
			}
			setDocumentSimilarityUtil();
			readProgressMarkers();
		} catch (Throwable e) {
			LOG.error("Exception in refresh configuration: " + e.getMessage(), e);
			FeedUtils.alertOperator("Exception in refresh configuration: " + e.getMessage(), mailTo, mailFrom, mailHost);
			throw e;
		}
		return this;
	}

	private void createSearcher() {
		try {
			searcher = new SolrSearcher();
			searcher.setDocSolrServer(this.docSolrServer);
			searcher.setEntitySolrServer(entitySolrServer);

		} catch (Exception e) {
			LOG.error("Error while creating searcher" + e.getMessage(), e);
		}
	}

	public void readProgressMarkers() {
		if (this.progressStore != null && this.progressStore.getFormat() != ProgressMarkStore.Format.Invalid) {
			try {
				switch (progressStore.getFormat()) {
					case File:
						Properties properties = progressStore.loadProperties();
						BeanUtils.populate(this, properties);
						break;
					case JDBC:
						ProgressMark mark = progressStore.loadProgressMark();
						if (mark != null) {
							if (mark.getLastTime() != null) {
								this.startMinuteId = TimeUtils.getMinuteNumber(mark.getLastTime());
							}
							LOG.info("Read progress @ " + mark.getLastTime() + ", " + this.startMinuteId);
							pipeLineId = mark.getPipelineId();
						}
						break;
					default:
						break;
				}
			} catch (Exception e) {
				LOG.error("Exception while loading progress from " + progressStore.getPath(), e);
				FeedUtils.alertOperator("Exception while loading progress from " + progressStore.getPath() + e.getMessage(), mailTo,
						mailFrom, mailHost);
				this.startMinuteId = -1;
			}
		}
	}

	@Override
	public Delta computeStaticConfigurationDelta(PipelineComponent pipelineComponent) throws Throwable {
		DocumentsFeedProcessor target = (DocumentsFeedProcessor) pipelineComponent;
		if ((!this.getDestLocation().equals(target.getDestLocation())) || (!this.getFeedfilename().equals(target.getFeedfilename()))
				|| this.getChunkSize() != target.getChunkSize()) {
			return new Delta(Delta.Mode.REPLACE);
		}

		return null;
	}

	private void saveProgress(int backlog) {
		if (progressMarkerUpdated) {
			// Date date = new Date(TimeUtils.getTimestampForMinute(this.startMinuteId).getTime());
			saveProgress(this.startMinuteId,
					new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(TimeUtils.getTimestampForMinute(this.startMinuteId)), backlog);
		} else {
			LOG.warn("Progress markers not yet in sync with pipeline. Ignored ...");
		}
	}

	protected synchronized void saveProgress(long siteDocId, String lastSendDate, int backlog) {
		if (this.progressStore == null) {
			return;
		}

		LOG.info("Saving progress @ " + lastSendDate + ", " + siteDocId);
		try {
			switch (this.progressStore.getFormat()) {
				case File:
					Properties properties = new Properties();
					properties.put("siteDocId", Long.toString(siteDocId));
					properties.put("lastSendDate", lastSendDate);
					this.progressStore.saveProperties(properties);
					break;
				case JDBC:
					ProgressMark mark = new ProgressMark();
					mark.setLastId(siteDocId);
					mark.setLastTime(FR_DateUtils.getTimeStamp(lastSendDate, "yyyy-MM-dd HH:mm:ss.S"));
					mark.setBacklog(backlog);
					this.progressStore.saveProgressMark(mark);
					break;
				case Invalid:
				default:
					break;
			}
		} catch (Exception e) {
			LOG.error("Exception while saving progress to " + this.progressStore.getPath(), e);
		}
	}

	public void setDocumentSimilarityUtil() throws Exception {
		try {
			String pathDict = null;
			String pathStopList = null;
			DocumentSimilarityUtil dsutil = new DocumentSimilarityUtil(pathStopList, pathDict, null, null);
			dsutil.setDistanceThreshold(DISTANCE_THRESHHOLD);
			dsutil.setIgnoreCategoryFiltering(true);
			dsutil.setIgnoreMaxCandidatesPerMail(true);
			dsutil.setIndexDistancesForResearch(false);
			dsutil.setWordNetSimilarity(false);
			dsutil.setComputeWordnetDistance(false);
			dsutil.setComputeWordPrefixDistance(true);
			dsutil.setDocSimilarityProperties(docSimilarityPropPath);
			this.documentSimilarityUtil = dsutil;
		} catch (Exception e) {
			LOG.error("Exception initializing DocumentSimilarityUtil ", e);
			throw e;
		}
	}

	/**
	 * @return the destLocation
	 */
	public String getDestLocation() {
		return destLocation;
	}

	/**
	 * @param destLocation the destLocation to set
	 */
	public void setDestLocation(String destLocation) {
		this.destLocation = destLocation;
	}

	/**
	 * @return the feedfilename
	 */
	public String getFeedfilename() {
		return feedfilename;
	}

	/**
	 * @param feedfilename the feedfilename to set
	 */
	public void setFeedfilename(String feedfilename) {
		this.feedfilename = feedfilename;
	}

	/**
	 * @return the fileNameFormat
	 */
	public String getFileNameFormat() {
		return fileNameFormat;
	}

	/**
	 * @param fileNameFormat the fileNameFormat to set
	 */
	public void setFileNameFormat(String fileNameFormat) {
		this.fileNameFormat = fileNameFormat;
	}

	/**
	 * @return the chunkSize
	 */
	public int getChunkSize() {
		return chunkSize;
	}

	/**
	 * @param chunkSize the chunkSize to set
	 */
	public void setChunkSize(int chunkSize) {
		this.chunkSize = chunkSize;
	}

	/**
	 * @return the progressPath
	 */
	public String getProgressPath() {
		return progressPath;
	}

	/**
	 * @param progressPath the progressPath to set
	 */
	public void setProgressPath(String progressPath) {
		this.progressPath = progressPath;
		this.progressStore = new ProgressMarkStore(this.progressPath);
	}

	/**
	 * @return the docSolrServerURL
	 */
	public String getDocSolrServerURL() {
		return docSolrServerURL;
	}

	/**
	 * @param docSolrServerURL the docSolrServerURL to set
	 */
	public void setDocSolrServerURL(String docSolrServerURL) {
		this.docSolrServerURL = docSolrServerURL;
	}

	/**
	 * @return the mailTo
	 */
	public String getMailTo() {
		return mailTo;
	}

	public SolrServer getEntitySolrServer() {
		return this.entitySolrServer;
	}

	public SolrServer getDocImageServer() {
		return docImageServer;
	}

	public SolrServer getDocSolrServer() {
		return docSolrServer;
	}

	/**
	 * @param mailTo the mailTo to set
	 */
	public void setMailTo(String mailTo) {
		this.mailTo = mailTo;
	}

	/**
	 * @return the mailFrom
	 */
	public String getMailFrom() {
		return mailFrom;
	}

	/**
	 * @param mailFrom the mailFrom to set
	 */
	public void setMailFrom(String mailFrom) {
		this.mailFrom = mailFrom;
	}

	/**
	 * @return the mailHost
	 */
	public String getMailHost() {
		return mailHost;
	}

	/**
	 * @param mailHost the mailHost to set
	 */
	public void setMailHost(String mailHost) {
		this.mailHost = mailHost;
	}

	/**
	 * @return the entitySolrServerURL
	 */
	public String getEntitySolrServerURL() {
		return entitySolrServerURL;
	}

	/**
	 * @param entitySolrServerURL the entitySolrServerURL to set
	 */
	public void setEntitySolrServerURL(String entitySolrServerURL) {
		this.entitySolrServerURL = entitySolrServerURL;
	}

	public String getTempLocation() {
		return tempLocation;
	}

	public void setTempLocation(String tempLocation) {
		this.tempLocation = tempLocation;
	}

	public String getExcludeSourceIds() {
		return excludeSourceIds;
	}

	public void setExcludeSourceIds(String excludeSourceIds) {
		this.excludeSourceIds = excludeSourceIds;
	}

	public short getIndustryClassificationId() {
		return industryClassificationId;
	}

	public void setIndustryClassificationId(short industryClassificationId) {
		this.industryClassificationId = industryClassificationId;
	}

	public short getWebResultsTotalCount() {
		return webResultsTotalCount;
	}

	public void setWebResultsTotalCount(short webResultsTotalCount) {
		this.webResultsTotalCount = webResultsTotalCount;
	}

	public String getDocImageServerURL() {
		return docImageServerURL;
	}

	public void setDocImageServerURL(String docImageServerURL) {
		this.docImageServerURL = docImageServerURL;
	}

	public String getTopicDimensionsCSV() {
		return topicDimensionsCSV;
	}

	public void setTopicDimensionsCSV(String topicDimensionsCSV) {
		this.topicDimensionsCSV = topicDimensionsCSV;
		this.topicDim = FR_ArrayUtils.csvToIntegerList(topicDimensionsCSV);
	}

	public String getRegionsSearchTokensCSV() {
		return regionsSearchTokensCSV;
	}

	public void setRegionsSearchTokensCSV(String regionsSearchTokensCSV) {
		this.regionsSearchTokensCSV = regionsSearchTokensCSV;
		this.regionsSearchTokensList = FR_ArrayUtils.csvToArrayList(regionsSearchTokensCSV);
	}

	public String getRegionsBandCSV() {
		return regionsBandCSV;
	}

	public void setRegionsBandCSV(String regionsBandCSV) {
		this.regionsBandCSV = regionsBandCSV;
		this.regionsBandList = FR_ArrayUtils.csvToIntegerList(regionsBandCSV);
	}

	public String getEntitySearchToken() {
		return entitySearchToken;
	}

	public void setEntitySearchToken(String entitySearchToken) {
		this.entitySearchToken = entitySearchToken;
	}

	public String getRelTopicsFilePath() {
		return relTopicsFilePath;
	}

	public void setRelTopicsFilePath(String relTopicsFilePath) {
		this.relTopicsFilePath = relTopicsFilePath;
	}

	public String getCompFiltersFilePath() {
		return compFiltersFilePath;
	}

	public void setCompFiltersFilePath(String compFiltersFilePath) {
		this.compFiltersFilePath = compFiltersFilePath;
	}

	public String getImageServiceURLsCSV() {
		return imageServiceURLsCSV;
	}

	public void setImageServiceURLsCSV(String imageServiceURLsCSV) {
		this.imageServiceURLsCSV = imageServiceURLsCSV;
	}

	public String getFilterSearchToken() {
		return filterSearchToken;
	}

	public void setFilterSearchToken(String filterSearchToken) {
		this.filterSearchToken = filterSearchToken;
	}

	public String getDocSimilarityPropPath() {
		return docSimilarityPropPath;
	}

	public void setDocSimilarityPropPath(String docSimilarityPropPath) {
		this.docSimilarityPropPath = docSimilarityPropPath;
	}
}
