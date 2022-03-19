package com.firstrain.frapi.service;

import org.springframework.stereotype.Service;

import com.firstrain.frapi.FRService;
import com.firstrain.frapi.domain.User;
import com.firstrain.frapi.pojo.UserAPIResponse;

@Service
public interface UserService extends FRService {

	/**
	 * @param frUserId user id that need to be fetched
	 * @return user details if frUserId is valid. Else, status code 101 (USER_NOT_FOUND)
	 * @throws Exception
	 */
	public UserAPIResponse getUserById(long frUserId) throws Exception;

	/**
	 * @param frUserId user id that need to be fetched
	 * @param needMembershipType if true, membership type is also fetched
	 * @return user details if frUserId is valid. Else, status code 101 (USER_NOT_FOUND)
	 * @throws Exception
	 */
	public UserAPIResponse getUserById(long frUserId, boolean needMembershipType) throws Exception;

	/**
	 * @param email email for which user need to be fetched
	 * @return user details if email exists. Else, status code 101 (USER_NOT_FOUND)
	 * @throws Exception
	 */
	public UserAPIResponse getUserByEmail(String email, long enterpriseId) throws Exception;

	/**
	 * @param email email for which user need to be fetched
	 * @param needMembershipType if true, membership type is also fetched
	 * @return user details if email exists. Else, status code 101 (USER_NOT_FOUND)
	 * @throws Exception
	 */
	public UserAPIResponse getUserByEmail(String email, long enterpriseId, boolean needMembershipType) throws Exception;

	/**
	 * @param actorId
	 * @param email
	 * @param firstName
	 * @param lastName
	 * @param timeZone
	 * @param subGroupIdCSV
	 * @param company
	 * @return
	 * @throws Exception
	 */
	public UserAPIResponse createUser(long actorId, String email, String firstName, String lastName, String timeZone, String subGroupIdCSV,
			String company) throws Exception;

	/**
	 * @param actor user who is actually carrying out this operation
	 * @param frUserId user id that need to be updated
	 * @param email new email of user
	 * @param firstName new first name of user
	 * @param lastName new last name of user
	 * @param timeZone new time zone of user
	 * @param status new status of user
	 * @param company new company of user
	 * @return updated user details
	 * @throws Exception
	 */
	public UserAPIResponse updateUserById(User actor, long frUserId, String email, String firstName, String lastName, String timeZone,
			String status, String company) throws Exception;

	/**
	 * @param actorId
	 * @return
	 * @throws Exception
	 */
	public UserAPIResponse deleteUser(long actorId) throws Exception;
}
