package com.firstrain.frapi.util;

import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import com.firstrain.db.obj.BaseItem.FLAGS;
import com.firstrain.db.obj.BaseItem.OwnedByType;
import com.firstrain.db.obj.Tags;
import com.firstrain.frapi.pojo.MonitorAPIResponse;

public final class TagsValidator {

    private static final Logger logger = Logger.getLogger(TagsValidator.class);

    private TagsValidator() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static boolean userDoesNotOwnMonitor(Tags tags, MonitorAPIResponse monitorAPIResponse) {
        if (OwnedByType.USER.equals(tags.getOwnedByType())) {
            monitorAPIResponse.setStatusCode(StatusCode.USER_DOESNOT_OWN_MONITOR);
            return true;
        }
        return false;
    }

    public static boolean userDoesNotOwnMonitor(Tags tags, long frUserId, MonitorAPIResponse monitorAPIResponse) {
        if (tags.getOwnedBy() != frUserId) {
            monitorAPIResponse.setStatusCode(StatusCode.USER_DOESNOT_OWN_MONITOR);
            return true;
        }
        return false;
    }

    public static boolean isEntityNotFound(Tags tag, long monitorId, MonitorAPIResponse monitorAPIResponse) {
        if (tag == null || FLAGS.DELETED.equals(tag.getFlags())) {
            logger.debug("Monitor Id :" + monitorId + "  not found in DB");
            monitorAPIResponse.setStatusCode(StatusCode.ENTITY_NOT_FOUND);
            return true;
        }
        return false;
    }

    public static boolean isIllegalArgument(List<String> entityList, MonitorAPIResponse monitorAPIResponse) {
        if (entityList.isEmpty()) {
            monitorAPIResponse.setStatusCode(StatusCode.ILLEGAL_ARGUMENT);
            return true;
        }
        return false;
    }

    public static boolean isInsufficientPrivilege(Tags tags, Set<Long> allGroupIds,
            MonitorAPIResponse monitorAPIResponse) {
        if (allGroupIds.contains(tags.getOwnedBy())) {
            return false;
        }
        monitorAPIResponse.setStatusCode(StatusCode.INSUFFICIENT_PRIVILEGE);
        return true;
    }

}
