package com.firstrain.frapi.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.firstrain.frapi.domain.BaseSpec;
import com.firstrain.frapi.domain.ManagementChange;
import com.firstrain.frapi.domain.MgmtTurnoverData;
import com.firstrain.frapi.domain.MgmtTurnoverSummary;
import com.firstrain.frapi.events.IEvents;
import com.firstrain.frapi.events.IEvents.EventGroupEnum;
import com.firstrain.frapi.events.IEvents.EventTypeEnum;
import com.firstrain.frapi.obj.MgmtTurnoverServiceSpec;
import com.firstrain.frapi.obj.SearchTokenSpec;
import com.firstrain.frapi.pojo.Entity;
import com.firstrain.frapi.pojo.EntityBriefInfo;
import com.firstrain.frapi.pojo.Event;
import com.firstrain.frapi.pojo.wrapper.EventSet;
import com.firstrain.frapi.repository.CompanyServiceRepository;
import com.firstrain.frapi.service.CompanyService;
import com.firstrain.frapi.service.EntityBaseService;
import com.firstrain.frapi.service.EventService;
import com.firstrain.frapi.util.ConvertUtil;
import com.firstrain.frapi.util.DefaultEnums.INPUT_ENTITY_TYPE;
import com.firstrain.frapi.util.DefaultEnums.MatchedEntityConfidenceScore;
import com.firstrain.frapi.util.FRAPIConstant;
import com.firstrain.frapi.util.StatusCode;
import com.firstrain.obj.IEntityInfo;
import com.firstrain.obj.IEntityInfoCache;
import com.firstrain.solr.client.SearchException;
import com.firstrain.solr.client.SearchTokenEntry;
import com.firstrain.solr.client.SolrSearcher;
import com.firstrain.utils.FR_ArrayUtils;
import com.firstrain.web.pojo.EntityMatchInputBean;
import com.firstrain.web.pojo.EntityMatchInputBean.EntityInput;
import com.google.common.net.InternetDomainName;

@Service
public class CompanyServiceImpl implements CompanyService {

	private final Logger LOG = Logger.getLogger(CompanyServiceImpl.class);
	// private HttpClient httpClient = new HttpClient(new MultiThreadedHttpConnectionManager());

	@Autowired
	@Qualifier("companyServiceRepositoryImpl")
	private CompanyServiceRepository companyServiceRepository;
	@Autowired
	private EventService eventService;
	@Autowired
	private ConvertUtil convertUtil;
	@Autowired
	private EntityBaseService entityBaseService;
	// @Value("${google.chart.url}")
	// private String googleChartUrl;

	// @Value("${check.mgmtgraph.url}")
	// private String checkGraphURL;

	// @Value("${persist.mgmtgraph.url.csv}")
	// private String persistGraphUrlCsv;

	// private String[] persistGraphUrlArr;

	// @PostConstruct
	// public void init() throws ServiceException {
	// if (persistGraphUrlCsv != null && !persistGraphUrlCsv.isEmpty()) {
	// persistGraphUrlArr = persistGraphUrlCsv.split(",");
	// } else {
	// LOG.warn("Property persist.mgmtgraph.url.csv not configured");
	// }
	// }


	@Override
	public SolrDocument getCompanyDocuments(String searchToken) throws Exception {
		SolrDocument solrDocument = null;
		try {
			solrDocument = companyServiceRepository.getCompanyInfoFromIndex(searchToken);
		} catch (SolrServerException e) {
			LOG.error("Error getCompanyDocuments()", e);
			throw e;
		}

		return solrDocument;
	}

	@Override
	public MgmtTurnoverServiceSpec getDefaultMonthlySpec() {
		MgmtTurnoverServiceSpec mtmtSpec = new MgmtTurnoverServiceSpec();
		mtmtSpec.details = false;
		mtmtSpec.days = 365;
		mtmtSpec.lastDay = new Date();
		mtmtSpec.lhs = false;
		mtmtSpec.quarterlyCount = false;
		mtmtSpec.monthlyCount = true;
		mtmtSpec.monthlyCountForMonth = 12;
		return mtmtSpec;
	}

