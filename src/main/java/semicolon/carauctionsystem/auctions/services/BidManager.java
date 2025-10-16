package semicolon.carauctionsystem.auctions.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import semicolon.carauctionsystem.auctions.dtos.request.BidRequestDto;

@Service
@Slf4j
public class BidManager {
    @Autowired
    private BidService bidService;

    @RabbitListener(queues = "bid-queue", concurrency = "1")
    public String handleBid(BidRequestDto requestDto) {
        try {
            bidService.placeBid(requestDto).block();
            log.info("Bid placed successfully");
            return "Bid placed successfully";
        }catch (Exception e) {
            return "Bid failed, " + e.getMessage();
        }

    }
}
