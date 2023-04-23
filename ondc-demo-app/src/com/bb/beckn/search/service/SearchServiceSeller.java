// 
// Decompiled by Procyon v0.5.36
// 

package com.bb.beckn.search.service;

import org.slf4j.LoggerFactory;
import com.bb.beckn.common.model.ConfigModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.bb.beckn.api.model.common.Context;
import com.bb.beckn.api.model.onsearch.OnSearchMessage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import com.bb.beckn.api.model.common.Ack;
import com.bb.beckn.api.model.common.Catalog;
import com.bb.beckn.ConnectionUtil;
import com.bb.beckn.api.enums.AckStatus;
import com.bb.beckn.api.model.response.ResponseMessage;
import com.bb.beckn.api.model.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.bb.beckn.search.extension.Schema;
import com.bb.beckn.search.model.BapFeeObj;
import com.bb.beckn.search.model.ItemDetailsKirana;
import com.bb.beckn.search.model.ItemObj;
import com.bb.beckn.search.utility.RetailResponseSearchfood;
import com.bb.beckn.search.utility.RetailResponseSearchkirana;
import com.bb.beckn.search.controller.OnSearchControllerSeller;
import com.bb.beckn.search.extension.OnSchema;
import java.sql.Timestamp;
import org.springframework.http.HttpHeaders;
import com.bb.beckn.common.util.JsonUtil;
import com.bb.beckn.common.validator.BodyValidator;
import com.bb.beckn.repository.BapFeeRepository;
import com.bb.beckn.repository.CityRepository;
import com.bb.beckn.repository.ItemRepository;
import com.bb.beckn.repository.ItemRepositoryKirana;
import com.bb.beckn.common.service.ApplicationConfigService;
import com.bb.beckn.common.sender.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.core.io.Resource;
import com.bb.beckn.common.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import com.bb.beckn.api.model.common.Error;

@Service
public class SearchServiceSeller
{
    private static final Logger log;
    @Autowired
	private ItemRepository mItemRepository;
    @Autowired
	ItemRepositoryKirana mItemRepositoryKirana;
    @Autowired
    private BapFeeRepository mBapFeeRepository;
    @Autowired
    private RetailResponseSearchkirana retailResponseSearchkirana;
    @Autowired
    private RetailResponseSearchfood retailResponseSearchfood;
    @Autowired
    private CityRepository mcityRepository;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private Sender sendRequest;
    @Autowired
    private ApplicationConfigService configService;
    @Autowired
    private BodyValidator bodyValidator;
    @Autowired
    private JsonUtil jsonUtil;
    @Autowired
    private OnSearchControllerSeller onSearchControllerSeller;
    @Autowired
    @Value("classpath:dummyResponses/onSearch.json")
    private Resource resource;
    
    Connection conn=null;    
    String strTosearchinDomain="";
    List<String> vendListKirana=null;
    List<String> vendListFood=null;
    
