package com.firstrain.web.service.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.firstrain.frapi.domain.Document;
import com.firstrain.frapi.domain.Ng;
import com.firstrain.frapi.pojo.wrapper.DocumentSet;
import com.firstrain.frapi.pojo.wrapper.GetBulk;
import com.firstrain.frapi.util.ServicesAPIUtil;
import com.firstrain.utils.FR_ArrayUtils;
import com.firstrain.utils.JSONUtility;
import com.firstrain.web.pojo.EntityLink;

@Service
public class StorageService {

	private static final Logger LOG = Logger.getLogger(StorageService.class);

	@Value("${storage.service.url}")
	private String storageServiceUrl;
	@Autowired
	private HttpClientService httpClientService;
	@Autowired
	private ServicesAPIUtil servicesAPIUtil;

	public Map<String, GetBulk> getDocFieldsFromStorageService(List<String> ids, boolean isNeedPhrase, boolean isEntityLink) {

		try {

			if (CollectionUtils.isEmpty(ids)) {
				return null;
			}

			Map<String, String> params = new HashMap<String, String>();
			params.put("objectids", FR_ArrayUtils.csvFromStringList(ids));
			StringBuffer sb = new StringBuffer();
			if (isNeedPhrase) {
				sb.append("ng,");
			}
			if (isEntityLink) {
				sb.append("entityLinking,");
			}

			int len = sb.length();
			if (len > 0) {
				sb.setLength(len - 1);
				params.put("scopes", sb.toString());
			}

			Map<String, GetBulk> res =
					httpClientService.postDataInReqBody(storageServiceUrl, "getbulk", params, new TypeReference<Map<String, GetBulk>>() {});

			return res;
		} catch (Exception e) {
			LOG.error("Error in getDocFieldsFromStorageService, where objectids : " + ids + ", isNeedPhrase : " + isNeedPhrase
					+ ", isEntityLink : " + isEntityLink + e);
		}
		return null;
	}

	public List<String> getListOfIdsfromDocumentSet(DocumentSet documentSet) {

		if (documentSet == null) {
			return null;
		}

		List<Document> documents = documentSet.getDocuments();

		if (CollectionUtils.isEmpty(documents)) {
			return null;
		}

		List<String> ids = new ArrayList<String>();
		for (Document doc : documents) {
			ids.add(doc.getId());
		}

		return ids;
	}

	public void populateFieldInDocSet(Map<String, GetBulk> map, DocumentSet documentSet) throws JsonParseException, IOException {

		if (documentSet == null) {
			return;
		}

		List<Document> documents = documentSet.getDocuments();
		if (CollectionUtils.isEmpty(documents)) {
			return;
		}

		for (Document doc : documents) {
			GetBulk getBulk = map.get(doc.getId());
			if (getBulk == null) {
				continue;
			}
			Ng ng = getBulk.getNg();
			if (ng != null && ng.getNgrams() != null) {
				doc.setNgrams(ng.getNgrams());
			}

			Map<String, List<EntityLink>> entityLinkMap = populateEntityLinkMap(getBulk.getEntityLinking());
			List<com.firstrain.frapi.pojo.Entity> entitiesList = doc.getCatEntries();
			if (CollectionUtils.isEmpty(entitiesList) || MapUtils.isEmpty(entityLinkMap)) {
				continue;
			}

			for (com.firstrain.frapi.pojo.Entity e : entitiesList) {
				List<EntityLink> entityLinks = entityLinkMap.get(e.getId());

				if (CollectionUtils.isEmpty(entityLinks)) {
					continue;
				}

				List<EntityLink> entityLinks2 = new ArrayList<EntityLink>();
				for (EntityLink el : entityLinks) {
					EntityLink e2 = new EntityLink();
					String searchToken = servicesAPIUtil.getSearchTokenOfCatId(el.getId());
					if (StringUtils.isEmpty(searchToken)) {
						continue;
					}

					String eLinkScore = el.getEntityLinkingScore();
					Double d = Double.parseDouble(eLinkScore);

					if (d <= 20) {
						continue;
					}

					if (searchToken.startsWith("C:")) {
						e2.setPrimaryCompany(searchToken);
					} else {
						e2.setPrimaryTopic(searchToken);
					}
					e2.setEntityLinkingScore(eLinkScore);
					entityLinks2.add(e2);
				}

				if (CollectionUtils.isEmpty(entityLinks2)) {
					continue;
				}

				e.setEntityLinks(entityLinks2);
			}
		}
	}

