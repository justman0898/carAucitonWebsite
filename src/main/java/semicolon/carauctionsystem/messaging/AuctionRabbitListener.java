package semicolon.carauctionsystem.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import semicolon.carauctionsystem.auctions.data.models.Bid;
import semicolon.carauctionsystem.auctions.data.models.BidEvent;
import semicolon.carauctionsystem.auctions.data.repositories.AuctionRepository;
import semicolon.carauctionsystem.auctions.data.repositories.BidRepository;
import semicolon.carauctionsystem.auctions.data.repositories.TempBidRepository;
import semicolon.carauctionsystem.auctions.dtos.response.BidResponseDto;
import semicolon.carauctionsystem.users.utils.Mapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuctionRabbitListener {
    @Autowired
    private BidRepository  bidRepository;
    @Autowired
    private AuctionRepository auctionRepository;
    @Autowired
    private TempBidRepository  tempBidRepository;
    @Autowired
    private AuctionWebSocketHandler auctionWebSocketHandler;
    @Autowired
    private Mapper mapper;

    @RabbitListener(queues = "auction-queue", concurrency = "5-10")
    public void handleBid(BidEvent bidEvent) {
        Bid bid = new Bid();
        bid.setAuctionId(bidEvent.getAuctionId());
        bid.setBuyerId(bidEvent.getUserId());
        bid.setBidAmount(bidEvent.getAmount());

        tempBidRepository.save(bid);

        BidResponseDto bidResponseDto = mapper.toDto(bid);

        bidRepository.save(bid)
                .subscribe(savedBid-> {
                    log.info("Bid saved successfully {}" ,savedBid.toString());
                    auctionWebSocketHandler.broadcastBids(savedBid.getAuctionId(),bidResponseDto);
                    log.info("Broadcast bid successfully {}" ,savedBid.toString());
                });

        auctionRepository.findById(bidEvent.getAuctionId())
                .flatMap(auction -> {
                    auction.setCurrentHighestBidAmount(bidEvent.getAmount());
                    auction.setWinnerId(bidEvent.getUserId());
                    return auctionRepository.save(auction);
                }).subscribe(auction -> log.info("Auction saved successfully {}" ,auction.toString()));

    }
}
