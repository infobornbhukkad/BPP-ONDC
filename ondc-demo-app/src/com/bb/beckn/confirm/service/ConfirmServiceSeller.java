// 
// Decompiled by Procyon v0.5.36
// 

package com.bb.beckn.confirm.service;

import com.bb.beckn.api.model.onconfirm.OnConfirmMessage;
import com.bb.beckn.api.model.onconfirm.OnConfirmRequest;
import com.bb.beckn.api.model.oninit.OnInitMessage;
import com.bb.beckn.common.service.OrderStateService;
import org.slf4j.LoggerFactory;

import com.bb.beckn.common.exception.ErrorCode;
import com.bb.beckn.common.model.ConfigModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.bb.beckn.api.model.common.Context;
import com.bb.beckn.api.model.common.Error;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import com.bb.beckn.api.model.common.Ack;
import com.bb.beckn.api.enums.AckStatus;
import com.bb.beckn.api.model.response.ResponseMessage;
import com.bb.beckn.api.model.response.Response;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.bb.beckn.confirm.extension.Schema;
import com.bb.beckn.confirm.utility.RetailResponseConfirmfood;
import com.bb.beckn.confirm.utility.RetailResponseConfirmkirana;
import com.bb.beckn.confirm.controller.OnConfirmControllerSeller;
import com.bb.beckn.confirm.extension.OnSchema;
import com.bb.beckn.repository.ItemRepository;
import com.bb.beckn.repository.ItemRepositoryKirana;
import com.bb.beckn.repository.KiranaRepository;
import com.bb.beckn.search.model.ItemDetailsKirana;
import com.bb.beckn.search.model.ItemObj;
import com.bb.beckn.search.model.KiranaObj;

