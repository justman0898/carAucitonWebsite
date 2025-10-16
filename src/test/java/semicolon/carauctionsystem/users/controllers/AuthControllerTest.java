package semicolon.carauctionsystem.users.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.annotation.Validated;
import semicolon.carauctionsystem.users.data.models.DocumentType;
import semicolon.carauctionsystem.users.dtos.AuthResponseDto;
import semicolon.carauctionsystem.users.dtos.KycRequestDto;
import semicolon.carauctionsystem.users.dtos.LoginRequestDto;
import semicolon.carauctionsystem.users.dtos.RegisterRequestDto;
import semicolon.carauctionsystem.users.exceptions.InvalidPasswordException;
import semicolon.carauctionsystem.users.exceptions.UserNotFoundException;
import semicolon.carauctionsystem.users.services.AuthService;
import semicolon.carauctionsystem.users.services.JwtService;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestPropertySource(properties = "logging.level.org.springframework=DEBUG")
@WebMvcTest(value = AuthController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@Validated
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;


    @Test
    void testThatCanRegisterWithValidUser() throws Exception {
        RegisterRequestDto requestDto = new RegisterRequestDto();
        requestDto.setEmail("test@test");
        requestDto.setPassword("testPassword12&3");
        requestDto.setFirstName("testName");
        requestDto.setLastName("testLastName");

        AuthResponseDto responseDto = new AuthResponseDto();
        responseDto.setToken("token");

        when(authService.register(requestDto)).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(responseDto.getToken()));
    }

    @Test
    void testThatCannotRegisterWithInvalidUser() throws Exception {
        RegisterRequestDto requestDto = new RegisterRequestDto();
        requestDto.setEmail("test@test");
        requestDto.setPassword("testPassword");
        requestDto.setFirstName("testName");
        requestDto.setLastName("testLastName");

        AuthResponseDto responseDto = new AuthResponseDto();
        responseDto.setToken("token");

        mockMvc.perform(post("/api/v1/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testThatCanLoginWithExistingDetails() throws Exception {
        LoginRequestDto requestDto = new LoginRequestDto();
        requestDto.setEmail("test@test");
        requestDto.setPassword("testPassword");

        AuthResponseDto responseDto = new AuthResponseDto();
        responseDto.setToken("token");

        when(authService.login(requestDto)).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(responseDto.getToken()));
    }

    @Test
    void testThatCannotLoginIfUserNotFound() throws Exception {
        LoginRequestDto requestDto = new LoginRequestDto();
        requestDto.setEmail("test@test");
        requestDto.setPassword("testPassword");

        when(authService.login(requestDto)).thenThrow(new UserNotFoundException());

        mockMvc.perform(post("/api/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));
        verify(authService).login(requestDto);
    }

    @Test
    void testThatReturnsInvalidPasswordWhenUserFoundButPasswordDoesNotMatch() throws Exception {
        LoginRequestDto requestDto = new LoginRequestDto();
        requestDto.setEmail("test@test");
        requestDto.setPassword("testPassword");

        when(authService.login(requestDto)).thenThrow(new InvalidPasswordException());
        mockMvc.perform(post("/api/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid password"));
        verify(authService).login(requestDto);

    }

    @Test
    void testThatCanRegisterAsAdminWithValidUserDetails() throws Exception {
        RegisterRequestDto requestDto = new RegisterRequestDto();
        requestDto.setEmail("test@test");
        requestDto.setPassword("testPassword12&3");
        requestDto.setFirstName("testName");
        requestDto.setLastName("testLastName");

        AuthResponseDto responseDto = new AuthResponseDto();
        responseDto.setToken("token");

        when(authService.registerAsAdmin(any(RegisterRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/auth/admin-register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(responseDto.getToken()));
    }

    @Test
    void testThatCanApplyToBecomeSellerWithValidUserDetails() throws Exception {
        KycRequestDto requestDto = new KycRequestDto();
        requestDto.setUserId(UUID.randomUUID());
        requestDto.setDocumentType(DocumentType.PASSPORT);
        requestDto.setDocumentId(UUID.randomUUID().toString());
        requestDto.setDocumentUrl("img1");

        mockMvc.perform(put("/api/v1/auth/register-seller")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        verify(authService).applyToBecomeSeller(any(KycRequestDto.class));

    }

    @Test
    void testThatCanApproveSellerWithValidUserDetails() throws Exception {


    }



}