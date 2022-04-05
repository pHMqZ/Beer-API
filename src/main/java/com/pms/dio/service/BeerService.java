package com.pms.dio.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pms.dio.dto.BeerDTO;
import com.pms.dio.exception.BeerAlreadyRegisteredException;
import com.pms.dio.exception.BeerNotFoundException;
import com.pms.dio.exception.BeerStockExceededException;
import com.pms.dio.mapper.BeerMapper;
import com.pms.dio.model.Beer;
import com.pms.dio.repository.BeerRepository;

@Service
public class BeerService {

	@Autowired
	private BeerRepository beerRepo;

	private final BeerMapper beerMap = BeerMapper.INSTANCE;

	public BeerDTO createBeer(BeerDTO beerDTO) throws BeerAlreadyRegisteredException {
		verifyIfIsAlreadyRegistered(beerDTO.getName());
		Beer beer = beerMap.toModel(beerDTO);
		Beer saveBeer = beerRepo.save(beer);
		return beerMap.toDTO(saveBeer);
	}

	public BeerDTO findByName(String name) throws BeerNotFoundException {
		Beer foundBeer = beerRepo.findByName(name).orElseThrow(() -> new BeerNotFoundException(name));
		return beerMap.toDTO(foundBeer);
	}

	public List<BeerDTO> listAll() {
		return beerRepo.findAll().stream().map(beerMap::toDTO).collect(Collectors.toList());
	}

	public void deleteById(Long id) throws BeerNotFoundException {
		verifyIfExists(id);
		beerRepo.deleteById(id);
	}

	private void verifyIfIsAlreadyRegistered(String name) throws BeerAlreadyRegisteredException {
		Optional<Beer> optSavedBeer = beerRepo.findByName(name);
		if (optSavedBeer.isPresent()) {
			throw new BeerAlreadyRegisteredException(name);
		}
	}

	private Beer verifyIfExists(Long id) throws BeerNotFoundException {
		return beerRepo.findById(id).orElseThrow(() -> new BeerNotFoundException(id));
	}

	public BeerDTO increment(Long id, int quantityToIncrement) throws BeerNotFoundException, BeerStockExceededException {
		Beer beerToIncrementStock = verifyIfExists(id);
		int beerStockAfterIncrement = quantityToIncrement + beerToIncrementStock.getQuantity();
		if(beerStockAfterIncrement <= beerToIncrementStock.getMax()) {
			beerToIncrementStock.setQuantity(beerStockAfterIncrement);
			Beer incrementedBeerStock = beerRepo.save(beerToIncrementStock);
			return beerMap.toDTO(incrementedBeerStock);
		}
		
		throw new BeerStockExceededException(id, quantityToIncrement);
	}
	
	public BeerDTO decrement(Long id, int quantityToDecrement) throws BeerStockExceededException, BeerNotFoundException {
		Beer beerToDecrementStock = verifyIfExists(id);
		int beerStockAfterDecremented = beerToDecrementStock.getQuantity() - quantityToDecrement;
		if(beerStockAfterDecremented >=0) {
			beerToDecrementStock.setQuantity(beerStockAfterDecremented);
			Beer decrementedStock = beerRepo.save(beerToDecrementStock);
			return beerMap.toDTO(decrementedStock);
		}
		throw new BeerStockExceededException(id, quantityToDecrement);
	}

}
