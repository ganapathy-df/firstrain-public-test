package com.firstrain.frapi.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.firstrain.db.obj.BaseItem;
import com.firstrain.db.obj.BaseItem.FLAGS;
import com.firstrain.db.obj.BaseItem.OwnedByType;
import com.firstrain.db.obj.EmailSchedule.EmailScheduleEnums;
import com.firstrain.db.obj.Groups;
import com.firstrain.db.obj.Items;
import com.firstrain.db.obj.MailingList;
import com.firstrain.db.obj.Tags;
import com.firstrain.db.obj.TagsInfo;
import com.firstrain.db.obj.UserGroupMap;
import com.firstrain.db.obj.UserGroupMap.MembershipType;
import com.firstrain.db.obj.Users;
import com.firstrain.frapi.domain.EntityStatus;
import com.firstrain.frapi.domain.ItemDetail;
import com.firstrain.frapi.domain.MonitorBucketForTitle;
import com.firstrain.frapi.domain.MonitorConfig;
import com.firstrain.frapi.domain.MonitorDetails;
import com.firstrain.frapi.domain.MonitorInfo;
import com.firstrain.frapi.domain.User;
import com.firstrain.frapi.obj.MonitorWizardFilters;
import com.firstrain.frapi.obj.MonitorWizardFilters.Advanced;
import com.firstrain.frapi.pojo.EnterprisePref;
import com.firstrain.frapi.pojo.Entity;
import com.firstrain.frapi.pojo.MonitorAPIResponse;
import com.firstrain.frapi.pojo.UserItem;
import com.firstrain.frapi.repository.EntityBaseServiceRepository;
import com.firstrain.frapi.repository.GroupServiceRepository;
import com.firstrain.frapi.repository.MonitorServiceRepository;
import com.firstrain.frapi.repository.UserServiceRepository;
import com.firstrain.frapi.service.MonitorService;
import com.firstrain.frapi.util.ConvertUtil;
import com.firstrain.frapi.util.FRAPIConstant;
import com.firstrain.frapi.util.MonitorOrderingUtils;
import com.firstrain.frapi.util.MonitorOrderingUtils.OrderedMonitors;
import com.firstrain.frapi.util.QueryParseUtil;
import com.firstrain.frapi.util.ServicesAPIUtil;
import com.firstrain.frapi.util.StatusCode;
import com.firstrain.frapi.util.TagsValidator;
import com.firstrain.frapi.util.UserMembership;
import com.firstrain.obj.IEntityInfo;
import com.firstrain.solr.client.SearchSpec;
import com.firstrain.utils.FR_ArrayUtils;
import com.firstrain.utils.JSONUtility;
import com.firstrain.utils.object.PerfActivityType;
import com.firstrain.utils.object.PerfMonitor;

@Service
public class MonitorServiceImpl implements MonitorService {

	private final Logger LOG = Logger.getLogger(MonitorServiceImpl.class);

	@Autowired
	@Qualifier("monitorServiceRepositoryImpl")
	private MonitorServiceRepository monitorServiceRepository;
	@Autowired
	private GroupServiceRepository groupServiceRepository;
	@Autowired
	private UserServiceRepository userServiceRepository;
	@Autowired
	private ConvertUtil convertUtil;
	@Autowired
	private ServicesAPIUtil servicesAPIUtil;
	@Autowired
	@Qualifier("entityBaseServiceRepositoryImpl")
	private EntityBaseServiceRepository entityBaseServiceRepository;

	@Override
	public MonitorAPIResponse getMonitorListByOwner(User actor, User user, String ownerType) throws Exception {

		long startTime = PerfMonitor.currentTime();
		try {
			MonitorAPIResponse res = new MonitorAPIResponse();

			/* ownerType is not used */

			MonitorDetails monitorDetails = getMonitorDetailsByUser(user, false);
			if (monitorDetails.getMonitors().isEmpty()) {
				res.setStatusCode(StatusCode.NO_MONITORS);
				return res;
			}
			res.setMonitorDetails(monitorDetails);
			res.setStatusCode(StatusCode.REQUEST_SUCCESS);
			return res;
		} catch (Exception e) {
			LOG.error("Error while getting monitor list for userId:" + user.getUserId(), e);
			throw e;
		} finally {
			PerfMonitor.recordStats(startTime, PerfActivityType.ReqTime, "getMonitorListByOwner");
		}
	}

	@Override
	public MonitorAPIResponse removeEntities(long frUserId, long monitorId, List<String> entityListParam) throws Exception {

		List<String> entityList = entityListParam;
		long startTime = PerfMonitor.currentTime();
		try {
			MonitorAPIResponse res = new MonitorAPIResponse();
			// This method simply replaces old token with new token for all valid entities and Keywords searches. Invalid entities remains
			// same.
			entityList = fetchLatestEntities(entityList);
            if (TagsValidator.isIllegalArgument(entityList, res)) {
                return res;
            }
            Tags tag = monitorServiceRepository.getTagById(monitorId);
            if (TagsValidator.isEntityNotFound(tag, monitorId, res)) {
                return res;
            }
            if (TagsValidator.userDoesNotOwnMonitor(tag, frUserId, res)) {
                return res;
            }
			List<Items> itemList = monitorServiceRepository.getItemsByTagId(tag.getId());
			if (itemList == null || itemList.isEmpty()) {
				res.setStatusCode(StatusCode.NO_ITEMS);
				return res;
			}

			MonitorWizardFilters filters = JSONUtility.deserialize(tag.getFilterString(), MonitorWizardFilters.class);

			// After below call, entityList contains only those entities which are not available in DB
			Map<String, Entity> qVsEntity = getEntitiesMap(itemList, entityList, filters);

			List<Long> entityIdList = new ArrayList<Long>();
			/* collect all entities id with value > 0 */
			for (Map.Entry<String, Entity> entry : qVsEntity.entrySet()) {
				if (!entry.getValue().getId().equals("-1L")) {
					entityIdList.add(Long.parseLong(entry.getValue().getId()));
				}
			}

			/*
			 * if(entityIdList.size() == itemList.size()){ res.setStatusCode(StatusCode.CANNOT_DELETE_ALL_ITEMS); return res; }
			 */

			if (!entityIdList.isEmpty()) {
				monitorServiceRepository.removeEntities(tag, entityIdList);
			}

			List<Entity> entities = new ArrayList<Entity>();
			for (Map.Entry<String, Entity> entry : qVsEntity.entrySet()) {
				Entity ent = entry.getValue();
				if (entry.getValue().getId().equals("-1L")) {
					ent.setRemoved(false);
					entities.add(ent);
				} else {
					ent.setRemoved(true);
					entities.add(ent);
				}
			}
			if (entityIdList.size() == itemList.size()) {
				res.setStatusCode(StatusCode.NO_ITEMS_IN_MONITOR);
			} else {
				res.setStatusCode(StatusCode.REQUEST_SUCCESS);
			}
			res.setMonitorName(tag.getTagName());
			res.setMonitorId(monitorId);
			res.setEntities(entities);
			return res;
		} catch (Exception e) {
			LOG.error("Error while removing entities from monitorId :" + monitorId + " and for userId :" + frUserId, e);
			throw e;
		} finally {
			PerfMonitor.recordStats(startTime, PerfActivityType.ReqTime, "removeEntities");
		}
	}

