package semicolon.carauctionsystem.auctions.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import semicolon.carauctionsystem.auctions.data.models.AuctionStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ApprovalRequestDto {
    @NotBlank
    private UUID auctionId;
    @NotBlank
    private AuctionStatus auctionStatus;

    private LocalDateTime auctionStartDate;
    private LocalDateTime auctionEndDate;

    private Duration auctionDuration;

}