	public Map<String, List<EntityLink>> populateEntityLinkMap(Map<String, Map<String, String>> map)
			throws JsonParseException, IOException {


		if (MapUtils.isEmpty(map)) {
			return null;
		}

		Map<String, List<EntityLink>> res = new HashMap<String, List<EntityLink>>();
		for (Map.Entry<String, Map<String, String>> m : map.entrySet()) {

			String key = m.getKey();
			List<EntityLink> entityLinks = createEntityLinkList(res, key); 
			

			Map<String, String> m2 = m.getValue();

			for (Map.Entry<String, String> m3 : m2.entrySet()) {

				EntityLink el = new EntityLink();
				String key2 = m3.getKey();
				el.setId(key2);
				el.setEntityLinkingScore(m3.getValue());
				entityLinks.add(el);

				EntityLink el2 = new EntityLink();
				el2.setId(key);
				el2.setEntityLinkingScore(m3.getValue());

				List<EntityLink> entityLinks2 = createEntityLinkList(res, key2); 
				
				entityLinks2.add(el2);
			}
		}
		return res;
	}
 
	private List<EntityLink> createEntityLinkList(final Map<String, List<EntityLink>> res, final String key2) { 
		List<EntityLink> entityLinks2 = res.get(key2); 
		if (entityLinks2 == null) { 
			entityLinks2 = new ArrayList<EntityLink>(); 
			res.put(key2, entityLinks2); 
		} 
		return entityLinks2; 
	} 
	

