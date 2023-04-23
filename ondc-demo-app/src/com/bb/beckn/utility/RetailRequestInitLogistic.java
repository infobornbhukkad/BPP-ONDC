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
import com.bb.beckn.api.model.common.Location;
import com.bb.beckn.api.model.common.Order;
import com.bb.beckn.api.model.common.Payload;
import com.bb.beckn.api.model.common.Payment;
import com.bb.beckn.api.model.common.Provider;
import com.bb.beckn.api.model.common.Scalar;
import com.bb.beckn.api.model.common.Start;
import com.bb.beckn.api.model.common.Time;
import com.bb.beckn.api.model.common.TimeRange;
import com.bb.beckn.api.model.init.InitMessage;
import com.bb.beckn.api.model.search.SearchMessage;
import com.bb.beckn.common.exception.ApplicationException;
import com.bb.beckn.common.exception.ErrorCode;
import com.bb.beckn.common.model.ConfigModel;
import com.bb.beckn.common.model.SigningModel;
import com.bb.beckn.common.sender.Sender;
import com.bb.beckn.common.service.ApplicationConfigService;
import com.bb.beckn.common.util.JsonUtil;
import com.bb.beckn.init.service.InitServiceSeller;
import com.bb.beckn.search.model.KiranaObj;
import com.bb.beckn.search.model.LogisticBppObj;
import com.bb.beckn.search.model.VendorObj;
import com.bb.beckn.init.extension.OnSchema;
import com.bb.beckn.init.extension.Schema;
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

@Service
public class RetailRequestInitLogistic
{
	private static final Logger log;

	@Autowired
	private Sender sender;
	@Autowired
	private RetailRequestLogisticHeader mRetailRequestLogisticHeader;
	@Autowired
	private ApplicationConfigService configService;
	@Autowired
	private JsonUtil jsonUtil;

	Context ctx =null;
	InitMessage logisticinitMessage =null;
	Order logisticOrder =null;
	//Category logisticCategory =null;
	Provider logisticProvider =null;
	Item logisticItem =null;
	List<Item> logisticItemList= null;
	Fulfillment logisticFulfillment = null;
	List<Fulfillment> logisticIFulfillmentList= null;
	Billing logisticBilling =null;
	Payment logisticPayment =null;
	Descriptor logisticDescriptor =null;
	Address logisticSellerAddress=null;
	Contact providercontact = null;
	//Time logisticProviderTime =null;
	//TimeRange logisticProviderTimeRange =null;
	//Fulfillment logisticProviderFulfillment =null;
	//Scalar payloadWeight =null;

