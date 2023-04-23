// 
// Decompiled by Procyon v0.5.36
// 

package com.bb.beckn.search.utility;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.apache.http.HttpResponse;
import com.bb.beckn.api.model.common.Address;
import com.bb.beckn.api.model.common.Catalog;
import com.bb.beckn.api.model.common.Circle;
import com.bb.beckn.api.model.common.Contact;
import com.bb.beckn.api.model.common.Descriptor;
import com.bb.beckn.api.model.common.Fulfillment;
import com.bb.beckn.api.model.common.Item;
import com.bb.beckn.api.model.common.ItemQuantities;
import com.bb.beckn.api.model.common.ItemQuantity;
import com.bb.beckn.api.model.common.ItemVegFruit;
import com.bb.beckn.api.model.common.Location;
import com.bb.beckn.api.model.common.Price;
import com.bb.beckn.api.model.onsearch.OnSearchMessage;
import com.bb.beckn.common.model.ConfigModel;
import com.bb.beckn.repository.ItemRepositoryKirana;
import com.bb.beckn.repository.KiranaRepository;
import org.springframework.core.io.Resource;
import com.bb.beckn.search.extension.OnSchema;
import com.bb.beckn.search.extension.Schema;
import com.bb.beckn.search.model.ItemDetailsKirana;
import com.bb.beckn.search.model.KiranaObj;
import com.bb.beckn.search.service.SearchServiceSeller;
import com.bb.beckn.api.model.common.Provider;
import com.bb.beckn.api.model.common.Scalar;
import com.bb.beckn.api.model.common.Schedule;
import com.bb.beckn.api.model.common.Tags;
import com.bb.beckn.api.model.common.Time;
import com.bb.beckn.api.model.common.TimeRange;
import com.bb.beckn.api.model.common.list;

@Service
public class RetailResponseSearchkirana
{
	 private static final Logger log;	
	 @Autowired
	 ItemRepositoryKirana mItemRepositoryKirana;	
     @Autowired
	 KiranaRepository mKiranaRepository;
    
     Fulfillment fulfillment = null;     
     List<Fulfillment> bppFulfillmentslist = null;
     List<Provider> Provider_list =  null;
     Provider provider = null;
 	 Location location_Provider = null;
 	 Address address_Provider = null;
 	 List<Location> location_list = null;     	
 	 List<KiranaObj> kiranaObjList =null; 	
 	 Fulfillment merchantFulfillment =null;
	 Contact merchantContact = null;
	 List<Fulfillment> merchantFulfillmentslist = null;			
 	 Optional<KiranaObj> kiranaObjopt= null;    	
 	 Time time = null;
	 TimeRange providerTimeRange = null;
	 Descriptor descriptor = null;
	 List<Item> item_list = null;        
     Item item = null;
     Descriptor descriptor_item = null;
     List<String> item_images = null;
     Price price =null;
     Circle providerCircleLocation = null;
     Scalar providercircleradius =null;
     List<ItemDetailsKirana> itemDetailsKirana= null;
     ItemQuantity itemQuantity = null;
     ItemQuantities itemQuantities = null;
     List<String> images =null;
     List<list> taglists = null;
     List<Tags> providertagsList = null;
     Tags providertag = null;
     list taglist= null;
     ItemVegFruit itemVegFruit = null;
     
