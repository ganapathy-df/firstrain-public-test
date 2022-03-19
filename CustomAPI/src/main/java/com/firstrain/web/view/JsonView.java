package com.firstrain.web.view;

import java.util.Map;

public class JsonView extends org.springframework.web.servlet.view.json.MappingJacksonJsonView {

	@Override
	protected Object filterModel(Map<String, Object> model) {
		Object view = model.get("JSONResponse");
		return view;
	}

}
