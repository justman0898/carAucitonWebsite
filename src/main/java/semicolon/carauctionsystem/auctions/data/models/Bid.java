package semicolon.carauctionsystem.auctions.data.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Table("bids")
public class Bid {
    @Id

    private Long id;

    private BigDecimal bidAmount;

    private UUID auctionId;

    private UUID buyerId;

    private LocalDateTime timeStamp = LocalDateTime.now();


}
