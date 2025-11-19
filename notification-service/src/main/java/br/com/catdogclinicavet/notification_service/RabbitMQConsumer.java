package br.com.catdogclinicavet.notification_service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQConsumer {

    @RabbitListener(queues = "${app.rabbitmq.queue.registration}")
    public void receiveRegistrationMessage(String message) {
        System.out.println("=================================================");
        System.out.println("NOVO USUÁRIO REGISTRADO - NOTIFICAÇÃO RECEBIDA");
        System.out.println("Mensagem: " + message);
        System.out.println("Simulando envio de e-mail de boas-vindas...");
        System.out.println("=================================================");
    }
}
