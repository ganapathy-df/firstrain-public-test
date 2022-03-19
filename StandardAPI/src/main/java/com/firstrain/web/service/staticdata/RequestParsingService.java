package com.firstrain.web.service.staticdata;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Service;

import com.firstrain.frapi.domain.SectionSpec;
import com.firstrain.frapi.pojo.wrapper.BaseSet.SectionType;
import com.firstrain.utils.JSONUtility;
import com.firstrain.web.pojo.Content;
import com.firstrain.web.pojo.Document;
import com.firstrain.web.pojo.EntityData;
import com.firstrain.web.pojo.EntityDataHtml;
import com.firstrain.web.pojo.EntityStandard;
import com.firstrain.web.pojo.MetaData;
import com.firstrain.web.pojo.Tweet;
import com.firstrain.web.response.EntityDataResponse;
import com.firstrain.web.service.BaseRequestParsingService;

@Service
public class RequestParsingService extends BaseRequestParsingService {

	private static final Logger LOG = Logger.getLogger(RequestParsingService.class);

	@Autowired
	private ResourceBundleMessageSource messageSource;
	@Value("${app.base.url}")
	private String appBaseUrl;

	public void setChartDataForHtml(Set<SectionType> keySet, EntityDataResponse obj, String Id) {
		EntityDataHtml edh = new EntityDataHtml();
		obj.getResult().setHtmlFrag(edh);
		if (keySet.contains(SectionType.valueOf("TT")) && Id.startsWith("M:")) {
			edh.setTt(getChartScript("tt", Id));
		}
		if (keySet.contains(SectionType.valueOf("BI"))) {
			edh.setBi(getChartScript("bi", Id));
		}
		if (keySet.contains(SectionType.valueOf("MD"))) {
			edh.setMd(getChartScript("md", Id));
		}
		if (keySet.contains(SectionType.valueOf("TWT"))) {
			edh.setTwt(getChartScript("twt", Id));
		}
		if (keySet.contains(SectionType.valueOf("GL"))) {
			edh.setGl(getChartScript("gl", Id));
		}
		if (keySet.contains(SectionType.valueOf("RL"))) {
			edh.setRl(getChartScript("rl", Id));
		}
	}

	private String getChartScript(String type, String Id) {
		return "<script class=\"jq-fr-visualization\"  src=\"" + appBaseUrl + "/" + Constant.getAppName() + "/" + Constant.getVersion()
				+ "/js/visual-init.js#authKey=Smi8E2GgBS16cl9w2q4aEIu0&frUserId=U:1234&id=" + Id + "&events=click:callback0" + type
				+ ",mouseenter:callback1" + type + ",mouseleave:callback2" + type + "&dim=165x150&chDim=250x150&chTypes=" + type + "&sp="
				+ appBaseUrl + "/" + Constant.getAppName() + "/" + Constant.getVersion() + "\" type=\"text/javascript\"></script>";
	}

	public void handleSectionsForStaticData(Set<SectionType> keySet, EntityDataResponse obj, String Id) {
		boolean isMetaData = (obj.getResult().getMetaData() != null);

		if (!keySet.contains(SectionType.valueOf("FR"))) {
			obj.getResult().getData().setFr(null);
			if (isMetaData) {
				obj.getResult().getMetaData().setFr(null);
			}
		}
		if (!keySet.contains(SectionType.valueOf("E"))) {
			obj.getResult().getData().setE(null);
			if (isMetaData) {
				// obj.getResult().getMetaData().setE(null);
			}
		}
		if (!keySet.contains(SectionType.valueOf("TE"))) {
			obj.getResult().getData().setTe(null);
			if (isMetaData) {
				// obj.getResult().getMetaData().setTe(null);
			}
		}
		if (!keySet.contains(SectionType.valueOf("FT"))) {
			obj.getResult().getData().setFt(null);
			if (isMetaData) {
				obj.getResult().getMetaData().setFt(null);
			}
		}
		if (!keySet.contains(SectionType.valueOf("TT")) || !Id.startsWith("M:")) {
			obj.getResult().getData().setTt(null);
			if (isMetaData) {
				obj.getResult().getMetaData().setTt(null);
			}
		}
		if (!keySet.contains(SectionType.valueOf("BI"))) {
			obj.getResult().getData().setBi(null);
			if (isMetaData) {
				obj.getResult().getMetaData().setBi(null);
			}
		}
		if (!keySet.contains(SectionType.valueOf("MD"))) {
			obj.getResult().getData().setMd(null);
			if (isMetaData) {
				obj.getResult().getMetaData().setMd(null);
			}
		}
		if (!keySet.contains(SectionType.valueOf("TWT"))) {
			obj.getResult().getData().setTwt(null);
			if (isMetaData) {
				obj.getResult().getMetaData().setTwt(null);
			}
		}
		if (!keySet.contains(SectionType.valueOf("GL"))) {
			obj.getResult().getData().setGl(null);
			if (isMetaData) {
				obj.getResult().getMetaData().setGl(null);
			}
		}
		if (!keySet.contains(SectionType.valueOf("RL"))) {
			obj.getResult().getData().setRl(null);
			if (isMetaData) {
				obj.getResult().getMetaData().setRl(null);
			}
		}
	}

