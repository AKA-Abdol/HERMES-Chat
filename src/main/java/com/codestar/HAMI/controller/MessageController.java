package com.codestar.HAMI.controller;

import com.codestar.HAMI.entity.Chat;
import com.codestar.HAMI.entity.Message;
import com.codestar.HAMI.entity.Profile;
import com.codestar.HAMI.model.ChatMessagesModel;
import com.codestar.HAMI.entity.Subscription;
import com.codestar.HAMI.model.MessageForwardRequest;
import com.codestar.HAMI.model.MessageModel;
import com.codestar.HAMI.service.ChatService;
import com.codestar.HAMI.service.MessageService;
import com.codestar.HAMI.service.SubscriptionService;
import com.codestar.HAMI.service.UserAuthenticationService;
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

    @Autowired
    SubscriptionService subscriptionService;

    @GetMapping("/{chatId}")
    public ChatMessagesModel getChatMessages(@PathVariable Long chatId) {
        Chat chat = chatService.getChatById(chatId);
        List<Message> messages = messageService.getChatMessages(chat);
        Message pinnedMessage = messageService.getMessageById(chat.getPinnedMessageId());

        ChatMessagesModel response = ChatMessagesModel
                .builder()
                .messages(
                        messages.stream()
                                .map(message -> {
                                    MessageModel.MessageModelBuilder builder = 
                                    MessageModel
                                        .builder()
                                        .file(message.getFile())
                                        .text(message.getText())
                                        .createdAt(message.getCreatedAt())
                                        .id(message.getId())
                                        .forwarded(false);

                                    if (message.getSubscription() != null) {
                                        builder.forwarded(true)
                                        .fullName(message.getSubscription().getFullName());
                                    }
                                    return builder.build();
                                    }
                                )
                                .collect(Collectors.toList())
                )
                .build();
        response.setPinned(pinnedMessage);
        return response;
    }

    @PostMapping("/{chatId}")
    public MessageModel createMessage(
            @PathVariable Long chatId, @RequestBody Message messageData
    ) {
        Profile profile = userAuthenticationService.getAuthenticatedProfile();
        Chat chat = chatService.getChatById(chatId);
        Message message = messageService.createMessage(messageData, profile, chat);
        if (!subscriptionService.hasSubscription(chat, profile)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access Denied");
        }
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
            @PathVariable Long messageId, @Valid @RequestBody Message messageData
    ) {
        Profile profile = userAuthenticationService.getAuthenticatedProfile();
        return messageService.editMessage(messageId, messageData, profile);
    }

    @PostMapping("/forward")
    public MessageModel forwardMessage(@Valid @RequestBody MessageForwardRequest forwardRequest){
        Profile senderProfile = userAuthenticationService.getAuthenticatedProfile();
        Chat senderChat = chatService.getChatById(forwardRequest.getChatId());
        Message message = messageService.getMessageById(forwardRequest.getMessageId());
        if (message == null){
            throw  new ResponseStatusException(HttpStatus.NOT_FOUND, "Message Not Found!");
        }
        Chat chat = message.getChat();
        if (!subscriptionService.hasSubscription(chat, senderProfile)
            || !subscriptionService.hasSubscription(senderChat, senderProfile)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access Denied");
        }
        Profile profile = message.getProfile();
        Subscription subscription = subscriptionService.getSubscription(chat, profile);
        message = messageService.saveForwardMessage(message, subscription, senderProfile, senderChat);
        return MessageModel
                .builder()
                .createdAt(message.getCreatedAt())
                .file(message.getFile())
                .text(message.getText())
                .forwarded(true)
                .fullName(message.getSubscription().getFullName())
                .build();
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