	@Override
	public MgmtTurnoverData getMgmtTurnoverData(MgmtTurnoverServiceSpec mtmtSpec, BaseSpec spec) throws Exception {
		MgmtTurnoverData mgmtTurnoverData = null;
		try {
			IEntityInfoCache entityCache = companyServiceRepository.getEntityInfoCache();
			IEntityInfo ieEntityInfo = entityCache.catIdToEntity(spec.getCacheKey());
			if (ieEntityInfo == null) {
				return null;
			}
			List<String> compTokens = new ArrayList<String>();
			compTokens.add(ieEntityInfo.getSearchToken());
			MgmtTurnoverSummary mgmtSummary = this.getMgmtTurnover(compTokens, mtmtSpec);
			mgmtTurnoverData = mgmtSummary.getMgmtTurnoverDataFor(ieEntityInfo.getSearchToken());
			mgmtTurnoverData.setCompanyId(ieEntityInfo.getCompanyId());

			/** persist mgmt chart on configured location */
			// if(checkGraphURL != null && !checkGraphURL.isEmpty()) {
			// persistGraphsOnDisk(mgmtTurnoverData, ieEntityInfo.getCompanyId());
			// }
		} catch (Exception e) {
			LOG.error("Error Getting MgmtTurnOver", e);
			throw e;
		}
		return mgmtTurnoverData;
	}

	@Override
	public EventSet getCompanyEvents(BaseSpec spec, String csExcludedEventTypeGroup, Map<Integer, SolrDocument> eventDocMap)
			throws Exception {
		spec.setCsExcludedEventTypeGroup(csExcludedEventTypeGroup);
		return getCompanyEvents(spec, eventDocMap);
	}

	@Override
	public EventSet getCompanyEvents(BaseSpec spec, Map<Integer, SolrDocument> eventDocMap) throws Exception {

		EventSet es = null;
		List<IEvents> eventList;
		try {
			IEntityInfoCache entityInfoCache = companyServiceRepository.getEntityInfoCache();
			IEntityInfo entityInfo = entityInfoCache.catIdToEntity(spec.getCacheKey());
			if (entityInfo == null) {
				return null;
			}
			SolrSearcher searcher = companyServiceRepository.getSearcher();
			if (spec.getCsExcludedEventTypeGroup() != null && !spec.getCsExcludedEventTypeGroup().isEmpty()) {
				eventList = eventService.getCompanyEvents(entityInfo.getCompanyId(), spec.getCsExcludedEventTypeGroup(), eventDocMap);
			} else {
				eventList = eventService.getCompanyEvents(entityInfo.getCompanyId(), null, eventDocMap);
			}

			eventList = retrieveAndApplyFilter(eventList);
			if (eventList == null || eventList.isEmpty()) {
				return es;
			}
			int totalCount = eventList.size();
			if (totalCount <= spec.getStart()) {
				return es;
			}
			eventList = retrieveSubList(eventList, spec, totalCount);

			es = createEventSet(es, eventDocMap, eventList, searcher, spec, totalCount);

		} catch (Exception e) {
			LOG.error("Error getCompanyEvents()", e);
			throw e;
		}
		return es;
	}

	private EventSet createEventSet(EventSet esParam, final Map<Integer, SolrDocument> eventDocMap, final List<IEvents> eventList, final SolrSearcher searcher, final BaseSpec spec, final int totalCount) throws Exception {
		EventSet es = esParam;
		boolean isIpad = (spec.getIpad() == null ? false : spec.getIpad().booleanValue());
		List<Event> events = convertUtil.convertToBaseType(eventList, isIpad, true);
		convertUtil.attachPropertiesForExpiredEvents(searcher, events);
		if (events != null && !events.isEmpty()) {
			// attaching related document with event
			if (spec.isNeedRelatedDoc()) {
				entityBaseService.attachRelatedDocumentDetails(events, eventDocMap, spec);
			}
			es = new EventSet(events);
			es.setTotalCount(totalCount);
		}
		return es;
	}

