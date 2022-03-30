package com.pms.dio.model;

import javax.persistence.Entity;

import com.pms.dio.enums.BeerType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Beer {

	private Long id;
	
	private String name;
	
	private String brand;
	
	private int max;
	
	private int quality;
	
	private BeerType type;
}
