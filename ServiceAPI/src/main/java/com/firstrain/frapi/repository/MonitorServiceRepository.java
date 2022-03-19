package com.firstrain.frapi.repository;

import java.util.List;

import org.springframework.stereotype.Service;

import com.firstrain.common.db.jpa.Transaction;
import com.firstrain.db.obj.BaseItem.OwnedByType;
import com.firstrain.db.obj.EmailUserSearch;
import com.firstrain.db.obj.EmailUserSearch.EmailSearchEnums;
import com.firstrain.db.obj.Items;
import com.firstrain.db.obj.SearchMap;
import com.firstrain.db.obj.TagItemsMap;
import com.firstrain.db.obj.Tags;
import com.firstrain.db.obj.Users;
import com.firstrain.frapi.obj.MonitorWizardFilters;
import com.firstrain.frapi.pojo.Entity;
import com.firstrain.obj.IEntityInfoCache;
import com.firstrain.solr.client.SolrSearcher;

@Service
public interface MonitorServiceRepository {

	public List<Tags> getTagsByOwner(OwnedByType ownerType, long... ownerIds);

	public Tags getTagById(long tagId) throws Exception;

	public List<TagItemsMap> getTagsItemsMapByTagID(long tagId);

	public void removeEntities(Tags tag, List<Long> entityIdList) throws Exception;

	public void addEntities(Tags tag, List<Entity> validEntityList, List<String> keywordSearches, List<String> keywordSearchesName)
			throws Exception;

	public void deleteItemsTagMapByTagAndItemID(Transaction txn, long tagID, List<Long> entityIdList);

	public SearchMap getSearchMapByUISearchIDAndTagID(long uiSearchID, long tagID);

	public void deleteSearchMap(Transaction txn, long id);

	public void deleteSearchFromEmailSchedule(Transaction txn, long scheduleId, long searchId) throws Exception;

	public void updateEmailSearchStatus(Transaction txn, long searchId, EmailSearchEnums status) throws Exception;

	public void updateTagFiltersAndSearches(long frUserId, Tags tag, String filteredString, MonitorWizardFilters oldData,
			MonitorWizardFilters finalData) throws Exception;

	public void updateEmailUserSearch(Transaction txn, EmailUserSearch emailUserSearch) throws Exception;

	public EmailUserSearch fetchEmailUserSearchById(Transaction txn, long emailUserSearchId) throws Exception;

	public List<Items> getItemsByTagId(long tagId);

	public List<Entity> validateTokens(List<String> tokenList) throws Exception;

	public List<Entity> validateTokensUsingEntityCache(List<String> tokenList, List<String> invalidEntities);

	public Tags getTagsByOwnerAndTagName(long ownerId, OwnedByType ownerType, String tagName);

	public Tags createMonitor(String monitorName, List<Entity> validEntityList, List<String> keywordSearches,
			List<String> keywordSearchesName, MonitorWizardFilters data, Users user) throws Exception;

	public void removeMonitor(Tags tag) throws Exception;

	public SolrSearcher getSearcher();

	public IEntityInfoCache getEntityInfoCache();
}
