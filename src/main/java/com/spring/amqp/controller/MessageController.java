package com.spring.amqp.controller;

import com.spring.amqp.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/sendMessage")
    public void send(String message) {
        messageService.send(message);
    }
}
