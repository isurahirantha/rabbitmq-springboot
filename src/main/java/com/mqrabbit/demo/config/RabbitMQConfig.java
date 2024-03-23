package com.mqrabbit.demo.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {


    // Producer -> |EXCHANGE        + ROUTING KEY                | -> Queue              -> Consumer
    //~~~~~~~~~~~~~|~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~|~~~~~~~~~~~~~~~~~~~~~~~~|~~~~~~~~~
    // Producer -> |exchange_of_dev  + routing_key_of_dev        | -> queue_of_dev       -> Consumer
    // Producer -> |exchange_of_dev  + routing_key_of_dev_json   | -> queue_of_dev_json  -> Consumer
    // Producer -> |exchange_for_dle + routing_key_for_dle       | -> queue_of_dle       -> Consumer

    // Injecting queue name from properties
    @Value("${rabbitmq.queue.name}")
    private String devQueue;

    @Value("${rabbitmq.queue.json.name}")
    private String devQueueJson;

    // Injecting exchange name from properties
    @Value("${rabbitmq.exchange.name}")
    private String commonExchange;

    // Injecting routing key from properties
    @Value("${rabbitmq.routing.key}")
    private String routingKeyDevQueue;

    @Value("${rabbitmq.routing.json.key}")
    private String routingKeyDevQueueJson;

    // Error messages to be put
    @Value("${rabbitmq.exchange.dle}")
    private String dleExchange;

    @Value("${rabbitmq.routing.dle.key}")
    private String dleRoutingKey;

    @Value("${rabbitmq.queue.dle}")
    private String dleQueue;

    // Bean definition for the queue
    @Bean
    public Queue queue() {
        return QueueBuilder.durable(devQueue).build();
    }

    // Bean definition for the queue
    // And Dead-Letter Configuration, This means that if a message cannot be processed by this queue,
    // it will be sent to the dead-letter exchange
    @Bean
    public Queue jsonQueue() {
        return QueueBuilder.durable(devQueueJson)
                .withArgument(
                        "x-dead-letter-exchange",
                        dleExchange
                )
                .withArgument(
                        "x-dead-letter-routing-key",
                        dleRoutingKey
                ).build();
    }

    @Bean
    Queue deadLetterQueue() {
        return QueueBuilder.durable(dleQueue)
                .build();
    }

    // Bean definition for the topic exchange
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(commonExchange);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(dleExchange);
    }

    // Bean definition for binding the queue to the exchange with the routing key
    @Bean
    public Binding bind() {
        return BindingBuilder.bind(queue()).to(exchange()).with(routingKeyDevQueue);
    }

    @Bean
    public Binding jsonBind() {
        return BindingBuilder.bind(jsonQueue()).to(exchange()).with(routingKeyDevQueueJson);
    }

    @Bean
    Binding bindingDLQUsers() {
        return BindingBuilder
                .bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with(dleRoutingKey);
    }

    @Bean
    public MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }

    @Bean("rabbitListenerContainerFactory")
    public RabbitListenerContainerFactory<?> rabbitFactory
            (ConnectionFactory connectionFactory) {
        var factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        factory.setDefaultRequeueRejected(false);
        return factory;
    }

    /*
       * RabbitTemplate
       * RabbitAdmin
       * ConnectionFactory
        -- These configurations are automatically configured via spring auto configuration. --
        *
        *
        *
        * In summary, the amqpTemplate() method configures an AmqpTemplate for sending messages to RabbitMQ,
        * while the rabbitFactory() method configures a RabbitListenerContainerFactory for receiving messages
        * from RabbitMQ. They serve different purposes in the application's interaction with RabbitMQ: one for
        *  sending messages and the other for receiving messages.
     */
}

