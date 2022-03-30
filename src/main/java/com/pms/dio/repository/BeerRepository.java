package com.pms.dio.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pms.dio.model.Beer;

public interface BeerRepository extends JpaRepository<Beer, Long>{
	
	Optional<Beer> findByName (String name);

}
