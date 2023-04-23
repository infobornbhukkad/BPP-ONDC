package com.bb.beckn.api.model.common;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data

public class Quotation {
	private Price price;
	private List<QuotationBreakUp> breakup;
	private String ttl;
}
