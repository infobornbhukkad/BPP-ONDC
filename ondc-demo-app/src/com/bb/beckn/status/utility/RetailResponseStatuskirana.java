// 
// Decompiled by Procyon v0.5.36
// 

package com.bb.beckn.status.utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bb.beckn.ConnectionUtil;
import com.bb.beckn.common.model.ConfigModel;
import com.bb.beckn.common.util.JsonUtil;
import com.bb.beckn.confirm.utility.RetailResponseConfirmkirana;
import com.bb.beckn.fetchResult.controller.FetchResultControllerLogisticBuyer;
import com.bb.beckn.repository.ConfirmAuditSellerRepository;
import com.bb.beckn.repository.LogisticFinderRepository;
import com.bb.beckn.search.model.ApiSellerObj;
import com.bb.beckn.search.model.ConfirmAuditSellerObj;
import com.bb.beckn.search.model.LogisticBppObj;
import com.bb.beckn.status.extension.OnSchema;
import com.bb.beckn.status.extension.Schema;
import com.bb.beckn.utility.RetailRequestStatusLogistic;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class RetailResponseStatuskirana {
	private static final Logger log;
	@Autowired
	RetailRequestStatusLogistic mRetailRequestStatusLogistic;
	@Autowired
	LogisticFinderRepository mLogisticFinderRepository;
	@Autowired
	FetchResultControllerLogisticBuyer mfetchResultControllerLogisticBuyer;
	@Autowired
	private JsonUtil jsonUtil;
	@Autowired
    ConfirmAuditSellerRepository mConfirmAuditSellerRepository;
	
	public OnSchema fetchResult(final Schema request, final ConfigModel	configModel, OnSchema respBody,String  strtransactionid, String strmessageid, ConfirmAuditSellerObj mconfirmAuditSellerObj) throws SQLException, IOException, NumberFormatException, JSONException {

		RetailResponseStatuskirana.log.info("Inside the RetailResponseStatuskirana class...");
		
		this.mRetailRequestStatusLogistic.creatingLogisticstatusRequestStructureKirana( request,strtransactionid,strmessageid,mconfirmAuditSellerObj);
		
		List<ApiSellerObj> apiLogisticSellerrObj_list = this.mfetchResultControllerLogisticBuyer.fetch(strtransactionid, strmessageid);
		
		com.bb.beckn.status.extension.OnSchema modeltosave=null;
		for(int i=0; i < apiLogisticSellerrObj_list.size(); i++) {
			try{
			modeltosave  = (com.bb.beckn.status.extension.OnSchema)this.jsonUtil.toModel(apiLogisticSellerrObj_list.get(i).getJson(), (Class)com.bb.beckn.status.extension.OnSchema.class);
			setLogisticProvider( modeltosave, strtransactionid,  strmessageid,"", mconfirmAuditSellerObj.getDomaintype(), modeltosave.getContext().getBppId(), modeltosave.getContext().getBppUri(),mconfirmAuditSellerObj);
			}catch(Exception e) {
				System.out.println(e.getMessage());
			}
			
		}
		
		return respBody;

	} 
    
	private void setLogisticProvider(com.bb.beckn.status.extension.OnSchema model,String strtransactionid, String strmessageid,String strProviderName, String strTosearchinDomain, String logisticBppid, String logisticBppurl, ConfirmAuditSellerObj mconfirmAuditSellerObj) {    	
		RetailResponseStatuskirana.log.info("Inside the RetailResponseStatuskirana class of setLogisticProvider method...");
    	try {
    		String querySTRUpdate="";
    		Timestamp timestamp = new Timestamp(System.currentTimeMillis());   
    		LogisticBppObj myapiLogisticBppObj = new LogisticBppObj(
    				strProviderName, 
    				model.getContext().getBppId(),model.getContext().getBppUri(),timestamp.toString(),"",this.jsonUtil.toJson((Object)model), strtransactionid, strmessageid, model.getMessage().getOrder().getProvider().getId(),model.getMessage().getOrder().getQuote().getPrice().getValue(),
    				model.getMessage().getOrder().getItems().get(0).getCategoryId(), model.getContext().getAction(),"");
    		
    		
    		List<LogisticBppObj> listLogisticBppObj =mLogisticFinderRepository.findBytransaction_idandaction(strtransactionid, model.getContext().getAction());
    		if (listLogisticBppObj.size()==0) {
    			mLogisticFinderRepository.save(myapiLogisticBppObj);
    			System.out.println("data saved--LogisticBppObj  ");
    		}else {
    			System.out.println("else ----------");
    			//ResultSet rsUpdate;
    			int resultset=0;
    	        Connection connUpdate= ConnectionUtil.getConnection();
    	        Statement stmUpdate= connUpdate.createStatement();
    	        querySTRUpdate= "update bornbhukkad.ondc_bpp_logistic_finder baf set baf.providername ='" +""+
    	        		         "', baf.bppid ='"+model.getContext().getBppId()+"' , baf.bppurl='"+model.getContext().getBppUri()+ "' , baf.updated_on='"+timestamp.toString()+
    	        		         "' , baf.json='"+this.jsonUtil.toJson((Object)model) + "', baf.transaction_id ='"+strtransactionid+ "', baf.message_id ='"+strmessageid+
    	        		        "' where baf.bppid ='"+ model.getContext().getBppId()  +"' and baf.action='"+ model.getContext().getAction()+"' "
    	        		        		+ " and baf.transaction_id ='" +strtransactionid+ "';";
    	        resultset = stmUpdate.executeUpdate(querySTRUpdate);
    	        System.out.println("data updated--LogisticBppObj  ");
    	        stmUpdate.close(); 
    		}
    		
    		/// Data Inserted in ondc_audit_transaction
         	String paymenttype ="";
         	String paymentcollectedby="";
         	String amountatconfirmation="";
         	String logisticprovidername="";
         	String logisticdeliverycharge="";
         	
         	if(model.getMessage().getOrder().getPayment().getType().equalsIgnoreCase("POST-FULFILLMENT") || model.getMessage().getOrder().getPayment().getType().equalsIgnoreCase("ON-FULFILLMENT")) {
         		paymenttype="CoD";
         		paymentcollectedby="seller";
         		
         	}else if(model.getMessage().getOrder().getPayment().getType().equalsIgnoreCase("ON-ORDER")) {
         		paymenttype="Prepaid";
         		paymentcollectedby="Buyer";
         	}
         	
         	amountatconfirmation=mconfirmAuditSellerObj.getAmountatconfirmation();
         	
         	logisticprovidername= mconfirmAuditSellerObj.getLogisticprovidername();
         	
         	for(int i=0; i < model.getMessage().getOrder().getQuote().getBreakup().size(); i++) {
         		if(model.getMessage().getOrder().getQuote().getBreakup().get(i).getTitle().equalsIgnoreCase("Delivery Charge")) {
         			logisticdeliverycharge=model.getMessage().getOrder().getQuote().getBreakup().get(i).getPrice().getValue();
         		}
         	}
         	
         	ConfirmAuditSellerObj myconfirmAuditSellerObj = new ConfirmAuditSellerObj(model.getMessage().getOrder().getId(), model.getContext().getTransactionId(),mconfirmAuditSellerObj.getSellerorderstate(), model.getMessage().getOrder().getState(),
         			paymenttype,  paymentcollectedby, amountatconfirmation, logisticprovidername,model.getMessage().getOrder().getProvider().getId(),logisticdeliverycharge,"","","","","","","","","","","","","",
         			timestamp.toString(), "",mconfirmAuditSellerObj.getBuyerappfindingfee(), mconfirmAuditSellerObj.getBuyerappfindingfeetype(), strTosearchinDomain,logisticBppid,logisticBppurl);
         	
         	mConfirmAuditSellerRepository.saveAndFlush(myconfirmAuditSellerObj);
     		
     		System.out.println("Data Inserted in ondc_audit_seller_transaction "); 
    	        
		}catch(Exception e) {
			System.out.println("Exception--"+ e);
		}
		
	 }
	static {
		log = LoggerFactory.getLogger((Class) RetailResponseStatuskirana.class);
	}
}
