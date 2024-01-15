package com.bb.beckn.search.controller;

import org.slf4j.LoggerFactory;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.bb.beckn.common.model.ConfigModel;

import com.bb.beckn.api.enums.AckStatus;
import com.bb.beckn.api.model.common.Ack;
import com.bb.beckn.api.model.common.Context;
import com.bb.beckn.api.model.common.Error;
import com.bb.beckn.api.model.lookup.LookupRequest;
import com.bb.beckn.api.model.response.Response;
import com.bb.beckn.api.model.response.ResponseMessage;
import com.bb.beckn.common.enums.BecknUserType;
import com.bb.beckn.search.extension.Schema;
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
import com.bb.beckn.common.service.ApplicationConfigService;
import com.bb.beckn.common.service.ErrorCodeService;
import com.bb.beckn.common.util.JsonUtil;
import com.bb.beckn.common.validator.HeaderValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchControllerSeller
{
    private static final Logger log;
   
    @Autowired
    private HeaderValidator validator;
    @Autowired
    private JsonUtil jsonUtil;
    @Autowired
    private ApplicationConfigService configService;
    @Autowired
	private ObjectMapper mapper; 
    @Autowired
    private ErrorCodeService errorCodeService;
    
   
    
    @Value("${beckn.entity.type}")
    private String entityType;
    
    @PostMapping({ "/seller/adaptor/search" })
    public ResponseEntity<String> search(@RequestBody final String body, @RequestHeader final HttpHeaders httpHeaders, final HttpServletRequest servletRequest) throws JsonProcessingException, SQLException {
    	SearchControllerSeller.log.info("Inside the SearchControllerSeller class of search method--- " + body );
    	
    	//Injecting remote client hostname to headers
        httpHeaders.add("remoteHost", servletRequest.getRemoteHost());
        SearchControllerSeller.log.info("Got call from " + servletRequest.getRemoteHost());
        
        Schema model = null;
        try {
        	model = (Schema)this.jsonUtil.toModel(body, (Class)Schema.class);
        }catch(Exception e) {
            SearchControllerSeller.log.debug("Exception while parsing model");

            return this.errorCodeService.sendErrorMessage(model,"Invalid request","30000", "Invalid request does not meet API contract specifications");
        }
        
        final Context context = model.getContext();
        final String bapId = context.getBapId();
        final String bppId = context.getBppId();
        final ConfigModel configModel = this.configService.loadApplicationConfiguration(bppId, "search");
        
        SearchControllerSeller.log.info("The body in {} adaptor is {}", (Object)"search", (Object)this.jsonUtil.unpretty(body));
        SearchControllerSeller.log.info("Entity type is {}", (Object)this.entityType);
        
        final boolean authenticate = configModel.getMatchedApi().isHeaderAuthentication();
        SearchControllerSeller.log.info("does buyer {} requires to be authenticated ? {}", (Object)bapId, (Object)authenticate);
        
        try {
        	if (authenticate) {
            	SearchControllerSeller.log.info("Inside authenticate ");
                final LookupRequest lookupRequest = new LookupRequest((String) null, context.getCountry(), context.getCity(), context.getDomain(), BecknUserType.BAP.type());
                this.validator.validateHeader(bapId, httpHeaders, body, lookupRequest);
                SearchControllerSeller.log.info("httpHeaders---- ", httpHeaders);
            }
        } catch(Exception e){
        	SearchControllerSeller.log.error("Exception caught while Authentication: "+ e);
        	return this.errorCodeService.sendErrorMessage(model,"Invalid Signature","30016","Cannot verify signature for request");
        }
        
        final boolean saveApiSellerObj = this.getMySQLDataSource(body,  model);
//        final boolean saveApiSellerObj = true; 
    	if(saveApiSellerObj) {
        	//return (ResponseEntity<String>)this.service.search(httpHeaders, model, configModel); 
        } else  {
        	return this.errorCodeService.sendErrorMessage(model,"Stale Request","30022","Cannot process stale request");
        }
		return null;
    }

    public boolean getMySQLDataSource(final String body, final Schema request) {
    	boolean obj = true;
    	SearchControllerSeller.log.info(" Inside the getMySQLDataSource method of SearchControllerSeller class. ");
    	
    	SearchControllerSeller.log.info(" Exiting the getMySQLDataSource method of SearchControllerSeller class. ");
    	return obj;
	}
    static {
        log = LoggerFactory.getLogger((Class)SearchControllerSeller.class);
    }
}
