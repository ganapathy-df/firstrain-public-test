package com.firstrain.frapi.repository.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.firstrain.common.db.jpa.PersistenceProvider;
import com.firstrain.common.db.jpa.Transaction;
import com.firstrain.db.api.EmailScheduleDbAPI;
import com.firstrain.db.api.EmailSearchScheduleMapDbAPI;
import com.firstrain.db.api.EmailUserSearchDbAPI;
import com.firstrain.db.api.ItemsDbAPI;
import com.firstrain.db.api.MailingListDbAPI;
import com.firstrain.db.api.SearchMapDbAPI;
import com.firstrain.db.api.TagsDbAPI;
import com.firstrain.db.api.TagsItemsMapDbAPI;
import com.firstrain.db.obj.BaseItem.FLAGS;
import com.firstrain.db.obj.BaseItem.OwnedByType;
import com.firstrain.db.obj.BaseItem.RefersToType;
import com.firstrain.db.obj.EmailSchedule;
import com.firstrain.db.obj.EmailSchedule.EmailScheduleEnums;
import com.firstrain.db.obj.EmailSearchScheduleMap;
import com.firstrain.db.obj.EmailUserSearch;
import com.firstrain.db.obj.EmailUserSearch.EmailSearchEnums;
import com.firstrain.db.obj.Items;
import com.firstrain.db.obj.MailingList;
import com.firstrain.db.obj.SearchMap;
import com.firstrain.db.obj.TagItemsMap;
import com.firstrain.db.obj.TagItemsMap.ItemType;
import com.firstrain.db.obj.Tags;
import com.firstrain.db.obj.Tags.SearchOrderType;
import com.firstrain.db.obj.Tags.TagType;
import com.firstrain.db.obj.UserAndGroupRelation;
import com.firstrain.db.obj.Users;
import com.firstrain.db.util.Util;
import com.firstrain.frapi.obj.MonitorWizardFilters;
import com.firstrain.frapi.pojo.Entity;
import com.firstrain.frapi.repository.MonitorServiceRepository;
import com.firstrain.frapi.util.ConvertUtil;
import com.firstrain.frapi.util.MonitorUtil;
import com.firstrain.frapi.util.ServicesAPIUtil;
import com.firstrain.obj.IEntityInfo;
import com.firstrain.obj.IEntityInfoCache;
import com.firstrain.solr.client.SearchResult;
import com.firstrain.solr.client.SearchSpec;
import com.firstrain.solr.client.SearchSpec.CfqMode;
import com.firstrain.solr.client.SearchTokenEntry;
import com.firstrain.solr.client.util.SolrServerReader;
import com.firstrain.utils.JSONUtility;

@Service
@Qualifier("monitorServiceRepositoryImpl")
public class MonitorServiceRepositoryImpl extends EntityBaseServiceRepositoryImpl implements MonitorServiceRepository {

	private final Logger LOG = Logger.getLogger(MonitorServiceRepositoryImpl.class);
	@Autowired
	private ServicesAPIUtil servicesAPIUtil;
	@Autowired
	private MonitorUtil monitorUtil;
	@Autowired
	private ConvertUtil convertUtil;

	@Override
	public Tags getTagById(long tagId) throws Exception {

		Transaction txn = null;
		try {
			txn = PersistenceProvider.newTxn(PersistenceProvider.EMAIL_DATABASE_READ);
			Tags tag = TagsDbAPI.getTagById(txn, tagId);

			// fallback on write DB
			if (tag == null) {

				if (txn != null) {
					txn.close();
				}

				txn = PersistenceProvider.newTxn(PersistenceProvider.EMAIL_DATABASE);
				tag = TagsDbAPI.getTagById(txn, tagId);
				if (tag != null) {
					LOG.debug("Monitor " + tagId + " not available in Read DB, so fetched from Write DB");
				} else {
					LOG.debug("Monitor " + tagId + " not available in both Read/Write DBs");
				}
			}

			return tag;
		} finally {
			if (txn != null) {
				txn.close();
			}
		}
	}


