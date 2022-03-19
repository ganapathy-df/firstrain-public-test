package com.firstrain.frapi.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.firstrain.content.similarity.DocumentSimilarityUtil;
import com.firstrain.db.obj.GroupDomainMap;
import com.firstrain.frapi.domain.BaseSpec;
import com.firstrain.frapi.domain.Document;
import com.firstrain.frapi.domain.HistoricalStat;
import com.firstrain.frapi.obj.DocEntrySiteDocument;
import com.firstrain.frapi.obj.MgmtTurnoverServiceSpec;
import com.firstrain.frapi.obj.MonitorWizardFilters;
import com.firstrain.frapi.obj.MonitorWizardFilters.Advanced;
import com.firstrain.frapi.pojo.EnterprisePref;
import com.firstrain.frapi.pojo.Entity;
import com.firstrain.frapi.pojo.Event;
import com.firstrain.frapi.pojo.Graph;
import com.firstrain.frapi.pojo.wrapper.EventSet;
import com.firstrain.frapi.repository.EntityBaseServiceRepository;
import com.firstrain.frapi.util.DateBucketUtils.BucketSpec;
import com.firstrain.frapi.util.DefaultEnums.DateBucketingMode;
import com.firstrain.frapi.util.QueryParser.ParseResult;
import com.firstrain.mip.object.FR_IQuote;
import com.firstrain.mip.object.FR_ISiteDocument;
import com.firstrain.obj.IEntityInfo;
import com.firstrain.obj.IEntityInfoCache;
import com.firstrain.solr.client.DocCatEntry;
import com.firstrain.solr.client.DocEntry;
import com.firstrain.solr.client.QuoteEntry;
import com.firstrain.utils.FR_StringUtils;
import com.firstrain.utils.JSONUtility;

@Service
public class ServicesAPIUtil {

	private final Logger LOG = Logger.getLogger(ServicesAPIUtil.class);

	@Autowired
	private ConvertUtil convertUtil;
	@Autowired
	@Qualifier("entityBaseServiceRepositoryImpl")
	private EntityBaseServiceRepository entityBaseServiceRepository;

	public long checkValidDomain(String email, List<GroupDomainMap> domainList) {
		long groupId = -1;
		if (domainList != null && !domainList.isEmpty()) {
			for (GroupDomainMap gpObj : domainList) {
				String domain = gpObj.getDomain();
				if (domain.startsWith("@")) {
					if (email.endsWith(domain)) {
						groupId = gpObj.getGroupId();
						break;
					}
				} else if (email.matches("(?i).*[@.]" + domain + "[.].*")) { // for universal match
					// put domain as
					// ".*"
					groupId = gpObj.getGroupId();
					break;
				}
			}
		}
		return groupId;
	}

	public int getSectorCatIdFromEntitySearchToken(String searchToken) {
		int secCatId = -1;
		IEntityInfoCache cache = entityBaseServiceRepository.getEntityInfoCache();
		IEntityInfo ent = cache.searchTokenToEntity(searchToken);
		if (ent != null) {
			secCatId = ent.getSectorCatId();
		}
		return secCatId;
	}

	public String generateRandomString(int count) {
		return RandomStringUtils.randomAlphanumeric(count);
	}

	public String removeFilters(List<String> filtersToRemove, MonitorWizardFilters oldData) throws Exception {


		// we need to fetch latest tokens from cache for all advanced filters
		if (oldData.advanced.advancedFilters != null && !oldData.advanced.advancedFilters.isEmpty()) {
			List<List<String>> keywordsAndFiltersList = getKeywordsAndFilters(oldData.advanced.advancedFilters);
			// As there are no keywords, so fetch only advanced filters
			oldData.advanced.advancedFilters = keywordsAndFiltersList.get(1);
		}

		for (String filter : filtersToRemove) {

			if (oldData.advanced.advancedFilters != null && !oldData.advanced.advancedFilters.isEmpty()) {
				boolean isRemoved = oldData.advanced.advancedFilters.remove(filter);

				if (isRemoved) {
					continue;
				}
			}

			if (oldData.advanced.keywords != null && !oldData.advanced.keywords.isEmpty()) {
				oldData.advanced.keywords = removeFilterFromKeywords(filter, oldData.advanced.keywords);
			}
		}
		return JSONUtility.serialize(oldData);
	}

