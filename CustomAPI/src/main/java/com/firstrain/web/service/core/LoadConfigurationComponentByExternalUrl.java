package com.firstrain.web.service.core;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import com.firstrain.web.util.LoadConfiguration;

import sun.net.www.protocol.http.HttpURLConnection;

public class LoadConfigurationComponentByExternalUrl {
	private static final Logger LOG = Logger.getLogger(LoadConfigurationComponentByExternalUrl.class);

	@Autowired
	private FRResourceUtils frResourceUtils;

	private List<LoadConfiguration> configurationQueue = new ArrayList<LoadConfiguration>();

	public void init() throws Exception {
		for (LoadConfiguration lc : configurationQueue) {
			try {
				loadIfModified(lc);
			} catch (Exception e) {
				LOG.error("Error while loading :" + lc.getFilePath(), e);
				throw e;
			}
		}
	}

	public void addToConfigurationQueue(LoadConfiguration loadConfiguration) throws Exception {
		try {
			loadIfModified(loadConfiguration);
		} catch (Exception e) {
			LOG.error("Error while loading :" + loadConfiguration.getFilePath(), e);
			throw e;
		}
		configurationQueue.add(loadConfiguration);
	}

	private void loadIfModified(LoadConfiguration lc) throws Exception {
		Resource resource = frResourceUtils.getResource(lc.getFilePath());
		if (resource == null) {
			LOG.warn("Required file " + lc.getFilePath() + " is not present in deployed (resource) folder");
			return;
		}
		URL url = new URL(resource.getURI().toString());
		HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();

		long lastModified = httpCon.getLastModified();
		if (lc.getLastModifiedTime() != lastModified) {
			LOG.info("File has been modified so going to reload " + lc.getFilePath());
			lc.setLastModifiedTime(lastModified);
			InputStream in = url.openStream();
			try {
				lc.load(in);
			} finally {
				if (in != null) {
					in.close();
				}
			}
		}
	}
}
