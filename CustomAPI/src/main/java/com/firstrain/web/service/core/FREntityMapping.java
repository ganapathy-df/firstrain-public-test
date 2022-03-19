package com.firstrain.web.service.core;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

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

import com.firstrain.frapi.domain.Entity;
import com.firstrain.web.util.LoadConfiguration;

@Service
public class FREntityMapping extends LoadConfiguration {

	private static final Logger LOG = Logger.getLogger(FREntityMapping.class);
	private Map<String, Entity> catIdVsFREntity = new ConcurrentHashMap<String, Entity>();


	@Value("${pw.brand.filepath}")
	private String pwBrandFilePath;

	@Autowired
	private LoadConfigurationComponentByExternalUrl loadConfigurationComponentByExternalUrl;

	
	@PostConstruct
	private void init() throws Exception {
		this.setFilePath(pwBrandFilePath + "FR_Entities.xlsx");
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

			for (int i = 0; i < sheetCount; i++) {
				populateFREntity(wb.getSheetAt(i));
			}
		} catch (Exception e) {
			LOG.error("Error while loading data from: " + this.getFilePath(), e);
		}
	}

	private void populateFREntity(Sheet sheet) {
		int rowIndex = 0;
		try {
			int rowCount = sheet.getPhysicalNumberOfRows();
			for (rowIndex = 1; rowIndex < rowCount; rowIndex++) {
				Row row = sheet.getRow(rowIndex);
				if (row == null) {
					continue;
				}
				Entity entity = new Entity();
				String searchToken = getCellValue(row.getCell(1));
				entity.setId(getCellValue(row.getCell(0)));
				entity.setSearchToken(searchToken);
				entity.setName(getCellValue(row.getCell(2)));
				catIdVsFREntity.put(searchToken, entity);
			}
			LOG.info("sheet process succesfully -> sheet Name : " + sheet.getSheetName() + ", number of rows : " + rowCount);
		} catch (Exception e) {
			LOG.error("Error while FR Entity Sheet " + e.getMessage(), e);
		}
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

	public Entity getCatIdVsFREntity(String key) {
		return catIdVsFREntity.get(key);
	}

}
