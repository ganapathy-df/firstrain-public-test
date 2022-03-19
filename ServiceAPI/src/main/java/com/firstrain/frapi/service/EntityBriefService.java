package com.firstrain.frapi.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.firstrain.frapi.FRService;
import com.firstrain.frapi.domain.EntityMap;
import com.firstrain.frapi.pojo.EnterprisePref;
import com.firstrain.frapi.pojo.EntityBriefInfo;
import com.firstrain.frapi.util.DefaultEnums.INPUT_ENTITY_TYPE;

@Service
public interface EntityBriefService extends FRService {

	public EntityBriefInfo getEntityBriefDetails(EnterprisePref enterprisePref, String searchToken, String fq) throws Exception;

	public EntityBriefInfo getEntityBriefDetails(EnterprisePref enterprisePref, String searchToken, String fq, String scopeDirective,
			Boolean blendDUNS) throws Exception;

	public EntityBriefInfo getEntityBriefDetailsForMT(EnterprisePref enterprisePref, String searchToken, String fq, String startDate,
			String endDate, String count) throws Exception;

	public EntityBriefInfo getEntityMap(String searchToken) throws Exception;

	public EntityBriefInfo getEntityMatch(String q, int count, INPUT_ENTITY_TYPE type, String diemnsionCSV) throws Exception;

	public EntityBriefInfo getEntityPeers(String primaryEntityToken, List<String> catIdList, boolean isDnBId) throws Exception;

	public EntityMap getEntityMapBySourceSearchToken(String searchToen, String language) throws Exception;
}
