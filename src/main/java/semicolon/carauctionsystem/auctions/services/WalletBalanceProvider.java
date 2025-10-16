package semicolon.carauctionsystem.auctions.services;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
public class WalletBalanceProvider {


    private final WebClient walletWebClient;

    private final AsyncLoadingCache<UUID, BigDecimal> balanceCache;

    @Autowired
    public  WalletBalanceProvider(WebClient walletWebClient) {
        this.walletWebClient = walletWebClient;
        this.balanceCache =  Caffeine.newBuilder()
                .expireAfterAccess(Duration.ofSeconds(30))
                .maximumSize(10_000)
                .buildAsync(this::fetchBalance);

    }

    private CompletableFuture<? extends BigDecimal> fetchBalance(UUID walletId, Executor executor) {
        return walletWebClient.get()
                .uri("/api/v1/wallets/balance/{walletId}", walletId)
                .retrieve()
                .bodyToMono(BigDecimal.class)
                .toFuture();
    }

    public Mono<BigDecimal> getBalance(UUID walletId) {
        return Mono.fromFuture(balanceCache.get(walletId));
    }

    public Mono<BigDecimal> refreshBalance(UUID walletId) {
        return Mono.fromFuture(balanceCache.synchronous().refresh(walletId));

    }


}
