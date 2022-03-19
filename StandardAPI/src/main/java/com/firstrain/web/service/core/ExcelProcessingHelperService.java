package com.firstrain.web.service.core;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.firstrain.utils.FR_Loader;

@Service
public class ExcelProcessingHelperService {

	private static final Logger LOG = Logger.getLogger(ExcelProcessingHelperService.class);
	private static final String DEFAULT_LANGUAGE = "English";
	private static final String EXCEL_FOLDER_PATH = "resource/";
	private static final String SECTOR_CXO_FILENAME = "Sectorwise_CXO.xlsx";
	private static final String JOP_TITLES_FILENAME = "Job_Titles.xlsx";
	@Value("${source.mapping.file:}")
	private String SOURCE_METADATA_FILE = "Source_Metadata.xlsx";
	private Map<Integer, String> sectorVsCXO = new HashMap<Integer, String>();
	private Map<String, String> jobVsTitles = new HashMap<String, String>();
	private Map<String, String> sourceMetaDataMap = null;
	private AtomicLong sectorCxoModifiedTime = new AtomicLong(-1L);
	private AtomicLong jobTitlesModifiedTime = new AtomicLong(-1L);
	private AtomicLong sourceMetaDataModifiedTime = new AtomicLong(-1L);
	@Qualifier("httpClient")
	@Autowired
	private HttpClient httpClient;

	@PostConstruct
	// check everyday if file has been changed
	@Scheduled(fixedDelay = 3600000)
	public void init() throws IOException {
		InputStream iStream = getFileFromPath(SECTOR_CXO_FILENAME, sectorCxoModifiedTime);
		if (iStream != null) {
			loadSectorCxoFromSheet(iStream);
			iStream.close();
		}
		iStream = getFileFromPath(JOP_TITLES_FILENAME, jobTitlesModifiedTime);
		if (iStream != null) {
			loadJobTitlesFromSheet(iStream);
			iStream.close();
		}
		loadFileFromHTTP(SOURCE_METADATA_FILE, sourceMetaDataModifiedTime);
	}

	public InputStream getFileFromPath(String fileName, AtomicLong lastModifiedTime) throws IOException {
		URL resource = FR_Loader.getResource(EXCEL_FOLDER_PATH + fileName);
		if (resource == null) {
			LOG.warn("Required file " + fileName + " is not present in deployed (resource) folder");
			return null;
		}
		File f = new File(resource.getPath());
		if (lastModifiedTime.get() != f.lastModified()) {
			LOG.info("File has been modified so going to reload " + fileName);
			lastModifiedTime.set(f.lastModified());
			InputStream iStream = resource.openStream();
			return iStream;
		} else {
			return null;
		}
	}

	public void loadFileFromHTTP(String url, AtomicLong lastModifiedTime) {
		GetMethod getMethod = null;
		String jsonInut = null;
		String res = null;
		try {
			getMethod = new GetMethod(url);
			httpClient.executeMethod(getMethod);
			long lastModTime = -1;
			if (getMethod.getResponseHeader("Last-Modified") != null) {
				String headerVal = getMethod.getResponseHeader("Last-Modified").getValue();
				lastModTime = DateUtil.parseDate(headerVal).getTime();
			} else {
				LOG.warn("Last-Modified header is null for url: " + url);
			}
			if (lastModTime == -1 || lastModifiedTime.get() != lastModTime) {
				LOG.info("File has been modified so going to reload " + url);
				lastModifiedTime.set(lastModTime);
				InputStream in = getMethod.getResponseBodyAsStream();
				loadSourceMetaDataMap(in);
				in.close();
			}
		} catch (Exception e) {
			LOG.error("Error while getting data for: " + url + " :: reqBody: " + jsonInut + " :: response: " + res, e);
		} finally {
			if (getMethod != null) {
				getMethod.releaseConnection();
			}
		}
	}

	public void loadSourceMetaDataMap(InputStream istream) {
		try {
			Map<String, String> returnMap = new HashMap<String, String>();
			XSSFWorkbook wb = new XSSFWorkbook(istream);
			XSSFSheet sheet = wb.getSheetAt(0);

			int rows = sheet.getPhysicalNumberOfRows();
			for (int r = 1; r < rows; r++) {
				XSSFRow row = sheet.getRow(r);
				if (row == null) {
					continue;
				}

				String searchToken = null;
				String language = null;
				if (row.getCell(0) != null && row.getCell(0).getCellType() == Cell.CELL_TYPE_STRING) {
					searchToken = row.getCell(0).getStringCellValue().trim();
				}

				if (row.getCell(1) != null && row.getCell(1).getCellType() == Cell.CELL_TYPE_STRING) {
					language = row.getCell(1).getStringCellValue().trim();
				}

				returnMap.put(searchToken, language);
			}
			LOG.info("Loaded <" + rows + "> of mapped rows from source metadata sheet : " + SOURCE_METADATA_FILE);
			sourceMetaDataMap = Collections.unmodifiableMap(returnMap);
		} catch (Exception e) {
			LOG.error("Error while loading SourceMetaDataMap sheet........." + istream + e.getMessage(), e);
		}
	}

