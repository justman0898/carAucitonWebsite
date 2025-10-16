package semicolon.carauctionsystem.users.controllers;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import semicolon.carauctionsystem.users.dtos.AuthResponseDto;
import semicolon.carauctionsystem.users.dtos.KycRequestDto;
import semicolon.carauctionsystem.users.dtos.LoginRequestDto;
import semicolon.carauctionsystem.users.dtos.RegisterRequestDto;
import semicolon.carauctionsystem.users.services.AuthService;

@RestController
@RequestMapping("/api/v1/auth")

public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegisterRequestDto requestDto) {
         AuthResponseDto responseDto = authService.register(requestDto);
         return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PostMapping("/login")
    ResponseEntity<AuthResponseDto> login(@RequestBody LoginRequestDto requestDto) {
        return ResponseEntity.ok(authService.login(requestDto));
    }

    @PostMapping("/admin-register")
    public  ResponseEntity<AuthResponseDto> registerAsAdmin(@RequestBody RegisterRequestDto requestDto) {
        AuthResponseDto responseDto = authService.registerAsAdmin(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PutMapping("/register-seller")
    public ResponseEntity<?> applyToSell(@RequestBody KycRequestDto requestDto) {
        authService.applyToBecomeSeller(requestDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }






}