    private String queryFormationGrocery(final Schema request, final ConfigModel configModel){
    	SearchServiceSeller.log.info("Inside the method queryFormationGrocery of class SearchServiceSeller...");
    	
    	String strcity = "";    
    	boolean bolquery=false;
    	String search_criteria= "";
    	String strQueryGrocery="";
    	String strLat= "";
    	String strLong= "";
    	String strGPS= "";
    	
    	List<String> cityList = mcityRepository.findByCityCode(request.getContext().getCity());
    	
		for(int j=0; j < cityList.size(); j++) {   
			SearchServiceSeller.log.info("cityList-----" + cityList.get(j));        	
        	strcity=cityList.get(j);        	
        }
		//need to implement strcity in below query as 'tel' is hard coded
		//when Item is given
    	if (request.getMessage().getIntent().getItem()!= null && request.getMessage().getIntent().getItem().getDescriptor().getName() !=null && !request.getMessage().getIntent().getItem().getDescriptor().getName().equalsIgnoreCase("")) 
    	{
    		bolquery=true;
    		search_criteria= request.getMessage().getIntent().getItem().getDescriptor().getName();     		   			
    		strQueryGrocery = "select k.vendorid " 
        				+ "FROM  bornbhukkad.bb_admin_panel_vendors_kirana  dest JOIN  bb_admin_panel_vendors_item_details_kirana k"
        				+ " where dest.address like '%tel%' and k.itemname like '%" +search_criteria+ "%'";    		
    	} 
    	//when end location is given and item is given or not given
    	if (request.getMessage().getIntent().getFulfillment().getEnd()!= null && request.getMessage().getIntent().getFulfillment().getEnd().getLocation() !=null) {
    		if (bolquery == true) {    			
    			if (request.getMessage().getIntent().getFulfillment().getEnd().getLocation().getGps() != null && 
    					!request.getMessage().getIntent().getFulfillment().getEnd().getLocation().getGps().equalsIgnoreCase("")) {
    				strGPS = request.getMessage().getIntent().getFulfillment().getEnd().getLocation().getGps();
    				String[] latlong = strGPS.split(",");  				
    				strQueryGrocery ="";
    				
    				//when category is given
    				if(request.getMessage().getIntent().getCategory()!=null && request.getMessage().getIntent().getCategory().getId() !=null && !request.getMessage().getIntent().getCategory().getId().equalsIgnoreCase("")) {
    					strQueryGrocery = "select distinct k.vendorid , " 
    	        				+ " 6371 * 2 * ASIN(SQRT(POWER(SIN(("+ latlong [0] +" -abs(dest.latitude)) * pi()/180 / 2),2) + COS(" +latlong [0] +" * pi()/180 ) * COS(abs(dest.latitude) *  pi()/180) * "
    	        				+ " POWER(SIN((" + latlong [1]+ " - abs(dest.longitude)) *  pi()/180 / 2), 2)) ) as distance "
    	        				+ " FROM  bornbhukkad.bb_admin_panel_vendors_kirana as dest JOIN  bb_admin_panel_vendors_item_details_kirana k "
    	        				+ " where dest.address like '%tel%' and k.itemname like '%" +search_criteria+ "%' " +" and k.itemcategory like '%" +request.getMessage().getIntent().getCategory().getId() +"%'"
    	        				+ "and k.vendorid= dest.id having distance <  " + configModel.getKiranaserviceRadius(); 
    			    	
    			    }else {
	    				strQueryGrocery = "select distinct k.vendorid , " 
	        				+ " 6371 * 2 * ASIN(SQRT(POWER(SIN(("+ latlong [0] +" -abs(dest.latitude)) * pi()/180 / 2),2) + COS(" +latlong [0] +" * pi()/180 ) * COS(abs(dest.latitude) *  pi()/180) * "
	        				+ " POWER(SIN((" + latlong [1]+ " - abs(dest.longitude)) *  pi()/180 / 2), 2)) ) as distance "
	        				+ " FROM  bornbhukkad.bb_admin_panel_vendors_kirana as dest JOIN  bb_admin_panel_vendors_item_details_kirana k "
	        				+ " where dest.address like '%tel%' and k.itemname like '%" +search_criteria+ "%' and k.vendorid= dest.id having distance <  " + configModel.getKiranaserviceRadius();    				
    			    }
    				
    			} else if(request.getMessage().getIntent().getFulfillment().getEnd().getLocation().getAddress().getAreaCode()!= null && 
    					!request.getMessage().getIntent().getFulfillment().getEnd().getLocation().getAddress().getAreaCode().equalsIgnoreCase("")) {    				
    				strQueryGrocery = strQueryGrocery + " and k.vendorid= dest.id and dest.address like '%" +request.getMessage().getIntent().getFulfillment().getEnd().getLocation().getAddress().getAreaCode() + "%'" ;    				
    				
    				//when category is given
    				if(request.getMessage().getIntent().getCategory()!=null && request.getMessage().getIntent().getCategory().getId() !=null && !request.getMessage().getIntent().getCategory().getId().equalsIgnoreCase("")) {
    			    	strQueryGrocery =strQueryGrocery+" and k.itemcategory like '%" +request.getMessage().getIntent().getCategory().getId() + "%'";
    			    }
    			}
    		}else { // when end location is given and no item is given    			
    			if (request.getMessage().getIntent().getFulfillment().getEnd().getLocation().getGps() != null && 
    					!request.getMessage().getIntent().getFulfillment().getEnd().getLocation().getGps().equalsIgnoreCase("")) {
    				//when category is given
    				if(request.getMessage().getIntent().getCategory()!=null && request.getMessage().getIntent().getCategory() != null && request.getMessage().getIntent().getCategory().getId() !=null && !request.getMessage().getIntent().getCategory().getId().equalsIgnoreCase("")) {
    					
    					strGPS = request.getMessage().getIntent().getFulfillment().getEnd().getLocation().getGps();
	    				String[] latlong = strGPS.split(",");  				
	    				strQueryGrocery ="";
	    				strQueryGrocery = "select distinct k.vendorid , " 
	        				+ " 6371 * 2 * ASIN(SQRT(POWER(SIN(("+ latlong [0] +" -abs(dest.latitude)) * pi()/180 / 2),2) + COS(" +latlong [0] +" * pi()/180 ) * COS(abs(dest.latitude) *  pi()/180) * "
	        				+ " POWER(SIN((" + latlong [1]+ " - abs(dest.longitude)) *  pi()/180 / 2), 2)) ) as distance "
	        				+ " FROM  bornbhukkad.bb_admin_panel_vendors_kirana as dest JOIN  bb_admin_panel_vendors_item_details_kirana k "	        				
	        				+ " where dest.address like '%tel%' " +" and k.itemcategory like '%" +request.getMessage().getIntent().getCategory().getId() +"%'"
	        				+ "and k.vendorid= dest.id having distance <  " + configModel.getKiranaserviceRadius();    
    					
    			    	//strQueryGrocery =strQueryGrocery+" and k.itemcategory like '%" +request.getMessage().getIntent().getCategory().getId() +"%'";
    			    }else {
	    				strGPS = request.getMessage().getIntent().getFulfillment().getEnd().getLocation().getGps();
	    				String[] latlong = strGPS.split(",");  				
	    				strQueryGrocery ="";
	    				strQueryGrocery = "select distinct k.vendorid , " 
	        				+ " 6371 * 2 * ASIN(SQRT(POWER(SIN(("+ latlong [0] +" -abs(dest.latitude)) * pi()/180 / 2),2) + COS(" +latlong [0] +" * pi()/180 ) * COS(abs(dest.latitude) *  pi()/180) * "
	        				+ " POWER(SIN((" + latlong [1]+ " - abs(dest.longitude)) *  pi()/180 / 2), 2)) ) as distance "
	        				+ " FROM  bornbhukkad.bb_admin_panel_vendors_kirana as dest JOIN  bb_admin_panel_vendors_item_details_kirana k "
	        				+ " where dest.address like '%tel%' and k.vendorid= dest.id having distance <  " + configModel.getKiranaserviceRadius();    				
    			    }
    				
    			} else if(request.getMessage().getIntent().getFulfillment().getEnd().getLocation().getAddress().getAreaCode()!= null && 
    					!request.getMessage().getIntent().getFulfillment().getEnd().getLocation().getAddress().getAreaCode().equalsIgnoreCase("")) {
    				strQueryGrocery ="";
    				strQueryGrocery = "select k.vendorid " 
     				+ "FROM  bornbhukkad.bb_admin_panel_vendors_kirana  dest JOIN  bb_admin_panel_vendors_item_details_kirana k"
     				+ " where dest.address like '%tel%' and and dest.address like '%" +request.getMessage().getIntent().getFulfillment().getEnd().getLocation().getAddress().getAreaCode() + "%'" ;
    				//when category is given
    				if(request.getMessage().getIntent().getCategory()!=null && request.getMessage().getIntent().getCategory().getId() !=null && !request.getMessage().getIntent().getCategory().getId().equalsIgnoreCase("")) {
    			    	strQueryGrocery =strQueryGrocery+" and k.itemcategory like '%" +request.getMessage().getIntent().getCategory().getId() +"%'";
    			    }
    			}    			
    		}
    	}   
    	//need to implement when only city is given later on
    	if(bolquery == false) {
    		
    	}
    	System.out.println("strQueryGrocery --  " + strQueryGrocery);    	
    	return strQueryGrocery;
    }
    