	@Override
	public MonitorAPIResponse addEntities(long frUserId, long monitorId, List<String> entityListParam, EnterprisePref enterprisePref)
			throws Exception {
		// TODO : Refactoring - directly call getEntitiesStatus()
		List<String> entityList = entityListParam;
		long startTime = PerfMonitor.currentTime();
		try {
			MonitorAPIResponse res = new MonitorAPIResponse();
			// This method simply replaces old token with new token for all valid entities and Keywords searches. Invalid entities remains
			// same.
			entityList = fetchLatestEntities(entityList);
            if (TagsValidator.isIllegalArgument(entityList, res)) {
                return res;
            }
            Tags tag = monitorServiceRepository.getTagById(monitorId);
            if (TagsValidator.isEntityNotFound(tag, monitorId, res)) {
                return res;
            }
            if (TagsValidator.userDoesNotOwnMonitor(tag, frUserId, res)) {
                return res;
            }
			List<Items> itemList = monitorServiceRepository.getItemsByTagId(tag.getId());

			/* Restrict no. of searches on the basis of enterprise configuration */
			if (enterprisePref.getSearchesPerMonitor() > 0
					&& (itemList.size() + entityList.size()) > enterprisePref.getSearchesPerMonitor()) {
				res.setStatusCode(StatusCode.MONITOR_SEARCH_LIMIT_EXCEEDED);
				return res;
			}

			MonitorWizardFilters filters = JSONUtility.deserialize(tag.getFilterString(), MonitorWizardFilters.class);

			// After below call, entityList contains only those entities which we need to add to DB.
			Map<String, Entity> qVsEntity = getEntitiesMap(itemList, entityList, filters);

			// After below call, entityList contains only single token entities, but these need to be validated on entity info cache to
			// remove invalid tokens
			List<List<String>> entityCategoryList = categorizeEntities(entityList);
			List<String> keywordSearches = entityCategoryList.get(0);
			List<String> invalidEntities = entityCategoryList.get(1);
			List<String> keywordSearchesName = entityCategoryList.get(2);

			// This call populates invalid entities in 'invalidEntities' and returns valid entities in separate list
			List<Entity> validEntityList = monitorServiceRepository.validateTokensUsingEntityCache(entityList, invalidEntities);

			LOG.debug("valid entities: " + validEntityList);
			LOG.debug("invalid entities: " + invalidEntities);
			LOG.debug("keyword searches: " + keywordSearches);
			LOG.debug("keyword searches names: " + keywordSearchesName);

			if ((validEntityList != null && !validEntityList.isEmpty()) || !keywordSearches.isEmpty()) {
				monitorServiceRepository.addEntities(tag, validEntityList, keywordSearches, keywordSearchesName);
			}

			List<Entity> entities = new ArrayList<Entity>();

			for (Map.Entry<String, Entity> entry : qVsEntity.entrySet()) {
				Entity ent = entry.getValue();
				if (!ent.getId().equals("-1L")) {
					ent.setAdded(false);// we did not add this entity because it was already in DB
					entities.add(ent);
				} else {
					if (invalidEntities.contains(ent.getSearchToken())) {
						ent.setAdded(null);// we did not add this entity because it is invalid token
						entities.add(ent);
						continue;
					}
					Entity validEntity = getValidEntity(validEntityList, entry.getKey());
					if (validEntity != null) {
						ent.setAdded(true);// we added this to DB because either it was valid token
						ent.setName(validEntity.getName());
						ent.setSearchToken(validEntity.getSearchToken());
						entities.add(ent);
						continue;
					}
					if (keywordSearches.contains(ent.getSearchToken())) {
						ent.setAdded(true);// we added this to DB because either it was keyword search
						int index = keywordSearches.indexOf(ent.getSearchToken());
						ent.setName(keywordSearchesName.get(index));
						entities.add(ent);
					}
				}
			}
			res.setStatusCode(StatusCode.REQUEST_SUCCESS);
			res.setMonitorName(tag.getTagName());
			res.setMonitorId(monitorId);
			res.setEntities(entities);
			return res;
		} catch (Exception e) {
			LOG.error("Error while adding entities to monitorId :" + monitorId + " and for userId :" + frUserId, e);
			throw e;
		} finally {
			PerfMonitor.recordStats(startTime, PerfActivityType.ReqTime, "addEntities");
		}
	}

	private List<String> fetchLatestEntities(List<String> entityList) {
		if (entityList == null || entityList.isEmpty()) {
			return entityList;
		}

		List<String> latestTokenList = new ArrayList<String>();

		for (String entity : entityList) {

			if (entity == null || entity.trim().isEmpty()) {
				continue;
			}

			entity = entity.trim();
			IEntityInfo entityInfo = monitorServiceRepository.getEntityInfoCache().searchTokenToEntity(entity);
			if (entityInfo == null) {

				if (entity.indexOf(":") != -1 && entity.indexOf(" ") != -1) {// cases: C:Apple AND C:Google, C:Apple AND microsoft AND
																				// C:Google

					List<String> tempList = FR_ArrayUtils.getListBySplitString(entity, " ");
					List<String> tokens = new ArrayList<String>();
					List<Entity> validatedTokens = validateAllTokensInKeywordSearch(tokens, tempList);

					if (validatedTokens != null && validatedTokens.size() == tokens.size()) {
						entity = prepareEntityWithNewTokens(entity, tokens, validatedTokens);
					}
				}

				if (!latestTokenList.contains(entity)) {
					latestTokenList.add(entity);
				}
			} else {
				if (!latestTokenList.contains(entityInfo.getSearchToken())) {
					latestTokenList.add(entityInfo.getSearchToken());
				}
			}
		}
		return latestTokenList;
	}

