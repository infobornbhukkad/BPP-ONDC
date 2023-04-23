// 
// Decompiled by Procyon v0.5.36
// 

package com.bb.beckn.select.controller;

import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.bb.beckn.common.model.ConfigModel;
import org.springframework.http.HttpStatus;
import com.bb.beckn.common.model.ApiParamModel;
import com.bb.beckn.select.extension.OnSchema;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestBody;

import com.bb.beckn.api.enums.AckStatus;
import com.bb.beckn.api.model.common.Ack;
import com.bb.beckn.api.model.response.Response;
import com.bb.beckn.api.model.response.ResponseMessage;
import com.bb.beckn.common.builder.HeaderBuilder;
import com.bb.beckn.common.exception.ErrorCode;
import com.bb.beckn.common.service.ApplicationConfigService;
import com.bb.beckn.common.sender.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import com.bb.beckn.common.util.JsonUtil;
import com.bb.beckn.repository.ApiAuditSellerRepository;
import com.bb.beckn.search.model.ApiSellerObj;

import java.sql.Timestamp;

import org.slf4j.Logger;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OnSelectControllerSeller
{
    private static final Logger log;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
	ApiAuditSellerRepository mApiAuditSellerRepository;
    @Autowired
    private JsonUtil jsonUtil;
    @Autowired
    private Sender sender;
    @Autowired
    private ApplicationConfigService configService;
    @Autowired
    private HeaderBuilder authHeaderBuilder;
    
    @PostMapping({ "/seller/adaptor/on_select" })
    public ResponseEntity<String> onSelect(@RequestBody final String body, @RequestHeader final HttpHeaders httpHeaders) throws JsonProcessingException {
        OnSelectControllerSeller.log.info("The body in {} adaptor is {}", (Object)"select", (Object)this.jsonUtil.unpretty(body));
        try {
            final OnSchema model = (OnSchema)this.jsonUtil.toModel(body, (Class)OnSchema.class);
            final String bapUrl = model.getContext().getBapUri() + "on_select";
            final ConfigModel appConfigModel = this.configService.loadApplicationConfiguration(model.getContext().getBppId(), "select");
            final HttpHeaders headers = this.authHeaderBuilder.buildHeaders(body, appConfigModel);
            OnSelectControllerSeller.log.info("response in seller controller[which is will now send back to buyer adaptor] is {}", (Object)model.toString());
            this.getMySQLDataSource(body,  model);
            this.sender.send(bapUrl, headers, body, (ApiParamModel)null);
            
        }
        catch (Exception e) {
            OnSelectControllerSeller.log.error("Error while sending request back to buyer adaptor:", (Throwable)e);
            return (ResponseEntity<String>)new ResponseEntity((Object)"Error while sending request back to buyer adaptor", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return (ResponseEntity<String>)new ResponseEntity((Object)"Response received. All Ok", HttpStatus.OK);
    }
    
   public void getMySQLDataSource(final String body, final OnSchema request) {
    	
    	try {
    		//System.out.println("Schema--  "+ request);
    		Timestamp timestamp = new Timestamp(System.currentTimeMillis());   
    		ApiSellerObj myapiSellerObj = new ApiSellerObj(request.getContext().getMessageId(), request.getContext().getTransactionId(),
    				request.getContext().getAction(), request.getContext().getDomain(), request.getContext().getCoreVersion(),
    				timestamp.toString(), body, "N",
    				"NA");
    		mApiAuditSellerRepository.saveAndFlush(myapiSellerObj);
    		
    		System.out.println("Data Inserted in OnSelectControllerSeller  ");    
		}catch(Exception e) {
			System.out.println("Exception--"+ e);
		}
		
	}
   
    static {
        log = LoggerFactory.getLogger((Class)OnSelectControllerSeller.class);
    }
}
