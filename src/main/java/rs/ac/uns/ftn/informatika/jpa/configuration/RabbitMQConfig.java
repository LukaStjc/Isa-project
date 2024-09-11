package rs.ac.uns.ftn.informatika.jpa.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue contractQueue() {
        return new Queue("contract-creation", false);
    }

    @Bean
    public Queue notificationQueue() {
        return new Queue("contract-notifications", false);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange("contract-exchange");
    }

    @Bean
    public Binding contractBinding(Queue contractQueue, DirectExchange exchange) {
        return BindingBuilder.bind(contractQueue).to(exchange).with("contract-routing-key");
    }

    @Bean
    public Binding notificationBinding(Queue notificationQueue, DirectExchange exchange) {
        return BindingBuilder.bind(notificationQueue).to(exchange).with("notification-routing-key");
    }
}
