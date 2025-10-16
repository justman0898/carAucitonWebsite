package semicolon.carauctionsystem.users.dtos;

import lombok.Data;
import semicolon.carauctionsystem.users.data.models.KycStatus;

import java.util.UUID;

@Data
public class KycResponse {

    private UUID id;
    private KycStatus status;
}
