package semicolon.carauctionsystem.users.data.models;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserRegisteredEvent {

    private UUID userId;
    private String email;
    private final LocalDateTime createdAt =  LocalDateTime.now();
}
