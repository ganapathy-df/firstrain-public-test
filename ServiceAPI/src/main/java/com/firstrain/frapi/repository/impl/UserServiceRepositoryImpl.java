package com.firstrain.frapi.repository.impl;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.firstrain.common.db.jpa.PersistenceProvider;
import com.firstrain.common.db.jpa.Transaction;
import com.firstrain.db.api.EmailScheduleDbAPI;
import com.firstrain.db.api.EmailSearchScheduleMapDbAPI;
import com.firstrain.db.api.EmailUserInfoDbAPI;
import com.firstrain.db.api.EmailUserSearchDbAPI;
import com.firstrain.db.api.GroupsDbAPI;
import com.firstrain.db.api.UserGroupMapDbAPI;
import com.firstrain.db.api.UserLogDbAPI;
import com.firstrain.db.api.UserRecoveryDbAPI;
import com.firstrain.db.api.UsersDbAPI;
import com.firstrain.db.obj.BaseItem.FLAGS;
import com.firstrain.db.obj.EmailSchedule;
import com.firstrain.db.obj.EmailSchedule.EmailScheduleEnums;
import com.firstrain.db.obj.EmailUserInfo;
import com.firstrain.db.obj.EmailUserInfo.EmailUserInfoEnums;
import com.firstrain.db.obj.EmailUserSearch;
import com.firstrain.db.obj.EmailUserSearch.EmailSearchEnums;
import com.firstrain.db.obj.Groups;
import com.firstrain.db.obj.UserAndGroupRelation;
import com.firstrain.db.obj.UserGroupMap;
import com.firstrain.db.obj.UserGroupMap.MembershipType;
import com.firstrain.db.obj.UserLog;
import com.firstrain.db.obj.UserLog.Action;
import com.firstrain.db.obj.UserLog.ChannelType;
import com.firstrain.db.obj.UserRecovery;
import com.firstrain.db.obj.Users;
import com.firstrain.frapi.repository.UserServiceRepository;
import com.firstrain.frapi.util.MailUtil;
import com.firstrain.utils.FR_ArrayUtils;
import com.firstrain.utils.FR_MailUtils;
import com.firstrain.utils.FR_MailUtils.MailInfo;

@Repository
public class UserServiceRepositoryImpl implements UserServiceRepository {

	@Value("${img.css.base.url}")
	private String imageUrl;
	@Value("${mail.server.host}")
	private String mailServer;
	@Autowired
	private MailUtil mailUtil;

	private final Logger LOG = Logger.getLogger(UserServiceRepositoryImpl.class);

	@Override
	public Users getUserById(long userId) throws Exception {

		Transaction txn = null;
		try {
			txn = PersistenceProvider.newTxn(PersistenceProvider.EMAIL_DATABASE_READ);
			Users user = UsersDbAPI.getUserById(txn, userId);

			// fallback on write DB
			if (user == null) {

				if (txn != null) {
					txn.close();
				}

				txn = PersistenceProvider.newTxn(PersistenceProvider.EMAIL_DATABASE);
				user = UsersDbAPI.getUserById(txn, userId);
				if (user != null) {
					LOG.debug("User " + userId + " not available in Read DB, so fetched from Write DB");
				} else {
					LOG.debug("User " + userId + " not available in both Read/Write DBs");
				}
			}

			return user;
		} finally {
			if (txn != null) {
				txn.close();
			}
		}
	}

	@Override
	public List<UserGroupMap> getUserGroupMapByUserId(long userId) {

		List<UserGroupMap> userGroupMapList = UserGroupMapDbAPI.getUserGroupMapByUserId(PersistenceProvider.EMAIL_DATABASE_READ, userId);
		if (userGroupMapList == null || userGroupMapList.isEmpty()) {
			userGroupMapList = UserGroupMapDbAPI.getUserGroupMapByUserId(PersistenceProvider.EMAIL_DATABASE, userId);

			if (userGroupMapList == null || userGroupMapList.isEmpty()) {
				LOG.debug("User-Group mapping for user " + userId + " not available in USER_GROUP_MAP table of both Read/Write DBs");
			} else {
				LOG.debug("User-Group mapping for user " + userId + " not available in USER_GROUP_MAP table of Read DB, so fetched from Write DB");
			}
		}
		return userGroupMapList;
	}