	@Override
	public MonitorAPIResponse addFilters(long frUserId, long monitorId, List<String> filtersToAddListParam) throws Exception {

		List<String> filtersToAddList = filtersToAddListParam;
		long startTime = PerfMonitor.currentTime();
		try {
			MonitorAPIResponse res = new MonitorAPIResponse();
			res.setMonitorId(monitorId);
			filtersToAddList = removeGarbage(filtersToAddList);
            if (TagsValidator.isIllegalArgument(filtersToAddList, res)) {
                return res;
            }
            Tags tag = monitorServiceRepository.getTagById(monitorId);
            if (TagsValidator.isEntityNotFound(tag, monitorId, res)) {
                return res;
            }
            if (TagsValidator.userDoesNotOwnMonitor(tag, frUserId, res)) {
                return res;
            }
			List<List<String>> keywordsAndFiltersList = servicesAPIUtil.getKeywordsAndFilters(filtersToAddList);
			List<String> keywords = keywordsAndFiltersList.get(0);
			filtersToAddList = keywordsAndFiltersList.get(1);

			String existingFilterString = tag.getFilterString();
			String finalString = existingFilterString;

			MonitorWizardFilters oldData = JSONUtility.deserialize(existingFilterString, MonitorWizardFilters.class);

			finalString = servicesAPIUtil.addFilters(filtersToAddList, oldData, keywords);

			LOG.debug("existing filter string: " + existingFilterString);
			LOG.debug("adv. filters to add: " + filtersToAddList);
			LOG.debug("keywords to add: " + keywords);
			LOG.debug("final filter string: " + finalString);

			// check for filters already available
			if (existingFilterString != null && existingFilterString.equals(finalString)) {
				res.setStatusCode(StatusCode.FILTERS_ALREADY_PRESENT);
				LOG.debug("All filters already present. Nothing to add.");
			} else {
				updateTagFiltersAndSearches(existingFilterString, finalString, frUserId, tag, res); 
				
			}
			return res;
		} catch (Exception e) {
			LOG.error("Error while adding filters from monitorId :" + monitorId + " and for userId :" + frUserId, e);
			throw e;
		} finally {
			PerfMonitor.recordStats(startTime, PerfActivityType.ReqTime, "addFilters");
		}
	}

	@Override
	public MonitorAPIResponse removeFilters(long frUserId, long monitorId, List<String> filtersToRemoveListParam) throws Exception {

		List<String> filtersToRemoveList = filtersToRemoveListParam;
		long startTime = PerfMonitor.currentTime();
		try {
			MonitorAPIResponse res = new MonitorAPIResponse();
			res.setMonitorId(monitorId);
			filtersToRemoveList = removeGarbage(filtersToRemoveList);
            if (TagsValidator.isIllegalArgument(filtersToRemoveList, res)) {
                return res;
            }
            Tags tag = monitorServiceRepository.getTagById(monitorId);
            if (TagsValidator.isEntityNotFound(tag, monitorId, res)) {
                return res;
            }
            if (TagsValidator.userDoesNotOwnMonitor(tag, frUserId, res)) {
                return res;
            }
			List<List<String>> keywordsAndFiltersList = servicesAPIUtil.getKeywordsAndFilters(filtersToRemoveList);
			List<String> keywords = keywordsAndFiltersList.get(0);
			filtersToRemoveList = keywordsAndFiltersList.get(1);
			filtersToRemoveList.addAll(keywords);

			String existingFilterString = tag.getFilterString();
			String filteredString = existingFilterString;

			MonitorWizardFilters oldData = null;
			if (existingFilterString != null && !existingFilterString.isEmpty()) {
				oldData = JSONUtility.deserialize(existingFilterString, MonitorWizardFilters.class);
				if (oldData.advanced != null) {
					filteredString = servicesAPIUtil.removeFilters(filtersToRemoveList, oldData);
				}
			}

			LOG.debug("existing filter string: " + existingFilterString);
			LOG.debug("final filter string: " + filteredString);

			if (existingFilterString == null || existingFilterString.isEmpty() || existingFilterString.equals(filteredString)) {
				res.setStatusCode(StatusCode.FILTERS_NOT_FOUND);
				LOG.debug("No filter matches. So, nothing to remove.");
			} else {
				updateTagFiltersAndSearches(existingFilterString, filteredString, frUserId, tag, res); 
				
			}
			return res;
		} catch (Exception e) {
			LOG.error("Error while removing filters from monitorId :" + monitorId + " and for userId :" + frUserId, e);
			throw e;
		} finally {
			PerfMonitor.recordStats(startTime, PerfActivityType.ReqTime, "removeFilters");
		}
	}
 
	private void updateTagFiltersAndSearches(final String existingFilterString, final String filteredString, final long frUserId, final Tags tag, final MonitorAPIResponse res) throws Exception { 
		MonitorWizardFilters oldData = JSONUtility.deserialize(existingFilterString, MonitorWizardFilters.class); 
		MonitorWizardFilters finalData = JSONUtility.deserialize(filteredString, MonitorWizardFilters.class); 
		monitorServiceRepository.updateTagFiltersAndSearches(frUserId, tag, filteredString, oldData, finalData); 
		res.setStatusCode(StatusCode.REQUEST_SUCCESS); 
		res.setMonitorName(tag.getTagName()); 
	} 
	

