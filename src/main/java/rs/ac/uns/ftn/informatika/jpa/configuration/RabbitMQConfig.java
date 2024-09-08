package rs.ac.uns.ftn.informatika.jpa.configuration;

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
        return new Queue("delivery-notifications", false);
    }
}
