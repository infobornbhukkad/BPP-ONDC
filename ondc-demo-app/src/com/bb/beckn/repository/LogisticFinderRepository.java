package com.bb.beckn.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bb.beckn.search.model.BapFeeObj;
import com.bb.beckn.search.model.ItemDetailsKirana;
import com.bb.beckn.search.model.KiranaObj;
import com.bb.beckn.search.model.LogisticBppObj;


public interface LogisticFinderRepository extends JpaRepository<LogisticBppObj, Long> {


	Boolean existsBybppid(String bppid);
	
	Boolean existsByprovidername(String providername);
	
	@Query("SELECT k FROM LogisticBppObj k where k.providername= :providername ")
	Optional<LogisticBppObj> findByprovidername( String providername);
	
	@Query("SELECT k FROM LogisticBppObj k where k.transaction_id= :transaction_id ")
	List<LogisticBppObj> findBytransaction_id( String transaction_id);
	
	@Query("SELECT k FROM LogisticBppObj k where k.transaction_id= :transaction_id and k.action= :action")
	List<LogisticBppObj> findBytransaction_idandaction( String transaction_id, String action);
	
	
}
