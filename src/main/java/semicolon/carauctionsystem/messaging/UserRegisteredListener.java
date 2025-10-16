package semicolon.carauctionsystem.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import semicolon.carauctionsystem.auctions.services.WalletCreationService;
import semicolon.carauctionsystem.users.data.models.UserRegisteredEvent;

@Slf4j
@Service
public class UserRegisteredListener {

    @Autowired
    private WalletCreationService walletCreationService;

    @RabbitListener(queues = "user.registered.queue", concurrency = "5-15")
    public void handleWalletCreation(UserRegisteredEvent userRegisteredEvent) {
        walletCreationService.createWallet(userRegisteredEvent)
                .doOnSuccess(result-> log.info("Wallet created successfully"))
                .doOnError(throwable -> log.error("Error creating wallet", throwable))
                .subscribe();
    }
}
