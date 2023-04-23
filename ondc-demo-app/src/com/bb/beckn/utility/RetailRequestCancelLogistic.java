// 
// Decompiled by Procyon v0.5.36
// 

package com.bb.beckn.utility;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import com.bb.beckn.api.model.cancel.CancelMessage;
import com.bb.beckn.api.model.common.Context;
import com.bb.beckn.api.model.confirm.ConfirmMessage;
import com.bb.beckn.api.model.status.StatusMessage;
import com.bb.beckn.common.model.ConfigModel;
import com.bb.beckn.common.sender.Sender;
import com.bb.beckn.common.service.ApplicationConfigService;
import com.bb.beckn.common.util.JsonUtil;
import com.bb.beckn.search.model.ConfirmAuditSellerObj;
import com.bb.beckn.search.model.LogisticBppObj;
import com.bb.beckn.cancel.extension.Schema;

import java.security.PrivateKey;
import java.security.Signature;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.crypto.digests.Blake2bDigest;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class RetailRequestCancelLogistic
{
	private static final Logger log;
	@Autowired
	private ApplicationConfigService configService;
	@Autowired
	private RetailRequestLogisticHeader mRetailRequestLogisticHeader;
	@Autowired
	private Sender sender;
	@Autowired
	private JsonUtil jsonUtil;
	
	Context ctx =null;
	CancelMessage logisticcancelMessage =null;
	
	
	
	  public void creatingLogisticcancelRequestStructureKirana(final Schema request, String strtransactionid,String strmessageid, ConfirmAuditSellerObj mconfirmAuditSellerObj, final ConfigModel configModel) {
		  RetailRequestCancelLogistic.log.info("Going to create Logistic status RequestStructure Kirana. in creatingLogisticcancelRequestStructureKirana method..");
	  
	  final com.bb.beckn.cancel.extension.Schema requestcancelLogistic = new com.bb.beckn.cancel.extension.Schema(); 
	 // final ConfigModel configModel = this.configService.loadApplicationConfiguration(request.getContext().getBapId(), "cancel");
	  
	  
	  ctx =new Context(); ctx.setDomain(configModel.getLogisticDomain());
	  ctx.setBapId(configModel.getSubscriberlogisticId());
	  ctx.setBapUri(configModel.getSubscriberlogisticUrl());
	  ctx.setBppId(mconfirmAuditSellerObj.getLogisticbppid());
	  ctx.setBppUri(mconfirmAuditSellerObj.getLogisticbppurl());
	  ctx.setCity(request.getContext().getCity()); ctx.setMessageId(strmessageid);
	  ctx.setTransactionId(strtransactionid);
	  ctx.setCoreVersion(configModel.getVersion()); String ttl=
	  ""+Duration.ofMinutes(Long.parseLong(configModel.getBecknTtl()));
	  ctx.setTtl(ttl); ctx.setCountry("IND"); ctx.setAction("cancel");
	  ctx.setTimestamp(Instant.now().toString());
	  requestcancelLogistic.setContext(ctx);
	  
	  logisticcancelMessage = new CancelMessage();
	  
	  logisticcancelMessage.setOrderId(request.getMessage().getOrderId());
	  logisticcancelMessage.setCancellationReasonId(request.getMessage().
	  getCancellationReasonId());
	  requestcancelLogistic.setMessage(logisticcancelMessage);
	  
	  
	  final String json = this.jsonUtil.toJson((Object)requestcancelLogistic);
	  System.out.println("json - "+ json);
	  
	  final String url = mconfirmAuditSellerObj.getLogisticbppurl() + "cancel"; //final
	  //String url = "https://my.ithinklogistics.com/ondc/search"; 
	  final HttpHeaders headers= this.mRetailRequestLogisticHeader.logisticbuildHeaders(json,configModel); 
	  System.out.println("headers - "+ headers);
	  this.sender.send(url, headers, json, configModel.getMatchedApi());
	  
	  System.out.println("end of creatingLogisticcancelRequestStructureKirana");
	  
	  } 
	  
	  public void creatingLogisticcancelRequestStructurefood(final Schema request, String strtransactionid,String strmessageid, ConfirmAuditSellerObj mconfirmAuditSellerObj,final ConfigModel configModel) {
		RetailRequestCancelLogistic.log.info("Going to create Logistic status RequestStructure food. in creatingLogisticcancelRequestStructurefood method..");
	  
	  final com.bb.beckn.cancel.extension.Schema requestcancelLogistic = new com.bb.beckn.cancel.extension.Schema(); 
	  //final ConfigModel configModel = this.configService.loadApplicationConfiguration(request.getContext().getBapId(), "cancel");
	  
	  
	  ctx =new Context(); ctx.setDomain(configModel.getLogisticDomain());
	  ctx.setBapId(configModel.getSubscriberlogisticId());
	  ctx.setBapUri(configModel.getSubscriberlogisticUrl());
	  ctx.setBppId(mconfirmAuditSellerObj.getLogisticbppid());
	  ctx.setBppUri(mconfirmAuditSellerObj.getLogisticbppurl());
	  ctx.setCity(request.getContext().getCity()); ctx.setMessageId(strmessageid);
	  ctx.setTransactionId(strtransactionid);
	  ctx.setCoreVersion(configModel.getVersion());
	  ctx.setTtl(configModel.getBecknTtl()); ctx.setCountry("IND");
	  ctx.setAction("cancel"); ctx.setTimestamp(Instant.now().toString());
	  requestcancelLogistic.setContext(ctx);
	  
	  logisticcancelMessage = new CancelMessage();
	  
	  logisticcancelMessage.setOrderId(request.getMessage().getOrderId());
	  logisticcancelMessage.setCancellationReasonId(request.getMessage().
	  getCancellationReasonId());
	  requestcancelLogistic.setMessage(logisticcancelMessage);
	  
	  
	  final String json = this.jsonUtil.toJson((Object)requestcancelLogistic);
	  System.out.println("json - "+ json);
	  
	  final String url = mconfirmAuditSellerObj.getLogisticbppurl() + "cancel";
	  //String url = "https://my.ithinklogistics.com/ondc/search"; 
	  final HttpHeaders headers= this.mRetailRequestLogisticHeader.logisticbuildHeaders(json,
	  configModel); System.out.println("headers - "+ headers);
	  this.sender.send(url, headers, json, configModel.getMatchedApi());
	  
	  System.out.println("end of creatingLogisticcancelRequestStructurefood"); }
	 

	static {
		log = LoggerFactory.getLogger((Class)RetailRequestCancelLogistic.class);
	}
}
