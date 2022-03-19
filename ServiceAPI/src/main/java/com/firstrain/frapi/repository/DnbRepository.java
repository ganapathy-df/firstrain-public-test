package com.firstrain.frapi.repository;

import java.util.Map;

import org.springframework.stereotype.Repository;

import com.firstrain.frapi.pojo.Entity;

@Repository
public interface DnbRepository extends EntityBaseServiceRepository {

	public Map<String, Entity> getFRCompanyFromDnbCompanyId(String dnbId, Boolean isOrdered) throws Exception;
}
