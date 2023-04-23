package com.bb.beckn.search.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "ondc_bap_finder_fee")
public class BapFeeObj {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	private String type;

	@NotBlank
	private String finder_fee_amount;	
	
	@NotBlank
	private String created_on;
	
	private String updated_on;
	
	private String subscriberid;
	
	public String getCreated_on() {
		return created_on;
	}

	public void setCreated_on(String created_on) {
		this.created_on = created_on;
	}

	public BapFeeObj() {
		super();
	}

	public BapFeeObj(@NotBlank String type, @NotBlank String finder_fee_amount, String created_on, String updated_on,  String subscriberid) {
		super();
		this.type = type;
		this.finder_fee_amount = finder_fee_amount;
		this.created_on = created_on;
		this.updated_on = updated_on;
		this.subscriberid = subscriberid;
		
	}
   
	public String getSubscriberid() {
		return subscriberid;
	}

	public void setSubscriberid(String subscriberid) {
		this.subscriberid = subscriberid;
	}

	public String getUpdated_on() {
		return updated_on;
	}

	public void setUpdated_on(String updated_on) {
		this.updated_on = updated_on;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFinder_fee_amount() {
		return finder_fee_amount;
	}

	public void setFinder_fee_amount(String finder_fee_amount) {
		this.finder_fee_amount = finder_fee_amount;
	}

	

	
	
	

}
