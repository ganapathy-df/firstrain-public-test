package com.firstrain.web.service.core;

import java.io.File;
import java.io.FileInputStream;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.firstrain.utils.JSONUtility;

@Service
public class StaticDataService {

	private static final Logger LOG = Logger.getLogger(StaticDataService.class);
	@Value("${static.data.file.path}")
	private String filepath;

	public <T> T getDataObject(String fileName, Class<T> clazz) {
		T res = null;
		try {
			FileInputStream fis = new FileInputStream(new File(filepath + "/" + fileName));
			res = JSONUtility.deserialize(fis, clazz);
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		return res;
	}
}
