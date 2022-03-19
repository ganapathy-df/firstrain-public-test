package com.firstrain.web.service.core;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.firstrain.web.domain.Brand;
import com.firstrain.web.domain.Group;
import com.firstrain.web.domain.PwToken;
import com.firstrain.web.domain.Topic;
import com.firstrain.web.util.LoadConfiguration;

@Service
public class PWBrandMapping extends LoadConfiguration {

	private static final Logger LOG = Logger.getLogger(PWBrandMapping.class);
	private Map<String, Brand> pwKeyVsBrand = new ConcurrentHashMap<String, Brand>();
	private Map<String, String> pwKeyMapping = new ConcurrentHashMap<String, String>();
	private Map<String, String> pwKeyVsBrandInitials = new ConcurrentHashMap<String, String>();
	private static final String SEPRATOR = "~";
	private static final String NOT_SEPRATOR = "#";
	private static final String SEPRATOR2 = "\\|";
	private static final String COMPANIES = "companies";
	private static final String TOPICS = "topics";


	@Value("${pw.brand.filepath}")
	private String pwBrandFilePath;

	@Autowired
	private LoadConfigurationComponentByExternalUrl loadConfigurationComponentByExternalUrl;

	
	@PostConstruct
	private void init() throws Exception {
		this.setFilePath(pwBrandFilePath + "PW Brands-FR Topic mapping.xlsx");
		loadConfigurationComponentByExternalUrl.addToConfigurationQueue(this);
	}

	@Override
	public void load() throws Exception {}

	@Override
	public void load(InputStream in) {
		try {
			Workbook wb = new XSSFWorkbook(in);
			int sheetCount = wb.getNumberOfSheets();

			LOG.info("Number of Sheets to process : " + (sheetCount - 1));

			for (int i = 1; i < sheetCount; i++) {
				populateBrandObj(wb.getSheetAt(i));
			}
		} catch (Exception e) {
			LOG.error("Error while loading data from: " + this.getFilePath(), e);
		}
	}

	private void populateBrandObj(Sheet sheet) {
		int rowIndex = 0;
		try {

			String pwKey = null;
			int group = 1;
			Brand brand = new Brand();

			int rowCount = sheet.getPhysicalNumberOfRows();
			rowIndex = 1;
			for (; rowIndex < rowCount; rowIndex++) {
				Row row = sheet.getRow(rowIndex);
				if (row == null) {
					continue;
				}
				if (rowIndex == 1) {
					pwKey = getCellValue(row.getCell(0));
				}
				String pwEntity = getCellValue(row.getCell(1));
				String pwToken = getCellValue(row.getCell(2));
				String FrTopic = getCellValue(row.getCell(3));
				String FrToken = getCellValue(row.getCell(4));
				populateBrandValues(pwEntity, pwToken, FrTopic, FrToken, group, brand);
				group++;
			}
			if (StringUtils.isNotEmpty(pwKey)) {

				String[] pwKeyStrArr = pwKey.split(SEPRATOR2);

				String pwKeyStr = pwKeyStrArr[0].trim().toLowerCase();
				String pwKeyStrCompanies = (pwKeyStr + COMPANIES);
				String pwKeyStrTopics = (pwKeyStr + TOPICS);

				pwKeyMapping.put(pwKeyStr, pwKeyStr);
				pwKeyMapping.put(pwKeyStrCompanies, pwKeyStr);
				pwKeyMapping.put(pwKeyStrTopics, pwKeyStr);

				if (pwKeyStrArr.length > 1) {
					String pwBrandInitialStr = pwKeyStrArr[1].trim().toLowerCase();
					pwKeyVsBrandInitials.put(pwKeyStr, pwBrandInitialStr);
					pwKeyVsBrandInitials.put(pwKeyStrCompanies, pwBrandInitialStr);
					pwKeyVsBrandInitials.put(pwKeyStrTopics, pwBrandInitialStr);
				}

				pwKeyVsBrand.put(pwKeyStr, brand);
			}
			LOG.info("sheet process succesfully -> sheet Name : " + sheet.getSheetName() + ", and brand key | brand initials : " + pwKey
					+ ", number of rows : "
					+ rowCount);
		} catch (Exception e) {
			LOG.error("Error while populateBrandObj " + e.getMessage(), e);
		}
	}

