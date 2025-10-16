package semicolon.carauctionsystem.users.data.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@Table(name = "event_retry")
public class EventRetry {
    @Id
    @GeneratedValue
    private UUID id;

    private String eventType;
    @Lob
    private String payload;
    private int attemptCount;
    private String status;
    private Instant lastAttemptAt;
    private Instant createdAt =  Instant.now();
}
