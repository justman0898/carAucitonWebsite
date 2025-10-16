package semicolon.carauctionsystem.auctions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import semicolon.carauctionsystem.auctions.dtos.request.AuctionRequestDto;
import semicolon.carauctionsystem.auctions.dtos.request.CarRequestDto;
import semicolon.carauctionsystem.auctions.dtos.response.AuctionResponseDto;
import semicolon.carauctionsystem.auctions.dtos.response.CarResponseDto;
import semicolon.carauctionsystem.auctions.services.AuctionService;

@RestController
@RequestMapping("/api/v1/seller")
public class AuctionRouter {

    @Autowired
    private AuctionService auctionService;

    @PostMapping("/list-vehicle")
    public ResponseEntity<?> listVehicle(@RequestBody CarRequestDto carRequestDto) {
        CarResponseDto responseDto = auctionService.listVehicle(carRequestDto).block();
        return ResponseEntity.ok().body(responseDto);
    }

    @PostMapping("/create-auction")
    public ResponseEntity<AuctionResponseDto> requestAuction(@RequestBody AuctionRequestDto auctionRequestDto) {
        AuctionResponseDto responseDto = auctionService.createAuction(auctionRequestDto).block();
        return ResponseEntity.ok().body(responseDto);
    }
}