	@Override
	public MonitorAPIResponse getMonitorDetails(User user, long monitorId) throws Exception {

		long startTime = PerfMonitor.currentTime();
		try {
			MonitorAPIResponse res = new MonitorAPIResponse();
			Tags tag = monitorServiceRepository.getTagById(monitorId);

			if (tag == null || tag.getFlags().equals(FLAGS.DELETED)) {
				res.setStatusCode(StatusCode.ENTITY_NOT_FOUND);
				return res;
			}

			long frUserId = Long.parseLong(user.getUserId());

			if (tag.getOwnedBy() != frUserId) {
                MembershipType type = UserMembership.retrieveMembershipType(userServiceRepository, frUserId);
                if (!MembershipType.ADMIN.equals(type)) {
                    if (TagsValidator.userDoesNotOwnMonitor(tag, res)) {
                        return res;
                    }
                    Set<Long> allGroupIds = getAllGroupIdsOfUser(frUserId, user.getOwnedBy());
                    if (TagsValidator.isInsufficientPrivilege(tag, allGroupIds, res)) {
                        return res;
                    }
                }
			}

			List<Items> itemList = monitorServiceRepository.getItemsByTagId(tag.getId());
			String filterString = tag.getFilterString();
			MonitorWizardFilters data = JSONUtility.deserialize(filterString, MonitorWizardFilters.class);
			MonitorConfig monitorConfig = new MonitorConfig();
			monitorConfig.setMonitorId(String.valueOf(tag.getId()));
			monitorConfig.setMonitorName(tag.getTagName());
			monitorConfig.setOwnedBy(tag.getOwnedBy());
			monitorConfig.setOwnedByType(tag.getOwnedByType().name());
			if (data.advanced.advancedFilters != null && !data.advanced.advancedFilters.isEmpty()) {
				monitorConfig.setFilters(data.advanced.advancedFilters);
			}
			List<ItemDetail> itemDetails = new ArrayList<ItemDetail>();
			for (Items item : itemList) {
				ItemDetail itemDetail = new ItemDetail();
				SearchSpec searchSpec = QueryParseUtil.parse(item.getData());
				itemDetail.setQueryName(item.getName());
				itemDetail.setQueryString(searchSpec.q);
				itemDetails.add(itemDetail);
			}
			monitorConfig.setQueries(itemDetails);
			res.setStatusCode(StatusCode.REQUEST_SUCCESS);
			res.setMonitorConfig(monitorConfig);
			return res;
		} catch (Exception e) {
			LOG.error("Error while getting monitor config for monitorId :" + monitorId, e);
			throw e;
		} finally {
			PerfMonitor.recordStats(startTime, PerfActivityType.ReqTime, "getMonitorDetails");
		}
	}

	@Override
	public MonitorAPIResponse getEntityStatus(User user, long monitorId, String entity) throws Exception {

		long startTime = PerfMonitor.currentTime();
		try {
			MonitorAPIResponse res = new MonitorAPIResponse();
			res.setMonitorId(monitorId);

			Tags tag = monitorServiceRepository.getTagById(monitorId);
			if (tag == null || FLAGS.DELETED.equals(tag.getFlags())) {
				res.setStatusCode(StatusCode.ENTITY_NOT_FOUND);
				return res;
			}

			long frUserId = Long.parseLong(user.getUserId());

			if (tag.getOwnedBy() != frUserId) {
                MembershipType type = UserMembership.retrieveMembershipType(userServiceRepository, frUserId);
                if (!MembershipType.ADMIN.equals(type)) {
                    if (TagsValidator.userDoesNotOwnMonitor(tag, res)) {
                        return res;
                    }
                    Set<Long> allGroupIds = getAllGroupIdsOfUser(frUserId, user.getOwnedBy());
                    if (TagsValidator.isInsufficientPrivilege(tag, allGroupIds, res)) {
                        return res;
                    }
                }
			}

			Entity finalEntity = new Entity();

			EntityStatus entityStatus = new EntityStatus();
			entityStatus.setName(tag.getTagName());
			entityStatus.setId("" + tag.getId());

			List<Items> itemList = monitorServiceRepository.getItemsByTagId(tag.getId());
			if (itemList == null || itemList.isEmpty()) {
				entityStatus.setEntityStatus(false);
				finalEntity.setName(entity);
				finalEntity.setSearchToken(entity);
			} else {
				MonitorWizardFilters filters = JSONUtility.deserialize(tag.getFilterString(), MonitorWizardFilters.class);
				Items item = getEntity(itemList, entity, filters);
				if (item == null) {
					entityStatus.setEntityStatus(false);
					finalEntity.setName(entity);
					finalEntity.setSearchToken(entity);
				} else {
					entityStatus.setEntityStatus(true);
					finalEntity.setName(item.getName());
					finalEntity.setSearchToken(entity);
				}

			}

			entityStatus.setEntity(finalEntity);

			res.setStatusCode(StatusCode.REQUEST_SUCCESS);
			res.setEntityStatus(entityStatus);
			return res;
		} catch (Exception e) {
			LOG.error("Error while checking entities from monitorId :" + monitorId + " and for userId :" + user.getUserId(), e);
			throw e;
		} finally {
			PerfMonitor.recordStats(startTime, PerfActivityType.ReqTime, "getEntitiesStatus");
		}
	}

	private Map<String, Entity> getEntitiesMap(List<Items> itemList, List<String> entityList, MonitorWizardFilters filters) {

		Map<String, Entity> entityVsId = new HashMap<String, Entity>(entityList.size());

		for (Items item : itemList) {
			String data = item.getData();
			Map<String, List<String>> map = servicesAPIUtil.prepareQueryMap(data);

			List<String> q = map.remove("q");
			if (q != null && !q.isEmpty()) {
				String query = q.get(0).trim();

				if (filters.advanced != null && filters.advanced.keywords != null && !filters.advanced.keywords.isEmpty()) {
					query = servicesAPIUtil.removeKeywordsFromQ(query, filters.advanced.keywords.trim());
				}

				String latestQuery = query;
				List<String> tempList = new ArrayList<String>();
				tempList.add(query);
				tempList = fetchLatestEntities(tempList);
				if (!tempList.isEmpty()) {
					latestQuery = tempList.get(0);
				}

				LOG.debug("DB Monitor item primary query (old):" + query);
				LOG.debug("DB Monitor item primary query (new):" + latestQuery);

				if (entityList.contains(latestQuery)) {
					LOG.debug("entity matched:" + latestQuery);
					Entity ent = new Entity();
					ent.setId(Long.toString(item.getId()));
					ent.setName(item.getName());
					ent.setSearchToken(latestQuery);
					entityVsId.put(latestQuery, ent);
					entityList.remove(latestQuery);
				}
			}

		}

		for (String entity : entityList) {
			LOG.debug("entity not matched:" + entity);
			Entity ent = new Entity();
			ent.setId("-1L");
			ent.setSearchToken(entity);
			ent.setName(entity);
			entityVsId.put(entity, ent);
		}

		return entityVsId;
	}

	private Items getEntity(List<Items> itemList, String entity, MonitorWizardFilters filters) {


		for (Items item : itemList) {
			String data = item.getData();
			Map<String, List<String>> map = servicesAPIUtil.prepareQueryMap(data);

			List<String> q = map.remove("q");
			if (q != null && !q.isEmpty()) {
				String query = q.get(0).trim();
				if (filters.advanced != null && filters.advanced.keywords != null && !filters.advanced.keywords.isEmpty()) {
					query = servicesAPIUtil.removeKeywordsFromQ(query, filters.advanced.keywords);
				}
				LOG.debug("DB Monitor item primary query:" + query);

				if (entity.equals(query)) {
					LOG.debug("entity matched:" + query);
					return item;
				}
			}

		}

		return null;
	}

