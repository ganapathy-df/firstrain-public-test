package com.firstrain.frapi.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.firstrain.db.obj.APIAccount;
import com.firstrain.db.obj.APIAuthKey;
import com.firstrain.db.obj.APIAuthKey.Status;
import com.firstrain.db.obj.APIDefinition;
import com.firstrain.frapi.pojo.APIDefinitionPojo;
import com.firstrain.frapi.pojo.AuthAPIResponse;
import com.firstrain.frapi.repository.AuthServiceRepository;
import com.firstrain.frapi.service.AuthService;
import com.firstrain.frapi.util.StatusCode;
import com.firstrain.utils.FR_ArrayUtils;
import com.firstrain.utils.object.PerfActivityType;
import com.firstrain.utils.object.PerfMonitor;

@Service
public class AuthServiceImpl implements AuthService {

	private final Logger LOG = Logger.getLogger(AuthServiceImpl.class);

	@Autowired
	private AuthServiceRepository apiAuthRepository;

	@Override
	public AuthAPIResponse generateAuthKey(String authName, String authPassword, String apiVersion) throws Exception {

		AuthAPIResponse res = new AuthAPIResponse();

		try {
			// 1. fetch unique row (authName, authPassword, apiVersion and status -> active) from API_ACCOUNT table
			APIAccount apiAccount = apiAuthRepository.getAccount(authName, authPassword, apiVersion);

			// 2. If no row, invalid credentials
			if (apiAccount == null) {
				res.setStatusCode(StatusCode.INVALID_CREDENTIAL);
				return res;
			}

			// 3. generate new authkey/expiry time and add to DB
			Calendar c = Calendar.getInstance();
			c.add(Calendar.MINUTE, apiAccount.getExpiryDuration());
			Timestamp expiryTime = new Timestamp(c.getTimeInMillis());

			String authKey = apiAuthRepository.generateAuthKey();

			APIAuthKey apiAuthKey = new APIAuthKey();
			apiAuthKey.setAccountId(apiAccount.getId());
			apiAuthKey.setAuthKey(authKey);
			apiAuthKey.setExpiryTime(expiryTime);
			apiAuthKey.setStatus(Status.ACTIVE);
			apiAuthRepository.persistAuthKey(apiAuthKey);

			// 4. return all details

			res.setAuthKey(authKey);
			res.setExpiryTime(expiryTime);
			populateAuthAPIResponse(res, apiAccount); 
			
			res.setId(apiAccount.getId());
			res.setPrefJson(apiAccount.getPrefJson());

			res.setStatusCode(StatusCode.REQUEST_SUCCESS);
			return res;
		} catch (Exception e) {
			LOG.error("Error during Auth key generation: " + e.getMessage(), e);
			throw e;
		}
	}


	// This API would be rarely used
	@Override
	public AuthAPIResponse getAuthKeyDetails(String authKey) throws Exception {

		long startTime = PerfMonitor.currentTime();
		AuthAPIResponse res = new AuthAPIResponse();

		try {
			APIAuthKey apiAuthKey = apiAuthRepository.getAuthKeyDetails(authKey);

			if (apiAuthKey == null) {
				res.setStatusCode(StatusCode.INVALID_AUTHKEY);
				return res;
			}

			APIAccount apiAccount = apiAuthRepository.getAccountById(apiAuthKey.getAccountId());

			res.setAuthKey(authKey);
			res.setExpiryTime(apiAuthKey.getExpiryTime());
			populateAuthAPIResponse(res, apiAccount); 
			
			res.setId(apiAuthKey.getAccountId());
			if (apiAuthKey.getExpiryTime().getTime() <= System.currentTimeMillis()) {
				res.setStatusCode(StatusCode.EXPIRED_AUTHKEY);
			} else {
				res.setStatusCode(StatusCode.REQUEST_SUCCESS);
			}
			res.setPrefJson(apiAccount.getPrefJson());
			return res;
		} catch (Exception e) {
			LOG.error("Error during fetching Auth key details: " + e.getMessage(), e);
			throw e;
		} finally {
			PerfMonitor.recordStats(startTime, PerfActivityType.ReqTime, "getAuthKeyDetails");
		}
	}
 
	private void populateAuthAPIResponse(final AuthAPIResponse res, final APIAccount apiAccount) { 
		res.setEnterpriseId(apiAccount.getEnterpriseId()); 
		res.setApiVersion(apiAccount.getApiVersion()); 
		res.setExcludedSectionList(csvToArrayList(apiAccount.getExcludedSectionCSV())); 
		res.setExcludedAPIList(FR_ArrayUtils.csvToArrayList(apiAccount.getExcludedAPICSV())); 
		res.setIncludedAPIList(FR_ArrayUtils.csvToArrayList(apiAccount.getIncludedAPICSV())); 
		res.setVersionStartDate(apiAccount.getVersionStartDate()); 
		res.setVersionEndDate(apiAccount.getVersionEndDate()); 
	} 
	

	public ArrayList<String> csvToArrayList(String csv) {
		ArrayList<String> arrList = new ArrayList<String>();
		if (csv != null && csv.trim().length() > 0) {
			String strArr[] = csv.split(",");
			if (strArr.length >= 1) {
				for (int i = 0; i < strArr.length; i++) {
					try {
						arrList.add(strArr[i].toUpperCase());
					} catch (Exception e) {
					}
				}
			}
		}
		return arrList;
	}


	@Override
	public List<APIDefinitionPojo> getAPIDefinitions() throws Exception {

		List<APIDefinitionPojo> apiDefList = new ArrayList<APIDefinitionPojo>();

		try {
			List<APIDefinition> apiDBDefList = apiAuthRepository.getAPIDefinitionList();
			if (apiDBDefList != null && !apiDBDefList.isEmpty()) {

				for (APIDefinition apiDBDef : apiDBDefList) {

					APIDefinitionPojo pojo = new APIDefinitionPojo();
					pojo.setApiVersion(apiDBDef.getApiVersion());
					pojo.setApiSignature(apiDBDef.getApiSignature());
					pojo.setId(apiDBDef.getId());
					apiDefList.add(pojo);
				}
			}
		} catch (Exception e) {
			LOG.error("Error during fetching API definitions: " + e.getMessage(), e);
			throw e;
		}
		return apiDefList;
	}
}
