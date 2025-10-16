package semicolon.carauctionsystem.auctions.data.models;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
public class BidEvent {
    private UUID auctionId;
    private UUID userId;
    private BigDecimal amount;
    private Instant timestamp = Instant.now();
}