	public boolean removeFilterFromKeywords(String filterParam, List<String> keywords) {

		String filter = filterParam;
		int index = keywords.indexOf(filter);

		if (index != -1) {
			filter = keywords.get(index);

			if (index > 0 && (keywords.get(index - 1).equalsIgnoreCase("AND") || keywords.get(index - 1).equalsIgnoreCase("OR"))) {
				keywords.remove(index - 1);
			} else if (index == 0 && keywords.size() > (index + 1)
					&& (keywords.get(index + 1).equalsIgnoreCase("AND") || keywords.get(index + 1).equalsIgnoreCase("OR"))) {
				keywords.remove(index + 1);
			}

			keywords.remove(filter);
			return true;
		}
		return false;
	}

	public String removeFilterFromKeywords(String filter, String keywordsParam) {

		String keywords = keywordsParam;
		keywords = " " + keywords + " ";
		int index = keywords.toLowerCase().indexOf(" " + filter.toLowerCase() + " ");

		if (index != -1) {

			if (index == 0) {
				keywords = filterKeywords(filter, keywords);

			} else {
				String[] array = keywords.split(" " + filter + " ");
				keywords = retrieveKeyWords(array);
			}
		}
		return keywords.trim();
	}

	private String retrieveKeyWords(final String[] array) {
		String keywords = null;
		String before = array[0].trim();
		if (before.endsWith("OR") || before.endsWith("or")) {
			before = before.substring(0, before.length() - "OR".length()).trim();
		} else if (before.endsWith("AND") || before.endsWith("and")) {
			before = before.substring(0, before.length() - "AND".length()).trim();
		}
		
		if (array.length == 2) {
			keywords = before + " " + array[1].trim();
		} else {
			keywords = before;
		}
		return keywords;
	}

	private String filterKeywords(final String filter, String keywordsParam) {
		String keywords = keywordsParam;
		keywords = keywords.trim();
		keywords = keywords.substring(filter.length()).trim();
		if (keywords.startsWith("OR") || keywords.startsWith("or")) {
			keywords = keywords.substring("OR".length()).trim();
		} else if (keywords.startsWith("AND") || keywords.startsWith("and")) {
			keywords = keywords.substring("AND".length()).trim();
		}
		return keywords;
	}

	public Map<String, List<String>> prepareQueryMap(String query) {
		Map<String, List<String>> map = new LinkedHashMap<String, List<String>>();
		ParseResult pResult = QueryParser.parseQueryString(query);
		for (String param : pResult.getParams()) {
			map.put(param, pResult.getParamValues(param));
		}
		return map;
	}

	public String addFilters(List<String> filterList, MonitorWizardFilters data, List<String> keywordsList) throws Exception {

		if (keywordsList != null && !keywordsList.isEmpty()) {

			resetAdvancedAndKeywords(data);


			populateAdvancedKeywords(data, keywordsList);
		}

		return populateAdvancedFiltersAndSerialize(data, filterList);
	}

	private void populateAdvancedKeywords(final MonitorWizardFilters data, final List<String> keywordsList) {
		for (String keyword : keywordsList) {
		
			data.advanced.keywords = " " + data.advanced.keywords + " ";
		
			if (data.advanced.keywords.toLowerCase().indexOf(" " + keyword.toLowerCase() + " ") == -1) {
		
				data.advanced.keywords = data.advanced.keywords.trim();
		
				if (keyword.startsWith("-")) {
					data.advanced.keywords += " " + keyword;
				} else {
					if (data.advanced.keywords.isEmpty()) {
						data.advanced.keywords = keyword;
					} else {
						data.advanced.keywords += " OR " + keyword;
					}
				}
			}
		}
		
		data.advanced.keywords = data.advanced.keywords.trim();
	}

