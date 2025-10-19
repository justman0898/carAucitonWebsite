package semicolon.carauctionsystem.auctions.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import semicolon.carauctionsystem.auctions.dtos.request.BidRequestDto;
import semicolon.carauctionsystem.auctions.dtos.response.AuctionResponseDto;
import semicolon.carauctionsystem.auctions.services.AuctionService;
import semicolon.carauctionsystem.auctions.utils.Utility;
import semicolon.carauctionsystem.messaging.BidQueuePublisher;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/buyer")
public class BidController {

    @Autowired
    private BidQueuePublisher publisher;
    @Autowired
    private AuctionService auctionService;

    @PostMapping("/bid")
    public ResponseEntity<String> placeBid(@RequestBody BidRequestDto bidRequestDto) {
        String result =  publisher.publishBidCreatedEvent(bidRequestDto);
        return Utility.checkStatus(result);
    }

    @GetMapping("/auction/{auctionId}")
    public ResponseEntity<?> viewAuction(@PathVariable String auctionId) {
        AuctionResponseDto response = auctionService.viewAuction(UUID.fromString(auctionId)).block();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    @GetMapping("/live-auctions")
    public ResponseEntity<?> viewLiveAuctions() {
        List<AuctionResponseDto> auctions = auctionService.getLiveAuctions().collectList().block();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(auctions);
    }

    @GetMapping("/approved-auctions")
    public ResponseEntity<?> viewAllApprovedAuctions(){
        List<AuctionResponseDto> auctions = auctionService.getAllApprovedAuctions().collectList().block();
        return  ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(auctions);

    }




}