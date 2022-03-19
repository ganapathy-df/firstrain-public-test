package com.firstrain.web.service.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.formula.functions.T;
import org.codehaus.jackson.JsonParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Service;

import com.firstrain.db.obj.PrivateEntity;
import com.firstrain.frapi.customapirepository.impl.PrivateEntityRepositoryImpl;
import com.firstrain.frapi.customapiservice.TakeDownService;
import com.firstrain.frapi.domain.Entity;
import com.firstrain.frapi.util.StatusCode;
import com.firstrain.utils.JSONUtility;
import com.firstrain.web.exception.CustomExceptionError;
import com.firstrain.web.pojo.Author;
import com.firstrain.web.pojo.CreateInputBean;
import com.firstrain.web.pojo.Definition;
import com.firstrain.web.pojo.InstructionDefinition;
import com.firstrain.web.response.JSONResponse;
import com.firstrain.web.response.JSONResponse.ResStatus;
import com.firstrain.web.wrapper.EntityListWrapperData;

@Service
public class RequestParsingService {

	private static final Logger LOG = Logger.getLogger(RequestParsingService.class);
	private static final String pattern = "^[a-zA-Z0-9]*$";
	private static final String CASE_SENSITIVE = "case-sensitive";
	private static final String CASE_INSENSITIVE = "case-insensitive";
	private static final String STEM = "stem";
	private static final String PROXIMITY = "proximity";
	private static final String COMPANIES = "companies";
	private static final String TOPICS = "topics";
	private static final String STATE_ACTIVE = "ACTIVE";
	private static final String STATE_INACTIVE = "INACTIVE";
	private static final String STATE_LIVE = "LIVE";

	@Autowired
	private ResourceBundleMessageSource messageSource;
	@Autowired
	private PWBrandMapping pwBrandMapping;
	@Autowired
	private FREntityMapping frEntityMapping;
	@Autowired
	private TakeDownService takeDownService;
	@Autowired
	private ServletContext servletContext;
	@Autowired
	private PrivateEntityRepositoryImpl privateEntityRepositoryImpl;
	@Value("${api.version}")
	private String version;

	@PostConstruct
	private void init() {
		servletContext.setAttribute("version", version);
	}

	public String getRefinedReqVal(Object obj) {

		if (obj == null) {
			return null;
		}

		String val = obj.toString();
		return getRefinedVal(val);
	}

	public String getRefinedReqVal(String val) {
		return getRefinedVal(val);
	}

	private String getRefinedVal(String valParam) {
		String val = valParam;
		if (val == null || (val = val.trim()).isEmpty()) {
			return null;
		}
		return val;
	}

	public JSONResponse getErrorResponseForCatgorizerService(int errorCode, String guid, String docId) {
		return getErrorResponseForCatgorizerService(errorCode, guid, docId, null);
	}

	public JSONResponse getErrorResponseForCatgorizerService(int errorCode, String guid, String docId, String message) {
		return getErrorResponseForCatgorizerService(errorCode, guid, docId, message, false);
	}

	public JSONResponse getErrorResponseForCatgorizerService(int errorCode, String guid, String docId, String message,
			boolean isCustomMessage) {
		JSONResponse res = new JSONResponse();

		EntityListWrapperData resp = new EntityListWrapperData();
		// set guid
		if (StringUtils.isNotEmpty(guid)) {
			resp.setGuid(guid);
			res.setResult(resp);
		} else if (StringUtils.isNotEmpty(docId)) {
			resp.setDocId(docId);
			res.setResult(resp);
		}

		res.setStatus(ResStatus.ERROR);
		res.setErrorCode(errorCode);
		String messageStr =
				StringUtils.isNotEmpty(message) ? message : messageSource.getMessage(("errorcode." + errorCode), null, Locale.getDefault());
		if (isCustomMessage && StringUtils.isNotEmpty(message)) {
			Object[] args = new Object[1];
			args[0] = message;
			messageStr = messageSource.getMessage(("errorcode." + errorCode), args, Locale.getDefault());
		}
		res.setMessage(messageStr);
		res.setVersion(version);
		return res;
	}

	@SuppressWarnings("rawtypes")
	public JSONResponse<T> getErrorResponse(int errorCodeParam, T t) {
		int errorCode = errorCodeParam;
		if (errorCode < 0) {
			errorCode = StatusCode.INTERNAL_SERVER_ERROR;
		}
		JSONResponse<T> res = new JSONResponse<T>();
		res.setResult(t);
		String messageKey = "errorcode." + errorCode;
		String message = messageSource.getMessage(messageKey, null, Locale.getDefault());
		res.setStatus(ResStatus.ERROR);
		res.setMessage(message);
		res.setErrorCode(errorCode);
		res.setVersion(version);
		return res;
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
		res.setVersion(version);
		return res;
	}

