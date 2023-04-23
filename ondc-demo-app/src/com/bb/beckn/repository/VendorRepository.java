package com.bb.beckn.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bb.beckn.search.model.KiranaObj;
import com.bb.beckn.search.model.VendorObj;

public interface VendorRepository extends JpaRepository<VendorObj, Long> {

	Optional<VendorObj> findByStorename(String storename);
	
	Optional<VendorObj> findById(Long id);

	Boolean existsByStorename(String storename);

	Boolean existsByEmail(String email);
	
	Boolean existsByPhone(String phone);
	
	@Query("select k from VendorObj k where k.id =:search_vendorid ")
	Optional<VendorObj> findBydata(@Param("search_vendorid") Long search_vendorid);
	
	@Query("SELECT k FROM VendorObj k where k.id= :imageId and k.symbol_name = :imagename ")
	List<VendorObj>  findByImagesearch( Long imageId, String imagename);

}
