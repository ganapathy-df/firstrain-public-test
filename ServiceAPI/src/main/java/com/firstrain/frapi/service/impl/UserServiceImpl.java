package com.firstrain.frapi.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.firstrain.db.obj.BaseItem.FLAGS;
import com.firstrain.db.obj.BaseItem.OwnedByType;
import com.firstrain.db.obj.GroupPreference;
import com.firstrain.db.obj.Groups;
import com.firstrain.db.obj.UserGroupMap;
import com.firstrain.db.obj.UserGroupMap.MembershipType;
import com.firstrain.db.obj.Users;
import com.firstrain.db.obj.Users.Origin;
import com.firstrain.db.obj.Users.Type;
import com.firstrain.frapi.domain.User;
import com.firstrain.frapi.pojo.UserAPIResponse;
import com.firstrain.frapi.repository.AuthServiceRepository;
import com.firstrain.frapi.repository.UserServiceRepository;
import com.firstrain.frapi.service.UserService;
import com.firstrain.frapi.util.ConvertUtil;
import com.firstrain.frapi.util.ServicesAPIUtil;
import com.firstrain.frapi.util.StatusCode;
import com.firstrain.utils.object.PerfActivityType;
import com.firstrain.utils.object.PerfMonitor;

@Service
public class UserServiceImpl implements UserService {

	private final Logger LOG = Logger.getLogger(UserServiceImpl.class);

	@Autowired
	private UserServiceRepository userServiceRepository;
	@Autowired
	private AuthServiceRepository authServiceRepository;
	@Autowired
	private ConvertUtil convertUtil;
	@Autowired
	private ServicesAPIUtil servicesAPIUtil;

	@Override
	public UserAPIResponse getUserById(long frUserId) throws Exception {

		return getUserById(frUserId, false);
	}

	@Override
	public UserAPIResponse getUserById(long frUserId, boolean needMembershipType) throws Exception {

		long startTime = PerfMonitor.currentTime();
		try {
			UserAPIResponse res = new UserAPIResponse();
			Users dbUser = userServiceRepository.getUserById(frUserId);
			if (dbUser == null) {
				res.setStatusCode(StatusCode.USER_NOT_FOUND);
				return res;
			}

			MembershipType type = null;
			if (needMembershipType) {
				List<UserGroupMap> map = userServiceRepository.getUserGroupMapByUserId(frUserId);
				if (map != null && !map.isEmpty()) {
					type = map.get(0).getMembershipType();
				}
			}

			res.setUser(convertUtil.convertDbUserToDomainUser(dbUser, type));
			res.setStatusCode(StatusCode.REQUEST_SUCCESS);
			return res;
		} catch (Exception e) {
			LOG.error("Error while getting user for id:" + frUserId, e);
			throw e;
		} finally {
			PerfMonitor.recordStats(startTime, PerfActivityType.ReqTime, "getUserById");
		}
	}

	@Override
	public UserAPIResponse updateUserById(User actor, long frUserId, String email, String firstName, String lastName, String timeZone,
			String status, String company) throws Exception {

		long startTime = PerfMonitor.currentTime();
		try {
			UserAPIResponse res = new UserAPIResponse();

			if (status != null) {
				try {
					FLAGS.valueOf(status.toUpperCase());
				} catch (Exception e) {
					res.setStatusCode(StatusCode.ILLEGAL_ARGUMENT);
					return res;
				}
			}

			List<UserGroupMap> map = userServiceRepository.getUserGroupMapByUserId(Long.parseLong(actor.getUserId()));
			MembershipType type = null;
			if (map != null && !map.isEmpty()) {
				type = map.get(0).getMembershipType();
			}
			if (Long.parseLong(actor.getUserId()) != frUserId && !type.equals(MembershipType.ADMIN)) {
				res.setStatusCode(StatusCode.INSUFFICIENT_PRIVILEGE);
				return res;
			}
			if (status != null && !type.equals(MembershipType.ADMIN)) {
				res.setStatusCode(StatusCode.INSUFFICIENT_PRIVILEGE);
				return res;
			}

			Users dbUser = userServiceRepository.getUserById(frUserId);

			if (dbUser == null) {
				res.setStatusCode(StatusCode.USER_NOT_FOUND);
				return res;
			}

			if (email != null && !email.isEmpty()) {
				Users user = userServiceRepository.getUserByEmailAndEnterpriseId(email, dbUser.getOwnedBy());
				if (user != null) {
					res.setStatusCode(StatusCode.EMAIL_ALREADY_EXISTS);
					return res;
				}
			}

			if (actor.getOwnedBy() != dbUser.getOwnedBy()) {
				res.setStatusCode(StatusCode.INSUFFICIENT_PRIVILEGE);
				return res;
			}

			if (email != null && !email.isEmpty()) {
				dbUser.setEmail(email);
			}
			if (firstName != null && !firstName.isEmpty()) {
				dbUser.setFirstName(firstName);
			}
			if (lastName != null && !lastName.isEmpty()) {
				dbUser.setLastName(lastName);
			}
			if (timeZone != null && !timeZone.isEmpty()) {
				dbUser.setTimeZone(timeZone);
			}

			String oldStatus = null;

			if (status != null) {
				if (dbUser.getFlags() != null) {
					oldStatus = dbUser.getFlags().name();
				}
				dbUser.setFlags(FLAGS.valueOf(status.toUpperCase()));
			}
			if (company != null && !company.isEmpty()) {
				dbUser.setCompany(company);
			}

			userServiceRepository.updateUser(dbUser, status, oldStatus);

			res.setUser(convertUtil.convertDbUserToDomainUser(dbUser, null));
			res.setStatusCode(StatusCode.REQUEST_SUCCESS);
			return res;
		} catch (Exception e) {
			LOG.error("Error while updating user for id:" + frUserId, e);
			throw e;
		} finally {
			PerfMonitor.recordStats(startTime, PerfActivityType.ReqTime, "updateUserById");
		}
	}

