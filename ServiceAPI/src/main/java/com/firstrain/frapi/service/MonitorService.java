package com.firstrain.frapi.service;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.firstrain.frapi.FRService;
import com.firstrain.frapi.domain.User;
import com.firstrain.frapi.pojo.EnterprisePref;
import com.firstrain.frapi.pojo.MonitorAPIResponse;

@Service
public interface MonitorService extends FRService {

	public MonitorAPIResponse getMonitorListByOwner(User actor, User user, String ownerType) throws Exception;

	public MonitorAPIResponse removeEntities(long frUserId, long monitorId, List<String> entityList) throws Exception;

	public MonitorAPIResponse removeFilters(long frUserId, long monitorId, List<String> filtersToRemoveList) throws Exception;

	public MonitorAPIResponse getMonitorDetails(User user, long monitorId) throws Exception;

	public MonitorAPIResponse getEntityStatus(User user, long monitorId, String entity) throws Exception;

	public MonitorAPIResponse addFilters(long frUserId, long monitorId, List<String> filterList) throws Exception;

	public MonitorAPIResponse addEntities(long frUserId, long monitorId, List<String> entityList, EnterprisePref enterprisePref)
			throws Exception;

	public MonitorAPIResponse createMonitor(long frUserId, String monitorName, List<String> entityList, List<String> filterList,
			EnterprisePref enterprisePref) throws Exception;

	public MonitorAPIResponse removeMonitor(User user, long monitorId) throws Exception;

	public Set<Long> getAllGroupIdsOfUser(long actorId, long groupId) throws Exception;
}
