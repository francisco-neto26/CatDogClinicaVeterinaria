package br.com.catdogclinicavet.notification_service.config;

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
        // Cria a fila como "durable" (true), para não perder mensagens se o Rabbit cair
        return new Queue(registrationQueue, true);
    }
}