	/*
	 * public static Map<String, Pagination> getSectionsPageSpecMap(String sectionParam) throws Exception { Map<String, Pagination>
	 * sectionsMap = new HashMap<String, Pagination>();
	 * 
	 * sectionParam = sectionParam.replaceAll("\\{", "").replaceAll("\\}", ""); String[] vals = null; if(sectionParam.contains("),") ) {
	 * vals = sectionParam.split("\\),"); } else { vals = sectionParam.split(","); } for(String val : vals) { if(val.contains("(") &&
	 * !val.endsWith(")")) { val = val + ")"; } System.out.println(val); int startIndex = val.indexOf('('); int endIndex = val.indexOf(')');
	 * if(startIndex > 0 && endIndex > 0) { String key = val.substring(0, startIndex); String valueCSV = val.substring(startIndex + 1,
	 * endIndex); String[] values = valueCSV.split(","); Pagination pagination = new Pagination(); for(String sec : values) { String[]
	 * keyVal = sec.split(":"); if(keyVal.length == 2) { if(keyVal[0].equals("io")) { pagination.setStart(Integer.parseInt(keyVal[1])); }
	 * else if (keyVal[0].equals("ic")) { pagination.setCount(Integer.parseInt(keyVal[1])); } } else { throw new Exception(
	 * "Invalid format specified: "+ sectionParam); } } sectionsMap.put(key, pagination); System.out.println(key); } else { String key =
	 * val; sectionsMap.put(key, null); System.out.println(key); } }
	 * 
	 * return sectionsMap; }
	 */

	private Map<SectionType, SectionSpec> getDefaultSpec() {
		Map<SectionType, SectionSpec> sectionsMap = new LinkedHashMap<SectionType, SectionSpec>();
		SectionSpec sectionSpec = new SectionSpec();

		sectionSpec.setStart((short) 0);
		sectionSpec.setCount((short) 10);
		sectionsMap.put(SectionType.FR, sectionSpec);

		sectionSpec.setStart((short) 0);
		sectionSpec.setCount((short) 10);
		sectionsMap.put(SectionType.FT, sectionSpec);
		return sectionsMap;
	}

	public Map<SectionType, SectionSpec> getSectionsPageSpecMap(String sectionParams) throws Exception {
		Map<SectionType, SectionSpec> defSectionsMap = getDefaultSpec();
		Map<SectionType, SectionSpec>  sectionsMap = getSectionsMap(defSectionsMap, sectionParams);
		if (LOG.isDebugEnabled()) {
			LOG.debug("sectionParams: " + sectionParams);
			LOG.debug("parsed Params: " + sectionsMap);
		}
		return sectionsMap;
	}

	@Override
	protected SectionSpec getSectionSpec(String secSpecs) {
		SectionSpec pagination = new SectionSpec();
		for (String secSpec : secSpecs.split(",")) {
			String[] keyVal = secSpec.split(":");
			if (keyVal.length == 2 && !keyVal[0].isEmpty() && !keyVal[1].isEmpty()) {
				if (keyVal[0].equals("io")) {
					pagination.setStart(Short.parseShort(keyVal[1]));
				} else if (keyVal[0].equals("ic")) {
					pagination.setCount(Short.parseShort(keyVal[1]));
				} else if (keyVal[0].equals("wi")) {
					pagination.setWidth(Integer.parseInt(keyVal[1]));
				} else if (keyVal[0].equals("hi")) {
					pagination.setHeight(Integer.parseInt(keyVal[1]));
				} else {
					throw new IllegalArgumentException("Unknown section spec: \"" + keyVal[0] + "\"");
				}
			} else {
				throw new IllegalArgumentException("Section spec parse error, expected: \"key:value\" found \"" + secSpec + "\"");
			}
		}
		return pagination;
	}

