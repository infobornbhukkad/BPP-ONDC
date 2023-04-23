// 
// Decompiled by Procyon v0.5.36
// 

package com.bb.beckn.search.controller;

import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import com.bb.beckn.common.model.AuditDataModel;
import com.bb.beckn.common.model.AuditFlagModel;
import com.bb.beckn.common.model.HttpModel;
import com.bb.beckn.common.model.AuditModel;
import org.springframework.web.bind.annotation.PostMapping;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.bb.beckn.common.model.ConfigModel;
import com.bb.beckn.ConnectionUtil;
import com.bb.beckn.api.model.common.Context;
import org.springframework.http.HttpStatus;
import com.bb.beckn.api.model.lookup.LookupRequest;
import com.bb.beckn.common.enums.BecknUserType;
import com.bb.beckn.search.extension.OnSchema;
import com.bb.beckn.search.model.ApiSellerObj;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestBody;
import com.bb.beckn.common.validator.HeaderValidator;
import com.bb.beckn.repository.ApiAuditSellerRepository;
import com.bb.beckn.common.service.AuditService;
import com.bb.beckn.common.service.ApplicationConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import com.bb.beckn.common.util.JsonUtil;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.RestController;

import com.mysql.cj.jdbc.MysqlDataSource;

import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.PreparedStatement;
@RestController
public class OnSearchControllerLogisticBuyer
{
    private static final Logger log;
    @Autowired
   	ApiAuditSellerRepository mApiAuditSellerRepository;
    @Autowired
    private JsonUtil jsonUtil;
    @Autowired
    private ApplicationConfigService configService;
    @Autowired
    private AuditService auditService;
    @Autowired
    private HeaderValidator validator;
    
    @PostMapping( "/buyer/adaptor/on_search" )
    public ResponseEntity<String> onSearch(@RequestBody final String body, @RequestHeader final HttpHeaders httpHeaders) throws JsonProcessingException {
    	OnSearchControllerLogisticBuyer.log.info("The body in {} adaptor is {}", (Object)"search", (Object)this.jsonUtil.unpretty(body));
        try {
            final OnSchema model = (OnSchema)this.jsonUtil.toModel(body, (Class)OnSchema.class);
            OnSearchControllerLogisticBuyer.log.info("data in buyer controller adaptor [which is will now send back to buyer internal api] is {}", (Object)model.toString());
            final Context context = model.getContext();
            final String bppId = context.getBppId();
            final ConfigModel configuration = this.configService.loadApplicationConfiguration(context.getBapId(), "search");
            final boolean authenticate = configuration.getMatchedApi().isHeaderAuthentication();
            OnSearchControllerLogisticBuyer.log.info("does seller {} requires to be authenticated ? {}", (Object)bppId, (Object)authenticate);
            if (authenticate) {
                final LookupRequest lookupRequest = new LookupRequest((String)null, context.getCountry(), context.getCity(), context.getDomain(), BecknUserType.BPP.type());
                this.validator.validateHeader(context.getBppId(), httpHeaders, body, lookupRequest);
            }
            //System.out.println("before database");
            this.getMySQLDataSource(body,  model);
            //this.auditService.audit(this.buildAuditModel(httpHeaders, body, model));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return (ResponseEntity<String>)new ResponseEntity((Object)"Response received. All Ok", HttpStatus.OK);
    }
    
	    
    public void getMySQLDataSource(final String body, final OnSchema request) {
    	
    	try {
    		System.out.println("Schema--  "+ request);
    		Timestamp timestamp = new Timestamp(System.currentTimeMillis());   
    		ApiSellerObj myapiSellerObj = new ApiSellerObj(request.getContext().getMessageId(), request.getContext().getTransactionId(),
    				request.getContext().getAction(), request.getContext().getDomain(), request.getContext().getCoreVersion(),
    				timestamp.toString(), body, "N",
    				"NA");
    		mApiAuditSellerRepository.save(myapiSellerObj);
    		
    		System.out.println("Data Inserted in OnSearchControllerSeller  ");    
		}catch(Exception e) {
			System.out.println("Exception--"+ e);
		}
		
	}
    
    static {
        log = LoggerFactory.getLogger((Class)OnSearchControllerLogisticBuyer.class);
    }
}
