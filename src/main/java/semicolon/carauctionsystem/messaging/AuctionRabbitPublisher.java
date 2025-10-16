package semicolon.carauctionsystem.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import semicolon.carauctionsystem.auctions.data.models.BidEvent;

@Service
@Slf4j
public class AuctionRabbitPublisher {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${app.exchange.auction}")
    private String exchange;

    @Value("${app.routing.auction-created}")
    private String routingKey;

    @Async
    public void publishBidEvent(BidEvent event) {
        log.info("Publishing bid event: {}", event);
        rabbitTemplate.convertAndSend(exchange, routingKey, event);
        log.info("Published bid event: {}", event);
    }
}
