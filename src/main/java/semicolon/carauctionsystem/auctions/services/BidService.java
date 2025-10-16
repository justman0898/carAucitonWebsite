package semicolon.carauctionsystem.auctions.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import semicolon.carauctionsystem.auctions.data.models.Auction;
import semicolon.carauctionsystem.auctions.data.models.Bid;
import semicolon.carauctionsystem.auctions.data.models.BidEvent;
import semicolon.carauctionsystem.auctions.dtos.request.BidRequestDto;

import java.util.UUID;

public interface BidService {

    Mono<BidEvent> placeBid(BidRequestDto requestDto);
    Flux<Auction> getAuctions();
    Flux<Bid> getBidsByAuctionId(UUID uuid);
}
