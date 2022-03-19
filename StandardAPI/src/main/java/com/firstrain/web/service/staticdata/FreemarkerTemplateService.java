package com.firstrain.web.service.staticdata;

import java.io.IOException;
import java.io.StringWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@Service
public class FreemarkerTemplateService {

	@Autowired
	private Configuration ftlConfig;

	public String getHtml(String ftlName, Object root) throws IOException, TemplateException {
		String html = null;
		// TODO : uncomment perflogging
		// long st = PerfMonitor.currentTime();
		try {
			Template template = ftlConfig.getTemplate(ftlName);
			StringWriter out = new StringWriter();
			template.process(root, out);
			html = out.toString();
		} finally {
			// PerfMonitor.recordStats(st, PerfActivityType.Others, "ftl.fmt");
		}
		return html;
	}
}
