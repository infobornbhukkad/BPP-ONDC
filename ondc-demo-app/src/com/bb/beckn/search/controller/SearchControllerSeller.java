// 
// Decompiled by Procyon v0.5.36
// 

package com.bb.beckn.search.controller;

import org.slf4j.LoggerFactory;



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.bb.beckn.common.model.AuditDataModel;
import com.bb.beckn.common.model.AuditFlagModel;
import com.bb.beckn.common.model.HttpModel;
import com.bb.beckn.common.model.AuditModel;
import org.springframework.web.bind.annotation.PostMapping;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.bb.beckn.common.model.ConfigModel;
import com.bb.beckn.ConnectionUtil;
import com.bb.beckn.api.model.common.Context;
import com.bb.beckn.api.model.lookup.LookupRequest;
import com.bb.beckn.common.enums.BecknUserType;
import com.bb.beckn.search.extension.Schema;
import com.bb.beckn.search.model.ApiSellerObj;
import com.bb.beckn.common.exception.ApplicationException;
import com.bb.beckn.common.exception.ErrorCode;
import com.bb.beckn.common.enums.OndcUserType;
import org.springframework.http.ResponseEntity;
import javax.servlet.http.HttpServletRequest;


import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.beans.factory.annotation.Value;
import com.bb.beckn.common.service.AuditService;
import com.bb.beckn.common.service.ApplicationConfigService;
import com.bb.beckn.common.util.JsonUtil;
import com.bb.beckn.common.validator.HeaderValidator;
import com.bb.beckn.repository.ApiAuditSellerRepository;
import com.bb.beckn.repository.KiranaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import com.bb.beckn.search.service.SearchServiceSeller;
import com.bb.beckn.select.controller.SelectControllerSeller;

import org.slf4j.Logger;
import org.springframework.web.bind.annotation.RestController;
import com.mysql.cj.jdbc.MysqlDataSource;

@RestController
public class SearchControllerSeller
{
    private static final Logger log;
    @Autowired
	ApiAuditSellerRepository mApiAuditSellerRepository;
    @Autowired
    private SearchServiceSeller service;
    @Autowired
    private HeaderValidator validator;
    @Autowired
    private JsonUtil jsonUtil;
    @Autowired
    private ApplicationConfigService configService;
    @Autowired
    private AuditService auditService;
    @Value("${beckn.entity.type}")
    private String entityType;
    
    @PostMapping({ "/seller/adaptor/search" })
    public ResponseEntity<String> search(@RequestBody final String body, @RequestHeader final HttpHeaders httpHeaders, final HttpServletRequest servletRequest) throws JsonProcessingException, SQLException {
    	SearchControllerSeller.log.info("Inside the SearchControllerSeller class of search method--- " + body );
    	if (!OndcUserType.SELLER.type().equalsIgnoreCase(this.entityType)) {
            throw new ApplicationException(ErrorCode.INVALID_ENTITY_TYPE);
        }
    	//Injecting remote client hostname to headers
        httpHeaders.add("remoteHost", servletRequest.getRemoteHost());
        SearchControllerSeller.log.info("Got call from " + servletRequest.getRemoteHost());
        
        final Schema model;
        try {
        	model = (Schema)this.jsonUtil.toModel(body, (Class)Schema.class);
        }catch(Exception e) {
            SearchControllerSeller.log.debug("Exception while parsing model");
            return new ResponseEntity(HttpStatus.OK);
        }
        
        final Context context = model.getContext();
        final String bapId = context.getBapId();
        final String bppId = context.getBppId();
        final ConfigModel configModel = this.configService.loadApplicationConfiguration(bppId, "search");
        
        SearchControllerSeller.log.info("The body in {} adaptor is {}", (Object)"search", (Object)this.jsonUtil.unpretty(body));
        SearchControllerSeller.log.info("Entity type is {}", (Object)this.entityType);
        
        final boolean authenticate = configModel.getMatchedApi().isHeaderAuthentication();
        SearchControllerSeller.log.info("does buyer {} requires to be authenticated ? {}", (Object)bapId, (Object)authenticate);
        
        if (authenticate) {
        	SearchControllerSeller.log.info("Inside authenticate ");
            final LookupRequest lookupRequest = new LookupRequest((String)null, context.getCountry(), context.getCity(), context.getDomain(), BecknUserType.BAP.type());
            this.validator.validateHeader(bapId, httpHeaders, body, lookupRequest);
            SearchControllerSeller.log.info("httpHeaders---- ", httpHeaders);
        }
        this.getMySQLDataSource(body,  model);
        
        return (ResponseEntity<String>)this.service.search(httpHeaders, model, configModel);
    }

    public void getMySQLDataSource(final String body, final Schema request) {
    	
    	try {
    		
    		Timestamp timestamp = new Timestamp(System.currentTimeMillis());   
    		ApiSellerObj myapiSellerObj = new ApiSellerObj(request.getContext().getMessageId(), request.getContext().getTransactionId(),
    				request.getContext().getAction(), request.getContext().getDomain(), request.getContext().getCoreVersion(),
    				timestamp.toString(), body, "N",
    				request.getMessage().getIntent().getItem().getDescriptor().getName());
    		
    		mApiAuditSellerRepository.save(myapiSellerObj);
    		
    		System.out.println("data saved in api_audit_seller table for search api ");
    	        
		}catch(Exception e) {
			System.out.println("Exception caught while saving the data in api_audit_seller table for search api -"+ e);
		}
		
	}
    static {
        log = LoggerFactory.getLogger((Class)SearchControllerSeller.class);
    }
}
