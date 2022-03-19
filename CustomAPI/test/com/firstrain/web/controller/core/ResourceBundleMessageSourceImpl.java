package com.firstrain.web.controller.core;

import java.util.Locale;
import org.springframework.context.support.ResourceBundleMessageSource;

public class ResourceBundleMessageSourceImpl extends ResourceBundleMessageSource {

  @Override
  public String getMessageInternal(String code, Object[] args, Locale locale) {
    return getMessageInternal(code, args, locale);
  }
}
