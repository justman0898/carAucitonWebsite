package semicolon.carauctionsystem.auctions.dtos.response;

import lombok.Data;
import semicolon.carauctionsystem.auctions.data.models.AuctionStatus;
import semicolon.carauctionsystem.auctions.data.models.Bid;
import semicolon.carauctionsystem.auctions.data.models.ConditionReport;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class AuctionResponseDto {

    private UUID id;
    private UUID carId;
    private UUID sellerId;
    private AuctionStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Duration bidDuration;
    private BigDecimal buyNowPrice;
    private BigDecimal currentHighestBidAmount;
    private List<BidResponseDto>  bids;
    private String carLocation;

    private String carMake;
    private String carTrim;
    private LocalDate year;
    private ConditionReport conditionReport;
    private List<String> imageUrls;

}