	@Override
	public List<TagItemsMap> getTagsItemsMapByTagID(long tagId) {
		List<TagItemsMap> tagItemsMapList = TagsItemsMapDbAPI.getTagsItemsMapByTagID(PersistenceProvider.EMAIL_DATABASE_READ, tagId);

		if (tagItemsMapList == null || tagItemsMapList.isEmpty()) {
			tagItemsMapList = TagsItemsMapDbAPI.getTagsItemsMapByTagID(PersistenceProvider.EMAIL_DATABASE, tagId);

			if (tagItemsMapList == null || tagItemsMapList.isEmpty()) {
				LOG.debug("Tag-Item mapping for monitor " + tagId + " not available in LIST_ITEM_MAP table of both Read/Write DBs");
			} else {
				LOG.debug("Tag-Item mapping for monitor " + tagId
						+ " not available in LIST_ITEM_MAP table of Read DB, so fetched from Write DB");
			}
		}

		return tagItemsMapList;
	}

	@Override
	public void removeEntities(Tags tag, List<Long> entityIdList) throws Exception {

		long scheduleId = tag.getEmailId();
		Transaction txn = null;

		try {
			txn = PersistenceProvider.newTxn(PersistenceProvider.EMAIL_DATABASE);
			this.deleteItemsTagMapByTagAndItemID(txn, tag.getId(), entityIdList);

			for (long itemId : entityIdList) {
				if (scheduleId > 0) {
					SearchMap searchMap = this.getSearchMapByUISearchIDAndTagID(itemId, tag.getId());
					if (searchMap != null) {
						this.deleteSearchMap(txn, searchMap.getId());
						this.deleteSearchFromEmailSchedule(txn, scheduleId, searchMap.getMailSearchID());
						this.updateEmailSearchStatus(txn, searchMap.getMailSearchID(), EmailSearchEnums.DELETED);
					}
				}
			}

			txn.commit();
		} catch (Exception e) {
			if (txn != null) {
				txn.rollback();
			}
			throw e;
		} finally {
			if (txn != null) {
				txn.close();
			}
		}
	}

	@Override
	public void deleteItemsTagMapByTagAndItemID(Transaction txn, long tagID, List<Long> entityIdList) {
		TagsItemsMapDbAPI.deleteItemsTagMapByTagAndItemID(txn, tagID, entityIdList);
	}

	@Override
	public SearchMap getSearchMapByUISearchIDAndTagID(long uiSearchId, long tagId) {
		SearchMap searchMap = SearchMapDbAPI.getSearchMapByUISearchIDAndTagID(PersistenceProvider.EMAIL_DATABASE_READ, uiSearchId, tagId);

		if (searchMap == null) {
			searchMap = SearchMapDbAPI.getSearchMapByUISearchIDAndTagID(PersistenceProvider.EMAIL_DATABASE, uiSearchId, tagId);

			if (searchMap == null) {
				LOG.debug("Search map for monitor " + tagId + " and search " + uiSearchId
						+ " not available in SEARCH_MAP table of both Read/Write DBs");
			} else {
				LOG.debug("Search map for monitor " + tagId + " and search " + uiSearchId
						+ " not available in SEARCH_MAP table of Read DB, so fetched from Write DB");
			}
		}

		return searchMap;
	}

	@Override
	public void deleteSearchMap(Transaction txn, long id) {
		SearchMapDbAPI.deleteSearchMap(txn, id);
	}

	@Override
	public void deleteSearchFromEmailSchedule(Transaction txn, long scheduleId, long searchId) throws Exception {
		EmailSearchScheduleMapDbAPI.deleteSearchFromEmailSchedule(txn, scheduleId, searchId);
	}

	@Override
	public void updateEmailSearchStatus(Transaction txn, long searchId, EmailSearchEnums status) throws Exception {
		EmailUserSearchDbAPI.updateEmailSearchStatus(txn, searchId, status);
	}

