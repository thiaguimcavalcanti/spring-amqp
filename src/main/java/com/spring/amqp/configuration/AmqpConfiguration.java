package com.spring.amqp.configuration;

import com.spring.amqp.util.CustomConfirmCallback;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class AmqpConfiguration {
    private final RabbitTemplate rabbitTemplate;
    private final CustomConfirmCallback customConfirmCallback;

    @PostConstruct
    public void init() {
        rabbitTemplate.setConfirmCallback(customConfirmCallback);
    }
}
