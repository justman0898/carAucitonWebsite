package semicolon.carauctionsystem.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import semicolon.carauctionsystem.users.data.models.EventRetry;
import semicolon.carauctionsystem.users.data.models.UserRegisteredEvent;
import semicolon.carauctionsystem.users.data.repository.EventRetryRepo;

import java.time.Instant;

@Service
@Slf4j
public class RabbitMqPublisher {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EventRetryRepo eventRetryRepo;

    @Value("${app.exchange.user}")
    private String exchange;


    @Value("${app.routing.user-registered}")
    private String routingKey;

    @Async
    @Retryable(value = {AmqpException.class},
                maxAttempts = 5,
                backoff = @Backoff(delay = 20000, multiplier = 1.5))
    public void publishUserRegisteredEvent(UserRegisteredEvent userRegisteredEvent) {
        log.info("Publishing user registered event for user: {}", userRegisteredEvent.getEmail());
        rabbitTemplate.convertAndSend(exchange, routingKey, userRegisteredEvent);
        log.info("Published user registered event for user: {}", userRegisteredEvent.getEmail());
    }

    @Async
    public void doPublishUserRegistered(UserRegisteredEvent event) {
        log.info("Publishing event for user {}", event.getUserId());
        rabbitTemplate.convertAndSend(exchange, routingKey, event);
        log.info("Published event for user {}", event.getUserId());
    }

    @Recover
    public void recover(AmqpException e, UserRegisteredEvent userRegisteredEvent) {
        log.error("All retries failed, saving to failed retries");
        try {
            String payLoad = objectMapper.writeValueAsString(userRegisteredEvent);
            EventRetry eventRetry = new EventRetry();
            eventRetry.setEventType("UserRegisteredEvent");
            eventRetry.setStatus("FAILED");
            eventRetry.setPayload(payLoad);
            eventRetry.setLastAttemptAt(Instant.now());
            eventRetry.setAttemptCount(5);
            eventRetryRepo.save(eventRetry);
            log.warn("Saved retry event for user: {}", userRegisteredEvent.getEmail());
        } catch (Exception ex) {
            log.error("Failed to save", ex);
        }

    }



}
