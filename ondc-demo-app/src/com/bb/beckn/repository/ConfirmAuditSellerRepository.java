package com.bb.beckn.repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bb.beckn.search.model.ApiSellerObj;
import com.bb.beckn.search.model.BapFeeObj;
import com.bb.beckn.search.model.ConfirmAuditSellerObj;
import com.bb.beckn.search.model.ItemDetailsKirana;
import com.bb.beckn.search.model.KiranaObj;

public interface ConfirmAuditSellerRepository extends JpaRepository<ConfirmAuditSellerObj, Long> {

	Optional<ConfirmAuditSellerObj> findBytransactionid(String Transactionid);
	List<ConfirmAuditSellerObj> findByorderid(String orderid);
	
	@Query("SELECT k FROM ConfirmAuditSellerObj k where k.orderid= :orderid  and k.sellerorderstate= :sellerorderstate ")
	ConfirmAuditSellerObj findByorderidandorderstate( String orderid, String sellerorderstate);
	
	@Modifying
    @Transactional
    @Query(value = "insert into ConfirmAuditSellerObj (Transactionid, Orderid, sellerorderstate,logisticorderstate, Paymenttype, Paymentdoneby, Amountatconfirmation,Logisticprovidername,"
    		+ "Logisticproviderid, Logisticdeliverycharge, Logisticdeliverytype, Refundtype, Refundamount, Refundby, Refundbearyby, Refundreason, Canceltype,"
    		+ "Cancelamount, Camncelby, cancelamountbearbybuyer,cancelamountbearbyseller,cancelamountbearbylogistic Cancelreason, Creationdate, Updationdate) VALUES (?, ?,?,?, ?,?,?,?, ?,?,?, ?,?,?, ?,?,?, ?,?,?,?,?, ?,?,?)", nativeQuery = true)
	
	ConfirmAuditSellerObj save(String Transactionid, String Orderid,String sellerorderstate,String logisticorderstate, String Paymenttype,String Paymentdoneby,String Amountatconfirmation,String Logisticprovidername,
			String Logisticproviderid, String Logisticdeliverycharge,String Logisticdeliverytype,String Refundtype,String Refundamount,String Refundby,String Refundbearyby,
			String Refundreason,String Canceltype,String Cancelamount,String Camncelby, String cancelamountbearbybuyer,String cancelamountbearbyseller,String cancelamountbearbylogistic, String Cancelreason, String Creationdate,String Updationdate);
 
	//Boolean existsByDomain(String storename);
	
	//@Query("SELECT k FROM ApiSellerObj k where transaction_id = :transactionid  and k.message_id =:messageid ")
	//List<ConfirmAuditSellerObj>  findByTransactionidandMessageid(@Param("transactionid") String transactionid , @Param("messageid") String messageid);
    
	//@Query("SELECT k FROM ApiSellerObj k where k.transaction_id= :transaction_id and k.action= :action")
	//List<ConfirmAuditSellerObj> findByTransactionIdandAction( String transaction_id, String action);
	
}
