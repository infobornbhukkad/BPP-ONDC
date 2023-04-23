// 
// Decompiled by Procyon v0.5.36
// 

package com.bb.beckn.select.utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bb.beckn.ConnectionUtil;
import com.bb.beckn.api.model.common.Address;
import com.bb.beckn.api.model.common.Descriptor;
import com.bb.beckn.api.model.common.Fulfillment;
import com.bb.beckn.api.model.common.Item;
import com.bb.beckn.api.model.common.ItemQuantities;
import com.bb.beckn.api.model.common.ItemQuantity;
import com.bb.beckn.api.model.common.Location;
import com.bb.beckn.api.model.common.Quotation;
import com.bb.beckn.api.model.common.QuotationBreakUp;
import com.bb.beckn.api.model.common.Scalar;
import com.bb.beckn.api.model.common.Start;
import com.bb.beckn.api.model.common.State;
import com.bb.beckn.common.builder.HeaderBuilder;
import com.bb.beckn.common.model.ConfigModel;
import com.bb.beckn.common.sender.Sender;
import com.bb.beckn.common.service.ApplicationConfigService;
import com.bb.beckn.common.util.JsonUtil;
import com.bb.beckn.fetchResult.controller.FetchResultControllerLogisticBuyer;
import com.bb.beckn.init.utility.RetailResponseInitfood;
import com.bb.beckn.repository.BapFeeRepository;
import com.bb.beckn.repository.ItemRepositoryKirana;
import com.bb.beckn.repository.KiranaRepository;
import com.bb.beckn.repository.LogisticFinderRepository;
import com.bb.beckn.search.model.ApiSellerObj;
import com.bb.beckn.search.model.BapFeeObj;
import com.bb.beckn.search.model.ItemDetailsKirana;
import com.bb.beckn.search.model.KiranaObj;
import com.bb.beckn.search.model.LogisticBppObj;
import com.bb.beckn.search.utility.RetailResponseSearchkirana;
import com.bb.beckn.select.extension.OnSchema;
import com.bb.beckn.select.extension.Schema;
import com.bb.beckn.utility.BapFinderFee;
import com.bb.beckn.utility.RetailRequestSearchLogistic;

