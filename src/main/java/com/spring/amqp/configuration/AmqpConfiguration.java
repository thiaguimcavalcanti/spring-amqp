package com.spring.amqp.configuration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class AmqpConfiguration {

    private static final String REPLY_QUEUE_NAME = "bookingEventQueue-replyTo";
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange}")
    private String rabbitMqExchange;

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(rabbitMqExchange);
    }

    @Bean
    Queue replyQueue() {
        return QueueBuilder.durable(REPLY_QUEUE_NAME).build();
    }

    @PostConstruct
    public void postConstruct() {
        rabbitTemplate.setExchange(rabbitMqExchange);
        rabbitTemplate.setBeforePublishPostProcessors(messagePostProcessor());
        rabbitTemplate.setMessageConverter(messageConverter());
    }

    @Bean
    public AsyncRabbitTemplate asyncRabbitTemplate(RabbitTemplate rabbitTemplate, SimpleMessageListenerContainer container) {
        AsyncRabbitTemplate asyncRabbitTemplate = new AsyncRabbitTemplate(rabbitTemplate, container);
        asyncRabbitTemplate.setEnableConfirms(true);
        asyncRabbitTemplate.setMandatory(true);
        return asyncRabbitTemplate;
    }

    @Bean
    public SimpleMessageListenerContainer simpleMessageListenerContainer(RabbitTemplate rabbitTemplate) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(rabbitTemplate.getConnectionFactory());
        container.setQueueNames(REPLY_QUEUE_NAME);
        //container.afterPropertiesSet();
        return container;
    }

    @RabbitListener(queues = "bookingEventQueue")
    public void consume() {
        System.out.println("");
    }

    private MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter(objectMapper());
    }

    private ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        objectMapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        return objectMapper;
    }

    private MessagePostProcessor messagePostProcessor() {
        return message -> {
            message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            return message;
        };
    }
}
