// 
// Decompiled by Procyon v0.5.36
// 

package com.bb.beckn.confirm.controller;

import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.bb.beckn.common.model.ConfigModel;
import org.springframework.http.HttpStatus;
import com.bb.beckn.common.model.ApiParamModel;
import com.bb.beckn.confirm.extension.OnSchema;
import com.bb.beckn.repository.ApiAuditSellerRepository;
import com.bb.beckn.repository.ConfirmAuditSellerRepository;
import com.bb.beckn.search.model.ApiSellerObj;
import com.bb.beckn.search.model.ConfirmAuditSellerObj;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestBody;
import com.bb.beckn.common.builder.HeaderBuilder;
import com.bb.beckn.common.service.ApplicationConfigService;
import com.bb.beckn.common.sender.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import com.bb.beckn.common.util.JsonUtil;

import java.sql.Timestamp;

import org.slf4j.Logger;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OnConfirmControllerSeller
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
	ApiAuditSellerRepository mApiAuditSellerRepository;
    @Autowired
    ConfirmAuditSellerRepository mConfirmAuditSellerRepository;
    
    @PostMapping({ "/seller/adaptor/on_confirm" })
    public ResponseEntity<String> onConfirm(@RequestBody final String body, @RequestHeader final HttpHeaders httpHeaders, String strTosearchinDomain) throws JsonProcessingException {
        OnConfirmControllerSeller.log.info("The body in {} adaptor is {}", (Object)"confirm", (Object)this.jsonUtil.unpretty(body));
        try {
            final OnSchema model = (OnSchema)this.jsonUtil.toModel(body, (Class)OnSchema.class);
            final String bppId = model.getContext().getBppId();
            final String bapUrl = model.getContext().getBapUri() + "on_confirm";;
            final ConfigModel appConfigModel = this.configService.loadApplicationConfiguration(bppId, "confirm");
            final HttpHeaders headers = this.authHeaderBuilder.buildHeaders(body, appConfigModel);
            OnConfirmControllerSeller.log.info("response in seller controller[which is will now send back to buyer adaptor] is {}", (Object)model.toString());
            this.getMySQLDataSource(body,  model, strTosearchinDomain);
            this.sender.send(bapUrl, headers, body, (ApiParamModel)null);
        }
        catch (Exception e) {
            OnConfirmControllerSeller.log.error("Error while sending request back to buyer adaptor:", (Throwable)e);
            return (ResponseEntity<String>)new ResponseEntity((Object)"Error while sending request back to buyer adaptor", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return (ResponseEntity<String>)new ResponseEntity((Object)"Response received. All Ok", HttpStatus.OK);
    }
    
    public void getMySQLDataSource(final String body, final OnSchema request, String strTosearchinDomain) {
 	   Timestamp timestamp = new Timestamp(System.currentTimeMillis());
     	try {
     		//System.out.println("Schema--  "+ request);
     		   
     		ApiSellerObj myapiSellerObj = new ApiSellerObj(request.getContext().getMessageId(), request.getContext().getTransactionId(),
     				request.getContext().getAction(), request.getContext().getDomain(), request.getContext().getCoreVersion(),
     				timestamp.toString(), body, "N",
     				"NA");
     		mApiAuditSellerRepository.saveAndFlush(myapiSellerObj);
     		
     		System.out.println("Data Inserted in OnSelectControllerSeller  ");    
 		}catch(Exception e) {
 			System.out.println("Exception--"+ e);
 		}
     	
		/*
		 * /// Data Inserted in ondc_audit_transaction String paymenttype =""; String
		 * paymentdoneby=""; String amountatconfirmation=""; String
		 * logisticprovidername=""; String logisticdeliverycharge="";
		 * if(request.getMessage().getOrder().getPayment().getStatus().equalsIgnoreCase(
		 * "Paid")) { paymenttype="Prepaid"; paymentdoneby="Buyer"; }else
		 * if(request.getMessage().getOrder().getPayment().getStatus().equalsIgnoreCase(
		 * "NOT-PAID")){ paymenttype="CoD"; paymentdoneby="seller"; }
		 * amountatconfirmation=request.getMessage().getOrder().getQuote().getPrice().
		 * getValue(); logisticprovidername=
		 * request.getMessage().getOrder().getFulfillments().get(0).getProviderName();
		 * 
		 * for(int i=0; i <
		 * request.getMessage().getOrder().getQuote().getBreakup().size(); i++) {
		 * if(request.getMessage().getOrder().getQuote().getBreakup().get(i).getTitle().
		 * equalsIgnoreCase("Delivery charges")) {
		 * logisticdeliverycharge=request.getMessage().getOrder().getQuote().getBreakup(
		 * ).get(i).getPrice().getValue(); } }
		 * 
		 * ConfirmAuditSellerObj myconfirmAuditSellerObj = new
		 * ConfirmAuditSellerObj(request.getMessage().getOrder().getId(),
		 * request.getContext().getTransactionId(),
		 * request.getMessage().getOrder().getState(), paymenttype, paymentdoneby,
		 * amountatconfirmation,
		 * logisticprovidername,request.getMessage().getOrder().getProvider().getId(),
		 * logisticdeliverycharge,"","","","","","","","","","","",
		 * timestamp.toString(),
		 * "",request.getMessage().getOrder().getPayment().getBuyerAppFinderFeeAmount(),
		 * request.getMessage().getOrder().getPayment().getBuyerAppFinderFeeType(),
		 * strTosearchinDomain,request.getContext().getBppId(),request.getContext().
		 * getBppUri());
		 * 
		 * mConfirmAuditSellerRepository.saveAndFlush(myconfirmAuditSellerObj);
		 * 
		 * System.out.println("Data Inserted in ondc_audit_seller_transaction ");
		 */
 		
 	}
    static {
        log = LoggerFactory.getLogger((Class)OnConfirmControllerSeller.class);
    }
}
