package com.firstrain.frapi.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.firstrain.frapi.config.ServiceException;
import com.firstrain.frapi.obj.MonitorObj;
import com.firstrain.frapi.repository.EntityBaseServiceRepository;
import com.firstrain.obj.IEntityInfo;
import com.firstrain.utils.FR_Loader;

@Service
public class RegionExcelUtilImpl {

	private static final Logger LOG = Logger.getLogger(RegionExcelUtilImpl.class);

	@Autowired
	@Qualifier("entityBaseServiceRepositoryImpl")
	private EntityBaseServiceRepository entityBaseServiceRepository;

	private List<Region> regions = null;
	private Map<String, String> regionVsParentRegion = null;
	private Map<String, String> regionVsCountryCode = null;
	private Map<String, String> regionNameVsSearchToken = null;

	@PostConstruct
	public void init() throws ServiceException {
		try {
			loadRegions();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	private void loadRegions() {
		try {
			List<Region> regions = new ArrayList<Region>();
			Map<String, String> regionVsParentRegion = new HashMap<String, String>();
			Map<String, String> regionVsCountryCode = new HashMap<String, String>();
			Map<String, String> regionNameVsSearchToken = new HashMap<String, String>();
			HSSFWorkbook wb = new HSSFWorkbook(FR_Loader.getResourceAsStream("resource/Regional_Category.xls"));
			HSSFSheet sheet = wb.getSheetAt(0);
			int rows = sheet.getPhysicalNumberOfRows();
			for (int r = 1; r < rows; r++) {
				HSSFRow row = sheet.getRow(r);
				if (row == null) {
					continue;
				}
				HSSFCell cell1 = row.getCell(1);

				IEntityInfo entity = entityBaseServiceRepository.getEntityInfoCache().searchTokenToEntity(cell1.getStringCellValue());
				if (entity != null) {
					Region rg = null;
					for (Region region : regions) {
						if (region.getParent().getDefaultURL().equals(entity.getSearchToken())) {
							rg = region;
							break;
						}
					}
					if (rg == null) {
						MonitorObj filter = new MonitorObj(entity.getName(), entity.getSearchToken());
						rg = new Region(filter);
						regions.add(rg);
					}
					HSSFCell cell2 = row.getCell(3);
					entity = entityBaseServiceRepository.getEntityInfoCache().searchTokenToEntity(cell2.getStringCellValue());
					if (entity != null) {
						regionVsParentRegion.put(entity.getSearchToken(), rg.getParent().getDefaultURL());
						regionNameVsSearchToken.put(entity.getName(), entity.getSearchToken());
						rg.addChild(new MonitorObj(entity.getName(), entity.getSearchToken()));

						if (row.getCell(5) != null) {
							String countryCodeCell = row.getCell(5).getStringCellValue();
							if (countryCodeCell.contains("-")) {
								String arr[] = countryCodeCell.split("-");
								countryCodeCell = arr[arr.length - 1];
							}
							regionVsCountryCode.put(entity.getSearchToken(), countryCodeCell.toLowerCase());
						}
					}
				}
			}
			this.regions = Collections.unmodifiableList(regions);
			this.regionVsParentRegion = Collections.unmodifiableMap(regionVsParentRegion);
			this.regionVsCountryCode = Collections.unmodifiableMap(regionVsCountryCode);
			this.regionNameVsSearchToken = Collections.unmodifiableMap(regionNameVsSearchToken);
			LOG.info("Loaded <" + rows + "> region rows from sheet from :"
					+ FR_Loader.getResource("resource/Regional_Category.xls").getPath());
		} catch (Exception e) {
			LOG.error("Error while loading excluded regions.", e);
		}
	}

	public List<Region> getRegions() {
		return this.regions;
	}

	public Map<String, String> getRegionVsParentRegion() {
		return this.regionVsParentRegion;
	}

	public Map<String, String> getRegionVsCountryCode() {
		return this.regionVsCountryCode;
	}

	public Map<String, String> getRegionNameVsSearchToken() {
		return regionNameVsSearchToken;
	}

	public class Region {
		MonitorObj parent;
		List<MonitorObj> children = new ArrayList<MonitorObj>();

		public Region(MonitorObj parent) {
			this.parent = parent;
		}

		public void addChild(MonitorObj child) {
			this.children.add(child);
		}

		public MonitorObj getParent() {
			return parent;
		}

		public List<MonitorObj> getChildren() {
			return children;
		}
	}

}
