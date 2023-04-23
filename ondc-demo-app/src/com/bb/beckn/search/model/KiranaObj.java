package com.bb.beckn.search.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "bb_admin_panel_vendors_kirana", uniqueConstraints = { @UniqueConstraint(columnNames = "username") })
public class KiranaObj {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	private String username;

	@NotBlank
	private String storetype;

	@NotBlank
	private String storename;

	private String email;

	@NotBlank
	private String address;

	@NotBlank
	private String latitude;

	@NotBlank
	private String longitude;

	@NotBlank
	private String opted;

	@NotBlank
	private String phone;

	@NotBlank
	private String accId;

	@NotBlank
	private String userId;

	@NotBlank
	private String isOpen;

	@NotBlank
	private String createddt;

	@NotBlank
	private String updateddt;

	@NotBlank
	private String updateduserid;

	@NotBlank
	private String cGST;

	@NotBlank
	private String sGST;
	
	private String gstn;

	@NotBlank
	private String offerPercen;
	
	private String cost_for_two;
	
	private String storeopentimehours;
	
	private String storeclosetimehours;
	
	private String storeworkingdays;
	
	private String timetoshipinminute;
	
    private String storesubtypehyperlocal;
	
	private String storesubtypeintercity;
	
	private String storesubtypepanindia;
	
	private String packagingcharge;	
	
    private String city;
    
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
	public String getTimetoshipinminute() {
		return timetoshipinminute;
	}

	public void setTimetoshipinminute(String timetoshipinminute) {
		this.timetoshipinminute = timetoshipinminute;
	}
	
	public String getPackagingcharge() {
		return packagingcharge;
	}

	public void setPackagingcharge(String packagingcharge) {
		this.packagingcharge = packagingcharge;
	}

	public String getStoresubtypehyperlocal() {
		return storesubtypehyperlocal;
	}

	public void setStoresubtypehyperlocal(String storesubtypehyperlocal) {
		this.storesubtypehyperlocal = storesubtypehyperlocal;
	}

	public String getStoresubtypeintercity() {
		return storesubtypeintercity;
	}

	public void setStoresubtypeintercity(String storesubtypeintercity) {
		this.storesubtypeintercity = storesubtypeintercity;
	}

	public String getStoresubtypepanindia() {
		return storesubtypepanindia;
	}

	public void setStoresubtypepanindia(String storesubtypepanindia) {
		this.storesubtypepanindia = storesubtypepanindia;
	}

	public String getStoreopentimehours() {
		return storeopentimehours;
	}

	public void setStoreopentimehours(String storeopentimehours) {
		this.storeopentimehours = storeopentimehours;
	}

	public String getStoreclosetimehours() {
		return storeclosetimehours;
	}

	public void setStoreclosetimehours(String storeclosetimehours) {
		this.storeclosetimehours = storeclosetimehours;
	}

	public String getStoreworkingdays() {
		return storeworkingdays;
	}

	public void setStoreworkingdays(String storeworkingdays) {
		this.storeworkingdays = storeworkingdays;
	}

	public String getGstn() {
		return gstn;
	}

	public void setGstn(String gstn) {
		this.gstn = gstn;
	}

	public String getCost_for_two() {
		return cost_for_two;
	}

	public void setCost_for_two(String cost_for_two) {
		this.cost_for_two = cost_for_two;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getShort_desc() {
		return short_desc;
	}

	public void setShort_desc(String short_desc) {
		this.short_desc = short_desc;
	}

	public String getLong_desc() {
		return long_desc;
	}

	public void setLong_desc(String long_desc) {
		this.long_desc = long_desc;
	}

	public String getSymbol_name() {
		return symbol_name;
	}

	public void setSymbol_name(String symbol_name) {
		this.symbol_name = symbol_name;
	}

	private String symbol;
	private String short_desc;
	private String long_desc;
	private String symbol_name;
	
	public KiranaObj() {
		super();
	}

	public KiranaObj(String storetype, String storename, String email, String address, String opted, String phone,
			String accId, String userId, String longitude, String latitude, String username, String createddt,
			String updateddt, String isOpen, String updateduserid, String cGST, String sGST, String offerPercen, String gstn, String symbol,String short_desc,
			String long_desc, String symbol_name, String cost_for_two) {
		this.storetype = storetype;
		this.storename = storename;
		this.email = email;
		this.address = address;
		this.opted = opted;
		this.phone = phone;
		this.accId = accId;
		this.userId = userId;
		this.longitude = longitude;
		this.latitude = latitude;
		this.username = username;
		this.createddt = createddt;
		this.updateddt = updateddt;
		this.isOpen = isOpen;
		this.updateduserid = updateduserid;
		this.cGST = cGST;
		this.sGST = sGST;
		this.offerPercen = offerPercen;
		this.cost_for_two=cost_for_two;
		this.symbol=symbol;
		this.short_desc=short_desc;
		this.long_desc=long_desc;
		this.symbol_name=symbol_name;
	}
    
	public String getgstn() {
		return gstn;
	}

	public void setgstn(String gstn) {
		this.gstn = gstn;
	}
	
	public String getOfferPercen() {
		return offerPercen;
	}

	public void setOfferPercen(String offerPercen) {
		this.offerPercen = offerPercen;
	}

	public String getcGST() {
		return cGST;
	}

	public void setcGST(String cGST) {
		this.cGST = cGST;
	}

	public String getsGST() {
		return sGST;
	}

	public void setsGST(String sGST) {
		this.sGST = sGST;
	}

	public String getUpdateduserid() {
		return updateduserid;
	}

	public void setUpdateduserid(String updateduserid) {
		this.updateduserid = updateduserid;
	}

	public String getIsOpen() {
		return isOpen;
	}

	public void setIsOpen(String isOpen) {
		this.isOpen = isOpen;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getCreateddt() {
		return createddt;
	}

	public void setCreateddt(String createddt) {
		this.createddt = createddt;
	}

	public String getUpdateddt() {
		return updateddt;
	}

	public void setUpdateddt(String updateddt) {
		this.updateddt = updateddt;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getPhone() {
		return phone;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAccId() {
		return accId;
	}

	public void setAccId(String accId) {
		this.accId = accId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getStoretype() {
		return storetype;
	}

	public void setStoretype(String storetype) {
		this.storetype = storetype;
	}

	public String getStorename() {
		return storename;
	}

	public void setStorename(String storename) {
		this.storename = storename;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getOpted() {
		return opted;
	}

	public void setOpted(String opted) {
		this.opted = opted;
	}

}
