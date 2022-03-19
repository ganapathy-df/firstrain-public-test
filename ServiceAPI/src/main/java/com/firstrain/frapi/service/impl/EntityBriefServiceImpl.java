package com.firstrain.frapi.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import com.firstrain.frapi.FRCompletionService;
import com.firstrain.frapi.domain.BaseSpec;
import com.firstrain.frapi.domain.BlendDunsInput;
import com.firstrain.frapi.domain.EntityMap;
import com.firstrain.frapi.domain.IndustryBriefDomain;
import com.firstrain.frapi.domain.MgmtTurnoverData;
import com.firstrain.frapi.domain.Parameter;
import com.firstrain.frapi.domain.SectionSpec;
import com.firstrain.frapi.domain.TwitterSpec;
import com.firstrain.frapi.domain.VisualizationData;
import com.firstrain.frapi.domain.VisualizationData.ChartType;
import com.firstrain.frapi.obj.MgmtTurnoverServiceSpec;
import com.firstrain.frapi.pojo.EnterprisePref;
import com.firstrain.frapi.pojo.Entity;
import com.firstrain.frapi.pojo.EntityBriefInfo;
import com.firstrain.frapi.pojo.Graph;
import com.firstrain.frapi.pojo.Graph.GraphFor;
import com.firstrain.frapi.pojo.wrapper.BaseSet;
import com.firstrain.frapi.pojo.wrapper.BaseSet.SectionType;
import com.firstrain.frapi.pojo.wrapper.DocumentSet;
import com.firstrain.frapi.pojo.wrapper.EventSet;
import com.firstrain.frapi.pojo.wrapper.GraphNodeSet;
import com.firstrain.frapi.pojo.wrapper.TweetSet;
import com.firstrain.frapi.repository.CompanyServiceRepository;
import com.firstrain.frapi.repository.EntityBaseServiceRepository;
import com.firstrain.frapi.repository.impl.IndustryClassificationMap;
import com.firstrain.frapi.service.CompanyService;
import com.firstrain.frapi.service.DnbService;
import com.firstrain.frapi.service.EntityBaseService;
import com.firstrain.frapi.service.EntityBriefService;
import com.firstrain.frapi.service.EntityProcessingService;
import com.firstrain.frapi.service.IndustryBriefService;
import com.firstrain.frapi.service.RestrictContentService;
import com.firstrain.frapi.service.VisualizationService;
import com.firstrain.frapi.util.ContentTokeHandler;
import com.firstrain.frapi.util.ConvertUtil;
import com.firstrain.frapi.util.DateBucketUtils;
import com.firstrain.frapi.util.DateBucketUtils.BucketSpec;
import com.firstrain.frapi.util.DefaultEnums.DateBucketingMode;
import com.firstrain.frapi.util.DefaultEnums.INPUT_ENTITY_TYPE;
import com.firstrain.frapi.util.DefaultEnums.MatchedEntityConfidenceScore;
import com.firstrain.frapi.util.EntityHandler;
import com.firstrain.frapi.util.FRAPIConstant;
import com.firstrain.frapi.util.SearchResultGenerator;
import com.firstrain.frapi.util.ServicesAPIUtil;
import com.firstrain.frapi.util.StatusCode;
import com.firstrain.obj.IEntityInfo;
import com.firstrain.obj.IEntityInfoCache;
import com.firstrain.solr.client.DocEntriesUpdator;
import com.firstrain.solr.client.EntityEntry;
import com.firstrain.solr.client.SearchResult;
import com.firstrain.solr.client.SearchSpec;
import com.firstrain.solr.client.SearchTokenEntry;
import com.firstrain.solr.client.SolrSearcher;
import com.firstrain.utils.FR_ArrayUtils;
import com.firstrain.utils.object.PerfActivityType;
import com.firstrain.utils.object.PerfMonitor;
import com.firstrain.utils.object.PerfRequestEntry;

@Service
public class EntityBriefServiceImpl implements EntityBriefService {

	private final Logger LOG = Logger.getLogger(EntityBriefServiceImpl.class);

	@Autowired
	private VisualizationService visualizationService;
	@Autowired
	@Qualifier("entityBaseServiceRepositoryImpl")
	private EntityBaseServiceRepository entityBaseServiceRepository;
	@Autowired
	private ConvertUtil convertUtil;
	@Autowired(required = true)
	protected ThreadPoolTaskExecutor taskExecutor;
	@Autowired
	private EntityProcessingService entityProcessingService;
	@Autowired
	private EntityBaseService entityBaseService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private IndustryBriefService industryBriefService;
	@Autowired
	private ServicesAPIUtil servicesAPIUtil;
	@Autowired
	private DateBucketUtils dateBucketUtils;
	@Autowired
	private RestrictContentService restrictContentService;
	@Autowired
	private DnbService dnbService;
	@Autowired
	private IndustryClassificationMap industryClassificationMap;
	@Autowired
	@Qualifier("companyServiceRepositoryImpl")
	private CompanyServiceRepository companyServiceRepository;

	@Value("${executor.timeout}")
	protected long executorTimeout;
	@Value("${content.filter.token}")
	protected String contentFilterToken;
	@Value("${exclude.filters}")
	private String excludeFilters;

	@Override
	public EntityBriefInfo getEntityBriefDetails(EnterprisePref enterprisePref, String searchToken, String fq) throws Exception {
		return getEntityBriefDetails(enterprisePref, searchToken, fq, null, Boolean.FALSE);
	}

