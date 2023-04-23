package com.bb.beckn.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bb.beckn.search.model.CityObj;
import com.bb.beckn.search.model.ItemDetailsKirana;
import com.bb.beckn.search.model.ItemObj;

public interface CityRepository extends JpaRepository<CityObj, Long> {


	Boolean existsByCode(String code);

	Boolean existsByCity(String city);
	
	@Query("SELECT k.city FROM CityObj k where code like %:code% ")
	List<String>  findByCityCode(@Param("code") String code);
	
	
}