	private List<IEvents> retrieveAndApplyFilter(List<IEvents> eventList) {
		if (eventList != null && !eventList.isEmpty()) {
			for (Iterator<IEvents> iter = eventList.iterator(); iter.hasNext();) {
				IEvents event = iter.next();
				if (event.getEventGroup() == EventGroupEnum.GROUP_8K_FILING && event.getEventType() != EventTypeEnum.TYPE_TEN_K_EVENT
						&& event.getEventType() != EventTypeEnum.TYPE_TEN_Q_EVENT) {
					iter.remove();
				}
			}
		}
		
		/* Change for providing Navigation. We need all events after applying below filter */
		// eventList = eventService.applySingleCompanyEventsFilter(eventList, spec.getCount(), false);
		return eventService.applySingleCompanyEventsFilter(eventList, FRAPIConstant.MAX_NO_OF_EVENTS, false);
	}

	private List<IEvents> retrieveSubList(List<IEvents> eventListParam, final BaseSpec spec, final int totalCount) {
		List<IEvents> eventList = eventListParam;
		if (totalCount >= (spec.getStart() + spec.getCount())) {
			int toIndex = spec.getStart() + spec.getCount();
			eventList = eventList.subList(spec.getStart(), toIndex);
		} else {
			eventList = eventList.subList(spec.getStart(), totalCount);
		}
		return eventList;
	}