	@Override
	public void updateUser(Users user, String status, String oldStatus) throws Exception {

		Transaction txn = null;
		try {
			txn = PersistenceProvider.newTxn(PersistenceProvider.EMAIL_DATABASE);
			UsersDbAPI.updateFRAPIUser(txn, user);
			if (status != null) {
				UserGroupMapDbAPI.updateFlagsByUserId(txn, user.getId(), FLAGS.valueOf(status.toUpperCase()));

				if (FLAGS.INACTIVE.name().equalsIgnoreCase(status) && (oldStatus == null || FLAGS.ACTIVE.name().equalsIgnoreCase(oldStatus))) {
					UserLog userLog = new UserLog();
					userLog.setUserId(user.getId());
					userLog.setUserAction(Action.INACTIVE);
					userLog.setChannelType(ChannelType.StandardAPI);
					userLog.setComment("StandardAPI");
					UserLogDbAPI.persist(txn, userLog);
				}
			}
			txn.commit();
		} finally {
			if (txn != null) {
				txn.close();
			}
		}
	}

	@Override
	public void createUser(Users user, Users actor, String subGroupIdCSV) throws Exception {

		Transaction txn = null;
		try {
			UserAndGroupRelation.setUserAndGroupRelation(new UserAndGroupRelation(actor.getId(), actor.getEmail()));
			txn = PersistenceProvider.newTxn(PersistenceProvider.EMAIL_DATABASE);
			UsersDbAPI.persistUser(txn, user);
			UserGroupMapDbAPI.addMember(txn, MembershipType.DEFAULT, user.getId(), actor.getOwnedBy());
			if (subGroupIdCSV != null && !subGroupIdCSV.isEmpty()) {

				List<Long> subGroupIdsList = FR_ArrayUtils.csvToLongList(subGroupIdCSV);
				for (Long subGroupId : subGroupIdsList) {
					UserGroupMapDbAPI.addMember(txn, MembershipType.DEFAULT, user.getId(), subGroupId);
				}
			}

			txn.commit();
		} finally {
			if (txn != null) {
				txn.close();
			}
		}
	}

	@Override
	public Groups getUserGroupById(long groupId) {
		// fallback on write DB not required
		return GroupsDbAPI.fetchUserGroupById(PersistenceProvider.EMAIL_DATABASE_READ, groupId);
	}

	// @Override
	// public Users getUserByEmail(String email) {
	// Users user = UsersDbAPI.getUserByEmailORUserName(PersistenceProvider.EMAIL_DATABASE_READ, null, email);
	//
	// if(user == null){
	// user = UsersDbAPI.getUserByEmailORUserName(PersistenceProvider.EMAIL_DATABASE, null, email);
	//
	// if(user != null){
	// LOG.debug("User with email " + email + " not available in Read DB, so fetched from Write DB");
	// }
	// else{
	// LOG.debug("User with email " + email + " not available in both Read/Write DBs");
	// }
	// }
	//
	// return user;
	// }

	@Override
	public void sendActivationMail(Users user) throws Exception {
		String ftl = "activation-Mail.ftl";
		String mailSubjectUser = "Your FirstRain Account Info";
		String mailFromSupport = "FirstRain<support@firstrain.com>";
		String mailToUser = FR_MailUtils.constructEmail(user.getFirstName(), user.getLastName(), user.getEmail());

		String mailMessage = null;
		try {
			Map<String, Object> userMap = new HashMap<String, Object>();
			userMap.put("password", user.getFrApiPassword());
			userMap.put("user", user);
			userMap.put("imgBaseURL", imageUrl);
			mailMessage = mailUtil.getHtmlToTransmit(userMap, ftl);
		} catch (Exception e) {
			LOG.error("Template failure for mail to " + user.getUserName() + " for email address:" + user.getEmail() + e.getMessage(), e);
			throw e;
		}
		if (mailMessage != null) {
			MailInfo mailInfo = null;
			try {
				mailInfo = mailUtil.prepareMailInfo(mailSubjectUser, mailToUser, mailFromSupport, mailServer); // subject,to, sender
			} catch (Exception e) {
				LOG.error("Mail Info failure to " + user.getUserName() + " for email address:" + user.getEmail() + e.getMessage(), e);
				throw e;
			}
			mailInfo.message = mailMessage;
			try {
				FR_MailUtils.sendMail(mailInfo);
				LOG.info("Mail successfully transmitted to " + user.getEmail());
			} catch (Exception e) {
				LOG.error("Mail transmission failure to " + user.getUserName() + " for email address:" + user.getEmail() + e.getMessage(), e);
				throw e;
			}
		}

	}