	public String getSerializedMetadata(Map<String, Object> reqMap) {
		if (reqMap != null && reqMap.isEmpty()) {
			try {
				return JSONUtility.serialize(reqMap);
			} catch (Exception e) {
				LOG.error("Error in getSerializedMetadata : ", e);
			}
		}
		return null;
	}

	public void validatePrivateEntityInputFields(CreateInputBean inputbean, boolean isUpdate) throws Exception {
		
		// check entity name and type
		if (!isUpdate && (StringUtils.isEmpty(inputbean.getName()) || StringUtils.isEmpty(inputbean.getType()))) {
			throw new CustomExceptionError(421);
		}

		validateAuthor(inputbean);

		if (!isUpdate) {
			validateTaxonomyDirective(inputbean);
		}

		Definition def = inputbean.getDefinition();
		// TP + ID + MR must be present

		// Match rule: first check what we have in a match rule
		// if match rule is empty
		// if phases or instruction is present in a match rule then phrases or instruction is mandatory else non - mandatory.
		// phrases: Empty - allowed now
		// instructions: Empty - are also allowed

		if (def == null || StringUtils.isEmpty(def.getMatchRule())
				|| (MapUtils.isEmpty(def.getPhrases()) && !MapUtils.isEmpty(def.getInstructions()))
				|| (!MapUtils.isEmpty(def.getPhrases()) && isEmptyVal(def.getPhrases().values()))) {
			throw new CustomExceptionError(406);
		}

		Set<String> keySet = null;
		if (!MapUtils.isEmpty(def.getInstructions())) {
			keySet = def.getInstructions().keySet();
			for (Map.Entry<String, InstructionDefinition> map : def.getInstructions().entrySet()) {

				String key = map.getKey();
				InstructionDefinition iDef = map.getValue();

				// key should be alpha numneric
				if (!isAlphaNumeric(key)) {
					throw new CustomExceptionError(407);
				}

				// key check equals AND, OR, NOT
				if (key.equals("AND") || key.equals("OR") || key.equals("NOT")) {
					throw new CustomExceptionError(408);
				}

				// Definition must be present
				if (iDef == null || def.getPhrases() == null) {
					throw new CustomExceptionError(415);
				}
				// Target phrase must be present
				if (iDef.getTargetPhrase() == null) {
					throw new CustomExceptionError(406);
				}

				// TP + QP must be present in definition
				if ((iDef.getTargetPhrase() != null && def.getPhrases().get(iDef.getTargetPhrase()) == null)
						|| (iDef.getQualifyingPhrase() != null && def.getPhrases().get(iDef.getQualifyingPhrase()) == null)) {
					throw new CustomExceptionError(409);
				}
				
				// if proximity present then QP must be present
				List<String> instruction = iDef.getInstruction();
				if (CollectionUtils.isEmpty(instruction)) {
					throw new CustomExceptionError(425);
				}
				
				for (String str : instruction) {

					if (!(str.equals(CASE_SENSITIVE) || str.equals(CASE_INSENSITIVE) || str.equals(STEM) || str.startsWith(PROXIMITY))) {
						throw new CustomExceptionError(414);
					}

					if (str.startsWith(PROXIMITY) && iDef.getQualifyingPhrase() == null) {
						throw new CustomExceptionError(410);
					}
				}
			}
		}

		// Match Rule Check
		String matchRule = inputbean.getDefinition().getMatchRule();
		String[] matchRuleTokens = matchRule.split(" ");
		for (String matchToken : matchRuleTokens) {
			matchToken = matchToken.trim().replace("(", "").replace(")", "");
			if (matchToken.equalsIgnoreCase("AND") || matchToken.equalsIgnoreCase("OR") || matchToken.equalsIgnoreCase("NOT")) {
				continue;
			} else if (matchToken.contains(":")) {
				// check for valid supported entities
				if (matchToken.contains("-PW-")) {
					validSupportedSearchTokens(matchToken);
				} else {
					// check in entityInfoCache
					if (matchToken.startsWith("C:")) {
						Entity e = frEntityMapping.getCatIdVsFREntity(matchToken);
						if (e == null) {
							throw new CustomExceptionError(matchToken, 411, true);
						}
					}
					validateSearchTokenFromEntityInfoCache(matchToken, false, null);
				}
			} else if ((keySet == null && matchToken != null) || (keySet != null && !keySet.contains(matchToken))) {
				// error
				throw new CustomExceptionError(412);
			}
		}
	}

	public void validateSearchTokenFromEntityInfoCache(String matchToken, boolean isTopicOnly, CreateInputBean inputBean) {
		if (Boolean.FALSE.equals(takeDownService.isValidSearchToken(matchToken, isTopicOnly, inputBean))) {
			throw new CustomExceptionError(matchToken, 411, true);
		}
	}