	@Override
	public void updateTagFiltersAndSearches(long frUserId, Tags tag, String filteredString, MonitorWizardFilters oldData,
			MonitorWizardFilters finalData) throws Exception {
		Transaction txn = null;
		try {
			UserAndGroupRelation.setUserAndGroupRelation(new UserAndGroupRelation(frUserId, null));
			txn = PersistenceProvider.newTxn(PersistenceProvider.EMAIL_DATABASE);
			TagsDbAPI.updateTagFilters(txn, tag.getId(), filteredString);

			long scheduleId = tag.getEmailId();
			List<Items> itemList = this.getItemsByTagId(tag.getId());

			if (itemList != null && !itemList.isEmpty()) {
				for (Items item : itemList) {
					String data = item.getData();
					if (data == null || data.isEmpty()) {
						continue;
					}

					Map<String, List<String>> map = servicesAPIUtil.prepareQueryMap(data);

					String finalQ = null;
					List<String> q = map.remove("q");
					if (q != null && !q.isEmpty()) {
						String query = q.get(0).trim();
						finalQ = servicesAPIUtil.prepareQForAddOrRemoveFilter(query, oldData, finalData);
					}

					// verify prepareFq method
					String finalFq = servicesAPIUtil.prepareFq(finalData.advanced.advancedFilters);

					String scope = "";
					List<String> scopeList = map.remove("scope");
					if (scopeList != null && !scopeList.isEmpty()) {
						scope = scopeList.get(0).trim();
					}

					// prepare data to persist to DB
					int scopeInt = 0;
					try {
						scopeInt = Integer.parseInt(scope);
					} catch (Exception e) {
					}

					String newData = getData(finalQ, finalFq, scopeInt);

					// update in DB
					if (!data.equals(newData)) {
						ItemsDbAPI.updateItems(txn, item.getId(), item.getName(), newData);

						if (scheduleId > 0) {
							SearchMap searchMap = this.getSearchMapByUISearchIDAndTagID(item.getId(), tag.getId());
							if (searchMap != null) {
								EmailUserSearch emailUserSearch = this.fetchEmailUserSearchById(txn, searchMap.getMailSearchID());
								emailUserSearch.setQuery(newData);
								this.updateEmailUserSearch(txn, emailUserSearch);
							}
						}
					}
				}
			}

			txn.commit();
		} catch (Exception e) {
			if (txn != null) {
				txn.rollback();
			}
			throw e;
		} finally {
			if (txn != null) {
				txn.close();
			}
		}
	}

	@Override
	public void updateEmailUserSearch(Transaction txn, EmailUserSearch emailUserSearch) throws Exception {
		EmailUserSearchDbAPI.updateEmailUserSearch(txn, emailUserSearch);
	}

	@Override
	public EmailUserSearch fetchEmailUserSearchById(Transaction txn, long emailUserSearchId) throws Exception {
		return EmailUserSearchDbAPI.fetchEmailUserSearchById(txn, emailUserSearchId);
	}

	@Override
	public List<Items> getItemsByTagId(long tagId) {
		List<Items> itemsList = ItemsDbAPI.getItemsByTagId(PersistenceProvider.EMAIL_DATABASE_READ, tagId, null, -1, 0);

		if (itemsList == null || itemsList.isEmpty()) {

			itemsList = ItemsDbAPI.getItemsByTagId(PersistenceProvider.EMAIL_DATABASE, tagId, null, -1, 0);
			if (itemsList == null || itemsList.isEmpty()) {
				LOG.debug("Items for monitor " + tagId + " not available in both Read/Write DBs");
			} else {
				LOG.debug("Items for monitor " + tagId + " not available in Read DB, so fetched from Write DB");
			}
		}

		return itemsList;
	}

	@Override
	public List<Entity> validateTokens(List<String> tokenList) throws Exception {

		if (tokenList == null || tokenList.isEmpty()) {
			return null;
		}

		int start = 0;
		int rows = tokenList.size();
		String[] fields = new String[] {"name", "attrCatId", "attrSearchToken", "attrDim", "searchScope"};

		String filterQuery = getQueryStringFromCollection(tokenList, " ");
		String query = "attrSearchToken:(" + filterQuery + ")";
		LOG.debug("Entity Solr query:" + query);
		SolrDocumentList enitytSolrDocList =
				SolrServerReader.retrieveNSolrDocs(this.getEntitySolrServer(), query, start, rows/* , "dayId", false */, fields);
		List<Entity> entityList = new ArrayList<Entity>();
		if (enitytSolrDocList != null && !enitytSolrDocList.isEmpty()) {
			LOG.debug("enitytSolrDocList found : " + enitytSolrDocList.size());
			for (SolrDocument enitytSolrDoc : enitytSolrDocList) {
				Entity entity = new Entity();
				String entityName = (String) enitytSolrDoc.getFieldValue("name");
				String entityCatId = (String) enitytSolrDoc.getFieldValue("attrCatId");
				String searchToken = (String) enitytSolrDoc.getFieldValue("attrSearchToken");
				String dimension = (String) enitytSolrDoc.getFieldValue("attrDim");
				Short scopeObj = (Short) enitytSolrDoc.getFieldValue("searchScope");
				entity.setName(entityName);
				entity.setDimension(dimension);
				entity.setId(entityCatId);
				entity.setSearchToken(searchToken);
				if (scopeObj != null) {
					entity.setScope(scopeObj);
				}
				entityList.add(entity);
			}
		}
		return entityList;
	}

