// 
// Decompiled by Procyon v0.5.36
// 

package com.bb.beckn.search.utility;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
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
import com.bb.beckn.repository.ItemRepository;
import com.bb.beckn.repository.ItemRepositoryKirana;
import com.bb.beckn.repository.KiranaRepository;
import com.bb.beckn.repository.VendorRepository;

import org.springframework.core.io.Resource;
import com.bb.beckn.search.extension.OnSchema;
import com.bb.beckn.search.extension.Schema;
import com.bb.beckn.search.model.ItemDetailsKirana;
import com.bb.beckn.search.model.ItemObj;
import com.bb.beckn.search.model.KiranaObj;
import com.bb.beckn.search.model.VendorObj;
import com.bb.beckn.search.service.SearchServiceSeller;
import com.bb.beckn.api.model.common.Provider;
import com.bb.beckn.api.model.common.Scalar;
import com.bb.beckn.api.model.common.Schedule;
import com.bb.beckn.api.model.common.Tags;
import com.bb.beckn.api.model.common.Time;
import com.bb.beckn.api.model.common.TimeRange;
import com.bb.beckn.api.model.common.list;

@Service
public class RetailResponseSearchfood
{
	private static final Logger log;	
	 
	 @Autowired
	 private ItemRepository mItemRepository;	 
	 @Autowired
	 private VendorRepository mVendorRepository; 
	 
    
	 Fulfillment fulfillment = null;
	 List<Fulfillment> bppFulfillmentslist = null;
	 List<Provider> Provider_list = null;
	 List<Address> retList = null;
	 JSONObject jsonObject =null;
	 Provider provider=null;
	 Location location_Provider=null;
	 Address address_Provider =null;
	 List<Location> location_list =null;
	 Fulfillment merchantFulfillment =null;
	 Contact merchantContact = null;
	 List<Fulfillment> merchantFulfillmentslist = null;
 	 List<VendorObj> foodObjList =null;
 	 Time time = null;
 	 TimeRange providerTimeRange =null;
 	 Descriptor descriptor =null;
 	 List<Item> item_list =null;
 	 Item item =null;
 	 Descriptor descriptor_item =null;
 	 List<String> item_images =null;
 	 Price price =null;
 	 List<ItemObj> itemDetailsfood=null;
 	 Circle providerCircleLocation = null;
     Scalar providercircleradius =null;
     ItemQuantity itemQuantity = null;
     ItemQuantities itemQuantities = null;
     List<list> taglists = null;
     List<Tags> providertagsList = null;
     Tags providertag = null;
     list taglist= null;
     ItemVegFruit itemVegFruit = null;
     List<String> images =null;
     