	private MonitorDetails getMonitorDetailsByUser(User userObj, boolean attachSubscriptionInfo) throws Exception {

		MonitorDetails userDetails = new MonitorDetails();
		userDetails.setUserId(FRAPIConstant.USER_ID_PREFIX + userObj.getUserId());
		userDetails.setEmail(userObj.getEmail());
		userDetails.setUserName(userObj.getUserName());
		long userId = Long.parseLong(userObj.getUserId());
		OrderedMonitors orderedMonitor = getOrderedMonitors(userId, userObj.getOwnedBy(), false, attachSubscriptionInfo);
		if (orderedMonitor != null) {

			List<MonitorBucketForTitle> titleBucketForMonitors = new ArrayList<MonitorBucketForTitle>();
			userDetails.setTitlesForMonitorBuckets(titleBucketForMonitors);

			LinkedHashMap<String, List<MonitorInfo>> monitors = new LinkedHashMap<String, List<MonitorInfo>>();
			userDetails.setMonitors(monitors);

			// List<MonitorInfo> monitorList = fillMonitorDetails(userObj, orderedMonitor.favoriteMonitors);

			// if(monitorList != null && !monitorList.isEmpty()) {
			// titleBucketForMonitors.add(new MonitorBucketForTitle("favoriteMonitorList", "Favorite Monitors"));
			// monitors.put("favoriteMonitorList", monitorList);
			// }

			List<MonitorInfo> monitorList = fillMonitorDetails(userId, orderedMonitor.userMonitors);
			if (monitorList != null && !monitorList.isEmpty()) {
				titleBucketForMonitors.add(new MonitorBucketForTitle("myMonitors", "My Monitors"));
				monitors.put("myMonitors", monitorList);
			}

			if (orderedMonitor.grpIdVsTagsInfoList != null && !orderedMonitor.grpIdVsTagsInfoList.isEmpty()) {
				for (Map.Entry<Long, List<TagsInfo>> entry : orderedMonitor.grpIdVsTagsInfoList.entrySet()) {
					List<TagsInfo> list = entry.getValue();
					if (list != null && !list.isEmpty()) {
						monitorList = fillMonitorDetails(userId, list);
						String groupName = list.get(0).groupName;
						// String key = groupName + "_" + entry.getKey();
						String key = groupName;
						monitors.put(key, monitorList);
						titleBucketForMonitors.add(new MonitorBucketForTitle(key, groupName + " Monitors"));
					}
				}
			}
		}
		return userDetails;
	}

	private List<MonitorInfo> fillMonitorDetails(long userId, List<TagsInfo> monitorList) throws Exception {

		if (monitorList == null || monitorList.isEmpty()) {
			return null;
		}

		List<MonitorInfo> myMonitorList = new ArrayList<MonitorInfo>();
		for (TagsInfo tag : monitorList) {
			Tags monitor = tag.getTag();
			try {
				MonitorInfo md = new MonitorInfo();
				LOG.debug("Processing for Monitor Id " + monitor.getId());
				getMonitorDetails(userId, tag, md);
				myMonitorList.add(md);
			} catch (Exception e) {
				LOG.error("Error while generating monitor details for monitorid " + monitor.getId() + " due to " + e.getMessage(), e);
			}
		}
		return myMonitorList;
	}

	private void getMonitorDetails(long userId, TagsInfo tag, MonitorInfo md) {

		Tags monitor = tag.tag;
		md.setMonitorId(FRAPIConstant.MONITOR_PREFIX + monitor.getId());
		md.setMonitorName(monitor.getTagName());
		md.setFavorite(tag.favorite);
		md.setMonitorAdmin(tag.monitorAdmin);

		if (monitor.getOwnedByType() == OwnedByType.GROUP) {
			MailingList mailingList = tag.mailingList;
			if (mailingList != null && mailingList.getFlags() == MailingList.Flags.ACTIVE) {
				md.setMailAvailable(true);
			}
		} else {
			if (tag.emailSchedule != null) {
				EmailScheduleEnums statusCheck = tag.getEmailSchedule().getStatus();
				if (statusCheck.equals(EmailScheduleEnums.NEW) || statusCheck.equals(EmailScheduleEnums.UPDATED)
						|| statusCheck.equals(EmailScheduleEnums.ACTIVE)) {
					md.setMailAvailable(true);
				}
			}
		}

		if (tag.favoriteUserItemId != null) {
			md.setFavoriteUserItemId(Long.parseLong(tag.favoriteUserItemId));
		}
	}

	private OrderedMonitors getOrderedMonitors(long actorId, long groupId, boolean onlyGrpMonitorNeeded, boolean attachSubscriptionInfo)
			throws Exception {

		OrderedMonitors orderedMonitors = new OrderedMonitors();

		Set<Long> groupIdsWhereActorHasAdminPrivileges = new HashSet<Long>();
		Set<Long> groupIdsWhereActorHasDefaultPrivileges = new HashSet<Long>();
		// fetch user group map.
		getUserGroupMapByUserId(groupIdsWhereActorHasAdminPrivileges, groupIdsWhereActorHasDefaultPrivileges, actorId);

		// fetch all possible default user group mapping.
		Set<Long> allDefultGroupIds = new HashSet<Long>();
		allDefultGroupIds.addAll(groupIdsWhereActorHasAdminPrivileges);
		allDefultGroupIds.addAll(groupIdsWhereActorHasDefaultPrivileges);

		allDefultGroupIds.addAll(groupServiceRepository.getGroupIdsWhereActorHasDefaultprivileges(actorId, groupId, allDefultGroupIds));

		// fetch all possible admin group mapping.
		groupIdsWhereActorHasAdminPrivileges =
				groupServiceRepository.getGroupIdsWhereActorHasAdminprivileges(actorId, groupId, groupIdsWhereActorHasAdminPrivileges);

		// removing default group privileges which are also mapped with admin privileges.
		allDefultGroupIds.removeAll(groupIdsWhereActorHasAdminPrivileges);

		Set<Long> allGroupIds = new HashSet<Long>();
		if (!groupIdsWhereActorHasAdminPrivileges.isEmpty()) {
			allGroupIds.addAll(groupIdsWhereActorHasAdminPrivileges);
		}
		allGroupIds.addAll(allDefultGroupIds);

		List<Tags> groupTags = null;

		if (allGroupIds != null && !allGroupIds.isEmpty()) {
			groupTags = monitorServiceRepository.getTagsByOwner(OwnedByType.GROUP, FR_ArrayUtils.collectionToLongArray(allGroupIds));
		}

		Map<Groups, List<Tags>> groupsVsTagsInfoList = null;
		if (groupTags != null && !groupTags.isEmpty()) {
			groupsVsTagsInfoList = getMapOfGroupVsTags(allGroupIds, groupTags, groupId);
		}

		List<Tags> myMonitors = monitorServiceRepository.getTagsByOwner(OwnedByType.USER, actorId);

		if ((myMonitors == null || myMonitors.isEmpty()) && (groupTags == null || groupTags.isEmpty())) {
			return orderedMonitors;
		}

		// Users user = null;
		// if(attachSubscriptionInfo) {
		// user = service.getUserById(actorId);
		// }

		if (groupTags != null && !groupTags.isEmpty()) {
			orderedMonitors.grpIdVsTagsInfoList = new LinkedHashMap<Long, List<TagsInfo>>();
			for (Groups g : groupsVsTagsInfoList.keySet()) {
				List<Tags> tagsList = groupsVsTagsInfoList.get(g);
				List<TagsInfo> tagsInfoList = getOrderedTagInfoList(orderedMonitors, tagsList, null, g,
						groupIdsWhereActorHasAdminPrivileges, attachSubscriptionInfo, null);
				orderedMonitors.grpIdVsTagsInfoList.put(g.getId(), tagsInfoList);
			}
		}

		if (!onlyGrpMonitorNeeded) {
			orderedMonitors.userMonitors =
					getOrderedTagInfoList(orderedMonitors, myMonitors, null, null, null, attachSubscriptionInfo, null);
		}
		return orderedMonitors;
	}

