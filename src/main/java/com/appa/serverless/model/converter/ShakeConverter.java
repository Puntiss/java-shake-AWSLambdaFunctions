package com.appa.serverless.model.converter;

import org.json.JSONArray;
import org.json.JSONObject;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.appa.serverless.model.Shake;

public class ShakeConverter implements DynamoDBTypeConverter<String, Shake[]> {
	@Override
	public String convert(Shake[] object) {
		String result = "";
		for (Shake s : object)
			result += "{id: '" + s.getId() + "', name: '" + s.getName() + "', numOrder: " + s.getNumOrder()
					+ ", price: " + s.getPrice() + ", discountedPrice: " + s.getDiscountedPrice() + ", shippingPrice: "
					+ s.getShippingPrice() + ", qtyAvailable: " + s.getQtyAvailable() + ", description: '"
					+ s.getDescription() + "', imgUrl: '" + s.getImgUrl() + "'}, ";
		result = result.substring(0, result.lastIndexOf(","));
		return "[" + result + "]";
	}

	public String convertNew(Shake s) {
		return "{id: '" + s.getId() + "', new_name: '" + s.getName() + "', new_numOrder: " + s.getNumOrder() + ", new_price: "
				+ s.getPrice() + ", new_discountedPrice: " + s.getDiscountedPrice() + ", new_shippingPrice: "
				+ s.getShippingPrice() + ", new_qtyAvailable: " + s.getQtyAvailable() + ", new_description: '"
				+ s.getDescription() + "', new_imgUrl: '" + s.getImgUrl() + "'}";
	}

	@Override
	public Shake[] unconvert(String object) {

		JSONArray jsonElements = new JSONArray(object);

		Shake[] elements = new Shake[jsonElements.length()];
		for (int i = 0; i < jsonElements.length(); i++) {
			JSONObject jsonElem = jsonElements.getJSONObject(i);
			elements[i] = new Shake(jsonElem.getString("id"), jsonElem.getString("name"), jsonElem.getInt("numOrder"),
					jsonElem.getDouble("price"), jsonElem.getDouble("discountedPrice"),
					jsonElem.getDouble("shippingPrice"), jsonElem.getInt("qtyAvailable"),
					jsonElem.getString("description"), jsonElem.getString("imgUrl"));
		}
		return elements;
	}

	public static void main(String[] args) {
		ShakeConverter converter = new ShakeConverter();

		Shake[] object = new Shake[] { new Shake("8967ad4d-3511-4262-86b6-4a565a837c17", "Coffe milkshake", 0, 15.3, 11,
				1, 110,
				"If you’ve never tried a Bailey’s cookies and cream shake before, friend, you’re going to love it. Cookies and cream is such a good combo with a bit of Irish cream.",
				"https://i2.wp.com/bakingmischief.com/wp-content/uploads/2022/03/coffee-milkshake-image-684x1024.jpg"),
				new Shake("2965d745-2230-4b72-bfb6-323bec7e0f55", "Strawberry Freakshakes", 0, 12, 11, 1, 110,
						"These Strawberry Freakshakes are fully loaded! Add toppings of your choice like donuts, sprinkles and shake to make the extreme milkshake of your dreams!",
						"https://i.pinimg.com/564x/f9/ad/ca/f9adcaab14b8341f47ff32c47cf3857a.jpg") };

		System.out.println(converter.convert(object));
		for (Shake s : converter.unconvert(converter.convert(object)))
			System.out.println(s);

	}
}