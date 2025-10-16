package semicolon.carauctionsystem.auctions.dtos.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class BidResponseDto {

    private BigDecimal bidAmount;
    private UUID auctionId;
    private UUID buyerId;
    private LocalDateTime timeStamp = LocalDateTime.now();
}
