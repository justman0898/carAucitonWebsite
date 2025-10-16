package semicolon.carauctionsystem.clients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientsBeanConfig {

    @Value("${EMAIL.SERVICE.BASEURL}")
    private String emailBaseUrl;

    @Value("${WALLET.SERVICE.BASEURL}")
    private String walletBaseUrl;

    @Bean("emailWebClient")
    public WebClient getEmailWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder.baseUrl(emailBaseUrl).build();
    }

    @Bean("walletWebClient")
    public WebClient getWalletWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder.baseUrl(walletBaseUrl).build();
    }
}
