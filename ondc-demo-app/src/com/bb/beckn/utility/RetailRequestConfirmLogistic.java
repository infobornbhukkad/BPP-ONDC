// 
// Decompiled by Procyon v0.5.36
// 

package com.bb.beckn.utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import com.bb.beckn.api.model.common.Address;
import com.bb.beckn.api.model.common.Billing;
import com.bb.beckn.api.model.common.Category;
import com.bb.beckn.api.model.common.Contact;
import com.bb.beckn.api.model.common.Context;
import com.bb.beckn.api.model.common.Descriptor;
import com.bb.beckn.api.model.common.Fulfillment;
import com.bb.beckn.api.model.common.Intent;
import com.bb.beckn.api.model.common.Item;
import com.bb.beckn.api.model.common.ItemQuantity;
import com.bb.beckn.api.model.common.Location;
import com.bb.beckn.api.model.common.OndcLinkedOrders;
import com.bb.beckn.api.model.common.Order;
import com.bb.beckn.api.model.common.Payload;
import com.bb.beckn.api.model.common.Payment;
import com.bb.beckn.api.model.common.Person;
import com.bb.beckn.api.model.common.Price;
import com.bb.beckn.api.model.common.Provider;
import com.bb.beckn.api.model.common.Quotation;
import com.bb.beckn.api.model.common.QuotationBreakUp;
import com.bb.beckn.api.model.common.Scalar;
import com.bb.beckn.api.model.common.SettlementDetails;
import com.bb.beckn.api.model.common.Start;
import com.bb.beckn.api.model.common.Tags;
import com.bb.beckn.api.model.common.Time;
import com.bb.beckn.api.model.common.TimeRange;
import com.bb.beckn.api.model.confirm.ConfirmMessage;

import com.bb.beckn.api.model.search.SearchMessage;
import com.bb.beckn.common.exception.ApplicationException;
import com.bb.beckn.common.exception.ErrorCode;
import com.bb.beckn.common.model.ConfigModel;
import com.bb.beckn.common.model.SigningModel;
import com.bb.beckn.common.sender.Sender;
import com.bb.beckn.common.service.ApplicationConfigService;
import com.bb.beckn.common.util.JsonUtil;
import com.bb.beckn.search.model.ItemDetailsKirana;
import com.bb.beckn.search.model.ItemObj;
import com.bb.beckn.search.model.KiranaObj;
import com.bb.beckn.search.model.LogisticBppObj;
import com.bb.beckn.search.model.VendorObj;
import com.bb.beckn.confirm.extension.OnSchema;
import com.bb.beckn.confirm.extension.Schema;
import com.bb.beckn.repository.ItemRepositoryKirana;

import java.security.PrivateKey;
import java.security.Signature;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.crypto.digests.Blake2bDigest;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.JSONWriter;

import java.io.StringWriter;

@Service
public class RetailRequestConfirmLogistic
{
	private static final Logger log;
    
	@Autowired
	ItemRepositoryKirana mItemRepositoryKirana;
	@Autowired
	private Sender sender;
	@Autowired
	private RetailRequestLogisticHeader mRetailRequestLogisticHeader;
	@Autowired
	private ApplicationConfigService configService;
	@Autowired
	private JsonUtil jsonUtil;

	Context ctx =null;
	
	ConfirmMessage logisticconfirmMessage =null;
	Order logisticOrder =null;
	//Category logisticCategory =null;
	Provider logisticProvider =null;
	Item logisticItem =null;
	List<Item> logisticItemList= null;
	List<OndcLinkedOrders> linkedOrders =null;
	Fulfillment logisticFulfillment = null;
	List<Fulfillment> logisticIFulfillmentList= null;
	Billing logisticBilling =null;
	Payment logisticPayment =null;
	Descriptor logisticDescriptor =null;
	Address logisticSellerAddress=null;
	Contact providercontact = null;
	Quotation logisticquote= null;
	ItemDetailsKirana ItemDetailsKiranaObj=null;
	Price logisticPrice =null;
	List<QuotationBreakUp> listlogisticbreakup = null;
	QuotationBreakUp logisticQuotationBreakUp =null;
	List<SettlementDetails> settlementDetailsObjList =null;
	SettlementDetails settlementDetails =null;
	
