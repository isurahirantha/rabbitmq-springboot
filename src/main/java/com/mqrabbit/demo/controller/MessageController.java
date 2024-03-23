package com.mqrabbit.demo.controller;

import com.mqrabbit.demo.publisher.RabbitMQProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MessageController {

    private RabbitMQProducer rabbitMQProducer;

    MessageController(RabbitMQProducer rabbitMQProducer){
        this.rabbitMQProducer = rabbitMQProducer;
    }

    @GetMapping("/publish")
    ResponseEntity<String> sendMessage(@RequestParam("message") String message){
        rabbitMQProducer.sendMessage(message);
        return ResponseEntity.ok("Message Sent..!");
    }

}
