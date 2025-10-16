package semicolon.carauctionsystem.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import semicolon.carauctionsystem.auctions.dtos.request.BidRequestDto;

@Slf4j
@Service
public class BidQueuePublisher {
    @Value("${app.exchange.bid}")
    private String exchange;

    @Value("${app.routing.bid-created}")
    private String routingKey;

    @Autowired
    public RabbitTemplate rabbitTemplate;

    public String publishBidCreatedEvent(BidRequestDto event) {
        log.info("Publishing bid created: {}", event);
        Object response = rabbitTemplate.convertSendAndReceive(exchange, routingKey, event);
        return (response != null) ? response.toString() : "Unknown error, Contact Support";
    }
}
