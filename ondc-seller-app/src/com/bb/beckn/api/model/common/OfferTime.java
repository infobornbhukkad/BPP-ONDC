package com.bb.beckn.api.model.common;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class OfferTime {
	private String valid;
	private OfferRange range;
}
