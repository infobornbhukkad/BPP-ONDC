// 
// Decompiled by Procyon v0.5.36
// 

package com.bb.beckn.status.service;

import com.bb.beckn.common.service.OrderStateService;
import org.slf4j.LoggerFactory;
import com.bb.beckn.common.model.ConfigModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.bb.beckn.api.model.common.Context;
import com.bb.beckn.api.model.onstatus.OnStatusMessage;
import com.bb.beckn.api.model.onstatus.OnStatusRequest;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import com.bb.beckn.api.model.common.Ack;
import com.bb.beckn.api.enums.AckStatus;
import com.bb.beckn.api.model.response.ResponseMessage;
import com.bb.beckn.api.model.response.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.bb.beckn.status.extension.Schema;
import com.bb.beckn.status.utility.RetailResponseStatusfood;
import com.bb.beckn.status.utility.RetailResponseStatuskirana;

import org.springframework.http.HttpHeaders;
import com.bb.beckn.common.util.JsonUtil;
import com.bb.beckn.common.validator.BodyValidator;
import com.bb.beckn.repository.ConfirmAuditSellerRepository;
import com.bb.beckn.search.model.ConfirmAuditSellerObj;
import com.bb.beckn.status.controller.OnStatusControllerSeller;
import com.bb.beckn.status.extension.OnSchema;
import com.bb.beckn.common.service.ApplicationConfigService;
import com.bb.beckn.common.sender.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;




@Service
public class StatusServiceSeller
{
    private static final Logger log;
    @Autowired
    RetailResponseStatuskirana mRetailResponseStatuskirana;
    @Autowired
    RetailResponseStatusfood mRetailResponseStatusfood;
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
    @Value("classpath:dummyResponses/onStatus.json")
    private Resource resource;
    @Autowired
    ConfirmAuditSellerRepository mconfirmAuditSellerRepository;
    @Autowired
    private OnStatusControllerSeller onStatusControllerSeller;
        
    public ResponseEntity<String> status(final HttpHeaders httpHeaders, final Schema request) throws JsonProcessingException {
        StatusServiceSeller.log.info("Going to validate json request before sending to buyer...");
        final Response errorResponse = this.bodyValidator.validateRequestBody(request.getContext(), "status");
        if (errorResponse != null) {
            return (ResponseEntity<String>)new ResponseEntity((Object)this.mapper.writeValueAsString((Object)errorResponse), HttpStatus.BAD_REQUEST);
        }
        List<ConfirmAuditSellerObj> mconfirmAuditSellerObjlist= mconfirmAuditSellerRepository.findByorderid(request.getMessage().getOrderId());
        ConfirmAuditSellerObj mconfirmAuditSellerObj1=null;
        if(mconfirmAuditSellerObjlist.size()==0) {
        	return (ResponseEntity<String>)new ResponseEntity((Object)this.mapper.writeValueAsString((Object)errorResponse), HttpStatus.BAD_REQUEST);
        }
        
        for(int i=0; i < mconfirmAuditSellerObjlist.size(); i++) {
        	if(mconfirmAuditSellerObjlist.get(i).getSellerorderstate().equalsIgnoreCase("cancelled")){
        		return (ResponseEntity<String>)new ResponseEntity((Object)this.mapper.writeValueAsString((Object)errorResponse), HttpStatus.BAD_REQUEST);
        	}else if(mconfirmAuditSellerObjlist.get(i).getSellerorderstate().equalsIgnoreCase("Accepted")){
        		mconfirmAuditSellerObj1=mconfirmAuditSellerObjlist.get(i);
        	}
        }
        //final strTosearchinDomain =mconfirmAuditSellerObjlist.get(0).getDomaintype();
        final ConfirmAuditSellerObj mconfirmAuditSellerObj =mconfirmAuditSellerObj1;
        final Response adaptorResponse = new Response();
        final ResponseMessage resMsg = new ResponseMessage();
        resMsg.setAck(new Ack(AckStatus.ACK));
        adaptorResponse.setMessage(resMsg);
        final Context ctx = request.getContext();
        adaptorResponse.setContext(ctx);
         
        //CompletableFuture.runAsync(this::lambda$status$0);
        CompletableFuture.runAsync(() -> {
            this.sendRequestToSellerInternalApi(httpHeaders, request, mconfirmAuditSellerObj);
        });

        return (ResponseEntity<String>)new ResponseEntity((Object)this.mapper.writeValueAsString((Object)adaptorResponse), HttpStatus.OK);
    }
    
    private void sendRequestToSellerInternalApi(final HttpHeaders httpHeaders, final Schema request, ConfirmAuditSellerObj mconfirmAuditSellerObj) {
        StatusServiceSeller.log.info("sending request to seller internal api [in seperate thread]");
        try {
        	
        	final Context context = request.getContext();
            final String bppId = context.getBppId();
            final ConfigModel configModel = this.configService.loadApplicationConfiguration(request.getContext().getBppId(), "status");
            final String json = this.jsonUtil.toJson((Object)request);
            
            OnSchema respBody = new OnSchema();
            respBody.setContext(request.getContext());
            respBody.getContext().setAction("on_status");
            respBody.getContext().setBppId(configModel.getSubscriberId());
            respBody.getContext().setBppUri(configModel.getSubscriberUrl());           
            respBody.getContext().setTimestamp(Instant.now().toString());
            String ttl= ""+Duration.ofMinutes(Long.parseLong(configModel.getBecknTtl()));
            respBody.getContext().setTtl(ttl);
            
            String strtransactionid =request.getContext().getTransactionId()+"Logistic";		
    		String strmessageid =request.getContext().getMessageId()+"Logistic";	
            
            if(mconfirmAuditSellerObj.getDomaintype().equalsIgnoreCase("Grocery")) {
             	 respBody = this.mRetailResponseStatuskirana.fetchResult(request, configModel, respBody, strtransactionid, strmessageid, mconfirmAuditSellerObj);
            }else if (mconfirmAuditSellerObj.getDomaintype().equalsIgnoreCase("Food")) {
             	 respBody = this.mRetailResponseStatusfood.fetchResult(request, configModel,  respBody, strtransactionid, strmessageid, mconfirmAuditSellerObj);
            }
             String respJson = this.jsonUtil.toJson((Object)respBody);
            
             onStatusControllerSeller.onStatus(respJson, httpHeaders);
        }
        catch (Exception e) {
            StatusServiceSeller.log.error("error while sending post request to seller internal api" + e);
            e.printStackTrace();
        }
    }
    
    static {
        log = LoggerFactory.getLogger((Class)StatusServiceSeller.class);
    }
}
