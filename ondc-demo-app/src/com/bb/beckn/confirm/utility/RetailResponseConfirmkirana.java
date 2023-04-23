// 
// Decompiled by Procyon v0.5.36
// 

package com.bb.beckn.confirm.utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bb.beckn.ConnectionUtil;
import com.bb.beckn.api.model.common.Address;
import com.bb.beckn.api.model.common.Contact;
import com.bb.beckn.api.model.common.Descriptor;
import com.bb.beckn.api.model.common.Fulfillment;
import com.bb.beckn.api.model.common.Item;
import com.bb.beckn.api.model.common.ItemQuantities;
import com.bb.beckn.api.model.common.ItemQuantity;
import com.bb.beckn.api.model.common.Location;
import com.bb.beckn.api.model.common.Quotation;
import com.bb.beckn.api.model.common.QuotationBreakUp;
import com.bb.beckn.api.model.common.SettlementDetails;
import com.bb.beckn.api.model.common.Start;
import com.bb.beckn.api.model.common.State;
import com.bb.beckn.api.model.common.Time;
import com.bb.beckn.api.model.common.TimeRange;
import com.bb.beckn.common.model.ConfigModel;
import com.bb.beckn.common.util.JsonUtil;
import com.bb.beckn.repository.BapFeeRepository;
import com.bb.beckn.repository.ConfirmAuditSellerRepository;
import com.bb.beckn.repository.ItemRepositoryKirana;
import com.bb.beckn.repository.KiranaRepository;
import com.bb.beckn.repository.LogisticFinderRepository;
import com.bb.beckn.search.model.ApiSellerObj;
import com.bb.beckn.search.model.BapFeeObj;
import com.bb.beckn.search.model.ConfirmAuditSellerObj;
import com.bb.beckn.search.model.ItemDetailsKirana;
import com.bb.beckn.search.model.KiranaObj;
import com.bb.beckn.search.model.LogisticBppObj;
import com.bb.beckn.search.utility.RetailResponseSearchkirana;
import com.bb.beckn.utility.RetailRequestConfirmLogistic;
import com.bb.beckn.confirm.extension.OnSchema;
import com.bb.beckn.confirm.extension.Schema;
import com.bb.beckn.fetchResult.controller.FetchResultControllerLogisticBuyer;
import com.bb.beckn.init.utility.RetailResponseInitkirana;
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
import com.bb.beckn.api.model.common.Document;
@Service
public class RetailResponseConfirmkirana {
	private static final Logger log;
	
	@Autowired
	RetailResponseConfirmfood mRetailResponseConfirmfood;
	@Autowired
	RetailRequestConfirmLogistic mRetailRequestConfirmLogistic;
	@Autowired
	private JsonUtil jsonUtil;
	@Autowired
	LogisticFinderRepository mLogisticFinderRepository;
	@Autowired
	BapFeeRepository mbapFeeRepository;
	@Autowired
	RetailResponseSearchkirana retailResponseSearchkirana;
	@Autowired
	ItemRepositoryKirana mItemRepositoryKirana;
	@Autowired
	KiranaRepository mKiranaRepository;
	@Autowired
	FetchResultControllerLogisticBuyer mfetchResultControllerLogisticBuyer;
	@Autowired
    ConfirmAuditSellerRepository mConfirmAuditSellerRepository;
	
	Provider provider = null;
	Location location_Provider =null;
	Address address_Provider =null;
	List<Location> location_list =null;
	List<KiranaObj> kiranaObjList =null;
	List<Item> item_list =null;
	String search_vendorid="";
	Item item =null;
	ItemQuantity itemQuantity =null;
	Fulfillment fulfillment = null;
	List<Fulfillment> fulfillment_list =null;
	QuotationBreakUp quotationBreakUp =null;
	QuotationBreakUp quotationBreakUptax=null;
	QuotationBreakUp quotationBreakUpdiscount=null;
	List<QuotationBreakUp> quotationBreakUp_list =null;
	Price itemPrice =null;
	Price itemPricetax=null;
	Price itemPricediscount=null;
	List<BapFeeObj> bapFeeObjObjList =null;
	List<SettlementDetails> settlementDetailsObjList =null;
	SettlementDetails settlementDetails =null;
	Document orderDocument =null;
	List<Document> orderDocumentObjList =null;
	State fulfillmentState =null;
	Descriptor	descriptor =null;
	String providerPhone="";
	String providerEmail="";
	Start fulfillmentstart= null;
	Location startLocation =null;
	String logisticProvidername="";
	Time time = null;
	TimeRange providerTimeRange = null;
	ItemDetailsKirana ItemDetailsKiranaObj=null;
	
