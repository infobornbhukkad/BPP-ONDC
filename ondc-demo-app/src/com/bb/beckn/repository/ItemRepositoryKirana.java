package com.bb.beckn.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bb.beckn.search.model.ItemDetailsKirana;
import com.bb.beckn.search.model.KiranaObj;

public interface ItemRepositoryKirana extends JpaRepository<ItemDetailsKirana, Long> {

	Optional<ItemDetailsKirana> findById(Long id);

	Optional<ItemDetailsKirana> findByItemname(String itemname);
	
	@Query("SELECT distinct (k.vendorid) FROM ItemDetailsKirana k where itemname like %:itemname% ")
	List<String>  findDistinctByVendorid(@Param("itemname") String itemname);

	Boolean existsByItemname(String itemname);

	Boolean existsByVendorid(Long vendorid);
	
	@Query("SELECT k FROM ItemDetailsKirana k where itemname like %:itemname%  and k.vendorid =:search_vendorid ")
	List<ItemDetailsKirana>  findByVendoridandsearch(@Param("itemname") String itemname , @Param("search_vendorid") String search_vendorid);
	
	@Query("SELECT k FROM ItemDetailsKirana k where itemname like %:itemname%  and k.vendorid =:search_vendorid and k.itemcategory like %:itemcategory% ")
	List<ItemDetailsKirana>  findByVendoridanditemcategoryandsearch(@Param("itemcategory") String itemcategory, @Param("itemname") String itemname , @Param("search_vendorid") String search_vendorid);
	
	@Query("SELECT k FROM ItemDetailsKirana k where k.itemcategory like %:itemcategory% and k.vendorid =:search_vendorid ")
	List<ItemDetailsKirana>  findByVendoridanditemcategory(@Param("itemcategory") String itemcategory,  @Param("search_vendorid") String search_vendorid);
	
	@Query("SELECT k FROM ItemDetailsKirana k where k.vendorid =:search_vendorid ")
	List<ItemDetailsKirana>  findByVendorid(@Param("search_vendorid") String search_vendorid);
	
	@Query("SELECT k FROM ItemDetailsKirana k where k.id= :imageId ")
	List<ItemDetailsKirana>  findByImagesearch( Long imageId);
    
	@Query("SELECT k FROM ItemDetailsKirana k where k.id= :id ")
	ItemDetailsKirana findByItemid( Long id);
	
	@Query("SELECT k FROM ItemDetailsKirana k where k.id= :id and k.vendorid =:search_vendorid")
	ItemDetailsKirana findByItemidandvendorid( Long id, @Param("search_vendorid") String search_vendorid);
	
	@Query("SELECT k FROM ItemDetailsKirana k where k.id in (:itemQery) and k.vendorid =:search_vendorid")
	List<ItemDetailsKirana> findBylistItemidandvendorid( List itemQery, @Param("search_vendorid") String search_vendorid);
	
}
