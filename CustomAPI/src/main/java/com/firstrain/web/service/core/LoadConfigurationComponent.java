package com.firstrain.web.service.core;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.firstrain.utils.FR_Loader;
import com.firstrain.web.util.LoadConfiguration;

public class LoadConfigurationComponent {
	private static final Logger LOG = Logger.getLogger(LoadConfigurationComponent.class);

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
		URL resource = FR_Loader.getResource(lc.getFilePath());
		if (resource == null) {
			LOG.warn("Required file " + lc.getFilePath() + " is not present in deployed (resource) folder");
			return;
		}
		File f = new File(resource.getPath());
		if (lc.getLastModifiedTime() != f.lastModified()) {
			LOG.info("File has been modified so going to reload " + lc.getFilePath());
			lc.setLastModifiedTime(f.lastModified());
			InputStream in = resource.openStream();
			try {
				lc.load();
			} finally {
				if (in != null) {
					in.close();
				}
			}
		}
	}
}
