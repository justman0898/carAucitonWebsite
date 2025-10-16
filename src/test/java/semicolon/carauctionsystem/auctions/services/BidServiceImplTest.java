package semicolon.carauctionsystem.auctions.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import semicolon.carauctionsystem.auctions.data.models.Auction;
import semicolon.carauctionsystem.auctions.data.models.AuctionStatus;
import semicolon.carauctionsystem.auctions.data.models.BidEvent;
import semicolon.carauctionsystem.auctions.data.models.Wallet;
import semicolon.carauctionsystem.auctions.data.repositories.AuctionRepository;
import semicolon.carauctionsystem.auctions.data.repositories.BidRepository;
import semicolon.carauctionsystem.auctions.data.repositories.Wallets;
import semicolon.carauctionsystem.auctions.dtos.request.BidRequestDto;
import semicolon.carauctionsystem.auctions.exceptions.AuctionNotFoundException;
import semicolon.carauctionsystem.auctions.exceptions.AuctionNotLiveException;
import semicolon.carauctionsystem.auctions.exceptions.InvalidBidException;
import semicolon.carauctionsystem.messaging.AuctionRabbitPublisher;
import semicolon.carauctionsystem.users.exceptions.UserNotFoundException;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)

@ExtendWith(MockitoExtension.class)
class BidServiceImplTest {

    @MockitoBean
    private AuctionRepository  auctionRepository;

    @MockitoBean
    private BidRepository bidRepository;

    @MockitoBean
    private Wallets wallets;

    @MockitoBean
    private ReactiveStringRedisTemplate reactiveStringRedisTemplate;

    @MockitoBean
    private AuctionRabbitPublisher auctionRabbitPublisher;

    @MockitoBean
    private ReactiveValueOperations<String, String> reactiveValueOperations;

    @Autowired
    BidService bidService;

    private final UUID auctionId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();
    private final BigDecimal bidAmount = BigDecimal.valueOf(100);
    private final String redisBidKey = "auction:highestBid:" + auctionId;
    private final String userKey = "auction:user:" + userId;
    private final String statusKey = "auction:status:" + auctionId;

    @BeforeEach
    void setUp() {
        when(reactiveStringRedisTemplate.opsForValue()).thenReturn(reactiveValueOperations);
    }

    @Test
    void testThatPlaceBidSuccessful_redisHit_shouldReturnEvent(){
        when(reactiveValueOperations.get(userKey)).thenReturn(Mono.just("present"));
        when(reactiveValueOperations.get(statusKey)).thenReturn(Mono.just("LIVE"));
        when(reactiveValueOperations.get(redisBidKey)).thenReturn(Mono.just("100"));
        when(reactiveValueOperations.set(eq(redisBidKey), any())).thenReturn(Mono.just(true));

        when(wallets.findByUserId(userId)).thenReturn(Mono.empty());
        when(auctionRepository.findById(auctionId)).thenReturn(Mono.empty());


        BidRequestDto request = new BidRequestDto();
        request.setAuctionId(auctionId);
        request.setBidderId(userId);
        request.setAmount(new BigDecimal(200));

        StepVerifier.create(bidService.placeBid(request))
                .expectNextMatches(event ->
                        event.getAuctionId().equals(request.getAuctionId()) &&
                                event.getUserId().equals(request.getBidderId())&&
                                event.getAmount().equals(request.getAmount())
                ).verifyComplete();

        verify(auctionRabbitPublisher).publishBidEvent(any(BidEvent.class));
    }

    @Test
    void testCanPlaceSuccessfulBid_redisMiss_fallbackOnDb_shouldReturnEvent(){
        when(reactiveValueOperations.get(userKey)).thenReturn(Mono.empty());
        when(wallets.findByUserId(userId)).thenReturn(Mono.just(new Wallet()));
        when(reactiveValueOperations.set(userKey, "present")).thenReturn(Mono.just(true));
        when(reactiveValueOperations.get(statusKey)).thenReturn(Mono.empty());
        when(reactiveValueOperations.get(redisBidKey)).thenReturn(Mono.empty());


        Auction auction = new Auction();
        auction.setCurrentHighestBidAmount(new BigDecimal(0));
        auction.setStatus(AuctionStatus.LIVE);

        when(auctionRepository.findById(auctionId)).thenReturn(Mono.just(auction));
        when(reactiveValueOperations.set(eq(redisBidKey), any())).thenReturn(Mono.just(true));
        when(reactiveValueOperations.set(eq(statusKey), any())).thenReturn(Mono.just(true));

        BidRequestDto request = new BidRequestDto();
        request.setAuctionId(auctionId);
        request.setBidderId(userId);
        request.setAmount(bidAmount);

        StepVerifier.create(bidService.placeBid(request))
                .expectNextMatches(event ->
                    event.getAuctionId().equals(request.getAuctionId()) &&
                            event.getUserId().equals(request.getBidderId())&&
                            event.getAmount().equals(request.getAmount())
                ).verifyComplete();

    }

