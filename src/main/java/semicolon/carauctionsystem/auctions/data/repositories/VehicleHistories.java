package semicolon.carauctionsystem.auctions.data.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import semicolon.carauctionsystem.auctions.data.models.VehicleHistory;

import java.util.UUID;

public interface VehicleHistories extends ReactiveCrudRepository<VehicleHistory, UUID> {

    Flux<VehicleHistory> findByCarIdOrderByCreatedAt(UUID carId);
}
