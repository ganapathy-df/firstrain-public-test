package com.firstrain.web.service.core;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class FRResourceUtils {

	private DefaultResourceLoader loader = new DefaultResourceLoader();

	public final Resource getResource(String location) {
		Assert.notNull(location, "Location must not be null");
		return loader.getResource(location);
	}
}
