package com.bb.beckn.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bb.beckn.search.model.ApiSellerObj;
import com.bb.beckn.search.model.BapFeeObj;
import com.bb.beckn.search.model.ItemDetailsKirana;
import com.bb.beckn.search.model.KiranaObj;

public interface ApiAuditSellerRepository extends JpaRepository<ApiSellerObj, Long> {

	Optional<ApiSellerObj> findByAction(String storename);
 
	Boolean existsByDomain(String storename);
	
	@Query("SELECT k FROM ApiSellerObj k where transaction_id = :transactionid  and k.message_id =:messageid ")
	List<ApiSellerObj>  findByTransactionidandMessageid(@Param("transactionid") String transactionid , @Param("messageid") String messageid);
    
	@Query("SELECT k FROM ApiSellerObj k where k.transaction_id= :transaction_id and k.action= :action")
	List<ApiSellerObj> findByTransactionIdandAction( String transaction_id, String action);
	
	@Query("SELECT k FROM ApiSellerObj k where k.transaction_id = :transactionid  and k.action =:action")
	ApiSellerObj  findByTransactionidandaction(@Param("transactionid") String transactionid ,@Param("action") String action);
	
}