	@Override
	public List<Entity> validateTokensUsingEntityCache(List<String> tokenList, List<String> invalidEntities) {

		if (tokenList == null || tokenList.isEmpty()) {
			return null;
		}

		List<Entity> entityList = new ArrayList<Entity>();
		IEntityInfoCache entityInfoCache = this.getEntityInfoCache();

		for (String token : tokenList) {
			IEntityInfo entityInfo = entityInfoCache.searchTokenToEntity(token.trim());
			if (entityInfo != null) {
				Entity entity = convertUtil.convertEntityInfo(entityInfo);
				entityList.add(entity);
			} else if (invalidEntities != null) {
				invalidEntities.add(token);
			}
		}
		return entityList;
	}

	public String getQueryStringFromCollection(Collection<?> values, String del) {
		if (values == null || values.isEmpty()) {
			return null;
		}
		StringBuilder str = new StringBuilder();
		for (Object value : values) {
			str.append("\"" + value + "\"").append(del);
		}
		return str.substring(0, str.length() - del.length());
	}

	@Override
	public void addEntities(Tags tagParam, List<Entity> validEntityList, List<String> keywordSearches, List<String> keywordSearchesName)
			throws Exception {

		Tags tag = tagParam;
		Transaction txn = null;

		try {
			MonitorWizardFilters data = JSONUtility.deserialize(tag.getFilterString(), MonitorWizardFilters.class);

			String fq = null;
			if (data.advanced != null && data.advanced.advancedFilters != null) {
				fq = servicesAPIUtil.prepareFq(data.advanced.advancedFilters);
			}

			String keywords = null;
			if (data.advanced != null && data.advanced.keywords != null) {
				keywords = data.advanced.keywords.trim();
			}

			txn = PersistenceProvider.newTxn(PersistenceProvider.EMAIL_DATABASE);

			UserAndGroupRelation userAndGroupRelation = new UserAndGroupRelation(tag.getOwnedBy(), "");
			UserAndGroupRelation.setUserAndGroupRelation(userAndGroupRelation);

			List<Items> itemList = new ArrayList<Items>();

			if (validEntityList != null && !validEntityList.isEmpty()) {
				for (Entity entity : validEntityList) {
					// Items item = getItem(entity.getSearchToken(), null, tag.getOwnedBy(), tag.getOwnedByType());
					String query = entity.getSearchToken();
					if (keywords != null && !keywords.isEmpty()) {
						query += " " + keywords;
					}
					String queryData = getData(query, fq, entity.getScope());
					Items item = ItemsDbAPI.addItem(txn, entity.getName(), queryData, Items.Type.Search, -1, -1, -1, RefersToType.USER,
							tag.getOwnedBy(), tag.getOwnedByType(), Items.STATUS_HISTORY_DISABLED);

					itemList.add(item);
				}
			}

			if (keywordSearches != null && !keywordSearches.isEmpty()) {
				for (int i = 0; i < keywordSearches.size(); i++) {
					// Items item = getItem(keywordSearch, null, tag.getOwnedBy(), tag.getOwnedByType());
					String query = keywordSearches.get(i);
					if (keywords != null && !keywords.isEmpty()) {
						query += " " + keywords;
					}
					String queryData = getData(query, fq, -1);

					Items item = ItemsDbAPI.addItem(txn, keywordSearchesName.get(i), queryData, Items.Type.Search, -1, -1, -1,
							RefersToType.USER, tag.getOwnedBy(), tag.getOwnedByType(), Items.STATUS_HISTORY_DISABLED);

					itemList.add(item);
				}
			}

			addSearchesToTagAndSchedule(txn, tag, itemList);
			tag = txn.fetch(tag.getId(), Tags.class);
			tag.setVersion(tag.getVersion() + 1);
			txn.commit();
		} catch (Exception e) {
			if (txn != null) {
				txn.rollback();
			}
			throw e;
		} finally {
			if (txn != null) {
				txn.close();
			}
		}
	}

