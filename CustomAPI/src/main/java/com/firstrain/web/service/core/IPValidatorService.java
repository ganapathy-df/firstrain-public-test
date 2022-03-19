package com.firstrain.web.service.core;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.firstrain.utils.security.IPValidatorHelper;
import com.firstrain.utils.security.IPValidatorHelper.IPRange;
import com.firstrain.web.util.ProjectConfig.EnterpriseConfig;

@Service
public final class IPValidatorService {

	private static final Logger LOG = Logger.getLogger(IPValidatorService.class);
	private Map<Long, IPValidatorHelper> ipValidatorMap = new HashMap<Long, IPValidatorHelper>();
	@Autowired
	private EnterpriseConfigLoader enterpriseConfigLoader;

	@PostConstruct
	private void populateIpValidatorMap() {
		if (enterpriseConfigLoader != null && enterpriseConfigLoader.getConfigMap() != null) {
			for (EnterpriseConfig ec : enterpriseConfigLoader.getConfigMap().values()) {
				IPValidatorHelper ipValidator = new IPValidatorHelper();
				ipValidator.addIPRange(ec.getIprange(), -1);
				ipValidatorMap.put(ec.getId(), ipValidator);
			}
		}
	}

	public boolean isIPSecure(HttpServletRequest request, long enterpriseId) {
		IPValidatorHelper ipValidator = ipValidatorMap.get(enterpriseId);
		if (ipValidator != null) {
			try {
				String remoteHost = getRemoteHost(request);
				if (remoteHost == null) {
					return false;
				}
				IPRange range = ipValidator.isIPSecure(remoteHost);
				return range != null;
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		}
		return false;
	}

	/**
	 * gets remote host of the request.
	 * 
	 * @param request
	 * @return
	 */
	private String getRemoteHost(HttpServletRequest request) {
		String remoteHost = request.getHeader("X-Forwarded-For");
		if (remoteHost == null) {
			remoteHost = request.getHeader("X-Forward-For");
		}
		/* This is placed here to avoid exceptions during local tests. */
		if (remoteHost == null) {
			remoteHost = request.getRemoteAddr();
		}
		return remoteHost;
	}
}
