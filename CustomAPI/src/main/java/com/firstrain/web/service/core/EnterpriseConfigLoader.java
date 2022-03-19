package com.firstrain.web.service.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.stereotype.Service;

import com.firstrain.web.util.LoadConfiguration;
import com.firstrain.web.util.ProjectConfig;
import com.firstrain.web.util.ProjectConfig.EnterpriseConfig;

@Service
public class EnterpriseConfigLoader extends LoadConfiguration {
	private static final String PROJECTCONFIG_FILENAME = "projectconfig.xml";
	private Marshaller marshaller;
	private Unmarshaller unmarshaller;
	private Map<String, EnterpriseConfig> configMap = null;
	private static final Logger LOG = Logger.getLogger(EnterpriseConfigLoader.class);
	@Autowired
	private LoadConfigurationComponent loadConfigurationComponent;

	@PostConstruct
	private void init() throws Exception {
		this.setFilePath("projectconfig.xml");
		loadConfigurationComponent.addToConfigurationQueue(this);
	}

	@Override
	public void load() throws Exception {
		InputStream is = null;
		try {
			is = this.getClass().getClassLoader().getResourceAsStream(PROJECTCONFIG_FILENAME);
			ProjectConfig pc = (ProjectConfig) this.unmarshaller.unmarshal(new StreamSource(is));
			if (pc != null) {
				configMap = new HashMap<String, ProjectConfig.EnterpriseConfig>();
				for (EnterpriseConfig ec : pc.getEnterprises()) {
					configMap.put(ec.getAccesstoken(), ec);
					LOG.info("Configuration loaded for enterprise Id : " + ec.getId());
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw e;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					LOG.error(e.getMessage(), e);
				}
			}
		}
	}

	public Map<String, EnterpriseConfig> getConfigMap() {
		return configMap;
	}

	public EnterpriseConfig getEnterpriseConfig(String accessToken) {
		return configMap.get(accessToken);
	}

	public Marshaller getMarshaller() {
		return marshaller;
	}

	public void setMarshaller(Marshaller marshaller) {
		this.marshaller = marshaller;
	}

	public Unmarshaller getUnmarshaller() {
		return unmarshaller;
	}

	public void setUnmarshaller(Unmarshaller unmarshaller) {
		this.unmarshaller = unmarshaller;
	}
}