     public void servicibilty(String strCategory, List<KiranaObj> kiranaObjList, final ConfigModel configModel) {		
    	
 		 taglists = new ArrayList<>();
 		 taglist = new list();
 		 providertagsList = new ArrayList<>();
		 providertag = new Tags();
		 providertag.setAdditionalProp1("serviceability");
		 
	     taglist.setAdditionalProp1("location");
	     taglist.setAdditionalProp2(location_Provider.getId());
	     taglists.add(taglist);
	     	         
			/*
			 * for(int ii=0; ii < itemDetailsKirana.size(); ii++) { taglist = new list();
			 * taglist.setAdditionalProp1("category");
			 * taglist.setAdditionalProp2(itemDetailsKirana.get(ii).getItemcategory());
			 * taglists.add(taglist); }
			 */
	     taglist = new list();
         taglist.setAdditionalProp1("category");
         taglist.setAdditionalProp2(strCategory);
         taglists.add(taglist);	
         
	     taglist = new list();
	     taglist.setAdditionalProp1("type");
	     String storeType="";
	        
         if(kiranaObjList.get(0).getStoresubtypehyperlocal() != null && kiranaObjList.get(0).getStoresubtypehyperlocal().equalsIgnoreCase("yes")) {
        	storeType = "hyperlocal";
         }
         if(kiranaObjList.get(0).getStoresubtypeintercity()!= null && kiranaObjList.get(0).getStoresubtypeintercity().equalsIgnoreCase("yes")) {
            if(!storeType.equalsIgnoreCase(""))
            {
            	storeType = storeType + " , intercity";
            }else {
        	  storeType = "intercity";
            }
         }
         if(kiranaObjList.get(0).getStoresubtypepanindia() != null && kiranaObjList.get(0).getStoresubtypepanindia().equalsIgnoreCase("yes")) {
        	if(!storeType.equalsIgnoreCase(""))
            {
            	storeType = storeType + " , pan-India";
            }else {
        	  storeType = "pan-India";
            }	        	
         }
	     taglist.setAdditionalProp2(storeType);
	     taglists.add(taglist);
	     taglist = new list();
	     taglist.setAdditionalProp1("val");
	     taglist.setAdditionalProp2(configModel.getKiranaserviceRadius());
	     taglists.add(taglist);
	     taglist = new list();
	     taglist.setAdditionalProp1("unit");
	     taglist.setAdditionalProp2("Km");
	     taglists.add(taglist);
	     providertag.setList(taglists);
	     
	     providertagsList.add(providertag);
	}
    public OnSchema fetchResult(String strCategoryId , String search_criteria , final Schema request, final ConfigModel configModel, String host, String subscriberName,String subscriber_S_description, String subscriber_L_description,List<String> vendList) throws SQLException, IOException, NumberFormatException, JSONException {
    	RetailResponseSearchkirana.log.info("Inside the RetailResponseSearchkirana class of fetchResult method...");
    	
    	OnSchema respBody = new OnSchema();    	
    	respBody.setContext(request.getContext());
    	respBody.getContext().setAction("on_search");
    	//respBody.getContext().setTtl(configModel.getBecknTtl());
    	respBody.getContext().setBppId(configModel.getSubscriberId());
        respBody.getContext().setBppUri(configModel.getSubscriberUrl());
        respBody.getContext().setCoreVersion(configModel.getVersion());
        respBody.getContext().setTimestamp(Instant.now().toString());        
        if(respBody.getMessage()==null) {
        	respBody.setMessage(new OnSearchMessage());
        }        
        if(respBody.getMessage()==null) {
        	respBody.setMessage(new OnSearchMessage());
        }
        if(respBody.getMessage().getCatalog()==null) {
        	respBody.getMessage().setCatalog(new Catalog());
        }
        if(respBody.getMessage().getCatalog().getBppDescriptor()==null) {
        	respBody.getMessage().getCatalog().setBppDescriptor(new Descriptor());
        }
        respBody.getMessage().getCatalog().getBppDescriptor().setName(subscriberName);   
        respBody.getMessage().getCatalog().getBppDescriptor().setShortDesc(subscriber_S_description);
        respBody.getMessage().getCatalog().getBppDescriptor().setLongDesc(subscriber_L_description);  
        
        String imagePath =host+"/image/check/bornbhkkad.jpeg";
        respBody.getMessage().getCatalog().getBppDescriptor().setSymbol(imagePath);
       
        images =new ArrayList<String>();
        images.add(imagePath);
        respBody.getMessage().getCatalog().getBppDescriptor().setImages(images);
        
        Fulfillment fulfillment = null;        
        bppFulfillmentslist = new ArrayList<Fulfillment>();
        
        if (respBody.getMessage().getCatalog().getBppFulfillments() == null) {
           fulfillment = new Fulfillment();
        }
        for (int j=0; j<=2; j++) {
        	fulfillment = new Fulfillment();
        	if(j == 0) {
        		fulfillment.setId("1");
                fulfillment.setType("Delivery");
                bppFulfillmentslist.add(fulfillment);
        	}
        	if(j == 1) {
        		fulfillment.setId("2");
                fulfillment.setType("Self-Pickup");
                bppFulfillmentslist.add(fulfillment);
        	}
        	if(j == 2) {
        		fulfillment.setId("3");
                fulfillment.setType("Delivery and Self-Pickup");
                bppFulfillmentslist.add(fulfillment);
        	}
        }        
        respBody.getMessage().getCatalog().setBppFulfillments(bppFulfillmentslist);        
        Provider_list = new ArrayList<Provider>();        
        if(respBody.getMessage().getCatalog().getBppProviders()==null) {        	
        	respBody.getMessage().getCatalog().setBppProviders(new ArrayList<Provider>());
        }        
             
        for(int j=0; j < vendList.size(); j++) {        	
        	System.out.println("vendorid-----" + vendList.get(j));
        	respBody=creatingRespStructre(strCategoryId, vendList.get(j) ,respBody,  request,  configModel, Provider_list, search_criteria, host);
        }				
    	return respBody;
    }
    