	@Override
	public EntityBriefInfo getEntityBriefDetails(EnterprisePref enterprisePref, String searchToken, String fqParam, String scopeDirective,
			Boolean blendDUNS) throws Exception {

		String fq = fqParam;
		long startTime = PerfMonitor.currentTime();

		try {
			IEntityInfoCache entityInfoCache = entityBaseServiceRepository.getEntityInfoCache();
			EntityBriefInfo entityBriefInfo = new EntityBriefInfo();
			IEntityInfo entityInfo = null;

			BlendDunsInput bdi = new BlendDunsInput();
			bdi.setBlendDUNS(blendDUNS);
			bdi.setScopeDirective(scopeDirective);

			if (Boolean.TRUE.equals(enterprisePref.isDnBId())) {
				// to support searchtoken for dunsid true case
				if (searchToken.startsWith(FRAPIConstant.INDUSTRY_PREFIX) || searchToken.startsWith(FRAPIConstant.TOPIC_PREFIX)
						|| searchToken.startsWith(FRAPIConstant.COMPANY_PREFIX)) {
					bdi.setBlendDUNS(Boolean.FALSE);
					entityInfo = entityInfoCache.searchTokenToEntity(searchToken.trim());
					if (entityInfo == null) {
						entityBriefInfo.setStatusCode(StatusCode.ENTITY_NOT_FOUND);
						return entityBriefInfo;
					}
					entityBriefInfo.setEntity(convertUtil.convertEntityInfo(entityInfo));
				} else { // handle duns here
                    Entity entity = EntityHandler.generateEntity(dnbService, searchToken, bdi);
					if (entity == null) {
						entityBriefInfo.setStatusCode(StatusCode.ENTITY_NOT_FOUND);
						return entityBriefInfo;
					}
					searchToken = entity.getSearchToken();
					entityInfo = entityInfoCache.searchTokenToEntity(searchToken);
					entityBriefInfo.setEntity(entity);
				}
			} else {
				bdi.setBlendDUNS(Boolean.FALSE);
				entityInfo = entityInfoCache.searchTokenToEntity(searchToken.trim());
				if (entityInfo == null) {
					entityBriefInfo.setStatusCode(StatusCode.ENTITY_NOT_FOUND);
					return entityBriefInfo;
				}
				entityBriefInfo.setEntity(convertUtil.convertEntityInfo(entityInfo));
			}

			FRCompletionService<BaseSet> completionService = new FRCompletionService<BaseSet>(taskExecutor.getThreadPoolExecutor());
			INPUT_ENTITY_TYPE inputEntityType = getInputEntityType(entityInfo);
			Map<SectionType, SectionSpec> sectionsMap = enterprisePref.getSectionsMap();

			boolean excludeSourceContent = false;
			boolean includeSourceContent = false;

			if (fq != null) {
				if (fq.contains("-" + contentFilterToken)) {
                    fq = ContentTokeHandler.processContent(fq, "-" + contentFilterToken);
					excludeSourceContent = true;
				} else if (fq.contains(contentFilterToken)) {
                    fq = ContentTokeHandler.processContent(fq, contentFilterToken);
					includeSourceContent = true;
				}
			}

			if (inputEntityType == INPUT_ENTITY_TYPE.INDUSTRY) {
				String industryCatId = entityInfo.getId();
				AtomicBoolean onlyIndustry = new AtomicBoolean(false);
				IndustryBriefDomain industryBriefDomain =
						industryBriefService.getVirtualMonitor(industryCatId, null, null, onlyIndustry, Boolean.FALSE);
				Set<String> catIdsSet = industryBriefDomain.getCategoryIdsSet();

				if (sectionsMap.containsKey(SectionType.FR)) {
					SectionSpec sectionSpec = sectionsMap.get(SectionType.FR);
					Short start = sectionSpec.getStart();
					if (start == null) {
						start = 0;
					}
					BaseSpec baseSpec = new BaseSpec();
					baseSpec.setStart(start);
					baseSpec.setNeedPagination(sectionSpec.getNeedPagination());
					baseSpec.setCount(sectionSpec.getCount());
					baseSpec.setNeedBucket(sectionSpec.getNeedBucket());
					// baseSpec.setNeedPhrase(enterprisePref.isNeedPhrase());

					String excludeArticleIdsSSV =
							restrictContentService.getAllHiddenContent(enterprisePref.getEnterpriseId(), FRAPIConstant.DOCUMENT_PREFIX);
					if (!excludeArticleIdsSSV.isEmpty()) {
						baseSpec.setExcludeArticleIdsSSV(excludeArticleIdsSSV);
					}
					baseSpec.setIndustryClassificationId(enterprisePref.getIndustryClassificationId());

					baseSpec = servicesAPIUtil.setSourceContent(excludeSourceContent, includeSourceContent, baseSpec, enterprisePref);
					baseSpec.setOnlyIndustry(onlyIndustry.get());
					String fqwr = fq;
					if (fqwr == null || fqwr.isEmpty()) {
						fqwr = excludeFilters;
					} else {
						fqwr = fqwr + " " + excludeFilters;
					}

					Callable<BaseSet> webResultsFuture = getIndustryWebResultsAsync(catIdsSet, fqwr, baseSpec);
					completionService.submit(webResultsFuture);

				}
				if (sectionsMap.containsKey(SectionType.FT)) {
					SectionSpec sectionSpec = sectionsMap.get(SectionType.FT);
					String[] catIdsAll = FR_ArrayUtils.collectionToStringArray(catIdsSet);
					String excludeArticleIdsSSV =
							restrictContentService.getAllHiddenContent(enterprisePref.getEnterpriseId(), FRAPIConstant.TWEET_PREFIX);
					Callable<BaseSet> tweetsFuture = getTweetsAsync(catIdsAll, sectionSpec, SearchSpec.SCOPE_AUTO, excludeArticleIdsSSV);
					completionService.submit(tweetsFuture);
				}
				/* Events */
				if (sectionsMap.containsKey(SectionType.E)) {
					SectionSpec sectionSpec = sectionsMap.get(SectionType.E);
					BaseSpec baseSpec = createBaseSpec(sectionSpec, enterprisePref); 
					
					populateCountAndNeedBucket(baseSpec, sectionSpec, FRAPIConstant.BASIC_WEB_RESULTS_SEARCH_DAYS);
					baseSpec.setCacheKey(industryCatId);
					Callable<BaseSet> eventsFuture = getEventSetForTopicsAndCompaniesAsync(industryBriefDomain.getCompanyIdsArray(),
							industryBriefDomain.getTopicIdsArray(), baseSpec);
					completionService.submit(eventsFuture);
				}
				/* Web Volume Graph */
				if (sectionsMap.containsKey(SectionType.WV)) {
					SectionSpec sectionSpec = sectionsMap.get(SectionType.WV);
					BaseSpec baseSpec = createBaseSpec(sectionSpec, enterprisePref); 
					
					baseSpec.setCount((short) FRAPIConstant.WV_EVENTS_COUNT);
					baseSpec.setDaysCount(FRAPIConstant.BASIC_WEB_RESULTS_SEARCH_DAYS);

					Callable<BaseSet> webVolumeGraphFuture = getWebVolumeGraphAsync(industryCatId, industryBriefDomain.getCompanyIdsArray(),
							industryBriefDomain.getTopicIdsArray(), baseSpec, GraphFor.CALL_PREP);
					completionService.submit(webVolumeGraphFuture);
				}
				/* Management Changes */
				// TODO : 1. dayscount......existing 180...or using docCount of entity?
				/*
				 * if(sectionsMap.containsKey(SectionType.TE)){ SectionSpec sectionSpec = sectionsMap.get(SectionType.TE); BaseSpec baseSpec = new
				 * BaseSpec(); Short start = sectionSpec.getStart(); if(start == null) { start = 0; } baseSpec.setStart(start);
				 * baseSpec.setCount(sectionSpec.getCount()); baseSpec.setDaysCount(FRAPIConstant.BASIC_WEB_RESULTS_SEARCH_DAYS);
				 * baseSpec.setNeedBucket(sectionSpec.getNeedBucket());
				 * 
				 * int[] companyIdArr = industryBriefDomain.getCompanyIdsArray(); int companyIdArrLength = companyIdArr.length; if ((companyIdArr ==
				 * null) || (companyIdArrLength == 0)) { LOG.info( "For industry id : " + industryCatId + ", companyId Array is null or empty."); }
				 * else { List<Integer> companyIdLst = new ArrayList<Integer>(companyIdArrLength); for (int id : companyIdArr) {
				 * companyIdLst.add(id); }
				 * 
				 * Callable<BaseSet> turnOverEventFuture = getTurnOverEventsAsync(companyIdLst, baseSpec);
				 * completionService.submit(turnOverEventFuture); } }
				 */
			} else if (inputEntityType == INPUT_ENTITY_TYPE.TOPIC || inputEntityType == INPUT_ENTITY_TYPE.COMPANY) {
				if (sectionsMap.containsKey(SectionType.FT)) {
					SectionSpec sectionSpec = sectionsMap.get(SectionType.FT);
					String[] catIdsAll = new String[1];
					catIdsAll[0] = String.valueOf(entityInfo.getId());
					String excludeArticleIdsSSV =
							restrictContentService.getAllHiddenContent(enterprisePref.getEnterpriseId(), FRAPIConstant.TWEET_PREFIX);
					Callable<BaseSet> tweetsFuture = getTweetsAsync(catIdsAll, sectionSpec, SearchSpec.SCOPE_AUTO, excludeArticleIdsSSV);
					completionService.submit(tweetsFuture);

				}

				if (sectionsMap.containsKey(SectionType.HR)) {
					SectionSpec sectionSpec = sectionsMap.get(SectionType.HR);
					Short start = sectionSpec.getStart();
					if (start == null) {
						start = 0;
					}
					BaseSpec baseSpec = new BaseSpec();
					baseSpec.setStart(start);
					baseSpec.setNeedPagination(false);
					baseSpec.setScope(SearchSpec.SCOPE_NARROW);
					populateCountAndNeedBucket(baseSpec, sectionSpec, 1);
					baseSpec.setNeedMatchedEntities(true);

					String excludeArticleIdsSSV =
							restrictContentService.getAllHiddenContent(enterprisePref.getEnterpriseId(), FRAPIConstant.DOCUMENT_PREFIX);
					if (!excludeArticleIdsSSV.isEmpty()) {
						baseSpec.setExcludeArticleIdsSSV(excludeArticleIdsSSV);
					}
					baseSpec.setIndustryClassificationId(enterprisePref.getIndustryClassificationId());

					baseSpec = servicesAPIUtil.setSourceContent(excludeSourceContent, includeSourceContent, baseSpec, enterprisePref);

					String fqwr = fq;
					if (fqwr == null || fqwr.isEmpty()) {
						fqwr = excludeFilters;
					} else {
						fqwr = fqwr + " " + excludeFilters;
					}

					Callable<BaseSet> highlightsResultsFuture = gethighlightsResultsAsync(searchToken.trim(), fqwr, baseSpec, bdi);
					completionService.submit(highlightsResultsFuture);
				}
				
				if (sectionsMap.containsKey(SectionType.FR)) {
					SectionSpec sectionSpec = sectionsMap.get(SectionType.FR);
					Short start = sectionSpec.getStart();
					if (start == null) {
						start = 0;
					}
					BaseSpec baseSpec = new BaseSpec();
					baseSpec.setStart(start);
					baseSpec.setNeedPagination(sectionSpec.getNeedPagination());
					baseSpec.setScope(entityInfo.getScope());
					baseSpec.setCount(sectionSpec.getCount());
					baseSpec.setDaysCount(getDaysCount(entityInfo));
					baseSpec.setNeedBucket(sectionSpec.getNeedBucket());
					baseSpec.setNeedMatchedEntities(true);
					// baseSpec.setNeedPhrase(enterprisePref.isNeedPhrase());

					String excludeArticleIdsSSV =
							restrictContentService.getAllHiddenContent(enterprisePref.getEnterpriseId(), FRAPIConstant.DOCUMENT_PREFIX);
					if (!excludeArticleIdsSSV.isEmpty()) {
						baseSpec.setExcludeArticleIdsSSV(excludeArticleIdsSSV);
					}
					baseSpec.setIndustryClassificationId(enterprisePref.getIndustryClassificationId());

					baseSpec = servicesAPIUtil.setSourceContent(excludeSourceContent, includeSourceContent, baseSpec, enterprisePref);

					String fqwr = fq;
					if (fqwr == null || fqwr.isEmpty()) {
						fqwr = excludeFilters;
					} else {
						fqwr = fqwr + " " + excludeFilters;
					}

					Callable<BaseSet> webResultsFuture = getWebResultsAsync(searchToken.trim(), fqwr, baseSpec, bdi);
					completionService.submit(webResultsFuture);
				}

				/* Analyst Comments */
				if (sectionsMap.containsKey(SectionType.AC) && inputEntityType == INPUT_ENTITY_TYPE.COMPANY) {
					SectionSpec sectionSpec = sectionsMap.get(SectionType.AC);
					Short start = sectionSpec.getStart();
					if (start == null) {
						start = 0;
					}

					BaseSpec baseSpec60Days = new BaseSpec();
					baseSpec60Days.setStart(start);
					baseSpec60Days.setCount(sectionSpec.getCount());
					baseSpec60Days.setDaysCount(FRAPIConstant.WEB_RESULTS_SEARCH_DAYS_BIMONTHLY);
					baseSpec60Days.setNeedMatchedEntities(false);
					// baseSpec60Days.setNeedPhrase(enterprisePref.isNeedPhrase());

					String excludeArticleIdsSSV =
							restrictContentService.getAllHiddenContent(enterprisePref.getEnterpriseId(), FRAPIConstant.DOCUMENT_PREFIX);
					if (!excludeArticleIdsSSV.isEmpty()) {
						baseSpec60Days.setExcludeArticleIdsSSV(excludeArticleIdsSSV);
					}

					/* create another BaseSpec object with 180 days count */
					BaseSpec baseSpec180Days = new BaseSpec();
					baseSpec180Days.setStart(baseSpec60Days.getStart());
					baseSpec180Days.setCount(baseSpec60Days.getCount());
					baseSpec180Days.setDaysCount(FRAPIConstant.BASIC_WEB_RESULTS_SEARCH_DAYS);
					baseSpec180Days.setNeedMatchedEntities(baseSpec60Days.getNeedMatchedEntities());
					baseSpec180Days.setExcludeArticleIdsSSV(baseSpec60Days.getExcludeArticleIdsSSV());
					// baseSpec180Days.setNeedPhrase(enterprisePref.isNeedPhrase());

					Callable<BaseSet> analystCommentsFuture = getAnalystCommentsAsync(searchToken.trim(), baseSpec60Days, baseSpec180Days);
					completionService.submit(analystCommentsFuture);
				}
				/* Management Changes */
				// TODO : 1. dayscount......existing 180...or using docCount of entity?
				if (sectionsMap.containsKey(SectionType.TE) && inputEntityType == INPUT_ENTITY_TYPE.COMPANY) {
					SectionSpec sectionSpec = sectionsMap.get(SectionType.TE);
					BaseSpec baseSpec = createAndPopulateBaseSpec(sectionSpec);
					List<Integer> companyIds = new ArrayList<Integer>(1);
					companyIds.add(entityInfo.getCompanyId());
					Callable<BaseSet> turnOverEventFuture = getTurnOverEventsAsync(companyIds, baseSpec);
					completionService.submit(turnOverEventFuture);
				}

				/* Web Volume Graph */
				if (sectionsMap.containsKey(SectionType.WV)) {
					SectionSpec sectionSpec = sectionsMap.get(SectionType.WV);
					BaseSpec baseSpec = new BaseSpec();
					Short start = getStart(sectionSpec);
					baseSpec.setStart(start);
					baseSpec.setCount((short) FRAPIConstant.WV_EVENTS_COUNT);
					baseSpec.setDaysCount(FRAPIConstant.BASIC_WEB_RESULTS_SEARCH_DAYS);
					baseSpec.setIndustryClassificationId(enterprisePref.getIndustryClassificationId());
					baseSpec.setNeedRelatedDoc(sectionSpec.isNeedRelatedDoc());

					String catId = entityInfo.getId();

					Callable<BaseSet> webVolumeGraphFuture = getWebVolumeGraphAsync(catId, null, null, baseSpec, GraphFor.CALL_PREP);
					completionService.submit(webVolumeGraphFuture);
				}

				/* Events */
				if (sectionsMap.containsKey(SectionType.E)) {
					SectionSpec sectionSpec = sectionsMap.get(SectionType.E);
					BaseSpec baseSpec = createAndPopulateBaseSpec(sectionSpec);
					baseSpec.setIndustryClassificationId(enterprisePref.getIndustryClassificationId());
					baseSpec.setNeedRelatedDoc(sectionSpec.isNeedRelatedDoc());
					// baseSpec.setNeedPhrase(enterprisePref.isNeedPhrase());
					String catId = entityInfo.getId();
					baseSpec.setCacheKey(catId);
					Callable<BaseSet> eventsFuture = null;
					if (inputEntityType == INPUT_ENTITY_TYPE.COMPANY) {
						eventsFuture = getEventsAsync(baseSpec);
					} else {
						eventsFuture = getEventSetForTopicsAndCompaniesAsync(null, new int[] {Integer.parseInt(catId)}, baseSpec);
					}
					completionService.submit(eventsFuture);
				}

				/* Management Turnover Chart */
				if (sectionsMap.containsKey(SectionType.MTC) && inputEntityType == INPUT_ENTITY_TYPE.COMPANY) {

					BaseSpec baseSpec = new BaseSpec();
					String catId = entityInfo.getId();
					baseSpec.setCacheKey(catId);
					Callable<BaseSet> mgmtTurnOverDataFuture = getMgmtTurnoverAsync(baseSpec);
					completionService.submit(mgmtTurnOverDataFuture);
				}
			}

			// handling visualization
			Set<SectionType> sectionsSet = sectionsMap.keySet();
			if (sectionsSet.contains(SectionType.VIZ)) {
				List<ChartType> chTypeList = new ArrayList<ChartType>();
				for (SectionType st : sectionsSet) {
					if (st.equals(SectionType.TT)) {
						chTypeList.add(ChartType.TREE_MONITOR_SEARCH);
					}
					if (st.equals(SectionType.BI)) {
						chTypeList.add(ChartType.TREE_COMPANY);
					}
					if (st.equals(SectionType.MD)) {
						chTypeList.add(ChartType.TREE_TOPICS);
					}
					if (st.equals(SectionType.TWT)) {
						chTypeList.add(ChartType.ACC_METER);
					}
					if (st.equals(SectionType.GL)) {
						chTypeList.add(ChartType.GEO_WORLD);
					}
					if (st.equals(SectionType.RL)) {
						chTypeList.add(ChartType.GEO_US);
					}
				}
				if (!chTypeList.isEmpty()) {
					Callable<BaseSet> visualizationFuture =
							getVisualizationAsync(searchToken.trim(), 12, chTypeList, fq, enterprisePref.isApplyMinNodeCheckInVisualization());
					completionService.submit(visualizationFuture);
				}
			}

			// collect results
			collectResults(completionService, entityBriefInfo);
			entityBriefInfo.setScopeDirective(bdi.getScopeDirective());
			entityBriefInfo.setStatusCode(StatusCode.REQUEST_SUCCESS);
			return entityBriefInfo;
		} catch (Exception e) {
			LOG.error("Error while generating entity brief for entity:" + searchToken, e);
			throw e;
		} finally {
			PerfMonitor.recordStats(startTime, PerfActivityType.ReqTime, "getEntityBriefDetails");
		}
	}

