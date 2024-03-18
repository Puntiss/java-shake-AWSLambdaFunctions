package com.appa.serverless.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Cart implements Serializable {

	private static final long serialVersionUID = 1958075160733872355L;

	private String id_product;

	private int qty;

}
