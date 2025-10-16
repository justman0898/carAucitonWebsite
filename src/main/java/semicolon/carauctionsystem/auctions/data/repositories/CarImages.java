package semicolon.carauctionsystem.auctions.data.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import semicolon.carauctionsystem.auctions.data.models.CarImage;

import java.util.UUID;

public interface CarImages extends ReactiveCrudRepository<CarImage, UUID> {
    Flux<CarImage> findByCarId(UUID carId);
}
