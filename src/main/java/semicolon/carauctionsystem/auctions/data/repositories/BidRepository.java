package semicolon.carauctionsystem.auctions.data.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import semicolon.carauctionsystem.auctions.data.models.Auction;
import semicolon.carauctionsystem.auctions.data.models.Bid;

import java.util.UUID;

public interface BidRepository extends ReactiveCrudRepository<Bid, UUID> {

    Flux<Bid> findBidsByAuctionId(UUID uuid);
}