	@Override
	protected void validateSectionsPageSpecMap(String s) {
		try {
			new RequestParsingService().getSectionsPageSpecMap(s);
		} catch (Exception e) {
			LOG.error("Error while getting sections page specific map" + e.getMessage(), e);
		}
	}
	
	public static void main(String[] args) throws Exception {
		new RequestParsingService().validateSectionsPageSpecMap();
	}

	public String getSerializedMetadata(String sections, String ft, String fq, String results, String htmlFrag) {
		Map<String, String> metaDataMap = new HashMap<String, String>();
		if (sections != null && !sections.isEmpty()) {
			metaDataMap.put("sections", sections);
		}
		if (ft != null && !ft.isEmpty()) {
			metaDataMap.put("ft", ft);
		}
		if (fq != null && !fq.isEmpty()) {
			metaDataMap.put("fq", fq);
		}
		if (results != null && !results.isEmpty()) {
			metaDataMap.put("results", results);
		}
		if (htmlFrag != null && !htmlFrag.isEmpty()) {
			metaDataMap.put("htmlFrag", htmlFrag);
		}
		if (metaDataMap != null && !metaDataMap.isEmpty()) {
			try {
				return JSONUtility.serialize(metaDataMap);
			} catch (Exception e) {
				LOG.error(
						"Error in getSerializedMetadata, sections" + sections + ", ft : " + ft + ", fq : " + fq + ", results : " + results,
						e);
			}
		}
		return null;
	}

	public MetaData getMetaDataEntitiyResponse(EntityDataResponse entityDataResponse) {
		List<Document> documentLst = null;
		List<Tweet> tweetLst = null;
		if (entityDataResponse.getResult() != null) {
			EntityData entityData = entityDataResponse.getResult().getData();
			if (entityData != null) {
				Content c1 = entityData.getFr();
				Content c2 = entityData.getFt();
				if (c1 != null) {
					documentLst = c1.getDocuments();
				}
				if (c2 != null) {
					tweetLst = c2.getTweets();
				}
			}
		}
		return getMetaData(documentLst, tweetLst);
	}

	public MetaData getMetaData(List<Document> docuemntLst, List<Tweet> tweetLst) {
		MetaData metaData = new MetaData();

		// set fr
		if (docuemntLst != null && !docuemntLst.isEmpty()) {
			StringBuilder name = new StringBuilder();
			for (Document document : docuemntLst) {
				if (document != null) {
					List<EntityStandard> entities = document.getEntity();
					if (entities != null && !entities.isEmpty()) {
						for (EntityStandard e : entities) {
							if (e != null) {
								name.append(e.getName() + "|");
							}
						}
					}
				}
			}
			metaData.setFr(name.substring(0, name.length() - 1));
		}

		// set ft
		if (tweetLst != null && !tweetLst.isEmpty()) {
			StringBuilder name = new StringBuilder();
			for (Tweet tweet : tweetLst) {
				if (tweet != null) {
					EntityStandard e = tweet.getEntity();
					if (e != null) {
						name.append(e.getName() + "|");
					}
				}
			}
			metaData.setFt(name.substring(0, name.length() - 1));
		}
		return metaData;
	}

	public static class defaultSpec {
		public static final String TOPIC_DIMENSION_FOR_TAGGING = "topdimtagging";
		public static final String FR_COUNT = "frcount";
		public static final String FR_START = "frstart";
		public static final String FT_COUNT = "ftcount";
		public static final String FT_START = "ftstart";
		public static final String INDUSTRY_CLASSIFICATION_ID = "indclsid";
		public static final String PRIVATE_SOURCE_IDS = "pvtsrcids";
		public static final String PUBLIC_SOURCE_IDS = "pubsrcids";
	}
}