	private BaseSpec createAndPopulateBaseSpec(final SectionSpec sectionSpec) {
		BaseSpec baseSpec = new BaseSpec();
		Short start = getStart(sectionSpec);
		baseSpec.setStart(start);
		populateCountAndNeedBucket(baseSpec, sectionSpec, FRAPIConstant.BASIC_WEB_RESULTS_SEARCH_DAYS);
		return baseSpec;
	}

	private void populateCountAndNeedBucket(final BaseSpec baseSpec, final SectionSpec sectionSpec, final int daysCount) {
		baseSpec.setCount(sectionSpec.getCount());
		baseSpec.setDaysCount(daysCount);
		baseSpec.setNeedBucket(sectionSpec.getNeedBucket());
	}
 
	private BaseSpec createBaseSpec(final SectionSpec sectionSpec, final EnterprisePref enterprisePref) { 
		BaseSpec baseSpec = new BaseSpec(); 
		Short start = getStart(sectionSpec);
		baseSpec.setIndustryClassificationId(enterprisePref.getIndustryClassificationId()); 
		baseSpec.setNeedRelatedDoc(sectionSpec.isNeedRelatedDoc()); 
		// baseSpec.setNeedPhrase(enterprisePref.isNeedPhrase()); 
		baseSpec.setStart(start); 
		return baseSpec; 
	} 

