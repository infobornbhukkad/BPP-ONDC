// 
// Decompiled by Procyon v0.5.36
// 

package com.bb.beckn.init.utility;

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
import com.bb.beckn.api.model.common.Scalar;
import com.bb.beckn.api.model.common.SettlementDetails;
import com.bb.beckn.api.model.common.Start;
import com.bb.beckn.api.model.common.State;
import com.bb.beckn.common.model.ConfigModel;
import com.bb.beckn.common.util.JsonUtil;
import com.bb.beckn.fetchResult.controller.FetchResultControllerLogisticBuyer;
import com.bb.beckn.repository.ApiAuditSellerRepository;
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
import com.bb.beckn.select.utility.RetailResponseSelectkirana;
import com.bb.beckn.utility.RetailRequestInitLogistic;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsonschema.JsonSchema;

import com.bb.beckn.init.extension.OnSchema;
import com.bb.beckn.init.extension.Schema;
import com.bb.beckn.api.model.common.Order;
import com.bb.beckn.api.model.common.Payment;
import com.bb.beckn.api.model.common.Price;
import com.bb.beckn.api.model.common.Provider;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class RetailResponseInitkirana {
	private static final Logger log;
	@Autowired
	LogisticFinderRepository mLogisticFinderRepository;
	@Autowired
	BapFeeRepository mbapFeeRepository;
	@Autowired
	RetailResponseSearchkirana retailResponseSearchkirana;
	@Autowired
	KiranaRepository mKiranaRepository;
	@Autowired
	ItemRepositoryKirana mItemRepositoryKirana;
	@Autowired
	private JsonUtil jsonUtil;
	@Autowired
	RetailRequestInitLogistic mRetailRequestInitLogistic;
	@Autowired
	FetchResultControllerLogisticBuyer mfetchResultControllerLogisticBuyer;
	
	Provider provider = null;
	Location location_Provider =null;
	Address address_Provider =null;
	List<Location> location_list =null;
	List<KiranaObj> kiranaObjList =null;
	String search_vendorid="";
	List<Item> item_list =null;
	Item item =null;
	ItemQuantity itemQuantity =null;
	List<QuotationBreakUp> quotationBreakUp_list =null;
	QuotationBreakUp quotationBreakUp =null;
	QuotationBreakUp quotationBreakUptax=null;
	QuotationBreakUp quotationBreakUpdiscount=null;
	State fulfilmentState =null;
	Descriptor fulfilmentDescriptor =null;
	Price itemPrice =null;
	Price itemPricetax=null;
	Price itemPricediscount=null;
	List<BapFeeObj> bapFeeObjObjList =null;
	List<SettlementDetails> settlementDetailsObjList =null;
	SettlementDetails settlementDetails =null;
	Fulfillment fulfillment = null;
	List<Fulfillment> fulfillment_list =null;


	public OnSchema fetchResult(final Schema request, final ConfigModel	configModel, String host, OnSchema respBody, List itemDetailsKirana) throws SQLException,IOException, NumberFormatException, JSONException {
		RetailResponseInitkirana.log.info("Inside the RetailResponseInitkirana class...");
        
		fulfillment_list = new ArrayList<Fulfillment>();	
		
		respBody.getMessage().getOrder().setBilling(request.getMessage().getOrder().getBilling()); 
		respBody.getMessage().getOrder().getBilling().setCreatedAt(Instant.now().toString());
		respBody.getMessage().getOrder().getBilling().setUpdatedAt(Instant.now().toString());
		
		fulfillment = new Fulfillment();
		for(int i =0; i < request.getMessage().getOrder().getFulfillments().size(); i++) {
			fulfillment_list.add(request.getMessage().getOrder().getFulfillments().get(i));
			//respBody.getMessage().getOrder().getFulfillments().get(i).setRateable(Boolean.valueOf(configModel.getRateable()));
			//respBody.getMessage().getOrder().getFulfillments().get(i).setTracking(Boolean.valueOf(configModel.getTracking()));
		}
		
		respBody.getMessage().getOrder().setFulfillments(fulfillment_list);
		createProviderStructure( request, respBody, configModel); 
		createItemStructure( request,respBody, configModel);
		//respBody.getMessage().getOrder().getFulfillments().get(0).setRateable(Boolean.valueOf(configModel.getRateable()));
		//respBody.getMessage().getOrder().getFulfillments().get(0).setTracking(Boolean.valueOf(configModel.getTracking()));
				
		settlementDetails = new SettlementDetails(); 
		bapFeeObjObjList =new ArrayList<BapFeeObj>(); 
		quotationBreakUp_list = new	ArrayList<QuotationBreakUp>(); 
		itemPrice = new Price();
		itemQuantity = new	ItemQuantity();
		
		float totalitemprice = 0; 
		float eachItemprice = 0;
		float totalitemweight = 0;
		
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

				//respBody.getMessage().getOrder().getItems().get(i).setReturnable(Boolean.valueOf(configModel.getReturnable()));
				//respBody.getMessage().getOrder().getItems().get(i).setCancellable(Boolean.valueOf(configModel.getCancellable()));

				} catch (Exception e) 
				{ 
					System.out.println("Exception occurs ----------" +	e); 
					break; 
				}
		    }


		
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

		//quotationBreakUp_list.add(quotationBreakUp); 
		//totalitemprice = totalitemprice + Float.parseFloat(itemPrice.getValue());
		String ttl= ""+Duration.ofMinutes(Long.parseLong(configModel.getBecknTtl()));
		respBody.getMessage().getOrder().getQuote().setTtl(ttl);
		respBody.getMessage().getOrder().getQuote().getPrice().setCurrency("INR");
		System.out.println("totalitemprice --"+totalitemprice );
		//respBody.getMessage().getOrder().getQuote().getPrice().setValue(Float.toString(totalitemprice));
		String strtransactionid =request.getContext().getTransactionId()+"Logistic";		
		String strmessageid =request.getContext().getMessageId()+"Logistic";
		
		System.out.println("strtransactionid --"+strtransactionid );
		System.out.println("strmessageid --"+strmessageid );
		 
		List<LogisticBppObj> logisticBppObj_list= this.mLogisticFinderRepository.findBytransaction_idandaction(strtransactionid, "on_search");
		
		String ordertodelivery = "";
		String strCaterogyIdToSave="";
		float deliveryCharge = 0;
		String strProviderName="";
		Duration duration=null;
        for(int i=0; i < logisticBppObj_list.size(); i++) {
			
				final com.bb.beckn.search.extension.OnSchema model = (com.bb.beckn.search.extension.OnSchema)this.jsonUtil.toModel(logisticBppObj_list.get(i).getJson(), (Class)com.bb.beckn.search.extension.OnSchema.class);
				for(int j=0; j < model.getMessage().getCatalog().getBppProviders().size(); j++) {
					if(model.getMessage().getCatalog().getBppProviders().get(j).getId().equalsIgnoreCase(logisticBppObj_list.get(i).getProviderid())) {
						strProviderName=model.getMessage().getCatalog().getBppProviders().get(j).getDescriptor().getName();
						strCaterogyIdToSave=logisticBppObj_list.get(i).getDeliverytype();
						respBody.getMessage().getOrder().getFulfillments().get(0).setCategory(model.getMessage().getCatalog().getBppProviders().get(j).getCategories().get(0).getId());
						respBody.getMessage().getOrder().getFulfillments().get(0).setProviderName(model.getMessage().getCatalog().getBppProviders().get(j).getDescriptor().getName());
						
						for(int k=0; k< model.getMessage().getCatalog().getBppProviders().get(j).getItems().size(); k++) {
							if(model.getMessage().getCatalog().getBppProviders().get(j).getItems().get(k).getParentItemId().equalsIgnoreCase("")) {
								ordertodelivery =  model.getMessage().getCatalog().getBppProviders().get(j).getItems().get(k).getTime().getDuration();
								deliveryCharge=Float.parseFloat(model.getMessage().getCatalog().getBppProviders().get(j).getItems().get(k).getPrice().getValue());
							}
						}
					}
				}
				
		duration = Duration.parse(ordertodelivery);
        }
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
		

		fulfilmentState = new State(); 
		fulfilmentDescriptor = new Descriptor();
		if(logisticBppObj_list.size() != 0 ) {
			fulfilmentDescriptor.setCode("Serviceable");
		}else {
			fulfilmentDescriptor.setCode("Non-serviceable");
			respBody.setError(new Scalar());
			
			respBody.getError().setType(" D O M AIN - E R R O R ");
			respBody.getError().setCode("30009");
		}
		
		fulfilmentState.setDescriptor(fulfilmentDescriptor);
		respBody.getMessage().getOrder().getFulfillments().get(0).setState(fulfilmentState);
		//delivery charges
		quotationBreakUp = new QuotationBreakUp();
		itemPrice = new Price();
		
		quotationBreakUp.setTitleType("Delivery");
		quotationBreakUp.setTitle("Delivery Charges"); 
		itemPrice.setCurrency("INR");
		itemPrice.setValue(String.valueOf(deliveryCharge)); 
		quotationBreakUp.setPrice(itemPrice);
		quotationBreakUp_list.add(quotationBreakUp); 
		totalitemprice = totalitemprice	+ deliveryCharge; 
		respBody.getMessage().getOrder().getQuote().getPrice().setValue(Float.toString(totalitemprice));
		respBody.getMessage().getOrder().setPayment(new	Payment());
		//respBody.getMessage().getOrder().getPayment().setSettlementDetails(new ArrayList<SettlementDetails>(0));

		Optional<BapFeeObj> bapFeeObj=	mbapFeeRepository.findBySubscriberIdItemid(request.getContext().getBapId());

		//bapFeeObjObjList =bapFeeObj.map(Collections::singletonList).orElse(Collections.emptyList());
		settlementDetailsObjList = new ArrayList<SettlementDetails>(0);
		settlementDetails.setSettlementCounterparty(configModel.getSettlementCounterparty());
		settlementDetails.setSettlementPhase(configModel.getSettlementPhase());
		settlementDetails.setSettlementType(configModel.getSettlementType());
		settlementDetails.setBeneficiaryAddress(configModel.getBeneficiaryName());
		settlementDetails.setUpiAddress(configModel.getUpiAddress());
		settlementDetails.setSettlementBankAccountNo(configModel.getSettlementbankaccNo());
		settlementDetails.setSettlementIfscCode(configModel.getSettlementifscCode());
		settlementDetails.setBankName(configModel.getBankName());
		settlementDetails.setBranchName(configModel.getBranchName());
		settlementDetails.setBeneficiaryName(configModel.getBeneficiaryName());
		settlementDetailsObjList.add(settlementDetails);
		respBody.getMessage().getOrder().getPayment().setSettlementDetails(settlementDetailsObjList);
		respBody.getMessage().getOrder().getPayment().setBuyerAppFinderFeeType(bapFeeObj.get().getType());
		respBody.getMessage().getOrder().getPayment().setBuyerAppFinderFeeAmount(bapFeeObj.get().getFinder_fee_amount());
         
		
		//init logistic call
		if(logisticBppObj_list.size() != 0 ) {		
			this.mRetailRequestInitLogistic.creatingLogisticinitRequestStructureKirana( request, respBody, logisticBppObj_list, strtransactionid, strmessageid);
			//end of init logistic call
			
			List<ApiSellerObj> apiLogisticSellerrObj_list = this.mfetchResultControllerLogisticBuyer.fetch(strtransactionid, strmessageid);
			for(int i=0; i < apiLogisticSellerrObj_list.size(); i++) {
				try{
				com.bb.beckn.init.extension.OnSchema modeltosave  = (com.bb.beckn.init.extension.OnSchema)this.jsonUtil.toModel(apiLogisticSellerrObj_list.get(i).getJson(), (Class)com.bb.beckn.init.extension.OnSchema.class);
				setLogisticProvider( modeltosave, strtransactionid,  strmessageid,strProviderName,ordertodelivery,strCaterogyIdToSave);
				}catch(Exception e) {
					System.out.println(e.getMessage());
				}
				
			}
		}
		
		return respBody;

	}
	 
	
	
	private void setLogisticProvider(com.bb.beckn.init.extension.OnSchema model,String strtransactionid, String strmessageid,String strProviderName,String ordertodelivery,String strCaterogyIdToSave) {    	
		RetailResponseInitkirana.log.info("Inside the RetailResponseInitkirana class of setLogisticProvider method...");
    	try {
    		String querySTRUpdate="";
    		Timestamp timestamp = new Timestamp(System.currentTimeMillis());   
    		LogisticBppObj myapiLogisticBppObj = new LogisticBppObj(
    				strProviderName, 
    				model.getContext().getBppId(),model.getContext().getBppUri(),timestamp.toString(),"",this.jsonUtil.toJson((Object)model), strtransactionid, strmessageid, model.getMessage().getOrder().getProvider().getId(),model.getMessage().getOrder().getQuote().getPrice().getValue(),
    				strCaterogyIdToSave, "on_init", ordertodelivery);
    		
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
    	        querySTRUpdate= "update bornbhukkad.ondc_bpp_logistic_finder baf set baf.providername ='" +strProviderName+
    	        		         "', baf.bppid ='"+model.getContext().getBppId()+"' , baf.bppurl='"+model.getContext().getBppUri()+ "',baf.deliverytype='"+strCaterogyIdToSave+"' , baf.updated_on='"+timestamp.toString()+"',baf.providerid='"+model.getMessage().getOrder().getProvider().getId()+"' ,baf.deliverycharge='"+model.getMessage().getOrder().getQuote().getPrice().getValue()+"' baf.ordertodelivery='"+ordertodelivery+                                    
    	        		         "' , baf.json='"+this.jsonUtil.toJson((Object)model) + "', baf.message_id ='"+strtransactionid+ "', baf.message_id ='"+strmessageid+
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
	
	private void createItemStructure(final Schema request, OnSchema respBody,  final ConfigModel configModel) throws NumberFormatException, ClientProtocolException, IOException,JSONException {
		RetailResponseInitkirana.log.info(" inside method of createItemStructure...");
		/*
		 * int itemSize= request.getMessage().getOrder().getItems().size(); item_list
		 * =new ArrayList<Item>();
		 * 
		 * for (int i =0; i<itemSize; i++) { item = new Item(); itemQuantity =new
		 * ItemQuantity(); ItemDetailsKirana
		 * itemDetailsKirana=mItemRepositoryKirana.findByItemidandvendorid(Long.
		 * parseLong(request.getMessage().getOrder().getItems().get(i).getId()),request.
		 * getMessage().getOrder().getProvider().getId());
		 * 
		 * item.setId((itemDetailsKirana.getId()).toString());
		 * itemQuantity.setCount(request.getMessage().getOrder().getItems().get(i).
		 * getQuantity().getCount()); item.setQuantity(itemQuantity);
		 * item.setFulfillmentId(request.getMessage().getOrder().getFulfillments().get(0
		 * ).getId()); item.setReturnable(Boolean.valueOf(configModel.getReturnable()));
		 * item.setCancellable(Boolean.valueOf(configModel.getCancellable()));
		 * item_list.add(item); }
		 */
		
		//respBody.getMessage().getOrder().setItems(item_list);
		respBody.getMessage().getOrder().setItems(request.getMessage().getOrder().getItems());

	} private void createProviderStructure(final Schema request, OnSchema respBody , final ConfigModel configModel) throws NumberFormatException, ClientProtocolException, IOException,	JSONException 
	{
		RetailResponseInitkirana.log.info(" inside method of createProviderStructure...");
		provider = new Provider(); 
		location_Provider = new Location();
		address_Provider = new Address(); 
		location_list = new ArrayList<Location>();
		kiranaObjList =new ArrayList<KiranaObj>(); 
		search_vendorid =request.getMessage().getOrder().getProvider().getId();

		Optional<KiranaObj> kiranaObjopt=mKiranaRepository.findBydata(Long.parseLong(search_vendorid)); 
		kiranaObjList=kiranaObjopt.map(Collections::singletonList).orElse(Collections.emptyList());
        String providerPhone="";
		for (int i=0; i< kiranaObjList.size(); i++) 
		{
			provider.setId((kiranaObjList.get(i).getId()).toString()); 
			//Descriptor	descriptor = new Descriptor();
			//descriptor.setName(kiranaObjList.get(i).getStorename());
			//provider.setDescriptor(descriptor);
	
			this.retailResponseSearchkirana.getStringFromLocation(Double.parseDouble(kiranaObjList.get(i).getLatitude()),Double.parseDouble(kiranaObjList.get(i).getLongitude()), address_Provider);
	
			location_Provider.setAddress(address_Provider);
			location_Provider.setId((kiranaObjList.get(i).getId()).toString());
	
			String latitude=kiranaObjList.get(i).getLatitude(); 
			String longitude=kiranaObjList.get(i).getLongitude(); 
			String gps=latitude +","+longitude; location_Provider.setGps(gps);
	
			location_list.add(location_Provider); provider.setLocations(location_list);
	
			respBody.getMessage().getOrder().setProvider(provider);
			providerPhone=kiranaObjList.get(i).getPhone();
		}
		
		for(int i =0; i < request.getMessage().getOrder().getFulfillments().size(); i++) {
			//fulfillment_list.add(request.getMessage().getOrder().getFulfillments().get(i));
			//respBody.getMessage().getOrder().getFulfillments().get(i).setRateable(Boolean.valueOf(configModel.getRateable()));
			respBody.getMessage().getOrder().getFulfillments().get(i).setTracking(Boolean.valueOf(configModel.getTracking()));
			respBody.getMessage().getOrder().getFulfillments().get(i).setStart(new Start());
			respBody.getMessage().getOrder().getFulfillments().get(i).getStart().setContact(new Contact());
			if(respBody.getMessage().getOrder().getFulfillments().get(i).getStart().getContact() ==null) {
				respBody.getMessage().getOrder().getFulfillments().get(i).getStart().setContact(new Contact());
			}
			respBody.getMessage().getOrder().getFulfillments().get(i).getStart().getContact().setPhone(providerPhone);
		}

	}
	private float calculatetax(float eachItemprice, List<KiranaObj> kiranaObjList)
	{
		RetailResponseInitkirana.log.info(" inside method of calculatetax...");
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
		RetailResponseInitkirana.log.info(" inside method of calculateDiscount...");
		float discount=0;
		
		if(kiranaObjList.get(0).getOfferPercen() != null && !kiranaObjList.get(0).getOfferPercen().equalsIgnoreCase("")) {
			discount = eachItemprice * Float.parseFloat(kiranaObjList.get(0).getOfferPercen());
			discount= discount/100;
		}
		
		return discount;
	}
	static {
		log = LoggerFactory.getLogger((Class) RetailResponseInitkirana.class);
	}
}
