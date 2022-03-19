package com.firstrain.frapi.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.NamedList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.firstrain.frapi.domain.Tweet;
import com.firstrain.frapi.domain.TwitterSpec;
import com.firstrain.frapi.pojo.Entity;
import com.firstrain.frapi.pojo.NotableDetails;
import com.firstrain.frapi.pojo.NotableDetails.NotableDetail;
import com.firstrain.frapi.pojo.wrapper.TweetSet;
import com.firstrain.frapi.pojo.wrapper.TweetSet.TweetGroup;
import com.firstrain.frapi.repository.EntityBaseServiceRepository;
import com.firstrain.frapi.repository.impl.IndustryClassificationMap;
import com.firstrain.frapi.service.TwitterService;
import com.firstrain.frapi.util.ConvertUtil;
import com.firstrain.frapi.util.DateBucketUtils;
import com.firstrain.frapi.util.DateBucketUtils.BucketSpec;
import com.firstrain.frapi.util.DefaultEnums.DateBucketingMode;
import com.firstrain.frapi.util.FRAPIConstant;
import com.firstrain.frapi.util.ServicesAPIUtil;
import com.firstrain.obj.IEntityInfo;
import com.firstrain.obj.IEntityInfoCache;
import com.firstrain.solr.client.SearchSpec;
import com.firstrain.solr.client.util.SolrServerReader;
import com.firstrain.utils.FR_ArrayUtils;
import com.firstrain.utils.TitleUtils;
import com.firstrain.utils.object.PerfActivityType;
import com.firstrain.utils.object.PerfMonitor;

@Service
public class TwitterServiceImpl implements TwitterService {

	private final Logger LOG = Logger.getLogger(TwitterServiceImpl.class);

	@Autowired
	@Qualifier("entityBaseServiceRepositoryImpl")
	private EntityBaseServiceRepository entityBaseServiceRepository;
	@Autowired
	private ConvertUtil convertUtil;
	@Autowired
	private ServicesAPIUtil servicesAPIUtil;
	@Autowired
	private DateBucketUtils dateBucketUtils;
	@Autowired
	private IndustryClassificationMap industryClassificationMap;

	private String[] TWITTER_FIELDS =
			{"tweetId", "source", "tweet", "coreTweet", "groupId", "groupLead", "links", "expandedLinks", "companyId", "topicIdCoreTweet", // topic
																																			// cat
																																			// id
					"tweetCreationDate", "name", "screenName", "userImage", "description", "tweetClass", "tweetScore", "affinityScore",
					"categories"};

	private String[] TWEET_GROUP_FIELDS = {"groupId", "tweetId", "companyId", "catId", "scope", "creationTime", "comboScore", "groupSize",
			"affinityScoreNormalized", "affinityScope"};
	private final int MAX_ENTITY_FOR_ROUND_ROBIN = 200;

