// 
// Decompiled by Procyon v0.5.36
// 

package com.bb.beckn.confirm.controller;

import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import com.bb.beckn.common.model.AuditDataModel;
import com.bb.beckn.common.model.AuditFlagModel;
import com.bb.beckn.common.model.HttpModel;
import com.bb.beckn.common.model.AuditModel;
import org.springframework.web.bind.annotation.PostMapping;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.bb.beckn.common.model.ConfigModel;
import com.bb.beckn.api.model.common.Context;
import org.springframework.http.HttpStatus;
import com.bb.beckn.api.model.lookup.LookupRequest;
import com.bb.beckn.common.enums.BecknUserType;
import com.bb.beckn.confirm.extension.OnSchema;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestBody;
import com.bb.beckn.common.validator.HeaderValidator;
import com.bb.beckn.common.service.AuditService;
import com.bb.beckn.common.service.ApplicationConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import com.bb.beckn.common.util.JsonUtil;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OnConfirmControllerBuyer
{
    private static final Logger log;
    @Autowired
    private JsonUtil jsonUtil;
    @Autowired
    private ApplicationConfigService configService;
    @Autowired
    private AuditService auditService;
    @Autowired
    private HeaderValidator validator;
    
    @PostMapping({ "/buyer////on_confirm" })
    public ResponseEntity<String> onConfirm(@RequestBody final String body, @RequestHeader final HttpHeaders httpHeaders) throws JsonProcessingException {
        OnConfirmControllerBuyer.log.info("The body in {} adaptor is {}", (Object)"confirm", (Object)this.jsonUtil.unpretty(body));
        try {
            final OnSchema model = (OnSchema)this.jsonUtil.toModel(body, (Class)OnSchema.class);
            OnConfirmControllerBuyer.log.info("data in buyer controller adaptor [which is will now send back to buyer internal api] is {}", (Object)model.toString());
            final Context context = model.getContext();
            final String bppId = context.getBppId();
            final ConfigModel configuration = this.configService.loadApplicationConfiguration(context.getBapId(), "confirm");
            final boolean authenticate = configuration.getMatchedApi().isHeaderAuthentication();
            OnConfirmControllerBuyer.log.info("does seller {} requires to be authenticated ? {}", (Object)bppId, (Object)authenticate);
            if (authenticate) {
                final LookupRequest lookupRequest = new LookupRequest((String)null, context.getCountry(), context.getCity(), context.getDomain(), BecknUserType.BPP.type());
                this.validator.validateHeader(context.getBapId(), httpHeaders, body, lookupRequest);
            }
            this.auditService.audit(this.buildAuditModel(httpHeaders, body, model));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return (ResponseEntity<String>)new ResponseEntity((Object)"Response received. All Ok", HttpStatus.OK);
    }
    
    private AuditModel buildAuditModel(final HttpHeaders httpHeaders, final String body, final OnSchema model) {
        final AuditModel auditModel = new AuditModel();
        final HttpModel httpModel = new HttpModel();
        httpModel.setRequestHeaders(httpHeaders);
        httpModel.setRequestBody(body);
        final AuditFlagModel flagModel = new AuditFlagModel();
        flagModel.setHttp(true);
        flagModel.setFile(true);
        flagModel.setDatabase(true);
        auditModel.setApiName("confirm");
        auditModel.setSubscriberId(model.getContext().getBapId());
        auditModel.setAuditFlags(flagModel);
        auditModel.setDataModel(this.buildAuditDataModel(body, model));
        auditModel.setHttpModel(httpModel);
        return auditModel;
    }
    
    private AuditDataModel buildAuditDataModel(final String body, final OnSchema request) {
        final AuditDataModel model = new AuditDataModel();
        model.setAction(request.getContext().getAction());
        model.setCoreVersion(request.getContext().getCoreVersion());
        model.setDomain(request.getContext().getDomain());
        model.setTransactionId(request.getContext().getTransactionId());
        model.setMessageId(request.getContext().getMessageId());
        model.setCreatedOn(LocalDateTime.now());
        model.setJson(body);
        model.setStatus("N");
        return model;
    }
    
    static {
        log = LoggerFactory.getLogger((Class)OnConfirmControllerBuyer.class);
    }
}
