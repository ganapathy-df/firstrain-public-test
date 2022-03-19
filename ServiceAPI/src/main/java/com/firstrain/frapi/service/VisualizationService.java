package com.firstrain.frapi.service;

import java.util.List;

import com.firstrain.frapi.FRService;
import com.firstrain.frapi.domain.VisualizationData;
import com.firstrain.frapi.domain.VisualizationData.ChartType;

public interface VisualizationService extends FRService {

	public VisualizationData getVisualizationByMonitorId(long tagId, int nodeCount, List<ChartType> chartTypes, String filters,
			boolean isHtmlSmartText, boolean isApplyMinNodeCheck) throws Exception;

	public VisualizationData getVisualizationByEntityToken(String searchToken, int nodeCount, List<ChartType> chartTypes, String filters,
			boolean isHtmlSmartText, boolean isApplyMinNodeCheck) throws Exception;

}
