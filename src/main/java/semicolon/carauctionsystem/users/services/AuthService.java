package semicolon.carauctionsystem.users.services;

import semicolon.carauctionsystem.users.dtos.AuthResponseDto;
import semicolon.carauctionsystem.users.dtos.KycRequestDto;
import semicolon.carauctionsystem.users.dtos.LoginRequestDto;
import semicolon.carauctionsystem.users.dtos.RegisterRequestDto;

import java.util.UUID;

public interface AuthService {

    AuthResponseDto register(RegisterRequestDto requestDto);
    AuthResponseDto login(LoginRequestDto loginRequest);
    AuthResponseDto registerAsAdmin(RegisterRequestDto requestDto);
    void applyToBecomeSeller (KycRequestDto  requestDto);
    void approveSeller(UUID userId);
}
