package com.pms.dio.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pms.dio.builder.BeerDTOBuilder;
import com.pms.dio.dto.BeerDTO;
import com.pms.dio.exception.BeerAlreadyRegisteredException;
import com.pms.dio.mapper.BeerMapper;
import com.pms.dio.model.Beer;
import com.pms.dio.repository.BeerRepository;

@ExtendWith(MockitoExtension.class)
public class BeerServiceTest {
	
	private static final long INVALID_BEER_ID = 1L;
	
	private BeerMapper beerMapper = BeerMapper.INSTANCE;
	
	@Mock
	private BeerRepository beerRepo;
	
	@InjectMocks
	private BeerService beerServ;
	
	//createdBeerTest
	@Test
	void whenBeerInformedThenItShouldBeCreated() throws BeerAlreadyRegisteredException {
		//dado
		BeerDTO expectedbeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		Beer savedBeer = beerMapper.toModel(expectedbeerDTO);
		
		//quando
		when(beerRepo.findByName(expectedbeerDTO.getName())).thenReturn(Optional.empty());
		when(beerRepo.save(savedBeer)).thenReturn(savedBeer);
		
		//então
		BeerDTO createdBeerDTO = beerServ.createBeer(expectedbeerDTO);
		
		assertThat(createdBeerDTO.getId(), is(equalTo(expectedbeerDTO.getId())));
		assertThat(createdBeerDTO.getName(), is(equalTo(expectedbeerDTO.getName())));
		assertThat(createdBeerDTO.getQuantity(), is(equalTo(expectedbeerDTO.getQuantity())));
		
		assertThat(createdBeerDTO.getQuantity(), is(greaterThan(2)));
	}
	
	
}
