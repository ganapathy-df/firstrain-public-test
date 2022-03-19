package com.firstrain.web.service.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Service;

import com.firstrain.frapi.domain.Parameter;
import com.firstrain.frapi.domain.SectionSpec;
import com.firstrain.frapi.pojo.AuthAPIResponse;
import com.firstrain.frapi.pojo.EnterprisePref;
import com.firstrain.frapi.pojo.wrapper.BaseSet.SectionType;
import com.firstrain.frapi.util.DefaultEnums.CoversationStarterType;
import com.firstrain.frapi.util.ServicesAPIUtil;
import com.firstrain.normalization.CompanyNormalizationUtils;
import com.firstrain.utils.FR_ArrayUtils;
import com.firstrain.utils.JSONUtility;
import com.firstrain.web.pojo.ConversationStartersInputBean;
import com.firstrain.web.pojo.EntityDataHtml;
import com.firstrain.web.pojo.EntityMatchInputBean;
import com.firstrain.web.response.EntityDataResponse;
import com.firstrain.web.service.BaseRequestParsingService;
import com.firstrain.web.util.AuthAPIResponseThreadLocal;
import com.firstrain.web.util.UserInfoThreadLocal;

@Service
public class RequestParsingService extends BaseRequestParsingService {

	private static final Logger LOG = Logger.getLogger(RequestParsingService.class);
	@Autowired
	private ResourceBundleMessageSource messageSource;
	@Value("${app.base.url}")
	private String appBaseUrl;
	@Value("${callback.methods.allowed}")
	private String callbackMethodsCSV;
	private List<String> callbackMethodsList;
	private Set<SectionType> allSectionIDs;
	@Autowired
	private ServicesAPIUtil servicesAPIUtil;
	@Autowired
	private ExcelProcessingHelperService excelProcessingHelperService;
	@Value("${company.business.events.filters}")
	private String companyBusinessEventsFilters;
	@Autowired
	private HttpClientService httpClientService;

	@PostConstruct
	private void init() {
		this.callbackMethodsList = FR_ArrayUtils.csvToArrayList(callbackMethodsCSV);
		allSectionIDs = new HashSet<SectionType>();
		allSectionIDs.add(SectionType.FR);
		allSectionIDs.add(SectionType.FT);
		allSectionIDs.add(SectionType.TT);
		allSectionIDs.add(SectionType.BI);
		allSectionIDs.add(SectionType.MD);
		allSectionIDs.add(SectionType.TWT);
		allSectionIDs.add(SectionType.GL);
		allSectionIDs.add(SectionType.RL);
		allSectionIDs.add(SectionType.WV);
		allSectionIDs.add(SectionType.AC);
		allSectionIDs.add(SectionType.E);
		allSectionIDs.add(SectionType.TE);
	}

	public void setChartDataForMTChart(EntityDataResponse obj, String Id, SectionSpec spec, boolean debug) {
		EntityDataHtml edh = obj.getResult().getHtmlFrag();
		if (edh == null) {
			edh = new EntityDataHtml();
			obj.getResult().setHtmlFrag(edh);
		}

		edh.setMgmtTurnoverChart(getMTChartScript(Id, spec, debug));
	}

	private String getMTChartScript(String Id, SectionSpec spec, boolean debug) {
		AuthAPIResponse authAPIResponse = AuthAPIResponseThreadLocal.get();
		String authKey = authAPIResponse.getAuthKey();
		String userId = "U:" + UserInfoThreadLocal.get().getUserId();

		int height = 90;
		int width = 200;
		if (spec != null) {
			if (spec.getHeight() > 0) {
				height = spec.getHeight();
			}
			if (spec.getWidth() > 0) {
				width = spec.getWidth();
			}
		}
		String file = "mtchart-init.min.js";
		if (debug) {
			file = "mtchart-init.js";
		}
		String urlStr = appBaseUrl + "/" + Constant.getAppName() + "/" + Constant.getVersion();
		return "<script class=\"jq-fr-mtchart\"  src=\"" + urlStr + "/js/" + file + "#authKey=" + authKey + "&frUserId=" + userId + "&id="
				+ Id + "&dim=" + width + "x" + height + "&sp=" + urlStr + "\" type=\"text/javascript\"></script>";
	}

