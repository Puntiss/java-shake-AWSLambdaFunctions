package com.appa.serverless.service;

import org.springframework.beans.factory.annotation.Value;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;

public class DynamoDBService {

	@Value("${security.KEY}")
	private String key = "";

	@Value("${security.SECRET}")
	private String secret = "";

	private DynamoDBMapper dynamoDBMapper = null;

	private static DynamoDBService instance = null;

	public static DynamoDBService getInstance() {
		if (instance == null) {
			instance = new DynamoDBService();
		}
		return instance;
	}

	private DynamoDBService() {
		AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(key, secret)))
				.withRegion(Regions.US_EAST_2).build();

		this.dynamoDBMapper = new DynamoDBMapper(client, DynamoDBMapperConfig.DEFAULT);
	}

	public DynamoDBMapper getDynamoDBMapper() {
		return dynamoDBMapper;
	}

}
