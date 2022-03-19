package com.firstrain.frapi.service.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import com.firstrain.db.obj.BaseItem.FLAGS;
import com.firstrain.db.obj.EmailSchedule;
import com.firstrain.db.obj.MailLog;
import com.firstrain.db.obj.Tags;
import com.firstrain.db.obj.Template;
import com.firstrain.db.obj.UserGroupMap.MembershipType;
import com.firstrain.frapi.FRCompletionService;
import com.firstrain.frapi.domain.BaseSpec;
import com.firstrain.frapi.domain.MonitorEmail;
import com.firstrain.frapi.domain.SectionSpec;
import com.firstrain.frapi.domain.TwitterSpec;
import com.firstrain.frapi.domain.User;
import com.firstrain.frapi.domain.VisualizationData;
import com.firstrain.frapi.domain.VisualizationData.ChartType;
import com.firstrain.frapi.obj.MonitorBriefDomain;
import com.firstrain.frapi.pojo.EnterprisePref;
import com.firstrain.frapi.pojo.MonitorAPIResponse;
import com.firstrain.frapi.pojo.MonitorBriefDetail;
import com.firstrain.frapi.pojo.MonitorEmailAPIResponse;
import com.firstrain.frapi.pojo.wrapper.BaseSet;
import com.firstrain.frapi.pojo.wrapper.BaseSet.SectionType;
import com.firstrain.frapi.pojo.wrapper.DocumentSet;
import com.firstrain.frapi.pojo.wrapper.TweetSet;
import com.firstrain.frapi.repository.EmailServiceRepository;
import com.firstrain.frapi.repository.MonitorServiceRepository;
import com.firstrain.frapi.repository.UserServiceRepository;
import com.firstrain.frapi.service.EntityBaseService;
import com.firstrain.frapi.service.EntityProcessingService;
import com.firstrain.frapi.service.MonitorBriefService;
import com.firstrain.frapi.service.MonitorService;
import com.firstrain.frapi.service.RestrictContentService;
import com.firstrain.frapi.service.TwitterService;
import com.firstrain.frapi.service.VisualizationService;
import com.firstrain.frapi.util.FRAPIConstant;
import com.firstrain.frapi.util.MonitorAnalyticsUtil;
import com.firstrain.frapi.util.SearchResultGenerator;
import com.firstrain.frapi.util.ServicesAPIUtil;
import com.firstrain.frapi.util.StatusCode;
import com.firstrain.frapi.util.TagsValidator;
import com.firstrain.frapi.util.UserMembership;
import com.firstrain.solr.client.DocEntry;
import com.firstrain.solr.client.SearchResult;
import com.firstrain.solr.client.SearchSpec;
import com.firstrain.utils.FR_ArrayUtils;
import com.firstrain.utils.object.PerfActivityType;
import com.firstrain.utils.object.PerfMonitor;
import com.firstrain.utils.object.PerfRequestEntry;

@Service
public class MonitorBriefServiceImpl implements MonitorBriefService {

	@Autowired
	private VisualizationService visualizationService;
	@Autowired
	private MonitorServiceRepository monitorServiceRepository;
	@Autowired
	private UserServiceRepository userServiceRepository;
	@Autowired
	private MonitorAnalyticsUtil monitorAnalyticsUtil;
	@Autowired
	private EntityProcessingService entityProcessingService;
	@Autowired
	private EntityBaseService entityBaseService;
	@Autowired
	private TwitterService twitterService;
	@Autowired
	private MonitorService monitorService;
	@Autowired
	private EmailServiceRepository emailServiceRepository;
	@Autowired
	private RestrictContentService restrictContentService;
	@Autowired
	private ServicesAPIUtil servicesAPIUtil;
	@Autowired(required = true)
	protected ThreadPoolTaskExecutor executorService;
	@Value("${executor.timeout}")
	protected long executorTimeout;
	@Value("${content.filter.token}")
	protected String contentFilterToken;

	private final Logger LOG = Logger.getLogger(MonitorBriefServiceImpl.class);

