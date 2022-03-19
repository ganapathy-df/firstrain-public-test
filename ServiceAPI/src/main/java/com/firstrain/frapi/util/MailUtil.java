package com.firstrain.frapi.util;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.firstrain.utils.FR_MailUtils.MailInfo;

import freemarker.template.Template;
import freemarker.template.TemplateException;

@Service
public class MailUtil {

	@Autowired
	FreemarkerTemplates freemarkerTemplates;

	private static final Logger LOG = Logger.getLogger(MailUtil.class);

	public MailInfo prepareMailInfo(String subject, String to, String sender, String mailServer) throws Exception {
		MailInfo mailInfo = new MailInfo();
		mailInfo.toRecipients = to;
		mailInfo.mailServerHostname = mailServer;
		mailInfo.sender = sender; // session user
		mailInfo.subject = subject;
		mailInfo.formatType = MailInfo.FORMAT_HTML;
		mailInfo.encoding = "UTF-8";
		if (LOG.isDebugEnabled()) {
			mailInfo.logLevel = "debug";
		}
		return mailInfo;
	}

	public String getHtmlToTransmit(Map<String, Object> userMap, String ftl) throws IOException, TemplateException {
		Template temp = freemarkerTemplates.getTemplate(ftl);
		Writer out = new StringWriter();
		temp.process(userMap, out);
		return out.toString();
	}

}
