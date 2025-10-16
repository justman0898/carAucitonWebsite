package semicolon.carauctionsystem.users.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import semicolon.carauctionsystem.clients.EmailServiceClient;
import semicolon.carauctionsystem.messaging.RabbitMqPublisher;
import semicolon.carauctionsystem.users.data.models.*;
import semicolon.carauctionsystem.users.data.repository.KycRepository;
import semicolon.carauctionsystem.users.data.repository.UserRepository;
import semicolon.carauctionsystem.users.dtos.*;
import semicolon.carauctionsystem.users.exceptions.EmailAlreadyInUseException;
import semicolon.carauctionsystem.users.exceptions.InvalidPasswordException;
import semicolon.carauctionsystem.users.exceptions.UserNotFoundException;
import semicolon.carauctionsystem.users.security.UserPrincipal;
import semicolon.carauctionsystem.users.utils.Mapper;
import org.springframework.transaction.annotation.Transactional;
import semicolon.carauctionsystem.users.utils.Utils;


import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService  jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private Mapper mapper;

    @Autowired
    private EmailServiceClient emailService;

    @Autowired
    private RabbitMqPublisher rabbitMqPublisher;
    @Autowired
    private KycRepository kycRepository;


    @Override
    @Transactional()
    public AuthResponseDto register(RegisterRequestDto requestDto) {
        if(userRepository.existsByEmail(requestDto.getEmail())){
            throw new EmailAlreadyInUseException();
        }

        User user = mapper.toEntity(requestDto);
        user.setRoles(Set.of(Role.BUYER));
        user.setFirstName(requestDto.getFirstName());
        user.setLastName(requestDto.getLastName());
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        userRepository.save(user);

        EmailRequestDto request = new EmailRequestDto();
        request.setTo(requestDto.getEmail());
        request.setSubject(Utils.getEmailSubject());
        request.setBody(Utils.getWelcomeMessage(requestDto.getFirstName()));
        emailService.sendEmail(request).subscribe(unused -> log.info("Registration Email queued successfully to {}",  request.getTo()));

        AuthResponseDto authResponseDto = new AuthResponseDto();
        authResponseDto.setToken(jwtService.generateToken(new UserPrincipal(user)));

        UserRegisteredEvent registeredUserEvent = new UserRegisteredEvent();
        registeredUserEvent.setUserId(user.getId());
        registeredUserEvent.setEmail(user.getEmail());

        try {
            rabbitMqPublisher.publishUserRegisteredEvent(registeredUserEvent);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return authResponseDto;
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponseDto login(LoginRequestDto requestDto) {
        User user = userRepository.findByEmail(requestDto.getEmail()).orElseThrow(UserNotFoundException::new);
        if(!(passwordEncoder.matches(requestDto.getPassword(), user.getPassword()))){
            EmailRequestDto request = new EmailRequestDto();
            request.setTo(requestDto.getEmail());
            request.setSubject(Utils.getFailedEmailSubject());
            request.setBody(Utils.getIncorrectPasswordMessage());
            emailService.sendEmail(request).subscribe(unused -> log.info("Unsuccessful Login Attempt: Email queued successfully to {}",  request.getTo()));
            throw new InvalidPasswordException();
        }
        String token = jwtService.generateToken(new UserPrincipal(user));
        AuthResponseDto authResponseDto = new AuthResponseDto();
        authResponseDto.setToken(token);
        return authResponseDto;
    }

    @Override
    @Transactional
    public AuthResponseDto registerAsAdmin(RegisterRequestDto requestDto) {
        if(userRepository.existsByEmail(requestDto.getEmail())){
            User user =  userRepository.findByEmail(requestDto.getEmail()).get();
            Set<Role> roles = user.getRoles();
            if(roles.contains(Role.ADMIN)) throw new EmailAlreadyInUseException();
            saveAndSendEmail(requestDto, user);
            userRepository.save(user);
            AuthResponseDto authResponseDto = new AuthResponseDto();
            authResponseDto.setToken(jwtService.generateToken(new UserPrincipal(user)));
            return authResponseDto;

        }
        User admin =  mapper.toEntity(requestDto);
        saveAndSendEmail(requestDto, admin);
        admin.setFirstName(requestDto.getFirstName());
        admin.setLastName(requestDto.getLastName());
        userRepository.save(admin);

        AuthResponseDto authResponseDto = new AuthResponseDto();
        authResponseDto.setToken(jwtService.generateToken(new UserPrincipal(admin)));

        return authResponseDto;
    }

    @Override
    @Transactional
    public void applyToBecomeSeller(KycRequestDto requestDto) {
        Kyc kyc = mapper.toEntity(requestDto);
        User user = userRepository.findById(kyc.getUserId()).orElseThrow(UserNotFoundException::new);
        user.setKycStatus(KycStatus.PENDING);
        kycRepository.save(kyc);

    }

    @Override
    public void approveSeller(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        user.setKycStatus(KycStatus.APPROVED);
        user.setRoles(Set.of(Role.BUYER,  Role.SELLER));
        userRepository.save(user);
    }

    private void saveAndSendEmail(RegisterRequestDto requestDto, User user) {
        Set<Role> roles = new HashSet<>();
        roles.add(Role.BUYER);
        roles.add(Role.ADMIN);
        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));



        EmailRequestDto request = new EmailRequestDto();
        request.setTo(requestDto.getEmail());
        request.setSubject(Utils.getEmailSubject());
        request.setBody(Utils.getAdminWelcomeMessage(requestDto.getFirstName()));
        emailService.sendEmail(request).subscribe(unused -> log.info("Registration Email queued successfully to {}",  request.getTo()));
    }
}
