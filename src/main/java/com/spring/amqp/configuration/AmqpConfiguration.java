package com.spring.amqp.configuration;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class AmqpConfiguration {

    public static final String BOOKING_EVENT_QUEUE = "bookingEventQueue";
    private final RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init() {
        rabbitTemplate.setConfirmCallback((correlation, ack, reason) -> {
            log.info("Received " + (ack ? " ack " : " nack ") + "for correlation: " + correlation);
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            log.info("Returned: " + message + "\nreplyCode: " + replyCode
                    + "\nreplyText: " + replyText + "\nexchange/rk: " + exchange + "/" + routingKey);
        });
    }

    @Bean
    public Queue bookingEventQueue() {
        return new Queue(BOOKING_EVENT_QUEUE);
    }
}
