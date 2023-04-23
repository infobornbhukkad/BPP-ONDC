// 
// Decompiled by Procyon v0.5.36
// 

package com.bb.beckn.status.controller;

import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import com.bb.beckn.common.model.AuditDataModel;
import com.bb.beckn.common.model.AuditFlagModel;
import com.bb.beckn.common.model.HttpModel;
import com.bb.beckn.common.model.AuditModel;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.bb.beckn.common.model.ConfigModel;
import com.bb.beckn.api.model.common.Context;
import com.bb.beckn.api.model.lookup.LookupRequest;
import com.bb.beckn.common.enums.BecknUserType;
import com.bb.beckn.status.extension.Schema;
import com.bb.beckn.common.exception.ApplicationException;
import com.bb.beckn.common.exception.ErrorCode;
import com.bb.beckn.common.enums.OndcUserType;
import org.springframework.http.ResponseEntity;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.beans.factory.annotation.Value;
import com.bb.beckn.common.service.AuditService;
import com.bb.beckn.common.service.ApplicationConfigService;
import com.bb.beckn.common.util.JsonUtil;
import com.bb.beckn.common.validator.HeaderValidator;
import com.bb.beckn.repository.ApiAuditSellerRepository;
import com.bb.beckn.search.model.ApiSellerObj;

import org.springframework.beans.factory.annotation.Autowired;
import com.bb.beckn.status.service.StatusServiceSeller;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusControllerSeller
{
    private static final Logger log;
    @Autowired
   	ApiAuditSellerRepository mApiAuditSellerRepository;
    @Autowired
    private StatusServiceSeller service;
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

    @PostMapping({ "/seller/adaptor/status" })
    public ResponseEntity<String> status(@RequestBody final String body, @RequestHeader final HttpHeaders httpHeaders, final HttpServletRequest servletRequest) throws JsonProcessingException {
        StatusControllerSeller.log.info("The body in {} adaptor is {}", (Object)"status", (Object)this.jsonUtil.unpretty(body));
        StatusControllerSeller.log.info("Entity type is {}", (Object)this.entityType);
        
        if (!OndcUserType.SELLER.type().equalsIgnoreCase(this.entityType)) {
            throw new ApplicationException(ErrorCode.INVALID_ENTITY_TYPE);
        }
        final Schema model = (Schema)this.jsonUtil.toModel(body, (Class)Schema.class);
        final Context context = model.getContext();
        final String bppId = context.getBppId();
        final String bapId = context.getBapId();
        final ConfigModel configModel = this.configService.loadApplicationConfiguration(bppId, "status");
        final boolean authenticate = configModel.getMatchedApi().isHeaderAuthentication();
        StatusControllerSeller.log.info("does buyer {} requires to be authenticated ? {}", (Object)bapId, (Object)authenticate);
        if (authenticate) {
            final LookupRequest lookupRequest = new LookupRequest((String)null, context.getCountry(), context.getCity(), context.getDomain(), BecknUserType.BAP.type());
            this.validator.validateHeader(bppId, httpHeaders, body, lookupRequest);
        }
        this.getMySQLDataSource(body,  model);
        return (ResponseEntity<String>)this.service.status(httpHeaders, model);
    }
    
    public void getMySQLDataSource(final String body, final Schema request) {
    	try {
    		//System.out.println("Schema--  "+ request);
    		Timestamp timestamp = new Timestamp(System.currentTimeMillis());   
    		ApiSellerObj myapiSellerObj = new ApiSellerObj(request.getContext().getMessageId(), request.getContext().getTransactionId(),
    				request.getContext().getAction(), request.getContext().getDomain(), request.getContext().getCoreVersion(),
    				timestamp.toString(), body, "N","NA");
    		mApiAuditSellerRepository.saveAndFlush(myapiSellerObj);
    		System.out.println("data saved--StatusControllerSeller  ");
    	        
		}catch(Exception e) {
			System.out.println("Exception--"+ e);
		}
		
	}
    
    static {
        log = LoggerFactory.getLogger((Class)StatusControllerSeller.class);
    }
}
