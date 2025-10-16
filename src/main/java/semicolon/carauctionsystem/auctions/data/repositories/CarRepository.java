package semicolon.carauctionsystem.auctions.data.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import semicolon.carauctionsystem.auctions.data.models.Car;

import java.util.UUID;

public interface CarRepository extends ReactiveCrudRepository<Car, UUID> {
    Flux<Car> findBySellerId(UUID sellerId);
}