	private void loadJobTitlesFromSheet(InputStream iStreamFile) {
		try {
			Map<String, String> returnMap = new HashMap<String, String>();
			XSSFWorkbook wb = new XSSFWorkbook(iStreamFile);
			XSSFSheet sheet = wb.getSheetAt(0);
			String mapKey = "";
			String mapValue = "";
			int rows = sheet.getPhysicalNumberOfRows();
			for (int r = 1; r < rows; r++) {
				XSSFRow row = sheet.getRow(r);
				if (row == null) {
					continue;
				}
				if (row.getCell(0) != null && row.getCell(0).getCellType() != Cell.CELL_TYPE_BLANK
						&& row.getCell(0).getCellType() == Cell.CELL_TYPE_STRING) {
					mapKey = row.getCell(0).getStringCellValue().trim();
				}
				if (row.getCell(1) != null && row.getCell(1).getCellType() != Cell.CELL_TYPE_BLANK
						&& row.getCell(1).getCellType() == Cell.CELL_TYPE_STRING) {
					mapValue = row.getCell(1).getStringCellValue().trim();
				}
				returnMap.put(mapKey, mapValue);
			}
			LOG.info("Loaded <" + rows + "> of mapped rows from job titles sheet : " + JOP_TITLES_FILENAME);
			jobVsTitles = Collections.unmodifiableMap(returnMap);
		} catch (Exception e) {
			LOG.error("Error while loading job titles sheet........." + iStreamFile + e.getMessage(), e);
		}
	}

	private void loadSectorCxoFromSheet(InputStream iStreamFile) {
		try {
			Map<Integer, String> returnMap = new HashMap<Integer, String>();
			XSSFWorkbook wb = new XSSFWorkbook(iStreamFile);
			XSSFSheet sheet = wb.getSheetAt(0);
			XSSFSheet sheet1 = wb.getSheetAt(1);
			getDefaultSectorCxoFromSheet(sheet1, returnMap);
			int mapKey = 0;
			String mapValue = "";
			int rows = sheet.getPhysicalNumberOfRows();
			for (int r = 1; r < rows; r++) {
				XSSFRow row = sheet.getRow(r);
				if (row == null) {
					continue;
				}
				if (row.getCell(0) != null && row.getCell(0).getCellType() != Cell.CELL_TYPE_BLANK
						&& row.getCell(0).getCellType() == Cell.CELL_TYPE_NUMERIC) {
					mapKey = ((Double) row.getCell(0).getNumericCellValue()).intValue();
				}
				if (row.getCell(2) != null && row.getCell(2).getCellType() != Cell.CELL_TYPE_BLANK
						&& row.getCell(2).getCellType() == Cell.CELL_TYPE_STRING) {
					mapValue = row.getCell(2).getStringCellValue().trim();
				}
				returnMap.put(mapKey, mapValue);
			}
			LOG.info("Loaded <" + rows + "> of mapped rows from sector cxo sheet : " + SECTOR_CXO_FILENAME);
			sectorVsCXO = Collections.unmodifiableMap(returnMap);
		} catch (Exception e) {
			LOG.error("Error while loading sector cxo sheet........." + iStreamFile + e.getMessage(), e);
		}
	}

	private void getDefaultSectorCxoFromSheet(XSSFSheet sheet1, Map<Integer, String> returnMap) {
		try {
			int sheet1Rows = sheet1.getPhysicalNumberOfRows();
			for (int r = 1; r < sheet1Rows; r++) {
				XSSFRow row = sheet1.getRow(r);
				int mapKey = 0;
				String mapValue = "";
				if (row == null) {
					continue;
				}
				if (row.getCell(0) != null && row.getCell(0).getCellType() != Cell.CELL_TYPE_BLANK
						&& row.getCell(0).getCellType() == Cell.CELL_TYPE_NUMERIC) {
					mapKey = ((Double) row.getCell(0).getNumericCellValue()).intValue();
				}
				if (row.getCell(2) != null && row.getCell(2).getCellType() != Cell.CELL_TYPE_BLANK
						&& row.getCell(2).getCellType() == Cell.CELL_TYPE_STRING) {
					mapValue = ((String) row.getCell(2).getStringCellValue());
				}
				returnMap.put(mapKey, mapValue);
			}
			LOG.info("Loaded <" + sheet1Rows + "> of mapped rows from sector cxo sheet1 " + SECTOR_CXO_FILENAME);
		} catch (Exception e) {
			LOG.error("Error while getting default data from sheet1 of sector cxo file........." + e.getMessage(), e);
		}
	}

	public Map<Integer, String> getSectorVsCXO() {
		return sectorVsCXO;
	}

	public void setSectorVsCXO(Map<Integer, String> sectorVsCXO) {
		this.sectorVsCXO = sectorVsCXO;
	}

	public String getCXOForSector(int sectorId) {
		return sectorVsCXO.get(sectorId);
	}

	public Map<String, String> getJobVsTitles() {
		return jobVsTitles;
	}

	public void setJobVsTitles(Map<String, String> jobVsTitles) {
		this.jobVsTitles = jobVsTitles;
	}

	public String getTitlesForJob(String job) {
		return jobVsTitles.get(job);
	}

	public String getSourceMetaData(String key) {
		String language = sourceMetaDataMap.get(key);
		if (StringUtils.isEmpty(language)) {
			return DEFAULT_LANGUAGE;
		}
		return language;
	}
}