	@Override
	public EntityBriefInfo getCompanyAutoSuggestList(String q, EntityMatchInputBean entityMatchInputBean) throws Exception {
		EntityBriefInfo entityBriefInfo = new EntityBriefInfo();
		List<Entity> entityList = new ArrayList<Entity>();

		boolean isAutoSuggest = true;

		// suggest on the basis of q
		if (q != null && !q.isEmpty()) {
			entityList = companyServiceRepository.getMatchedCompanyForQ(q);
			if (entityList != null && !entityList.isEmpty()) {
				// will not use auto suggest in this case
				isAutoSuggest = false;
				int size = entityList.size();
				for (Entity entity : entityList) {
					if (size == 1) {
						entity.setBand((int) MatchedEntityConfidenceScore.VERY_HIGH.getValue());
					} else { // handling case of tickers or multiple primary tokens passed for different entities
						entity.setBand((int) MatchedEntityConfidenceScore.HIGH.getValue());
					}

				}
			} else {
				LOG.debug("No Company found after Solr Search");
			}
		}

		// entity match service on the basis of company name
		EntityInput ei = entityMatchInputBean.getCompany();
		if (ei.getName() != null && isAutoSuggest) {
			String compName = ei.getName().trim();
			entityList = entityBaseService.autoSuggestForEntity(compName, INPUT_ENTITY_TYPE.COMPANY.name(), entityMatchInputBean.getCount(),
					null, null);
			if (entityList != null) {
				// set relevance for matched companies
				// if any of these additional info input is provided
				if (ei.getHomePage() != null || ei.getAddress() != null || ei.getCountry() != null || ei.getCity() != null
						|| ei.getState() != null || ei.getZip() != null) {

					StringBuilder companyIds = new StringBuilder();
					Map<String, Entity> compEntityMap = new LinkedHashMap<String, Entity>();
					for (Entity entity : entityList) {
						String compId = entity.getCompanyId().toString();
						companyIds.append(compId).append(" ");
						compEntityMap.put(compId, entity);
					}
					if (companyIds.length() > 0) {
						companyIds.setLength(companyIds.length() - 1);
						SolrDocumentList solrDocumentList =
								companyServiceRepository.getCompanyDetailsFromCompanyIds(companyIds.toString(), entityList.size());
						Entity highRelevantCompany = null;
						for (SolrDocument doc : solrDocumentList) {
							String companyIdStr = (String) doc.getFieldValue("attrCompanyId");
							Entity entity = compEntityMap.get(companyIdStr);
							// set relevance band on the basis of confidence score
							setRelevancebandForMatchedEntity(compName, entity, doc, ei);
							// only one company will be returned in this case
							if (entity.getBand() == MatchedEntityConfidenceScore.VERY_HIGH.getValue()) {
								highRelevantCompany = entity;
								break;
							}
						}
						// only one company will be returned in this case
						if (highRelevantCompany != null) {
							entityList.clear();
							entityList.add(highRelevantCompany);
						} else {
							entityList = new ArrayList<Entity>(compEntityMap.values());
							// compare on the basis of confidence score
							Collections.sort(entityList, new Comparator<Entity>() {
								@Override
								public int compare(Entity o1, Entity o2) {
									return o2.getBand().compareTo(o1.getBand());
								}
							});
						}
					}
				} else { // if only company name is provided
					for (Entity entity : entityList) {

						String synonym = entity.getSynonym();
						// exact company match case
						if (synonym != null && synonym.length() > 0 && compName.equalsIgnoreCase(synonym)) {
							entity.setBand((int) MatchedEntityConfidenceScore.HIGH.getValue());
						} else if (compName.equalsIgnoreCase(entity.getName())) {
							entity.setBand((int) MatchedEntityConfidenceScore.MEDIUM.getValue());
						} else { // partial company match case
							entity.setBand((int) MatchedEntityConfidenceScore.LOW.getValue());
						}
					}
				}
			} else {
				LOG.debug("No Companyfound after AutoSuggest");
			}
		}

		// New Development to handle home page as primary field
		if (entityList == null || entityList.isEmpty() && ei.getHomePage() != null) {

			String inputHomePage = ei.getHomePage().toLowerCase().replace("https://", "").replace("http://", "");
			if (inputHomePage.startsWith("www.")) {
				inputHomePage = inputHomePage.replace("www.", "");
			}
			if (inputHomePage.startsWith(".")) {
				inputHomePage = inputHomePage.replace(".", "");
			}

			StringBuilder homePageQuery = new StringBuilder();

			homePageQuery.append("attrWebsite:").append("http*." + inputHomePage + "*").append(" OR ").append("attrWebsite:")
			.append("www*." + inputHomePage + "*").append(" OR ").append("attrWebsite:").append(inputHomePage + "*");

			int index = inputHomePage.indexOf("/");
			String inputHomePageWithoutSuffix = inputHomePage;
			if (index != -1) {
				inputHomePageWithoutSuffix = appendQuery(inputHomePage, index, homePageQuery); 
				
			}

			String inputHomePageWithoutCountry = null;

			if (hasCountrySuffix(inputHomePageWithoutSuffix)) {
				index = inputHomePageWithoutSuffix.lastIndexOf(".");
				inputHomePageWithoutCountry = appendQuery(inputHomePageWithoutSuffix, index, homePageQuery); 
				

				homePageQuery.append(" OR ").append("attrWebsite:").append("http*." + inputHomePageWithoutCountry + "/*").append(" OR ")
				.append("attrWebsite:").append("www*." + inputHomePageWithoutCountry + "/*").append(" OR ").append("attrWebsite:")
				.append(inputHomePageWithoutCountry + "/*");
			}

			SolrDocumentList solrDocumentList = companyServiceRepository.getCompanySolrDocsForQuery(homePageQuery.toString());

			if (solrDocumentList != null && !solrDocumentList.isEmpty()) {

				if (entityList == null) {
					entityList = new ArrayList<Entity>();
				}

				for (SolrDocument doc : solrDocumentList) {
					String website = (String) doc.getFieldValue("attrWebsite");

					if (website == null || website.isEmpty()) {
						continue;
					}

					if (ei.getHomePage().equalsIgnoreCase(website)) {
						entityList.add(getEntityFromSolrDoc(doc, (int) MatchedEntityConfidenceScore.HIGH.getValue()));
						continue;
					}

					website = website.toLowerCase().replace("https://", "").replace("http://", "");
					if (website.startsWith("www.")) {
						website = website.replace("www.", "");
					}
					if (inputHomePage.equals(website)) {
						entityList.add(getEntityFromSolrDoc(doc, (int) MatchedEntityConfidenceScore.HIGH.getValue()));
						continue;
					}
					if (website.endsWith("." + inputHomePage) || website.startsWith(inputHomePage)
							|| website.contains("." + inputHomePage + ".")) {
						entityList.add(getEntityFromSolrDoc(doc, (int) MatchedEntityConfidenceScore.MEDIUM.getValue()));
						continue;
					}

					if (website.endsWith("." + inputHomePageWithoutSuffix) || website.equals(inputHomePageWithoutSuffix)) {
						entityList.add(getEntityFromSolrDoc(doc, (int) MatchedEntityConfidenceScore.MEDIUM.getValue()));
						continue;
					}

					if (inputHomePageWithoutCountry != null
							&& (website.equals(inputHomePageWithoutCountry) || website.endsWith("." + inputHomePageWithoutCountry))) {
						entityList.add(getEntityFromSolrDoc(doc, (int) MatchedEntityConfidenceScore.MEDIUM.getValue()));
						continue;
					}

					// finally remove .sg etc as suffix
					// int indexOfSuffix = inputHomePage.lastIndexOf(".");
					// if(indexOfSuffix != -1 && inputHomePage.substring(indexOfSuffix+1).length() == 2) {
					// inputHomePage = inputHomePage.substring(0, indexOfSuffix);
					// if(website.equals(inputHomePage) || website.startsWith(inputHomePage) || website.endsWith("." + inputHomePage)
					// || website.contains("." + inputHomePage + ".")) {
					//
					// entityList.add(getEntityFromSolrDoc(doc, (int)MatchedEntityConfidenceScore.MEDIUM.getValue()));
					// continue;
					// }
					// }
				}
			}
		}

		if (entityList != null && !entityList.isEmpty()) {
			entityBriefInfo.setMatchedEntity(entityList);
			entityBriefInfo.setStatusCode(StatusCode.REQUEST_SUCCESS);
		} else {
			entityBriefInfo.setStatusCode(StatusCode.ENTITY_NOT_FOUND);
		}
		return entityBriefInfo;
	}
 
