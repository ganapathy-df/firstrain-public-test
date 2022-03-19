package com.firstrain.frapi.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.firstrain.frapi.domain.BlendDunsInput;
import com.firstrain.frapi.pojo.Entity;
import com.firstrain.frapi.repository.DnbRepository;
import com.firstrain.frapi.service.DnbService;
import com.firstrain.utils.FR_ArrayUtils;

@Service
public class DnbServiceImpl implements DnbService {

	private final String DNB_COMPANY_ID_SEPARATOR = "~";

	@Autowired
	@Qualifier("dnbRepositoryImpl")
	private DnbRepository dnbRepository;

	@Override
	public Entity getDnbEntity(String entityId, BlendDunsInput bdi) throws Exception {

		String[] splittedCompanyIds = entityId.split(DNB_COMPANY_ID_SEPARATOR);
		return getFirstMatchedEntity(splittedCompanyIds, bdi);
	}

	private Entity getFirstMatchedEntity(String[] splittedCompanyIds, BlendDunsInput bdi) throws Exception {
		if (splittedCompanyIds == null || splittedCompanyIds.length == 0) {
			return null;
		}
		Map<String, Entity> dnbEntityMap =
				dnbRepository.getFRCompanyFromDnbCompanyId(FR_ArrayUtils.getStringFromStringArr(splittedCompanyIds, " "), Boolean.TRUE);

		if (bdi != null) {
			bdi.setDnbEntityMap(dnbEntityMap);
		}

		if (MapUtils.isNotEmpty(dnbEntityMap)) {
			for (Map.Entry<String, Entity> map : dnbEntityMap.entrySet()) {
				Entity e = map.getValue();
				if (e != null) {
					return e;
				}
			}
		}

		return null;
	}

	private List<Entity> getAllEntities(String[] splittedCompanyIds) throws Exception {

		List<Entity> entityList = null;
		if (splittedCompanyIds == null || splittedCompanyIds.length == 0) {
			return entityList;
		}

		entityList = new ArrayList<Entity>(splittedCompanyIds.length);
		Map<String, Entity> dnbEntityMap =
				dnbRepository.getFRCompanyFromDnbCompanyId(FR_ArrayUtils.getStringFromStringArr(splittedCompanyIds, " "), Boolean.FALSE);
		List<String> splittedCompanyIdList = (List<String>) FR_ArrayUtils.stringArrayToCollection(splittedCompanyIds);

		/* load all entities found on entity solr */
		if (dnbEntityMap != null && !dnbEntityMap.isEmpty()) {
			for (Iterator<String> itr = splittedCompanyIdList.iterator(); itr.hasNext();) {
				String dnbCompanyId = itr.next();
				if (dnbEntityMap.containsKey(dnbCompanyId)) {
					entityList.add(dnbEntityMap.get(dnbCompanyId));
					itr.remove();
				}
			}
		}

		/* For all other entities, return empty Entity objects */
		for (String dnbCompanyId : splittedCompanyIdList) {
			Entity entity = new Entity();
			entity.setDnbEntityId(dnbCompanyId);
			entityList.add(entity);
		}
		return entityList;
	}

	@Override
	public List<Entity> getDnbEntities(String dnbIds) throws Exception {
		String[] splittedCompanyIds = dnbIds.split(DNB_COMPANY_ID_SEPARATOR);
		return getAllEntities(splittedCompanyIds);
	}

	public static void main(String[] args) {
		String[] strArr = new String[2];
		/*
		 * strArr[0] = "0"; strArr[1] = "1"; strArr[2] = "2";
		 * 
		 * String[] strArr1 = new String[1]; strArr1 = strArr;
		 * 
		 * for (String string : strArr1) { System.out.println(string); }
		 */

		Map<String, String> abc = new HashMap<String, String>();
		m1(strArr, abc);

		for (String string : strArr) {
			System.out.println(string);
		}
		System.out.println(abc);

	}

	private static Void m1(String[] strArr1, Map<String, String> abcParam) {

		Map<String, String> abc = abcParam;
		String[] strArr = new String[3];
		strArr[0] = "0";
		strArr[1] = "1";
		strArr[2] = "2";

		strArr1 = strArr;

		Map<String, String> map = new HashMap<String, String>();
		map.put("1", "1");
		map.put("2", "2");

		abc = map;
		return null;
	}
}

