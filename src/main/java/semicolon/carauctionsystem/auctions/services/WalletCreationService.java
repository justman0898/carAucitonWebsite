package semicolon.carauctionsystem.auctions.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import semicolon.carauctionsystem.auctions.data.models.Wallet;
import semicolon.carauctionsystem.auctions.data.repositories.Wallets;
import semicolon.carauctionsystem.auctions.dtos.WalletResponse;
import semicolon.carauctionsystem.users.data.models.UserRegisteredEvent;
import semicolon.carauctionsystem.users.utils.Mapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletCreationService {

    @Autowired
    private R2dbcEntityTemplate r2dbcEntityTemplate;

    @Autowired
    private WebClient walletWebClient;

    @Autowired
    private Wallets  wallets;

    @Autowired
    private Mapper mapper;

    public Mono<WalletResponse> createWallet(UserRegisteredEvent userRegisteredEvent){
        return walletWebClient.post()
                .uri("/api/v1/wallets/create")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userRegisteredEvent)
                .retrieve()
                .bodyToMono(WalletResponse.class)

                .flatMap(response -> {
                    Wallet wallet = new Wallet();
                    wallet.setId(response.getId());
                    wallet.setUserId(response.getUserId());

                    return r2dbcEntityTemplate.insert(Wallet.class).using(wallet);

                }).map(mapper::toDto);

    }

}
