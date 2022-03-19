package com.firstrain.frapi.customapiservice;

import org.springframework.stereotype.Service;

import com.firstrain.frapi.FRService;
import com.firstrain.web.pojo.CreateInputBean;

@Service
public interface TakeDownService extends FRService {

	public int takeDownContent(long enterpriseId, long articleId) throws Exception;
	public int takeDownContentForced(long enterpriseId, long articleId) throws Exception;

	public Boolean isValidSearchToken(String searchToken, boolean isTopicOnly, CreateInputBean inputBean);

}
