package com.bb.beckn.api.model.common;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;



@Data
public class Offer {
	private String id;
	private Descriptor descriptor;
	private List<String> locationIds;
	private List<Category> categoryIds;
	private List<String> itemIds;
	private OfferTime time;
	private List<OfferTags> tags;
}



//@Data
//public class Offer {
//	private Long id;
//	private String vendorid;
//	private String offerCode;
//	private String offerDescriptorCode;
//	private String offerDescription;
//	private Time startTime;
//	private Time endTime;
//	private Status status;
//}

//class OfferImage{
//	private Long id;
//	private Offer offer;
//	private String image_url;
//}
//
//class OfferLocation{
//	private Long id;
//	private Offer offer;
//	private String location;
//	private String latitude;
//	private String 	longitude;
//}
//
//class OfferTags {
//	private Long id;
//	private Offer offer;
//	private String code;
//	private String value;
//	private OfferTags parentOfferTag;
//}



//public enum Status {
//
//	VALID("VALID"),
//	INVALID("INVALID");
//
//	private final String value;
//
//	Status(String value) {
//		this.value = value;
//	}
//
//}