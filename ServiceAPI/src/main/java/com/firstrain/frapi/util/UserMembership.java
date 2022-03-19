package com.firstrain.frapi.util;

import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import com.firstrain.db.obj.UserGroupMap;
import com.firstrain.db.obj.UserGroupMap.MembershipType;
import com.firstrain.frapi.repository.UserServiceRepository;

public final class UserMembership {

    private UserMembership() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static MembershipType retrieveMembershipType(UserServiceRepository userServiceRepository, long frUserId) {
        MembershipType membershipType = null;
        List<UserGroupMap> map = userServiceRepository.getUserGroupMapByUserId(frUserId);
        if (CollectionUtils.isNotEmpty(map)) {
            membershipType = map.get(0).getMembershipType();
        }
        return membershipType;
    }

}
