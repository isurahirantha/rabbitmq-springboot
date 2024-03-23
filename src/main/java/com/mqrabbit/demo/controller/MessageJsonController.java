package com.mqrabbit.demo.controller;

import com.mqrabbit.demo.dto.User;
import com.mqrabbit.demo.publisher.RabbitMQJsonProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/json")
public class MessageJsonController {
    private RabbitMQJsonProducer  rabbitMQJsonProducer;

    public MessageJsonController(RabbitMQJsonProducer rabbitMQJsonProducer) {
        this.rabbitMQJsonProducer = rabbitMQJsonProducer;
    }

    @PostMapping("/publish")
    public ResponseEntity<String> sendJsonMessage(@RequestBody User user){
        rabbitMQJsonProducer.sendJsonMessage(user);
        return ResponseEntity.ok("Json Message Sent..!");
    }
}
