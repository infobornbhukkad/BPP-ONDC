package com.bb.beckn.api.model.oninit;

import com.bb.beckn.api.model.common.Context;
import com.bb.beckn.api.model.common.Scalar;

import lombok.Data;

@Data
public class OnInitRequest {
	private Context context;
	private OnInitMessage message;
	private Scalar error;
}