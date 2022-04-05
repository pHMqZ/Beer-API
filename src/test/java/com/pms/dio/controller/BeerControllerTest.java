package com.pms.dio.controller;

import static com.pms.dio.utils.JsonConvertionUtils.asJsonString;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.pms.dio.builder.BeerDTOBuilder;
import com.pms.dio.dto.BeerDTO;
import com.pms.dio.dto.QuantityDTO;
import com.pms.dio.exception.BeerNotFoundException;
import com.pms.dio.exception.BeerStockExceededException;
import com.pms.dio.service.BeerService;

@ExtendWith(MockitoExtension.class)
public class BeerControllerTest {
	
	private static final String BEER_API_URL_PATH = "/api/v1/beers";
    private static final long VALID_BEER_ID = 1L;
    private static final long INVALID_BEER_ID = 2l;
    private static final String BEER_API_SUBPATH_INCREMENT_URL = "/increment";
    private static final String BEER_API_SUBPATH_DECREMENT_URL = "/decrement";
    
    private MockMvc mockMvc;
    
    @Mock
    private BeerService beerServ;
    
    @InjectMocks
    private BeerController beerController;
    
    @BeforeEach
    void setUp() {
    	mockMvc = MockMvcBuilders.standaloneSetup(beerController)
    			.setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
    			.setViewResolvers((s, locale)-> new MappingJackson2JsonView())
    			.build();
    }
    //POST SUCESS
    @Test
    void whenPOSTIsCalledThenABeerIsCreated() throws Exception {
    	//given
    	BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
    	
    	//when
    	when(beerServ.createBeer(beerDTO)).thenReturn(beerDTO);
    	
    	//then
    	mockMvc.perform(post(BEER_API_URL_PATH)
    		.contentType(MediaType.APPLICATION_JSON)
    		.content(asJsonString(beerDTO)))
    		.andExpect(status().isCreated())
    		.andExpect(jsonPath("$.name", is(beerDTO.getName())))
    		.andExpect(jsonPath("$.brand", is(beerDTO.getBrand())))
    		.andExpect(jsonPath("$.type", is(beerDTO.getType().toString())));
    }
    
    //Testando POST Validation (Se falta um campo, não salva a cerveja)
    @Test
    void whenPOSTIsCalledwithoutRequiredFielThenAnErrorIsReturned() throws Exception {
    	//given
    	BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
    	beerDTO.setBrand(null);
    	    	
    	//then
    	mockMvc.perform(post(BEER_API_URL_PATH)
    		.contentType(MediaType.APPLICATION_JSON)
    		.content(asJsonString(beerDTO)))
    		.andExpect(status().isBadRequest());
    }
    //Testando GET - Find By Name
    @Test
    void wheGETIsCalledWithValidNameThenOkStatusIsReturned() throws Exception {
    	//given
    	BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
    	
    	//when
    	when(beerServ.findByName(beerDTO.getName())).thenReturn(beerDTO);
    	
    	//then
    	mockMvc.perform(MockMvcRequestBuilders.get(BEER_API_URL_PATH + "/" + beerDTO.getName())
    			.contentType(MediaType.APPLICATION_JSON))
    			.andExpect(status().isOk())
    			.andExpect(jsonPath("$.name", is(beerDTO.getName())))
        		.andExpect(jsonPath("$.brand", is(beerDTO.getBrand())))
        		.andExpect(jsonPath("$.type", is(beerDTO.getType().toString())));
    	
    }
    
    //Testando Exceção - Find By Name
    @Test
    void wheGETIsCalledWithoutRegisteredNameTheNotFoundIsReturned() throws Exception {
    	//given
    	BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
    	
    	//when
    	when(beerServ.findByName(beerDTO.getName())).thenThrow(BeerNotFoundException.class);
    	
    	//then
    	mockMvc.perform(MockMvcRequestBuilders.get(BEER_API_URL_PATH + "/" + beerDTO.getName())
    			.contentType(MediaType.APPLICATION_JSON))
    			.andExpect(status().isNotFound());
    	
    }
	//Teste GET - Find All
    @Test
    void wheGETListWithBeersIsCalledTheOkStatusIsReturned() throws Exception {
    	//given
    	BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
    	
    	//when
    	when(beerServ.listAll()).thenReturn(Collections.singletonList(beerDTO));
    	
    	//then
    	mockMvc.perform(MockMvcRequestBuilders.get(BEER_API_URL_PATH)
    			.contentType(MediaType.APPLICATION_JSON))
    			.andExpect(status().isOk())
    			.andExpect(jsonPath("$[0].name", is(beerDTO.getName())))
        		.andExpect(jsonPath("$[0].brand", is(beerDTO.getBrand())))
        		.andExpect(jsonPath("$[0].type", is(beerDTO.getType().toString())));
    	
    }
    
