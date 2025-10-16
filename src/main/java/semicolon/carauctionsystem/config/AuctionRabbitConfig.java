package semicolon.carauctionsystem.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuctionRabbitConfig {

    @Value("${app.exchange.auction}")
    private String exchange;

    @Value("${app.routing.auction-created}")
    private String routingKey;

    @Bean("auctionExchange")
    public TopicExchange topicExchange(){
        return new TopicExchange(exchange);
    }

    @Bean("auctionQueue")
    public Queue queue(){
        return new Queue("auction-queue", true);
    }

    @Bean("auctionBinding")
    public Binding binding(@Qualifier("auctionQueue") Queue auctionQueue, @Qualifier("auctionExchange") TopicExchange auctionExchange){
        return BindingBuilder.bind(auctionQueue).to(auctionExchange).with(routingKey);
    }
}
