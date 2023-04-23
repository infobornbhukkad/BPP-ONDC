package com.bb.beckn.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bb.beckn.search.model.ItemDetailsKirana;
import com.bb.beckn.search.model.ItemObj;

public interface ItemRepository extends JpaRepository<ItemObj, Long> {

	Optional<ItemObj> findById(Long id);

	Optional<ItemObj> findByItemname(String itemname);

	Boolean existsByItemname(String itemname);

	Boolean existsByVendorid(Long vendorid);
	
	@Query("SELECT distinct (k.vendorid) FROM ItemObj k where itemname like %:itemname% ")
	List<String>  findDistinctByVendorid(@Param("itemname") String itemname);
	
	@Query("SELECT k FROM ItemObj k where k.itemname like %:itemname%  and k.vendorid =:search_vendorid ")
	List<ItemObj>  findByVendoridandsearch(@Param("itemname") String itemname , @Param("search_vendorid") String search_vendorid);
	
	@Query("SELECT k FROM ItemObj k where k.itemname like %:itemname%  and k.vendorid =:search_vendorid and k.itemcategory like %:itemcategory%  ")
	List<ItemObj>  findByVendoridanditemcategoryandsearch(@Param("itemcategory") String itemcategory, @Param("itemname") String itemname , @Param("search_vendorid") String search_vendorid);
	
	@Query("SELECT k FROM ItemObj k where k.vendorid =:search_vendorid and k.itemcategory like %:itemcategory%  ")
	List<ItemObj>  findByVendoridanditemcategory(@Param("itemcategory") String itemcategory,  @Param("search_vendorid") String search_vendorid);
	
	@Query("SELECT k FROM ItemObj k where k.vendorid =:search_vendorid ")
	List<ItemObj>  findByVendorid(@Param("search_vendorid") String search_vendorid);
	
	@Query("SELECT k FROM ItemObj k where k.id= :imageId ")
	List<ItemObj>  findByImagesearch( Long imageId);
	
	@Query("SELECT k FROM ItemObj k where k.id= :id and k.vendorid =:search_vendorid")
	ItemObj findByItemidandvendorid( Long id, @Param("search_vendorid") String search_vendorid);
	
	@Query("SELECT k FROM ItemObj k where k.id in(:itemQery) and k.vendorid =:search_vendorid")
	List<ItemObj> findBylistItemidandvendorid( List itemQery , @Param("search_vendorid") String search_vendorid);
	
	@Query("SELECT k FROM ItemObj k where k.id= :id ")
	ItemObj findByItemid( Long id);

}