	private Short getStart(final SectionSpec sectionSpec) {
		Short start = sectionSpec.getStart(); 
		if (start == null) { 
			start = 0; 
		}
		return start;
	}
	

	@Override
	public EntityBriefInfo getEntityBriefDetailsForMT(EnterprisePref enterprisePref, String searchToken, String fqParam, String from, String to,
			String count) throws Exception {

		String fq = fqParam;
		long startTime = PerfMonitor.currentTime();

		try {
			IEntityInfoCache entityInfoCache = entityBaseServiceRepository.getEntityInfoCache();
			EntityBriefInfo entityBriefInfo = new EntityBriefInfo();
			IEntityInfo entityInfo = null;

			entityInfo = entityInfoCache.searchTokenToEntity(searchToken.trim());
			if (entityInfo == null) {
				entityBriefInfo.setStatusCode(StatusCode.ENTITY_NOT_FOUND);
				return entityBriefInfo;
			}
			entityBriefInfo.setEntity(convertUtil.convertEntityInfo(entityInfo));

			FRCompletionService<BaseSet> completionService = new FRCompletionService<BaseSet>(taskExecutor.getThreadPoolExecutor());
			INPUT_ENTITY_TYPE inputEntityType = getInputEntityType(entityInfo);
			Map<SectionType, SectionSpec> sectionsMap = enterprisePref.getSectionsMap();

			if (fq != null) {
				if (fq.contains("-" + contentFilterToken)) {
                    fq = ContentTokeHandler.processContent(fq, "-" + contentFilterToken);
				} else if (fq.contains(contentFilterToken)) {
                    fq = ContentTokeHandler.processContent(fq, contentFilterToken);
				}
			}

			if (inputEntityType == INPUT_ENTITY_TYPE.INDUSTRY) {
				String industryCatId = entityInfo.getId();
				AtomicBoolean onlyIndustry = new AtomicBoolean(false);
				IndustryBriefDomain industryBriefDomain =
						industryBriefService.getVirtualMonitor(industryCatId, null, null, onlyIndustry, Boolean.TRUE);

				/* Management Changes */
				// TODO : 1. dayscount......existing 180...or using docCount of entity?
				if (sectionsMap.containsKey(SectionType.TE)) {
					BaseSpec baseSpec = new BaseSpec();
					baseSpec.setDaysCount(FRAPIConstant.BASIC_WEB_RESULTS_SEARCH_DAYS);
					baseSpec.setCustomized(Boolean.TRUE);
					baseSpec.setCount(Short.valueOf(count));
					baseSpec.setStartDate(from);
					baseSpec.setEndDate(to);

					int[] companyIdArr = industryBriefDomain.getCompanyIdsArray();
					if ((companyIdArr == null) || (companyIdArr.length == 0)) {
						LOG.info("For industry id : " + industryCatId + ", companyId Array is null or empty.");
					} else {
						List<Integer> companyIdLst = new ArrayList<Integer>(companyIdArr.length);
						for (int id : companyIdArr) {
							companyIdLst.add(id);
						}

						Callable<BaseSet> turnOverEventFuture = getTurnOverEventsAsync(companyIdLst, baseSpec);
						completionService.submit(turnOverEventFuture);
					}
				}
			}

			// collect results
			collectResults(completionService, entityBriefInfo);

			entityBriefInfo.setStatusCode(StatusCode.REQUEST_SUCCESS);
			return entityBriefInfo;
		} catch (Exception e) {
			LOG.error("Error while generating entity brief for entity:" + searchToken, e);
			throw e;
		} finally {
			PerfMonitor.recordStats(startTime, PerfActivityType.ReqTime, "getEntityBriefDetails");
		}
	}

	@Override
	public EntityBriefInfo getEntityMap(String searchToken) throws Exception {
		long startTime = PerfMonitor.currentTime();
		EntityBriefInfo entityBriefInfo = new EntityBriefInfo();
		try {
			IEntityInfoCache entityInfoCache = entityBaseServiceRepository.getEntityInfoCache();
			IEntityInfo entityInfo = entityInfoCache.searchTokenToEntity(searchToken.trim());
			if (entityInfo == null) {
				entityBriefInfo.setStatusCode(StatusCode.ENTITY_NOT_FOUND);
				return entityBriefInfo;
			}

			EntityMap entityMap = new EntityMap();
			IEntityInfo entityInfo1 = null;
			com.firstrain.frapi.domain.Entity entityObj = new com.firstrain.frapi.domain.Entity();
			entityObj.setId(entityInfo.getSearchToken());
			entityObj.setName(entityInfo.getName());
			SolrDocument solrDoc = setCompanyInfo(entityObj);
			entityMap.setEntity(entityObj);
			if (entityInfo.getBizLineCatIds() != null && !entityInfo.getBizLineCatIds().isEmpty() && entityInfo.getBizLineCatIds().size() > 0) {
				List<com.firstrain.frapi.domain.Entity> entitiesList = new ArrayList<com.firstrain.frapi.domain.Entity>();
				for (int bzlineCatId : entityInfo.getBizLineCatIds()) {
					entityInfo1 = entityInfoCache.catIdToEntity(String.valueOf(bzlineCatId));
					if (entityInfo1 != null) {
						com.firstrain.frapi.domain.Entity entity = createEntity(entityInfo1);
						entitiesList.add(entity);
					}
					entityMap.setBizlines(entitiesList);
				}
			}
			if (entityInfo.getIndustryCatId() > 0) {
				entityInfo1 = entityInfoCache.catIdToEntity(String.valueOf(entityInfo.getIndustryCatId()));
				if (entityInfo1 != null) {
					com.firstrain.frapi.domain.Entity entity = createEntity(entityInfo1);
					entityMap.setIndustry(entity);
				}
			}
			if (entityInfo.getSectorCatId() > 0) {
				entityInfo1 = entityInfoCache.catIdToEntity(String.valueOf(entityInfo.getSectorCatId()));
				if (entityInfo1 != null) {
					com.firstrain.frapi.domain.Entity entity = createEntity(entityInfo1);
					entityMap.setSector(entity);
				}
			}
			if (entityInfo.getSegmentCatId() > 0) {
				entityInfo1 = entityInfoCache.catIdToEntity(String.valueOf(entityInfo.getSegmentCatId()));
				if (entityInfo1 != null) {
					com.firstrain.frapi.domain.Entity entity = createEntity(entityInfo1);
					entityMap.setSegment(entity);
				}
			}

			// Fetch image service url from DocEntriesUpdator.
			String imageServiceURL = DocEntriesUpdator.getFirstImageServiceUrl();
			if (imageServiceURL == null) {
				LOG.warn("No {image.service.urls.csv} property defined, skipped company logo image url association.");
			}

			if (solrDoc != null) {
				String logo = (String) solrDoc.getFieldValue("logoImageUrl");
				if (logo != null) {
					entityMap.setCompanyLogo(DocEntriesUpdator.getResolvedUrl(imageServiceURL, logo));
				}
			}

			entityBriefInfo.setEntityMap(entityMap);
			entityBriefInfo.setStatusCode(StatusCode.REQUEST_SUCCESS);

		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} finally {
			PerfMonitor.recordStats(startTime, PerfActivityType.ReqTime, "getCompanyDetails");
		}
		return entityBriefInfo;
	}

