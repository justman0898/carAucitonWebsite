package semicolon.carauctionsystem.auctions.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import reactor.core.publisher.Mono;
import semicolon.carauctionsystem.auctions.data.models.AuctionStatus;
import semicolon.carauctionsystem.auctions.dtos.request.ApprovalRequestDto;
import semicolon.carauctionsystem.auctions.dtos.request.AuctionRequestDto;
import semicolon.carauctionsystem.auctions.dtos.request.CarRequestDto;
import semicolon.carauctionsystem.auctions.dtos.response.AuctionResponseDto;
import semicolon.carauctionsystem.auctions.dtos.response.CarResponseDto;
import semicolon.carauctionsystem.auctions.services.AuctionService;
import semicolon.carauctionsystem.users.services.JwtService;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuctionRouter.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class AuctionRouterTest {

    @MockitoBean
    private AuctionService auctionService;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testThatCanListVehicleForAuction() throws Exception {
        CarResponseDto carResponseDto = new CarResponseDto();
        carResponseDto.setId(UUID.randomUUID());
        carResponseDto.setMake("Toyota");
        carResponseDto.setStatus(AuctionStatus.PENDING);

        when(auctionService.listVehicle(any(CarRequestDto.class))).thenReturn(Mono.just(carResponseDto));

        CarRequestDto carRequestDto = new CarRequestDto();
        carRequestDto.setMake("Toyota");

        mockMvc.perform(post("/api/v1/seller/list-vehicle")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(carRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.make").value("Toyota"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void testThatCanCreateAuction() throws Exception {
        AuctionResponseDto responseDto = new AuctionResponseDto();
        responseDto.setId(UUID.randomUUID());
        responseDto.setStatus(AuctionStatus.PENDING);

        when(auctionService.createAuction(any())).thenReturn(Mono.just(responseDto));

        AuctionRequestDto auctionRequestDto = new AuctionRequestDto();
        auctionRequestDto.setCarId(UUID.randomUUID());

        mockMvc.perform(post("/api/v1/seller/create-auction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(auctionRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("PENDING"));
        
        verify(auctionService).createAuction(any());
    }









}