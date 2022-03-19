package com.firstrain.web.service.core;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.firstrain.db.obj.EntityBacktest;
import com.firstrain.frapi.customapirepository.impl.EntityBackTestRepositoryImpl;

@Service
public class EntiyBackTestService {

	private static final Logger LOG = Logger.getLogger(EntiyBackTestService.class);
	private static final String STATE_TERMINATED = "TERMINATED";

	@Autowired
	private EntityBackTestRepositoryImpl entityBackTestRepositoryImpl;

	public EntityBacktest updateState(long id) throws Exception {
		EntityBacktest eb = entityBackTestRepositoryImpl.updateState(STATE_TERMINATED, id);
		return eb;
	}
}