    private String queryFormationFood(final Schema request, final ConfigModel configModel){
    	SearchServiceSeller.log.info("Inside the method queryFormationGrocery of class SearchServiceSeller...");
    	
    	String strcity = "";    
    	boolean bolquery=false;
    	String search_criteria= "";
    	String strQueryGrocery="";
    	String strLat= "";
    	String strLong= "";
    	String strGPS= "";
    	
    	List<String> cityList = mcityRepository.findByCityCode(request.getContext().getCity());
    	
		for(int j=0; j < cityList.size(); j++) {   
			SearchServiceSeller.log.info("cityList-----" + cityList.get(j));        	
        	strcity=cityList.get(j);        	
        }
		//need to implement strcity in below query as 'tel' is hard coded
		//when Item is given
    	if (request.getMessage().getIntent().getItem()!= null && request.getMessage().getIntent().getItem().getDescriptor().getName() !=null && !request.getMessage().getIntent().getItem().getDescriptor().getName().equalsIgnoreCase("")) 
    	{
    		bolquery=true;
    		search_criteria= request.getMessage().getIntent().getItem().getDescriptor().getName();     		   			
    		strQueryGrocery = "select k.vendorid " 
        				+ "FROM  bornbhukkad.bb_admin_panel_vendors  dest JOIN  bb_admin_panel_vendors_item_details k"
        				+ " where dest.address like '%tel%' and k.itemname like '%" +search_criteria+ "%'";    		
    	} 
    	//when end location is given and item is given or not given
    	if (request.getMessage().getIntent().getFulfillment().getEnd()!= null && request.getMessage().getIntent().getFulfillment().getEnd().getLocation() !=null) {
    		if (bolquery == true) {    			
    			if (request.getMessage().getIntent().getFulfillment().getEnd().getLocation().getGps() != null && 
    					!request.getMessage().getIntent().getFulfillment().getEnd().getLocation().getGps().equalsIgnoreCase("")) {
    				strGPS = request.getMessage().getIntent().getFulfillment().getEnd().getLocation().getGps();
    				String[] latlong = strGPS.split(",");  				
    				strQueryGrocery ="";
    				
    				//when category is given
    				if(request.getMessage().getIntent().getCategory()!=null && request.getMessage().getIntent().getCategory().getId() !=null && !request.getMessage().getIntent().getCategory().getId().equalsIgnoreCase("")) {
    					strQueryGrocery = "select distinct k.vendorid , " 
    	        				+ " 6371 * 2 * ASIN(SQRT(POWER(SIN(("+ latlong [0] +" -abs(dest.latitude)) * pi()/180 / 2),2) + COS(" +latlong [0] +" * pi()/180 ) * COS(abs(dest.latitude) *  pi()/180) * "
    	        				+ " POWER(SIN((" + latlong [1]+ " - abs(dest.longitude)) *  pi()/180 / 2), 2)) ) as distance "
    	        				+ " FROM  bornbhukkad.bb_admin_panel_vendors as dest JOIN  bb_admin_panel_vendors_item_details k "
    	        				+ " where dest.address like '%tel%' and k.itemname like '%" +search_criteria+ "%' " +" and k.itemcategory like '%" +request.getMessage().getIntent().getCategory().getId() +"%'"
    	        				+ "and k.vendorid= dest.id having distance <  " + configModel.getKiranaserviceRadius(); 
    			    	
    			    }else {
	    				strQueryGrocery = "select distinct k.vendorid , " 
	        				+ " 6371 * 2 * ASIN(SQRT(POWER(SIN(("+ latlong [0] +" -abs(dest.latitude)) * pi()/180 / 2),2) + COS(" +latlong [0] +" * pi()/180 ) * COS(abs(dest.latitude) *  pi()/180) * "
	        				+ " POWER(SIN((" + latlong [1]+ " - abs(dest.longitude)) *  pi()/180 / 2), 2)) ) as distance "
	        				+ " FROM  bornbhukkad.bb_admin_panel_vendors as dest JOIN  bb_admin_panel_vendors_item_details k "
	        				+ " where dest.address like '%tel%' and k.itemname like '%" +search_criteria+ "%' and k.vendorid= dest.id having distance <  " + configModel.getKiranaserviceRadius();    				
    			    }
    				
    			} else if(request.getMessage().getIntent().getFulfillment().getEnd().getLocation().getAddress().getAreaCode()!= null && 
    					!request.getMessage().getIntent().getFulfillment().getEnd().getLocation().getAddress().getAreaCode().equalsIgnoreCase("")) {    				
    				strQueryGrocery = strQueryGrocery + " and k.vendorid= dest.id and dest.address like '%" +request.getMessage().getIntent().getFulfillment().getEnd().getLocation().getAddress().getAreaCode() + "%'" ;    				
    				
    				//when category is given
    				if(request.getMessage().getIntent().getCategory()!=null && request.getMessage().getIntent().getCategory().getId() !=null && !request.getMessage().getIntent().getCategory().getId().equalsIgnoreCase("")) {
    			    	strQueryGrocery =strQueryGrocery+" and k.itemcategory like '%" +request.getMessage().getIntent().getCategory().getId() + "%'";
    			    }
    			}
    		}else { // when end location is given and no item is given    			
    			if (request.getMessage().getIntent().getFulfillment().getEnd().getLocation().getGps() != null && 
    					!request.getMessage().getIntent().getFulfillment().getEnd().getLocation().getGps().equalsIgnoreCase("")) {
    				//when category is given
    				if(request.getMessage().getIntent().getCategory()!=null && request.getMessage().getIntent().getCategory().getId() !=null && !request.getMessage().getIntent().getCategory().getId().equalsIgnoreCase("")) {
    					
    					strGPS = request.getMessage().getIntent().getFulfillment().getEnd().getLocation().getGps();
	    				String[] latlong = strGPS.split(",");  				
	    				strQueryGrocery ="";
	    				strQueryGrocery = "select distinct k.vendorid , " 
	        				+ " 6371 * 2 * ASIN(SQRT(POWER(SIN(("+ latlong [0] +" -abs(dest.latitude)) * pi()/180 / 2),2) + COS(" +latlong [0] +" * pi()/180 ) * COS(abs(dest.latitude) *  pi()/180) * "
	        				+ " POWER(SIN((" + latlong [1]+ " - abs(dest.longitude)) *  pi()/180 / 2), 2)) ) as distance "
	        				+ " FROM  bornbhukkad.bb_admin_panel_vendors as dest JOIN  bb_admin_panel_vendors_item_details k "	        				
	        				+ " where dest.address like '%tel%' " +" and k.itemcategory like '%" +request.getMessage().getIntent().getCategory().getId() +"%'"
	        				+ "and k.vendorid= dest.id having distance <  " + configModel.getKiranaserviceRadius();   
    					
    			    	//strQueryGrocery =strQueryGrocery+" and k.itemcategory like '%" +request.getMessage().getIntent().getCategory().getId() +"%'";
    			    }else {
	    				strGPS = request.getMessage().getIntent().getFulfillment().getEnd().getLocation().getGps();
	    				String[] latlong = strGPS.split(",");  				
	    				strQueryGrocery ="";
	    				strQueryGrocery = "select distinct k.vendorid , " 
	        				+ " 6371 * 2 * ASIN(SQRT(POWER(SIN(("+ latlong [0] +" -abs(dest.latitude)) * pi()/180 / 2),2) + COS(" +latlong [0] +" * pi()/180 ) * COS(abs(dest.latitude) *  pi()/180) * "
	        				+ " POWER(SIN((" + latlong [1]+ " - abs(dest.longitude)) *  pi()/180 / 2), 2)) ) as distance "
	        				+ " FROM  bornbhukkad.bb_admin_panel_vendors as dest JOIN  bb_admin_panel_vendors_item_details k "
	        				+ " where dest.address like '%tel%' and k.vendorid= dest.id having distance <  " + configModel.getKiranaserviceRadius();    				
    			    }
    				
    			} else if(request.getMessage().getIntent().getFulfillment().getEnd().getLocation().getAddress().getAreaCode()!= null && 
    					!request.getMessage().getIntent().getFulfillment().getEnd().getLocation().getAddress().getAreaCode().equalsIgnoreCase("")) {
    				strQueryGrocery ="";
    				strQueryGrocery = "select k.vendorid " 
     				+ "FROM  bornbhukkad.bb_admin_panel_vendors  dest JOIN  bb_admin_panel_vendors_item_details k"
     				+ " where dest.address like '%tel%' and and dest.address like '%" +request.getMessage().getIntent().getFulfillment().getEnd().getLocation().getAddress().getAreaCode() + "%'" ;
    				//when category is given
    				if(request.getMessage().getIntent().getCategory()!=null && request.getMessage().getIntent().getCategory().getId() !=null && !request.getMessage().getIntent().getCategory().getId().equalsIgnoreCase("")) {
    			    	strQueryGrocery =strQueryGrocery+" and k.itemcategory like '%" +request.getMessage().getIntent().getCategory().getId() +"%'";
    			    }
    			}    			
    		}
    	}   
    	//need to implement when only city is given later on
    	if(bolquery == false) {
    		
    	}
    	System.out.println("strQueryGrocery --  " + strQueryGrocery);    	
    	return strQueryGrocery;
    }
    