	@Override
	public TweetSet getTweets(String[] catIds, boolean attachGroupInfo, TwitterSpec spec, long... tweetIds) throws Exception {

		TweetSet tweetSet = null;
		if (tweetIds == null || tweetIds.length == 0) {
			throw new IllegalArgumentException("Supplied tweetIds is null/empty");
		}
		StringBuilder query = new StringBuilder("tweetId:(");
		for (long id : tweetIds) {
			query.append(id).append(" ");
		}
		query.setLength(query.length() - 1);
		query.append(")");
		LOG.debug("Query for tweets: " + query);
		SolrDocumentList docList = SolrServerReader.retrieveNSolrDocs(entityBaseServiceRepository.getTwitterServer(), query.toString(), 0,
				tweetIds.length, TWITTER_FIELDS);

		if (docList != null && !docList.isEmpty()) {
			List<Tweet> tweets = new ArrayList<Tweet>();
			for (SolrDocument sd : docList) {
				tweets.add(getTweet(sd, null, catIds, spec.getIpad()));
			}
			fillTimeLabel(tweets);
			Collections.sort(tweets, new Comparator<Tweet>() {
				@Override
				public int compare(Tweet o1, Tweet o2) {
					return o1.getTweetId().compareTo(o2.getTweetId());
				}
			});
			tweetSet = new TweetSet();
			/*
			 * if (spec.getNeedBucket() != null && spec.getNeedBucket() == Boolean.TRUE) { BucketSpec bSpec = null; if (spec.getBucketMode()
			 * != null) { bSpec = servicesAPIUtil.getDateBucketingSpec(spec.getBucketMode(), "tweetCreationDate"); } else { bSpec =
			 * servicesAPIUtil.getDateBucketingSpec(DateBucketingMode.AUTO, "tweetCreationDate"); }
			 * tweetSet.setBucketedTweets(dateBucketUtils.getListGroupByDate(tweets, bSpec));
			 * tweetSet.setBucketMode(ConvertUtil.convertToBaseDateBucketingMode(bSpec.mode)); } else { tweetSet.setTweets(tweets); }
			 */

			tweetSet.setTweets(tweets);

			tweetSet.setTotal((int) docList.getNumFound());
			if (attachGroupInfo) {
				attachGroupInfoToTweets(tweetSet, null);
			}
		}
		return tweetSet;
	}

