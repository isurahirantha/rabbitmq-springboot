package com.mqrabbit.demo.publisher;


import com.mqrabbit.demo.dto.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQJsonProducer {

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.json.key}")
    private String routingKey;

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQJsonProducer.class);

    private RabbitTemplate rabbitTemplate;

    public RabbitMQJsonProducer(RabbitTemplate rabbitTemplate){
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendJsonMessage(User user){
        rabbitTemplate.convertAndSend(exchange,routingKey,user);
        LOGGER.info(String.format("Message sent -> %s",user.toString()));
    }

}
