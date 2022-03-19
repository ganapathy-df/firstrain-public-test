package com.firstrain.frapi.repository.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.firstrain.common.db.jpa.PersistenceProvider;
import com.firstrain.db.api.AccelerometerDbAPI;
import com.firstrain.db.obj.Accelerometer;
import com.firstrain.frapi.repository.AccelerometerServiceRepository;

@Repository
public class AccelerometerServiceRepositoryImpl implements AccelerometerServiceRepository {

	@Override
	public List<Accelerometer> getAccelerometer(String catIds) throws Exception {

		return AccelerometerDbAPI.getAccelerometerByCatIds(PersistenceProvider.CACHE_DATABASE_READ, catIds);
	}
}
