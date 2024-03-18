package com.appa.serverless.model;

import java.io.Serializable;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import com.appa.serverless.model.converter.CartConverter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBTable(tableName = "order")
public class Order implements Serializable {

	private static final long serialVersionUID = 1554640551199966153L;

	@DynamoDBHashKey(attributeName = "id")
	private String id;

	@DynamoDBAttribute(attributeName = "id_user")
	private String id_user;

	@DynamoDBAttribute(attributeName = "elements")
	@DynamoDBTypeConverted(converter = CartConverter.class)
	private Cart[] elements;

	@DynamoDBAttribute(attributeName = "total")
	private double total;

}
