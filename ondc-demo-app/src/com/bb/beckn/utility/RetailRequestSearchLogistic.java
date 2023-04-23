// 
// Decompiled by Procyon v0.5.36
// 

package com.bb.beckn.utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import com.bb.beckn.api.model.common.Category;
import com.bb.beckn.api.model.common.Context;
import com.bb.beckn.api.model.common.Fulfillment;
import com.bb.beckn.api.model.common.Intent;
import com.bb.beckn.api.model.common.Location;
import com.bb.beckn.api.model.common.Payload;
import com.bb.beckn.api.model.common.Payment;
import com.bb.beckn.api.model.common.Provider;
import com.bb.beckn.api.model.common.Scalar;
import com.bb.beckn.api.model.common.Start;
import com.bb.beckn.api.model.common.Time;
import com.bb.beckn.api.model.common.TimeRange;
import com.bb.beckn.api.model.search.SearchMessage;
import com.bb.beckn.common.exception.ApplicationException;
import com.bb.beckn.common.exception.ErrorCode;
import com.bb.beckn.common.model.ConfigModel;
import com.bb.beckn.common.model.SigningModel;
import com.bb.beckn.common.sender.Sender;
import com.bb.beckn.common.service.ApplicationConfigService;
import com.bb.beckn.common.util.JsonUtil;
import com.bb.beckn.init.service.InitServiceSeller;
import com.bb.beckn.search.model.KiranaObj;
import com.bb.beckn.search.model.VendorObj;
import com.bb.beckn.select.extension.OnSchema;
import com.bb.beckn.select.extension.Schema;
import java.security.PrivateKey;
import java.security.Signature;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.crypto.digests.Blake2bDigest;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class RetailRequestSearchLogistic
{
	private static final Logger log;

	@Autowired
	private Sender sender;
	@Autowired
	private RetailRequestLogisticHeader mRetailRequestLogisticHeader;
	@Autowired
	private ApplicationConfigService configService;
	@Autowired
	private JsonUtil jsonUtil;

	Context ctx =null;
	SearchMessage logisticSearchMessage =null;
	Intent logisticIntent =null;
	Category logisticCategory =null;
	Provider logisticProvider =null;
	Time logisticProviderTime =null;
	TimeRange logisticProviderTimeRange =null;
	Fulfillment logisticProviderFulfillment =null;
	Scalar payloadWeight =null;

	public void creatingLogisticsearchRequestStructureKirana( final Schema request ,OnSchema respBody, float totalitemweight , float totalitemprice, 
			String storeType, List<KiranaObj> kiranaObjList, String strtransactionid, String strmessageid) { 
		RetailRequestSearchLogistic.log.info("Going to create Logistic search RequestStructure Kirana...");
		final com.bb.beckn.search.extension.Schema requestLogistic = new com.bb.beckn.search.extension.Schema();

			payloadWeight = new Scalar();

			final ConfigModel configModel =	this.configService.loadApplicationConfiguration(request.getContext().getBapId(), "search");

			ctx =new Context(); ctx.setDomain(configModel.getLogisticDomain());
			ctx.setBapId(configModel.getSubscriberlogisticId());
			ctx.setBapUri(configModel.getSubscriberlogisticUrl());			
			ctx.setCity(request.getContext().getCity());
			ctx.setMessageId(strmessageid);
			ctx.setTransactionId(strtransactionid);
			ctx.setCoreVersion(configModel.getVersion()); 
			String ttl= ""+Duration.ofMinutes(Long.parseLong(configModel.getBecknTtl()));
			ctx.setTtl(ttl);
			ctx.setCountry("IND"); 
			ctx.setAction("search");
			ctx.setTimestamp(Instant.now().toString()); requestLogistic.setContext(ctx);

			logisticSearchMessage = new SearchMessage();

			if (logisticIntent==null) { 
				logisticIntent =new Intent(); 
			}
			logisticSearchMessage.setIntent(logisticIntent); 
			
			if (logisticCategory==null)
			{
               logisticCategory =new Category(); 
            }
            if(storeType.equalsIgnoreCase("kirana")) 
            {
			  logisticCategory.setId("Standard Delivery");
            }
			logisticSearchMessage.getIntent().setCategory(logisticCategory);

			if (logisticProvider==null) 
			{ 
				logisticProvider = new Provider(); 
			} if(logisticProviderTime==null) 
			{ 
				logisticProviderTime = new Time(); 
			}
			logisticProviderTime.setDays(kiranaObjList.get(0).getStoreworkingdays());

			logisticProviderTime.setDuration(null);

			if (logisticProviderTimeRange==null) 
			{ 
				logisticProviderTimeRange = new	TimeRange(); 
			} 
			logisticProviderTimeRange.setStart(kiranaObjList.get(0).getStoreopentimehours());
			logisticProviderTimeRange.setEnd(kiranaObjList.get(0).getStoreclosetimehours());
			logisticProviderTime.setRange(logisticProviderTimeRange);

			logisticProvider.setTime(logisticProviderTime);
			logisticIntent.setProvider(logisticProvider);
			logisticSearchMessage.setIntent(logisticIntent);

			if (logisticProviderFulfillment==null)  
			  { 
				logisticProviderFulfillment = new Fulfillment();
			  }

			logisticProviderFulfillment.setType("CoD");
			logisticProviderFulfillment.setEnd(request.getMessage().getOrder().getFulfillments().get(0).getEnd());

			logisticProviderFulfillment.setStart(new Start());
			logisticProviderFulfillment.getStart().setLocation(new Location());
			logisticProviderFulfillment.getEnd().getLocation().setGps(respBody.getMessage().getOrder().getFulfillments().get(0).getEnd().getLocation().getGps());
			logisticProviderFulfillment.getStart().getLocation().setGps(respBody.getMessage().getOrder().getProvider().getLocations().get(0).getGps());
			logisticProviderFulfillment.getStart().getLocation().setAddress(respBody.getMessage().getOrder().getProvider().getLocations().get(0).getAddress());
			
			logisticIntent.setPayment(new Payment());

			if(logisticProviderFulfillment.getType().equalsIgnoreCase("CoD")) 
			{
				logisticIntent.getPayment().setCollectionAmount(String.valueOf(totalitemprice)); 
			}

			logisticIntent.setPayloaddetail(new Payload());
			logisticIntent.getPayloaddetail().setWeight(new Scalar());
			payloadWeight.setUnit("Kilogram"); 
			//dummy data 
			//payloadWeight.setValue(new	Float(6)); 
			payloadWeight.setValue((int)Math.round(totalitemweight));
			logisticIntent.getPayloaddetail().setWeight(payloadWeight);

			logisticIntent.getPayloaddetail().setAdditionalProp1("Fruits and Vegetables"); 			
			logisticIntent.setFulfillment(logisticProviderFulfillment);
			logisticSearchMessage.setIntent(logisticIntent);
			requestLogistic.setMessage(logisticSearchMessage);

			final String json = this.jsonUtil.toJson((Object)requestLogistic);
			System.out.println("json - "+ json);

			final String url = configModel.getBecknGateway() + "/search"; 
			//final String	url = "https://my.ithinklogistics.com/ondc/search"; 
			final HttpHeaders headers= this.mRetailRequestLogisticHeader.logisticbuildHeaders(json, configModel); 
			System.out.println("headers - "+ headers); 
			this.sender.send(url, headers, json, configModel.getMatchedApi());
			System.out.println("end of creatingLogisticselectRequestStructure");
	}
	public void creatingLogisticsearchRequestStructurefood( final Schema request ,OnSchema respBody, float totalitemweight , float totalitemprice, 
			String storeType, List<VendorObj> vendorObjList, String strtransactionid, String strmessageid) { 
		RetailRequestSearchLogistic.log.info("Going to create Logistic search RequestStructure food...");
		final com.bb.beckn.search.extension.Schema requestLogistic = new com.bb.beckn.search.extension.Schema();

			payloadWeight = new Scalar();

			final ConfigModel configModel =	this.configService.loadApplicationConfiguration(request.getContext().getBapId(), "search");

			ctx =new Context(); ctx.setDomain(configModel.getLogisticDomain());
			ctx.setBapId(configModel.getSubscriberlogisticId());
			ctx.setBapUri(configModel.getSubscriberlogisticUrl());			
			ctx.setCity(request.getContext().getCity());
			ctx.setMessageId(strmessageid);
			ctx.setTransactionId(strtransactionid);
			ctx.setCoreVersion(configModel.getVersion()); 
			String ttl= ""+Duration.ofMinutes(Long.parseLong(configModel.getBecknTtl()));
			ctx.setTtl(ttl);
			ctx.setCountry("IND"); 
			ctx.setAction("search");
			ctx.setTimestamp(Instant.now().toString()); requestLogistic.setContext(ctx);

			logisticSearchMessage = new SearchMessage();

			if (logisticIntent==null) { 
				logisticIntent =new Intent(); 
			}
			logisticSearchMessage.setIntent(logisticIntent); 
			
			if (logisticCategory==null)
			{
               logisticCategory =new Category(); 
            }
            if(storeType.equalsIgnoreCase("restaurant"))
            {
            	logisticCategory.setId("Immediate Delivery");
            }
			logisticSearchMessage.getIntent().setCategory(logisticCategory);

			if (logisticProvider==null) 
			{ 
				logisticProvider = new Provider(); 
			} if(logisticProviderTime==null) 
			{ 
				logisticProviderTime = new Time(); 
			}
			logisticProviderTime.setDays(vendorObjList.get(0).getStoreworkingdays());

			logisticProviderTime.setDuration(null);

			if (logisticProviderTimeRange==null) 
			{ 
				logisticProviderTimeRange = new	TimeRange(); 
			} 
			logisticProviderTimeRange.setStart(vendorObjList.get(0).getStoreopentimehours());
			logisticProviderTimeRange.setEnd(vendorObjList.get(0).getStoreclosetimehours());
			logisticProviderTime.setRange(logisticProviderTimeRange);

			logisticProvider.setTime(logisticProviderTime);
			logisticIntent.setProvider(logisticProvider);
			logisticSearchMessage.setIntent(logisticIntent);

			if (logisticProviderFulfillment==null)  
			  { 
				logisticProviderFulfillment = new Fulfillment();
			  }

			logisticProviderFulfillment.setType("CoD");
			logisticProviderFulfillment.setEnd(request.getMessage().getOrder().getFulfillments().get(0).getEnd());

			logisticProviderFulfillment.setStart(new Start());
			logisticProviderFulfillment.getStart().setLocation(new Location());
			logisticProviderFulfillment.getEnd().getLocation().setGps(respBody.getMessage().getOrder().getFulfillments().get(0).getEnd().getLocation().getGps());
			logisticProviderFulfillment.getStart().getLocation().setGps(respBody.getMessage().getOrder().getProvider().getLocations().get(0).getGps());
			logisticProviderFulfillment.getStart().getLocation().setAddress(respBody.getMessage().getOrder().getProvider().getLocations().get(0).getAddress());

			logisticIntent.setPayment(new Payment());

			if(logisticProviderFulfillment.getType().equalsIgnoreCase("CoD")) 
			{
				logisticIntent.getPayment().setCollectionAmount(String.valueOf(totalitemprice)); 
			}

			logisticIntent.setPayloaddetail(new Payload());
			logisticIntent.getPayloaddetail().setWeight(new Scalar());
			payloadWeight.setUnit("Kilogram"); 
			//dummy data 
			//payloadWeight.setValue(new	Float(6)); 
			payloadWeight.setValue((int)Math.round(totalitemweight));
			logisticIntent.getPayloaddetail().setWeight(payloadWeight);

			logisticIntent.getPayloaddetail().setAdditionalProp1("Fruits and Vegetables"); 			
			logisticIntent.setFulfillment(logisticProviderFulfillment);
			logisticSearchMessage.setIntent(logisticIntent);
			requestLogistic.setMessage(logisticSearchMessage);

			final String json = this.jsonUtil.toJson((Object)requestLogistic);
			System.out.println("json - "+ json);

			final String url = configModel.getBecknGateway() + "/search"; 
			//final String	url = "https://my.ithinklogistics.com/ondc/search"; 
			final HttpHeaders headers= this.mRetailRequestLogisticHeader.logisticbuildHeaders(json, configModel); 
			System.out.println("headers - "+ headers); 
			this.sender.send(url, headers, json, configModel.getMatchedApi());
			System.out.println("end of creatingLogisticselectRequestStructure");
	}	

	static {
		log = LoggerFactory.getLogger((Class)RetailRequestSearchLogistic.class);
	}
}