	@Override
	public MonitorEmailAPIResponse getMonitorEmailList(User user, long monitorId, String startDateStr, String endDateStr) throws Exception {
		long startTime = PerfMonitor.currentTime();
		try {
			MonitorEmailAPIResponse res = new MonitorEmailAPIResponse();
			Tags tag = monitorServiceRepository.getTagById(monitorId);

			if (tag == null || tag.getFlags().equals(FLAGS.DELETED)) {
				res.setStatusCode(StatusCode.ENTITY_NOT_FOUND);
				return res;
			}

			res.setMonitorId(monitorId);
			res.setMonitorName(tag.getTagName());

			if (tag.getEmailId() == -1) {
				// to do: add StatusCode no email scheudle for the monitor
				res.setStatusCode(StatusCode.NO_EMAIL_SCHEDULE);
				return res;
			}

			Date startDate;
			Date endDate;
			Timestamp startDateTS = null;
			Timestamp endDateTS = null;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			sdf2.setTimeZone(TimeZone.getTimeZone("PST"));
			if (startDateStr == null || startDateStr.length() == 0 || endDateStr == null || endDateStr.length() == 0) {
				startDateTS = Timestamp.valueOf(sdf2.format(new Date()));
				Calendar cal = Calendar.getInstance();
				cal.setTime(startDateTS);
				cal.add(Calendar.DATE, -7);
				startDateTS.setTime(cal.getTime().getTime());
			} else {
				String[] start = populateTimeZone(startDateStr, sdf); 
				
				if (start != null && start.length > 0) {
					startDate = sdf.parse(start[0]);
					startDateTS = Timestamp.valueOf(sdf2.format(startDate));
				}
			}

			if (endDateStr == null || endDateStr.length() == 0) {
				endDateTS = Timestamp.valueOf(sdf2.format(new Date()));
			} else {
				String[] end = populateTimeZone(endDateStr, sdf); 
				
				if (end != null && end.length > 0) {
					endDate = sdf.parse(end[0]);
					endDateTS = Timestamp.valueOf(sdf2.format(endDate));
					Calendar cal = Calendar.getInstance();
					cal.setTime(endDateTS);
					cal.add(Calendar.DATE, 1);
					endDateTS.setTime(cal.getTime().getTime());
				}
			}
			int sentStatus = -1;

			List<MailLog> mailLogs = emailServiceRepository.getMailLog(tag.getEmailId(), startDateTS, sentStatus);
			EmailSchedule emailSchedule = emailServiceRepository.getEmailSchedule(tag.getEmailId());
			Template template = emailServiceRepository.getTemplate(emailSchedule.getTemplateID());
			if (template != null) {
				res.setEmailTemplate(template.getDisplayName());
			}
			if (mailLogs == null || mailLogs.isEmpty()) {
				res.setStatusCode(StatusCode.NO_EMAIL_SENT);
				res.setEmails(null);
				return res;
			} else {
				List<MonitorEmail> monitorEmails = new ArrayList<MonitorEmail>();
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
				simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
				for (MailLog ml : mailLogs) {
					if (ml.getSentTime().before(endDateTS)) {
						MonitorEmail monitorEmail = new MonitorEmail();
						monitorEmail.setEmailId(FRAPIConstant.EMAIL_PREFIX + Long.valueOf(ml.getId()).toString());
						monitorEmail.setSubject(ml.getMailSubject());
						monitorEmail.setTimeStamp(simpleDateFormat.format(ml.getSentTime()));
						monitorEmails.add(monitorEmail);
					}
				}
				res.setStatusCode(StatusCode.REQUEST_SUCCESS);
				res.setEmails(monitorEmails);
			}
			return res;
		} catch (Exception e) {
			LOG.error("Error while getting monitor emails for monitorId :" + monitorId, e);
			throw e;
		} finally {
			PerfMonitor.recordStats(startTime, PerfActivityType.ReqTime, "getMonitorEmails");
		}
	}
 
	private String[] populateTimeZone(final String endDateStr, final SimpleDateFormat sdf) { 
		String[] end = endDateStr.split(" "); 
		if (end != null && end.length > 1) { 
			sdf.getCalendar().setTimeZone(TimeZone.getTimeZone(end[1])); 
		} 
		return end; 
	} 
	

