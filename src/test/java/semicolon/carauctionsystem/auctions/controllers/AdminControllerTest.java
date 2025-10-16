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
import semicolon.carauctionsystem.auctions.services.AuctionService;
import semicolon.carauctionsystem.users.dtos.AuthResponseDto;
import semicolon.carauctionsystem.users.dtos.RegisterRequestDto;
import semicolon.carauctionsystem.users.services.AuthService;
import semicolon.carauctionsystem.users.services.AuthServiceImpl;
import semicolon.carauctionsystem.users.services.JwtService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = { AdminController.class }, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class AdminControllerTest {

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

    @MockitoBean
    private AuthService authService;

    @Test
    void testThatCanApproveAuctionEvent() throws Exception {
        when(auctionService.approveAuction(any())).thenReturn(Mono.empty());

        ApprovalRequestDto approvalRequestDto = new ApprovalRequestDto();
        approvalRequestDto.setAuctionId(UUID.randomUUID());
        approvalRequestDto.setAuctionStatus(AuctionStatus.NOT_STARTED);
        approvalRequestDto.setAuctionDuration(Duration.ofHours(1));
        approvalRequestDto.setAuctionStartDate(LocalDateTime.now().plusDays(1));
        approvalRequestDto.setAuctionEndDate(LocalDateTime.now().plusDays(2));


        mockMvc.perform(put("/api/v1/admin/approve-auction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(approvalRequestDto)))
                .andExpect(status().isOk());

        verify(auctionService).approveAuction(any());
    }

    @Test
    void testThatCanRegisterAsAdmin() throws Exception {
        AuthResponseDto responseDto = new AuthResponseDto();
        responseDto.setToken("token");

        when(authService.registerAsAdmin(any())).thenReturn(responseDto);

        RegisterRequestDto registerRequestDto = new RegisterRequestDto();
        registerRequestDto.setFirstName("firstName");
        registerRequestDto.setLastName("lastName");
        registerRequestDto.setEmail("email");
        registerRequestDto.setPassword("Password123");

        mockMvc.perform(post("/api/v1/admin/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value(responseDto.getToken()));
    }

    @Test
    void testThatCanApproveSeller() throws Exception {

        doNothing().when(authService).approveSeller(any());

        mockMvc.perform(put("/api/v1/admin/approve-seller/{sellerId}", UUID.randomUUID()))
                .andExpect(status().isAccepted());

        verify(authService).approveSeller(any());




    }
}