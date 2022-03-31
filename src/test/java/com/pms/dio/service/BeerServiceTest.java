package com.pms.dio.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
import com.pms.dio.exception.BeerNotFoundException;
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
		BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		Beer savedBeer = beerMapper.toModel(expectedBeerDTO);
		
		//quando
		when(beerRepo.findByName(expectedBeerDTO.getName())).thenReturn(Optional.empty());
		when(beerRepo.save(savedBeer)).thenReturn(savedBeer);
		
		//então
		BeerDTO createdBeerDTO = beerServ.createBeer(expectedBeerDTO);
		
		assertThat(createdBeerDTO.getId(), is(equalTo(expectedBeerDTO.getId())));
		assertThat(createdBeerDTO.getName(), is(equalTo(expectedBeerDTO.getName())));
		assertThat(createdBeerDTO.getQuantity(), is(equalTo(expectedBeerDTO.getQuantity())));
		
		assertThat(createdBeerDTO.getQuantity(), is(greaterThan(2)));
	}
	
	//ExceptionTest-AlreadyRegistered
	@Test
	void wheAlreadyRegisteredBeerInformedThenExceptionShouldBeThrow() throws BeerAlreadyRegisteredException {
		//given
		BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		Beer duplicatedBeer = beerMapper.toModel(expectedBeerDTO);
		
		//when
		when(beerRepo.findByName(expectedBeerDTO.getName())).thenReturn(Optional.of(duplicatedBeer));
		
		//then
		assertThrows(BeerAlreadyRegisteredException.class,() -> beerServ.createBeer(expectedBeerDTO));
		
	}
	
	//FindByName Teste
	@Test
	void whenValidBeerNameIsGivenTheReturnABeer() throws BeerNotFoundException {
		//given
		BeerDTO expectedFoundBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		Beer expectedFoundBeer = beerMapper.toModel(expectedFoundBeerDTO);
		
		//when
		when(beerRepo.findByName(expectedFoundBeer.getName())).thenReturn(Optional.of(expectedFoundBeer));
		
		//then
		BeerDTO foundBeerDTO = beerServ.findByName(expectedFoundBeerDTO.getName());
		
		assertThat(foundBeerDTO, is(equalTo(expectedFoundBeerDTO)));
		
		
	}
	
	
}
