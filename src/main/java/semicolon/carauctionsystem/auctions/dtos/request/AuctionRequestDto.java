package semicolon.carauctionsystem.auctions.dtos.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class AuctionRequestDto {

    @DecimalMin("100.00")
    @NotNull
    private BigDecimal startBidAmount;
    @DecimalMin("100.00")
    private BigDecimal buyNowPrice;
    @NotBlank
    private String carLocation;
    @NotNull
    private UUID carId;
    @NotNull
    private UUID sellerId;

}
