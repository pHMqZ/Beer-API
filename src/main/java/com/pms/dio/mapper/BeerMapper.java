package com.pms.dio.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.pms.dio.dto.BeerDTO;
import com.pms.dio.model.Beer;

@Mapper
public interface BeerMapper {

	BeerMapper INSTANCE = Mappers.getMapper(BeerMapper.class);

    Beer toModel(BeerDTO beerDTO);

    BeerDTO toDTO(Beer beer);;
}