	public String getData(String q, String fqParam, int scope) {
		String fq = fqParam;
		if (q == null) {
			return null;
		}
		q = q.trim();
		String data = "q=" + q;
		if (fq != null && !fq.isEmpty()) {
			fq = fq.trim();
			data += "&fq=" + fq;
		}
		if (scope > 0) {
			data += "&scope=" + scope;
		} else {
			data += "&scope=2";
		}
		return data;
	}

	private void addSearchesToTagAndSchedule(Transaction txn, Tags tag, List<Items> itemList) throws Exception {
		SearchOrderType orderType = tag.getSearchOrderType();
		if (orderType == SearchOrderType.CUSTOM) { // just append
			List<TagItemsMap> tagsItemsMaps = this.getTagsItemsMapByTagID(tag.getId());
			int order = tagsItemsMaps != null ? tagsItemsMaps.size() : 0;
			for (Items item : itemList) {
				addTagMapping(txn, tag, ++order, item);
			}
		} else {
			List<Items> folderItems = this.getItemsByTagId(tag.getId());
			if (folderItems == null) {
				folderItems = new ArrayList<Items>();
			}
			folderItems.addAll(itemList);
			List<Long> orderedSearches = monitorUtil.getOrderedSearches(folderItems, orderType, tag);
			for (Items item : itemList) {
				int order = orderedSearches.indexOf(item.getId()) + 1;
				addTagMapping(txn, tag, order, item);
				folderItems.remove(item);
			}
			if (!folderItems.isEmpty()) {
				updateOrder(txn, tag, orderedSearches);
			}
		}
	}

	private void updateOrder(Transaction txn, Tags tag, List<Long> orderedSearches) throws Exception {

		List<TagItemsMap> tagMapList = this.getTagsItemsMapByTagID(tag.getId());
		for (TagItemsMap map : tagMapList) {
			int order = orderedSearches.indexOf(map.getItemId()) + 1;
			map.setOrder(order);
			TagsItemsMapDbAPI.updateItemsTagSetOrder(txn, order, map.getId());

			if (tag.getEmailId() > 0) {
				SearchMap searchMap = this.getSearchMapByUISearchIDAndTagID(map.getItemId(), tag.getId());
				if (searchMap != null) {
					EmailUserSearch emailUserSearch = EmailUserSearchDbAPI.fetchEmailUserSearchById(txn, searchMap.getMailSearchID());
					emailUserSearch.setOrder(order);
					EmailUserSearchDbAPI.updateEmailUserSearch(txn, emailUserSearch);
				} else {
					LOG.debug("No mapping found for ui search " + map.getItemId());
				}
			}
		}
	}

	public void addTagMapping(Transaction txn, Tags tag, int order, Items item) throws Exception {
		TagItemsMap tagsItemsMap = new TagItemsMap();
		tagsItemsMap.setItemId(item.getId());
		tagsItemsMap.setTagId(tag.getId());
		tagsItemsMap.setOrder(order);
		tagsItemsMap.setItemType(ItemType.ITEM);
		Util.setBaseItemInfo(tagsItemsMap, tag.getInsertedBy(), tag.getUpdatedBy(), tag.getOwnedBy(), tag.getOwnedByType());
		TagsItemsMapDbAPI.persistItemsTagSet(txn, tagsItemsMap);

		if (tag.getEmailId() > 0) {
			EmailSchedule es = EmailScheduleDbAPI.fetchEmailScheduleById(txn, tag.getEmailId());
			addSearchToSchedule(txn, es, item, order, tag);
		}
	}

	public void addSearchToSchedule(Transaction txn, EmailSchedule schedule, Items item, int order, Tags tag) throws Exception {
		String query = item.getData();
		if (query.isEmpty()) {
			LOG.debug("Search query is empty for search :" + item.getId());
			return;
		}
		EmailUserSearch search = new EmailUserSearch();
		search.setSearchName(item.getName());
		search.setQuery(query);
		search.setUserId(schedule.getUserId());
		EmailScheduleEnums status = schedule.getStatus();
		if (status == EmailScheduleEnums.ACTIVE || status == EmailScheduleEnums.UPDATED || status == EmailScheduleEnums.NEW) {
			search.setStatus(EmailSearchEnums.ACTIVE);
		} else {
			search.setStatus(EmailSearchEnums.INACTIVE);
		}
		search.setOrder(order);
		search.setType(EmailUserSearch.TYPE_PRIMARY);
		EmailUserSearchDbAPI.persistEmailUserSearch(txn, search);
		/* create the mappings between the UI search and email search and email search and the Schedule */
		SearchMap searchMap = new SearchMap();
		searchMap.setUiSearchID(item.getId());
		searchMap.setMailSearchID(search.getId());
		searchMap.setTagID(tag.getId());
		SearchMapDbAPI.persistSearchMap(txn, searchMap);

		EmailSearchScheduleMapDbAPI.addSearchToEmailSchedule(txn, schedule.getId(), search.getId());
	}

