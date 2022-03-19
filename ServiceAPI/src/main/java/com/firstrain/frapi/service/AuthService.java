package com.firstrain.frapi.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.firstrain.frapi.FRService;
import com.firstrain.frapi.pojo.APIDefinitionPojo;
import com.firstrain.frapi.pojo.AuthAPIResponse;

@Service
public interface AuthService extends FRService {

	public AuthAPIResponse generateAuthKey(String authName, String authPassword, String apiVersion) throws Exception;

	public AuthAPIResponse getAuthKeyDetails(String authKey) throws Exception;

	public List<APIDefinitionPojo> getAPIDefinitions() throws Exception;
}