	private String appendQuery(final String inputHomePageWithoutSuffix, final int index, final StringBuilder homePageQuery) { 
		String inputHomePageWithoutCountry = inputHomePageWithoutSuffix.substring(0, index); 
		 
		homePageQuery.append(" OR ").append("attrWebsite:").append("http*." + inputHomePageWithoutCountry).append(" OR ") 
		.append("attrWebsite:").append("www*." + inputHomePageWithoutCountry).append(" OR ").append("attrWebsite:") 
		.append(inputHomePageWithoutCountry); 
		return inputHomePageWithoutCountry; 
	} 
	

	private Entity getEntityFromSolrDoc(SolrDocument doc, int confidence) {

		String companyIdStr = (String) doc.getFieldValue("attrCompanyId");
		IEntityInfo entityInfo = companyServiceRepository.getEntityInfoCache().companyIdToEntity(Integer.parseInt(companyIdStr));
		Entity entity = convertUtil.convertEntityInfo(entityInfo);
		entity.setBand(confidence);
		entity.setHomePage((String) doc.getFieldValue("attrWebsite"));
		return entity;
	}

	private void setRelevancebandForMatchedEntity(String compName, Entity entity, SolrDocument doc, EntityInput ei) {
		// if any of the input match with the entity
		if ((ei.getAddress() != null && ei.getAddress().equalsIgnoreCase((String) doc.getFieldValue("attrAddress")))
				|| (ei.getHomePage() != null && doc.getFieldValue("attrWebsite") != null
				&& isHomePageMatched(ei.getHomePage(), (String) doc.getFieldValue("attrWebsite")))
				|| (ei.getCountry() != null && ei.getCountry().equalsIgnoreCase((String) doc.getFieldValue("attrCountry")))
				|| (ei.getCity() != null && ei.getCity().equalsIgnoreCase((String) doc.getFieldValue("attrCity")))
				|| (ei.getState() != null && ei.getState().equalsIgnoreCase((String) doc.getFieldValue("attrStateName")))
				|| (ei.getZip() != null && isZIPMatched(ei.getZip(), (String) doc.getFieldValue("attrZip")))
				|| (ei.getState() != null && ei.getState().equalsIgnoreCase((String) doc.getFieldValue("attrStateNameAbr")))) {

			String synonym = entity.getSynonym();
			// exact company match case
			LOG.debug("synonym == " + synonym + "compName == " + compName);
			if (synonym != null && synonym.length() > 0 && compName.equalsIgnoreCase(synonym.trim())) {
				populateBand(ei, doc, entity); 
				
			} else if (compName.equalsIgnoreCase(entity.getName())) {
				populateBand(ei, doc, entity); 
				
			} else {// partial company match case
				entity.setBand((int) MatchedEntityConfidenceScore.MEDIUM.getValue());
			}
		} else {// if no input match with the entity
			// exact company match case
			if (compName.equalsIgnoreCase(entity.getName())) {
				entity.setBand((int) MatchedEntityConfidenceScore.MEDIUM.getValue());
			} else {// partial company match case
				entity.setBand((int) MatchedEntityConfidenceScore.LOW.getValue());
			}
		}
	}
 
