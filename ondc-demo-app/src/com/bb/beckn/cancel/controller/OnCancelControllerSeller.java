// 
// Decompiled by Procyon v0.5.36
// 

package com.bb.beckn.cancel.controller;

import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.bb.beckn.common.model.ConfigModel;
import org.springframework.http.HttpStatus;
import com.bb.beckn.common.model.ApiParamModel;
import com.bb.beckn.cancel.extension.OnSchema;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestBody;
import com.bb.beckn.common.builder.HeaderBuilder;
import com.bb.beckn.common.service.ApplicationConfigService;
import com.bb.beckn.common.sender.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import com.bb.beckn.common.util.JsonUtil;
import com.bb.beckn.repository.ApiAuditSellerRepository;
import com.bb.beckn.repository.ConfirmAuditSellerRepository;
import com.bb.beckn.search.model.ApiSellerObj;
import com.bb.beckn.search.model.ConfirmAuditSellerObj;

import java.sql.Timestamp;

import org.slf4j.Logger;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OnCancelControllerSeller
{
    private static final Logger log;
    @Autowired
    private JsonUtil jsonUtil;
    @Autowired
    private Sender sender;
    @Autowired
    private ApplicationConfigService configService;
    @Autowired
    private HeaderBuilder authHeaderBuilder;
    @Autowired
    ConfirmAuditSellerRepository mConfirmAuditSellerRepository;
    @Autowired
	ApiAuditSellerRepository mApiAuditSellerRepository;
    
    @PostMapping({ "/seller/adaptor/on_cancel" })
    public ResponseEntity<String> onCancel(@RequestBody final String body, @RequestHeader final HttpHeaders httpHeaders, String strTosearchinDomain) throws JsonProcessingException {
        OnCancelControllerSeller.log.info("The body in {} adaptor is {}", (Object)"cancel", (Object)this.jsonUtil.unpretty(body));
        try {
            final OnSchema model = (OnSchema)this.jsonUtil.toModel(body, (Class)OnSchema.class);
            final String bppId = model.getContext().getBppId();
            final String bapUrl = model.getContext().getBapUri() + "on_cancel";
            final ConfigModel appConfigModel = this.configService.loadApplicationConfiguration(bppId, "cancel");
            final HttpHeaders headers = this.authHeaderBuilder.buildHeaders(body, appConfigModel);
            OnCancelControllerSeller.log.info("response in seller controller[which is will now send back to buyer adaptor] is {}", (Object)model.toString());
            this.getMySQLDataSource(body,  model, strTosearchinDomain);
            this.sender.send(bapUrl, headers, body, (ApiParamModel)null);
        }
        catch (Exception e) {
            OnCancelControllerSeller.log.error("Error while sending request back to buyer adaptor:", (Throwable)e);
            return (ResponseEntity<String>)new ResponseEntity((Object)"Error while sending request back to buyer adaptor", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return (ResponseEntity<String>)new ResponseEntity((Object)"Response received. All Ok", HttpStatus.OK);
    }
    
    public void getMySQLDataSource(final String body, final OnSchema request, String strTosearchinDomain) {
       
    	OnCancelControllerSeller.log.info("Inside the method getMySQLDataSource of OnCancelControllerSeller class");
  	   Timestamp timestamp = new Timestamp(System.currentTimeMillis());
      	try {
      		//System.out.println("Schema--  "+ request);
      		   
      		ApiSellerObj myapiSellerObj = new ApiSellerObj(request.getContext().getMessageId(), request.getContext().getTransactionId(),
      				request.getContext().getAction(), request.getContext().getDomain(), request.getContext().getCoreVersion(),
      				timestamp.toString(), body, "N",
      				"NA");
      		mApiAuditSellerRepository.saveAndFlush(myapiSellerObj);
      		
      		System.out.println("Data Inserted in OnCancelControllerSeller api_audit_seller table in cancel");    
  		}catch(Exception e) {
  			System.out.println("Exception occurs while inserting the data in api_audit_seller table of  method getMySQLDataSource of OnCancelControllerSeller class" +  e);
  		}
      	
      	/// Data Inserted in ondc_audit_transaction
      	String canceltype ="";
    	String cancelamount="";
    	String camncelby="";
    	String cancelamountbearbybuyer="";
    	String cancelamountbearbyseller="";
    	String cancelamountbearbylogistic="";
    	String cancelreason="";
		      	
    	if(request.getMessage().getOrder().getTags().getCancellationReasonId().equalsIgnoreCase("001") || request.getMessage().getOrder().getTags().getCancellationReasonId().equalsIgnoreCase("003")||
    			request.getMessage().getOrder().getTags().getCancellationReasonId().equalsIgnoreCase("004")|| request.getMessage().getOrder().getTags().getCancellationReasonId().equalsIgnoreCase("006") ||
    			request.getMessage().getOrder().getTags().getCancellationReasonId().equalsIgnoreCase("009")|| request.getMessage().getOrder().getTags().getCancellationReasonId().equalsIgnoreCase("010") ||
    			request.getMessage().getOrder().getTags().getCancellationReasonId().equalsIgnoreCase("012"))
    	{
    		canceltype="Full";
    		cancelamount=
    		camncelby="Buyer";
    		cancelamountbearbybuyer="";
    		cancelreason=request.getMessage().getOrder().getTags().getCancellationReasonId();
    		
    	}
    	
    	if(request.getMessage().getOrder().getTags().getCancellationReasonId().equalsIgnoreCase("002") || request.getMessage().getOrder().getTags().getCancellationReasonId().equalsIgnoreCase("005"))
    	{
    		camncelby="Seller";
    	}
    	if(request.getMessage().getOrder().getTags().getCancellationReasonId().equalsIgnoreCase("008") || request.getMessage().getOrder().getTags().getCancellationReasonId().equalsIgnoreCase("011")||
    			request.getMessage().getOrder().getTags().getCancellationReasonId().equalsIgnoreCase("013")|| request.getMessage().getOrder().getTags().getCancellationReasonId().equalsIgnoreCase("014") ||
    			request.getMessage().getOrder().getTags().getCancellationReasonId().equalsIgnoreCase("015")|| request.getMessage().getOrder().getTags().getCancellationReasonId().equalsIgnoreCase("016") ||
    			request.getMessage().getOrder().getTags().getCancellationReasonId().equalsIgnoreCase("017") || request.getMessage().getOrder().getTags().getCancellationReasonId().equalsIgnoreCase("018") ||
    			request.getMessage().getOrder().getTags().getCancellationReasonId().equalsIgnoreCase("019"))
    	{
    		camncelby="LogisticsProvider";
    	}
    	
      	ConfirmAuditSellerObj myconfirmAuditSellerObj = new ConfirmAuditSellerObj(request.getMessage().getOrder().getId(), request.getContext().getTransactionId(), request.getMessage().getOrder().getState(),"Cancelled",
      			"",  "", "", "","","","","","","","","",canceltype,cancelamount,camncelby,cancelamountbearbybuyer,cancelamountbearbyseller,cancelamountbearbylogistic,cancelreason,
      			timestamp.toString(), "","", "", "","","");
      	
      	mConfirmAuditSellerRepository.saveAndFlush(myconfirmAuditSellerObj);
  		
  		System.out.println("Data Inserted in ondc_audit_seller_transaction cancel"); 
  		
  	}
    static {
        log = LoggerFactory.getLogger((Class)OnCancelControllerSeller.class);
    }
}
