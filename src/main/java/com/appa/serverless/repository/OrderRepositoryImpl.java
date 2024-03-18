package com.appa.serverless.repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.appa.serverless.model.Cart;
import com.appa.serverless.model.Order;
import com.appa.serverless.model.Response;
import com.appa.serverless.model.Shake;
import com.appa.serverless.model.converter.ShakeConverter;
import com.appa.serverless.service.DynamoDBService;
import com.appa.serverless.service.JWTService;

@Repository
public class OrderRepositoryImpl implements OrderRepository {

	@Autowired
	private ShakeProductRepository shakeProductRepository = new ShakeProductRepositoryImpl();

	@Override
	// Params:
	// token: string => is a protected method
	// id_user: int => Who make the order
	// total: double => how mush he spent
	// elements {id_product: int, qty: int} => the list of the product
	// Return code:
	// 1 => Order correctly saved
	// -1 => The Token is not valid
	// -2 => The Token is correct but the other params are not valid
	// -999 => Undefined error occurs
	public Response createOrder(String params) {
		Response res = new Response();
		JSONObject jsonObject = new JSONObject(params);

		try {
			String token = jsonObject.getString("token");
			if (token != null && JWTService.validateToken(token).getCode() == 1) {

				String id_user = jsonObject.getString("id_user");
				double total = jsonObject.getDouble("total");
				JSONArray jsonElements = jsonObject.getJSONArray("elements");
				if (jsonObject.get("id_user") != null && total != '0' && jsonElements != null) {
					String msg = "";
					Cart[] elements = new Cart[jsonElements.length()];
					for (int i = 0; i < jsonElements.length(); i++) {
						JSONObject jsonElem = jsonElements.getJSONObject(i);
						elements[i] = new Cart(jsonElem.getString("id_product"), jsonElem.getInt("qty"));
					}

					Order ord = new Order(Long.toString(new Date().getTime()), id_user, elements, total);
					DynamoDBService.getInstance().getDynamoDBMapper().save(ord);

					msg += "ORDER_SAVED ";

					ShakeConverter converter = new ShakeConverter();
					for (Cart c : elements) {
						res = shakeProductRepository.findShakeById("{id:'" + c.getId_product() + "'}");
						if (res.getData() != null) {
							Shake s = (Shake) res.getData()[0];
							s.setNumOrder(s.getNumOrder() + 1);
							res = shakeProductRepository
									.updateShake("{token:" + token + ", " + converter.convertNew(s).substring(1));
							msg += "& SHAKE_" + s.getId() + "_UPDATED";
						}

					}

					res.setCode(1);
					res.setMessage(msg);
					res.setData(new Object[] { ord });
				} else {
					res.setCode(-2);
					res.setMessage("PARAMS_NOT_VALID");
				}

			} else {
				res.setCode(-1);
				res.setMessage("TOKEN_NOT_VALID");
			}
		} catch (Exception e) {
			res.setCode(-999);
			res.setMessage("ERROR_OCCURS: " + e.getMessage());
		}

		return res;
	}

	@Override
	// Params:
	// token: string => is a protected method and contains the sub = id_user
	// Return code:
	// 1 => Order correctly retrieved
	// -1 => The Token is not valid
	// -2 => The Token is correct but the other params are not valid (id_user)
	// -999 => Undefined error occurs
	public Response getOrderByUser(String token) {
		Response res = new Response();
		try {
			res = JWTService.validateToken(token);
			if (token != null && res.getCode() == 1) {
				System.out.println(res);
				String id_user = (String) res.getData()[0];
				if (id_user != null) {

					Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
					expressionAttributeValues.put(":id_user", new AttributeValue().withS(id_user));

					DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
							.withFilterExpression("id_user = :id_user")
							.withExpressionAttributeValues(expressionAttributeValues);

					List<Order> ords = DynamoDBService.getInstance().getDynamoDBMapper().scan(Order.class,
							scanExpression);

					List<Order> o = new ArrayList<Order>(ords);
					o.sort(new Comparator<Order>() {

						@Override
						public int compare(Order o1, Order o2) {
							// Discenting => the most recent first
							long orderID1 = Long.parseLong(o1.getId());
							long orderID2 = Long.parseLong(o2.getId());
							if (orderID1 < orderID2)
								return 1;
							else if (orderID1 > orderID2)
								return -1;
							else
								return 0;
						}

					});

					res.setCode(1);
					res.setMessage("ORDER_RETRIEVED");
					res.setData(new Object[] { o.toArray() });
				} else {
					res.setCode(-2);
					res.setMessage("PARAMS_NOT_VALID");
				}

			} else {
				res.setCode(-1);
				res.setMessage("TOKEN_NOT_VALID");
			}
		} catch (Exception e) {
			res.setCode(-999);
			res.setMessage("ERROR_OCCURS: " + e.getMessage());
		}

		return res;
	}

	/*
	 * NOT EFFICENT
	 * 
	 * @Override // Params: // id_product // Return code: // 1 => Order quantity
	 * correctly retrieved // -1 => Params are not valid (id_product) // -999 =>
	 * Undefined error occurs public Response getOrdersNumberOfProduct(String
	 * params) { Response res = new Response(); JSONObject jsonObject = new
	 * JSONObject(params);
	 * 
	 * try {
	 * 
	 * String id_product = jsonObject.getString("id_product"); if (id_product !=
	 * null) {
	 * 
	 * List<Order> ords =
	 * DynamoDBService.getInstance().getDynamoDBMapper().scan(Order.class, new
	 * DynamoDBScanExpression());
	 * 
	 * // DynamoDB does not support querying on nested attributes directly within a
	 * // KeyConditionExpression, so I have to filer after retrieve all the orders
	 * int count = 0; for (Order o : ords) for (Cart c : o.getElements()) { if
	 * (c.getId_product() == Integer.parseInt(id_product)) { count++; // assuming
	 * that for every order the product that i am searching i specified one // time
	 * next the attribute qty break; } }
	 * 
	 * res.setCode(1); res.setMessage("ORDER_COUNT_RETRIEVED"); res.setData(new
	 * Object[] { count }); } else { res.setCode(-1);
	 * res.setMessage("PARAMS_NOT_VALID"); }
	 * 
	 * } catch (Exception e) { res.setCode(-999); res.setMessage("ERROR_OCCURS: " +
	 * e.getMessage()); }
	 * 
	 * return res; }
	 */

	public static void main(String[] args) {
		OrderRepositoryImpl impl = new OrderRepositoryImpl();

		// System.out.println(impl
		// .createOrder("{token:'',id_user:'1',elements:[{id_product:1,qty:2},{id_product:2,qty:4}],total:2.2}"));

		String s = "{'token':'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI4NzFkZWYwNS1iYmQwLTQ2YmYtYTdjZS0zYjZjNzE2ZDQ5NTciLCJleHAiOjE3MTA1ODk0MzJ9.2-UArRWce_FxSUNz3EEWEG43XHauKhZfP5-XX9j60pk','id_user':'871def05-bbd0-46bf-a7ce-3b6c716d4957','elements':[],'total':'26.84'}";

		System.out.println(impl.createOrder(s));
		// System.out.println(impl.getOrderByUser(
		// "ey....."));

	}

}