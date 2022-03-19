package com.firstrain.web.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.BasicConfigurator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;

import com.firstrain.frapi.domain.SectionSpec;
import com.firstrain.frapi.pojo.wrapper.BaseSet.SectionType;
import com.firstrain.frapi.util.StatusCode;
import com.firstrain.web.response.JSONResponse;
import com.firstrain.web.response.JSONResponse.ResStatus;
import com.firstrain.web.service.core.Constant;

public abstract class BaseRequestParsingService {

	@Autowired
	private ResourceBundleMessageSource messageSource;
	
	protected abstract void validateSectionsPageSpecMap(String s); 
	
	public void validateSectionsPageSpecMap() {
		BasicConfigurator.configure();
		final String[] sectionParam = {"{fr(io:5,ic:10),ft (io:6,ic:6),rl(wi:200,hi:150), "
				+ "gl (wi:220,hi:180),bi,md,twt,tt(wi:250,hi:150)}",
				"{fr,ft}", "{fr}", "{fr }", "{ fr}", "{ fr }", "{fr(io:5,ic:10)}"};
		for (String s : sectionParam) {
			validateSectionsPageSpecMap(s);
		}
	}

	public String getRefinedReqVal(String valParam) {
		String val = valParam;
		if (val == null || (val = val.trim()).isEmpty()) {
			return null;
		}
		return val;
	}

	@SuppressWarnings("rawtypes")
	public JSONResponse getErrorResponse(int errorCodeParam) {
		int errorCode = errorCodeParam;
		if (errorCode < 0) {
			errorCode = StatusCode.INTERNAL_SERVER_ERROR;
		}
		JSONResponse res = new JSONResponse();
		String messageKey = "errorcode." + errorCode;
		String message = messageSource.getMessage(messageKey, null, Locale.getDefault());
		res.setStatus(ResStatus.ERROR);
		res.setMessage(message);
		res.setErrorCode(errorCode);
		res.setVersion(Constant.getVersion());
		return res;
	}

	public String getErrorHtmlResponse(int errorCodeParam) {
		int errorCode = errorCodeParam;
		if (errorCode < 0) {
			errorCode = StatusCode.INTERNAL_SERVER_ERROR;
		}
		String messageKey = "errorcode." + errorCode;
		return messageSource.getMessage(messageKey, null, Locale.getDefault());
	}

	
	public String getRequestScheme(HttpServletRequest req) {
		String scheme = req.getHeader("X-Forwarded-Proto");
		if ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme)) {
			return scheme.toLowerCase();
		}
		return "https";
	}
	
	private void validateExcSectionList(String sec, String sectionParams, List<String> excSectionList) {
		if (excSectionList != null && excSectionList.contains(sec)) {
			throw new IllegalArgumentException(
					"Section not supported for this API : " + sec + " sectionParams : " + sectionParams);
		}
	}

	protected abstract SectionSpec getSectionSpec(String secSpecs);
	
	private Map<SectionType, SectionSpec> getSectionsSpecMap(String spec, String section, String sectionParams,
			boolean needPagination, boolean needBucket, List<String> excSectionList) {
		SectionSpec sectionSpec = getSectionSpec(spec);

		if (needPagination) {
			sectionSpec.setNeedPagination(true);
		}
		if (needBucket && SectionType.valueOf(section).equals(SectionType.E)) {
			sectionSpec.setNeedBucket(needBucket);
		}
		validateExcSectionList(section, sectionParams, excSectionList);
		Map<SectionType, SectionSpec> sectionsMap = new LinkedHashMap<SectionType, SectionSpec>();
		sectionsMap.put(SectionType.valueOf(section), sectionSpec);
		return sectionsMap;
	}
	
	protected SectionType getSectionsType(StringBuilder key, String sectionParams, List<String> excSectionList) {
		String sec = key.toString().toUpperCase();
		validateExcSectionList(sec, sectionParams, excSectionList);
		SectionType sectionType = SectionType.valueOf(sec);
		return sectionType;
	}
	
	protected Map<SectionType, SectionSpec> getSectionsMap(Map<SectionType, SectionSpec> defSectionsMap, 
			String sectionParams) {
		return getSectionsMap(defSectionsMap, sectionParams, false, false, null);
	}
	
	protected Map<SectionType, SectionSpec> getSectionsMap(Map<SectionType, SectionSpec> defSectionsMap,
			String sectionParams, boolean needPagination, boolean needBucket, List<String> excSectionList) {
		Map<SectionType, SectionSpec> sectionsMap = new LinkedHashMap<SectionType, SectionSpec>();
		char[] chars = sectionParams.trim().toCharArray();
		int size = chars.length - 1;
		StringBuilder key = new StringBuilder();
		StringBuilder value = new StringBuilder();
		for (int index = 1; index < size; index++) {
			char character = chars[index];
			if (character == ' ') {
				continue;
			}
			if (character == '(') {
				while (++index < size) {
					character = chars[index];
					if (character == ' ') {
						continue;
					}
					if (character == '(') {
						break;
					}
					if (character == ')') {
						Map<SectionType, SectionSpec> sectionSpecMap = getSectionsSpecMap(value.toString(),
								key.toString().toUpperCase(), sectionParams, needPagination,
								needBucket, excSectionList);
						sectionsMap.putAll(sectionSpecMap);
						key.setLength(0);
						value.setLength(0);
						break;
					} else {
						value.append(character);
					}
				}
				if (value.length() > 0) {
					throw new IllegalArgumentException("Request parameter parse error, expected: \")\" found: \""
							+ character + "\"" + " at: " + value + " sectionParams: " + sectionParams);
				}
			} else if (character == ',') {
				if (key.length() > 0) {
					SectionType sectionType = getSectionsType(key, sectionParams, excSectionList);
					sectionsMap.put(sectionType, defSectionsMap.get(sectionType));
					key.setLength(0);
				}
			} else {
				key.append(character);
			}
		}
		if (key.length() > 0) {
			SectionType sectionType = getSectionsType(key, sectionParams, excSectionList);
			sectionsMap.put(sectionType, defSectionsMap.get(sectionType));
		}
		return sectionsMap;
	}
}