package com.firstrain.frapi.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.firstrain.db.obj.APIAccount;
import com.firstrain.db.obj.APIAuthKey;
import com.firstrain.db.obj.APIDefinition;

@Repository
public interface AuthServiceRepository {

	public APIAccount getAccount(String authName, String authPassword, String apiVersion) throws Exception;

	public APIAccount getAccountById(long accountId) throws Exception;

	public void persistAuthKey(APIAuthKey apiAuthKey) throws Exception;

	public String generateAuthKey();

	public APIAuthKey getAuthKeyDetails(String authKey);

	public List<APIDefinition> getAPIDefinitionList();
}
