package com.codestar.HAMI.controller;

import com.codestar.HAMI.entity.Message;
import com.codestar.HAMI.model.MessageModel;
import com.codestar.HAMI.service.MessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/message")
@Configuration
@EnableAutoConfiguration
public class MessageController {

    @Autowired
    MessageService messageService;

    @Autowired
    Validator validator;

    @GetMapping("/{chatId}")
    public List<MessageModel> getChatMessages(@PathVariable Long chatId) {
        List<Message> messages = messageService.getChatMessagesByChatId(chatId);
        return messages.stream()
                .map(message -> MessageModel
                        .builder()
                        .file(message.getFile())
                        .text(message.getText())
                        .createdAt(message.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    @PostMapping()
    public MessageModel createMessage(@RequestBody Map<String, Object> messageMap) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        Message message = mapper.convertValue(messageMap, Message.class);
        this.validateCreateMessage(messageMap, message);
        try {
            if (messageMap.containsKey("chatId") && messageMap.get("chatId") != null) {
                message = messageService.createMessage(message, Long.valueOf(messageMap.get("chatId").toString()));
            } else {
                message = messageService.createChatAndMessage(message, Long.valueOf(messageMap.get("profileId").toString()));
            }
        } catch (EntityNotFoundException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }
        return MessageModel
                .builder()
                .text(message.getText())
                .file(message.getFile())
                .createdAt(message.getCreatedAt())
                .build();
    }

    @DeleteMapping("/{messageId}")
    public void deleteMessage(@PathVariable Long messageId) {
        messageService.deleteMessage(messageId);
    }

    @PutMapping("/{messageId}")
    public void editMessage(@PathVariable Long messageId, @Valid @RequestBody Message message) {
        try {
            messageService.editMessage(messageId, message);
        } catch (EntityNotFoundException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }
    }

    private void validateCreateMessage(Map<String, Object> messageMap, Message message) {
        Set<ConstraintViolation<Message>> violations = validator.validate(message);
        if (!messageMap.containsKey("profileId") && !messageMap.containsKey("chatId")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing 'profileId' key in the request body");
        }
        if (!violations.isEmpty()) {
            List<String> errorMessages = new ArrayList<>();
            for (ConstraintViolation<Message> violation : violations) {
                errorMessages.add(violation.getPropertyPath() + ": " + violation.getMessage());
            }
            String errorMessage = String.join(", ", errorMessages);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }
    }
}
