package com.bb.beckn.search.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "ondc_bpp_logistic_finder")
public class LogisticBppObj {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	private String providername;

	@NotBlank
	private String bppid;	
	
	@NotBlank
	private String bppurl;
	
	private String updated_on;
	
	private String created_on;
	
	private String json;
	private String transaction_id;
	private String message_id;
	private String providerid;
	private String deliverytype;
	private String action;	
	private String deliverycharge;
	private String ordertodelivery;
	
	
	public String getOrdertodelivery() {
		return ordertodelivery;
	}

	public void setOrdertodelivery(String ordertodelivery) {
		this.ordertodelivery = ordertodelivery;
	}

	public String getDeliverytype() {
		return deliverytype;
	}

	public void setDeliverytype(String deliverytype) {
		this.deliverytype = deliverytype;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getDeliverycharge() {
		return deliverycharge;
	}

	public void setDeliverycharge(String deliverycharge) {
		this.deliverycharge = deliverycharge;
	}

	public String getProviderid() {
		return providerid;
	}

	public void setProviderid(String providerid) {
		this.providerid = providerid;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	public LogisticBppObj() {
		super();
	}
    
	public LogisticBppObj(@NotBlank String providername, @NotBlank String bppid, String bppurl, String created_on,  String updated_on, String json, String transaction_id, String message_id, String providerid,
			String deliverycharge, String deliverytype, String action, String ordertodelivery) {
		super();
		this.providername = providername;
		this.bppid = bppid;
		this.bppurl = bppurl;
		this.updated_on = updated_on;
		this.created_on = created_on;
		this.json = json;
		this.transaction_id = transaction_id;
		this.message_id = message_id;
		this.providerid = providerid;
		this.deliverycharge=deliverycharge;
		this.deliverytype=deliverytype;
		this.action=action;
		this.ordertodelivery=ordertodelivery;
	}
    
	public String getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(String transaction_id) {
		this.transaction_id = transaction_id;
	}

	public String getMessage_id() {
		return message_id;
	}

	public void setMessage_id(String message_id) {
		this.message_id = message_id;
	}

	public String getProvidername() {
		return providername;
	}

	public void setProvidername(String providername) {
		this.providername = providername;
	}

	public String getBppid() {
		return bppid;
	}

	public void setBppid(String bppid) {
		this.bppid = bppid;
	}

	public String getBppurl() {
		return bppurl;
	}

	public void setBppurl(String bppurl) {
		this.bppurl = bppurl;
	}

	public String getUpdated_on() {
		return updated_on;
	}

	public void setUpdated_on(String updated_on) {
		this.updated_on = updated_on;
	}

	public String getCreated_on() {
		return created_on;
	}

	public void setCreated_on(String created_on) {
		this.created_on = created_on;
	}
   
		

}
