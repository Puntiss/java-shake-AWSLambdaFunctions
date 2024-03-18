package com.appa.serverless.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.stereotype.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.appa.serverless.model.Response;
import com.appa.serverless.model.User;
import com.appa.serverless.service.DynamoDBService;
import com.appa.serverless.service.EncryptService;
import com.appa.serverless.service.JWTService;

@Repository
public class UserRepositoryImpl implements UserRepository {

	@Override
	// Params
	// username: string
	// password: string
	// Return code:
	// 1 => Login done and JWT token generated
	// -1 => Params id_user or password not valid
	// -2 => User not found
	// -3 => Too many user found
	// -999 => Undefined error occurs
	public Response login(String params) {
		Response res = new Response();
		JSONObject jsonObject = new JSONObject(params);

		try {
			String username = jsonObject.getString("username");
			String password = jsonObject.getString("password");
			if (username != null && password != null) {

				Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
				expressionAttributeValues.put(":username", new AttributeValue().withS(username));
				expressionAttributeValues.put(":password",
						new AttributeValue().withS(EncryptService.encryptString(password)));

				DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
						.withFilterExpression("username = :username and password = :password")
						.withExpressionAttributeValues(expressionAttributeValues);

				List<User> users = DynamoDBService.getInstance().getDynamoDBMapper().scan(User.class, scanExpression);

				if (users == null || users.size() == 0) {
					res.setCode(-2);
					res.setMessage("USER_NOT_FOUND");
				} else if (users.size() == 1) {
					res.setCode(1);
					res.setMessage("LOGIN_DONE");
					res.setData(new Object[] { users.get(0),
							JWTService.generateToken(String.valueOf(users.get(0).getId())) });
				} else {
					res.setCode(-3);
					res.setMessage("TOO_MANY_USER_FOUND");
				}

			} else {
				res.setCode(-1);
				res.setMessage("PARAMS_NOT_VALID");
			}

		} catch (Exception e) {
			res.setCode(-999);
			res.setMessage("ERROR_OCCURS: " + e.getMessage());
		}

		return res;
	}

	@Override
	// Params
	// username: string
	// Return code:
	// 1 => User present
	// 2 => User not present
	// -1 => Params username not valid
	// -999 => Undefined error occurs
	public Response getUserByUsername(String params) {

		Response res = new Response();
		JSONObject jsonObject = new JSONObject(params);
		try {

			String username = jsonObject.getString("username");
			if (username != null) {

				Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
				expressionAttributeValues.put(":username", new AttributeValue().withS(username));

				DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
						.withFilterExpression("username = :username")
						.withExpressionAttributeValues(expressionAttributeValues);

				List<User> users = DynamoDBService.getInstance().getDynamoDBMapper().scan(User.class, scanExpression);

				if (users == null || users.size() == 0) {
					res.setCode(2);
					res.setMessage("USER_NOT_FOUND");
				} else if (users.size() > 0) {
					res.setCode(1);
					res.setMessage("USER_FOUND");
				}

			} else {
				res.setCode(-1);
				res.setMessage("PARAMS_NOT_VALID");
			}

		} catch (Exception e) {
			res.setCode(-999);
			res.setMessage("ERROR_OCCURS: " + e.getMessage());
		}

		return res;
	}

	@Override
	// Params
	// username: string
	// password: string
	// Return code:
	// 1 => User registered
	// -1 => Params id_user or password not valid
	// -2 => User is already present in DB
	// -999 => Undefined error occurs
	public Response register(String params) {
		Response res = new Response();
		JSONObject jsonObject = new JSONObject(params);

		try {
			String username = jsonObject.getString("username");
			String password = jsonObject.getString("password");
			if (username != null && password != null) {

				User user = new User();
				user.setUsername(username);
				user.setPassword(EncryptService.encryptString(password));

				if (getUserByUsername(params).getCode() == 2) {
					DynamoDBService.getInstance().getDynamoDBMapper().save(user);

					res.setCode(1);
					res.setMessage("USER_SAVED");
					res.setData(new Object[] { user });
				} else {
					res.setCode(-2);
					res.setMessage("USER_ALREADY_PRESENT");
				}

			} else {
				res.setCode(-1);
				res.setMessage("PARAMS_NOT_VALID");
			}

		} catch (Exception e) {
			res.setCode(-999);
			res.setMessage("ERROR_OCCURS: " + e.getMessage());
		}

		return res;
	}

	public static void main(String[] args) {
		UserRepositoryImpl impl = new UserRepositoryImpl();

		// System.out.println(impl.getUserByUsername("{username:'mario3'}"));
		//System.out.println(impl.register("{username:'mario3@gmail.com',password:'12345'}"));
		System.out.println(impl.login("{username:'mario3@gmail.com',password:'12345'}"));

	}

}