    public ResponseEntity<String> search(final HttpHeaders httpHeaders, final Schema request, final ConfigModel configModel) throws JsonProcessingException, SQLException {
        SearchServiceSeller.log.info(" Inside the search metod of SearchServiceSeller class");
        
        vendListKirana = new ArrayList<String>();
        vendListFood = new ArrayList<String>();
        
        final Response errorResponse = this.bodyValidator.validateRequestBody(request.getContext(), "search");
        if (errorResponse != null) {
            return (ResponseEntity<String>)new ResponseEntity((Object)this.mapper.writeValueAsString((Object)errorResponse), HttpStatus.BAD_REQUEST);
        }
        
        //executing for Grocery
        String querySTR= queryFormationGrocery(request,configModel);        
        
        ResultSet rs;
        
        if(conn == null) {
          conn= ConnectionUtil.getConnection();
        }
        Statement stm= conn.createStatement();
        rs = stm.executeQuery(querySTR);
        
        while(rs.next()) { 
        	String search_vendorid =rs.getString("vendorid");
        	List<ItemDetailsKirana> itemDetailsKirana=null;
        	if(request.getMessage().getIntent().getCategory()!= null && request.getMessage().getIntent().getCategory().getId() !=null && request.getMessage().getIntent().getItem() != null && !request.getMessage().getIntent().getCategory().getId().equalsIgnoreCase("") && !request.getMessage().getIntent().getItem().getDescriptor().getName().equalsIgnoreCase("")) {
        		itemDetailsKirana= mItemRepositoryKirana.findByVendoridanditemcategoryandsearch(request.getMessage().getIntent().getCategory().getId(),request.getMessage().getIntent().getItem().getDescriptor().getName(), search_vendorid);
        	   
        	}else if(request.getMessage().getIntent().getCategory()!= null && request.getMessage().getIntent().getCategory().getId() !=null &&  !request.getMessage().getIntent().getCategory().getId().equalsIgnoreCase("")) {
        		
        		itemDetailsKirana= mItemRepositoryKirana.findByVendoridanditemcategory(request.getMessage().getIntent().getCategory().getId(), search_vendorid);
        		
        	}else if(request.getMessage().getIntent().getItem()!=null && !request.getMessage().getIntent().getItem().getDescriptor().getName().equalsIgnoreCase("")) {
        		itemDetailsKirana= mItemRepositoryKirana.findByVendoridandsearch(request.getMessage().getIntent().getItem().getDescriptor().getName(), search_vendorid);
        	}else {
        		itemDetailsKirana= mItemRepositoryKirana.findByVendorid(search_vendorid);
        	}
        	
        	if(itemDetailsKirana!= null && itemDetailsKirana.size() != 0) {
        		vendListKirana.add(search_vendorid);
        	}
        }
        if(vendListKirana != null && vendListKirana.size() !=0) {
        	strTosearchinDomain="Grocery";
        }
        rs.close();                    
        stm.close(); 
        
        //checking the results of grocery, if empty the look for food. this will be taken later once domain is created at ONDC
         //vendListKirana.clear();
        if(vendListKirana != null && vendListKirana.size() ==0) {
       	 
        	String querySTRFood= queryFormationFood(request, configModel);
        	vendListFood = new ArrayList<String>();
            ResultSet rs1;
            conn= ConnectionUtil.getConnection();
            Statement stm1= conn.createStatement();
            rs1 = stm1.executeQuery(querySTRFood);
            
            while(rs1.next()) {
            	
            	String search_vendorid =rs1.getString("vendorid");
            	List<ItemObj> itemDetailsfood=null;
            	if(request.getMessage().getIntent().getCategory()!= null && request.getMessage().getIntent().getCategory().getId() !=null && request.getMessage().getIntent().getItem() != null 
            			&& !request.getMessage().getIntent().getCategory().getId().equalsIgnoreCase("") && !request.getMessage().getIntent().getItem().getDescriptor().getName().equalsIgnoreCase("")) {
             	      itemDetailsfood= mItemRepository.findByVendoridanditemcategoryandsearch(request.getMessage().getIntent().getCategory().getId(),request.getMessage().getIntent().getItem().getDescriptor().getName(), search_vendorid);
             	   
            	}else if(request.getMessage().getIntent().getCategory()!= null 
            			&& request.getMessage().getIntent().getCategory().getId() !=null &&  !request.getMessage().getIntent().getCategory().getId().equalsIgnoreCase("")) {
             		
             		  itemDetailsfood= mItemRepository.findByVendoridanditemcategory(request.getMessage().getIntent().getCategory().getId(), search_vendorid);
             		
            	}else if(request.getMessage().getIntent().getItem()!=null && !request.getMessage().getIntent().getItem().getDescriptor().getName().equalsIgnoreCase("")) {
             		  itemDetailsfood= mItemRepository.findByVendoridandsearch(request.getMessage().getIntent().getItem().getDescriptor().getName(), search_vendorid);
             	}else {
             		itemDetailsfood= mItemRepository.findByVendorid(search_vendorid);
            	}
            	
            	if(itemDetailsfood != null && itemDetailsfood.size() != 0) {
            		
            		vendListFood.add(search_vendorid);
            	}            	
             }
	            rs1.close();                   
	            stm1.close();
	            if(vendListFood!= null && vendListFood.size() !=0) {
	            	strTosearchinDomain="Food";
	            }
            
        }
                
        if(vendListKirana.size() == 0 && vendListFood.size() == 0) {
       	 final Response adaptorResponse = new Response();
            final ResponseMessage resMsg = new ResponseMessage();
            resMsg.setAck(new Ack(AckStatus.NACK));
            adaptorResponse.setMessage(resMsg);
            final Error error = new Error();
            error.setCode(ErrorCode.PROVIDER_NOTFOUND_ERROR.toString());
            error.setMessage("Provider not found");
            error.setCode("30001");
            adaptorResponse.setContext(request.getContext());
            adaptorResponse.setError(error);
            
       	   return (ResponseEntity<String>)new ResponseEntity((Object)this.mapper.writeValueAsString((Object)adaptorResponse), HttpStatus.BAD_REQUEST);
       }
        
        
        final Response adaptorResponse = new Response();
        final ResponseMessage resMsg = new ResponseMessage();
        resMsg.setAck(new Ack(AckStatus.ACK));
        adaptorResponse.setMessage(resMsg);
        final Context ctx = request.getContext();
        adaptorResponse.setContext(ctx);
        CompletableFuture.runAsync(() -> {
            this.sendRequestToSellerInternalApi(httpHeaders, request, configModel);
         });
        return (ResponseEntity<String>)new ResponseEntity((Object)this.mapper.writeValueAsString((Object)adaptorResponse), HttpStatus.OK);
    }
    
