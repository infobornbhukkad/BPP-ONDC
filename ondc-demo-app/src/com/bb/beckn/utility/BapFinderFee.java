// 
// Decompiled by Procyon v0.5.36
// 

package com.bb.beckn.utility;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bb.beckn.repository.BapFeeRepository;
import com.bb.beckn.search.model.BapFeeObj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class BapFinderFee
{
	private static final Logger log;

	@Autowired
    private BapFeeRepository mBapFeeRepository;
	
	BapFeeObj mBapFeeObj=null;
	
	public BapFeeObj getBapFinderFee(String strBapId ) { 
		BapFinderFee.log.info("Inside the BapFinderFee class of getBapFinderFee method..");
		mBapFeeObj= mBapFeeRepository.findBySubscriberId(strBapId);
		
		return mBapFeeObj;
	}	

	static {
		log = LoggerFactory.getLogger((Class)BapFinderFee.class);
	}
}