	@Override
	public Set<Long> getAllGroupIdsOfUser(long actorId, long groupId) throws Exception {

		Set<Long> groupIdsWhereActorHasAdminPrivileges = new HashSet<Long>();
		Set<Long> groupIdsWhereActorHasDefaultPrivileges = new HashSet<Long>();
		// fetch user group map.
		getUserGroupMapByUserId(groupIdsWhereActorHasAdminPrivileges, groupIdsWhereActorHasDefaultPrivileges, actorId);

		// fetch all possible default user group mapping.
		Set<Long> allDefultGroupIds = new HashSet<Long>();
		allDefultGroupIds.addAll(groupIdsWhereActorHasAdminPrivileges);
		allDefultGroupIds.addAll(groupIdsWhereActorHasDefaultPrivileges);

		allDefultGroupIds.addAll(groupServiceRepository.getGroupIdsWhereActorHasDefaultprivileges(actorId, groupId, allDefultGroupIds));

		// fetch all possible admin group mapping.
		groupIdsWhereActorHasAdminPrivileges =
				groupServiceRepository.getGroupIdsWhereActorHasAdminprivileges(actorId, groupId, groupIdsWhereActorHasAdminPrivileges);

		// removing default group privileges which are also mapped with admin privileges.
		allDefultGroupIds.removeAll(groupIdsWhereActorHasAdminPrivileges);

		Set<Long> allGroupIds = new HashSet<Long>();
		if (!groupIdsWhereActorHasAdminPrivileges.isEmpty()) {
			allGroupIds.addAll(groupIdsWhereActorHasAdminPrivileges);
		}
		allGroupIds.addAll(allDefultGroupIds);

		return allGroupIds;
	}

	private List<TagsInfo> getOrderedTagInfoList(OrderedMonitors orderedMonitors, List<Tags> tagList,
			Map<Long, UserItem> monitorVsFavMarkTime, Groups group, Set<Long> groupIdsWhereActorHasAdminPrivileges,
			boolean attachSubscriptionInfo, Users user) {

		List<TagsInfo> tagInfoList = new ArrayList<TagsInfo>();

		try {
			if (tagList != null && !tagList.isEmpty()) {
				for (Tags tag : tagList) {
					TagsInfo tagInfo = new TagsInfo();
					tagInfo.tag = tag;

					if (tag.getOwnedByType() == OwnedByType.GROUP) {
						if (group != null) {
							tagInfo.groupName = group.getGroupName();
							tagInfo.groupType = group.getGroupType();
							if (groupIdsWhereActorHasAdminPrivileges.contains(group.getId())) {
								tagInfo.monitorAdmin = true;
							}
						}
					} else {
						tagInfo.monitorAdmin = true;
					}

					tagInfoList.add(tagInfo);
				}
			}
		} catch (Exception e) {
			LOG.error("Error while ordering monitor list " + e.getMessage(), e);
		}
		if (group != null) {
			MonitorOrderingUtils.sortGroupMonitorByOrderType(group, tagInfoList);
		} else {
			MonitorOrderingUtils.sortTagListByName(tagInfoList);
		}
		return tagInfoList;
	}

	private Map<Groups, List<Tags>> getMapOfGroupVsTags(Set<Long> groupIds, List<Tags> tagList, long parentId) {
		Map<Groups, List<Tags>> groupsVsTagsInfoList = null;
		List<Tags> parentTagList = new ArrayList<Tags>();
		Map<Long, Groups> groupIdVsGroup = new HashMap<Long, Groups>();
		if (tagList != null) {
			groupsVsTagsInfoList = new LinkedHashMap<Groups, List<Tags>>();
			List<Groups> groups = groupServiceRepository.getGroupsByIds(groupIds);
			groupServiceRepository.groupNameComparator(groups);
			Map<Long, List<Tags>> grpIdVsTagList = getOrderedMapOfIdVsObject(tagList);
			groupIdVsGroup = getMapOfGroupIdVsGroups(groups);
			for (Long gId : groupIdVsGroup.keySet()) {
				// add account group always at bottom of the list.
				if (gId == parentId) {
					List<Tags> list = grpIdVsTagList.get(gId);
					if (list != null) {
						parentTagList.addAll(list);
					}
					continue;
				}
				List<Tags> list = grpIdVsTagList.get(gId);
				if (list != null) {
					groupsVsTagsInfoList.put(groupIdVsGroup.get(gId), list);
				}
			}
			groupsVsTagsInfoList.put(groupIdVsGroup.get(parentId), parentTagList);
		}
		return groupsVsTagsInfoList;
	}

