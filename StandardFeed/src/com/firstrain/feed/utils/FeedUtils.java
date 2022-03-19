package com.firstrain.feed.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import com.firstrain.content.similarity.DocumentSimilarityUtil;
import com.firstrain.content.similarity.DocumentSimilarityUtil.DocSimilarityMatchResult;
import com.firstrain.content.similarity.measures.DiceCoefficientStrategy;
import com.firstrain.content.util.IConstants;
import com.firstrain.feed.obj.DocEntrySiteDocument;
import com.firstrain.frapi.domain.Document.DocQuote;
import com.firstrain.frapi.pojo.Entity;
import com.firstrain.frapi.util.ContentType;
import com.firstrain.frapi.util.DefaultEnums.RelevanceBand;
import com.firstrain.frapi.util.FRAPIConstant;
import com.firstrain.mip.object.FR_IQuote;
import com.firstrain.mip.object.FR_ISiteDocument;
import com.firstrain.solr.client.DocCatEntry;
import com.firstrain.solr.client.DocEntriesUpdator;
import com.firstrain.solr.client.DocEntry;
import com.firstrain.solr.client.EntityEntry;
import com.firstrain.solr.client.QuoteEntry;
import com.firstrain.solr.client.SearchTokenEntry;
import com.firstrain.solr.client.util.SolrServerReader;
import com.firstrain.utils.FR_ArrayUtils;
import com.firstrain.utils.FR_StringUtils;
import com.firstrain.utils.object.PerfMonitor;
import com.firstrain.web.pojo.Document;
import com.firstrain.web.pojo.EntityStandard;
import com.firstrain.web.pojo.Source;

public class FeedUtils {
	private static final Logger LOG = Logger.getLogger(FeedUtils.class);
	private static final float SUMMARY_TRIM_FACTOR = 0.15f;
	private static final int MIN_SUMMARY_LENGTH = 100;
	private static final int MAX_SUMMARY_LENGTH = 400;
	private final DiceCoefficientStrategy diceMeasure = new DiceCoefficientStrategy();
	private static final int N_GRAMS_SIZE = 7;
	private static final double SUMMARY_SIMILARITY_THRESHHOLD = 0.15;
	private static AtomicInteger idfountain = new AtomicInteger(10000);

	public static boolean copy(String srcPath, String destPath) throws IOException {
		boolean copyStatus = false;
		try {
			File src = new File(srcPath);
			File dst = new File(destPath);
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dst);
			/// Transfer bytes from in to out
			byte[] buf = new byte[1024];
			writeData(in, buf, out); 
			copyStatus = true;
			in.close();
			out.close();


		} catch (IOException e) {
			LOG.error("Write Failed");
			LOG.error("Exception : " + e);
		}

