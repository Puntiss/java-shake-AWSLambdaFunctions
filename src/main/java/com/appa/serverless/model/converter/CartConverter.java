package com.appa.serverless.model.converter;

import org.json.JSONArray;
import org.json.JSONObject;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.appa.serverless.model.Cart;

public class CartConverter implements DynamoDBTypeConverter<String, Cart[]> {
	@Override
	public String convert(Cart[] object) {
		String result = "";
		for (Cart c : object)
			result += "{id_product: " + c.getId_product() + ", qty: " + c.getQty() + "}, ";
		result = result.substring(0, result.lastIndexOf(","));
		return "[" + result + "]";
	}

	@Override
	public Cart[] unconvert(String object) {

		JSONArray jsonElements = new JSONArray(object);

		Cart[] elements = new Cart[jsonElements.length()];
		for (int i = 0; i < jsonElements.length(); i++) {
			JSONObject jsonElem = jsonElements.getJSONObject(i);
			elements[i] = new Cart(jsonElem.getString("id_product"), jsonElem.getInt("qty"));
		}
		return elements;
	}

	public static void main(String[] args) {
		CartConverter converter = new CartConverter();
		Cart[] object = new Cart[] { new Cart("896-dec", 2), new Cart("896-dec", 3) };

		System.out.println(converter.convert(object));
		for (Cart c : converter.unconvert(converter.convert(object)))
			System.out.println(c);

	}
}