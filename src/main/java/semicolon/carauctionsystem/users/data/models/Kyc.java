package semicolon.carauctionsystem.users.data.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
public class Kyc {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private DocumentType documentType;
    private String documentId;
    private String documentUrl;
    private UUID userId;
}