	private void populateBand(final EntityInput ei, final SolrDocument doc, final Entity entity) { 
		if (ei.getHomePage() != null && doc.getFieldValue("attrWebsite") != null 
				&& isHomePageMatched(ei.getHomePage(), (String) doc.getFieldValue("attrWebsite"))) { 
			entity.setBand((int) MatchedEntityConfidenceScore.VERY_HIGH.getValue()); 
		} else { 
			entity.setBand((int) MatchedEntityConfidenceScore.HIGH.getValue()); 
		} 
	} 
	
	
	public static void main(String[] args) {
		String homePageInput = "vinicorp.com.vn";
		String	homePage = "www.vinicorp.com.vn";	
		homePageInput = homePageInput.trim().replace("http://", "").replace("http://www.", "").replace("https://", "")
				.replace("https://www.", "").replace("www.", "");
		homePage = homePage.trim().replace("http://", "").replace("http://www.", "").replace("https://", "").replace("https://www.", "")
				.replace("www.", "").trim();

		if (homePageInput.equalsIgnoreCase(homePage)) {
			System.out.println("true");
		}else {
			System.out.println("false");
		}
	}
	private boolean isHomePageMatched(String homePageInput, String homePageParam) {
		String homePage = homePageParam;
		homePageInput = homePageInput.trim().replace("http://", "").replace("http://www.", "").replace("https://", "")
				.replace("https://www.", "").replace("www.", "");
		homePage = homePage.trim().replace("http://", "").replace("http://www.", "").replace("https://", "").replace("https://www.", "")
				.replace("www.", "").trim();

		if (homePageInput.equalsIgnoreCase(homePage)) {
			return true;
		}
		return false;
	}

	private boolean isZIPMatched(String inputZIP, String zip) {
		if (zip == null) {
			return false;
		}
		if (inputZIP.equalsIgnoreCase(zip)) {
			return true;
		}
		boolean isZIPMatched = false;

		if (!inputZIP.contains("-") && zip.contains("-") && inputZIP.equalsIgnoreCase(zip.split("-")[0])) {
			isZIPMatched = true;
		}
		return isZIPMatched;
	}

	private MgmtTurnoverSummary getMgmtTurnover(List<String> searchTokens, MgmtTurnoverServiceSpec spec) throws Exception {
		return getMgmtTurnover(new SearchTokenSpec().setSearchTokens(searchTokens), spec);
	}

	private MgmtTurnoverSummary getMgmtTurnover(SearchTokenSpec stSpec, MgmtTurnoverServiceSpec spec) throws Exception {

		IEntityInfoCache entityInfoCache = companyServiceRepository.getEntityInfoCache();
		if (stSpec == null || !stSpec.areSearchTokensAvailable()) {
			throw new IllegalArgumentException("Input tokens must be provided.");
		}
		List<String> searchTokens = stSpec.getSearchTokens();

		MgmtTurnoverSummary summary = new MgmtTurnoverSummary();
		try {
			List<Integer> companyIDs = new ArrayList<Integer>();
			FR_ArrayUtils.removeDuplicate(searchTokens);
			for (String token : searchTokens) {
				IEntityInfo entity = entityInfoCache.searchTokenToEntity(token);
				if (entity != null && entity.getType() == SearchTokenEntry.SEARCH_TOKEN_TAG_COMPANY) {
					companyIDs.add(entity.getCompanyId());
					summary.addMgmtTurnoverData(entity.getSearchToken(), new MgmtTurnoverData());
				}
			}

			if (companyIDs.isEmpty()) {
				LOG.debug("No Company in provided search Token");
				return summary;
			}

			getSummary(summary, companyIDs, spec);

			getDetails(summary, companyIDs, spec);

		} catch (Exception e) {
			LOG.error("Error getting Mgmt turnover summary " + e.getMessage(), e);
			throw e;
		}
		return summary;
	}

