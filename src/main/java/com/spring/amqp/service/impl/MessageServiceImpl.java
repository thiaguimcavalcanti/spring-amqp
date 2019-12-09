package com.spring.amqp.service.impl;

import com.spring.amqp.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MessageServiceImpl implements MessageService {

    public static final String BOOKING_EXCHANGE = "booking";
    public static final String BOOKING_FLAGGED = "BOOKING.FLAGGED";
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void send(String message) {
        rabbitTemplate.convertAndSend(BOOKING_EXCHANGE, BOOKING_FLAGGED, "test");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
           log.info("end");
        }
    }
}
