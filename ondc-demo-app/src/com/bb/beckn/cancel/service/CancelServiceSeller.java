// 
// Decompiled by Procyon v0.5.36
// 

package com.bb.beckn.cancel.service;

import org.slf4j.LoggerFactory;
import com.bb.beckn.common.model.ConfigModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.bb.beckn.api.model.common.Context;
import com.bb.beckn.api.model.common.Order;
import com.bb.beckn.api.model.common.Tags;
import com.bb.beckn.api.model.oncancel.OnCancelMessage;
import com.bb.beckn.api.model.onselect.OnSelectMessage;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import com.bb.beckn.api.model.common.Error;
import com.bb.beckn.api.model.common.Ack;
import com.bb.beckn.ConnectionUtil;
import com.bb.beckn.api.enums.AckStatus;
import com.bb.beckn.api.model.response.ResponseMessage;
import com.bb.beckn.api.model.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.bb.beckn.cancel.controller.OnCancelControllerSeller;
import com.bb.beckn.cancel.extension.Schema;
import org.springframework.http.HttpHeaders;
import com.bb.beckn.common.util.JsonUtil;
import com.bb.beckn.common.validator.BodyValidator;
import com.bb.beckn.confirm.utility.RetailResponseConfirmkirana;
import com.bb.beckn.fetchResult.controller.FetchResultControllerLogisticBuyer;
import com.bb.beckn.repository.ApiAuditSellerRepository;
import com.bb.beckn.repository.ConfirmAuditSellerRepository;
import com.bb.beckn.repository.LogisticFinderRepository;
import com.bb.beckn.search.model.ApiSellerObj;
import com.bb.beckn.search.model.ConfirmAuditSellerObj;
import com.bb.beckn.search.model.LogisticBppObj;
import com.bb.beckn.cancel.extension.OnSchema;
import com.bb.beckn.utility.RetailRequestCancelLogistic;
import com.bb.beckn.common.service.ApplicationConfigService;
import com.bb.beckn.common.sender.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class CancelServiceSeller
{
    private static final Logger log;
    @Autowired
    OnCancelControllerSeller onCancelControllerSeller;
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
    ConfirmAuditSellerRepository mConfirmAuditSellerRepository;
    @Autowired
	LogisticFinderRepository mLogisticFinderRepository;
    @Autowired
    RetailRequestCancelLogistic mRetailRequestCancelLogistic;
    @Autowired
	FetchResultControllerLogisticBuyer mfetchResultControllerLogisticBuyer;
    @Autowired
    ApiAuditSellerRepository mApiAuditSellerRepository;
    
    List<ConfirmAuditSellerObj> mConfirmAuditSellerObjlist=null;
    List<LogisticBppObj> logisticBppObj_list=null;
    final String strTosearchinDomain="";
    
    
	public ResponseEntity<String> cancel(final HttpHeaders httpHeaders, final Schema request, final ConfigModel configModel) throws JsonProcessingException, ParseException {
        CancelServiceSeller.log.info("Going to validate json request before sending to buyer...");
        final Response errorResponse = this.bodyValidator.validateRequestBody(request.getContext(), "cancel");
        if (errorResponse != null) {
            return (ResponseEntity<String>)new ResponseEntity((Object)this.mapper.writeValueAsString((Object)errorResponse), HttpStatus.BAD_REQUEST);
        }
        final Response adaptorResponse = new Response();
        final ResponseMessage resMsg = new ResponseMessage();
        resMsg.setAck(new Ack(AckStatus.ACK));
        adaptorResponse.setMessage(resMsg);
        final Context ctx = request.getContext();
        adaptorResponse.setContext(ctx);
        //add validation logic of verifying the SLA of days
        if(!configModel.getCancellable().equalsIgnoreCase("true")) {
        	resMsg.setAck(new Ack(AckStatus.NACK));
            adaptorResponse.setMessage(resMsg);
            adaptorResponse.getError().setCode("50001");
        	return (ResponseEntity<String>)new ResponseEntity((Object)this.mapper.writeValueAsString((Object)adaptorResponse), HttpStatus.BAD_REQUEST);
        }
        com.bb.beckn.select.extension.OnSchema model =null;
       
        List<ConfirmAuditSellerObj> mconfirmAuditSellerObjlist= mConfirmAuditSellerRepository.findByorderid(request.getMessage().getOrderId());
        
        if(mconfirmAuditSellerObjlist.size() == 0) {
        	resMsg.setAck(new Ack(AckStatus.NACK));
            adaptorResponse.setMessage(resMsg);
            adaptorResponse.getError().setCode("50001");
        	return (ResponseEntity<String>)new ResponseEntity((Object)this.mapper.writeValueAsString((Object)adaptorResponse), HttpStatus.BAD_REQUEST);
        }
        ConfirmAuditSellerObj tempmconfirmAuditSellerObj =null;
        
        for(int i=0; i <mconfirmAuditSellerObjlist.size(); i++ ) {
        	
        	if(mconfirmAuditSellerObjlist.get(i).getLogisticorderstate().equalsIgnoreCase("Completed") || mconfirmAuditSellerObjlist.get(i).getSellerorderstate().equalsIgnoreCase("Cancelled")) {
        		resMsg.setAck(new Ack(AckStatus.NACK));
                adaptorResponse.setMessage(resMsg);
                adaptorResponse.getError().setCode("50001");
        		return (ResponseEntity<String>)new ResponseEntity((Object)this.mapper.writeValueAsString((Object)adaptorResponse), HttpStatus.BAD_REQUEST);
        	}else if(mconfirmAuditSellerObjlist.get(i).getSellerorderstate().equalsIgnoreCase("Accepted")) {
        		tempmconfirmAuditSellerObj=mconfirmAuditSellerObjlist.get(i);
        	}
        }
        final ConfirmAuditSellerObj mconfirmAuditSellerObj=tempmconfirmAuditSellerObj;
        if (request.getMessage().getCancellationReasonId()!=null && request.getMessage().getCancellationReasonId().equals("004") || request.getMessage().getCancellationReasonId().equals("006")) {
        	
        	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
            Date parsedDate = dateFormat.parse(mconfirmAuditSellerObj.getCreationdate());
            Timestamp creationTimestamp = new java.sql.Timestamp(parsedDate.getTime());
        	
            Timestamp nowTimestamp = new Timestamp(System.currentTimeMillis());
            System.out.println("nowTimestamp -- "+ nowTimestamp);
            System.out.println("mconfirmAuditBuyerObj.getCreationdate() -- "+ mconfirmAuditSellerObj.getCreationdate());
            System.out.println("creationTimestamp.getTime() -- "+ creationTimestamp.getTime());
            System.out.println("nowTimestamp.getTime() -- "+ nowTimestamp.getTime());
            // Calculate the time difference between the two Timestamp objects
            long timeDiffMillis = nowTimestamp.getTime() - creationTimestamp.getTime();
            //long timeDiffSeconds = TimeUnit.MILLISECONDS.toSeconds(timeDiffMillis);
            long timeDiffMinutes = TimeUnit.MILLISECONDS.toMinutes(timeDiffMillis);
            //long timeDiffHours = TimeUnit.MILLISECONDS.toHours(timeDiffMillis);
           
            System.out.println("The time difference between " + creationTimestamp + " and " + nowTimestamp + " is " + timeDiffMillis + " minutes");
            ApiSellerObj mApiBuyerObj= mApiAuditSellerRepository.findByTransactionidandaction(mconfirmAuditSellerObj.getTransactionid(),"on_select");
             
            model = (com.bb.beckn.select.extension.OnSchema)this.jsonUtil.toModel(mApiBuyerObj.getJson(), (Class)com.bb.beckn.select.extension.OnSchema.class);
            String ordertodelivery= model.getMessage().getOrder().getFulfillments().get(0).getTat();
            Duration duration = Duration.parse(ordertodelivery);
            
            System.out.println("duration.toMinutes()---  "+ duration.toMinutes());
            if(duration.toMinutes() < timeDiffMinutes) {
            	final Error error = new Error();
            	error.setMessage("Delivery SLA is not Breached");
            	errorResponse.setError(error);
            	resMsg.setAck(new Ack(AckStatus.NACK));
                adaptorResponse.setMessage(resMsg);
                adaptorResponse.getError().setCode("50001");
            	return (ResponseEntity<String>)new ResponseEntity((Object)this.mapper.writeValueAsString((Object)errorResponse), HttpStatus.BAD_REQUEST);
            }
        }   
        
        CompletableFuture.runAsync(() -> {
            this.sendRequestToSellerInternalApi(httpHeaders, request,mconfirmAuditSellerObj, configModel);
        });
        
        return (ResponseEntity<String>)new ResponseEntity((Object)this.mapper.writeValueAsString((Object)adaptorResponse), HttpStatus.OK);
    }
    
    private void sendRequestToSellerInternalApi(final HttpHeaders httpHeaders, final Schema request,ConfirmAuditSellerObj mconfirmAuditSellerObj, final ConfigModel configModel) {
        CancelServiceSeller.log.info("sending request to seller internal api [in seperate thread]");
        try {
            final Context context = request.getContext();
            //final String json = this.jsonUtil.toJson((Object)request);
            String strtranstionId= request.getContext().getTransactionId()+"Logistic";
            String strmessageId= request.getContext().getMessageId()+"Logistic";
                       
            if(mconfirmAuditSellerObj.getDomaintype().equalsIgnoreCase("Grocery")) {
            	 mRetailRequestCancelLogistic.creatingLogisticcancelRequestStructureKirana(request, strtranstionId, strmessageId, mconfirmAuditSellerObj, configModel);
            }else if(mconfirmAuditSellerObj.getDomaintype().equalsIgnoreCase("Food")){
            	mRetailRequestCancelLogistic.creatingLogisticcancelRequestStructurefood(request, strtranstionId, strmessageId, mconfirmAuditSellerObj,configModel);
            }
            
            List<ApiSellerObj> apiLogisticSellerrObj_list = this.mfetchResultControllerLogisticBuyer.fetch(strtranstionId, strmessageId);
            
            OnSchema respBody = new OnSchema();
            respBody.setContext(request.getContext());
            respBody.getContext().setAction("on_cancel");
            //Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            respBody.getContext().setTimestamp(Instant.now().toString());
            respBody.setMessage(new OnCancelMessage());
            if(respBody.getMessage().getOrder()==null) {
            	respBody.getMessage().setOrder(new Order());
            }
            respBody.getMessage().getOrder().setId(request.getMessage().getOrderId());
            respBody.getMessage().getOrder().setTags(new Tags());
            respBody.getMessage().getOrder().getTags().setCancellationReasonId(request.getMessage().getCancellationReasonId());
            if(apiLogisticSellerrObj_list.size() != 0) {
            	respBody.getMessage().getOrder().setState("Cancelled");
            	setLogisticProvider(apiLogisticSellerrObj_list, strtranstionId, strmessageId);
            }else {
            	respBody.getMessage().getOrder().setState("TBD");
            }
            
           String respJson = this.jsonUtil.toJson((Object)respBody);            
           onCancelControllerSeller.onCancel(respJson, httpHeaders,strTosearchinDomain);
        }
        catch (Exception e) {
            CancelServiceSeller.log.error("error while sending post request to seller internal api" + e);
            e.printStackTrace();
        }
    }
    private void setLogisticProvider(List<ApiSellerObj> apiLogisticSellerrObj_list ,String strtransactionid, String strmessageid) {    	
    	CancelServiceSeller.log.info("Inside the CancelServiceSeller class of setLogisticProvider method...");
		
		com.bb.beckn.cancel.extension.OnSchema model=null;
		for(int i=0; i < apiLogisticSellerrObj_list.size(); i++) {
			try{
				model  = (com.bb.beckn.cancel.extension.OnSchema)this.jsonUtil.toModel(apiLogisticSellerrObj_list.get(i).getJson(), (Class)com.bb.beckn.cancel.extension.OnSchema.class);
			
			}catch(Exception e) {
				System.out.println(e.getMessage());
			}
		}
		
    	try {
    		
    		Timestamp timestamp = new Timestamp(System.currentTimeMillis());   
    		LogisticBppObj myapiLogisticBppObj = new LogisticBppObj(
    				"", 
    				model.getContext().getBppId(),model.getContext().getBppUri(),timestamp.toString(),"",this.jsonUtil.toJson((Object)model), strtransactionid, strmessageid, "","",
    				"", model.getContext().getAction(),"");
    		
    		mLogisticFinderRepository.save(myapiLogisticBppObj);
			System.out.println("data saved--LogisticBppObj  ");
    		
    	        
		}catch(Exception e) {
			System.out.println("Exception--"+ e);
		}
		
	 }
    static {
        log = LoggerFactory.getLogger((Class)CancelServiceSeller.class);
    }
}
