package com.firstrain.frapi.service;

import org.springframework.stereotype.Service;

import com.firstrain.frapi.FRService;
import com.firstrain.frapi.domain.User;
import com.firstrain.frapi.pojo.EnterprisePref;
import com.firstrain.frapi.pojo.MonitorAPIResponse;
import com.firstrain.frapi.pojo.MonitorEmailAPIResponse;

@Service
public interface MonitorBriefService extends FRService {

	public MonitorAPIResponse getMonitorBriefDetails(User user, long monitorId, EnterprisePref enterprisePref, String fq) throws Exception;

	public MonitorEmailAPIResponse getMonitorEmailList(User user, long monitorId, String startDate, String endDate) throws Exception;
}