	private void attachGroupInfoToTweets(TweetSet tweetset, Map<Long, TweetGroup> tweetIdvsTweetGrpMapParam) throws Exception {

		Map<Long, TweetGroup> tweetIdvsTweetGrpMap = tweetIdvsTweetGrpMapParam;
		if (tweetset == null || tweetset.getTweets() == null || tweetset.getTweets().isEmpty()) {
			return;
		}
		if (tweetIdvsTweetGrpMap == null) {
			StringBuilder ids = new StringBuilder();
			for (Tweet tweet : tweetset.getTweets()) {
				ids.append(tweet.getGroupId()).append(" ");
			}
			ids.setLength(ids.length() - 1);
			String query = "groupId:(" + ids + ")";
			SolrDocumentList docList = SolrServerReader.retrieveSolrDocsInBatches(entityBaseServiceRepository.getTwitterGroupServer(),
					query, tweetset.getTweets().size(), TWEET_GROUP_FIELDS);
			if (docList != null && !docList.isEmpty()) {
				tweetIdvsTweetGrpMap = new HashMap<Long, TweetGroup>();
				for (SolrDocument sd : docList) {
					TweetGroup tweetGroup = getTweetGroup(sd);
					tweetIdvsTweetGrpMap.put(tweetGroup.getGroupId(), tweetGroup);
				}
			} else {
				return;
			}
		}
		for (Tweet tweet : tweetset.getTweets()) {
			TweetGroup tgrp = tweetIdvsTweetGrpMap.get(Long.parseLong(tweet.getTweetId()));
			if (tgrp != null) {
				tweet.setComboScore(tgrp.getComboScore());
				tweet.setScope(tgrp.getScope());
				tweet.setGroupSize(tgrp.getGroupSize());

				// set affinityScoreNorm and affinityScope
				List<Integer> categories = (List) tgrp.getCatId();
				List<Integer> affinityScore = (List) tgrp.getAffinityScoreNormalized();
				List<Byte> affinityScope = (List) tgrp.getAffinityScope();

				Entity entity = tweet.getEntity();
				if (categories != null && categories.contains(Integer.valueOf(entity.getId()))) {
					int index = categories.indexOf(Integer.valueOf(entity.getId()));
					LOG.info(index);
					if (affinityScore != null) {
						Integer score = affinityScore.get(index);
						if (score != null) {
							entity.setRelevanceScore(score.shortValue());
						} else {
							entity.setRelevanceScore(null);
						}
					}

					if (affinityScope != null) {
						Byte scope = affinityScope.get(index);
						if (scope != null) {
							entity.setBand(scope.intValue());
						}
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private TweetGroup getTweetGroup(SolrDocument sd) throws Exception {
		TweetGroup twg = new TweetGroup();
		twg.setTweetId((Long) sd.getFieldValue("tweetId"));
		twg.setComboScore((Integer) sd.getFieldValue("comboScore"));
		twg.setCreationTime((Date) sd.getFieldValue("creationTime"));
		twg.setGroupId((Long) sd.getFieldValue("groupId"));
		twg.setScope((Byte) sd.getFieldValue("scope"));
		twg.setAffinityScoreNormalized((Collection) sd.getFieldValues("affinityScoreNormalized"));
		twg.setAffinityScope((Collection) sd.getFieldValues("affinityScope"));
		twg.setGroupSize((Integer) sd.getFieldValue("groupSize"));
		twg.setCatId((Collection) sd.getFieldValues("catId"));

		return twg;
	}

	/**
	 * @param date to format
	 * @return 1 min, 2 mins, 59 mins, 1 hrs 3 hrs, Yesterday, or Date in format dd-MMM-yyyy
	 */
	public void fillTimeLabel(List<Tweet> tweets) {
		if (tweets == null || tweets.isEmpty()) {
			return;
		}
		Calendar cal = Calendar.getInstance();
		Date current = cal.getTime();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.add(Calendar.MILLISECOND, -1);
		Date yesterday = cal.getTime();

		cal.add(Calendar.DATE, -1);
		Date dayBeforeYesterday = cal.getTime();

		SimpleDateFormat ddMMMyyyy = new SimpleDateFormat("dd-MMM-yyyy");
		for (Tweet tweet : tweets) {
			Date date = tweet.getTweetCreationDate();
			String timeLabel = null;
			if (date.after(yesterday)) {
				long diff = current.getTime() - date.getTime();
				diff = diff / (1000 * 60); // minutes
				if (diff < 1) {
					diff = 1;
				}
				if (diff < 60) {
					timeLabel = diff + (diff == 1 ? " min" : " mins");
				} else {
					diff = diff / 60; // hours
					timeLabel = diff + (diff == 1 ? " hour" : " hours");
				}
			} else if (date.after(dayBeforeYesterday)) {
				timeLabel = "Yesterday";
			} else {
				timeLabel = ddMMMyyyy.format(date);
			}
			tweet.setTimeLabel(timeLabel);
		}
	}

	@SuppressWarnings("unchecked")
	private Tweet getTweet(SolrDocument sd, Map<Long, NamedList<Object>> tweetMap, String[] catIds, Boolean ipad) {
		Tweet tweet = new Tweet();
		tweet.setTweetId(((Long) sd.getFieldValue("tweetId")).toString());
		Collection<Integer> companyIds = (Collection) sd.getFieldValues("companyId");
		List<Integer> compTopicCatId = new ArrayList<Integer>();
		if (companyIds != null && !companyIds.isEmpty()) {
			IEntityInfo eInfo = null;
			for (int companyId : companyIds) {
				eInfo = entityBaseServiceRepository.getEntityInfoCache().companyIdToEntity(companyId);
				if (eInfo != null) {
					compTopicCatId.add((Integer.parseInt(eInfo.getId())));
				}
			}
			tweet.setCompanyIds(companyIds);
		}
		Collection<Integer> topicCatIds = (Collection) sd.getFieldValues("topicIdCoreTweet");
		if (topicCatIds != null && !topicCatIds.isEmpty()) {
			compTopicCatId.addAll(topicCatIds);
			tweet.setTopicIdCoreTweet(topicCatIds);
		}
		tweet.setDescription((String) sd.getFieldValue("description"));
		tweet.setExpandedLinks((Collection) sd.getFieldValues("expandedLinks"));
		tweet.setTweetCreationDate((Date) sd.getFieldValue("tweetCreationDate"));
		tweet.setLinks((Collection) sd.getFieldValues(("links")));
		tweet.setName((String) sd.getFieldValue("name"));
		tweet.setScreenName((String) sd.getFieldValue("screenName"));
		tweet.setTweet((String) sd.getFieldValue("tweet"));
		tweet.setCoreTweet((String) sd.getFieldValue("coreTweet"));
		tweet.setTweetClass((String) sd.getFieldValue("tweetClass"));
		tweet.setUserImage((String) sd.getFieldValue("userImage"));
		tweet.setSources((Collection) sd.getFieldValues(("source")));
		Long groupId = (Long) sd.getFieldValue("groupId");
		if (groupId != null) {
			tweet.setGroupId(groupId);
		}

		Boolean groupLead = (Boolean) sd.getFieldValue("groupLead");
		if (groupLead != null) {
			tweet.setGroupLead(groupLead);
		}
		// setTitle based on input for ipad, default is already set
		if (ipad != null && ipad.booleanValue()) {
			tweet.setTitle(TitleUtils.getTweetTitleHtml(tweet.getTweet(), tweet.getCoreTweet(), tweet.getLinks()));
		} else {
			String title = TitleUtils.getTweetTitleWithoutLink(tweet.getTweet(), tweet.getCoreTweet(), tweet.getLinks(), false);
			tweet.setTitle(title);
		}
		NamedList<Object> tweetObj = null;
		String catId = null;
		if (tweetMap != null) {
			tweetObj = tweetMap.get(Long.parseLong(tweet.getTweetId()));
			if (tweetObj != null) {
				catId = (String) tweetObj.get("catId");
			}
		}
		if (catId == null) {
			if (catIds != null && compTopicCatId.size() > 1) {
				final int[] intCatIds = new int[catIds.length];
				int i = 0;
				for (String catId1 : catIds) {
					intCatIds[i++] = Integer.parseInt(catId1);
				}
				Collections.sort(compTopicCatId, new Comparator<Integer>() {
					@Override
					public int compare(Integer o1, Integer o2) {
						int index1 = getIndex(intCatIds, o1);
						int index2 = getIndex(intCatIds, o2);
						if (index1 != -1 && index2 != -1) {
							return Integer.valueOf(index1).compareTo(index2);
						}
						if (index1 != -1) {
							return -1;
						}
						if (index2 != -1) {
							return 1;
						}
						return 0;
					}

					public int getIndex(int arr[], int a) {
						int result = -1;
						for (int i = 0; i < arr.length; i++) {
							if (arr[i] == a) {
								result = i;
								break;
							}
						}
						return result;
					}
				});
				catId = Integer.toString(compTopicCatId.get(0));
			} else if (!compTopicCatId.isEmpty()) {
				catId = Integer.toString(compTopicCatId.iterator().next());
			}
		}
		if (catId != null) {
			IEntityInfoCache cache = entityBaseServiceRepository.getEntityInfoCache();
			Map<Integer, Integer> industryClassfMap = industryClassificationMap.getIndustryClassificationMap();
			IEntityInfo entityInfo = cache.catIdToEntity(catId);
			if (entityInfo != null) {
				Entity entity = convertUtil.convertEntityInfo(entityInfo);
				if (tweetObj != null) {
					if (tweetObj.get("affinityScoreNormalized") != null) {
						entity.setRelevanceScore(Short.parseShort(tweetObj.get("affinityScoreNormalized").toString()));
					} else {
						entity.setRelevanceScore(null);
					}
					entity.setBand((Integer) tweetObj.get("scope"));
				}
				tweet.setEntity(entity);
				// set industries - to be used in tagging
				List<Entity> industries = new ArrayList<Entity>();
				tweet.setIndustries(industries);

				// add all primary and secondary industry, sector and segment
				Set<Integer> taggedIndustries = new HashSet<Integer>();
				taggedIndustries.add(entityInfo.getIndustryCatId());
				taggedIndustries.add(entityInfo.getSectorCatId());
				taggedIndustries.add(entityInfo.getSegmentCatId());
				if (entityInfo.getSecondaryIndustry() != null) {
					taggedIndustries.addAll(entityInfo.getSecondaryIndustry());
				}
				if (entityInfo.getSecondarySector() != null) {
					taggedIndustries.addAll(entityInfo.getSecondarySector());
				}
				if (entityInfo.getSecondarySegment() != null) {
					taggedIndustries.addAll(entityInfo.getSecondarySegment());
				}

				// set all indusrry,sector and segment in the tweet object
				for (int indcatId : taggedIndustries) {
					IEntityInfo indEntityInfo = cache.catIdToEntity(Integer.toString(indcatId));
					if (indEntityInfo != null) {
						Entity ent = convertUtil.convertEntityInfo(indEntityInfo);
						industries.add(ent);
						Integer industryClassificationId = industryClassfMap.get(indcatId);
						ent.setIndustryClassificationId(industryClassificationId);
						// set primary entity score and scope
						ent.setBand(entity.getBand());
						ent.setRelevanceScore(entity.getRelevanceScore());
					}
				}
			}
		}
		return tweet;
	}

	@Override
	public List<Tweet> getTweetUsers(long groupId, long tweetId) throws Exception {

		List<Tweet> tweetList = new ArrayList<Tweet>();

		String query = "groupId:(" + groupId + ") AND -tweetId:(" + tweetId + ")";
		LOG.debug("SOLR getTweets query: " + query);
		SolrDocumentList docList = SolrServerReader.retrieveNSolrDocs(entityBaseServiceRepository.getTwitterServer(), query, 0, 10,
				"userTweetScore", false, "screenName", "userImage");
		if (docList != null && !docList.isEmpty()) {
			for (SolrDocument sd : docList) {
				Tweet tw = new Tweet();
				tw.setScreenName((String) sd.getFieldValue("screenName"));
				tw.setUserImage((String) sd.getFieldValue("userImage"));
				tweetList.add(tw);
			}
		}
		return tweetList;
	}

	@Override
	@SuppressWarnings("unchecked")
	public TweetSet getTopTweets(TwitterSpec tSpec) throws Exception {

		if (tSpec.getCatIds() == null || tSpec.getCatIds().length == 0) {
			return null;
		}

		String[] catIds = tSpec.getCatIds();
		if (catIds.length > MAX_ENTITY_FOR_ROUND_ROBIN) {
			String[] tmpArr = new String[MAX_ENTITY_FOR_ROUND_ROBIN];
			System.arraycopy(catIds, 0, tmpArr, 0, MAX_ENTITY_FOR_ROUND_ROBIN);
			LOG.debug("trimming company ids to " + MAX_ENTITY_FOR_ROUND_ROBIN + " length." + Arrays.toString(catIds));
			catIds = tmpArr;
		}

		SolrQuery solrQuery = new SolrQuery();
		solrQuery.set("qt", "topTweets");
		solrQuery.add("q", catIds);
		// solrQuery.set("rows", tSpec.getRows());
		solrQuery.set("rows", FRAPIConstant.MAX_TWEET_COUNT);
		solrQuery.set("start", tSpec.getStart());
		String key = "merge";
		if (tSpec.isListView()) {
			if (tSpec.getScope() != SearchSpec.SCOPE_AUTO) {
				solrQuery.set("scope", tSpec.getScope());
			}
			solrQuery.set("rows.autoscope", 60);
			key = "roundrobin";
			solrQuery.set("merge", "roundrobin");
			solrQuery.set("rows.roundrobin", 150);
		} else {
			solrQuery.set("rows.autoscope", tSpec.getRows());
			if (tSpec.getCatIds().length == 1) {
				key = "merge";
				solrQuery.set("merge", "merge");
			} else {
				key = "roundrobin";
				solrQuery.set("merge", "roundrobin");
				solrQuery.set("rows.roundrobin", tSpec.getRows());
			}
		}

		LOG.debug("query = " + solrQuery.toString());
		QueryRequest qRequest = new QueryRequest(solrQuery, SolrRequest.METHOD.GET);
		QueryResponse qResponse = qRequest.process(entityBaseServiceRepository.getTwitterGroupServer());
		NamedList<Object> results = qResponse.getResponse();

		Map<Long, NamedList<Object>> tweetMap = new LinkedHashMap<Long, NamedList<Object>>();
		int total = 0;
		int tempTotal = 0;
		if (results != null) {
			NamedList<Object> topTweets = (NamedList<Object>) results.get("topTweets");
			NamedList<Object> summary = (NamedList<Object>) results.get("summary");
			if (summary != null) {
				NamedList<Object> summaryDetail = (NamedList<Object>) summary.get(key);
				if (summaryDetail != null) {
					tSpec.setScope((Integer) summaryDetail.get("resultScope"));
					if (summaryDetail.get("maxPagingRows") != null) {
						tempTotal = (Integer) summaryDetail.get("maxPagingRows");
						if (tempTotal > 150) {
							total = 150;
						} else {
							total = tempTotal;
						}
					}
				}
			}

			if (topTweets != null) {
				List<Object> tweetList = (List<Object>) topTweets.get(key);
				for (int i = 0; i < tweetList.size(); i++) {
					NamedList<Object> tweetObj = (NamedList<Object>) tweetList.get(i);
					tweetMap.put((Long) tweetObj.get("tweetId"), tweetObj);
				}
			}
		}

		// Exclude hidden tweets
		if (!tweetMap.isEmpty() && tSpec.getExcludeArticleIdsSSV() != null && !tSpec.getExcludeArticleIdsSSV().isEmpty()) {
			tweetMap = excludeHiddenTweets(tweetMap, tSpec.getExcludeArticleIdsSSV());
		}

		tempTotal = total = tweetMap.size();

		if (tweetMap.size() > tSpec.getRows()) {
			Map<Long, NamedList<Object>> temp = new LinkedHashMap<Long, NamedList<Object>>();

			for (Map.Entry<Long, NamedList<Object>> entry : tweetMap.entrySet()) {
				if (temp.size() == tSpec.getRows()) {
					break;
				}
				temp.put(entry.getKey(), entry.getValue());
			}
			tweetMap = temp;
		}

		LOG.debug("Total Count == " + total);
		TweetSet tweetList = null;
		if (!tweetMap.isEmpty()) {
			tweetList = getTweets(tweetMap, tSpec);
			tweetList.setTotalCount(tempTotal + tSpec.getStart());
			tweetList.setTotal(total);
		}

		return tweetList;
	}

	private TweetSet getTweets(Map<Long, NamedList<Object>> tweetMap, TwitterSpec tSpec) throws Exception {
		TweetSet tweetset = null;
		if (tweetMap == null || tweetMap.isEmpty()) {
			throw new IllegalArgumentException("Supplied tweetIds is null/empty");
		}
		String query = "tweetId:(" + FR_ArrayUtils.getStringFromCollection(tweetMap.keySet(), " ") + ")";
		LOG.debug("Query for tweets: " + query);
		SolrDocumentList docList = SolrServerReader.retrieveNSolrDocs(entityBaseServiceRepository.getTwitterServer(), query.toString(), 0,
				tweetMap.size(), TWITTER_FIELDS);

		if (docList != null && !docList.isEmpty()) {
			List<Tweet> tweets = new ArrayList<Tweet>();
			for (SolrDocument sd : docList) {
				tweets.add(getTweet(sd, tweetMap, null, tSpec.getIpad()));
			}
			fillTimeLabel(tweets);
			sortTweetsBySuppliedIds(tweets, FR_ArrayUtils.collectionToLongArray(tweetMap.keySet()));
			tweetset = new TweetSet();
			tweetset.setTotal((int) docList.getNumFound());
			tweetset.setTweets(tweets);
			attachGroupInfoToTweets(tweetset, null);

			if (tSpec.getNeedBucket() != null && tSpec.getNeedBucket() == Boolean.TRUE) {
				BucketSpec bSpec = null;
				if (tSpec.getBucketMode() != null) {
					bSpec = servicesAPIUtil.getDateBucketingSpec(convertUtil.convertToServiceDateBucketingMode(tSpec.getBucketMode()),
							"tweetCreationDate");
				} else {
					bSpec = servicesAPIUtil.getDateBucketingSpec(DateBucketingMode.AUTO, "tweetCreationDate");
				}
				tweetset.setBucketedTweets(dateBucketUtils.getListGroupByDate(tweets, bSpec));
				tweetset.setBucketMode(convertUtil.convertToBaseDateBucketingMode(bSpec.mode));
				// tweetset.setTweets(null);
			}
		}
		return tweetset;
	}

	@Override
	public NotableDetails getNotableDetails(long tweetId, long groupIdParam, boolean isIpad) throws Exception {
		long groupId = groupIdParam;
		NotableDetails details = null;
		long start = PerfMonitor.currentTime();
		try {
			if (groupId < 0) {
				groupId = tweetId;
			}
			List<Tweet> portalTweet = getTweetUsers(tweetId, groupId);
			if (portalTweet != null) {
				details = new NotableDetails();
				List<NotableDetails.NotableDetail> notableDetails = new ArrayList<NotableDetails.NotableDetail>();
				for (Tweet portal : portalTweet) {
					NotableDetail detail = new NotableDetail();
					detail.setUserImage(portal.getUserImage());
					detail.setScreenName(portal.getScreenName());
					if (isIpad) {
						detail.setProfileUrl(TitleUtils.TWITTER_URL_MOBILE + portal.getScreenName());
					} else {
						detail.setProfileUrl(TitleUtils.TWITTER_URL + portal.getScreenName());
					}
					notableDetails.add(detail);
				}
				details.setNotableDetails(notableDetails);
				details.setTweetId(FRAPIConstant.TWEET_PREFIX + tweetId);
			}
		} catch (Exception e) {
			LOG.error("Error while fetching notable icons details for tweetId " + tweetId + " " + e.getMessage(), e);
			throw e;
		} finally {
			PerfMonitor.recordStats(start, PerfActivityType.ReqTime, "getNotableDetails");
		}
		return details;
	}

	private void sortTweetsBySuppliedIds(List<Tweet> tweets, final long[] tweetIds) throws Exception {
		Collections.sort(tweets, new Comparator<Tweet>() {
			@Override
			public int compare(Tweet o1, Tweet o2) {
				String tweetId = o1.getTweetId();
				String tweet2Id = o2.getTweetId();
				int index1 = getIndex(tweetIds, Long.parseLong(tweetId));
				int index2 = getIndex(tweetIds, Long.parseLong(tweet2Id));
				if (index1 != -1 && index2 != -1) {
					return Integer.valueOf(index1).compareTo(index2);
				}
				if (index1 != -1) {
					return -1;
				}
				if (index2 != -1) {
					return 1;
				}
				return 0;
			}

			public int getIndex(long arr[], long a) {
				int result = -1;
				for (int i = 0; i < arr.length; i++) {
					if (arr[i] == a) {
						result = i;
						break;
					}
				}
				return result;
			}
		});
	}

	private Map<Long, NamedList<Object>> excludeHiddenTweets(Map<Long, NamedList<Object>> tweetMap, String excludeArticleIdsSSV) {

		List<String> tweetIdsToExclude = FR_ArrayUtils.getListBySplitString(excludeArticleIdsSSV, " ");

		for (String tweetId : tweetIdsToExclude) {

			Long tweetLongId = 0L;
			try {
				tweetLongId = Long.parseLong(tweetId.trim());
			} catch (Exception e) {
			}

			tweetMap.remove(tweetLongId);
		}

		return tweetMap;
	}
}
