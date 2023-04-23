package com.bb.beckn.search.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

/**
 * @author Rishaan
 *
 */
@Entity
@Table(name = "bb_admin_panel_vendors_item_details")
public class ItemObj {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	private String vendorid;

	@NotBlank
	private String itemname;

	@NotBlank
	private String itemcategory;
    
	private String newCategory;
	
	@NotBlank
	private String itemstatus;

	@NotBlank
	private String itemquantity;
    
	private String newQuantity;
	
	private String unit;
	
	@NotBlank
	private String itemprice;

	@NotBlank
	private String createdDate;

	@NotBlank
	private String updatedDate;
	
	private String symbol;
	
	private String vegNonveg;
	
	
	public String getVegNonveg() {
		return vegNonveg;
	}

	public void setVegNonveg(String vegNonveg) {
		this.vegNonveg = vegNonveg;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	private String short_desc;
	private String long_desc;
	private String Item_image_1;
	private String Item_image_2;
	private String Item_image_3;
	private String Item_image_4;
	private String Item_image_1_name;
	private String Item_image_2_name;
	private String Item_image_3_name;
	private String Item_image_4_name;
	private String symbol_name;

	public String getNewCategory() {
		return newCategory;
	}

	public void setNewCategory(String newCategory) {
		this.newCategory = newCategory;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
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

	public String getItem_image_1() {
		return Item_image_1;
	}

	public void setItem_image_1(String item_image_1) {
		Item_image_1 = item_image_1;
	}

	public String getItem_image_2() {
		return Item_image_2;
	}

	public void setItem_image_2(String item_image_2) {
		Item_image_2 = item_image_2;
	}

	public String getItem_image_3() {
		return Item_image_3;
	}

	public void setItem_image_3(String item_image_3) {
		Item_image_3 = item_image_3;
	}

	public String getItem_image_4() {
		return Item_image_4;
	}

	public void setItem_image_4(String item_image_4) {
		Item_image_4 = item_image_4;
	}

	public String getItem_image_1_name() {
		return Item_image_1_name;
	}

	public void setItem_image_1_name(String item_image_1_name) {
		Item_image_1_name = item_image_1_name;
	}

	public String getItem_image_2_name() {
		return Item_image_2_name;
	}

	public void setItem_image_2_name(String item_image_2_name) {
		Item_image_2_name = item_image_2_name;
	}

	public String getItem_image_3_name() {
		return Item_image_3_name;
	}

	public void setItem_image_3_name(String item_image_3_name) {
		Item_image_3_name = item_image_3_name;
	}

	public String getItem_image_4_name() {
		return Item_image_4_name;
	}

	public void setItem_image_4_name(String item_image_4_name) {
		Item_image_4_name = item_image_4_name;
	}

	public String getSymbol_name() {
		return symbol_name;
	}

	public void setSymbol_name(String symbol_name) {
		this.symbol_name = symbol_name;
	}

	public ItemObj() {
		super();
	}

	public ItemObj(@NotBlank String vendorid, @NotBlank String itemname, @NotBlank String itemcategory, String newCategory,
			@NotBlank String itemstatus, @NotBlank String itemquantity, 
			String newQuantity,
			String unit, @NotBlank String itemprice, String createdDate,String symbol,  String short_desc, String long_desc, String Item_image_1, String Item_image_2, String Item_image_3, String Item_image_4,
			String Item_image_1_name,String Item_image_2_name, String Item_image_3_name, String Item_image_4_name, String symbol_name,
			String updatedDate, String vegNonveg) {
		super();
		this.vendorid = vendorid;
		this.itemname = itemname;
		this.itemcategory = itemcategory;
		this.newCategory = newCategory;
		this.itemstatus = itemstatus;
		this.itemquantity = itemquantity;
		this.newQuantity = newQuantity;
		this.unit = unit;
		this.itemprice = itemprice;
		this.createdDate = createdDate;
		this.updatedDate = updatedDate;
		
		this.symbol = symbol;
		this.short_desc = short_desc;
		this.long_desc = long_desc;
		this.Item_image_1 = Item_image_1;
		this.Item_image_2 = Item_image_2;
		this.Item_image_3 = Item_image_3;
		this.Item_image_4 = Item_image_4;
		this.Item_image_1_name = Item_image_1_name;
		this.Item_image_2_name = Item_image_2_name;
		this.Item_image_3_name = Item_image_3_name;
		this.Item_image_4_name = Item_image_4_name;
		this.symbol_name = symbol_name;
		this.vegNonveg = vegNonveg;
		
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(String updatedDate) {
		this.updatedDate = updatedDate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getVendorid() {
		return vendorid;
	}

	public void setVendorid(String vendorid) {
		this.vendorid = vendorid;
	}

	public String getItemname() {
		return itemname;
	}

	public void setItemname(String itemname) {
		this.itemname = itemname;
	}

	public String getItemcategory() {
		return itemcategory;
	}

	public void setItemcategory(String itemcategory) {
		this.itemcategory = itemcategory;
	}
    
	public String getnewCategory(){
		return newCategory;
	}

	public void setnewCategory(String newCategory) {
		this.newCategory = newCategory;
	}
	public String getItemstatus() {
		return itemstatus;
	}

	public void setItemstatus(String itemstatus) {
		this.itemstatus = itemstatus;
	}

	public String getItemquantity() {
		return itemquantity;
	}

	public void setItemquantity(String itemquantity) {
		this.itemquantity = itemquantity;
	}
    
	public String getNewQuantity(){
		return newQuantity;
	}

	public void setNewQuantity(String newQuantity) {
		this.newQuantity = newQuantity;
	}
	
	/*
	 * public String getunit(){ return unit; }
	 * 
	 * public void setunit(String unit) { this.unit = unit; }
	 */
	
	public String getItemprice() {
		return itemprice;
	}

	public void setItemprice(String itemprice) {
		this.itemprice = itemprice;
	}

}
