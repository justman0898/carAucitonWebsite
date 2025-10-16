package semicolon.carauctionsystem.auctions.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import semicolon.carauctionsystem.auctions.dtos.request.ApprovalRequestDto;
import semicolon.carauctionsystem.auctions.dtos.request.AuctionRequestDto;
import semicolon.carauctionsystem.auctions.dtos.request.CarRequestDto;
import semicolon.carauctionsystem.auctions.dtos.response.AuctionResponseDto;
import semicolon.carauctionsystem.auctions.dtos.response.CarResponseDto;

import java.util.UUID;

public interface AuctionService {

    Mono<AuctionResponseDto> createAuction (AuctionRequestDto requestDto);
    Mono<Void> approveAuction (ApprovalRequestDto requestDto);
    Flux<AuctionResponseDto> getLiveAuctions();
    Mono<AuctionResponseDto> viewAuction (UUID auctionId);
    Mono<CarResponseDto> listVehicle(CarRequestDto requestDto);
    Flux<AuctionResponseDto> viewAuctionListing(UUID sellerId);
    Flux<AuctionResponseDto> getAllApprovedAuctions();
}
