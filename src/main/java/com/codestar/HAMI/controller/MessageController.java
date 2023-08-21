package com.codestar.HAMI.controller;

import com.codestar.HAMI.entity.Chat;
import com.codestar.HAMI.entity.Message;
import com.codestar.HAMI.entity.Profile;
import com.codestar.HAMI.model.MessageModel;
import com.codestar.HAMI.service.ChatService;
import com.codestar.HAMI.service.MessageService;
import com.codestar.HAMI.service.UserAuthenticationService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    @Autowired
    ChatService chatService;

    @Autowired
    UserAuthenticationService userAuthenticationService;

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

    @PostMapping("/{chatId}")
    public MessageModel createMessage(
            @PathVariable Long chatId, @RequestBody Message messageData
    ) {
        Profile profile = userAuthenticationService.getAuthenticatedProfile();
        Chat chat = chatService.getChatById(chatId);
        Message message = messageService.createMessage(messageData, profile, chat);
        return MessageModel
                .builder()
                .createdAt(message.getCreatedAt())
                .text(message.getText())
                .file(null)
                .build();
    }

    @DeleteMapping("/{messageId}")
    public void deleteMessage(@PathVariable Long messageId) {
        messageService.deleteMessage(messageId);
    }

    @PutMapping("/{messageId}")
    public Message editMessage(
            @PathVariable Long messageId, @Valid @RequestBody Message message
    ) {
        return messageService.editMessage(messageId, message);
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
