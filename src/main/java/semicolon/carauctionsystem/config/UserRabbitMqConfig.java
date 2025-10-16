package semicolon.carauctionsystem.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class UserRabbitMqConfig {

    @Value("${app.exchange.user}")
    private String exchangeName;

    @Value("${app.routing.user-registered}")
    private String routingName;

    @Bean("userExchange")
    public TopicExchange topicExchange() {
        return new TopicExchange(exchangeName);
    }

    @Bean(name = "userQueue")
    public Queue queue() {
        return new Queue("user.registered.queue");
    }

    @Bean(name = "userBinding")
    public Binding binding(@Qualifier("userQueue")Queue queue, @Qualifier("userExchange")TopicExchange topicExchange) {
        return BindingBuilder
                .bind(queue)
                .to(topicExchange)
                .with(routingName);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }



}