import org.springframework.http.HttpHeaders;
import com.bb.beckn.common.util.JsonUtil;
import com.bb.beckn.common.validator.BodyValidator;
import com.bb.beckn.common.service.ApplicationConfigService;
import com.bb.beckn.common.sender.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class ConfirmServiceSeller
{
    private static final Logger log;
    
    @Autowired
	ItemRepository mItemRepository;
    @Autowired
    RetailResponseConfirmfood retailResponseConfirmfood;
    @Autowired
    RetailResponseConfirmkirana retailResponseConfirmkirana;
    @Autowired
    OnConfirmControllerSeller onConfirmControllerSeller;
    @Autowired
   	KiranaRepository mKiranaRepository;
    @Autowired
   	ItemRepositoryKirana mItemRepositoryKirana;
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
    private OrderStateService orderStateService;
    
    String strTosearchinDomain="";
    List<ItemObj> itemDetailsFood = null;
    List<ItemDetailsKirana> itemDetailsKirana =null;
    
    public ResponseEntity<String> confirm(final HttpHeaders httpHeaders, final Schema request) throws JsonProcessingException {
        ConfirmServiceSeller.log.info("Going to validate json request before sending to buyer...");
        final Response errorResponse = this.bodyValidator.validateRequestBody(request.getContext(), "confirm");
        if (errorResponse != null) {
            return (ResponseEntity<String>)new ResponseEntity((Object)this.mapper.writeValueAsString((Object)errorResponse), HttpStatus.BAD_REQUEST);
        }
        if(request.getMessage().getOrder().getState()== null && request.getMessage().getOrder().getItems()==null && request.getMessage().getOrder().getBilling()== null
        		&& request.getMessage().getOrder().getFulfillments()==null && request.getMessage().getOrder().getQuote()==null && request.getMessage().getOrder().getPayment()==null) {
        	return (ResponseEntity<String>)new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        String search_vendorid = request.getMessage().getOrder().getProvider().getId();
        
        int itemSize= request.getMessage().getOrder().getItems().size();
        boolean bPresent=false;
        List<Long> itemQery= new ArrayList<Long>();
        for(int k=0; k< itemSize; k++) {
        	itemQery.add(Long.parseLong(request.getMessage().getOrder().getItems().get(k).getId()));
        }
        itemDetailsFood = new ArrayList<ItemObj>();
        itemDetailsKirana = new ArrayList<ItemDetailsKirana>();
        
        itemDetailsFood= mItemRepository.findBylistItemidandvendorid(itemQery,request.getMessage().getOrder().getProvider().getId());
        
        if(itemDetailsFood.isEmpty()) {
        	
        	itemDetailsKirana = mItemRepositoryKirana.findBylistItemidandvendorid(itemQery,request.getMessage().getOrder().getProvider().getId());
        }
        
        //for(int k=0; k< itemSize; k++) {
        	//ItemObj itemDetailsFood= mItemRepository.findByItemidandvendorid(Long.parseLong(request.getMessage().getOrder().getItems().get(k).getId()),	request.getMessage().getOrder().getProvider().getId());
        	if(itemDetailsFood!=null && itemDetailsFood.size() == itemSize) {
        		bPresent=true;
        		strTosearchinDomain="food";
        		//continue;
        	}else {
        		bPresent=false;
        		//break;
        	}
        //}
        if(bPresent==false) {
        	//for(int k=0; k< itemSize; k++) {
        		//ItemDetailsKirana itemDetailsKirana = mItemRepositoryKirana.findByItemidandvendorid(Long.parseLong(request.getMessage().getOrder().getItems().get(k).getId()),
        				//request.getMessage().getOrder().getProvider().getId());
            	if(itemDetailsKirana!=null && itemDetailsKirana.size() == itemSize) {
            		bPresent=true;
            		strTosearchinDomain="Grocery";
            		//continue;
            	}else {
            		bPresent=false;
            		//break;
            	}
           // }
        	
        }
        //List<KiranaObj> kiranaObjList =new ArrayList<KiranaObj>();
    	//Optional<KiranaObj> kiranaObjopt= mKiranaRepository.findBydata(Long.parseLong(search_vendorid));    	
    	//kiranaObjList = kiranaObjopt.map(Collections::singletonList).orElse(Collections.emptyList());
    	//int vSize= kiranaObjList.size();
    	// if provider is tampered in request
    	//if(vSize == 0) {
    	if(bPresent==false) {
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
    	// if item id is tampered in request
		/*
		 * int itemSize= request.getMessage().getOrder().getItems().size(); for (int i
		 * =0; i<itemSize; i++) { ItemDetailsKirana itemDetailsKirana=
		 * mItemRepositoryKirana.findByItemidandvendorid(Long.parseLong(request.
		 * getMessage().getOrder().getItems().get(i).getId()),
		 * request.getMessage().getOrder().getProvider().getId()); if (itemDetailsKirana
		 * == null ) { final Response adaptorResponse = new Response(); final
		 * ResponseMessage resMsg = new ResponseMessage(); resMsg.setAck(new
		 * Ack(AckStatus.NACK)); adaptorResponse.setMessage(resMsg); final Error error =
		 * new Error(); error.setCode(ErrorCode.PROVIDER_NOTFOUND_ERROR.toString());
		 * error.setMessage("Item not found"); error.setCode("30004");
		 * adaptorResponse.setContext(request.getContext());
		 * adaptorResponse.setError(error);
		 * 
		 * return (ResponseEntity<String>)new
		 * ResponseEntity((Object)this.mapper.writeValueAsString((Object)adaptorResponse
		 * ), HttpStatus.BAD_REQUEST); } }
		 */
        final Response adaptorResponse = new Response();
        final ResponseMessage resMsg = new ResponseMessage();
        resMsg.setAck(new Ack(AckStatus.ACK));
        adaptorResponse.setMessage(resMsg);
        final Context ctx = request.getContext();
        adaptorResponse.setContext(ctx);

        CompletableFuture.runAsync(() -> {
            this.sendRequestToSellerInternalApi(httpHeaders, request);
        });

        return (ResponseEntity<String>)new ResponseEntity((Object)this.mapper.writeValueAsString((Object)adaptorResponse), HttpStatus.OK);
    }
    
    private void sendRequestToSellerInternalApi(final HttpHeaders httpHeaders, final Schema request) {
        ConfirmServiceSeller.log.info("sending request to seller internal api [in seperate thread]");
        try {
            final Context context = request.getContext();
            final String bppId = context.getBppId();
            final ConfigModel configModel = this.configService.loadApplicationConfiguration(bppId, "confirm");
            //final String url = configModel.getMatchedApi().getHttpEntityEndpoint();
            final String json = this.jsonUtil.toJson((Object)request);
            
            OnSchema respBody = new OnSchema();
            respBody.setContext(request.getContext());
            respBody.getContext().setAction("on_confirm");
            respBody.getContext().setBppId(configModel.getSubscriberId());
            respBody.getContext().setBppUri(configModel.getSubscriberUrl());
           
            respBody.getContext().setTimestamp(Instant.now().toString());
            String ttl= ""+Duration.ofMinutes(Long.parseLong(configModel.getBecknTtl()));
            respBody.getContext().setTtl(ttl);
            //respBody.getContext().setKey(configModel.getKeyid());
            
            respBody.setMessage(new OnConfirmMessage());
			respBody.getMessage().setOrder(request.getMessage().getOrder());
            //this.sendRequest.send(url, httpHeaders, json, configModel.getMatchedApi());
			            
            String host = httpHeaders.get("remoteHost").get(0);
            if("0:0:0:0:0:0:0:1".equals(host)) {
                host="localhost";
            }
            
            //if(request.getContext().getDomain().equalsIgnoreCase("nic2004:52110")) {
            if(strTosearchinDomain.equalsIgnoreCase("Grocery")) {
              	 respBody = this.retailResponseConfirmkirana.fetchResult(request, configModel, host, respBody, itemDetailsKirana, strTosearchinDomain);
               }else //if (request.getContext().getDomain().equalsIgnoreCase("nic2004:60232")) {
            	   if (strTosearchinDomain.equalsIgnoreCase("Food")){
              	 respBody = this.retailResponseConfirmfood.fetchResult(request, configModel, host, respBody, itemDetailsFood, strTosearchinDomain);
               }
            
            //creating a dummy response
            //OnConfirmMessage onConfirm = this.mapper.readValue(this.resource.getInputStream(), OnConfirmMessage.class);
            //ConfirmServiceSeller.log.info(onConfirm.toString());

			/*
			 * OnConfirmRequest respBody = new OnConfirmRequest();
			 * respBody.setContext(request.getContext());
			 * respBody.getContext().setAction("on_confirm");
			 * respBody.getContext().setBppId(configModel.getSubscriberId());
			 * respBody.getContext().setBppUri(configModel.getSubscriberUrl());
			 * httpHeaders.remove("host");
			 * 
			 * respBody.setMessage(createResponseMessage(request)); String respJson =
			 * this.jsonUtil.toJson((Object)respBody);
			 * 
			 * 
			 * 
			 * String onConfirmResp =
			 * this.sendRequest.send(respBody.getContext().getBapUri() +"on_confirm",
			 * httpHeaders, respJson, configModel.getMatchedApi());
			 */
            String respJson = this.jsonUtil.toJson((Object)respBody);
            
            onConfirmControllerSeller.onConfirm(respJson, httpHeaders, strTosearchinDomain);
            
            //ConfirmServiceSeller.log.info(onConfirmResp);
        }
        catch (Exception e) {
            ConfirmServiceSeller.log.error("error while sending post request to seller internal api" + e);
            e.printStackTrace();
        }
    }

	/*
	 * private OnConfirmMessage createResponseMessage(final Schema request){
	 * OnConfirmMessage onConfirm =new OnConfirmMessage();
	 * onConfirm.setOrder(request.getMessage().getOrder());
	 * onConfirm.getOrder().setState("SEARCHING-FOR-FMD-AGENT"); String uniqueID =
	 * UUID.randomUUID().toString(); onConfirm.getOrder().setId(uniqueID);
	 * orderStateService.addOrder(onConfirm.getOrder()); return onConfirm; }
	 */
    static {
        log = LoggerFactory.getLogger((Class)ConfirmServiceSeller.class);
    }
}