	private void getSummary(MgmtTurnoverSummary summary, List<Integer> companyIDs, MgmtTurnoverServiceSpec spec) throws Exception {
		try {

			IEntityInfoCache entityInfoCache = companyServiceRepository.getEntityInfoCache();
			if (!spec.monthlyCount && !spec.quarterlyCount) {
				return;
			}
			MgmtTurnoverData mgmtTurnover = new MgmtTurnoverData();

			// Call this in a loop for each company and put the value against the search token
			for (int cid : companyIDs) {
				String searchToken = entityInfoCache.companyIdToEntity(cid).getSearchToken();
				MgmtTurnoverData mgmtTurnoverData = (MgmtTurnoverData) summary.getMgmtTurnoverDataFor(searchToken);

				if (spec.monthlyCount) {
					companyServiceRepository.getMgmtTurnoverMonthlySummary(mgmtTurnover, cid, spec);
					mgmtTurnoverData.setAverageTurnoverMth(mgmtTurnover.getAverageTurnoverMth());
					mgmtTurnoverData.setMonthlySummary(mgmtTurnover.getMonthlySummary());
					mgmtTurnoverData.setTotalTurnoverMth(mgmtTurnover.getTotalTurnoverMth());
					// summary.setMaxMth(Math.max(summary.getMaxMth(), mgmtTurnover.getMonthlyMaxTurnover()));
				}
			}
		} catch (Exception e) {
			LOG.error("Error getting summary " + e.getMessage(), e);
			throw e;
		}
	}

	private void getDetails(MgmtTurnoverSummary summary, List<Integer> companyIDs, MgmtTurnoverServiceSpec spec)
			throws SolrServerException, SearchException {
		List<ManagementChange> mgmtDetailsList = companyServiceRepository.getMgmtTurnoverDetails(companyIDs, spec);
		if (mgmtDetailsList == null || mgmtDetailsList.isEmpty()) {
			return;
		}
		summary.setDetails(mgmtDetailsList);
	}

	// private void persistGraphsOnDisk(MgmtTurnoverData mgmtTurnoverData, int companyId) throws Exception {
	//
	// if (mgmtTurnoverData != null && mgmtTurnoverData.getMgmtChart() != null) {
	// if (checkGraphforCurrentDate(companyId, "mgmtgraph")) {
	// byte[] graphBytes = getMgmtChartBinaries(mgmtTurnoverData.getMgmtChart());
	// if (graphBytes != null && graphBytes.length > 0) {
	// if (!persist(graphBytes, companyId)) {
	// LOG.error("Unable to persist mgmt Graph for companyId: " + companyId);
	// }
	// } else {
	// LOG.warn("No mgmtGraphBytes generated for companyId: " + companyId);
	// }
	// }
	// }
	// }

	// private boolean checkGraphforCurrentDate(int companyID, String graphType) throws IOException {
	// boolean regenerateGraph = true;
	// // check if graph is already generated for current day
	// try {
	// String url = checkGraphURL + "?companyid=" + companyID + "&graphtype=" + graphType;
	// URL u = new URL(url);
	// // Open the connection and prepare to POST
	// URLConnection uc = u.openConnection();
	// InputStream in = uc.getInputStream();
	// BufferedReader r = new BufferedReader(new InputStreamReader(in));
	// StringBuffer buf = new StringBuffer();
	// String line;
	// while ((line = r.readLine()) != null) {
	// buf.append(line);
	// }
	//
	// if (buf.toString().equalsIgnoreCase("false")) {
	// regenerateGraph = false;
	// }
	// in.close();
	// } catch (IOException e) {
	// LOG.error("Exception checkGraphforCurrentDate() " + e.getMessage(), e);
	// throw e;
	// }
	// return regenerateGraph;
	// }

