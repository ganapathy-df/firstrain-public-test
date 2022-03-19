package com.firstrain.web.service.staticdata;

import org.apache.log4j.Logger;

import freemarker.core.Environment;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

public class FRTemplateExceptionHandler implements TemplateExceptionHandler {
	private static final Logger LOG = Logger.getLogger(FRTemplateExceptionHandler.class);

	public void handleTemplateException(TemplateException te, Environment env, java.io.Writer out) throws TemplateException {
		/*
		 * if(LOG.isDebugEnabled()) { LOG.error(te.getMessage(), te); } else { throw te; }
		 */
		throw te;
	}
}
