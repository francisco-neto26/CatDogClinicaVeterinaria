package br.com.catdogclinicavet.backend_api.config;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${app.rabbitmq.queue.registration}")
    private String registrationQueue;

    @Bean
    public Queue queue() {
        return new Queue(registrationQueue, true);
    }
}