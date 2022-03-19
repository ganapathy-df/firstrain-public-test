package com.firstrain.frapi.repository;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.firstrain.db.obj.Groups;

@Repository
public interface GroupServiceRepository {

	public Set<Long> getGroupIdsWhereActorHasDefaultprivileges(long userId, long ownerId, Set<Long> groupIdsWithDefaultMapping);

	public Set<Long> getGroupIdsWhereActorHasAdminprivileges(long userId, long groupId, Set<Long> groupIdWithAdminMapping);

	public List<Groups> getGroupsByIds(Set<Long> groupIds);

	public void groupNameComparator(List<Groups> groupList);
}
