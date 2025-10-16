package semicolon.carauctionsystem.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitBidConfig {

    @Value("${app.exchange.bid}")
    private String exchange;

    @Value("${app.routing.bid-created}")
    private String routingKey;

    @Bean("bidExchange")
    public TopicExchange topicExchange(){
        return new TopicExchange(exchange);
    }

    @Bean("bidQueue")
    public Queue queue(){
        return new Queue("bid-queue", true);
    }

    @Bean("bidBinding")
    public Binding binding(@Qualifier("bidQueue") Queue auctionQueue, @Qualifier("bidExchange") TopicExchange auctionExchange){
        return BindingBuilder.bind(auctionQueue).to(auctionExchange).with(routingKey);
    }
}
