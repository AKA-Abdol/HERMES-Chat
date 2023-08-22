package com.codestar.HAMI.controller;

import com.codestar.HAMI.entity.Chat;
import com.codestar.HAMI.entity.ChatTypeEnum;
import com.codestar.HAMI.entity.Profile;
import com.codestar.HAMI.model.ChatModel;
import com.codestar.HAMI.model.CreateChannelRequest;
import com.codestar.HAMI.model.CreateGroupRequest;
import com.codestar.HAMI.entity.*;
import com.codestar.HAMI.model.*;
import com.codestar.HAMI.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/chat")
public class ChatController {
    @Autowired
    ChatService chatService;
    @Autowired
    UserAuthenticationService userAuthenticationService;
    @Autowired
    SubscriptionService subscriptionService;
    @Autowired
    ProfileService profileService;
    @Autowired
    MessageService messageService;

    @GetMapping("")
    public List<ChatModel> getChats() {
        Long profileId = userAuthenticationService.getAuthenticatedProfile().getId();
        List<Chat> chats = chatService.getAllChats(profileId);
        List<ChatModel> chatModels = new ArrayList<>();
        for(Chat chat : chats) {
            chatModels.add(ChatModel.builder()
                            .chatId(chat.getId())
                            .bio(chat.getBio())
                            .chatType(chat.getChatType())
                            .description(chat.getDescription())
                            .photo(chat.getPhoto())
                            .build());
        }
        return chatModels;

    }

    @GetMapping("/{chatId}")
    public ChatModel getChat(@PathVariable long chatId) {
        Chat chat = chatService.getChatById(chatId);
        if (chat == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found chat by chatId.");
        if(chat.getChatType() == ChatTypeEnum.PV)
            throw new ResponseStatusException(HttpStatus.I_AM_A_TEAPOT, "you can't access with chatId.");

        return ChatModel.builder()
                .chatId(chat.getId())
                .bio(chat.getBio())
                .chatType(chat.getChatType())
                .description(chat.getDescription())
                .photo(chat.getPhoto())
                .build();
    }

    @PutMapping("/{chatId}")
    public ChatModel updateChat(@PathVariable Long chatId, @RequestBody Chat chatDetail) {
        Chat chat = chatService.getChatById(chatId);
        if (chat == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat id doesn't exist!");
        if(chat.getChatType() == ChatTypeEnum.PV)
            throw new ResponseStatusException(HttpStatus.I_AM_A_TEAPOT, "you can't access with chatId.");

        try {
            chat = chatService.updateChat(chatId,chatDetail);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something were wrong");
        }

        return ChatModel.builder()
                .chatId(chat.getId())
                .bio(chat.getBio())
                .chatType(chat.getChatType())
                .description(chat.getDescription())
                .photo(chat.getPhoto())
                .build();
    }

    @PostMapping("/channel")
    public ChatModel createChannel(@Valid @RequestBody CreateChannelRequest request) {
        Profile profile = userAuthenticationService.getAuthenticatedProfile();
        Chat chat = chatService.createChatForChannel(
                request.getName(), request.getPhoto(),
                request.getDescription(), profile.getId()
        );

        ArrayList<Profile> profiles = new ArrayList<>(
                request
                        .getProfileIds()
                        .stream()
                        .map(profileId -> profileService.getProfileById(profileId))
                        .toList());
        profiles.add(profile);
        subscriptionService.createSubscription(chat, profiles);

        return ChatModel.builder()
                .chatId(chat.getId())
                .bio(chat.getBio())
                .chatType(chat.getChatType())
                .description(chat.getDescription())
                .photo(chat.getPhoto())
                .build();
    }

    @PostMapping("/group")
    public ChatModel createGroup(@Valid @RequestBody CreateGroupRequest request) {
        Profile profile = userAuthenticationService.getAuthenticatedProfile();
        Chat chat = chatService.createChatForGroup(
                request.getName(), request.getPhoto(), profile.getId()
        );

        ArrayList<Profile> profiles = new ArrayList<>(
                request
                        .getProfileIds()
                        .stream()
                        .map(profileId -> profileService.getProfileById(profileId))
                        .toList());
        profiles.add(profile);
        subscriptionService.createSubscription(chat, profiles);

        return ChatModel.builder()
                .chatId(chat.getId())
                .bio(chat.getBio())
                .chatType(chat.getChatType())
                .description(chat.getDescription())
                .photo(chat.getPhoto())
                .build();
    }

    @PostMapping("/pv/{profileId}")
    public ChatModel createPv(@Valid @PathVariable Long profileId) {
        Profile profile = userAuthenticationService.getAuthenticatedProfile();
        Chat chat = chatService.createChatForPv(profileId);

        subscriptionService.createSubscription(chat, profile);
        subscriptionService.createSubscription(chat, profileService.getProfileById(profileId));

        return ChatModel.builder()
                .chatId(chat.getId())
                .bio(chat.getBio())
                .chatType(chat.getChatType())
                .description(chat.getDescription())
                .photo(chat.getPhoto())
                .build();
    }
    @PostMapping("/{chatId}/pin/{messageId}")
    public void pinMessage(
            @Valid @PathVariable Long chatId,
            @Valid @PathVariable Long messageId
    ) {
        Profile profile = userAuthenticationService.getAuthenticatedProfile();
        Chat chat = chatService.getChatById(chatId);
        chatService.pinMessage(profile, chat, messageId);
    }

    @DeleteMapping("/{chatId}/unpin")
    public void unpinMessage(@Valid @PathVariable Long chatId) {
        Profile profile = userAuthenticationService.getAuthenticatedProfile();
        Chat chat = chatService.getChatById(chatId);
        chatService.unpinMessage(profile, chat);
    }

    @GetMapping("/{chatId}/subscribers")
    public List<ChatSubscriberResponse> subscribesChat(@PathVariable Long chatId) {
        Chat chat = chatService.getChatById(chatId);
        if(chat.getChatType() == ChatTypeEnum.PV)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "don't have access in PV");
        List<Subscription> subscriptions = subscriptionService.getSubscriptionsByChatId(chatId);
        return subscriptions
                .stream()
                .map(subscription -> ChatSubscriberResponse
                        .builder()
                        .photo(subscription.getProfile().getPhoto())
                        .fullName(subscription.getProfile().getFullName())
                        .build())
                .toList();
    }

}
