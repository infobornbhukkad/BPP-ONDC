// 
// Decompiled by Procyon v0.5.36
// 

package com.bb.beckn.select.service;

import org.slf4j.LoggerFactory;

import com.bb.beckn.common.exception.ErrorCode;
import com.bb.beckn.common.model.ConfigModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.bb.beckn.api.model.common.Context;
import com.bb.beckn.api.model.common.Error;
import com.bb.beckn.api.model.common.Order;
import com.bb.beckn.api.model.onselect.OnSelectMessage;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import com.bb.beckn.api.model.common.Ack;
import com.bb.beckn.ConnectionUtil;
import com.bb.beckn.api.enums.AckStatus;
import com.bb.beckn.api.model.response.ResponseMessage;
import com.bb.beckn.api.model.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.bb.beckn.select.controller.OnSelectControllerSeller;
import com.bb.beckn.select.extension.OnSchema;
import com.bb.beckn.select.extension.Schema;

import com.bb.beckn.select.utility.RetailResponseSelectfood;
import com.bb.beckn.select.utility.RetailResponseSelectkirana;

import org.springframework.http.HttpHeaders;
import com.bb.beckn.common.util.JsonUtil;
import com.bb.beckn.common.validator.BodyValidator;
import com.bb.beckn.repository.BapFeeRepository;
import com.bb.beckn.repository.ItemRepository;
import com.bb.beckn.repository.ItemRepositoryKirana;
import com.bb.beckn.repository.KiranaRepository;
import com.bb.beckn.repository.LogisticFinderRepository;
import com.bb.beckn.search.controller.OnSearchControllerSeller;
import com.bb.beckn.search.model.BapFeeObj;
import com.bb.beckn.search.model.ItemDetailsKirana;
import com.bb.beckn.search.model.ItemObj;
import com.bb.beckn.search.model.KiranaObj;
import com.bb.beckn.search.model.LogisticBppObj;
import com.bb.beckn.search.utility.RetailResponseSearchfood;
import com.bb.beckn.search.utility.RetailResponseSearchkirana;
import com.bb.beckn.common.service.ApplicationConfigService;
import com.bb.beckn.common.sender.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class SelectServiceSeller
{
    private static final Logger log;
    
    @Autowired
	ItemRepository mItemRepository;
    @Autowired
	ItemRepositoryKirana mItemRepositoryKirana;
    @Autowired
	KiranaRepository mKiranaRepository;
    @Autowired
    private RetailResponseSelectkirana retailResponseSelectkirana;
    @Autowired
    private RetailResponseSelectfood retailResponseSelectfood;
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
    private OnSelectControllerSeller onSelectControllerSeller;
    String strTosearchinDomain="";
    List<ItemObj> itemDetailsFood = null;
    List<ItemDetailsKirana> itemDetailsKirana =null;
    
    public ResponseEntity<String> select(final HttpHeaders httpHeaders, final Schema request) throws JsonProcessingException {
        SelectServiceSeller.log.info("Going to validate json request before sending to buyer...");
        final Response errorResponse = this.bodyValidator.validateRequestBody(request.getContext(), "select");
        if (errorResponse != null) {
            return (ResponseEntity<String>)new ResponseEntity((Object)this.mapper.writeValueAsString((Object)errorResponse), HttpStatus.BAD_REQUEST);
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
    	
        final Response adaptorResponse = new Response();
        final ResponseMessage resMsg = new ResponseMessage();
        resMsg.setAck(new Ack(AckStatus.ACK));
        adaptorResponse.setMessage(resMsg);
        final Context ctx = request.getContext();
        adaptorResponse.setContext(ctx);
       
        CompletableFuture.runAsync(() -> sendRequestToSellerInternalApi(httpHeaders, request));
        return (ResponseEntity<String>)new ResponseEntity((Object)this.mapper.writeValueAsString((Object)adaptorResponse), HttpStatus.OK);
    }
    
    private void sendRequestToSellerInternalApi(final HttpHeaders httpHeaders, final Schema request) {
        SelectServiceSeller.log.info("sending request to seller internal api [in seperate thread]");
        try {
            final Context context = request.getContext();
            final String bppId = context.getBppId();
            final String action = context.getAction();
            SelectServiceSeller.log.info("The bppId is {} and action is {}", (Object)bppId, (Object)action);
            final ConfigModel configModel = this.configService.loadApplicationConfiguration(bppId, "select");
            final String url = configModel.getMatchedApi().getHttpEntityEndpoint();
            final String json = this.jsonUtil.toJson((Object)request);
            
            OnSchema respBody = new OnSchema();
            respBody.setContext(request.getContext());
            respBody.getContext().setAction("on_select");
            respBody.getContext().setBppId(configModel.getSubscriberId());
            respBody.getContext().setBppUri(configModel.getSubscriberUrl());
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            respBody.getContext().setTimestamp(Instant.now().toString());
            //String ttl= ""+Duration.ofMinutes(Long.parseLong(configModel.getBecknTtl()));
            //respBody.getContext().setTtl(ttl);
            //respBody.getContext().setKey(configModel.getKeyid());
            
            respBody.setMessage(new OnSelectMessage());
            if(respBody.getMessage().getOrder()==null) {
            	respBody.getMessage().setOrder(new Order());
            }
			            
            String host = httpHeaders.get("remoteHost").get(0);
            if("127.0.0.1".equals(host)) {
            	host="localhost:8082/seller/adaptor";
            }else {
            	host = configModel.getBecknGateway();
            }
            
            //if(request.getContext().getDomain().equalsIgnoreCase("nic2004:52110")) {
            if(strTosearchinDomain.equalsIgnoreCase("Grocery")) {
           	 respBody = retailResponseSelectkirana.fetchResult(request, configModel, host, respBody,itemDetailsKirana);
            }else if (strTosearchinDomain.equalsIgnoreCase("Food")){
           	 respBody = retailResponseSelectfood.fetchResult(request, configModel, host, respBody, itemDetailsFood);
            }
            
            String respJson = this.jsonUtil.toJson((Object)respBody);            
            onSelectControllerSeller.onSelect(respJson, httpHeaders);
            
        }
        catch (Exception e) {
            SelectServiceSeller.log.error("error while sending post request to seller internal api" + e);
            e.printStackTrace();
        }
    }
     
    static {
        log = LoggerFactory.getLogger((Class)SelectServiceSeller.class);
    }
}
