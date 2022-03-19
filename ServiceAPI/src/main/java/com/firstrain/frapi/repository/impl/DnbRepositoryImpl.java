package com.firstrain.frapi.repository.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.firstrain.frapi.pojo.Entity;
import com.firstrain.frapi.repository.DnbRepository;
import com.firstrain.frapi.util.ConvertUtil;
import com.firstrain.obj.IEntityInfo;
import com.firstrain.obj.IEntityInfoCache;
import com.firstrain.solr.client.util.SolrServerReader;

@Repository
@Qualifier("dnbRepositoryImpl")
public class DnbRepositoryImpl extends EntityBaseServiceRepositoryImpl implements DnbRepository {

	@Autowired
	private ConvertUtil convertUtil;

	@Override
	public Map<String, Entity> getFRCompanyFromDnbCompanyId(String dnbIds, Boolean isOrdered) throws Exception {
		if (dnbIds.isEmpty()) {
			return null;
		}
		int companyId = -1;
		Map<String, Entity> dnbEntityMap = null;
		String query = "dnbCompanyId:(" + dnbIds + ")";
		SolrDocumentList solrDocumentList =
				SolrServerReader.retrieveNSolrDocs(this.getEntitySolrServer(), query, 0, 10,
						"attrCompanyId,dnbCompanyId,attrCountry,bizLineCatIds");
		IEntityInfoCache entityInfoCache = this.getEntityInfoCache();
		if (solrDocumentList != null && !solrDocumentList.isEmpty()) {
			dnbEntityMap = new HashMap<String, Entity>();
			for (SolrDocument doc : solrDocumentList) {
				String companyIdStr = (String) doc.getFieldValue("attrCompanyId");
				String dnbCompanyId = (String) doc.getFieldValue("dnbCompanyId");
				String country = (String) doc.getFieldValue("attrCountry");
				Collection<Integer> bizLineCatIds = (Collection) doc.getFieldValues("bizLineCatIds");

				companyId = Integer.parseInt(companyIdStr);
				IEntityInfo entityInfo = entityInfoCache.companyIdToEntity(companyId);
				Entity entity = convertUtil.convertEntityInfo(entityInfo);
				entity.setDnbEntityId(dnbCompanyId);
				entity.setCountry(country);
				entity.setBizLineCatIds(bizLineCatIds);
				dnbEntityMap.put(dnbCompanyId, entity);
			}
		}

		if (Boolean.FALSE.equals(isOrdered)) {
			return dnbEntityMap;
		}

		Map<String, Entity> dnbEntityLinkedMap = new LinkedHashMap<String, Entity>();
		String[] dnbIdsArr = dnbIds.split(" ");
		for (String id : dnbIdsArr) {
			dnbEntityLinkedMap.put(id, dnbEntityMap.get(id));
		}
		return dnbEntityLinkedMap;
	}
}