	public Items getItem(String token, String fq, long ownedBy, OwnedByType ownedByType) throws Exception {

		SearchResult sr = new SearchResult();

		SearchSpec searchSpec = new SearchSpec();
		searchSpec.useLikelySearchIntention = true;
		searchSpec.setCfqMode(CfqMode.PREFERRED);
		searchSpec.q = token.trim();
		if (fq != null) {
			searchSpec.fq = fq.trim();
		}
		this.getSearcher().parseSearchSpec(searchSpec, sr);

		Items item = new Items();
		item.setName(getSearchName(sr));
		// item.setData(getSearchQuery(sr));
		item.setType(Items.Type.Search);
		item.setDocGroupId(-1);
		item.setResultSize((int) sr.total);
		item.setRefersTo(-1);
		item.setRefersToType(RefersToType.USER);
		item.setStatus(Items.STATUS_HISTORY_DISABLED);
		item.setOwnedBy(ownedBy);
		item.setOwnedByType(ownedByType);
		item.setFlags(FLAGS.ACTIVE);
		return item;
	}

	private String getSearchName(SearchResult sr) {
		StringBuilder searchName = new StringBuilder();
		for (SearchTokenEntry tkn : sr.parsedQ) {
			int diff = Items.SEARCH_NAME_MAX_LENGTH - searchName.length();
			String name = tkn.name != null ? tkn.name : tkn.searchToken;
			if (name.length() > diff) {
				break;
			}
			searchName.append(name).append(", ");
		}
		searchName.setLength(searchName.length() - 2);
		return searchName.toString();
	}

	@Override
	public List<Tags> getTagsByOwner(OwnedByType ownerType, long... ownerIds) {
		List<Tags> tagList = TagsDbAPI.getTagsByOwner(PersistenceProvider.EMAIL_DATABASE_READ, ownerType, ownerIds);

		if (tagList == null || tagList.isEmpty()) {
			tagList = TagsDbAPI.getTagsByOwner(PersistenceProvider.EMAIL_DATABASE, ownerType, ownerIds);

			if (tagList == null || tagList.isEmpty()) {
				LOG.debug("No monitors available in LIST table of both Read/Write DBs");
			} else {
				LOG.debug("Monitors not available in LIST table of Read DB, so fetched from Write DB");
			}
		}

		return tagList;
	}

	@Override
	public Tags getTagsByOwnerAndTagName(long ownerId, OwnedByType ownerType, String tagName) {
		// this call is directly on write DB...as there is high probability of monitor not being available...so avoiding both R/W DB hits
		return TagsDbAPI.getTagsByOwnerAndTagName(PersistenceProvider.EMAIL_DATABASE, ownerId, ownerType, tagName);
	}

	@Override
	public void removeMonitor(Tags tag) throws Exception {
		Transaction txn = null;
		try {
			txn = PersistenceProvider.newTxn(PersistenceProvider.EMAIL_DATABASE);
			long tagId = tag.getId();

			TagsItemsMapDbAPI.deleteItemsTagMapByTagID(txn, tagId);
			if (tag.getEmailId() > -1) {
				// here not using cached emailSchedule, since that may have non synced data
				EmailSchedule emailSchedule = EmailScheduleDbAPI.fetchEmailScheduleById(txn, tag.getEmailId());
				String _emailName = emailSchedule.getEmailName() + "_" + new Timestamp(System.currentTimeMillis());
				if (_emailName.length() > 50) {
					int lenExceeds = _emailName.length() - 50;
					_emailName = emailSchedule.getEmailName().substring(0, (emailSchedule.getEmailName().length() - lenExceeds)) + "_"
							+ new Timestamp(System.currentTimeMillis());
				}
				emailSchedule.setEmailName(_emailName);
				emailSchedule.setStatus(EmailScheduleEnums.MARKED_FOR_DELETION);
				EmailScheduleDbAPI.updateEmailSchedule(txn, emailSchedule);
				updateSearchStatusByEmailScheduleId(txn, tag.getEmailId(), EmailSearchEnums.DELETED);
				EmailSearchScheduleMapDbAPI.deleteEmailSearchScheduleMapByScheduleId(txn, emailSchedule.getId());
				SearchMapDbAPI.deleteSearchMapByTagID(txn, tagId);
				// For group folder deletion we have to delete the email-schedule from mailing_list
				List<MailingList> mailingList = MailingListDbAPI.getMailingListByListId(tagId);
				if (mailingList != null) {
					MailingListDbAPI.deleteMailSchedule(txn, mailingList, MailingList.Flags.DELETED);
				}
			}
			TagsDbAPI.markDeleted(txn, tagId);
			txn.commit();
		} catch (Exception e) {
			if (txn != null) {
				txn.rollback();
			}
			throw e;
		} finally {
			if (txn != null) {
				txn.close();
			}
		}
	}

