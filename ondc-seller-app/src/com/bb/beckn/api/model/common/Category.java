package com.bb.beckn.api.model.common;

import java.util.List;

//import com.bb.beckn.search.model.CustomGroupObj;
//import com.bb.beckn.search.model.VariantGroupObj;

import lombok.Data;

@Data
public class Category {
	private String id;
	private String parentCategoryId;
	private Descriptor descriptor;
	private Time time;
	private List<Tags> tags;
	//private CustomGroupObj customGroupObj;
	//private VariantGroupObj variantGroupObj;
}
