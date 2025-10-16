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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import semicolon.carauctionsystem.auctions.data.models.AuctionStatus;
import semicolon.carauctionsystem.auctions.dtos.request.BidRequestDto;
import semicolon.carauctionsystem.auctions.dtos.response.AuctionResponseDto;
import semicolon.carauctionsystem.auctions.services.AuctionService;
import semicolon.carauctionsystem.messaging.AuctionWebSocketHandler;
import semicolon.carauctionsystem.messaging.BidQueuePublisher;
import semicolon.carauctionsystem.users.services.JwtService;
import static org.hamcrest.Matchers.hasItems;


import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = BidController.class, excludeAutoConfiguration= SecurityAutoConfiguration.class)
class BidControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BidQueuePublisher publisher;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private AuctionWebSocketHandler  auctionWebSocketHandler;

    @MockitoBean
    private AuctionService auctionService;

    @Autowired
    ObjectMapper objectMapper;



    @Test
    void testThatCanPlaceValidBidSuccessfully() throws Exception {
        BidRequestDto request = new BidRequestDto();
        request.setAuctionId(UUID.randomUUID());
        request.setBidderId(UUID.randomUUID());
        request.setAmount(new BigDecimal(100));

        when(publisher.publishBidCreatedEvent(any())).thenReturn("Bid placed successfully");

        mockMvc.perform(post("/api/v1/buyer/bid")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testThatCanViewAuctionSuccessfully() throws Exception {

        AuctionResponseDto response = new AuctionResponseDto();
        response.setId(UUID.randomUUID());
        response.setStatus(AuctionStatus.NOT_STARTED);

        when(auctionService.viewAuction(any(UUID.class))).thenReturn(Mono.just(response));

        mockMvc.perform(get("/api/v1/buyer/auction/{auctionId}", UUID.randomUUID().toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(objectMapper.writeValueAsString(response)))
                .andExpect(jsonPath("$.status").value(AuctionStatus.NOT_STARTED.toString()));
    }

    @Test
    void testThatCanViewAllLiveAuctionsSuccessfully() throws Exception {

        AuctionResponseDto response = new AuctionResponseDto();
        response.setId(UUID.randomUUID());
        response.setStatus(AuctionStatus.LIVE);

        AuctionResponseDto response2 = new AuctionResponseDto();
        response2.setId(UUID.randomUUID());
        response2.setStatus(AuctionStatus.LIVE);

        when(auctionService.getLiveAuctions()).thenReturn(Flux.just(response, response2));

        mockMvc.perform(get("/api/v1/buyer/live-auctions"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[*].status", hasItems(AuctionStatus.LIVE.toString(), AuctionStatus.LIVE.toString())));
    }


}