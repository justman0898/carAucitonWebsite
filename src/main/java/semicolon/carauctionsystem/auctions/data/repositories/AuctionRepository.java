package semicolon.carauctionsystem.auctions.data.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import semicolon.carauctionsystem.auctions.data.models.Auction;
import semicolon.carauctionsystem.auctions.data.models.AuctionStatus;
import semicolon.carauctionsystem.auctions.dtos.response.AuctionResponseDto;

import java.util.UUID;

public interface AuctionRepository extends ReactiveCrudRepository<Auction, UUID> {
    Flux<Auction> findByStatus(AuctionStatus status);

    Flux<Auction> findBySellerId(UUID sellerId);
    Mono<Auction> findByCarId(UUID carId);
}