	@Override
	public Tags createMonitor(String monitorName, List<Entity> validEntityList, List<String> keywordSearches,
			List<String> keywordSearchesName, MonitorWizardFilters data, Users user) throws Exception {
		Transaction txn = null;
		Tags tag = null;
		UserAndGroupRelation.setUserAndGroupRelation(new UserAndGroupRelation(user.getId(), user.getEmail()));

		String filterString = JSONUtility.serialize(data);

		try {
			txn = PersistenceProvider.newTxn(PersistenceProvider.EMAIL_DATABASE);
			tag = TagsDbAPI.addTag(txn, monitorName.trim(), TagType.FOLDER_CUSTOM, filterString, -1, null, user.getId(), OwnedByType.USER,
					-1, SearchOrderType.DATE, null);

			// add all searches
			List<Items> itemList = new ArrayList<Items>();
			String fq = servicesAPIUtil.prepareFq(data.advanced.advancedFilters);

			if (validEntityList != null && !validEntityList.isEmpty()) {
				for (Entity entity : validEntityList) {

					String query = entity.getSearchToken();
					if (data.advanced.keywords != null && !data.advanced.keywords.isEmpty()) {
						query += " " + data.advanced.keywords.trim();
					}
					String queryData = getData(query, fq, entity.getScope());
					Items item = ItemsDbAPI.addItem(txn, entity.getName(), queryData, Items.Type.Search, -1, -1, -1, RefersToType.USER,
							tag.getOwnedBy(), tag.getOwnedByType(), Items.STATUS_HISTORY_DISABLED);

					itemList.add(item);
				}
			}
			if (keywordSearches != null && !keywordSearches.isEmpty()) {
				for (int i = 0; i < keywordSearches.size(); i++) {
					String query = keywordSearches.get(i);
					if (data.advanced.keywords != null && !data.advanced.keywords.isEmpty()) {
						query += " " + data.advanced.keywords.trim();
					}
					String queryData = getData(query, fq, -1);
					Items item = ItemsDbAPI.addItem(txn, keywordSearchesName.get(i), queryData, Items.Type.Search, -1, -1, -1,
							RefersToType.USER, tag.getOwnedBy(), tag.getOwnedByType(), Items.STATUS_HISTORY_DISABLED);

					itemList.add(item);
				}
			}

			if (itemList != null && !itemList.isEmpty()) {
				int order = 1;
				for (Items item : itemList) {
					addTagMapping(txn, tag, order++, item);
				}
			}

			txn.commit();
		} catch (Exception e) {
			if (txn != null) {
				txn.rollback();
			}
			throw e;
		} finally {
			if (txn != null) {
				txn.close();
			}
		}
		return tag;
	}

	private void updateSearchStatusByEmailScheduleId(Transaction txn, long emailScheduleId, EmailSearchEnums searchStatus)
			throws Exception {
		List<EmailSearchScheduleMap> searchSchMap =
				EmailSearchScheduleMapDbAPI.fetchSearchScheduleMapByScheduleIdSearchStatus(emailScheduleId);
		if (searchSchMap != null && !searchSchMap.isEmpty()) {
			for (EmailSearchScheduleMap map : searchSchMap) {
				long searchID = map.getEmailUserSearch().getId();
				EmailUserSearch s = txn.fetch(searchID, EmailUserSearch.class);
				s.setStatus(searchStatus);
			}
		}
	}
}
