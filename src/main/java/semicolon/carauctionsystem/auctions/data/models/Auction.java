package semicolon.carauctionsystem.auctions.data.models;

import lombok.Data;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.annotation.Id;


import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Table("auctions")
public class Auction {
    @Id
    private UUID id;

    private UUID carId;
    private UUID sellerId;
    private UUID winnerId;
    private BigDecimal currentHighestBidAmount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Duration bidDuration;
    private BigDecimal bidIncrement = new BigDecimal(100);
    private BigDecimal startBidAmount;
    private BigDecimal buyNowPrice;
    private String carLocation;

    private AuctionStatus status ;

    @Transient
    private List<Bid> bids;

    @Transient
    private List<CarImage> carImages;


}