    @Test
    void testThatThrowsExceptionUserNotFound_redisMiss_dbMiss(){
        when(reactiveValueOperations.get(statusKey)).thenReturn(Mono.just("LIVE"));
        when(reactiveValueOperations.get(redisBidKey)).thenReturn(Mono.just("100"));
        when(reactiveValueOperations.set(eq(redisBidKey), any())).thenReturn(Mono.just(true));

        when(reactiveValueOperations.get(userKey)).thenReturn(Mono.empty());
        when(wallets.findByUserId(userId)).thenReturn(Mono.empty());

        Auction auction = new Auction();
        auction.setCurrentHighestBidAmount(new BigDecimal(0));
        auction.setStatus(AuctionStatus.LIVE);

        when(auctionRepository.findById(auctionId)).thenReturn(Mono.just(auction));

        BidRequestDto request = new BidRequestDto();
        request.setAuctionId(auctionId);
        request.setBidderId(userId);
        request.setAmount(bidAmount);

        StepVerifier.create(bidService.placeBid(request))
                .expectErrorMatches(e -> e instanceof UserNotFoundException)
                .verify();
    }

    @Test
    void testThatThrowsExceptionWhenBidAmountTooLow(){
        when(reactiveValueOperations.get(statusKey)).thenReturn(Mono.just("LIVE"));
        when(reactiveValueOperations.get(redisBidKey)).thenReturn(Mono.just("100"));
        when(reactiveValueOperations.set(eq(redisBidKey), any())).thenReturn(Mono.just(true));
        when(reactiveValueOperations.get(userKey)).thenReturn(Mono.just("present"));
        when(wallets.findByUserId(userId)).thenReturn(Mono.empty());

        when(auctionRepository.findById(auctionId)).thenReturn(Mono.empty());
        BidRequestDto request = new BidRequestDto();
        request.setAuctionId(auctionId);
        request.setBidderId(userId);
        request.setAmount(new BigDecimal(149));

        StepVerifier.create(bidService.placeBid(request))
                .expectErrorMatches(e -> e instanceof InvalidBidException)
                .verify();
    }

    @Test
    void testThatExceptionIsThrownWhenStatusIsNotLive(){
        when(reactiveValueOperations.get(userKey)).thenReturn(Mono.just("present"));
        when(reactiveValueOperations.get(statusKey)).thenReturn(Mono.empty());
        when(reactiveValueOperations.get(redisBidKey)).thenReturn(Mono.empty());
        when(wallets.findByUserId(userId)).thenReturn(Mono.empty());


        when(reactiveValueOperations.set(eq(redisBidKey), any())).thenReturn(Mono.just(true));
        when(reactiveValueOperations.set(eq(statusKey), any())).thenReturn(Mono.just(true));


        Auction auction = new Auction();
        auction.setCurrentHighestBidAmount(new BigDecimal(0));
        auction.setStatus(AuctionStatus.NOT_STARTED);

        when(auctionRepository.findById(auctionId)).thenReturn(Mono.just(auction));

        BidRequestDto request = new BidRequestDto();
        request.setAuctionId(auctionId);
        request.setBidderId(userId);
        request.setAmount(new BigDecimal(149));

        StepVerifier.create(bidService.placeBid(request))
                .expectErrorMatches(e -> e instanceof AuctionNotLiveException)
                .verify();
    }

    @Test
    void testThatFails_auctionRedisMiss_dbMiss(){
        when(reactiveValueOperations.get(userKey)).thenReturn(Mono.empty());
        when(wallets.findByUserId(userId)).thenReturn(Mono.just(new Wallet()));
        when(reactiveValueOperations.set(userKey, "present")).thenReturn(Mono.just(true));
        when(reactiveValueOperations.get(statusKey)).thenReturn(Mono.empty());
        when(reactiveValueOperations.get(redisBidKey)).thenReturn(Mono.empty());

        when(auctionRepository.findById(auctionId)).thenReturn(Mono.empty());

        BidRequestDto request = new BidRequestDto();
        request.setAuctionId(auctionId);
        request.setBidderId(userId);
        request.setAmount(new BigDecimal(149));

        StepVerifier.create(bidService.placeBid(request))
                .expectErrorMatches(e -> e instanceof AuctionNotFoundException)
                .verify();




    }






}