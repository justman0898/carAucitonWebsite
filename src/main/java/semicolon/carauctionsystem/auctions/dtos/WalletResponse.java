package semicolon.carauctionsystem.auctions.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

@Data
public class WalletResponse {

    @JsonProperty("walletId")
    private UUID id;

    private UUID userId;
}
