package semicolon.carauctionsystem.auctions.data.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import semicolon.carauctionsystem.auctions.data.models.Wallet;

import java.util.UUID;


public interface Wallets extends ReactiveCrudRepository<Wallet, UUID> {
    Mono<Wallet> findByUserId(UUID userId);

}