	@Override
	public List<Users> getUsersByGrpIdAndMembershipType(long groupId, MembershipType... mt) {
		List<Users> usersList = UsersDbAPI.getUsersByGrpIdAndMembershipType(PersistenceProvider.EMAIL_DATABASE_READ, groupId, mt);

		if (usersList == null || usersList.isEmpty()) {
			usersList = UsersDbAPI.getUsersByGrpIdAndMembershipType(PersistenceProvider.EMAIL_DATABASE, groupId, mt);

			if (usersList == null || usersList.isEmpty()) {
				LOG.debug("Results not available in both Read/Write DBs");
			} else {
				LOG.debug("Results not available in USER_GROUP_MAP table of Read DB, so fetched from Write DB");
			}
		}

		return usersList;
	}

	@Override
	public Users getUserByEmailAndEnterpriseId(String email, long enterpriseId) {
		// this call is directly on write DB...as there is high probability of user not being available...so avoiding both R/W DB hits
		Groups groups = GroupsDbAPI.fetchUserGroupById(PersistenceProvider.EMAIL_DATABASE, enterpriseId);
		String productType = groups != null ? groups.getProductType() : null;
		return UsersDbAPI.getUserByUserNameWithoutOrigin(PersistenceProvider.EMAIL_DATABASE, email, productType);
	}

	@Override
	public void deleteUser(long actorId) throws Exception {
		Transaction txn = null;
		try {
			txn = PersistenceProvider.newTxn(PersistenceProvider.EMAIL_DATABASE);
			UserRecovery ur = UserRecoveryDbAPI.getUserRecoveryInfoByUId(actorId);
			if (ur != null) {
				UserRecoveryDbAPI.flushUserRecoveryInfo(txn, ur.getId());
			}

			Users user = UsersDbAPI.getUserById(txn, actorId);
			if (user != null && user.getFlags() != FLAGS.DELETED) {
				user.setUserName(getStringWithTimestamp(user.getUserName(), 255));// max length of column in DB: 255
				String email = user.getEmail();
				user.setEmail(getStringWithTimestamp(email, 255));// max length of column in DB: 255
				user.setFlags(FLAGS.DELETED);
				/* Also update the marking for User Group Map */
				UserGroupMapDbAPI.updateFlagsByUserId(txn, actorId, FLAGS.DELETED);
				if (email != null) {
					deleteEmailInfoByUser(txn, email);
				}
				txn.commit();
			}
		} finally {
			if (txn != null) {
				txn.close();
			}
		}
	}

	private String getStringWithTimestamp(String s, int maxLen) {

		String _s = s + "_" + new Timestamp(System.currentTimeMillis());
		if (maxLen == -1) {// no limit
			return _s;
		}
		if (_s.length() > maxLen) {
			int lenExceeds = _s.length() - maxLen;
			_s = s.substring(0, (s.length() - lenExceeds)) + "_" + new Timestamp(System.currentTimeMillis());
		}
		return _s;
	}

	private void deleteEmailInfoByUser(Transaction txn, String email) throws Exception {
		List<EmailUserInfo> userInfoList = EmailUserInfoDbAPI.fetchEmailUserInfoByEmail(email);
		if (userInfoList != null && !userInfoList.isEmpty()) {
			long emailUserID = userInfoList.get(0).getId();
			EmailUserInfo emailUserInfo = EmailUserInfoDbAPI.fetchEmailUserInfoByUserId(txn, emailUserID);
			emailUserInfo.setStatus(EmailUserInfoEnums.DELETED);
			// free this email address
			emailUserInfo.setEmail(getStringWithTimestamp(emailUserInfo.getEmail(), 255));// max length of column in DB: 255
			EmailUserInfoDbAPI.updateEmailUserInfo(txn, emailUserInfo);
			List<EmailSchedule> scheduleList = EmailScheduleDbAPI.fetchEmailScheduleByUserId(txn, emailUserID);
			if (scheduleList != null && !scheduleList.isEmpty()) {
				for (EmailSchedule schedule : scheduleList) {
					EmailSearchScheduleMapDbAPI.deleteEmailSearchScheduleMapByScheduleId(txn, schedule.getId());
					EmailScheduleDbAPI.updateEmailScheduleStatus(txn, schedule.getId(), EmailScheduleEnums.MARKED_FOR_DELETION);
				}
			}
			updateSearchStatus(txn, emailUserID, EmailSearchEnums.DELETED);
		}
	}

	private void updateSearchStatus(Transaction txn, long userID, EmailSearchEnums searchStatus) throws Exception {
		List<EmailUserSearch> searchList = EmailUserSearchDbAPI.fetchEmailUserSearchByUserId(userID);
		if (searchList != null && !searchList.isEmpty()) {
			for (EmailUserSearch eus : searchList) {
				EmailUserSearchDbAPI.updateEmailSearchStatus(txn, eus.getId(), searchStatus);
			}
		}
	}
}
