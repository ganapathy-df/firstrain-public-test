package com.firstrain.web.util;

import java.io.InputStream;

public abstract class LoadConfiguration {
	private long lastModifiedTime = -1;
	private String filePath;

	public abstract void load() throws Exception;

	public void load(InputStream in) {};

	public long getLastModifiedTime() {
		return lastModifiedTime;
	}

	public void setLastModifiedTime(long lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

}
