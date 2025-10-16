package semicolon.carauctionsystem.clients;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import semicolon.carauctionsystem.users.dtos.EmailRequestDto;

@Slf4j
@Service
public class EmailServiceClient {

    @Autowired
    private WebClient emailWebClient;

    public Mono<Void> sendEmail(EmailRequestDto requestDto){
        return emailWebClient.post()
                .uri("/api/emails/enqueue")
                .bodyValue(requestDto)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnError(error -> log.error("Failed to send email", error));
    }
}
