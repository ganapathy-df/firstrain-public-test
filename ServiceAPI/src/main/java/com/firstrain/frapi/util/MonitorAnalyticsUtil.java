package com.firstrain.frapi.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.firstrain.db.obj.Items;
import com.firstrain.frapi.obj.MonitorBriefDomain;
import com.firstrain.frapi.repository.EntityBaseServiceRepository;
import com.firstrain.obj.IEntityInfo;
import com.firstrain.obj.IEntityInfoCache;
import com.firstrain.solr.client.SearchSpec;
import com.firstrain.solr.client.SearchTokenEntry;
import com.firstrain.solr.client.SearchTokenEntry.Relation;
import com.firstrain.solr.client.SolrSearcher;
import com.firstrain.utils.FR_ArrayUtils;
import com.firstrain.utils.object.PerfActivityType;
import com.firstrain.utils.object.PerfMonitor;

@Service
public class MonitorAnalyticsUtil {
	private static final Logger LOG = Logger.getLogger(MonitorAnalyticsUtil.class);

	@Autowired
	@Qualifier("entityBaseServiceRepositoryImpl")
	private EntityBaseServiceRepository entityBaseServiceRepository;
	@Autowired
	private ServicesAPIUtil servicesAPIUtil;
	@Autowired
	private ConvertUtil convertUtil;
	public static final int MAX_SOLR_CHUNK_SIZE = 450;

