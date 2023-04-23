// 
// Decompiled by Procyon v0.5.36
// 

package com.bb.beckn.search.controller;

import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.bb.beckn.common.model.ConfigModel;
import org.springframework.http.HttpStatus;
import com.bb.beckn.common.model.ApiParamModel;
import com.bb.beckn.search.extension.OnSchema;
import com.bb.beckn.search.extension.Schema;
import com.bb.beckn.search.model.ApiSellerObj;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestBody;

import com.bb.beckn.api.enums.AckStatus;
import com.bb.beckn.api.model.common.Ack;
import com.bb.beckn.api.model.common.Context;
import com.bb.beckn.api.model.response.Response;
import com.bb.beckn.api.model.response.ResponseMessage;
import com.bb.beckn.common.builder.HeaderBuilder;
import com.bb.beckn.common.service.ApplicationConfigService;
import com.bb.beckn.common.sender.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.bb.beckn.common.util.JsonUtil;
import com.bb.beckn.repository.ApiAuditSellerRepository;

import java.sql.Timestamp;
import com.bb.beckn.api.model.common.Error;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OnSearchControllerSeller
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
    
    
    @PostMapping({ "/seller/adaptor/on_search" })
    public ResponseEntity<String> onSearch(@RequestBody final String body, @RequestHeader final HttpHeaders httpHeaders) throws JsonProcessingException {
        OnSearchControllerSeller.log.info("The body in {} adaptor is {}", (Object)"search", (Object)this.jsonUtil.unpretty(body));
        try {
        	log.info("on_search response to be send back to buyer {}", body);
            final OnSchema model = (OnSchema)this.jsonUtil.toModel(body, (Class)OnSchema.class);
            final String bppId = model.getContext().getBppId();
            
            final ConfigModel appConfigModel = this.configService.loadApplicationConfiguration(bppId, "search");
            final HttpHeaders headers = this.authHeaderBuilder.buildHeaders(body, appConfigModel);
            String url = appConfigModel.getBecknGateway() + "/on_search";
            OnSearchControllerSeller.log.info("response in seller controller[which is will now send back to buyer adaptor] is {}", (Object)model.toString());
            this.sender.send(url, headers, body, appConfigModel.getMatchedApi());
            this.getMySQLDataSource(body,  model);
        }
        catch (Exception e) {
            OnSearchControllerSeller.log.error("Error while sending request back to buyer adaptor:", (Throwable)e);
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
    				request.getMessage().getCatalog().getBppProviders().get(0).getItems().get(0).getDescriptor().getName());
    		mApiAuditSellerRepository.save(myapiSellerObj);
    		
    		System.out.println("Data Inserted in OnSearchControllerSeller  ");    
		}catch(Exception e) {
			System.out.println("Exception--"+ e);
		}
		
	}
    
    static {
        log = LoggerFactory.getLogger((Class)OnSearchControllerSeller.class);
    }
}