	private String populateAdvancedFiltersAndSerialize(final MonitorWizardFilters data, final List<String> filterList) throws Exception {
		if (filterList != null && !filterList.isEmpty()) {
		
			if (data.advanced == null) {
				data.advanced = new Advanced();
				data.advanced.advancedFilters = new ArrayList<String>();
			} else {
				if (data.advanced.advancedFilters == null) {
					data.advanced.advancedFilters = new ArrayList<String>();
				}
			}
		
			// we need to fetch latest tokens from cache for all advanced filters
			List<List<String>> keywordsAndFiltersList = getKeywordsAndFilters(data.advanced.advancedFilters);
			// As there are no keywords, so fetch only advanced filters
			data.advanced.advancedFilters = keywordsAndFiltersList.get(1);
		
			for (String filter : filterList) {
		
				if (!data.advanced.advancedFilters.contains(filter)) {
					data.advanced.advancedFilters.add(filter);
				}
			}
		}
		String finalString = JSONUtility.serialize(data);
		return finalString;
	}

	private void resetAdvancedAndKeywords(final MonitorWizardFilters data) {
		if (data.advanced == null) {
			data.advanced = new Advanced();
		}
		if (data.advanced.keywords == null) {
			data.advanced.keywords = "";
		}
	}

	public String prepareFq(List<String> filters) {

		String fq = "";

		if (filters == null || filters.isEmpty()) {
			return fq;
		}

		for (String filter : filters) {

			if (filter.startsWith("-")) {
				fq += " " + filter;
			} else {
				if (fq.isEmpty()) {
					fq += filter;
				} else {
					fq += " OR " + filter;
				}
			}
		}

		return fq.trim();
	}

	public String prepareQForAddOrRemoveFilter(String queryParam, MonitorWizardFilters oldData, MonitorWizardFilters finalData) {

		String query = queryParam;
		if (oldData.advanced.keywords == null || oldData.advanced.keywords.isEmpty()) {
			if (finalData.advanced.keywords != null) {
				query += " " + finalData.advanced.keywords;
			}
			return query.trim();
		}

		query = removeKeywordsFromQ(query, oldData.advanced.keywords);
		if (finalData.advanced.keywords != null) {
			query += " " + finalData.advanced.keywords.trim();
		}
		return query;
	}

	public String removeKeywordsFromQ(String qParam, String keywords) {
		String q = qParam;
		int index = q.indexOf(keywords);
		if (index != -1) {
			q = q.substring(0, index).trim();
		}
		return q;
	}