	public void validateTaxonomyDirectiveForList(CreateInputBean inputbean) {
		String taxonomyDirective = inputbean.getTaxonomyDirective();
		// check taxonomy directive
		if (StringUtils.isEmpty(taxonomyDirective)) {
			throw new CustomExceptionError(427);
		}

		inputbean.setTaxonomyDirective(taxonomyDirective.toLowerCase());
		taxonomyDirective = inputbean.getTaxonomyDirective();
		List<String> taxonomyDirectiveLst = new ArrayList<String>();

		if (!taxonomyDirective.contains("topics") && !taxonomyDirective.contains("companies")) {
			taxonomyDirectiveLst.add(taxonomyDirective + TOPICS);
			taxonomyDirectiveLst.add(taxonomyDirective + COMPANIES);
		} else {
			taxonomyDirectiveLst.add(taxonomyDirective);
		}

		inputbean.setTaxonomyDirectiveLst(taxonomyDirectiveLst);
		for (String taxonomyDirectiveStr : taxonomyDirectiveLst) {
			validateTaxonomyDirective(inputbean, taxonomyDirectiveStr);
		}
	}

	public void validateTaxonomyDirective(CreateInputBean inputbean, String taxonomyDirective) {
		LOG.info("taxonomyDirective looking from excel : " + taxonomyDirective);
		String brandKey = pwBrandMapping.getPwKey(taxonomyDirective);
		String brandInitial = pwBrandMapping.getPwBrandInitials(taxonomyDirective);
		LOG.info("taxonomyDirective : " + taxonomyDirective + ", brandKey : " + brandKey + ", brandInitial : " + brandInitial);
		inputbean.setBrandInitial(brandInitial);

		if (StringUtils.isEmpty(brandKey)) {
			throw new CustomExceptionError(420);
		}
	}

	public void validateTaxonomyDirective(CreateInputBean inputbean) {
		String taxonomyDirective = inputbean.getTaxonomyDirective();
		// check taxonomy directive
		if (StringUtils.isNotEmpty(taxonomyDirective)) {
			inputbean.setTaxonomyDirective(taxonomyDirective.toLowerCase());
		}

		taxonomyDirective = inputbean.getTaxonomyDirective();
		String type = inputbean.getType();
		if ("topic".equalsIgnoreCase(type)) {
			if (!taxonomyDirective.contains("topics")) {
				inputbean.setTaxonomyDirective(taxonomyDirective + TOPICS);
			}
		} else if ("company".equalsIgnoreCase(type)) {
			if (!taxonomyDirective.contains("companies")) {
				inputbean.setTaxonomyDirective(taxonomyDirective + COMPANIES);
			}
		} else {
			throw new CustomExceptionError(421);
		}

		taxonomyDirective = inputbean.getTaxonomyDirective();
		validateTaxonomyDirective(inputbean, taxonomyDirective);
	}

	public void validateAuthor(CreateInputBean inputbean) {
		// check author name and email
		Author author = inputbean.getAuthor();
		if (author == null || StringUtils.isEmpty(author.getName()) && StringUtils.isEmpty(author.getEmail())) {
			Author author1 = inputbean.getRequester();
			if (author1 == null || StringUtils.isEmpty(author1.getName()) && StringUtils.isEmpty(author1.getEmail())) {
				throw new CustomExceptionError(405);
			}
		}
	}

	public void validateGetDefinitionInputFields(CreateInputBean inputbean) throws JsonParseException, IOException {

		// check entity name and type
		String version = inputbean.getVersion();

		if (!("DEV".equalsIgnoreCase(version) || "LIVE".equalsIgnoreCase(version))) {
			throw new CustomExceptionError(416);
		}
	}

	private boolean isAlphaNumeric(String s) {
		return s.matches(pattern);
	}

	public void validSupportedSearchTokens(String searchToken) throws Exception {

		// check private entity is present in db
		List<PrivateEntity> privateEntityList = privateEntityRepositoryImpl.getDefinitions(searchToken);
		if (privateEntityList == null || privateEntityList.isEmpty()) {
			throw new CustomExceptionError(searchToken, 411, true);
		}
		// check private entity is present in db with "live" status
		for (PrivateEntity pe : privateEntityList) {
			if (pe.getSearchToken().equalsIgnoreCase(searchToken) && !pe.getStatus().equals(STATE_LIVE)) {
				throw new CustomExceptionError(searchToken, 429, true);
			}
		}
		/*
		 * if (StringUtils.isEmpty(searchToken) || !(searchToken.startsWith("T:") || searchToken.startsWith("R:"))) { throw new
		 * CustomExceptionError(errorCode); }
		 */

	}

	public void validateState(String state) {

		if (StringUtils.isEmpty(state) || !(STATE_ACTIVE.equalsIgnoreCase(state) || STATE_INACTIVE.equalsIgnoreCase(state))) {
			throw new CustomExceptionError(424);
		}

	}

	private boolean isEmptyVal(Collection<List<String>> values) {
		for (List<String> val : values) {
			if (val == null || val.isEmpty()) {
				return true;
			}
			for (String s : val) {
				if (s == null || s.isEmpty()) {
					return true;
				}
			}
		}
		return false;
	}
}
