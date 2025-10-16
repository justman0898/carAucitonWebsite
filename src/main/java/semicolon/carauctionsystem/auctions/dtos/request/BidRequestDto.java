package semicolon.carauctionsystem.auctions.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class BidRequestDto {
    @NotBlank
    private UUID auctionId;
    @NotBlank
    private UUID bidderId;
    @NotNull
    private BigDecimal amount;

}
