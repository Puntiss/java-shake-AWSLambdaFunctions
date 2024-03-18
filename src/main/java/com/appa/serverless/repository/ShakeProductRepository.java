package com.appa.serverless.repository;

import com.appa.serverless.model.Response;

public interface ShakeProductRepository {

	public Response getAllShake();

	public Response insertShake(String params);

	public Response findShakeById(String params);
	
	public Response removeShake(String params);

	public Response updateShake(String params);
}