	public static void main(String[] args) throws Exception, IOException {
		String json =
				"{\"D:780884138\":{\"ng\":{\"ngrams\":[{\"phrase\":\"international law firm greenberg traurig\",\"phraseCount\":1,\"firstLocation\":\"Q1\"},{\"phrase\":\"california by the daily journal\",\"phraseCount\":1,\"firstLocation\":\"Q0\"},{\"phrase\":\"resources executive\",\"phraseCount\":1,\"firstLocation\":\"Q3\"},{\"phrase\":\"named\",\"phraseCount\":1,\"firstLocation\":\"Q1\"},{\"phrase\":\"practice\",\"phraseCount\":1,\"firstLocation\":\"Q3\"},{\"phrase\":\"resolve\",\"phraseCount\":1,\"firstLocation\":\"Q2\"},{\"phrase\":\"honor\",\"phraseCount\":1,\"firstLocation\":\"Q1\"},{\"phrase\":\"industry leaders\",\"phraseCount\":1,\"firstLocation\":\"Q2\"},{\"phrase\":\"complex issues for clients\",\"phraseCount\":1,\"firstLocation\":\"Q2\"},{\"phrase\":\"firm\",\"phraseCount\":1,\"firstLocation\":\"Q1\"},{\"phrase\":\"appeared\",\"phraseCount\":1,\"firstLocation\":\"Q3\"},{\"phrase\":\"matthew b. gorson\",\"phraseCount\":1,\"firstLocation\":\"Q1\"},{\"phrase\":\"testified\",\"phraseCount\":1,\"firstLocation\":\"Q3\"},{\"phrase\":\"publication\",\"phraseCount\":1,\"firstLocation\":\"Q3\"},{\"phrase\":\"greenberg traurig shareholder charles s.\",\"phraseCount\":1,\"firstLocation\":\"Q0\"},{\"phrase\":\"selected\",\"phraseCount\":1,\"firstLocation\":\"Q1\"},{\"phrase\":\"energy\",\"phraseCount\":1,\"firstLocation\":\"Q2\"},{\"phrase\":\"birenbaum recognized for fourth consecutive\",\"phraseCount\":1,\"firstLocation\":\"Q1\"},{\"phrase\":\"profession\",\"phraseCount\":1,\"firstLocation\":\"Q3\"},{\"phrase\":\"employers\",\"phraseCount\":1,\"firstLocation\":\"Q2\"},{\"phrase\":\"traurig shareholder charles s. birenbaum\",\"phraseCount\":1,\"firstLocation\":\"Q0\"},{\"phrase\":\"birenbaum\",\"phraseCount\":3,\"firstLocation\":\"Q2\"},{\"phrase\":\"well-deserved recognition\",\"phraseCount\":1,\"firstLocation\":\"Q1\"},{\"phrase\":\"commitment\",\"phraseCount\":1,\"firstLocation\":\"Q1\"},{\"phrase\":\"consecutive year for outstanding client\",\"phraseCount\":1,\"firstLocation\":\"Q1\"},{\"phrase\":\"list\",\"phraseCount\":1,\"firstLocation\":\"Q1\"},{\"phrase\":\"testament\",\"phraseCount\":1,\"firstLocation\":\"Q1\"},{\"phrase\":\"keen abilities\",\"phraseCount\":1,\"firstLocation\":\"Q1\"},{\"phrase\":\"chambers usa guide\",\"phraseCount\":1,\"firstLocation\":\"Q3\"},{\"phrase\":\"employment litigators and labor specialists\",\"phraseCount\":1,\"firstLocation\":\"Q3\"},{\"phrase\":\"lawyers in america\",\"phraseCount\":1,\"firstLocation\":\"Q3\"},{\"phrase\":\"consistently lauded\",\"phraseCount\":1,\"firstLocation\":\"Q3\"},{\"phrase\":\"federal courts and various administrative\",\"phraseCount\":1,\"firstLocation\":\"Q3\"},{\"phrase\":\"field\",\"phraseCount\":1,\"firstLocation\":\"Q3\"},{\"phrase\":\"nation's most powerful employment lawyers\",\"phraseCount\":1,\"firstLocation\":\"Q3\"},{\"phrase\":\"employment lawyers in california\",\"phraseCount\":1,\"firstLocation\":\"Q1\"},{\"phrase\":\"chair of northern california\",\"phraseCount\":1,\"firstLocation\":\"Q2\"},{\"phrase\":\"difference\",\"phraseCount\":1,\"firstLocation\":\"Q3\"},{\"phrase\":\"firms\",\"phraseCount\":1,\"firstLocation\":\"Q3\"},{\"phrase\":\"honored\",\"phraseCount\":1,\"firstLocation\":\"Q3\"},{\"phrase\":\"reasons\",\"phraseCount\":1,\"firstLocation\":\"Q1\"},{\"phrase\":\"clients\",\"phraseCount\":2,\"firstLocation\":\"Q1\"},{\"phrase\":\"firm's san francisco\",\"phraseCount\":1,\"firstLocation\":\"Q2\"},{\"phrase\":\"regulators\",\"phraseCount\":1,\"firstLocation\":\"Q2\"},{\"phrase\":\"entire industries\",\"phraseCount\":1,\"firstLocation\":\"Q3\"},{\"phrase\":\"litigating and counseling\",\"phraseCount\":1,\"firstLocation\":\"Q2\"},{\"phrase\":\"defend\",\"phraseCount\":1,\"firstLocation\":\"Q1\"},{\"phrase\":\"super lawyers\",\"phraseCount\":1,\"firstLocation\":\"Q3\"},{\"phrase\":\"daily journal's annual list\",\"phraseCount\":1,\"firstLocation\":\"Q3\"},{\"phrase\":\"outstanding client service\",\"phraseCount\":1,\"firstLocation\":\"Q1\"},{\"phrase\":\"shareholder with the international\",\"phraseCount\":1,\"firstLocation\":\"Q1\"},{\"phrase\":\"construction industries\",\"phraseCount\":1,\"firstLocation\":\"Q2\"},{\"phrase\":\"san francisco\",\"phraseCount\":1,\"firstLocation\":\"Q1\"},{\"phrase\":\"legislation\",\"phraseCount\":1,\"firstLocation\":\"Q3\"},{\"phrase\":\"senior chairman of greenberg traurig\",\"phraseCount\":1,\"firstLocation\":\"Q1\"},{\"phrase\":\"clients in the health\",\"phraseCount\":1,\"firstLocation\":\"Q2\"},{\"phrase\":\"employment lawyer in california\",\"phraseCount\":1,\"firstLocation\":\"Q0\"},{\"phrase\":\"chuck's\",\"phraseCount\":1,\"firstLocation\":\"Q1\"},{\"phrase\":\"courts and various administrative agencies\",\"phraseCount\":1,\"firstLocation\":\"Q3\"},{\"phrase\":\"northern california super lawyers\",\"phraseCount\":1,\"firstLocation\":\"Q3\"},{\"phrase\":\"counseling on both employment\",\"phraseCount\":1,\"firstLocation\":\"Q2\"},{\"phrase\":\"legislative committees\",\"phraseCount\":1,\"firstLocation\":\"Q3\"},{\"phrase\":\"fourth consecutive year for outstanding\",\"phraseCount\":1,\"firstLocation\":\"Q1\"},{\"phrase\":\"politicians\",\"phraseCount\":1,\"firstLocation\":\"Q2\"},{\"phrase\":\"employment\",\"phraseCount\":2,\"firstLocation\":\"Q3\"},{\"phrase\":\"employment and traditional labor matters\",\"phraseCount\":1,\"firstLocation\":\"Q2\"},{\"phrase\":\"interfaced and negotiated\",\"phraseCount\":1,\"firstLocation\":\"Q2\"},{\"phrase\":\"llp\",\"phraseCount\":1,\"firstLocation\":\"Q1\"},{\"phrase\":\"charles s. birenbaum\",\"phraseCount\":1,\"firstLocation\":\"Q1\"},{\"phrase\":\"impactful employment litigators\",\"phraseCount\":1,\"firstLocation\":\"Q3\"},{\"phrase\":\"daily journal's\",\"phraseCount\":1,\"firstLocation\":\"Q1\"},{\"phrase\":\"organizations\",\"phraseCount\":1,\"firstLocation\":\"Q2\"},{\"phrase\":\"selections by the best lawyers\",\"phraseCount\":1,\"firstLocation\":\"Q3\"}]},\"entityLinking\":{\"254877\":{\"311198\":56.67,\"311191\":56.67},\"254895\":{\"311198\":38.5}}}}";

		Map<String, GetBulk> res = JSONUtility.deserialize(json, new TypeReference<Map<String, GetBulk>>() {});


		System.out.println(JSONUtility.serialize(res));

	}

}