	public void creatingLogisticinitRequestStructureKirana(final Schema request, OnSchema respBody, List<LogisticBppObj> logisticBppObj_list, String strtransactionid, String strmessageid ) { 
		RetailRequestInitLogistic.log.info("Going to create Logistic init RequestStructure Kirana. in creatingLogisticinitRequestStructureKirana method..");
		final com.bb.beckn.init.extension.Schema requestinitLogistic = new com.bb.beckn.init.extension.Schema();


			final ConfigModel configModel =	this.configService.loadApplicationConfiguration(request.getContext().getBapId(), "init");

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
			ctx.setAction("init");
			ctx.setTimestamp(Instant.now().toString()); 
			requestinitLogistic.setContext(ctx);

			logisticinitMessage = new InitMessage();

			if (logisticOrder==null) { 
				logisticOrder =new Order(); 
			}
			logisticinitMessage.setOrder(logisticOrder); 
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
			
			for(int i=0; i< logisticBppObj_list.size(); i++) {
				final com.bb.beckn.search.extension.OnSchema model = (com.bb.beckn.search.extension.OnSchema)this.jsonUtil.toModel(logisticBppObj_list.get(i).getJson(), (Class)com.bb.beckn.search.extension.OnSchema.class);
				for(int ii=0; ii< model.getMessage().getCatalog().getBppProviders().size(); ii++) {
					if(model.getMessage().getCatalog().getBppProviders().get(ii).getId().equalsIgnoreCase(logisticBppObj_list.get(i).getProviderid())) {
						
						logisticProvider.setId(logisticBppObj_list.get(i).getProviderid());
						//logisticProvider.setDescriptor(model.getMessage().getCatalog().getBppProviders().get(ii).getDescriptor());
						if(model.getMessage().getCatalog().getBppProviders().get(ii).getLocations()!= null) {
							logisticProvider.setLocations(model.getMessage().getCatalog().getBppProviders().get(ii).getLocations());
							logisticOrder.setProvider(logisticProvider);
							
						}
					}
				}
				
			}
			logisticDescriptor =new Descriptor();
			logisticItemList = new ArrayList<Item>();
			for(int i=0; i< logisticBppObj_list.size(); i++) {
				final com.bb.beckn.search.extension.OnSchema model = 
						(com.bb.beckn.search.extension.OnSchema)this.jsonUtil.toModel(logisticBppObj_list.get(i).getJson(), (Class)com.bb.beckn.search.extension.OnSchema.class);
				for(int ii=0; ii< model.getMessage().getCatalog().getBppProviders().size(); ii++) {
					if(model.getMessage().getCatalog().getBppProviders().get(ii).getId()!=null && model.getMessage().getCatalog().getBppProviders().get(ii).getId().equalsIgnoreCase(logisticBppObj_list.get(i).getProviderid())) {
						for(int k=0; k < model.getMessage().getCatalog().getBppProviders().get(ii).getItems().size(); k++){
							if(model.getMessage().getCatalog().getBppProviders().get(ii).getItems()!= null && 
									model.getMessage().getCatalog().getBppProviders().get(ii).getItems().get(k).getParentItemId().equalsIgnoreCase("")	) {
								
								logisticItem.setId(model.getMessage().getCatalog().getBppProviders().get(ii).getItems().get(k).getId());
								logisticItem.setCategoryId(model.getMessage().getCatalog().getBppProviders().get(ii).getItems().get(k).getCategoryId());
								logisticDescriptor.setCode(model.getMessage().getCatalog().getBppProviders().get(ii).getItems().get(k).getDescriptor().getCode());
								logisticItem.setDescriptor(logisticDescriptor);
								logisticItemList.add(logisticItem);
								logisticOrder.setItems(logisticItemList);
							}
						}
						
					}
					
				}
			}
			
			logisticIFulfillmentList = new ArrayList<Fulfillment>();
			logisticFulfillment.setId(request.getMessage().getOrder().getFulfillments().get(0).getId());
			logisticFulfillment.setType("CoD");
			logisticFulfillment.setEnd(request.getMessage().getOrder().getFulfillments().get(0).getEnd());
			logisticFulfillment.setStart(new Start());
			logisticFulfillment.getStart().setLocation(new Location());
			logisticFulfillment.getStart().getLocation().setGps(respBody.getMessage().getOrder().getProvider().getLocations().get(0).getGps());
			logisticFulfillment.getStart().getLocation().setAddress(respBody.getMessage().getOrder().getProvider().getLocations().get(0).getAddress());
			
			//logisticFulfillment.getStart().setLocation(respBody.getMessage().getOrder().getFulfillments().get(0).getStart().getLocation());
			if(logisticFulfillment.getStart().getContact()==null) {
				logisticFulfillment.getStart().setContact(new Contact());				
			}
			providercontact = new Contact();
			providercontact.setPhone(respBody.getMessage().getOrder().getFulfillments().get(0).getStart().getContact().getPhone());
			logisticFulfillment.getStart().setContact(providercontact);
			logisticFulfillment.getStart().getContact().setPhone(respBody.getMessage().getOrder().getFulfillments().get(0).getStart().getContact().getPhone());
			logisticIFulfillmentList.add(logisticFulfillment);
			//respBody.getMessage().getOrder().getFulfillments().get(0).getStart().getLocation().setGps(respBody.getMessage().getOrder().getProvider().getLocations().get(0).getGps());
			//respBody.getMessage().getOrder().getFulfillments().get(0).getStart().getLocation().setAddress(respBody.getMessage().getOrder().getProvider().getLocations().get(0).getAddress());
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
			logisticBilling.setTaxNumber("123456");
			logisticBilling.setPhone("9890196855");
			logisticBilling.setCreatedAt(Instant.now().toString());
			logisticBilling.setUpdatedAt(Instant.now().toString());
			logisticOrder.setBilling(logisticBilling);
			
			
			
			logisticPayment.setSettlementDetails(respBody.getMessage().getOrder().getPayment().getSettlementDetails());
			logisticOrder.setPayment(logisticPayment);
			logisticinitMessage.setOrder(logisticOrder);
			logisticinitMessage.getOrder().getPayment().getSettlementDetails().get(0).setSettlementCounterparty("buyer-app");
			
			
			requestinitLogistic.setMessage(logisticinitMessage);

			final String json = this.jsonUtil.toJson((Object)requestinitLogistic);
			System.out.println("json - "+ json);

			final String url = logisticBppObj_list.get(0).getBppurl() + "init"; 
			//final String	url = "https://my.ithinklogistics.com/ondc/search"; 
			final HttpHeaders headers= this.mRetailRequestLogisticHeader.logisticbuildHeaders(json, configModel); 
			System.out.println("headers - "+ headers); 
			System.out.println("url - "+ url); 
			this.sender.send(url, headers, json, configModel.getMatchedApi());
			System.out.println("end of creatingLogisticselectRequestStructure");
	}
	public void creatingLogisticinitRequestStructurefood(final Schema request, OnSchema respBody, List<LogisticBppObj> logisticBppObj_list, String strtransactionid, String strmessageid) { 
		RetailRequestInitLogistic.log.info("Going to create Logistic init RequestStructure food. in creatingLogisticinitRequestStructurefood method..");
		final com.bb.beckn.init.extension.Schema requestinitLogistic = new com.bb.beckn.init.extension.Schema();


		final ConfigModel configModel =	this.configService.loadApplicationConfiguration(request.getContext().getBapId(), "init");

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
		ctx.setAction("init");
		ctx.setTimestamp(Instant.now().toString()); 
		requestinitLogistic.setContext(ctx);

		logisticinitMessage = new InitMessage();

		if (logisticOrder==null) { 
			logisticOrder =new Order(); 
		}
		logisticinitMessage.setOrder(logisticOrder); 
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
		
		for(int i=0; i< logisticBppObj_list.size(); i++) {
			final com.bb.beckn.search.extension.OnSchema model = 
					(com.bb.beckn.search.extension.OnSchema)this.jsonUtil.toModel(logisticBppObj_list.get(i).getJson(), (Class)com.bb.beckn.search.extension.OnSchema.class);
			for(int ii=0; ii< model.getMessage().getCatalog().getBppProviders().size(); ii++) {
				if(model.getMessage().getCatalog().getBppProviders().get(ii).getId().equalsIgnoreCase(logisticBppObj_list.get(i).getProviderid())) {
					
					logisticProvider.setId(logisticBppObj_list.get(i).getProviderid());
					//logisticProvider.setDescriptor(model.getMessage().getCatalog().getBppProviders().get(ii).getDescriptor());
					if(model.getMessage().getCatalog().getBppProviders().get(ii).getLocations()!= null) {
						logisticProvider.setLocations(model.getMessage().getCatalog().getBppProviders().get(ii).getLocations());
						logisticOrder.setProvider(logisticProvider);
						
					}
				}
			}
			
		}
		logisticDescriptor =new Descriptor();
		logisticItemList = new ArrayList<Item>();
		for(int i=0; i< logisticBppObj_list.size(); i++) {
			final com.bb.beckn.search.extension.OnSchema model = 
					(com.bb.beckn.search.extension.OnSchema)this.jsonUtil.toModel(logisticBppObj_list.get(i).getJson(), (Class)com.bb.beckn.search.extension.OnSchema.class);
			for(int ii=0; ii< model.getMessage().getCatalog().getBppProviders().size(); ii++) {
				if(model.getMessage().getCatalog().getBppProviders().get(ii).getId()!=null && model.getMessage().getCatalog().getBppProviders().get(ii).getId().equalsIgnoreCase(logisticBppObj_list.get(i).getProviderid())) {
					for(int k=0; k < model.getMessage().getCatalog().getBppProviders().get(ii).getItems().size(); k++){
						if(model.getMessage().getCatalog().getBppProviders().get(ii).getItems()!= null && 
								model.getMessage().getCatalog().getBppProviders().get(ii).getItems().get(k).getParentItemId().equalsIgnoreCase("")	) {
							
							logisticItem.setId(model.getMessage().getCatalog().getBppProviders().get(ii).getItems().get(k).getId());
							logisticItem.setCategoryId(model.getMessage().getCatalog().getBppProviders().get(ii).getItems().get(k).getCategoryId());
							logisticDescriptor.setCode(model.getMessage().getCatalog().getBppProviders().get(ii).getItems().get(k).getDescriptor().getCode());
							logisticItem.setDescriptor(logisticDescriptor);
							logisticItemList.add(logisticItem);
							logisticOrder.setItems(logisticItemList);
						}
					}
					
				}
				
			}
		}
		logisticIFulfillmentList = new ArrayList<Fulfillment>();
		logisticFulfillment.setId(request.getMessage().getOrder().getFulfillments().get(0).getId());
		logisticFulfillment.setType("CoD");
		logisticFulfillment.setEnd(request.getMessage().getOrder().getFulfillments().get(0).getEnd());
		logisticFulfillment.setStart(new Start());
		logisticFulfillment.getStart().setLocation(new Location());
		logisticFulfillment.getStart().getLocation().setGps(respBody.getMessage().getOrder().getProvider().getLocations().get(0).getGps());
		logisticFulfillment.getStart().getLocation().setAddress(respBody.getMessage().getOrder().getProvider().getLocations().get(0).getAddress());
		
		//logisticFulfillment.getStart().setLocation(respBody.getMessage().getOrder().getFulfillments().get(0).getStart().getLocation());
		if(logisticFulfillment.getStart().getContact()==null) {
			logisticFulfillment.getStart().setContact(new Contact());				
		}
		providercontact = new Contact();
		providercontact.setPhone(respBody.getMessage().getOrder().getFulfillments().get(0).getStart().getContact().getPhone());
		logisticFulfillment.getStart().setContact(providercontact);
		logisticFulfillment.getStart().getContact().setPhone(respBody.getMessage().getOrder().getFulfillments().get(0).getStart().getContact().getPhone());
		logisticIFulfillmentList.add(logisticFulfillment);
		//respBody.getMessage().getOrder().getFulfillments().get(0).getStart().getLocation().setGps(respBody.getMessage().getOrder().getProvider().getLocations().get(0).getGps());
		//respBody.getMessage().getOrder().getFulfillments().get(0).getStart().getLocation().setAddress(respBody.getMessage().getOrder().getProvider().getLocations().get(0).getAddress());
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
		logisticBilling.setTaxNumber("123456");
		logisticBilling.setPhone("9890196855");
		logisticBilling.setCreatedAt(Instant.now().toString());
		logisticBilling.setUpdatedAt(Instant.now().toString());
		logisticOrder.setBilling(logisticBilling);
		
		
		
		logisticPayment.setSettlementDetails(respBody.getMessage().getOrder().getPayment().getSettlementDetails());
		logisticOrder.setPayment(logisticPayment);
		logisticinitMessage.setOrder(logisticOrder);
		logisticinitMessage.getOrder().getPayment().getSettlementDetails().get(0).setSettlementCounterparty("buyer-app");
		
		
		requestinitLogistic.setMessage(logisticinitMessage);

		final String json = this.jsonUtil.toJson((Object)requestinitLogistic);
		System.out.println("json - "+ json);

		final String url = logisticBppObj_list.get(0).getBppurl() + "/init"; 
		//final String	url = "https://my.ithinklogistics.com/ondc/search"; 
		final HttpHeaders headers= this.mRetailRequestLogisticHeader.logisticbuildHeaders(json, configModel); 
		System.out.println("headers - "+ headers); 
		this.sender.send(url, headers, json, configModel.getMatchedApi());
		System.out.println("end of creatingLogisticselectRequestStructure");
	}	

	static {
		log = LoggerFactory.getLogger((Class)RetailRequestInitLogistic.class);
	}
}
