package com.appa.serverless.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Response {
	private int code;
	private String message;
	private Object[] data;

}
