package com.bb.beckn.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bb.beckn.search.model.ItemDetailsKirana;
import com.bb.beckn.search.model.KiranaObj;

public interface KiranaRepository extends JpaRepository<KiranaObj, Long> {

	Optional<KiranaObj> findByStorename(String storename);
 
	@Query("select k from KiranaObj k where k.id =:search_vendorid ")
	Optional<KiranaObj> findBydata(@Param("search_vendorid") Long search_vendorid);

	Boolean existsByStorename(String storename);

	Boolean existsByEmail(String email);

	Boolean existsByPhone(String phone);

	Boolean existsByUsername(String username);

	@Query("SELECT k FROM KiranaObj k where k.id= :imageId and k.symbol_name = :imagename ")
	List<KiranaObj>  findByImagesearch( Long imageId, String imagename);

}