    @Test
    void wheGETListWithoutBeersIsCalledTheOkStatusIsReturned() throws Exception {
    	//given
    	BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
    	
    	//when
    	when(beerServ.listAll()).thenReturn(Collections.singletonList(beerDTO));
    	
    	//then
    	mockMvc.perform(MockMvcRequestBuilders.get(BEER_API_URL_PATH)
    			.contentType(MediaType.APPLICATION_JSON))
    			.andExpect(status().isOk());
    	
    }
    //Teste metodo Delete
    @Test
    void whenDELETEIsCalledWithValidIdThenNoContentStatusIsReturned() throws Exception {
        doNothing().when(beerServ).deleteById(VALID_BEER_ID);

        mockMvc.perform(delete(BEER_API_URL_PATH + "/" + VALID_BEER_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(beerServ, times(1)).deleteById(VALID_BEER_ID);
    }

    @Test
    void whenDELETEIsCalledWithoutValidIdThenNotFoundStatusIsReturned() throws Exception {
        doThrow(BeerNotFoundException.class).when(beerServ).deleteById(INVALID_BEER_ID);

        mockMvc.perform(delete(BEER_API_URL_PATH + "/" + INVALID_BEER_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void whenPATCHIsCalledToIncrementDiscountThenOKstatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(10)
                .build();

        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        beerDTO.setQuantity(beerDTO.getQuantity() + quantityDTO.getQuantity());

        when(beerServ.increment(VALID_BEER_ID, quantityDTO.getQuantity())).thenReturn(beerDTO);

        mockMvc.perform(patch(BEER_API_URL_PATH + "/" + VALID_BEER_ID + BEER_API_SUBPATH_INCREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO))).andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(beerDTO.getName())))
                .andExpect(jsonPath("$.brand", is(beerDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(beerDTO.getType().toString())))
                .andExpect(jsonPath("$.quantity", is(beerDTO.getQuantity())));
    }

    @Test
    void whenPATCHIsCalledToIncrementGreatherThanMaxThenBadRequestStatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(30)
                .build();

        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        beerDTO.setQuantity(beerDTO.getQuantity() + quantityDTO.getQuantity());

        when(beerServ.increment(VALID_BEER_ID, quantityDTO.getQuantity())).thenThrow(BeerStockExceededException.class);

        mockMvc.perform(patch(BEER_API_URL_PATH + "/" + VALID_BEER_ID + BEER_API_SUBPATH_INCREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO))).andExpect(status().isBadRequest());
    }

    @Test
    void whenPATCHIsCalledWithInvalidBeerIdToIncrementThenNotFoundStatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(30)
                .build();

        when(beerServ.increment(INVALID_BEER_ID, quantityDTO.getQuantity())).thenThrow(BeerNotFoundException.class);
        mockMvc.perform(patch(BEER_API_URL_PATH + "/" + INVALID_BEER_ID + BEER_API_SUBPATH_INCREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenPATCHIsCalledToDecrementDiscountThenOKstatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(5)
                .build();

        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        beerDTO.setQuantity(beerDTO.getQuantity() + quantityDTO.getQuantity());

        when(beerServ.decrement(VALID_BEER_ID, quantityDTO.getQuantity())).thenReturn(beerDTO);

        mockMvc.perform(patch(BEER_API_URL_PATH + "/" + VALID_BEER_ID + BEER_API_SUBPATH_DECREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO))).andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(beerDTO.getName())))
                .andExpect(jsonPath("$.brand", is(beerDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(beerDTO.getType().toString())))
                .andExpect(jsonPath("$.quantity", is(beerDTO.getQuantity())));
    }

    @Test
    void whenPATCHIsCalledToDecrementLowerThanZeroThenBadRequestStatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(60)
                .build();

        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        beerDTO.setQuantity(beerDTO.getQuantity() + quantityDTO.getQuantity());

        when(beerServ.decrement(VALID_BEER_ID, quantityDTO.getQuantity())).thenThrow(BeerStockExceededException.class);

        mockMvc.perform(patch(BEER_API_URL_PATH + "/" + VALID_BEER_ID + BEER_API_SUBPATH_DECREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO))).andExpect(status().isBadRequest());
    }

    @Test
    void whenPATCHIsCalledWithInvalidBeerIdToDecrementThenNotFoundStatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(5)
                .build();

        when(beerServ.decrement(INVALID_BEER_ID, quantityDTO.getQuantity())).thenThrow(BeerNotFoundException.class);
        mockMvc.perform(patch(BEER_API_URL_PATH + "/" + INVALID_BEER_ID + BEER_API_SUBPATH_DECREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO)))
                .andExpect(status().isNotFound());
    }
}