	public void highlightQuote(QuoteEntry quoteEntry) {
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

	public String getKeywordFromFilterString(String filterString, long monitorId) {
		String keyword = null;
		try {
			if (filterString != null && !filterString.isEmpty()) {
				MonitorWizardFilters fwFilters = JSONUtility.deserialize(filterString, MonitorWizardFilters.class);
				keyword = fwFilters.advanced.keywords;
				if (LOG.isDebugEnabled() && (keyword != null && !keyword.isEmpty())) {
					LOG.debug("Keyword " + keyword + " found for monitordoc " + monitorId);
				}
			}
		} catch (Exception e) {
			LOG.warn("Illegal filterString caught {" + filterString + "} for monitordoc " + monitorId);
		}
		return keyword;
	}

	public String getQToken(String query, String keyword) {
		String q = query.split("&")[0];
		if (keyword != null && !keyword.isEmpty()) {
			if (q.lastIndexOf(keyword) > 2) { // check if keyword is actually present in q
				q = q.substring(0, q.length() - keyword.length());
			}
		}
		return q.trim();
	}

	public boolean getHasMoreValue(int actual, int expectedParam, Short start) {
		int expected = expectedParam;
		boolean hasMore = false;
		if (start != null && start > 0) {
			expected = expected + start;
		}
		if (actual > expected) {
			hasMore = true;
		}
		return hasMore;
	}

	public BucketSpec getDateBucketingSpec(DateBucketingMode mode, String methodName) {
		BucketSpec bSpec = new BucketSpec();
		bSpec.dateFieldOrMethodName = methodName;
		bSpec.bucketSizeThreshold = 1;
		bSpec.mode = mode;
		bSpec.dateBucket = true;
		return bSpec;
	}

	public List<DocEntry> filterSimilarEntries(List<DocEntry> entries, DocumentSimilarityUtil dsutil, int reqCountAfterFiltering,
			boolean sendAllEntries) {
		List<DocEntry> filteredEntries = null;

		try {
			// return if null
			if (entries == null || entries.isEmpty()) {
				return null;
			}

			List<FR_ISiteDocument> siteDocList = createSiteDocumentList(entries);
			// process siteDocList for documentSimilarity
			filteredEntries = getFilteredEntries(dsutil, filteredEntries, reqCountAfterFiltering, sendAllEntries, siteDocList);
		} catch (Exception e) {
			LOG.error("Exception while processing docEntries for DocumentSimilarity ", e);
			filteredEntries = entries;
		}
		return filteredEntries;
	}

	private List<DocEntry> getFilteredEntries(final DocumentSimilarityUtil dsutil, List<DocEntry> filteredEntriesParam, final int reqCountAfterFiltering, final boolean sendAllEntries, List<FR_ISiteDocument> siteDocList) throws Exception {
		List<DocEntry> filteredEntries = filteredEntriesParam;
		siteDocList = dsutil.processDocument(siteDocList, -1);
		if (siteDocList != null && !siteDocList.isEmpty()) {
			filteredEntries = new ArrayList<DocEntry>(siteDocList.size());
			int i = (sendAllEntries) ? siteDocList.size() : Math.min(siteDocList.size(), reqCountAfterFiltering);
			for (int j = siteDocList.size() - 1; i > 0; i--, j--) {
				DocEntrySiteDocument doc = (DocEntrySiteDocument) siteDocList.get(j);
				filteredEntries.add(doc.getOriginalDocEntry());
			}
		}
		return filteredEntries;
	}

	private List<FR_ISiteDocument> createSiteDocumentList(final List<DocEntry> entries) {
		List<FR_ISiteDocument> siteDocList = new ArrayList<FR_ISiteDocument>(entries.size());
		
		// create siteDocList (in site_doc_id asc order as expected by DocumentSimilarityUtil)
		for (int i = entries.size() - 1; i >= 0; i--) {
			DocEntry doc = entries.get(i);
			siteDocList.add(new DocEntrySiteDocument(doc));
		}
		return siteDocList;
	}

	/**
	 * Filter DocEntries
	 * 
	 * @param numberOfDocs
	 * @param docEntries
	 * @param allCatIdSet
	 * @param entityInfoCache
	 * @return
	 */
	public List<Document> filterDocEntries(int numberOfDocs, List<DocEntry> docEntries, Set<String> allCatIdSet,
			IEntityInfoCache entityInfoCache, boolean onlyCompany, boolean needPagination, Boolean onlyIndustry) {
		if (docEntries == null) {
			return null;
		}
		// categories round robin algo is not required with pagination and onlyindustry case
		if (onlyIndustry || needPagination) {
			return filterDocEntriesPagination(numberOfDocs, docEntries, allCatIdSet, entityInfoCache, onlyCompany, needPagination, onlyIndustry);
		}

		List<Document> documentEntries = new ArrayList<Document>();
		try {
			// for each date we'll make a catId VS doc map to apply round robin algo
			Map<String, Map<String, List<DocEntry>>> dateVScatIdDocsMap = new LinkedHashMap<String, Map<String, List<DocEntry>>>();
			SimpleDateFormat sdf = new SimpleDateFormat("dd MM yyyy");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

			for (DocEntry doc : docEntries) {
				String date = sdf.format(doc.getInsertTime());
				Map<String, List<DocEntry>> catIdVsDocsMap = dateVScatIdDocsMap.get(date);
				if (catIdVsDocsMap == null) {
					catIdVsDocsMap = new HashMap<String, List<DocEntry>>();
				}

				retrieveAndPopulateMap(allCatIdSet, catIdVsDocsMap, date, dateVScatIdDocsMap, doc, onlyCompany);
			}

			// now get the desired documents from each category using round robin algo
			for (Entry<String, Map<String, List<DocEntry>>> entry : dateVScatIdDocsMap.entrySet()) {
				// for a particular date, add documents from all categories
				Map<String, List<DocEntry>> catIdVsDocsMap = entry.getValue();
				int i = 0;
				int maxSize = 0;
				while (i < maxSize || i == 0) {
					for (Entry<String, List<DocEntry>> entry1 : catIdVsDocsMap.entrySet()) {

						IEntityInfo entityInfo = entityInfoCache.catIdToEntity(entry1.getKey());
						Entity entity = convertUtil.convertEntityInfo(entityInfo);

						maxSize = retrieveMaxSizeAndCovertDocument(documentEntries, entity, entry1, i, maxSize);

						if (documentEntries.size() == numberOfDocs) {
							return documentEntries;
						}
					}
					i++;
				}
			}
		} catch (Exception e) {
			LOG.error("Exception in Filtering Doc Entries", e);
		}
		return documentEntries;
	}

	private void retrieveAndPopulateMap(final Set<String> allCatIdSet, final Map<String, List<DocEntry>> catIdVsDocsMap, final String date, final Map<String, Map<String, List<DocEntry>>> dateVScatIdDocsMap, final DocEntry doc, final boolean onlyCompany) {
		List<DocCatEntry> matchedEntities = createDocCatEntryList(onlyCompany, doc); 
		
		for (DocCatEntry docCatEntry : matchedEntities) {
			String catId = docCatEntry.getEntity().getId();
		
			if (allCatIdSet.contains(catId) && docCatEntry.getBand() == DocCatEntry.BAND_SELECTIVE) {
				List<DocEntry> docs = catIdVsDocsMap.get(catId);
				if (docs == null) {
					docs = new ArrayList<DocEntry>();
					catIdVsDocsMap.put(catId, docs);
				}
				docs.add(doc);
				break;
			}
		}
		dateVScatIdDocsMap.put(date, catIdVsDocsMap);
	}

	private int retrieveMaxSizeAndCovertDocument(final List<Document> documentEntries, final Entity entity, final Entry<String, List<DocEntry>> entry1, final int i, int maxSizeParam) {
		int maxSize = maxSizeParam;
		int size = entry1.getValue().size();
		// calculate the max size of documents from all categories for each date
		if (i == 0) {
			maxSize = Math.max(maxSize, size);
		}
		if (i < size) {
			Document document = convertUtil.convertDocumentPOJOFromDocEntry(entry1.getValue().get(i));
			document.setPrimaryEntity(entity);
			documentEntries.add(document);
		}
		return maxSize;
	}

	private List<Document> filterDocEntriesPagination(int numberOfDocs, List<DocEntry> docEntries, Set<String> allCatIdSet,
			IEntityInfoCache entityInfoCache, boolean onlyCompany, boolean needPagination, Boolean onlyIndustry) {
		if (docEntries == null) {
			return null;
		}
		List<Document> documentEntries = new ArrayList<Document>();
		Entity onlyIndustryEntity = null;

		// fetch industry entity for the only industry
		onlyIndustryEntity = getOnlyIndustryEntity(allCatIdSet, entityInfoCache, onlyIndustry, onlyIndustryEntity);


		try {
			for (DocEntry doc : docEntries) {
				if (!Boolean.TRUE.equals(onlyIndustry)) {

					List<DocCatEntry> matchedEntities = createDocCatEntryList(onlyCompany, doc); 
					
					for (DocCatEntry docCatEntry : matchedEntities) {
						String catId = docCatEntry.getEntity().getId();

						if (!needPagination && allCatIdSet.contains(catId) && docCatEntry.getBand() == DocCatEntry.BAND_SELECTIVE) {
							IEntityInfo entityInfo = entityInfoCache.catIdToEntity(catId);
							convertAndAdd(doc, documentEntries, entityInfo);
							break;
						} else if (needPagination && allCatIdSet.contains(catId)) {
							IEntityInfo entityInfo = entityInfoCache.catIdToEntity(catId);
							convertAndAdd2(doc, documentEntries, entityInfo);
							break;
						}
					}

				} else {
					addDocument(doc, onlyIndustryEntity, documentEntries);
				}

				if (documentEntries.size() == numberOfDocs) {
					break;
				}
			}
			Collections.sort(documentEntries, new PojoDocumentDateComparator());
		} catch (Exception e) {
			LOG.error("Exception in Filtering Doc Entries", e);
		}
		return documentEntries;
	}

	private Entity getOnlyIndustryEntity(final Set<String> allCatIdSet, final IEntityInfoCache entityInfoCache, final Boolean onlyIndustry, Entity onlyIndustryEntityParam) {
		Entity onlyIndustryEntity = onlyIndustryEntityParam;
		if (Boolean.TRUE.equals(onlyIndustry)) {
			String catId = null;
			for (Iterator<String> it = allCatIdSet.iterator(); it.hasNext();) {
				catId = it.next();
				break;
			}
			IEntityInfo entityInfo = entityInfoCache.catIdToEntity(catId);
			if (entityInfo != null) {
				onlyIndustryEntity = convertUtil.convertEntityInfo(entityInfo);
			}
		}
		return onlyIndustryEntity;
	}

	private void convertAndAdd(final DocEntry doc, final List<Document> documentEntries, final IEntityInfo entityInfo) {
		Entity entity = convertUtil.convertEntityInfo(entityInfo);
		addDocument(doc, entity, documentEntries);
	}

	private void convertAndAdd2(final DocEntry doc, final List<Document> documentEntries, final IEntityInfo entityInfo) {
		Entity entity = convertUtil.convertEntityInfo(entityInfo);
		addDocument(doc, entity, documentEntries);
	}

	private void addDocument(final DocEntry doc, final Entity entity, final List<Document> documentEntries) {
		Document document = convertUtil.convertDocumentPOJOFromDocEntry(doc);
		document.setPrimaryEntity(entity);
		documentEntries.add(document);
	}
 
	private List<DocCatEntry> createDocCatEntryList(final boolean onlyCompany, final DocEntry doc) { 
		List<DocCatEntry> matchedEntities = new ArrayList<DocCatEntry>(); 
		if (onlyCompany) { 
			matchedEntities.addAll(doc.getMatchedCompanies()); 
		} else { 
			matchedEntities.addAll(doc.getMatchedCompanies()); 
			matchedEntities.addAll(doc.getMatchedTopics()); 
		} 
		return matchedEntities; 
	} 
	

	public class PojoDocumentDateComparator implements Comparator<Document> {
		@Override
		public int compare(Document o1, Document o2) {
			return o2.getDate().compareTo(o1.getDate());
		}
	}

	public BaseSpec setSourceContent(boolean excludeSourceContent, boolean includeSourceContent, BaseSpec baseSpec,
			EnterprisePref enterprisePref) {
		if (excludeSourceContent) {
			if (enterprisePref.getPublicSourceIdsSSV() != null) {
				baseSpec.setExcludeSourceIdsSSV(enterprisePref.getPublicSourceIdsSSV().trim());
			}
		} else if (includeSourceContent) {

			String includeSourceIdsSSV = "";
			if (enterprisePref.getPrivateSourceIdsSSV() != null) {
				includeSourceIdsSSV += enterprisePref.getPrivateSourceIdsSSV().trim();
			}
			if (enterprisePref.getPublicSourceIdsSSV() != null) {
				includeSourceIdsSSV += " " + enterprisePref.getPublicSourceIdsSSV().trim();
			}

			baseSpec.setIncludeSourceIdsSSV(includeSourceIdsSSV.trim());
		}
		return baseSpec;
	}

	/*
	 * This method returns keywords (invalid filters and composite filters) and validated advanced filters in separate lists
	 */
	public List<List<String>> getKeywordsAndFilters(List<String> filtersToAddList) throws Exception {

		List<String> keywordList = new ArrayList<String>();
		List<String> newFilterList = new ArrayList<String>();

		List<List<String>> keywordsAndFiltersLists = new ArrayList<List<String>>();
		keywordsAndFiltersLists.add(keywordList);
		keywordsAndFiltersLists.add(newFilterList);

		for (String filter : filtersToAddList) {

			boolean isExclusionFilter = false;
			String tempFilter = filter;

			if (tempFilter.startsWith("-") && tempFilter.length() >= 2) {
				isExclusionFilter = true;
				tempFilter = tempFilter.substring(1);
			}

			IEntityInfo entityInfo = entityBaseServiceRepository.getEntityInfoCache().searchTokenToEntity(tempFilter);

			if (entityInfo == null) {
				if (!keywordList.contains(filter)) {
					keywordList.add(filter);
				}
			} else {
				// add new search token for this filter
				filter = entityInfo.getSearchToken();
				if (isExclusionFilter) {
					filter = "-" + filter;
				}
				if (!newFilterList.contains(filter)) {
					newFilterList.add(filter);
				}
			}
		}

		return keywordsAndFiltersLists;
	}

	public MgmtTurnoverServiceSpec getMgmtSpec(int count, int days, int start) {
		MgmtTurnoverServiceSpec spec = new MgmtTurnoverServiceSpec();
		spec.lastDay = new Date();
		spec.days = days;
		spec.details = true;
		spec.monthlyCount = false;
		spec.quarterlyCount = false;
		spec.setRows(count);
		spec.setStart(start);
		return spec;
	}

	public void mapHistoricalStatsToTriggers(Graph graphData, List<Event> events) {
		List<HistoricalStat> hs = graphData.getHistoricalStat();
		if (hs != null && !hs.isEmpty() && events != null && !events.isEmpty()) {
			Map<Integer, List<Event>> map = createEventMap(events);

			populateSignals(hs, map);

		}
	}

	private Map<Integer, List<Event>> createEventMap(final List<Event> events) {
		Map<Integer, List<Event>> map = new HashMap<Integer, List<Event>>();
		
		for (Event ev : events) {
			Event o = (Event) ev;
			List<Event> el = map.get(o.getDayId());
			if (el == null) {
				el = new ArrayList<Event>();
				map.put(o.getDayId(), el);
			}
			el.add(o);
			map.put(o.getDayId(), el);
		}
		return map;
	}

	private void populateSignals(final List<HistoricalStat> hs, final Map<Integer, List<Event>> map) {
		for (HistoricalStat h : hs) {
			HistoricalStat ho = (HistoricalStat) h;
			int dayId = ho.getCompanyVolume().getDiffId();
			List<Event> el = map.get(dayId);
			if (el != null && !el.isEmpty()) {
				EventSet esObj = new EventSet();
				esObj.setEvents(new ArrayList<Event>(el));
				ho.setSignals(esObj.getEvents());
			}
		}
	}

	public Map<Long, Integer> getNDaysCountForEntities(List<Long> inputCategories, int days) {
		IEntityInfoCache entityInfoCache = entityBaseServiceRepository.getEntityInfoCache();
		if (entityInfoCache == null) {
			return null;
		} else {
			Map<Long, Integer> rs = new HashMap<Long, Integer>();
			for (Long catId : inputCategories) {
				IEntityInfo catIdToEntity = entityInfoCache.catIdToEntity(String.valueOf(catId));
				if (catIdToEntity == null) {
					continue;
				} else {
					if (days == 7) {
						Integer docVolumeWeek = catIdToEntity.getDocVolumeWeek();
						rs.put(catId, docVolumeWeek);
					} else {
						rs.put(catId, catIdToEntity.getDocCount());
					}
				}
			}

			return rs;
		}
	}

	public String getSearchTokenOfCatId(String catId) {
		IEntityInfoCache entityInfoCache = entityBaseServiceRepository.getEntityInfoCache();
		if (entityInfoCache == null) {
			return null;
		}

		IEntityInfo catIdToEntity = entityInfoCache.catIdToEntity(catId);
		if (catIdToEntity == null) {
			LOG.info("catId is not present in entityInfoCache : " + catId);
			return null;
		}
		return catIdToEntity.getSearchToken();
	}

}
