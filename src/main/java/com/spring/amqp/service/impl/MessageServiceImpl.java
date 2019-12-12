package com.spring.amqp.service.impl;

import com.spring.amqp.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpMessageReturnedException;
import org.springframework.amqp.core.AsyncAmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

@Slf4j
@RequiredArgsConstructor
@Service
public class MessageServiceImpl implements MessageService {

    public static final String BOOKING_FLAGGED = "BOOKING.FLAGGED";
    private final AsyncRabbitTemplate asyncRabbitTemplate;
    private final AsyncAmqpTemplate asyncAmqpTemplate;

    @Override
    public void send(String message) {
        AsyncRabbitTemplate.RabbitMessageFuture future = asyncRabbitTemplate.sendAndReceive(BOOKING_FLAGGED,
                new Message("{ \"message\": \"test\"}".getBytes(), new MessageProperties()));

        future.completable()
                .thenApply(Message::getBody)
                .thenApply(String::new)
                .thenAccept(result -> {
                    log.info("Ack: " + result);
                });

        future.addCallback(result -> {
            log.info("Ack: " + result);
        }, throwable -> {
            if (throwable instanceof AmqpMessageReturnedException) {
                AmqpMessageReturnedException exception = (AmqpMessageReturnedException) throwable;
                log.error("Returned: " + exception.getMessage() + "\nreplyCode: " +
                        exception.getReplyCode() + "\nreplyText: " + exception.getReplyText() + "\nexchange/rk: " +
                        exception.getExchange() + "/" + exception.getRoutingKey());
            } else {
                log.error(throwable.getMessage(), throwable);
            }
        });

        future.getConfirm().addCallback(result -> {
            log.info("Ack: " + result);
        }, throwable -> {
            log.error("Error", throwable);
        });
    }
}