	public void creatingLogisticconfirmRequestStructureKirana(final Schema request, OnSchema respBody, String strtransactionid, String strmessageid, 
			List<LogisticBppObj> logisticBppObj_list, List item_list, String providerPhone, List itemDetailsKirana, String providerEmail) { 
		RetailRequestConfirmLogistic.log.info("Going to create Logistic confirm RequestStructure Kirana. in RetailRequestConfirmLogistic method..");
		final com.bb.beckn.confirm.extension.Schema requestconfirmLogistic = new com.bb.beckn.confirm.extension.Schema();


			final ConfigModel configModel =	this.configService.loadApplicationConfiguration(request.getContext().getBapId(), "confirm");

			ctx =new Context(); 
			ctx.setDomain(configModel.getLogisticDomain());
			ctx.setBapId(configModel.getSubscriberlogisticId());
			ctx.setBapUri(configModel.getSubscriberlogisticUrl());
			ctx.setBppId(logisticBppObj_list.get(0).getBppid());
			ctx.setBppUri(logisticBppObj_list.get(0).getBppurl());	
			ctx.setCity(request.getContext().getCity());
			ctx.setMessageId(strmessageid);
			ctx.setTransactionId(strtransactionid);
			ctx.setCoreVersion(configModel.getVersion()); 
			String ttl= ""+Duration.ofMinutes(Long.parseLong(configModel.getBecknTtl()));
			ctx.setTtl(ttl);
			ctx.setCountry("IND"); 
			ctx.setAction("confirm");
			ctx.setTimestamp(Instant.now().toString()); 
			requestconfirmLogistic.setContext(ctx);
			 
			logisticconfirmMessage = new ConfirmMessage();
			if (logisticOrder==null) { 
				logisticOrder =new Order(); 
			}
			logisticconfirmMessage.setOrder(logisticOrder); 
			logisticconfirmMessage.getOrder().setId(request.getMessage().getOrder().getId());
			logisticconfirmMessage.getOrder().setState("Created");
			if (logisticProvider==null) 
			{ 
				logisticProvider = new Provider(); 
				
			}
			if (logisticItem==null) 
			{ 
				logisticItem = new Item(); 
				
			} 
			if (logisticFulfillment==null) 
			{ 
				logisticFulfillment = new Fulfillment(); 
				
			} 
			if (logisticBilling==null) 
			{ 
				logisticBilling = new Billing(); 
				
			} 
			if (logisticPayment==null) 
			{ 
				logisticPayment = new Payment(); 
				
			} 
			if (logisticquote==null) 
			{ 
				logisticquote = new Quotation(); 
				
			} 
			if (logisticPrice==null) 
			{ 
				logisticPrice = new Price(); 
				
			} 
			if (settlementDetails==null) 
			{ 
				settlementDetails = new SettlementDetails(); 
				
			} 
			
			
			for(int i=0; i< logisticBppObj_list.size(); i++) {
				final com.bb.beckn.search.extension.OnSchema model = (com.bb.beckn.search.extension.OnSchema)this.jsonUtil.toModel(logisticBppObj_list.get(i).getJson(), (Class)com.bb.beckn.search.extension.OnSchema.class);
				for(int ii=0; ii< model.getMessage().getCatalog().getBppProviders().size(); ii++) {
					if(model.getMessage().getCatalog().getBppProviders().get(ii).getId().equalsIgnoreCase(logisticBppObj_list.get(i).getProviderid())) {
						
						logisticProvider.setId(logisticBppObj_list.get(i).getProviderid());
						logisticProvider.setDescriptor(model.getMessage().getCatalog().getBppProviders().get(ii).getDescriptor());
						if(model.getMessage().getCatalog().getBppProviders().get(ii).getLocations()!= null) {
							logisticProvider.setLocations(model.getMessage().getCatalog().getBppProviders().get(ii).getLocations());
							logisticOrder.setProvider(logisticProvider);
							
						}
					}
				}
				
			}
			logisticconfirmMessage.setOrder(logisticOrder);
			
			logisticDescriptor =new Descriptor();
			logisticItemList = new ArrayList<Item>();
			for(int i=0; i< logisticBppObj_list.size(); i++) {
				logisticItem = new Item();
				final com.bb.beckn.search.extension.OnSchema model = 
						(com.bb.beckn.search.extension.OnSchema)this.jsonUtil.toModel(logisticBppObj_list.get(i).getJson(), (Class)com.bb.beckn.search.extension.OnSchema.class);
				for(int ii=0; ii< model.getMessage().getCatalog().getBppProviders().size(); ii++) 
				{
					if(model.getMessage().getCatalog().getBppProviders().get(ii).getId().equalsIgnoreCase(logisticBppObj_list.get(i).getProviderid())) {
						for(int k=0; k < model.getMessage().getCatalog().getBppProviders().get(ii).getItems().size(); k++) {
								if(model.getMessage().getCatalog().getBppProviders().get(ii).getItems()!= null && 
										model.getMessage().getCatalog().getBppProviders().get(ii).getItems().get(k).getParentItemId().equalsIgnoreCase("")	) {
									logisticItem.setId(model.getMessage().getCatalog().getBppProviders().get(ii).getItems().get(k).getId());
									logisticItem.setCategoryId(model.getMessage().getCatalog().getBppProviders().get(ii).getItems().get(k).getCategoryId());
									logisticDescriptor.setCode(model.getMessage().getCatalog().getBppProviders().get(ii).getItems().get(k).getDescriptor().getCode());
									logisticItem.setDescriptor(logisticDescriptor);
									logisticItemList.add(logisticItem);
									
								}
						}
					}
				}
			}
			logisticOrder.setItems(logisticItemList);
			
			//quote wil get from logistic init
			listlogisticbreakup = new ArrayList<>();
			logisticQuotationBreakUp =new QuotationBreakUp();
			
			logisticPrice.setCurrency("INR");
			logisticPrice.setValue(logisticBppObj_list.get(0).getDeliverycharge());
			logisticquote.setPrice(logisticPrice);
			
			logisticQuotationBreakUp.setItemId(logisticItem.getId());
			logisticQuotationBreakUp.setTitleType("Delivery Charge");
			logisticPrice.setCurrency("INR");
			logisticPrice.setValue(logisticBppObj_list.get(0).getDeliverycharge());
			logisticQuotationBreakUp.setPrice(logisticPrice);
			
			//listlogisticbreakup.add(logisticQuotationBreakUp);
			//logisticquote.setBreakup(listlogisticbreakup);
			listlogisticbreakup.add(logisticQuotationBreakUp);
			logisticquote.setBreakup(listlogisticbreakup);
			logisticOrder.setQuote(logisticquote);
			
			
			logisticIFulfillmentList =new ArrayList<Fulfillment>();
			//fulfillment
			for(int k=0; k< request.getMessage().getOrder().getFulfillments().size(); k++)
			{
				logisticFulfillment.setId(request.getMessage().getOrder().getFulfillments().get(k).getId());
				if(request.getMessage().getOrder().getPayment().getStatus().equalsIgnoreCase("PAID")) {
					logisticFulfillment.setType("Prepaid");
				}else if(request.getMessage().getOrder().getPayment().getStatus().equalsIgnoreCase("UN-PAID")) {
					logisticFulfillment.setType("CoD");
				}else {
					logisticFulfillment.setType("Reverse QC");
				}
				
				if(logisticDescriptor.getCode().equalsIgnoreCase("P2H2P")) {
					logisticFulfillment.setAwbnumber("765");
				}
				//end destination
				logisticFulfillment.setEnd(request.getMessage().getOrder().getFulfillments().get(k).getEnd());
				
				//start destination
				logisticFulfillment.setStart(new Start());
				logisticFulfillment.getStart().setPerson(new Person());
				logisticFulfillment.getStart().getPerson().setName(respBody.getMessage().getOrder().getProvider().getDescriptor().getName());
				logisticFulfillment.getStart().setLocation(new Location());
				logisticFulfillment.getStart().setLocation(respBody.getMessage().getOrder().getFulfillments().get(k).getStart().getLocation());
				if(logisticFulfillment.getStart().getContact()==null) {
					logisticFulfillment.getStart().setContact(new Contact());				
				}
				providercontact = new Contact();
				providercontact.setPhone(providerPhone);
				providercontact.setEmail(providerEmail);
				//logisticFulfillment.getStart().setContact(providercontact);
				logisticFulfillment.getStart().getContact().setPhone(respBody.getMessage().getOrder().getFulfillments().get(k).getStart().getContact().getPhone());
				logisticFulfillment.getStart().getContact().setEmail(respBody.getMessage().getOrder().getFulfillments().get(k).getStart().getContact().getEmail());
				logisticFulfillment.setTags(new Tags());
				
				logisticFulfillment.getTags().setReadytoship("Yes");			
				
				logisticIFulfillmentList.add(logisticFulfillment);
				
			}
			logisticOrder.setFulfillments(logisticIFulfillmentList);
			logisticSellerAddress = new Address();
			
			logisticBilling.setName(configModel.getBillingsellerName());
			logisticSellerAddress.setName(configModel.getBillingselleraddressbuildingName());
			logisticSellerAddress.setLocality(configModel.getBillingselleraddressbuildingBuilding());
			logisticSellerAddress.setCity(configModel.getBillingselleraddressbuildingCity());
			logisticSellerAddress.setState(configModel.getBillingselleraddressbuildingState());
			logisticSellerAddress.setCountry(configModel.getBillingselleraddressbuildingCountry());
			logisticSellerAddress.setAreaCode(configModel.getBillingselleraddressbuildingAreacode());
			logisticBilling.setAddress(logisticSellerAddress);
			///need to set correct number
			logisticBilling.setTaxNumber(configModel.getBillingsellertaxNumber());
			logisticBilling.setPhone(configModel.getBillingsellerPhonenumber());
			logisticBilling.setCreatedAt(Instant.now().toString());
			logisticBilling.setUpdatedAt(Instant.now().toString());
			logisticOrder.setBilling(logisticBilling);
			
			logisticconfirmMessage.setOrder(logisticOrder);
			
			settlementDetailsObjList = new ArrayList<SettlementDetails>();
			settlementDetails = new SettlementDetails();
			
			logisticPayment.setCollectionAmount(respBody.getMessage().getOrder().getPayment().getParams().getAmount());
			logisticPayment.setCollectedBy(respBody.getMessage().getOrder().getPayment().getCollectedBy());
			logisticPayment.setType(respBody.getMessage().getOrder().getPayment().getType());
			
			for(int i=0; i < respBody.getMessage().getOrder().getPayment().getSettlementDetails().size(); i++) {
				settlementDetails.setSettlementCounterparty(respBody.getMessage().getOrder().getPayment().getSettlementDetails().get(i).getSettlementCounterparty());
				settlementDetails.setSettlementType(respBody.getMessage().getOrder().getPayment().getSettlementDetails().get(i).getSettlementType());
				settlementDetails.setUpiAddress(respBody.getMessage().getOrder().getPayment().getSettlementDetails().get(i).getUpiAddress());
				settlementDetails.setSettlementBankAccountNo(respBody.getMessage().getOrder().getPayment().getSettlementDetails().get(i).getSettlementBankAccountNo());
				settlementDetails.setSettlementIfscCode(respBody.getMessage().getOrder().getPayment().getSettlementDetails().get(i).getSettlementIfscCode());
				settlementDetailsObjList.add(settlementDetails);
			}
			logisticPayment.setSettlementDetails(settlementDetailsObjList);
			
			logisticOrder.setPayment(logisticPayment);
		    //linkedOrders = new ArrayList<OndcLinkedOrders>();
			logisticItemList = new ArrayList<Item>();
			float flTotalweight = 0;
			float flweight=0;
			for(int l=0; l < itemDetailsKirana.size(); l++) {
				ItemDetailsKiranaObj =	mItemRepositoryKirana.findByItemidandvendorid(Long.parseLong(request.getMessage().getOrder().getItems().get(l).getId()),
						request.getMessage().getOrder().getProvider().getId());
				
				logisticItem = new Item();
				logisticItem.setCategoryId(ItemDetailsKiranaObj.getItemcategory().trim());
				logisticItem.setDescriptor(new Descriptor());
				logisticItem.getDescriptor().setName(ItemDetailsKiranaObj.getItemname());
				logisticItem.setQuantity(new ItemQuantity());
				logisticItem.getQuantity().setMeasure(new Scalar());
				logisticItem.getQuantity().setCount(request.getMessage().getOrder().getItems().get(l).getQuantity().getCount());
				logisticItem.getQuantity().getMeasure().setUnit(ItemDetailsKiranaObj.getUnit());
				System.out.println("ItemDetailsKiranaObj.getNewQuantity()- " + ItemDetailsKiranaObj.getNewQuantity());
				
				//logisticItem.getQuantity().getMeasure().setValue(Float.valueOf(ItemDetailsKiranaObj.getNewQuantity()));
				logisticItem.getQuantity().getMeasure().setValue((int)(Math.round(Double.valueOf(ItemDetailsKiranaObj.getNewQuantity()))));
				logisticItem.setPrice(new Price());
				logisticItem.getPrice().setCurrency("INR");
				logisticItem.getPrice().setValue(ItemDetailsKiranaObj.getItemprice());	
				if(ItemDetailsKiranaObj.getUnit().equalsIgnoreCase("Gms") || ItemDetailsKiranaObj.getUnit().equalsIgnoreCase("ml")) {
					flweight= request.getMessage().getOrder().getItems().get(l).getQuantity().getCount() * Float.valueOf(ItemDetailsKiranaObj.getNewQuantity());
				  flTotalweight = flTotalweight + flweight/1000;
				}else {
					flweight= request.getMessage().getOrder().getItems().get(l).getQuantity().getCount() * Float.valueOf(ItemDetailsKiranaObj.getNewQuantity());
					flTotalweight =flTotalweight+ flweight;
				}
				logisticItemList.add(logisticItem);
				
			}
			
			logisticOrder.setLinkedOrders(new OndcLinkedOrders());
			
			logisticOrder.getLinkedOrders().setItems(logisticItemList);
			
			logisticOrder.getLinkedOrders().setProvider(new Provider());
			logisticOrder.getLinkedOrders().getProvider().setDescriptor(respBody.getMessage().getOrder().getFulfillments().get(0).getStart().getLocation().getDescriptor());
			logisticOrder.getLinkedOrders().getProvider().setAddress(respBody.getMessage().getOrder().getFulfillments().get(0).getStart().getLocation().getAddress());
			
			logisticOrder.getLinkedOrders().setOrder(new Order());
			logisticOrder.getLinkedOrders().getOrder().setId(request.getMessage().getOrder().getId());
			logisticOrder.getLinkedOrders().getOrder().setWeight(new Scalar());
			logisticOrder.getLinkedOrders().getOrder().getWeight().setUnit("Kilogram");
			logisticOrder.getLinkedOrders().getOrder().getWeight().setValue((int)flTotalweight);
			
			
			logisticconfirmMessage.setOrder(logisticOrder);
			
			requestconfirmLogistic.setMessage(logisticconfirmMessage);

			String json = (this.jsonUtil.toJson((Object)requestconfirmLogistic));	
			String minifiedJson = new Minify ().minify (json);
			System.out.println("minifiedJson - "+ minifiedJson);

			final String url = logisticBppObj_list.get(0).getBppurl() + "confirm"; 
			//final String url = "https://localhost:8083/buyer/adaptor/on_confirm"; 
			final HttpHeaders headers= this.mRetailRequestLogisticHeader.logisticbuildHeaders(minifiedJson, configModel); 
			System.out.println("headers - "+ headers); 
			try {
			this.sender.send(url, headers, minifiedJson, configModel.getMatchedApi());
			}catch(Exception e) {
				System.out.println("exception on RetailRequestConfirmLogistic " + e.getMessage());
			}
			System.out.println("end of creatingLogisticconfirmRequestStructureKirana");
	}
	