	public void handleSectionsForStaticData(Set<String> keySet, EntityDataResponse obj, String Id) {
		boolean isMetaData = (obj.getResult().getMetaData() != null);

		if (!keySet.contains("fr")) {
			obj.getResult().getData().setFr(null);
			if (isMetaData) {
				obj.getResult().getMetaData().setFr(null);
			}
		}
		if (!keySet.contains("ft")) {
			obj.getResult().getData().setFt(null);
			if (isMetaData) {
				obj.getResult().getMetaData().setFt(null);
			}
		}
		if (!keySet.contains("tt")) {
			obj.getResult().getData().setTt(null);
			if (isMetaData) {
				obj.getResult().getMetaData().setTt(null);
			}
		}
		if (!keySet.contains("bi")) {
			obj.getResult().getData().setBi(null);
			if (isMetaData) {
				obj.getResult().getMetaData().setBi(null);
			}
		}
		if (!keySet.contains("md")) {
			obj.getResult().getData().setMd(null);
			if (isMetaData) {
				obj.getResult().getMetaData().setMd(null);
			}
		}
		if (!keySet.contains("twt")) {
			obj.getResult().getData().setTwt(null);
			if (isMetaData) {
				obj.getResult().getMetaData().setTwt(null);
			}
		}
		if (!keySet.contains("gl")) {
			obj.getResult().getData().setGl(null);
			if (isMetaData) {
				obj.getResult().getMetaData().setGl(null);
			}
		}
		if (!keySet.contains("rl")) {
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

	public EnterprisePref getDefaultSpec() {
		return getDefaultSpec(false, false);
	}

	public EnterprisePref getDefaultSpec(boolean needPagination, boolean needBucket) {
		EnterprisePref enterprisePref = new EnterprisePref();
		enterprisePref.setEnterpriseId(UserInfoThreadLocal.get().getOwnedBy());

		AuthAPIResponse authAPIResponse = AuthAPIResponseThreadLocal.get();
		String specJson = authAPIResponse.getPrefJson();
		if (specJson != null) {
			try {
				Map<SectionType, SectionSpec> sectionsMap = new LinkedHashMap<SectionType, SectionSpec>();

				Map<String, Object> specParam = JSONUtility.deserialize(specJson, Map.class);

				for (SectionType st : allSectionIDs) {
					SectionSpec sectionSpec = new SectionSpec();
					// set default count from pref_json in db
					String str = st.toString().toLowerCase() + "count";
					if (specParam.get(str) != null) {
						sectionSpec.setCount(Short.valueOf(specParam.get(str).toString()));
					}
					sectionSpec.setNeedPagination(needPagination);
					if (st.equals(SectionType.E) && needBucket) {
						sectionSpec.setNeedBucket(needBucket);
					}
					sectionsMap.put(st, sectionSpec);
				}

				enterprisePref.setSectionsMap(sectionsMap);

				if (specParam.get(defaultSpec.INDUSTRY_CLASSIFICATION_ID) != null) {
					enterprisePref.setIndustryClassificationId(
							Short.parseShort(specParam.get(defaultSpec.INDUSTRY_CLASSIFICATION_ID).toString()));
				}
				if (specParam.get(defaultSpec.CUSTOMIZED_CSS_FILE_NAME) != null) {
					enterprisePref.setCustomizedCssFileName(specParam.get(defaultSpec.CUSTOMIZED_CSS_FILE_NAME).toString());
				}
				if (specParam.get(defaultSpec.IS_DUNS_SUPPORTED) != null) {
					enterprisePref.setDnBId(Boolean.parseBoolean(specParam.get(defaultSpec.IS_DUNS_SUPPORTED).toString()));
				}
				if (specParam.get(defaultSpec.PRIVATE_SOURCE_IDS) != null) {
					enterprisePref.setPrivateSourceIdsSSV(specParam.get(defaultSpec.PRIVATE_SOURCE_IDS).toString());
				}
				if (specParam.get(defaultSpec.PUBLIC_SOURCE_IDS) != null) {
					enterprisePref.setPublicSourceIdsSSV(specParam.get(defaultSpec.PUBLIC_SOURCE_IDS).toString());
				}
				if (specParam.get(defaultSpec.SEARCHES_PER_MONITOR) != null) {
					String searchesPerMonitor = (String) specParam.get(defaultSpec.SEARCHES_PER_MONITOR);
					if (searchesPerMonitor != null) {
						int limit = 0;
						try {
							limit = Integer.parseInt(searchesPerMonitor);
						} catch (Exception e) {
						}
						enterprisePref.setSearchesPerMonitor(limit);
					}
				}
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		}
		return enterprisePref;
	}

	public short getDefaultIndustryClassificationId() {
		short iclId = -1;
		AuthAPIResponse authAPIResponse = AuthAPIResponseThreadLocal.get();
		String specJson = authAPIResponse.getPrefJson();
		if (specJson != null) {
			try {
				Map<String, Object> specParam = JSONUtility.deserialize(specJson, Map.class);
				iclId = Short.parseShort(specParam.get(defaultSpec.INDUSTRY_CLASSIFICATION_ID).toString());
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		}
		return iclId;
	}

	public EnterprisePref getSectionsPageSpecMap(String sectionParams) throws Exception {
		return getSectionsPageSpecMap(sectionParams, false, false);
	}
	
	public EnterprisePref getSectionsPageSpecMap(String sectionParams, boolean needPagination, boolean needBucket) throws Exception {
		AuthAPIResponse authResp = AuthAPIResponseThreadLocal.get();
		List<String> excSectionList = authResp.getExcludedSectionList();
		EnterprisePref enterprisePref = getDefaultSpec(needPagination, needBucket);
		Map<SectionType, SectionSpec> defSectionsMap = enterprisePref.getSectionsMap();
		Map<SectionType, SectionSpec> sectionsMap = getSectionsMap(defSectionsMap, sectionParams, 
				needPagination, needBucket, excSectionList);
		if (LOG.isDebugEnabled()) {
			LOG.debug("sectionParams: " + sectionParams);
			LOG.debug("parsed Params: " + sectionsMap);
		}
		enterprisePref.setSectionsMap(sectionsMap);
		return enterprisePref;
	}

	@Override
	protected SectionSpec getSectionSpec(String secSpecs) {
		SectionSpec pagination = new SectionSpec();
		Map<String, String> callbackMethodsMap = new HashMap<String, String>();
		for (String secSpec : secSpecs.split(",")) {
			String[] keyVal = secSpec.split(":");
			if (keyVal.length == 2 && !keyVal[0].isEmpty() && !keyVal[1].isEmpty()) {
				if (keyVal[0].equals("io")) {
					pagination.setStart(Short.parseShort(keyVal[1]));
					pagination.setNeedPagination(true);
				} else if (keyVal[0].equals("ic")) {
					pagination.setCount(Short.parseShort(keyVal[1]));
				} else if (keyVal[0].equals("wi")) {
					pagination.setWidth(Integer.parseInt(keyVal[1]));
				} else if (keyVal[0].equals("hi")) {
					pagination.setHeight(Integer.parseInt(keyVal[1]));
				} else if (callbackMethodsList.contains(keyVal[0].toLowerCase())) {
					callbackMethodsMap.put(keyVal[0].toLowerCase(), keyVal[1]);
				} else {
					throw new IllegalArgumentException("Unknown section spec: \"" + keyVal[0] + "\"");
				}
			} else {
				throw new IllegalArgumentException("Section spec parse error, expected: \"key:value\" found \"" + secSpec + "\"");
			}
		}
		pagination.setCallbackMethodsMap(callbackMethodsMap);
		return pagination;
	}

	@Override
	protected void validateSectionsPageSpecMap(String s) {
		try {
			getSectionsPageSpecMap(s);
		} catch (Exception e) {
			LOG.error("Error while getting sections page specific map" + e.getMessage(), e);
		}
	}
	
	public static void main(String[] args) {
		new RequestParsingService().validateSectionsPageSpecMap();
	}

	@Override
	public String getErrorHtmlResponse(int errorCode) {
		String errorMessage = super.getErrorHtmlResponse(errorCode);
		StringBuilder message = new StringBuilder();
		message.append(
				"<div style='font:bold 14px Arial,Helvetica,sans-serif; color: rgb(102, 102, 102); text-align: center; top: 50%; position: absolute; left: 0px; right: 0px;'>")
				.append(errorMessage).append("</div>");
		return message.toString();
	}

	public String getSerializedMetadata(String sections, String ft, String fq, String results, String htmlFrag) {
		return getSerializedMetadata(sections, ft, fq, results, htmlFrag, null, false, false, false);
	}

	public String getSerializedMetadata(String sections, String ft, String fq, String results, String htmlFrag, String searchToken) {
		return getSerializedMetadata(sections, ft, fq, results, htmlFrag, searchToken, false, false, false);
	}

	public String getSerializedMetadata(String sections, String ft, String fq, String results, String htmlFrag, String searchToken,
			boolean needPhrase, boolean entityLinkApi, boolean blendDUNS) {
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
		if (searchToken != null && !searchToken.isEmpty()) {
			metaDataMap.put("searchToken", searchToken);
		}
		if (needPhrase) {
			metaDataMap.put("needPhrase", "true");
		}
		if (entityLinkApi) {
			metaDataMap.put("entitylinks", "true");
		}
		if (blendDUNS) {
			metaDataMap.put("blendDUNS", "true");
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

	public static class defaultSpec {
		public static final String TOPIC_DIMENSION_FOR_TAGGING = "topdimtagging";
		public static final String INDUSTRY_CLASSIFICATION_ID = "indclsid";
		public static final String PRIVATE_SOURCE_IDS = "pvtsrcids";
		public static final String PUBLIC_SOURCE_IDS = "pubsrcids";
		public static final String SEARCHES_PER_MONITOR = "searchespermonitor";
		public static final String CUSTOMIZED_CSS_FILE_NAME = "custcssname";
		public static final String IS_DUNS_SUPPORTED = "isdnbid";
	}

	public Set<SectionType> intersectSets(Set<SectionType> set1, Set<SectionType> set2) {
		if (set1 == null || set2 == null) {
			return null;
		}
		Set<SectionType> intersection = new HashSet<SectionType>(set1);
		intersection.retainAll(set2);
		return intersection;
	}

	public Set<SectionType> getAllSectionIDs() {
		return allSectionIDs;
	}

	public String[] getQMultiFromReqBody(ConversationStartersInputBean conversationStartersInputBean,
			Map<String, CoversationStarterType> coversationStarterMap) throws Exception {
		String[] qMulti = null;
		Map<String, String> leadCompanyMap = conversationStartersInputBean.getLeadCompany();
		if (leadCompanyMap != null && leadCompanyMap.get("token") != null && leadCompanyMap.get("name") != null) {
			List<Object> qMultiList = new ArrayList<Object>();
			String searchToken = leadCompanyMap.get("token");
			String compName = leadCompanyMap.get("name");
			String compNameNormalized = CompanyNormalizationUtils.cleanupAndNormalizeCompanyName(compName);
			compNameNormalized = compNameNormalized.replaceAll(" ", "");
			if (compNameNormalized.isEmpty()) {
				compNameNormalized = compName;
			}

			int sectorCatId = servicesAPIUtil.getSectorCatIdFromEntitySearchToken(searchToken);
			String blTokens = null;
			if (conversationStartersInputBean.getTargetProductAreas() != null
					&& conversationStartersInputBean.getTargetProductAreas().get("token") != null) {
				blTokens =
						FR_ArrayUtils.getStringFromCollection(conversationStartersInputBean.getTargetProductAreas().get("token"), " OR ");
			}
			StringBuilder query = new StringBuilder();

			if (blTokens != null) {
				// query for target product areas anded with lead company
				query.append(searchToken).append(" AND ").append(blTokens);
				qMultiList.add(query.toString());
				coversationStarterMap.put(query.toString(), CoversationStarterType.COMPANY_NEWS);
			}

			// query for target product areas anded with lead company's industry
			if (conversationStartersInputBean.getIndustry() != null) {
				List<String> tokens = conversationStartersInputBean.getIndustry().get("token");
				if (tokens != null && blTokens != null) {
					String industrySearchToken = tokens.get(0);
					query = createQuery(industrySearchToken, blTokens, qMultiList); 
					
					coversationStarterMap.put(query.toString(), CoversationStarterType.INDUSTRY_NEWS);
				}
			}

			// CXO Commentary ANDED WITH Target Product Areas
			String cxoTokens = excelProcessingHelperService.getCXOForSector(sectorCatId);
			query = addQuery(cxoTokens, blTokens, qMultiList, coversationStarterMap, CoversationStarterType.CXO_COMMENTARY);

			// query for Business Events of Lead Company
			query = createQuery(searchToken, companyBusinessEventsFilters, qMultiList); 
			
			coversationStarterMap.put(query.toString(), CoversationStarterType.BUSINESS_EVENTS);

			if (conversationStartersInputBean.getLead() != null) {
				// query for Identified Lead�s commentary
				if (conversationStartersInputBean.getLead().get("name") != null) {
					String personName = conversationStartersInputBean.getLead().get("name");
					if (personName != null && !personName.isEmpty()) {
						query = new StringBuilder();
						query.append("\"" + compNameNormalized + "\"").append(" AND ").append("\"" + personName + "\"");
						qMultiList.add(query.toString());
						coversationStarterMap.put(query.toString(), CoversationStarterType.LEAD_COMMENTARY);
					}
				}

				// PEER Commentary ANDED WITH Target Product Areas
				String jobTitles = excelProcessingHelperService.getTitlesForJob(conversationStartersInputBean.getLead().get("designation"));
				query = addQuery(jobTitles, blTokens, qMultiList, coversationStarterMap, CoversationStarterType.PEER_COMMENTARY);
			}

			qMulti = FR_ArrayUtils.arrayListToStringArray(qMultiList);
		} else {
			// if lead company not provided throw exception
			throw new Exception();
		}
		return qMulti;
	}

	private StringBuilder addQuery(final String jobTitles, final String blTokens, final List<Object> qMultiList, final Map<String, CoversationStarterType> coversationStarterMap, final CoversationStarterType coversationStarterType) {
		StringBuilder query = null;
		if (jobTitles != null && blTokens != null) {
			query = createQuery(jobTitles, blTokens, qMultiList); 
			
			coversationStarterMap.put(query.toString(), coversationStarterType);
		}
		return query;
	}
 
	private StringBuilder createQuery(final String industrySearchToken, final String blTokens, final List<Object> qMultiList) { 
		StringBuilder query = new StringBuilder(); 
		query.append(industrySearchToken).append(" AND ").append(blTokens); 
		qMultiList.add(query.toString()); 
		return query; 
	} 
	

	public String getQForEntityMatch(EntityMatchInputBean entityMatchInputBean) throws Exception {
		StringBuilder q = new StringBuilder();
		EntityMatchInputBean.EntityInput entity = entityMatchInputBean.getCompany();
		if (entity != null && (entity.getName() != null || entity.getTicker() != null || entity.getCikCode() != null
				|| entity.getDuns() != null || entity.getIsin() != null || entity.getValeron() != null || entity.getSedol() != null
				|| entity.getHomePage() != null)) {

			if (entity.getTicker() != null && !entity.getTicker().isEmpty()) {
				q.append("attrPrimaryTicker:").append("\"").append(entity.getTicker()).append("\"").append(" OR ");
			}
			if (entity.getCikCode() != null && !entity.getCikCode().isEmpty()) {
				// append 0 to make cikCode of 10 digits
				String cikCode = preppendCharInString(entity.getCikCode(), '0', 10);
				q.append("attrCik:").append(cikCode).append(" OR ");
			}
			if (entity.getDuns() != null && !entity.getDuns().isEmpty()) {
				// append 0 to make cikCode of 9 digits
				String duns = preppendCharInString(entity.getDuns(), '0', 9);
				q.append("dnbCompanyId:").append(duns).append(" OR ");
			}
			if (entity.getIsin() != null && !entity.getIsin().isEmpty()) {
				q.append("attrIsin:").append(entity.getIsin()).append(" OR ");
			}
			if (entity.getValeron() != null && !entity.getValeron().isEmpty()) {
				q.append("attrValoren:").append(entity.getValeron()).append(" OR ");
			}
			if (entity.getSedol() != null && !entity.getSedol().isEmpty()) {
				q.append("attrSedol:").append(entity.getSedol()).append(" OR ");
			}
			if (q.length() <= 0 && (entity.getName() == null || entity.getName().isEmpty())
					&& (entity.getHomePage() == null || entity.getHomePage().isEmpty())) {
				throw new Exception();
			}
			if (q.toString().endsWith(" OR ")) {
				q.setLength(q.length() - 4);
			}
		} else {
			// if any of the primary input is not provided throw exception
			throw new Exception("getQForEntityMatch: Required parameter is missing");
		}
		return q.toString();
	}

	private String preppendCharInString(String inputStr, char charToAppend, int noOfCharsToAppend) {
		StringBuilder builder = new StringBuilder(inputStr);
		// Loop and append values.
		while (builder.length() < noOfCharsToAppend) {
			builder.insert(0, charToAppend);
		}
		return builder.toString();
	}

	public Parameter getParameterForMultiSearch(int qMultiLength) throws Exception {
		Parameter parameter = new Parameter();
		parameter.setNeedImage(true);
		parameter.setNeedMatchedEntities(true);
		parameter.setNeedPagination(false);
		parameter.setDays(60);
		parameter.setStart((short) 0);
		// after merging the documents from all buckets max count shouldn't be greater than 300
		// divide by 3 -> in case of needPagination false, count is multiplied by 3 in search
		// service API
		int count = (Constant.MULTI_SEARCH_PAGINATION_COUNT / qMultiLength) / 3;
		parameter.setCount((short) count);
		return parameter;
	}
}