import com.bb.beckn.api.model.common.Order;
import com.bb.beckn.api.model.common.Price;
import com.bb.beckn.api.model.common.Provider;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class RetailResponseSelectkirana {
	private static final Logger log;
	
	@Autowired
	LogisticFinderRepository mLogisticFinderRepository;
	@Autowired
	FetchResultControllerLogisticBuyer mfetchResultControllerLogisticBuyer;
	@Autowired
	RetailResponseSearchkirana retailResponseSearchkirana;
	@Autowired
	RetailRequestSearchLogistic mRetailRequestSearchLogistic;
	@Autowired
	ItemRepositoryKirana mItemRepositoryKirana;
	@Autowired
	KiranaRepository mKiranaRepository;
	@Autowired
	private Sender sender;
	@Autowired
	private JsonUtil jsonUtil;
	@Autowired
	private HeaderBuilder authHeaderBuilder;
	@Autowired
	private ApplicationConfigService configService;
	@Autowired
	private BapFinderFee mBapFinderFee;
	
	
	Provider provider = null;
	Location location_Provider = null;
	Address address_Provider = null;
	List<Location> location_list = null;
	List<KiranaObj> kiranaObjList = null;
	String search_vendorid = "";
	List<Item> item_list = null;
	Item item = null;
	ItemQuantity itemQuantity = null;
	List<QuotationBreakUp> quotationBreakUp_list = null;
	QuotationBreakUp quotationBreakUp = null;
	QuotationBreakUp quotationBreakUptax = null;
	QuotationBreakUp quotationBreakUpdiscount = null;
	State fulfilmentState = null;
	Descriptor fulfilmentDescriptor = null;
	Price itemPrice = null;
	Price itemPricetax = null;
	Price itemPricediscount = null;
	
	Fulfillment fulfillment =null;
	List<Fulfillment> fulfillmentObjList = null;
	
	String strLogisticPrName="";
	String strLogisticBppId="";
	String strLogisticPrBppUrl="";
	
	private void createProviderStructure(final Schema request, OnSchema respBody)throws NumberFormatException, ClientProtocolException, IOException, JSONException
	{
		RetailResponseSelectkirana.log.info("Inside the RetailResponseSelectkirana class of createProviderStructure method...");
		provider = new Provider();
		location_Provider = new Location();
		address_Provider = new Address();
		location_list = new ArrayList<Location>();
		kiranaObjList = new ArrayList<KiranaObj>();
		search_vendorid = request.getMessage().getOrder().getProvider().getId();

		Optional<KiranaObj> kiranaObjopt = mKiranaRepository.findBydata(Long.parseLong(search_vendorid));
		kiranaObjList = kiranaObjopt.map(Collections::singletonList).orElse(Collections.emptyList());
		//added extra line
		
		for (int i = 0; i < kiranaObjList.size(); i++) {
			provider.setId((kiranaObjList.get(i).getId()).toString());
			//Descriptor descriptor = new Descriptor();
			//descriptor.setName(kiranaObjList.get(i).getStorename());
			//provider.setDescriptor(descriptor);

			this.retailResponseSearchkirana.getStringFromLocation(Double.parseDouble(kiranaObjList.get(i).getLatitude()),Double.parseDouble(kiranaObjList.get(i).getLongitude()), address_Provider);

			location_Provider.setAddress(address_Provider);
			location_Provider.setId((kiranaObjList.get(i).getId()).toString());

			String latitude = kiranaObjList.get(i).getLatitude();
			String longitude = kiranaObjList.get(i).getLongitude();
			String gps = latitude + "," + longitude;
			location_Provider.setGps(gps);

			location_list.add(location_Provider);
			provider.setLocations(location_list);

			respBody.getMessage().getOrder().setProvider(provider);
		}

	}

	private void createItemStructure(final Schema request, OnSchema respBody, String strRandom , final ConfigModel	configModel)
			throws NumberFormatException, ClientProtocolException, IOException, JSONException {
		RetailResponseSelectkirana.log.info("Inside the RetailResponseSelectkirana class of createItemStructure method...");
		int itemSize = request.getMessage().getOrder().getItems().size();
		item_list = new ArrayList<Item>();

		for (int i = 0; i < itemSize; i++) {
			item = new Item();
			//itemQuantity = new ItemQuantity();
			//ItemDetailsKirana itemDetailsKirana = mItemRepositoryKirana.findByItemidandvendorid(Long.parseLong(request.getMessage().getOrder().getItems().get(i).getId()),
					//request.getMessage().getOrder().getProvider().getId());

			//item.setId((itemDetailsKirana.getId()).toString());
			item.setId(request.getMessage().getOrder().getItems().get(i).getId());
			//itemQuantity.setCount(request.getMessage().getOrder().getItems().get(i).getQuantity().getCount());
			//item.setQuantity(itemQuantity);
			item.setFulfillmentId(strRandom);
			//item.setReturnable(Boolean.valueOf(configModel.getReturnable()));
			//item.setCancellable(Boolean.valueOf(configModel.getCancellable())); 
			item_list.add(item);
		}
		respBody.getMessage().getOrder().setItems(item_list);

	}

	public OnSchema fetchResult(final Schema request, final ConfigModel	configModel, String host, OnSchema respBody, List itemDetailsKirana) throws SQLException, IOException, NumberFormatException, JSONException
	{
		RetailResponseSelectkirana.log.info("Inside the RetailResponseSelectkirana class of fetchResult method...");
		// Validating list of items for particlar select in DB
		createProviderStructure( request, respBody); item = new Item(); String
		strRandom= String.valueOf(Math.random()); 
		createItemStructure( request,respBody, strRandom,configModel); 
		item_list = new ArrayList<Item>();
		quotationBreakUp_list = new ArrayList<QuotationBreakUp>(); 
		quotationBreakUp =	new QuotationBreakUp();
		fulfillmentObjList = new ArrayList<Fulfillment>();
		item_list = request.getMessage().getOrder().getItems();
		
		
		fulfillmentObjList.add(request.getMessage().getOrder().getFulfillments().get(0));
		
		respBody.getMessage().getOrder().setFulfillments(fulfillmentObjList);
		//respBody.getMessage().getOrder().getFulfillments().get(0).setStart(new Start());
		//respBody.getMessage().getOrder().getFulfillments().get(0).getStart().setLocation(new Location());
		//respBody.getMessage().getOrder().getFulfillments().get(0).getStart().getLocation().setGps(respBody.getMessage().getOrder().getProvider().getLocations().get(0).getGps());
		//respBody.getMessage().getOrder().getFulfillments().get(0).getStart().getLocation().setAddress(respBody.getMessage().getOrder().getProvider().getLocations().get(0).getAddress()); 
		respBody.getMessage().getOrder().getFulfillments().get(0).setId(strRandom);
		
		itemPrice = new Price();
		itemQuantity = new ItemQuantity();
		
		float totalitemweight = 0;
		float eachItemprice = 0;
		float totalitemprice = 0;
		
		if (respBody.getMessage().getOrder() == null) 
		{
			respBody.getMessage().setOrder(new Order()); 
		} 
		if(respBody.getMessage().getOrder().getQuote() == null) 
		{
			respBody.getMessage().getOrder().setQuote(new Quotation());
		} 
		if(respBody.getMessage().getOrder().getQuote().getBreakup() == null)
		{
			respBody.getMessage().getOrder().getQuote().setBreakup(quotationBreakUp_list); 
		} 
		if (respBody.getMessage().getOrder().getQuote().getPrice() == null) 
		{
			respBody.getMessage().getOrder().getQuote().setPrice(new Price()); 
		}
        
		BapFeeObj mBapFeeObj = mBapFinderFee.getBapFinderFee(request.getContext().getBapId());
		String type="";
		String finder_fee_amount="";
		if(mBapFeeObj!= null) {
			 type = mBapFeeObj.getType();
			 finder_fee_amount =	mBapFeeObj.getFinder_fee_amount();
		}
		
		for (int i = 0; i < item_list.size(); i++) {
			
			float eachItempriceTax = 0; 
			float eachItempricediscont = 0; 
			String itemUnit="";
			float eachItemweight = 0;
			quotationBreakUp = new	QuotationBreakUp(); 
			itemPrice = new Price(); 
			String itemID =	request.getMessage().getOrder().getItems().get(i).getId();
			item = new Item();
			try {
                
				ItemDetailsKirana ItemDetailsKiranaObj =(ItemDetailsKirana)itemDetailsKirana.get(i);
				
				//ItemDetailsKirana ItemDetailsKiranaObj =mItemRepositoryKirana.findByItemid(Long.parseLong(itemID));
				quotationBreakUp.setItemId(itemID);

				itemQuantity.setCount(request.getMessage().getOrder().getItems().get(i).getQuantity().getCount()); 
				quotationBreakUp.setItemQuantity(itemQuantity);
				quotationBreakUp.setTitleType("item");
				quotationBreakUp.setTitle(ItemDetailsKiranaObj.getItemname());

				item.setPrice(new Price()); 
				item.getPrice().setCurrency("INR");
				item.getPrice().setValue(ItemDetailsKiranaObj.getItemprice());
				item.setQuantity(new ItemQuantity());
				item.getQuantity().setAvailable(new ItemQuantities());
				item.getQuantity().setMaximum(new ItemQuantities());
				item.getQuantity().getAvailable().setCount(20);
				item.getQuantity().getMaximum().setCount(20); 
				quotationBreakUp.setItem(item);
				
				eachItemprice = Float.parseFloat(ItemDetailsKiranaObj.getItemprice())*	request.getMessage().getOrder().getItems().get(i).getQuantity().getCount();
				totalitemprice = totalitemprice + eachItemprice;
                
				if(ItemDetailsKiranaObj.getUnit()!= null && !ItemDetailsKiranaObj.getUnit().equalsIgnoreCase("")) {
					
					itemUnit =ItemDetailsKiranaObj.getUnit();
					if(itemUnit.contains("Litre")  || itemUnit.contains("ltr") || itemUnit.contains("MilliLitre")  || itemUnit.contains("Pound") )
					{
						if(itemUnit.contains("MilliLitre")) {
							eachItemweight =Float.parseFloat(ItemDetailsKiranaObj.getNewQuantity())/1000;
							totalitemweight=totalitemweight+eachItemweight;
						}else if(itemUnit.contains("Litre")  || itemUnit.contains("ltr")) {
							eachItemweight =Float.parseFloat(ItemDetailsKiranaObj.getNewQuantity());
							totalitemweight=totalitemweight+eachItemweight;
						}else if(itemUnit.contains("Pound")) {
							eachItemweight =(float) (Float.parseFloat(ItemDetailsKiranaObj.getNewQuantity()) * 0.45);
							totalitemweight=totalitemweight+eachItemweight;
						}
					}
					if(itemUnit.contains("Gms")  || itemUnit.contains("Gram") || itemUnit.contains("Kg") || itemUnit.contains("Kilogram")|| itemUnit.contains("Milligram"))
					{
						if(itemUnit.contains("Gms") || itemUnit.contains("Gram")) {
							eachItemweight =Float.parseFloat(ItemDetailsKiranaObj.getNewQuantity())/1000;
							totalitemweight=totalitemweight+eachItemweight;
						}else if(itemUnit.contains("Milligram")) {
							eachItemweight =(float) (Float.parseFloat(ItemDetailsKiranaObj.getNewQuantity()) * 0.000001);
							totalitemweight=totalitemweight+eachItemweight;							
						}else if(itemUnit.contains("Kg") || itemUnit.contains("Kilogram")){
							eachItemweight =Float.parseFloat(ItemDetailsKiranaObj.getNewQuantity());
							totalitemweight=totalitemweight+eachItemweight;
						}
					}
				}
				
				itemPrice.setCurrency("INR");				
				quotationBreakUp.setPrice(itemPrice);
				quotationBreakUp_list.add(quotationBreakUp);				
                eachItempriceTax= calculatetax(eachItemprice, kiranaObjList);
                RetailResponseSelectkirana.log.info("eachItempriceTax-- "+ eachItempriceTax);
                
				quotationBreakUptax = new QuotationBreakUp();
				quotationBreakUptax.setTitleType("tax"); 
				quotationBreakUptax.setTitle("Tax");
				itemPricetax = new Price();
				itemPricetax.setCurrency("INR");
				itemPricetax.setValue(String.valueOf(eachItempriceTax)); 
				quotationBreakUptax.setPrice(itemPricetax);
				quotationBreakUptax.setItemId(itemID);
				quotationBreakUp_list.add(quotationBreakUptax);
				
				itemPrice.setValue(Float.toString(eachItemprice));
				
				totalitemprice = totalitemprice + eachItempriceTax;
				//discount
				eachItempricediscont= calculateDiscount(eachItemprice, kiranaObjList);
				RetailResponseSelectkirana.log.info("eachItempricediscont-- "+ eachItempricediscont);
				if(eachItempricediscont !=0) {
					quotationBreakUpdiscount = new QuotationBreakUp();
					quotationBreakUpdiscount.setTitleType("discount");
					quotationBreakUpdiscount.setTitle("Discount"); 
					itemPricediscount = new	Price(); 
					itemPricediscount.setCurrency("INR");
					itemPricediscount.setValue(String.valueOf(eachItempricediscont));
					quotationBreakUpdiscount.setPrice(itemPricediscount);
					quotationBreakUpdiscount.setItemId(itemID);
					quotationBreakUp_list.add(quotationBreakUpdiscount);
					
					totalitemprice = totalitemprice - eachItempricediscont;
				}
				respBody.getMessage().getOrder().getItems().get(i).setReturnable(Boolean.valueOf(configModel.getReturnable()));
				respBody.getMessage().getOrder().getItems().get(i).setCancellable(Boolean.valueOf(configModel.getCancellable()));

				} catch (Exception e) 
				{ 
					RetailResponseSelectkirana.log.info("Exception occurs in exection of itemlist ----------" +	e.getMessage()); 
					break; 
				}
		    }

		
		// packaging charges
		Optional<KiranaObj> kiranaObjopt = mKiranaRepository.findBydata(Long.parseLong(search_vendorid));
		kiranaObjList = kiranaObjopt.map(Collections::singletonList).orElse(Collections.emptyList());
		if(kiranaObjList.get(0).getPackagingcharge()!=null && !kiranaObjList.get(0).getPackagingcharge().equalsIgnoreCase("") && !kiranaObjList.get(0).getPackagingcharge().equalsIgnoreCase("0")) {
			quotationBreakUp = new QuotationBreakUp(); 
			itemPrice = new Price();
			quotationBreakUp.setTitleType("packing");
			quotationBreakUp.setTitle("Packing charges"); 
			itemPrice.setCurrency("INR");
			if(kiranaObjList.get(0).getPackagingcharge()!=null && !kiranaObjList.get(0).getPackagingcharge().equalsIgnoreCase("") ) {
				itemPrice.setValue(kiranaObjList.get(0).getPackagingcharge()); 
			}else {
				 itemPrice.setValue("0"); 
			}
			quotationBreakUp.setPrice(itemPrice);
			quotationBreakUp_list.add(quotationBreakUp); 
			totalitemprice = totalitemprice	+ Float.parseFloat(itemPrice.getValue());
		}
		//misc charges 
		/*quotationBreakUp =new QuotationBreakUp(); 
		itemPrice = new Price();
		quotationBreakUp.setTitleType("misc");
		quotationBreakUp.setTitle("Convenience Fee");
		itemPrice.setCurrency("INR");
		itemPrice.setValue("0"); 
		quotationBreakUp.setPrice(itemPrice);
		quotationBreakUp_list.add(quotationBreakUp);
		totalitemprice = totalitemprice	+ Float.parseFloat(itemPrice.getValue());
        */
		//quotationBreakUp_list.add(quotationBreakUp); 
		//totalitemprice = totalitemprice + Float.parseFloat(itemPrice.getValue());
		String ttl= ""+Duration.ofMinutes(Long.parseLong(configModel.getBecknTtl()));
		respBody.getMessage().getOrder().getQuote().setTtl(ttl);
		respBody.getMessage().getOrder().getQuote().getPrice().setCurrency("INR");
		System.out.println("totalitemprice --"+totalitemprice );
		
		String strtransactionid =request.getContext().getTransactionId()+"Logistic";
		String strmessageid =request.getContext().getMessageId()+"Logistic";
		
		this.mRetailRequestSearchLogistic.creatingLogisticsearchRequestStructureKirana(request, respBody, totalitemweight, totalitemprice, kiranaObjList.get(0).getStoretype(),kiranaObjList, strtransactionid,strmessageid); 
		
		List<ApiSellerObj> apiLogisticSellerrObj_list = this.mfetchResultControllerLogisticBuyer.fetch(strtransactionid, strmessageid);
		String ordertodelivery = "";
			
		/// sorting the logistic json object
		com.bb.beckn.search.extension.OnSchema modelToSave =null;
		String providerNameTosave="";
		String provideridToSave ="";
		String strCaterogyIdToSave ="";
		float deliverychargeToSave =0;
		if(apiLogisticSellerrObj_list.size()!=0) {
			
			for(int i=0; i < apiLogisticSellerrObj_list.size(); i++) {
				com.bb.beckn.search.extension.OnSchema model  = (com.bb.beckn.search.extension.OnSchema)this.jsonUtil.toModel(apiLogisticSellerrObj_list.get(i).getJson(), (Class)com.bb.beckn.search.extension.OnSchema.class);
			    for(int j=0; j < model.getMessage().getCatalog().getBppProviders().size(); j++ ) {
			    	
			    	Provider tempProvider = new Provider();
			    	tempProvider= model.getMessage().getCatalog().getBppProviders().get(j);
			    	String tempproviderid= tempProvider.getId();
			    	String strProviderName=tempProvider.getDescriptor().getName();
			    	String tempCaterogyId=tempProvider.getCategories().get(0).getId();
			    	
			    	for(int k=0; k < tempProvider.getItems().size(); k++) {	  
			    		if(tempProvider.getItems().get(k).getParentItemId()!= null && tempProvider.getItems().get(k).getParentItemId().equalsIgnoreCase("")) {
			    			float tempdeliverycharge= Float.parseFloat(tempProvider.getItems().get(k).getPrice().getValue());
			    			
			    			if(i==0 && k==0 && j==0) {
				    			deliverychargeToSave=tempdeliverycharge;
				    		}
			    			if(tempCaterogyId!=null) {
				    			if(tempCaterogyId.equalsIgnoreCase("Immediate Delivery") || tempCaterogyId.equalsIgnoreCase("Same Day Delivery")) {
				    				if(tempdeliverycharge <= deliverychargeToSave) {
				    					deliverychargeToSave =tempdeliverycharge;
					    				modelToSave=model;
					    				providerNameTosave=strProviderName;
					    				provideridToSave=tempproviderid;
					    				strCaterogyIdToSave=tempCaterogyId;
									} /*
										 * else { deliverychargeToSave =tempdeliverycharge; modelToSave=model;
										 * providerNameTosave=strProviderName; provideridToSave=tempproviderid;
										 * strCaterogyIdToSave=tempCaterogyId; }
										 */
				    				//continue;
				    			}else if(tempdeliverycharge <= deliverychargeToSave){
				    				deliverychargeToSave=tempdeliverycharge;
				    				modelToSave=model;
				    				providerNameTosave=strProviderName;
				    				provideridToSave=tempproviderid;
				    				strCaterogyIdToSave=tempCaterogyId;
				    			}
			    			}
			    		}
			    	}
			    }
			}
			Duration duration=null;
			if(apiLogisticSellerrObj_list.size() != 0 && modelToSave != null) {
		        
	    	    respBody.getMessage().getOrder().getFulfillments().get(0).setCategory(modelToSave.getMessage().getCatalog().getBppProviders().get(0).getCategories().get(0).getId());
				respBody.getMessage().getOrder().getFulfillments().get(0).setProviderName(modelToSave.getMessage().getCatalog().getBppProviders().get(0).getDescriptor().getName());
				ordertodelivery =  modelToSave.getMessage().getCatalog().getBppProviders().get(0).getCategories().get(0).getTime().getDuration();
				duration = Duration.parse(ordertodelivery);					
					       
		        if(duration!= null) {
			        Long timetodeiver= duration.toMinutes() + Long.parseLong(kiranaObjList.get(0).getTimetoshipinminute());
			        ordertodelivery = ""+Duration.ofMinutes(timetodeiver);
					respBody.getMessage().getOrder().getFulfillments().get(0).setTracking(Boolean.valueOf(configModel.getTracking()));
					respBody.getMessage().getOrder().getFulfillments().get(0).setTat(ordertodelivery);
		        }else {
		        	respBody.getMessage().getOrder().getFulfillments().get(0).setTracking(Boolean.valueOf(configModel.getTracking()));
					respBody.getMessage().getOrder().getFulfillments().get(0).setTat("");
					respBody.getMessage().getOrder().getFulfillments().get(0).setCategory("");
					respBody.getMessage().getOrder().getFulfillments().get(0).setProviderName("");
		        }
		        setLogisticProvider( modelToSave,  strtransactionid,  strmessageid, provideridToSave, providerNameTosave, deliverychargeToSave, strCaterogyIdToSave, ordertodelivery);
			}else {
				
				    respBody.getMessage().getOrder().getFulfillments().get(0).setCategory("");
					respBody.getMessage().getOrder().getFulfillments().get(0).setProviderName("");
					//ordertodelivery =  modelToSave.getMessage().getCatalog().getBppProviders().get(0).getCategories().get(0).getTime().getDuration();
					//duration = Duration.parse(ordertodelivery);	
		        	respBody.getMessage().getOrder().getFulfillments().get(0).setTracking(Boolean.valueOf(configModel.getTracking()));
					respBody.getMessage().getOrder().getFulfillments().get(0).setTat("");
					respBody.getMessage().getOrder().getFulfillments().get(0).setCategory("");
					respBody.getMessage().getOrder().getFulfillments().get(0).setProviderName("");
			       
				
			}
		}
		
		
		fulfilmentState = new State(); 
		fulfilmentDescriptor = new Descriptor();
		if(apiLogisticSellerrObj_list.size() != 0 && modelToSave != null) {
			fulfilmentDescriptor.setCode("Serviceable");
		}else {
			fulfilmentDescriptor.setCode("Non-serviceable");
			respBody.setError(new Scalar());
			
			respBody.getError().setType("DOMAIN-ERROR");
			respBody.getError().setCode("30009");
			deliverychargeToSave=0;
		}
		
		fulfilmentState.setDescriptor(fulfilmentDescriptor);
		respBody.getMessage().getOrder().getFulfillments().get(0).setState(fulfilmentState);
		//delivery charges
		quotationBreakUp = new QuotationBreakUp();
		itemPrice = new Price();
		
		if(deliverychargeToSave!=0) {
			quotationBreakUp.setTitleType("Delivery");
			quotationBreakUp.setTitle("Delivery Charges"); 
			itemPrice.setCurrency("INR");
			itemPrice.setValue(String.valueOf(deliverychargeToSave)); 
			quotationBreakUp.setPrice(itemPrice);
			quotationBreakUp_list.add(quotationBreakUp); 
			totalitemprice = totalitemprice	+ deliverychargeToSave; 
		}
		respBody.getMessage().getOrder().getQuote().getPrice().setValue(Float.toString(totalitemprice));
		
		return respBody;
	}
	
	private void setLogisticProvider(com.bb.beckn.search.extension.OnSchema model,String strtransactionid, String strmessageid,String provideridToSave,String providerNameTosave,float deliverychargeToSave, String CaterogyIdToSave,String ordertodelivery) {    	
		RetailResponseSelectkirana.log.info("Inside the RetailResponseSelectkirana class of setLogisticProvider method...");
    	try {
    		String querySTRUpdate="";
    		Timestamp timestamp = new Timestamp(System.currentTimeMillis());   
    		LogisticBppObj myapiLogisticBppObj = new LogisticBppObj(providerNameTosave, 
    				model.getContext().getBppId(),model.getContext().getBppUri(),timestamp.toString(),"",this.jsonUtil.toJson((Object)model), strtransactionid, strmessageid, provideridToSave,Float.toString(deliverychargeToSave),CaterogyIdToSave, "on_search", ordertodelivery);
    		
    		//if (!mLogisticFinderRepository.existsBybppid(model.getContext().getBppId())) {
    		List<LogisticBppObj> listLogisticBppObj =mLogisticFinderRepository.findBytransaction_idandaction(strtransactionid, model.getContext().getAction());
    		if (listLogisticBppObj.size()==0) {
    			mLogisticFinderRepository.save(myapiLogisticBppObj);
    			System.out.println("data saved--LogisticBppObj  ");
    		}else {
    			System.out.println("else ----------");
    			//ResultSet rsUpdate;
    			int resultset=0;
    	        Connection connUpdate= ConnectionUtil.getConnection();
    	        Statement stmUpdate= connUpdate.createStatement();
    	        querySTRUpdate= "update bornbhukkad.ondc_bpp_logistic_finder baf set baf.providername ='" +model.getMessage().getCatalog().getBppProviders().get(0).getDescriptor().getName()+
    	        		         "', baf.bppid ='"+model.getContext().getBppId()+"' , baf.bppurl='"+model.getContext().getBppUri()+ "' , baf.updated_on='"+timestamp.toString()+
    	        		         "' , baf.json='"+this.jsonUtil.toJson((Object)model) + "', baf.transaction_id ='"+strtransactionid+ "', baf.message_id ='"+strmessageid+
    	        		        "' where baf.bppid ='"+ model.getContext().getBppId()  +"' and baf.action='"+ model.getContext().getAction()+"' "
    	        		        		+ " and baf.transaction_id ='" +strtransactionid+ "';";
    	        resultset = stmUpdate.executeUpdate(querySTRUpdate);
    	        System.out.println("data updated--LogisticBppObj  ");
    	        stmUpdate.close(); 
    		}
    		
    	        
		}catch(Exception e) {
			System.out.println("Exception--"+ e);
		}
		
	 }
	private float calculatetax(float eachItemprice, List<KiranaObj> kiranaObjList)
	{
		RetailResponseSelectkirana.log.info("Inside the RetailResponseSelectkirana class of calculatetax method...");
		float cGSTtax = 0;
		float sGSTTtax = 0;
		float tax=0;
		
		if(kiranaObjList.get(0).getcGST() != null && !kiranaObjList.get(0).getcGST().equalsIgnoreCase("")) {
			cGSTtax = eachItemprice * Float.parseFloat(kiranaObjList.get(0).getcGST());
			cGSTtax=cGSTtax/100;
		}
		if(kiranaObjList.get(0).getsGST() != null && !kiranaObjList.get(0).getsGST().equalsIgnoreCase("")) {
			sGSTTtax = eachItemprice * Float.parseFloat(kiranaObjList.get(0).getsGST());
			sGSTTtax=sGSTTtax/100;
		}
		tax=cGSTtax + sGSTTtax;
		
		return tax;
	}
	
	private float calculateDiscount(float eachItemprice, List<KiranaObj> kiranaObjList)
	{
		RetailResponseSelectkirana.log.info("Inside the RetailResponseSelectkirana class of calculateDiscount method...");
		float discount=0;
		
		if(kiranaObjList.get(0).getOfferPercen() != null && !kiranaObjList.get(0).getOfferPercen().equalsIgnoreCase("")) {
			discount = eachItemprice * Float.parseFloat(kiranaObjList.get(0).getOfferPercen());
			discount= discount/100;
		}
		
		return discount;
	}
	
	static {
		log = LoggerFactory.getLogger((Class) RetailResponseSelectkirana.class);
	}
}