    private OnSchema creatingRespStructre(String strCategoryId, String search_vendorid, OnSchema respBody ,final Schema request , final ConfigModel configModel,List<Provider> Provider_list, 
    		String search_criteria, String host) throws SQLException, IOException, NumberFormatException, JSONException {
    	RetailResponseSearchkirana.log.info("Inside the RetailResponseSearchkirana class of creatingRespStructre method...");
    	provider = new Provider();
    	location_Provider = new Location();
    	address_Provider = new Address();
    	location_list = new ArrayList<Location>();     	
    	kiranaObjList =new ArrayList<KiranaObj>();  
		merchantContact = new Contact();
		merchantFulfillmentslist = new ArrayList<Fulfillment>();
		//fetching vendor detail
    	kiranaObjopt= mKiranaRepository.findBydata(Long.parseLong(search_vendorid));    	
    	kiranaObjList = kiranaObjopt.map(Collections::singletonList).orElse(Collections.emptyList());    	
    	//fetching item detail
    	    	
    	if(strCategoryId!= null && search_criteria != null && !strCategoryId.equalsIgnoreCase("") && !search_criteria.equalsIgnoreCase("")) {
    		itemDetailsKirana= mItemRepositoryKirana.findByVendoridanditemcategoryandsearch(strCategoryId,search_criteria, search_vendorid);
    	   
    	}else if(strCategoryId!= null && !strCategoryId.equalsIgnoreCase("")) {
    		
    		itemDetailsKirana= mItemRepositoryKirana.findByVendoridanditemcategory(strCategoryId, search_vendorid);
    		
    	}else if(search_criteria!=null && !search_criteria.equalsIgnoreCase("")) {
    		itemDetailsKirana= mItemRepositoryKirana.findByVendoridandsearch(search_criteria, search_vendorid);
    	}else {
    		itemDetailsKirana= mItemRepositoryKirana.findByVendorid(search_vendorid);
    	}
    	
        String opted="";
        String is_open="";
        for (int i=0; i< kiranaObjList.size(); i++) {
        	 merchantFulfillment =new Fulfillment();
        	 opted=kiranaObjList.get(i).getOpted();	
        	 is_open=kiranaObjList.get(i).getIsOpen();
        	 provider.setId((kiranaObjList.get(i).getId()).toString());
        	 time = new Time();
        	 providerTimeRange = new TimeRange();
        	 if(is_open.equalsIgnoreCase("open")) {
        		 time.setLabel("enable");
        	 }else {
        		 time.setLabel("disable");
        	 }
        	         	 
        	 time.setTimestamp(Instant.now().toString());
        	 provider.setTime(time);
        	 
        	 descriptor = new Descriptor();
        	 descriptor.setName(kiranaObjList.get(i).getStorename());
        	 descriptor.setShortDesc(kiranaObjList.get(i).getShort_desc());
        	 descriptor.setLongDesc(kiranaObjList.get(i).getLong_desc());  
        	 String providerdecriptorimage = kiranaObjList.get(i).getSymbol_name();        	 
        	 providerdecriptorimage =host+"/image/view/"+kiranaObjList.get(i).getId()+"/"+providerdecriptorimage +"/"+"symbol_name_k";
        	
        	 descriptor.setSymbol(providerdecriptorimage);
        	 images =new ArrayList<String>();
             images.add(providerdecriptorimage);
             
        	 descriptor.setImages(images);  
    	     provider.setDescriptor(descriptor);
    	   	 
    	    getStringFromLocation(Double.parseDouble(kiranaObjList.get(i).getLatitude()), Double.parseDouble(kiranaObjList.get(i).getLongitude()), address_Provider);    	    	        
    	    //address_Provider.setCity("Telangana");
    	    location_Provider.setAddress(address_Provider);
	        location_Provider.setId((kiranaObjList.get(i).getId()).toString());	        
	        String latitude=kiranaObjList.get(i).getLatitude();
	        String longitude=kiranaObjList.get(i).getLongitude();
	        String gps=latitude +","+ longitude;
	        location_Provider.setGps(gps);
	        providerCircleLocation = new Circle();
	        providerCircleLocation.setGps(gps);
	        providercircleradius = new Scalar();
	        providercircleradius.setUnit("KM");
	        providercircleradius.setValue(new Integer(configModel.getServicableradiusProvider()));
	        providerCircleLocation.setRadius(providercircleradius);
	        location_Provider.setCircle(providerCircleLocation);
	        
	        time = new Time();
	        
	        time.setDays(kiranaObjList.get(i).getStoreworkingdays());
	        List<String> holidays =new ArrayList<String>();
	        holidays.add("2022-08-15");
	        holidays.add("2022-08-19");
	        time.setSchedule(new Schedule());	        
	        time.getSchedule().setHolidays(holidays);
	        //time.getSchedule().setFrequency("PT4H");
	        //List<String> times = new ArrayList<String>();
	        //times.add("1100");
	        //times.add("1900");
	        //time.getSchedule().setTimes(times);
	        providerTimeRange.setStart(kiranaObjList.get(i).getStoreopentimehours());
	        providerTimeRange.setEnd(kiranaObjList.get(i).getStoreclosetimehours());
	        time.setRange(providerTimeRange);
	        location_Provider.setTime(time);	        
	        location_list.add(location_Provider);
	        provider.setLocations(location_list);
	        String ttl= ""+Duration.ofMinutes(Long.parseLong(configModel.getBecknTtl()));
	        provider.setTtl(ttl);
	        //provider.setTtl(configModel.getBecknTtl());
	                
	        merchantContact.setEmail(kiranaObjList.get(i).getEmail());
	        merchantContact.setPhone(kiranaObjList.get(i).getPhone());
	        merchantFulfillment.setContact(merchantContact);
	        merchantFulfillmentslist.add(merchantFulfillment);
	        
		}
        provider.setFulfillments(merchantFulfillmentslist);
        //list of items for particlar provider       
        item_list = new ArrayList<Item>();        
        item = new Item();
        descriptor_item = new Descriptor();
        item_images = new ArrayList<String>();
        price =new Price();               
        		
		for (int i=0; i< itemDetailsKirana.size(); i++) {	
			   
			   itemVegFruit = new ItemVegFruit();
			   descriptor_item = new Descriptor();
			   item = new Item();
			   price =new Price();
			   item_images = new ArrayList<String>();
			   descriptor_item.setName(itemDetailsKirana.get(i).getItemname());				  	   
			
	           String itemdecriptorimage = itemDetailsKirana.get(i).getItem_image_1_name();	          
	           itemdecriptorimage =host+"/image/view/"+itemDetailsKirana.get(i).getId()+"/"+itemdecriptorimage +"/"+"Item_image_1_kdetail";	                	  
		       descriptor_item.setSymbol(itemdecriptorimage);
		       descriptor_item.setShortDesc(itemDetailsKirana.get(i).getShort_desc());
		       descriptor_item.setLongDesc(itemDetailsKirana.get(i).getLong_desc());		       
		       item_images.add(itemdecriptorimage);
		     
	           String itemdecriptorimage2 = itemDetailsKirana.get(i).getItem_image_2_name();	        	 
	           itemdecriptorimage2 =host+"/image/view/"+itemDetailsKirana.get(i).getId()+"/"+itemdecriptorimage2 +"/"+"Item_image_2_kdetail";
	        	 
		       item_images.add(itemdecriptorimage2);		     
	           String itemdecriptorimage3 = itemDetailsKirana.get(i).getItem_image_3_name();	        	
	           itemdecriptorimage3 =host+"/image/view/"+itemDetailsKirana.get(i).getId()+"/"+itemdecriptorimage3 +"/"+"Item_image_3_kdetail";
	        	
		       item_images.add(itemdecriptorimage3);		    
	           String itemdecriptorimage4 = itemDetailsKirana.get(i).getItem_image_4_name();	        	 
	           itemdecriptorimage4 =host+"/image/view/"+itemDetailsKirana.get(i).getId()+"/"+itemdecriptorimage4 +"/"+"Item_image_4_kdetail";
	        	 
		       item_images.add(itemdecriptorimage4);		       
		       descriptor_item.setImages(item_images);
		       item.setId((itemDetailsKirana.get(i).getId()).toString());
		       item.setDescriptor(descriptor_item);		       
		       item.setLocationId(itemDetailsKirana.get(i).getVendorid());
		       
		       itemQuantity = new ItemQuantity();
		       itemQuantities = new ItemQuantities();
		       itemQuantities.setCount(25);		       
		       itemQuantity.setAvailable(itemQuantities);
		       itemQuantities = new ItemQuantities();
		       itemQuantities.setCount(50);
		       itemQuantity.setMaximum(itemQuantities);
		       item.setQuantity(itemQuantity);
		       
		       price.setCurrency("INR");
		       price.setValue(itemDetailsKirana.get(i).getItemprice());
		       price.setMaximumValue(itemDetailsKirana.get(i).getItemprice());
		       item.setPrice(price);		       
		       item.setCategoryId(itemDetailsKirana.get(i).getItemcategory());
		       //item.setCategoryId("Salt & Sugar");
		       if(opted.equalsIgnoreCase("true")) {
		    	   item.setFulfillmentId("1");
		       }else {
		    	   item.setFulfillmentId("2");
		       }
		       item.setReturnable(Boolean.valueOf(configModel.getReturnable()));
		       item.setCancellable(Boolean.valueOf(configModel.getCancellable()));
		       item.setAvailableOnCod(true);
		       item.setReturnWindow(configModel.getKiranareturnWindow());
		       item.setSellerPickupReturn(false);
		       item.setRecommended(true);
		       if(kiranaObjList.get(0).getTimetoshipinminute()!=null) {
		    	   Long ll=Long.parseLong(kiranaObjList.get(0).getTimetoshipinminute());
			       String timeToShip =""+Duration.ofMinutes(ll);
			       item.setTimeToShip(timeToShip);
		       }else {
		    	   item.setTimeToShip("PT45M");
		       }
		       item.setAvailableOnCod(true);
		       String customer_care="Lava Foods Private Limited, Hyderabad,+91-6304928766,  mailto:support@bornbhukkad.com";
		       item.setContactDetailsConsumerCare(customer_care);
		       String strWeight= itemDetailsKirana.get(i).getNewQuantity()+ " "+ itemDetailsKirana.get(i).getUnit();
		       itemVegFruit.setAdditionalProp1(strWeight);
		       item.setMandatoryReqsVeggiesFruits(itemVegFruit);
		       //item.setMandatoryReqsVeggiesFruits(strWeight);
		       item_list.add(item);  
		}
		HashSet<String> uniqueCategory =new HashSet<String>();
		
		for(int k=0; k < itemDetailsKirana.size(); k++) {
			uniqueCategory.add(itemDetailsKirana.get(k).getItemcategory());
		}
		Iterator<String> itr=uniqueCategory.iterator();  
		  while(itr.hasNext()){  
			  servicibilty(itr.next(), kiranaObjList,configModel);
		  }  
		
		//servicibilty(itemDetailsKirana, kiranaObjList,configModel);
		provider.setTags(providertagsList);
		
		provider.setItems(item_list);	
		System.out.println("provider.getItems().size()--  "+ provider.getItems().size());
		Provider_list.add(provider);		
		respBody.getMessage().getCatalog().setBppProviders(Provider_list);
		System.out.println("respBody--  "+ respBody);
    	return respBody;
    	
    }
    
