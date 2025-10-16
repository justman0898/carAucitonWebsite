package semicolon.carauctionsystem.users.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import semicolon.carauctionsystem.users.data.models.Kyc;
import semicolon.carauctionsystem.users.data.models.KycStatus;
import semicolon.carauctionsystem.users.data.models.Role;
import semicolon.carauctionsystem.users.data.models.User;
import semicolon.carauctionsystem.users.data.repository.KycRepository;
import semicolon.carauctionsystem.users.data.repository.UserRepository;
import semicolon.carauctionsystem.users.dtos.*;
import semicolon.carauctionsystem.users.exceptions.EmailAlreadyInUseException;
import semicolon.carauctionsystem.users.exceptions.InvalidPasswordException;
import semicolon.carauctionsystem.users.exceptions.UserNotFoundException;
import semicolon.carauctionsystem.users.services.AuthServiceImpl;
import semicolon.carauctionsystem.users.services.JwtService;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private KycRepository kycRepository;

    @Autowired
    private AuthServiceImpl authService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Captor
    private ArgumentCaptor<Kyc> kycCaptor;

    @Test
    void testThatCanSignUpWithValidUserDetails() {
        RegisterRequestDto  user = new RegisterRequestDto();
        user.setLastName("test");
        user.setFirstName("test");
        user.setEmail("test@test");
        user.setPassword(passwordEncoder.encode("test"));

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(passwordEncoder.encode("test")).thenReturn("test");
        when(jwtService.generateToken(any())).thenReturn("JwtToken");
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]);

        AuthResponseDto registeredUser = authService.register(user);
        assertNotNull(registeredUser);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testThatCannotSignUpWithEmailAlreadyExists() {
        RegisterRequestDto  user = new RegisterRequestDto();
        user.setLastName("test");
        user.setFirstName("test");
        user.setEmail("test@test");
        user.setPassword(passwordEncoder.encode("test"));

        when(userRepository.existsByEmail(any())).thenReturn(true);

        assertThrowsExactly(EmailAlreadyInUseException.class, ()-> authService.register(user));
    }

    @Test
    void testThatCanSignInWithValidUserDetails() {
        LoginRequestDto user = new LoginRequestDto();
        user.setEmail("test@test");
        user.setPassword("password");

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(new User()));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(jwtService.generateToken(any())).thenReturn("JwtToken");

        AuthResponseDto registeredUser = authService.login(user);
        assertNotNull(registeredUser);
        assertEquals("JwtToken",registeredUser.getToken());
    }

    @Test
    void testThatCannotSignInWithIncorrectPassword() {
        LoginRequestDto user = new LoginRequestDto();
        user.setEmail("test@test");
        user.setPassword("password");

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(new User()));
        when(passwordEncoder.matches(user.getPassword(), user.getPassword())).thenReturn(false);

        assertThrowsExactly(InvalidPasswordException.class, ()-> authService.login(user));
    }

    @Test
    void testThatThrowsUserNotFoundExceptionWhenUserNotFound() {
        LoginRequestDto user = new LoginRequestDto();
        user.setEmail("test@test");
        user.setPassword("password");

        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        assertThrowsExactly(UserNotFoundException.class, ()-> authService.login(user));
    }

    @Test
    void testThatCanRegisterWithValidDetailsAsAdmin(){
        RegisterRequestDto  user = new RegisterRequestDto();
        user.setLastName("test");
        user.setFirstName("test");
        user.setEmail("test@test");
        user.setPassword(passwordEncoder.encode("test"));

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(passwordEncoder.encode("test")).thenReturn("test");
        when(jwtService.generateToken(any())).thenReturn("JwtToken");
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]);

        AuthResponseDto registeredUser = authService.registerAsAdmin(user);
        assertNotNull(registeredUser);
        verify(userRepository).save(userCaptor.capture());

        User capturedValue = userCaptor.getValue();
        assertEquals(Set.of(Role.ADMIN, Role.BUYER), capturedValue.getRoles());
    }

    @Test
    void testThatExistingUserUpgradesToAdminDuringAdminRegistration() {
        RegisterRequestDto  user = new RegisterRequestDto();
        user.setLastName("test");
        user.setFirstName("test");
        user.setEmail("test@test");
        user.setPassword(passwordEncoder.encode("test"));

        User existingUser = new User();
        existingUser.setEmail("test@test");
        existingUser.setPassword("password");
        existingUser.setFirstName("test");
        existingUser.setLastName("test");
        existingUser.setRoles(Set.of(Role.BUYER));


        when(userRepository.existsByEmail(any())).thenReturn(true);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(existingUser));
        when(jwtService.generateToken(any())).thenReturn("JwtToken");
        when(passwordEncoder.encode("test")).thenReturn("test");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]);

        AuthResponseDto registeredUser = authService.registerAsAdmin(user);
        assertNotNull(registeredUser);
        assertEquals("JwtToken", registeredUser.getToken());

        verify(userRepository).save(userCaptor.capture());

        User capturedValue = userCaptor.getValue();
        assertEquals(Set.of(Role.ADMIN, Role.BUYER), capturedValue.getRoles());
    }

    @Test
    void testThatCanApplyToBecomeSeller() {

        when(userRepository.findById(any())).thenReturn(Optional.of(new User()));
        when(kycRepository.save(any(Kyc.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]);

        KycRequestDto request = new KycRequestDto();
        request.setUserId(UUID.randomUUID());

        authService.applyToBecomeSeller(request);

        verify(kycRepository).save(kycCaptor.capture());
        Kyc capturedValue = kycCaptor.getValue();

        assertEquals(capturedValue.getUserId(), request.getUserId());

    }

    @Test
    void testThatOnlyUsersCanApplyToBecomeSeller() {

        when(userRepository.findById(any())).thenReturn(Optional.empty());

        KycRequestDto request = new KycRequestDto();
        request.setUserId(UUID.randomUUID());

        assertThrowsExactly(UserNotFoundException.class , ()-> authService.applyToBecomeSeller(request));
    }

    @Test
    void testThatAdminCanApproveSeller() {
        User user = new User();
        user.setRoles(Set.of(Role.BUYER));

        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        authService.approveSeller(UUID.randomUUID());

        verify(userRepository).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();
        assertEquals(Set.of(Role.BUYER,  Role.SELLER), capturedUser.getRoles());
        assertEquals(KycStatus.APPROVED, capturedUser.getKycStatus());
    }

    @Test
    void testThatThrowsEmailAlreadyInUseExceptionIfAdminAlreadyExists(){
        RegisterRequestDto  user = new RegisterRequestDto();
        user.setLastName("test");
        user.setFirstName("test");
        user.setEmail("test@test");
        user.setPassword(passwordEncoder.encode("test"));

        User existingUser = new User();
        existingUser.setEmail("test@test");
        existingUser.setPassword("password");
        existingUser.setFirstName("test");
        existingUser.setLastName("test");
        existingUser.setRoles(Set.of(Role.ADMIN));

        when(userRepository.existsByEmail(any())).thenReturn(true);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(existingUser));

        assertThrowsExactly(EmailAlreadyInUseException.class , ()-> authService.registerAsAdmin(user));

    }

}