package semicolon.carauctionsystem.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import semicolon.carauctionsystem.users.data.models.EventRetry;
import semicolon.carauctionsystem.users.data.models.UserRegisteredEvent;
import semicolon.carauctionsystem.users.data.repository.EventRetryRepo;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventRetryProcessor {

    @Autowired
    private EventRetryRepo eventRetryRepo;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    RabbitMqPublisher rabbitMqPublisher;

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void retryFailedEvents() {

        List<EventRetry> events = eventRetryRepo.findByStatus("FAILED");
        events.forEach(eventRetry ->{
            if(eventRetry.getEventType().equalsIgnoreCase("UserRegisteredEvent")){
                try {
                    UserRegisteredEvent event = objectMapper.readValue(eventRetry.getPayload(), UserRegisteredEvent.class);
                    rabbitMqPublisher.doPublishUserRegistered(event);
                    eventRetry.setStatus("SUCCESS");
                    eventRetry.setLastAttemptAt(Instant.now());
                } catch (Exception e) {
                    eventRetry.setAttemptCount(eventRetry.getAttemptCount() + 1);
                    eventRetry.setLastAttemptAt(Instant.now());
                    log.warn("Retry Failed for {}",eventRetry.getId() ,e);
                }
            }
        });


    }

}