		return copyStatus;
	}

	public static void alertOperator(String message, String to, String from, String host) {

		sendMail(to, from, "", message, host); // This will be move to Confiuration File
	}

	public static boolean sendMail(String to, String from, String message, String subject, String host) {
		try {
			StringTokenizer st_to = new StringTokenizer(to, ",");
			LOG.debug(to);
			LOG.debug(from);
			LOG.debug(subject);
			// String host = "10.32.1.16";

			Properties prop = new Properties();
			prop.put("mail.smtp.host", host);

			Session session = Session.getInstance(prop);
			Message msg = new MimeMessage(session);
			// Message msg2=new MimeMessage(session);

			msg.setFrom(new InternetAddress(from));
			// msg2.setFrom(new InternetAddress(from));

			while (st_to.hasMoreTokens()) {
				InternetAddress[] address = {new InternetAddress(st_to.nextToken())};
				msg.addRecipients(Message.RecipientType.TO, address);
			}
			msg.setSentDate(new Date());
			msg.setSubject(subject);
			msg.setContent(message, "text/html");

			Transport.send(msg);
			LOG.debug("Message delivered Successfully");
			return true;
		} catch (Exception e) {
			LOG.error("Error in FeedUtils while sending mail:" + e.getMessage(), e);
			return false;

		}

	}

	private static String getFormatTime(Date date) {
		// date in UTC - 18 Sep 2014 07:16:43 UTC
		SimpleDateFormat formater = new SimpleDateFormat("dd MMM yyyy HH:mm:ss z");
		formater.setTimeZone(TimeZone.getTimeZone("UTC"));
		return formater.format(date);
	}

	private static String setRelevanceBand(Integer relevanceBandParam) {
		Integer relevanceBand = relevanceBandParam;
		String relevanceBandStr;
		if (relevanceBand == null) {
			relevanceBand = (int) RelevanceBand.LOW.getValue();
		}
		if (relevanceBand == RelevanceBand.HIGH.getValue()) {
			relevanceBandStr = RelevanceBand.HIGH.getName();
		} else if (relevanceBand == RelevanceBand.MEDIUM.getValue()) {
			relevanceBandStr = RelevanceBand.MEDIUM.getName();
		} else {
			relevanceBandStr = RelevanceBand.LOW.getName();
		}
		return relevanceBandStr;
	}

	private static String getTrimmedSummary(String summary, DocEntry doc) {
		String trimmedsummary = "";
		int bodyLength = doc.bodyLength;
		int headlineLength = doc.title.length();
		bodyLength = bodyLength - headlineLength;
		if (bodyLength > 1) {
			int trimLength = Math.min(((Float) (bodyLength * SUMMARY_TRIM_FACTOR)).intValue(), MAX_SUMMARY_LENGTH);

			if (summary.length() > trimLength) {
				trimmedsummary = summary.substring(0, trimLength);
				if (trimmedsummary.lastIndexOf(" ") > -1) {
					trimmedsummary = trimmedsummary.substring(0, trimmedsummary.lastIndexOf(" "));
				}
				LOG.debug("Summary trimmed, summary length: " + summary.length() + " bodyLength: " + bodyLength + " trimLength: "
						+ trimLength);
			} else {
				trimmedsummary = summary;
			}
			if (!trimmedsummary.endsWith(".")) {
				trimmedsummary += "...";
			}
		}
		return trimmedsummary;
	}


	public static Document convertDocumentPOJOFromDocEntry(DocEntry doc, List<Integer> topicDim, Set<Integer> companyCatIdsSet,
			Set<Integer> reltopicCatIdsSet) {
		Document document = new Document();
		try {
			document = convertDocumentPOJOFromDocEntryObject(doc);
			document.setGroupId(FRAPIConstant.GROUP_ID_PREFIX + doc.getGroupId());
			if (doc.getSmartSummary() != null) {
				document.setSnippet(doc.getSmartSummary());
			} else {
				document.setSnippet(doc.getSummary());
			}
			if (document.getSnippet() != null) {
				// trim summary - trim to MAX_SUMMARY_LENGTH or SUMMARY_TRIM_FACTOR of body length
				String trimmedSummary = getTrimmedSummary(document.getSnippet(), doc);
				// show summary only if it's more than MIN_SUMMARY_LENGTH
				if (trimmedSummary.length() > MIN_SUMMARY_LENGTH) {
					document.setSnippet(trimmedSummary);
				} else {
					document.setSnippet(null);
				}
			}

			ContentType contentType = null;
			if (doc.catEntries != null) {
				outer: for (ContentType ct : ContentType.values()) {
					for (DocCatEntry docCat : doc.catEntries) {
						if (ct.getId() == Integer.parseInt(docCat.getEntity().getId())) {
							contentType = ct;
							break outer;
						}
					}
				}
			}
			if (contentType == null) {
				contentType = ContentType.WEBNEWS;
			}
			document.setContentType(contentType.name());
			List<com.firstrain.frapi.pojo.Entity> entitiesList = getEntityPOJOList(doc.getCatEntries(), contentType, false);
			if (entitiesList != null && !entitiesList.isEmpty()) {
				List<EntityStandard> entities = new ArrayList<EntityStandard>();
				for (Entity e1 : entitiesList) {
					if (topicDim != null && topicDim.contains(e1.getType())) {
						int id = Integer.parseInt(e1.getId());
						if (e1.getType() == 1 && !companyCatIdsSet.contains(id)) {
							LOG.debug("filtering companyid: " + id);
							continue;
						}
						if (e1.getType() == 3 && !reltopicCatIdsSet.contains(id)) {
							LOG.debug("filtering topicid: " + id);
							continue;
						}
						EntityStandard e = new EntityStandard();
						String name = e1.getName();
						e.setName(name);
						e.setSearchToken(e1.getSearchToken());
						e.setRelevanceBand(setRelevanceBand(e1.getBand()));
						e.setRelevanceScore(e1.getRelevanceScore());
						entities.add(e);
					}
				}
				if (!entities.isEmpty()) {
					document.setEntity(entities);
				}
			}

			if (doc.getDocImage() != null) {
				document.setImage(doc.getDocImage().getUrlLocaliPad());
			}

			List<QuoteEntry> quotes = doc.getOtrQuotes();
			if (quotes != null && !quotes.isEmpty()) {
				DocQuote docQuote = new DocQuote(quotes.get(0).getText(), quotes.get(0).getPerson());
				if (docQuote != null) {
					document.setQuotes(docQuote.getHighlightedQuote());
				}
			}

		} catch (Exception e) {
			LOG.error("Exception Converting DocumentPOJO", e);
		}
		return document;
	}

	private static List<Entity> getEntityPOJOList(List<DocCatEntry> docEntryList, ContentType contentType, boolean doCheck) {
		List<Entity> entityList = null;
		try {
			if (docEntryList != null) {
				entityList = new ArrayList<Entity>();
				if (doCheck) {
					for (int i = 0; i < docEntryList.size(); i++) {
						DocCatEntry dce = docEntryList.get(i);
						EntityEntry input = dce.getEntity();
						if (contentType.getId() == Integer.parseInt(input.getId())) {
							entityList.add(convertEntityFromEntityEntry(input, dce.band, dce.relevanceNormalized));
						}
					}
				} else {
					for (int i = 0; i < docEntryList.size(); i++) {
						DocCatEntry dce = docEntryList.get(i);
						EntityEntry input = dce.getEntity();
						entityList.add(convertEntityFromEntityEntry(input, dce.band, dce.relevanceNormalized));
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Exception Getting EntityPOJOList", e);
		}
		return entityList;
	}

	private static Entity convertEntityFromEntityEntry(EntityEntry entry, short band, short relevance) {
		if (entry == null) {
			return null;
		}

		Entity entity = new Entity();
		entity.setId(entry.getId());
		entity.setName(entry.getName());
		entity.setSearchToken(entry.getSearchToken());
		entity.setTicker(entry.getPrimaryTicker());
		entity.setBand(new Integer(band));
		entity.setType(entry.getType() - SearchTokenEntry.SEARCH_TOKEN_TAG_BASE);
		entity.setRelevanceScore(relevance);
		return entity;
	}

	private static Document convertDocumentPOJOFromDocEntryObject(DocEntry doc) {
		Document document = new Document();
		try {
			document.setId(FRAPIConstant.DOCUMENT_PREFIX + doc.getSitedocId());
			document.setTitle(doc.getTitle());
			document.setLink(doc.getUrl());
			document.setSource(convertSourceFromEntityEntry(doc.getSourceEntity(), (short) 0));
			document.setTimeStamp(getFormatTime(doc.getInsertTime()));
		} catch (Exception e) {
			LOG.error("Exception Converting DocumentPOJO", e);
		}
		return document;
	}

	private static Source convertSourceFromEntityEntry(EntityEntry entry, short band) {
		if (entry == null) {
			return null;
		}
		Source source = new Source();
		source.setName(entry.getName());
		source.setSearchToken(entry.getSearchToken());

		return source;
	}

	public static void attachDocSummaryImageAndHighlightQuote(List<DocEntry> docEntryList, SolrServer docImageServer) throws Exception {
		try {
			DocEntriesUpdator.attachFavIconNDocImageDetails(null, docImageServer, docEntryList);
		} catch (Exception e) {
			LOG.error("Error while attaching docimage and favicon", e);
		}
		try {
			for (DocEntry doc : docEntryList) {
				if (doc.otrQuotes != null && !doc.otrQuotes.isEmpty()) {
					for (QuoteEntry qe : doc.otrQuotes) {
						highlightQuote(qe);
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Error while highlighting quote", e);
		}
	}

	private static void highlightQuote(QuoteEntry quoteEntry) {
		String contextBody = FR_StringUtils.replace(quoteEntry.text, FR_IQuote.SENTENCE_MARKER, "", false);

		int firstOccur = contextBody.indexOf(FR_IQuote.QUOTE_MARKER);
		if (firstOccur > 0) {
			if (contextBody.charAt(firstOccur - 1) == ' ' || contextBody.charAt(firstOccur + 1) == ' ') {
				contextBody = FR_StringUtils.replace(contextBody, FR_IQuote.QUOTE_MARKER, "<b>", false, 1);
			} else {
				contextBody = FR_StringUtils.replace(contextBody, FR_IQuote.QUOTE_MARKER, " <b>", false, 1);
			}
		}
		int lastOccur = contextBody.indexOf(FR_IQuote.QUOTE_MARKER);
		if (lastOccur > 0 && lastOccur < (contextBody.length() - 1)) {
			if (contextBody.charAt(lastOccur - 1) == ' ' || contextBody.charAt(lastOccur + 1) == ' ') {
				contextBody = FR_StringUtils.replace(contextBody, FR_IQuote.QUOTE_MARKER, "</b>", false);
			} else {
				contextBody = FR_StringUtils.replace(contextBody, FR_IQuote.QUOTE_MARKER, "</b> ", false);
			}
		}
		quoteEntry.text = contextBody;
	}

	public static ContentType getContentTypeAndFilterOutCT(DocEntry doc, Collection<String> toExcludeIds) {
		if (doc.matchedTopics != null) {
			for (Iterator<DocCatEntry> iter = doc.matchedTopics.iterator(); iter.hasNext();) {
				EntityEntry e = iter.next().entity;
				if ((toExcludeIds != null && toExcludeIds.contains(e.id)) || e.type == SearchTokenEntry.SEARCH_TOKEN_TAG_FUNDAMENTAL) {// exclude
																																		// company
																																		// topics
					iter.remove();
				}
			}
		}
		if (toExcludeIds != null && doc.matchedCompanies != null) {
			for (Iterator<DocCatEntry> iter = doc.matchedCompanies.iterator(); iter.hasNext();) {
				if (toExcludeIds.contains(iter.next().entity.id)) {
					iter.remove();
				}
			}
		}

		ContentType contentType = null;
		if (doc.matchedContentTypes != null) {
			outer: for (ContentType ct : ContentType.values()) {
				for (DocCatEntry docCat : doc.matchedContentTypes) {
					if (ct.getId() == Integer.parseInt(docCat.getEntity().getId())) {
						contentType = ct;
						break outer;
					}
				}
			}
		}
		if (contentType == null) {
			contentType = ContentType.WEBNEWS;
		}
		return contentType;
	}

	public static boolean createJSONFile(String json, String FileName) {
		FileOutputStream feed = null;
		OutputStreamWriter osw = null;
		BufferedWriter out = null;
		try {
			feed = new FileOutputStream(FileName);
			osw = new OutputStreamWriter(feed, "UTF-8");
			out = new BufferedWriter(osw);
			out.write(json);
			out.flush();
			out.close();
			LOG.debug("JSON file " + FileName + " created at " + FileName);
		} catch (Exception e) {
			LOG.error("Error in FeedUtils while creating JSON file:" + e.getMessage(), e);
			return false;
		}
		return true;
	}

	public static boolean createZipfile(String srcFileLocation, String srcFileName, String zipFileName) {
		try {
			// making zip file
			byte[] buf = new byte[1024];

			ZipOutputStream zipout = new ZipOutputStream(new FileOutputStream(zipFileName));
			FileInputStream in = new FileInputStream(srcFileLocation);

			// Add ZIP entry to output stream.
			zipout.putNextEntry(new ZipEntry(srcFileName));

			// Transfer bytes from the file to the ZIP file
			writeData(in, buf, zipout); 
			zipout.closeEntry();
			in.close();

			// Complete the ZIP file
			zipout.close();
			LOG.debug("Zip file " + srcFileName + " created at " + zipFileName);
		} catch (Exception e) {
			LOG.error("Error in FeedUtils while creating ZIP file:" + e.getMessage(), e);
			return false;
		}
		return true;
	}
 
	private static <T0 extends InputStream, T1 extends OutputStream> void writeData(final T0 in, final byte[] buf, final T1 zipout) throws IOException { 
		int len; 
		while ((len = in.read(buf)) > 0) { 
			zipout.write(buf, 0, len); 
		} 
	} 

	public static void deleteOldFiles(String destLocation, String dateFormat, int backupDays) {
		try {
			File dir = new File(destLocation);

			SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

			List<String> deletedFiles = new ArrayList<String>();
			for (int j = backupDays; j < 30; j++) {
				String date = sdf.format(new Date());
				Date dateobj = (Date) sdf.parseObject(date);
				Calendar cal = Calendar.getInstance();
				cal.setTime(dateobj);
				cal.add(Calendar.DAY_OF_MONTH, -(j));
				date = (new SimpleDateFormat("yyyyMMdd")).format(cal.getTime());

				String[] children = dir.list();
				if (children == null) {
					LOG.error("Either dir - " + destLocation + " does not exist or is not a directory");// Either dir does not exist or is
																										// not a directory
				} else {
					for (int i = 0; i < children.length; i++) {
						// Get filename of file or directory
						String filename = children[i];
						if (filename.contains(date)) {
							File file = new File(destLocation + File.separator + filename);
							boolean deleted = file.delete();
							if (deleted) {
								deletedFiles.add(file.getAbsolutePath());
							}
						}
					}
				}
			}
			if (!deletedFiles.isEmpty()) {
				LOG.info("Listing removed older files \n" + deletedFiles.toString());
			}
		} catch (Exception e) {
			LOG.error("Error in FeedUtils while deleting old files:" + e.getMessage(), e);
		}
	}

	public static String getUniqueFileName(String location, String fileName) {
		int index = 1;
		String filePath = location + File.separator + fileName;
		File f = new File(filePath + "_" + index + ".json");
		while (f.exists()) {
			index++;
			f = new File(filePath + "_" + index + ".json");
		}
		return fileName + "_" + index;
	}

	public static Set<Integer> getCompanyCatIDsOfIndustry(String token, SolrServer entitySolrServer) throws Exception {
		long catId = getIndustryCatId(token, entitySolrServer);
		Set<Integer> catIdsSet = new HashSet<Integer>();

		try {
			SolrDocumentList results = SolrServerReader.retrieveSolrDocsInBatches(entitySolrServer,
					"(secondarySector:" + catId + " OR sector:" + catId + ") AND attrDim:1", 10000, "attrCatId");

			if (results.getNumFound() == 0) {
				LOG.debug("Companies not found for Sector Id " + catId);
				return catIdsSet;
			}
			for (SolrDocument compDoc : results) {
				catIdsSet.add(Integer.parseInt(compDoc.getFieldValue("attrCatId").toString()));
			}
			LOG.info(catIdsSet.size() + " companies loaded for sector Id: " + catId);
		} catch (Exception e) {
			LOG.error("Error in FeedUtils while getCompanyCatIDsOfIndustry: " + e.getMessage(), e);
			throw e;
		}
		return catIdsSet;
	}

	public static long getIndustryCatId(String token, SolrServer entitySolrServer) throws Exception {
		long catID = -1;
		SolrDocument indDoc = null;
		try {
			SolrQuery solrQuery = new SolrQuery();
			solrQuery.setQuery("attrSearchToken:\"" + token + "\"");

			solrQuery.setRows(1);
			solrQuery.setStart(0);
			solrQuery.setQueryType("standard");

			QueryRequest request = new QueryRequest(solrQuery, SolrRequest.METHOD.GET);
			QueryResponse response = request.process(entitySolrServer);

			SolrDocumentList results = response.getResults();

			if (results.getNumFound() == 0) {
				LOG.debug("Industry not found for token " + token);
				return catID;
			}

			indDoc = results.get(0);
			catID = Long.parseLong(indDoc.getFieldValue("attrCatId").toString());
		} catch (Exception e) {
			LOG.error("Error in FeedUtils while getIndustryCatId:" + e.getMessage(), e);
			throw e;
		}

		return catID;
	}

	public static Set<Integer> getReltopicCatIdsSet(String filePath) throws Exception {
		Set<Integer> catIdsSet = new HashSet<Integer>();
		try {
			XSSFWorkbook wb = new XSSFWorkbook(filePath);
			XSSFSheet sheet = wb.getSheet("reltopics");
			int rows = sheet.getPhysicalNumberOfRows();
			for (int r = 1; r < rows; r++) {
				XSSFRow row = sheet.getRow(r);
				if (row == null) {
					continue;
				}
				if (row.getCell(1) != null && row.getCell(1).getCellType() != Cell.CELL_TYPE_BLANK) {
					catIdsSet.add((int) row.getCell(1).getNumericCellValue());
				}
			}
			LOG.info(catIdsSet.size() + " related topics loaded from file: " + filePath);
		} catch (Exception e) {
			LOG.error("Error while loading related topics from: " + filePath, e);
			throw e;
		}
		return catIdsSet;
	}

	public static List<DocEntry> filterSimilarEntries(List<DocEntry> entries, DocumentSimilarityUtil dsutil, int reqCountAfterFiltering,
			boolean sendAllEntries) {
		List<DocEntry> filteredEntries = null;
		PerfMonitor.startRequest("sageworks doc exlusion", "activityTrack");
		try {
			// return if null
			if (entries == null || entries.isEmpty()) {
				return null;
			}

			List<FR_ISiteDocument> siteDocList = new ArrayList<FR_ISiteDocument>(entries.size());

			Integer id = idfountain.incrementAndGet();

			// create siteDocList (in site_doc_id asc order as expected by DocumentSimilarityUtil)
			for (int i = entries.size() - 1; i >= 0; i--) {
				DocEntry doc = entries.get(i);
				DocEntrySiteDocument siteDoc = new DocEntrySiteDocument(doc);
				Set<Integer> ids = new HashSet<Integer>();
				ids.add(id);
				// set cats info for dsutil
				setCategoriesInfo(doc, siteDoc);

				DocSimilarityMatchResult matchResult = dsutil.processDocument(siteDoc, ids, ids);

				if (matchResult.getExcludedPersonalMailIdSet() != null && matchResult.getExcludedPersonalMailIdSet().size() > 0) {
					LOG.debug("SiteDocument found similar : " + siteDoc.getSiteDocID());
				} else {
					siteDocList.add(siteDoc);
				}
			}

			if (siteDocList != null && !siteDocList.isEmpty()) {
				filteredEntries = new ArrayList<DocEntry>(siteDocList.size());
				int i = (sendAllEntries) ? siteDocList.size() : Math.min(siteDocList.size(), reqCountAfterFiltering);
				for (int j = siteDocList.size() - 1; i > 0; i--, j--) {
					DocEntrySiteDocument doc = (DocEntrySiteDocument) siteDocList.get(j);
					filteredEntries.add(doc.getOriginalDocEntry());
				}
			}
		} catch (Exception e) {
			LOG.error("Exception while processing docEntries for DocumentSimilarity ", e);
			filteredEntries = entries;
		}
		PerfMonitor.endRequest();
		return filteredEntries;
	}

	private static void setCategoriesInfo(DocEntry doc, DocEntrySiteDocument siteDoc) {
		List<DocCatEntry> docCatList = doc.getCatEntries();
		if (docCatList != null) {
			Set<Integer> narrowCats = new HashSet<Integer>();
			Set<Integer> topics = new HashSet<Integer>();
			Set<Integer> companies = new HashSet<Integer>();

			// iterate on all doc categories
			for (DocCatEntry catEntry : docCatList) {
				EntityEntry entity = catEntry.getEntity();
				if (entity.isValid() && !entity.isExcluded()) {
					int entityId = Integer.parseInt(entity.getId());
					// apply filter for Regions
					if (entity.getType() == SearchTokenEntry.SEARCH_TOKEN_TAG_COMPANY) { // apply filter for Companies
						companies.add(entityId);
					} else if (entity.getType() == SearchTokenEntry.SEARCH_TOKEN_TAG_FUNDAMENTAL
							|| entity.getType() == SearchTokenEntry.SEARCH_TOKEN_TAG_SECTOR_TOPIC) {
						topics.add(entityId);
					}
					if (catEntry.getBand() == DocCatEntry.BAND_SELECTIVE) {
						narrowCats.add(entityId);
					}
				}
			}

			siteDoc.attributes().put(IConstants.resultFilteredBucketingCatIdsAttrName, FR_ArrayUtils.objListToIntArray(narrowCats));
			if (!topics.isEmpty()) {
				int[] topicsArr = FR_ArrayUtils.objListToIntArray(topics);
				siteDoc.attributes().put(IConstants.topicCatIdsAttrName, topicsArr);
			}

			if (!companies.isEmpty()) {
				int[] companiesArr = FR_ArrayUtils.objListToIntArray(companies);
				siteDoc.attributes().put(IConstants.companyCatIdsAttrName, companiesArr);
			}

			Set<Integer> dimensions123 = new HashSet<Integer>(topics);
			dimensions123.addAll(companies);
			// now validate
			if (!dimensions123.isEmpty()) {
				siteDoc.attributes().put(IConstants.topicAllCatIdsAttrName, dimensions123);
			}

		}
	}

	public static List<DocEntry> filterSimilarEntries(List<DocEntry> entries) {
		FeedUtils fu = new FeedUtils();
		try {
			// return if null
			if (entries == null || entries.isEmpty()) {
				return null;
			}
			List<DocEntry> tempList = new ArrayList<DocEntry>(entries);

			for (Iterator<DocEntry> candidateIter = entries.iterator(); candidateIter.hasNext();) {
				DocEntry candidateDoc = candidateIter.next();

				for (Iterator<DocEntry> doclistItr = tempList.iterator(); doclistItr.hasNext();) {
					DocEntry currentDoc = doclistItr.next();
					if (candidateDoc.getSitedocId().equals(currentDoc.getSitedocId())) {
						doclistItr.remove();
						continue;
					}
					if (fu.similarSummary(candidateDoc.summary, currentDoc.summary)) {
						LOG.info("Found similiar summary documents <" + candidateDoc.getSitedocId() + "," + currentDoc.getSitedocId()
								+ "> excluding documentId: " + candidateDoc.getSitedocId());
						candidateIter.remove();
						break;
					}

				}
			}
		} catch (Exception e) {
			LOG.error("Exception while processing docEntries for DocumentSimilarity ", e);
		}
		return entries;
	}

	private final boolean similarSummary(String summary1, String summary2) {
		double result = diceMeasure.score(summary1, summary2, N_GRAMS_SIZE);
		return result >= SUMMARY_SIMILARITY_THRESHHOLD;
	}
}
