package com.firstrain.frapi.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.firstrain.db.obj.Accelerometer;

@Repository
public interface AccelerometerServiceRepository {

	public List<Accelerometer> getAccelerometer(String catIds) throws Exception;

}
