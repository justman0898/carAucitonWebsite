package semicolon.carauctionsystem.auctions.dtos.request;

import lombok.Data;
import semicolon.carauctionsystem.auctions.data.models.AuctionStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ApprovalRequestDto {
    private UUID auctionId;
    private AuctionStatus auctionStatus;
    private LocalDateTime auctionStartDate;
    private LocalDateTime auctionEndDate;
    private Duration auctionDuration;

}