	@Override
	public EntityBriefInfo getEntityMatch(String q, int count, INPUT_ENTITY_TYPE type, String diemnsionCSV) throws Exception {
		long startTime = PerfMonitor.currentTime();
		EntityBriefInfo entityBriefInfo = new EntityBriefInfo();
		List<Entity> entityList = new ArrayList<Entity>();
		try {
			// entity match service on the basis of company name
			if (q != null) {
				Map<Integer, Integer> industryClassfMap = industryClassificationMap.getIndustryClassificationMap();
				entityList = entityBaseService.autoSuggestForEntity(q.trim(), type.name(), count, diemnsionCSV, industryClassfMap);
				if (entityList != null && !entityList.isEmpty()) {
					if (type.name().equalsIgnoreCase(INPUT_ENTITY_TYPE.COMPANY.name())) {
						for (Entity entity : entityList) {
							// exact company match case
							if (q.equalsIgnoreCase(entity.getName())) {
								entity.setBand((int) MatchedEntityConfidenceScore.MEDIUM.getValue());
							} else if (q.equalsIgnoreCase(entity.getSynonym())) {
								entity.setBand((int) MatchedEntityConfidenceScore.MEDIUM.getValue());
							} else { // partial company match case
								entity.setBand((int) MatchedEntityConfidenceScore.LOW.getValue());
							}
						}
					}
				}
				if (type.name().equalsIgnoreCase(INPUT_ENTITY_TYPE.INDUSTRY.name())) {
					populateContextMatchedEntitites(q.trim(), entityList, count, industryClassfMap);
				}
			}
			if (entityList != null && !entityList.isEmpty()) {
				entityBriefInfo.setMatchedEntity(entityList);
				entityBriefInfo.setStatusCode(StatusCode.REQUEST_SUCCESS);
			} else {
				entityBriefInfo.setStatusCode(StatusCode.ENTITY_NOT_FOUND);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} finally {
			PerfMonitor.recordStats(startTime, PerfActivityType.ReqTime, "getEntityMatch");
		}
		return entityBriefInfo;
	}

	private void populateContextMatchedEntitites(String q, List<Entity> entityList, int count, Map<Integer, Integer> industryClassfMap) {
		Map<String, Entity> tokenEntityMap = new HashMap<String, Entity>();
		for (Entity entity : entityList) {
			entity.setMatchedType("Text Match");
			tokenEntityMap.put(entity.getSearchToken(), entity);
		}
		List<EntityEntry> rawList = entityBaseServiceRepository.getContextMatchEntities(q, industryClassfMap);

		for (EntityEntry entry : rawList) {
			if (!tokenEntityMap.containsKey(entry.getSearchToken())) {
				Entity ent = new Entity();
				ent.setSearchToken(entry.getSearchToken());
				ent.setName(entry.getName());
				ent.setMatchedType("Context Match");
				entityList.add(ent);
			}
		}
	}

	private SolrDocument setCompanyInfo(com.firstrain.frapi.domain.Entity entityObj) throws Exception {
		SolrDocument solrDoc = companyService.getCompanyDocuments(entityObj.getId());
		entityObj.setCountry((String) solrDoc.getFieldValue("attrCountry"));
		entityObj.setAddress((String) solrDoc.getFieldValue("attrAddress"));
		entityObj.setCity((String) solrDoc.getFieldValue("attrCity"));
		entityObj.setWebsite((String) solrDoc.getFieldValue("attrWebsite"));
		entityObj.setZip((String) solrDoc.getFieldValue("attrZip"));
		if (solrDoc.getFieldValue("attrStateName") != null && !solrDoc.getFieldValue("attrStateName").toString().isEmpty()) {
			entityObj.setState((String) solrDoc.getFieldValue("attrStateName"));
		} else if (solrDoc.getFieldValue("attrStateNameAbr") != null && !solrDoc.getFieldValue("attrStateNameAbr").toString().isEmpty()) {
			entityObj.setState((String) solrDoc.getFieldValue("attrStateNameAbr"));
		}
		return solrDoc;
	}

	private Callable<BaseSet> getVisualizationAsync(final String searchToken, final int nodeCount, final List<ChartType> chartTypes,
			final String filters, final boolean isApplyMinNodeCheck) {
		return new Callable<BaseSet>() {
			@Override
			public VisualizationData call() throws Exception {
				PerfMonitor.startRequest(searchToken + "", "Visualization");
				long start = PerfMonitor.currentTime();
				VisualizationData vd = null;
				try {
					vd = visualizationService.getVisualizationByEntityToken(searchToken, nodeCount, chartTypes, filters, false, isApplyMinNodeCheck);
					vd.setSectionType(SectionType.VIZ);
				} catch (Exception e) {
					LOG.error("Error Getting Visualization for entity " + searchToken, e);
				} finally {
					if (vd == null) {
						vd = new VisualizationData();
					}
					PerfMonitor.recordStats(start, PerfActivityType.ReqTime);
					PerfRequestEntry stat = PerfMonitor.endRequest();
					vd.setStat(stat);
				}
				return vd;
			}
		};
	}

	private Callable<BaseSet> getTweetsAsync(final String[] catIdsAll, final SectionSpec sectionSpec, final int scope,
			final String excludeArticleIdsSSV) {
		return new Callable<BaseSet>() {
			@Override
			public TweetSet call() throws Exception {
				TweetSet tweet = getTweets(catIdsAll, sectionSpec, scope, excludeArticleIdsSSV);
				tweet.setSectionType(SectionType.FT);
				return tweet;
			}
		};
	}

	private Callable<BaseSet> getWebResultsAsync(final String q, final String fq, final BaseSpec baseSpec, final BlendDunsInput bdi) {
		return new Callable<BaseSet>() {

			@Override
			public DocumentSet call() throws Exception {
				DocumentSet docSet = getWebResults(q, fq, baseSpec, bdi);
				docSet.setSectionType(SectionType.FR);
				return docSet;
			}
		};
	}

	private Callable<BaseSet> gethighlightsResultsAsync(final String q, final String fq, final BaseSpec baseSpec, final BlendDunsInput bdi) {
		return new Callable<BaseSet>() {

			@Override
			public DocumentSet call() throws Exception {
				DocumentSet docSet = gethighlightsResults(q, fq, baseSpec, bdi);
				docSet.setSectionType(SectionType.HR);
				return docSet;
			}
		};
	}
	
	
	private Callable<BaseSet> getAnalystCommentsAsync(final String q, final BaseSpec baseSpec60Days, final BaseSpec baseSpec180Days) {
		return new Callable<BaseSet>() {

			@Override
			public DocumentSet call() throws Exception {
				DocumentSet docSet = getAnalystComments(q, baseSpec60Days, baseSpec180Days);
				docSet.setSectionType(SectionType.AC);
				return docSet;
			}
		};
	}

	private Callable<SearchResult> getAnalystCommentsSearchResultAsync(final String q, final BaseSpec baseSpec) {
		return new Callable<SearchResult>() {
			@Override
			public SearchResult call() throws Exception {
				SearchResult searchResults = entityBaseService.getSearchResultForAnalystComments(q, baseSpec);
				return searchResults;
			}
		};
	}

	private Callable<BaseSet> getTurnOverEventsAsync(final List<Integer> companyIds, final BaseSpec baseSpec) {
		return new Callable<BaseSet>() {

			@Override
			public EventSet call() throws Exception {
				EventSet eventSet = getTurnOverEvents(companyIds, baseSpec);
				eventSet.setSectionType(SectionType.TE);
				return eventSet;
			}
		};
	}

	private Callable<BaseSet> getWebVolumeGraphAsync(final String catId, final int[] companyIdsArr, final int[] tCatIds,
			final BaseSpec baseSpec, final GraphFor graphFor) {
		return new Callable<BaseSet>() {
			@Override
			public Graph call() throws Exception {

				Graph graph = getWebVolumeGraph(catId, companyIdsArr, tCatIds, baseSpec, graphFor);
				graph.setSectionType(SectionType.WV);
				return graph;
			}
		};
	}

	private Callable<BaseSet> getEventsAsync(final BaseSpec baseSpec) {
		return new Callable<BaseSet>() {
			@Override
			public EventSet call() throws Exception {

				EventSet eventsTimeline = getEvents(baseSpec);
				eventsTimeline.setSectionType(SectionType.E);
				return eventsTimeline;
			}
		};
	}

	private Callable<BaseSet> getEventSetForTopicsAndCompaniesAsync(final int[] companyIdsArr, final int[] tCatIds, final BaseSpec baseSpec) {
		return new Callable<BaseSet>() {
			@Override
			public EventSet call() throws Exception {

				EventSet eventsTimeline = getEventSetForTopicsAndCompanies(companyIdsArr, tCatIds, baseSpec);
				eventsTimeline.setSectionType(SectionType.E);
				return eventsTimeline;
			}
		};
	}

	private Callable<BaseSet> getMgmtTurnoverAsync(final BaseSpec baseSpec) {

		return new Callable<BaseSet>() {
			@Override
			public MgmtTurnoverData call() throws Exception {
				MgmtTurnoverData mgmtTurnoverData = getMgmtTurnover(baseSpec);
				mgmtTurnoverData.setSectionType(SectionType.MTC);
				return mgmtTurnoverData;
			}
		};
	}

	private Callable<BaseSet> getIndustryWebResultsAsync(final Set<String> catIdsSet, final String fq, final BaseSpec baseSpec) {
		return new Callable<BaseSet>() {
			@Override
			public DocumentSet call() throws Exception {
				DocumentSet documentSet = getIndustryWebResults(catIdsSet, fq, baseSpec);
				documentSet.setSectionType(SectionType.FR);
				return documentSet;
			}
		};
	}

	private DocumentSet getIndustryWebResults(Set<String> catIdsSet, final String fq, BaseSpec baseSpec) {
		PerfMonitor.startRequest("", "Web Results");
		long start = PerfMonitor.currentTime();
		DocumentSet webResults = null;
		try {
			webResults = industryBriefService.getWebResults(catIdsSet, fq, baseSpec);

			if (webResults != null) {
				entityProcessingService.getDocumentSetWithId(webResults);
			}
		} catch (Exception e) {
			LOG.error("Exception getting Web Results for catIdsSet:" + catIdsSet.toString(), e);
		} finally {
			if (webResults == null) {
				webResults = new DocumentSet();
			}
			PerfMonitor.recordStats(start, PerfActivityType.ReqTime);
			PerfRequestEntry stat = PerfMonitor.endRequest();
			webResults.setStat(stat);
		}
		return webResults;
	}

	private DocumentSet gethighlightsResults(String q, String fq, BaseSpec spec, BlendDunsInput bdi) {
		DocumentSet webResults = null;
		PerfMonitor.startRequest(q, "highlights Results");
		long start = PerfMonitor.currentTime();
		try {
			
			webResults = entityBaseService.gethighlightsResults(new String[] {q}, new int[] {SearchSpec.SCOPE_NARROW}, spec.getCount());
			processDocument(webResults, spec); 
			
		} catch (Exception e) {
			LOG.error("Exception getting highlights Results for " + q, e);
		} finally {
			if (webResults == null) {
				webResults = new DocumentSet();
			}
			PerfMonitor.recordStats(start, PerfActivityType.ReqTime);
			PerfRequestEntry stat = PerfMonitor.endRequest();
			webResults.setStat(stat);
		}
		return webResults;
	}
	
	private DocumentSet getWebResults(String q, String fq, BaseSpec spec, BlendDunsInput bdi) {
		DocumentSet webResults = null;
		PerfMonitor.startRequest(q, "Web Results");
		long start = PerfMonitor.currentTime();
		try {
			webResults = entityBaseService.getWebResultsForSearch(q, fq, spec, bdi);
			processDocument(webResults, spec); 
			
		} catch (Exception e) {
			LOG.error("Exception getting Web Results for " + q, e);
		} finally {
			if (webResults == null) {
				webResults = new DocumentSet();
			}
			PerfMonitor.recordStats(start, PerfActivityType.ReqTime);
			PerfRequestEntry stat = PerfMonitor.endRequest();
			webResults.setStat(stat);
		}
		return webResults;
	}
 
	private void processDocument(final DocumentSet webResults, final BaseSpec spec) throws Exception { 
		if (webResults != null) { 
			entityProcessingService.getDocumentSetWithId(webResults); 
		 
			if (spec.getNeedBucket() != null && spec.getNeedBucket() == Boolean.TRUE) { 
				BucketSpec bSpec = servicesAPIUtil.getDateBucketingSpec(DateBucketingMode.AUTO, "getDate"); 
				webResults.setDocumentBucket(dateBucketUtils.getListGroupByDate(webResults.getDocuments(), bSpec)); 
				// webResults.setDocuments(null); 
			} 
		} 
	} 
	

	private DocumentSet getAnalystComments(String q, BaseSpec baseSpec60Days, BaseSpec baseSpec180Days) {
		PerfMonitor.startRequest(q, "Analyst Comments");
		long start = PerfMonitor.currentTime();
		DocumentSet analystCommentsSet = null;
		try {

			Future<SearchResult> analystCommentsFuture60Days = taskExecutor.submit(getAnalystCommentsSearchResultAsync(q, baseSpec60Days));
			Future<SearchResult> analystCommentsFuture180Days = taskExecutor.submit(getAnalystCommentsSearchResultAsync(q, baseSpec180Days));

            SearchResult sr = SearchResultGenerator.collectSearchResults(analystCommentsFuture60Days,
                    analystCommentsFuture180Days, baseSpec60Days);

			// Call for populating matched entities with documents.
			if (Boolean.TRUE.equals(baseSpec60Days.getNeedMatchedEntities()) && sr.buckets.get(0).docs != null) {
				SolrSearcher.setupMatchesForEntries(sr.buckets.get(0).docs, -1);
			}

			analystCommentsSet = entityBaseService.makeDocSetFromEntries(sr, false, baseSpec60Days.getCount(), null, null, false, 0, false,
					baseSpec60Days.isNeedPhrase());
			if (analystCommentsSet != null) {
				entityProcessingService.getDocumentSetWithId(analystCommentsSet);
			}
		} catch (Exception e) {
			LOG.error("Exception getting Analyst Comments for " + q, e);
		} finally {
			if (analystCommentsSet == null) {
				analystCommentsSet = new DocumentSet();
			}
			PerfMonitor.recordStats(start, PerfActivityType.ReqTime);
			PerfRequestEntry stat = PerfMonitor.endRequest();
			analystCommentsSet.setStat(stat);
		}
		return analystCommentsSet;
	}

	private EventSet getTurnOverEvents(List<Integer> companyIds, BaseSpec baseSpec) {
		PerfMonitor.startRequest("", "TurnOver Events");
		long start = PerfMonitor.currentTime();
		EventSet turnOverEvents = null;
		try {
			turnOverEvents = entityBaseService.getEventSetForMTEvents(companyIds, baseSpec);
			populateEventData(turnOverEvents, baseSpec, "getReportDate");
		} catch (Exception e) {
			LOG.error("Exception getting TurnOverEvents for companyIds " + companyIds, e);
		} finally {
			turnOverEvents = createEventSetAndSetStat(turnOverEvents, start);
		}
		return turnOverEvents;
	}

	private Graph getWebVolumeGraph(String catId, final int[] companyIdsArr, final int[] tCatIds, BaseSpec baseSpec, GraphFor graphFor) {
		PerfMonitor.startRequest("", "Web Volume Data");
		long start = PerfMonitor.currentTime();
		Graph graph = null;
		try {
			graph = entityBaseService.getWebVolumeGraph(catId, companyIdsArr, tCatIds, baseSpec, graphFor, Parameter.DEFAULT_N_DAY_FROM_TODAY,
					SearchSpec.SCOPE_BROAD, null, true);
		} catch (Exception e) {
			LOG.error("Error getting Graph Data for cat id " + catId, e);
		} finally {
			if (graph == null) {
				graph = new Graph();
			}
			PerfMonitor.recordStats(start, PerfActivityType.ReqTime);
			PerfRequestEntry stat = PerfMonitor.endRequest();
			graph.setStat(stat);
		}
		return graph;
	}

	private EventSet getEvents(BaseSpec baseSpec) {
		PerfMonitor.startRequest("", "Events");
		long start = PerfMonitor.currentTime();
		EventSet eventsTimeline = null;

		try {
			// eventsTimeline = entityBaseService.getEventsTimeline(companyIdsArr, null, baseSpec);
			Map<Integer, SolrDocument> eventDocMap = new HashMap<Integer, SolrDocument>();
			eventsTimeline = companyService.getCompanyEvents(baseSpec, eventDocMap);
			populateEventData(eventsTimeline, baseSpec); 
			
		} catch (Exception e) {
			LOG.error("Error getting Events for cat id " + baseSpec.getCacheKey(), e);
		} finally {
			eventsTimeline = createEventSetAndSetStat(eventsTimeline, start);
		}
		return eventsTimeline;
	}

	private EventSet getEventSetForTopicsAndCompanies(int[] companyIdsArr, int[] tCatIds, BaseSpec baseSpec) {
		PerfMonitor.startRequest("", "Events");
		long start = PerfMonitor.currentTime();
		EventSet eventsTimeline = null;

		try {
			eventsTimeline = entityBaseService.getEventsTimeline(companyIdsArr, tCatIds, baseSpec);
			populateEventData(eventsTimeline, baseSpec); 
			
		} catch (Exception e) {
			LOG.error("Error getting Events for cat id " + baseSpec.getCacheKey(), e);
		} finally {
			eventsTimeline = createEventSetAndSetStat(eventsTimeline, start);
		}
		return eventsTimeline;
	}

	private EventSet createEventSetAndSetStat(EventSet eventsTimelineParam, final long start) {
		EventSet eventsTimeline = eventsTimelineParam;
		if (eventsTimeline == null) {
			eventsTimeline = new EventSet();
		}
		PerfMonitor.recordStats(start, PerfActivityType.ReqTime);
		PerfRequestEntry stat = PerfMonitor.endRequest();
		eventsTimeline.setStat(stat);
		return eventsTimeline;
	}
 
	private void populateEventData(final EventSet eventsTimeline, final BaseSpec baseSpec) throws Exception { 
		populateEventData(eventsTimeline, baseSpec, "getDate");
	} 

	private void populateEventData(final EventSet turnOverEvents, final BaseSpec baseSpec, final String methodName) throws Exception {
		if (turnOverEvents != null) {
			entityProcessingService.getEventSetWithId(turnOverEvents);
		
			if (baseSpec.getNeedBucket() != null && baseSpec.getNeedBucket() == Boolean.TRUE) {
				BucketSpec bSpec = servicesAPIUtil.getDateBucketingSpec(DateBucketingMode.AUTO, methodName);
				entityProcessingService.getEventSetWithDateBucketing(turnOverEvents, bSpec);
			}
		}
	}
	

	private MgmtTurnoverData getMgmtTurnover(BaseSpec baseSpec) {
		PerfMonitor.startRequest("", "Mgmt TurnOver Data");
		long start = PerfMonitor.currentTime();
		MgmtTurnoverData mgmtTurnOverData = null;

		try {
			MgmtTurnoverServiceSpec mtmtSpec = companyService.getDefaultMonthlySpec();
			mgmtTurnOverData = companyService.getMgmtTurnoverData(mtmtSpec, baseSpec);
		} catch (Exception e) {
			LOG.error("Error Getting MgmtTurnOverdata for cat id " + baseSpec.getCacheKey(), e);
		} finally {
			if (mgmtTurnOverData == null) {
				mgmtTurnOverData = new MgmtTurnoverData();
			}
			PerfMonitor.recordStats(start, PerfActivityType.ReqTime);
			PerfRequestEntry stat = PerfMonitor.endRequest();
			mgmtTurnOverData.setStat(stat);
		}
		return mgmtTurnOverData;
	}

	private TweetSet getTweets(String[] catIdsAll, SectionSpec sectionSpec, int scope, String excludeArticleIdsSSV) {
		PerfMonitor.startRequest("", "Tweets");
		long start = PerfMonitor.currentTime();
		TweetSet tweetSet = null;
		try {
			TwitterSpec spec = new TwitterSpec();
			if (sectionSpec.getCount() == null) {
				sectionSpec.setCount(SectionSpec.DEFAULT_COUNT);
			}
			spec.setRows(sectionSpec.getCount());
			spec.setCatIds(catIdsAll);
			spec.setNeedBucket(sectionSpec.getNeedBucket());
			spec.setStart(sectionSpec.getStart());
			spec.setScope(scope);
			if (!excludeArticleIdsSSV.isEmpty()) {
				spec.setExcludeArticleIdsSSV(excludeArticleIdsSSV);
			}
			tweetSet = entityBaseService.getTweetList(catIdsAll, spec);
			if (tweetSet != null) {
				entityProcessingService.getTweetsWithId(tweetSet);
			}
			if (Boolean.TRUE.equals(sectionSpec.getNeedTweetAccelerometer())) {
				boolean isIpad = false;
				boolean isSingleEntity = true;
				if (catIdsAll != null && catIdsAll.length > 1) {
					isSingleEntity = false;
				}
				String catIdsCSV = FR_ArrayUtils.csvFromStringArray(catIdsAll);
				GraphNodeSet graphNodeSet = entityBaseService.getAccelerometerNode(catIdsCSV, isSingleEntity, isIpad);
				if (tweetSet == null) {
					tweetSet = new TweetSet();
				}
				tweetSet.setTweetAccelerometer(graphNodeSet);
			}

		} catch (Exception e) {
			LOG.error("Exception Fetching Tweets!", e);
		} finally {
			if (tweetSet == null) {
				tweetSet = new TweetSet();
			}
			PerfMonitor.recordStats(start, PerfActivityType.ReqTime);
			PerfRequestEntry stat = PerfMonitor.endRequest();
			tweetSet.setStat(stat);
		}
		return tweetSet;
	}

	private void collectResults(FRCompletionService<BaseSet> completionService, EntityBriefInfo entityBriefInfo)
			throws InterruptedException, ExecutionException {
		int submissions = completionService.getSubmissions();
		for (int i = 0; i < submissions; i++) {
			BaseSet obj = null;
			Future<BaseSet> f = completionService.poll(this.executorTimeout, TimeUnit.MILLISECONDS);
			if (f != null) {
				obj = f.get();
				if (obj != null) {
					entityBriefInfo.updatePrep(obj);
					entityBriefInfo.setPerfStats(obj);
				}
			} else {
				LOG.warn("service not responded for the given timeout " + this.executorTimeout);
			}
		}
	}

	private int getDaysCount(IEntityInfo entityInfo) {

		int daysCount = 180;
		/*
		 int docCount = entityInfo.getDocCount();

		if (docCount >= 0 && docCount < 90) {
			daysCount = 180;
		} else if (docCount >= 90 && docCount < 3000) {
			daysCount = 90;
		} else if (docCount >= 3000) {
			daysCount = 30;
		}*/
		return daysCount;
	}

	private INPUT_ENTITY_TYPE getInputEntityType(IEntityInfo entityInfo) {
		if (entityInfo != null) {
			if (entityInfo.getType() == SearchTokenEntry.SEARCH_TOKEN_TAG_SECTOR_TOPIC) {
				return INPUT_ENTITY_TYPE.TOPIC;
			} else if (entityInfo.getType() == SearchTokenEntry.SEARCH_TOKEN_TAG_COMPANY) {
				return INPUT_ENTITY_TYPE.COMPANY;
			} else if (entityInfo.getType() == SearchTokenEntry.SEARCH_TOKEN_TAG_INDUSTRY) {
				return INPUT_ENTITY_TYPE.INDUSTRY;
			}
		}
		return null;
	}

	@Override
	public EntityMap getEntityMapBySourceSearchToken(String searchToen, String language) throws Exception {

		if (StringUtils.isEmpty(searchToen)) {
			return null;
		}

		SolrDocumentList solrDocumentList = entityBaseServiceRepository.getSolrDocForSourceSearchToken(searchToen);

		if (CollectionUtils.isEmpty(solrDocumentList)) {
			return null;
		}

		IEntityInfoCache entityInfoCache = entityBaseServiceRepository.getEntityInfoCache();
		SolrDocument solrDocument = solrDocumentList.get(0);
		com.firstrain.frapi.domain.Entity entity = new com.firstrain.frapi.domain.Entity();
		entity.setId(searchToen);
		entity.setName((String) solrDocument.getFieldValue("title"));

		EntityMap entityMap = new EntityMap();
		entityMap.setEntity(entity);
		entityMap.setLanguage(FR_ArrayUtils.csvToArrayList(language));
		entityMap.setDomain((String) solrDocument.getFieldValue("attrDomain"));

		com.firstrain.frapi.domain.Entity sector = getEntityByEntityInfoCache((Integer) solrDocument.getFieldValue("sector"), entityInfoCache);
		if (sector != null) {
			entityMap.setSector(sector);
		}

		com.firstrain.frapi.domain.Entity segment =
				getEntityByEntityInfoCache((Integer) solrDocument.getFieldValue("segment"), entityInfoCache);
		if (segment != null) {
			entityMap.setSegment(segment);
		}

		com.firstrain.frapi.domain.Entity industry =
				getEntityByEntityInfoCache((Integer) solrDocument.getFieldValue("industry"), entityInfoCache);
		if (industry != null) {
			entityMap.setIndustry(industry);
		}

		com.firstrain.frapi.domain.Entity country =
				getEntityByEntityInfoCache((Integer) solrDocument.getFieldValue("country"), entityInfoCache);
		if (country != null) {
			entityMap.setCountry(country);
		}

		return entityMap;
	}

	private com.firstrain.frapi.domain.Entity getEntityByEntityInfoCache(Integer catId, IEntityInfoCache entityInfoCache) {

		if (catId == null || catId < 1) {
			return null;
		}

		IEntityInfo entityInfo = entityInfoCache.catIdToEntity(catId.toString());

		if (entityInfo != null) {
			com.firstrain.frapi.domain.Entity e = createEntity(entityInfo);
			return e;
		} else {
			LOG.warn("Entity not found in cache for catId : " + catId);
		}
		return null;
	}

	private com.firstrain.frapi.domain.Entity createEntity(final IEntityInfo entityInfo1) {
		com.firstrain.frapi.domain.Entity entity = new com.firstrain.frapi.domain.Entity();
		entity.setName(entityInfo1.getName());
		entity.setSearchToken(entityInfo1.getSearchToken());
		return entity;
	}

	@Override
	public EntityBriefInfo getEntityPeers(String primaryEntityTokenParam, List<String> searchTokenList, boolean isDnBId) throws Exception {

		String primaryEntityToken = primaryEntityTokenParam;
		long startTime = PerfMonitor.currentTime();

		try {

			IEntityInfoCache entityInfoCache = entityBaseServiceRepository.getEntityInfoCache();
			EntityBriefInfo entityBriefInfo = new EntityBriefInfo();
			IEntityInfo entityInfo = null;

			if (Boolean.TRUE.equals(isDnBId)) {
				// to support searchtoken for dunsid true case
				if (primaryEntityToken.startsWith(FRAPIConstant.COMPANY_PREFIX)) {
					entityInfo = entityInfoCache.searchTokenToEntity(primaryEntityToken.trim());
					if (entityInfo == null) {
						entityBriefInfo.setStatusCode(StatusCode.ENTITY_NOT_FOUND);
						return entityBriefInfo;
					}
					entityBriefInfo.setEntity(convertUtil.convertEntityInfo(entityInfo));
				} else { // handle duns here
                    Entity entity = EntityHandler.generateEntity(dnbService, primaryEntityToken, null);
					if (entity == null) {
						entityBriefInfo.setStatusCode(StatusCode.ENTITY_NOT_FOUND);
						return entityBriefInfo;
					}
					primaryEntityToken = entity.getSearchToken();
					entityBriefInfo.setEntity(entity);
				}
			} else {
				entityInfo = entityInfoCache.searchTokenToEntity(primaryEntityToken.trim());
				if (entityInfo == null) {
					entityBriefInfo.setStatusCode(StatusCode.ENTITY_NOT_FOUND);
					return entityBriefInfo;
				}
				entityBriefInfo.setEntity(convertUtil.convertEntityInfo(entityInfo));
			}

			// Actual logic goes here
			if (searchTokenList == null || searchTokenList.isEmpty()) {
				List<String> competitorsCatIdList =
						companyServiceRepository.getCompetitorCatIdsFromSolr(entityBriefInfo.getEntity().getCompanyId());

				if (competitorsCatIdList == null || competitorsCatIdList.isEmpty()) {
					entityBriefInfo.setStatusCode(StatusCode.PEERS_NOT_FOUND);
					return entityBriefInfo;
				}

				List<Entity> peers = getPeersObjectsFromCatIdList(competitorsCatIdList);
				entityBriefInfo.setStatusCode(StatusCode.REQUEST_SUCCESS);
				entityBriefInfo.setMatchedEntity(peers);
				return entityBriefInfo;
			}

			final Map<String, List<String>> blVsCompetitorsMap =
					companyServiceRepository.getBLVsCompetitorsCatIdsFromSolr(entityBriefInfo.getEntity().getCompanyId(), searchTokenList);

			if (blVsCompetitorsMap.isEmpty()) {
				entityBriefInfo.setStatusCode(StatusCode.PEERS_NOT_FOUND);
				return entityBriefInfo;
			}

			if (blVsCompetitorsMap.size() == 1) {

				List<Entity> peers = getPeersObjectsFromCatIdList(blVsCompetitorsMap.get(blVsCompetitorsMap.keySet().iterator().next()));
				entityBriefInfo.setStatusCode(StatusCode.REQUEST_SUCCESS);
				entityBriefInfo.setMatchedEntity(peers);
				return entityBriefInfo;
			}

			// creating map with key as competitor's catid and value as its score
			Map<String, Integer> competitorCatIdVsScore = new HashMap<String, Integer>();
			for (List<String> competitors : blVsCompetitorsMap.values()) {
				for (int index = 0; index < competitors.size(); index++) {
					if (competitorCatIdVsScore.get(competitors.get(index)) == null) {
						competitorCatIdVsScore.put(competitors.get(index), 1);
					} else {
						int lastScore = competitorCatIdVsScore.get(competitors.get(index));
						competitorCatIdVsScore.put(competitors.get(index), lastScore + 1);
					}
				}
			}

			// sort this map in ascending order of scores (low score means top priority for
			// competitor)
			List<Map.Entry<String, Integer>> entryList = new ArrayList<Map.Entry<String, Integer>>(competitorCatIdVsScore.entrySet());

			Comparator<Map.Entry<String, Integer>> byMapValues = new Comparator<Map.Entry<String, Integer>>() {
				@Override
				public int compare(Map.Entry<String, Integer> left, Map.Entry<String, Integer> right) {

					if (!left.getValue().equals(right.getValue())) {
						return right.getValue().compareTo(left.getValue());
					}

					int leftWeigth = getWeight(blVsCompetitorsMap.values(), left.getKey());
					int rightWeigth = getWeight(blVsCompetitorsMap.values(), right.getKey());
					return Integer.valueOf(leftWeigth).compareTo(rightWeigth);
				}

			};

			Collections.sort(entryList, byMapValues);
			List<String> competitorsCatIdList = new ArrayList<String>(entryList.size());
			for (Map.Entry<String, Integer> entry : entryList) {
				competitorsCatIdList.add(entry.getKey());
			}

			List<Entity> peers = getPeersObjectsFromCatIdList(competitorsCatIdList);
			entityBriefInfo.setStatusCode(StatusCode.REQUEST_SUCCESS);
			entityBriefInfo.setMatchedEntity(peers);
			return entityBriefInfo;
		} catch (Exception e) {
			LOG.error("Error while fetching peers for entity:" + primaryEntityToken, e);
			throw e;
		} finally {
			PerfMonitor.recordStats(startTime, PerfActivityType.ReqTime, "getEntityPeers");
		}
	}

	private int getWeight(Collection<List<String>> values, String key) {

		int score = 0;

		for (List<String> competitors : values) {
			int index = competitors.indexOf(key);
			if (index != -1) {
				score += index + 1;
			}
		}
		return score;
	}

	private List<Entity> getPeersObjectsFromCatIdList(List<String> catIdList) {

		List<Entity> peers = new ArrayList<Entity>();

		for (String catId : catIdList) {
			IEntityInfo entityInfo = entityBaseServiceRepository.getEntityInfoCache().catIdToEntity(catId);
			if (entityInfo != null) {
				peers.add(convertUtil.convertEntityInfo(entityInfo));
			} else {
				LOG.warn("Entity not found in cache:" + catId);
			}
		}
		return peers;
	}
}