	public void creatingLogisticconfirmRequestStructurefood(final Schema request, OnSchema respBody, String strtransactionid, String strmessageid, 
			List<LogisticBppObj> logisticBppObj_list, List item_list, String providerPhone,List itemDetailsFood, String providerEmail) { 
		RetailRequestConfirmLogistic.log.info("Going to create Logistic confirm RequestStructure food. in creatingLogisticconfirmRequestStructurefood method..");
		final com.bb.beckn.confirm.extension.Schema requestconfirmLogistic = new com.bb.beckn.confirm.extension.Schema();


		final ConfigModel configModel =	this.configService.loadApplicationConfiguration(request.getContext().getBapId(), "confirm");

		ctx =new Context(); 
		ctx.setDomain(configModel.getLogisticDomain());
		ctx.setBapId(configModel.getSubscriberlogisticId());
		ctx.setBapUri(configModel.getSubscriberlogisticUrl());
		ctx.setBppId(logisticBppObj_list.get(0).getBppid());
		ctx.setBppUri(logisticBppObj_list.get(0).getBppurl());	
		ctx.setCity(request.getContext().getCity());
		ctx.setMessageId(strmessageid);
		ctx.setTransactionId(strtransactionid);
		ctx.setCoreVersion(configModel.getVersion()); 
		String ttl= ""+Duration.ofMinutes(Long.parseLong(configModel.getBecknTtl()));
		ctx.setTtl(ttl);
		ctx.setCountry("IND"); 
		ctx.setAction("confirm");
		ctx.setTimestamp(Instant.now().toString()); 
		requestconfirmLogistic.setContext(ctx);
		 
		logisticconfirmMessage = new ConfirmMessage();
		if (logisticOrder==null) { 
			logisticOrder =new Order(); 
		}
		logisticconfirmMessage.setOrder(logisticOrder); 
		logisticconfirmMessage.getOrder().setId(request.getMessage().getOrder().getId());
		logisticconfirmMessage.getOrder().setState("Created");
		if (logisticProvider==null) 
		{ 
			logisticProvider = new Provider(); 
			
		}
		if (logisticItem==null) 
		{ 
			logisticItem = new Item(); 
			
		} 
		if (logisticFulfillment==null) 
		{ 
			logisticFulfillment = new Fulfillment(); 
			
		} 
		if (logisticBilling==null) 
		{ 
			logisticBilling = new Billing(); 
			
		} 
		if (logisticPayment==null) 
		{ 
			logisticPayment = new Payment(); 
			
		} 
		if (logisticquote==null) 
		{ 
			logisticquote = new Quotation(); 
			
		} 
		if (logisticPrice==null) 
		{ 
			logisticPrice = new Price(); 
			
		} 
		for(int i=0; i< logisticBppObj_list.size(); i++) {
			final com.bb.beckn.search.extension.OnSchema model = (com.bb.beckn.search.extension.OnSchema)this.jsonUtil.toModel(logisticBppObj_list.get(i).getJson(), (Class)com.bb.beckn.search.extension.OnSchema.class);
			for(int ii=0; ii< model.getMessage().getCatalog().getBppProviders().size(); ii++) {
				if(model.getMessage().getCatalog().getBppProviders().get(ii).getId().equalsIgnoreCase(logisticBppObj_list.get(i).getProviderid())) {
					
					logisticProvider.setId(logisticBppObj_list.get(i).getProviderid());
					logisticProvider.setDescriptor(model.getMessage().getCatalog().getBppProviders().get(ii).getDescriptor());
					if(model.getMessage().getCatalog().getBppProviders().get(ii).getLocations()!= null) {
						logisticProvider.setLocations(model.getMessage().getCatalog().getBppProviders().get(ii).getLocations());
						logisticOrder.setProvider(logisticProvider);
						
					}
				}
			}
			
		}
		logisticconfirmMessage.setOrder(logisticOrder);
		
		logisticDescriptor =new Descriptor();
		logisticItemList = new ArrayList<Item>();
		for(int i=0; i< logisticBppObj_list.size(); i++) {
			logisticItem = new Item();
			final com.bb.beckn.search.extension.OnSchema model = 
					(com.bb.beckn.search.extension.OnSchema)this.jsonUtil.toModel(logisticBppObj_list.get(i).getJson(), (Class)com.bb.beckn.search.extension.OnSchema.class);
			for(int ii=0; ii< model.getMessage().getCatalog().getBppProviders().size(); ii++) 
			{
				if(model.getMessage().getCatalog().getBppProviders().get(ii).getId().equalsIgnoreCase(logisticBppObj_list.get(i).getProviderid())) {
					for(int k=0; k < model.getMessage().getCatalog().getBppProviders().get(ii).getItems().size(); k++) {
							if(model.getMessage().getCatalog().getBppProviders().get(ii).getItems()!= null && 
									model.getMessage().getCatalog().getBppProviders().get(ii).getItems().get(k).getParentItemId().equalsIgnoreCase("")	) {
								logisticItem.setId(model.getMessage().getCatalog().getBppProviders().get(ii).getItems().get(k).getId());
								logisticItem.setCategoryId(model.getMessage().getCatalog().getBppProviders().get(ii).getItems().get(k).getCategoryId());
								logisticDescriptor.setCode(model.getMessage().getCatalog().getBppProviders().get(ii).getItems().get(k).getDescriptor().getCode());
								logisticItem.setDescriptor(logisticDescriptor);
								logisticItemList.add(logisticItem);
								
							}
					}
				}
			}
		}
		logisticOrder.setItems(logisticItemList);
		
		//quote wil get from logistic init
		listlogisticbreakup = new ArrayList<>();
		logisticQuotationBreakUp =new QuotationBreakUp();
		
		logisticPrice.setCurrency("INR");
		logisticPrice.setValue(logisticBppObj_list.get(0).getDeliverycharge());
		logisticquote.setPrice(logisticPrice);
		
		logisticQuotationBreakUp.setItemId(logisticItem.getId());
		logisticQuotationBreakUp.setTitleType("Delivery Charge");
		logisticPrice.setCurrency("INR");
		logisticPrice.setValue(logisticBppObj_list.get(0).getDeliverycharge());
		logisticQuotationBreakUp.setPrice(logisticPrice);
		
		//listlogisticbreakup.add(logisticQuotationBreakUp);
		//logisticquote.setBreakup(listlogisticbreakup);
		listlogisticbreakup.add(logisticQuotationBreakUp);
		logisticquote.setBreakup(listlogisticbreakup);
		logisticOrder.setQuote(logisticquote);
		
		logisticIFulfillmentList =new ArrayList<Fulfillment>();
		//fulfillment
		for(int k=0; k< request.getMessage().getOrder().getFulfillments().size(); k++)
		{
			logisticFulfillment.setId(request.getMessage().getOrder().getFulfillments().get(k).getId());
			if(request.getMessage().getOrder().getPayment().getStatus().equalsIgnoreCase("PAID")) {
				logisticFulfillment.setType("Prepaid");
			}else if(request.getMessage().getOrder().getPayment().getStatus().equalsIgnoreCase("UN-PAID")) {
				logisticFulfillment.setType("CoD");
			}else {
				logisticFulfillment.setType("Reverse QC");
			}
			
			if(logisticDescriptor.getCode().equalsIgnoreCase("P2H2P")) {
				logisticFulfillment.setAwbnumber("123");
			}
			//end destination
			logisticFulfillment.setEnd(request.getMessage().getOrder().getFulfillments().get(k).getEnd());
			
			//start destination
			logisticFulfillment.setStart(new Start());
			logisticFulfillment.getStart().setPerson(new Person());
			logisticFulfillment.getStart().getPerson().setName(respBody.getMessage().getOrder().getProvider().getDescriptor().getName());
			logisticFulfillment.getStart().setLocation(new Location());
			logisticFulfillment.getStart().setLocation(respBody.getMessage().getOrder().getFulfillments().get(k).getStart().getLocation());
			if(logisticFulfillment.getStart().getContact()==null) {
				logisticFulfillment.getStart().setContact(new Contact());				
			}
			providercontact = new Contact();
			providercontact.setPhone(providerPhone);
			providercontact.setEmail(providerEmail);
			//logisticFulfillment.getStart().setContact(providercontact);
			logisticFulfillment.getStart().getContact().setPhone(respBody.getMessage().getOrder().getFulfillments().get(k).getStart().getContact().getPhone());
			logisticFulfillment.getStart().getContact().setEmail(respBody.getMessage().getOrder().getFulfillments().get(k).getStart().getContact().getEmail());
			logisticFulfillment.setTags(new Tags());
			
			logisticFulfillment.getTags().setReadytoship("Yes");			
			
			logisticIFulfillmentList.add(logisticFulfillment);
			
		}
		logisticOrder.setFulfillments(logisticIFulfillmentList);
		logisticSellerAddress = new Address();
		
		logisticBilling.setName("Seller App");
		logisticSellerAddress.setName("Flat 401");
		logisticSellerAddress.setLocality("Uniworldcity");
		logisticSellerAddress.setCity("Kolkata");
		logisticSellerAddress.setState("West Bengal");
		logisticSellerAddress.setCountry("India");
		logisticSellerAddress.setAreaCode("700156");
		logisticBilling.setAddress(logisticSellerAddress);
		///need to set correct number
		logisticBilling.setTaxNumber("123456");
		logisticBilling.setPhone("9890196855");
		logisticBilling.setCreatedAt(Instant.now().toString());
		logisticBilling.setUpdatedAt(Instant.now().toString());
		logisticOrder.setBilling(logisticBilling);
		
		logisticconfirmMessage.setOrder(logisticOrder);
		
		settlementDetailsObjList = new ArrayList<SettlementDetails>();
		settlementDetails = new SettlementDetails();
		
		logisticPayment.setCollectionAmount(respBody.getMessage().getOrder().getPayment().getParams().getAmount());
		logisticPayment.setCollectedBy(respBody.getMessage().getOrder().getPayment().getCollectedBy());
		logisticPayment.setType(respBody.getMessage().getOrder().getPayment().getType());
		
		for(int i=0; i < respBody.getMessage().getOrder().getPayment().getSettlementDetails().size(); i++) {
			settlementDetails.setSettlementCounterparty(respBody.getMessage().getOrder().getPayment().getSettlementDetails().get(i).getSettlementCounterparty());
			settlementDetails.setSettlementType(respBody.getMessage().getOrder().getPayment().getSettlementDetails().get(i).getSettlementType());
			settlementDetails.setUpiAddress(respBody.getMessage().getOrder().getPayment().getSettlementDetails().get(i).getUpiAddress());
			settlementDetails.setSettlementBankAccountNo(respBody.getMessage().getOrder().getPayment().getSettlementDetails().get(i).getSettlementBankAccountNo());
			settlementDetails.setSettlementIfscCode(respBody.getMessage().getOrder().getPayment().getSettlementDetails().get(i).getSettlementIfscCode());
			settlementDetailsObjList.add(settlementDetails);
		}
		logisticPayment.setSettlementDetails(settlementDetailsObjList);
		
		logisticOrder.setPayment(logisticPayment);
	    //linkedOrders = new ArrayList<OndcLinkedOrders>();
		logisticItemList = new ArrayList<Item>();
		float flTotalweight = 0;
		float flweight=0;
		for(int l=0; l < itemDetailsFood.size(); l++) {
			ItemDetailsKiranaObj =	mItemRepositoryKirana.findByItemidandvendorid(Long.parseLong(request.getMessage().getOrder().getItems().get(l).getId()),
					request.getMessage().getOrder().getProvider().getId());
			
			logisticItem = new Item();
			logisticItem.setCategoryId(ItemDetailsKiranaObj.getItemcategory());
			logisticItem.setDescriptor(new Descriptor());
			logisticItem.getDescriptor().setName(ItemDetailsKiranaObj.getItemname());
			logisticItem.setQuantity(new ItemQuantity());
			logisticItem.getQuantity().setMeasure(new Scalar());
			logisticItem.getQuantity().setCount(request.getMessage().getOrder().getItems().get(l).getQuantity().getCount());
			logisticItem.getQuantity().getMeasure().setUnit(ItemDetailsKiranaObj.getUnit());
			System.out.println("ItemDetailsKiranaObj.getNewQuantity()- " + ItemDetailsKiranaObj.getNewQuantity());
			logisticItem.getQuantity().getMeasure().setValue((int)(Math.round(Double.valueOf(ItemDetailsKiranaObj.getNewQuantity()))));
			logisticItem.setPrice(new Price());
			logisticItem.getPrice().setCurrency("INR");
			logisticItem.getPrice().setValue(ItemDetailsKiranaObj.getItemprice());	
			if(ItemDetailsKiranaObj.getUnit().equalsIgnoreCase("Gms") || ItemDetailsKiranaObj.getUnit().equalsIgnoreCase("ml")) {
				flweight= request.getMessage().getOrder().getItems().get(l).getQuantity().getCount() * Float.valueOf(ItemDetailsKiranaObj.getNewQuantity());
			  flTotalweight = flTotalweight + flweight/1000;
			}else {
				flweight= request.getMessage().getOrder().getItems().get(l).getQuantity().getCount() * Float.valueOf(ItemDetailsKiranaObj.getNewQuantity());
				flTotalweight =flTotalweight+ flweight;
			}
			logisticItemList.add(logisticItem);
			
		}
		//logisticOrder.setItems(logisticItemList);
		logisticOrder.setLinkedOrders(new OndcLinkedOrders());
		
		logisticOrder.getLinkedOrders().setItems(logisticItemList);
		
		logisticOrder.getLinkedOrders().setProvider(new Provider());
		logisticOrder.getLinkedOrders().getProvider().setDescriptor(respBody.getMessage().getOrder().getFulfillments().get(0).getStart().getLocation().getDescriptor());
		logisticOrder.getLinkedOrders().getProvider().setAddress(respBody.getMessage().getOrder().getFulfillments().get(0).getStart().getLocation().getAddress());
		
		logisticOrder.getLinkedOrders().setOrder(new Order());
		logisticOrder.getLinkedOrders().getOrder().setId("O1");
		logisticOrder.getLinkedOrders().getOrder().setWeight(new Scalar());
		logisticOrder.getLinkedOrders().getOrder().getWeight().setUnit("Kilogram");
		logisticOrder.getLinkedOrders().getOrder().getWeight().setValue((int)flTotalweight);
		
		
		logisticconfirmMessage.setOrder(logisticOrder);
		
		requestconfirmLogistic.setMessage(logisticconfirmMessage);

		final String json = this.jsonUtil.toJson((Object)requestconfirmLogistic);
		System.out.println("json - "+ json);

		final String url = logisticBppObj_list.get(0).getBppurl() + "confirm"; 
		//final String	url = "https://my.ithinklogistics.com/ondc/search"; 
		final HttpHeaders headers= this.mRetailRequestLogisticHeader.logisticbuildHeaders(json, configModel); 
		System.out.println("headers - "+ headers); 
		this.sender.send(url, headers, json, configModel.getMatchedApi());
		System.out.println("end of creatingLogisticconfirmRequestStructurefood");
	}	

	static {
		log = LoggerFactory.getLogger((Class)RetailRequestConfirmLogistic.class);
	}
}
