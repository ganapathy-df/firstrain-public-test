package com.firstrain.frapi.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.firstrain.db.obj.Groups;
import com.firstrain.db.obj.UserGroupMap;
import com.firstrain.db.obj.UserGroupMap.MembershipType;
import com.firstrain.db.obj.Users;

@Repository
public interface UserServiceRepository {

	public Users getUserById(long userId) throws Exception;

	public List<UserGroupMap> getUserGroupMapByUserId(long userId);

	public void updateUser(Users user, String status, String oldStatus) throws Exception;

	public void createUser(Users user, Users actor, String subGroupIdCSV) throws Exception;

	public Groups getUserGroupById(long groupId);

	// public Users getUserByEmail(String email);

	public Users getUserByEmailAndEnterpriseId(String email, long enterpriseId);

	public void sendActivationMail(Users user) throws Exception;

	public List<Users> getUsersByGrpIdAndMembershipType(long groupId, MembershipType... mt);

	public void deleteUser(long actorId) throws Exception;
}
