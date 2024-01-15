package com.bb.beckn.api.model.common;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Payload {
	private Scalar weight;
	private Dimensions dimensions;
	//added
	@JsonProperty("category")
	private String additionalProp1;
	private Scalar value;
}
