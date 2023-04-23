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
import com.bb.beckn.api.model.common.Payload;
import com.bb.beckn.api.model.common.Payment;
import com.bb.beckn.api.model.common.Provider;
import com.bb.beckn.api.model.common.Scalar;
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
import com.bb.beckn.search.model.KiranaObj;
import com.bb.beckn.search.model.VendorObj;
import com.bb.beckn.select.extension.OnSchema;
import com.bb.beckn.select.extension.Schema;
import com.bb.beckn.select.utility.RetailResponseSelectkirana;

import java.security.PrivateKey;
import java.security.Signature;
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
public class RetailRequestLogisticHeader
{
	private static final Logger log;

	@Autowired
	private Sender sender;
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
	

	public HttpHeaders logisticbuildHeaders(final String req, final ConfigModel configModel)
	 {
		RetailRequestLogisticHeader.log.info("Inside the logisticbuildHeaders method...");
		
        final HttpHeaders headers = new HttpHeaders();
	    headers.add("Accept", "application/json");
	    if(configModel.getMatchedApi().isSetAuthorizationHeader()) 
	    { 
	    	final String authHeader = this.buildAuthorizationHeader(req, configModel, headers);
			headers.add("Authorization", authHeader);
			RetailRequestLogisticHeader.log.info("Authorization header added to HttpHeaders");
		} else 
		{ 
			RetailRequestLogisticHeader.log.info("Authorization header will not be set as its disabled in the config file"); 
		} 
	    return headers; 
	    } 
	private String buildAuthorizationHeader(final String req, final ConfigModel configModel, final HttpHeaders headers)
	{
	 RetailRequestLogisticHeader.log.info("Inside the buildAuthorizationHeader method...");
     final long	currentTime = System.currentTimeMillis() / 1000L; 
     final int headerValidity =	configModel.getMatchedApi().getHeaderValidity(); 
     final String blakeHash =this.generateBlakeHash(req); 
     //System.out.println("req--"+req );
     final String signingString = "(created): " +
									currentTime + "\n(expires): " + (currentTime + headerValidity) +
									"\ndigest: BLAKE-512=" + blakeHash + ""; 
     //System.out.println("signingString--"+signingString );
     final String signature =generateSignature(signingString, configModel.getSigning()); 
     //final String	kid = configModel.getSubscriberId() + "|" +configModel.getKeyid() + "|" +"ed25519"; 
	 final String kid = "bap.logistic.bornbhukkad.in" + "|" +"547" +"|" + "ed25519"; 
	 final String authHeader = "Signature keyId=\"" + kid +
													"\",algorithm=\"" + "ed25519" + "\", created=\"" + currentTime +
													"\", expires=\"" + (currentTime + headerValidity) +
													"\", headers=\"(created) (expires) digest\", signature=\"" + signature +
													"\""; 
	 return authHeader; 
	 } 
	private String generateBlakeHash(String req) 
	{
		RetailRequestLogisticHeader.log.info("Inside the generateBlakeHash method...");
		Blake2bDigest blake2bDigest = new Blake2bDigest(512);
		byte[] test =req.getBytes();
		blake2bDigest.update(test, 0, test.length);
		byte[] hash = new byte[blake2bDigest.getDigestSize()]; 
		blake2bDigest.doFinal(hash, 0); 
		String bs64 = Base64.getEncoder().encodeToString(hash); //
		log.info("Base64 URL Encoded : " + bs64); 
		return bs64; 
	} 
	private String	generateSignature(final String req, final SigningModel model) {
		String sign =null; 
		try {
			/*if (model.isCertificateUsed())
		{ 
			//final PrivateKey privateKey =	this.getPrivateKeyFromP12(model); 
			final PrivateKey privateKey = null;
			final	Signature rsa = Signature.getInstance("SHA1withRSA");
			rsa.initSign(privateKey);
			rsa.update(req.getBytes());
			final byte[] str =	rsa.sign(); 
			sign = Base64.getEncoder().encodeToString(str); 
		} else {*/
			if(!StringUtils.isNoneBlank(new CharSequence[] { model.getPrivateKey() })) {
				RetailRequestLogisticHeader.log.error("neither certificate nor private key has been set for signature");
					throw new ApplicationException(ErrorCode.SIGNATURE_ERROR,
							ErrorCode.SIGNATURE_ERROR.getMessage()); } 
			//Ed25519PrivateKeyParameters	privateKey = new Ed25519PrivateKeyParameters(Base64.getDecoder().decode("5ve0Q6EcVIlpHLP01mpO+fWLIQAwVURj87mvldlwPlM=".getBytes()), 0); 
			Ed25519PrivateKeyParameters privateKey = new Ed25519PrivateKeyParameters(Base64.getDecoder().decode(model.getLogisticprivateKey().getBytes()), 0);
			Ed25519Signer sig = new Ed25519Signer(); 
			sig.init(true, privateKey);
			sig.update(req.getBytes(), 0, req.length());

			byte[] s1 = sig.generateSignature(); 
			sign =	Base64.getEncoder().encodeToString(s1);
			//} 
		} catch (Exception e) 
		 {
			RetailRequestLogisticHeader.log.error("error while generating the signature",	(Throwable)e); 
			throw new ApplicationException(ErrorCode.SIGNATURE_ERROR,ErrorCode.SIGNATURE_ERROR.getMessage()); 
		 }
		RetailRequestLogisticHeader.log.info("Signature Generated From Data : " + sign);
	
	return sign;
	}

	static {
		log = LoggerFactory.getLogger((Class)RetailRequestLogisticHeader.class);
	}
}
