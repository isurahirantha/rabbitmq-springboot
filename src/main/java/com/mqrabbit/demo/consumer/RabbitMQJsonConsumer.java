package com.mqrabbit.demo.consumer;

import com.mqrabbit.demo.cutomerexception.EmailException;
import com.mqrabbit.demo.dto.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class RabbitMQJsonConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQJsonConsumer.class);

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
                    Pattern.CASE_INSENSITIVE);

    @RabbitListener(queues = {"${rabbitmq.queue.json.name}"})
    public void consume(User user) throws EmailException {

        LOGGER.info(String.format("Received deserialized message -> user: %s", user));
            var matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(user.getEmail());
            if (!matcher.find()) {
                throw new EmailException("Invalid email address");
            }
        LOGGER.info(String.format("Successfully validated message -> user: %s", user));

    }

}