	/**
	 * API: Compute Primary Industry for a monitor
	 * 
	 * Accumulate Industry/Segment/Sector both primary and secondary for each entity. Look in Industry, if industry with highest count is >=
	 * entityCount, select as primary industry else Look in Segment, if Segment with highest count is >= entityCount, select as primary
	 * industry else Look in Sector, if Segment with highest count is >= entityCount, select as primary industry else No primary industry
	 * selected for monitor.
	 * 
	 * @param itemList
	 * @param monitorId
	 * @param entityInfoCache
	 * @return
	 */
	public MonitorBriefDomain getMonitorBriefDomainFromFolderId(List<com.firstrain.db.obj.Items> itemList, long monitorId,
			IEntityInfoCache entityInfoCache, String filterString, String fqInput, boolean needIndustryEntities) throws Exception {
		long start = PerfMonitor.currentTime();
		MonitorBriefDomain monitorBriefDomain = null;

		boolean industryOnly = true;
		int industryCatId = -1;

		if (itemList == null || itemList.isEmpty()) {
			LOG.error("No items found for Tag " + monitorId);
			return monitorBriefDomain;
		}

		try {
			Map<String, Map<Integer, EntityCount>> classMap = new HashMap<String, Map<Integer, EntityCount>>();
			classMap.put("industry", new HashMap<Integer, EntityCount>());
			classMap.put("segment", new HashMap<Integer, EntityCount>());
			classMap.put("sector", new HashMap<Integer, EntityCount>());
			int entityCountInMonitor = 0;

			monitorBriefDomain = new MonitorBriefDomain();

			Set<String> companyCatIds = new HashSet<String>();
			Set<String> topicCatIds = new HashSet<String>();
			Set<Integer> companyIds = new HashSet<Integer>();
			List<String> qList = new ArrayList<String>();
			List<Integer> scopeList = new ArrayList<Integer>();
			List<com.firstrain.frapi.pojo.Items> itemListPojo = new ArrayList<com.firstrain.frapi.pojo.Items>();

			// ServicesAPIUtil.fillSearchMetaData(itemList, qList, scopeList, companyIds, companyCatIds, topicCatIds, entityInfoCache,
			// itemListPojo);
			String keyword = servicesAPIUtil.getKeywordFromFilterString(filterString, monitorId);

			List<String> industryCatIds = new ArrayList<String>();
			List<String> bizLineCatIds = new ArrayList<String>();
			for (Items item : itemList) {
				SearchSpec searchSpec = QueryParseUtil.parse(item.getData());
				if (searchSpec != null) {
					String fq = searchSpec.getFq();
					if (fq == null) {
						fq = "";
					}

					if (fqInput != null && !fqInput.isEmpty()) {
						if (fq.isEmpty()) {
							fq = fqInput;
						} else {
							fq = fq + " AND " + fqInput;
						}
					}
					qList.add((searchSpec.getQ() + " " + fq).trim());
					scopeList.add(searchSpec.scope);
					String qQuery = searchSpec.getQ();
					if (keyword != null && !keyword.isEmpty()) {
						qQuery = servicesAPIUtil.getQToken(searchSpec.getQ(), keyword);
					}
					LOG.debug("item:" + item.getName() + ", item.getId():" + item.getId());
					List<SearchTokenEntry> tokens = SolrSearcher.parseInput(qQuery);

					com.firstrain.frapi.pojo.Items itemPojo = new com.firstrain.frapi.pojo.Items();
					itemPojo.setId(item.getId());
					itemPojo.setName(item.getName());
					itemPojo.setData(item.getData());
					if (tokens != null) {
						if (tokens.size() == 1) {
							IEntityInfo entityInfo = entityInfoCache.searchTokenToEntity(tokens.get(0).getSearchToken());
							if (entityInfo != null) {
								itemPojo.setEntity(convertUtil.convertEntityInfo(entityInfo));
							}
						}
						boolean isCompanyOrTopicInSearch = false;
						boolean isIndustryInSearch = false;
						// iterate and populate classification with count for entities
						for (SearchTokenEntry token : tokens) {
							if (token.relation != Relation.MUST_NOT_HAVE) {

								IEntityInfo e = entityInfoCache.searchTokenToEntity(token.searchToken);
								if (e != null) {
									LOG.debug("SearchTokenEntry:" + e.getName() + ", e.getType()" + e.getType() + ", relation:"
											+ token.relation);

									if (e.getType() == SearchTokenEntry.SEARCH_TOKEN_TAG_COMPANY) {
										companyCatIds.add(e.getId());
										companyIds.add(e.getCompanyId());
										isCompanyOrTopicInSearch = true;
									} else if (e.getType() == SearchTokenEntry.SEARCH_TOKEN_TAG_SECTOR_TOPIC) {
										topicCatIds.add(e.getId());
										isCompanyOrTopicInSearch = true;
									} else if (e.getType() == SearchTokenEntry.SEARCH_TOKEN_TAG_INDUSTRY) {
										industryCatIds.add(e.getId());
										isIndustryInSearch = true;
									} else if (e.getType() == SearchTokenEntry.SEARCH_TOKEN_TAG_BIZLINE) {
										bizLineCatIds.add(e.getId());
									}

									if (e.getType() == SearchTokenEntry.SEARCH_TOKEN_TAG_COMPANY
											|| e.getType() == SearchTokenEntry.SEARCH_TOKEN_TAG_INDUSTRY
											|| e.getType() == SearchTokenEntry.SEARCH_TOKEN_TAG_SECTOR_TOPIC) {

										entityCountInMonitor++;

										// check if entity is other than industry
										if (e.getType() != SearchTokenEntry.SEARCH_TOKEN_TAG_INDUSTRY) {
											industryOnly = false;
										}

										// add industry
										if (e.getIndustryCatId() > 0) {
											if (classMap.get("industry").get(e.getIndustryCatId()) == null) {
												classMap.get("industry").put(e.getIndustryCatId(), new EntityCount(e.getIndustryCatId()));
											} else {
												classMap.get("industry").get(e.getIndustryCatId()).addCount();
											}
										}

										// add SecondaryIndustry
										if (e.getSecondaryIndustry() != null && e.getSecondaryIndustry().size() > 0) {
											for (int id : e.getSecondaryIndustry()) {
												addEntityCount(classMap, "industry", id);
											}
										}

										// add Segment
										if (e.getSegmentCatId() > 0) {
											if (classMap.get("segment").get(e.getSegmentCatId()) == null) {
												classMap.get("segment").put(e.getSegmentCatId(), new EntityCount(e.getSegmentCatId()));
											} else {
												classMap.get("segment").get(e.getSegmentCatId()).addCount();
											}
										}

										// add SecondarySegment
										if (e.getSecondarySegment() != null && e.getSecondarySegment().size() > 0) {
											for (int id : e.getSecondarySegment()) {
												addEntityCount(classMap, "segment", id);
											}
										}

										// add Sector
										if (e.getSectorCatId() > 0) {
											if (classMap.get("sector").get(e.getSectorCatId()) == null) {
												classMap.get("sector").put(e.getSectorCatId(), new EntityCount(e.getSectorCatId()));
											} else {
												classMap.get("sector").get(e.getSectorCatId()).addCount();
											}
										}

										// add SecondarySector
										if (e.getSecondarySector() != null && e.getSecondarySector().size() > 0) {
											for (int id : e.getSecondarySector()) {
												addEntityCount(classMap, "sector", id);
											}
										}
									}
								}
							}
						}
						/* Consider entities of type [BL/Industry] only when [Company/Rel.Topic] are not present in search. */
						if (isCompanyOrTopicInSearch) {
							industryCatIds = new ArrayList<String>();
							bizLineCatIds = new ArrayList<String>();
						}
						/* Consider entity type [BL] only when [Industry/Company/Rel.Topic] are not present in search. */
						if (isIndustryInSearch && !bizLineCatIds.isEmpty()) {
							bizLineCatIds = new ArrayList<String>();
						}

					}
					itemListPojo.add(itemPojo);
				}
			}

			LOG.debug("classMap populated for monitor :" + monitorId);

			// look PrimaryIndustry in industry
			industryCatId = getPrimaryIndustryforClass(classMap, "industry", entityCountInMonitor);
			if (industryCatId < 0) {
				// look PrimaryIndustry in segment
				industryCatId = getPrimaryIndustryforClass(classMap, "segment", entityCountInMonitor);
				if (industryCatId < 0) {
					// look PrimaryIndustry in sector
					industryCatId = getPrimaryIndustryforClass(classMap, "sector", entityCountInMonitor);
				}
			}

			if (industryCatId > -1) {
				monitorBriefDomain.setIndustryCatId(industryCatId);
			} else if (!bizLineCatIds.isEmpty()) {
				monitorBriefDomain.setBizlineCatIdCSV(FR_ArrayUtils.csvFromStringList(bizLineCatIds));
			}
			monitorBriefDomain.setIndustryOnly(industryOnly);
			monitorBriefDomain.setqList(qList.toArray(new String[qList.size()]));
			monitorBriefDomain.setScopeList(FR_ArrayUtils.collectionToIntArray(scopeList));
			monitorBriefDomain.setCompanyCatIds(companyCatIds);
			monitorBriefDomain.setCompanyIdsArr(FR_ArrayUtils.collectionToIntArray(companyIds));
			monitorBriefDomain.setItemList(itemListPojo);
			monitorBriefDomain.setTopicCatIds(topicCatIds);

			// collect all monitor entities.
			LOG.debug("Total entities in monitor companyCatIds:" + companyCatIds + " topicCatIds:" + topicCatIds + " industryCatIds:"
					+ industryCatIds);
			Set<String> monitorEntities = new HashSet<String>(companyCatIds);
			monitorEntities.addAll(topicCatIds);
			monitorEntities.addAll(industryCatIds);
			monitorBriefDomain.setMonitorEntities(monitorEntities);

			if (needIndustryEntities) {
				// Honor industry related company and topic.
				String[] fields = {"attrCatId"};
				SolrDocumentList docList = entityBaseServiceRepository.getSolrDocFromIndustryAndBizlineCatIds(industryCatIds, bizLineCatIds,
						fields, false, Boolean.FALSE);
				List<IEntityInfo> companyList = new ArrayList<IEntityInfo>();
				List<IEntityInfo> topicList = new ArrayList<IEntityInfo>();
				// populate data from solr doc list.
				entityBaseServiceRepository.populateCompanyTopicsFromSolrDocs(docList, companyList, topicList, null);
				backFillEntities(monitorBriefDomain, companyList, topicList);
				if (LOG.isDebugEnabled()) {
					LOG.debug("Total eligible companyCatIds "
							+ (monitorBriefDomain.getCompanyCatIds() == null ? 0 : monitorBriefDomain.getCompanyCatIds().size()));
					LOG.debug("Total eligible companyIds "
							+ (monitorBriefDomain.getCompanyIdsArr() == null ? 0 : monitorBriefDomain.getCompanyIdsArr().length));
					LOG.debug("Total eligible topicCatIds "
							+ (monitorBriefDomain.getTopicCatIds() == null ? 0 : monitorBriefDomain.getTopicCatIds().size()));
				}

			}

		} catch (Exception e) {
			LOG.error("Error Filling MonitorBriefDomain", e);
			throw e;
		} finally {
			PerfMonitor.recordStats(start, PerfActivityType.ReqTime, "Monitor Brief Object");
		}
		return monitorBriefDomain;
	}

