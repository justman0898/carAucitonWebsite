package semicolon.carauctionsystem.auctions.data.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table
@Data
public class Wallet {
    @Id
    private UUID id;

    private UUID userId;

    private Instant createdAt = Instant.now();
}
