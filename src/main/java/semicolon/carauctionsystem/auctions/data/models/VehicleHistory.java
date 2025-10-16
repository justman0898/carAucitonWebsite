package semicolon.carauctionsystem.auctions.data.models;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;
@Data
@Table(name = "vehicle_history")
public class VehicleHistory {
    @Id
    private UUID id;

    private String fileName;
    private String filePath;
    private LocalDateTime createdAt;
    private UUID carId;
}