    public List<Address> getStringFromLocation(double lat, double lng, Address address_Provider)
            throws ClientProtocolException, IOException, JSONException {
    	RetailResponseSearchkirana.log.info("Inside the RetailResponseSearchkirana class of getStringFromLocation method...");
        String address = String.format(Locale.ENGLISH,"https://maps.googleapis.com/maps/api/geocode/json?key=AIzaSyDdDXmDfdTgN0N1nTF4T1Qq6y2ZG4hZTj0&latlng=%1$f,%2$f&sensor=true&language="
                                + Locale.getDefault().getCountry(), lat, lng);
        HttpGet httpGet = new HttpGet(address);
        //HttpClient client = new DefaultHttpClient();
        CloseableHttpClient client= HttpClientBuilder.create().build();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        List<Address> retList = null;
        response = client.execute(httpGet);
        HttpEntity entity = response.getEntity();
        InputStream stream = entity.getContent();
        int b;
        while ((b = stream.read()) != -1) {
            stringBuilder.append((char) b);
        }
        client.close();
        JSONObject jsonObject = new JSONObject(stringBuilder.toString());
        retList = new ArrayList<Address>();

        if ("OK".equalsIgnoreCase(jsonObject.getString("status"))) {
            JSONArray results = jsonObject.getJSONArray("results");
            
            for (int i = 0; i < results.length(); i++) {
                JSONObject result = results.getJSONObject(i);                
                JSONArray types = result.getJSONArray("address_components");
                String Street="";
                int k=0;
                for (int ii = 0; ii < types.length(); ii++) {
                	JSONObject type = types.getJSONObject(ii);
                	String Stype= type.getString("types");
                	Stype = Stype.substring(1, Stype.length() - 1);                	
                	String[] stringsType = Stype.split(",");
                	
                	for (String str : stringsType) {
                		str = str.substring(1, str.length() - 1);
                		if(str.equals("postal_code"))
                        {                            
                            address_Provider.setAreaCode(type.getString("long_name"));
                        }
                		if(str.equals("administrative_area_level_1"))
                        {                           
                            address_Provider.setState(type.getString("short_name"));
                        }
                		if(str.equals("administrative_area_level_2"))
                        {                            
                            address_Provider.setCity(type.getString("long_name"));
                        }
                		if(str.equals("locality"))
                        {
                			if (k!=0) {
                				Street=Street+" , ";
                			}
                			Street=Street+type.getString("long_name");
                        }
                		if(str.equals("neighborhood"))
                        {
                			if (k!=0) {
                				Street=Street+" , ";
                			}
                			Street=Street+type.getString("long_name");
                        }
                		if(str.equals("premise"))
                        {
                			if (k!=0) {
                				Street=Street+" , ";
                			}
                			Street=Street+type.getString("long_name");
                        }
                		k++;
                	}                	
                	address_Provider.setStreet(Street);
                }                
                break;       
            }
        }
        return retList;
    }
    static {
        log = LoggerFactory.getLogger((Class)RetailResponseSearchkirana.class);
    }
}
