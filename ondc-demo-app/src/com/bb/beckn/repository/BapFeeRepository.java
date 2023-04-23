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


public interface BapFeeRepository extends JpaRepository<BapFeeObj, Long> {


	Boolean existsByType(String type);
	
	Boolean existsBySubscriberid(String subscriberid);
	
	@Query("SELECT k FROM BapFeeObj k where k.subscriberid= :subscriberid ")
	Optional<BapFeeObj> findBySubscriberIdItemid( String subscriberid);
	
	@Query("SELECT k FROM BapFeeObj k where k.subscriberid= :subscriberid ")
	BapFeeObj findBySubscriberId( String subscriberid);
	
	/*
	 * @Modifying(clearAutomatically = true)
	 * 
	 * @Query("update bornbhukkad.ondc_bap_finder_fee baf set baf.finder_fee_amount =:finder_fee_amount , baf.type =:type , baf.updated_on=:updated_on where baf.subscriberid=:subscriberid"
	 * ) void updateSubscriberRecord(@Param("finder_fee_amount") String
	 * finder_fee_amount , @Param("type") String type , @Param("updated_on") String
	 * updated_on, @Param("subscriberid") String subscriberid);
	 * 
	 */
	
	
	
}