	@Override
	public MonitorAPIResponse getMonitorBriefDetails(User user, long monitorId, EnterprisePref enterprisePref, String fqInputParam)
			throws Exception {
		String fqInput = fqInputParam;
		MonitorBriefDetail monitorBriefDetail = new MonitorBriefDetail();

		long startTime = PerfMonitor.currentTime();
		try {
			FRCompletionService<BaseSet> completionService = new FRCompletionService<BaseSet>(executorService.getThreadPoolExecutor());
			MonitorAPIResponse res = new MonitorAPIResponse();
			Tags tag = monitorServiceRepository.getTagById(monitorId);

			if (tag == null || tag.getFlags().equals(FLAGS.DELETED)) {
				res.setStatusCode(StatusCode.ENTITY_NOT_FOUND);
				return res;
			}

			long frUserId = Long.parseLong(user.getUserId());

			if (tag.getOwnedBy() != frUserId) {
                MembershipType type = UserMembership.retrieveMembershipType(userServiceRepository, frUserId);
                if (MembershipType.ADMIN != type) {
                    if (TagsValidator.userDoesNotOwnMonitor(tag, res)) {
                        return res;
                    }
                    Set<Long> allGroupIds = monitorService.getAllGroupIdsOfUser(frUserId, user.getOwnedBy());
                    if (TagsValidator.isInsufficientPrivilege(tag, allGroupIds, res)) {
                        return res;
                    }
                }
			}

			List<com.firstrain.db.obj.Items> itemList = monitorServiceRepository.getItemsByTagId(tag.getId());
			if (itemList == null || itemList.isEmpty()) {
				res.setStatusCode(StatusCode.NO_ITEMS_IN_MONITOR);
				MonitorBriefDetail mbd = new MonitorBriefDetail();
				mbd.setMonitorId(Long.toString(tag.getId()));
				mbd.setMonitorName(tag.getTagName());
				res.setMonitorBriefDetail(mbd);
				return res;
			}
			String filterString = tag.getFilterString();

			Pattern ptn = Pattern.compile("\\s+");
			boolean excludeSourceContent = false;
			boolean includeSourceContent = false;

			if (fqInput != null) {
				if (fqInput.contains("-" + contentFilterToken)) {
					fqInput = fqInput.replaceAll("-" + contentFilterToken, "");
					Matcher mtch = ptn.matcher(fqInput);
					fqInput = mtch.replaceAll(" ");
					excludeSourceContent = true;
				} else if (fqInput.contains(contentFilterToken)) {
					fqInput = fqInput.replaceAll(contentFilterToken, "");
					Matcher mtch = ptn.matcher(fqInput);
					fqInput = mtch.replaceAll(" ");
					includeSourceContent = true;
				}
			}

			MonitorBriefDomain monitorBriefDomain = monitorAnalyticsUtil.getMonitorBriefDomainFromFolderId(itemList, tag.getId(),
					monitorServiceRepository.getEntityInfoCache(), filterString, fqInput, true);
			if (monitorBriefDomain == null) {
				throw new Exception("Monitor Brief Domain is Null for " + tag.getId());
			}

			Map<SectionType, SectionSpec> sectionSpecMap = enterprisePref.getSectionsMap();
			Set<SectionType> sectionsList = sectionSpecMap.keySet();

			if (sectionsList.contains(SectionType.FT)) {
				String[] compCatIdArr = FR_ArrayUtils.collectionToStringArray(monitorBriefDomain.getCompanyCatIds());
				String[] topicCatIdArr = FR_ArrayUtils.collectionToStringArray(monitorBriefDomain.getTopicCatIds());
				String[] catIdsAll = FR_ArrayUtils.concatStrArray(compCatIdArr, topicCatIdArr);
				SectionSpec sectionSpec = sectionSpecMap.get(SectionType.FT);
				String excludeArticleIdsSSV =
						restrictContentService.getAllHiddenContent(enterprisePref.getEnterpriseId(), FRAPIConstant.TWEET_PREFIX);
				Callable<BaseSet> tweetsFuture = fetchTweetsAsync(catIdsAll, frUserId, sectionSpec, excludeArticleIdsSSV);
				completionService.submit(tweetsFuture);
			}
			if (sectionsList.contains(SectionType.FR)) {

				SectionSpec sectionSpec = sectionSpecMap.get(SectionType.FR);
				Short start = sectionSpec.getStart();
				if (start == null) {
					start = 0;
				}

				BaseSpec baseSpec60Days = new BaseSpec();
				baseSpec60Days.setStart(start);
				baseSpec60Days.setNeedPagination(sectionSpec.getNeedPagination());

				baseSpec60Days.setCount(sectionSpec.getCount());

				baseSpec60Days.setDaysCount(FRAPIConstant.WEB_RESULTS_SEARCH_DAYS_BIMONTHLY);
				baseSpec60Days.setNeedImage(true);

				String excludeArticleIdsSSV =
						restrictContentService.getAllHiddenContent(enterprisePref.getEnterpriseId(), FRAPIConstant.DOCUMENT_PREFIX);
				if (!excludeArticleIdsSSV.isEmpty()) {
					baseSpec60Days.setExcludeArticleIdsSSV(excludeArticleIdsSSV);
				}
				baseSpec60Days.setIndustryClassificationId(enterprisePref.getIndustryClassificationId());

				baseSpec60Days =
						servicesAPIUtil.setSourceContent(excludeSourceContent, includeSourceContent, baseSpec60Days, enterprisePref);

				/* create another BaseSpec object with 180 days count */
				BaseSpec baseSpec180Days = new BaseSpec();
				baseSpec180Days.setStart(baseSpec60Days.getStart());
				baseSpec180Days.setNeedPagination(baseSpec60Days.getNeedPagination());
				baseSpec180Days.setCount(baseSpec60Days.getCount());
				baseSpec180Days.setDaysCount(FRAPIConstant.BASIC_WEB_RESULTS_SEARCH_DAYS);
				baseSpec180Days.setNeedImage(baseSpec60Days.getNeedImage());
				baseSpec180Days.setExcludeArticleIdsSSV(baseSpec60Days.getExcludeArticleIdsSSV());
				baseSpec180Days.setIndustryClassificationId(baseSpec60Days.getIndustryClassificationId());
				baseSpec180Days.setExcludeSourceIdsSSV(baseSpec60Days.getExcludeSourceIdsSSV());
				baseSpec180Days.setIncludeSourceIdsSSV(baseSpec60Days.getIncludeSourceIdsSSV());

				Callable<BaseSet> webResultsFuture = getWebResultsAsync(monitorBriefDomain.getqList(), monitorBriefDomain.getScopeList(),
						baseSpec60Days, baseSpec180Days, monitorId, frUserId);
				completionService.submit(webResultsFuture);
			}
			// handling visualization
			if (sectionsList.contains(SectionType.VIZ)) {
				List<ChartType> chTypeList = new ArrayList<ChartType>();
				for (SectionType st : sectionsList) {
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
							getVisualizationAsync(monitorId, 12, chTypeList, fqInput, enterprisePref.isApplyMinNodeCheckInVisualization());
					completionService.submit(visualizationFuture);
				}
			}

			res.setStatusCode(StatusCode.REQUEST_SUCCESS);
			monitorBriefDetail = collectResults(completionService, monitorBriefDetail);
			monitorBriefDetail.setMonitorId("" + tag.getId());
			monitorBriefDetail.setMonitorName(tag.getTagName());
			res.setMonitorBriefDetail(monitorBriefDetail);
			return res;
		} catch (Exception e) {
			LOG.error("Error while getting monitor config for monitorId :" + monitorId, e);
			throw e;
		} finally {
			PerfMonitor.recordStats(startTime, PerfActivityType.ReqTime, "getMonitorDetails");
		}
	}