	private Map<Long, Groups> getMapOfGroupIdVsGroups(List<Groups> groups) {
		Map<Long, Groups> grpIdVsGroup = null;
		if (groups != null) {
			grpIdVsGroup = new LinkedHashMap<Long, Groups>();
			for (Groups g : groups) {
				grpIdVsGroup.put(g.getId(), g);
			}
		}
		return grpIdVsGroup;
	}

	private Map<Long, List<Tags>> getOrderedMapOfIdVsObject(List<Tags> tagList) {
		Map<Long, List<Tags>> grpIdVsTagsInfoList = null;
		try {
			if (tagList != null && !tagList.isEmpty()) {
				grpIdVsTagsInfoList = new HashMap<Long, List<Tags>>();
				for (Tags obj : tagList) {
					List<Tags> list = grpIdVsTagsInfoList.get(obj.getOwnedBy());
					if (list == null) {
						list = new ArrayList<Tags>();
						grpIdVsTagsInfoList.put(obj.getOwnedBy(), list);
					}
					list.add(obj);
				}
			}
		} catch (Exception e) {
			LOG.error("Error while creating map from list of object " + e.getMessage(), e);
		}
		return grpIdVsTagsInfoList;
	}

	private void getUserGroupMapByUserId(Set<Long> groupIdsWhereActorHasAdminPrivileges, Set<Long> groupIdsWhereActorHasDefaultPrivileges,
			long userId) throws Exception {
		List<UserGroupMap> userMap = userServiceRepository.getUserGroupMapByUserId(userId);
		if (userMap != null) {
			for (UserGroupMap map : userMap) {
				if (map.getFlags() != BaseItem.FLAGS.ACTIVE) {
					continue;
				}
				MembershipType type = map.getMembershipType();
				if (type.equals(MembershipType.ADMIN)) {
					groupIdsWhereActorHasAdminPrivileges.add(map.getGroupId());
				} else {
					groupIdsWhereActorHasDefaultPrivileges.add(map.getGroupId());
				}
			}
		}
	}

	@Override
	public MonitorAPIResponse removeMonitor(User user, long monitorId) throws Exception {
		long startTime = PerfMonitor.currentTime();
		try {
			MonitorAPIResponse res = new MonitorAPIResponse();

			Tags tag = monitorServiceRepository.getTagById(monitorId);

			if (tag == null || FLAGS.DELETED.equals(tag.getFlags())) {
				res.setStatusCode(StatusCode.ENTITY_NOT_FOUND);
				return res;
			}
            long frUserId = Long.parseLong(user.getUserId());
            if (tag.getOwnedBy() != frUserId) {
                MembershipType type = UserMembership.retrieveMembershipType(userServiceRepository, frUserId);
                if (!MembershipType.ADMIN.equals(type)) {
                    res.setStatusCode(StatusCode.INSUFFICIENT_PRIVILEGE);
                    return res;
                }
                Set<Long> allGroupIds = getAllGroupIdsOfUser(frUserId, user.getOwnedBy());
                if (TagsValidator.isInsufficientPrivilege(tag, allGroupIds, res)) {
                    return res;
                }
            }
			monitorServiceRepository.removeMonitor(tag);

			res.setStatusCode(StatusCode.REQUEST_SUCCESS);
			res.setMonitorName(tag.getTagName());
			res.setMonitorId(tag.getId());
			return res;
		} catch (Exception e) {
			LOG.error("Error while removing monitor :" + monitorId + " for userId :" + user.getUserId(), e);
			throw e;
		} finally {
			PerfMonitor.recordStats(startTime, PerfActivityType.ReqTime, "removeMonitor");
		}
	}

	@Override
	public MonitorAPIResponse createMonitor(long frUserId, String monitorName, List<String> entityListParam, List<String> filtersToAddList,
			EnterprisePref enterprisePref) throws Exception {

		List<String> entityList = entityListParam;
		long startTime = PerfMonitor.currentTime();
		try {
			MonitorAPIResponse res = new MonitorAPIResponse();
			entityList = removeGarbage(entityList);

			if (entityList.isEmpty()) {
				res.setStatusCode(StatusCode.ILLEGAL_ARGUMENT);
				return res;
			}

			/* Restrict no. of searches on the basis of enterprise configuration */
			if (enterprisePref.getSearchesPerMonitor() > 0 && entityList.size() > enterprisePref.getSearchesPerMonitor()) {
				res.setStatusCode(StatusCode.MONITOR_SEARCH_LIMIT_EXCEEDED);
				return res;
			}

			Users user = userServiceRepository.getUserById(frUserId);
			Tags duplicateTag = monitorServiceRepository.getTagsByOwnerAndTagName(user.getId(), OwnedByType.USER, monitorName.trim());

			if (duplicateTag != null) {
				res.setStatusCode(StatusCode.MONITOR_ALREADY_EXISTS);
				res.setMonitorName(duplicateTag.getTagName());
				res.setMonitorId(duplicateTag.getId());
				return res;
			}

			// After below call, entityList contains only single token entities, but these need to be validated on entity info cache.
			List<List<String>> entityCategoryList = categorizeEntities(entityList);
			List<String> keywordSearches = entityCategoryList.get(0);
			List<String> invalidEntities = entityCategoryList.get(1);
			List<String> keywordSearchesName = entityCategoryList.get(2);

			LOG.debug("invalid entities...initial list: " + invalidEntities);
			LOG.debug("keyword searches: " + keywordSearches);
			LOG.debug("keyword searches Name: " + keywordSearchesName);

			// This call populates invalid entities in 'invalidEntities' and returns valid entities in separate list
			List<Entity> validEntityList = monitorServiceRepository.validateTokensUsingEntityCache(entityList, invalidEntities);

			LOG.debug("valid entities: " + validEntityList);
			LOG.debug("invalid entities...final list: " + invalidEntities);

			if ((validEntityList == null || validEntityList.isEmpty()) && keywordSearches.isEmpty()) {
				res.setStatusCode(StatusCode.ILLEGAL_ARGUMENT);
				return res;
			}

			List<List<String>> keywordsAndFiltersList = null;
			List<String> keywords = null;

			if (filtersToAddList != null && !filtersToAddList.isEmpty()) {

				filtersToAddList = removeGarbage(filtersToAddList);
				keywordsAndFiltersList = servicesAPIUtil.getKeywordsAndFilters(filtersToAddList);
				keywords = keywordsAndFiltersList.get(0);
				filtersToAddList = keywordsAndFiltersList.get(1);
			}

			LOG.debug("advanced filters:" + filtersToAddList);
			LOG.debug("keywords:" + keywords);

			// Step 1 : create MonitorWizardFilters object for new monitor. Load Adv. filters list and keywords.
			MonitorWizardFilters data = new MonitorWizardFilters();
			String keywordsString = servicesAPIUtil.prepareFq(keywords);
			data.advanced = new Advanced(keywordsString, filtersToAddList);

			Tags tag = monitorServiceRepository.createMonitor(monitorName.trim(), validEntityList, keywordSearches, keywordSearchesName,
					data, user);

			res.setStatusCode(StatusCode.REQUEST_SUCCESS);
			res.setMonitorName(tag.getTagName());
			res.setMonitorId(tag.getId());
			return res;
		} catch (Exception e) {
			LOG.error("Error while creating monitor with name :" + monitorName + " for userId :" + frUserId, e);
			throw e;
		} finally {
			PerfMonitor.recordStats(startTime, PerfActivityType.ReqTime, "createMonitor");
		}
	}