	public OnSchema fetchResult(final Schema request, final ConfigModel	configModel, String host, OnSchema respBody,List itemDetailsKirana,String strTosearchinDomain) throws SQLException, IOException, NumberFormatException, JSONException {

		RetailResponseConfirmkirana.log.info("Inside the RetailResponseConfirmkirana class...");
		fulfillment_list = new ArrayList<Fulfillment>();
		
		respBody.getMessage().setOrder(request.getMessage().getOrder());
		if(request.getMessage().getOrder()!=null && request.getMessage().getOrder().getState().equalsIgnoreCase("Created")) {
			respBody.getMessage().getOrder().setState("Accepted");
		}
		
		//respBody.getMessage().getOrder().getProvider().setRateable(true);
		createProviderStructure( request, respBody, configModel);  
		createItemStructure( request,respBody);
		
		String strtransactionid =request.getContext().getTransactionId()+"Logistic";		
		String strmessageid =request.getContext().getMessageId()+"Logistic";	
		
		List<LogisticBppObj> logisticBppObj_list= this.mLogisticFinderRepository.findBytransaction_idandaction(strtransactionid, "on_search");
		try {
		 for(int i=0; i < logisticBppObj_list.size(); i++) {
			 logisticProvidername=  logisticBppObj_list.get(i).getProvidername();
				/*
				 * final com.bb.beckn.search.extension.OnSchema model =
				 * (com.bb.beckn.search.extension.OnSchema)this.jsonUtil.toModel(
				 * logisticBppObj_list.get(i).getJson(),
				 * (Class)com.bb.beckn.search.extension.OnSchema.class); for(int j=0; j <
				 * model.getMessage().getCatalog().getBppProviders().size(); j++) {
				 * if(model.getMessage().getCatalog().getBppProviders().get(j).getId().
				 * equalsIgnoreCase(logisticBppObj_list.get(i).getProviderid())) {
				 * logisticProvidername=
				 * model.getMessage().getCatalog().getBppProviders().get(j).getDescriptor().
				 * getName(); } }
				 */
		 }
		}catch(Exception e) {
			System.out.println("exception - "+e.getMessage() );
		}
		 
		for(int j=0; j< request.getMessage().getOrder().getFulfillments().size(); j++) {
			fulfillment = new Fulfillment();
			fulfillmentState = new State();
			descriptor = new Descriptor();
			fulfillmentstart = new Start();
			startLocation = new Location();
       	    
			if(request.getMessage().getOrder().getFulfillments().get(j).getId()!=null) {
				fulfillment.setId(request.getMessage().getOrder().getFulfillments().get(j).getId());
			}
			if(request.getMessage().getOrder().getFulfillments().get(j).getType()!=null) {
				fulfillment.setType(request.getMessage().getOrder().getFulfillments().get(j).getType());
			}
			fulfillment.setTracking(Boolean.valueOf(configModel.getTracking()));
			fulfillment.setProviderName(logisticProvidername);
			if(respBody.getMessage().getOrder().getState().equalsIgnoreCase("Accepted")) {
				descriptor.setCode("Pending");
				fulfillmentState.setDescriptor(descriptor);
				fulfillment.setState(fulfillmentState);				
		    }
			
			if(request.getMessage().getOrder().getFulfillments().get(j).getEnd()!=null) {
				fulfillment.setEnd(request.getMessage().getOrder().getFulfillments().get(j).getEnd());
			}
			startLocation.setGps(respBody.getMessage().getOrder().getProvider().getLocations().get(0).getGps());
			startLocation.setAddress(respBody.getMessage().getOrder().getProvider().getLocations().get(0).getAddress());
			startLocation.setDescriptor(new Descriptor() );
			startLocation.getDescriptor().setName(respBody.getMessage().getOrder().getProvider().getDescriptor().getName());
			startLocation.setId(respBody.getMessage().getOrder().getProvider().getId());
			fulfillmentstart.setLocation(startLocation);
			fulfillmentstart.setContact(new Contact());
			fulfillmentstart.getContact().setPhone(providerPhone);
			fulfillmentstart.getContact().setEmail(providerEmail);
			
			
			//time = new Time();
       	    //providerTimeRange = new TimeRange();
       	   // providerTimeRange.setStart("TBD");
	       // providerTimeRange.setEnd("TBD");
	        //time.setRange(providerTimeRange);
	        //fulfillmentstart.setTime(time);
	        
			fulfillment.setStart(fulfillmentstart);
			fulfillment_list.add(fulfillment);			
		}		
		respBody.getMessage().getOrder().setFulfillments(fulfillment_list);
		
		respBody.getMessage().getOrder().getFulfillments().get(0).setRateable(Boolean.valueOf(configModel.getRateable()));
		respBody.getMessage().getOrder().getFulfillments().get(0).setTracking(Boolean.valueOf(configModel.getTracking()));
		
		respBody.getMessage().getOrder().setBilling(request.getMessage().getOrder().getBilling()); 
		this.mRetailRequestConfirmLogistic.creatingLogisticconfirmRequestStructureKirana(request, respBody,strtransactionid, strmessageid, logisticBppObj_list, item_list, providerPhone,itemDetailsKirana,providerEmail);
				
		settlementDetails = new SettlementDetails(); 
		bapFeeObjObjList =new ArrayList<BapFeeObj>(); 
		quotationBreakUp_list = new	ArrayList<QuotationBreakUp>(); 
		itemPrice = new Price();
		itemQuantity = new	ItemQuantity();
		float totalitemprice = 0; 
		float eachItemprice = 0;

		if (respBody.getMessage().getOrder() == null)
		{
			respBody.getMessage().setOrder(new Order()); 
		} 
		if(respBody.getMessage().getOrder().getQuote() == null) 
		{
			respBody.getMessage().getOrder().setQuote(new Quotation()); 
				
		} if(respBody.getMessage().getOrder().getQuote().getBreakup() == null)
		{
			respBody.getMessage().getOrder().getQuote().setBreakup(quotationBreakUp_list); 
		} 
		if (respBody.getMessage().getOrder().getQuote().getPrice() == null) 
		{
			respBody.getMessage().getOrder().getQuote().setPrice(new Price()); 
		} 
		//quote	breakup 
        for (int i = 0; i < itemDetailsKirana.size(); i++) {
			
			float eachItempriceTax = 0; 
			float eachItempricediscont = 0; 
			
			quotationBreakUp = new	QuotationBreakUp(); 
			itemPrice = new Price(); 
			String itemID =	request.getMessage().getOrder().getItems().get(i).getId();
			item = new Item();
			try {
                
				ItemDetailsKirana ItemDetailsKiranaObj =(ItemDetailsKirana)itemDetailsKirana.get(i);
				
				//ItemDetailsKiranaObj =mItemRepositoryKirana.findByItemid(Long.parseLong(itemID));
				quotationBreakUp.setItemId(itemID);

				itemQuantity.setCount(request.getMessage().getOrder().getItems().get(i).getQuantity().getCount()); 
				quotationBreakUp.setItemQuantity(itemQuantity);
				quotationBreakUp.setTitleType("item");
				quotationBreakUp.setTitle(ItemDetailsKiranaObj.getItemname());

				item.setPrice(new Price()); 
				item.getPrice().setCurrency("INR");
				item.getPrice().setValue(ItemDetailsKiranaObj.getItemprice());
				//item.setQuantity(new ItemQuantity());
				//item.getQuantity().setAvailable(new ItemQuantities());
				//item.getQuantity().setMaximum(new ItemQuantities());  
				//item.getQuantity().getAvailable().setCount(20);
				//item.getQuantity().getMaximum().setCount(20); 
				quotationBreakUp.setItem(item);

				eachItemprice = Float.parseFloat(ItemDetailsKiranaObj.getItemprice())*	request.getMessage().getOrder().getItems().get(i).getQuantity().getCount();
				totalitemprice = totalitemprice + eachItemprice;

				//eachItemweight=Integer.parseInt(ItemDetailsKiranaObj.getItemquantity()) *	request.getMessage().getOrder().getItems().get(i).getQuantity().getCount();
				//totalitemweight= totalitemweight + eachItemweight;

				itemPrice.setCurrency("INR");
				//itemPrice.setValue(Integer.toString(eachItemprice));
				quotationBreakUp.setPrice(itemPrice);
				quotationBreakUp_list.add(quotationBreakUp);
				
                eachItempriceTax= calculatetax(eachItemprice, kiranaObjList);
                System.out.println("eachItempriceTax-- "+ eachItempriceTax);
                
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
                //System.out.println("Integer.parseInt(itemPricetax.getValue() -" + Integer.parseInt(itemPricetax.getValue()));
				//totalitemprice = totalitemprice + Integer.parseInt(itemPricetax.getValue());
				
				totalitemprice = totalitemprice + eachItempriceTax;
				//discount
				eachItempricediscont= calculateDiscount(eachItemprice, kiranaObjList);
				System.out.println("eachItempricediscont-- "+ eachItempricediscont);
				
				quotationBreakUpdiscount = new QuotationBreakUp();
				quotationBreakUpdiscount.setTitleType("discount");
				quotationBreakUpdiscount.setTitle("Discount"); 
				itemPricediscount = new	Price(); 
				itemPricediscount.setCurrency("INR");
				itemPricediscount.setValue(String.valueOf(eachItempricediscont));
				quotationBreakUpdiscount.setPrice(itemPricediscount);
				quotationBreakUpdiscount.setItemId(itemID);
				quotationBreakUp_list.add(quotationBreakUpdiscount);
				//System.out.println("Integer.parseInt(itemPricetax.getValue() -" + Integer.parseInt(itemPricediscount.getValue()));
				totalitemprice = totalitemprice - eachItempricediscont;

				respBody.getMessage().getOrder().getItems().get(i).setReturnable(Boolean.valueOf(configModel.getReturnable()));
				respBody.getMessage().getOrder().getItems().get(i).setCancellable(Boolean.valueOf(configModel.getCancellable()));

				} catch (Exception e) 
				{ 
					System.out.println("Exception occurs ----------" +	e); 
					break; 
				}
		    }

		quotationBreakUp = new QuotationBreakUp();
		itemPrice = new Price();
		//delivery charges
		quotationBreakUp.setTitleType("Delivery");
		quotationBreakUp.setTitle("Delivery Charges"); 
		itemPrice.setCurrency("INR");
		itemPrice.setValue("10"); 
		quotationBreakUp.setPrice(itemPrice);
		quotationBreakUp_list.add(quotationBreakUp); 
		totalitemprice = totalitemprice	+ Float.parseFloat(itemPrice.getValue()); 
		// packaging charges
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
		//misc charges 
		quotationBreakUp =new QuotationBreakUp(); 
		itemPrice = new Price();
		quotationBreakUp.setTitleType("misc");
		quotationBreakUp.setTitle("Convenience Fee");
		itemPrice.setCurrency("INR");
		itemPrice.setValue("0"); 
		quotationBreakUp.setPrice(itemPrice);
		quotationBreakUp_list.add(quotationBreakUp);
		totalitemprice = totalitemprice	+ Float.parseFloat(itemPrice.getValue());

		quotationBreakUp_list.add(quotationBreakUp); 
		totalitemprice = totalitemprice + Float.parseFloat(itemPrice.getValue());
        
		String ttl= ""+Duration.ofMinutes(Long.parseLong(configModel.getBecknTtl()));
		
		respBody.getMessage().getOrder().getQuote().setTtl(ttl);
		respBody.getMessage().getOrder().getQuote().getPrice().setCurrency("INR");
		System.out.println("totalitemprice --"+totalitemprice );
		respBody.getMessage().getOrder().getQuote().getPrice().setValue(Float.toString(totalitemprice)); 
		
		Optional<BapFeeObj> bapFeeObj=	mbapFeeRepository.findBySubscriberIdItemid(request.getContext().getBapId());

		//bapFeeObjObjList =bapFeeObj.map(Collections::singletonList).orElse(Collections.emptyList());
		settlementDetailsObjList = new ArrayList<SettlementDetails>(0);
		if(request.getMessage().getOrder().getPayment().getStatus().equalsIgnoreCase("PAID")) {
			
			settlementDetails.setSettlementCounterparty(configModel.getSettlementCounterparty());
			settlementDetails.setSettlementPhase(configModel.getSettlementPhase());
			settlementDetails.setSettlementType(configModel.getSettlementType().trim());
			settlementDetails.setBeneficiaryAddress(configModel.getBeneficiaryName());
			settlementDetails.setUpiAddress(configModel.getUpiAddress());
			settlementDetailsObjList.add(settlementDetails);
		}else {
			settlementDetails.setSettlementCounterparty(request.getMessage().getOrder().getPayment().getSettlementDetails().get(0).getSettlementCounterparty());
			settlementDetails.setSettlementPhase(request.getMessage().getOrder().getPayment().getSettlementDetails().get(0).getSettlementPhase());
			settlementDetails.setSettlementType(request.getMessage().getOrder().getPayment().getSettlementDetails().get(0).getSettlementType());
			settlementDetails.setBeneficiaryAddress(request.getMessage().getOrder().getPayment().getSettlementDetails().get(0).getBeneficiaryAddress());
			settlementDetails.setUpiAddress(request.getMessage().getOrder().getPayment().getSettlementDetails().get(0).getUpiAddress());
			settlementDetailsObjList.add(settlementDetails);
		}
		respBody.getMessage().getOrder().getPayment().setSettlementDetails(settlementDetailsObjList);
		respBody.getMessage().getOrder().getPayment().setBuyerAppFinderFeeType(bapFeeObj.get().getType());
		respBody.getMessage().getOrder().getPayment().setBuyerAppFinderFeeAmount(bapFeeObj.get().getFinder_fee_amount());
		
		//orderDocument =new Document();
		//orderDocumentObjList = new ArrayList<Document>(); 
		//orderDocument.setUrl("TBD");
		//orderDocument.setLabel("TBD");
		//orderDocumentObjList.add(orderDocument);
		//respBody.getMessage().getOrder().setDocuments(orderDocumentObjList);
		respBody.getMessage().getOrder().setCreatedAt(request.getMessage().getOrder().getCreatedAt());
		respBody.getMessage().getOrder().setUpdatedAt(request.getMessage().getOrder().getUpdatedAt());
		
		List<ApiSellerObj> apiLogisticSellerrObj_list = this.mfetchResultControllerLogisticBuyer.fetch(strtransactionid, strmessageid);
		com.bb.beckn.confirm.extension.OnSchema modeltosave=null;
		for(int i=0; i < apiLogisticSellerrObj_list.size(); i++) {
			try{
			modeltosave  = (com.bb.beckn.confirm.extension.OnSchema)this.jsonUtil.toModel(apiLogisticSellerrObj_list.get(i).getJson(), (Class)com.bb.beckn.confirm.extension.OnSchema.class);
			setLogisticProvider( modeltosave, strtransactionid,  strmessageid,logisticProvidername, strTosearchinDomain, modeltosave.getContext().getBppId(), modeltosave.getContext().getBppUri(),respBody);
			}catch(Exception e) {
				System.out.println(e.getMessage());
			}
		}

		///
		for(int j=0; j< request.getMessage().getOrder().getFulfillments().size(); j++) {
			if(request.getMessage().getOrder().getFulfillments().get(j).getStart()!=null) {
				time = new Time();
	       	    providerTimeRange = new TimeRange();
	       	    
				providerTimeRange.setStart(modeltosave.getMessage().getOrder().getFulfillments().get(j).getStart().getTime().getRange().getStart());
		        providerTimeRange.setEnd(modeltosave.getMessage().getOrder().getFulfillments().get(j).getStart().getTime().getRange().getEnd());
		        time.setRange(providerTimeRange);
				fulfillment.getEnd().setTime(time);
			}
			if(request.getMessage().getOrder().getFulfillments().get(j).getEnd()!=null) {
				time = new Time();
	       	    providerTimeRange = new TimeRange();
	       	    
				providerTimeRange.setStart(modeltosave.getMessage().getOrder().getFulfillments().get(j).getEnd().getTime().getRange().getStart());
		        providerTimeRange.setEnd(modeltosave.getMessage().getOrder().getFulfillments().get(j).getEnd().getTime().getRange().getEnd());
		        time.setRange(providerTimeRange);
				fulfillment.getStart().setTime(time);
			}
		}
		
		
		///set the time range
				
		return respBody;

	} 
	private void setLogisticProvider(com.bb.beckn.confirm.extension.OnSchema model,String strtransactionid, String strmessageid,String strProviderName, String strTosearchinDomain, String logisticBppid, String logisticBppurl, com.bb.beckn.confirm.extension.OnSchema respBody) {    	
		RetailResponseConfirmkirana.log.info("Inside the RetailResponseInitkirana class of setLogisticProvider method...");
    	try {
    		String querySTRUpdate="";
    		Timestamp timestamp = new Timestamp(System.currentTimeMillis());   
    		LogisticBppObj myapiLogisticBppObj = new LogisticBppObj(
    				strProviderName, 
    				model.getContext().getBppId(),model.getContext().getBppUri(),timestamp.toString(),"",this.jsonUtil.toJson((Object)model), strtransactionid, strmessageid, model.getMessage().getOrder().getProvider().getId(),model.getMessage().getOrder().getQuote().getPrice().getValue(),
    				model.getMessage().getOrder().getItems().get(0).getCategoryId(), model.getContext().getAction(),"");
    		
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
    	        querySTRUpdate= "update bornbhukkad.ondc_bpp_logistic_finder baf set baf.providername ='" +""+
    	        		         "', baf.bppid ='"+model.getContext().getBppId()+"' , baf.bppurl='"+model.getContext().getBppUri()+ "' , baf.updated_on='"+timestamp.toString()+
    	        		         "' , baf.json='"+this.jsonUtil.toJson((Object)model) + "', baf.transaction_id ='"+strtransactionid+ "', baf.message_id ='"+strmessageid+
    	        		        "' where baf.bppid ='"+ model.getContext().getBppId()  +"' and baf.action='"+ model.getContext().getAction()+"' "
    	        		        		+ " and baf.transaction_id ='" +strtransactionid+ "';";
    	        resultset = stmUpdate.executeUpdate(querySTRUpdate);
    	        System.out.println("data updated--LogisticBppObj  ");
    	        stmUpdate.close(); 
    		}
    		
    		/// Data Inserted in ondc_audit_transaction
         	String paymenttype ="";
         	String paymentcollectedby="";
         	String amountatconfirmation="";
         	String logisticprovidername="";
         	String logisticdeliverycharge="";
         	if(respBody.getMessage().getOrder().getPayment().getStatus().equalsIgnoreCase("Paid")) {
         		paymenttype="Prepaid";
         		paymentcollectedby="Buyer";
         	}else if(respBody.getMessage().getOrder().getPayment().getStatus().equalsIgnoreCase("NOT-PAID")){
         		paymenttype="CoD";
         		paymentcollectedby="seller";
         	}
         	amountatconfirmation=respBody.getMessage().getOrder().getQuote().getPrice().getValue();
         	logisticprovidername= respBody.getMessage().getOrder().getFulfillments().get(0).getProviderName();
         	
         	for(int i=0; i < respBody.getMessage().getOrder().getQuote().getBreakup().size(); i++) {
         		if(respBody.getMessage().getOrder().getQuote().getBreakup().get(i).getTitle().equalsIgnoreCase("Delivery charges")) {
         			logisticdeliverycharge=respBody.getMessage().getOrder().getQuote().getBreakup().get(i).getPrice().getValue();
         		}
         	}
         	
         	ConfirmAuditSellerObj myconfirmAuditSellerObj = new ConfirmAuditSellerObj(respBody.getMessage().getOrder().getId(), respBody.getContext().getTransactionId(), respBody.getMessage().getOrder().getState(),model.getMessage().getOrder().getState(),
         			paymenttype,  paymentcollectedby, amountatconfirmation, logisticprovidername,model.getMessage().getOrder().getProvider().getId(),logisticdeliverycharge,model.getMessage().getOrder().getItems().get(0).getCategoryId(),"","","","","","","","","","","","",
         			timestamp.toString(), "",respBody.getMessage().getOrder().getPayment().getBuyerAppFinderFeeAmount(), respBody.getMessage().getOrder().getPayment().getBuyerAppFinderFeeType(), strTosearchinDomain,logisticBppid,logisticBppurl);
         	
         	mConfirmAuditSellerRepository.saveAndFlush(myconfirmAuditSellerObj);
     		
     		System.out.println("Data Inserted in ondc_audit_seller_transaction "); 
    	        
		}catch(Exception e) {
			System.out.println("Exception--"+ e);
		}
		
	 }
	private void createProviderStructure(final Schema request, OnSchema	respBody, final ConfigModel	configModel) throws NumberFormatException, ClientProtocolException, IOException,JSONException {
		RetailResponseConfirmkirana.log.info("Inside the RetailResponseInitkirana class of createProviderStructure method...");
		provider = new Provider(); 
		location_Provider = new Location();
		address_Provider = new Address(); 
		location_list = new ArrayList<Location>();
		kiranaObjList =new ArrayList<KiranaObj>(); 
		search_vendorid =request.getMessage().getOrder().getProvider().getId();

		Optional<KiranaObj> kiranaObjopt=mKiranaRepository.findBydata(Long.parseLong(search_vendorid)); 
		kiranaObjList=	kiranaObjopt.map(Collections::singletonList).orElse(Collections.emptyList());

				for (int i=0; i< kiranaObjList.size(); i++) {
					provider.setId((kiranaObjList.get(i).getId()).toString()); 
					descriptor = new Descriptor();
					descriptor.setName((kiranaObjList.get(i).getStorename()).trim());
					provider.setDescriptor(descriptor);

					this.retailResponseSearchkirana.getStringFromLocation(Double.parseDouble(kiranaObjList.get(i).getLatitude()),
							Double.parseDouble(kiranaObjList.get(i).getLongitude()), address_Provider);

					location_Provider.setAddress(address_Provider);
					location_Provider.setId((kiranaObjList.get(i).getId()).toString());

					String latitude=kiranaObjList.get(i).getLatitude(); String
					longitude=kiranaObjList.get(i).getLongitude();
					String gps=latitude +","+longitude; 
					location_Provider.setGps(gps);

					location_list.add(location_Provider);
					provider.setLocations(location_list);
					provider.setRateable(Boolean.valueOf(configModel.getRateable()));
					providerPhone=kiranaObjList.get(i).getPhone();
					providerEmail=kiranaObjList.get(i).getEmail();
					respBody.getMessage().getOrder().setProvider(provider); 
				}

	}

	private void createItemStructure(final Schema request, OnSchema respBody)	throws NumberFormatException, ClientProtocolException, IOException,	JSONException {
		RetailResponseConfirmkirana.log.info("Inside the RetailResponseInitkirana class of createItemStructure method...");
		/*
		 * int itemSize= request.getMessage().getOrder().getItems().size(); item_list =
		 * new ArrayList<Item>();
		 * 
		 * for (int i =0; i<itemSize; i++) { item = new Item();
		 * 
		 * itemQuantity =new ItemQuantity();
		 * 
		 * ItemDetailsKiranaObj =
		 * mItemRepositoryKirana.findByItemidandvendorid(Long.parseLong(request.
		 * getMessage().getOrder().getItems().get(i).getId()),
		 * request.getMessage().getOrder().getProvider().getId());
		 * 
		 * item.setId((ItemDetailsKiranaObj.getId()).toString());
		 * itemQuantity.setCount(request.getMessage().getOrder().getItems().get(i).
		 * getQuantity().getCount()); item.setQuantity(itemQuantity);
		 * item.setFulfillmentId(request.getMessage().getOrder().getFulfillments().get(0
		 * ).getId()); //item.setReturnable(true); //item.setCancellable(true);
		 * item_list.add(item); }
		 */
		//respBody.getMessage().getOrder().setItems(item_list);
		respBody.getMessage().getOrder().setItems(request.getMessage().getOrder().getItems());

	}

	private float calculatetax(float eachItemprice, List<KiranaObj> kiranaObjList)
	{
		RetailResponseConfirmkirana.log.info("Inside the RetailResponseInitkirana class of calculatetax method...");
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
		RetailResponseConfirmkirana.log.info("Inside the RetailResponseInitkirana class of calculateDiscount method...");
		float discount=0;
		
		if(kiranaObjList.get(0).getOfferPercen() != null && !kiranaObjList.get(0).getOfferPercen().equalsIgnoreCase("")) {
			discount = eachItemprice * Float.parseFloat(kiranaObjList.get(0).getOfferPercen());
			discount= discount/100;
		}
		
		return discount;
	}
	/*
	 * public OnSchema fetchResult(final Schema request, final ConfigModel
	 * configModel, String host, OnSchema respBody) throws SQLException,
	 * IOException, NumberFormatException, JSONException {
	 * RetailResponseConfirmkirana.log.
	 * info("Inside the RetailResponseConfirmkirana class...");
	 * 
	 * // Validating list of items for particlar select in DB
	 * 
	 * List<Item> item_list = new ArrayList<Item>(); List<QuotationBreakUp>
	 * quotationBreakUp_list = new ArrayList<QuotationBreakUp>(); QuotationBreakUp
	 * quotationBreakUp = new QuotationBreakUp(); item_list =
	 * request.getMessage().getOrder().getItems(); Price itemPrice = new Price();
	 * ItemQuantity itemQuantity = new ItemQuantity();
	 * 
	 * int totalitemprice = 0; int eachItemprice = 0;
	 * 
	 * if (respBody.getMessage().getOrder() == null) {
	 * respBody.getMessage().setOrder(new Order()); } if
	 * (respBody.getMessage().getOrder().getQuote() == null) {
	 * respBody.getMessage().getOrder().setQuote(new Quotation()); } if
	 * (respBody.getMessage().getOrder().getQuote().getBreakup() == null) {
	 * respBody.getMessage().getOrder().getQuote().setBreakup(quotationBreakUp_list)
	 * ; } if (respBody.getMessage().getOrder().getQuote().getPrice() == null) {
	 * respBody.getMessage().getOrder().getQuote().setPrice(new Price()); }
	 * 
	 * for (int i = 0; i < item_list.size(); i++) { quotationBreakUp = new
	 * QuotationBreakUp(); itemPrice = new Price(); String itemID =
	 * request.getMessage().getOrder().getItems().get(i).getId();
	 * 
	 * try {
	 * 
	 * ItemDetailsKirana ItemDetailsKiranaObj =
	 * mItemRepositoryKirana.findByItemid(Long.parseLong(itemID));
	 * quotationBreakUp.setItemId(itemID);
	 * 
	 * itemQuantity.setCount(request.getMessage().getOrder().getItems().get(i).
	 * getQuantity().getCount()); quotationBreakUp.setItemQuantity(itemQuantity);
	 * quotationBreakUp.setTitleType(ItemDetailsKiranaObj.getItemcategory());
	 * quotationBreakUp.setTitle(ItemDetailsKiranaObj.getItemname());
	 * 
	 * eachItemprice = Integer.parseInt(ItemDetailsKiranaObj.getItemprice())
	 * request.getMessage().getOrder().getItems().get(i).getQuantity().getCount();
	 * totalitemprice = totalitemprice + eachItemprice;
	 * itemPrice.setCurrency("INR");
	 * itemPrice.setValue(Integer.toString(eachItemprice));
	 * quotationBreakUp.setPrice(itemPrice);
	 * quotationBreakUp_list.add(quotationBreakUp);
	 * respBody.getMessage().getOrder().getItems().get(i).setReturnable(false);
	 * respBody.getMessage().getOrder().getItems().get(i).setCancellable(true);
	 * 
	 * } catch (Exception e) { System.out.println("Exception occurs ----------" +
	 * e); break; }
	 * 
	 * }
	 * 
	 * quotationBreakUp = new QuotationBreakUp(); itemPrice = new Price();
	 * 
	 * quotationBreakUp.setTitleType("Delivery");
	 * quotationBreakUp.setTitle("Delivery Charges"); itemPrice.setCurrency("INR");
	 * itemPrice.setValue("10"); quotationBreakUp.setPrice(itemPrice);
	 * quotationBreakUp_list.add(quotationBreakUp);
	 * 
	 * respBody.getMessage().getOrder().getQuote().setTtl("PT10M");
	 * respBody.getMessage().getOrder().getQuote().getPrice().setCurrency("INR");
	 * totalitemprice = totalitemprice + 10;
	 * respBody.getMessage().getOrder().getQuote().getPrice().setValue(Integer.
	 * toString(totalitemprice)); respBody.getMessage().getOrder().getFulfillment().
	 * setCategory("Immediate Delivery");
	 * respBody.getMessage().getOrder().getFulfillment().setTat("PT2H");
	 * respBody.getMessage().getOrder().getFulfillment().setTracking(Boolean.valueOf(configModel.getTracking())); return
	 * respBody; }
	 */


	static {
		log = LoggerFactory.getLogger((Class) RetailResponseConfirmkirana.class);
	}
}
