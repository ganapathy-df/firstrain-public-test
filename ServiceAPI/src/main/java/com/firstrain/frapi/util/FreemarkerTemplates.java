package com.firstrain.frapi.util;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import freemarker.template.Configuration;
import freemarker.template.Template;

@Service
public class FreemarkerTemplates {

	private final Logger LOG = Logger.getLogger(FreemarkerTemplates.class);
	private Configuration config = null;

	@Value("${ftl.dir}")
	private String ftlTemplatePath;

	public void init() {
		init(new File(ftlTemplatePath));
	}

	public void init(File template) {
		try {
			config = new Configuration();
			config.setDirectoryForTemplateLoading(template);
			config.setOutputEncoding("UTF-8");
			config.setDefaultEncoding("UTF-8");
			config.setNumberFormat("###################");
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	public Template getTemplate(String name) {
		if (config == null) {
			throw new IllegalStateException("Please call init method first.");
		}
		Template template = null;
		try {
			template = config.getTemplate(name);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
		return template;
	}


}