	private Entity getValidEntity(List<Entity> validEntityList, String entityToCheck) {

		if (validEntityList == null) {
			return null;
		}

		for (Entity entity : validEntityList) {
			if (entity.getSearchToken().equals(entityToCheck)) {
				return entity;
			}
		}
		return null;
	}


	/*
	 * This method returns keywordSearches and invalid entities and removes these from i/p param entityList After this method execution,
	 * input param entityList contains only single token entities, but these need to be validated on entity solr.
	 */
	private List<List<String>> categorizeEntities(List<String> entityList) throws Exception {

		List<List<String>> entityCategoryList = new ArrayList<List<String>>();

		List<String> keywordSearches = new ArrayList<String>();
		List<String> invalidEntities = new ArrayList<String>();
		List<String> keywordSearchesName = new ArrayList<String>();

		for (Iterator<String> itr = entityList.iterator(); itr.hasNext();) {

			String entity = itr.next();

			if (entity.indexOf(":") == -1) {// cases : google, google AND microsoft
				keywordSearches.add(entity);
				keywordSearchesName.add(prepareSearchName(entity));
				itr.remove();
			} else if (entity.indexOf(":") != -1 && entity.indexOf(" ") != -1) {// cases: C:Apple AND C:Google, C:Apple AND microsoft AND
																				// C:Google

				List<String> tempList = FR_ArrayUtils.getListBySplitString(entity, " ");
				List<String> tokens = new ArrayList<String>();
				List<Entity> validatedTokens = validateAllTokensInKeywordSearch(tokens, tempList);

				if (validatedTokens != null && validatedTokens.size() == tokens.size()) {

					// replace old tokens in input entity with new tokens
					entity = prepareEntityWithNewTokens(entity, tokens, validatedTokens);
					keywordSearches.add(entity);

					// prepare search name
					StringBuilder searchName = new StringBuilder();
					// ==============================
					// for(String token : tokens) {
					// for(Entity validEntity : validatedTokens) {
					// if(validEntity.getSearchToken().equals(token)) {
					// int diff = Items.SEARCH_NAME_MAX_LENGTH - searchName.length();
					// if(validEntity.getName().length() > diff) {
					// break;
					// }
					// searchName.append(validEntity.getName()).append(", ");
					// break;
					// }
					// }
					// }

					for (Entity validatedToken : validatedTokens) {
						int diff = Items.SEARCH_NAME_MAX_LENGTH - searchName.length();
						if (validatedToken.getName().length() > diff) {
							break;
						}
						searchName.append(validatedToken.getName()).append(", ");
					}

					appendSearchName(tempList, searchName); 
					
					keywordSearchesName.add(searchName.toString());
				} else {
					invalidEntities.add(entity);
				}
				itr.remove();
			}
		}

		entityCategoryList.add(keywordSearches);
		entityCategoryList.add(invalidEntities);
		entityCategoryList.add(keywordSearchesName);
		return entityCategoryList;
	}

	private String prepareEntityWithNewTokens(String entity, List<String> tokens, List<Entity> validatedTokens) {
		List<String> tempList = FR_ArrayUtils.getListBySplitString(entity, " ");

		int i = 0;
		for (String token : tokens) {
			int index = tempList.indexOf(token);
			tempList.set(index, validatedTokens.get(i).getSearchToken());
			i++;
		}

		// prepare string
		StringBuffer sb = new StringBuffer();
		for (String s : tempList) {
			sb.append(s).append(" ");
		}
		return sb.toString().trim();
	}

	private String prepareSearchName(String entity) {
		StringBuilder searchName = new StringBuilder();
		List<String> tempList = FR_ArrayUtils.getListBySplitString(entity, " ");
		for (Iterator<String> itrTemp = tempList.iterator(); itrTemp.hasNext();) {
			String temp = itrTemp.next();
			if (temp.equalsIgnoreCase("OR") || temp.equalsIgnoreCase("AND") || temp.equalsIgnoreCase("NOT")) {
				itrTemp.remove();
			}
		}
		appendSearchName(tempList, searchName); 
		
		return searchName.toString().trim();
	}
 
	private void appendSearchName(final List<String> tempList, final StringBuilder searchName) { 
		for (String keyword : tempList) { 
			int diff = Items.SEARCH_NAME_MAX_LENGTH - searchName.length(); 
			if (keyword.length() > diff) { 
				break; 
			} 
			searchName.append(keyword).append(", "); 
		} 
		// ============================= 
		 
		if (searchName.length() >= 2) { 
			searchName.setLength(searchName.length() - 2); 
		} 
	} 
	

	private List<Entity> validateAllTokensInKeywordSearch(List<String> tokens, List<String> tempList) {

		for (Iterator<String> itrTemp = tempList.iterator(); itrTemp.hasNext();) {
			String temp = itrTemp.next();
			if (temp.indexOf(":") != -1) {
				tokens.add(temp);
				itrTemp.remove();
			} else if (temp.equalsIgnoreCase("OR") || temp.equalsIgnoreCase("AND") || temp.equalsIgnoreCase("NOT")) {
				itrTemp.remove();
			}
		}

		return monitorServiceRepository.validateTokensUsingEntityCache(tokens, null);
	}

	/* Remove null/empty strings from input. Trim remaining strings. */
	private List<String> removeGarbage(List<String> list) {
		List<String> newList = new ArrayList<String>();
		for (Iterator<String> itr = list.iterator(); itr.hasNext();) {
			String filter = itr.next();
			if (filter != null && !filter.trim().isEmpty()) {
				newList.add(filter.trim());
			}
		}
		return newList;
	}
}
