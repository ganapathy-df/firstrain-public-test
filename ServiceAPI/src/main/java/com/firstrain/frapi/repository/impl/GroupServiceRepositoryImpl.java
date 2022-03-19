package com.firstrain.frapi.repository.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.firstrain.common.db.jpa.PersistenceProvider;
import com.firstrain.db.api.GroupsDbAPI;
import com.firstrain.db.obj.Groups;
import com.firstrain.frapi.repository.GroupServiceRepository;

@Repository
public class GroupServiceRepositoryImpl implements GroupServiceRepository {

	private final Logger LOG = Logger.getLogger(GroupServiceRepositoryImpl.class);

	@Override
	public Set<Long> getGroupIdsWhereActorHasDefaultprivileges(long userId, long ownerId, Set<Long> groupIdsWithDefaultMapping) {
		Set<Long> groupIds = new HashSet<Long>();
		try {
			if (groupIdsWithDefaultMapping != null) {
				for (Long id : groupIdsWithDefaultMapping) {
					groupIds.addAll(GroupsDbAPI.getAllParentGroupIdsByGroupId(PersistenceProvider.EMAIL_DATABASE_READ, id, ownerId));
				}
			}
		} catch (Exception e) {
			LOG.error("Error while fetching groups where actor has default privileges " + e.getMessage(), e);
		}
		return groupIds;
	}

	@Override
	public Set<Long> getGroupIdsWhereActorHasAdminprivileges(long userId, long groupId, Set<Long> groupIdWithAdminMapping) {
		Set<Long> groupIdsWhereActorHasAdminprivileges = new HashSet<Long>();
		try {
			if (groupIdWithAdminMapping != null) {
				for (Long id : groupIdWithAdminMapping) {
					groupIdsWhereActorHasAdminprivileges
							.addAll(GroupsDbAPI.getAllChildGroupIdsByGroupIds(PersistenceProvider.EMAIL_DATABASE_READ, id));
				}
				groupIdsWhereActorHasAdminprivileges.addAll(groupIdWithAdminMapping);
			}

		} catch (Exception e) {
			LOG.error("Error while getting groups where actor has admin privileges " + e.getMessage(), e);
		}
		return groupIdsWhereActorHasAdminprivileges;
	}

	@Override
	public List<Groups> getGroupsByIds(Set<Long> groupIds) {
		return GroupsDbAPI.getGroupByIds(PersistenceProvider.EMAIL_DATABASE_READ, groupIds);
	}

	@Override
	public void groupNameComparator(List<Groups> groupList) {
		Collections.sort(groupList, new Comparator<Groups>() {
			@Override
			public int compare(Groups g1, Groups g2) {
				return g1.getGroupName().compareToIgnoreCase(g2.getGroupName());
			}
		});
	}
}