	private Void populateBrandValues(String pwEntity, String pwToken, String frEntity, String frToken, int group, Brand brand) {

		LOG.debug("pwEntity : " + pwEntity + ", pwToken : " + pwToken + ", frEntity : " + frEntity + ", frToken : " + frToken);

		Map<String, PwToken> pwTokenMap = brand.getPwTokenMap();
		if (pwTokenMap == null) {
			pwTokenMap = new HashMap<String, PwToken>();
			brand.setPwTokenMap(pwTokenMap);
		}

		Map<Integer, Group> groupMap = brand.getGroupMap();
		if (groupMap == null) {
			groupMap = new HashMap<Integer, Group>();
			brand.setGroupMap(groupMap);
		}

		Map<String, Topic> topicMap = brand.getTopicMap();
		if (topicMap == null) {
			topicMap = new HashMap<String, Topic>();
			brand.setTopicMap(topicMap);
		}
		Map<String, List<String>> excludeTopicMap = brand.getExcludeTopicMap();
		if (excludeTopicMap == null) {
			excludeTopicMap = new HashMap<String, List<String>>();
			brand.setExcludeTopicMap(excludeTopicMap);
		}
		Map<String, List<String>> andTopicMap = brand.getAndTopicMap();
		if (andTopicMap == null) {
			andTopicMap = new HashMap<String, List<String>>();
			brand.setAndTopicMap(andTopicMap);
		}

		// populate pwTokenMap --> <pwToken vs pwName + [G1,G2,G3,....]>
		populatePwTokenMap(pwEntity, pwToken, group, pwTokenMap);

		// populate groupMap --> <group vs [T1,T2,T3,...] + frTopic + frToken>
		Group groupObj = groupMap.get(group);
		if (groupObj == null) {
			groupObj = new Group();
			groupMap.put(group, groupObj);
		}
		groupObj.setFrTopic(frEntity);
		groupObj.setFrToken(frToken);
		Set<String> tokenSet = groupObj.getTokenSet();
		if (tokenSet == null) {
			tokenSet = new HashSet<String>();
			groupObj.setTokenSet(tokenSet);
		}

		if (StringUtils.isNotEmpty(frToken)) {
			String[] tokenArr = frToken.split(NOT_SEPRATOR);
			if (tokenArr.length > 1) {
				// frToken = arr[0];
				retrieveTokensAndPopulate(excludeTopicMap, pwToken, tokenArr);

			} else {
				tokenArr = frToken.split(SEPRATOR);
				if (tokenArr.length > 1) {
					retrieveTokensAndPopulate(andTopicMap, pwToken, tokenArr);
				}
				addTokenAndPopulateTopicMap(tokenArr, tokenSet, pwToken, group, topicMap); 
			}
			addTokenAndPopulateTopicMap(tokenArr, tokenSet, pwToken, group, topicMap); 
		}

		return null;

	}

	private void retrieveTokensAndPopulate(final Map<String, List<String>> andTopicMap, final String pwToken, final String[] tokenArr) {
		List<String> tokens = andTopicMap.get(pwToken);
		if (tokens == null) {
			tokens = new ArrayList<String>();
			andTopicMap.put(pwToken, tokens);
		}
		tokens.add(tokenArr[1]);
	}
 
	private void addTokenAndPopulateTopicMap(final String[] tokenArr, final Set<String> tokenSet, final String pwToken, final int group, final Map<String, Topic> topicMap) { 
		for (String token : tokenArr) { 
			tokenSet.add(token); 
			// populate topicMap --> <topic vs [G1,G2,G3,...] + [pwToken1, pwToken2, pwToken3, .....]> 
			populateTopicMap(pwToken, group, topicMap, token); 
		} 
	} 

	private void populateTopicMap(String pwToken, int group, Map<String, Topic> topicMap, String token) {
		Topic topic = topicMap.get(token);
		if (topic == null) {
			topic = new Topic();
			topicMap.put(token, topic);
		}
		Set<Integer> groups = topic.getGroups();
		if (groups == null) {
			groups = new HashSet<Integer>();
			topic.setGroups(groups);
		}
		groups.add(group);

		Set<String> pwTokens = topic.getPwTokens();
		if (pwTokens == null) {
			pwTokens = new HashSet<String>();
			topic.setPwTokens(pwTokens);
		}
		pwTokens.add(pwToken);
	}

	private void populatePwTokenMap(String pwEntity, String pwToken, int group, Map<String, PwToken> pwTokenMap) {
		PwToken pwTokenObj = pwTokenMap.get(pwToken);
		if (pwTokenObj == null) {
			pwTokenObj = new PwToken();
			pwTokenMap.put(pwToken, pwTokenObj);
		}
		String pwName = pwTokenObj.getName();
		if (pwName == null) {
			pwTokenObj.setName(pwEntity);
		}

		Set<Integer> groups = pwTokenObj.getGroups();
		if (groups == null) {
			groups = new HashSet<Integer>();
			pwTokenObj.setGroups(groups);
		}
		groups.add(group);
	}

	private String getCellValue(Cell cell) {
		if (cell == null) {
			return null;
		}
		String value = null;
		switch (cell.getCellType()) {
			case Cell.CELL_TYPE_STRING:
				value = cell.getStringCellValue();
				break;
			case Cell.CELL_TYPE_NUMERIC:
				if (DateUtil.isCellDateFormatted(cell)) {
					value = cell.getDateCellValue().toString();
				} else {
					value = Double.toString(cell.getNumericCellValue());
				}
				break;

			case Cell.CELL_TYPE_BLANK:
			case Cell.CELL_TYPE_BOOLEAN:
				break;
			default:
				break;
		}
		return value;
	}

	public String getPwKey(String key) {
		return pwKeyMapping.get(key);
	}

	public Brand getBrand(String key) {
		return pwKeyVsBrand.get(key);
	}

	public String getPwBrandInitials(String key) {
		return pwKeyVsBrandInitials.get(key);
	}
}