	// private boolean persist(byte[] data, int companyID) throws Exception {
	// boolean ispersist = false;
	//
	// try {
	// if (persistGraphUrlArr != null) {
	// for (String graphUrlStr : persistGraphUrlArr) {
	// String url = graphUrlStr + "?companyid=" + companyID + "&ismgmtgarph=true";
	// URL u = new URL(url);
	// // Open the connection and prepare to POST
	// URLConnection uc = u.openConnection();
	// uc.setDoOutput(true);
	// uc.setDoInput(true);
	// uc.setAllowUserInteraction(false);
	//
	// DataOutputStream dstream = new DataOutputStream(uc.getOutputStream());
	// dstream.write(data);
	// dstream.close();
	// InputStream in = uc.getInputStream();
	// BufferedReader r = new BufferedReader(new InputStreamReader(in));
	// StringBuffer buf = new StringBuffer();
	// String line;
	// while ((line = r.readLine()) != null) {
	// buf.append(line);
	// }
	//
	// if (buf.toString().equalsIgnoreCase("true")) {
	// ispersist = true;
	// } else {
	// LOG.error("Unable to persist mgmt Graph for companyId: " + companyID + " at " + url);
	// ispersist = false;
	// }
	// in.close();
	// }
	// } else {
	// LOG.error("No persistGraphUrl found to perist mgmt graph");
	// }
	// } catch (Exception e) {
	// // should do real exception handling
	// LOG.error("Failed to persist mgmt graph on "+persistGraphUrlCsv+" "+ e.getMessage(), e);
	// throw e;
	// }
	// return ispersist;
	// }

	// private byte[] getMgmtChartBinaries(MgmtChart managementChart) throws SQLException {
	// String chartURL =
	// "cht=bvg&chxt=x,r&chf=c,lg,90,efefef,1,ffffff,.7&chco=DD3333,77BB11,0066cc&chs=182x75&chd=t:"+managementChart.getValuesAsString()+"&chds=-"+managementChart.getMax()+","+managementChart.getMax()+"&chxl=0:"+managementChart.getLabelAsString()+"|1:|"+managementChart.getMax()+"||"+managementChart.getMax()+"&chbh=4,1,1&chm=R,000000,0,.085,.0855|R,000000,0,.168,.1685|R,000000,0,.252,.2525|R,000000,0,.335,.3355|R,000000,0,.418,.4185|R,000000,0,.500,.5005|R,000000,0,.584,.5845|R,000000,0,.664,.6645|R,000000,0,.749,.7495|R,000000,0,.835,.8355|R,000000,0,.915,.9155|R,000000,0,1.0,1.002|r,000000,0,0,.002|r,666666,0,.49,.501|r,000000,0,1.0,1.002&chof=png&chxs=0,333333,10,0,t|1,333333,10,-1,_";
	// byte[] chart = getGoogleChartBytes(chartURL);
	// return chart;
	// }

	// private byte[] getGoogleChartBytes(String queryString) {
	// GetMethod method = null;
	// byte[] btarr = null;
	// try {
	// method = new GetMethod(googleChartUrl);
	// method.setQueryString(queryString);
	// httpClient.executeMethod(method);
	// btarr = method.getResponseBody();
	// } catch (Exception e) {
	// LOG.error("Failed to getGoogleChartBytes " + e.getMessage() + "\n URL : " + googleChartUrl + "?" + queryString);
	// return null;
	// } finally {
	// if (method != null) {
	// method.releaseConnection();
	// }
	// }
	// return btarr;
	// }


	private boolean hasCountrySuffix(String url) {

		InternetDomainName fullDomainName = InternetDomainName.from(url);
		InternetDomainName publicDomainName = fullDomainName.publicSuffix();

		if (publicDomainName != null && publicDomainName.parts().size() > 1) {
			return true;
		}
		return false;
	}
}
