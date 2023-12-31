package com.codestar.HAMI.controller;

import com.codestar.HAMI.entity.*;
import com.codestar.HAMI.model.*;
import com.codestar.HAMI.service.*;
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
    @Autowired
    FileService fileService;

    @GetMapping("/{chatId}")
    public ChatMessagesModel getChatMessages(@PathVariable Long chatId) {
        Long profileId = userAuthenticationService.getAuthenticatedProfile().getId();
        Chat chat = chatService.getChatById(chatId);
        List<Message> messages = messageService.getChatMessages(chat);

        ChatMessagesModel.ChatMessagesModelBuilder responseBuilder =
                ChatMessagesModel
                        .builder()
                        .messages(
                                messages.stream()
                                        .map(message -> {
                                                    MessageModel.MessageModelBuilder builder =
                                                            MessageModel
                                                                    .builder()
                                                                    .file(
                                                                            message.getFile() != null
                                                                                    ? message.getFile().getData()
                                                                                    : null
                                                                    )
                                                                    .text(message.getText())
                                                                    .createdAt(message.getCreatedAt())
                                                                    .isSelf(profileId.equals(message.getProfile().getId()))
                                                                    .id(message.getId())
                                                                    .viewCount(message.getViewCount())
                                                                    .replyMessageId(message.getReplyMessageId());
                                            if(message.getReplyMessageId() != null) {
                                                builder.replyPreview(ReplyPreview
                                                        .builder()
                                                        .text(messageService.getMessageById(message.getReplyMessageId()).getText())
                                                        .fullName(messageService.getMessageById(message.getReplyMessageId()).getProfile().getFullName())
                                                        .build());
                                            }
                                                                    builder.fullName(message.getProfile().getFullName())
                                                                    .forwarded(false);

                                                    if (message.getSubscription() != null) {
                                                        builder.forwarded(true)
                                                                .fullName(message.getSubscription().getFullName());
                                                    }

                                                    if (message.getChat().getChatType().equals(ChatTypeEnum.SELF)
                                                        && message.getSubscription() != null){
                                                        builder.isSelf(false);
                                                    }

                                                    return builder.build();
                                                }
                                        )
                                        .collect(Collectors.toList())
                        );
        if (chat.getPinnedMessageId() != null) {
            Message pinnedMessage = messageService.getMessageById(chat.getPinnedMessageId());
            responseBuilder
                    .pinned(
                            MessageModel
                                    .builder()
                                    .id(pinnedMessage.getId())
                                    .text(pinnedMessage.getText())
                                    .file(
                                            pinnedMessage.getFile() != null
                                                    ? pinnedMessage.getFile().getData()
                                                    : null
                                    )
                                    .createdAt(pinnedMessage.getCreatedAt())
                                    .fullName(
                                            pinnedMessage.getSubscription() == null
                                                    ? pinnedMessage.getProfile().getFullName()
                                                    : pinnedMessage.getSubscription().getFullName()
                                    )
                                    .forwarded(pinnedMessage.getSubscription() != null)
                                    .build()
                    );
        }
        return responseBuilder.build();
    }

    @PostMapping("/{chatId}")
    public MessageModel createMessage(
            @PathVariable Long chatId, @RequestBody MessageRequest messageData
    ) {
        Profile profile = userAuthenticationService.getAuthenticatedProfile();
        Chat chat = chatService.getChatById(chatId);
        Message newMessage = new Message();
        newMessage.setText(messageData.getText());
        if (messageData.getFileId() != null) {
            File file = fileService.getFileById(messageData.getFileId());
            newMessage.setFile(file);
        }
        Message message = messageService.createMessage(newMessage, profile, chat);
        if (!subscriptionService.hasSubscription(chat, profile)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access Denied");
        }
        return MessageModel
                .builder()
                .createdAt(message.getCreatedAt())
                .text(message.getText())
                .viewCount(message.getViewCount())
                .file(
                        message.getFile() != null
                                ? message.getFile().getData()
                                : null
                )
                .build();
    }

    @DeleteMapping("/{messageId}")
    public void deleteMessage(@PathVariable Long messageId) {
        messageService.deleteMessage(messageId);
    }

    @PutMapping("/{messageId}")
    public Message editMessage(
            @PathVariable Long messageId,
            @Valid @RequestBody Message message
    ) {
        Profile profile = userAuthenticationService.getAuthenticatedProfile();
        return messageService.editMessage(messageId, message, profile);
    }

    @PostMapping("/forward")
    public MessageModel forwardMessage(@Valid @RequestBody MessageForwardRequest forwardRequest) {
        Profile senderProfile = userAuthenticationService.getAuthenticatedProfile();
        Chat senderChat = chatService.getChatById(forwardRequest.getChatId());
        Message message = messageService.getMessageById(forwardRequest.getMessageId());
        if (message == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Message Not Found!");
        }
        Chat chat = message.getChat();
        if (!subscriptionService.hasSubscription(chat, senderProfile)
                || !subscriptionService.hasSubscription(senderChat, senderProfile)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access Denied");
        }
        Profile profile = message.getProfile();
        Subscription subscription = subscriptionService.getSubscription(chat, profile);
        message = messageService.saveForwardMessage(message, subscription, senderProfile, senderChat);
        return MessageModel
                .builder()
                .createdAt(message.getCreatedAt())
                .file(
                        message.getFile() != null
                                ? message.getFile().getData()
                                : null
                )
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

    @PutMapping("view-message/{messageId}")
    public Long viewMessage(@PathVariable long messageId) {
        Message message = messageService.getMessageById(messageId);
        if (message == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "message doesn't exist.");

        if (message.getViewCount() == 0)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "this message a channel message.");

        return messageService.updateMessageView(messageId);
    }

    @PostMapping("reply/{messageId}")
    public Long replyMessage(@PathVariable Long messageId, @RequestBody Message messageDetaile) {
        Profile profile = userAuthenticationService.getAuthenticatedProfile();
        Message message = messageService.getMessageById(messageId);
        Chat chat = chatService.getChatById(message.getChat().getId());

        messageDetaile.setReplyMessageId(messageId);

        return messageService.createMessage(messageDetaile, profile, chat).getId();
    }

}