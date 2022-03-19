package com.firstrain.frapi.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.firstrain.frapi.FRService;
import com.firstrain.frapi.domain.BlendDunsInput;
import com.firstrain.frapi.pojo.Entity;

@Service
public interface DnbService extends FRService {

	public Entity getDnbEntity(String entityId, BlendDunsInput blendDunsInput) throws Exception;

	public List<Entity> getDnbEntities(String dnbIds) throws Exception;
}
