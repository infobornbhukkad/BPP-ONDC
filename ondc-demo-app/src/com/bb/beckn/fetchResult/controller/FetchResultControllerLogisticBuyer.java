// 
// Decompiled by Procyon v0.5.36
// 

package com.bb.beckn.fetchResult.controller;

import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.bb.beckn.confirm.controller.ConfirmControllerBuyer;
import com.bb.beckn.confirm.extension.Schema;
import com.bb.beckn.common.exception.ApplicationException;
import com.bb.beckn.common.exception.ErrorCode;
import com.bb.beckn.common.enums.OndcUserType;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.beans.factory.annotation.Value;
import com.bb.beckn.common.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import com.bb.beckn.confirm.service.ConfirmServiceBuyer;
import com.bb.beckn.repository.ApiAuditSellerRepository;

import com.bb.beckn.search.model.ApiSellerObj;

import org.hibernate.mapping.Map;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FetchResultControllerLogisticBuyer
{
        
	private static final Logger log;
	@Autowired
   	ApiAuditSellerRepository mApiAuditSellerRepository;
	@Autowired
    private JsonUtil jsonUtil;
	List<ApiSellerObj> apiLogisticSellerrObj_list= null;
	
    @PostMapping({ "/buyer/adaptor/fetch" })
    public List fetch( String transactionId, String messageId)  {
    	
    	FetchResultControllerLogisticBuyer.log.info("The request in FetchResultControllerLogisticBuyer" ); 
    	   	
    	System.out.println("transactionId --" + transactionId);
    	System.out.println("messageId -- "+ messageId);
    	apiLogisticSellerrObj_list = new ArrayList<ApiSellerObj>();
    	List<ApiSellerObj> apiSellerObj=null;
    	
        int retries = 3; // maximum number of retries
        while (retries > 0) {
                 try {
                     Thread.sleep(10000); // wait for 1 second before retrying
                 } catch (InterruptedException ex) {
                     ex.printStackTrace();
                 }
        	
                 System.out.println("retries --" + retries);
        	 retries--;
        }
        apiSellerObj=mApiAuditSellerRepository.findByTransactionidandMessageid(transactionId , messageId);
        
        if(apiSellerObj.size() == 0) {
        	
        	System.out.println("record not found");
        }
        for(int i=0; i < apiSellerObj.size(); i++) {
        	
        	apiLogisticSellerrObj_list.add(apiSellerObj.get(i));
        }
        return apiLogisticSellerrObj_list;
    }
    	
    
    
    static {
        log = LoggerFactory.getLogger((Class)FetchResultControllerLogisticBuyer.class);
    }
}
