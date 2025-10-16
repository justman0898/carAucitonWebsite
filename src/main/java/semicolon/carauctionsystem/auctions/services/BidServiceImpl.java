package semicolon.carauctionsystem.auctions.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;
import semicolon.carauctionsystem.auctions.data.models.Auction;
import semicolon.carauctionsystem.auctions.data.models.AuctionStatus;
import semicolon.carauctionsystem.auctions.data.models.Bid;
import semicolon.carauctionsystem.auctions.data.models.BidEvent;
import semicolon.carauctionsystem.auctions.data.repositories.AuctionRepository;
import semicolon.carauctionsystem.auctions.data.repositories.BidRepository;
import semicolon.carauctionsystem.auctions.data.repositories.TempBidRepository;
import semicolon.carauctionsystem.auctions.data.repositories.Wallets;
import semicolon.carauctionsystem.auctions.dtos.request.BidRequestDto;
import semicolon.carauctionsystem.auctions.exceptions.AuctionNotFoundException;
import semicolon.carauctionsystem.auctions.exceptions.AuctionNotLiveException;
import semicolon.carauctionsystem.auctions.exceptions.InvalidBidException;
import semicolon.carauctionsystem.auctions.exceptions.RedisUpdateFailedException;
import semicolon.carauctionsystem.messaging.AuctionRabbitPublisher;
import semicolon.carauctionsystem.users.exceptions.UserNotFoundException;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BidServiceImpl implements BidService {
    @Autowired
    private AuctionRepository auctionRepository;
    @Autowired
    private Wallets wallets;
    @Autowired
    private AuctionRabbitPublisher auctionRabbitPublisher;
    @Autowired
    private ReactiveStringRedisTemplate reactiveStringRedisTemplate;
    @Autowired
    private BidRepository bidRepository;
    @Autowired
    private TempBidRepository tempBidRepository;



    private static final String AUCTION_BID_KEY_PREFIX = "auction:highestBid:";
    private static final String AUCTION_STATUS_KEY_PREFIX = "auction:status:";
    private static final String AUCTION_USER_KEY_PREFIX = "auction:user:";

    @Override
    public Mono<BidEvent> placeBid(BidRequestDto requestDto) {
        String redisBidKey = AUCTION_BID_KEY_PREFIX + requestDto.getAuctionId();
        String redisStatusKey = AUCTION_STATUS_KEY_PREFIX + requestDto.getAuctionId();
        String redisUserKey = AUCTION_USER_KEY_PREFIX + requestDto.getBidderId();
        BigDecimal auctionIncrement = BigDecimal.ZERO;

        Mono<String> user =  reactiveStringRedisTemplate.opsForValue().get(redisUserKey)
                .switchIfEmpty(
                        wallets.findByUserId(requestDto.getBidderId())
                                .flatMap(wallet -> reactiveStringRedisTemplate.opsForValue()
                                        .set(redisUserKey, "present")
                                        .thenReturn("present"))
                ).switchIfEmpty(Mono.error(new UserNotFoundException()));

        Mono<Tuple2<BigDecimal, AuctionStatus>> auctionMono = reactiveStringRedisTemplate.opsForValue().get(redisBidKey)
                .map(BigDecimal::new)
                .zipWhen(status-> reactiveStringRedisTemplate.opsForValue().get(redisStatusKey)
                .map(AuctionStatus::valueOf))
                .switchIfEmpty(
                        auctionRepository.findById(requestDto.getAuctionId())
                                .switchIfEmpty(Mono.error(new AuctionNotFoundException(requestDto.getAuctionId().toString())))
                                .flatMap(auction -> {
                                    BigDecimal currentHighestBid = auction.getCurrentHighestBidAmount();
                                    AuctionStatus status = auction.getStatus();


                                    return reactiveStringRedisTemplate.opsForValue().set(redisStatusKey, status.name())
                                            .onErrorResume(e -> {
                                                return Mono.error(new RedisUpdateFailedException("Failed to set auction status in Redis", e));
                                            })
                                            .then(reactiveStringRedisTemplate.opsForValue().set(redisBidKey, currentHighestBid.toString()))
                                            .onErrorResume(e -> {
                                                return Mono.error(new RedisUpdateFailedException("Failed to set highest bid in Redis", e));
                                            })
                                            .thenReturn(Tuples.of(currentHighestBid, status));
                                })
                );


        return Mono.zip(user, auctionMono)
                .flatMap(tuple -> {
                    BigDecimal currentHighest = tuple.getT2().getT1();
                    AuctionStatus status = tuple.getT2().getT2();

                    if(requestDto.getAmount().compareTo(currentHighest.add(new BigDecimal(50)))<0){
                        return Mono.error(new InvalidBidException());
                    }

                    if(!(status.name().equals("LIVE"))){
                        return Mono.error(new AuctionNotLiveException());
                    }

                    return reactiveStringRedisTemplate.opsForValue().set(redisBidKey, requestDto.getAmount().toString())
                            .onErrorResume(e -> {
                                return Mono.error(new RedisUpdateFailedException("Failed to set highest bid in Redis", e));
                            })
                            .flatMap(success -> {
                                BidEvent event = new BidEvent();
                                event.setAmount(requestDto.getAmount());
                                event.setAuctionId(requestDto.getAuctionId());
                                event.setUserId(requestDto.getBidderId());

                                auctionRabbitPublisher.publishBidEvent(event);
                                return Mono.just(event);
                            });
                });
    }

    @Override
    public Flux<Auction> getAuctions() {
        return auctionRepository.findAll();
    }

    @Override
    public Flux<Bid> getBidsByAuctionId(UUID auctionId) {
        return bidRepository.findBidsByAuctionId(auctionId);
    }

    public List<Bid> getCurrentBidsByAuctionId(UUID auctionId){
        return tempBidRepository.getBidsByAuctionId(auctionId);

    }
}
