package com.appa.serverless.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.stereotype.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.appa.serverless.model.Response;
import com.appa.serverless.model.Shake;
import com.appa.serverless.service.DynamoDBService;
import com.appa.serverless.service.JWTService;

@Repository
public class ShakeProductRepositoryImpl implements ShakeProductRepository {

	@Override
	// Return code:
	// 1 => Shake retrieved possible empty []
	// -999 => Undefined error occurs
	public Response getAllShake() {
		Response res = new Response();

		try {

			DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
			PaginatedScanList<Shake> shakeList = DynamoDBService.getInstance().getDynamoDBMapper().scan(Shake.class,
					scanExpression);

			res.setCode(1);
			res.setMessage("SHAKE_RETRIEVED");
			res.setData(new Object[] { shakeList.toArray() });

		} catch (

		Exception e) {
			res.setCode(-999);
			res.setMessage("ERROR_OCCURS: " + e.getMessage());
		}

		return res;
	}

	@Override
	// Params:
	// token: string => is a protected method
	// name: string
	// price: string
	// discountedPrice: string
	// shippingPrice: string
	// qtyAvailable: string
	// description: string
	// imgUrl: string
	// Return code:
	// 1 => Shake correctly insert
	// -1 => The Token is not valid
	// -2 => The Token is correct but the other params are not valid
	// -999 => Undefined error occurs
	public Response insertShake(String params) {
		Response res = new Response();
		JSONObject jsonObject = new JSONObject(params);

		try {
			String token = jsonObject.getString("token");
			if (token != null && JWTService.validateToken(token).getCode() == 1) {

				String name = jsonObject.getString("name");
				double price = jsonObject.getDouble("price");
				double discountedPrice = jsonObject.getDouble("discountedPrice");
				double shippingPrice = jsonObject.getDouble("shippingPrice");
				int qtyAvailable = jsonObject.getInt("qtyAvailable");
				String description = jsonObject.getString("description");
				String imgUrl = jsonObject.getString("imgUrl");

				if (name != null && description != null && imgUrl != null) {

					Shake shake = new Shake();
					shake.setName(name);
					shake.setPrice(price);
					shake.setDiscountedPrice(discountedPrice);
					shake.setShippingPrice(shippingPrice);
					shake.setQtyAvailable(qtyAvailable);
					shake.setDescription(description);
					shake.setImgUrl(imgUrl);

					DynamoDBService.getInstance().getDynamoDBMapper().save(shake);

					res.setCode(1);
					res.setMessage("SHAKE_SAVED");
					res.setData(new Object[] { shake });
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
	// id: string => id of the existing shake
	// Return code:
	// 1 => Shake found
	// -1 => Shake not found
	// -999 => Undefined error occurs
	public Response findShakeById(String params) {
		Response res = new Response();
		JSONObject jsonObject = new JSONObject(params);

		try {

			String id = jsonObject.getString("id");
			if (id != null) {

				Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
				expressionAttributeValues.put(":id", new AttributeValue().withS(id));

				DynamoDBScanExpression scanExpression = new DynamoDBScanExpression().withFilterExpression("id = :id")
						.withExpressionAttributeValues(expressionAttributeValues);

				List<Shake> shake = DynamoDBService.getInstance().getDynamoDBMapper().scan(Shake.class, scanExpression);

				if (shake == null || shake.size() == 0) {
					res.setCode(-3);
					res.setMessage("SHAKE_NOT_FOUND");
				} else {

					res.setCode(1);
					res.setMessage("SHAKE_FOUND");
					res.setData(new Object[] { shake.toArray()[0] });
				}

			} else {
				res.setCode(-2);
				res.setMessage("PARAMS_NOT_VALID");
			}

		} catch (Exception e) {
			res.setCode(-999);
			res.setMessage("ERROR_OCCURS: " + e.getMessage());
		}

		return res;

	}

	@Override
	// Params:
	// token: string => is a protected method
	// id: string
	// Return code:
	// 1 => Shake correctly deleted
	// -1 => The Token is not valid
	// -2 => The Token is correct but the other params are not valid
	// -3 => Shake given not found and not deleted
	// -999 => Undefined error occurs
	public Response removeShake(String params) {
		Response res = new Response();
		JSONObject jsonObject = new JSONObject(params);

		try {
			String token = jsonObject.getString("token");
			if (token != null && JWTService.validateToken(token).getCode() == 1) {

				String id = jsonObject.getString("id");
				if (id != null) {

					res = findShakeById("{id:'" + id + "'}");
					if (res.getCode() == 1)

						DynamoDBService.getInstance().getDynamoDBMapper().delete(res.getData()[0]);

					res.setCode(1);
					res.setMessage("SHAKE_DELETED");

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
	// token: string => is a protected method
	// id: string => id of the existing shake
	// new_name: string
	// new_price: string
	// new_discountedPrice: number
	// new_shippingPrice: number
	// new_qtyAvailable: number
	// new_description: string
	// new_imgUrl: string
	// Return code:
	// 1 => Shake correctly updated
	// -999 => Undefined error occurs
	public Response updateShake(String params) {
		Response res = new Response();
		JSONObject jsonObject = new JSONObject(params);

		try {
			String token = jsonObject.getString("token");
			if (token != null && JWTService.validateToken(token).getCode() == 1) {

				String id = jsonObject.getString("id");
				if (id != null) {

					res = findShakeById("{id:'" + id + "'}");
					if (res.getCode() == 1) {

						String new_name = jsonObject.getString("new_name");
						int new_numOrder = jsonObject.getInt("new_numOrder");
						double new_price = jsonObject.getDouble("new_price");
						double new_discountedPrice = jsonObject.getDouble("new_discountedPrice");
						double new_shippingPrice = jsonObject.getDouble("new_shippingPrice");
						int new_qtyAvailable = jsonObject.getInt("new_qtyAvailable");
						String new_description = jsonObject.getString("new_description");
						String new_imgUrl = jsonObject.getString("new_imgUrl");

						if (new_name != null && new_description != null && new_imgUrl != null) {

							Shake shake = new Shake();
							shake.setId(id);
							shake.setName(new_name);
							shake.setNumOrder(new_numOrder);
							shake.setPrice(new_price);
							shake.setDiscountedPrice(new_discountedPrice);
							shake.setShippingPrice(new_shippingPrice);
							shake.setQtyAvailable(new_qtyAvailable);
							shake.setDescription(new_description);
							shake.setImgUrl(new_imgUrl);

							DynamoDBService.getInstance().getDynamoDBMapper().save(shake);

							res.setCode(1);
							res.setMessage("SHAKE_UPDATED");
							res.setData(new Object[] { findShakeById("{id:'" + id + "'}").getData()[0] });
						}

					}

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

	public static void main(String[] args) {
		ShakeProductRepositoryImpl impl = new ShakeProductRepositoryImpl();
		Response res = impl.insertShake(
				"{token:'', name:'Type Shake', price:12.3, discountedPrice:12.3, shippingPrice:0, qtyAvailable:0, description:'I like this shake', imgUrl:''}");
		// System.out.println(res);
		// System.out.println(impl.getAllShake());
		// System.out.println(impl.removeShake("{token:'', id:'" + ((Shake)
		// res.getData()[0]).getId() + "'}"));

		// System.out.println(impl.updateShake(
		// "{token:'',
		// id: '', new_name: 'Type Shake333',
		// new_numOrder: 1, new_price: 12.3, new_discountedPrice: 12.3,
		// new_shippingPrice: 0.0, new_qtyAvailable: 0, new_description: 'I like this
		// shake', new_imgUrl: 'null'}"));

	}

}
