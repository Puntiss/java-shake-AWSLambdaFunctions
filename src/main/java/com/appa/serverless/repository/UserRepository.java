package com.appa.serverless.repository;

import com.appa.serverless.model.Response;

public interface UserRepository {

	public Response login(String params);

	public Response getUserByUsername(String params);
	
	public Response register(String params);
}
