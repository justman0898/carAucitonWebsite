package semicolon.carauctionsystem.auctions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import semicolon.carauctionsystem.auctions.dtos.request.ApprovalRequestDto;
import semicolon.carauctionsystem.auctions.services.AuctionService;
import semicolon.carauctionsystem.users.dtos.AuthResponseDto;
import semicolon.carauctionsystem.users.dtos.RegisterRequestDto;
import semicolon.carauctionsystem.users.services.AuthService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    @Autowired
    private AuctionService auctionService;

    @Autowired
    private AuthService authService;

    @PutMapping("/approve-auction")
    public ResponseEntity<?> approveAuction(@RequestBody ApprovalRequestDto approvalRequestDto) {
        auctionService.approveAuction(approvalRequestDto).block();
        return  ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@RequestBody RegisterRequestDto registerRequestDto) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(authService.registerAsAdmin(registerRequestDto));
    }

    @PutMapping("/approve-seller/{sellerId}")
    public ResponseEntity<?> approveSeller(@PathVariable UUID sellerId) {
        authService.approveSeller(sellerId);
        return ResponseEntity.accepted().build();
    }


}
