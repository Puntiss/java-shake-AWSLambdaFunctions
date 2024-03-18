package com.appa.serverless.repository;

import com.appa.serverless.model.Response;

public interface OrderRepository {

	public Response createOrder(String params);
	
	public Response getOrderByUser(String params);
	
	//public Response getOrdersNumberOfProduct(String params);

}
