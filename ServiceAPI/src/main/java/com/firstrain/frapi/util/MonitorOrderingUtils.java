package com.firstrain.frapi.util;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.firstrain.db.obj.Groups;
import com.firstrain.db.obj.Tags.SearchOrderType;
import com.firstrain.db.obj.TagsInfo;

/**
 * @author Akanksha
 * 
 */

public class MonitorOrderingUtils {

	public static void sortGroupMonitorByOrderType(Groups group, List<TagsInfo> tagInfoList) {
		if (group.getMonitorOrderType() == SearchOrderType.DATE) {
			sortTagListByDate(tagInfoList);
		} else if (group.getMonitorOrderType() == SearchOrderType.CUSTOM) {
			sortTagListByCustomOrder(tagInfoList);
		} else {
			sortTagListByName(tagInfoList);
		}
	}

	public static void sortTagListByName(List<TagsInfo> tagInfoList) {
		if (tagInfoList == null) {
			return;
		}
		Collections.sort(tagInfoList, new Comparator<TagsInfo>() {
			@Override
			public int compare(TagsInfo o1, TagsInfo o2) {
				if (o1.favorite && !o2.favorite) {
					return -1;
				}
				if (!o1.favorite && o2.favorite) {
					return 1;
				}
				if (o1.favorite && o2.favorite) {
					if (o1.activityTime.equals(o2.activityTime)) {
						return Long.valueOf(o1.favoriteUserItemId).compareTo(Long.valueOf(o2.favoriteUserItemId));
					}
					return o1.activityTime.compareTo(o2.activityTime);
				}
				return o1.tag.getTagName().compareToIgnoreCase(o2.tag.getTagName());
			}
		});
	}

	private static void sortTagListByDate(List<TagsInfo> tagInfoList) {
		if (tagInfoList == null) {
			return;
		}
		Collections.sort(tagInfoList, new Comparator<TagsInfo>() {
			@Override
			public int compare(TagsInfo o1, TagsInfo o2) {
				if (o1.favorite && !o2.favorite) {
					return -1;
				}
				if (!o1.favorite && o2.favorite) {
					return 1;
				}
				if (o1.favorite && o2.favorite) {
					return o1.activityTime.compareTo(o2.activityTime);
				}
				Timestamp t1 = o1.tag.getInsertTime();
				Timestamp t2 = o2.tag.getInsertTime();
				return -t1.compareTo(t2);
			}
		});
	}

	private static void sortTagListByCustomOrder(List<TagsInfo> tagInfoList) {
		if (tagInfoList == null) {
			return;
		}
		Collections.sort(tagInfoList, new Comparator<TagsInfo>() {
			@Override
			public int compare(TagsInfo o1, TagsInfo o2) {
				if (o1.favorite && !o2.favorite) {
					return -1;
				}
				if (!o1.favorite && o2.favorite) {
					return 1;
				}
				if (o1.favorite && o2.favorite) {
					return o1.activityTime.compareTo(o2.activityTime);
				}
				Integer t1 = o1.tag.getOrder();
				Integer t2 = o2.tag.getOrder();
				return t1.compareTo(t2);
			}
		});
	}

	public static class OrderedMonitors {
		public List<TagsInfo> favoriteMonitors;
		public List<TagsInfo> userMonitors;
		public List<Groups> groupsWhereActorHasAdminPrivileges;
		public List<Groups> groupsWhereActorHasDefaultPrivileges;
		public Map<Long, List<TagsInfo>> grpIdVsTagsInfoList;

		// internal use
		Set<Long> groupIdsWhereActorIsAdmin = new HashSet<Long>();

		public void setGroupsWhereActorHasAdminPrivileges(List<Groups> groupsWhereActorHasAdminPrivileges) {
			this.groupsWhereActorHasAdminPrivileges = groupsWhereActorHasAdminPrivileges;
			if (groupsWhereActorHasAdminPrivileges != null) {
				for (Groups g : groupsWhereActorHasAdminPrivileges) {
					groupIdsWhereActorIsAdmin.add(g.getId());
				}
			}
		}
	}
}
