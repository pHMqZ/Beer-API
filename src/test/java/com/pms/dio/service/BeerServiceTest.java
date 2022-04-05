package com.pms.dio.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
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
		//given
		BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		Beer savedBeer = beerMapper.toModel(expectedBeerDTO);
		
		//when
		when(beerRepo.findByName(expectedBeerDTO.getName())).thenReturn(Optional.empty());
		when(beerRepo.save(savedBeer)).thenReturn(savedBeer);
		
		//then
		BeerDTO createdBeerDTO = beerServ.createBeer(expectedBeerDTO);
		
		assertThat(createdBeerDTO.getId(), is(equalTo(expectedBeerDTO.getId())));
		assertThat(createdBeerDTO.getName(), is(equalTo(expectedBeerDTO.getName())));
		assertThat(createdBeerDTO.getQuantity(), is(equalTo(expectedBeerDTO.getQuantity())));
		
		assertThat(createdBeerDTO.getQuantity(), is(greaterThan(2)));
	}
	
	//ExceptionTest-Already Registered Beer
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
	//Teste de Exceção - Beer Not Found
	@Test
	void whenNotRegisteredBeerNameIsGivenTheThrowsAException() throws BeerNotFoundException {
		//given
		BeerDTO expectedFoundBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		
		
		//when
		when(beerRepo.findByName(expectedFoundBeerDTO.getName())).thenReturn(Optional.empty());
		
		//then
		assertThrows(BeerNotFoundException.class, () -> beerServ.findByName(expectedFoundBeerDTO.getName()));
		
	}
	//Teste - Find All
	@Test
	void whenListBeerIsCalledThenReturnAListOfBeers() {
		//given
		BeerDTO expectedFoundBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		Beer expectedFoundBeer = beerMapper.toModel(expectedFoundBeerDTO);
		
		//when
		when(beerRepo.findAll()).thenReturn(Collections.singletonList(expectedFoundBeer));
		
		//then
		List<BeerDTO> foundBeerDTO = beerServ.listAll();
		
		assertThat(foundBeerDTO, is(not(empty())));
		assertThat(foundBeerDTO.get(0), is(equalTo(expectedFoundBeerDTO)));
		
	}


	@Test
	void whenListBeerIsCalledThenReturnAnEmptyListOfBeers() {
		// when
		when(beerRepo.findAll()).thenReturn(Collections.EMPTY_LIST);

		// then
		List<BeerDTO> foundBeerDTO = beerServ.listAll();

		assertThat(foundBeerDTO, is(empty()));

	}
	
	@Test
	void whenExclusionIsCalledWithValidIdABeerShouldBeDeleted() throws BeerNotFoundException {
		// given
        BeerDTO expectedDeletedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer expectedDeletedBeer = beerMapper.toModel(expectedDeletedBeerDTO);
        
        //when
        when(beerRepo.findById(expectedDeletedBeerDTO.getId())).thenReturn(Optional.of(expectedDeletedBeer));
        doNothing().when(beerRepo).deleteById(expectedDeletedBeerDTO.getId());
        
        //then
        beerServ.deleteById(expectedDeletedBeerDTO.getId());
        
        verify(beerRepo, times(1)).findById(expectedDeletedBeerDTO.getId());
        verify(beerRepo, times(1)).deleteById(expectedDeletedBeerDTO.getId());
	}
	
	@Test
	void whenExclusionIsCalledWithInvalidIdThenExceptionShouldBeThrow() {
		//when
		when(beerRepo.findById(INVALID_BEER_ID)).thenReturn(Optional.empty());
		
		//then
		assertThrows(BeerNotFoundException.class, () -> beerServ.deleteById(INVALID_BEER_ID));
		
	}
}
