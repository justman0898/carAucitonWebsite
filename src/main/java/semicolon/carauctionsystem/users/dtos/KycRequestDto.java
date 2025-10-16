package semicolon.carauctionsystem.users.dtos;

import lombok.Data;
import semicolon.carauctionsystem.users.data.models.DocumentType;

import java.util.UUID;

@Data
public class KycRequestDto {

    private DocumentType documentType;
    private String documentId;
    private String documentUrl;
    private UUID userId;

}