	@Override
	public UserAPIResponse createUser(long actorId, String email, String firstName, String lastName, String timeZone, String subGroupIdCSV,
			String company) throws Exception {

		long startTime = PerfMonitor.currentTime();
		try {
			UserAPIResponse res = new UserAPIResponse();
			Users actorDBObj = userServiceRepository.getUserById(actorId);

			// Users user = userServiceRepository.getUserByEmail(email);
			Users user = userServiceRepository.getUserByEmailAndEnterpriseId(email, actorDBObj.getOwnedBy());

			if (user != null) {
				res.setStatusCode(StatusCode.USER_ALREADY_EXISTS);
				return res;
			}

			List<Users> enterpriseUserList =
					userServiceRepository.getUsersByGrpIdAndMembershipType(actorDBObj.getOwnedBy(), MembershipType.ANONYMOUS);
			if (enterpriseUserList == null || enterpriseUserList.isEmpty()) {
				res.setStatusCode(StatusCode.ANONYMOUS_USER_NOT_FOUND);
				return res;
			}

			user = prepareUsersObject(enterpriseUserList.get(0), actorDBObj, email, firstName, lastName, timeZone, company);
			String password = user.getPassword();// plain password...to be used later to send activation email

			userServiceRepository.createUser(user, actorDBObj, subGroupIdCSV);

			// get Group object and check activation email flag in group preferences
			Groups group = userServiceRepository.getUserGroupById(actorDBObj.getOwnedBy());
			if (group != null) {
				GroupPreference groupPreferenceDbObj = GroupPreference.parseXML(group.getPrefXML());
				if (groupPreferenceDbObj.isActivationEmail()) {
					user.setFrApiPassword(password);
					userServiceRepository.sendActivationMail(user);
				}
			}

			res.setUser(convertUtil.convertDbUserToDomainUser(user, null));
			res.setStatusCode(StatusCode.REQUEST_SUCCESS);
			return res;
		} catch (Exception e) {
			LOG.error("Error while creating user for email:" + email, e);
			throw e;
		} finally {
			PerfMonitor.recordStats(startTime, PerfActivityType.ReqTime, "createUser");
		}
	}

	@Override
	public UserAPIResponse deleteUser(long actorId) throws Exception {
		long startTime = PerfMonitor.currentTime();
		try {
			UserAPIResponse res = new UserAPIResponse();
			userServiceRepository.deleteUser(actorId);
			res.setStatusCode(StatusCode.REQUEST_SUCCESS);
			return res;
		} catch (Exception e) {
			LOG.error("Error while deleting user:" + actorId, e);
			throw e;
		} finally {
			PerfMonitor.recordStats(startTime, PerfActivityType.ReqTime, "createUser");
		}
	}

	private Users prepareUsersObject(Users enterpriseUser, Users actor, String email, String firstName, String lastName, String timeZoneParam,
			String company) {

		String timeZone = timeZoneParam;
		Users user = new Users();
		user.setUserName(email);
		user.setEmail(email);
		user.setCompany(company);

		if (firstName == null || firstName.isEmpty()) {
			user.setFirstName(email.substring(0, email.indexOf("@")));
		} else {
			user.setFirstName(firstName);
		}

		user.setLastName(lastName);
		user.setDomain(email.substring(email.indexOf("@") + 1, email.length()));
		user.setSalt("");
		String password = servicesAPIUtil.generateRandomString(8);
		user.setPassword(password);
		if (timeZone == null || timeZone.isEmpty()) {
			timeZone = enterpriseUser.getTimeZone();
		}
		user.setTimeZone(timeZone);
		user.setType(Type.IDENTIFIED);
		user.setOrigin(Origin.FRAPI);
		user.setFormat(enterpriseUser.getFormat());
		user.setTemplateID(enterpriseUser.getTemplateID());
		user.setPrefXML(enterpriseUser.getPrefXML());
		// BaseItem info:
		user.setFlags(FLAGS.ACTIVE);
		user.setInsertedBy(actor.getId());
		user.setUpdatedBy(actor.getId());
		user.setOwnedBy(actor.getOwnedBy());
		user.setOwnedByType(OwnedByType.GROUP);
		user.setProductType(actor.getProductType());

		return user;
	}

	@Override
	public UserAPIResponse getUserByEmail(String email, long enterpriseId) throws Exception {
		return getUserByEmail(email, enterpriseId, false);
	}

	@Override
	public UserAPIResponse getUserByEmail(String email, long enterpriseId, boolean needMembershipType) throws Exception {
		long startTime = PerfMonitor.currentTime();
		try {
			UserAPIResponse res = new UserAPIResponse();
			Users dbUser = userServiceRepository.getUserByEmailAndEnterpriseId(email, enterpriseId);
			if (dbUser == null) {
				res.setStatusCode(StatusCode.USER_NOT_FOUND);
				return res;
			}

			MembershipType type = null;
			if (needMembershipType) {
				List<UserGroupMap> map = userServiceRepository.getUserGroupMapByUserId(dbUser.getId());
				if (map != null && !map.isEmpty()) {
					type = map.get(0).getMembershipType();
				}
			}

			res.setUser(convertUtil.convertDbUserToDomainUser(dbUser, type));
			res.setStatusCode(StatusCode.REQUEST_SUCCESS);
			return res;
		} catch (Exception e) {
			LOG.error("Error while getting user for email: " + email + " and enterpriseId: " + enterpriseId, e);
			throw e;
		} finally {
			PerfMonitor.recordStats(startTime, PerfActivityType.ReqTime, "getUserByEmail");
		}
	}
}