	private void addEntityCount(final Map<String, Map<Integer, EntityCount>> classMap, final String key, final int id) {
		if (classMap.get(key).get(id) == null) {
			classMap.get(key).put(id, new EntityCount(id));
		} else {
			classMap.get(key).get(id).addCount();
		}
	}

	private void backFillEntities(MonitorBriefDomain monitorBriefDomain, List<IEntityInfo> companyList, List<IEntityInfo> topicList) {
		if (monitorBriefDomain == null || (companyList == null && topicList == null)) {
			return;
		}
		Set<String> companyCatIds = monitorBriefDomain.getCompanyCatIds();
		int[] companyIdsArr = monitorBriefDomain.getCompanyIdsArr();
		List<Integer> companyIdsList = (List<Integer>) FR_ArrayUtils.intArrayToCollection(companyIdsArr);
		Set<String> topicCatIds = monitorBriefDomain.getTopicCatIds();
		if (companyCatIds == null) {
			companyCatIds = new HashSet<String>();
		}
		if (topicCatIds == null) {
			topicCatIds = new HashSet<String>();
		}
		if (companyIdsList == null) {
			companyIdsList = new ArrayList<Integer>();
		}
		int remaining_count = MAX_SOLR_CHUNK_SIZE - (companyCatIds.size() + topicCatIds.size());
		if (companyList != null && !companyList.isEmpty() && remaining_count > 0) {
			for (int i = 0; i < Math.min(remaining_count, companyList.size()); i++) {
				companyCatIds.add(companyList.get(i).getId());
				companyIdsList.add(companyList.get(i).getCompanyId());
				remaining_count--;
			}
		}
		if (remaining_count > 0 && topicList != null && !topicList.isEmpty()) {
			for (int i = 0; i < Math.min(remaining_count, topicList.size()); i++) {
				topicCatIds.add(topicList.get(i).getId());
			}
		}
		monitorBriefDomain.setCompanyIdsArr(FR_ArrayUtils.collectionToIntArray(companyIdsList));
		monitorBriefDomain.setCompanyCatIds(companyCatIds);
		monitorBriefDomain.setTopicCatIds(topicCatIds);
	}

	private int getPrimaryIndustryforClass(Map<String, Map<Integer, EntityCount>> classMap, String classify, int entityCount) {
		int industryCatId = -1;

		Map<Integer, EntityCount> map = classMap.get(classify);
		if (map != null && map.size() > 0) {
			List<EntityCount> list = new ArrayList<EntityCount>(map.values());
			Collections.sort(list, new EntityCountComparator());

			if (!list.isEmpty() && list.get(0).count >= entityCount) {
				industryCatId = list.get(0).id;
			}
		}
		LOG.debug("Primary industry found in " + classify + " : " + industryCatId);
		return industryCatId;
	}

	private static class EntityCountComparator implements Comparator<EntityCount> {
		@Override
		public int compare(EntityCount o1, EntityCount o2) {
			if (o1.count > o2.count) {
				return -1;
			}
			return 1;
		}
	}

	private static class EntityCount {
		int id = 0;
		int count = 0;

		public EntityCount(int id) {
			this.id = id;
			this.count = 1;
		}

		public void addCount() {
			this.count++;
		}
	}
}