    private void sendRequestToSellerInternalApi(final HttpHeaders httpHeaders, final Schema request,final ConfigModel configModel) {
        SearchServiceSeller.log.info("sending request to seller internal api [in seperate thread]");
        try {
        	        	
        	String host = httpHeaders.get("remoteHost").get(0);
           
            if("0:0:0:0:0:0:0:1".equals(host)) {
            	host="https://localhost:443";
            }else {
            	host = configModel.getContexturl();
            }
            String search_criteria="";
            if(request.getMessage().getIntent().getItem()!=null ) {
            	 search_criteria= request.getMessage().getIntent().getItem().getDescriptor().getName();
            }else {
            	 search_criteria="";
            }
            String strCategoryId="";
            if(request.getMessage().getIntent().getCategory()!=null && request.getMessage().getIntent().getCategory().getId() !=null && !request.getMessage().getIntent().getCategory().getId().equalsIgnoreCase("")) {
            	strCategoryId= request.getMessage().getIntent().getCategory().getId();
            }else {
            	strCategoryId="";
            }
        	//String ttl= configModel.getBecknTtl();
            String subscriberName = configModel.getSubscriberName();
            String subscriber_S_description = configModel.getSubscriber_Shortdescription();
            String subscriber_L_description = configModel.getSubscriber_Longdescription();
            OnSchema respBody=null;
            
            if(strTosearchinDomain.equalsIgnoreCase("Grocery")) {
            	respBody = retailResponseSearchkirana.fetchResult(strCategoryId,search_criteria , request ,configModel,host,subscriberName,subscriber_S_description, subscriber_L_description,  vendListKirana);
            }else if (strTosearchinDomain.equalsIgnoreCase("Food")){
            	respBody = retailResponseSearchfood.fetchResult(strCategoryId,search_criteria , request ,configModel,host,subscriberName,subscriber_S_description, subscriber_L_description ,  vendListFood);
            }	
            
            setBapFinderFee( request);            	            	
            String respJson = this.jsonUtil.toJson((Object)respBody); 
            onSearchControllerSeller.onSearch(respJson, httpHeaders);
            SearchServiceSeller.log.info(respJson);
            
        }
        catch (Exception e) {
            SearchServiceSeller.log.error("error while sending post request to seller internal api" + e);
            e.printStackTrace();
        }
    }
    private void setBapFinderFee(final Schema request) {    	
    	try {
    		String querySTRUpdate="";
    		Timestamp timestamp = new Timestamp(System.currentTimeMillis());   
    		BapFeeObj myapiBapFeeObj = new BapFeeObj(request.getMessage().getIntent().getPayment().getBuyerAppFinderFeeType(), request.getMessage().getIntent().getPayment().getBuyerAppFinderFeeAmount(),timestamp.toString(),"", request.getContext().getBapId() );
    		
    		if (!mBapFeeRepository.existsBySubscriberid(request.getContext().getBapId())) {
    			mBapFeeRepository.save(myapiBapFeeObj);
    		}else {
    			System.out.println("else ----------");
    			//ResultSet rsUpdate;
    			int resultset=0;
    	        Connection connUpdate= ConnectionUtil.getConnection();
    	        Statement stmUpdate= connUpdate.createStatement();
    	        querySTRUpdate= "update bornbhukkad.ondc_bap_finder_fee baf set baf.finder_fee_amount ='" +request.getMessage().getIntent().getPayment().getBuyerAppFinderFeeAmount()+
    	        		         "', baf.type ='"+request.getMessage().getIntent().getPayment().getBuyerAppFinderFeeType()+"' , baf.updated_on='"+timestamp.toString()+
    	        		        "' where baf.subscriberid ='"+ request.getContext().getBapId()  +"';";
    	        resultset = stmUpdate.executeUpdate(querySTRUpdate);
    	        stmUpdate.close();
    		}
    		System.out.println("data saved--BapFeeObj  ");
    		
		}catch(Exception e) {
			System.out.println("Exception--"+ e);
		}
		
	}
    static {
        log = LoggerFactory.getLogger((Class)SearchServiceSeller.class);
    }
}