     public void servicibilty(String strCategory, List<VendorObj> foodObjList, final ConfigModel configModel) {		
     	
  		taglists = new ArrayList<>();
  		taglist = new list();
  		providertagsList = new ArrayList<>();
  			 providertag = new Tags();
  			 providertag.setAdditionalProp1("serviceability");
  			 
 	         taglist.setAdditionalProp1("location");
 	         taglist.setAdditionalProp2(location_Provider.getId());
 	         taglists.add(taglist);
 	         
 	         taglist = new list();
 	         taglist.setAdditionalProp1("category");
 	         taglist.setAdditionalProp2(strCategory);
 	         taglists.add(taglist);	
 	         
 	         taglist = new list();
 	         taglist.setAdditionalProp1("type");
 	         String storeType="";
 	        
 	         if(foodObjList.get(0).getStoresubtypehyperlocal() != null && foodObjList.get(0).getStoresubtypehyperlocal().equalsIgnoreCase("yes")) {
 	        	storeType = "hyperlocal";
 	         }
 	         if(foodObjList.get(0).getStoresubtypeintercity() != null && foodObjList.get(0).getStoresubtypeintercity().equalsIgnoreCase("yes")) {
 	            if(!storeType.equalsIgnoreCase(""))
 	            {
 	            	storeType = storeType + " , intercity";
 	            }else {
 	        	  storeType = "intercity";
 	            }
 	         }
 	         if(foodObjList.get(0).getStoresubtypepanindia() != null && foodObjList.get(0).getStoresubtypepanindia().equalsIgnoreCase("yes")) {
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
 	         taglist.setAdditionalProp2(configModel.getRestaurantserviceRadius());
 	         taglists.add(taglist);
 	         taglist = new list();
 	         taglist.setAdditionalProp1("unit");
 	         taglist.setAdditionalProp2("Km");
 	         taglists.add(taglist);
 	         providertag.setList(taglists);
 	         
 	         providertagsList.add(providertag);
 	}
     
     public OnSchema fetchResult(String strCategoryId, String search_criteria , final Schema request, final ConfigModel configModel, String host,String subscriberName,String subscriber_S_description, String subscriber_L_description, List<String> vendList) throws SQLException, IOException, NumberFormatException, JSONException {
    	RetailResponseSearchfood.log.info("Inside the RetailResponseSearchfood class fetchResult method...");
    	
        OnSchema respBody = new OnSchema();    	
    	respBody.setContext(request.getContext());
    	respBody.getContext().setAction("on_search");
    	respBody.getContext().setCity("std:040");
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
        
        //To Do == set symbol and image of bornbhukkad
        String imagePath =host+"/image/check/bornbhkkad.jpeg";
        respBody.getMessage().getCatalog().getBppDescriptor().setSymbol(imagePath);
        images =new ArrayList<String>();
        images.add(imagePath);
        respBody.getMessage().getCatalog().getBppDescriptor().setImages(images);
        
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
    
    private OnSchema creatingRespStructre(String strCategoryId, String search_vendorid, OnSchema respBody ,final Schema request , final ConfigModel configModel,List<Provider> Provider_list, String search_criteria, String host) throws SQLException, IOException, NumberFormatException, JSONException {
    	RetailResponseSearchfood.log.info("Inside the RetailResponseSearchfood class creatingRespStructre method...");    	
    	provider = new Provider();
    	location_Provider = new Location();
    	address_Provider = new Address();
    	location_list = new ArrayList<Location>();    	
    	
		merchantContact = new Contact();
		merchantFulfillmentslist = new ArrayList<Fulfillment>();		
    	foodObjList =new ArrayList<VendorObj>();
    	
    	Optional<VendorObj> foodObjopt= mVendorRepository.findBydata(Long.parseLong(search_vendorid));    	
    	foodObjList = foodObjopt.map(Collections::singletonList).orElse(Collections.emptyList());
    	
    	if(strCategoryId!= null && search_criteria != null && !strCategoryId.equalsIgnoreCase("") && !search_criteria.equalsIgnoreCase("")) {
    	   itemDetailsfood= mItemRepository.findByVendoridanditemcategoryandsearch(strCategoryId,search_criteria, search_vendorid);
    	   
    	}else if(strCategoryId!= null && !strCategoryId.equalsIgnoreCase("")) {
    		
    		itemDetailsfood= mItemRepository.findByVendoridanditemcategory(strCategoryId, search_vendorid);
    		
    	}else if(search_criteria!=null && !search_criteria.equalsIgnoreCase("")) {
    		itemDetailsfood= mItemRepository.findByVendoridandsearch(search_criteria, search_vendorid);
    	}else {
     		itemDetailsfood= mItemRepository.findByVendorid(search_vendorid);
    	}
    	
        String opted="";
        String is_open="";
        for (int i=0; i< foodObjList.size(); i++) {
          merchantFulfillment =new Fulfillment();
          opted=foodObjList.get(i).getOpted();	
          is_open=foodObjList.get(i).getIsOpen();	
       	  provider.setId((foodObjList.get(i).getId()).toString());
       	  provider.setFssaiLicenseNo(foodObjList.get(i).getFssai());
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
       	  descriptor.setName(foodObjList.get(i).getStorename()); 
       	  //need to write the image       	          	  
       	  descriptor.setShortDesc(foodObjList.get(i).getShort_desc());
       	  descriptor.setLongDesc(foodObjList.get(i).getLong_desc());    
       	//need to write the image
       	  String providerdecriptorimage = foodObjList.get(i).getSymbol_name();
       	  
       	  providerdecriptorimage =host+"/image/view/"+foodObjList.get(i).getId()+"/"+providerdecriptorimage +"/"+"symbol_name_f";       	  
       	  descriptor.setSymbol(providerdecriptorimage);
       	  images =new ArrayList<String>();
          images.add(providerdecriptorimage);
       	  descriptor.setImages(images);       	
   	   
   	      provider.setDescriptor(descriptor);
   	       	 
   	      getStringFromLocation(Double.parseDouble(foodObjList.get(i).getLatitude()), Double.parseDouble(foodObjList.get(i).getLongitude()), address_Provider);
   	      //address_Provider.setCity("Telangana");        
	        location_Provider.setAddress(address_Provider);
	        location_Provider.setId((foodObjList.get(i).getId()).toString());
	        
	        String latitude=foodObjList.get(i).getLatitude();
	        String longitude=foodObjList.get(i).getLongitude();
	        String gps=latitude +","+ longitude;
	        location_Provider.setGps(gps);
	        
	        providerCircleLocation = new Circle();
	        providerCircleLocation.setGps(gps);
	        providercircleradius = new Scalar();
	        providercircleradius.setUnit("KM");
	        providercircleradius.setValue(new Integer(configModel.getRestaurantserviceRadius()));
	        providerCircleLocation.setRadius(providercircleradius);
	        location_Provider.setCircle(providerCircleLocation);
	        time = new Time();
	        time.setDays(foodObjList.get(i).getStoreworkingdays());
	        List<String> holidays =new ArrayList<String>();
	        holidays.add("2022-08-15");
	        holidays.add("2022-08-19");
	        time.setSchedule(new Schedule());
	        time.getSchedule().setHolidays(holidays);
	        providerTimeRange.setStart(foodObjList.get(i).getStoreopentimehours());
	        providerTimeRange.setEnd(foodObjList.get(i).getStoreclosetimehours());
	        time.setRange(providerTimeRange);
	        location_Provider.setTime(time);
	        
	        location_list.add(location_Provider);
	        provider.setLocations(location_list);
	        String ttl= ""+Duration.ofMinutes(Long.parseLong(configModel.getBecknTtl()));
	        provider.setTtl(ttl);      
	        
	        merchantContact.setEmail(foodObjList.get(i).getEmail());
	        merchantContact.setPhone(foodObjList.get(i).getPhone());
	        merchantFulfillment.setContact(merchantContact);
	        merchantFulfillmentslist.add(merchantFulfillment);
		}
        		      
        provider.setFulfillments(merchantFulfillmentslist);
        
        item_list = new ArrayList<Item>();
        item = new Item();
        descriptor_item = new Descriptor();
        item_images = new ArrayList<String>();
        price =new Price();
        
        for (int i=0; i< itemDetailsfood.size(); i++) {
        	
        	   itemVegFruit = new ItemVegFruit();
			   descriptor_item = new Descriptor();
			   item = new Item();
			   price =new Price();
			   item_images = new ArrayList<String>();
			   descriptor_item.setName(itemDetailsfood.get(i).getItemname());
			 
	           String itemdecriptorimage = itemDetailsfood.get(i).getItem_image_1_name();	           
	           itemdecriptorimage =host+"/image/view/"+itemDetailsfood.get(i).getId()+"/"+itemdecriptorimage +"/"+"Item_image_1_fdetail";
		       descriptor_item.setSymbol(itemdecriptorimage);
		       descriptor_item.setShortDesc(itemDetailsfood.get(i).getShort_desc());
		       descriptor_item.setLongDesc(itemDetailsfood.get(i).getLong_desc());			       
		       item_images.add(itemdecriptorimage);
		       
		       //optional for F&B
	           //String itemdecriptorimage2 = itemDetailsfood.get(i).getItem_image_2_name();	        	
	           //itemdecriptorimage2 =host+"/image/view/"+itemDetailsfood.get(i).getId()+"/"+itemdecriptorimage2 +"/"+"Item_image_2_kdetail";	        	
		       //item_images.add(itemdecriptorimage2);
		       //optional for F&B
	           //String itemdecriptorimage3 = itemDetailsfood.get(i).getItem_image_3_name();	        	
	           //itemdecriptorimage3 =host+"/image/view/"+itemDetailsfood.get(i).getId()+"/"+itemdecriptorimage3 +"/"+"Item_image_3_kdetail";	        
		       //item_images.add(itemdecriptorimage3);
		       //optional for F&B
	           //String itemdecriptorimage4 = itemDetailsfood.get(i).getItem_image_4_name();	        	
	           //itemdecriptorimage4 =host+"/image/view/"+itemDetailsfood.get(i).getId()+"/"+itemdecriptorimage4 +"/"+"Item_image_4_kdetail";	        	
		       //item_images.add(itemdecriptorimage4);
		       
		       descriptor_item.setImages(item_images);
		       item.setId((itemDetailsfood.get(i).getId()).toString());
		       item.setDescriptor(descriptor_item);		       
		       
		       itemQuantity = new ItemQuantity();
		       itemQuantities = new ItemQuantities();
		       itemQuantities.setCount(25);		       
		       itemQuantity.setAvailable(itemQuantities);
		       itemQuantities = new ItemQuantities();
		       itemQuantities.setCount(50);
		       itemQuantity.setMaximum(itemQuantities);
		       item.setQuantity(itemQuantity);
		       
		       item.setLocationId(itemDetailsfood.get(i).getVendorid());
		       price.setCurrency("INR");
		       price.setValue(itemDetailsfood.get(i).getItemprice());
		       price.setMaximumValue(itemDetailsfood.get(i).getItemprice());
		       item.setPrice(price);		       
		       item.setCategoryId(itemDetailsfood.get(i).getItemcategory());
		       item.setRecommended(true);
		       if(opted.equalsIgnoreCase("true")) {
		    	   item.setFulfillmentId("1");
		       }else {
		    	   item.setFulfillmentId("2");
		       }
		       item.setReturnable(Boolean.valueOf(configModel.getReturnable()));
		       item.setCancellable(Boolean.valueOf(configModel.getCancellable()));
		       item.setAvailableOnCod(true);
		       item.setReturnWindow(configModel.getRestaurantreturnWindow());
		       item.setSellerPickupReturn(false);
		       if(foodObjList.get(0).getTimetoshipinminute()!=null) {
		    	   Long ll=Long.parseLong(foodObjList.get(0).getTimetoshipinminute());
			       String timeToShip =""+Duration.ofMinutes(ll);
			       item.setTimeToShip(timeToShip);
		       }else {
		    	   item.setTimeToShip("PT45M");
		       }
		       
		       item.setAvailableOnCod(true);
		       String customer_care="Lava Foods Private Limited, Hyderabad,+91-6304928766,  mailto:support@bornbhukkad.com";		       
		       item.setContactDetailsConsumerCare(customer_care);
		       
		       item.setTags(new Tags());
		       if(itemDetailsfood.get(i).getVegNonveg() != null && itemDetailsfood.get(i).getVegNonveg().equalsIgnoreCase("")) {
		    	   String strcusineType= itemDetailsfood.get(i).getVegNonveg();
		    	   if(strcusineType.equalsIgnoreCase("veg")) {
		    		   item.getTags().setAdditionalProp2("yes");
		    		   item.getTags().setAdditionalProp3("no");
		    	   }else {
		    		   item.getTags().setAdditionalProp2("no");
		    		   item.getTags().setAdditionalProp3("yes");
		    	   }
		       }
		       String strWeight= itemDetailsfood.get(i).getNewQuantity()+ " "+ itemDetailsfood.get(i).getUnit();
		       itemVegFruit.setAdditionalProp1(strWeight);
		       item.setMandatoryReqsVeggiesFruits(itemVegFruit);
		       item_list.add(item);
		}
        HashSet<String> uniqueCategory =new HashSet<String>();
		
		for(int k=0; k < itemDetailsfood.size(); k++) {
			uniqueCategory.add(itemDetailsfood.get(k).getItemcategory());
		}
		Iterator<String> itr=uniqueCategory.iterator();  
		  while(itr.hasNext()){  
			  servicibilty(itr.next(), foodObjList,configModel);
		  }  
        //servicibilty(itemDetailsfood , foodObjList, configModel);
		provider.setTags(providertagsList);
		
        provider.setItems(item_list);
		Provider_list.add(provider);		
		respBody.getMessage().getCatalog().setBppProviders(Provider_list);
		System.out.println("respBody--  "+ respBody);
    	return respBody;
    	
    }
    
    private List<Address> getStringFromLocation(double lat, double lng, Address address_Provider)
            throws ClientProtocolException, IOException, JSONException {
    	RetailResponseSearchfood.log.info("Inside the RetailResponseSearchfood class getStringFromLocation method...");   
        String address = String.format(Locale.ENGLISH,"https://maps.googleapis.com/maps/api/geocode/json?key=AIzaSyDdDXmDfdTgN0N1nTF4T1Qq6y2ZG4hZTj0&latlng=%1$f,%2$f&sensor=true&language="
                                + Locale.getDefault().getCountry(), lat, lng);
        HttpGet httpGet = new HttpGet(address);
        //HttpClient client = new DefaultHttpClient();        
        CloseableHttpClient client= HttpClientBuilder.create().build();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();
        response = client.execute(httpGet);
        HttpEntity entity = response.getEntity();
        InputStream stream = entity.getContent();
        int b;
        while ((b = stream.read()) != -1) {
            stringBuilder.append((char) b);
        }
        client.close();
        jsonObject = new JSONObject(stringBuilder.toString());

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
        log = LoggerFactory.getLogger((Class)RetailResponseSearchfood.class);
    }
}