	private MonitorBriefDetail collectResults(FRCompletionService<BaseSet> completionService, MonitorBriefDetail monitorBrief)
			throws InterruptedException, ExecutionException {
		int submissions = completionService.getSubmissions();
		for (int i = 0; i < submissions; i++) {
			BaseSet obj = null;
			Future<BaseSet> f = completionService.poll(this.executorTimeout, TimeUnit.MILLISECONDS);
			if (f != null) {
				obj = f.get();
				if (obj != null) {
					monitorBrief.updatePrep(obj);
					monitorBrief.setPerfStats(obj);
				}
			} else {
				LOG.warn("service not responded for the given timeout " + this.executorTimeout);
			}
		}
		return monitorBrief;
	}

	private Callable<BaseSet> getWebResultsAsync(final String[] qMulti, final int[] scopeMulti, final BaseSpec baseSpec60Days,
			final BaseSpec baseSpec180Days, final long folderId, final long userId) {
		return new Callable<BaseSet>() {
			@Override
			public DocumentSet call() throws Exception {
				DocumentSet documentSet = getWebResults(qMulti, scopeMulti, baseSpec60Days, baseSpec180Days, folderId, userId);
				documentSet.setSectionType(SectionType.FR);
				return documentSet;
			}
		};
	}

	private Callable<BaseSet> getVisualizationAsync(final long tagId, final int nodeCount, final List<ChartType> chartTypes,
			final String filters, final boolean isApplyMinNodeCheck) {
		return new Callable<BaseSet>() {
			@Override
			public VisualizationData call() throws Exception {
				PerfMonitor.startRequest(tagId + "", "Visualization");
				long start = PerfMonitor.currentTime();
				VisualizationData vd = null;
				try {
					vd = visualizationService.getVisualizationByMonitorId(tagId, nodeCount, chartTypes, filters, false,
							isApplyMinNodeCheck);
					vd.setSectionType(SectionType.VIZ);
				} catch (Exception e) {
					LOG.error("Error Getting Visualization for monitor " + tagId, e);
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

	private Callable<SearchResult> getSearchResultAsync(final String[] qMulti, final int[] scopeMulti, final String fq,
			final BaseSpec baseSpec) {
		return new Callable<SearchResult>() {
			@Override
			public SearchResult call() throws Exception {
				SearchResult searchResults = entityBaseService.getSearchResult(qMulti, scopeMulti, null, baseSpec);
				return searchResults;
			}
		};
	}

	private DocumentSet getWebResults(String[] qMulti, int[] scopeMulti, BaseSpec baseSpec60Days, BaseSpec baseSpec180Days, long folderId,
			long userId) {
		PerfMonitor.startRequest(userId + "", "Web Results");
		long start = PerfMonitor.currentTime();
		DocumentSet webResults = null;
		try {
			int desiredCount = baseSpec60Days.getCount();

			if (!Boolean.TRUE.equals(baseSpec60Days.getNeedPagination())) {
				baseSpec60Days.setCount((short) (baseSpec60Days.getCount() * 3));
			}
			baseSpec180Days.setCount(baseSpec60Days.getCount());

			Future<SearchResult> webResultFuture60Days =
					executorService.submit(getSearchResultAsync(qMulti, scopeMulti, null, baseSpec60Days));
			Future<SearchResult> webResultFuture180Days =
					executorService.submit(getSearchResultAsync(qMulti, scopeMulti, null, baseSpec180Days));

            SearchResult searchResults = SearchResultGenerator.collectSearchResults(webResultFuture60Days,
                    webResultFuture180Days, baseSpec60Days);

			baseSpec60Days.setCount((short) desiredCount);
			webResults = entityBaseService.getWebResults(searchResults, baseSpec60Days);
			if (webResults != null) {
				entityProcessingService.getDocumentSetWithId(webResults);
			}
		} catch (Exception e) {
			LOG.error("Error Getting Web Results for monitor " + folderId, e);
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

	private Callable<BaseSet> fetchTweetsAsync(final String[] catIdsAll, final long userId, final SectionSpec sectionSpec,
			final String excludeArticleIdsSSV) {
		return new Callable<BaseSet>() {
			@Override
			public TweetSet call() throws Exception {
				TweetSet tweet = fetchTweets(catIdsAll, userId, sectionSpec, excludeArticleIdsSSV);
				tweet.setSectionType(SectionType.FT);
				return tweet;
			}
		};
	}

	private TweetSet fetchTweets(String[] catIdsAll, long userId, SectionSpec sectionSpec, String excludeArticleIdsSSV) {
		PerfMonitor.startRequest("", "Tweets");
		long tStart = PerfMonitor.currentTime();
		TweetSet tweets = null;
		try {
			TwitterSpec spec = new TwitterSpec();
			spec.setRows(sectionSpec.getCount());
			spec.setNeedBucket(sectionSpec.getNeedBucket());
			spec.setStart(sectionSpec.getStart());
			spec.setIpad(false);
			spec.setScope(SearchSpec.SCOPE_AUTO);
			spec.setCatIds(catIdsAll);
			if (!excludeArticleIdsSSV.isEmpty()) {
				spec.setExcludeArticleIdsSSV(excludeArticleIdsSSV);
			}
			tweets = entityBaseService.getTweetList(catIdsAll, spec);
			if (tweets != null) {
				entityProcessingService.getTweetsWithId(tweets);
			}
		} catch (Exception e) {
			LOG.error("Exception Fetching Tweets!", e);
		} finally {
			if (tweets == null) {
				tweets = new TweetSet();
			}
			PerfMonitor.recordStats(tStart, PerfActivityType.ReqTime);
			PerfRequestEntry stat = PerfMonitor.endRequest();
			tweets.setStat(stat);
		}
		return tweets;
	}
}
